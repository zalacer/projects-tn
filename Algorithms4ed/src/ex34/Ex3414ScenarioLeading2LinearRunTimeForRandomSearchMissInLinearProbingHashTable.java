package ex34;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTX;

/* p481
  3.4.14  Answer the previous question for search miss, assuming the 
  search key is equally likely to hash to each table position, i.e.
  
  Which of the following scenarios leads to expected linear running 
  time for a random search miss in a linear-probing hash table?
  a. All keys hash to the same index.                
  b. All keys hash to different indices.              
  c. All keys hash to an even-numbered index.          
  d. All keys hash to different even-numbered indices.
  
  Same answer as for exercise 3.4.13: For search misses, linear run 
  time is expected when all keys hash to the same index whether or 
  not it's even-numbered, such as cases b and c; and constant run 
  time is expected when all keys hash to different indices whether
  or not they are even-numbered,such as cases a and d.
 
  There are some illustrative tests below.
  
*/             

public class Ex3414ScenarioLeading2LinearRunTimeForRandomSearchMissInLinearProbingHashTable {
  
  public static void main(String[] args) {
    
    int N = 100, M, c; long probes;
    Integer[] a = rangeInteger(0,N);
    LinearProbingHashSTX<Integer, Integer> ht;

    System.out.println("probes for search misses after hashing keys to distinct indices:"
        + "\n  shows constant search hit runtime");
    // using the default hash function      
    ht = new LinearProbingHashSTX<>(a,a);  
    M = ht.getM(); probes = 0; c = 0;
    for (int i = 0; i < M; i++) {
      ht.zeroProbes();  
      if (ht.get(i) == null) {
        probes += ht.probes();
        c++;
      }
    }
//    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search misses after hashing all keys to 9:"
        + "\n  shows N - 9 + 1 runtime = 92 probes/hit."
        + "\n  (note that keys [0..8] hash to null because they are not reachable"
        + "\n   since all keys hash to 9, but the cluster runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 9
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"9");  
    M = ht.getM(); probes = 0; c = 0;
    for (int i = 0; i < M; i++) {
      ht.zeroProbes();  
      if (ht.get(i) == null) {
        probes += ht.probes();
        c++;
      }
    }
//    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search misses after hashing all keys to 10:"
        + "\n  shows N - 9 + 1 runtime = 91 probes/hit."
        + "\n  (note that keys [0..9] hash to null because they are not reachable"
        + "\n   since all keys hash to 10, but the cluster runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 10
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"10"); 
    M = ht.getM(); probes = 0; c = 0;
    for (int i = 0; i < M; i++) {
      ht.zeroProbes();  
      if (ht.get(i) == null) {
        probes += ht.probes();
        c++;
      }
    }
//    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search misses after hashing all keys to 51:"
        + "\n  shows N - 51 + 2 runtime = 51 probes/hit."
        + "\n  (note that keys [0..49] in the first cluster hash to null because they are not reachable "
        + "\n   since all keys hash to 51, that is the location of key=50, while the second cluster of "
        + "\n   [50,99] runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 51
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"51");  
    M = ht.getM(); probes = 0; c = 0;
    for (int i = 0; i < M; i++) {
      ht.zeroProbes();  
      if (ht.get(i) == null) {
        probes += ht.probes();
        c++;
      }
    }
//    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
     
    System.out.println("\nprobes for search misses after hashing keys to distinct even indices:"
        + "\n  shows constant search hit runtime");
    N = 200; a = rangeInteger(0,N,2);
    // using the default hash function on only even keys   
    ht = new LinearProbingHashSTX<>();
    for (int i : a) ht.put(i,1);
    M = ht.getM(); probes = 0; c = 0;
    for (int i = 0; i < M; i++) {
      ht.zeroProbes();  
      if (ht.get(i) == null) {
        probes += ht.probes();
        c++;
      }
    }
//    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
     

/*
    probes for search misses after hashing keys to distinct indices:
      shows constant search hit runtime
    probes = 100
    averageProbes/key = 1.0
    
    probes for search misses after hashing all keys to 9:
      shows N - 9 + 1 runtime = 92 probes/hit.
      (note that keys [0..8] hash to null because they are not reachable
       since all keys hash to 9, but the cluster runs into null after 99.)
    probes = 10028
    averageProbes/key = 92.0
    
    probes for search misses after hashing all keys to 10:
      shows N - 9 + 1 runtime = 91 probes/hit.
      (note that keys [0..9] hash to null because they are not reachable
       since all keys hash to 10, but the cluster runs into null after 99.)
    probes = 10010
    averageProbes/key = 91.0
    
    probes for search misses after hashing all keys to 51:
      shows N - 51 + 2 runtime = 51 probes/hit.
      (note that keys [0..49] in the first cluster hash to null because they are not reachable 
       since all keys hash to 51, that is the location of key=50, while the second cluster of 
       [50,99] runs into null after 99.)
    probes = 7650
    averageProbes/key = 51.0
    
    probes for search misses after hashing keys to distinct even indices:
      shows constant search hit runtime
    probes = 156
    averageProbes/key = 1.0

*/    
     
  }
 

}

