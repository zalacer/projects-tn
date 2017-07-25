package ex44;

import edu.princeton.cs.algs4.In;
import graph.BellmanFordSPX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.44 Worst case (Bellman-Ford). Describe a family of graphs for which 
  Algorithm 4.11 takes time proportional to VE.
  
  Any family of graphs with a negative-weight cycle and processed using 
  an iterative implementation of the Bellman-Ford algorithm that halts after
  a maximum of VE steps. Such a family is defined and tested below using 
  a special constructor of graph.BellmanFordSPX that implements the required
  Bellman-Ford algorithm, does negative cycle detection and prints the results.
 
 */  

public class Ex4444BellmanFordWorstCase {
 
  public static void demo(String ewd, int source) {
    // run BellmanFordSPX iterative constructor on edge-weighted digraph represented by ewd
    if (ewd == null) throw new IllegalArgumentException("String ewd is null");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In(ewd));
    if (source < 0 || source > G.V()-1) 
      throw new IllegalArgumentException("source "+source+" is out of bounds");
    System.out.println("results for "+ewd+":");
    new BellmanFordSPX(G, source, true); // iterative Bellman-Ford constructor
    System.out.println();
  }
  
  public static void demo(int start, int stop) {
    // run BellmanFordSPX iterative constructor on graphs in family with
    // a negative cycle containing start to stop vertices using source 0
    if (start > stop) { int tmp = start; start = stop; stop = tmp; }
    for (int v = start; v <= stop; v++) {
      EdgeWeightedDigraphX G = graph(v);
      System.out.println("results for negative cycle graph family with "+v+" vertices:");
      new BellmanFordSPX(G, 0, true); // iterative Bellman-Ford constructor
      System.out.println();
    }
  }
  
  public static EdgeWeightedDigraphX graph(int V) {
    // define a family of graphs with a negative-weight cycle
    if (V < 2) throw new IllegalArgumentException("V < 2");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (int i = 0; i < V-1; i++) G.addEdge(i, i+1, 1);
    G.addEdge(V-1, V-2, -2);
    return G;
  }

  public static void main(String[] args) {
    
    demo("tinyEWD.txt", 0);     // no negative cycle
    demo("tinyEWDnc.txt", 0);   // has a negative cycle
    demo(2,7);                  // all have a negative cycle
    /*
    results for tinyEWD.txt:
    V=8 E=15
    relaxations=60
    no negative cycle
    shortest paths from source 0:
    0->1: 0>4>5>1
    0->2: 0>2
    0->3: 0>2>7>3
    0->4: 0>4
    0->5: 0>4>5
    0->6: 0>2>7>3>6
    0->7: 0>2>7
    
    results for tinyEWDnc.txt:
    V=8 E=15
    relaxations = 120 == V*E
    negative cycle = 4>5>4
    
    results for negative cycle graph family with 2 vertices:
    V=2 E=2
    relaxations = 4 == V*E
    negative cycle = 0>1>0
    
    results for negative cycle graph family with 3 vertices:
    V=3 E=3
    relaxations = 9 == V*E
    negative cycle = 1>2>1
    
    results for negative cycle graph family with 4 vertices:
    V=4 E=4
    relaxations = 16 == V*E
    negative cycle = 2>3>2
    
    results for negative cycle graph family with 5 vertices:
    V=5 E=5
    relaxations = 25 == V*E
    negative cycle = 3>4>3
    
    results for negative cycle graph family with 6 vertices:
    V=6 E=6
    relaxations = 36 == V*E
    negative cycle = 4>5>4
    
    results for negative cycle graph family with 7 vertices:
    V=7 E=7
    relaxations = 49 == V*E
    negative cycle = 5>6>5
     */
    }
  
  

}


