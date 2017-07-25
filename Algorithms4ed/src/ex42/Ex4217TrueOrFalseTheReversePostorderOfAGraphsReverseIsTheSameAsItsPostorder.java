package ex42;

import ds.Queue;
import graph.DepthFirstOrderX;
import graph.DigraphX;

/* p597
  4.2.17  True or false: The reverse postorder of a graph’s reverse is the same 
  as the post-order of the graph.
  
  It may happen that they are the same however they can be different and therefore
  generally false. See examples below;

 */                                                   

public class Ex4217TrueOrFalseTheReversePostorderOfAGraphsReverseIsTheSameAsItsPostorder {

  public static void main(String[] args) {
     
    DigraphX g = new DigraphX(4);
    g.addEdge(0,1); g.addEdge(1,2); g.addEdge(0,3); 
    DepthFirstOrderX d = new DepthFirstOrderX(g);
    System.out.println("reversePost="+d.reversePost()); 
    //reversePost=(0,1,2,3)
    DigraphX r = g.reverse();
    DepthFirstOrderX e = new DepthFirstOrderX(r);
    System.out.println("postReverse="+((Queue<Integer>)e.post()).toString2());
    //postReverse=(0,1,2,3)

    g = new DigraphX(5);
    g.addEdge(0,1); g.addEdge(1,2); g.addEdge(0,3); g.addEdge(2,4);
    d = new DepthFirstOrderX(g);
    System.out.println("\nreversePost="+d.reversePost()); 
    //reversePost=(0,1,2,4,3)
    r = g.reverse();
    e = new DepthFirstOrderX(r);
    System.out.println("postReverse="+((Queue<Integer>)e.post()).toString2());
    //postReverse=(0,1,2,3,4)

  }

}



