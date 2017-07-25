package ex41;

import graph.CycleTrace;
import graph.GraphGeneratorX;
import graph.GraphX;

/* p560
  4.1.20  Show, in the style of the figures in this section, a detailed trace   
  ofCycle for finding a cycle in the graph built by Graph's input stream con-
  structor for the file tinyGex2.txt (see Exercise 4.1.2). What is the order  
  of growth of the running time of the Cycle constructor, in the worst case?
  
  This is implemented in graph.CycleTrace and demonstrated below.
  
  Benchmarks with worst case (acyclic) graphs, namely paths and trees, show 
  quadratic order of growth. This is also demonstrated below.
 */

public class Ex4120CycleTrace {

  public static void main(String[] args) {

    // edges are from tinyGex2.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
    GraphX G = new GraphX(12,edges);
    System.out.println("CycleTrace(\"tinyGex2.txt\":");
    new CycleTrace(G);
    
    System.out.println("\nCycleTrace doubling test using trees:");
    int h = 2; CycleTrace trace = null;
    System.out.println("V        dfsForLoopCount   ratio");
    trace = new CycleTrace(GraphGeneratorX.tree(h),"quiet");
    double previous = trace.dfsForLoopCount();
    while (h < 8193) {
      h = 2*h;
      trace = new CycleTrace(GraphGeneratorX.tree(h),"quiet");
      double x = trace.dfsForLoopCount();
      double r = x/previous;
      System.out.printf("%-7d  %-7d           %3.3f\n", h, trace.dfsForLoopCount(), r); 
      previous = x;
      if (trace.hasCycle()) { System.out.println("cycle detected"); return; }
    }
/*
    CycleTrace("tinyGex2.txt":
    graph.adj = [(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),
                 (8,11),(1,11,7,4),(),(3,5,3),(8,7,1)]
    parallelEdges: (3,10,3)
    cycle identification trace:
                   marked[]                   edgeTo[]
                   0 1 2 3 4 5 6 7 8 9 10 11  0 1 2 3 4 5 6 7 8 9 10 11
    dfs(-1,0)        T                                               
     dfs(0,2)        T   T                        0                  
      dfs(2,5)       T   T     T                  0     2            
       dfs(5,10)     T   T     T         T        0     2         5  
        dfs(10,3)    T   T T   T         T        0 10  2         5  
         dfs(3,6)    T   T T   T T       T        0 10  2 3       5  
    dfs(-1,1)        T T T T   T T       T        0 10  2 3       5  
    dfs(-1,4)        T T T T T T T       T        0 10  2 3       5  
    dfs(-1,7)        T T T T T T T T     T        0 10  2 3       5  
    dfs(-1,8)        T T T T T T T T T   T        0 10  2 3       5  
    dfs(-1,9)        T T T T T T T T T T T        0 10  2 3       5  
    dfs(-1,11)       T T T T T T T T T T T T      0 10  2 3       5  
    cycle: (6,2,5,10,3,6)
    
    CycleTrace doubling test using trees:
    V        dfsForLoopCount   ratio
    4        6                 3.000
    8        14                2.333
    16       30                2.143
    32       62                2.067
    64       126               2.032
    128      254               2.016
    256      510               2.008
    512      1022              2.004
    1024     2046              2.002
    2048     4094              2.001
    4096     8190              2.000
    8192     16382             2.000
    16384    32766             2.000
*/

  }

}



