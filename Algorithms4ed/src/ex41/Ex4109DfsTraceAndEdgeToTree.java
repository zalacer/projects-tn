package ex41;

import static graph.DepthFirstTrace.trace;

/* p558
  4.1.9  Show, in the style of the figure on page 533, a detailed trace 
  of the call dfs(0) for the graph built by Graph’s input stream con-
  structor for the file tinyGex2.txt (see Exercise 4.1.2). Also, draw 
  the tree represented by edgeTo[].
  
  My version of the trace is given by running DepthFirstTrace.trace("tinyGex2.txt",0)
  that is demonstrated below.
  
  Based on its output, the tree represented by edgeTo[] and more directly by
  DepthFirstTrace.pathTo() is 0-2-5-10-3-6.
    
 */

public class Ex4109DfsTraceAndEdgeToTree {
  
  public static void main(String[] args) {
    
    trace("tinyGex2.txt",0);
/*
      adj: [(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),(8,11),(1,11,7,4),(),(3,5,3),(8,7,1)]
      
                  marked[]
                  0 1 2 3 4 5 6 7 8 9 10 11
      dfs(0)      T                      
       dfs(2)     T   T                  
        dfs(5)    T   T     T            
         dfs(10)  T   T     T         T  
          dfs(3)  T   T T   T         T  
           dfs(6) T   T T   T T       T  
      
      edgeTo: [null,null,0,10,null,2,3,null,null,null,5,null]
      
      paths:  0 to 0:  0
              0 to 2:  0-2
              0 to 3:  0-2-5-10-3
              0 to 5:  0-2-5
              0 to 6:  0-2-5-10-3-6
              0 to 10: 0-2-5-10
*/
    
  }

}

