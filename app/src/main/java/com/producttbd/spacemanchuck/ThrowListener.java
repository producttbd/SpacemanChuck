package com.producttbd.spacemanchuck;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Class for listening to the accelerometer and detecting throws.
 */

class ThrowListener implements SensorEventListener {

    private static final String TAG = ThrowListener.class.getSimpleName();
    private static final double NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds
    private static final int NOT_STARTED = 0;
    private static final int LAUNCHING = 1;
    private static final int ZERO_GRAVITY = 2;
    private static final double LAUNCH_GRAVITY_THRESHOLD = 25.0;
    private static final double LAUNCH_SECONDS_THRESHOLD = 0.3;
    private static final double ZERO_GRAVITY_START_THRESHOLD = 5.0;
    private static final double ZERO_GRAVITY_FINISH_THRESHOLD = 15.0;

    private SensorManager mSensorManager;
    private ThrowListenerCallback mCallback;
    private Sensor mAccelerometer;
    private boolean mListening = false;
    private int mCurrentState = NOT_STARTED;
    private double mLaunchVelocity = 0.0;
    private double mLaunchStartTimestampSeconds = 0.0;
    private double mLastLaunchTimestampSeconds = 0.0;
    private double mZeroGravityStartTimestampSeconds = 0.0;

    ThrowListener(SensorManager sensorManager, ThrowListenerCallback callback) {
        mCallback = callback;
        mSensorManager = sensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void startListening() {
        if (!mListening) {
            mSensorManager
                    .registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

            mListening = true;
            setThrowNotStartedState();
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
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float total = 0.0f;
        for (int i = 0; i < event.values.length; ++i) {
            total += event.values[i] * event.values[i];
        }
        updateState(event.timestamp * NS2S, Math.sqrt(total));
    }

    /** For SensorEventListener */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateState(double timestampSeconds, double magnitude) {
        switch (mCurrentState) {
            case NOT_STARTED:
                handleNotStartedState(timestampSeconds, magnitude);
                break;
            case LAUNCHING:
                handleLaunchingState(timestampSeconds, magnitude);
                break;
            case ZERO_GRAVITY:
                handleZeroGravityState(timestampSeconds, magnitude);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void handleNotStartedState(double timestampSeconds, double magnitude) {
        if (magnitude > LAUNCH_GRAVITY_THRESHOLD) {
            setLaunchingState(timestampSeconds, magnitude);
        }
    }

    private void handleLaunchingState(double timestampSeconds, double magnitude) {
        if (magnitude < ZERO_GRAVITY_START_THRESHOLD) {
            setZeroGravityState(timestampSeconds);
        }
        if (magnitude < LAUNCH_GRAVITY_THRESHOLD
                && (timestampSeconds - mLaunchStartTimestampSeconds) > LAUNCH_SECONDS_THRESHOLD) {
            setThrowNotStartedState();
        } else { // Still launching, accumulate velocity
            mLaunchVelocity += (timestampSeconds - mLastLaunchTimestampSeconds) * magnitude;
            mLastLaunchTimestampSeconds = timestampSeconds;
        }
    }

    private void handleZeroGravityState(double timestampSeconds, double magnitude) {
        if (magnitude > ZERO_GRAVITY_FINISH_THRESHOLD) {
            stopListening();
            StringBuilder sb = new StringBuilder();
            sb.append("Finished!\n");
            sb.append("Launch Vel.: ");
            sb.append(mLaunchVelocity);
            sb.append("\nTotal air time: ");
            double flightTime = timestampSeconds - mZeroGravityStartTimestampSeconds;
            sb.append(flightTime);
            sb.append("\nEstimated height from time: ");
            double height = 9.81 / 2.0 * flightTime * flightTime / 4.0;
            sb.append(height);
            // TODO
            //sb.append("\nEstimated height from launch velocity: ");
            //sb.append()
            mCallback.onThrowCompleted(height, sb.toString());
        }
    }

    private void setThrowNotStartedState() {
        logStateText("not started");
        mCurrentState = NOT_STARTED;
    }

    private void setLaunchingState(double timestampSeconds, double magnitude) {
        logStateText("launching");
        mCurrentState = LAUNCHING;
        mLaunchVelocity = 0;
        mLaunchStartTimestampSeconds = timestampSeconds;
        mLastLaunchTimestampSeconds = timestampSeconds;
    }

    private void setZeroGravityState(double timestampSeconds) {
        logStateText("zero gravity");
        mCurrentState = ZERO_GRAVITY;
        mZeroGravityStartTimestampSeconds = timestampSeconds;
    }

    private void logStateText(String state) {
        Log.d(TAG, state);
    }

    interface ThrowListenerCallback {
        void onThrowCompleted(double height, String debugString);
    }
}