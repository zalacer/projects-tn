package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ds.BagX;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import exceptions.InvalidDataException;
import st.ST;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/SymbolGraph.java
// implemented using GraphX and updated SymbolGraphOnePass(String, String) compared
// to SymbolGraphOnePass

@SuppressWarnings("unused")
public class SymbolGraphOnePass { 
  private ST<String, Integer> st;  // string -> index
  private String[] keys;           // index  -> string
  private GraphX graph;             // the underlying graph

  public SymbolGraphOnePass(String filename, String delimiter) {
    if (filename == null) throw new IllegalArgumentException("SymbolGraphX: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolGraphX: delimiter is null");
    st = new ST<String, Integer>();
    int[] q = {0}; // counter of number of separators in filename
    Object[] lines = null;
    final Pattern p = Pattern.compile(delimiter);
    // probably overestimate the number of keys and store input linewise in an Object[]
    try {
      lines = Files.lines(Paths.get(filename)).filter(x -> {
        Matcher m = p.matcher(x); while (m.find()) q[0]++; return true;}).toArray();
    } catch (IOException e) {
      e.printStackTrace(); throw new InvalidDataException();
    }
    // build the graph: number of edges will be ok, but probably not the number of vertices
    graph = new GraphX(++q[0]); int v, w, c = 0;
    for (Object line : lines) {
      String[] a = ((String)line).split(delimiter);
      if (a.length == 0) continue;
      if (!st.contains(a[0])) {
        v = st.size();
        st.put(a[0], v); c++;
        for (int i = 1; i < a.length; i++) 
          if (!st.contains(a[i])) {
            w = st.size();
            st.put(a[i], w); c++;
            graph.addEdge(v, w); 
          } else graph.addEdge(v, st.get(a[i]));
      } else { // may usually be superfluous but not necessarily
        v = st.get(a[0]);
        for (int i = 1; i < a.length; i++) 
          if (!st.contains(a[i])) {
            w = st.size();
            st.put(a[i], w); c++;
            graph.addEdge(v, w);
          } else {
            w = st.get(a[i]);
            graph.addEdge(v, w);
          }
      }
    }
    if (c < q[0]) {
      graph.setV(c);
      BagX<Integer>[] x = graph.adj();
      if (x != null) graph.setAdj(take(x,c));
    }
    StdOut.println("done reading " + filename + " and building SymbolGraph");
    // inverted index to get string keys in an aray
    keys = new String[c];
    for (String name : st.keys()) keys[st.get(name)] = name;
    StdOut.println("done building inverted index");
  }
  
  public SymbolGraphOnePass(String filename, String delimiter, int y) {
    // this still uses two passes
    if (filename == null) throw new IllegalArgumentException("SymbolGraphX: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolGraphX: delimiter is null");
    if (y < 0) throw new IllegalArgumentException("SymbolGraphX: y is < 0");   
    st = new ST<String, Integer>();
    final Pattern p = Pattern.compile(delimiter);
    final Pattern p2 = Pattern.compile("\\((\\d{4})\\)");
    final int year = LocalDate.now().getYear();      
    int[] q = {0};  Object[] lines = null;
    // probably overestimate the number of keys and store input linewise in an Object[]
    try {
      lines = Files.lines(Paths.get(filename)).filter(x -> {
        if (filename.equals("movies.txt")) {
          // skip movies more than y years old
          String[] a = x.split(delimiter);
          int date = -1;
          Matcher m2 = p2.matcher(a[0]);
          if (m2.find()) date = new Integer(m2.group(1));
          if (date != -1 && year - date > y) return false;
        }
        Matcher m = p.matcher(x); while (m.find()) q[0]++; return true;}).toArray();
    } catch (IOException e) {
      e.printStackTrace(); throw new InvalidDataException();
    }
    // build the graph: number of edges will be ok, but probably not the number of vertices
    graph = new GraphX(q[0]); int v, w, c = 0;
    for (Object line : lines) {
      String[] a = ((String)line).split(delimiter);
      if (a.length == 0) continue;
      if (!st.contains(a[0])) {
        v = st.size();
        st.put(a[0], v); c++;
        for (int i = 1; i < a.length; i++) 
          if (!st.contains(a[i])) {
            w = st.size();
            st.put(a[i], w); c++;
            graph.addEdge(v, w); 
          } else graph.addEdge(v, st.get(a[i]));
      } else { // may usually be superfluous but not necessarily
        v = st.get(a[0]);
        for (int i = 1; i < a.length; i++) 
          if (!st.contains(a[i])) {
            w = st.size();
            st.put(a[i], w); c++;
            if (!graph.hasEdge(v, w)) graph.addEdge(v, w);
          } else {
            w = st.get(a[i]);
            if (!graph.hasEdge(v, w)) graph.addEdge(v, w);
          }
      }
    }
    if (c < q[0]) {
      graph.setV(c);
      BagX<Integer>[] x = graph.adj();
      if (x != null) graph.setAdj(take(x,c));
    }
    StdOut.println("done reading " + filename + " and building SymbolGraph");
    // inverted index to get string keys in an aray
    keys = new String[c];
    for (String name : st.keys()) keys[st.get(name)] = name;
    StdOut.println("done building inverted index");
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
    
//    String[] vargs = {"movies.txt", "/"};
//    String filename  = vargs[0];
//    String delimiter = vargs[1];
//    SymbolGraphOnePass sg = new SymbolGraphOnePass(filename, delimiter);
//    GraphX G = sg.graph();
//    GraphPropertiesX2 gp = new GraphPropertiesX2(G);
//    System.out.println("diameter = "+gp.diameter());
//    System.out.println("radius = "+gp.radius());
//    System.out.println("center = "+gp.center());
//    System.out.print("centers = "); par(gp.centers());
//    System.out.print("eccentricities = "); par(gp.eccentricities());
//    System.out.println("girth = "+gp.girth());
//    System.out.print("minCycle = "); par(gp.minCycle());
    
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
