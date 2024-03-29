package com.producttbd.spacemanchuck.tosslistening;

/**
 * Interface for a class that receives data points containing the magnitude of the accelerometer.
 */

interface AccelerometerMagnitudeListener {
    void reset();
    void onNewDataPoint(long timestampNanoseconds, double magnitude);
}
