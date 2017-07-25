package ex41;

import static graph.BreadthFirstPathsX.bfSearch;

/* p559
  4.1.12  What does the BFS tree tell us about the  distance from v 
  to w when neither is at the root?
  
  Since BFS is about shortest paths it gives a shortest path between
  v and w using vertices connected to the root of the search, but not
  necessarily an absolute shortest path between determined by using v 
  or w as the root of the search. This is interesting since it shows
  a way of modeling suboptimal behaviour that may account for a wide
  variety of behaviour.
  
  For example a shortest path between 6 and 10 using only vertices
  connected to 0 is 6-0-2-5-10, however an actually shortest path from
  6 to 10 is 6-3-10 as shown by running bfSearch("tinyGex2.txt",6) or
  bfSearch("tinyGex2.txt",10).
  
  Tree from running bfSearch("tinyGex2.txt",0):
          
           0
          / \
         /   6
        2
       / \
      3   5
           \
           10
           
  Tree from running bfSearch("tinyGex2.txt",6):
   
           6
          /|\
         / | 3
        0  2  \
           |  10
           5 
           
  Tree from running bfSearch("tinyGex2.txt",10):
   
           10
          /  \
         /    5
        3   
       / \
      2   6    
           \        
            0
   
 */

public class Ex4112BfsDistanceBetween2Vertices {
  
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
    
    bfSearch("tinyGex2.txt",6);
/*    
    Paths:
    6 to 0 (1):  6-0
    6 to 1 (-):  not connected
    6 to 2 (1):  6-2
    6 to 3 (1):  6-3
    6 to 4 (-):  not connected
    6 to 5 (2):  6-2-5
    6 to 6 (0):  6
    6 to 7 (-):  not connected
    6 to 8 (-):  not connected
    6 to 9 (-):  not connected
    6 to 10 (2):  6-3-10
    6 to 11 (-):  not connected
    
    edgeTo:[6,null,6,6,null,2,null,null,null,null,3,null]   
*/  
    
    bfSearch("tinyGex2.txt",10);
/*    
    Paths:
    10 to 0 (3):  10-3-6-0
    10 to 1 (-):  not connected
    10 to 2 (2):  10-3-2
    10 to 3 (1):  10-3
    10 to 4 (-):  not connected
    10 to 5 (1):  10-5
    10 to 6 (2):  10-3-6
    10 to 7 (-):  not connected
    10 to 8 (-):  not connected
    10 to 9 (-):  not connected
    10 to 10 (0):  10
    10 to 11 (-):  not connected
    
    edgeTo:[6,null,3,10,null,10,3,null,null,null,null,null]

*/    
    
  }

}

