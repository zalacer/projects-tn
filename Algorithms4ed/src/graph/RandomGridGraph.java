package graph;

import static ex15.Ex1518RandomGridGenerator.generate;
import static v.ArrayUtils.iterator;
import static v.ArrayUtils.repeat;
import static v.ArrayUtils.shuffle;
import static v.ArrayUtils.space;

import java.security.SecureRandom;
import java.util.Iterator;

import ds.Seq;
import ex15.Ex1518RandomGridGenerator.Connection;
import ex15.Ex1519RandomGridUnionFindAnimation.WeightedQuickUnionWithPathCompressionUF;
import st.HashSET;
import v.Tuple2;
import v.Tuple3;

// for ex4143

public class RandomGridGraph {

  public static EuclidianGraph completeGrid(int v) {
    // generates a complete v-by-v grid of undirected edges connecting
    // adjacent vertices (without using union find)
    if (v < 2) throw new IllegalArgumentException(
        "completeGrid(int): v is < 2");
    EuclidianGraph g = null;
    g = new EuclidianGraph(v*v);
    // add edges
    Connection[] c = generate(v);
    for (int i = 0; i < c.length; i++) g.addEdge(c[i].p(),c[i].q());
    // add coordinates 
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    } 
    g.trim(); // clear wasted memory in graph
    return g;
  }

  public static EuclidianGraph randomGrid(int v) {
    // generates a random v-by-v grid grid with directed connecting adjacent edges 
    // not all filled as in ex1519 and with no additional edges
    if (v < 2) throw new IllegalArgumentException(
        "randomGrid(int): v is < 2");
    EuclidianGraph g = null;
    String sa[]; int p; int q;
    g = new EuclidianGraph(v*v);
    // add initial directional edges identified with union find
    // based on ex15.Ex1519RandomGridGenerator
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(2*v*(v-1) + 2*(v-1)*(v-1) - 1);
    Iterator<Connection> it = (Iterator<Connection>) iterator(generate(v));
    while (it.hasNext()) {
      sa = it.next().toString().split("-");
      p = Integer.parseInt(sa[0]);
      q = Integer.parseInt(sa[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p,q);
      g.addEdge(p, q);
    }
    // add coordinates
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    }
    g.trim(); // clear wasted memory in graph
    return g;
  }
  
  public static Tuple2<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>> 
      randomGrid(int v, int r) {
    // generates a random v-by-v grid grid with directed edges not all filled 
    // as in ex1519 plus add r extra random edges (between unconnected vertices) 
    if (v < 2) throw new IllegalArgumentException(
        "randomGrid(int,int): v is < 2");
    if (r < 0) throw new IllegalArgumentException(
        "randomGrid(int,int): r is < 0");
    EuclidianGraph g = null;
    String sa[]; int p; int q;
    int V = v*v;
    g = new EuclidianGraph(V);
    // add initial directional edges identified with union find
    // based on ex15.Ex1519RandomGridUnionFindAnimation
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(2*v*(v-1) + 2*(v-1)*(v-1) - 1);
    Iterator<Connection> it = (Iterator<Connection>) iterator(generate(v));
    if (r >= V) r = V/3;
    int conslimit = V - r; // limit the number of edges created to V-r
    int c = 0;
    while (it.hasNext() && c < conslimit) {
      sa = it.next().toString().split("-");
      p = Integer.parseInt(sa[0]);
      q = Integer.parseInt(sa[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p,q);
      g.addEdge(p, q);
      c++;
    }
    HashSET<Tuple2<Integer,Integer>> extraEdges = new HashSET<>();
    if (r > 0) {
      // accumulate unused directional edges in seq using union find
      Seq<Tuple2<Integer,Integer>> seq = new Seq<>();
      int d = 0;
      it = (Iterator<Connection>) iterator(generate(v));
      while (it.hasNext()) {
        sa = it.next().toString().split("-");
        p = Integer.parseInt(sa[0]);
        q = Integer.parseInt(sa[1]);
        if (g.hasEdge(q, p)) continue;
        seq.add(new Tuple2<Integer,Integer>(q,p));
      }
      // convert seq to array and shuffle    
      Tuple2<Integer,Integer>[] edges = seq.to();
      SecureRandom sr = new SecureRandom(); sr.setSeed(System.currentTimeMillis());
      for (int i = 0; i < 130002; i++) sr.nextDouble();
      shuffle(edges,sr);
      // create r extra edges and add them to extraEdges to be returned in a Tuple2
      d = 0; 
      for (Tuple2<Integer,Integer> t : edges) {
        if (!g.hasEdge(t._1, t._2)) { 
          g.addEdge(t._1, t._2); d++; extraEdges.add(t);
        }
        if (d == r) break;
      }
    }
    // add coordinates
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    }
    g.trim(); // clear wasted memory in graph
    return new Tuple2<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>>(g,extraEdges);
  }
  
  public static Tuple3<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>,
      HashSET<Tuple2<Integer,Integer>>> randomGrid(int v, int r, boolean b) {
    // generates a random v-by-v grid grid with directed edges not all filled 
    // as in ex1519 plus add r extra random edges (between unconnected vertices)
    // plus add extra edges between all pairs of vertices with probability in-
    // versely proportional to the distance between them iff b == true.
    if (v < 2) throw new IllegalArgumentException(
        "randomGrid(int,int,boolean): v is < 2");
    if (r < 0) throw new IllegalArgumentException(
        "randomGrid(int,int,boolean): r is < 0");
    EuclidianGraph g = null;
    String sa[]; int s; int t;
    int V = v*v;
    g = new EuclidianGraph(V);
    // add initial directional edges identified with union find
    // based on ex15.Ex1519RandomGridUnionFindAnimation
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(2*v*(v-1) + 2*(v-1)*(v-1) - 1);
    Iterator<Connection> it = (Iterator<Connection>) iterator(generate(v));
    if (r >= V) r = V/3;
    int conslimit = V - r; // limit the number of edges created to V-r
    int c = 0;
    while (it.hasNext() && c < conslimit) {
      sa = it.next().toString().split("-");
      s = Integer.parseInt(sa[0]);
      t = Integer.parseInt(sa[1]);
      if (uf.connected(s, t)) continue;
      uf.union(s,t);
      g.addEdge(s,t);
      c++;
    }
    HashSET<Tuple2<Integer,Integer>> extraEdges = new HashSET<>();
    SecureRandom sr = new SecureRandom(); sr.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 130002; i++) sr.nextDouble();
    if (r > 0) {
      // accumulate unused directional edges in seq using union find
      Seq<Tuple2<Integer,Integer>> seq = new Seq<>();
      it = (Iterator<Connection>) iterator(generate(v));
      while (it.hasNext()) {
        sa = it.next().toString().split("-");
        s = Integer.parseInt(sa[0]);
        t = Integer.parseInt(sa[1]);
        if (g.hasEdge(t, s)) continue;
        seq.add(new Tuple2<Integer,Integer>(t,s));
      } 
      // convert seq to array and shuffle
      Tuple2<Integer,Integer>[] edges = seq.to();
      shuffle(edges,sr);
      // create r extra edges and add them to extraEdges to be returned in a Tuple3
      c = 0;
      for (Tuple2<Integer,Integer> t2 : edges) {
        if (!g.hasEdge(t2._1, t2._2)) { 
          g.addEdge(t2._1, t2._2); c++; extraEdges.add(t2);
        }
        if (c == r) break;
      }
    }
    // add coordinates
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    }   
    // if b == true add edges between all pairs of vertices with probability = 1/distance
    // after normalizing the distances so the least == 1 and then scaling them by 25 to
    // avoid obscuring the plot
    HashSET<Tuple2<Integer,Integer>> pEdges = new HashSET<>();
    if (b == true) {
      Seq<Tuple2<Double,Double>> coords = g.coords();
      // find the min distance between any two edges over entire graph
      double minDist = Double.POSITIVE_INFINITY; double d = 0, f= 0;
      for (int i = 0; i < V; i++)
        for (int j = i+1; j < V; j++) {
           d = distance(coords.get(i),coords.get(j));
           if (d < minDist) {
             minDist = d;
           }
        }
      // scale the normalization factor f multiplicatively up by 100 so a 
      // preponderance of these new edges doesn't cloud the plot
      f = 100./minDist; 
      for (int i = 0; i < V; i++)
        for (int j = i+1; j < V; j++){
          double p = 1./(f*distance(coords.get(i),coords.get(j)));
          if (!(p >= 0.0 && p <= 1.0)) continue;
          if (sr.nextDouble() < p) { // bernoulli criterion
            g.addEdge(i,j);
            pEdges.add(new Tuple2<Integer,Integer>(i,j));   
          }
        }
    }
    g.trim(); // clear wasted memory in graph
    return new Tuple3<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>,HashSET<Tuple2<Integer,Integer>>>(g,extraEdges,pEdges);
  }

  public static double distance(Tuple2<Double,Double> p, Tuple2<Double,Double> q) {
    // calculate and return distance between two 2D points represented with Tuple2ss
    if (p==null || q==null || p._1==null || p._2==null || q._1==null || q._2==null)
      throw new IllegalArgumentException("distance: p or q or a component thereof is null");
    return Math.sqrt((p._1 - q._1)*(p._1 - q._1)+(p._2 - q._2)*(p._2 - q._2));
  }
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    EuclidianGraph g;
    
    System.out.println("method"+space(22)+"V"+space(6)+"E");
    System.out.println(repeat("=",38));
    
    g = completeGrid(15);
    System.out.printf("%-24s %6d %6d\n","completeGrid("+15+"):", g.V(), g.E());
    /* plot g: v*v vertices are red, edges are black, the number of edges isn't limited. */ 
    g.showGrid();
    
    pause(3);
    
    g = randomGrid(25);
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+25+"):", g.V(), g.E());
    /* plot g: v*v vertices are red, edges identified with union find are black, 
      the number of edges isn't limited. */ 
    g.showGrid();
    
    pause(3);
    
    Tuple2<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>> t2 = randomGrid(15,19);
    g = t2._1;
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+15+","+19+"):", +g.V(), g.E());
    /* plot g: v*v vertices are red, initial edges identified with union find are black, 
      random edges specified by the second arg are green. the total number of initial 
      and random edges is normalized to v. */ 
    g.showGrid(t2._2);
    
    pause(3);

    Tuple3<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>,
        HashSET<Tuple2<Integer,Integer>>> t3 = randomGrid(15,19,true);
    g = t3._1;
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+15+","+19+", true):", g.V(), g.E());
    /* plot g: v*v vertices are red, initial edges identified with union find are black, 
       random edges specified by the second arg are green. the total number of initial 
       and random edges is normalized to v. the edges created with probability inversly 
       proportional to (1./(10*distance_between_the_vertices) are (yellowish) orange.
       run wil last arg=false to omit the edges created with probability inversly pro-
       portional to distance. */ 
    g.showGrid(t3._2,t3._3);
    
  }

}
