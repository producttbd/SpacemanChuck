package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.producttbd.spacemanchuck.tosslistening.TossCollector;
import com.producttbd.spacemanchuck.tosslistening.TossResult;
import com.producttbd.spacemanchuck.user.GoogleSignInManager;

/**
 * Class to coordinate all of the Achievements classes.
 */

public class AchievementsCoordinator implements TossCollector {

    private GoogleSignInManager mGoogleSignInManager;

    private AchievementTossCollector mAchievementTossCollector;
    private AchievementUploader mAchievementUploader;

    public AchievementsCoordinator(@NonNull GoogleSignInManager googleSignInManager, SharedPreferences sharedPreferences,
                                   Resources resources) {
        mGoogleSignInManager = googleSignInManager;
        TotalsManager totalsManager = new LocalTotalsManager(sharedPreferences);
        mAchievementTossCollector = new AchievementTossCollector(totalsManager);
        GoogleAchievementClient googleAchievementClient =
                new GoogleAchievementClient(googleSignInManager.getGoogleApiClient());
        mAchievementUploader =
                new AchievementUploader(resources, totalsManager, googleAchievementClient);
    }

    @Override
    public void add(TossResult tossResult) {
        mAchievementTossCollector.add(tossResult);
        if (mGoogleSignInManager.isConnected()) {
            mAchievementUploader
                    .uploadAchievements(mAchievementTossCollector.getAchievementOutbox());
        }
    }
}
