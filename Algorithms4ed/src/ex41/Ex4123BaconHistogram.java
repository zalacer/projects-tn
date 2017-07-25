package ex41;

import graph.BaconHistogram;

/* p560
  4.1.23  Write a program  BaconHistogram that prints a histogram 
  of Kevin Bacon numbers, indicating how many performers from  
  movies.txt have a Bacon number of 0, 1, 2, 3, ... . Include a 
  category for those who have an infinite number (not connected
  to Kevin Bacon).
  
  
  
 */

public class Ex4123BaconHistogram {

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



