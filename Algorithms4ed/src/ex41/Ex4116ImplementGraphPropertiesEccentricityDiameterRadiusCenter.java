package ex41;

import static v.ArrayUtils.par;

import graph.GraphGeneratorX;
import graph.GraphPropertiesX;
import graph.GraphX;

/* p559
  4.1.16  The eccentricity of a vertex v is the the length of the shortest 
  path from that vertex to the furthest vertex from  v. The diameter of a 
  graph is the maximum eccentricity of any vertex. The radius of a graph is 
  the smallest eccentricity of any vertex. A center is a vertex whose eccen-
  tricity is the radius. Implement the following API:
  
  public class GraphProperties API
  --------------------------------------------------------------------
  GraphProperties(Graph G)  constructor (exception if  G not connected)
  int eccentricity(int v)  eccentricity of  v
  int diameter()  diameter of  G
  int radius()  radius of  G
  int center()  a center of  G
  
  This is implemented in graph.GraphPropertiesX and demonstrated below.
  Note that BreadthFirstPathsX is used by GraphPropertiesX and ignores
  cycles when calculating path lengths between vertices, otherwise a loop
  will produce path lengths of INIFINITY == Integer.MAX_VALUE == 2147483647
  that maybe is correct and I think cyclic graphs should be excluded.
 */

public class Ex4116ImplementGraphPropertiesEccentricityDiameterRadiusCenter {
  
  public static void main(String[] args) {
    
    GraphX G = GraphGeneratorX.tree(55);
    GraphPropertiesX gp = new GraphPropertiesX(G);
    System.out.println("diameter = "+gp.diameter());
    System.out.println("radius = "+gp.radius());
    System.out.println("center = "+gp.center());
    System.out.print("centers = "); par(gp.centers());
    System.out.print("eccentricities = "); par(gp.eccentricities());
    for (int i = 0; i < G.V(); i++)
      System.out.println("eccentricity("+i+") = "+gp.eccentricity(i));
    
  }
/*
    diameter = 20
    radius = 10
    center = 17
    centers = [17]
    eccentricities = [11,16,16,13,12,20,18,17,15,11,20,20,11,19,13,17,14,10,
                      17,15,16,13,16,15,16,19,19,17,17,15,17,13,20,15,13,18,
                      17,13,19,14,14,11,16,12,16,14,13,15,15,17,18,16,12,17,14]
    eccentricity(0) = 11
    eccentricity(1) = 16
    eccentricity(2) = 16
    eccentricity(3) = 13
    eccentricity(4) = 12
    eccentricity(5) = 20
    eccentricity(6) = 18
    eccentricity(7) = 17
    eccentricity(8) = 15
    eccentricity(9) = 11
    eccentricity(10) = 20
    eccentricity(11) = 20
    eccentricity(12) = 11
    eccentricity(13) = 19
    eccentricity(14) = 13
    eccentricity(15) = 17
    eccentricity(16) = 14
    eccentricity(17) = 10
    eccentricity(18) = 17
    eccentricity(19) = 15
    eccentricity(20) = 16
    eccentricity(21) = 13
    eccentricity(22) = 16
    eccentricity(23) = 15
    eccentricity(24) = 16
    eccentricity(25) = 19
    eccentricity(26) = 19
    eccentricity(27) = 17
    eccentricity(28) = 17
    eccentricity(29) = 15
    eccentricity(30) = 17
    eccentricity(31) = 13
    eccentricity(32) = 20
    eccentricity(33) = 15
    eccentricity(34) = 13
    eccentricity(35) = 18
    eccentricity(36) = 17
    eccentricity(37) = 13
    eccentricity(38) = 19
    eccentricity(39) = 14
    eccentricity(40) = 14
    eccentricity(41) = 11
    eccentricity(42) = 16
    eccentricity(43) = 12
    eccentricity(44) = 16
    eccentricity(45) = 14
    eccentricity(46) = 13
    eccentricity(47) = 15
    eccentricity(48) = 15
    eccentricity(49) = 17
    eccentricity(50) = 18
    eccentricity(51) = 16
    eccentricity(52) = 12
    eccentricity(53) = 17
    eccentricity(54) = 14
*/
}

