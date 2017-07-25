package ex41;

import static v.ArrayUtils.*;

import ds.BagX;
import graph.GraphX;

/* p558
  4.1.2  Draw, in the style of the figure in the text (page 524), the ad-
  jacency lists built by Graph's input stream constructor for the file
  tinyGex2.txt with contents:
  12
  16
  8 4
  2 3
  1 11
  0 6
  3 6
  10 3
  7 11
  7 8
  11 8
  2 0
  6 2
  5 2
  5 10
  3 10
  8 1
  4 1

  I got the following running the given data through graph.GraphX:
   0: 2,6
   1: 4,8,11
   2: 5,6,0,3
   3: 10,10,6,2
   4: 1,8
   5: 10,2
   6: 2,3,0
   7: 8,11
   8: 1,11,7,4
   9: 
  10: 3,5,3
  11: 8,7,1

 */

public class Ex4102AdjacencyLists {


  public static void main(String[] args) {

    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
    GraphX g = new GraphX(12, edges);
    BagX<Integer>[] ba = g.adj();
    par(ba);
    // [(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),(8,11),(1,11,7,4),(),
    //  (3,5,3),(8,7,1)]
    for (int i = 0; i < ba.length; i++)
      System.out.printf("  %2d: %s\n", i, ba[i].showToString());
  }
  
}

