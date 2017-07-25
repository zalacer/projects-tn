package graph;

import edu.princeton.cs.algs4.StdOut;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/41graph/BaconHistogram.java
 *  http://algs4.cs.princeton.edu/41graph/BaconHistogram.java.html
 *  Compilation:  javac BaconHistogram.java
 *  Execution:    java BaconHistogram input.txt delimiter actor
 *  Dependencies: SymbolGraph.java Graph.java In.java BreadthFirstPaths.java
 *  Data files:   http://algs4.cs.princeton.edu/41graph/movies.txt
 *  
 *  Reads in a data file containing movie records (a movie followed by a list 
 *  of actors appearing in that movie), and runs breadth first search to
 *  find the shortest distance from the source (Kevin Bacon) to each other
 *  actor and movie. After computing the Kevin Bacon numbers, the programs
 *  prints a histogram of the number of actors with each Kevin Bacon number.
 *
 * 
 *  % java BaconHistogram movies.txt "/" "Bacon, Kevin"
 *    0        1
 *    1     1324
 *    2    70717
 *    3    40862
 *    4     1591
 *    5      125
 *  Inf        0
 *
 *  Remark: hard to identify actors with infinite bacon numbers because
 *  we can't tell whether an unreachable vertex is an actor or movie.
 *
 ******************************************************************************/

public class BaconHistogram {
  public static void main(String[] args) {
    String filename  = args[0];
    String delimiter = args[1];
    String source    = args[2];

    SymbolGraph sg = new SymbolGraph(filename, delimiter);
    Graph G = sg.graph();
    if (!sg.contains(source)) {
      StdOut.println(source + " not in database.");
      return;
    }

    // run breadth-first search from s
    int s = sg.indexOf(source);
    BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);


    // compute histogram of Kevin Bacon numbers - 100 for infinity
    int MAX_BACON = 100;
    int[] hist = new int[MAX_BACON + 1];
    for (int v = 0; v < G.V(); v++) {
      int bacon = Math.min(MAX_BACON, bfs.distTo(v));
      hist[bacon]++;

      // to print actors and movies with large bacon numbers
      if (bacon/2 >= 7 && bacon < MAX_BACON)
        StdOut.printf("%d %s\n", bacon/2, sg.nameOf(v));
    }

    // print out histogram - even indices are actors
    for (int i = 0; i < MAX_BACON; i += 2) {
      if (hist[i] == 0) break;
      StdOut.printf("%3d %8d\n", i/2, hist[i]);
    }
    StdOut.printf("Inf %8d\n", hist[MAX_BACON]);
  }
}

