package ex12;

//  1.2.19 Parsing. Develop the parse constructors for your  Date and  Transaction im-
//  plementations of Exercise 1.2.13 that take a single  String argument to specify the
//  initialization values, using the formats given in the table below.
//  Partial solution:
//    public Date(String date) {
//      String[] fields = date.split("/");
//      month = Integer.parseInt(fields[0]);
//      day = Integer.parseInt(fields[1]);
//      year = Integer.parseInt(fields[2]);
//    }

// this has already been done in ex12.Ex1213Transaction:

//  //from http://algs4.cs.princeton.edu/21elementary/Date.java.html
//  public Date(String date) {
//    String[] fields = date.split("/");
//    if (fields.length != 3) {
//        throw new IllegalArgumentException("Invalid date");
//    }
//    month = Integer.parseInt(fields[0]);
//    day   = Integer.parseInt(fields[1]);
//    year  = Integer.parseInt(fields[2]);
//    if (!isValid(month, day, year)) throw new IllegalArgumentException("Invalid date");
//  }

//  //using comma separated fields to mesh with Transaction.toString()
//  public Transaction(String transactionString) {
//    String[] fields = transactionString.split(",");
//    if (fields.length != 3) throw new IllegalArgumentException("Invalid transaction string");
//    if (fields[0].length() < 1) throw new IllegalArgumentException("Invalid transaction string");
//    this.who = fields[0];
//    this.when = new Date(fields[1]); // throws exception for invalid Date
//    this.amount = Double.parseDouble(fields[2]); //throws throws NumberFormatException if invalid
//  }


public class Ex1219AgumentsParsing {


}
