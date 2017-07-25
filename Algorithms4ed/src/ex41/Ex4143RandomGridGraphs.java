package ex41;

import static graph.RandomGridGraph.completeGrid;
import static graph.RandomGridGraph.pause;
import static graph.RandomGridGraph.randomGrid;
import static v.ArrayUtils.repeat;
import static v.ArrayUtils.space;

import graph.EuclidianGraph;
import st.HashSET;
import v.Tuple2;
import v.Tuple3;

/* p564
  4.1.43 Random grid graphs. Write a  EuclideanGraph client RandomGridGraph 
  that generates random graphs by connecting vertices arranged in a sqrt(V)-
  by-sqrt(V) grid to their neighbors (see Exercise 1.5.15). Augment your pro-
  gram to add R extra random edges. For large R, shrink the grid so that the 
  total number of edges remains about V. Add an option such that an extra edge 
  goes from a vertex s to a vertex t with probability inversely proportional 
  to the Euclidean distance between s and t.
  
  Excercise 1.5.15 is about binomial trees. Perhaps the referral should be to
  exercise 1.5.18 and 1.5.19 since they're about generating random grids.
  
  Random grid graphs are done with 4 static methods of successive complexity in
  graph.RandomGridGraph:
  
  1. completeGrid(int v) - creates a v-by-v grid with all adjacent vertices 
                           connected.
     
  2. randomGrid(int v) -   does the same thing except with random edges between 
                           adjacent vertices.
     
  3. randomGrid(int v,int r) - does (2) plus adds r extra random edges and ensures 
                           that the total number of edges is v. 
                          
  4. randomGrid(int v,int r, boolean b - does (3) exactly when b is false and when 
                           it's true additionally randomly connects all pairs of 
                           vertices with probability inversely proportion to the 
                           distance between them, after normalizing the distances so 
                           all are greater than or equal one and scaling them to avoid 
                           obscuring the plot with too many extra edges.
                           
  For all these methods the vertices are red dots and the initial edges are black 
  lines. For (3) and (4) the r extra edges are green lines and for (4) the inverse 
  distance edges are orange lines.
  
  All of the methods are demonstrated below with plotted output.
 
 */                                                   

public class Ex4143RandomGridGraphs {

  public static void main(String[] args) {

    EuclidianGraph g;
                                 
    System.out.println("method"+space(22)+"V"+space(6)+"E");
    System.out.println(repeat("=",38));
    
    g = completeGrid(15);
    System.out.printf("%-24s %6d %6d\n","completeGrid("+15+"):", g.V(), g.E());
    /* plot g: v*v vertices are red, edges are black, the number of edges isn't limited. */ 
    g.showGrid();
    
    pause(3);
    
    g = randomGrid(25);
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+25+"):", g.V(), g.E());
    /* plot g: v*v vertices are red, edges identified with union find are black, 
      the number of edges isn't limited. */ 
    g.showGrid();
    
    pause(3);
    
    Tuple2<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>> t2 = randomGrid(15,19);
    g = t2._1;
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+15+","+19+"):", +g.V(), g.E());
    /* plot g: v*v vertices are red, initial edges identified with union find are black, 
      random edges specified by the second arg are green. the total number of initial 
      and random edges is normalized to v. */ 
    g.showGrid(t2._2);
    
    pause(3);

    Tuple3<EuclidianGraph,HashSET<Tuple2<Integer,Integer>>,
        HashSET<Tuple2<Integer,Integer>>> t3 = randomGrid(15,19,true);
    g = t3._1;
    System.out.printf("%-24s %6d %6d\n", "randomGrid("+15+","+19+", true):", g.V(), g.E());
    /* plot g: v*v vertices are red, initial edges identified with union find are black, 
       random edges specified by the second arg are green. the total number of initial 
       and random edges is normalized to v. the edges created with probability inversly 
       proportional to (1./(10*distance_between_the_vertices) are (yellowish) orange.
       run wil last arg=false to omit the edges created with probability inversly pro-
       portional to distance. */ 
    g.showGrid(t3._2,t3._3);
    
  }

}



