package ex34;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTX;

/* p480
  3.4.10  Insert the keys E A S Y Q U T I O N in that order into an 
  initially empty table of size M=16 using linear probing. Use the 
  hash function 11 k % M to transform the kth letter of the alphabet 
  into a table index. Redo this exercise for M = 10.  
  
*/             

public class Ex3410InsertEASYQUTIONintoALinearProbingHashTableWithVariousHashFuns {
 
  public static void main(String[] args) {
    
    // LinearProbingHashSTX is the same as LinearProbingHashST with modifications
    // including extra constructors for building from arrays and modifying the
    // hash function, and extra functions to modify the hash function, show the
    // keys, values and both and convert to Key and Value arrays without Nulls.
    
    Character[] ca = (Character[])box("E A S Y Q U T I O N".replaceAll("\\s+","")
        .toCharArray());
    Integer[] ia = rangeInteger(0,ca.length);
    
    LinearProbingHashSTX<Character, Integer> ht;
    
    ht = new LinearProbingHashSTX<>(16,11);
    for (int i = 0; i < ca.length; i++) ht.put(ca[i], ia[i]);
    ht.show(); // A:1 E:0 O:8 I:7 Q:4 S:2 T:6 U:5 Y:3 N:9
    System.out.println(ht.getM()+"\n"); // 32
    
    ht = new LinearProbingHashSTX<>(10,11);
    for (int i = 0; i < ca.length; i++) ht.put(ca[i], ia[i]);
    ht.show(); // Q:4 S:2 T:6 A:1 I:7 Y:3 E:0 O:8 U:5 N:9 
    System.out.println(ht.getM()); // 20
 
  }
 

}

