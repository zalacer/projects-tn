package ex43;

/* p631
  4.3.3  Show that if a graph’s edges all have distinct weights, the MST is unique.
  
  Solution from http://algs4.cs.princeton.edu/43mst/:
  
  "For the sake of contradiction, suppose there are two different MSTs of G, say T1 
  and T2. Let e = v-w be the min weight edge of G that is in one of T1 or T2, but not 
  both. Let's suppose e is in T1. Adding e to T2 creates a cycle C. There is at least
  one edge, say f, in C that is not in T1 (otherwise T1 would be cyclic). By our choice 
  of e, w(e) ≤ w(f). Since all of the edge weights are distinct, w(e) < w(f). Now, re-
  placing f with e in T2 yields a new spanning tree with weight less than that of T2 
  (contradicting the minimality of T2)."
   
 */  

public class Ex4303ShowThatIfEdgesHaveDistinctWeightsTheMSTIsUnique {
  
  public static void main(String[] args) {
 
  }

}
