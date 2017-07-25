package graph;

import static v.ArrayUtils.*;

import java.util.Iterator;

// based on
//   http://www.sanfoundry.com/java-program-check-if-given-adj-contain-hamiltonian-cycle-not/

// uses a VxV adj int[][] filled with 0s and 1s where adj[x,y] == 1 means that vertex x has
// a path to vertex y and adj[x,y] == 0 means that x doesn't have a path to y. the conversion 
// from a GraphX adj array to this type of adj array can be done with GraphX.hArray();

public class HamiltonianCycleX {
  private int V, pathCount;
  private int[]  path;
  private int[][] adj; // note this isn't the same type of adj[] used by GraphX
  private boolean r = false;

  public void findHamiltonianCycle(int[][] g)  {
    if (g == null) throw new IllegalArgumentException("findHamiltonianCycle: g is null");
    V = g.length;
    for (int[] a : g) if (a.length != V) throw new IllegalArgumentException(
        "findHamiltonianCycle: g doesn't have matrix VxV dimensions");
    path = fillInt(V, () -> -1);
    adj = g;
    try {
      path[0] = 0; pathCount = 1; solve(0);  r = false;
    } catch (Exception e) { r = true; }
  }

  public void solve(int vertex) throws Exception  {
    // attempts to fill path[] with all vertices
    if (adj[vertex][0] == 1 && pathCount == V) throw new Exception("Solution found");
    if (pathCount == V) return;
    for (int v = 0; v < V; v++) {
      if (adj[vertex][v] == 1) {
        path[pathCount++] = v;
        adj[vertex][v] = adj[v][vertex] = 0;       
        if (!isPresent(v)) solve(v);
        adj[vertex][v] = adj[v][vertex] = 1;
        path[--pathCount] = -1;
      }
    }
  }

  public boolean isPresent(int v)  {
    // return true is v is already in path[] else return false
    for (int i = 0; i < pathCount - 1; i++) if (path[i] == v) return true;
    return false;
  }
  
  public boolean hasHamiltonianCycle() { return r; }
  
  public int[] cycleToArray() {
    int[] a = new int[V+1];
    for (int i = 0; i <= V; i++) a[i] = path[i % V];
    return a;
  }
  
  public Iterator<Integer> cycleIterator() { return iterator(cycleToArray()); }
  
  public Iterable<Integer> cycle() { return () -> iterator(cycleToArray()); }
  
  public String cycleToString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= V; i++) sb.append(path[i % V] + " ");
    return sb.toString();
  }  
  
  public static void main(String[] args) {

    HamiltonianCycleX h = new HamiltonianCycleX();
    GraphX g = GraphGeneratorX.wheel(4);
    String gs = "GraphGeneratorX.wheel(4)";
//    int[][] adj = g.hArray();
//    par(adj);
    h.findHamiltonianCycle(g.hArray());
    boolean b = h.hasHamiltonianCycle();
    if (b) System.out.println(gs+" has hamiltonian cycle: "+h.cycleToString());
    else System.out.println(gs+" doesn't have a hamiltonian cycle");

  }
  
}
