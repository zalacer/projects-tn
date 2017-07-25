package graph;

import static analysis.Log.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.function.Predicate;

import v.Tuple2;

// for ex4235

public class RandomEuclidianDigraph {
  
  public static EuclidianDigraph createRandomEuclidianDigraph(int v, boolean connected) {
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 119557; i++) r.nextDouble();
    double m = 100.*Math.sqrt(lg(v)/(Math.PI*v));
    EuclidianDigraph g = null;
    int count = 0; 
    Predicate<Integer> p = connected ? (q)-> q != 1 : (q)-> !(q > 1);
    while (p.test(count)) {
      g = new EuclidianDigraph(v);
      double[] da = r.doubles(2*v,1,100).toArray();
      Tuple2<Double,Double>[] ta = ofDim(Tuple2.class,v); int j = 0;
      for (int i = 0; i < 2*v; i+=2) ta[j++] = new Tuple2<Double,Double>(da[i],da[i+1]);
      for (int x = 0; x < v-1; x++)
        for (int y = x+1; y < v; y++) 
          if (connected) {
            if (distance(ta[x],ta[y]) > m) {
              int u = r.nextInt(2);
              if (u == 1) g.addEdge(y,x);
              else g.addEdge(x,y); 
            }
          } else if (distance(ta[x],ta[y]) < m) {
            int u = r.nextInt(2);
            if (u == 1) g.addEdge(y,x);
            else g.addEdge(x,y); 
          }
      for (int i = 0; i < v; i++) g.addCoords(i,ta[i]._1, ta[i]._2);
      g.trim();
      g.search();
      count = g.count();
    }
    return g;
  }
  
  private static double distance(Tuple2<Double,Double> p, Tuple2<Double,Double> q) {
    if (p==null || q==null || p._1==null || p._2==null || q._1==null || q._2==null)
      throw new IllegalArgumentException("distance:p or q or a component thereof is null");
    return Math.sqrt((p._1 - q._1)*(p._1 - q._1)+(p._2 - q._2)*(p._2 - q._2));
  }

  public static void main(String[] args) {
    
    boolean connected = true;
    EuclidianDigraph g = createRandomEuclidianDigraph(5,connected);
    g.show();
    
    
    
  }

}
