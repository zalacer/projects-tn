package ex44;

import graph.DijkstraSPX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.42 Worst case (Dijkstra). Describe a family of graphs with V vertices 
  and E edges for which the worst-case running time of Dijkstra’s algorithm 
  is achieved.
  
  Such a family can be generated using the following algorithm for creating
  an EdgeWeightedDigraphX with V vertices and V*(V-1)/2 edges:
  
  The Vickers Algorithm:
  ====================================================
  EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V)
  int c = 2;
  for (int i = 1; i < n; i++) G.addEdge(i-1, i, 1);
    for (int i = n-2; i > 0; i--) {
      c++;
      for (int j = i+2; j < n+1 ; j++) {
        G.addEdge(i-1, j-1, c);
        c++;
      }
    }
    
  This algorithm is implemented below in creatGraph(). Demonstration below shows
  that the number of IndexMinPQ changeKey() operations used by DijkstraSPX for 
  source vertex 0 approaches the number of edges as the number of vertices increases 
  for this family of graphs. Additionally each such operation causes the vertex of 
  the key changed to be placed at the top of the queue.
  
  Reference:
  A Comparison of Worst Case Performance of Priority Queues used in Dijkstra's 
  Shortest Path Algorithm, Alex Vickers, University of Canterbury, Department of 
  Computer Science, 1986.
  https://ir.canterbury.ac.nz/handle/10092/13567
  https://ir.canterbury.ac.nz/bitstream/handle/10092/13567/vickers_report_1986.pdf
 
 */  

public class Ex4442DijkstraWorstCase {
  
  public static void demo() {
    int c = 1;
    System.out.println("V      E         inserts  delmins  chgpris    chgpris/E");
    while (c < 2048) calculateSP(c *= 2);
  }
  
  public static void calculateSP(int V) {
    // run DijkstraSPX on the graph with V vertices created using Vickers algo
    if (V < 1) throw new IllegalArgumentException("V < 1");
    EdgeWeightedDigraphX G = creatGraph(V);
    int E = G.E();
    DijkstraSPX sp = new DijkstraSPX(G,0);
    long inserts = sp.inserts(); 
    long delmins = sp.delmins();
    long chgpris = sp.chgpris();
    System.out.printf("%-5d  %-9d %-5d    %-5d    %-9d  %5.3f\n",
        V, E, inserts, delmins, chgpris, 1.*chgpris/E);
  }
  
  public static EdgeWeightedDigraphX creatGraph(int V) {
    // Create a graph with V vertices and V*(V-2)/2 edges using the Vickers algorithm
    EdgeWeightedDigraphX g = new EdgeWeightedDigraphX(V);
    int c = 2;
    for (int i = 1; i < V; i++) g.addEdge(i-1, i, 1);
    for (int i = V-2; i > 0; i--) {
      c++;
      for (int j = i+2; j < V+1 ; j++) {
        g.addEdge(i-1, j-1, c);
        c++;
      }
    }
    return g;
  }

  public static void main(String[] args) {
    
    demo();
    /*
    V      E         inserts  delmins  chgpris    chgpris/E
    2      1         2        2        0          0.000
    4      6         4        4        3          0.500
    8      28        8        8        21         0.750
    16     120       16       16       105        0.875
    32     496       32       32       465        0.938
    64     2016      64       64       1953       0.969
    128    8128      128      128      8001       0.984
    256    32640     256      256      32385      0.992
    512    130816    512      512      130305     0.996
    1024   523776    1024     1024     522753     0.998
    2048   2096128   2048     2048     2094081    0.999
     */    
  }

}


