package com.producttbd.spacemanchuck.throwlistening;

/**
 * Interface to implement to be notified with the height when a throw is finished.
 */

public interface ThrowCompletedListener {
    void onThrowCompleted(double height, String debugText);
}
