package ex31;

/* p391
  3.1.21 Memory usage. Compare the memory usage of BinarySearchST with 
  that of SequentialSearchST for N key-value pairs, under the 
  assumptions described in Section 1.4. Do not count the memory for the 
  keys and values themselves, but do count references to them. For  
  BinarySearchST , assume that array resizing is used, so that the array 
  is between 25 percent and 100 percent full.
  
  I'm doing this for base memory usage on a 64 bit system not including 
  additional memory transiently used by certain methods, however I'm 
  counting the Node objects with field references for SequentialSearchST 
  since they make an assymetric difference vs. the array used in 
  BinarySearchST.
  
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
                  16 * 3N + 56 = 48*N + 56
                  
  SequentialSearchST
  ------------------
  Node * N        N*48 including Node object Key, Value and Node next refs
  int * 1         4
  -------------   ------
          total   48*N + 4 
                  48*(N+1) + 4 = 48*N + 52
          
  In comparison a BinarySearchST of size 3N uses 4 bytes more 
  memory than a SequentialSearchST of size N+1.
            
*/

public class Ex3121CompareMemoryUsageBinarySearchSTvsSequentialSearchST {
  

  public static void main(String[] args) {
  
  }
}
