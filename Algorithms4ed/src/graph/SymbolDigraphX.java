package graph;

import static v.ArrayUtils.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import analysis.ArithmeticExpressionEvaluation.Node;

import static graph.AllTopologicalSorts.allTopologicalSorts;

import ds.BagX;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import st.ST;
import st.SeparateChainingHashSTX;

// from http://algs4.cs.princeton.edu/42digraph/SymbolDigraph.java

/*  %  java SymbolDigraph routes.txt " "
 *  JFK
 *     MCO
 *     ATL
 *     ORD
 *  ATL
 *     HOU
 *     MCO
 *  LAX */

@SuppressWarnings("unused")
public class SymbolDigraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private ST<String, Integer> st;  // string -> index
  private String[] keys;           // index  -> string
  private DigraphX graph;           // the underlying digraph
  private KosarajuSharirSCCX scc;

  /**  
   * Initializes a digraph from a file using the specified delimiter.
   * Each line in the file contains
   * the name of a vertex, followed by a list of the names
   * of the vertices adjacent to that vertex, separated by the delimiter.
   * @param filename the name of the file
   * @param delimiter the delimiter between fields
   */
  public SymbolDigraphX(String filename, String delimiter) {
    if (filename == null) throw new IllegalArgumentException("SymbolDigraph: filename is null");
    if (delimiter == null) throw new IllegalArgumentException("SymbolDigraph: delimiter is null");
    st = new ST<String, Integer>();
    // First pass builds the index by reading strings to associate
    // distinct strings with an index
    In in = new In(filename);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      for (int i = 0; i < a.length; i++) {
        if (!st.contains(a[i]))
          st.put(a[i], st.size());
      }
    }
    // inverted index to get string keys in an aray
    keys = new String[st.size()];
    for (String name : st.keys()) {
      keys[st.get(name)] = name;
    }
    // second pass builds the digraph by connecting first vertex on each
    // line to all others
    graph = new DigraphX(st.size());
    in = new In(filename);
    while (in.hasNextLine()) {
      String[] a = in.readLine().split(delimiter);
      int v = st.get(a[0]);
      for (int i = 1; i < a.length; i++) {
        int w = st.get(a[i]);
        graph.addEdge(v, w);
      }
    }
    scc = new KosarajuSharirSCCX(graph);
  }
  
  public SymbolDigraphX(Map<String,Seq<String>> jda) {
    // for ex4229, see analysis.ArithmeticExpressionEvaluation
    if (jda == null) throw new IllegalArgumentException("SymbolDigraph: jda is null");
    if (jda.isEmpty()) throw new IllegalArgumentException("SymbolDigraph: jda is empty");
    st = new ST<String, Integer>();
    String[] syms = jda.keySet().toArray(new String[0]);
    for (String k : syms) if (!st.contains(k)) st.put(k, st.size());
        
    // inverted index to get string keys in an aray
    keys = new String[st.size()];
    for (String name : st.keys()) {
      keys[st.get(name)] = name;
    }
    // second pass builds the digraph by connecting each key to all
    // the vertices in its list
    graph = new DigraphX(st.size());
    for (String k : keys)  {
      int v = st.get(k);
      for (String s : jda.get(k)) {
        int w = st.get(s);
        graph.addEdge(v, w);
      }
    }
    scc = new KosarajuSharirSCCX(graph);
  }
  
  public SymbolDigraphX(String formula) {
    // formula must be a 2-CNF formula using "!" for not and "||" for or and "&&" for and
    // for example: "(x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4)"
    // where the literal symbols x0, x1, x2, x3, x4 are arbitrary
    // for ex4226
    if (formula == null) throw new IllegalArgumentException("formula is null");
    st = new ST<String, Integer>();
    // first pass builds the index from distinct string symbols
    String f = formula.replaceAll("\\(|\\)|\\||&|\\!"," ").replaceAll("\\s+"," ").trim();
    String[] symbols = unique(f.split("\\s+"));
    Arrays.sort(symbols);
    for (int i = 0; i < symbols.length; i++)
      if (!st.contains(symbols[i])) st.put(symbols[i], st.size());
    // add the negated symbols
    for (int i = 0; i < symbols.length; i++) {
      String sym = "!"+symbols[i];
      if (!st.contains(sym)) st.put(sym, st.size());
    }
    // inverted index to get string keys in an array
    keys = new String[st.size()];
    for (String name : st.keys()) keys[st.get(name)] = name;
    // second pass builds the digraph by adding edges
    graph = new DigraphX(st.size());
    String[] pairs = formula.replaceAll("\\|\\|",",").replaceAll("\\s+"," ").split("\\&&");
    for (String s : pairs) {
      String[] c = s.replaceAll("\\(|\\)|\\s+","").split(",");
      if (c.length < 2) continue;
      String x = c[0], y = c[1];
      String nx = x.startsWith("!") ? x.substring(1,x.length()) : "!"+x;
      String ny = y.startsWith("!") ? y.substring(1,y.length()) : "!"+y;
     graph.addEdge(st.get(ny), st.get(x));
     graph.addEdge(st.get(nx), st.get(y));
    }
    scc = new KosarajuSharirSCCX(graph);
  }

  public boolean contains(String s) { return st.contains(s); }

  public int index(String s) { return st.get(s); }

  public int indexOf(String s) { return st.get(s); }
  
  public String key(int v) { validateVertex(v); return keys[v]; }
  
  public String[] keys() { return keys; }

  public String name(int v) { validateVertex(v); return keys[v]; }

  public String nameOf(int v) { validateVertex(v); return keys[v]; }

  public DigraphX G() { return graph;  }

  public DigraphX digraph() { return graph; }
  
  public void rebuildSCC() { scc = new KosarajuSharirSCCX(graph); }
  
  public Seq<Seq<String>> components() {
    Seq<Seq<Integer>> c = scc.components();
    Seq<Seq<String>> seq = new Seq<>();
    for (Seq<Integer> s : c) {
      Seq<String> ss = new Seq<>();
      for (int i : s) ss.add(keys[i]);
      seq.add(ss);
    } 
    return seq;
  }

  private void validateVertex(int v) {
    int V = graph.V();
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  @Override
  public String toString() {
    DigraphX g = G(); int E = g.E(), V = g.V();
    BagX<Integer>[] adj = g.adj();
    StringBuilder s = new StringBuilder();
    s.append(V + " vertices, " + E + " edges " + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(String.format("%s: ", keys[v]));
      for (int w : adj[v]) {
        s.append(String.format("%s ", keys[w]));
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

  public static void main(String[] args) {
    
    boolean x0,x1,x2,x3,x4,x5,x6; x0=x1=x2=x3=x4=x5=x6=false;
        
    boolean expression = (x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4)
        && (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6);
    
    System.out.println("expression = "+expression);
    
    String formula = "(x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4) "
        + "&& (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6)";
    
//    formula = "(x0 | x2) & (x0 |!x3) & (x1 |!x3)";
//    formula = "(x0 | x2) & (x1 |!x2)";
    //!x0 x2 !x1 !x5 !x4 !x3 x6
    //x6,!x3,!x4,!x5,!x1,x2,!x0
    SymbolDigraphX sg = new SymbolDigraphX(formula);
//    System.out.println(sg);
    DigraphX g = sg.G(); BagX<Integer>[] adj = g.adj();
//    System.out.println("Digraph"); System.out.println(g);
    Seq<Seq<String>> sgcomponents = sg.components();
    Seq<Integer> topOrder;
    // test if g is a DAG
    DirectedCycleX finder = new DirectedCycleX(g);
    if (finder.hasCycle()) {
      TopologicalX topological = new TopologicalX(g);
      topOrder = new Seq<>(toArray(topological.order().iterator()));
    }
//    System.out.println("topologicalOrder:");
//    TopologicalX topological = new TopologicalX(g);
//    for (int v : topological.order()) System.out.print(sg.key(v)+" "); System.out.println();
//    Seq<Seq<String>> sgcomponents = sg.components();
//    int sccCount = sgcomponents.size();
//    System.out.println("sccCount="+sccCount);
//    if (sccCount == sg.graph.V()) {
//      System.out.println("each vertex is in a separate SCC");
//    }
//    System.out.println("sgcomponents:");
//    for(Seq<String> s : sgcomponents) System.out.print(s+" "); System.out.println();
    else {
      // test that no component and its negation are in the same strong component
      for (Seq<String> seq : sgcomponents) {
        if (seq.size() < 2) continue;
        for (String s : seq) {
          if (s.startsWith("!")) {
            String s2 = s.substring(1,s.length());
            if (seq.contains(s2)) {
              System.out.println("the formula isn't satisfieable");
              return;
            } 
          } else {
            String s2 = "!"+s;
            if (seq.contains(s2)) {
              System.out.println("the formula isn't satisfieable");
              return;
            } 
          }
        }
      }
      // construct condensation (AKA kernel) graph (DAG) of components in Digraph g
      // https://en.wikipedia.org/wiki/2-satisfiability#Problem_representations    
      Seq<Seq<Integer>> components = (new KosarajuSharirSCCX(g)).components();
      DigraphX condensation = new DigraphX(components.size());
      for (int i = 0; i < components.size()-1; i++) {
        Seq<Integer> c = components.get(i);
        for (int j : c) {
          for (int k = i+1; k < components.size(); k++) {
            Seq<Integer> c2 = components.get(k);
            for (int j2 : c2) {
              if (adj[j].contains(j2)) condensation.addEdge(i,k);
              if (adj[j2].contains(j)) condensation.addEdge(k,i);
            }
          }
        }  
      }
      TopologicalX topological = new TopologicalX(condensation);
//      System.out.println("condensation topological order");
//      for (int v : topological.order()) System.out.print(keys[v]+" "); System.out.println();
//      for (int v : topological.order()) System.out.print(v+" "); System.out.println();
      // a satisfying assignment
      topOrder = new Seq<>(toArray(topological.order().iterator()));
    }
    Seq<Integer> reverseOrder = topOrder.reverse();
//    System.out.println("reverseOrder="+reverseOrder);
    String[] keys = new String[sgcomponents.size()];
    for (int i = 0; i < keys.length; i++) keys[i] = sgcomponents.get(i).get(0);
    Seq<String> reverseTop = new Seq<>();
    for (int i : reverseOrder) reverseTop.add(keys[i]);
//    System.out.println("reverseTop="+reverseTop);
    SeparateChainingHashSTX<String,Boolean> map = new SeparateChainingHashSTX<>();
//    System.out.println("a satisfying assignment:");
    for (String s : reverseTop) {
      String r = s.startsWith("!") ? s.substring(1,s.length()) : "!"+s; 
      if (!(map.contains(s) || map.contains(r))) {
        map.put(s,true);
        if (s.startsWith("!")) map.put(s.substring(1,s.length()),false);
        else map.put("!"+s,false);
      }
    }
    Comparator<String> cmptr = (s1,s2) -> {
      if (s1.startsWith("!") && !s2.startsWith("!")) return 1;
      if (!s1.startsWith("!") && s2.startsWith("!")) return -1;
      return s1.compareTo(s2);
    };
    String[] mkeys = toArray(map.keys().iterator());
    Arrays.sort(mkeys,cmptr);
    mkeys = take(mkeys,mkeys.length/2);
    for (String k : mkeys) {
      boolean b = map.get(k);
      switch(k) {
        case "x0":  x0 = b;  break;
        case "x1":  x1 = b;  break;
        case "x2":  x2 = b;  break;
        case "x3":  x3 = b;  break;
        case "x4":  x4 = b;  break;
        case "x5":  x5 = b;  break;
        case "x6":  x6 = b;  break;
        case "!x0": x0 = !b; break;
        case "!x1": x1 = !b; break;
        case "!x2": x2 = !b; break;
        case "!x3": x3 = !b; break;
        case "!x4": x4 = !b; break;
        case "!x5": x5 = !b; break;
        case "!x6": x6 = !b; break;
      }
//      System.out.println(k+" "+map.get(k));
    }
    
    System.out.println();
    System.out.println("a solution:");
    System.out.println("x0 = "+x0);
    System.out.println("x1 = "+x1);
    System.out.println("x2 = "+x2);
    System.out.println("x3 = "+x3);
    System.out.println("x4 = "+x4);
    System.out.println("x5 = "+x5);
    System.out.println("x6 = "+x6);
    
//    System.out.println("(x0 || x2) = "+(x0 || x2));
//    System.out.println("(x0 ||!x3) = "+(x0 ||!x3));
//    System.out.println("(x1 ||!x3) = "+(x1 ||!x3));
//    System.out.println("(x1 ||!x4) = "+(x1 ||!x4));
//    System.out.println("(x2 ||!x4) = "+(x2 ||!x4));
//    System.out.println("(x0 ||!x5) = "+(x0 ||!x5));
//    System.out.println("(x1 ||!x5) = "+(x1 ||!x5));
//    System.out.println("(x2 ||!x5) = "+(x2 ||!x5));
//    System.out.println("(x3 || x6) = "+(x3 || x6));
//    System.out.println("(x4 || x6) = "+(x4 || x6));
//    System.out.println("(x5 || x6) = "+(x5 || x6));

    expression = (x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4)
        && (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6);
    System.out.println("\nproof of the solution:");
    System.out.println("expression = "+expression);
 
    

//    Seq<String> satisfiableAssignment = sg.SatisfiableAssignment();
//    System.out.println("satisfiableAssignment="+satisfiableAssignment);
//    Seq<Seq<Integer>> allTopologicalSorts = allTopologicalSorts(g);
//    System.out.println("allTopologicalSorts:"); 
//    for (Seq<Integer> s : allTopologicalSorts) {
//      for (int i : s) System.out.print(sg.key(i)+" "); System.out.println();
//    }
  
//    String filename  = args[0];
//    String delimiter = args[1];
//    SymbolDigraphX sg = new SymbolDigraphX(filename, delimiter);
//    DigraphX graph = sg.digraph();
//    while (!StdIn.isEmpty()) {
//      String t = StdIn.readLine();
//      for (int v : graph.adj(sg.index(t))) {
//        StdOut.println("   " + sg.name(v));
//      }
//    }
  }
}
