package com.producttbd.spacemanchuck.tosslistening;

/**
 * Interface to implement to be notified with the height when a throw is finished.
 */

public interface TossCompletedListener {
    void onTossCompleted(TossResult tossResult);
}
