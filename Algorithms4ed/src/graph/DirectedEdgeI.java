package graph;

import java.util.Comparator;
import java.util.Objects;

import ds.Seq;

// from http://algs4.cs.princeton.edu/44sp/DirectedEdge.java
// same as DirectedEdgeX but with int weights

public class DirectedEdgeI implements Cloneable { 
  private final int u;
  private final int v;
  private int w;

  public DirectedEdgeI(int u, int v, int w) {
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

  public int w() { return w; }
  
  public int weight() { return w; }
  
  public void setW(int x) { w = x; }
  
  public void setWeight(int x) { w = x; }
  
  @Override
  public DirectedEdgeI clone() { return new DirectedEdgeI(u,v,w); }
  
  public DirectedEdgeI reverse() { return new DirectedEdgeI(v,u,w); }
  
  public NonWeightedDirectedEdgeX toNonWeightedDirectedEdgeX() {
    return new NonWeightedDirectedEdgeX(u,v);
  }

  public String toString() {
    return u + ">" + v + " " + w;
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
    DirectedEdgeI other = (DirectedEdgeI) obj;
    if (u != other.u) return false;
    if (v != other.v) return false;
    if (w != other.w) return false;
    return true;
  }
   
  public static Seq<NonWeightedDirectedEdgeX> convertEdges(Seq<DirectedEdgeI> ds) {
    if (ds == null || ds.isEmpty()) return new Seq<>();
    return ds.map(d -> d.toNonWeightedDirectedEdgeX());
  }
  
  public static Comparator<DirectedEdgeI> cmp = (x,y) -> {
    int d = x.w() - y.w();
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
    DirectedEdgeI e = new DirectedEdgeI(12, 34, 6);
    System.out.println(e);
    
    DirectedEdgeI a = new DirectedEdgeI(1, 2, 10);
    DirectedEdgeI b = new DirectedEdgeI(2, 3, 11);
    DirectedEdgeI c = new DirectedEdgeI(3, 4, 12);
    
    Seq<DirectedEdgeI> dseq = new Seq<>();
    dseq.add(a); dseq.add(b); dseq.add(c); 
    System.out.println("dseq = "+dseq);
    Seq<NonWeightedDirectedEdgeX> nseq = convertEdges(dseq);
    System.out.println("nseq = "+nseq);

    
     

    
  }
}

