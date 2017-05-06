package com.producttbd.spacemanchuck.achievements;

import android.content.res.Resources;

import com.producttbd.spacemanchuck.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AchievementUploader}.
 */
public class AchievementUploaderTest {

    private static final String LEADERBOARD = "leaderboard";
    private static final String RECRUIT = "recruit";
    private static final String PILOT = "pilot";
    private static final String SPACE_JOCKEY = "space_jockey";
    private static final String COMMANDER = "commander";
    private static final String LIFTOFF = "liftoff";
    private static final String LAUNCH = "launch";
    private static final String ORBIT = "orbit";
    private static final String MOONSHOT = "moonshot";
    private static final String NEIL_ARMSTRONG = "neil_armstrong";
    private static final String GENNADY_PADALKA = "gennady_padalka";

    @Mock private AchievementClient mAchievementClient;
    @Mock private TotalsManager mTotalsManager;
    @Mock private Resources mResources;

    private AchievementOutbox mOutbox;
    private AchievementUploader mSystemUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mOutbox = new AchievementOutbox();
        mSystemUnderTest = new AchievementUploader(mResources, mTotalsManager, mAchievementClient);

        when(mResources.getString(R.string.leaderboard_the_worlds_highest_flights)).thenReturn(LEADERBOARD);
        when(mResources.getString(R.string.achievement_recruit)).thenReturn(RECRUIT);
        when(mResources.getString(R.string.achievement_pilot)).thenReturn(PILOT);
        when(mResources.getString(R.string.achievement_space_jockey)).thenReturn(SPACE_JOCKEY);
        when(mResources.getString(R.string.achievement_commander)).thenReturn(COMMANDER);
        when(mResources.getString(R.string.achievement_liftoff)).thenReturn(LIFTOFF);
        when(mResources.getString(R.string.achievement_launch)).thenReturn(LAUNCH);
        when(mResources.getString(R.string.achievement_orbit)).thenReturn(ORBIT);
        when(mResources.getString(R.string.achievement_moonshot)).thenReturn(MOONSHOT);
        when(mResources.getString(R.string.achievement_neil_armstrong)).thenReturn(NEIL_ARMSTRONG);
        when(mResources.getString(R.string.achievement_gennady_padalka)).thenReturn(GENNADY_PADALKA);
    }

    @Test
    public void uploadAchievements_leaderboardUploaded() {
        mOutbox.LeaderboardBestHeight = 8.2;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 8.2);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_flightNumberAchievementsUploaded() {
        mOutbox.FlightsToUpload = 3;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).incrementAchievement(RECRUIT, 3);
        verify(mAchievementClient).incrementAchievement(PILOT, 3);
        verify(mAchievementClient).incrementAchievement(SPACE_JOCKEY, 3);
        verify(mAchievementClient).incrementAchievement(COMMANDER, 3);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_flightMetersAchievementIncremented() {
        mOutbox.FlightMetersToUpload = 3;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).incrementAchievement(NEIL_ARMSTRONG, 3);
        verify(mTotalsManager).setSuccessfullyUploadedHeight(3);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_flightSecondsAchievementIncremented() {
        mOutbox.FlightSecondsToUpload = 3;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).incrementAchievement(GENNADY_PADALKA, 3);
        verify(mTotalsManager).setSuccessfullyUploadedTime(3);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_threeSuccessivelyHigherFlights() {
        mOutbox.ThreeSuccessivelyHigherFlights = true;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).unlockAchievement(LIFTOFF);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_tenSuccessivelyHigherFlights() {
        mOutbox.TenSuccessivelyHigherFlights = true;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).unlockAchievement(LAUNCH);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_fifteenSuccessivelyHigherFlights() {
        mOutbox.FifteenSuccessivelyHigherFlights = true;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).unlockAchievement(ORBIT);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    @Test
    public void uploadAchievements_twentySuccessivelyHigherFlights() {
        mOutbox.TwentySuccessivelyHigherFlights = true;

        mSystemUnderTest.uploadAchievements(mOutbox);

        verify(mAchievementClient).submitLeaderboardScore(LEADERBOARD, 0.0);
        verify(mAchievementClient).unlockAchievement(MOONSHOT);
        verifyNoMoreInteractions(mAchievementClient);
        verifyNoMoreInteractions(mTotalsManager);
        assertOutboxEmpty();
    }

    private void assertOutboxEmpty() {
        assertEquals(0.0, mOutbox.LeaderboardBestHeight, 0.0000001);
        assertEquals(0, mOutbox.FlightsToUpload);
        assertEquals(0, mOutbox.FlightMetersToUpload);
        assertEquals(0, mOutbox.FlightSecondsToUpload);
        assertFalse(mOutbox.ThreeSuccessivelyHigherFlights);
        assertFalse(mOutbox.TenSuccessivelyHigherFlights);
        assertFalse(mOutbox.FifteenSuccessivelyHigherFlights);
        assertFalse(mOutbox.TwentySuccessivelyHigherFlights);
    }
}