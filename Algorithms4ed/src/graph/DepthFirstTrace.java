package graph;

import static v.ArrayUtils.*;

import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// based on http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DepthFirstSearch.java

@SuppressWarnings("unused")
public class DepthFirstTrace {
  private Boolean[] marked; // marked[v] = is there an s-v path?
  private Integer[] edgeTo;     // last vertex on known path to this vertex
  private int s;
  private int last;         // track last on path from s
  private int count;        // number of vertices connected to s

  public DepthFirstTrace(GraphX G, int s) {
    if (G == null) throw new IllegalArgumentException("DepthFirstTrace: graph is null");
    if (s < 0) throw new IllegalArgumentException("DepthFirstTrace: s is < 0");
    this.s = s;  last = s;
    System.out.println("adj: "+arrayToString(G.adj(),900,1,1));
    marked = new Boolean[G.V()]; edgeTo = new Integer[G.V()];
    System.out.println("\n            marked[]");
    String l = arrayToString(range(0,G.V()),900,1,1);
    l = l.replaceAll("\\[|\\]","").replaceAll(",", " ");
    System.out.println("            "+l);
    validateVertex(s); dfs(G, s);
    System.out.println("\nedgeTo: "+arrayToString(edgeTo,900,1,1));
    System.out.print("\npaths:"); boolean first = true;
    for (int i = 0; i < marked.length; i++) {
      if (marked[i] != null && marked[i]) {
        String p = pathTo(i).toString().replaceAll(",", "-").replaceAll("\\(|\\)", "");
        if (first) {
          System.out.printf("  %-8s %s\n", s+" to "+i+":", p);
          first = false;
        } else System.out.printf("        %-8s %s\n", s+" to "+i+":", p);
      }
    }
  } 

  private void dfs(GraphX G, int v, String...sa) {
    count++; marked[v] = true;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true","T").replaceAll("false","F").replaceAll("\\[|\\]","")
        .replaceAll("null|,"," ");
    String s = sa != null && sa.length > 0 ? sa[0] : "";
    System.out.printf("%-11s %s\n", s+"dfs("+v+")", m);
    for (int w : G.adj(v)) if (marked[w] == null) { last = w; edgeTo[w] = v; dfs(G, w, s+" "); }   
  }
  
  private void dfs2(GraphX G, int v) {
    count++; marked[v] = true;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true", "T").replaceAll("false", "F");
    System.out.printf("%-7s %s\n", "dfs("+v+")", m);
    for (int w : G.adj(v)) 
      if (!marked[w]) { last = w; edgeTo[w] = v; dfs2(G, w); }   
  }
  
  public boolean marked(int v) { validateVertex(v); return marked[v] != null; }
  
  public int count() { return count; }
  
  public boolean hasPathTo(int v) { return marked(v); }
  
  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != s; x = edgeTo[x])  path.push(x);
    path.push(s);
    return path;
  }

  private void validateVertex(int v) {
    int V = marked.length; if (v<0||v>=V) 
      throw new IllegalArgumentException("vertex "+v+" isn't between 0 and "+(V-1)); }
  
  public static void trace(String file, int start) {
    if (file == null) throw new IllegalArgumentException("depthFirstTrace: file is null");
    if (start < 0) throw new IllegalArgumentException("depthFirstTrace: start is < 0");
    In in = new In(file);
    GraphX G = new GraphX(in);
    new DepthFirstTrace(G, start);
  }

  public static void main(String[] args) {
    
    trace("tinyGex2.txt",0);
//    trace("tinyCG2.txt",0);
    
    
//    In in = new In(args[0]);
//    GraphX G = new GraphX(in);
////    System.out.println(G);
//    int s = Integer.parseInt(args[1]);
////    DepthFirstSearchTrace search = new DepthFirstSearchTrace(G, s);
//    new DepthFirstTrace(G, s);
////    for (int v = 0; v < G.V(); v++) {
////      if (search.marked(v))
////        StdOut.print(v + " ");
////    }
////    StdOut.println();
////    if (search.count() != G.V()) StdOut.println("NOT connected");
////    else                         StdOut.println("connected");
  }

}
