package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.android.gms.common.api.GoogleApiClient;
import com.producttbd.spacemanchuck.tosslistening.TossCollector;
import com.producttbd.spacemanchuck.tosslistening.TossResult;

/**
 * Class to coordinate all of the Achievements classes.
 */

public class AchievementsManager implements TossCollector {

    private GoogleApiClient mGoogleApiClient;

    private AchievementTossCollector mAchievementTossCollector;
    private AchievementUploader mAchievementUploader;

    public AchievementsManager(GoogleApiClient googleApiClient, SharedPreferences sharedPreferences,
                        Resources resources) {
        TotalsManager totalsManager = new LocalTotalsManager(sharedPreferences);
        mAchievementTossCollector = new AchievementTossCollector(totalsManager);
        GoogleAchievementClient googleAchievementClient =
                new GoogleAchievementClient(googleApiClient);
        mAchievementUploader =
                new AchievementUploader(resources, totalsManager, googleAchievementClient);
    }

    @Override
    public void add(TossResult tossResult) {
        mAchievementTossCollector.add(tossResult);
        mAchievementUploader.uploadAchievements(mAchievementTossCollector.getAchievementOutbox());
    }
}
