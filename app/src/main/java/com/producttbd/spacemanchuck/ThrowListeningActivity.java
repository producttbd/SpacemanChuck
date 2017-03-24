package com.producttbd.spacemanchuck;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ThrowListeningActivity extends AppCompatActivity {

    public static final String THROW_RESULT_DEBUG = "com.producttbd.spacemanchuck.THROW_RESULT_DEBUG";
    public static final String THROW_RESULT_HEIGHT = "com.producttbd.spacemanchuck.THROW_RESULT_HEIGHT";

    private SensorManager mSensorManager;
    private ThrowListener mThrowListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_listening);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mThrowListener = new ThrowListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThrowListener.stopListening();
    }

    public void onReadyClick(View view) {
        mThrowListener.startListening();
    }

    public void setStateText(String state) {
        TextView textView = (TextView) findViewById(R.id.debugStateText);
        textView.setText(state);
    }

    private void SendResults(double height, String debugString) {
        mThrowListener.stopListening();
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(THROW_RESULT_DEBUG, debugString);
        intent.putExtra(THROW_RESULT_HEIGHT, height);
        startActivity(intent);
    }

    private class ThrowListener implements SensorEventListener {

        private static final double NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds
        private static final int NOT_STARTED = 0;
        private static final int LAUNCHING = 1;
        private static final int ZERO_GRAVITY = 2;
        private static final double LAUNCH_GRAVITY_THRESHOLD = 15.0;
        private static final double LAUNCH_SECONDS_THRESHOLD = 0.3;
        private static final double ZERO_GRAVITY_THRESHOLD = 2.0;

        private Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        private boolean mListening = false;
        private int mCurrentState = NOT_STARTED;
        private double mLaunchVelocity = 0.0;
        private double mLaunchStartTimestampSeconds = 0.0;
        private double mLastLaunchTimestampSeconds = 0.0;
        private double mZeroGravityStartTimestampSeconds = 0.0;

        public void startListening() {
            if (!mListening) {
                mSensorManager
                        .registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                mListening = true;
                setNotStartedState();
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
            if (magnitude < ZERO_GRAVITY_THRESHOLD) {
                setZeroGravityState(timestampSeconds);
            } if (magnitude < LAUNCH_GRAVITY_THRESHOLD
                    && (timestampSeconds - mLaunchStartTimestampSeconds) > LAUNCH_SECONDS_THRESHOLD) {
                setNotStartedState();
            } else { // Still launching, accumulate velocity
                mLaunchVelocity += (timestampSeconds - mLastLaunchTimestampSeconds) * magnitude;
                mLastLaunchTimestampSeconds = timestampSeconds;
            }
        }

        private void handleZeroGravityState(double timestampSeconds, double magnitude) {
            if (magnitude > ZERO_GRAVITY_THRESHOLD) {
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
                //sb.append("\nEstimated height from launch velocity: ");
                //sb.append()
                SendResults(height, sb.toString());
            }
        }

        private void setNotStartedState() {
            setStateText("not started"); // remove
            mCurrentState = NOT_STARTED;
        }

        private void setLaunchingState(double timestampSeconds, double magnitude) {
            setStateText("launching");
            mCurrentState = LAUNCHING;
            mLaunchVelocity = 0;
            mLaunchStartTimestampSeconds = timestampSeconds;
            mLastLaunchTimestampSeconds = timestampSeconds;
        }

        private void setZeroGravityState(double timestampSeconds) {
            setStateText("zero gravity");
            mCurrentState = ZERO_GRAVITY;
            mZeroGravityStartTimestampSeconds = timestampSeconds;
        }
    }
}
