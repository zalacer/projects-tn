package ex42;

import graph.DigraphX;
import graph.TransitiveClosureX;

/* p597
  4.2.14  Prove that the strong components in the reverse of G are the same as in G.
  
  This follows immediately because strong connectivity is an equivilance
  relation and therefore reflexive, as mentioned on p584 of the text.
  
 */                                                   

public class Ex4214ProveThatADigraphAndItsReverseHaveTheSameStrongComponents {

  public static void main(String[] args) {
    
    // TransitiveClosureX constructor updates the input graph with added edges
    
    String edges = "3->7 1->4 7->8 0->5 5->2 3->8 2->9 0->6 4->9 2->6 6->4"
        .replaceAll("->", " ");
    DigraphX G = new DigraphX(10,edges);
    System.out.println("G:\n"+G);
    new TransitiveClosureX(G);
    System.out.println("TransitiveClosureX(G):\n"+G);
/*
    G:
    10 vertices, 11 edges 
    0: 6 5 
    1: 4 
    2: 6 9 
    3: 8 7 
    4: 9 
    5: 2 
    6: 4 
    7: 8 
    8: 
    9: 
    
    TransitiveClosureX(G):
    10 vertices, 30 edges 
    0: 9 4 2 0 6 5 
    1: 9 1 4 
    2: 4 2 6 9 
    3: 3 8 7 
    4: 4 9 
    5: 9 6 5 4 2 
    6: 9 6 4 
    7: 7 8 
    8: 8 
    9: 9 
    
*/    
    
  }

}



