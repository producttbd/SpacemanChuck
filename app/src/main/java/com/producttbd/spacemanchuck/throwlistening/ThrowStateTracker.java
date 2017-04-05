package com.producttbd.spacemanchuck.throwlistening;

import android.util.Log;

/**
 * Keeps track of a throw based on timestamped accelerometer events.
 */
public class ThrowStateTracker implements AccelerometerMagnitudeListener {
    private static final String TAG = ThrowStateTracker.class.getSimpleName();
    private static final double HALF_STANDARD_GRAVITY = 9.80665 / 2.0;
    private static final double NS2S = 1.0f / 1000000000.0f; // Nanoseconds to seconds
    private static final int NOT_STARTED = 0;
    private static final int LAUNCHING = 1;
    private static final int ZERO_GRAVITY = 2;
    private static final double LAUNCH_GRAVITY_THRESHOLD = 25.0;
    private static final double LAUNCH_SECONDS_THRESHOLD = 0.3;
    private static final double ZERO_GRAVITY_START_THRESHOLD = 5.0;
    private static final double ZERO_GRAVITY_FINISH_THRESHOLD = 15.0;

    private int mCurrentState = NOT_STARTED;
    private double mLaunchVelocity = 0.0;
    private double mLaunchStartTimestampSeconds = 0.0;
    private double mLastLaunchTimestampSeconds = 0.0;
    private double mZeroGravityStartTimestampSeconds = 0.0;
    private final ThrowCompletedListener mListener;

    public ThrowStateTracker(ThrowCompletedListener listener) {
        mListener = listener;
    }

    int getCurrentState() {
        return mCurrentState;
    }

    /** For AccelerometerMagnitudeListener */
    @Override
    public void reset() {
        setThrowNotStartedState();
    }

    /** For AccelerometerMagnitudeListener */
    @Override
    public void onNewDataPoint(long timestampNanoseconds, double magnitude) {
        double timestampSeconds = timestampNanoseconds * NS2S;
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
                Log.e(TAG, "Currently in invalid state.");
                setThrowNotStartedState();
                handleNotStartedState(timestampSeconds, magnitude);
        }
    }

    private void handleNotStartedState(double timestampSeconds, double magnitude) {
        if (magnitude > LAUNCH_GRAVITY_THRESHOLD) {
            setLaunchingState(timestampSeconds);
        }
    }

    private void handleLaunchingState(double timestampSeconds, double magnitude) {
        if (magnitude < ZERO_GRAVITY_START_THRESHOLD) {
            setZeroGravityState(timestampSeconds);
        } else if (magnitude < LAUNCH_GRAVITY_THRESHOLD
                && (timestampSeconds - mLaunchStartTimestampSeconds) > LAUNCH_SECONDS_THRESHOLD) {
            setThrowNotStartedState();
        } else { // Still launching, accumulate velocity
            mLaunchVelocity += (timestampSeconds - mLastLaunchTimestampSeconds) * magnitude;
            mLastLaunchTimestampSeconds = timestampSeconds;
        }
    }

    private void handleZeroGravityState(double timestampSeconds, double magnitude) {
        if (magnitude > ZERO_GRAVITY_FINISH_THRESHOLD) {
            double flightTime = timestampSeconds - mZeroGravityStartTimestampSeconds;
            // x = (1/2)acceleration * pow(t, 2)
            // Assume half of flight is up, half of flight is down so only use half of the time:
            // height = g/2 * pow(flightTime/2.0, 2)
            double halfTime = flightTime / 2.0;
            double height = HALF_STANDARD_GRAVITY * halfTime * halfTime;
            // TODO
            String debug = "Finished!\n" +
                    "Launch Vel.: " + mLaunchVelocity +
                    "\nTotal air time: " + flightTime +
                    "\nEstimated height from time: " + height;
            //sb.append("\nEstimated height from launch velocity: ");
            //sb.append()
            mListener.onThrowCompleted(height, debug);
        }
    }

    private void setThrowNotStartedState() {
        logStateText("not started");
        mCurrentState = NOT_STARTED;
    }

    private void setLaunchingState(double timestampSeconds) {
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


}
