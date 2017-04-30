package com.producttbd.spacemanchuck.achievements;

/**
 * Interface for interacting with a achievement service.
 */
interface AchievementClient {
    void unlockAchievement(String id);
    void incrementAchievement(String id, int number);
    void submitLeaderboardScore(String id, double score);
}
