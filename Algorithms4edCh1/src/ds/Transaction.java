package ds;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Transaction API from text p79
//public class Transaction implements Comparable<Transaction>
//Transaction(String who, Date when, double amount)
//Transaction(String transaction)  create a transaction (parse constructor)
//String who()  customer name
//Date when()  date
//double amount()  amount
//String toString()  string representation
//boolean equals(Object that)  is this the same transaction as  that ?
//int compareTo(Transaction that)  compare this transaction to  that
//int hashCode() 

public class Transaction implements Comparable<Transaction>{
  private String who; private Date when; private double amount;

  public Transaction(String who, Date when, double amount) {
    this.who = who;  this.when=when; this.amount=amount;
  }

  public Transaction(String s) {
    if (!isValidTransactionString(s)) throw new IllegalArgumentException(
        "Transaction constructor: invalid transaction string: cannot create instance");
    String[] fields = s.split(":");
    this.who = fields[0];
    this.when = new Date(fields[1]);
    this.amount = Double.parseDouble(fields[2]);
  }
  
  public static boolean isValidTransactionString(String s) {
    if (s == null) {
      System.err.println("isValidTransactionString: null is not a valid transaction string");
      return false;
    }
    if (!s.matches(".+:.+:.+")) {
      System.err.println("isValidTransactionString: invalid transaction string format: "
          + "should have 3 colon-separated string fields");
      return false;
    }
    String[] fields = s.split(":");
    if (fields[0].length() < 1) {
      System.err.println("isValidTransactionString: invalid transaction string: "
          + "the name field is empty");
      return false;
    }
    String dateString = fields[1];
    if (!Date.isValidDateString(dateString)) {
      System.err.println("isValidTransactionString: invalid transaction string: "
          + "invalid date field");
      return false;
    }
    String doubleString = fields[2];
    String doubleRegex = "[+-]?(\\d*)(?:\\.(\\d*))?(?:[eE]-?\\d+)?";
    Matcher m = Pattern.compile(doubleRegex).matcher(doubleString);
    int len1 = 0; int len2 = 0;
    if (m.matches()) {
      len1 = m.group(1).length();
      len2 = m.group(2) == null ? 0 : m.group(2).length();
    }
    if (!m.matches() || (len1 + len2 < 1)) {
      System.err.println("isValidTransactionString: invalid transaction string: "
          + "invalid amount field");
      return false;
    }
    return true;
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
  
  public static Transaction getInstance() {
    return new Transaction("Joe", new Date(5,15,2017), 39.95);
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
    return "Transaction("+who+":"+when+":"+amount+")";
  }

  public static void main(String[] args) {

  }

}
