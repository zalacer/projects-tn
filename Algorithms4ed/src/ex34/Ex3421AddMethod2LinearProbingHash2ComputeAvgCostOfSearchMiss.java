package ex34;

import static analysis.Log.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import ds.Queue;
import st.LinearProbingHashSTX;

/* p482
  3.4.21  Add a method to  LinearProbingHashST that computes the average 
  cost of a search miss in the table, assuming a random hash function. 
  Note : You do not have to compute any hash functions to solve this problem.

  I implemented two methods to compute the average cost of a search miss.  
  The first is based on the on the discussion of Proposition M in the text on 
  page  473 and is searchMissAvgCost(). The second uses the formula in the 
  statement of the same proposition and is searchMissAvgCost2(). In the tests 
  I did, that are shown below, both give results that are generally so way off
  off as to be worthless. I guess that means Assumption J (the uniform hashing 
  assumption) is invalid for these tests, however I tried to conform to it by 
  using hash tables of the same fixed size as the exclusive upper bound of keys 
  generated with nearly uniform randomness over the range of indices in the table.

 */             

@SuppressWarnings("unused")
public class Ex3421AddMethod2LinearProbingHash2ComputeAvgCostOfSearchMiss {
  
  public static void testSearchMissCostMethods(int lim, int bound) {
    // test search hit cost estimations methods in LinearProbingHashSTX,
    // namely searchHitAvgCost() and searchHitAvgCost2().
    // Integer keys and values are used.
    // lim the inclusive upper bound on the number of keys to put.
    // bound is the exclusive upper bound on each randomly generated key
    //   inserted into test LinearProbingHashSTX that's created with fixed
    //   size bound to conform with Assumption J on page 463 of the text.
    SecureRandom r = new SecureRandom();
    LinearProbingHashSTX<Integer,Integer> h = new LinearProbingHashSTX<>(bound,true);
    int c = 0;
    while (c < lim) {
      int x = r.nextInt(bound);
      h.put(x, x);
      c++;
    }
    // create search misses and count the probes
    int probes = 0, m = h.getM(); c = 0;
    Iterator<Integer> it = h.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h.zeroProbes();
      for (int i = 0; i < 9; i++) {
        if (h.get(x+i*m) == null) probes += h.probes();
        c++;
      }
    }
    System.out.println(c);
    // print results
    System.out.println("testing search miss cost methods with lim="
        +lim+", bound="+bound+":");
    System.out.println("actualSearchMissAvgCost = "+(1.*probes/c));
    System.out.println("searchMissAvgCost = "+h.searchMissAvgCost());
    System.out.println("searchMissAvgCost2 = "+h.searchMissAvgCost2());
    System.out.println();
  }

  public static void main(String[] args) {
    
    testSearchMissCostMethods(10000, 5000);
    
    testSearchMissCostMethods(10000, 10000);

    testSearchMissCostMethods(10000, 20000);
    
    testSearchMissCostMethods(10000, 40000);
    
    testSearchMissCostMethods(10000, 100000);
    
    testSearchMissCostMethods(10000, 1000000);

/*   
    testing search miss cost methods with lim=10000, bound=5000:
    actualSearchMissAvgCost = 33.816747854326145
    searchMissAvgCost = 3.6397705078125
    searchMissAvgCost2 = 1.4208300462201802
    
    testing search miss cost methods with lim=10000, bound=500:
    actualSearchMissAvgCost = 1006.8888888888889
    searchMissAvgCost = 123.826171875
    searchMissAvgCost2 = 2.409445836489715
    
    testing search miss cost methods with lim=10000, bound=50:
    actualSearchMissAvgCost = 106.88888888888889
    searchMissAvgCost = 11.5703125
    searchMissAvgCost2 = 1.846482577251808
    
    testing search miss cost methods with lim=10000, bound=5:
    actualSearchMissAvgCost = 16.88888888888889
    searchMissAvgCost = 2.625
    searchMissAvgCost2 = 1.5578512396694215
    
    testing search miss cost methods with lim=10000, bound=10000:
    actualSearchMissAvgCost = 15.89795810129939
    searchMissAvgCost = 2.67218017578125
    searchMissAvgCost2 = 1.815991664695963
    
    testing search miss cost methods with lim=10000, bound=20000:
    actualSearchMissAvgCost = 21.956955425203322
    searchMissAvgCost = 3.4576416015625
    searchMissAvgCost2 = 2.3394616960359658
    
    testing search miss cost methods with lim=10000, bound=40000:
    actualSearchMissAvgCost = 11.508809336774773
    searchMissAvgCost = 2.1627197265625
    searchMissAvgCost2 = 1.4432747585365584
    
    testing search miss cost methods with lim=10000, bound=100000:
    actualSearchMissAvgCost = 11.86836674012663
    searchMissAvgCost = 2.184814453125
    searchMissAvgCost2 = 1.4941112815503257

*/

  }

}

