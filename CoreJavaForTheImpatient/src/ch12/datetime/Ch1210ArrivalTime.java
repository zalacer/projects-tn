package ch12.datetime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

//  10. Your flight from Los Angeles to Frankfurt leaves at 3:05 pm local time 
//  and takes 10 hours and 50 minutes. When does it arrive? Write a program 
//  that can handle calculations like this.

public class Ch1210ArrivalTime {

  public static ZonedDateTime getArrivalDateTime(ZonedDateTime departDateTime, 
      String destZoneId, long months, long weeks, long days, long hours, long minutes) {
    ZonedDateTime arrivalDateTime = departDateTime
        .withZoneSameInstant(ZoneId.of(destZoneId));
    arrivalDateTime = arrivalDateTime.plus(months, ChronoUnit.MONTHS);
    arrivalDateTime = arrivalDateTime.plus(weeks, ChronoUnit.WEEKS);
    arrivalDateTime = arrivalDateTime.plus(days, ChronoUnit.DAYS);
    arrivalDateTime = arrivalDateTime.plus(hours, ChronoUnit.HOURS);
    arrivalDateTime = arrivalDateTime.plus(minutes, ChronoUnit.MINUTES);
    return arrivalDateTime;
  }

  // could also use the more specific ZonedDateTime.plus methods
  public static ZonedDateTime getArrivalDateTime2(ZonedDateTime departDateTime, 
      String destZoneId, long months, long weeks, long days, long hours, long minutes) {
    ZonedDateTime arrivalDateTime = departDateTime
        .withZoneSameInstant(ZoneId.of(destZoneId));
    arrivalDateTime = arrivalDateTime.plusMonths(months);
    arrivalDateTime = arrivalDateTime.plusWeeks(weeks);
    arrivalDateTime = arrivalDateTime.plusDays(days);
    arrivalDateTime = arrivalDateTime.plusHours(hours);
    arrivalDateTime = arrivalDateTime.plusMinutes(minutes);
    return arrivalDateTime;
  }

  public static void main(String[] args) {

    // given that LA ZoneId string is "America/Los_Angeles" and
    // Frankfurt's is  "CET"

    ZonedDateTime departureInLaZone = ZonedDateTime.of(2016, 1, 1, 15, 5, 0, 0,
        ZoneId.of("America/Los_Angeles"));

    ZonedDateTime arrivalInFrankfurtZone = getArrivalDateTime(departureInLaZone,
        "CET", 0, 0, 0, 10, 50);

    System.out.println("arrivalInFrankfurtZone: "+arrivalInFrankfurtZone); 
    // 2016-01-02T10:55+01:00[CET]

    ZonedDateTime arrivalInFrankfurtZone2 = getArrivalDateTime2(departureInLaZone,
        "CET", 0, 0, 0, 10, 50);

    System.out.println("arrivalInFrankfurtZone2: "+arrivalInFrankfurtZone2); 
    // 2016-01-02T10:55+01:00[CET]

    // to find the arrival DateTime in the departure zone:

    ZonedDateTime arrivalDateTimeLaZone = getArrivalDateTime(departureInLaZone,
        departureInLaZone.getZone().toString(), 0, 0, 0, 10, 50);

    System.out.println("arrivalDateTimeLaZone: "+arrivalDateTimeLaZone);
    // 2016-01-02T01:55-08:00[America/Los_Angeles]


  }

}
