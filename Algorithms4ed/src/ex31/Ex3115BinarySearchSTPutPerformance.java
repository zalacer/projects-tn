package ex31;

import static java.lang.Math.*;
import static analysis.Log.*;

/* p390
  3.1.15  Assume that searches are 1,000 times more frequent than 
  insertions for a BinarySearchST client. Estimate the percentage 
  of the total time that is devoted to insertions, when the number 
  of searches is 10^3, 10^6 and 10^9.
  
  From p384 Proposition B the worst case for insert cost is N^2 for
  ordered ST's and from p385 for BinarySearchST on average (after N 
  random inserts) the cost of a search hit is lgN.
  
  Assuming the only operations done are put (insertion) and get (search),
  all keys are unique on insertion, and searches aren't done until N keys
  have been inserted after which no more keys are inserted:
              
             average   worse case
  number of  cost of   cost of                      
  searches   searches  insertions  total cost       insertions %cost 
  ---------  --------  ----------  -------------    ------------------ 
  10^3       10^3*lgN  N^2         10^3*lgN+N^2     N^2/(10^3*lgN+N^2)
  10^6       10^6*lgN  N^2         10^6*lgN+N^2     N^2/(10^6*lgN+N^2)
  10^9       10^9*lgN  N^2         10^9*lgN++N^2    N^2/(10^9*lgN+N^2)
  
  For searches 1000 times more frequent than inserts 
  and assuming time taken ~ cost:
  
  N            #searches    insertions %time
  10           1000         0.043
  10           1000000      0.000
  10           1000000000   0.000
  100          1000         0.022
  100          1000000      0.000
  100          1000000000   0.000
  1000         1000         0.014
  1000         1000000      0.000
  1000         1000000000   0.000
  10000        1000         0.011
  10000        1000000      0.000
  10000        1000000000   0.000
  100000       1000         0.009
  100000       1000000      0.000
  100000       1000000000   0.000
  1000000      1000         0.007
  1000000      1000000      0.000
  1000000      1000000000   0.000
  10000000     1000         0.006
  10000000     1000000      0.000
  10000000     1000000000   0.000
  100000000    1000         0.005
  100000000    1000000      0.000
  100000000    1000000000   0.000
  1000000000   1000         0.005
  1000000000   1000000      0.000
  1000000000   1000000000   0.000
 */

public class Ex3115BinarySearchSTPutPerformance {
  
  public static double insertionsCost(long n, long r, long g) {
    // return the puts %cost for ST.size() == n and number
    // of gets == g, gets are r times more frequent than puts
    // and assuming only puts and gets are done and all keys
    // are unique on insertion and have all been inserted before
    // any searches.
    return 100.*pow(n,2)/(pow(n,2)*g*ln(n));
  }
  
  public static void insertionsCost(long n) {
    long g = 1000L;
    while (g < 10000000000L) {
      System.out.printf("%-11d  %-11d  %5.3f\n", n, g, insertionsCost(n,1000,g));
      g *= 1000;
    }
  }
  
  public static void insertionsCost() {
    System.out.println("N            #searches    insertions %time");
    long x = 10;
    while (x < 10000000000L) { insertionsCost(x); x *= 10; } 
  }
   
  public static void main(String[] args) {
    
    insertionsCost();

  }

}
