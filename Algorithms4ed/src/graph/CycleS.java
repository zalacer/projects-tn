package graph;

import static v.ArrayUtils.*;

import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import st.HashSETint;
import v.Tuple2;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Cycle.java

@SuppressWarnings("unused")
public class CycleS {
  private boolean[] marked;
  private int[] edgeTo;
  private Stack<Integer> cycle;
  private Seq<Stack<Integer>> pe; // parallel edges accumulator
  private int fapecount; // count iterations of findAllParallelEdges inner loop

  public CycleS(GraphS G) {
    if (G == null) throw new IllegalArgumentException("Cycle: graph is null");
    //System.out.print("graph.adj = "); //par(G.adj());
    fapecount = 0;
    pe = new Seq<Stack<Integer>>();
    findAllParallelEdges(G);
    if (!pe.isEmpty()) System.out.println("parallel edges : "+pe);
    else System.out.println("no parallel edges");
//    if (hasParallelEdges(G)) { 
//      System.out.println("hasParallelEdges: "+cycle); cycle = null;
//      pe = new Seq<Stack<Integer>>();
//      findAllParallelEdges(G);
//      if (!pe.isEmpty()) System.out.println(pe);
//    }
    if (hasSelfLoop(G)) { System.out.println("hasSelfLoop: "+cycle); cycle = null; }
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    pe = new Seq<Stack<Integer>>();
    for (int v = 0; v < G.V(); v++)
      if (!marked[v])
        dfs(G, -1, v);
    if (hasCycle())  System.out.println("cycle: "+cycle);
    //par(edgeTo);
  }

  // does this GraphS have a self loop?
  // side effect: initialize cycle to be self loop
  private boolean hasSelfLoop(GraphS G) {
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        if (v == w) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(v);
          return true;
        }
      }
    }
    return false;
  }

  // does this GraphS have two parallel edges?
  // side effect: initialize cycle to be two parallel edges
  private boolean hasParallelEdges(GraphS G) {
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      // check for parallel edges incident to v
      for (int w : G.adj(v)) {
        if (marked[w]) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(w);
          cycle.push(v);
          return true;
        }
        marked[w] = true;
      }
      // reset so marked[v] = false for all v
      for (int w : G.adj(v)) marked[w] = false;
    }
    return false;
  }
  
  public boolean findAllParallelEdges(GraphS G) {
    marked = new boolean[G.V()]; 
    Set<String> done = new HashSet<>();
    StringBuilder sb = new StringBuilder(); String s1,s2;
    for (int v = 0; v < G.V(); v++) {
      // check for parallel edges incident to v excluding duplicates and self-loops
      for (int w : G.adj(v)) {
        fapecount++;
        s1 = sb.delete(0, sb.length()).append(v).append(",").append(w).toString();
        s2 = sb.delete(0, sb.length()).append(w).append(",").append(v).toString();
        if (marked[w] && v != w && !done.contains(s1) && !done.contains(s2)) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(w);
          cycle.push(v);
          pe.add(cycle);
          done.add(s1); done.add(s2);        
        }
        marked[w] = true;
      }
      for (int w : G.adj(v)) marked[w] = false;
    }
    return false;
  }
  
  public int getFapecount() { return fapecount; };

  public boolean hasCycle() { return cycle != null; }

  public Iterable<Integer> cycle() { return cycle; }

  private void dfs(GraphS G, int u, int v) {
    marked[v] = true;
    for (int w : G.adj(v)) {
      // short circuit if cycle already found
      if (cycle != null) return;
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, v, w);
      }
      // check for cycle (but disregard reverse of edge leading to v)
      else if (w != u) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) {
          cycle.push(x);
        }
        cycle.push(w);
        cycle.push(v);
      }
    }
  }

  public static void main(String[] args) {

    In in = new In(args[0]);
    GraphS G = new GraphS(in);
    CycleS finder = new CycleS(G);
    System.out.println(finder.getFapecount());
    System.out.println(G.adj());
//    if (finder.hasCycle()) {
//      StdOut.print("graph has cycle: ");
//      for (int v : finder.cycle()) StdOut.print(v + " "); StdOut.println();
//      if (finder.hasSelfLoop(G)) {
//        System.out.print("graph has self-loop:");
//        for (int v : finder.cycle()) StdOut.print(v + " "); StdOut.println();
//      } else System.out.print("graph doesn't have self-loop:");           
//      if (finder.hasParallelEdges(G)) {
//        System.out.print("graph has parallel edges:");
//        for (int v : finder.cycle()) StdOut.print(v + " "); StdOut.println();
//      } else System.out.print("graph doesn't have parallel edges");           
//    } else StdOut.println("graph is acyclic");           
//    System.out.print("graph.adj = "); par(G.adj());

  }

}
