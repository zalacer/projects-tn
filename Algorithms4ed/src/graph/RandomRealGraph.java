package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Comparator;

import ds.Seq;
import exceptions.InvalidDataException;
import java.util.Arrays;
import st.HashSET;
import st.SeparateChainingHashSTX;
import v.Tuple2;

// for ex4144
// using data from https://snap.stanford.edu/data/higgs-reply_network.edgelist.gz
// downloaded from https://snap.stanford.edu/data/higgs-twitter.html.

public class RandomRealGraph {

  public static Tuple2<String[],Tuple2<String,String>[]> readData(String f) {
    // f is a file pathname with possible per line comments beginning with "#" 
    // that are skipped and remaining lines containing at least 2 space-separated 
    // keys per line, followed by a weight that's ignored.
    if (f == null) throw new IllegalArgumentException("readData: f is null");
    HashSET<String> set = new HashSET<>();
    Seq<Tuple2<String,String>> seq = new Seq<>();    
    try {
      Files.lines(Paths.get(f)).filter(line -> {
        if (line.startsWith("#")) return false;
        String[] sa = line.split("\\s+");
        if (sa.length < 2) return false;
        if (sa[0] != sa[1]) { // discard self-loops
          set.addArray(sa);
          seq.add(new Tuple2<String,String>(sa[0],sa[1]));       
        }
        return true;
      }).count();
    } catch (IOException e) {
      e.printStackTrace();
      throw new InvalidDataException();
    }
    return new Tuple2<String[],Tuple2<String,String>[]>(set.toArray(),seq.to());
  }

  public static Tuple2<String,String>[] 
      processData(Tuple2<String[],Tuple2<String,String>[]> t2, int v, int e) {
    // return a Tuple2<String,String>[] of e random edges in the subgraph induced 
    // by v random vertices selected from t2._1 and where t2._2 contains a covering
    // set of edges for the complete graph
//    par(t2._1);
//    par(t2._2);
    if (t2 == null || t2._1 == null || t2._2 == null) throw new IllegalArgumentException(
        "processData: t2 or a component thereof is null");
    if (t2._1.length == 0) throw new IllegalArgumentException(
        "processData: t2._1.length == 0");
    if (t2._2.length == 0) throw new IllegalArgumentException(
        "processData: t2._2.length == 0");
    // select v random vertices from t2._1
    String[] sa = t2._1;
    Arrays.sort(sa, Comparator.nullsLast(Comparator.naturalOrder()));
    if (sa[0] == null) throw new IllegalArgumentException(
        "processData: t2._1 contains all null elements");
    String[] unique = new String[sa.length];
    unique[0] = sa[0]; int c = 1;
    for (int i = 1; i < sa.length; i++) {
      if (sa[i] == null) break;
      if (!sa[i].equals(sa[i-1])) unique[c++] = sa[i];
    }
    unique = take(unique,c);         
    HashSET<String> vertices = new HashSET<>();
    SecureRandom sr = new SecureRandom(); sr.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 105379; i++) sr.nextInt(unique.length);
    while(vertices.size() < v) vertices.add(unique[sr.nextInt(unique.length)]);
    // build an adjacency map from the edges in t2._2 assuming the graph is undirected
    SeparateChainingHashSTX<String,HashSET<String>> map = new SeparateChainingHashSTX<>();
    for (Tuple2<String,String> t : t2._2) {
      String a = t._1; String b = t._2;
      if (map.contains(a)) map.get(a).add(b);
      else map.put(a, new HashSET<String>(b));
      if (map.contains(b)) map.get(b).add(a);
      else map.put(b, new HashSET<String>(a));
    }
    // build a set named edges induced by vertices assuming the graph is undirected
    // see http://mathworld.wolfram.com/Vertex-InducedSubgraph.html for definition
    HashSET<Tuple2<String,String>> edges = new HashSET<>();
    for (String s1 : map.keys())
      for (String s2 : map.get(s1)) {
        if (vertices.contains(s1) && vertices.contains(s2) && !s1.equals(s2)) {
          if (s1.compareTo(s2) < 1) edges.add(new Tuple2<String,String>(s1,s2));
          else edges.add(new Tuple2<String,String>(s2,s1));
        }
      }
    // select e random edges from the set edges and return them in an array
    Tuple2<String,String>[] ea;
    if (edges.size() > 0) ea = edges.toArray();
    else ea = ofDim(Tuple2.class,0);
    if (e > edges.size()) return ea;
    HashSET<Tuple2<String,String>> finalEdges = new HashSET<>();
    while (finalEdges.size() < e) finalEdges.add(ea[sr.nextInt(ea.length)]);
    return finalEdges.toArray();
  }
  
  public static SymbolGraphSB createGraph(String filename, int v, int e) {
    Tuple2<String,String>[] ta = processData(readData(filename), v, e);
    SymbolGraphSB sg = new SymbolGraphSB(ta);
    GraphSB g = sg.graph();
    System.out.println("number of connected components= "+g.count());
    (new EuclidianGraph(g)).showCC();
    return sg;
  }

  public static void main(String[] args) {
    
    String filename = "Ex4144-higgs-reply_network.edgelist.txt";
    SymbolGraphSB sg = createGraph(filename, 37000, 700);
    System.out.println(sg);
    
//    //String filename = "Ex4144-higgs-mention_network.edgelist.txt";
//    Tuple2<String,String>[] x = processData(readData(filename), 3000, 101);
//    System.out.println(x.length);
//    SymbolGraphSB sg = new SymbolGraphSB(x);
//    GraphSB g = sg.graph();
//    EuclidianGraph eg = new EuclidianGraph(g);
//    eg.showp();
    


  }

}
