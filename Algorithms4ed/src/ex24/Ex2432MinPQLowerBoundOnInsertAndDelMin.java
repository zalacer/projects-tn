package ex24;

/* p332
  2.4.32 Lower bound. Prove that it is impossible to develop a compare-
  based implementation of the MinPQ API such that both insert and delete 
  the minimum guarantee to use ~N log log N compares.
  
  As shown on http://algs4.cs.princeton.edu/24pq/, if that were possible,
  then insert and delete the minimum could be used to make a compare-based 
  sorting method that uses ~N log log N compares. But that violates 
  Proposition I on p280:
  
    Proposition I. No compare-based sorting algorithm can guarantee to 
    sort N items with fewer than lg(N !) ~ N lg N compares.
 */

public class Ex2432MinPQLowerBoundOnInsertAndDelMin {

  public static void main(String[] args) {

  }

}
