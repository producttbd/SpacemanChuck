package com.producttbd.spacemanchuck.user;

/**
 * Interface to wrap a connection API.
 */

public interface SignInManager {

    // request codes we use when invoking an external activity
    int RC_RESOLVE = 5000;
    int RC_UNUSED = 5001;
    int RC_SIGN_IN = 9001;

    /**
     * Checks previous state and connects, re-connects, does not connect, or calls
     * createPromptToSignIn as appropriate depending on previous state and user preference.
     * To be called in Activity.onStart.
     */
    void start();

    /**
     * Disconnects any connected client. To be called in Activity.onStop.
     */
    void stop();

    /**
     * Returns true if actively connected.
     */
    boolean isConnected();

    /**
     * To be called when the user has requested or accepted a signing in.
     */
    void onSignInRequested();

    /**
     * To be called when the user has requested signing out.
     */
    void onSignOutRequested();

//    Intent getAchievementsIntent();
//
//    Intent getLeaderboardIntent();

    interface SignInListener {
        void createPromptToSignIn();

        void onSignedIn();

        void onSignedOut();

        void onFailure();
    }
}
