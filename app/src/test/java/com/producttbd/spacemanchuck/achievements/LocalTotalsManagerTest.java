package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for LocalTotalsManager.
 */
public class LocalTotalsManagerTest {

    // TODO Move to pref_strings.xml or not?
    private static final String HEIGHT_PREF_KEY = "com.producttbd.spacemanchuck.achievements" +
            ".CUMULATIVE_HEIGHT";
    private static final String TIME_PREF_KEY = "com.producttbd.spacemanchuck.achievements" +
            ".CUMULATIVE_TIME";

    @Mock SharedPreferences mMockSharedPreferences;
    @Mock SharedPreferences.Editor mMockEditor;
    LocalTotalsManager mSystemUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        when(mMockEditor.putFloat(anyString(), anyFloat())).thenReturn(mMockEditor);
        mSystemUnderTest = new LocalTotalsManager(mMockSharedPreferences);
    }

    @Test
    public void getCumulativeHeightToUpload_zeroReturnsZero() {
        testCombinesWritesAndRoundsValuesAppropriately(HEIGHT_PREF_KEY, 0.0f, 0.0f, 0);
    }

    @Test
    public void getCumulativeHeightToUpload_lessThanOneReturnsZero() {
        testCombinesWritesAndRoundsValuesAppropriately(HEIGHT_PREF_KEY, 0.0f, 0.98f, 0);
    }

    @Test
    public void getCumulativeHeightToUpload_greaterThanOneRoundsDown() {
        testCombinesWritesAndRoundsValuesAppropriately(HEIGHT_PREF_KEY, 0.0f, 5.98f, 5);
    }

    @Test
    public void getCumulativeHeightToUpload_combinesWithStoredValue() {
        testCombinesWritesAndRoundsValuesAppropriately(HEIGHT_PREF_KEY, 0.8f, 1.3f, 2);
    }

    @Test
    public void getCumulativeTimeToUpload_zeroReturnsZero() {
        testCombinesWritesAndRoundsValuesAppropriately(TIME_PREF_KEY, 0.0f, 0.0f, 0);
    }

    @Test
    public void getCumulativeTimeToUpload_lessThanOneReturnsZero() {
        testCombinesWritesAndRoundsValuesAppropriately(TIME_PREF_KEY, 0.0f, 0.98f, 0);
    }

    @Test
    public void getCumulativeTimeToUpload_greaterThanOneRoundsDown() {
        testCombinesWritesAndRoundsValuesAppropriately(TIME_PREF_KEY, 0.0f, 5.98f, 5);
    }

    @Test
    public void getCumulativeTimeToUpload_combinesWithStoredValue() {
        testCombinesWritesAndRoundsValuesAppropriately(TIME_PREF_KEY, 0.8f, 1.3f, 2);
    }

    private void testCombinesWritesAndRoundsValuesAppropriately(
            @NonNull String key, float currentlyStoredValue, float newValue, int expectedValueToUpload) {
        when(mMockSharedPreferences.getFloat(key, 0.0f)).thenReturn(currentlyStoredValue);


        int resultValue = key.equals(HEIGHT_PREF_KEY) ?
                mSystemUnderTest.getCumulativeHeightToUpload(newValue)
                : mSystemUnderTest.getCumulativeTimeToUpload(newValue);
        assertEquals(expectedValueToUpload, resultValue);

        verify(mMockSharedPreferences).getFloat(key, 0.0f);
        verify(mMockSharedPreferences).edit();
        verify(mMockEditor).putFloat(key, currentlyStoredValue + newValue);
        verify(mMockEditor).apply();
        verifyNoMoreInteractions(mMockSharedPreferences);
        verifyNoMoreInteractions(mMockEditor);
    }
}