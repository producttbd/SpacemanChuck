package com.producttbd.spacemanchuck.user;

/**
 * Created by t on 4/30/17.
 */

public interface SignInManager {
    void setListener(SignInListener listener);

    void onSignInRequested();
    void onSignOutRequested();

    void connect();
    void disconnect();
    boolean isConnected();

    public interface SignInListener {
        void onSignedIn();
        void onSignedOut();
        void onFailure();
    }
}
