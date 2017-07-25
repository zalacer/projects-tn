package ex41;

import static graph.RandomEuclideanGraph.createRandomEuclidianGraph;

import graph.EuclidianGraph;

/* p564
  4.1.42 Random Euclidean graphs. Write a EuclideanGraph client (see Exercise
  4.1.37) RandomEuclideanGraph that produces random graphs by generating V 
  random points in the plane, then connecting each point with all points that 
  are within a circle of radius d centered at that point. Note : The graph will 
  almost certainly be connected if d is larger than the threshold value 
  sqrt(lg(V)/PI*V) and almost certainly disconnected otherwise.
  
  Based on http://www.seas.upenn.edu/~venkates/Papers/initialconnectivity.pdf 
  (Connectivity of Metric Random Graphs), the given threshold assumes all the 
  points are in the unit disc centered at x=y=0. However that's inconvenient to 
  plot given work already done so I scaled it up by 100 and shifted the points 
  into the upper right quadrant.
  
  This is implemented with 
    graph.RandomEuclideanGraph.createRandomEuclidianGraph(int,boolean)
  and is demonstrated below.
 
 */                                                   

public class Ex4142RandomEuclideanGraphs {
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    EuclidianGraph g = null;
    
    g = createRandomEuclidianGraph(7, true);
    System.out.println("number of components = "+g.count());
    g.showCC();
     
    pause(3);
    
    g = createRandomEuclidianGraph(7, false);
    System.out.println("number of components = "+g.count());
    g.showCC();
    
  }

}



