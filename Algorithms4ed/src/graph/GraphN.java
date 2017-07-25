package graph;

//AlgorithmsInJava-3edPt5-GraphAlgorithms-Sedgewick-2003.pdf pp40-42 program 17.9-10

public class GraphN {
  private int Vcnt, Ecnt;
  private boolean digraph;
  public class Node { 
    int v; Node next;
   Node(int x, Node t) { v = x; next = t; }
   public int v() { return v; }
   public void setV(int q) { v = q; }
  }
  private Node adj[];
  
  GraphN(int V, boolean flag) {
    Vcnt = V; Ecnt = 0; digraph = flag;
    adj = new Node[V];
  }
  
  public int V() { return Vcnt; }
  public int E() { return Ecnt; }
  public Node[] adj() { return adj; }
  public boolean directed() { return digraph; }
  public void insert(EdgeX e) {
    int u = e.u(), v = e.v();
    adj[u] = new Node(v, adj[u]);
    if (!digraph) adj[v] = new Node(u, adj[v]);
    Ecnt++;
  }
  
  AdjList adj(int v) { return new AdjLinkedList(v); }
  
  private class AdjLinkedList implements AdjList {
    private int v;
    private Node t;
    AdjLinkedList(int v) { this.v = v; t = null; }
    public int beg() { t = adj[v]; return t == null ? -1 : t.v; }
    public int nxt() { if (t != null) t = t.next; return t == null ? -1 : t.v; }
    public boolean end() { return t == null; }
  }

//  public static void main(String[] args) {
//
//  }

}
