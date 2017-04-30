package com.producttbd.spacemanchuck.achievements;

import android.content.res.Resources;
import android.util.Log;

import com.producttbd.spacemanchuck.R;

/**
 *
 */
class AchievementUploader {

    private static final String TAG = AchievementUploader.class.getSimpleName();
    private static final int[] FLIGHT_NUMBER_ACHIEVEMENTS = {
            R.string.achievement_recruit, R.string.achievement_pilot,
            R.string.achievement_space_jockey, R.string.achievement_commander };

    private AchievementClient mAchievementClient;
    private TotalsManager mTotalsManager;
    private Resources mResources;

    AchievementUploader(Resources resources, TotalsManager totalsManager, AchievementClient achievementClient) {
        mAchievementClient = achievementClient;
        mTotalsManager = totalsManager;
        mResources = resources;
    }

    public void uploadAchievements(AchievementOutbox outbox) {
        Log.d(TAG, "uploadAchievements");
        mAchievementClient
                .submitLeaderboardScore(
                        mResources.getString(R.string.leaderboard_the_worlds_highest_flights),
                        outbox.LeaderboardBestHeight);
        if (outbox.FlightsToUpload > 0) {
            for (int id : FLIGHT_NUMBER_ACHIEVEMENTS) {
                mAchievementClient
                        .incrementAchievement(mResources.getString(id), outbox.FlightsToUpload);
            }
        }
        if (outbox.FlightMetersToUpload > 0) {
            mAchievementClient
                    .incrementAchievement(mResources.getString(R.string.achievement_neil_armstrong),
                            outbox.FlightMetersToUpload);
        }
        if (outbox.FlightSecondsToUpload > 0) {
            mAchievementClient
                    .incrementAchievement(mResources.getString(R.string.achievement_gennady_padalka),
                            outbox.FlightSecondsToUpload);
        }
        if (outbox.ThreeSuccessivelyHigherFlights) {
            mAchievementClient.unlockAchievement(mResources.getString(R.string.achievement_liftoff));
        }
        if (outbox.TenSuccessivelyHigherFlights) {
            mAchievementClient.unlockAchievement(mResources.getString(R.string.achievement_launch));
        }
        if (outbox.FifteenSuccessivelyHigherFlights) {
            mAchievementClient.unlockAchievement(mResources.getString(R.string.achievement_orbit));
        }
        if (outbox.TwentySuccessivelyHigherFlights) {
            mAchievementClient.unlockAchievement(mResources.getString(R.string.achievement_moonshot));
        }
    }
}
