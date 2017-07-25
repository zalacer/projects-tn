package ex34;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import st.CuckooHashST;

/* p484
  3.4.32 Hash attack. Find 2 N strings, each of length 2 N , that have the 
  same hashCode() value, supposing that the  hashCode() implementation for  
  String is the following:
  
    public int hashCode() {
      int hash = 0;
      for (int i = 0; i < length(); i ++)
      hash = (hash * 31) + charAt(i);
      return hash;
    }
    
  Strong hint :  Aa and  BB have the same value.
  
  This is solved on http://algs4.cs.princeton.edu/34hash/ as follows:
  
   "Solution. It is easy to verify that "Aa" and "BB" hash to the same hashCode() 
   value (2112). Now, any string of length 2N that is formed by concatenating these 
   two strings together in any order (e.g., BBBBBB, AaAaAa, BBAaBB, AaBBBB) will 
   hash to the same value. At http://algs4.cs.princeton.edu/34hash/hash-attack.txt
   is a list of 10000 strings with the same hash value."
   
*/

public class Ex3432HashAttack {
  
  public static void cuckooTest(boolean noresize, int tables) {
    System.out.println("cuckooTest("+noresize+","+tables+"):");
    SecureRandom r = new SecureRandom(); int c = 0, n = 1000000;
    CuckooHashST<Integer, Integer> h;
    if (noresize && tables < 2) h = new CuckooHashST<Integer, Integer>(83401,true);
    if (!noresize && tables == 2) h = new CuckooHashST<Integer, Integer>(100000,2);
    else if (!noresize && tables > 1) h = new CuckooHashST<Integer, Integer>(tables);
    else h = new CuckooHashST<Integer, Integer>();
    while (c < n) {
      int M = (int)h.M();
      int x = r.nextInt(5000);
      for (int i = 0; i < 15; i++)  h.put((int)(x + M*i), 1);
      c++;
    }
    System.out.println("overall alpha = "+h.alpha());
    System.out.print("per table alphas = "); par(h.alphas());
    System.out.println("size = "+h.size());
    System.out.println("per table size = "+h.m());
    System.out.println("total table size = "+h.M());
    System.out.println("number of tables = "+h.NT());
    Integer[] keys = h.toKeyArray();
    assert keys.length == h.size();
    h.zeroProbes();
    for (Integer k : keys) {
      Integer g = h.get(k);
      if (g == null) System.out.println("get returned null");
    }
    System.out.println("avgProbes/get = "+(1.*h.probes()/keys.length));
    System.out.println("check = "+h.check());
    System.out.println();
  }
   
  public static void main(String[] args) {
    
    cuckooTest(false,0); // 3 tables with resizing
    cuckooTest(true,0);  // 3 tables with no resizing
    cuckooTest(false,4); // 4 tables with resizing
    cuckooTest(false,2); // 2 tables with resizing
/*
    cuckooTest(false):
    overall alpha = 0.6263353722766996
    per table alphas = [0.8726587188563834,0.6478007893559019,0.3585466086178134]
    size = 138541
    per table size = 73731
    total table size = 221193
    avgProbes/get = 1.7263914653423895
    check = true
    
    cuckooTest(true):
    overall alpha = 0.8992482284809898
    per table alphas = [1.0,0.9260098557605841,0.7717348296823855]
    size = 75000
    per table size = 27801
    total table size = 83403
    avgProbes/get = 1.9153866666666666
    check = true
    
    cuckooTest(false):
    overall alpha = 0.6715313126342392
    per table alphas = [0.9324126901133375,0.6983714457071678,0.383809802082212]
    size = 131003
    per table size = 65027
    total table size = 195081
    avgProbes/get = 1.7276856255200264
    check = true
    
    cuckooTest(true):
    overall alpha = 0.9027048413783181
    per table alphas = [0.8864829868354794,0.9259046111790519,0.895726926120423]
    size = 75291
    per table size = 27802
    total table size = 83406
    avgProbes/get = 2.003413422587029
    alpha = 0.9027048413783181 is greater than LOADFACTOR = 0.9
    check = false
    
    cuckooTest(false,4):
    overall alpha = 0.8503110069413143
    per table alphas = [0.9855163917185011,0.9657141140058295,0.8624958682652724,0.5875176537756543]
    size = 113190
    per table size = 33279
    total table size = 133116
    number of tables = 4
    avgProbes/get = 2.309302941956003
    check = true
    
    cuckooTest(false,2):
    overall alpha = 0.749985000299994
    per table alphas = [0.9166016679666407,0.5833683326333473]
    size = 75000
    per table size = 50001
    total table size = 100002
    number of tables = 2
    avgProbes/get = 1.38892
    check = true

*/    
  }

}

