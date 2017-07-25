package ex43;

import java.util.Objects;
import java.util.Scanner;

import analysis.Draw;
import ds.Seq;

/* p632
  4.3.18  Give traces that show the process of computing the MST of the graph 
  defined in Exercise 4.3.6 with the lazy version of Prim’s algorithm, the 
  eager version of Prim’s algorithm, and Kruskal’s algorithm.
  
  This is implemented with the static methods
     graph.LazyPrimMSTtrace.traceTinyEWG()
     graph.EagerPrimMSTtrace.traceTinyEWG()
     graph.KruskalMSTtrace.traceTinyEWG()
  that rely on the following static methods to display the traces as analysis.Draw
  drawings 
    graph.EuclidianGraph.showLazyPrimMSTtrace()
    graph.EuclidianGraph.showEagerPrimMSTtrace()
    graph.EuclidianGraph.showKruskalMSTtrace()
  analysis.Draw is http://introcs.cs.princeton.edu/java/stdlib/Draw.java modified
  only by addition of public JFrame frame() for external access to the JFrame 
  instance used in order to disable its visibility and dispose of it as done
  with closeDrawings() below.
  
  These methods are demonstrated with the demo() method below that provides
  interactive control using System.in and System.out. To use it just run main().
  In it the trace methods display sequences of 18, 10 and 15 drawings respectively. 
  For each trace it's drawings will remain on-screen indefinitely until you proceed.
  
 */  

public class Ex4318ShowTracesOfComputingMSTwithLazyPrimEagerPrimAndKruskalsAlgorithm {
  
  public static Seq<Draw> lazyPrimTrace() {
    return graph.LazyPrimMSTtrace.traceTinyEWG();
  }
  
  public static Seq<Draw> eagerPrimTrace() {
    return graph.EagerPrimMSTtrace.traceTinyEWG();
  }
  
  public static Seq<Draw> kruskalTrace() {
     return graph.KruskalMSTtrace.traceTinyEWG();
  }
  
  public static void closeDrawings(Seq<Draw> s) {
    for (Draw d : s) {
      d.frame().setVisible(false);
      d.frame().dispose();
    }
  }
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static String prompt = 
      "\nenter 1 to trace LazyPrimMST on tinyEWG.txt\n"
      + "enter 2 to trace EagerPrimMST on tinyEWG.txt\n"
      + "enter 3 to trace KruskalMST on tinyEWG.txt\n"
      + "enter anything else to exit\n\n";
  
  public static void demo() {
    Scanner sc = new Scanner(System.in); Seq<Draw> d = null;
    System.out.print(prompt);
    while (sc.hasNext()) {
      String r = sc.next();
      if (Objects.nonNull(d)) closeDrawings(d);
      switch (r) {
        case "1": {
          System.out.println("\nrunning LazyPrimMST trace");
          d = lazyPrimTrace();  pause(2); break; }
        case "2": {
          System.out.println("\nrunning EagerPrimMST trace");
          d = eagerPrimTrace(); pause(2); break; }
        case "3": {
          System.out.println("\nrunning KruskalMST trace");
          d = kruskalTrace();  pause(2); break; }
        default: {
          if (Objects.nonNull(d)) closeDrawings(d);
          sc.close(); System.exit(0); }
      }
      System.out.print(prompt);
    }
  }
  
  public static void main(String[] args) {
    
    demo();
 
  }

}


