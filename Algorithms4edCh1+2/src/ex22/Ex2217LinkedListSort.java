package ex22;

import static ds.LinkedList.isSorted;
import static ds.LinkedList.sortNM;
import static ds.LinkedList.sortRolfe;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import analysis.Timer;
import ds.LinkedList;

public class Ex2217LinkedListSort {

  /* p286
  2.2.17 Linked-list sort. Implement a natural mergesort for linked lists. 
  (This is the method of choice for sorting linked lists because it uses no 
  extra space and is guaranteed to be linearithmic.)
  
  I implemented a couple of static mergesorts in ds.LinkedList. They are named
  sortSlowAndDifficult and SortNM. sortSlowAndDifficult spends too much time
  extracting sublists for merging and splicing in the merged sublist. Possibly
  this could be improved by doing it only at the end of every full pass when the
  the accumulated sublist sizes plus the next are greater than the initial length
  of the LinkedList. But doing that is too much trouble. SortNM is an easy to
  implement take on NaturalMerge and is fast but has ~2N storage overhead for
  converting the Linked List to an array and for the aux array needed for merging. 
  Timothy J. Rolfe's mergesort algorithms for getting runs and merging keeps 
  storage overhead at ~N and sorts quickly, sometimes faster than sortNM. It's  
  available at http://penguin.ewu.edu/~trolfe/NaturalMerge/List.java as part of an 
  article on natural mergesorts at http://penguin.ewu.edu/~trolfe/NaturalMerge/index.html. 
  I adapted it for my generic version of LinkedList. Some tests and results for these 
  mergesorts are below, excluding sortSlowAndDifficult
   */ 

  public static void main(String[] args) {

    Random r; Integer[] x, y; LinkedList<Integer> lx, ly;
    Double[] d, d2; LinkedList<Double> lu, lv;
    Timer t = new Timer(); long time, rtime; int n=0; 
    
    // sortRolfe vs. sortNM for small to medium small random Integer arrays
    // this is mainly to show they both sort, but it's interesting that
    // sortRolfe sorts faster than sortNM for this test. somehow sortRolfe
    // has better agility here. 
    time = 0; rtime = 0; 
    System.out.println("\n    for random Integer arrays");
    System.out.println("           total times");
    System.out.println("    sortRolfe       sortNM");
    for (int i = 2; i < 10002; i++) {
      x = rangeInteger(1, i, 1);
      r = new Random(System.currentTimeMillis());
      shuffle(x, r);
      y = x.clone();
      lx  = new LinkedList<Integer>(x);
      if (i % 2 == 0) {
        t.reset();
        sortRolfe(lx);
        rtime += t.num();
        assert isSorted(lx);
      } else {
        t.reset();
        sortNM(lx);
        time += t.num();
        assert isSorted(lx);
      }
      ly  = new LinkedList<Integer>(y);
      if (i % 2 == 0) {
        t.reset();
        sortNM(ly);
        time += t.num();
        assert isSorted(ly);
      } else {
        t.reset();
        sortRolfe(ly);
        rtime += t.num();
        assert isSorted(ly);
      }
    }
    System.out.printf("   %10d   %10d\n", rtime, time);
    
    /*
            for random Integer arrays
                   total times
            sortRolfe       sortNM
                 6717         8357   
                 6529         8455
                 6688         8380

    */
    
    // sortRolfe vs. sortNM for small to large random Integer arrays
    int[] len = {11, 101, 1001, 10001, 100001, 1000001}; // array lengths + 1
    String[] lens = {"10", "100", "1K", "10K", "100K", "1M"}; n = 100;
    System.out.println("\n    for random Integer arrays");
    System.out.println("    array           average times");
    System.out.println("    length    sortRolfe        sortNM");
    for (int k = 0; k < len.length; k++) {
      x = rangeInteger(1, len[k]); time = 0; rtime = 0; 
      for (int i = 0; i < n; i++) {
        r = new Random(System.currentTimeMillis());
        shuffle(x,r);
        y = x.clone();
        lx = new LinkedList<Integer>(x);
        if (i % 2 == 0) {
          t.reset();
          sortRolfe(lx);
          rtime += t.num();
        } else {
          t.reset();
          sortNM(lx);
          time += t.num();
        }
        ly  = new LinkedList<Integer>(y);
        if (i % 2 == 0) {
          t.reset();
          sortNM(ly);
          time += t.num();
        } else { 
          t.reset();
          sortRolfe(ly);
          rtime += t.num();
        }
      }
      System.out.printf("    %6s   %10.3f    %10.3f\n", lens[k], 1.*rtime/n, 1.*time/n);
    }
    /*      for random Integer arrays                   
            array           average times
            length    sortRolfe        sortNM
                10        0.010         0.060
               100        0.000         0.100
                1K        0.100         1.260
               10K        1.620         2.280
              100K       40.360        33.620
                1M     1000.890       787.770
                
            array           average times
            length    sortRolfe        sortNM
                10        0.000         0.000
               100        0.020         0.000
                1K        0.120         0.090
               10K        1.600         1.740
              100K       43.600        31.070
                1M      942.330       706.130 

            array           average times
            length    sortRolfe        sortNM
                10        0.000         0.000
               100        0.010         0.010
                1K        0.100         0.100
               10K        1.520         1.890
              100K       49.360        36.410
                1M      946.100       673.360                               
    */
    
    // sortRolfe vs. sortNM for small to large random Double arrays
    System.out.println("\n    for random Double arrays");
    System.out.println("    array           average times");
    System.out.println("    length    sortRolfe        sortNM");
    for (int k = 0; k < len.length; k++) {
      time = 0; rtime = 0; 
      for (int i = 0; i < n; i++) {
        r = new Random(System.currentTimeMillis());
        d = new Double[len[k]-1];
        for (int l = 0; l < len[k]-1; l++) d[l] = r.nextDouble();
        d2 = d.clone();
        lu = new LinkedList<Double>(d);
        if (i % 2 == 0) {
          t.reset();
          sortRolfe(lu);
          rtime += t.num();
        } else {
          t.reset();
          sortNM(lu);
          time += t.num();
        }
        lv  = new LinkedList<Double>(d2);
        if (i % 2 == 0) {
          t.reset();
          sortNM(lv);
          time += t.num();
        } else {
          t.reset();
          sortRolfe(lv);
          rtime += t.num();
        }
      }
      System.out.printf("    %6s   %10.3f    %10.3f\n", lens[k], 1.*rtime/n, 1.*time/n);
    }
    /*      for random Double arrays                   
            array           average times
            length    sortRolfe        sortNM
                10        0.290         0.020
               100        0.220         0.010
                1K        0.270         0.270
               10K        2.500         2.490
              100K       44.240        44.530
                1M      768.780       767.910
                
            array           average times
            length    sortRolfe        sortNM
                10        0.020         0.010
               100        0.150         0.120
                1K        0.510         0.400
               10K        2.430         2.430
              100K       40.180        40.110
                1M      664.070       671.220
                
            array           average times
            length    sortRolfe        sortNM
                10        0.020         0.020
               100        0.070         0.090
                1K        0.260         0.400
               10K        2.390         2.370
              100K       41.690        41.640
                1M      686.030       679.100             
    */
  }
  
}

