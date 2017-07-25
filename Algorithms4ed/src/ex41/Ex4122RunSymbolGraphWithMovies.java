package ex41;

import ds.BagX;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import graph.BreadthFirstPathsX;
import graph.GraphX;
import graph.SymbolGraphX;

/* p560
  4.1.22 Run SymbolGraph with movies.txt to find the Kevin Bacon 
  number of this year's Oscar nominees.
  
 */

public class Ex4122RunSymbolGraphWithMovies {

  public static void main(String[] args) {

    String[] vargs = {"movies.txt", "/", "Bacon, Kevin"};
    SymbolGraphX sg = new SymbolGraphX(vargs[0],vargs[1]);
    GraphX G = sg.graph();
    BagX<Integer>[] x = G.adj();
    for (int i = 0; i < G.V(); i++) System.out.println(i+": "+x[i]);
    String source = vargs[2];
    if (!sg.contains(source)) { StdOut.println(source + " not in database."); return; }
    int s = sg.indexOf(source);
    BreadthFirstPathsX bfs = new BreadthFirstPathsX(G, s);
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
/*
    Done reading movies.txt
    Streep, Meryl
     Bacon, Kevin
     River Wild, The (1994)
     Streep, Meryl
    
    Stone, Emma
    Not in database.
    
    Portman, Natalie
     Bacon, Kevin
     Wild Things (1998)
     Dillon, Matt (I)
     Beautiful Girls (1996)
     Portman, Natalie
     
    Huppert, Isabelle
     Bacon, Kevin
     Stir of Echoes (1999)
     Dunn, Kevin (I)
     I Heart Huckabees (2004)
     Huppert, Isabelle
    
    Washinton, Denzel
    Not in database.
    
    Gosline, Ryan
    Not in database.
    
    Mortensen, Viggo
     Bacon, Kevin
     Wild Things (1998)
     Dillon, Matt (I)
     Albino Alligator (1996)
     Mortensen, Viggo
      
    Affleck, Casey
     Bacon, Kevin
     Woodsman, The (2004)
     Roehm Sr., David C.
     Ocean's Eleven (2001)
     Affleck, Casey
*/

  }

}



