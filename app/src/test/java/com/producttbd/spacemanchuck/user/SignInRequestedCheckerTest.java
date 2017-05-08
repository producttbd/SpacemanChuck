package com.producttbd.spacemanchuck.user;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SignInRequestedChecker} to verify reading and writing to SharedPreferences
 */
public class SignInRequestedCheckerTest {

    private static final String KEY = "KEY";

    @Mock SharedPreferences mMockSharedPreferences;
    @Mock SharedPreferences.Editor mMockEditor;

    private SignInRequestedChecker mSystemUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mSystemUnderTest = new SignInRequestedChecker(mMockSharedPreferences, KEY);
    }

    @Test
    public void shouldSignInAutomatically_falseRecordReturnsFalse() {
        when(mMockSharedPreferences.getBoolean(KEY, false)).thenReturn(false);
        assertFalse(mSystemUnderTest.shouldSignInAutomatically());
    }

    @Test
    public void shouldSignInAutomatically_trueRecordReturnsTrue() {
        when(mMockSharedPreferences.getBoolean(KEY, false)).thenReturn(true);
        assertTrue(mSystemUnderTest.shouldSignInAutomatically());
    }

    @Test
    public void shouldPromptToSignIn_noKeyReturnsTrue() {
        when(mMockSharedPreferences.contains(KEY)).thenReturn(false);
        assertTrue(mSystemUnderTest.shouldPromptToSignIn());
    }

    @Test
    public void shouldPromptToSignIn_keyReturnsFalse() {
        when(mMockSharedPreferences.contains(KEY)).thenReturn(true);
        assertFalse(mSystemUnderTest.shouldPromptToSignIn());
    }

    @Test
    public void setSignInRequested() throws Exception {
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        when(mMockEditor.putBoolean(eq(KEY), anyBoolean())).thenReturn(mMockEditor);

        mSystemUnderTest.setSignInRequested(true);
        verify(mMockSharedPreferences).edit();
        verify(mMockEditor).putBoolean(KEY, true);
        verify(mMockEditor).apply();
        verifyNoMoreInteractions(mMockSharedPreferences);
        verifyNoMoreInteractions(mMockEditor);
    }

}