package ex24;

import static pq.SumCubes.printPairsWithSameCubeSum;

/* p331
  2.4.25 Computational number theory. Write a program CubeSum.java that prints
  out all integers of the form a^3 + b^3 where a and b are integers between 0 
  and N in sorted order, without using excessive space. That is, instead of 
  computing an array of the N^2 sums and sorting them, build a minimum-oriented 
  priority queue, initially containing (0^3, 0, 0), (1^3, 1, 0), (2^3, 2, 0), 
  . . . , (N^3, N, 0). Then, while the priority queue is nonempty, remove the 
  smallest item(i^3 + j^3 , i, j), print it, and then, if j < N, insert the item 
  (i 3 + (j+1) 3 , i, j+1). Use this program to find all distinct mintegers a, b, 
  c, and d between 0 and 10^6  such that a^3 + b^3 = c^3 + d^3 .
  
  To get started see http://algs4.cs.princeton.edu/24pq/CubeSum.java which is also
  available locally at pq.CubeSum.

*/

public class Ex2425MinPQtoImplementCubeSum {
 
  public static void main(String[] args) {
    
/*
   printPairsWithSameCubeSum(int n) prints all pairs (a,b) and (c,d) such that
   a^3 + b^3 = c^3 + d^3 for a, b, c, d between 0 and n.
*/    
    printPairsWithSameCubeSum(25); 
/*
    (1,12), (9,10)
    (2,16), (9,15)
    (2,24), (18,20)
*/
    }

}
