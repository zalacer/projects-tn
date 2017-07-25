package ex34;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import st.LinearProbingHashSTX;

/* p483
  3.4.28 Double hashing. Modify LinearProbingHashST to use a second 
  hash function to define the probe sequence. Specifically, replace  
  (i + 1) % M (both occurrences) by (i + k) % M where k is a nonzero 
  key-dependent integer that is relatively prime to  M. Note : You may 
  meet the last condition by assuming that  M is prime. Give a trace 
  of the process of inserting the keys  E A S Y Q U T I O N in that 
  order into an initially empty table of size M = 11, using the hash 
  functions described in the previous exercise. Give the average num-
  ber of probes for random search hit and search miss in this table.
  
  (i + 1) % M occurs in put(), get() and delete() = 3 occurrences.
  
  I tried several double hashing implementations and settled on two,
  both which are invoked by used of string arguments in a constructor.
  Both implementations use the default hash function as the first hash
  function that is:
    hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; }; 
 
  The implementation invoked with "dhash" then uses
    key.hashCode()%(m-1)
  as the second hash function and calculates steps using it with
    i = (i + (key.hashCode()%(m-1))) % m)  
  based on a suggestion on p273 of Introduction to Algorithms; Cormen, 
  Leiserson, Rivest, Stein; 3Ed; 2009; MIT. The table size m is always 
  chosen to be prime resulting in larger tables compared to the default.
  
  The implementation invoked with "dhash2" uses
    hash2 = (k) -> { return prime[0] - hash.apply(k) % prime[0]; };
  as the second hash function where prime[0] is the greatest prime
  smaller than m and calculates steps using it with 
    i = (i+h2) % m
  where i is initialized to hash.apply(key) and h2 is initialzed to
  hash2.apply(key). This is based on 
    http://www.sanfoundry.com/java-program-implement-hash-table-with-double-hashing/
  that's implemented locally at st.HashTable.
  
  Tests below show that double hashing is relatively inconsequential when alpha
  is low but considerably improves performance for search misses, search hits 
  and inserts when it's high.
 
*/

public class Ex3428DoubleHashing {
  
  public static <T> void traceLPDblH(T[] a, int m, boolean trace, boolean noresize) {
    if (a == null) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "the array is null");
    if (m <= 0) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "m is <= 0");
    if (!isPrime(m)) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "m isn't prime");
    LinearProbingHashSTX<T,Integer> h;
    if (noresize == true) h = new LinearProbingHashSTX<>(m, "dhash", true);
    else  h = new LinearProbingHashSTX<>(m, "dhash");
    String c = a.getClass().getComponentType().getSimpleName();
    if (c.equals("Character")) {
      String x = new String((char[])unbox((Character[])a));
      if (noresize == true)
        System.out.println(x+" using double hashing 1st version with "+c+" keys "
            + "and fixed table size "+m+":");
      else System.out.println(x+" using double hashing 1st version with "+c+" keys:");
    } else {
      if (noresize == true)
        System.out.println("using double 1st version hashing with "+c+" keys "
            + "and fixed table size "+m+":");
      else System.out.println("using double 1st version hashing with "+c+" keys:");
    }
    if (trace) System.out.println("  keys trace:");
    for (T t : a) {
      if (t != null) h.put(t, 1);
      if (trace) { System.out.print("    "); par(h.getKeys()); }
    }
    System.out.println("  alpha                = "+h.alpha());
    System.out.println("  total probes         = "+h.probes());
    System.out.println("  avg search hit cost  = "+h.searchHitAvgCost());
    System.out.println("  avg search miss cost = "+h.searchMissAvgCost());
    System.out.println();
  }
  
  public static <T> void traceLPDblH2(T[] a, int m, boolean trace, boolean noresize) {
    if (a == null) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "the array is null");
    if (m <= 0) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "m is <= 0");
    LinearProbingHashSTX<T,Integer> h;
    if (noresize == true) h = new LinearProbingHashSTX<>(m, "dhash2", true);
    else  h = new LinearProbingHashSTX<>(m, "dhash2");
    String c = a.getClass().getComponentType().getSimpleName();
    if (c.equals("Character")) {
      String x = new String((char[])unbox((Character[])a));
      if (noresize == true)
        System.out.println(x+" using double hashing 2nd version with "+c+" keys " 
            + "and fixed table size "+m+":");
      else System.out.println(x+" using double hashing 2nd version with "+c+" keys:");
    } else {
      if (noresize == true)
        System.out.println("using double hashing 2nd version with "+c+" keys "
            + "and fixed table size "+m+":");
      else System.out.println("using double hashing 2nd version with "+c+" keys:");
    }
    if (trace) System.out.println("  keys trace:");
    for (T t : a) {
      if (t != null) h.put(t, 1);
      if (trace) { System.out.print("    "); par(h.getKeys()); }
    }
    System.out.println("  alpha                = "+h.alpha());
    System.out.println("  total probes         = "+h.probes());
    System.out.println("  avg search hit cost  = "+h.searchHitAvgCost());
    System.out.println("  avg search miss cost = "+h.searchMissAvgCost());
    System.out.println();
  }
  
  public static <T> void traceLPDefH(T[] a, int m, boolean trace, boolean noresize) {
    if (a == null) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "the array is null");
    if (m <= 0) throw new IllegalArgumentException("traceLinearProbingDoubleHashing: "
        + "m is <= 0");
    LinearProbingHashSTX<T,Integer> h;
    if (noresize == true) h = new LinearProbingHashSTX<>(m, true);
    else  h = new LinearProbingHashSTX<>(m);
    String c = a.getClass().getComponentType().getSimpleName();
    if (c.equals("Character")) {
      String x = new String((char[])unbox((Character[])a));
      if (noresize == true)
        System.out.println(x+" using default hashing with "+c+" keys "
            + "and fixed table size "+m+":");
      else System.out.println(x+" using default hashing with "+c+" keys:");
    } else {
      if (noresize == true) 
        System.out.println("using default hashing with "+c+" keys "
            + "and fixed table size "+m+":");
      else System.out.println("using default hashing with "+c+" keys:");
    }
    if (trace) System.out.println("  keys trace:");
    for (T t : a) {
      if (t != null) h.put(t, 1);
      if (trace) { System.out.print("    "); par(h.getKeys()); }
    }
    System.out.println("  alpha                = "+h.alpha());
    System.out.println("  total probes         = "+h.probes());
    System.out.println("  avg search hit cost  = "+h.searchHitAvgCost());
    System.out.println("  avg search miss cost = "+h.searchMissAvgCost());
    System.out.println();
  }
  
  public static boolean isPrime(int N) {
    if (N < 2) return false;
    for (int i = 2; i*i <= N; i++)
      if (N % i == 0) return false;
    return true;
  }
 
  public static void main(String[] args) {
    
//    String[] sa = "E A S Y Q U T I O N".split("\\s+");
//    HashTable h = new HashTable(30);
//    for (String s : sa) h.put(s,1);
//    h.printHashTable();
//    
//    System.exit(0);
    
    Character[] ca = (Character[])box(("EASYQUTION").toCharArray());
    traceLPDblH(ca,11,true,false);
    traceLPDblH2(ca,11,true,false);
    traceLPDefH(ca,11,true,false);
    traceLPDblH(ca,11,true,true);
    traceLPDblH2(ca,11,true,true);
    traceLPDefH(ca,11,true,true);
    
    SecureRandom r = new SecureRandom();
    Integer[] ia = new Integer[100003]; int c = 0;
    while (c < ia.length/10) {
      for (int i = 1; i < 17; i++) {
        int x = r.nextInt(999999)+1;
        ia[c++] = i*31+x;
      }
    }
    traceLPDblH(ia,10987,false,false);
    traceLPDblH2(ia,10987,false,false);
    traceLPDefH(ia,10987,false,false);
    traceLPDblH(ia,10987,false,true);
    traceLPDblH2(ia,10987,false,true);
    traceLPDefH(ia,10987,false,true);
/*
    EASYQUTION using double hashing 1st version with Character keys:
      keys trace:
        [null,null,null,E,null,null,null,null,null,null,null]
        [null,null,null,E,null,null,null,null,null,null,A]
        [null,null,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,Q,null,S,null,null,null,A]
        m=23
        [E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,null,U,null,null,A,Y,null,null]
        [E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,T,U,null,null,A,Y,null,null]
        [E,null,null,null,I,null,null,null,null,null,null,null,Q,null,S,T,U,null,null,A,Y,null,null]
        [E,null,null,null,I,null,null,null,null,null,O,null,Q,null,S,T,U,null,null,A,Y,null,null]
        [E,null,null,null,I,null,null,null,null,N,O,null,Q,null,S,T,U,null,null,A,Y,null,null]
      total probes         = 0
      avg search hit cost  = 1.0
      avg search miss cost = 2.217391304347826
    
    EASYQUTION using double hashing 2nd version with Character keys:
      keys trace:
        [null,null,null,E,null,null,null,null,null,null,null]
        [null,null,null,E,null,null,null,null,null,null,A]
        [null,null,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,Q,null,S,null,null,null,A]
        m=22
        [null,Y,null,E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,null,U,null,A]
        [null,Y,null,E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,null,null,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,null,O,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,N,O,null,Q,null,S,T,U,null,A]
      total probes         = 0
      avg search hit cost  = 1.0
      avg search miss cost = 2.1818181818181817
    
    EASYQUTION using default hashing with Character keys:
      keys trace:
        [null,null,null,E,null,null,null,null,null,null,null]
        [null,null,null,E,null,null,null,null,null,null,A]
        [null,null,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,null,null,S,null,null,null,A]
        [null,Y,null,E,Q,null,S,null,null,null,A]
        m=22
        [null,Y,null,E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,null,U,null,A]
        [null,Y,null,E,null,null,null,null,null,null,null,null,null,null,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,null,null,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,null,O,null,Q,null,S,T,U,null,A]
        [null,Y,null,E,null,null,null,I,null,null,null,null,N,O,null,Q,null,S,T,U,null,A]
      total probes         = 0
      avg search hit cost  = 1.0
      avg search miss cost = 2.1818181818181817
    
    using double hashing 1st version with Integer keys:
        m=23
        m=61
        m=127
        m=509
        m=1021
        m=4093
        m=8191
        m=32749
      total probes         = 5232
      avg search hit cost  = 1.2127236580516898
      avg search miss cost = 2.137164493572323
    
    using double hashing 2nd version with Integer keys:
        m=22
        m=44
        m=88
        m=176
        m=352
        m=704
        m=1408
        m=2816
        m=5632
        m=11264
        m=22528
      total probes         = 8852
      avg search hit cost  = 1.459244532803181
      avg search miss cost = 2.368741122159091
    
    using default hashing with Integer keys:
        m=22
        m=44
        m=88
        m=176
        m=352
        m=704
        m=1408
        m=2816
        m=5632
        m=11264
        m=22528
      total probes         = 8902
      avg search hit cost  = 1.3081510934393639
      avg search miss cost = 2.6266867897727275

*/  
    
  }

}

