package ex42;

import static graph.RandomEuclidianDigraph.createRandomEuclidianDigraph;

import graph.EuclidianDigraph;

/* p601
  4.2.35 Random Euclidean digraphs. Modify your solution to Exercise 4.1.42 to 
  create a EuclideanDigraph client RandomEuclideanDigraph that assigns a random 
  direction to each edge.
 
  This is implemented with 
    graph.RandomEuclidianDigraph.createRandomEuclidianDigraph(int,boolean)
  and is demonstrated below.
 
 */  

public class Ex4235RandomEuclidianDigraphs {
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) {
    
    EuclidianDigraph g = null;
    
    g = createRandomEuclidianDigraph(7, true);
    System.out.println("number of strongly connected components = "+g.count());
    g.show();
     
    pause(3);
    
    g = createRandomEuclidianDigraph(7, false);
    System.out.println("number of strongly connected components = "+g.count());
    g.show();

  }

}



