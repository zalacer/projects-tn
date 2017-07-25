package ex34;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.SeparateChainingHashST;

/* p480
  3.4.7  Consider the idea of implementing modular hashing for integer 
  keys with the code (a * k) %  M , where a is an arbitrary fixed prime. 
  Does this change mix up the bits sufficiently well that you can use 
  nonprime M?
  
  A test below shows this formula works ok and as well as the fancier
  formula from Introduction to Algorithms 3ed, Cormen et al., MIT, 2009, 
  p267 that is of the form (((a*k) % p) % m where p is a prime large 
  enough so that every k is in [0..p-1] and a is in [1..p-1]

*/             

public class Ex3407ImplementationIdeaForModularHashing {
  
  public static int hash1(int a, int k, int m) {
    return (a*k) %  m; 
  }
  
  public static int hash2(int a, int k, int p, int m) {
    return ((a*k) % p) % m;
  }

  public static void main(String[] args) {
    
  /* This test hashes n integers from 0 to n-1 using 5 different hash functions.
     The results of each hash function are put in a separate HashST and counted
     for frequency of occurrence. Each HashST is initialized with capacity m<n.
     The distributions of the frequencies are compared using simple statistal 
     measures (mean, stddev, min, max) that are printed.
     
     The first four hash functions are of the form (a*k) %  m for values of the
     prime p at different orders of magnitude (31, 311, 3109 and 31013
     of the prime a. The fifth hash function is of the form ((a*k) % p) % m
     where a == 1 and p == 15259.
     
     The value of n used is 10000 and m is 300.
     
     The results show that all hash functions distribute the keys evenly. In fact
     inspection of the data shows that the fifth hash function distributes the keys
     slightly less uniformly than the others (the h5a array begins with a sequence
     of keys for which the frequency is 34 after which the frequencies are all 33). 
     The results aren't definitive or a proof, but they are convincing and certainly 
     beyond coincidence.
        
  */  
    
    int n = 10000, m = 300, h1, h2, h3, h4, h5;
    int[] a = range(0,n); 
    
    SeparateChainingHashST<Integer,Integer> ht1 = new SeparateChainingHashST<>(m);
    SeparateChainingHashST<Integer,Integer> ht2 = new SeparateChainingHashST<>(m);
    SeparateChainingHashST<Integer,Integer> ht3 = new SeparateChainingHashST<>(m);
    SeparateChainingHashST<Integer,Integer> ht4 = new SeparateChainingHashST<>(m);
    SeparateChainingHashST<Integer,Integer> ht5 = new SeparateChainingHashST<>(m);

    for (int i : a) {
      h1 = hash1(31,i,m);
      if (ht1.contains(h1)) ht1.put(h1, ht1.get(h1)+1);
      else ht1.put(h1,1);
      
      h2 = hash1(311,i,m);
      if (ht2.contains(h2)) ht2.put(h2, ht2.get(h2)+1);
      else ht2.put(h2,1);
      
      h3 = hash1(3109,i,m);
      if (ht3.contains(h3)) ht3.put(h3, ht3.get(h3)+1);
      else ht3.put(h3,1);
      
      h4 = hash1(31013,i,m);
      if (ht4.contains(h4)) ht4.put(h4, ht4.get(h4)+1);
      else ht4.put(h4,1);
      
      h5 = hash2(1,i,15259,m);
      if (ht5.contains(h5)) ht5.put(h5, ht5.get(h5)+1);
      else ht5.put(h5,1);
    }
    
    System.out.println("ht1.size = "+ht1.size());
    System.out.println("ht2.size = "+ht2.size());
    System.out.println("ht3.size = "+ht3.size());
    System.out.println("ht4.size = "+ht4.size());
    System.out.println("ht5.size = "+ht5.size());

    int[] h1a = new int[m];
    Iterator<Integer> it = ht1.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h1a[x] = ht1.get(x);
    }
    
    int[] h2a = new int[m];
    it = ht2.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h2a[x] = ht2.get(x);
    }
    
    int[] h3a = new int[m];
    it = ht3.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h3a[x] = ht3.get(x);
    }
    
    int[] h4a = new int[m];
    it = ht4.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h4a[x] = ht4.get(x);
    }
    
    int[] h5a = new int[m];
    it = ht5.keys().iterator();
    while (it.hasNext()) {
      int x = it.next();
      h5a[x] = ht5.get(x);
    }
        
    String[] hs = "h1a h2a h3a h4a h5a".split("\\s+");
    int[][] ha = {h1a, h2a, h3a, h4a, h5a};
    assert hs.length == ha.length;
    
    for (int i = 0; i < ha.length; i++) {
      System.out.println("\n"+hs[i]+" stats:");
      System.out.println("mean = "+mean(ha[i]));
      System.out.println("stddev = "+stddev(ha[i]));
      System.out.println("max = "+max(ha[i]));
      System.out.println("min = "+min(ha[i]));
      System.out.print(hs[i]+" = ");par(ha[i]);
    }

/*
  ht1.size = 300
  ht2.size = 300
  ht3.size = 300
  ht4.size = 300
  ht5.size = 300
  
  h1a stats:
  mean = 33.333333333333336
  stddev = 0.4721921646498869
  max = 34
  min = 33
  h1a = [34,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,
  34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,3
  4,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34
  ,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34,34,33,33,33,33,33,33,33,34,34,34,33,33,33,33,33,33,33,34,34,34]
  
  h2a stats:
  mean = 33.333333333333336
  stddev = 0.4721921646498869
  max = 34
  min = 33
  h2a = [34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,
  33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,3
  3,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,34,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34
  ,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33,33,33,34,33,33,34,33,33,34,33,33]
  
  h3a stats:
  mean = 33.333333333333336
  stddev = 0.47219216464988684
  max = 34
  min = 33
  h3a = [34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,
  34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,3
  3,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33
  ,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,34,34,34,34,34,34,34,34,34]
  
  h4a stats:
  mean = 33.333333333333336
  stddev = 0.4721921646498869
  max = 34
  min = 33
  h4a = [34,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,
  33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,34,33,33,3
  4,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33
  ,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,34,34,33,33,34,34,33,33,34,34,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33,33,34,33,33]
  
  h5a stats:
  mean = 33.333333333333336
  stddev = 0.47219216464988706
  max = 34
  min = 33
  h5a = [34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,
  34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,34,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,3
  3,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33
  ,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33,33]

*/
  }

}

