package com.producttbd.spacemanchuck.throwlistening;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Class for listening to the accelerometer and passing the events to a supplied listener.
 */

public class AccelerometerListener implements SensorEventListener {

    final private static String TAG = AccelerometerListener.class.getSimpleName();
    final private SensorManager mSensorManager;
    final private AccelerometerMagnitudeListener mMagnitudeListener;
    final private Sensor mAccelerometer;
    private boolean mListening = false;


    public AccelerometerListener(SensorManager sensorManager,
                          AccelerometerMagnitudeListener magnitudeListener) {
        mSensorManager = sensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnitudeListener = magnitudeListener;
    }

    public void startListening() {
        if (!mListening) {
            boolean success = mSensorManager
                    .registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            if (success) {
                mListening = true;
                mMagnitudeListener.reset();
            } else {
                Log.e(TAG, "Could not register accelerometer.");
            }
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
    public void onSensorChanged(@NonNull SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || event.values.length != 3) {
            return;
        }
        mMagnitudeListener.onNewDataPoint(event.timestamp,
                                          AccelerometerCalculator.getMagnitude(event.values));
    }

    /** For SensorEventListener */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}