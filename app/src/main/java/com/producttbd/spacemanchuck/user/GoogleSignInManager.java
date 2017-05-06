package com.producttbd.spacemanchuck.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
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
    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    private SignInListener mListener = null;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    public GoogleSignInManager(Activity activity) {
        mActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }

    @Override
    public void setListener(SignInListener listener) {
        mListener = listener;
    }

    @Override
    public void onSignInRequested() {
        mSignInClicked = true;
        connect();
    }

    @Override
    public void onSignOutRequested() {
        mSignInClicked = false;
        disconnect();
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
        if (mListener != null) {
            mListener.onSignedIn();
        }
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

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            boolean resolved = false;
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
                    resolved = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mGoogleApiClient.connect();
                    resolved = false;
                }
            } else {
                // not resolvable... so show an error message
                int errorCode = connectionResult.getErrorCode();
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                        mActivity, RC_SIGN_IN);
                if (dialog != null) {
                    dialog.show();
                } else {
                    // no built-in dialog: show the fallback error message
                    if (mListener != null) {
                        mListener.onFailure();
                    }
                }
                resolved = false;
            }
            if (!resolved) {
                mResolvingConnectionFailure = false;
            }
        }

        // Sign-in failed
        if (mListener != null) {
            mListener.onFailure();
        }
    }
}
