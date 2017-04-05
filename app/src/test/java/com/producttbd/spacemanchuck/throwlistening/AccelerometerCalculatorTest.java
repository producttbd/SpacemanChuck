package com.producttbd.spacemanchuck.throwlistening;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for AccelerometerCalculator.
 */
public class AccelerometerCalculatorTest {

    @Test
    public void getMagnitude() {
        Assert.assertEquals(
                1.0, AccelerometerCalculator.getMagnitude(new float[]{ 1.0f }), 0.00001);
        Assert.assertEquals(
                1.0, AccelerometerCalculator.getMagnitude(new float[]{ -1.0f }), 0.00001);
        Assert.assertEquals(
                5.0, AccelerometerCalculator.getMagnitude(new float[]{ 3.0f, -4.0f }), 0.00001);
        Assert.assertEquals(13.0,
                AccelerometerCalculator.getMagnitude(new float[]{3.0f, 4.0f, 12.0f}), 0.00001);
    }
}