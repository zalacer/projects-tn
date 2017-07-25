package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import exceptions.InvalidDataException;
import st.ST;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/SymbolGraph.java

@SuppressWarnings("unused")
public class SymbolGraphS2 {
  private ST<String, Integer> st;  // string -> index
  private String[] keys;           // index  -> string
  private GraphS graph;             // the underlying graph

  public SymbolGraphS2(String filename, String delimiter) {
    if (filename == null) throw new IllegalArgumentException("SymbolGraphX: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolGraphX: delimiter is null");
    st = new ST<String, Integer>();
//    int[] q = {0};  Object[] lines = null;
//    final Pattern p = Pattern.compile(delimiter);
    // probably overestimate the number of keys and store input linewise in an Object[]
//    try {
//      lines = Files.lines(Paths.get(filename)).filter(x -> {
//        Matcher m = p.matcher(x); while (m.find()) q[0]++; return true;}).toArray();
//    } catch (IOException e) {
//      e.printStackTrace(); throw new InvalidDataException();
//    }
//    Object[] lines = null;
    graph = new GraphS();
    try {
      Files.lines(Paths.get(filename)).filter(line -> {
        String[] a = ((String)line).split(delimiter);
        if (a.length == 0) return false;
        int v, w;
        if (!st.contains(a[0])) {
          v = st.size();
          st.put(a[0], v);
          for (int i = 1; i < a.length; i++) 
            if (!st.contains(a[i])) {
              w = st.size();
              st.put(a[i], w);
              graph.insertEdge(v, w); 
            } else graph.insertEdge(v, st.get(a[i]));
        } else { // may usually be superfluous but not necessarily
          v = st.get(a[0]);
          for (int i = 1; i < a.length; i++) 
            if (!st.contains(a[i])) {
              w = st.size();
              st.put(a[i], w);
              graph.insertEdge(v, w);
            } else {
              w = st.get(a[i]);
              graph.insertEdge(v, w);
            }
        }
        return false;
      }).count();
    } catch (IOException e) {
      e.printStackTrace(); throw new InvalidDataException();
    }
    // build the graph
//    graph = new GraphS();
////    System.out.println("SymbolGraphS: graph.adj().size()="+graph.adj().size());
////    for (Object line : lines) {
//    for (Object line : lines) {
//        String[] a = ((String)line).split(delimiter);
//        if (a.length == 0) continue;
//        int v, w;
//        if (!st.contains(a[0])) {
//          v = st.size();
//          st.put(a[0], v);
//          for (int i = 1; i < a.length; i++) 
//            if (!st.contains(a[i])) {
//              w = st.size();
//              st.put(a[i], w);
//              graph.insertEdge(v, w); 
//            } else graph.insertEdge(v, st.get(a[i]));
//        } else { // may usually be superfluous but not necessarily
//          v = st.get(a[0]);
//          for (int i = 1; i < a.length; i++) 
//            if (!st.contains(a[i])) {
//              w = st.size();
//              st.put(a[i], w);
//              graph.insertEdge(v, w);
//            } else {
//              w = st.get(a[i]);
//              graph.insertEdge(v, w);
//            }
//        }
//       }
  
    
    graph.trim(); // update graph.V and trim graph.adj
    System.out.println("adj.size="+graph.adj().size()+" V="+graph.V()+" E="+graph.E());
    int V = graph.V();
//    if (c[0] < q[0]) {
//      graph.setV(c[0]);
//      Seq<Seq<Integer>> x = graph.adj();
//      if (x != null) graph.setAdj(x.take(c[0]));
//    }
    StdOut.println("done reading " + filename + " and building SymbolGraph");
    // inverted index to get string keys in an aray
    keys = new String[V];
    for (String name : st.keys()) keys[st.get(name)] = name;
    StdOut.println("done building inverted index");
  }
  
  public SymbolGraphS2(String filename, String delimiter, int y) {
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
    graph = new GraphS(q[0]); int v, w, c = 0;
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
      Seq<Seq<Integer>> x = graph.adj();
      if (x != null) graph.setAdj(x.take(c));
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

  public GraphS graph() { return graph; }

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
