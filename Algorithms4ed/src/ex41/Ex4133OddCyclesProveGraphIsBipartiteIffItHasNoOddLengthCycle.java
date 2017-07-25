package ex41;

import graph.CycleX;
import graph.GraphX;

/* p562
  4.1.33 Odd cycles. Prove that a graph is two-colorable (bipartite) if 
  and only if it contains no odd-length cycle.
  
  This is done in a clear fashion in the only answer to the question at
    https://math.stackexchange.com/questions/61920/if-a-graph-has-no-cycles-of-odd-length-then-it-is-bipartite-is-my-proof-correc?rq=1
  a copy of which is in this project at IfAGraphHasNoCyclesOfOddLengthThenItIsBipartite.pdf.
    
  Note this proof uses the term "walks" of a graph. Those are defined at 
  https://proofwiki.org/wiki/Definition:Walk  as follows:
  
    A walk on a graph is an alternating series of vertices and edges beginning 
    and ending with a vertex in which each edge is incident with the vertex 
    immediately preceding it and the vertex immediately following it.
    
  In other words a walk is the same as a path as defined in this text in the 
  glossary on the second page of section 4.1 (page 519).
  
 */                                                   

public class Ex4133OddCyclesProveGraphIsBipartiteIffItHasNoOddLengthCycle {

  public static void main(String[] args) {
    
    // edges are from tinyGex3.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 " 
        + "2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    GraphX g = new GraphX(12,edges);
    CycleX c = new CycleX(new GraphX(12,edges));
    System.out.println("graph edges = "+g.E());
    System.out.println("CycleX.findAllParallelEdges() inner for loop "
        + "iterations = " + c.getFapecount());
 
/*    
    parallel edges : ((1,8,1),(2,5,2),(3,10,3))
    hasSelfLoop: (5,5)
    cycle: (5,5)
    graph edges = 21
    CycleX.findAllParallelEdges() inner for loop iterations = 42
*/ 
    
  }

}



