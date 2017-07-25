package graph;

import static v.ArrayUtils.*;

import java.util.Comparator;
import java.util.Iterator;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;

/*
 rates.txt
 5
 USD 1      0.741  0.657  1.061  1.005
 EUR 1.349  1      0.888  1.433  1.366
 GBP 1.521  1.126  1      1.614  1.538
 CHF 0.942  0.698  0.619  1      0.953
 CAD 0.995  0.732  0.650  1.049  1
 */

//@SuppressWarnings("unused")
@SuppressWarnings("unused")
public class ArbitrageX {

  private ArbitrageX() {}

  public static void cyclesAnalyzer(String rates, boolean verbose) {
    if (rates == null) throw new IllegalArgumentException(
        "cyclesAnalyzer: String rates is null");
    In in = new In(rates);    
    int V = in.readInt();
    String[] name = new String[V];

    // create complete network
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < V; v++) {
      name[v] = in.readString();
      for (int w = 0; w < V; w++) {
        double rate = in.readDouble();
        DirectedEdgeX e = new DirectedEdgeX(v, w, -Math.log(rate));
        G.addEdge(e);
      }
    }

    if (verbose) System.out.println("Rates EdgeWeightedDigraphX:\n"+G);

    Tuple2<Double,Seq<DirectedEdgeX>> t;
    Seq<Tuple2<Double,Seq<DirectedEdgeX>>> r = new Seq<>();

    for (int i = 2; i < 6; i++) {
      t = cyclesAnalyzer(G, i, true, verbose);  // using double arithmetic
      if (t != null && t._1 != null) r.add(t);
      t = cyclesAnalyzer(G, i, false, verbose); // using long arithmetic
      if (t != null && t._1 != null) r.add(t);
    }

    Comparator<Tuple2<Double,Seq<DirectedEdgeX>>> c = (a,b) -> { return a._1.compareTo(b._1); };
    t = r.min(c);

    System.out.println("\nOverall the cycle with min weight is:");
    System.out.println(str(t._2)+"  weight:"+t._1);
    
    double stake = 1000.0, start = stake; String from = null; boolean first = true;
    for (DirectedEdgeX de : t._2) {
      //System.out.printf("%10.5f %s ", stake, name[de.from()]);
      if (first) { from = name[de.from()]; first = false; }
      stake *= Math.exp(-de.weight());
      //System.out.printf("= %10.5f %s\n", stake, name[de.to()]);
    }
    double profitPerExch = (100*(stake - start)/stake)/(t._2.size()-1);
    System.out.println("profitPerExchange = "+profitPerExch+"%");
    //System.out.println("Profit for 1 cycle = "+(stake-start)+" "+from);

  }

  public static Tuple2<Double,Seq<DirectedEdgeX>> cyclesAnalyzer(EdgeWeightedDigraphX G, 
      int i, boolean dbl, boolean verbose) {
    if (G == null) throw new IllegalArgumentException("cyclesAnalyzer: G is null");
    if (i < 2) throw new IllegalArgumentException("cyclesAnalyzer: i < 2");
    int V = G.V();
    if (i > V) throw new IllegalArgumentException("cyclesAnalyzer: i < G.V()"); 
    if (verbose) 
      if (dbl) System.out.println("\n"+i+"cycles using double arithmetic:");
      else System.out.println("\n"+i+"cycles using long arithmetic:"); 
    Seq<DirectedEdgeX> mins = null; Double minw = null;
    Iterator<int[]> it = combinations(range(0,G.V()),i);
    Seq<DirectedEdgeX> s; DirectedEdgeX e; Double w;
    while(it.hasNext()) {
      int[] a = it.next();
      Iterator<int[]> it2 = permutations(a);
      LOOP: while (it2.hasNext()) {
        int[] b = it2.next();
        s = new Seq<>();
        for (int j = 0; j < b.length-1; j++) {
          e = G.findEdge(b[j],b[j+1]);
          if (e == null) continue LOOP;
          s.add(e);      
        }
        e = G.findEdge(b[b.length-1], b[0]);
        if (e == null) continue LOOP;
        s.add(e);
        if (verbose) System.out.print(str(s));
        w = dbl ? wt(s) : wt2(s);
        if (verbose) System.out.println(" weight = "+w);
        if (w == null) continue;
        if (minw == null || w < minw) { minw = w; mins = s; } 
      }
    }

    if (verbose) System.out.println("minSequence = "+str(mins));
    if (verbose) System.out.println("minWeight   = "+minw);
    return new Tuple2<Double,Seq<DirectedEdgeX>>(minw,mins);
  }

  public static Double wt(Seq<DirectedEdgeX> s) {
    // using double arithmetic
    if (s == null) return null;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  public static Double wt2(Seq<DirectedEdgeX> s) {
    // using long arithmetic
    if (s == null) return null;
    long w = 0; long sf = (long) Math.pow(10,sf(s));
    for (DirectedEdgeX e : s) {
      long w2 = (long) (e.w()*sf);
      if (w2 == -w) w = 0;
      else w += w2;
    }
    return 1.*w/sf;
  }

  public static Double wt3(Seq<DirectedEdgeX> s) {
    // using integer arithmetic
    if (s == null) return null;
    int w = 0;
    for (DirectedEdgeX e : s) {
      int w2 = (int) (e.w()*100);
      if (w2 == -w) w = 0;
      else w += w2;
    }
    return 1.*w/100;
  }

  public static Integer sf(Double d) {
    // return the number of digits after the decimal point in d
    if (d == null) return null;
    if ((""+d).indexOf('.') == -1) return 0;
    else {
      String x = (""+d).replaceAll("[0-9+-]*\\.", "");
      return x.matches("0+") ? 0 : x.length();
    }
  }

  public static Integer sf(Seq<DirectedEdgeX> s) {
    // return multiplicative scale factor for the weights of edges 
    // in s for computing their sum using integer arithmetic
    if (s == null) return null;
    Integer sf = null;
    for (DirectedEdgeX e : s) {
      if (sf == null) sf = sf(e.w());
      else sf = Math.max(sf, sf(e.w()));
    }
    return sf; 
  }

  public static String str(Seq<DirectedEdgeX> s) {
    return convert(s).mkString(">");
  }

  public static Seq<Integer> convert(Seq<DirectedEdgeX> edges) {
    // return Seq<Integer> of vertices in edges in order
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeX");
      seq.add(e.from());
      seq.add(e.to());
    }
    seq = seq.uniquePreservingOrder();
    seq.add(seq.get(0));
    return seq;
  }

  public static void bellmanFord(String rates, boolean verbose) {
    In in = new In(rates);    
    // V currencies
    int V = in.readInt();
    String[] name = new String[V];

    // create complete network
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < V; v++) {
      name[v] = in.readString();
      for (int w = 0; w < V; w++) {
        double rate = in.readDouble();
        DirectedEdgeX e = new DirectedEdgeX(v, w, -Math.log(rate));
        G.addEdge(e);
      }
    }

    BellmanFordSPX spt = new BellmanFordSPX(G, 0);
    if (spt.hasNegativeCycle()) {
      Seq<DirectedEdgeX> ncycle = spt.ncycle();
      double ncycleWeight = spt.ncycleWeight();
      double ncycleWeight2 = spt.ncycleWeight2();       
      System.out.println("\nBellmanFordSPX negativeCycle arbitrage opportunity found:");
      if (verbose) System.out.println("negativeCycle = "+str(ncycle));
      if (verbose) System.out.println("negativeCycleWeightUsingDoubleArithmetic = "+ncycleWeight);
      if (verbose) System.out.println("negativeCycleWeightUsingIntegerArithmetic = "+ncycleWeight2);
      if (!verbose) System.out.println(str(ncycle)+"  weight:"+ncycleWeight);
      double stake = 1000.0, start = stake; String from = null; boolean first = true;
      for (DirectedEdgeX de : spt.negativeCycle()) {
        //System.out.printf("%10.5f %s ", stake, name[de.from()]);
        if (first) { from = name[de.from()]; first = false; }
        stake *= Math.exp(-de.weight());
        //System.out.printf("= %10.5f %s\n", stake, name[de.to()]);
      }
      double profitPerExch = (100*(stake - start)/stake)/(ncycle.size()-1);
      System.out.println("profitPerExchange = "+profitPerExch+"%");
      //System.out.println("Profit for 1 cycle = "+(stake-start)+" "+from);
    }
    else {
      StdOut.println("No arbitrage opportunity");
    }

  }

  public static void main(String[] args) {

    cyclesAnalyzer("rates.txt",false);

    In in = new In("rates.txt");    
    // V currencies
    int V = in.readInt();
    String[] name = new String[V];

    // create complete network
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < V; v++) {
      name[v] = in.readString();
      for (int w = 0; w < V; w++) {
        double rate = in.readDouble();
        DirectedEdgeX e = new DirectedEdgeX(v, w, -Math.log(rate));
        G.addEdge(e);
      }
    }

    //    System.out.println("G:");
    //    System.out.println(G);
    //
    //    System.out.println("2cycles using double arithmetic:");
    //    Seq<DirectedEdgeX> mins2 = null; Double minw2 = null;
    //    Iterator<int[]> it2 = combinations(range(0,V),2);
    //    Seq<DirectedEdgeX> s; DirectedEdgeX e; Double w;
    //    LOOP: while(it2.hasNext()) {
    //      int[] a = it2.next();
    //      s = new Seq<>();
    //      for (int i = 0; i < a.length-1; i++) {
    //        e = G.findEdge(a[i],a[i+1]);
    //        if (e == null) continue LOOP;
    //        s.add(e);      
    //      }
    //      e = G.findEdge(a[a.length-1], a[0]);
    //      if (e == null) continue LOOP;
    //      s.add(e);
    //      System.out.print("s = "+s);
    //      w = wt(s);
    //      System.out.println(" w = "+w);
    //      if (w == null) continue;
    //      if (minw2 == null || w < minw2) { minw2 = w; mins2 = s; }     
    //    }
    //
    //    System.out.println("\n2cycles using integer arithmetic:");
    //    mins2 = null; minw2 = Double.POSITIVE_INFINITY;
    //    it2 = combinations(range(0,V),2);
    //    //Seq<DirectedEdgeX> s; DirectedEdgeX e; double w;
    //    LOOP: while(it2.hasNext()) {
    //      int[] a = it2.next();
    //      s = new Seq<>();
    //      for (int i = 0; i < a.length-1; i++) {
    //        e = G.findEdge(a[i],a[i+1]);
    //        if (e == null) continue LOOP;
    //        s.add(e);      
    //      }
    //      e = G.findEdge(a[a.length-1], a[0]);
    //      if (e == null) continue LOOP;
    //      s.add(e);
    //      System.out.print("s = "+s);
    //      w = wt2(s);
    //      System.out.println(" w = "+w);
    //      if (w == null) continue;
    //      if (minw2 == null || w < minw2) { minw2 = w; mins2 = s; }     
    //    }
    //
    //    System.out.println("mins2 = "+mins2);
    //    System.out.println("minw2 = "+minw2);
    //
    //    System.out.println("\n3cycles using double arithmetic:");
    //    Seq<DirectedEdgeX> mins3 = null; Double minw3 = null;
    //    Iterator<int[]> it3 = combinations(range(0,V),3);
    //    //Seq<DirectedEdgeX> s; DirectedEdgeX e; double w;
    //    LOOP: while(it3.hasNext()) {
    //      int[] a = it3.next();
    //      s = new Seq<>();
    //      for (int i = 0; i < a.length-1; i++) {
    //        e = G.findEdge(a[i],a[i+1]);
    //        if (e == null) continue LOOP;
    //        s.add(e);      
    //      }
    //      e = G.findEdge(a[a.length-1], a[0]);
    //      if (e == null) continue LOOP;
    //      s.add(e);
    //      System.out.print("s = "+s);
    //      w = wt(s);
    //      System.out.println(" w = "+w);
    //      if (w == null) continue;
    //      if (minw3 == null || w < minw3) { minw3 = w; mins3 = s; }     
    //    }
    //
    //    System.out.println("mins3 = "+mins3);
    //    System.out.println("minw3 = "+minw3);
    //
    //    System.out.println("\n3cycles using integer arithmetic:");
    //    mins3 = null; minw3 = null;
    //    it3 = combinations(range(0,V),3);
    //    //Seq<DirectedEdgeX> s; DirectedEdgeX e; double w;
    //    LOOP: while(it3.hasNext()) {
    //      int[] a = it3.next();
    //      s = new Seq<>();
    //      for (int i = 0; i < a.length-1; i++) {
    //        e = G.findEdge(a[i],a[i+1]);
    //        if (e == null) continue LOOP;
    //        s.add(e);      
    //      }
    //      e = G.findEdge(a[a.length-1], a[0]);
    //      if (e == null) continue LOOP;
    //      s.add(e);
    //      System.out.print("s = "+s);
    //      w = wt2(s);
    //      System.out.println(" w = "+w);
    //      if (w == null) continue;
    //      if (minw3 == null || w < minw3) { minw3 = w; mins3 = s; }     
    //    }
    //
    //    System.out.println("mins3 = "+mins3);
    //    System.out.println("minw3 = "+minw3);   

    System.out.println("\nBellmanFordSPX NegativeCycle:");
    BellmanFordSPX spt = new BellmanFordSPX(G, 0);
    if (spt.hasNegativeCycle()) {
      //      Seq<DirectedEdgeX> ncycle = spt.ncycle();
      //      double ncycleWeight = spt.ncycleWeight();
      System.out.println("ncycle = "+spt.ncycle());
      System.out.println("ncycleWeight = "+spt.ncycleWeight());
      System.out.println("ncycleWeight2 = "+spt.ncycleWeight2());
      double stake = 1000.0;
      for (DirectedEdgeX de : spt.negativeCycle()) {
        StdOut.printf("%10.5f %s ", stake, name[de.from()]);
        stake *= Math.exp(-de.weight());
        //        stake *= de.weight();
        StdOut.printf("= %10.5f %s\n", stake, name[de.to()]);
      }
    }
    else {
      StdOut.println("No arbitrage opportunity");
    }
  }

}
