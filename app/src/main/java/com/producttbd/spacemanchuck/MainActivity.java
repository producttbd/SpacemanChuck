package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.producttbd.spacemanchuck.achievements.AchievementsManager;
import com.producttbd.spacemanchuck.tosslistening.TossResult;
import com.producttbd.spacemanchuck.user.GoogleSignInManager;
import com.producttbd.spacemanchuck.user.WarningAcceptanceChecker;

public class MainActivity extends AppCompatActivity implements WarningFragment
        .OnWarningFragmentInteractionListener, TossListeningFragment
        .OnTossListeningFragmentInteractionListener, ResultsFragment.OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private WarningFragment mWarningFragment;
    private TossListeningFragment mTossListeningFragment;
    private ResultsFragment mResultsFragment;

    private SharedPreferences mSharedPreferences;
    private WarningAcceptanceChecker mWarningAcceptanceChecker;

    private GoogleSignInManager mSignInManager;
    private AchievementsManager mAchievementsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWarningFragment = new WarningFragment();
        mTossListeningFragment = new TossListeningFragment();
        mResultsFragment = new ResultsFragment();

        mSharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);
        mWarningAcceptanceChecker = new WarningAcceptanceChecker(
                mSharedPreferences, getString(R.string.warning_accepted_time));

        mSignInManager = new GoogleSignInManager(this);
        mAchievementsManager = new AchievementsManager(
                mSignInManager.getGoogleApiClient(), mSharedPreferences, getResources());

        Fragment firstFragment = mWarningAcceptanceChecker.shouldShowWarning(System
                .currentTimeMillis()) ? mWarningFragment : mTossListeningFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                firstFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSignInManager.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Trigger animations here?
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mSignInManager.disconnect();
    }


    @Override
    public void onTossCompleted(TossResult tossResult) {
        mResultsFragment.setThrowResults(tossResult);
        mAchievementsManager.add(tossResult);
        switchToFragment(mResultsFragment);
    }

    @Override
    public void onWarningAccept() {
        Log.d(TAG, "onWarningAccept");
        mWarningAcceptanceChecker.setAcceptedWarning(System.currentTimeMillis());
        switchToFragment(mTossListeningFragment);
    }

    @Override
    public void onWarningReject() {
        Log.d(TAG, "onWarningReject");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onAchievementsRequested() {
        if (mSignInManager.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mSignInManager.getGoogleApiClient()),
                    5001);
        } else {
            // TODO handle error
        }
    }

    @Override
    public void onLeaderboardsRequested() {
        if (mSignInManager.isConnected()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mSignInManager.getGoogleApiClient()),
                    5001);
        } else {
            // TODO handle error
        }
    }

    @Override
    public void onRetryRequested() {
        switchToFragment(mTossListeningFragment);
    }

    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .addToBackStack(null).commit();
    }
}
