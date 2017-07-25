package ex41;

import graph.DegreesOfSeparationOnePass;
import graph.DegreesOfSeparationS;
import graph.DegreesOfSeparationSB;

/* p562
  4.1.35 Biconnectedness. A graph is biconnected if every pair of vertices 
  is connected by two disjoint paths. An articulation point in a connected 
  graph is a vertex that would disconnect the graph if it (and its adjacent 
  edges) were removed. Prove that any graph with no articulation points is 
  biconnected. Hint : Given a pair of vertices s and t and a path connecting 
  them, use the fact that none of the vertices on the path are articulation
  points to construct two disjoint paths connecting s and t.
  
  Consider a connected graph with three vertices s, u, t. If it has no articu-
  lation points and has a path s-u-t then it must have another path from s to 
  t avoiding u, since otherwise u would be an articulation point. Therefore 
  generally if a connected graph has no articulation points then every vertex 
  must be connected in at least two independent ways essentially by definition 
  (because if it wasn't it would have an articulation point) and it's biconnected, 
  again by definition (since independent == disjoint).
  
 */                                                   

@SuppressWarnings("unused")
public class Ex4135Biconnectedness {

  public static void main(String[] args) {

    String[] vargs = {"movies.txt", "/", "Bacon, Kevin"};
    
    DegreesOfSeparationSB.main(vargs);
        
//    DegreesOfSeparationS.main(vargs);
    
//    DegreesOfSeparationOnePass.main(vargs);

/* some test names
Portman, Natalie
Huppert, Isabelle
Mortensen, Viggo
Affleck, Casey
*/ 
    
  }

}



