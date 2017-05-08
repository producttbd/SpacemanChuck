package com.producttbd.spacemanchuck.user;

import android.content.SharedPreferences;

/**
 * Class for determining if the user has requested Google sign in in the past.
 */

class SignInRequestedChecker {
    private final SharedPreferences mSharedPreferences;
    private final String mSignInRequestedKey;

    public SignInRequestedChecker(SharedPreferences sharedPreferences, String signInRequestedKey) {
        mSharedPreferences = sharedPreferences;
        mSignInRequestedKey = signInRequestedKey;
    }

    public boolean shouldSignInAutomatically() {
        return mSharedPreferences.getBoolean(mSignInRequestedKey, false);
    }

    public boolean shouldPromptToSignIn() {
        return !mSharedPreferences.contains(mSignInRequestedKey);
    }

    public void setSignInRequested(boolean signInRequested) {
        mSharedPreferences.edit().putBoolean(mSignInRequestedKey, signInRequested).apply();
    }
}
