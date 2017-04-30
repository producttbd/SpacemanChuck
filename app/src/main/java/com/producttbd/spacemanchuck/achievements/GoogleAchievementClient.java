package com.producttbd.spacemanchuck.achievements;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Class for interacting with the Google achievement service.
 */
class GoogleAchievementClient implements AchievementClient {

    private GoogleApiClient mGoogleApiClient;

    public GoogleAchievementClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void unlockAchievement(String id) {
        // TODO use pending result?
        Games.Achievements.unlock(mGoogleApiClient, id);
    }

    @Override
    public void incrementAchievement(String id, int number) {
        Games.Achievements.increment(mGoogleApiClient, id, number);
    }

    @Override
    public void submitLeaderboardScore(String id, double score) {
        long formattedScore = (long) (score * 1000);
        Games.Leaderboards.submitScore(mGoogleApiClient, id, formattedScore);
    }
}
