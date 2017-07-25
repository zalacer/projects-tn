package ex42;

import graph.EuclidianDigraph;
import graph.DigraphX;

/* p601
  4.2.32 Random digraphs. Write a program ErdosRenyiDigraph that takes 
  integer values V and E from the command line and builds a digraph by 
  generating E random pairs of integers between 0 and V-1. Note: This 
  generator produces self-loops and parallel edges.
  
  This is implemented as a constructor in DigraphX and demonstrated below.
  
 */  


public class Ex4232RandomDigraphsErdosRenyiDigraph {
  
  public static void main(String[] args) {
    
    // this constructor generates random vertex coordinates
    EuclidianDigraph g = new EuclidianDigraph(new DigraphX(9,7));
    g.showLabelled(.5,null); 
    

  }

}



