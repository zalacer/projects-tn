package ex13;

import static ds.Date.isValidDateString;
import static v.ArrayUtils.*;

import ds.Date;
import ds.Queue;
import edu.princeton.cs.algs4.In;

//  p163
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

public class Ex1316ReadDates {

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

  public static void main(String[] args) {

  Date[] data = readDates("Ex1316ReadDates.dates.txt");
  pa(data,89); // 6/15/2016 is missing intentionally from the output
  // Date[6/10/2016,6/11/2016,6/12/2016,6/13/2016,6/14/2016,6/16/2016,6/17/2016,6/18/2016]
  // contents of Ex1316ReadDates.dates.txt:
  //  06/10/2016 06/11/2016 06/12/2016
  //  06/13/2016 06/14/2016 06/35/2016
  //  06/16/2016 06/17/2016 06/18/2016

  }

}
