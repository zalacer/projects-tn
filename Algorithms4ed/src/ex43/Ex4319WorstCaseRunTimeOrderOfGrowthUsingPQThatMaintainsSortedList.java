package ex43;

/* p632
  4.3.19  Suppose that you use a priority-queue implementation that maintains 
  a sorted list. What would be the order of growth of the worst-case running 
  time for Prim’s algorithm and for Kruskal’s algorithm for graphs with V vertices 
  and E edges? When would this method be appropriate, if ever? Defend your answer.
  
  Assuming Prim's algorithm means the eager one, if the PQ is implemented with quicksort
  it would have amortized (E+V)*V*logV worst case running time since the maximum number
  of elements to sort is V and there are V insert and E change priority operations. The
  cost of the delete the minimum operations is discounted since they don't require resort-
  ing and could be implemented by incrementing the low index of the array. Quicksort could 
  be implemented to work incrementally using quickselect as shown by Navarro and Paredes 
  in "On Sorting, Heaps, and Minimum Spanning Trees" (Algorithmica 57(4),585–620, 2010)
  that's available at  https://www.dcc.uchile.cl/~gnavarro/ps/algor09.pdf. This method 
  extended with Quickheap can be useful for real-time online MST determination from streamed 
  data. In practice it may be more generally useful since Quickheaps use less I/O and are 
  cache-friendlier than other methods.
  
  Kruskal's algorithm could be implemented with E*logE worst case run time with quicksort
  since the edges need to be sorted only once after which an offset into the array can be
  incremented to locate the next edge with the least weight. Since it has the same order of
  growth as the usual minPQ implementation it's appropriate whenever Kruskal's algorithm is,
  namely for sparse graphs.
  
  Notes:
  OOG = order of growth and expressed as (time,space)
  
  text p310: heap based PQ OOG to find largest/smallest M items out of N is (NlogM, M)
  while for sort its (NlogN,N)
  
  text p618: lazy Prim worst case OOG (ElogE,E)
  
  text p623: eager Prim worst case OOG is (ElogV,V)
  
  text p625: Kruskal worst case OOG is (ElogE,E)
 
 */  

public class Ex4319WorstCaseRunTimeOrderOfGrowthUsingPQThatMaintainsSortedList {
 
  public static void main(String[] args) {
     
  }

}


