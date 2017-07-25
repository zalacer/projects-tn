package graph;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

@SuppressWarnings("unused")
public class CPMX {

  private CPMX() { }

  public static Seq<Integer> convert(Seq<DirectedEdgeX> edges, int n) {
    // return Seq<Integer> of vertices in edges < n in order
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeX");
      int f = e.from(), t = e.to();
      if (f < n) seq.add(f);
      if (t < n) seq.add(t);
    }
    return seq.uniquePreservingOrder();
  }

  public static Seq<Seq<Integer>> convert2(Seq<Seq<DirectedEdgeX>> edges2, int n) {
    // return unique Seq<Integer>s in edges2 after removing vertices < n preserving
    // the order of remaining vertices and bundled in a Seq<Seq<Integer>> 
    if (edges2 == null) throw new IllegalArgumentException("convert: edges2 is null");
    Seq<Seq<Integer>> seq = new Seq<>();
    for (Seq<DirectedEdgeX> edges : edges2) {
      if (edges == null) continue;
      seq.add(convert(edges,n));
    }
    return seq.unique();
  }

  public static Seq<Seq<Integer>> criticalPaths(Seq<Seq<DirectedEdgeX>> edges, int n) {
    // convert edges to a Seq<Seq<Integer>> by removing vertices < n and return
    // the longest Seq<Integer>s bundled in a Seq<Seq<Integer>>
    if (edges == null) throw new IllegalArgumentException("criticalPaths: edges is null");
    Seq<Seq<Integer>> paths = convert2(edges,n);     
    // 1st find length of longest path
    int max = -1; Seq<Integer> longest = null;
    for (Seq<Integer> p : paths) {
      if (p == null) continue;
      if (longest == null) { longest = p; max = p.size(); }
      else if (p.size()>max) { longest = p; max = p.size(); }
    }
    if (longest == null) return null;
    Seq<Seq<Integer>> o = new Seq<>();
    // accumulate paths of length max in o
    for (Seq<Integer> p : paths) {
      if (p == null) continue;
      if (p.size() == max) o.add(p);
    }
    return o;
  }
  
  public static String seq2String(Seq<Integer> seq) {
    // stringify seq with "->" separators
    if (seq == null) return "(null)";
    if (seq.size() == 0) return "(empty)";
    StringBuilder sb = new StringBuilder();
    sb.append(seq.get(0));
    for (int i = 1; i < seq.size(); i++) sb.append("->"+seq.get(i));
    return sb.toString();   
  }
  
  public static void criticalPaths(String file) {
    // print the critical paths of the jobs defined in file
    In in = new In(file);
    int n = in.readInt(); // number of jobs
    int source = 2*n;
    int sink   = 2*n + 1;
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(2*n + 2);
    for (int i = 0; i < n; i++) {
      double duration = in.readDouble();
      G.addEdge(new DirectedEdgeX(source, i, 0.0));
      G.addEdge(new DirectedEdgeX(i+n, sink, 0.0));
      G.addEdge(new DirectedEdgeX(i, i+n,    duration));
      int m = in.readInt();
      for (int j = 0; j < m; j++) {
        int precedent = in.readInt();
        G.addEdge(new DirectedEdgeX(n+i, precedent, 0.0));
      }
    }
    AcyclicLPX lp = new AcyclicLPX(G, source);
    Seq<Seq<Integer>> criticalPaths = criticalPaths(lp.allPaths(),n);
    if (criticalPaths.size() < 1) {
      System.out.println("\nthere are no critical paths");
      return;
    }
    else if (criticalPaths.size() == 1) System.out.print("\ncritical path: ");
    else System.out.println("\ncritical paths:");
    for (Seq<Integer> seq : criticalPaths) System.out.println(seq2String(seq));
  }

  public static void cpmclient(String file) {
    In in = new In(file);
    int n = in.readInt(); // number of jobs
    int source = 2*n;
    int sink   = 2*n + 1;
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(2*n + 2);
    for (int i = 0; i < n; i++) {
      double duration = in.readDouble();
      G.addEdge(new DirectedEdgeX(source, i, 0.0));
      G.addEdge(new DirectedEdgeX(i+n, sink, 0.0));
      G.addEdge(new DirectedEdgeX(i, i+n,    duration));
      int m = in.readInt();
      for (int j = 0; j < m; j++) {
        int precedent = in.readInt();
        G.addEdge(new DirectedEdgeX(n+i, precedent, 0.0));
      }
    }
    System.out.println("EdgeWeightedDigraphX G:");
    System.out.println(G);
    AcyclicLPX lp = new AcyclicLPX(G, source);
    System.out.println(" job   start  finish");
    System.out.println("--------------------");
    for (int i = 0; i < n; i++) {
      System.out.printf("%4d %7.1f %7.1f\n", i, lp.distTo(i), lp.distTo(i+n));
    }
    System.out.printf("Finish time: %7.1f\n", lp.distTo(sink));
    Seq<DirectedEdgeX> longest = lp.longestPath();
    System.out.println("\nlongestPath = "+longest);
    System.out.println("\nallPaths:");
    for (Seq<DirectedEdgeX> p : lp.allPaths()) System.out.println(p);
    System.out.println("\ncriticalPaths:");
    Seq<Seq<Integer>> criticalPaths = criticalPaths(lp.allPaths(),n);
    for (Seq<Integer> seq : criticalPaths) System.out.println(seq2String(seq));
  }
  
  public static void main(String[] args) {

    criticalPaths("jobsPC.txt");

    //    // number of jobs
    //    int n = StdIn.readInt();
    //
    //    // source and sink
    //    int source = 2*n;
    //    int sink   = 2*n + 1;
    //
    //    // build network
    //    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(2*n + 2);
    //    for (int i = 0; i < n; i++) {
    //      double duration = StdIn.readDouble();
    //      G.addEdge(new DirectedEdgeX(source, i, 0.0));
    //      G.addEdge(new DirectedEdgeX(i+n, sink, 0.0));
    //      G.addEdge(new DirectedEdgeX(i, i+n,    duration));
    //
    //      // precedence constraints
    //      int m = StdIn.readInt();
    //      for (int j = 0; j < m; j++) {
    //        int precedent = StdIn.readInt();
    //        G.addEdge(new DirectedEdgeX(n+i, precedent, 0.0));
    //      }
    //    }
    //
    //    // compute longest path
    //    AcyclicLPX lp = new AcyclicLPX(G, source);
    //
    //    // print results
    //    StdOut.println(" job   start  finish");
    //    StdOut.println("--------------------");
    //    for (int i = 0; i < n; i++) {
    //      StdOut.printf("%4d %7.1f %7.1f\n", i, lp.distTo(i), lp.distTo(i+n));
    //    }
    //    StdOut.printf("Finish time: %7.1f\n", lp.distTo(sink));

  }
}
