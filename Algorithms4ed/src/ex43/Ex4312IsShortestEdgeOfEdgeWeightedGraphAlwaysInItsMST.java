package ex43;

/* p631
  4.3.12 Suppose that a graph has distinct edge weights. Does its shortest 
  edge have to belong to the MST? Can its longest edge belong to the MST? 
  Does a min-weight edge on every cycle have to belong to the MST? Prove 
  your answer to each question or give a counterexample.
  
  1. Does its shortest edge have to belong to the MST? 
     Yes. Consider a spanning tree of the graph that doesn't contain the
     shortest edge. Adding the shortest edge to the spanning tree causes it
     to have a cycle containing that shortest edge (otherwise the cycle would
     have been in the original spanning tree that's impossible). Remove any
     edge other than the shortest edge from that cycle to get another spanning 
     tree that's shorter than the first proving the the latter can not be minimal.
    
  2. Can its longest edge belong to the MST?
     Yes. The graph below contains a longest edge and its MST (A,B,C,D) contains 
     all its edges.
     
       1   2   3
     A---B---C---D
  
  3. Does a min-weight edge on every cycle have to belong to the MST?
     No. The graph below the cycle B-D-E has a min-weight edge but none of its
     edges are in its MST (D,A,B,C,E).
  
         1   1
       A---B---C
       |  / \  |
     1 | /4 5\ | 1
       |/  6  \|
       D-------E
           
 */  

public class Ex4312IsShortestEdgeOfEdgeWeightedGraphAlwaysInItsMST {
  
  public static void main(String[] args) {
    
  }

}


