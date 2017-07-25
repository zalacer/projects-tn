package ex42;

import graph.DigraphX;
import graph.LCA;
import v.Tuple3;

/* p598
  4.2.21 LCA of a DAG. Given a DAG and two vertices v and w, find the lowest 
  common ancestor (LCA) of v and w. The LCA of v and w is an ancestor of v and 
  w that has no descendants that are also ancestors of v and w.  Computing the 
  LCA is useful in multiple inheritance in programming languages, analysis of 
  genealogical data (find degree of inbreeding in a pedigree graph) and other 
  applications. Hint: Define the height of a vertex v in a DAG to be the length 
  of the longest path from a root to v. Among vertices that are ancestors of 
  both v and w, the one with the greatest height is an LCA of v and w.

  This is done in graph.LCA that is designed for repeated queries on the same
  graph since it first constructs an int[] of heights for all vertices from the
  lowest source and a HashSET<Integer>[] of ancestors for all pairs of vertices
  using graph.AllSimpleDirectedPaths. In addition to findLCA(int v, int w) for
  finding the LCA, if any, of v and w, it has methods for finding the LCAs of all
  pairs of vertices using the lowest and all sources and heights from all sources.
  Some of these methods are demonstrated below.

 */                                                   

public class Ex4221LCAofDAG {

  public static void main(String[] args) {

    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX d = new DigraphX(13,edges);
    LCA l = new LCA(d);
    System.out.println(" i   j  LCA");
    for (int i = 0; i < d.V()-1; i++)
      for (int j = i+1; j < d.V(); j++) {
        int lca = l.findLCA(i,j);
        if (lca != -1) System.out.printf("%2d  %2d  %2d\n", i, j, l.findLCA(i,j));
      }
    // another way to print all LCAs:
    System.out.println("\n i   j  LCA (using LCA.lcas())");
    for (Tuple3<Integer,Integer,Integer> t : l.lcas()) 
      System.out.printf("%2d  %2d  %2d\n", t._1, t._2, t._3);

  }

}



