package com.producttbd.spacemanchuck.user;

/**
 * Interface that can receive events for when the user explicitly requests signing in or out.
 */

public interface SignInOutRequestListener {
    void onSignInRequested();
    void onSignOutRequested();
}
