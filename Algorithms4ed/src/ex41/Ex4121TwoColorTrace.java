package ex41;

import graph.TwoColorTrace;
import graph.GraphGeneratorX;
import graph.GraphX;

/* p560
  4.1.21  Show, in the style of the figures in this section, a detailed 
  trace of TwoColor for finding a two-coloring of the graph built by 
  Graph's input stream constructor for the file tinyGex2.txt (see Exercise 
  4.1.2). What is the order of growth of the running time of the TwoColor 
  constructor, in the worst case?
  
  This is implemented in graph.TwoColorTrace and demonstrated below.
  
  Benchmarks with worst case graphs, namely Eulerian Cycles with the same
  number of edges as vertices, show quadratic growth and is demonstrated below.
 */

public class Ex4121TwoColorTrace {

  public static void main(String[] args) {

    // edges are from tinyGex2.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
    GraphX G = new GraphX(12,edges);
    System.out.println("TwoColorTrace(\"tinyGex2.txt\":");
    new TwoColorTrace(G);
    
    System.out.println("\nTwoColorTrace doubling test using Eulerian Cycles:");
    int h = 4; TwoColorTrace trace = null;
    LOOP: while (true) {
      System.out.println("V        dfsForLoopCount   ratio");
      trace = new TwoColorTrace(GraphGeneratorX.eulerianCycle(h,h),"quiet");
      double previous = trace.dfsForLoopCount();
      while (h < 8193) {
        h = h*2;
        trace = new TwoColorTrace(GraphGeneratorX.eulerianCycle(h,h),"quiet");
        double x = trace.dfsForLoopCount();
        double r = x/previous;
        System.out.printf("%-7d  %-7d           %3.3f\n", h, trace.dfsForLoopCount(), r); 
        previous = x;
      }
      if (!trace.isTwoColorable()) break LOOP;
      else System.out.println("\n\n");
    }

/*
    TwoColorTrace("tinyGex2.txt":
    graph.adj = [(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),
                 (8,11),(1,11,7,4),(),(3,5,3),(8,7,1)]
                   marked[]                   color[]
                   0 1 2 3 4 5 6 7 8 9 10 11  0 1 2 3 4 5 6 7 8 9 10 11
    dfs(0)         T                                                 
     dfs(2)        T   T                          T                  
      dfs(5)       T   T     T                    T     F            
       dfs(10)     T   T     T         T          T     F         T  
        dfs(3)     T   T T   T         T          T F   F         T  
         dfs(6)    T   T T   T T       T          T F   F T       T  
    isTwoColorable = false
    
    TwoColorTrace doubling test using Eulerian Cycles:
    V        dfsForLoopCount   ratio
    8        14                14.000
    16       30                2.143
    32       53                1.767
    64       92                1.736
    128      209               2.272
    256      422               2.019
    512      803               1.903
    1024     1501              1.869
    2048     3244              2.161
    4096     6354              1.959
    8192     12806             2.015
    16384    25869             2.020
*/

  }

}



