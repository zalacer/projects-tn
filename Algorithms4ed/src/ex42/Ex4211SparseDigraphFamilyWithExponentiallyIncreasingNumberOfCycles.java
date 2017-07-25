package ex42;

import static v.ArrayUtils.*;

import graph.AllTopologicalSorts;
import graph.DigraphX;
import graph.TopologicalX;

/* p597
  4.2.11  Describe a family of sparse digraphs whose number of directed 
  cycles grows exponentially in the number of vertices.
  
  Such a family can be constructed starting with an n-cycle and doubling
  every edge possibly also with crosslinking the parallel edges.
  
  See 
    https://stackoverflow.com/questions/32650192/what-is-a-family-of-digraphs-whose-number-of-directed-cycles-grows-exponentially
  and http://theory.cs.princeton.edu/complexity/book.pdf p189.
 */                                                   

public class Ex4211SparseDigraphFamilyWithExponentiallyIncreasingNumberOfCycles {

  public static void main(String[] args) {
    
    DigraphX g = new DigraphX(2);
    g.addEdge(0,1);
    System.out.println(AllTopologicalSorts.allTopologicalSorts(g)); //((0,1))
    TopologicalX t = new TopologicalX(g);
    par(toArray(t.order().iterator())); //[0,1]
    
    g = new DigraphX(4);
    g.addEdge(0,1); g.addEdge(0,2); g.addEdge(2,3);
    System.out.println("\n"+AllTopologicalSorts.allTopologicalSorts(g));
    // ((0,1,2,3),(0,2,1,3),(0,2,3,1))
    t = new TopologicalX(g);
    par(toArray(t.order().iterator())); //[0,1,2,3]
    
    g = new DigraphX(4);
    g.addEdge(0,2); g.addEdge(2,3); g.addEdge(0,1); 
    System.out.println("\n"+AllTopologicalSorts.allTopologicalSorts(g)); 
    // ((0,1,2,3),(0,2,1,3),(0,2,3,1))
    t = new TopologicalX(g);
    par(toArray(t.order().iterator())); //[0,2,3,1]
    
  }

}



