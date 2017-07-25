package ex44;

import static java.lang.Math.pow;

import analysis.Timer;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPBucketPQ;
import graph.DijkstraSPI;
import graph.DirectedEdgeI;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphI;
import graph.EdgeWeightedDigraphX;

/* p691
  4.4.45 Fast Bellman-Ford. Develop an algorithm that breaks the linearithmic 
  running time barrier for the single-source shortest-paths problem in general 
  edge-weighted digraphs for the special case where the weights are integers 
  known to be bounded in absolute value by a constant.
  
  I know of no such algorithm without additional constraints particularly a re-
  quirement of no negative cyles. "Fast Bellman-Ford" seems to refer to what's
  usually called the "Shortest Path Faster Algorithm", but that's already im-
  plemented in graph.BellManFordSP and has the same VE worst case performance as 
  the basic Bellman-Ford algorithm. 
  
  If a graph doesn't have a negative cycle and its weights are integers bounded 
  in absolute value by a constant, then it can be reweighted to have all positive 
  weights that are bounded by a possibly different constant C and processed with 
  Dijkstra's algorithm using a bucket queue to compute the single-source shortest 
  paths with runtime ~ E + VC or faster with radix heaps according to Mehlhorn 
  and Sanders* (sections 10.4.1-2).  
  
  An implementation of Dijkstra's algorithm using a bucket queue based on Mehlhorn 
  and Sanders* and an improved algorithm by Robert B. Dial as described by Subhash 
  Suri in https://www.cs.ucsb.edu/~suri/cs231/ShortestPaths.pdf on pages 40-41
  (available locally at ImprovedDialDijkstraSP-Suri-2004.pdf) is demonstrated below. 
  It requires larger graphs with relatively small max edge weights to show a per-
  formance advantage over Dijkstra's algorithm immplemented with an IndexMinPQ.
  
  * Algorithms and Data Structures: The Basic Toolbox, Mehlhorn, Kurt; Sanders, Peter 
    (2008), Springer, ISBN 978-3-540-77977-3
    https://www.springer.com/us/book/9783540779773 
    (Initial work on this was published in "Faster algorithms for the shortest path 
     problem", Ahuja, Ravindra K.; Mehlhorn, Kurt; Orlin, James B.; Tarjan, Robert E. 
     (April 1990), Journal of the Association for Computing Machinery, 37 (2): 213–223, 
     CiteSeerX 10.1.1.85.5847
     http://jorlin.scripts.mit.edu/docs/publications/32-faster%20algorithms%20shortest.pdf)
 
 */  

public class Ex4445FastBellmanFord {
  
  public static Integer sf(Double d) {
    // return the number of digits after the decimal point in d
    if (d == null) return null;
    if ((""+d).indexOf('.') == -1) return 0;
    else {
      String x = (""+d).replaceAll("[0-9+-]*\\.", "");
      return x.matches("0+") ? 0 : x.length();
    }
  }
  
  public static Integer sf(Seq<DirectedEdgeX> s) {
    // return multiplicative scale factor for the weights of edges 
    // in s for computing their sum using integer arithmetic
    if (s == null) return null;
    Integer sf = null;
    for (DirectedEdgeX e : s) {
      if (sf == null) sf = sf(e.w());
      else sf = Math.max(sf, sf(e.w()));
    }
    return sf; 
  }
  
  public static int sf(String ewd) {
    // return weight scale factor for graph ewd
    EdgeWeightedDigraphX g = new EdgeWeightedDigraphX(new In(ewd));
    return sf(g.edgeSeq());
  }
  
  public static EdgeWeightedDigraphI convert(String ewd) {
    EdgeWeightedDigraphX g = new EdgeWeightedDigraphX(new In(ewd));
    int sf = sf(g.edgeSeq());
    EdgeWeightedDigraphI i = new EdgeWeightedDigraphI(g.V());
    for (DirectedEdgeX e : g.edges()) i.addEdge(e.from(),e.to(),(int)(pow(10,sf)*e.w()));   
    return i;
  }
  
  public static void demo(String ewd, int source, boolean scaleDown, int factor, int trials) {
    // performance comparison of Dijkstra's SP algorithm using a IndexMinPQ vs. 
    // a bucket queue (pq.BucketMinPQWithDoublyLinkedList3).
    // shows advantage of bucket queue for moderately large graph with a small
    // max edge weight.
    if (ewd == null) throw new IllegalArgumentException("String ewd is null");
    if (source < 0) throw new IllegalArgumentException("int source < 0"); 
    if (factor <= 0) throw new IllegalArgumentException("int factor <= 0"); 
    if (trials <= 0) throw new IllegalArgumentException("int trials <= 0"); 
    
    System.out.print("testing "+ewd+" for "+trials+" trials ");
    if (scaleDown == false) System.out.println("without reduced edge weights:");
    else System.out.println("with edge weights reduced by a\nfactor of "+factor+" "
        + "then incremented by 1:");
      
    Timer t = new Timer();
    
    EdgeWeightedDigraphI G = convert(ewd);
    if (scaleDown) for (DirectedEdgeI e : G.edges()) e.setW(e.w()/factor+1);
    int maxWeight = G.maxWeight();
    System.out.println("maxWeight = "+maxWeight);
    
    int c = 0, n = 1000, s = source; 
    double sum = 0; 
    
    while (c < n) {
      t.reset();
      new DijkstraSPBucketPQ(G,s,maxWeight);
      sum += t.elapsed();
     c++;
    }
    System.out.println("spb avg = "+(sum/n)+" ms  (BucketMinPQ)");
    
    c = 0; sum = 0;
    while (c < n) {
      t.reset();
      new DijkstraSPI(G,s);
      sum += t.elapsed();
      c++;
    }
    System.out.println("spi avg = "+(sum/n)+" ms  (IndexMinPQ)\n");
  }

  public static void main(String[] args) {
    demo("tinyEWD.txt",   0, false,  1, 1000);
    demo("tinyEWD.txt",   0, true,  10, 1000);
    demo("mediumEWD.txt", 0, false,  1, 1000);
    demo("mediumEWD.txt", 0, true, 100, 1000);
    demo("1000EWD.txt",   0, false,  1, 1000);
    demo("1000EWD.txt",   0, true, 100, 1000);
    demo("10000EWD.txt",  0, false,  1, 1000);
    demo("10000EWD.txt",  0, true, 100, 1000);

    /*
    testing tinyEWD.txt for 1000 trials without decreasing edge weights:
    maxWeight = 93
    spb avg = 0.041 ms  (BucketMinPQ)
    spi avg = 0.022 ms  (IndexMinPQ)
  
    testing tinyEWD.txt for 1000 trials with edge weights reduced by a
    factor of 10 then incremented by 1:
    maxWeight = 10
    spb avg = 0.035 ms  (BucketMinPQ)
    spi avg = 0.031 ms  (IndexMinPQ)

    testing mediumEWD.txt for 1000 trials without decreasing edge weights:
    maxWeight = 11996
    spb avg = 1.101 ms  (BucketMinPQ)
    spi avg = 0.293 ms  (IndexMinPQ)
    
    testing mediumEWD.txt for 1000 trials with edge weights reduced by a 
    factor of 100 then incremented by 1:
    maxWeight = 120
    spb avg = 0.168 ms  (BucketMinPQ)
    spi avg = 0.242 ms  (IndexMinPQ)
    
    testing 1000EWD.txt for 1000 trials without decreasing edge weights:
    maxWeight = 7500
    spb avg = 1.96 ms  (BucketMinPQ)
    spi avg = 1.125 ms  (IndexMinPQ)
    
    testing 1000EWD.txt for 1000 trials with edge weights reduced by a 
    factor of 100 then incremented by 1:
    maxWeight = 76
    spb avg = 1.057 ms  (BucketMinPQ)
    spi avg = 1.106 ms  (IndexMinPQ)
    
    testing 10000EWD.txt for 1000 trials without decreasing edge weights:
    maxWeight = 2000
    spb avg = 13.736 ms  (BucketMinPQ)
    spi avg = 14.108 ms  (IndexMinPQ)
    
    testing 10000EWD.txt for 1000 trials with edge weights reduced by a 
    factor of 100 then incremented by 1:
    maxWeight = 21
    spb avg = 12.511 ms  (BucketMinPQ)
    spi avg = 13.843 ms  (IndexMinPQ)

     */
    
  }
  
  

}


