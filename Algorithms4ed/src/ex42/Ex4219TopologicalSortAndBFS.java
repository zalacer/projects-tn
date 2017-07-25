package ex42;

import ds.Queue;
import ds.Seq;
import graph.AllTopologicalSorts;
import graph.DigraphX;
import graph.TopologicalBFSWrong;
import graph.TopologicalIterativeX;

/* p598
  4.2.19 Topological sort and BFS. Explain why the following algorithm does not 
  necessarily produce a topological order: Run BFS, and label the vertices by 
  increasing distance to their respective source.
  
  This is an idiotic question because it's vague about how BFS is instrumented and
  used. Fact is that BFS can be used to do reliable topological sorting as shown by
  http://algs4.cs.princeton.edu/42digraph/TopologicalX.java that is available 
  locally at graph.TopologicalIterative for Digraph and graph.TopologicalIterativeX 
  for DigraphX. The key to making it work is to start with a source and at every 
  iteration over the adj list of the current vertex decrease the indegree of the 
  chosen vertex and enqueue it only if it's a source (indegree == 0). If that isn't 
  done then the method seems to always produce an order consisting of the vertices in 
  increasing numerical order that may not be a topological sort. This is illustrated
  below. An obvious issue with BFS is finding a vertex and putting it in the order too
  soon because it has a link from an earlier vertex in the order that is at or near 
  the beginning of the adjacency list of that vertex. Avoiding that circumstance is
  what TopologicalX does.
  
  Instead of this exercise it would be more constructive to ask how can BFS be used
  to do topological sorting correctly. 
 */                                                   

public class Ex4219TopologicalSortAndBFS {
 
  public static void main(String[] args) {
    
    DigraphX g; 
    Seq<Seq<Integer>> sorts; 
    TopologicalBFSWrong wrong; 
    TopologicalIterativeX right;
    Seq<Integer> order; 
    Integer[] a;
    
    System.out.println("demo with a very small DAG:");
    System.out.println("=========================================================");
    g = new DigraphX(4);
    g.addEdge(0,2);  g.addEdge(1,0); g.addEdge(0,3); g.addEdge(2,3);
    System.out.println(g);
    
    System.out.println("all topological sorts:");
    sorts = AllTopologicalSorts.allTopologicalSorts(g);
    for (Seq<Integer> s : sorts) System.out.println(s);
    
    System.out.println("\nwrong topological order using BFS incorrectly:");
    wrong = new TopologicalBFSWrong(g);
    a = ((Queue<Integer>)wrong.order()).toArray(1);
    order = new Seq<>(a);
    System.out.println("order="+order);
    System.out.println("TopologicalBFSWrong.order is a topological order = "
        +sorts.contains(order));
    
    System.out.println("\nright topological order using BFS correctly:");
    right = new TopologicalIterativeX(g);
    a = ((Queue<Integer>)right.order()).toArray(1);
    order = new Seq<>(a);
    System.out.println("order="+order);
    System.out.println("TopologicalIterativeX.order is a topological order = "
        +sorts.contains(order));
    
    System.out.println("\ndemo with tinyDAG.txt:");
    System.out.println("=========================================================");
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    g = new DigraphX(13,edges);
    System.out.println(g);
    
    sorts = AllTopologicalSorts.allTopologicalSorts(g);
    
    System.out.println("wrong topological order using BFS incorrectly:");
    wrong = new TopologicalBFSWrong(g);
    a = ((Queue<Integer>)wrong.order()).toArray(1);
    order = new Seq<>(a);
    System.out.println("order="+order);
    System.out.println("TopologicalBFSWrong.order is a topological order = "
        +sorts.contains(order));
    
    System.out.println("\nright topological order using BFS correctly:");
    right = new TopologicalIterativeX(g);
    a = ((Queue<Integer>)right.order()).toArray(1);
    order = new Seq<>(a);
    System.out.println("order="+order);
    System.out.println("TopologicalIterativeX.order is a topological order = "
        +sorts.contains(order));
  }
/*
    demo with a very small DAG:
    =========================================================
    4 vertices, 4 edges 
    0: 3 2 
    1: 0 
    2: 3 
    3: 
    
    all topological sorts:
    (1,0,2,3)
    
    wrong topological order using BFS incorrectly:
    order=(0,1,2,3)
    TopologicalBFSWrong.order is a topological order = false
    
    right topological order using BFS correctly:
    order=(1,0,2,3)
    TopologicalIterativeX.order is a topological order = true
    
    demo with tinyDAG.txt:
    =========================================================
    13 vertices, 15 edges 
    0: 5 1 6 
    1: 
    2: 0 3 
    3: 5 
    4: 
    5: 4 
    6: 9 4 
    7: 6 
    8: 7 
    9: 11 10 12 
    10: 
    11: 12 
    12: 
    
    wrong topological order using BFS incorrectly:
    order=(0,1,2,3,4,5,6,7,8,9,10,11,12)
    TopologicalBFSWrong.order is a topological order = false
    
    right topological order using BFS correctly:
    order=(2,8,0,3,7,1,5,6,9,4,11,10,12)
    TopologicalIterativeX.order is a topological order = true  
*/  
}



