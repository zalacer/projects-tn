package graph;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Edge.java

// for trace programs LazyPrimMSTtrace, EagerPrimMSTtrace

import edu.princeton.cs.algs4.StdOut;

public class EdgeY implements Comparable<EdgeY> { 
  private final int u;
  private final int v;
  private final double w;

  public EdgeY(int u, int v, double w) {
    if (u < 0) throw new IllegalArgumentException("u must be a nonnegative integer");
    if (v < 0) throw new IllegalArgumentException("v must be a nonnegative integer");
    if (Double.isNaN(w)) throw new IllegalArgumentException("w is NaN");
    this.u = u;
    this.v = v;
    this.w = w;
  }
  
  public int u() { return u; }
  
  public int v() { return v; }
  
  public int from() { return u; }
  
  public int to() { return v; }
  
  public double w() { return w; }

  public double weight() { return w; }

  public int either() { return u; }

  public int other(int vertex) {
    if      (vertex == u) return v;
    else if (vertex == v) return u;
    else throw new IllegalArgumentException("Illegal endpoint");
  }

  @Override
  public int compareTo(EdgeY that) {
    return Double.compare(this.w, that.w);
  }

  public String toString() {
    return String.format("(%d-%d,%.2f)",u,v,w);
  }
  
  public String toString2() {
    return String.format("%d-%d,%.17f",u,v,w);
  }
  
  public String toString3() {
    return u+"-"+v;
  }
  
  public String toString4() {
    return String.format("(%d-%d, %.2f)",u,v,w);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + u;
    result = prime * result + v;
    long temp;
    temp = Double.doubleToLongBits(w);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    EdgeY other = (EdgeY) obj;
    if (u != other.u) return false;
    if (v != other.v) return false;
    if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
      return false;
    return true;
  }

  public static void main(String[] args) {
    EdgeY e = new EdgeY(12, 34, 5.67);
    StdOut.println(e);
  }
}
