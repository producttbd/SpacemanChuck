package com.producttbd.spacemanchuck.achievements;

/**
 * Class for holding achievements before they are set on the server.
 */

class AchievementOutbox {
    double LeaderboardBestHeight = 0.0;

    int FlightsToUpload = 0;
    int FlightMetersToUpload = 0;
    int FlightSecondsToUpload = 0;

    boolean ThreeSuccessivelyHigherFlights = false;
    boolean TenSuccessivelyHigherFlights = false;
    boolean FifteenSuccessivelyHigherFlights = false;
    boolean TwentySuccessivelyHigherFlights = false;
}
