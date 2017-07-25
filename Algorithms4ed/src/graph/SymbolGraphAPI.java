package graph;

// from text p548

public interface SymbolGraphAPI {

  //  SymbolGraph(String filename, String delim){} //  build graph specified in filename 
  //                                               //  using delim to separate vertex names
  boolean contains(String key); // is  key a vertex?
  int index(String key); // index associated with  key
  String name(int v); // key associated with index  v
  Graph G(); // underlying  Graph  

}
