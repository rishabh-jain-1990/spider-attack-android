package com.bowstringllp.spiderattack.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowstringllp.spiderattack.R;
import com.bowstringllp.spiderattack.ui.view.GameBoard;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LEADERBOARD = 121;
    private static final String EXPLICIT_SIGN_OUT = "Explicitly signed out";
    private static final String HIGHSCORE = "Game Highscore";
    private static final String IS_FIRST_CONNECTION = "Is connecting for first time";
    private static final int NUM_OF_STARS = 10;
    private Handler frame = new Handler();
    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 25; //50 frames per second
    private GameBoard gameBoard;
    private int playerAddFactor = 20;

    private ImageView playButton;
    private TextView timerText;
    private View timerLayout;
    private View scoreboardLayout;
    private TextView scoreBoardTimerText;
    //    private TextView readyText;
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
    private ImageView leaderboardImage;
    private TextView leaderboardText;
    private ImageView signOutImage;
    private TextView signOutText;

    public static int getNoOfStars() {
        return NUM_OF_STARS;
    }

    public GameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        playButton = (ImageView) view.findViewById(R.id.scoreboard_play_button);
        timerText = (TextView) view.findViewById(R.id.timer_text);
        timerLayout = view.findViewById(R.id.timer_layout);
        scoreboardLayout = view.findViewById(R.id.scoreboard_layout);
        scoreBoardTimerText = (TextView) view.findViewById(R.id.scoreboard_cur_score_text);
        scoreBoardHighText = (TextView) view.findViewById(R.id.scoreboard_high_score_text);
        leaderboardImage = (ImageView) view.findViewById(R.id.scoreboard_leaderboard_button);
        leaderboardText = (TextView) view.findViewById(R.id.scoreboard_leaderboard_text);
        signOutImage = (ImageView) view.findViewById(R.id.scoreboard_signout_button);
        signOutText = (TextView) view.findViewById(R.id.scoreboard_signout_text);
//        readyText = (TextView) view.findViewById(R.id.ready_text);

        gameBoard = (GameBoard) view.findViewById(R.id.the_canvas);

        view.findViewById(R.id.left_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
//                        if (readyText.getVisibility() == View.VISIBLE) {
//                        initGfx();
//                            readyText.setVisibility(View.GONE);
//                        }

                        gameBoard.getBee().setAddFactor(playerAddFactor * -1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getBee().getAddFactor() <= 0)
                            gameBoard.getBee().setAddFactor(0);
                        break;
                }
                return true;
            }
        });

        view.findViewById(R.id.right_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
//                        if (readyText.getVisibility() == View.VISIBLE) {
//                        initGfx();
//                            readyText.setVisibility(View.GONE);
//                        }
                        gameBoard.getBee().setAddFactor(playerAddFactor);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getBee().getAddFactor() >= 0)
                            gameBoard.getBee().setAddFactor(0);
                        break;
                }
                return true;
            }
        });

        mAdView = (AdView) view.findViewById(R.id.adView);
        //  AdRequest adRequest = new AdRequest.Builder().build();
        //  mAdView.setAdSize(AdSize.SMART_BANNER);
        // mAdView.setAdUnitId(getString(R.thread_bit.banner_ad_unit_id_test));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("58A3D42C750DA991B6399A4591CD3793")  // An example device ID
                .build();
        mAdView.loadAd(adRequest);

        leaderboardImage.setOnClickListener(this);
        leaderboardText.setOnClickListener(this);
        signOutImage.setOnClickListener(this);
        signOutText.setOnClickListener(this);
        playButton.setOnClickListener(this);
        gameBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (readyText.getVisibility() == View.VISIBLE) {
//                    initGfx();
//                    readyText.setVisibility(View.GONE);
//                } else

                if (scoreboardLayout.getVisibility() != View.VISIBLE) {
                    pauseGame();
                }
            }
        });

        initGfx();

        return view;
    }

    private final Object lock = new Object();

    private void pauseGame() {
        synchronized (lock) {
            isPaused = true;
        }

        playButton.setImageResource(R.drawable.play);
        timer.cancel();
        scoreBoardTimerText.setText("Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        long high = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(HIGHSCORE, 0);

        if (high > timeElapsed)
            scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
        else
            scoreBoardHighText.setText("High Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            leaderboardImage.setVisibility(View.VISIBLE);
            leaderboardText.setVisibility(View.VISIBLE);
            signOutImage.setImageResource(R.drawable.controller_filled);
            signOutText.setText("Sign Out");
        } else {
            leaderboardImage.setVisibility(View.INVISIBLE);
            leaderboardText.setVisibility(View.INVISIBLE);
            signOutImage.setImageResource(R.drawable.controller);
            signOutText.setText("Sign In");
        }

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
    public void onStart() {
        super.onStart();
        if (!mInSignInFlow && !PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(EXPLICIT_SIGN_OUT, true)) {
            Log.d(TAG, "onStart(): connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
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
        if (v.getId() == R.id.scoreboard_signout_button || v.getId() == R.id.scoreboard_signout_text) {
            // sign out.
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(EXPLICIT_SIGN_OUT, true).commit();
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();

                // show sign-in button, hide the sign-out button
                leaderboardImage.setVisibility(View.INVISIBLE);
                leaderboardText.setVisibility(View.INVISIBLE);
                signOutImage.setImageResource(R.drawable.controller);
                signOutText.setText("Sign In");
            } else if (mGoogleApiClient != null) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        } else if (v.getId() == R.id.scoreboard_play_button) {
            synchronized (lock) {
                if (isPaused) {
                    unPauseGame();
                    return;
                }
            }

            initGfx();
        } else if (v.getId() == R.id.scoreboard_leaderboard_button || v.getId() == R.id.scoreboard_leaderboard_text) {
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
                playButton.setImageResource(R.drawable.replay);
                timerLayout.setVisibility(View.INVISIBLE);
                timer.cancel();

                scoreboardLayout.setVisibility(View.VISIBLE);
                mAdView.setVisibility(View.VISIBLE);
                scoreBoardTimerText.setText("Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                long high = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(HIGHSCORE, 0);

                if (high > timeElapsed)
                    scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
                else
                    scoreBoardHighText.setText("High Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    leaderboardImage.setVisibility(View.VISIBLE);
                    leaderboardText.setVisibility(View.VISIBLE);
                    signOutImage.setImageResource(R.drawable.controller_filled);
                    signOutText.setText("Sign Out");

                    Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_best_time), timeElapsed * 1000).setResultCallback(new ResolvingResultCallbacks<SubmitScoreResult>(getActivity(), REQUEST_LEADERBOARD) {
                        @Override
                        public void onSuccess(SubmitScoreResult submitScoreResult) {
                            long high = submitScoreResult.getScoreData().getScoreResult(
                                    LeaderboardVariant.TIME_SPAN_ALL_TIME).rawScore / 1000;
                            scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));

                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(HIGHSCORE, high).commit();
                        }

                        @Override
                        public void onUnresolvableFailure(Status status) {

                            Log.d(TAG, status.getStatusMessage());
                        }
                    });
                } else {
                    leaderboardImage.setVisibility(View.INVISIBLE);
                    leaderboardText.setVisibility(View.INVISIBLE);
                    signOutImage.setImageResource(R.drawable.controller);
                    signOutText.setText("Sign In");
                    if (high < timeElapsed)
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(HIGHSCORE, timeElapsed).commit();
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
        leaderboardImage.setVisibility(View.VISIBLE);
        leaderboardText.setVisibility(View.VISIBLE);
        signOutImage.setImageResource(R.drawable.controller_filled);
        signOutText.setText("Sign Out");
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(EXPLICIT_SIGN_OUT, false).commit();
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(IS_FIRST_CONNECTION, true)) {
            loadScoreOfLeaderBoard();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(IS_FIRST_CONNECTION, false).apply();
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
                        scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(HIGHSCORE, scoreResult.getScore().getRawScore() / 1000).commit();
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
            // The R.thread_bit.signin_other_error value should reference a generic
            // error thread_bit in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(getActivity(),
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
    public void onActivityResult(int request, int response, Intent data) {

        // check for "inconsistent state"
        if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED && request == REQUEST_LEADERBOARD) {
            // force a disconnect to sync up state, ensuring that mClient reports "not connected"
            mGoogleApiClient.disconnect();
        }

        if (request == RC_SIGN_IN) {
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
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