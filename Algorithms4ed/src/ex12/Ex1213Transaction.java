package ex12;

import java.util.Objects;

//  1.2.13  Using our implementation of  Date as a model (page 91), develop an implementa-
//  tion of  Transaction 


public class Ex1213Transaction {

  //  Transaction API p79
  //  public class Transaction implements Comparable<Transaction>
  //  Transaction(String who, Date when, double amount)
  //  Transaction(String transaction)  create a transaction (parse constructor)
  //  String who()  customer name
  //  Date when()  date
  //  double amount()  amount
  //  String toString()  string representation
  //  boolean equals(Object that)  is this the same transaction as  that ?
  //  int compareTo(Transaction that)  compare this transaction to  that
  //  int hashCode()  
  
  public static class Transaction implements Comparable<Transaction>{
    private String who; private Date when; private double amount;
    
    public Transaction(String who, Date when, double amount) {
      this.who = who;  this.when=when; this.amount=amount;
    }
    
    public Transaction(String transactionString) {
      String[] fields = transactionString.split(",");
      if (fields.length != 3) throw new IllegalArgumentException("Invalid transaction string");
      if (fields[0].length() < 1) throw new IllegalArgumentException("Invalid transaction string");
      this.who = fields[0];
      this.when = new Date(fields[1]);
      this.amount = Double.parseDouble(fields[2]);
    }
    
    public String who() {
      return who;
    }
    
    public Date when() {
      return when;
    }
    
    public double amount() {
      return amount;
    }
    
    public int compareTo(Transaction that) {
      if (this.who.compareTo(that.who) < 0)    return -1;
      if (this.who.compareTo(that.who) > 0)    return +1;
      if (this.when.compareTo(that.when) < 0)  return -1;
      if (this.when.compareTo(that.when) > 0)  return +1;
      if (this.amount < that.amount)           return -1;
      if (this.amount > that.amount)           return +1;
      return 0;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(who,when,amount);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Transaction other = (Transaction) obj;
      if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
        return false;
      if (when == null) {
        if (other.when != null)
          return false;
      } else if (!when.equals(other.when))
        return false;
      if (who == null) {
        if (other.who != null)
          return false;
      } else if (!who.equals(other.who))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return ""+who+","+when+","+amount;
    }
    
  }

  public static class Date implements Comparable<Date> {
    private final int month; private final int day; private final int year;

    public Date(int m, int d, int y) {
      if (!isValid(m, d, y)) throw new IllegalArgumentException("SmartDate constructor: "
          + "invalid date");
      month = m; day = d; year = y; 
    }
    
    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    public Date(String date) {
      String[] fields = date.split("/");
      if (fields.length != 3) {
          throw new IllegalArgumentException("Invalid date");
      }
      month = Integer.parseInt(fields[0]);
      day   = Integer.parseInt(fields[1]);
      year  = Integer.parseInt(fields[2]);
      if (!isValid(month, day, year)) throw new IllegalArgumentException("Invalid date");
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

    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    public Date next() {
      if (isValid(month, day + 1, year))    return new Date(month, day + 1, year);
      else if (isValid(month + 1, 1, year)) return new Date(month + 1, 1, year);
      else                                  return new Date(1, 1, year + 1);
    }

    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    public boolean isAfter(Date that) {
      return compareTo(that) > 0;
    }
    
    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    public boolean isBefore(Date that) {
      return compareTo(that) < 0;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(day,month,year);
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

    // from http://algs4.cs.princeton.edu/21elementary/Date.java.html
    @Override
    public String toString() {
        return month + "/" + day + "/" + year;
    }

  }

  public static void main(String[] args) {
    //Ex1317ReadTransactions.transactions.txt
    
    Transaction t1 = new Transaction("Joe", new Date(5,15,2017), 39.95);
    System.out.println(t1);
    Transaction t2 = new Transaction("Mary", new Date(5,15,2019), 55.63);
    System.out.println(t2);
    Transaction t3 = new Transaction("Mary", new Date(5,15,2019), 75.11);
    System.out.println(t3);
    assert t1.compareTo(t1) == 0;
    assert t2.compareTo(t1) > 0;
    assert t3.compareTo(t2) > 0;
    assert t1.equals(t1);
    assert !t1.equals(t3);

    




  }


}
