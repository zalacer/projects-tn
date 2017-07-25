package ex32;

/* p419
  3.2.27 Memory usage. Compare the memory usage of BST with the memory usage of
  BinarySearchST and SequentialSearchST for N key-value pairs, under the assump-
  tions described in Section 1.4 (see Exercise 3.1.21). Do not count the memory 
  for the keys and values themselves, but do count references to them. Then draw 
  a diagram that depicts the precise memory usage of a BST with String keys and  
  Integer values (such as the ones built by  FrequencyCounter), and then estimate 
  the memory usage (in bytes) for the BST built when FrequencyCounter uses BST for 
  Tale of Two Cities.
  
  I'm doing this for base memory usage on a 64 bit system not including additional 
  memory transiently used by certain methods, however I'm counting the Node objects 
  with field references for SequentialSearchST and BST since they make an assymetric 
  difference vs. the array used in BinarySearchST. 
  
  BST
  ---
  Node * N        N*60 including Node object, references for Key, Value, left and 
                  right and int (size).
  Node root ref   8
  --------------  --------
          total   N*60 + 4
  
  BinarySearchST
  --------------
  item            bytes
  -------------   -----
  array * 2       2*24
  Key ref * N     8*N
  Value ref * N   8*N
  int * 2         2*4
  --------------  ---------
          total   16*N + 56
                  
  SequentialSearchST
  ------------------
  Node * N        N*48 including Node object and Key, Value and Node next refs
  int * 1         4
  -------------   ------
          total   48*N + 4 
          
  From exercise 3.1.6 tale.txt has 135643 words and 124969 of them are distinct.
  Therefore a BST built for a FrequencyCounter for it would use about 
  7,498,144 bytes. If the memory requirements of the Strings and Integer objects
  are included that comes to 487,379,104 bytes or 476 KB.
  
*/             

public class Ex3227CompareBSTmemoryUsageVsBinarySearchSTandSequentialSearchST {
  
  public static void main(String[] args) {

  }

}

