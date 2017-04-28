package com.producttbd.spacemanchuck.tosslistening;

import android.support.annotation.Nullable;

/**
 * Simple data class for the results of a throw.
 */

public class TossResult {
    public final double HeightMeters;
    public final double TimeSeconds;
    @Nullable final public String DebugString;

    TossResult(double timeSeconds, double heightMeters, @Nullable String debugString) {
        HeightMeters = heightMeters;
        TimeSeconds = timeSeconds;
        DebugString = debugString;
    }
}
