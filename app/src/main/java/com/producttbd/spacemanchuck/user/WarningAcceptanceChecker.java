package com.producttbd.spacemanchuck.user;

import android.content.SharedPreferences;

/**
 * A class for encapsulating logic of whether the user needs to be shown the warning.
 */

public class WarningAcceptanceChecker {

    private static final long ONE_WEEK_MS = 7l * 24l * 60l * 60l * 1000l;

    private final SharedPreferences mSharedPreferences;
    private final String mWarningAcceptedTimeKey;

    public WarningAcceptanceChecker(SharedPreferences sharedPreferences, String warningAcceptedTimeKey) {
        mSharedPreferences = sharedPreferences;
        mWarningAcceptedTimeKey = warningAcceptedTimeKey;
    }

    public boolean shouldShowWarning(long currentTimeMillis) {
        long acceptedTime = mSharedPreferences.getLong(mWarningAcceptedTimeKey, -1l);
        return acceptedTime < 0 || (currentTimeMillis - acceptedTime) > ONE_WEEK_MS;
    }

    public void setAcceptedWarning(long currentTimeMillis) {
        mSharedPreferences.edit().putLong(mWarningAcceptedTimeKey, currentTimeMillis).commit();
    }
}
