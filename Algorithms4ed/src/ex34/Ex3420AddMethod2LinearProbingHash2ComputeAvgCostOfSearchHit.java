package ex34;

import java.security.SecureRandom;
import java.util.Iterator;

import st.LinearProbingHashSTX;

/* p482
  3.4.20  Add a method to  LinearProbingHashST that computes the average 
  cost of a search hit in the table, assuming that each key in the table 
  is equally likely to be sought.
  
  I added a couple of methods to determine the average search hit cost.
  Neither is based on a theoretical formula and both are dynamic. The
  first is called searchHitAvgCost() and it returns the average number of
  probes for get() by doing a relatively small number of gets on non-null 
  keys, by default up to 503 gets, if possible, starting with keys[0]
  and proceeding sequentially and then does reservoir sampling over the
  remaining N-503 keys if N>503. This method requires modification of get() 
  to count probes, that had already been done.
  
  The second method is called searchHitAvgCost2() and it returns the average 
  number of probes by calculation from data in an IdentityHashMap that, only 
  for each key requiring more than one probe to insert with put, stores its
  identityHashCode as key and the number of probes required as value. The 
  calculation sums the values in the IdentityHashMap, adds them to N minus its 
  size and divides the total by N. Implementation of this method required mod-
  ification of put() and delete() in order to maintain the IdentityHashMap.

 */             

public class Ex3420AddMethod2LinearProbingHash2ComputeAvgCostOfSearchHit {
  
  public static void testSearchHitCostMethods(int lim, int bound, int ss) {
    // test search hit cost estimations methods in LinearProbingHashSTX,
    // namely searchHitAvgCost() and searchHitAvgCost2().
    // Integer keys and values are used.
    // lim the inclusive upper bound on the number of keys to put.
    // bound is the exclusive upper bound on each randomly generated key.
    // ss is the sample size for searchHitAvgCost() and is applied only if >0.    
  
    SecureRandom r = new SecureRandom();
    LinearProbingHashSTX<Integer,Integer> h = new LinearProbingHashSTX<>();
    int c = 0;
    while (c < lim) {
      int x = r.nextInt(bound);
      h.put(x, x);
      c++;
    }
    // sum probes used for get of all keys
    int probes = 0;
    Iterator<Integer> it = h.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h.zeroProbes();
      h.get(x);
      probes += h.probes();
    }
    if (ss > 0) h.setSearchHitSampleSize(ss);
    // print results
    System.out.println("testing search hit cost methods with lim="
        +lim+", bound="+bound+", ss="+h.getSearchHitSampleSize()+":");
    System.out.println("actualSearchHitAvgCost = "+(1.*probes/h.size()));
    // test the sampling method
    System.out.println("searchHitAvgCost = "+h.searchHitAvgCost());
    // test the method tracking the number of keys taking >1 probes.
    System.out.println("searchHitAvgCost2 = "+h.searchHitAvgCost2());
    // run the worst case method
    System.out.println();
  }

  public static void main(String[] args) {
    
    testSearchHitCostMethods(10000, 5000, 0);
    
    testSearchHitCostMethods(10000, 500, 0);
    
    testSearchHitCostMethods(10000, 50, 0);

    testSearchHitCostMethods(10000, 5, 0);
    
    testSearchHitCostMethods(10000, 10000, 0);

    testSearchHitCostMethods(10000, 20000, 0);
    
    testSearchHitCostMethods(10000, 40000, 0);
    
    testSearchHitCostMethods(10000, 100000, 0);
    
    testSearchHitCostMethods(10000, 100000,1000);

    
/*
    testing search hit cost methods with lim=10000, bound=5000, ss=503:
    actualSearchHitAvgCost = 1.0
    searchHitAvgCost = 1.0
    searchHitAvgCost2 = 1.206375058166589
    
    testing search hit cost methods with lim=10000, bound=500, ss=503:
    actualSearchHitAvgCost = 1.0
    searchHitAvgCost = 1.0
    searchHitAvgCost2 = 1.062
    
    testing search hit cost methods with lim=10000, bound=50, ss=503:
    actualSearchHitAvgCost = 1.0
    searchHitAvgCost = 1.0
    searchHitAvgCost2 = 1.04
    
    testing search hit cost methods with lim=10000, bound=5, ss=503:
    actualSearchHitAvgCost = 1.0
    searchHitAvgCost = 1.0
    searchHitAvgCost2 = 1.0
    
    testing search hit cost methods with lim=10000, bound=10000, ss=503:
    actualSearchHitAvgCost = 1.0
    searchHitAvgCost = 1.0
    searchHitAvgCost2 = 1.4884821710318712
    
    testing search hit cost methods with lim=10000, bound=20000, ss=503:
    actualSearchHitAvgCost = 1.3587776499168904
    searchHitAvgCost = 1.3081510934393639
    searchHitAvgCost2 = 1.4204065976217874
    
    testing search hit cost methods with lim=10000, bound=40000, ss=503:
    actualSearchHitAvgCost = 1.0763574660633484
    searchHitAvgCost = 1.0974155069582505
    searchHitAvgCost2 = 1.355316742081448
    
    testing search hit cost methods with lim=10000, bound=100000, ss=503:
    actualSearchHitAvgCost = 1.1470216796463901
    searchHitAvgCost = 1.151093439363817
    searchHitAvgCost2 = 1.3887602609976848
    
    testing search hit cost methods with lim=10000, bound=100000, ss=1000:
    actualSearchHitAvgCost = 1.1356857802982567
    searchHitAvgCost = 1.134
    searchHitAvgCost2 = 1.3315479941188826

*/


  }

}

