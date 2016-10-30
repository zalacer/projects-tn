package ds;

public class Date implements Comparable<Date> {
  private int year;
  private int month;
  private int day;

  public Date(String date) {
    if (date == null || !date.matches("\\d+/\\d+/\\d+")) throw new IllegalArgumentException(
        "Date constructor: invalid date string format");
    String[] fields = date.split("/");
    month = Integer.parseInt(fields[0]);
    day = Integer.parseInt(fields[1]);
    year = Integer.parseInt(fields[2]);
    if (!isValid(month, day, year)) throw new IllegalArgumentException("Date constructor: "
        + "invalid date");
  }
  
  public Date(int m, int d, int y) {
    if (!isValid(m, d, y)) throw new IllegalArgumentException("Date constructor: "
        + "invalid date");
    month = m; day = d; year = y; 
  }

  public int month() {
    return month; 
  }
  
  public void setMonth(int month) {
    if (!isValid(month, this.day, this.year)) throw new IllegalArgumentException(
        "setMonth: invalid month for date " + month + "/" + day() + "/" + year());
    this.month = month;
  }
  
  public int day() { 
    return day;
  }
  
  public void setDay(int day) {
    if (!isValid(this.month, day, this.year)) throw new IllegalArgumentException(
        "setDay: invalid day for date " + month() + "/" + day + "/" + year());
    this.day = day;
  }
  
  public int year() {
    return year; 
  }
  
  public void setYear(int year) {
    if (!isValid(this.month, this.day, year)) throw new IllegalArgumentException(
        "setYear: invalid year for date " + month() + "/" + day() + "/" + year);
    this.year = year;
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
  public static boolean isValid(int m, int d, int y) {
      if (m < 1 || m > 12)      return false;
      if (d < 1 || d > DAYS[m]) return false;
      if (m == 2 && d == 29 && !isLeapYear(y)) return false;
      return true;
  }
  
  public static boolean isValidDateString(String s) {
    if (s == null || !s.matches("\\d+/\\d+/\\d+")) return false;
    String[] fields = s.split("/");
    int m = Integer.parseInt(fields[0]);
    int d = Integer.parseInt(fields[1]);
    int y = Integer.parseInt(fields[2]);
    return isValid(m, d, y) ? true : false;
  }
  
  // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
  @Override
  public int compareTo(Date that) {
    if (this.year  < that.year)  return -1;
    if (this.year  > that.year)  return +1;
    if (this.month < that.month) return -1;
    if (this.month > that.month) return +1;
    if (this.day   < that.day)   return -1;
    if (this.day   > that.day)   return +1;
    return 0;
  }
 
  public static Date getInstance(String date) {
    return new Date(date);
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
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + day;
    result = prime * result + month;
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Date other = (Date) obj;
    if (day != other.day)
      return false;
    if (month != other.month)
      return false;
    if (year != other.year)
      return false;
    return true;
  }

  public String toString() { 
    return month() + "/" + day() + "/" + year();
  }
  
  public static void main(String[] args) {

  }

}
