package ex12;


//  1.2.12  Add a method  dayOfTheWeek() to  SmartDate that returns a  String value
//  Monday ,  Tuesday ,  Wednesday ,  Thursday ,  Friday ,  Saturday , or  Sunday , giving the ap-
//  propriate day of the week for the date. You may assume that the date is in the 21st
//  century.


public class Ex1212SmartDateDayOfWeek {

  public static class SmartDate {
    private final int month;
    private final int day;
    private final int year;

    public SmartDate(int m, int d, int y) {
      if (!isValid(m, d, y)) throw new IllegalArgumentException("SmartDate constructor: "
          + "invalid date");
      month = m; day = d; year = y; 
      
    }

    public int month() {
      return month; 
    }
    
    public int day() { 
      return day;
    }
    
    public int year() {
      return year; 
    }
    
    public String toString() { 
      return month() + "/" + day() + "/" + year();
    }
    
    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    // is y a leap year?
    private static boolean isLeapYear(int y) {
        if (y % 400 == 0) return true;
        if (y % 100 == 0) return false;
        return y % 4 == 0;
    }
    
    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    private static final int[] DAYS = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    
    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    // is the given date valid?
    private static boolean isValid(int m, int d, int y) {
        if (m < 1 || m > 12)      return false;
        if (d < 1 || d > DAYS[m]) return false;
        if (m == 2 && d == 29 && !isLeapYear(y)) return false;
        return true;
    }
    
    public String dayOfTheWeek() {
      //https://en.wikipedia.org/wiki/Determination_of_the_day_of_the_week#Implementation-dependent_methods
      // this assumes the y/m/d has been validated
      int y = year; int m = month; int d = day;
      d+= m<3 ? y-- : y-2;
      int x = (23*m/9+d+4+y/4-y/100+y/400)%7;
      switch (x) {
      case 0: return "Sunday";
      case 1: return "Monday";
      case 2: return "Tuesday";
      case 3: return "Wednesday";
      case 4: return "Thursday";
      case 5: return "Friday";
      case 6: return "Saturday";}
      return "";
    }
    
  }

  public static void main(String[] args) {
    
    SmartDate sd = new SmartDate(11, 15, 2025);
    System.out.println(sd); //11/15/2025
    System.out.println(sd.dayOfTheWeek()); //Saturday
    SmartDate sd2 = new SmartDate(11, 32, 2025);
    //IllegalArgumentException: SmartDate constructor: invalid date
    System.out.println(sd2);
    


  }


}
