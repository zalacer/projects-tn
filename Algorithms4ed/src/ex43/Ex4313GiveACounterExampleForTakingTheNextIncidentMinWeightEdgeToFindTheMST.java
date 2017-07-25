package ex43;

/* p631
  4.3.13  Give a counterexample that shows why the following strategy does 
  not necessarily find the MST: ‘Start with any vertex as a single-vertex 
  MST, then add  V-1 edges to it, always taking next a min-weight edge 
  incident to the vertex most recently added to the MST.
  
  As stated the suggested strategy doesn't make sense enough to try because
  for many graphs it would be impossible to add V-1 edges to the MST using it. 
  For example with the graph below starting from A it would add edge (A,B) 
  and then be unable to add a final edge since the most recently added vertex,
  B, is a sink.
  
              A
             / \ 
          1 /   \ 2
           /     \  
          B       C
          
  Maybe the algorithm backtracks, but now it's too much of a guessing game to
  understand unambiguously what it is and we already have several well tested
  MST algorithms that work.
  

 */  

public class Ex4313GiveACounterExampleForTakingTheNextIncidentMinWeightEdgeToFindTheMST {
  
  public static void main(String[] args) {
    
  }

}


