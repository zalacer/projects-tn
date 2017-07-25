package ex44;

import static graph.EdgeWeightedDigraphGrid.*;

import graph.DijkstraSourceSinkSPEEWD;
import graph.EuclidianEdgeWeightedDigraph;

/* p689 
  4.4.33 Shortest path in a grid. Given an N-by-N matrix of positive integers, 
  find the shortest path from the (0, 0) entry to the (N-1, N-1) entry, where 
  the length of the path is the sum of the integers in the path. Repeat the 
  problem but assume you can only move right and down.
*/  

public class Ex4433ShortestPathInAGrid {
  

  public static void main(String[] args) {
    
    // completeVertexWeightedGrid(int) includes up, down, left, right,
    //     diagonal up and diagonal down edges    
    // rightDownVertexWeightedGrid(int) includes only right and down edges
    
    // using Euclidian graph since it's easy to demonstrate grid is good
    // visually -- run EdgeWeightedDigraphGrid.main() that runs
    // EdgeWeightedDigraphGrid.gridDemo() to see.
    EuclidianEdgeWeightedDigraph G; DijkstraSourceSinkSPEEWD sp;
    
    G = completeVertexWeightedGrid(5);
    sp = new DijkstraSourceSinkSPEEWD(G,0,G.V()-1);
    sp.printSouceSinkPath();
    // shortest path from 0 to 24: (60.00) 0>6>12>18>24
    
    G = rightDownVertexWeightedGrid(5);
    sp = new DijkstraSourceSinkSPEEWD(G,0,G.V()-1);
    sp.printSouceSinkPath();
    // shortest path from 0 to 24: (76.00) 0>1>2>3>4>9>14>19>24

  }

}


