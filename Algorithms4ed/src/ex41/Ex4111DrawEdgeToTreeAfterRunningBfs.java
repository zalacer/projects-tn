package ex41;

import static graph.BreadthFirstPathsX.bfSearch;

/* p558
  4.1.11  Draw the tree represented by edgeTo[] after the call bfs(G, 0)
  in Algorithm 4.2 for the graph built by Graph’s input stream constructor 
  for the file tinyGex2.txt (see Exercise 4.1.2).
  
  Based on the output of bfSearch("tinyGex2.txt",0) shown below the tree is:
          
           0
          / \
         /   6
        2
       / \
      3   5
           \
           10
           
 */

public class Ex4111DrawEdgeToTreeAfterRunningBfs {
  
  public static void main(String[] args) {
    
    bfSearch("tinyGex2.txt",0);
/*    
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
    0 to 10 (3): 0-2-5-10
    0 to 11 (-): not connected
    
    edgeTo:[null,null,0,2,null,2,0,null,null,null,5,null]
 
*/
  }

}

