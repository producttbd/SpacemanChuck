package com.producttbd.spacemanchuck;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements WarningFragment.OnWarningFragmentInteractionListener,
                   TossListeningFragment.OnTossListeningFragmentInteractionListener,
                   ResultsFragment.OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private WarningFragment mWarningFragment;
    private TossListeningFragment mTossListeningFragment;
    private ResultsFragment mResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWarningFragment = new WarningFragment();
        mTossListeningFragment = new TossListeningFragment();
        mResultsFragment = new ResultsFragment();

        // add initial fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mWarningFragment).commit();

    }

    @Override
    public void onThrowCompleted(double height, String debugString) {
        mResultsFragment.setThrowResults(height, debugString);
        switchToFragment(mResultsFragment);
    }

    @Override
    public void onWarningAccept() {
        Log.d(TAG, "onWarningAccept");
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
    public void onRetryRequested() {
        switchToFragment(mTossListeningFragment);
    }

    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .addToBackStack(null)
                .commit();
    }
}
