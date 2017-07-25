package ex22;

public class Ex2205MergeSortSequenceOfSubarraySizes {
  
/* p284
  2.2.5  Give the sequence of subarray sizes in the merges performed by both the top-
  down and the bottom-up mergesort algorithms, for N = 39.
  
  Top-down: 2, 3, 2, 5, 2, 3, 2, 5, 10, 2, 3, 2, 5, 2, 3, 2, 5, 10, 20, 
            2, 3, 2, 5, 2, 3, 2, 5, 10, 2, 3, 2, 5, 2, 2, 4, 9, 19, 39. 
            
  Top-down in left-right pairs: 
  (1,1), (2,1), (1,1), (3,2), (1,1), (2,1), (1,1), (3,2), 
  (5,5), 
  (1,1), (2,1), (1,1), (3,2), (1,1), (2,1), (1,1), (3,2), 
  (5,5), 
  (10,10), 
  (1,1), (2,1), (1,1), (3,2), (1,1), (2,1), (1,1), (3,2), 
  (5,5), 
  (1,1), (2,1), (1,1), (3,2), (1,1), (1,1), (2,2), 
  (5,4),
  (10,9), 
  (20,19)
  
  
  Bottom up: 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 
             4, 4, 4, 4, 4, 4, 4, 3, 8, 8, 8, 8, 7, 16, 16, 32, 39. 
  
  Bottom-up in left-right pairs:
  (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), 
  (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), (1,1), 
  (2,2), (2,2), (2,2), (2,2), (2,2), (2,2), (2,2), (2,2), (2,2), (2,1), 
  (4,4), (4,4), (4,4), (4,4), (4,3), 
  (8,8), (8,8),
  (16,16), 
  (32,7)
  
  The top-down data was gathered by running sort.MergeTopDown.sort3(a);
  The bottom-up data was gathered by running sort.MergeBottomUp.sort3(a).
  
*/
  
  public static void main(String[] args) {

  }

}
