package com.producttbd.spacemanchuck.user;

/**
 * Interface for a class to
 */

public interface SignInManager {

    void onSignInRequested();
    void onSignOutRequested();
    void connect();
    void disconnect();
    boolean isConnected();
}
