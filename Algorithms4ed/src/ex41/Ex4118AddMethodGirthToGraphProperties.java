package ex41;

import static v.ArrayUtils.par;

import graph.GraphPropertiesX2;
import ds.Seq;
import graph.GraphGeneratorX;
import graph.GraphX;

/* p559
  4.1.18  The girth of a graph is the length of its shortest cycle. 
  If a graph is acyclic, then its girth is infinite. Add a method  
  girth() to GraphProperties that returns the girth of the graph. 
  Hint : Run BFS from each vertex. The shortest cycle containing s 
  is a shortest path from s to some vertex v, plus the edge from  
  v back to s 
  
  public class GraphProperties API2
  --------------------------------------------------------------------
  GraphProperties(Graph G)  constructor (exception if  G not connected)
  int eccentricity(int v)  eccentricity of  v
  int diameter()  diameter of  G
  int radius()  radius of  G
  int center()  a center of  G
  int girth()  length of shortest cycle or -1 if none
  
  This is implemented in graph.GraphPropertiesX2 and demonstrated below.
  Note that BreadthFirstPathsX is used by GraphPropertiesX2 and ignores
  cycles when calculating path lengths between vertices, otherwise a loop
  will produce path lengths of INIFINITY == Integer.MAX_VALUE == 2147483647
  that maybe is correct and I think cyclic graphs should be excluded.
  
 */

public class Ex4118AddMethodGirthToGraphProperties {
  
  public static void main(String[] args) {
    
    // the first graph demonstrates detection of a self-loop for girth == 1
    // edges from tinyGex3.txt and define multiple self-loops and pairs of parallel edges
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    Seq<GraphX> sg = new Seq<>(new GraphX(12, edges), GraphGeneratorX.tree(55)); 
    Seq<String> ss = new Seq<>("GraphX(12,edges)","GraphGeneratorX.tree(55)");
    GraphPropertiesX2 gp; int c = 0;
    for (GraphX G : sg) {
      System.out.println("graph properties for "+ss.get(c));
      gp= new GraphPropertiesX2(G);
      System.out.println("diameter = "+gp.diameter());
      System.out.println("radius = "+gp.radius());
      System.out.println("center = "+gp.center());
      System.out.print("centers = "); par(gp.centers());
      System.out.print("eccentricities = "); par(gp.eccentricities());
      if (gp.girth() == 2147483647) System.out.println("girth = INFINITY");
      else System.out.println("girth = "+gp.girth());
      System.out.print("minCycle = "); par(gp.minCycle());
      System.out.print(ss.get(c)+".adj = "); par(G.adj());
      System.out.println();
      c++;
    }
/*
    graph properties for GraphX(12,edges)
    diameter = 3
    radius = 0
    center = 9
    centers = [9]
    eccentricities = [3,2,1,2,2,2,2,2,1,0,3,2]
    girth = 1
    minCycle = [5,5]
    GraphX(12,edges).adj = [(2,6),(8,4,8,11),(5,5,6,0,3),(10,10,6,2),(1,8),(5,5,2,10,2),
                            (2,3,0),(7,7,8,11),(1,1,11,7,4),(9,9),(3,5,3),(8,7,1)]
    
    graph properties for GraphGeneratorX.tree(55)
    diameter = 17
    radius = 9
    center = 17
    centers = [17,37]
    eccentricities = [15,12,13,14,16,10,14,13,10,11,12,17,15,12,13,14,14,9,14,13,16,17,
                      12,13,13,11,13,14,13,17,16,12,11,16,16,17,14,9,12,16,17,13,16,16,
                      15,14,10,11,15,12,12,15,16,13,15]
    girth = INFINITY
    minCycle = []
    GraphGeneratorX.tree(55).adj = [(36,42,20,4),(47,7,24,28,26),(10,15,6),(23),(0),
                                    (9,17,25,32),(2),(1,18),(37,47),(49,10,5,38),
                                    (9,53,23,2),(42),(18,52),(47),(22),(2),(19),(5,37,46),
                                    (7,12,48),(45,49,27,16),(0),(30),(25,41,14),(10,3),
                                    (1,36),(5,50,22),(1),(19),(1),(42),(51,21),(32),(5,31),
                                    (44),(51,40),(52),(24,44,0),(17,8),(9),(44),(34),(22),
                                    (0,29,11),(54),(36,39,33),(54,19,51),(17),(8,1,13),(18),
                                    (19,9),(25),(45,34,30),(12,35),(10),(45,43)]
*/

  }
  
}


