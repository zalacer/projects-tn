package ch12.datetime;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

//  3. Implement a method next that takes a Predicate<LocalDate> and returns
//  an adjuster yielding the next date fulfilling the predicate. For example,
//    today.with(next(w -> getDayOfWeek().getValue() < 6))
//  computes the next workday.

public class Ch1203DatePredicateAdjuster {

  public static TemporalAdjuster next(Predicate<LocalDate> p) {
    return TemporalAdjusters.ofDateAdjuster(w -> {
      LocalDate result = w;
      do {
        result = result.plusDays(1);
      } while (!p.test(result));
      return result;
    });
  }

  //   some references:
  //   http://javapapers.com/java/java-8-date-and-time-temporal-adjuster/
  //   public static class CustomTemporalAdjuster implements TemporalAdjuster {
  //    public Temporal adjustInto(Temporal temporalInput) {
  //      LocalDate localDate = LocalDate.from(temporalInput);
  //      int day = localDate.getDayOfMonth();
  //      if (day % 2 == 0) {
  //        localDate = localDate.plusDays(1);
  //      } else {
  //        localDate = localDate.plusDays(2);
  //      }
  //
  //      return temporalInput.with(localDate);
  //    }
  //    
  //    https://docs.oracle.com/javase/tutorial/datetime/iso/adjusters.html
  //    /**
  //     * The adjustInto method accepts a Temporal instance 
  //     * and returns an adjusted LocalDate. If the passed in
  //     * parameter is not a LocalDate, then a DateTimeException is thrown.
  //     */
  //    public static Temporal adjustInto(Temporal input) {
  //        LocalDate date = LocalDate.from(input);
  //        int day;
  //        if (date.getDayOfMonth() < 15) {
  //            day = 15;
  //        } else {
  //            day = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
  //        }
  //        date = date.withDayOfMonth(day);
  //        if (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
  //            date.getDayOfWeek() == DayOfWeek.SUNDAY) {
  //            date = date.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
  //        }
  //
  //        return input.with(date);
  //    }

  public static void main(String[] args) {

    LocalDate today = LocalDate.now();
    System.out.println("today: "+today); //2015-12-30, Wednesday
    System.out.println(today.getDayOfWeek().getValue()); //3

    // get next working day
    LocalDate nextWorkingDay = today.with(next(ld -> ld.getDayOfWeek().getValue() < 6));
    System.out.println("nextWorkingDay: "+nextWorkingDay); // 2015-12-31, Thursday

    // get next weekend day
    LocalDate nextWeekEndDay = today.with(next(ld -> ld.getDayOfWeek().getValue() >= 6));
    System.out.println("nextWeekEndDay: "+nextWeekEndDay); // 2016-01-02, Saturday

  }

}
