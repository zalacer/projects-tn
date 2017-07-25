package ex41;

import static graph.BreadthFirstPathsX.bfSearch;

/* p559
  4.1.14  Suppose you use a stack instead of a queue when running 
  breadth-first search. Does it still compute shortest paths?
  
  In some cases but unreliably and consistency checking fails. 
  In particular the graph adj array has edge 2-5 but BFS with stack
  reports the distance from 0 to 2 is 1 while the distance from 0 to
  5 is 4 while the latter should be 1 due to edge 2-5.
 
  The outputs of running BFS with a queue and with a stack are
  shown below after the commands that produced them.
  
 */

public class Ex4114DoesStackWorkforBfs {
  
  public static void main(String[] args) {
    
    System.out.println("bfSearch(\"tinyGex2.txt\",0,\"s\"):");
    bfSearch("tinyGex2.txt",0,"s"); // using stack results in consistency errors
/*
    check() messages:
    edge 2-5
    distTo[2] = 1
    distTo[5] = 4
    check returned false
    
    Paths:
    0 to 0 (0):  0
    0 to 1 (-):  not connected
    0 to 2 (1):  0-2
    0 to 3 (2):  0-6-3
    0 to 4 (-):  not connected
    0 to 5 (4):  0-6-3-10-5
    0 to 6 (1):  0-6
    0 to 7 (-):  not connected
    0 to 8 (-):  not connected
    0 to 9 (-):  not connected
    0 to 10 (3): 0-6-3-10
    0 to 11 (-): not connected
    
    edgeTo:[null,null,0,6,null,10,0,null,null,null,3,null]
    G.adj:[(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),
              (8,11),(1,11,7,4),(),(3,5,3),(8,7,1)]
*/
    
    System.out.println("\nbfSearch(\"tinyGex2.txt\",0):");
    bfSearch("tinyGex2.txt",0); // using a queue all ok
/*
    check() messages:
    check returned true
     
    Paths:
    0 to 0 (0):  0
    0 to 1 (-):  not connected
    0 to 2 (1):  0-2
    0 to 3 (2):  0-2-3
    0 to 4 (-):  not connected
    0 to 5 (2):  0-2-5
    0 to 6 (1):  0-6
    0 to 7 (-):  not connected
    0 to 8 (-):  not connected
    0 to 9 (-):  not connected
    0 to 10 (3):  0-2-5-10
    0 to 11 (-):  not connected
    
    edgeTo:[null,null,0,2,null,2,0,null,null,null,5,null]
    G.adj:[(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),(8,11),
              (1,11,7,4),(),(3,5,3),(8,7,1)]
*/
    
  }

}

