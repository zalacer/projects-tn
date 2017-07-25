package graph;

// from p535

public interface PathsAPI {
  // PathsAPI(Graph G, int s){}  //find paths in  G from source  s
  boolean hasPathTo(int v);  //is there a path from  s to  v ?
  Iterable<Integer> pathTo(int v);  //path from  s to  v ; null if no such path
}
