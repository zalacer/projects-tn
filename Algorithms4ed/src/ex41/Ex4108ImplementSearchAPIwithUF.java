package ex41;

/* p558
  4.1.8  Develop an implementation for the Search API on page 528 that 
  uses UF, as described in the text.
  
  Search API:
  public Search(Graph G, int s) {} // find vertices connected to a source vertex s
  public boolean marked(int v);    // is v connected to s?
  public int count();              // how many vertices are connected to s?
  
  Use of UF from text p 529:
  We have already seen one way to implement the Search API: the union-find algo-
  rithms of Chapter 1. The constructor can build a UF object, do a union() operation
  for each of the graph’s edges, and implement marked(v) by calling  connected(s, v) .
  Implementing count() requires using a weighted UF implementation and extending
  its API to use a count() method that returns wt[find(v)] (see Exercise 4.1.8). 
  
  A solution is implemented in graph.UFSearch. It would of course be better to not
  create a Graph just for search if UF is going to be used.
  
 */

public class Ex4108ImplementSearchAPIwithUF {
  
  public static void main(String[] args) {

  }

}

