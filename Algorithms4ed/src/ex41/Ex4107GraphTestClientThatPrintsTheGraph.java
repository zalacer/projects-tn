package ex41;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import graph.Graph;

/* p558
  4.1.7  Develop a test client for Graph that reads a graph from 
  the input stream named as command-line argument and then prints 
  it, relying on toString().
  
  The test client is this class using its main() method
  
 */

public class Ex4107GraphTestClientThatPrintsTheGraph {
  
  public static void main(String[] args) {
    
    In in = new In(args[0]);
    Graph G = new Graph(in);
    StdOut.println(G);
 
  }

}

