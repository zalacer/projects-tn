package graph.cycles;

import static v.ArrayUtils.*;

import java.util.List;
import java.util.Vector;

import ds.BagX;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DigraphX;

/**
 * Testfile for elementary cycle search.
 *
 * @author Frank Meyer
 *
 */
public class TestCycles {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
    int V = d.V();
    Integer[] vertices = rangeInteger(0,V);
    BagX<Integer>[] b = d.adj();
//    System.out.print("adj="); par(b);
    Seq<Seq<Integer>> seq = new Seq<>();
    for (int i = 0; i < V; i++) {
      if (b[i].size() > 0) seq.add(new Seq<Integer>(b[i].toArray()));
      else seq.add(new Seq<Integer>());
    }
//    seq.get(1).add(3);
//    System.out.println(seq);
//    int[][] adj = (int[][])unbox((Integer[][])Seq.toArrayObject(seq));
    int[][] adj = (int[][])unbox((Integer[][])seq.toArrayObject());
//    System.out.print("adj="); par(adj);
    ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(adj, vertices);
    List<Vector<Object>> cycles = ecs.getElementaryCycles();
    for (int i = 0; i < cycles.size(); i++) {
      List<Object> cycle = cycles.get(i);
      for (int j = 0; j < cycle.size(); j++) {
        Object node = cycle.get(j);
        if (j < cycle.size() - 1) {
          System.out.print(node + " -> ");
        } else {
          System.out.print(node);
        }
      }
      System.out.print("\n");
    }
    
    System.exit(0);
    
    String nodes[] = new String[10];
    boolean adjMatrix[][] = new boolean[10][10];

    for (int i = 0; i < 10; i++) {
      nodes[i] = "Node " + i;
    }

    /*adjMatrix[0][1] = true;
    adjMatrix[1][2] = true;
    adjMatrix[2][0] = true;
    adjMatrix[2][4] = true;
    adjMatrix[1][3] = true;
    adjMatrix[3][6] = true;
    adjMatrix[6][5] = true;
    adjMatrix[5][3] = true;
    adjMatrix[6][7] = true;
    adjMatrix[7][8] = true;
    adjMatrix[7][9] = true;
    adjMatrix[9][6] = true;*/
    
        adjMatrix[0][1] = true;
        adjMatrix[1][2] = true;
        adjMatrix[2][0] = true; adjMatrix[2][6] = true;
        adjMatrix[3][4] = true;
        adjMatrix[4][5] = true; adjMatrix[4][6] = true;
        adjMatrix[5][3] = true;
        adjMatrix[6][7] = true;
        adjMatrix[7][8] = true;
        adjMatrix[8][6] = true;
        
        adjMatrix[6][1] = true;

    ElementaryCyclesSearch ecs2 = new ElementaryCyclesSearch(adjMatrix, nodes);
    List<Vector<Object>> cycles2 = ecs2.getElementaryCycles();
    for (int i = 0; i < cycles2.size(); i++) {
      List<Object> cycle = cycles2.get(i);
      for (int j = 0; j < cycle.size(); j++) {
        Object node = cycle.get(j);
        if (j < cycle.size() - 1) {
          System.out.print(node + " -> ");
        } else {
          System.out.print(node);
        }
      }
      System.out.print("\n");
    }
  }

}