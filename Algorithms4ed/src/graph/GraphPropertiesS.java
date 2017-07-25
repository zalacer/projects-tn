package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import edu.princeton.cs.algs4.In;

@SuppressWarnings("unused")
public class GraphPropertiesS implements GraphPropertiesAPI {
  private int[] eccentricities;
  private int[] centers;
  private int diameter = 0; 
  private int radius = 0;
  private int center = -1;
  private int V = 0;

  public GraphPropertiesS(GraphS G) {
    if (G == null) throw new IllegalArgumentException("GraphPropertiesX : G is null");
    V = G.V();
    eccentricities = new int[V]; centers = new int[V];
    if (V == 0) { diameter = radius = center = -1; return; }
    if (V == 1) { eccentricities[0] = 0; centers[0] = 0; diameter = radius = center = 0; return; }
//    if (G.count() > 1) throw new IllegalArgumentException("GraphPropertiesX : G isn't connected");
//    if ((new CycleX(G)).hasCycle()) throw new IllegalArgumentException(
//        "GraphPropertiesX : G is cyclic");
    for (int i = 0; i < V; i++) 
      eccentricities[i] = (new BreadthFirstPathsS(G,i)).longestPathLength();
    diameter = max(eccentricities);
    radius = min(eccentricities);
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (eccentricities[i] == radius) s.add(i);
    centers = (int[]) s.toPrimitiveArray();
    if (s.size() > 0) center = centers[0];
  }
  
  public int[] eccentricities() { return eccentricities.clone(); }

  public int eccentricity(int v) { 
    if (v < 0 || v > V-1) throw new IllegalArgumentException("eccentricity : v is out of range");
    return eccentricities[v]; 
  }
    
  public int diameter() { return diameter; }
  
  public int radius() { return radius; }
  
  public int[] centers() { return centers.clone(); }
  
  public int center() { return center; }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    GraphS G = new GraphS(in);
    GraphPropertiesS gp = new GraphPropertiesS(GraphGeneratorS.tree(55));
    System.out.println("diameter = "+gp.diameter());
    System.out.println("radius = "+gp.radius());
    System.out.println("center = "+gp.center());
    System.out.print("centers = "); par(gp.centers());
    System.out.print("eccentricities = "); par(gp.eccentricities());


  }

}
