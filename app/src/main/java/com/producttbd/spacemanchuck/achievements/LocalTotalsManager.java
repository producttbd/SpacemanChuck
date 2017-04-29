package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;

/**
 * Class for managing interactions with SharedPreferences for determining personal totals.
 */
class LocalTotalsManager {

    private final SharedPreferences mSharedPreferences;
    // TODO Move to pref_strings.xml or not?
    private static final String HEIGHT_PREF_KEY = "com.producttbd.spacemanchuck.achievements.CUMULATIVE_HEIGHT";
    private static final String TIME_PREF_KEY = "com.producttbd.spacemanchuck.achievements.CUMULATIVE_TIME";

    LocalTotalsManager(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public double getNewCumulativeHeight(double lastThrowHeight) {
        float storedValue = mSharedPreferences.getFloat(HEIGHT_PREF_KEY, 0.0f);
        double newTotal = storedValue + lastThrowHeight;
        setCumulativeHeight(newTotal);
        return newTotal;
    }

    public double getNewCumulativeTime(double lastThrowTime) {
        float storedValue = mSharedPreferences.getFloat(TIME_PREF_KEY, 0.0f);
        double newTotal = storedValue + lastThrowTime;
        setCumulativeTime(newTotal);
        return newTotal;
    }

    private void setCumulativeHeight(double newValue) {
        // TODO Better way to go double to float?
        mSharedPreferences.edit().putFloat(HEIGHT_PREF_KEY, (float) newValue).commit();
    }

    private void setCumulativeTime(double newValue) {
        // TODO Better way to go double to float?
        mSharedPreferences.edit().putFloat(TIME_PREF_KEY, (float) newValue).commit();
    }
}
