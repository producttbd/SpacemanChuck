package com.producttbd.spacemanchuck.throwlistening;

/**
 * Class to calculate the magnitude of an accelerometer sensor event.
 */
class AccelerometerCalculator {
    static double getMagnitude(float[] values) {
        float total = 0.0f;
        for (float value : values) {
            total += value * value;
        }
        return Math.sqrt(total);
    }
}
