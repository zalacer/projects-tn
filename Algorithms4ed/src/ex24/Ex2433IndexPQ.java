package ex24;

/* p332
  2.4.33 Index priority-queue implementation. Implement the basic operations
  in the index priority-queue API on page 320 by modifying Algorithm 2.6 as 
  follows: Change pq[] to hold indices, add an array keys[] to hold the key 
  values, and add an array qp[] that is the inverse of pq[] —  qp[i] gives 
  the position of  i in  pq[] (the index  j such that pq[j] is  i ). Then 
  modify the code in Algorithm 2.6 to maintain these data structures.  Use 
  the convention that  qp[i] = -1 if  i is not on the queue, and include a method
  contains() that tests this condition. You need to modify the helper methods
  exch() and  less() but not  sink() or  swim().
  
  Implementations are available at:
    http://algs4.cs.princeton.edu/24pq/IndexMaxPQ.java
    http://algs4.cs.princeton.edu/24pq/IndexMinPQ.java
  See also:
    http://algs4.cs.princeton.edu/99misc/IndexMultiwayMinPQ.java
    http://algs4.cs.princeton.edu/99misc/IndexBinomialMinPQ.java
    http://algs4.cs.princeton.edu/99misc/IndexFibonacciMinPQ.java
    ftp://ftp.cs.princeton.edu/pub/cs226/map/IndexPQ.java

  These are all available locally in the pq package.  
 */

public class Ex2433IndexPQ {

  public static void main(String[] args) {

  }

}
