package com.bowstringllp.spiderattack.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowstringllp.spiderattack.MyApplication;
import com.bowstringllp.spiderattack.R;
import com.bowstringllp.spiderattack.model.Spider;
import com.bowstringllp.spiderattack.ui.view.GameBoard;
import com.bowstringllp.spiderattack.util.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameActivity extends AppCompatActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LEADERBOARD = 121;
    private static final String EXPLICIT_SIGN_OUT = "Explicitly signed out";
    private static final String HIGHSCORE = "Game Highscore";
    private static final String IS_FIRST_CONNECTION = "Is connecting for first time";
    private static final String IS_MUTE = "Is mute";
    private static final int NUM_OF_STARS = 10;
    private Handler frame = new Handler();
    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 25; //40 frames per second

    @Inject
    MixpanelAPI mixpanel;

    @Inject
    SharedPreferences preferences;

    @Inject
    FirebaseAnalytics mFirebaseAnalytics;

    private GameBoard gameBoard;
    private int playerAddFactor;

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
    private TextView scoreBoardTitleText;
    private TextView scoreBoardHighText;
    private ImageView leaderboardImage;
    private TextView leaderboardText;
    private ImageView signOutImage;
    private TextView signOutText;
    private MediaPlayer mBGMediaPlayer;
    private MediaPlayer mBiteMediaPlayer;
    private Animation fadeInAnimation;
    private TextView countdownText;
    private CountDownTimer countdownTimer;
    private ImageView muteButton;
    private boolean isMute = false;
    private CountDownTimer mBGTimer;

    public static int getNoOfStars() {
        return NUM_OF_STARS;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);
        MyApplication.getInstance().getNetComponent().inject(this);

        findViewById(R.id.help_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layout_help).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layout_help).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.play_image).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = false;

                if (!isMute)
                    mBGMediaPlayer.start();
                findViewById(R.id.layout_fragment_splash).setVisibility(View.GONE);
                initGfx();
                //   findViewById(R.id.layout_fragment_game).setVisibility(View.VISIBLE);
            }
        });

        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        playButton = (ImageView) findViewById(R.id.scoreboard_play_button);
        muteButton = (ImageView) findViewById(R.id.scoreboard_mute_button);
        timerText = (TextView) findViewById(R.id.timer_text);
        timerLayout = findViewById(R.id.timer_layout);
        scoreboardLayout = findViewById(R.id.scoreboard_layout);
        scoreBoardTitleText = (TextView) findViewById(R.id.game_over_label);
        scoreBoardTimerText = (TextView) findViewById(R.id.scoreboard_cur_score_text);
        scoreBoardHighText = (TextView) findViewById(R.id.scoreboard_high_score_text);
        leaderboardImage = (ImageView) findViewById(R.id.scoreboard_leaderboard_button);
        leaderboardText = (TextView) findViewById(R.id.scoreboard_leaderboard_text);
        signOutImage = (ImageView) findViewById(R.id.scoreboard_signout_button);
        signOutText = (TextView) findViewById(R.id.scoreboard_signout_text);
        countdownText = (TextView) findViewById(R.id.countdown_text);
//        readyText = (TextView) findViewById(R.id.ready_text);

        gameBoard = (GameBoard) findViewById(R.id.the_canvas);

        findViewById(R.id.left_button).setOnTouchListener(new OnTouchListener() {
            @Override
            synchronized public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
//                        if (readyText.getVisibility() == VISIBLE) {
//                        initGfx();
//                            readyText.setVisibility(GONE);
//                        }

                        gameBoard.getBee().moveLeft();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getBee().getAddFactor() <= 0)
                            gameBoard.getBee().stopMoving();
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
//                        if (readyText.getVisibility() == VISIBLE) {
//                        initGfx();
//                            readyText.setVisibility(GONE);
//                        }
                        gameBoard.getBee().moveRight();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (gameBoard.getBee().getAddFactor() >= 0)
                            gameBoard.getBee().stopMoving();
                        break;
                }
                return true;
            }
        });

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5934924366370104~6193564076");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("58A3D42C750DA991B6399A4591CD3793")  // An example device ID
//                .build();
        mAdView.loadAd(adRequest);

        leaderboardImage.setOnClickListener(this);
        leaderboardText.setOnClickListener(this);
        signOutImage.setOnClickListener(this);
        signOutText.setOnClickListener(this);
        playButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);
        findViewById(R.id.scoreboard_share_button).setOnClickListener(this);
        findViewById(R.id.scoreboard_share_text).setOnClickListener(this);
        findViewById(R.id.scoreboard_rate_button).setOnClickListener(this);
        findViewById(R.id.scoreboard_rate_text).setOnClickListener(this);

        gameBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (readyText.getVisibility() == View.VISIBLE) {
//                    initGfx();
//                    readyText.setVisibility(View.GONE);
//                } else

                if (!gameBoard.isCollisionDetected()) {
                    pauseGame();
                }
            }
        });

        fadeInAnimation = AnimationUtils.loadAnimation(GameActivity.this, android.R.anim.fade_in);
// Now Set your animation
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scoreboardLayout.setVisibility(View.VISIBLE);
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        isMute = preferences.getBoolean(IS_MUTE, false);
        muteButton.setImageResource(isMute ? R.drawable.mute_icon : R.drawable.unmute_icon);
    }

    private final Object lock = new Object();

    private void pauseGame() {
        synchronized (lock) {
            isPaused = true;
        }

        if (mBGMediaPlayer != null) {
            mBGMediaPlayer.pause();
            if (mBGTimer != null)
                mBGTimer.cancel();
        }

        playButton.setImageResource(R.drawable.play);

        if (timer != null)
            timer.cancel();

        if (countdownTimer != null)
            countdownTimer.cancel();

        countdownText.setVisibility(View.GONE);
        scoreBoardTitleText.setText("Game Paused");
        scoreBoardTimerText.setText("Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        long high = preferences.getLong(HIGHSCORE, 0);

        if (high > timeElapsed)
            scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
        else
            scoreBoardHighText.setText("High Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            leaderboardImage.setVisibility(View.VISIBLE);
//            leaderboardText.setVisibility(View.VISIBLE);
            signOutImage.setImageResource(R.drawable.controller_filled);
            signOutText.setText("Sign Out");
        } else {
//            leaderboardImage.setVisibility(View.INVISIBLE);
//            leaderboardText.setVisibility(View.INVISIBLE);
            signOutImage.setImageResource(R.drawable.controller);
            signOutText.setText("Sign In");
        }

        mAdView.setVisibility(View.VISIBLE);
        scoreboardLayout.setVisibility(View.VISIBLE);

        if (gameBoard != null && gameBoard.getSpiderArray() != null)
            for (Spider s : gameBoard.getSpiderArray())
                if (s != null)
                    s.pause();
    }

    private void unPauseGame() {
        synchronized (lock) {
            isPaused = false;
        }

        if (mBGMediaPlayer != null && !isMute)
            mBGMediaPlayer.start();

        countdownText.setVisibility(View.VISIBLE);
        mAdView.setVisibility(View.GONE);
        scoreboardLayout.setVisibility(View.GONE);
        countdownTimer = new CountDownTimer(3000, 900) {
            int countdown = 3;

            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.valueOf(countdown));
                countdown--;
                Log.d("Countdown", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                countdownText.setVisibility(View.GONE);
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

                if (gameBoard != null && gameBoard.getSpiderArray() != null)
                    for (Spider s : gameBoard.getSpiderArray())
                        if (s != null)
                            s.unPause();

                frame.removeCallbacks(frameUpdate);
                //make any updates to on screen objects here
                //then invoke the on draw by invalidating the canvas
                gameBoard.invalidate();
                frame.postDelayed(frameUpdate, 0);
            }
        }.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mInSignInFlow && !preferences.getBoolean(EXPLICIT_SIGN_OUT, true)) {
            Log.d(TAG, "onStart(): connecting");
            mGoogleApiClient.connect();
        }

        if (mBGMediaPlayer == null) {
            mBGMediaPlayer = MediaPlayer.create(this, R.raw.sound_bg_short);
            mBGMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    int time = new Random().nextInt(4) + 2;
                    mBGTimer = new CountDownTimer(time * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if(!isMute)
                            mBGMediaPlayer.start();
                        }
                    }.start();
                }
            });
        }

        if (mBiteMediaPlayer == null) {
            mBiteMediaPlayer = MediaPlayer.create(this, R.raw.chew);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (mBGMediaPlayer != null) {
            mBGMediaPlayer.reset();
            mBGMediaPlayer.release();
            mBGMediaPlayer = null;
        }

        if (mBiteMediaPlayer != null) {
            mBiteMediaPlayer.reset();
            mBiteMediaPlayer.release();
            mBiteMediaPlayer = null;
        }

        if (mBGTimer != null)
            mBGTimer.cancel();

        if (!gameBoard.isCollisionDetected() && !isPaused)
            pauseGame();
    }

    synchronized public void initGfx() {
        //It's a good idea to remove any existing callbacks to keep
        //them from inadvertently stacking up.
        gameBoard.reset();
        mAdView.setVisibility(View.GONE);
        scoreboardLayout.setVisibility(View.INVISIBLE);
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
                preferences.edit().putBoolean(EXPLICIT_SIGN_OUT, true).apply();
                Games.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();

                // show sign-in button, hide the sign-out button
//                leaderboardImage.setVisibility(View.INVISIBLE);
//                leaderboardText.setVisibility(View.INVISIBLE);
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

            if (!isMute)
                mBGMediaPlayer.start();
            initGfx();
        } else if (v.getId() == R.id.scoreboard_leaderboard_button || v.getId() == R.id.scoreboard_leaderboard_text) {
            showLeadershipBoard();
        } else if (v.getId() == R.id.scoreboard_share_button || v.getId() == R.id.scoreboard_share_text) {
            long high = preferences.getLong(HIGHSCORE, 0);
            if (high < timeElapsed)
                high = timeElapsed;

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Try and beat my high score of " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60) + " at Spider Attack\nhttps://goo.gl/bOhbH3");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (v.getId() == R.id.scoreboard_rate_button || v.getId() == R.id.scoreboard_rate_text) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (v.getId() == R.id.scoreboard_mute_button) {
            isMute = !isMute;
            muteButton.setImageResource(isMute ? R.drawable.mute_icon : R.drawable.unmute_icon);
            preferences.edit().putBoolean(IS_MUTE, isMute).apply();

            if (isMute) {
                mBGMediaPlayer.pause();

                if (mBGTimer != null)
                    mBGTimer.cancel();

                mBiteMediaPlayer.pause();
            }
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
                mBGMediaPlayer.pause();

                if (mBGTimer != null)
                    mBGTimer.cancel();

                if (!isMute)
                    mBiteMediaPlayer.start();

                scoreBoardTitleText.setText("Game Over");
                playButton.setImageResource(R.drawable.replay);
                timerLayout.setVisibility(View.INVISIBLE);
                timer.cancel();

                scoreboardLayout.startAnimation(fadeInAnimation);
                scoreBoardTimerText.setText("Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                long high = preferences.getLong(HIGHSCORE, 0);

                if (high > timeElapsed)
                    scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));
                else
                    scoreBoardHighText.setText("High Score- " + String.format("%02d", timeElapsed / 60) + " : " + String.format("%02d", timeElapsed % 60));

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                    leaderboardImage.setVisibility(View.VISIBLE);
//                    leaderboardText.setVisibility(View.VISIBLE);
                    signOutImage.setImageResource(R.drawable.controller_filled);
                    signOutText.setText("Sign Out");

                    if (timeElapsed >= Constants.ROOKIE_TIME_THRESHOLD)
                        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_rookie));
                    if (timeElapsed >= Constants.BEGINNER_TIME_THRESHOLD)
                        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_beginner));
                    if (timeElapsed >= Constants.INTERMEDIATE_TIME_THRESHOLD)
                        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_intermediate));
                    if (timeElapsed >= Constants.EXPERT_TIME_THRESHOLD)
                        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_expert));
                    if (timeElapsed >= Constants.INVINCIBLE_TIME_THRESHOLD)
                        Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_invincible));

                    Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_best_time), timeElapsed * 1000).setResultCallback(new ResolvingResultCallbacks<SubmitScoreResult>(GameActivity.this, REQUEST_LEADERBOARD) {
                        @Override
                        public void onSuccess(SubmitScoreResult submitScoreResult) {
                            long high = submitScoreResult.getScoreData().getScoreResult(
                                    LeaderboardVariant.TIME_SPAN_ALL_TIME).rawScore / 1000;
                            scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));

                            Log.d("Score: 549 ", String.valueOf(high));
                            preferences.edit().putLong(HIGHSCORE, high).apply();
                        }

                        @Override
                        public void onUnresolvableFailure(Status status) {

                            Log.d(TAG, status.getStatusMessage());

                            if (status.getStatusCode() == 2)
                                mGoogleApiClient.reconnect();
                        }
                    });
                } else {
//                    leaderboardImage.setVisibility(View.INVISIBLE);
//                    leaderboardText.setVisibility(View.INVISIBLE);
                    signOutImage.setImageResource(R.drawable.controller);
                    signOutText.setText("Sign In");
                    if (high < timeElapsed)
                        preferences.edit().putLong(HIGHSCORE, timeElapsed).apply();
                }

                try {
                    JSONObject props = new JSONObject();
                    props.put("Current Score", timeElapsed);
                    mixpanel.track("Score", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putLong(Param.SCORE, timeElapsed);
                bundle.putLong(Param.VALUE, timeElapsed);
                mFirebaseAnalytics.logEvent(Event.POST_SCORE, bundle);

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
//        leaderboardImage.setVisibility(View.VISIBLE);
//        leaderboardText.setVisibility(View.VISIBLE);
        signOutImage.setImageResource(R.drawable.controller_filled);
        signOutText.setText("Sign Out");
        preferences.edit().putBoolean(EXPLICIT_SIGN_OUT, false).apply();
        if (preferences.getBoolean(IS_FIRST_CONNECTION, true)) {
            loadScoreOfLeaderBoard();
            preferences.edit().putBoolean(IS_FIRST_CONNECTION, false).apply();
        }
        // (your code here: update UI, enable functionality that depends on sign in, etc)

    }

    private void loadScoreOfLeaderBoard() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            long highscore = preferences.getLong(HIGHSCORE, 0);
            if (timeElapsed > highscore)
                highscore = timeElapsed;

            Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_best_time), highscore * 1000).setResultCallback(new ResolvingResultCallbacks<SubmitScoreResult>(GameActivity.this, REQUEST_LEADERBOARD) {
                @Override
                public void onSuccess(SubmitScoreResult submitScoreResult) {
                    long high = submitScoreResult.getScoreData().getScoreResult(
                            LeaderboardVariant.TIME_SPAN_ALL_TIME).rawScore / 1000;
                    scoreBoardHighText.setText("High Score- " + String.format("%02d", high / 60) + " : " + String.format("%02d", high % 60));

                    Log.d("Score: 549 ", String.valueOf(high));
                    preferences.edit().putLong(HIGHSCORE, high).apply();
                }

                @Override
                public void onUnresolvableFailure(Status status) {

                    Log.d(TAG, status.getStatusMessage());

                    if (status.getStatusCode() == 2)
                        mGoogleApiClient.reconnect();
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
            if (!BaseGameUtils.resolveConnectionFailure(GameActivity.this,
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
        } else
            Snackbar.make(leaderboardText, "Please sign in to view the leaderboard", Snackbar.LENGTH_LONG).show();
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
}