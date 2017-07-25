package ex42;

import static graph.SAP.sap;
import graph.DigraphX;

/* p598
  4.2.22 Shortest ancestral path. Given a DAG and two vertices v and w, find 
  the shortest ancestral path between v and w. An ancestral path between v 
  and w is a common ancestor x along with a shortest path from  v to  x and 
  a shortest path from w to x. The shortest ancestral path is the ancestral 
  path whose total length is minimized. Warmup: Find a DAG where the shortest 
  ancestral path goes to a common ancestor x that is not an LCA. Hint: Run BFS 
  twice, once from v and once from w.
  
  This done with the static method graphs.SAP.sap(Digraph,int,int) that's
  demonstrated below.

 */                                                   

public class Ex4222ShortestAncestralPathBetween2VerticesOfaDAG {

  public static void main(String[] args) {

    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX d = new DigraphX(13,edges);
    // print sap between 0 and 8 as a Tuple3<Integer,Iterable<Integer>,Iterable<Integer>>  
    // with components:
    // .1 commonAncestor
    // .2 shortest path from 0 to commonAncestor
    // .3 shortest path from 8 to common Ancestor
    System.out.println(sap(d,0,8)); //(6,(0,6),(8,7,6))

  }

}



