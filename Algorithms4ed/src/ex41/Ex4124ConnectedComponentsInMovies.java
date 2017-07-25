package ex41;

import graph.BaconHistogram;

/* p560
  4.1.24  Compute the number of connected components in  movies.txt, 
  the size of the largest component, and the number of components of 
  size less than 10. Find the eccentricity, diameter, radius, a center, 
  and the girth of the largest component in the graph. Does it contain 
  Kevin Bacon?
  
  This takes too much processing.
  
 */

public class Ex4124ConnectedComponentsInMovies {

  public static void main(String[] args) {

    String[] vargs = {"movies.txt", "/", "Bacon, Kevin"};
    BaconHistogram.main(vargs);
 
/*
    Done reading movies.txt
      0        1
      1     1324
      2    70717
      3    40862
      4     1591
      5      125
    Inf      655
*/

  }

}



