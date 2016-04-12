package ch02.ooprogramming;

import static utils.StringUtils.title;

import java.time.DayOfWeek;
import java.time.LocalDate;

// 1. Change the calendar printing program so it starts the week on a Sunday. Also make
// it print a newline at the end (but only one).

public class Ch0201Calendar {

  public static void printCalendar(String...args) {
    LocalDate date = LocalDate.now().withDayOfMonth(1);
    int month;
    int year;
    if (args.length >= 2) {        
      month = Integer.parseInt(args[0]);
      year = Integer.parseInt(args[1]);
      date = LocalDate.of(year, month, 1);
    } else {
      month = date.getMonthValue();
      year = date.getYear();
    }

    String monthName = title(date.getMonth().toString());
    System.out.println(monthName +" "+ year);
    System.out.println(" Sun Mon Tue Wed Thu Fri Sat");
    DayOfWeek weekday = date.getDayOfWeek();
    int value = weekday.getValue(); 
    for (int i = 0; i < value % 7; i++) 
      System.out.print("    ");

    while (date.getMonthValue() == month) {
      System.out.printf("%4d", date.getDayOfMonth());
      date = date.plusDays(1);
      if (date.getDayOfWeek().getValue() == 7) // newline just before Sunday
        System.out.println();
    }
    if (date.getDayOfWeek().getValue() != 7) 
      System.out.println(); //  this is for the newline at the end requirement

    // trailing blank line for consecutive printing
    System.out.println();
  }

  public static void main(String[] args) {
    printCalendar("10", "2015");
    printCalendar("10", "2016");
    printCalendar("2", "2016");
    printCalendar("5", "2016");
    printCalendar("6", "2016");

  }
}

//  October 2015
//  Sun Mon Tue Wed Thu Fri Sat
//                    1   2   3
//    4   5   6   7   8   9  10
//   11  12  13  14  15  16  17
//   18  19  20  21  22  23  24
//   25  26  27  28  29  30  31
//  
//  October 2016
//  Sun Mon Tue Wed Thu Fri Sat
//                            1
//    2   3   4   5   6   7   8
//    9  10  11  12  13  14  15
//   16  17  18  19  20  21  22
//   23  24  25  26  27  28  29
//   30  31
//  
//  February 2016
//  Sun Mon Tue Wed Thu Fri Sat
//        1   2   3   4   5   6
//    7   8   9  10  11  12  13
//   14  15  16  17  18  19  20
//   21  22  23  24  25  26  27
//   28  29
//  
//  May 2016
//  Sun Mon Tue Wed Thu Fri Sat
//    1   2   3   4   5   6   7
//    8   9  10  11  12  13  14
//   15  16  17  18  19  20  21
//   22  23  24  25  26  27  28
//   29  30  31
//  
//  June 2016
//  Sun Mon Tue Wed Thu Fri Sat
//                1   2   3   4
//    5   6   7   8   9  10  11
//   12  13  14  15  16  17  18
//   19  20  21  22  23  24  25
//   26  27  28  29  30

