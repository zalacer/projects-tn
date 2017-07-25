package ex42;

import graph.DigraphX;

/* p596
  4.2.4  Add a method hasEdge() to Digraph which takes two int
  arguments v and w and returns true if the graph has an edge  
  v->w, false otherwise.
  
  hasEdge() has been addeded to graph.Digraph and graph.DigraphX and is
  demonstrated for the latter below.
  
 */                                                   

public class Ex4204DigraphHasEdgeMethod {

  public static void main(String[] args) {
    
    // edges from tinyDGex2.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
    DigraphX d = new DigraphX(12, edges);
    String[] e = edges.split("\\s+");
    for (int i = 0; i < e.length-1; i+=2)
      assert d.hasEdge(Integer.parseInt(e[i]),Integer.parseInt(e[i+1]));
  }

}



