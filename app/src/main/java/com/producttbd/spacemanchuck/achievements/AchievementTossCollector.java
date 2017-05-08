package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.producttbd.spacemanchuck.tosslistening.TossCollector;
import com.producttbd.spacemanchuck.tosslistening.TossResult;

/**
 * Class for receiving {@link TossResult}s and providing an appropriate {@link AchievementOutbox}.
 */
class AchievementTossCollector implements TossCollector {

    private TotalsManager mTotalsManager;
    @NonNull
    private AchievementOutbox mAchievementOutbox = new AchievementOutbox();
    private int mSuccessivelyHigherFlights = 0;
    private double mLastFlightHeight = 0.0;

    AchievementTossCollector(TotalsManager totalsManager) {
        mTotalsManager = totalsManager;
    }

    @NonNull
    public AchievementOutbox getAchievementOutbox() {
        return mAchievementOutbox;
    }

    public void add(@NonNull TossResult tossResult) {
        mAchievementOutbox.LeaderboardBestHeight =
                Math.max(mAchievementOutbox.LeaderboardBestHeight, tossResult.HeightMeters);
        ++mAchievementOutbox.FlightsToUpload;

        mAchievementOutbox.FlightMetersToUpload =
                mTotalsManager.getCumulativeHeightToUpload(tossResult.HeightMeters);
        mAchievementOutbox.FlightSecondsToUpload =
                mTotalsManager.getCumulativeTimeToUpload(tossResult.TimeSeconds);
        setSucessivelyHigherFlightsAchievements(tossResult.HeightMeters);
    }

    private void setSucessivelyHigherFlightsAchievements(double tossHeight) {
        mSuccessivelyHigherFlights =
                tossHeight > mLastFlightHeight ? ++mSuccessivelyHigherFlights : 0;
        mLastFlightHeight = tossHeight;
        mAchievementOutbox.ThreeSuccessivelyHigherFlights = mSuccessivelyHigherFlights >= 3;
        mAchievementOutbox.TenSuccessivelyHigherFlights = mSuccessivelyHigherFlights >= 10;
        mAchievementOutbox.FifteenSuccessivelyHigherFlights = mSuccessivelyHigherFlights >= 15;
        mAchievementOutbox.TwentySuccessivelyHigherFlights = mSuccessivelyHigherFlights >= 20;
    }
}
