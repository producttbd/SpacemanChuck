package com.producttbd.spacemanchuck.achievements;

/**
 * A class for managing local achievemnt totals before they are uploaded. Only integers are uploaded
 * so this interface helps handle accumulating fractional amounts as well as offline amounts.
 */
interface TotalsManager {
    int getCumulativeHeightToUpload(double latestHeight);
    void setSuccessfullyUploadedHeight(int uploadedHeight);
    int getCumulativeTimeToUpload(double latestTime);
    void setSuccessfullyUploadedTime(int uploadedHTime);
}
