package ex43;

/* p634
  4.3.33 Certification. Write an  MST and EdgeWeightedGraph client check() 
  that uses the following cut optimality conditions implied by Proposition 
  J to verify that a proposed set of edges is in fact an MST: A set of edges 
  is an MST if it is a spanning tree and every edge is a minimum-weight edge 
  in the cut defined by removing that edge from the tree. What is the order 
  of growth of the running time of your method?
  
  This is already done in the check(EdgeWeightedGraph) method in
    http://algs4.cs.princeton.edu/43mst/KruskalMST.java and
    http://algs4.cs.princeton.edu/43mst/PrimMST.java
  that are locally available at graph.KruskalMST and graph.PrimMST.
  According to a comment just above the method, it "takes time proportional to
  E V lg* V".
 
 */  

public class Ex4333MSTandEdgeWeightedGraphCertificationClient {

  public static void main(String[] args) {

  }

}


