package com.producttbd.spacemanchuck.throwlistening;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Class to calculate the magnitude of an accelerometer sensor event.
 */
public class AccelerometerCalculator {
    public static double getMagnitude(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length != 3) {
            throw new RuntimeException("Improper type of SensorEvent to Accelerometer Calculator.");
        }
        float total = 0.0f;
        for (int i = 0; i < event.values.length; ++i) {
            total += event.values[i] * event.values[i];
        }
        return Math.sqrt(total);
    }
}
