package ex25;

import static utils.RandomUtils.randomString;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.ofDim;

import java.security.SecureRandom;
import java.util.function.Consumer;

import analysis.Timer;
import ds.Date;
import ds.Transaction;
import sort.Heap;
import sort.Merges;
import sort.QuickX;
import sort.Shell;

/* p358
  2.5.33 Random transactions. Develop a generator that takes an argument N, 
  generates N random  Transaction objects (see Exercises 2.1.21 and 2.1.22), 
  using assumptions about the transactions that you can defend. Then compare 
  the performance of shellsort, mergesort, quicksort, and heapsort for sorting 
  N transactions, for N=10^3, 10^4, 10^5 and 10^6.
  
  I'm using transactions with fields that are random within finite ranges,
  reasonable by not causing errors, relatively convenient and tend to make
  duplicates unlikely through 10^6 values and similarly for fields except 
  date.
  
 */

public class Ex2533RandomTransactions { 
  
  public static SecureRandom r = new SecureRandom();

  public static Date date() {
    // return a random Date.
    int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    int month = r.nextInt(12)+1;
    int day = r.nextInt(days[month])+1;
    int year = r.nextInt(2101)+1900;
    return new Date(month, day, year);
  }
  
  public static Transaction transaction() {
    // return a random Transaction.
    double amount = Double.parseDouble(r.nextInt(50001)+5+"."+r.nextInt(100));
    return new Transaction(randomString(12), date(), amount);
  }
  
  public static Transaction[] transaction(int n) {
    // return a Transaction array n random Transactions.
    Transaction[] t = ofDim(Transaction.class, n);
    for (int i = 0; i < n; i++) t[i] = transaction();
    return t;
  }
   
  public static double algsort(int n, String alg, int trials) {
    // sort trials numbers of random Transaction arrays of length n with 
    // the algorithm represented by alg and return the mean sorting time. 
    Consumer<Transaction[]> c;
    switch (alg) {
      case "Shell":     c = (x) ->  Shell.sort(x);              break;
      case "Merge":     c = (x) ->  Merges.topDownAcCoSm(x,31); break;
      case "QuickX":    c = (x) ->  QuickX.sort(x);             break;
      case "Heap":      c = (x) ->  Heap.sort(x);               break;
      default: throw new IllegalArgumentException("stable: alg unrecognized");
    }
    long[] y = new long[trials]; Timer t = new Timer();
    Transaction[] z = transaction(n);
    for (int i = 0; i < trials; i++) {
      t.reset();
      c.accept(z);
      y[i] = t.num();
    }
    return mean(y);
  }
  
  public static void algsort(int trials) {
    // run algsort(int,int,int) with parameters specified by this exercise
    // and print the results.
    int[] n = {1000,10000,100000,1000000};
    String[] alg = {"Shell","Merge","QuickX","Heap"};
    System.out.println("N         alg       time(ms)");
    for (int i = 0; i < n.length; i++) {
      for (int j = 0; j < alg.length; j++) {
        double t = algsort(n[i], alg[j], trials);
        System.out.printf("%-8d  %-8s  %8.0f\n", n[i], alg[j], t);
      }
    }
    System.out.println();
  }
  /*
  N         alg       time(ms)
  1000      Shell            4
  1000      Merge           19
  1000      QuickX           4
  1000      Heap             3
  10000     Shell           27
  10000     Merge           19
  10000     QuickX          14
  10000     Heap            22
  100000    Shell          268
  100000    Merge          150
  100000    QuickX         193
  100000    Heap           235
  1000000   Shell         3902
  1000000   Merge         1427
  1000000   QuickX        1477
  1000000   Heap          2717  
*/  
  public static void algsort2(int trials) {
    // run algsort(int,int,int) with parameters specified by this exercise
    // and print the results.
    int[] n = {1000,10000,100000,1000000};
    String[] alg = {"Shell","Merge","QuickX","Heap"};
    System.out.println("N         alg       time(ms)");
    for (int j = 0; j < alg.length; j++) {
      for (int i = 0; i < n.length; i++) {
        double t = algsort(n[i], alg[j], trials);
        System.out.printf("%-8d  %-8s  %8.0f\n", n[i], alg[j], t);
      }
    }
    System.out.println();
  }
/*
  N         alg       time(ms)
  1000      Shell            4
  10000     Shell           31
  100000    Shell          276
  1000000   Shell         4379
  1000      Merge           11
  10000     Merge           27
  100000    Merge          223
  1000000   Merge         1411
  1000      QuickX           3
  10000     QuickX          13
  100000    QuickX         177
  1000000   QuickX        1252
  1000      Heap             3
  10000     Heap            15
  100000    Heap           249
  1000000   Heap          3051
*/  

  public static void main(String[] args) {
    
    algsort(1);
    algsort2(1);

  }

}


