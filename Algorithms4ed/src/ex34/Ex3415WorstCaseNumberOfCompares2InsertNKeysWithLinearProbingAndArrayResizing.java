package ex34;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTX;

/* p481
  3.4.15  How many compares could it take, in the worst case, to insert N keys
  into an initially empty table, using linear probing with array resizing?
  
  According to p471 in reference to probe, "We use the term interchangeably with 
  the term compare". The maximum number of "compares" is (1.*n*(n+1)/2) - n that
  is seen by hashing all keys to the same number. This is the same as the number
  of times an explicit equals() method is executed, as demonstrated below.
  
*/             

public class Ex3415WorstCaseNumberOfCompares2InsertNKeysWithLinearProbingAndArrayResizing {
  
  public static int predict(int n) { return (int)1.*n*(n+1)/2 - n; }
  
  public static void main(String[] args) {
    /*  
    LinearProbingHashSTX is the same as LinearProbingHashST with modifications
    including extra constructors for building from arrays and defining the
    hash function, and extra functions to modify the hash function, show the
    keys, values and both and convert to Key and Value arrays.
  */
     
     int N = 12345;
     Integer[] a = rangeInteger(0,N);
     // hash everything to 0
     LinearProbingHashSTX<Integer, Integer> h = new LinearProbingHashSTX<>("0");
     for (int i : a) h.put(i, i);
     assert h.probes() == predict(N);
     assert h.getEquals() == predict(N);
     
  }
 

}

