package com.producttbd.spacemanchuck.throwlistening;

/**
 * Interface for a class that receives data points containing the magnitude of the accelerometer.
 */

public interface AccelerometerMagnitudeListener {
    public void reset();
    public void onNewDataPoint(double timestampNanoseconds, double magnitude);
}
