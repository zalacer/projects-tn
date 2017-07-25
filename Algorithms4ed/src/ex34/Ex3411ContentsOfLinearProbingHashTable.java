package ex34;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTX;

/* p481
  3.4.11  Give the contents of a linear-probing hash table that results when 
  you insert the keys E A S Y Q U T I O N in that order into an initially empty 
  table of initial size M = 4 that is expanded with doubling whenever half full. 
  Use the hash function 11 k % M to transform the kth letter of the alphabet 
  into a table index.
  
*/             

public class Ex3411ContentsOfLinearProbingHashTable {
 
  public static void main(String[] args) {
    
    // LinearProbingHashSTX is the same as LinearProbingHashST with modifications
    // including extra constructors for building from arrays and modifying the
    // hash function, and extra functions to modify the hash function, show the
    // keys, values and both and convert to Key and Value arrays without Nulls.
    
    Character[] ca = (Character[])box("E A S Y Q U T I O N".replaceAll("\\s+","")
        .toCharArray());
    Integer[] ia = rangeInteger(0,ca.length);
    
    LinearProbingHashSTX<Character, Integer> ht;
    
    ht = new LinearProbingHashSTX<>(4,11);
    for (int i = 0; i < ca.length; i++) ht.put(ca[i], ia[i]);
    
    ht.printKeyArray();
    // [null,A,null,null,null,E,O,null,null,I,null,null,null,null,null,null,null,
    //  Q,null,S,T,U,null,null,null,Y,N,null,null,null,null,null]
    
    ht.printValArray();
    // [null,1,null,null,null,0,8,null,null,7,null,null,null,null,null,null,null,
    //  4,null,2,6,5,null,null,null,3,9,null,null,null,null,null]

  }
 

}

