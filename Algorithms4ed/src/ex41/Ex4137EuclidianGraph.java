package ex41;

import graph.EuclidianGraph;
import graph.GraphGeneratorX;

/* p562
  4.1.37 Euclidean graphs. Design and implement an API EuclideanGraph 
  for graphs whose vertices are points in the plane that include coor-
  dinates. Include a method show() that uses StdDraw to draw the graph.
  
  EuclidianGraph is a basic undirected graph plus an assignment of planar 
  coordinates to each vertex.
  
  EuclidianGraph API:
  ============================================
  public EuclidianGraph(){};
  public EuclidianGraph(ErdosRenyiGraph){}
  public EuclidianGraph(EuclidianGraph){}
  public EuclidianGraph(GraphX){}
  public EuclidianGraph(GraphX,String){}
  public EuclidianGraph(In){}
  public EuclidianGraph(int){}  
  public EuclidianGraph(int,int,int[][],double[][]){}
  public EuclidianGraph(int,int,String,String){}
  public EuclidianGraph(int,String,String){}
  public EuclidianGraph(String,String){}        
  
  public void addCoords(int,double,double)
  public void addEdge(int,int);
  public Seq<BagX<Integer>> adj();
  public Iterable<Integer> adj(int);
  public double avgDegree()
  public EuclidianGraph clone();
  public boolean connected(int,int)
  public Seq<Tuple2<Double,Double>> coords();
  public int count();
  public Stack<Integer> cycle();
  public int degree(int);
  private void dfs(int);
  public int E();
  public int[][] hArray();
  public boolean hasCycle();
  public boolean hasEdge(int,int);
  public boolean hasParallelEdges();
  public boolean hasSelfLoop();
  public int[] id();
  public boolean insertEdge(int,int);
  public boolean isEdge(int,int);
  public boolean[] marked();
  public int maxDegree();
  public int numberOfSelfLoops();
  public Iterable<Integer> parallelEdges();
  public Integer[] parallelEdgesArray();
  public boolean removeCoordinates(int);
  public boolean removeEdge(int,int);
  public void search();
  public Iterable<Integer> selfLoop();
  public void setAdj(Seq<BagX<Integer>>);
  public void setE();
  public void setId(int[]);
  public void setMarked(boolean[]);
  public void setSize(int[]);
  public void setV(int);
  public void show();
  public int[] size();
  public String toString();
  public void trim();
  public void updateX(int, double);
  public void updateY(int, double);
  public int V();
  public boolean validate();
  private void validateVertex(int);
  
  EuclidianGraph is implemented at graph.EuclidianGraph and is demonstrated below.

 */                                                   

public class Ex4137EuclidianGraph {

  public static void main(String[] args) {
    
    // this constructor generates random vertex coordinates
    // all constructors are demonstrated in graph.EuclidianGraph.main();
    EuclidianGraph g = new EuclidianGraph(GraphGeneratorX.complete(7));
    g.show(); 

  }

}



