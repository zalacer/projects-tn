package graph;

import java.util.Objects;

// from DirectedEdge.java without weight

public class NonWeightedDirectedEdgeX { 
  private final int u;
  private final int v;

  public NonWeightedDirectedEdgeX(int u, int v) {
    if (u < 0) throw new IllegalArgumentException("u < 0");
    if (v < 0) throw new IllegalArgumentException("v < 0");
    this.u = u;
    this.v = v;
  }

  public int u() { return u; }

  public int from() { return u; }
  
  public int v() { return v; }

  public int to() { return v; }


  public String toString() {
    return u + "->" + v;
  }

  @Override
  public int hashCode() { return Objects.hash(u,v); }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NonWeightedDirectedEdgeX other = (NonWeightedDirectedEdgeX) obj;
    if (u != other.u) return false;
    if (v != other.v) return false;
    return true;
  }

  public static void main(String[] args) {
    NonWeightedDirectedEdgeX e = new NonWeightedDirectedEdgeX(12, 34);
    System.out.println(e);
  }
}

