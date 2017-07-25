package graph;

import static v.ArrayUtils.*;

import ds.BagX;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// based on http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Cycle.java

@SuppressWarnings("unused")
public class CycleTrace {
  private Boolean[] marked;
  private Integer[] edgeTo;
  private Stack<Integer> cycle;
  private final GraphX g;
  private final int V;
  private int dfsForLoopCount;

  public CycleTrace(GraphX G) {
    if (G == null) throw new IllegalArgumentException("CycleTrace: graph is null");
    g = G; V = g.V();
    System.out.print("graph.adj = "); par(g.adj());
    if (V < 2) { System.out.println("graph is acyclic"); return; }
    if (hasSelfLoop()) { System.out.println("selfLoop: "+cycle); cycle = null; }
    if (hasParallelEdges()) { System.out.println("parallelEdges: "+cycle); cycle = null; }
    marked = new Boolean[V]; edgeTo = new Integer[V];
    System.out.println("cycle identification trace:");
    String l = arrayToString(range(0,V),900,1,1);
    l = l.replaceAll("\\[|\\]","").replaceAll(",", " ");
    System.out.println("               marked[]                   edgeTo[]");
    System.out.println("               "+l+"  "+l);
    dfsForLoopCount = 0;
    for (int v = 0; v < V; v++) if (marked [v] == null) dfs(-1, v);
    if (hasCycle()) System.out.println("cycle: "+cycle);
    else System.out.println("graph is acyclic");
  }
  
  public CycleTrace(GraphX G, String quiet) {
    // quiet mode for performance testing
    if (G == null) throw new IllegalArgumentException("CycleTrace: graph is null");
    g = G; V = g.V();
    if (V < 2) return;
    marked = new Boolean[V]; edgeTo = new Integer[V];
    dfsForLoopCount = 0;
    for (int v = 0; v < V; v++) if (marked [v] == null) dfsQuiet(-1, v);
  }
  
  public int dfsForLoopCount() { return dfsForLoopCount; }
  
  public void zeroDfsForLoopCount() { dfsForLoopCount = 0; }

  // does this GraphX have a self loop?
  // side effect: initialize cycle to be self loop
  private boolean hasSelfLoop() {
    for (int v = 0; v < V; v++) for (int w : g.adj(v))
      if (v == w) { cycle = new Stack<Integer>(v,v); return true; }
    return false;
  }

  // does this GraphX have two parallel edges?
  // side effect: initialize cycle to be two parallel edges
  private boolean hasParallelEdges() {
    marked = new Boolean[V];
    for (int v = 0; v < V; v++) {
      // check for parallel edges incident to v
      for (int w : g.adj(v)) {
        if (marked[w] != null && marked[w]) { 
          cycle = new Stack<Integer>(v,w,v);
          return true;
        }
        marked[w] = true;
      }
      // reset so marked[v] = null || false for all v
      for (int w : g.adj(v)) marked[w] = null;
    }
    return false;
  }

  public boolean hasCycle() { return cycle != null; }

  public Iterable<Integer> cycle() { return cycle; }

  private final void dfs(int u, int v, String...sa) {
    marked[v] = true;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true","T").replaceAll("false","F").replaceAll("\\[|\\]","")
        .replaceAll("null|,"," ");
    String e = arrayToString(edgeTo,900,1,1);
    e = e.replaceAll("\\[|\\]","").replaceAll("null|,"," ").replaceAll("(\\d{2}) ", "$1");
    String s = sa != null && sa.length > 0 ? sa[0] : "";
    System.out.printf("%-14s %"+(V+V+1)+"s  %s\n", s+"dfs("+u+","+v+")", m, e);
    for (int w : g.adj(v)) {
      dfsForLoopCount++;
      // short circuit if cycle already found
      if (cycle != null) return;
      if (marked[w] == null) { 
        edgeTo[w] = v; 
        dfs(v, w, s+" "); 
      }
      // check for cycle (but disregard reverse of edge leading to v)
      else if (w != u) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) cycle.push(x);
        cycle.push(w);
        cycle.push(v);
      }
    }
  }
  
  private final void dfsQuiet(int u, int v) {
    marked[v] = true;
    for (int w : g.adj(v)) {
      dfsForLoopCount++;
      // short circuit if cycle already found
      if (cycle != null) return;
      if (marked[w] == null) { 
        edgeTo[w] = v; 
        dfsQuiet(v, w); 
      }
      // check for cycle (but disregard reverse of edge leading to v)
      else if (w != u) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) cycle.push(x);
        cycle.push(w);
        cycle.push(v);
      }
    }
  }

  public static void main(String[] args) {

//    In in = new In(args[0]);
//    GraphX G = new GraphX(in);
//    CycleTrace finder = new CycleTrace(G);
    
    int h = 1; CycleTrace trace = null;
    System.out.println("h        dfsForLoopCount");
    while (h < 1000) {
      h = 2*h;
      trace = new CycleTrace(GraphGeneratorX.tree(h),"quiet");
      System.out.printf("%-7d  %-7d\n", h, trace.dfsForLoopCount); 
      if (trace.hasCycle()) { System.out.println("cycle detected"); return; }
    }
    
/*
path:
h        dfsForLoopCount
2        2      
4        6      
8        14     
16       30     
32       62     
64       126    
128      254    
256      510    
512      1022   
1024     2046

tree:
h        dfsForLoopCount
2        2      
4        6      
8        14     
16       30     
32       62     
64       126    
128      254    
256      510    
512      1022   
1024     2046   



*/  }

}
