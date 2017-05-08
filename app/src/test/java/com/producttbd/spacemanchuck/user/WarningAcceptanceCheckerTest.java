package com.producttbd.spacemanchuck.user;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * A test for the WarningAcceptanceChecker. We will mock the SharedPreferences and make sure this
 * class is returning the correct answers based on what the MockSharedPreferences returns and writes
 * to them as appropriate.
 */
public class WarningAcceptanceCheckerTest {

    private static final String KEY = "KEY";
    private static final long EIGHT_DAYS_MS = 8 * 24 * 60 * 60 * 1000;

    @Mock SharedPreferences mMockSharedPreferences;
    @Mock SharedPreferences.Editor mMockEditor;

    private WarningAcceptanceChecker mSystemUnderTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mSystemUnderTest = new WarningAcceptanceChecker(mMockSharedPreferences, KEY);
    }

    @Test
    public void setAcceptedWarning_writesToSharedPreferences() {
        // Verify that when the warning is accepted at time 500, the SUT writes it to the
        // MockSharedPreferences.
        long currentTimeMillis = 500;
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        when(mMockEditor.putLong(KEY, currentTimeMillis)).thenReturn(mMockEditor);

        mSystemUnderTest.setAcceptedWarning(currentTimeMillis);

        verify(mMockSharedPreferences).edit();
        verify(mMockEditor).putLong(KEY, currentTimeMillis);
        verify(mMockEditor).apply();
        verifyNoMoreInteractions(mMockSharedPreferences);
        verifyNoMoreInteractions(mMockEditor);
    }

    @Test
    public void shouldShowWarning_noValueInSharedPreferencesReturnsTrue() {
        // Set out MockSharedPreferences to return the default value given.
        when(mMockSharedPreferences.getLong(eq(KEY), anyLong())).thenAnswer(new Answer<Long>() {
            @NonNull
            @Override
            public Long answer(@NonNull InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Long) args[1];
            }
        });

        assertEquals(true, mSystemUnderTest.shouldShowWarning(0));
        assertEquals(true, mSystemUnderTest.shouldShowWarning(500));
        assertEquals(true, mSystemUnderTest.shouldShowWarning(EIGHT_DAYS_MS));
    }

    @Test
    public void shouldShowWarning_recentValueInSharedPreferencesReturnsFalse() {
        when(mMockSharedPreferences.getLong(KEY, -1l)).thenReturn(0l);

        assertEquals(false, mSystemUnderTest.shouldShowWarning(10l));
        assertEquals(false, mSystemUnderTest.shouldShowWarning(500l));
        assertEquals(true, mSystemUnderTest.shouldShowWarning(EIGHT_DAYS_MS));
    }

    @Test
    public void shouldShowWarning_elapsedTimeOfEightDaysReturnsTrue() {
        when(mMockSharedPreferences.getLong(KEY, -1l)).thenReturn(0l);

        assertEquals(true, mSystemUnderTest.shouldShowWarning(EIGHT_DAYS_MS));
    }
}