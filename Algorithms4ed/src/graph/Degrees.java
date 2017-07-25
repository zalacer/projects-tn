package graph;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.In;

public class Degrees {
  DigraphX D;
  
  public Degrees(DigraphX d) {
    if (d == null) throw new IllegalArgumentException("Degrees: DigraphX d is null");
    D = d;
  }
  
  public int indegree(int v)  { return D.indegree(v); }
  
  public int outdegree(int v)  { return D.outdegree(v); }
  
  public Iterable<Integer> sources() { return D.sources(); }
  
  public Iterable<Integer> sinks() { return D.sinks(); }
  
  public boolean isMap() { return D.isMap(); }
  
  public static void main(String[] args) {

    In in = new In(args[0]);
    DigraphX dg = new DigraphX(in);
    System.out.println(dg);
    Degrees d = new Degrees(dg);
    System.out.println("indegree(dg(0)="+d.indegree(0));
    System.out.println("outdegree(dg(0)="+d.outdegree(0));
    System.out.print("sources="); par(toArray(d.sources().iterator()));
    System.out.print("sinks="); par(toArray(d.sinks().iterator()));
    System.out.println("isMap="+d.isMap());
    
  }

}
