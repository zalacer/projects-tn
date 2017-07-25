package graph;

import java.util.Comparator;
import java.util.Objects;

import ds.Seq;

// from http://algs4.cs.princeton.edu/44sp/DirectedEdge.java

public class DirectedEdgeX implements Cloneable { 
  private final int u;
  private final int v;
  private double w;

  public DirectedEdgeX(int u, int v, double w) {
    if (u < 0) throw new IllegalArgumentException("u < 0");
    if (v < 0) throw new IllegalArgumentException("v < 0");
    this.u = u;
    this.v = v;
    this.w = w;
  }

  public int u() { return u; }

  public int from() { return u; }
  
  public int v() { return v; }

  public int to() { return v; }

  public double w() { return w; }
  
  public double weight() { return w; }
  
  public void setW(double x) { w = x; }
  
  public void setWeight(double x) { w = x; }
  
  @Override
  public DirectedEdgeX clone() { return new DirectedEdgeX(u,v,w); }
  
  public DirectedEdgeX reverse() { return new DirectedEdgeX(v,u,w); }
  
  public NonWeightedDirectedEdgeX toNonWeightedDirectedEdgeX() {
    return new NonWeightedDirectedEdgeX(u,v);
  }

  public String toString() {
    return u + ">" + v + " " + String.format("%5.2f", w);
  }
  
  public String toString2() {
    return u + "->" + v;
  }

  @Override
  public int hashCode() { return Objects.hash(u,v,w); }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DirectedEdgeX other = (DirectedEdgeX) obj;
    if (u != other.u) return false;
    if (v != other.v) return false;
    if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w)) return false;
    return true;
  }
   
  public static Seq<NonWeightedDirectedEdgeX> convertEdges(Seq<DirectedEdgeX> ds) {
    if (ds == null || ds.isEmpty()) return new Seq<>();
    return ds.map(d -> d.toNonWeightedDirectedEdgeX());
  }
  
  public static Comparator<DirectedEdgeX> cmp = (x,y) -> {
    double d = x.w() - y.w();
    if (d < 0) return -1;
    if (d > 0) return 1;
    int i = x.from() - y.from();
    if (i < 0) return -1;
    if (i > 0) return 1;
    i = x.to() - y.to();
    if (i < 0) return -1;
    if (i > 0) return 1;
    return 0;
  };

  public static void main(String[] args) {
    DirectedEdgeX e = new DirectedEdgeX(12, 34, 5.67);
    System.out.println(e);
    
    DirectedEdgeX a = new DirectedEdgeX(1, 2, 10);
    DirectedEdgeX b = new DirectedEdgeX(2, 3, 11);
    DirectedEdgeX c = new DirectedEdgeX(3, 4, 12);
    
    Seq<DirectedEdgeX> dseq = new Seq<>();
    dseq.add(a); dseq.add(b); dseq.add(c); 
    System.out.println("dseq = "+dseq);
    Seq<NonWeightedDirectedEdgeX> nseq = convertEdges(dseq);
    System.out.println("nseq = "+nseq);

    
     

    
  }
}

