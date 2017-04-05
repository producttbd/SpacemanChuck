package com.producttbd.spacemanchuck.throwlistening;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for ThrowStateTracker.
 */
public class ThrowStateTrackerTest {

    private static final long ZERO_TIME = 0L;
    private static final long TENTH_OF_A_SECOND_IN_NS = 100000000L;
    private static final long HALF_SECOND_IN_NS = 500000000L;
    private static final long TWO_SECONDS_IN_NS = 2000000000L;

    private class TestListener implements ThrowCompletedListener {
        boolean completed = false;
        double height = 0.0;
        String debugText = null;

        @Override
        public void onThrowCompleted(double height, String debugText) {
            completed = true;
            this.height = height;
            this.debugText = debugText;
        }
    }

    @Test
    public void testReset() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, throwStateTracker.getCurrentState());
        throwStateTracker.reset();
        Assert.assertEquals(0 /* NOT_STARTED */, throwStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_strongMagnitudeStartsLaunchState() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, throwStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_zeroGravityWithoutLaunchDoesNotWork() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 0.0); // Should trigger launching state.
        Assert.assertEquals(0 /* NOT_STARTED */, throwStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_returnToNormalAfterLaunchingDoesNotWork() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, throwStateTracker.getCurrentState());
        throwStateTracker.onNewDataPoint(HALF_SECOND_IN_NS, 9.0); // About normal gravity.
        Assert.assertEquals(0 /* NOT_STARTED */, throwStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_launchingThenZeroGravityWorks() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        Assert.assertEquals(1 /* LAUNCHING */, throwStateTracker.getCurrentState());
        throwStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Zero gravity
        Assert.assertEquals(2 /* ZERO_GRAVITY */, throwStateTracker.getCurrentState());
    }

    @Test
    public void testOnNewDataPoint_launchThenZeroGravityThenLandTriggersCallback() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        throwStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Flying in air.
        throwStateTracker.onNewDataPoint(2 * TENTH_OF_A_SECOND_IN_NS, 50.0); // Landed.
        Assert.assertTrue(testListener.completed);
        Assert.assertTrue(testListener.height > 0.001);
        Assert.assertNotNull(testListener.debugText);
    }

    @Test
    public void testOnNewDataPoint_twoSecondsZeroGravityGivesAppropriateHeight() {
        TestListener testListener = new TestListener();
        ThrowStateTracker throwStateTracker = new ThrowStateTracker(testListener);
        throwStateTracker.onNewDataPoint(ZERO_TIME, 50.0); // Should trigger launching state.
        throwStateTracker.onNewDataPoint(TENTH_OF_A_SECOND_IN_NS, 0.0); // Flying in air.
        throwStateTracker.onNewDataPoint(TWO_SECONDS_IN_NS + TENTH_OF_A_SECOND_IN_NS, 50.0); // Landed.
        Assert.assertTrue(testListener.completed);
        Assert.assertNotNull(testListener.debugText);
        // Two seconds in air means one second up, one second down. An object in free fall for one
        // second should fall about half of gravitational constant.
        Assert.assertEquals(testListener.height, 9.80665 / 2.0, 0.001);
    }
}