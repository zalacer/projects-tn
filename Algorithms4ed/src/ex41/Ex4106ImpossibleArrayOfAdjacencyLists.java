package ex41;

import graph.GraphEx4106;

/* p558
  4.1.6  Consider the four-vertex graph with edges 0-1, 1-2, 2-3,
  and 3-0. Draw an array of adjacency-lists that could not have been
  built calling addEdge() for these edges no matter what order.
  
  0: 1 2           0--1
  1: 2 3   gives   |\/|   (that's incorrect)
  2: 3 0           |/\|
  3: 0 1           2--3
  
  It's impossible because:
    0 is directly linked only to 1 and 3
    1 is directly linked only to 2 and 0
    2 is directly linked only to 3 and 1
    3 is directly linked only to 0 and 2
   
  In other words the graph is the cycle:
    0--1
    |  |
    3--2
  
  Using graph.GraphEx4106.generateAllPermutations(int,int,int[][]) all
  input permutations of the edges results in the following adjacency lists:
  
  [(1,3),(0,2),(1,3),(2,0)]
  [(1,3),(0,2),(1,3),(0,2)]
  [(1,3),(0,2),(1,3),(0,2)]
  [(3,1),(0,2),(1,3),(0,2)]
  [(3,1),(0,2),(3,1),(0,2)]
  [(1,3),(0,2),(3,1),(0,2)]
  [(1,3),(0,2),(3,1),(2,0)]
  [(1,3),(0,2),(3,1),(2,0)]
  [(1,3),(0,2),(3,1),(2,0)]
  [(1,3),(0,2),(3,1),(2,0)]
  [(3,1),(0,2),(3,1),(2,0)]
  [(3,1),(0,2),(3,1),(0,2)]
  [(3,1),(2,0),(3,1),(0,2)]
  [(3,1),(2,0),(3,1),(2,0)]
  [(3,1),(2,0),(3,1),(2,0)]
  [(1,3),(2,0),(3,1),(2,0)]
  [(1,3),(2,0),(1,3),(2,0)]
  [(3,1),(2,0),(1,3),(2,0)]
  [(3,1),(2,0),(1,3),(0,2)]
  [(3,1),(2,0),(1,3),(0,2)]
  [(3,1),(2,0),(1,3),(0,2)]
  [(3,1),(2,0),(1,3),(0,2)]
  [(1,3),(2,0),(1,3),(0,2)]
  [(1,3),(2,0),(1,3),(2,0)]


 */

public class Ex4106ImpossibleArrayOfAdjacencyLists {

  public static void main(String[] args) {
    
    int[][] x = new int[4][];
    x[0] = new int[]{0,1};
    x[1] = new int[]{1,2};
    x[2] = new int[]{2,3};
    x[3] = new int[]{3,0};
    GraphEx4106.generateAllPermutations(4,4,x);
 
  }

}

