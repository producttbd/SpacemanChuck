package com.producttbd.spacemanchuck.tosslistening;

import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for TossStateTracker.
 */
public class TossStateTrackerTest {

    private static final long ZERO_TIME = 0L;
    private static final long TENTH_OF_A_SECOND_IN_NS = 100000000L;
    private static final long HALF_SECOND_IN_NS = 500000000L;
    private static final long TWO_SECONDS_IN_NS = 2000000000L;

    private class TestListener implements TossCompletedListener {
        boolean completed = false;
        double time = 0.0;
        double height = 0.0;
        @Nullable String debugText = null;

        @Override
        public void onTossCompleted(TossResult tossResult) {
            completed = true;
            this.time = tossResult.TimeSeconds;
            this.height = tossResult.HeightMeters;
            this.debugText = tossResult.DebugString;
        }
    }

    @Test
    public void testReset() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, tossStateTracker.getCurrentState());
        tossStateTracker.reset();
        Assert.assertEquals(0 /* NOT_STARTED */, tossStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_strongMagnitudeStartsLaunchState() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, tossStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_zeroGravityWithoutLaunchDoesNotWork() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 0.0); // Should trigger launching state.
        Assert.assertEquals(0 /* NOT_STARTED */, tossStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_returnToNormalAfterLaunchingDoesNotWork() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, tossStateTracker.getCurrentState());
        tossStateTracker.onNewDataPoint(HALF_SECOND_IN_NS, 9.0); // About normal gravity.
        Assert.assertEquals(0 /* NOT_STARTED */, tossStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_launchingThenZeroGravityWorks() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, tossStateTracker.getCurrentState());
        tossStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Zero gravity
        Assert.assertEquals(2 /* ZERO_GRAVITY */, tossStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_launchThenZeroGravityThenLandTriggersCallback() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        tossStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Flying in air.
        tossStateTracker.onNewDataPoint(2 * TENTH_OF_A_SECOND_IN_NS, 50.0); // Landed.
        Assert.assertTrue(testListener.completed);
        Assert.assertTrue(testListener.height > 0.001);
        Assert.assertNotNull(testListener.debugText);
    }

    @Test
    public void testOnNewDataPoint_twoSecondsZeroGravityGivesAppropriateHeight() {
        TestListener testListener = new TestListener();
        TossStateTracker tossStateTracker = new TossStateTracker(testListener);
        tossStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        tossStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Flying in air.
        tossStateTracker.onNewDataPoint(TWO_SECONDS_IN_NS + TENTH_OF_A_SECOND_IN_NS, 50.0); // Landed.
        Assert.assertTrue(testListener.completed);
        Assert.assertNotNull(testListener.debugText);
        // Two seconds in air means one second up, one second down. An object in free fall for one
        // second should fall about half of gravitational constant.
        Assert.assertEquals(testListener.height, 9.80665 / 2.0, 0.001);
    }
}