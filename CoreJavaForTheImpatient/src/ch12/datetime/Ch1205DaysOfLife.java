package ch12.datetime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 5. Write a program that prints how many days you have been alive.

public class Ch1205DaysOfLife {

  public static long daysSince(int year, int month, int dayOfMonth) {
    LocalDate bdate = LocalDate.of(year, month, dayOfMonth);
    return ChronoUnit.DAYS.between(bdate, LocalDate.now());
    // did not add 1 to result because today isn't done yet
  }

  public static void main(String[] args) {

    System.out.println(daysSince(1983, 5, 17));

  }

}
