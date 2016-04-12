package ch12.datetime;

import static utils.StringUtils.space;
import static utils.StringUtils.title;

import java.time.DayOfWeek;
import java.time.LocalDate;

//  4. Write an equivalent of the Unix cal program that displays a calendar for a month.
//  For example, java Cal 3 2013 should display
//    1 2 3
//    4 5 6 7 8 9 10
//    11 12 13 14 15 16 17
//    18 19 20 21 22 23 24
//    25 26 27 28 29 30 31
//  indicating that March 1 is a Friday. (Show the weekend at the end of the week.)

public class Ch1204CalendarProgram {
  
  public static void printCalendar(String...args) {
    int month;
    int year;
    month = Integer.parseInt(args[0]);
    year = Integer.parseInt(args[1]);
    LocalDate date = LocalDate.of(year, month, 1);

    while (date.getMonthValue() == month) {
      System.out.print(date.getDayOfMonth()+" ");
      date = date.plusDays(1);
      if (date.getDayOfWeek().getValue() == 1)
        System.out.println();
    }
    if (date.getDayOfWeek().getValue() != 1) 
      System.out.println();
  }
  
  public static void printCalendarBetter(String...args) {
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
        
    String monthAndYearTitle = title(date.getMonth().toString())+" "+year;
    String daysTitle = " Mon Tue Wed Thu Fri Sat Sun";
    System.out.println(space((daysTitle.length()-monthAndYearTitle.length())/2)
        +monthAndYearTitle);
    System.out.println(" Mon Tue Wed Thu Fri Sat Sun");
    DayOfWeek weekday = date.getDayOfWeek();
    int value = weekday.getValue();
    for (int i = 1; i < value; i++) 
      System.out.print("    ");
    while (date.getMonthValue() == month) {
      System.out.printf("%4d", date.getDayOfMonth());
      date = date.plusDays(1);
      if (date.getDayOfWeek().getValue() == 1)
        System.out.println();
    }
    if (date.getDayOfWeek().getValue() != 1) 
      System.out.println();
  }
 
  public static void main(String[] args) {
    
    printCalendar("3", "2013");
//  1 2 3 
//  4 5 6 7 8 9 10 
//  11 12 13 14 15 16 17 
//  18 19 20 21 22 23 24 
//  25 26 27 28 29 30 31 
    
    printCalendarBetter("3", "2013");
//          March 2013
//  Mon Tue Wed Thu Fri Sat Sun
//                    1   2   3
//    4   5   6   7   8   9  10
//   11  12  13  14  15  16  17
//   18  19  20  21  22  23  24
//   25  26  27  28  29  30  31
          
  }

}

//    printCalendar("3", "2013");
//    1 2 3 
//    4 5 6 7 8 9 10 
//    11 12 13 14 15 16 17 
//    18 19 20 21 22 23 24 
//    25 26 27 28 29 30 31 
//    
//    printCalendarBetter("3", "2013");
//              March 2013
//     Mon Tue Wed Thu Fri Sat Sun
//                       1   2   3
//       4   5   6   7   8   9  10
//      11  12  13  14  15  16  17
//      18  19  20  21  22  23  24
//      25  26  27  28  29  30  31

