package ex44;

/* p691
  4.4.46 Animate. Write a client program that does dynamic graphical 
  animations of Dijkstra’s algorithm.
  
  This is done by introducing a simplified  constructor and a static 
  method named animateDijkstraEx4446 in DijkstraSPXtrace that are 
  demonstrated below by drawing a map of Oldenburg using data from
  https://www.cs.utah.edu/~lifeifei/SpatialDataset.htm (#5). The map is 
  populated by drawing only newly introduced forward edges of shortest 
  paths from all vertices in interleaved increasing and decreasing order.
  
 */  

public class Ex4446AnimateDijkstrasAlgorithm {
 
  public static void main(String[] args) {
    
    graph.DijkstraSPXtrace.animateDijkstraEx4446("OldenburgEWD.txt", "OldenburgCoords.txt");
    // final output is at OldenburgMap.jpg in this project
    // for comparison see https://www.cs.utah.edu/~lifeifei/research/tpq/mapol.jpg
    // a copy of which is at OldenburgMap.utah.edu.jpg in this project
    
  }
  
  

}


