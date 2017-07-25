package graph;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DegreesOfSeparation.java
// implemented using SymbolGraphOnePass

public class DegreesOfSeparationOnePass {
  // this class cannot be instantiated
  private DegreesOfSeparationOnePass() { }
  /*  Reads in a social network from a file, and then repeatedly reads in
   *  individuals from standard input and prints out their degrees of
   *  separation.
   *  Takes three command-line arguments: the name of a file,
   *  a delimiter, and the name of the distinguished individual.
   *  Each line in the file contains the name of a vertex, followed by a
   *  list of the names of the vertices adjacent to that vertex,
   */
  public static void main(String[] args) {
    String filename  = args[0];
    String delimiter = args[1];
    String source    = args[2];
//    String cutoff       = args[3];
    // StdOut.println("Source: " + source);
//    SymbolGraphOnePass sg = new SymbolGraphOnePass(filename, delimiter, new Integer(cutoff).intValue());
    SymbolGraphOnePass sg = new SymbolGraphOnePass(filename, delimiter);
    GraphX G = sg.graph();
    if (!sg.contains(source)) {
      StdOut.println(source + " not in database."); return; }
    int s = sg.indexOf(source);
    BreadthFirstPathsX bfs = new BreadthFirstPathsX(G, s);
    System.out.println("enter names to find DoS from "+source+":");
    while (!StdIn.isEmpty()) {
      String sink = StdIn.readLine();
      if (sg.contains(sink)) {
        int t = sg.indexOf(sink);
        if (bfs.hasPathTo(t))
          for (int v : bfs.pathTo(t)) StdOut.println("   " + sg.nameOf(v));
        else  StdOut.println("Not connected");
      }
      else StdOut.println("Not in database.");
    }
  }
}
