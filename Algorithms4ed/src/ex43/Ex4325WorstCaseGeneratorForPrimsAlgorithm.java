package ex43;

import analysis.Timer;
import ds.Seq;
import graph.EdgeWeightedGraphGeneratorX;
import graph.EdgeWeightedGraphX;
import graph.LazyPrimMST;
import graph.PrimMSTBX;

/* p633
  4.3.25 Worst-case generator. Develop a reasonable generator for edge-weighted 
  graphs with V vertices and E edges such that the running time of the lazy ver-
  sion of Prim’s algorithm is nonlinear. Answer the same question for the eager 
  version.

  I converted graph.GraphGeneratorX to graph.EdgeWeightedGraphGeneratorX for
  general purpose generation of edge-weighted graphs  by generating reasonable
  random weights for each edge in the input GraphX and using a single static method,
  namely EdgeWeightedGraphGeneratorX.toEdgeWeightedGraphX(GraphX G). In turn
  EdgeWeightedGraphGeneratorX creates a parallel GraphX to allow access to other
  classes based on GraphX such as BipartiteX, BridgeX, CCX, etc.
  
  The worst case runtime for LazyPrimMst is ~ ElogE that's nonlinear in V for dense
  graphs when E ~ V^2.

  The worst case runtime for EagerPrimMst is ~ ElogV that's also nonlinear in V for
  dense graphs when E ~ V^2.

  To avoid self-edges and parallel edges a maximum of  V*(V-1)/2 edges are allowed for
  V vertices and that's built into EdgeWeightedGraphGeneratorX.simple(int V, int E).
  This is demonstrated below showing non-linear behavior for both Prim algos however 
  much more pronounced for LazyPrim while EagerPrim has comparatively low runtimes.

 */  

public class Ex4325WorstCaseGeneratorForPrimsAlgorithm {

  public static void main(String[] args) {

    // generating all graphs for testing in advance so the same ones can
    // be used for both tests
    Seq<EdgeWeightedGraphX> graphs = new Seq<>();
    int m = 16, n = 7;
    for (int i = 0; i < n; i++) 
      graphs.add(EdgeWeightedGraphGeneratorX.simple(m *= 2, m*(m-1)/2));

    Timer t = new Timer(); long prev, time; double r;

    System.out.println("LazyPrimMST doubling test:");
    System.out.println("vertices  edges     runtime(ms)  ratio");
    EdgeWeightedGraphX g = graphs.get(0);
    t.reset();
    new LazyPrimMST(g);
    prev = t.elapsed();
    System.out.printf("%-7d   %-10d%-10d   (NA)\n", g.V(), g.E(), prev); 
    for (int i = 1; i < 5; i++) {
      g = graphs.get(i);
      t.reset();
      new LazyPrimMST(g);
      time = t.elapsed();
      if (prev != 0) {
        r = 1.*time/prev;
        System.out.printf("%-7d   %-10d%-10d   %-3.3f\n", g.V(), g.E(), time, r); 
      } else {
        System.out.printf("%-7d   %-10d%-10d   (NA)\n", g.V(), g.E(), time); 
      }
      prev = time;
    }

    System.out.println("\nEagerPrimMST doubling test:");
    System.out.println("vertices  edges     runtime(ms)  ratio");
    g = graphs.get(0);
    t.reset();
    new PrimMSTBX(g);
    prev = t.elapsed();
    System.out.printf("%-7d   %-10d%-10d   (NA)\n", g.V(), g.E(), prev); 
    for (int i = 1; i < graphs.size(); i++) {
      g = graphs.get(i);
      t.reset();
      new PrimMSTBX(g);
      time = t.elapsed();
      if (prev != 0) {
        r = 1.*time/prev;
        System.out.printf("%-7d   %-10d%-10d   %-3.3f\n", g.V(), g.E(), time, r); 
      } else {
        System.out.printf("%-7d   %-10d%-10d   (NA)\n", g.V(), g.E(), time); 
      }
      prev = time;
    }

    /*   
      LazyPrimMST doubling test:
      vertices  edges     runtime(ms)  ratio
      32        496       15           (NA)
      64        2016      33           2.200
      128       8128      499          15.121
      256       32640     9391         18.820
      512       130816    440800       46.939
      
      EagerPrimMST doubling test:
      vertices  edges     runtime(ms)  ratio
      32        496       16           (NA)
      64        2016      0            0.000
      128       8128      0            (NA)
      256       32640     15           (NA)
      512       130816    31           2.067
      1024      523776    63           2.032
      2048      2096128   203          3.222

     */    

  }

}


