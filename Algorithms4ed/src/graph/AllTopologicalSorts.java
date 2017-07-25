package graph;

//import static v.ArrayUtils.*;

import ds.BagX;
import ds.Seq;
import edu.princeton.cs.algs4.In;

public class AllTopologicalSorts {
  // based on http://www.geeksforgeeks.org/all-topological-sorts-of-a-directed-acyclic-graph/
  private int V;
  private int[] indegree;
  private BagX<Integer>[] adj;
  Seq<Seq<Integer>> all;

  public AllTopologicalSorts(DigraphX g) {
    this(g,"");
    System.out.println("number of topological sorts = "+all.size());
    printAll();
  }
  
  public AllTopologicalSorts(DigraphX g, String quiet) { 
    // for use by allTopologicalSorts(DigraphX)
    if (g == null) throw new IllegalArgumentException("DigraphX is null");
    DirectedCycleX cyclefinder = new DirectedCycleX(g);
    if (cyclefinder.hasCycle()) throw new IllegalArgumentException("DigraphX isn't a DAG");
    V = g.V();
    indegree = g.indegree();
    adj = g.adj();
    all = new  Seq<Seq<Integer>>();
    topologicalSorts(new Seq<>(),new boolean[V]);
  }
  
  public void topologicalSorts(Seq<Integer> acc, boolean visited[]) {
    boolean flag = false;
    for (int i = 0; i < V; i++) {
      if (indegree[i] == 0 && !visited[i]) {
        for (Integer j : adj[i]) indegree[j]--;
        acc.add(i);
        visited[i] = true;
        topologicalSorts(acc, visited);
        // backtrack
        visited[i] = false;
        acc.delete();
        for (Integer j : adj[i]) indegree[j]++;
        flag = true;
      }
    }
    if (!flag) all.add(new Seq<Integer>(acc));
  }
  
  public void printAll() {
    if (all == null) { System.out.println("()"); return; }
    for (Seq<Integer> s : all) System.out.println(s);    
  }
  
  public Seq<Seq<Integer>> sorts() { return all; };
  
  public static Seq<Seq<Integer>> allTopologicalSorts(DigraphX g) {
    if (g == null) throw new IllegalArgumentException("DigraphX is null");
    DirectedCycleX cyclefinder = new DirectedCycleX(g);
    if (cyclefinder.hasCycle()) {
      //System.out.println("cycle="+cyclefinder.getCycle());
      throw new IllegalArgumentException("DigraphX isn't a DAG");
    }
    return (new AllTopologicalSorts(g,"")).sorts();
  }
  
  public static void main(String[] args) {
    
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
//    DigraphX d = new DigraphX(4);
//    d.addEdge(0, 1); d.addEdge(1, 2); d.addEdge(1, 3);
    new AllTopologicalSorts(d);
    

  }

}
