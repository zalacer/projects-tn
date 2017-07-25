package graph;

import static v.ArrayUtils.*;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import graph.GraphPropertiesX2;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import st.ST;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/SymbolGraph.java
// implemented using GraphX instead of Graph

@SuppressWarnings("unused")
public class SymbolGraphX {
  private ST<String, Integer> st;  // string -> index
  private String[] keys;           // index  -> string
  private GraphX graph;             // the underlying graph

  public SymbolGraphX(String filename, String delimiter) {
    if (filename == null) throw new IllegalArgumentException("SymbolGraphX: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolGraphX: delimiter is null");
    st = new ST<String, Integer>();
    // First pass builds the index by reading strings to associate
    // distinct strings with an index
    In in = new In(filename);
    while (!in.isEmpty()) {
      String[] a = in.readLine().split(delimiter);
      for (int i = 0; i < a.length; i++) if (!st.contains(a[i])) st.put(a[i], st.size());
    }
    StdOut.println("Done reading " + filename);
    // inverted index to get string keys in an aray
    keys = new String[st.size()];
    for (String name : st.keys()) keys[st.get(name)] = name;
    // second pass builds the graph by connecting first vertex on each
    // line to all others
    graph = new GraphX(st.size());
    in = new In(filename);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      int v = st.get(a[0]);
      for (int i = 1; i < a.length; i++) graph.addEdge(v, st.get(a[i]));
    }
  }
  
  public SymbolGraphX(String filename, String delimiter, int y) {
    if (filename == null) throw new IllegalArgumentException("SymbolGraphX: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolGraphX: delimiter is null");
    if (y < 0) throw new IllegalArgumentException("SymbolGraphX: y is < 0");   
    st = new ST<String, Integer>();
    // First pass builds the index by reading strings to associate
    // distinct strings with an index
    In in = new In(filename);
    Pattern p = Pattern.compile("\\((\\d{4})\\)");
    Matcher m; int date, year = LocalDate.now().getYear();  
    while (!in.isEmpty()) {
      String[] a = in.readLine().split(delimiter);
      for (int i = 0; i < a.length; i++) {
        if (filename.equals("movies.txt")) {
          // skip movies more than y years old
          date = -1;
          m = p.matcher(a[i]);
          if (m.find()) date = new Integer(m.group(1));
          if (date != -1 && year - date > y) continue;
        }
        if (!st.contains(a[i])) st.put(a[i], st.size());
      }
    }
    StdOut.println("Done reading " + filename);
    // inverted index to get string keys in an aray
    keys = new String[st.size()];
    for (String name : st.keys()) keys[st.get(name)] = name;
    // second pass builds the graph by connecting first vertex on each
    // line to all others
    graph = new GraphX(st.size());
    in = new In(filename);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      Integer v = st.get(a[0]); if (v == null) continue;
      for (int i = 1; i < a.length; i++) graph.addEdge(v, st.get(a[i]));
    }
  }

  public boolean contains(String s) { return st.contains(s); }

  public int index(String s) { return st.get(s); }
  
  public int indexOf(String s) { return st.get(s); }

  public String name(int v) { validateVertex(v); return keys[v]; }
  
  public String nameOf(int v) { validateVertex(v); return keys[v]; }

  public GraphX graph() { return graph; }

  private void validateVertex(int v) {
    int V = graph.V();
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
  }

  public static void main(String[] args) {
    
    String[] vargs = {"movies.txt", "/"};
    String filename  = vargs[0];
    String delimiter = vargs[1];
    SymbolGraphX sg = new SymbolGraphX(filename, delimiter);
    GraphX G = sg.graph();
    GraphPropertiesX2 gp = new GraphPropertiesX2(G);
    System.out.println("diameter = "+gp.diameter());
    System.out.println("radius = "+gp.radius());
    System.out.println("center = "+gp.center());
    System.out.print("centers = "); par(gp.centers());
    System.out.print("eccentricities = "); par(gp.eccentricities());
    System.out.println("girth = "+gp.girth());
    System.out.print("minCycle = "); par(gp.minCycle());
    
//    while (StdIn.hasNextLine()) {
//      String source = StdIn.readLine();
//      if (sg.contains(source)) {
//        int s = sg.index(source);
//        for (int v : G.adj(s)) {
//          StdOut.println("   " + sg.name(v));
//        }
//      }
//      else {
//        StdOut.println("input not contain '" + source + "'");
//      }
//    }
  }

}
