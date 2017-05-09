package com.producttbd.spacemanchuck.user;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.producttbd.spacemanchuck.R;

/**
 * Class for managing interactions with GoogleApiClient
 */

public class GoogleSignInManager implements SignInManager, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleSignInManager.class.getSimpleName();
    @NonNull private final Activity mActivity;
    @NonNull private final GoogleApiClient mGoogleApiClient;
    @NonNull private final SignInRequestedChecker mSignInRequestedChecker;
    @NonNull private final SignInListener mListener;
    private boolean mResolvingConnectionFailure = false;

    public GoogleSignInManager(@NonNull Activity activity, @NonNull SignInListener listener) {
        mActivity = activity;
        mListener = listener;

        String sharedPreferencesKey = activity.getString(R.string.preference_file_key);
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE);
        mSignInRequestedChecker = new SignInRequestedChecker(
                sharedPreferences, activity.getString(R.string.sign_in_requested));
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @NonNull
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void start() {
        if (mSignInRequestedChecker.shouldPromptToSignIn()) {
            mListener.createPromptToSignIn();
        } else if (mSignInRequestedChecker.shouldSignInAutomatically()) {
            connect();
        }
    }

    @Override
    public void stop() {
        disconnect();
    }

    @Override
    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onSignInRequested() {
        mSignInRequestedChecker.setSignInRequested(true);
        connect();
    }

    @Override
    public void onSignOutRequested() {
        mSignInRequestedChecker.setSignInRequested(false);
        disconnect();
    }

//    Intent getAchievementsIntent() {
//
//    }
//
//    Intent getLeaderboardIntent() {
//        return Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient, RC_UNUSED);
//    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        mListener.onSignedIn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInRequestedChecker.shouldSignInAutomatically()) {
            mResolvingConnectionFailure = true;

            boolean resolved = false;
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
                    resolved = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    connect();
                    resolved = false;
                }
            } else {
                // not resolvable... so show an error message
                int errorCode = connectionResult.getErrorCode();
                // TODO look into this
                boolean dialogShown = GooglePlayServicesUtil.showErrorDialogFragment(errorCode,
                        mActivity, RC_SIGN_IN);
                if (!dialogShown) {
                    mListener.onFailure();
                }
                resolved = false;
            }
            if (!resolved) {
                mResolvingConnectionFailure = false;
            }
        }

        // Sign-in failed
        mListener.onFailure();
    }

    private void connect() {
        mGoogleApiClient.connect();
    }

    private void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
