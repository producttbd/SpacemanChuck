package com.producttbd.spacemanchuck;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.producttbd.spacemanchuck.achievements.AchievementsCoordinator;
import com.producttbd.spacemanchuck.tosslistening.TossResult;
import com.producttbd.spacemanchuck.user.GoogleSignInManager;
import com.producttbd.spacemanchuck.user.SignInDialogFragment;
import com.producttbd.spacemanchuck.user.SignInManager;
import com.producttbd.spacemanchuck.user.WarningAcceptanceChecker;

public class MainActivity extends AppCompatActivity implements
        WarningFragment.OnWarningFragmentInteractionListener,
        TossListeningFragment.OnTossListeningFragmentInteractionListener,
        ResultsFragment.OnResultsFragmentInteractionListener,
        SignInManager.SignInListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private TossListeningFragment mTossListeningFragment;
    private ResultsFragment mResultsFragment;

    private WarningAcceptanceChecker mWarningAcceptanceChecker;

    private GoogleSignInManager mSignInManager;
    private AchievementsCoordinator mAchievementsCoordinator;

    // ===== Lifecycle overrides =====
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTossListeningFragment = new TossListeningFragment();
        mResultsFragment = new ResultsFragment();

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);
        mSignInManager = new GoogleSignInManager(this, this);
        mAchievementsCoordinator = new AchievementsCoordinator(
                mSignInManager, sharedPreferences, getResources());
        mWarningAcceptanceChecker = new WarningAcceptanceChecker(
                sharedPreferences, getString(R.string.warning_accepted_time));

        Fragment firstFragment;
        if (mWarningAcceptanceChecker.shouldShowWarning(System.currentTimeMillis())) {
            // Only create this fragment if needed.
            firstFragment = new WarningFragment();
        } else {
            firstFragment = mTossListeningFragment;
        }
        // TODO There is a comment in the TypeANumber example talking about supporting rotation and
        // that being problematic---it could cause overlapping fragments.
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                firstFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSignInManager.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Trigger animations here?
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSignInManager.stop();
    }

    // ===== WarningFragment.OnWarningFragmentInteractionListener implementations =====
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

    // ===== TossListeningFragment.OnTossListeningFragmentInteractionListener implementations =====
    @Override
    public void onTossCompleted(TossResult tossResult) {
        mResultsFragment.setThrowResults(tossResult);
        mAchievementsCoordinator.add(tossResult);
        switchToFragment(mResultsFragment);
    }

    // ===== ResultsFragment.OnResultsFragmentInteractionListener implementations =====

    @Override
    public void onSignInRequested() {
        signIn();
    }

    @Override
    public void onSignOutRequested() {
        signOut();
    }

    @Override
    public void onAchievementsRequested() {
        Log.d(TAG, "onAchievementsRequested");
        if (mSignInManager.isConnected()) {
            startActivityForResult(
                    Games.Achievements.getAchievementsIntent(mSignInManager.getGoogleApiClient()),
                    SignInManager.RC_UNUSED);
        } else {
            // TODO handle error
        }
    }

    @Override
    public void onLeaderboardsRequested() {
        Log.d(TAG, "onLeaderboardsRequested");
        if (mSignInManager.isConnected()) {
            startActivityForResult(
                    Games.Leaderboards.getAllLeaderboardsIntent(mSignInManager.getGoogleApiClient()),
                    SignInManager.RC_UNUSED);
        } else {
            // TODO handle error
        }
    }

    @Override
    public void onRetryRequested() {
        switchToFragment(mTossListeningFragment);
        mTossListeningFragment.jumpToListening();
    }

    // ===== SignInManager.SignInListener implementations =====
    @Override
    public void createPromptToSignIn() {
        Log.d(TAG, "createPromptToSignIn");
        DialogFragment dialogFragment = new SignInDialogFragment();
        dialogFragment.show(getFragmentManager(), "signin");
    }

    @Override
    public void onSignedIn() {
        mResultsFragment.setSignedIn(true);
    }

    @Override
    public void onSignedOut() {
        mResultsFragment.setSignedIn(false);
    }

    @Override
    public void onFailure() {
        // TODO Show UI change or toast?
        mResultsFragment.setSignedIn(false);
    }

    private void switchToFragment(Fragment newFrag) {
        Log.d(TAG, "switchToFragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .addToBackStack(null).commit();
    }

    private void signIn() {
        mSignInManager.onSignInRequested();
    }

    private void signOut() {
        mSignInManager.onSignOutRequested();
    }
}
