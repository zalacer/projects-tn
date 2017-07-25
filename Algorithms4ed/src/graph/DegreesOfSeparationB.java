package graph;

// from text p555

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class DegreesOfSeparationB {

  public static void main(String[] args) {
    
    String[] vargs = {"movies.txt", "/", "Bacon, Kevin"};
    SymbolGraph sg = new SymbolGraph(vargs[0],vargs[1]);
    Graph G = sg.graph();
    String source = vargs[2];
    if (!sg.contains(source)) { StdOut.println(source + " not in database."); return; }
    int s = sg.indexOf(source);
    BreadthFirstPaths bfs = new BreadthFirstPaths(G, s);
    while (!StdIn.isEmpty()) {
      String sink = StdIn.readLine();
      if (sg.contains(sink)) {
        int t = sg.indexOf(sink);
        if (bfs.hasPathTo(t))
          for (int v : bfs.pathTo(t))
            StdOut.println(" " + sg.nameOf(v));
        else StdOut.println("Not connected");
      }
      else StdOut.println("Not in database.");
    }
  }
  
}
