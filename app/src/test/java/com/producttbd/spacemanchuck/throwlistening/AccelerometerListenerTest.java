package com.producttbd.spacemanchuck.throwlistening;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for AccelerometerListener
 */
public class AccelerometerListenerTest {

    @Mock AccelerometerMagnitudeListener mMagnitudeListener;
    @Mock SensorManager mSensorManager;
    @Mock Sensor mSensor;
    AccelerometerListener mAccelerometerListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mSensorManager.getDefaultSensor(anyInt())).thenReturn(mSensor);
        when(mSensorManager.registerListener(isA(SensorEventListener.class), isA(Sensor.class),
                anyInt())).thenReturn(true);
        mAccelerometerListener = new AccelerometerListener(mSensorManager, mMagnitudeListener);
    }

    @Test
    public void testStartListening() {
        mAccelerometerListener.startListening();
        verify(mSensorManager).registerListener(isA(SensorEventListener.class), isA(Sensor.class)
                , anyInt());
        verify(mMagnitudeListener).reset();
    }

    @Test
    public void testStopListening() {
        mAccelerometerListener.startListening();
        mAccelerometerListener.stopListening();
        verify(mSensorManager).unregisterListener(isA(SensorEventListener.class));
    }

    @Test
    public void testOnSensorChanged_nonAccelerometerEvent() throws Exception {
        mAccelerometerListener.onSensorChanged(getNonAccelerometerEvent());
        verify(mMagnitudeListener, never()).onNewDataPoint(anyLong(), anyDouble());
    }

    @Test
    public void testOnSensorChanged_AccelerometerEventWithTooFewValues() throws Exception {
        mAccelerometerListener.onSensorChanged(getAccelerometerEventWithValues(new float[]{1.0f}));
        verify(mMagnitudeListener, never()).onNewDataPoint(anyLong(), anyDouble());
    }

    @Test
    public void testOnSensorChanged_AccelerometerEventWithCorrectValues() throws Exception {
        mAccelerometerListener.onSensorChanged(getAccelerometerEventWithValues(new float[]{1.0f,
                1.0f, 1.0f}));
        verify(mMagnitudeListener).onNewDataPoint(anyLong(), anyDouble());
    }

    @Test
    public void onAccuracyChanged() {

    }

    private SensorEvent getNonAccelerometerEvent() throws Exception {
        // Create the SensorEvent to eventually return.
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);
        // Get the 'sensor' field in order to set it.
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        // Create the value we want for the 'sensor' field.
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorField.set(sensorEvent, sensor);
        return sensorEvent;
    }

    private SensorEvent getAccelerometerEventWithValues(float[] desiredValues) throws Exception {
        // Create the SensorEvent to eventually return.
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        // Get the 'sensor' field in order to set it.
        Field sensorField = SensorEvent.class.getField("sensor");
        sensorField.setAccessible(true);
        // Create the value we want for the 'sensor' field.
        Sensor sensor = Mockito.mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);
        // Set the 'sensor' field.
        sensorField.set(sensorEvent, sensor);

        // Get the 'values' field in order to set it.
        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        // Create the values we want to return for the 'values' field.
        valuesField.set(sensorEvent, desiredValues);

        return sensorEvent;
    }
}