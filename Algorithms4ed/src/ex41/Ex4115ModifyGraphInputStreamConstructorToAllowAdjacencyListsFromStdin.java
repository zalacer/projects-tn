package ex41;


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import graph.GraphX;

/* p559
  4.1.15  Modify the input stream constructor for Graph to also allow adjacency 
  lists from standard input (in a manner similar to  SymbolGraph), as in the 
  example tinyGadj.txt shown at right. After the number of vertices and edges, 
  each line contains a vertex and its list of adjacent vertices.
  
  This is done in GraphX, demo below.
  
 */

public class Ex4115ModifyGraphInputStreamConstructorToAllowAdjacencyListsFromStdin {
  
  public static void main(String[] args) {
    
    In in; GraphX G;
    
    in = new In("tinyGex2.txt"); // read from file original format
    G = new GraphX(in);
    System.out.println("GraphX(new In(\"tinyGex2.txt\")):");
    StdOut.println(G+"\n");
    
    in = new In("tinyGadj.txt"); // read from file adj format
    G = new GraphX(in);
    System.out.println("GraphX(new In(\"tinyGadj.txt\")):");
    StdOut.println(G+"\n");
    
    in = new In(); // read from stdin
    G = new GraphX(in);
    System.out.println("GraphX(new In()):");
    StdOut.println(G);
    
/*
    Stdin test1: cut and paste the following adj format data into stdin, 
    hit enter then Ctl-Z to enter
13
13
0 1 2 5 6
3 4 5
4 5 6
7 8
9 10 11 12
11 12

    Stdintest2:  cut and paste the following original format data into stdin, 
    hit enter then Ctl-Z to enter
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
*/
    
    
  }

}

