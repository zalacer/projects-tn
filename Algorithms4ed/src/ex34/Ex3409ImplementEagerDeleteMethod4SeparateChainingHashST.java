package ex34;

import static v.ArrayUtils.*;
import static java.lang.Math.*;

import java.security.SecureRandom;

import st.SeparateChainingHashSTX;

/* p480
  3.4.9  Implement an eager delete() method for  SeparateChainingHashST.
  
  This is already implemented in st.SeparateChainingHashST.
  
  
*/             

@SuppressWarnings("unused")
public class Ex3409ImplementEagerDeleteMethod4SeparateChainingHashST {
  
  public static int predict(int m, int n) {
    // return the predicted number of duplicates in a sequence of n
    // integers ranging from 0 to m-1 generated with random uniformity.
    if (m < 1 || n < 1) throw new IllegalArgumentException(
        "predict: m and n must both be > 0");
    int unique = (int)floor(1.*m*(1.-pow(1.-(1./m),n)));
    return n - unique ;
  }
  
  public static void main(String[] args) {
    
    // SeparateChainingHashSTX is the same as SeparateChainingHashST with the
    // addition of emptyLists().
    
    SecureRandom r = new SecureRandom();
    int bound = 100, n = 1000, c = 0, dups = 0, k;
    SeparateChainingHashSTX<Integer,Integer> ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt(bound);
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("for bound="+bound+" and N="+n+":");
    System.out.println("numberOfEmptyLists = "+ht.emptyLists()); // 0
    System.out.println("actual number of duplicates = "+dups); // 900
    System.out.println("predicted number of duplicates = "+predict(bound,n)); // 901
 
    bound = 1000; n = 100; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt(bound);
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nfor bound="+bound+" and N="+n+":");
    System.out.println("numberOfEmptyLists = "+ht.emptyLists()); // 0
    System.out.println("actual number of duplicates = "+dups); // 6
    System.out.println("predicted number of duplicates = "+predict(bound,n)); // 5
    
    
    System.out.println("\nfor random ints not specifically bounded:");
    
    // testing with boundless random int generation
    n = 10; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
    
    // testing with boundless random int generation
    n = 100; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
    
    // testing with boundless random int generation
    n = 1000; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
    
    // testing with boundless random int generation
    n = 10000; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
    
    // testing with boundless random int generation
    n = 100000; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
    
    // testing with boundless random int generation
    n = 1000000; c = 0; dups = 0;
    ht = new SeparateChainingHashSTX<>();
    while (c < n) {
      k = r.nextInt();
      if (ht.contains(k)) dups++;
      ht.put(k,1);
      c++;
    }
    System.out.println("\nnumberOfEmptyLists with N="+n+": "+ht.emptyLists()); // 0
/*
    for bound=100 and N=1000:
    numberOfEmptyLists = 0
    actual number of duplicates = 900
    predicted number of duplicates = 901
    
    for bound=1000 and N=100:
    numberOfEmptyLists = 0
    actual number of duplicates = 7
    predicted number of duplicates = 5
    
    for random ints not specifically bounded:
    
    numberOfEmptyLists with N=10: 0
    
    numberOfEmptyLists with N=100: 0
    
    numberOfEmptyLists with N=1000: 0
    
    numberOfEmptyLists with N=10000: 0
    
    numberOfEmptyLists with N=100000: 33
    
    numberOfEmptyLists with N=1000000: 73
    
*/    
  }
 

}

