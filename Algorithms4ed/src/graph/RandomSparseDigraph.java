package graph;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import ds.Seq;
import v.Tuple2;

/* create random undirected sparse graphs for ex4141
  maxEdges = V*(V-1)/2 [for simple graphs: https://en.wikipedia.org/wiki/Dense_graph]
  omitting the gray area, implement sparse as E < maxEdges/3 for V < 6 
  and E = V for V > 5. for V < 6:
  V E
  0 0
  1 0
  2 0
  3 1
  4 2
  5 3
 */

public class RandomSparseDigraph {

  public static SecureRandom r = new SecureRandom();

  public static DigraphX createRandomSparseDigraph(int v) {
    if (v < 1) throw new IllegalArgumentException("createRandomSparseDigraph: v < 0");
    int e = 0;
    switch (v) {
      case 0: case 1: case 2: break;
      default: e = v;
    }
    r.setSeed(System.currentTimeMillis());
    int u = 1+v*(v-1);
    for (int i = 0; i < 119953; i++) r.nextInt(u);
    int stop  = r.nextInt(u);
    DigraphX g = null;
    if (e == 0) g = new DigraphX(v);
    else while (g == null) g = generateRSG(v, e, stop);    
    return g;
  }

  private static DigraphX generateRSG(int v, int e, int stop) {
    // return the stopth simple graph for v and e
    if (v < 0) throw new IllegalArgumentException("generateSRG: v < 0");
    if (e < 0) throw new IllegalArgumentException("generateSRG: e < 0");
    if (e > v*(v-1)) throw new IllegalArgumentException("generateSRG: E > v*(v-1) == "+(v*(v-1)));
    if (stop < 0 || stop > v*(v-1)) throw new IllegalArgumentException(
        "generateSRG: stop is < 0 or > v*(v-1) == "+(v*(v-1)));
    Seq<Tuple2<Integer,Integer>> s = new Seq<>(); // Seq of all possible edges for v
    for (int i = 0; i < v-1; i++)
      for (int j = i+1; j < v; j++) {
        s.add(new Tuple2<Integer,Integer>(i,j));
        s.add(new Tuple2<Integer,Integer>(j,i));
      }
    // generate all combinations of the elements of s taken E at a time
    int[] a = range(0,s.size());
    Iterator<int[]> it = combinations(a,e); // iterator over elements of a taken E at a time
    Tuple2<Integer,Integer>[] ta = s.to(); shuffle(ta,r);
    int d = -1; DigraphX g = null;
    while (it.hasNext()) {
      int[] c = it.next();
      if (++d != stop) continue;
      Tuple2<Integer,Integer>[] tc = ofDim(Tuple2.class,e);
      for (int i = 0; i < e; i++) tc[i] = ta[c[i]]; // mask of c in ta
      int[] edges = new int[2*e]; int j = 0; // convert mask from array of Tuple2s to ints
      for (int i = 0; i < e; i++) { edges[j++] = tc[i]._1; edges[j++] = tc[i]._2; }
      g = new DigraphX(v,e,edges); // create graph from V, E and int array e
      break;
    }  
    return g;
  }

  public static void main(String[] args) {

    DigraphX g = createRandomSparseDigraph(7);
    System.out.println(g);
    EuclidianDigraph e = new EuclidianDigraph(g);
    e.show();

  }

}
