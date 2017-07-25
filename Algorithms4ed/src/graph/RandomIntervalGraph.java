package graph;

import java.security.SecureRandom;
import java.util.Arrays;

import ds.Seq;
import v.Tuple2;

// for ex4145
// intervals are all in the unit interval [0,1] of the real line
// https://en.wikipedia.org/wiki/Unit_interval

public class RandomIntervalGraph {
  
  public static SymbolGraphSB createRandomIntervalGraph(int n, double d) {
    // return a SymbolGraphSB of intervals in the unit interval
    // each of length d. intervals are represented by their start values and
    // two intervals are connected iff they overlap.
    if (n < 0) throw new IllegalArgumentException(
        "createRandomIntervalGraph: n is < 0");
    if (d < 0) throw new IllegalArgumentException(
        "createRandomIntervalGraph: d is < 0");
    if (d > 1) throw new IllegalArgumentException(
        "createRandomIntervalGraph: d is > 1");
    SecureRandom sr = new SecureRandom(); sr.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 117191; i++) sr.nextDouble();
    double[] da = sr.doubles(n,0.,1.+Math.ulp(1.)).toArray();
    Arrays.sort(da);
    Seq<Tuple2<String,String>> edges = new Seq<>();
    for (int i = 0; i < n-1; i++) {
      double p = da[i];
      for (int j = i+1; j < n; j++) {
        double q = da[j];
        if (p + d >= q) 
          edges.add(new Tuple2<String,String>(Double.toString(p),Double.toString(q)));    
      }
    }
    if (edges.isEmpty()) {
      System.err.println("no edges");
      return null;
    }
    SymbolGraphSB sg = new SymbolGraphSB(edges.to());
    GraphSB g = sg.graph();
    System.out.println("number of connected components= "+g.count());
    (new EuclidianGraph(g)).showCC();
    return sg;
  }

  public static void main(String[] args) {
    
    SymbolGraphSB sg =  createRandomIntervalGraph(199,.0201);
    System.out.println(sg);

  }

}
