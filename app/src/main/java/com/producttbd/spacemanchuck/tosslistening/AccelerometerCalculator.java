package com.producttbd.spacemanchuck.tosslistening;

import android.support.annotation.NonNull;

/**
 * Class to calculate the magnitude of an accelerometer sensor event.
 */
class AccelerometerCalculator {
    static double getMagnitude(@NonNull float[] values) {
        float total = 0.0f;
        for (float value : values) {
            total += value * value;
        }
        return Math.sqrt(total);
    }
}
