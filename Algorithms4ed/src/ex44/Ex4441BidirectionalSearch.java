package ex44;

import static v.ArrayUtils.combinations;
import static v.ArrayUtils.range;

import java.util.Iterator;

import analysis.Timer;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSourceSinkBidirectionalSP;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.41 Bidirectional search. Develop a class for the source-sink shortest
  -paths problem that is based on code like Algorithm 4.9 but that initial-
  izes the priority queue with both the source and the sink. Doing so leads 
  to the growth of an SPT from each vertex; your main task is to decide pre-
  cisely what to do when the two SPTs collide.
  
  (Algorithm 4.9 is Dijkstra’s shortest-paths algorithm on p655)
  
  This is implemented in graph.DijkstraSourceSinkBidirectionalSP for graphs with
  nonnegative edge weights and demonstrated below. Note that some paths found
  bidirectionally are different than those found unidirectionally, as can be ex-
  pected, but their endpoints are identical and the path weight differences are 
  less than 2.3*10^-16 for all shortest paths in tinyEWD, mediumEWD and 1000EWD.
  These differences may be accountable as rounding discrepancies and in some cases
  the bidirectional algorithm found technically shorter paths.
  
  DijkstraSourceSinkBidirectionalSP includes only necessary methods to simplify
  reading of it. A complete version with all supporting methods in 
  graph.DijkstraSourceSinkSP is graph.DijkstraSourceSinkBidirectionalSPComplete.
  
  DijkstraSourceSinkBidirectionalSP uses the same basic format as DijkstraSourceSinkSP
  except its constructor takes both the input graph and its reverse as arguments to 
  separate the overhead of graph reversal from that of finding shortest paths. The 
  stopping condition is implemented in relax1() and relax2() as described on page 3 of
  "Efficient Point-to-Point Shortest Path Algorithms" that's  available for download 
  from http://www.cs.princeton.edu/courses/archive/spr06/cos423/Handouts/EPP%20shortest%20path%20algorithms.pdf.
  When the stopping condition is true, the boolean variable done is set to true causing
  queue processing to stop. Then the shortest path can be constructed by joining the 
  forward and reverse shortest path segments on the common vertex with the minimum forward 
  + reverse weight as mentioned on page 2 of https://courses.csail.mit.edu/6.006/fall11/lectures/lecture18.pdf. 
  path() does this using min() to find the min-weighted common vertex.
 
 */  

public class Ex4441BidirectionalSearch {
  
  public static String str(Seq<DirectedEdgeX> p) {
    StringBuilder sb = new StringBuilder();
    for (DirectedEdgeX e : p) sb.append(e + "   ");
    return sb.toString();
  }
  
  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }
  
  public static void compare(String ewd, boolean quiet) {
    // one-off comparison of unidirectional vs bidirectional Dijkstra SP algorithms
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In(ewd));
    EdgeWeightedDigraphX R = G.reverse();
    DijkstraSourceSinkSP sp1; DijkstraSourceSinkBidirectionalSP sp2;
    Seq<DirectedEdgeX> path1, path2;
    long unequalPathsCount = 0, combinations = 0, relaxations1 = 0, relaxations2 = 0;
    double w1, w2, diff, maxDiff = -1, maxAcceptableDiff = 3.*Math.pow(10, -16);
    boolean broke = false;
    Timer t1 = new Timer(), t2 = new Timer();
    long runtime1 = 0, runtime2 = 0;

    System.out.println("\nresults for "+ewd);
    System.out.println("===============================================================");
    
    Iterator<int[]> it = combinations(range(0,G.V()),2);
    while (it.hasNext()) {
      combinations++;
      int[] a = it.next();
      t1.reset();
      sp1 = new DijkstraSourceSinkSP(G,a[0],a[1],"q");
      runtime1 += t1.elapsed();
      relaxations1 += sp1.relaxations();
      path1 = sp1.seqTo();
      t2.reset();
      sp2 = new DijkstraSourceSinkBidirectionalSP(G,R,a[0],a[1]);
      runtime2 += t2.elapsed();
      relaxations2 += sp2.relaxations();
      path2 = sp2.path();
      w1 = weight(path1); w2 = weight(path2);
      diff = Math.abs(w1 - w2);    
      
      if (diff > maxDiff) maxDiff = diff;
      
      if (!path1.equals(path2)) {
        unequalPathsCount++;
        if (diff < maxAcceptableDiff) {
          if (!quiet) {
            System.out.println("unequal but equally weighted paths for "+a[0]+"->"+a[1]);
            System.out.println("path1 = "+str(path1)+"("+w1+")");
            System.out.println("path2 = "+str(path2)+"("+w2+")\n");
          }
        } else {
          System.out.println("unequal and unequally weighted paths for "+a[0]+"->"+a[1]);
          System.out.println("path1 = "+str(path1)+"("+w1+")");
          System.out.println("path2 = "+str(path2)+"("+w2+")\n");
          System.out.println("break on unacceptable weight difference");
          broke = true;
          break;
        }
      }
    }
    
    if (!broke) {
      System.out.println("max difference in path weights          = "+maxDiff);
      System.out.println("total number of paths                   = "+combinations);
      System.out.println("number of different paths               = "+unequalPathsCount);
      System.out.println("percent different paths                 = "
          +String.format("%1.3f", (100.*unequalPathsCount/combinations))+"%");
      System.out.println("total number unidirectional relaxations = "+relaxations1);
      System.out.println("total number bidirectional relaxations  = "+relaxations2);
      System.out.println("bidirectional relaxation efficiency     = "
          +String.format("%1.3f", (100.*relaxations1/relaxations2))+"%");
      System.out.println("total unidirectional runtime            = "+runtime1+" ms");
      System.out.println("total bidirectional runtime             = "+runtime2+" ms");
      System.out.println("bidirectional runtime efficiency        = "
          +String.format("%1.3f", (100.*runtime1/runtime2))+"%");
    }
  }
  
  public static void main(String[] args) {
    
    compare("tinyEWD.txt", true);
    compare("mediumEWD.txt", true);
    compare("1000EWD.txt", true);
    
/*
    runtimes in the results below depend on the quality of jvm 
    optimization of the classes used, can be reduced by repeated 
    executions and that may affect their ratios

    results for tinyEWD.txt
    ===============================================================
    max difference in path weights          = 0.0
    total number of paths                   = 28
    number of different paths               = 0
    percent different paths                 = 0.000%
    total number unidirectional relaxations = 178
    total number bidirectional relaxations  = 205
    bidirectional relaxation efficiency     = 86.829%
    total unidirectional runtime            = 6 ms
    total bidirectional runtime             = 3 ms
    bidirectional runtime efficiency        = 200.000%
    
    results for mediumEWD.txt
    ===============================================================
    max difference in path weights          = 2.220446049250313E-16
    total number of paths                   = 31125
    number of different paths               = 90
    percent different paths                 = 0.289%
    total number unidirectional relaxations = 40600534
    total number bidirectional relaxations  = 27486454
    bidirectional relaxation efficiency     = 147.705%
    total unidirectional runtime            = 3686 ms
    total bidirectional runtime             = 2278 ms
    bidirectional runtime efficiency        = 161.809%
    
    results for 1000EWD.txt
    ===============================================================
    max difference in path weights          = 2.220446049250313E-16
    total number of paths                   = 499500
    number of different paths               = 12509
    percent different paths                 = 2.505%
    total number unidirectional relaxations = 4304700849
    total number bidirectional relaxations  = 2910650044
    bidirectional relaxation efficiency     = 147.894%
    total unidirectional runtime            = 356174 ms
    total bidirectional runtime             = 200658 ms
    bidirectional runtime efficiency        = 177.503%

*/    
  }

}


