package ex13;

import static v.ArrayUtils.*;

import ds.Queue;

//  p168
//  1.3.37 Josephus problem. In the Josephus problem from antiquity, N people are in dire
//  straits and agree to the following strategy to reduce the population. They arrange them-
//  selves in a circle (at positions numbered from 0 to N–1) and proceed around the circle,
//  eliminating every Mth person until only one person is left. Legend has it that Josephus
//  figured out where to sit to avoid being eliminated. Write a  Queue client  Josephus that
//  takes N and M from the command line and prints out the order in which people are
//  eliminated (and thus would show Josephus where to sit in the circle).
//  % java Josephus 7 2
//  1 3 5 0 4 2 6

// looks like an array application to me, though some would prefer an ArrayList and
// nobody would use a queue except to pass this exercise, which is bad training for
// encouraging choice of techically appropriate solutions.

public class Ex1337JosephusProblem {
  
  public static int[] josephus(int n, int m) {
    // solves the Josephus problem for n and m by returning an array with elements 
    // whose values are the indices in order of eliminination of every mth element 
    // from an array of length n until none are left. the value of the last element 
    // in the returned array is the answer to the Josephus problem.
    int[] a = range(0, n);
    int[] b = new int[a.length];
    int id = 0; int i = 0;
    while(a.length > 0) {
      id = (id + m - 1) % a.length;
      b[i++] = a[id];
      a = remove(a, id);
    }    
    return b;
  }

  // this is the client
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);
    System.out.println("n="+n); //7
    int m = Integer.parseInt(args[1]);
    System.out.println("m="+m); //2
    int[] josephus = josephus(n, m);
    pa(josephus,75,1,1); //[1,3,5,0,4,2,6]
    
    // dragging a queue into it...
    String[] sa = {"Beth", "Peter", "Laura", "Jack", "Cindy", "Steve", "Rebecca"};
    assert sa.length == josephus.length;
    Queue<String> q = new Queue<String>(sa);
    System.out.println("The person remaining from 7-2 Josephus elimination is "
        +q.toArray()[josephus.length-1]+".");
    // The person remaining from 7-2 Josephus elimination is Rebecca.
    
  }

}
