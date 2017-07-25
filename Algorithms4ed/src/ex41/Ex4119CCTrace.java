package ex41;

import static v.ArrayUtils.ofDim;

import ds.Queue;
import edu.princeton.cs.algs4.StdOut;
import graph.CCTrace;
import graph.GraphX;

/* p560
  4.1.19  Show, in the style of the figure on page 545, a detailed trace of  
  CC for finding the connected components in the graph built by Graph’s 
  input stream constructor for the file tinyGex2.txt (see Exercise 4.1.2).

  My version of the tracing implementation is graph.CCTrace and is demonstrated
  below.

 */

public class Ex4119CCTrace {

  public static void main(String[] args) {

    // edges are from tinyGex2.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
    GraphX G = new GraphX(12,edges);
    CCTrace cc = new CCTrace(G);

    // number of connected components
    int m = cc.count();
    StdOut.println("\n"+m + " components:");

    // compute list of vertices in each connected component
    Queue<Integer>[] components = ofDim(Queue.class, m);
    for (int i = 0; i < m; i++) components[i] = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) components[cc.id(v)].enqueue(v);

    // print components
    for (int i = 0; i < m; i++) {
      for (int v : components[i]) StdOut.print(" " + v);
      StdOut.println();
    }
/*
    adj: [(2,6),(4,8,11),(5,6,0,3),(10,10,6,2),(1,8),(10,2),(2,3,0),(8,11),
          (1,11,7,4),(),(3,5,3),(8,7,1)]
    
                  marked[]                   id[]
                  0 1 2 3 4 5 6 7 8 9 10 11  0 1 2 3 4 5 6 7 8 9 10 11
    dfs(0)        T                          0                      
     dfs(2)       T   T                      0   0                  
      dfs(5)      T   T     T                0   0     0            
       dfs(10)    T   T     T         T      0   0     0         0  
        dfs(3)    T   T T   T         T      0   0 0   0         0  
         dfs(6)   T   T T   T T       T      0   0 0   0 0       0  
    dfs(1)        T T T T   T T       T      0 1 0 0   0 0       0  
     dfs(4)       T T T T T T T       T      0 1 0 0 1 0 0       0  
      dfs(8)      T T T T T T T   T   T      0 1 0 0 1 0 0   1   0  
       dfs(11)    T T T T T T T   T   T  T   0 1 0 0 1 0 0   1   0  1
        dfs(7)    T T T T T T T T T   T  T   0 1 0 0 1 0 0 1 1   0  1
    dfs(9)        T T T T T T T T T T T  T   0 1 0 0 1 0 0 1 1 2 0  1
    
    3 components:
     0 2 3 5 6 10
     1 4 7 8 11
     9
*/

  }

}



