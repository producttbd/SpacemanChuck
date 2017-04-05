package com.producttbd.spacemanchuck.throwlistening;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class for listening to the accelerometer and detecting throws.
 */

public class AccelerometerListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private AccelerometerMagnitudeListener mMagnitudeListener;
    private Sensor mAccelerometer;
    private boolean mListening = false;


    public AccelerometerListener(SensorManager sensorManager,
                          AccelerometerMagnitudeListener magnitudeListener) {
        mSensorManager = sensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnitudeListener = magnitudeListener;
    }

    public void startListening() {
        if (!mListening) {
            mSensorManager
                    .registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

            mListening = true;
            mMagnitudeListener.reset();
        }
    }

    public void stopListening() {
        if (mListening) {
            mSensorManager.unregisterListener(this);
            mListening = false;
        }
    }

    /** For SensorEventListener */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length != 3) {
            return;
        }
        mMagnitudeListener.onNewDataPoint(event.timestamp,
                                          AccelerometerCalculator.getMagnitude(event));
    }

    /** For SensorEventListener */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}