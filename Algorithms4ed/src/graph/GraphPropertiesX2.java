package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.BreadthFirstPathsX;
import graph.CycleX;
import graph.GraphPropertiesAPI2;
import graph.GraphX;

@SuppressWarnings("unused")
public class GraphPropertiesX2 implements GraphPropertiesAPI2 {
  private static final int INFINITY = Integer.MAX_VALUE;
  private int[] eccentricities;
  private int[] centers;
  private int diameter = -1; 
  private int radius = -1;
  private int center = -1;
  private int girth = INFINITY;
  private int[] minCycle = new int[0];
  private int V = 0;

  public GraphPropertiesX2(GraphX G) {
    if (G == null) throw new IllegalArgumentException("GraphPropertiesX : G is null");
    V = G.V();
    eccentricities = new int[V]; centers = new int[V];
    if (V == 0)  return;
    if (V == 1) { 
      eccentricities[0] = 0; centers[0] = 0; 
      diameter = radius = center = 0; 
      return; 
    }
    // if (G.count() > 1) throw new IllegalArgumentException("GraphPropertiesX : G isn't connected");
    boolean cyclic = false;
    if ((new CycleX(G)).hasCycle()) cyclic = true;
    BreadthFirstPathsX b = null; int[] p = null, cycle = null; int min = INFINITY;
    for (int i = 0; i < V; i++) {
      b = new BreadthFirstPathsX(G,i);
      eccentricities[i] = b.longestPathLength(); 
      if (cyclic) {
        for (int j = i; j < V; j++) {
          p = b.arrayPathTo(j);
          if (G.adj()[j].contains(i) && p.length < min) {
            min = p.length; cycle = append(p,i);
          }
        } 
      }
    }
    diameter = max(eccentricities);
    radius = min(eccentricities);
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (eccentricities[i] == radius) s.add(i);
    centers = (int[]) s.toPrimitiveArray();
    if (s.size() > 0) center = centers[0];
    if (cycle != null) { girth = min; minCycle = cycle; }
  }
  
  public int[] eccentricities() { return eccentricities.clone(); }

  public int eccentricity(int v) { 
    if (v < 0 || v > V-1) throw new IllegalArgumentException("eccentricity : v is out of range");
    return eccentricities[v]; 
  }
    
  public int diameter() { return diameter; }
  
  public int radius() { return radius; }
  
  public int center() { return center; }
  
  public int[] centers() { return centers.clone(); }
  
  public int girth() { return girth; }

  public int[] minCycle() { return minCycle.clone(); }

  public static void main(String[] args) {
    
//    // this demonstrates detection of a self-loop for girth == 1
//    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
//    GraphX G1 = new GraphX(12, edges);   
//    GraphPropertiesX2 gp = new GraphPropertiesX2(G1);
//    System.out.println("graph properties for graph G1:");
//    System.out.println("diameter = "+gp.diameter());
//    System.out.println("radius = "+gp.radius());
//    System.out.println("center = "+gp.center());
//    System.out.print("centers = "); par(gp.centers());
//    System.out.print("eccentricities = "); par(gp.eccentricities());
//    System.out.println("girth = "+gp.girth());
//    System.out.print("minCycle = "); par(gp.minCycle());
//    System.out.print("G1.adj = "); par(G1.adj());
//    System.out.println();
//    
//    GraphX G2 = GraphGeneratorX.simple(9,.5);
//    gp = new GraphPropertiesX2(G2);
//    System.out.println("graph properties for graph G2:");
//    System.out.println("diameter = "+gp.diameter());
//    System.out.println("radius = "+gp.radius());
//    System.out.println("center = "+gp.center());
//    System.out.print("centers = "); par(gp.centers());
//    System.out.print("eccentricities = "); par(gp.eccentricities());
//    System.out.println("girth = "+gp.girth());
//    System.out.print("minCycle = "); par(gp.minCycle());
//    System.out.print("G2.adj = "); par(G2.adj());
//    System.out.println();


  }

}
