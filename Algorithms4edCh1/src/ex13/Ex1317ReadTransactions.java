package ex13;

import static ds.Date.isValidDateString;
import static v.ArrayUtils.*;

import ds.Date;
import ds.Queue;
import ds.Transaction;
import edu.princeton.cs.algs4.In;

//  p163
//  1.3.17  Do Exercise 1.3.16 for  Transaction 
//  1.3.16  Using  readInts() on page 126 as a model, write a static method  readDates() for
//  Date that reads dates from standard input in the format specified in the table on page 119
//  and returns an array containing them.

//  formats for parsing dates text p117
//  type  format  example
//  Date  integers separated by slashes  5/22/1939
//  Transaction
//  customer, date, and amount,
//  separated by whitespace
//  Turing 5/22/1939 11.99

public class Ex1317ReadTransactions {

  // from text p126
  public static int[] readInts(String name) {
    In in = new In(name);
    Queue<Integer> q = new Queue<Integer>();
    while (!in.isEmpty())
      q.enqueue(in.readInt());
    int N = q.size();
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = q.dequeue();
    return a;
  }

  public static Date[] readDates(String name) {
    In in = new In(name);
    Queue<Date> q = new Queue<Date>();
    while (!in.isEmpty()) {
      String dateString = in.readString();
      if (isValidDateString(dateString)) q.enqueue(new Date(dateString));
    }
    return q.toArray(Date.getInstance("5/5/2016"));
  }
  
  public static Transaction[] readTransactions(String name) {
    In in = new In(name);
    Queue<Transaction> q = new Queue<Transaction>();
    while (!in.isEmpty()) {
      String transactionString = in.readLine().trim();
      if (Transaction.isValidTransactionString(transactionString)) 
        q.enqueue(new Transaction(transactionString));
    }
    return q.toArray(Transaction.getInstance());
  }

  public static void main(String[] args) {
    
    Transaction[] data = readTransactions("Ex1317ReadTransactions.transactions.txt");
    pa(data);
    //  Transaction[
    //  Transaction(Joe:5/15/2017:39.95),
    //  Transaction(Mary:5/15/2019:55.63),
    //  Transaction(Peter:6/1/2019:21.13),
    //  Transaction(Beth:7/20/2019:98.02),
    //  Transaction(Paul:3/7/2019:44.96),
    //  Transaction(Susan:8/10/2019:125.36),
    //  Transaction(Steve:9/18/2019:54.21),
    //  Transaction(Alice:10/2/2019:72.39),
    //  Transaction(David:11/9/2019:83.12)]
    
    //  contents of Ex1317ReadTransactions.transactions.txt:
    //  Joe:5/15/2017:39.95 
    //  Mary:5/15/2019:55.63
    //  Peter:6/1/2019:21.13
    //  Beth:7/20/2019:98.02
    //  Paul:3/7/2019:44.96
    //  Susan:8/10/2019:125.36
    //  Steve:9/18/2019:54.21
    //  Alice:10/2/2019:72.39
    //  David:11/9/2019:83.12
    
  }

}
