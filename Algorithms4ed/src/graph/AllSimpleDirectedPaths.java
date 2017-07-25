package graph;

import java.util.Comparator;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// based on http://algs4.cs.princeton.edu/41graph/AllPaths.java


public class AllSimpleDirectedPaths {
  private boolean[] onPath;        // vertices in current path
  private Stack<Integer> path;     // the current path
  private Seq<Seq<Integer>> allPaths;
  private int numberOfPaths;       // number of simple paths

  public AllSimpleDirectedPaths(DigraphX G, int s, int t) {
    if (G == null) throw new IllegalArgumentException(
        "AllSimpleDirectedPaths: DigraphX is null");
    int V = G.V(); 
    if (s < 0 || s > V-1) throw new IllegalArgumentException(
        "AllSimpleDirectedPaths: int s is out of bounds");
    if (t < 0 || t > V-1) throw new IllegalArgumentException(
        "AllSimpleDirectedPaths: int t is out of bounds");
    onPath = new boolean[G.V()];
    path   = new Stack<Integer>();
    allPaths = new Seq<>();
    dfs(G, s, t);
  }

  private void dfs(DigraphX G, int v, int t, int...r) {
    // add v to current path
    path.push(v);
    onPath[v] = true;
    // found path from s to t
    if (v == t) {
      processCurrentPath();
      numberOfPaths++;
    }
    // consider all neighbors that would continue path without repeating a node
    else {
      for (int w : G.adj(v)) {
        if (!onPath[w])
          dfs(G, w, t, 1);
      }
    }
    // done exploring from v, so remove from path
    path.pop();
    onPath[v] = false;
  }

  // this implementation just prints the path to standard output
  private void processCurrentPath() {
    Stack<Integer> reverse = new Stack<Integer>();
    for (int v : path) reverse.push(v);
    allPaths.add(new Seq<Integer>(reverse.toArray()));
//    if (reverse.size() >= 1)
//      StdOut.print(reverse.pop());
//    while (!reverse.isEmpty())
//      StdOut.print("-" + reverse.pop());
//    StdOut.println();
  }

  // return number of simple paths between s and t
  public int numberOfPaths() { return numberOfPaths; }
  
  Seq<Seq<Integer>> allPaths() { return allPaths; }
  
  Seq<Integer> longestPath() {
    if (allPaths.size() == 0) {
      Seq<Integer> seq = new Seq<Integer>(); seq.add(-1); return seq;    
    }
//    if (allPaths.size() == 1 && allPaths.get(0).size() == 0) {
//      Seq<Integer> seq = new Seq<Integer>(); seq.add(-2); return seq;   
//    }
    Comparator<Seq<Integer>> comp = (s1,s2) -> { return s1.size() - s2.size(); };
    return allPaths.max(comp);
  }
  
  public int height() {
    if (allPaths() == null) return -1;
    Seq<Integer> lp = longestPath();
    if (lp.size() == 1 && lp.get(0) < 0) return lp.get(0);
    return longestPath().size()-1;
  }
  
  public static int height(DigraphX G, int v, int t) {
    // return the length of the longest path from v to t in G if possible
    return (new AllSimpleDirectedPaths(G,v,t)).height();
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
//    DirectedCycleX cyclefinder = new DirectedCycleX(d);
//    if (cyclefinder.hasCycle()) throw new IllegalArgumentException("DigraphX isn't a DAG");
    System.out.println(d);
    AllSimpleDirectedPaths allpaths = new AllSimpleDirectedPaths(d, 0, 4);
    System.out.println("# paths = " + allpaths.numberOfPaths());
    System.out.println("\nallPaths:");
    Seq<Seq<Integer>> paths = allpaths.allPaths();
    for (Seq<Integer> s : paths) System.out.println(s);
    System.out.println("longestPath="+allpaths.longestPath());
    System.out.println("height="+allpaths.height());
    System.exit(0);
    
    DigraphX G = new DigraphX(7);
    G.addEdge(0, 1);
    G.addEdge(0, 2);
    G.addEdge(2, 3);
    G.addEdge(3, 4);
    G.addEdge(2, 5);
    G.addEdge(1, 5);
    G.addEdge(5, 4);
    G.addEdge(3, 6);
    G.addEdge(4, 6);
    StdOut.println(G);

    StdOut.println();
    StdOut.println("all simple paths between 0 and 6:");
    AllSimpleDirectedPaths allpaths1 = new AllSimpleDirectedPaths(G, 0, 6);
    StdOut.println("# paths = " + allpaths1.numberOfPaths());

//    StdOut.println();
//    StdOut.println("all simple paths between 1 and 5:");
//    AllSimpleDirectedPaths allpaths2 = new AllSimpleDirectedPaths(G, 1, 5);
//    StdOut.println("# paths = " + allpaths2.numberOfPaths());
  }


}

