package ch12.datetime;

import java.time.LocalDate;

// 1. Compute Programmer’s Day without using plusDays.

// references:
// https://www.google.com/webhp?gws_rd=ssl#q=Programmer%E2%80%99s+Day
// Monday, September 12 // correct only for leap years

// http://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=18&cad=rja&uact=8&ved=0ahUKEwjGwJGx3YLKAhXCSiYKHaCFA8MQmhMIfzAR&url=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FDay_of_the_Programmer&usg=AFQjCNGqWEv6fOZ5RIVvn7QbVNEUOO9ezg&sig2=p4-jNc-3mqfHyz_pgiG8ng
// The Day of the Programmer is an international professional day 
// recognized in many technology companies and programming firms, 
// that is celebrated on the 256th day of each year.

//http://www.programmerday.info/FAQ.html
// Programmer Day is the 256th day of every year, September 13th or the 12th on leap years.

public class Ch1201ProgrammersDay {

  public static LocalDate getNextProgrammersDay() {
    LocalDate next = LocalDate.now();
    if (next.getDayOfYear() < 256) {
      return next.withDayOfYear(256);
    } else {
      return next.plusYears(1).withDayOfYear(256);
    }
  }

  public static void main(String[] args) {

    LocalDate nextProgrammersDay = getNextProgrammersDay();
    System.out.println(nextProgrammersDay); // 2016-09-12

  }

}
