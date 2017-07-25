package graph;

// from text p528

public interface SearchAPI {
  
//  public Search(Graph G, int s) {} // find vertices connected to a source vertex s
  public boolean marked(int v); // is v connected to s?
  public int count(); // how many vertices are connected to s?

}
