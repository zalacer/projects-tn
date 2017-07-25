package graph;

import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.unbox;

import java.util.List;
import java.util.Vector;

import ds.BagX;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.cycles.ElementaryCyclesSearch;

public class AllSimpleDirectedCycles {
  Seq<Seq<Integer>> all;
  
  public AllSimpleDirectedCycles(DigraphX g) {
    this(g,"");
    System.out.println("number of directed simple cycles = "+all.size());
    printAll();
  }
  
  public AllSimpleDirectedCycles(DigraphX g, String quiet) {
    // for use by allSimpleDirectedCycles(DigraphX)
    if (g == null) throw new IllegalArgumentException("DigraphX is null");
    int V = g.V();
    Integer[] vertices = rangeInteger(0,V);
    BagX<Integer>[] b = g.adj();
    Seq<Seq<Integer>> seq = new Seq<>();
    for (int i = 0; i < V; i++) {
      if (b[i].size() > 0) seq.add(new Seq<Integer>(b[i].toArray()));
      else seq.add(new Seq<Integer>());
    }
    int[][] adj = (int[][])unbox((Integer[][])seq.toArrayObject());
    allSimpleDirectedCycles(adj,vertices);
  }
  
  public void allSimpleDirectedCycles(int[][] adj, Integer[] vertices) {
    ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(adj, vertices);
    List<Vector<Object>> cycles = ecs.getElementaryCycles();
    int size = cycles.size();
    all = new Seq<Seq<Integer>>(size);
    for (int i = 0; i < size; i++) {
      if (all.size() <= i) all.add(new Seq<Integer>());
      for (Object o : cycles.get(i)) all.get(i).add((Integer)o);
      all.get(i).add((Integer)cycles.get(i).get(0));
    }  
  }
 
  public void printAll() {
    if (all == null) { System.out.println("()"); return; }
    for (Seq<Integer> s : all) System.out.println(s);    
  }
  
  public Seq<Seq<Integer>> cycles() { return all; };
  
  public static Seq<Seq<Integer>> allSimpleDirectedCycles(DigraphX g) {
    if (g == null) throw new IllegalArgumentException("DigraphX is null");
    return (new AllSimpleDirectedCycles(g,"")).cycles();
  }
  
  public static void main(String[] args) {

    In in = new In(args[0]);
    DigraphX g = new DigraphX(in);
    new AllSimpleDirectedCycles(g);
//    Seq<Seq<Integer>> cycles = allSimpleDirectedCycles(g);
//    for (Seq<Integer> s : cycles) System.out.println(s);
    
  }

}
