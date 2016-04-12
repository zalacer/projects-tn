package ch12.datetime;

import static utils.DateTimeUtils.printDuration;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

//  11. Your return flight leaves Frankfurt at 14:05 and arrives in Los Angeles at 16:40.
//  How long is the flight? Write a program that can handle calculations like this.

public class Ch1211TripDuration {

  public static Duration getFlightDuration(ZonedDateTime start, ZonedDateTime end) {
    return Duration.between(start, end).abs();
  }

  public static void main(String[] args) {


    ZonedDateTime start = ZonedDateTime.of(2016, 1, 1, 14, 5, 0, 0,
        ZoneId.of("CET"));

    ZonedDateTime end = ZonedDateTime.of(2016, 1, 1, 16, 40, 0, 0,
        ZoneId.of("America/Los_Angeles"));

    Duration length = getFlightDuration(start, end);
    printDuration(length);
    // 11 hours and 35 minutes  

  }

}
