package graph;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// based on http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DegreesOfSeparation.java
// for excercise 4.1.25

public class DegreesOfSeparationWithCutoff {
  // build a SymbolGraphX from a file with a cutoff on movies by year
  // iff that file is movies.txt, then determine the degrees of sepa-
  // ration of arbitrary keys relative to a source
  
  // this class cannot be instantiated
  private DegreesOfSeparationWithCutoff() { }
  /*  Reads movies.txt, and then repeatedly reads in individuals from 
   *  standard input and prints out their degrees of separation from the
   *  source given as arg[1]. arg[2] is the age of the cutoff for movies
   *  by year.
   */
  public static void main(String[] args) {
    String filename  = "movies.txt"; 
    String delimiter = "/"; 
    String source    = args[0];
    String age       = args[1];
    if (!age.matches("\\d+")) throw new IllegalArgumentException(
        "DegreesOfSeparationOfNewMovies: args[1] doesn't match \\d+");
    int y = new Integer(age);
    SymbolGraphX sg = new SymbolGraphX(filename, delimiter, y);
    GraphX G = sg.graph();
    if (!sg.contains(source)) {
      StdOut.println(source + " not in database."); return; }
    int s = sg.indexOf(source);
    BreadthFirstPathsX bfs = new BreadthFirstPathsX(G, s);
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
