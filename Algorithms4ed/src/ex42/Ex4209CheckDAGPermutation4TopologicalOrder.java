package ex42;

import static v.ArrayUtils.*;

import java.util.Arrays;

import ds.Seq;
import graph.AllTopologicalSorts;
import graph.DigraphX;
import graph.DirectedCycleX;

/* p597
  4.2.9  Write a method that checks whether or not a given permutation of a 
  DAG's vertices is a topological order of that DAG.
  
  This is done below relying on graph.AllTopologicalSorts after preliminary
  filtering.
   
 */                                                   

public class Ex4209CheckDAGPermutation4TopologicalOrder {
  
  public static boolean isTopologicalOrder(DigraphX g, int[] order) {
    if (g == null) throw new IllegalArgumentException("DigraphX is null");
    if (order == null) throw new IllegalArgumentException("order array is null");
    int V = g.V();
    if (order.length != V) return false;
    int[] oclone = order.clone(); Arrays.sort(oclone);
    if (!Arrays.equals(oclone,range(0,V))) return false;
    DirectedCycleX finder = new DirectedCycleX(g);
    if (finder.hasCycle()) return false;
    // 1st vertex in order must be a source:
    if (g.indegree()[order[0]] != 0) return false;
    // last vertex in order must be a sink:
    if (g.adj()[order[order.length-1]].size() != 0) return false;
    Seq<Seq<Integer>> sorts = AllTopologicalSorts.allTopologicalSorts(g);
    Seq<Integer> ord = new Seq<>((Integer[]) box(order));
    return sorts.contains(ord);
  }

  public static void main(String[] args) {
    
    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX g = new DigraphX(13,edges);
    int[] order1 = {2,0,1,3,5,8,7,6,4,9,10,11,12};
    System.out.println(isTopologicalOrder(g,order1));
    int[] order2 = {8,7,2,3,0,6,9,11,12,10,5,4,1};
    System.out.println(isTopologicalOrder(g,order2));
    int[] order3 = {8,7,3,2,0,6,9,11,12,10,5,4,1};
    System.out.println(isTopologicalOrder(g,order3));

  }

}



