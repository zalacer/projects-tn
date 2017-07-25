package ex43;

/* p631
  4.3.1  Prove that you can rescale the weights by adding a positive 
  constant to all of them or by multiplying them all by a positive 
  constant without affecting the MST.
  
  That this is true seems obvious to me because the MST algo's presented
  select edges for the MST using MinPQ.delMin() that's based on the
  comparative values of Edge.w assesed using Double.compare() comparisons.
  Arithmetically, the outcomes of these comparisons are unaffected by
  constant scaling of the w values of all edges by a positive additive or
  multiplicative factor. In other words, if the edges were put into an 
  array and sorted with java.util.Arrays.sort() the order of edge objects
  would be the same with and without such scaling.
 
  Also in answer to this question, on http://algs4.cs.princeton.edu/43mst/
  there is the following:
  "Solution. Kruskal's algorithm accesses the edge weights only through the 
  compareTo() method. Adding a positive constant to each weight (or multiplying 
  by a positive constant) won't change the result of the compareTo() method."
  
  I don't consider this solution or my answer as proof, since that's too strong
  a word for a justification of what's clear by design once that's inspected.
  
 */  

public class Ex4301MSTUnaffectedByPositiveAdditiveAndMultiplicativeScaling {
  
  public static void main(String[] args) {
    
  }

}



