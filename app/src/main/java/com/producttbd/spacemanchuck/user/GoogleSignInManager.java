package com.producttbd.spacemanchuck.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Class for managing interactions with GoogleApiClient
 */

public class GoogleSignInManager implements SignInManager, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleSignInManager.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;

    GoogleSignInManager(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    public void onSignInRequested() {

    }

    public void onSignOutRequested() {

    }

    @Override
    public void connect() {
        mGoogleApiClient.connect();
    }

    @Override
    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        // TODO Things like update UI, upload achievements
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

//        if (mSignInClicked || mAutoStartSignInFlow) {
//            mAutoStartSignInFlow = false;
//            mSignInClicked = false;
//            mResolvingConnectionFailure = true;
//            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
//                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
//                mResolvingConnectionFailure = false;
//            }
//        }
//
//        // Sign-in failed, so show sign-in button on main menu
//        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
//        mMainMenuFragment.setShowSignInButton(true);
//        mWinFragment.setShowSignInButton(true);
    }
}
