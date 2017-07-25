package graph;

import static v.ArrayUtils.*;

import ds.BagX;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import st.HashSET;
import v.Tuple2;
import v.Tuple3;
import v.Tuple4;

@SuppressWarnings("unused")
public class LCA {
  private DigraphX G;
  private int V;
  private BagX<Integer>[] adj;
  int[] indegree;
  private int[] sources;
//  private Seq<Tuple3<Integer,Integer,Integer>> heights; // source, to, height
  private int[] heights;
  private Seq<Tuple2<Integer,Integer>>[] heightsFromAllSources;
  private HashSET<Integer>[] ancestors;
  private Seq<Tuple3<Integer,Integer,Integer>> lcas; // v1, v2, lcas
  private Seq<Tuple4<Integer,Integer,Integer,Integer>> allcas; // source, v1, v2, lcas
  
  public LCA(DigraphX g) {
    if (g == null) throw new IllegalArgumentException("LCA: DigraphX is null");
    DirectedCycleX cyclefinder = new DirectedCycleX(g);
    if (cyclefinder.hasCycle()) throw new IllegalArgumentException("DigraphX isn't a DAG");
    G = g; V = G.V(); adj = g.adj(); 
    indegree = g.indegree();  
    sources = new int[V]; 
    findSources();
    heights = new int[V];
    findHeights();
    ancestors = ofDim(HashSET.class,V);
    for (int i = 0; i < V; i++) ancestors[i] = new HashSET<Integer>();
    findAncestors();
  }
  
  public Seq<Tuple4<Integer,Integer,Integer,Integer>> allcas() { 
    findLCAs4AllSources();
    return allcas; 
  }
  
  public HashSET<Integer>[] ancestors() { return ancestors; }
   
  public int[] heights() { return heights; }
  
  public Seq<Tuple2<Integer,Integer>>[] heightsFromAllSources() {
    findHeightsFromAllSources();
    return heightsFromAllSources; 
  }
  
  public Seq<Tuple3<Integer,Integer,Integer>> lcas() { 
    findLCAs();
    return lcas; 
  }
  
  public int numberOfSources() { return sources.length; }

  public int[] sources() { return sources; }

  private void findSources() {
    int c = 0;
    for (int i = 0; i < indegree.length; i++) if (indegree[i] == 0) sources[c++] = i;
    sources = take(sources,c);
  }
  
  private void findHeights() {
    // using the lowest source if any
    if (numberOfSources() == 0) {
      System.out.println("findHeights: no sources");
      return;
    }
    for (int i = 0; i < V; i++) {
      int height = AllSimpleDirectedPaths.height(G,sources[0],i);
      heights[i] = height;
    }
  }

  private void findHeightsFromAllSources() {
    if (numberOfSources() == 0) {
      System.out.println("findHeightsFromAllSources: no sources");
      return;
    }
    heightsFromAllSources = ofDim(Seq.class,V);
    for (int i = 0; i < sources.length; i++) {
      for (int j = 0; j < V; j++) {
        int height = AllSimpleDirectedPaths.height(G,sources[i],j);
        if (heightsFromAllSources[j] == null) heightsFromAllSources[j] = new Seq<>();
        heightsFromAllSources[j].add(new Tuple2<Integer,Integer>(sources[i],height));
      }
    }
  }
  
  private void findAncestors() {
    for (int i = 0; i < V-1; i++) {
      for (int j = 1; j < V; j++) {
        AllSimpleDirectedPaths a = new AllSimpleDirectedPaths(G,i,j);
        Seq<Seq<Integer>> p = a.allPaths();
        for (Seq<Integer> s : p) for (int k : s) ancestors[i].add(k);
        ancestors[i].remove(i);
      }
    }
  }
  
  public int findLCA(int v, int w) {
    // return the LCA of v and w if possible or return -1
    if (numberOfSources() == 0) {
      System.out.println("findLCA: no sources");
      return -1;
    }
    if (v < 0 || v > V-1) throw new IllegalArgumentException("findLCA: int v is out of bounds");
    if (w < 0 || w > V-1) throw new IllegalArgumentException("findLCA: int w is out of bounds");
    if (v == w) return -1;
    HashSET<Integer> x = ancestors[v].intersection(ancestors[w]);
    if (x.isEmpty()) return -1;
    int maxh = Integer.MIN_VALUE, maxv = -1;
    for (int k : x) {
      int height = heights[k];
      if (height > maxh) { maxh = height; maxv = k; }
    }
    return maxv;
  }
  
  public void findLCAs() {
    // find LCAs for all non-trivial pairs of vertices using the lowest source
    if (numberOfSources() == 0) {
      System.out.println("findLCAs: no sources");
      return;
    }
    lcas = new Seq<>();
    for (int i = 0; i < V-1; i++)
      for (int j = i+1; j < V; j++) {
        HashSET<Integer> x = ancestors[i].intersection(ancestors[j]);
        if (x.isEmpty()) continue;
        int maxh = Integer.MIN_VALUE, maxv = -1;
        for (int k : x) {
          int height = heights[k];
          if (height > maxh) { maxh = height; maxv = k; }
        }
        if (maxv != -1) lcas.add(new Tuple3<>(i,j,maxv));
      }
  }

  public void findLCAs4AllSources() {
    // find LCAs for all non-trivial pairs of vertices for all sources
    // this demonstrates all sources give the same LCAs
    if (numberOfSources() == 0) {
      System.out.println("findLCAs4AllSources: no sources");
      return;
    }
    allcas = new Seq<>();
    for (int i = 0; i < V-1; i++)
      for (int j = i+1; j < V; j++) {
        HashSET<Integer> x = ancestors[i].intersection(ancestors[j]);
        if (x.isEmpty()) continue;
        int[] maxh = fillInt(sources.length,()->Integer.MIN_VALUE);
        int[] maxv = fillInt(sources.length,()->-1);
        for (int k : x) {
          Seq<Tuple2<Integer,Integer>> h = heightsFromAllSources[k];
          for (Tuple2<Integer,Integer> t : h) {
            int d = indexOf(sources,t._1);
            if (t._2 > maxh[d]) { maxh[d] = t._2; maxv[d] = k; }
          }
        }
        for (int k = 0; k < sources.length; k++)
          if (maxv[k] != -1) allcas.add(new Tuple4<>(sources[k],i,j,maxv[k]));
      }
  }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
    LCA l = new LCA(d);
    System.out.println(l.findLCA(0,9));
    int[] heights = l.heights();
    System.out.print("heights="); par(heights);
    
    
//    int[] sources = l.sources();
//    System.out.print("sources="); par(sources);
//    System.out.println("heights:");
//    Seq<Tuple2<Integer,Integer>>[] heights = l.heights();
//    for (int i = 0; i < heights.length; i++)
//      System.out.println(i+": "+heights[i]);
//    System.out.println("ancestors:");
//    HashSET<Integer>[] ancestors = l.ancestors();
//    for (int i = 0; i < ancestors.length; i++)
//      System.out.println(i+": "+ancestors[i]);  
//    System.out.println("lcas:");
//    Seq<Tuple3<Integer,Integer,Integer>> lcas = l.lcas();
//    for (Tuple3<Integer,Integer,Integer> t : lcas) 
//      System.out.println(t);
//    System.out.println("allcas:");
//    Seq<Tuple4<Integer,Integer,Integer,Integer>> allcas = l.allcas();
//    for (Tuple4<Integer,Integer,Integer,Integer> t : allcas) 
//      System.out.println(t);

/*
    indegree=[1,1,0,1,2,2,2,1,0,1,1,1,2]
    sources=[2,8]
    heights:
    0: ((2,1),(8,-1))
    1: ((2,2),(8,-1))
    2: ((2,0),(8,-1))
    3: ((2,1),(8,-1))
    4: ((2,3),(8,3))
    5: ((2,2),(8,-1))
    6: ((2,2),(8,2))
    7: ((2,-1),(8,1))
    8: ((2,-1),(8,0))
    9: ((2,3),(8,3))
    10: ((2,4),(8,4))
    11: ((2,4),(8,4))
    12: ((2,5),(8,5))
    ancestors:
    0: (4,12,1,5,9,6,10,11)
    1: ()
    2: (0,4,12,1,5,9,6,10,3,11)
    3: (4,5)
    4: ()
    5: (4)
    6: (4,12,9,10,11)
    7: (4,12,9,6,10,11)
    8: (4,12,9,6,10,7,11)
    9: (12,10,11)
    10: ()
    11: (12)
    12: ()
    lcas:
    (0,2,12)
    (0,3,4)
    (0,5,4)
    (0,6,12)
    (0,7,12)
    (0,8,12)
    (0,9,12)
    (0,11,12)
    (2,3,4)
    (2,5,4)
    (2,6,12)
    (2,7,12)
    (2,8,12)
    (2,9,12)
    (2,11,12)
    (3,5,4)
    (3,6,4)
    (3,7,4)
    (3,8,4)
    (5,6,4)
    (5,7,4)
    (5,8,4)
    (6,7,12)
    (6,8,12)
    (6,9,12)
    (6,11,12)
    (7,8,12)
    (7,9,12)
    (7,11,12)
    (8,9,12)
    (8,11,12)
    (9,11,12)
    allcas:
    (2,0,2,12)
    (8,0,2,12)
    (2,0,3,4)
    (8,0,3,4)
    (2,0,5,4)
    (8,0,5,4)
    (2,0,6,12)
    (8,0,6,12)
    (2,0,7,12)
    (8,0,7,12)
    (2,0,8,12)
    (8,0,8,12)
    (2,0,9,12)
    (8,0,9,12)
    (2,0,11,12)
    (8,0,11,12)
    (2,2,3,4)
    (8,2,3,4)
    (2,2,5,4)
    (8,2,5,4)
    (2,2,6,12)
    (8,2,6,12)
    (2,2,7,12)
    (8,2,7,12)
    (2,2,8,12)
    (8,2,8,12)
    (2,2,9,12)
    (8,2,9,12)
    (2,2,11,12)
    (8,2,11,12)
    (2,3,5,4)
    (8,3,5,4)
    (2,3,6,4)
    (8,3,6,4)
    (2,3,7,4)
    (8,3,7,4)
    (2,3,8,4)
    (8,3,8,4)
    (2,5,6,4)
    (8,5,6,4)
    (2,5,7,4)
    (8,5,7,4)
    (2,5,8,4)
    (8,5,8,4)
    (2,6,7,12)
    (8,6,7,12)
    (2,6,8,12)
    (8,6,8,12)
    (2,6,9,12)
    (8,6,9,12)
    (2,6,11,12)
    (8,6,11,12)
    (2,7,8,12)
    (8,7,8,12)
    (2,7,9,12)
    (8,7,9,12)
    (2,7,11,12)
    (8,7,11,12)
    (2,8,9,12)
    (8,8,9,12)
    (2,8,11,12)
    (8,8,11,12)
    (2,9,11,12)
    (8,9,11,12)
*/  
  }

}
