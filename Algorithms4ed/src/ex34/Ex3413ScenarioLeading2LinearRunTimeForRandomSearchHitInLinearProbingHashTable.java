package ex34;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.LinearProbingHashSTX;

/* p481
  3.4.13  Which of the following scenarios leads to expected linear 
  running time for a random search hit in a linear-probing hash table?
  a. All keys hash to the same index.                
  b. All keys hash to different indices.              
  c. All keys hash to an even-numbered index.          
  d. All keys hash to different even-numbered indices.
  
  For search hits, linear run time is expected when all keys hash to the 
  same index whether or not it's even-numbered, such as cases b and c; 
  and constant run time is expected when all keys hash to different indi-
  ces whether or not they are even-numbered,such as cases a and d.
  
  I generally expect average linear run time for search hits when all keys 
  hash to the same index, such as cases a and c. But I would rather say that  
  I expect constant run time when all keys hash to distinct indices. Between
  that and all keys hashing to the same index is a mass of intemediate pos-
  ibilities defined by the degree of hash collisions and configuration of
  clusters in the hash table that depends on N, M and the keys, but not 
  typically in a simple way.
  
  There are some tests below for illustration.
  
*/             

public class Ex3413ScenarioLeading2LinearRunTimeForRandomSearchHitInLinearProbingHashTable {
  
  public static void main(String[] args) {
    
    int N = 100, c; long probes;
    Iterator<Integer> it;
    Integer[] a = rangeInteger(0,N);
    LinearProbingHashSTX<Integer, Integer> ht;

    System.out.println("probes for search hits after hashing keys to distinct indices:"
        + "\n  shows constant search hit runtime");
    // using the default hash function      
    ht = new LinearProbingHashSTX<>(a,a);  
    probes = 0; c = 0;
    it = ht.keys().iterator();
    while (it.hasNext()) {
      Integer x = it.next();
      ht.zeroProbes();  
      if (ht.get(x) != null) {
        probes += ht.probes();
        c++;
      }
    }
    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search hits after hashing all keys to 9:"
        + "\n  shows .46N runtime or (N-9)(N-9+1)/2/(N-9) = 46 probes/hit."
        + "\n  (note that keys [0..8] hash to null because they are not reachable"
        + "\n   since all keys hash to 9, but the cluster runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 9
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"9");  
    probes = 0; c = 0;
    it = ht.keys().iterator();
    while (it.hasNext()) {
      Integer x = it.next();
      ht.zeroProbes();  
      if (ht.get(x) != null) {
        probes += ht.probes();
        c++;
      }
    }
    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search hits after hashing all keys to 10:"
        + "\n  shows .455N runtime or (N-10)(N-10+1)/2/(N-10) = 45.5 probes/hit."
        + "\n  (note that keys [0..9] hash to null because they are not reachable"
        + "\n   since all keys hash to 10, but the cluster runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 10
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"10");  
    probes = 0; c = 0;
    it = ht.keys().iterator();
    while (it.hasNext()) {
      Integer x = it.next();
      ht.zeroProbes();  
      if (ht.get(x) != null) {
        probes += ht.probes();
        c++;
      }
    }
    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
    System.out.println("\nprobes for search hits after hashing all keys to 51:"
        + "\n  shows .255N runtime or (N-50)(N-50+1)/2/(N-50) = 25.5 probes/hit."
        + "\n  (note that keys [0..49] in the first cluster hash to null because they are not reachable "
        + "\n   since all keys hash to 51, that is the location of key=50, while the second cluster of "
        + "\n   [50,99] runs into null after 99.)");
    // this constructor creates a hash function that hashes all keys to 51
    N = 100; a = rangeInteger(0,N);
    ht = new LinearProbingHashSTX<>(a,a,"51");  
    probes = 0; c = 0;
    it = ht.keys().iterator();
    while (it.hasNext()) {
      Integer x = it.next();
      ht.zeroProbes();  
      if (ht.get(x) != null) {
        probes += ht.probes();
        c++;
      }
    }
    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
     
    System.out.println("\nprobes for search hits after hashing keys to distinct even indices:"
        + "\n  shows constant search hit runtime");
    N = 200; a = rangeInteger(0,N,2);
    // using the default hash function on only even keys   
    ht = new LinearProbingHashSTX<>();
    for (int i : a) ht.put(i,1);
    probes = 0; c = 0;
    it = ht.keys().iterator();
    while (it.hasNext()) {
      Integer x = it.next();
      ht.zeroProbes();  
      if (ht.get(x) != null) {
        probes += ht.probes();
        c++;
      }
    }
    System.out.print("keys = "); par(ht.getKeys());
    System.out.println("probes = "+probes);
    System.out.println("averageProbes/key = "+(1.*probes/c));
    
/*
    probes for search hits after hashing keys to distinct indices:
      shows constant search hit runtime
    keys = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,
    28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,
    55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,
    82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null]
    probes = 100
    averageProbes/key = 1.0
    
    probes for search hits after hashing all keys to 9:
      shows .46N runtime or (N-9)(N-9+1)/2/(N-9) = 46 probes/hit.
      (note that keys [0..8] hash to null because they are not reachable
       since all keys hash to 9, but the cluster runs into null after 99.)
    keys = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,
    28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,
    55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,
    82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null]
    probes = 4186
    averageProbes/key = 46.0
    
    probes for search hits after hashing all keys to 10:
      shows .455N runtime or (N-10)(N-10+1)/2/(N-10) = 45.5 probes/hit.
      (note that keys [0..9] hash to null because they are not reachable
       since all keys hash to 10, but the cluster runs into null after 99.)
    keys = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,
    28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,
    55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,
    82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null]
    probes = 4095
    averageProbes/key = 45.5
    
    probes for search hits after hashing all keys to 51:
      shows .255N runtime or (N-50)(N-50+1)/2/(N-50) = 25.5 probes/hit.
      (note that keys [0..49] in the first cluster hash to null because they are not reachable 
       since all keys hash to 51, that is the location of key=50, while the second cluster of 
       [50,99] runs into null after 99.)
    keys = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,
    28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,null,50,51,52,53,
    54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,
    82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null]
    probes = 1275
    averageProbes/key = 25.5
    
    probes for search hits after hashing keys to distinct even indices:
      shows constant search hit runtime
    keys = [0,null,2,null,4,null,6,null,8,null,10,null,12,null,14,null,16,null,18,null,20,
    null,22,null,24,null,26,null,28,null,30,null,32,null,34,null,36,null,38,null,40,null,42,
    null,44,null,46,null,48,null,50,null,52,null,54,null,56,null,58,null,60,null,62,null,64,
    null,66,null,68,null,70,null,72,null,74,null,76,null,78,null,80,null,82,null,84,null,86,
    null,88,null,90,null,92,null,94,null,96,null,98,null,100,null,102,null,104,null,106,null,
    108,null,110,null,112,null,114,null,116,null,118,null,120,null,122,null,124,null,126,null,
    128,null,130,null,132,null,134,null,136,null,138,null,140,null,142,null,144,null,146,null,
    148,null,150,null,152,null,154,null,156,null,158,null,160,null,162,null,164,null,166,null,
    168,null,170,null,172,null,174,null,176,null,178,null,180,null,182,null,184,null,186,null,
    188,null,190,null,192,null,194,null,196,null,198,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
    null,null,null,null,null,null,null,null,null,null,null,null,null]
    probes = 100
    averageProbes/key = 1.0

*/    
  }
 

}

