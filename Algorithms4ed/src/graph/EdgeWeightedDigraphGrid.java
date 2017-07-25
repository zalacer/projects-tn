package graph;

import static v.ArrayUtils.*;

import java.util.Scanner;

import ds.RandomBag;

// for ex4433

@SuppressWarnings("unused")
public class EdgeWeightedDigraphGrid {
  
  public static class Connection {
    int p;
    int q;
    public Connection(int p, int q) { 
      this.p = p; this.q = q; 
     }
    public int p() { return p; }
    public int q() { return q; }
    @Override
    public String toString() {
      return p+"-"+q;
    }
  }
  
  public static Connection[] generateComplete(int N) {
    // return an array of all adjacent connections for an N-by-N grid   
    int n = N;
    if (n < 2) throw new IllegalArgumentException("generate: N must be > 1");
    
    //                                       adjacent    diagonal
    Connection[] c = ofDim(Connection.class, 4*n*(n-1) + 4*(n-1)*(n-1));
    
    int[][] z = new int[n][n];
    int offset = 0;
    for (int i = 0; i < n; i++) {
      z[i] = range(offset, offset+n);
      offset = n*(i+1);
    }
    
    int k = 0; // index for c
    
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n-1; j++) {
        // adjacent sideways right and left
        c[k++] = new Connection(z[i][j], z[i][j+1]);
        c[k++] = new Connection(z[i][j+1], z[i][j]);
      }
    
    for (int i = 0; i < n-1; i++) 
      for (int j = 0; j < n; j++) {
        // adjacent down and up
          c[k++] = new Connection(z[i][j], z[i+1][j]);
          c[k++] = new Connection(z[i+1][j], z[i][j]);
        
        if (j < n-1) { // right diagonal down and left diagonal up
            c[k++] = new Connection(z[i][j], z[i+1][j+1]);
            c[k++] = new Connection(z[i+1][j+1], z[i][j]);
        }
        
        if (j > 0) {// left diagonal down and right diagonal up
            c[k++] = new Connection(z[i][j], z[i+1][j-1]);
            c[k++] = new Connection(z[i+1][j-1], z[i][j]);
        }
      }

    RandomBag<Connection> bag = new  RandomBag<Connection>(c);
    return bag.toArray(c[0]);
  }
  
  public static Connection[] generateRightDown(int N) {
    // return an array of  only right and down connections for an N-by-N grid 
    int n = N;
    if (n < 2) throw new IllegalArgumentException("generate: N must be > 1");
    
    //                                       right and down  
    Connection[] c = ofDim(Connection.class, 2*n*(n-1));
    
    int[][] z = new int[n][n];
    int offset = 0;
    for (int i = 0; i < n; i++) {
      z[i] = range(offset, offset+n);
      offset = n*(i+1);
    }
    
    int k = 0; // index for c
    
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n-1; j++) {
        // adjacent sideways right
        c[k++] = new Connection(z[i][j], z[i][j+1]);
      }
    
    for (int i = 0; i < n-1; i++) 
      for (int j = 0; j < n; j++) {
        // adjacent down
          c[k++] = new Connection(z[i][j], z[i+1][j]);
      }

    RandomBag<Connection> bag = new  RandomBag<Connection>(c);
    return bag.toArray(c[0]);
  }

  public static EuclidianEdgeWeightedDigraph completeVertexWeightedGrid(int v) {
    // generates a complete v-by-v grid of to-vertex weighted directed edges con-
    // necting adjacent vertices left-right, up-down and diagonal in both directions
    if (v < 2) throw new IllegalArgumentException(
        "completeGrid(int): v is < 2");
    EuclidianEdgeWeightedDigraph g = null;
    g = new EuclidianEdgeWeightedDigraph(v*v);
    // add edges
    Connection[] c = generateComplete(v);
    for (int i = 0; i < c.length; i++) g.addEdge(c[i].p(),c[i].q(),c[i].q());
    // add coordinates 
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    } 
    g.trim(); // clear wasted memory in graph
    return g;
  }
  
  public static EuclidianEdgeWeightedDigraph rightDownVertexWeightedGrid(int v) {
    // generates a complete v-by-v grid of to-vertex weighted directed edges con-
    // necting adjacent vertices left-right, up-down and diagonal in both directions
    if (v < 2) throw new IllegalArgumentException(
        "completeGrid(int): v is < 2");
    EuclidianEdgeWeightedDigraph g = null;
    g = new EuclidianEdgeWeightedDigraph(v*v);
    // add edges
    Connection[] c = generateRightDown(v);
    for (int i = 0; i < c.length; i++) g.addEdge(c[i].p(),c[i].q(),c[i].q());
    // add coordinates 
    double x,y; x = 1; y = .5; int k = 0;
    for (int i = 0; i < v; i++) {
      x = 1; y+=.5;
      for (int j = 0; j < v; j++) {
        g.addCoords(k++,x+=.5,y);
      }
    } 
    g.trim(); // clear wasted memory in graph
    return g;
  }
  
  public static void gridDemo(int v) {
    EuclidianEdgeWeightedDigraph g;
    
    System.out.println("method"+space(28)+"V"+space(7)+"E");
    System.out.println(repeat("=",49));
    
    g = completeVertexWeightedGrid(v);
    System.out.printf("%-32s  %-6d  %-6d\n","completeVertexWeightedGrid("+v+"):", g.V(), g.E());
    g.showGrid();
    
    pause(3);
    
    g = rightDownVertexWeightedGrid(v);
    System.out.printf("%-32s  %-6d  %-6d\n","rightDownVertexWeightedGrid("+v+"):", g.V(), g.E());
    g.showGrid();
  }
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    gridDemo(5);
    
    
    //  test generate()
//    Scanner sc = new Scanner(System.in);
//    if (sc.hasNextInt()) {
//      int n = sc.nextInt();
//      System.out.println("N="+n);
//      Connection[] ca = generate(n);
//      for (Connection c : ca) System.out.println(c);
//    }
//    sc.close();
    
  }

}
