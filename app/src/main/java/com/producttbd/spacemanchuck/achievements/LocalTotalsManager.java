package com.producttbd.spacemanchuck.achievements;

import android.content.SharedPreferences;

/**
 * Class for managing interactions with SharedPreferences for determining personal totals.
 */
class LocalTotalsManager implements TotalsManager {
    // TODO Move to pref_strings.xml or not?
    private static final String HEIGHT_PREF_KEY = "com.producttbd.spacemanchuck.achievements.CUMULATIVE_HEIGHT";
    private static final String TIME_PREF_KEY = "com.producttbd.spacemanchuck.achievements.CUMULATIVE_TIME";

    private final SharedPreferences mSharedPreferences;

    LocalTotalsManager(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public int getCumulativeHeightToUpload(double latestHeight) {
        return getCumulativeKeyValueToUpload(HEIGHT_PREF_KEY, latestHeight);
    }

    @Override
    public void setSuccessfullyUploadedHeight(int uploadedHeight) {
        setSuccessfullyUploadedKey(HEIGHT_PREF_KEY, uploadedHeight);
    }

    @Override
    public int getCumulativeTimeToUpload(double latestTime) {
        return getCumulativeKeyValueToUpload(TIME_PREF_KEY, latestTime);
    }

    @Override
    public void setSuccessfullyUploadedTime(int uploadedTime) {
        setSuccessfullyUploadedKey(TIME_PREF_KEY, uploadedTime);
    }

    private int getCumulativeKeyValueToUpload(String key, double newValue) {
        if (newValue < 0.0) return 0;
        double newTotal = newValue + getKeyValue(key);
        setKeyValue(key, (float) newTotal);
        return (int) newTotal;
    }

    private void setSuccessfullyUploadedKey(String key, int uploaded) {
        float previousValue = getKeyValue(key);
        float newValue = previousValue - (float) uploaded;
        setKeyValue(key, newValue);
    }

    private void setKeyValue(String key, float newValue) {
        newValue = Math.max(newValue, 0.0f);
        mSharedPreferences.edit().putFloat(key, newValue).apply();
    }

    private float getKeyValue(String key) {
        return mSharedPreferences.getFloat(key, 0.0f);
    }
}
