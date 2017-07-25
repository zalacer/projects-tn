package ex34;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.function.Function;

import ds.Queue;
import ds.SET;
import st.BSTX;
import st.LinearProbingHashSTX;
import st.SequentialSearchST;
import v.Tuple2;

/* p481
  3.4.12  Suppose that the keys A through G, with the hash values 
  given below, are inserted in some order into an initially empty 
  table of size 7 using a linear-probing table (with no resizing 
  for this problem). Which of the following could not possibly 
  result from inserting these keys? 
      a. E F G A C B D
      b. C E B G F D A
      c. B D F A C E G
      d. C G B A D E F
      e. F G B D A C E
      f. G E C A D B F
  Give the minimum and the maximum number of probes that could be 
  required to build a table of size 7 with these keys, and an insertion
  order that justifies your answer.    
      
  The exercise statement says the hash values are given but they are not.
  Neither is the hash function to use provided. Therefore I equipped 
  LinearProbingHashSTX with hash functions that generate each of the 
  results as shown below.
  
  The minimum number of probes required to build a table of size 7 is zero
  to get any of the key orders given above for all input permutations using 
  the specialized hash functions optimized for them mentioned above.
  
  The maximum number of probes is 21 when adhering to a fixed table size of
  7 and that results from mapping all keys in any order to any possible  
  fixed key table index. This happens because the ith key requires i probes 
  from i = 0 to 6 that totals to 21 and is demonstrated below.
  
  On the other hand, with LinearProbingHashSTX using the default hash function 
    k.hashCode() & 0x7fffffff) % m
  the only possible resulting sequence of keys is F,G,A,B,C,D,E and the number 
  of probes is 0 for all input permutations.
  
*/             

public class Ex3412BuildingLinearProbingHashTableWith7Keys {
  
  // this has been included as a private non-static field in LinearProbingHashSTX
  private static char[][] ca = { 
      ("E F G A C B D".replaceAll("\\s+","").toCharArray()),
      ("C E B G F D A".replaceAll("\\s+","").toCharArray()),
      ("B D F A C E G".replaceAll("\\s+","").toCharArray()),
      ("C G B A D E F".replaceAll("\\s+","").toCharArray()),
      ("F G B D A C E".replaceAll("\\s+","").toCharArray()),
      ("G E C A D B F".replaceAll("\\s+","").toCharArray())
  };  
  
  public static void doExerciseWithDefaultLinearProbingHashSTXhashFunction() {
    char[] c = ("ABCDEFG").toCharArray(); int len = c.length; int[] z; 
    Character[] x = new Character[len]; Integer[] a = rangeInteger(0,len);
    Iterator<String> is; Iterator<int[]> it = permutations(range(0,len));
    Iterator<Tuple2<String,Integer>> it2;
    String s = null; Queue<Tuple2<String,Integer>> q;
    BSTX<String,Queue<Tuple2<String,Integer>>> map = new BSTX<>();
    LinearProbingHashSTX<Character,Integer> h;
    while (it.hasNext()) {
      z = it.next();
      for (int i = 0; i < len; i++) x[i] = c[z[i]];
      h = new LinearProbingHashSTX<>(len,true);
      for (int i = 0; i < len; i++) h.put(x[i], a[i]);
      s = h.keysToString();
      if (map.contains(s)) {
        map.get(s).enqueue(new Tuple2<String,Integer>(arrayToString(x,900,1,1),h.probes()));
      } else {
        q = new Queue<>();
        q.enqueue(new Tuple2<String,Integer>(arrayToString(x,900,1,1),h.probes()));
        map.put(s, q);
      }   
    }
    SET<String> impossible = new SET<>();
    for (char[] cr : ca) {
      s = arrayToString(cr,100,1,1);
      if (!map.contains(s)) impossible.add(s);
    }
    
    System.out.println("using the default hash function k.hashCode() & 0x7fffffff) % m:");
    
    if (impossible.isEmpty()) System.out.println("none of the given sequences are impossible");
    else if (impossible.size() == ca.length) 
      System.out.println("all of the given sequences are impossible");
    else {
      is = impossible.iterator();
      if (impossible.size() == 1) 
        System.out.println("the sequence "+is.next()+" is impossible in the keys array "
            + "\nusing the default hash function k.hashCode() & 0x7fffffff) % m:");
      else {
        System.out.println("the following sequences are impossible in the keys array "
            + "\n  using the default hash function k.hashCode() & 0x7fffffff) % m:");
        while (is.hasNext()) System.out.println("    "+is.next());
      }
    }
    int minp = Integer.MAX_VALUE, maxp = Integer.MIN_VALUE;
    is = map.keys().iterator();
    while (is.hasNext()) {
      q = map.get(is.next());
      it2 = q.iterator();
      while (it2.hasNext()) {
        Tuple2<String,Integer> t2 = it2.next();
        if (t2._2 < minp) minp = t2._2;
        if (t2._2 > maxp) maxp = t2._2;
      }
    }
    System.out.println("the minimum number of probes is "+minp);
    System.out.println("the maximum number of probes is "+maxp);
    Queue<String> min = new Queue<>(); Queue<String> max = new Queue<>();
    is = map.keys().iterator();
    while (is.hasNext()) {
      s = is.next();
      q = map.get(s);
      it2 = q.iterator();
      while (it2.hasNext()) {
        Tuple2<String,Integer> t2 = it2.next();
        if (t2._2 == maxp) max.enqueue(t2._1);
        if (t2._2 == minp) min.enqueue(t2._1);
      }
    }
    if (map.size() == 1 && minp == maxp) {
      System.out.println("all insertion orders result in the same order of keys "
          + "\n  the number of probes for each is "+minp
          + "\n  and the only posible resulting sequence of keys is:"
          + "\n    "+s+"\n");
      return;
    }   
    if (minp == maxp) {
      is = min.iterator();
      if (min.size() == 1) {
        System.out.println("the following insertion order has the min and max "
            + "probes both equal to "+minp+":");
        System.out.println("  "+is.next());
      } else {
        System.out.println("the following insertion orders have the min and max "
            + "probes both equal to "+minp+":");
        while (is.hasNext()) System.out.println("  "+is.next());
      }        
    } else {
      is = min.iterator();
      if (min.size() == 1) {
        System.out.println("the following insertion order has the min probes of "+minp+":");
        System.out.println("  "+is.next());
      } else {
        System.out.println("the following insertion orders have the min probes of "+minp+":");
        while (is.hasNext()) System.out.println("  "+is.next());
      }
      is = max.iterator();
      if (max.size() == 1) {
        System.out.println("the following insertion order has the max probes of "+maxp+":");
        System.out.println("  "+is.next());
      } else {
        System.out.println("the following insertion orders have the max probes of "+maxp+":");
        while (is.hasNext()) System.out.println("  "+is.next());
      }     
    }
    System.out.println();
  }
  
  public static <Key> Function<Key,Integer> generateFunction(Key[] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    // and maps each key to a unique index in the keys array identical
    // to its index in ca.
    Function<Key,Integer> h = (c) -> {
      SequentialSearchST<Key,Integer> st = new SequentialSearchST<>();
      for (int i = 0; i < ca.length; i++) st.put(ca[i], i);
      if (st.contains(c)) return st.get(c);
      else return -1;          
    };
    return h;
  }
  
  public static <Key> Function<Key,Integer> generateFunction2(Key[] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    // and maps each key to the same index in the keys array, namely 0.
    Function<Key,Integer> h = (c) -> {
      SequentialSearchST<Key,Integer> st = new SequentialSearchST<>();
      for (int i = 0; i < ca.length; i++) st.put(ca[i], 0);
      if (st.contains(c)) return st.get(c);
      else return -1;          
    };
    return h;
  }
  
  public static <Key> Function<Key,Integer>[] generateFunctions(Key[][] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    // and uses generateFunction to create an array of hash functions.
    Function<Key,Integer>[] ha = ofDim(Function.class, ca.length);
    for (int i = 0; i < ca.length; i++) ha[i] = generateFunction(ca[i]);
    return ha;
  }
  
  public static <Key> Function<Key,Integer>[] generateFunctions2(Key[][] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    // and uses generateFunction2 to create an array of hash functions.
    Function<Key,Integer>[] ha = ofDim(Function.class, ca.length);
    for (int i = 0; i < ca.length; i++) ha[i] = generateFunction2(ca[i]);
    return ha;
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
  /*  
   LinearProbingHashSTX is the same as LinearProbingHashST with modifications
   including extra constructors for building from arrays and modifying the
   hash function, and extra functions to modify the hash function, show the
   keys, values and both and convert to Key and Value arrays without Nulls.
  
   ca, generateFunction, generateFunctions, generateFunction2, generateFunctions2 
   are included in LinearProbingHashSTX and used to create specific hash functions 
   iff Key.getClass() == Character.class to reproduce the character orders in the 
   subarrays in ca as demonstrated below using generateFunction and generateFunctions
   and to maximize the number of probes using generateFunction2 and generateFunctions2
   as demonstrated below.
 */
    
    doExerciseWithDefaultLinearProbingHashSTXhashFunction();
/*
    using the default hash function k.hashCode() & 0x7fffffff) % m:
    all of the given sequences are impossible
    the minimum number of probes is 0
    the maximum number of probes is 0
    all insertion orders result in the same order of keys 
      the number of probes for each is 0
      and the only posible resulting sequence of keys is:
        [F,G,A,B,C,D,E]
*/
    SecureRandom r = SecureRandom.getInstanceStrong();
    
    Character[] a = (Character[])box("ABCDEFG".toCharArray());

    Integer[] ia = rangeInteger(0,a.length);
    
    LinearProbingHashSTX<Character, Integer> ht;
    
    System.out.println("demonstrating key table sequences identical to those in ca:\n");
    
    for (int i = 0; i < 6; i++) {
      String s = "hash"+i; // hash prefix creates hash functions with generateFunctions
      ht = new LinearProbingHashSTX<>(7,s,true);
      for (int j = 0; j < a.length; j++) ht.put(a[j], ia[j]);    
      System.out.print("keys = "); ht.printKeyArray();
      System.out.println("number of probes = "+ht.probes());
      System.out.println();
    }

    System.out.println("demonstrating max number of probes for fixed table size of 7:");
    System.out.println("  (input key sequences are randomly shuffled "
        + "\n   so output key sequences vary from run to run)\n");
        
    for (int i = 0; i < 6; i++) {
      String s = "bash"+i; // bash prefix creates hash functions with generateFunctions2
      ht = new LinearProbingHashSTX<>(7,s,true);
      if (i > 0) shuffle(a,r);
      for (int j = 0; j < a.length; j++) ht.put(a[j], ia[j]);    
      System.out.print("keys = "); ht.printKeyArray();
      System.out.println("number of probes = "+ht.probes());
      System.out.println();
    }

/*    
    demonstrating key table sequences identical to those in ca:
    
    keys = [E,F,G,A,C,B,D]
    number of probes = 0
    
    keys = [C,E,B,G,F,D,A]
    number of probes = 0
    
    keys = [B,D,F,A,C,E,G]
    number of probes = 0
    
    keys = [C,G,B,A,D,E,F]
    number of probes = 0
    
    keys = [F,G,B,D,A,C,E]
    number of probes = 0
    
    keys = [G,E,C,A,D,B,F]
    number of probes = 0
    
    demonstrating max number of probes for fixed table size of 7:
      (input key sequences are randomly shuffled 
       so output key sequences vary from run to run)
    
    keys = [A,B,C,D,E,F,G]
    number of probes = 21
    
    keys = [F,C,D,G,A,E,B]
    number of probes = 21
    
    keys = [F,G,B,A,C,E,D]
    number of probes = 21
    
    keys = [E,D,C,B,G,F,A]
    number of probes = 21
    
    keys = [B,C,G,D,A,E,F]
    number of probes = 21
    
    keys = [A,F,G,E,B,C,D]
    number of probes = 21

*/
    
  }
 

}

