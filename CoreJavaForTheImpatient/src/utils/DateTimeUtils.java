package utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeUtils {
    
    public static String now() {
        return new SimpleDateFormat("yyyyMMddHHmmSSSSS").format(new Date());
    }
    
    public static void printDuration(Duration dr) {
      Duration mdr = dr;
      long s = mdr.getSeconds();
      int sig = s >= 0 ? 1 : -1;
      if (sig < 0) {
        mdr = dr.abs();
        s = mdr.getSeconds();
      }
      long n = mdr.getNano();
      if (s == 0 && n == 0) {
        System.out.println(0);
        return;
      }
      long d = (s/(60*60*24));
      long h = 0;
      if (d > 0) {
        h = (s - d*60*60*24)/(60*60);
        s = s - d*60*60*24 - h*60*60;
      } else {
        h = s/(60*60);
        s = s - h*60*60;
      }
      long m = 0;
      if (s >= 60) {
        m = s/60;
        s = s - (m*60);
      }
      StringBuilder sb = new StringBuilder();
      if (sig < 0) sb.append("-(");
      if (d != 0) sb.append(d+" days");
      if (h != 0) {
        if (d != 0) sb.append(", ");
        sb.append(h+" hours");
      }
      if (m != 0) {
        if (d != 0 || h != 0) sb.append(", ");
        sb.append(m+" minutes");
      }   
      if (s != 0) {
        if (d != 0 || h != 0 || m != 0) sb.append(", ");
        sb.append(s+" seconds");
      }
      if (n != 0) {
        if (d != 0 || h != 0 || m != 0 || s != 0) sb.append(", ");
        sb.append(n+" nanoseconds");
      }
      if (sig < 0) sb.append(")");
      sb.append(System.lineSeparator());
      System.out.println(sb);  
    }
    
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
    
    public static Duration getFlightDuration(ZonedDateTime start, ZonedDateTime end) {
      return Duration.between(start, end).abs();
    }


}
