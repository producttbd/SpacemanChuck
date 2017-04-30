package com.producttbd.spacemanchuck.achievements;

import com.producttbd.spacemanchuck.tosslistening.TossResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.when;

/**
 * Tests for AchievementTossCollector.
 */
public class AchievementTossCollectorTest {

    @Mock TotalsManager mMockTotalsManager;
    AchievementTossCollector mSystemUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mSystemUnderTest = new AchievementTossCollector(mMockTotalsManager);
    }

    @Test
    public void add_oneFlightNoPreviousSetsLeaderboardAndIncremental() {
        double time = 1.4;
        double height = 3.2;
        TossResult tossResult = new TossResult(time, height, /* debugString */ null);
        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(height)).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(time)).thenReturn(timeToUpload);


        mSystemUnderTest.add(tossResult);
        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(3.2, outbox.LeaderboardBestHeight, 0.0001);
        assertEquals(1, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertFalse(outbox.ThreeSuccessivelyHigherFlights);
        assertFalse(outbox.TenSuccessivelyHigherFlights);
        assertFalse(outbox.FifteenSuccessivelyHigherFlights);
        assertFalse(outbox.TwentySuccessivelyHigherFlights);
    }

    @Test
    public void add_threeFlightsSetsLeaderboardAndIncremental() {
        TossResult tossResult1 = new TossResult(/*time*/ 1.4, /*height*/ 3.1, /*debugString*/ null);
        TossResult tossResult2 = new TossResult(/*time*/ 1.4, /*height*/ 3.2, /*debugString*/ null);
        TossResult tossResult3 = new TossResult(/*time*/ 1.4, /*height*/ 0.4, /*debugString*/ null);

        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(anyDouble())).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(anyDouble())).thenReturn(timeToUpload);

        mSystemUnderTest.add(tossResult1);
        mSystemUnderTest.add(tossResult2);
        mSystemUnderTest.add(tossResult3);

        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(3.2, outbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(3, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertFalse(outbox.ThreeSuccessivelyHigherFlights);
        assertFalse(outbox.TenSuccessivelyHigherFlights);
        assertFalse(outbox.FifteenSuccessivelyHigherFlights);
        assertFalse(outbox.TwentySuccessivelyHigherFlights);
    }

    @Test
    public void add_threeSuccessivelyHigherFlightsAchievements() {
        TossResult tossResult1 = new TossResult(/*time*/ 1.4, /*height*/ 3.1, /*debugString*/ null);
        TossResult tossResult2 = new TossResult(/*time*/ 1.4, /*height*/ 3.2, /*debugString*/ null);
        TossResult tossResult3 = new TossResult(/*time*/ 1.4, /*height*/ 3.3, /*debugString*/ null);

        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(anyDouble())).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(anyDouble())).thenReturn(timeToUpload);


        mSystemUnderTest.add(tossResult1);
        mSystemUnderTest.add(tossResult2);
        mSystemUnderTest.add(tossResult3);

        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(3.3, outbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(3, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertTrue(outbox.ThreeSuccessivelyHigherFlights);
        assertFalse(outbox.TenSuccessivelyHigherFlights);
        assertFalse(outbox.FifteenSuccessivelyHigherFlights);
        assertFalse(outbox.TwentySuccessivelyHigherFlights);
    }

    @Test
    public void add_tenSuccessivelyHigherFlightsAchievements() {
        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(anyDouble())).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(anyDouble())).thenReturn(timeToUpload);

        double height = 0.1;

        for (int i = 0; i < 10; ++i) {
            TossResult tossResult = new TossResult(/*time*/ 0.3, height, null);
            mSystemUnderTest.add(tossResult);
            height += 0.1;
        }

        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(1.0, outbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(10, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertTrue(outbox.ThreeSuccessivelyHigherFlights);
        assertTrue(outbox.TenSuccessivelyHigherFlights);
        assertFalse(outbox.FifteenSuccessivelyHigherFlights);
        assertFalse(outbox.TwentySuccessivelyHigherFlights);
    }

    @Test
    public void add_fifteenSuccessivelyHigherFlightsAchievements() {
        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(anyDouble())).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(anyDouble())).thenReturn(timeToUpload);

        double height = 0.1;

        for (int i = 0; i < 15; ++i) {
            TossResult tossResult = new TossResult(/*time*/ 0.3, height, null);
            mSystemUnderTest.add(tossResult);
            height += 0.1;
        }

        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(1.5, outbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(15, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertTrue(outbox.ThreeSuccessivelyHigherFlights);
        assertTrue(outbox.TenSuccessivelyHigherFlights);
        assertTrue(outbox.FifteenSuccessivelyHigherFlights);
        assertFalse(outbox.TwentySuccessivelyHigherFlights);
    }

    @Test
    public void add_twentySuccessivelyHigherFlightsAchievements() {
        int heightToUpload = 3;
        int timeToUpload = 2;
        when(mMockTotalsManager.getCumulativeHeightToUpload(anyDouble())).thenReturn(heightToUpload);
        when(mMockTotalsManager.getCumulativeTimeToUpload(anyDouble())).thenReturn(timeToUpload);

        double height = 0.1;

        for (int i = 0; i < 20; ++i) {
            TossResult tossResult = new TossResult(/*time*/ 0.3, height, null);
            mSystemUnderTest.add(tossResult);
            height += 0.1;
        }

        AchievementOutbox outbox = mSystemUnderTest.getAchievementOutbox();

        assertEquals(2.0, outbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(20, outbox.FlightsToUpload);
        assertEquals(heightToUpload, outbox.FlightMetersToUpload);
        assertEquals(timeToUpload, outbox.FlightSecondsToUpload);
        assertTrue(outbox.ThreeSuccessivelyHigherFlights);
        assertTrue(outbox.TenSuccessivelyHigherFlights);
        assertTrue(outbox.FifteenSuccessivelyHigherFlights);
        assertTrue(outbox.TwentySuccessivelyHigherFlights);
    }
}