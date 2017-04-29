package com.producttbd.spacemanchuck.achievements;

import com.producttbd.spacemanchuck.tosslistening.TossCollector;
import com.producttbd.spacemanchuck.tosslistening.TossResult;

/**
 *
 */
class AchievementTossCollector implements TossCollector {

    private AchievementOutbox mOutbox;
    private LocalTotalsManager mLocalTotalsManager;

    public void add(TossResult tossResult) {
        incrementTossNumberAchievements();
        checkCumulativeTimeAchievements(tossResult.TimeSeconds);
        checkCumulativeHeightAchievements(tossResult.HeightMeters);
    }

    private void incrementTossNumberAchievements() {

    }

    private void checkCumulativeHeightAchievements(double heightMeters) {
        double totalHeight = mLocalTotalsManager.getNewCumulativeHeight(heightMeters);
    }

    private void checkCumulativeTimeAchievements(double timeSeconds) {
        double totalTime = mLocalTotalsManager.getNewCumulativeTime(timeSeconds);
    }
}
