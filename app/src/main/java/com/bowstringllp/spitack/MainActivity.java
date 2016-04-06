package com.bowstringllp.spitack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.bowstringllp.spitack.util.SystemUiHider;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LevelEndEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LEADERBOARD = 121;
    private static final String EXPLICIT_SIGN_OUT = "Explicitly signed out";
    private static final String HIGHSCORE = "Game Highscore";
    private static final String IS_FIRST_CONNECTION = "Is connecting for first time";
    private static final int NUM_OF_STARS = 10;
    private Handler frame = new Handler();
    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 20; //50 frames per second
    private GameBoard gameBoard;
    private int playerAddFactor = 20;

    private Button playButton;
    private TextView timerText;
    private View timerLayout;
    private View scoreboardLayout;
    private TextView scoreBoardTimerText;
    private TextView readyText;
    private CountDownTimer timer;
    private long timeElapsed = 0;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;
    final String TAG = "TanC";
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private boolean isPaused = false;

    boolean mInSignInFlow = false; // set to true when you're in the middle of the
    private AdView mAdView;
    private static List<Integer> countdownValueList = new ArrayList<>();
    private TextView scoreBoardHighText;
    private MixpanelAPI mixpanel;

    public static int getNoOfStars() {
        return NUM_OF_STARS;
    }
    // sign in flow, to know you should not attempt
    // to connect in onStart()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_token));
        setContentView(R.layout.activity_main);

    // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        playButton = (Button) findViewById(R.id.scoreboard_play_button);
        timerText = (TextView) findViewById(R.id.timer_text);
        timerLayout = findViewById(R.id.timer_layout);
        scoreboardLayout = findViewById(R.id.scoreboard_layout);
        scoreBoardTimerText = (TextView) findViewById(R.id.scoreboard_cur_score_text);
        scoreBoardHighText = (TextView) findViewById(R.id.scoreboard_high_score_text);
        readyText = (TextView) findViewById(R.id.ready_text);

        gameBoard = (GameBoard) findViewById(R.id.the_canvas);

        findViewById(R.id.left_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (readyText.getVisibility() == View.VISIBLE) {
                            initGfx();
                            readyText.setVisibility(View.GONE);
                        }

                        gameBoard.getPlayer().setAddFactor(playerAddFactor * -1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getPlayer().getAddFactor() <= 0)
                            gameBoard.getPlayer().setAddFactor(0);
                        break;
                }
                return true;
            }
        });

        findViewById(R.id.right_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (readyText.getVisibility() == View.VISIBLE) {
                            initGfx();
                            readyText.setVisibility(View.GONE);
                        }
                        gameBoard.getPlayer().setAddFactor(playerAddFactor);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getPlayer().getAddFactor() >= 0)
                            gameBoard.getPlayer().setAddFactor(0);
                        break;
                }
                return true;
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);
        //  AdRequest adRequest = new AdRequest.Builder().build();
        //  mAdView.setAdSize(AdSize.SMART_BANNER);
        // mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id_test));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("58A3D42C750DA991B6399A4591CD3793")  // An example device ID
                .build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.scoreboard_leaderboard_button).setOnClickListener(this);
        findViewById(R.id.scoreboard_siginin_button).setOnClickListener(this);
        findViewById(R.id.scoreboard_siginout_button).setOnClickListener(this);
        playButton.setOnClickListener(this);
        gameBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyText.getVisibility() == View.VISIBLE) {
                    initGfx();
                    readyText.setVisibility(View.GONE);
                } else if (scoreboardLayout.getVisibility() != View.VISIBLE) {
                    pauseGame();
                }
            }
        });
    }


    private final Object lock = new Object();

    private void pauseGame() {
        synchronized (lock) {
            isPaused = true;
        }

        playButton.setText(getString(R.string.resume_text));
        timer.cancel();
        scoreBoardTimerText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        long high = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong(HIGHSCORE, 0);

        if (high > timeElapsed)
            scoreBoardHighText.setText(String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
        else
            scoreBoardHighText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        scoreboardLayout.setVisibility(View.VISIBLE);
    }

    private void unPauseGame() {
        synchronized (lock) {
            isPaused = false;
        }

        timer = new CountDownTimer(120000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsed++;
                timerText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));
            }


            @Override
            public void onFinish() {
                timer.start();
            }

        }.start();

        scoreboardLayout.setVisibility(View.GONE);

        frame.removeCallbacks(frameUpdate);
        //make any updates to on screen objects here
        //then invoke the on draw by invalidating the canvas
        gameBoard.invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean(EXPLICIT_SIGN_OUT, true)) {
            Log.d(TAG, "onStart(): connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    synchronized public void initGfx() {
        //It's a good idea to remove any existing callbacks to keep
        //them from inadvertently stacking up.
        gameBoard.reset();
        mAdView.setVisibility(View.GONE);
        scoreboardLayout.setVisibility(View.GONE);
        timerLayout.setVisibility(View.VISIBLE);
        timerText.setText("0 sec");
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);

        timeElapsed = 0;
        timer = new CountDownTimer(120000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsed++;
                timerText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));
            }


            @Override
            public void onFinish() {
                timer.start();
            }

        }.start();
    }

    @Override
    synchronized public void onClick(View v) {
        if (v.getId() == R.id.scoreboard_siginin_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        } else if (v.getId() == R.id.scoreboard_siginout_button) {
            // sign out.
            mSignInClicked = false;
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(EXPLICIT_SIGN_OUT, true).commit();
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }

            // show sign-in button, hide the sign-out button
            findViewById(R.id.scoreboard_leaderboard_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.scoreboard_siginin_button).setVisibility(View.VISIBLE);
            findViewById(R.id.scoreboard_siginout_button).setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.scoreboard_play_button) {
            synchronized (lock) {
                if (isPaused) {
                    unPauseGame();
                    return;
                }
            }

            initGfx();
        } else if (v.getId() == R.id.scoreboard_leaderboard_button) {
            showLeadershipBoard();
        }
    }

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            synchronized (lock) {
                if (isPaused)
                    return;
            }

            if (gameBoard.isCollisionDetected()) {
                playButton.setText(getString(R.string.replay_text));
                timerLayout.setVisibility(View.INVISIBLE);
                timer.cancel();
                scoreboardLayout.setVisibility(View.VISIBLE);
                mAdView.setVisibility(View.VISIBLE);
                scoreBoardTimerText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                long high = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong(HIGHSCORE, 0);

                if (high > timeElapsed)
                    scoreBoardHighText.setText(String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
                else
                    scoreBoardHighText.setText(String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_best_time), timeElapsed * 1000).setResultCallback(new ResolvingResultCallbacks<SubmitScoreResult>(MainActivity.this, REQUEST_LEADERBOARD) {
                        @Override
                        public void onSuccess(SubmitScoreResult submitScoreResult) {
                            long high = submitScoreResult.getScoreData().getScoreResult(
                                    LeaderboardVariant.TIME_SPAN_ALL_TIME).rawScore / 1000;
                            scoreBoardHighText.setText(String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));

                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong(HIGHSCORE, high).commit();
                        }

                        @Override
                        public void onUnresolvableFailure(Status status) {

                            Log.d(TAG, status.getStatusMessage());
                        }
                    });
                } else {
                    if (high < timeElapsed)
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong(HIGHSCORE, timeElapsed).commit();
                }

                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                        .putScore(timeElapsed));
                Answers.getInstance().logCustom(new CustomEvent("Score")
                        .putCustomAttribute("Time Elapsed", timeElapsed));

                try {
                    JSONObject props = new JSONObject();
                    props.put("Current Score", timeElapsed);
                    mixpanel.track("Score", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

            frame.removeCallbacks(frameUpdate);
            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            gameBoard.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        // show sign-out button, hide the sign-in button
        findViewById(R.id.scoreboard_siginin_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.scoreboard_leaderboard_button).setVisibility(View.VISIBLE);
        findViewById(R.id.scoreboard_siginout_button).setVisibility(View.VISIBLE);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(EXPLICIT_SIGN_OUT, false).commit();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IS_FIRST_CONNECTION, true)) {
            loadScoreOfLeaderBoard();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(IS_FIRST_CONNECTION, false).commit();
        }
        // (your code here: update UI, enable functionality that depends on sign in, etc)

    }

    private void loadScoreOfLeaderBoard() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient, getString(R.string.leaderboard_best_time), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<LoadPlayerScoreResult>() {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    if (isScoreResultValid(scoreResult)) {
                        // here you can get the score like this
                        long high = scoreResult.getScore().getRawScore() / 1000;
                        scoreBoardHighText.setText(String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong(HIGHSCORE, scoreResult.getScore().getRawScore() / 1000).commit();
                    }
                }
            });
        }
    }

    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    public void showLeadershipBoard() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    getString(R.string.leaderboard_best_time)), REQUEST_LEADERBOARD);
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {

        // check for "inconsistent state"
        if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED && request == REQUEST_LEADERBOARD) {
            // force a disconnect to sync up state, ensuring that mClient reports "not connected"
            mGoogleApiClient.disconnect();
        }

        if (request == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (response == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static int getCountdownValue() {
        if (countdownValueList.isEmpty()) {
            for (int i = 0; i < NUM_OF_STARS; i++) {
                int num;
                int occurrences = 0;
                do {
                    num = new Random().nextInt(10);
                    occurrences = Collections.frequency(countdownValueList, num);
                } while (occurrences > 2);
                countdownValueList.add(num);
            }
        }
        return countdownValueList.remove(0);
    }
}
