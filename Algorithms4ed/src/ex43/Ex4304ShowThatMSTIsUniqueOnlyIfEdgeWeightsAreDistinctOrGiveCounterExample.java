package ex43;

/* p631
  4.3.4  Consider the assertion that an edge-weighted graph has a unique MST only 
  if its edge weights are distinct. Give a proof or a counterexample.
  
  A counterexample is the following graph:   0
                                             / \
                                            /   \  
                                  weight 1 /     \ weight 2
                                          /       \
                                         /         \
                                        1-----------2
                                           weight 1
  
  This graph has the unique MST {(0,1),(1,2)} containing the two edges with weight 1.
  Edge (0,2) can't be in any MST of this graph because the remaining two edges have
  lower weight and span it.
  
  Another way to state the assertion is: an edge-weighted graph has a unique MST only
  if every cut has unique minimum weight edge. For the graph shown, the cut ({1},{0,2})
  has no unique minimum weight edge.
                                           
 */   

public class Ex4304ShowThatMSTIsUniqueOnlyIfEdgeWeightsAreDistinctOrGiveCounterExample {
  
  public static void main(String[] args) {
    
  }

}

