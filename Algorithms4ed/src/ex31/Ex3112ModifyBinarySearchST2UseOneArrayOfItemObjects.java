package ex31;

/* p389
  3.1.12  Modify  BinarySearchST to maintain one array of Item 
  objects that contain keys and values, rather than two parallel 
  arrays. Add a constructor that takes an array of Item values 
  as argument and uses mergesort to sort the array.
  
  I interpret the last sentence of this exercise's statement as
  "Add a constructor that takes an array of Items as argument and 
  uses mergesort to sort the array".
  
  A solution is st.BinarySearchSTex3112. It has two array constructors,
  one taking a Tuple2<K,V> array and the other taking K[] and V[] arrays
  that builds a Tuple2<K,V>[] from them. Only the constructors of 
  st.BinarySearchSTex3112 have been debugged.
  
 */

public class Ex3112ModifyBinarySearchST2UseOneArrayOfItemObjects {
   
  public static void main(String[] args) {   
    
  }

}
