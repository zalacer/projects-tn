package ex42;

import static graph.RandomSparseDigraph.createRandomSparseDigraph;

import graph.DigraphX;
import graph.EuclidianDigraph;

/* p601
  4.2.34 Random sparse digraphs. Modify your solution to Exercise 4.1.41 to create
  a program RandomSparseDigraph that generates random sparse digraphs for a well-
  chosen set of values of V and E that can be used to run empirical tests.
 
  The implementation is graph.RandomSparseDigraph.createRandomSparseDigraph(int) 
  and is demonstrated below.
 
 */  


public class Ex4234RandomSparseDigraphs {
  
  public static void main(String[] args) {
    
    // this constructor generates random vertex coordinates
    DigraphX g = createRandomSparseDigraph(7);
    System.out.println(g);
    EuclidianDigraph e = new EuclidianDigraph(g);
    e.show();

    

  }

}



