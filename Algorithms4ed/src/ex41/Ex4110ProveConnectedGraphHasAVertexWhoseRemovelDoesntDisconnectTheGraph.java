package ex41;

import static graph.BiconnectedX.articulationPoints;
import static graph.BiconnectedX.joinPoints;
import static v.ArrayUtils.par;

/* p558
  4.1.10  Prove that every connected graph has a vertex whose removal 
  (including all adjacent edges) will not disconnect the graph, and 
  write a DFS method that finds such a vertex. Hint : Consider a vertex 
  whose adjacent vertices are all marked.
  
  The proof requires careful logic and thoroughness. The best version I 
  found is by Jacob Schrum of the University of Texas given in
  http://www.cs.utexas.edu/~pstone/Courses/313Hfall12/resources/euler.pdf
  that's available in this project at 
    ProofOfSufficiecyForEulerCircuits-JacobSchrum-2012.pdf and also given
  below.
  
  The main website of CS313H - Logic, Sets, and Functions: Honors - Fall 2012
  from the University of Texas is 
    https://www.cs.utexas.edu/~pstone/Courses/313Hfall12/index.html
  
  Proof that every connected graph has a vertex whose removal (including all 
  adjacent edges) will not disconnect the graph by Jacob Schrum:
  
  Claim: Every connected graph of two or more vertices has a vertex that can
  be removed (along with its incident edges) without disconnecting the remaining
  graph.
  
  Proof: We will prove an even stronger fact by induction on the number of
  vertices: 
  Proposition P(n): A connected graph with n >= 2 vertices has two distinct vertices, 
  each of which can be removed individually (along with incident edges) without discon-
  necting the remaining graph.
  
  Base Case: 
  Proposition P(2): For a graph with two vertices either of the vertices can be removed. 
  The remaining graph has a single vertex in both cases, which is a connected graph.
  
  Inductive Hypothesis (IH): P(2)∧ ··· ∧ P(n) → P(n + 1)
  
  1. Consider arbitrary connected graph G with n + 1 vertices.
  
  2. Remove an arbitrary vertex v to get G'.
  
  3. Case 1: G' is connected
  
     (a) Since P(n) is true by IH, there are two distinct vertices w and u 
         that (individually) can be removed from G' without disconnecting it.
         
     (b) If w were removed from G instead of v, the resulting graph would
         still be connected.
         
     (c) Therefore, two distinct vertices can be safely removed from G, and
         we are done.
         
  4. Case 2: G' is disconnected
  
  5. Then G' is made up of r connected component subgraphs C₁ ,...,Cᵣ.
  
  6. r ≥ 2 because with at least n+1 vertices in G where n ≥ 2, the remaining n
     vertices (at least 2) must be in separate components if G' is disconnected.
     
  7. Each component Cᵢ has a vertex vᵢ that was a neighbor of v in G.
  
  8. Case 2.1: For each Cᵢ, vertex vᵢ is the only vertex in the component.
  
     (a) Add v back to G' get G again, and then remove v₁ instead.
     
     (b) Alternately, since r ≥ 2, we could have removed vᵣ instead.
     
     (c) Now we have removed a vertex from G in two different ways, and each 
         results in a connected graph.
         
  9. Case 2.2: At least one of Cᵢ, call it Cᵦ, has 2 or more vertices.
  
    (a) The Strong Inductive Hypothesis applies to Cᵦ, so there are two distinct 
        vertices that can be removed from Cᵦ without disconnecting it.
        
    (b) Since there are two, and they are distinct, at least one of them is not
        vᵦ. Call the other one x.
        
    (c) Add v back to G' to get G again.
    
    (d) We have already identified one vertex, x, that can be safely removed from 
        G without disconnecting it.
        
    (e) We also know that there is at least one other Cᵧ where ɣ != β  because r ≥ 2.
    
    (f) If Cᵧ is just one vertex, we can remove it (vᵧ) as in Case 2.1.
    
    (g) If Cᵧ is more than one vertex, then the IH allows us to find and remove 
        one vertex (not vᵧ) as in Case 2.2.a-d.
        
    (h) The vertex removed from Cᵧ accounts for the second vertex that we could 
        remove from G without disconnecting it.
        
  10. In all cases, P(n+1) is true because we found two distinct vertices, either
      of which could be removed without disconnecting G.
      
  For the second part of the exercise another hint, from 
  http://algs4.cs.princeton.edu/41graph/ is:
  32. Delete a vertex without disconnecting a graph. Given a connected graph, design 
      a linear-time algorithm to find a vertex whose removal (deleting the vertex and 
      all incident edges) does not disconnect the graph.
      Hint 1 (using DFS): run DFS from some vertex s and consider the first vertex in 
      DFS that finishes.
      Hint 2 (using BFS): run BFS from some vertex s and consider any vertex with the 
      highest distance.
    
   I considered the hints and they did not provide an easy and complete solution.
   
   What this exercise calls for in the second part is a non-articulation point that I
   call a join point.  Somewhat oddly articulation points aren't mentioned until exercise
   4.1.35. However I implemented a solution using, graph.Biconnected that's based on
   http://algs4.cs.princeton.edu/41graph/Biconnected.java to find all join points as well
   as all articulation points in a simple graph. It's demonstrated below for a graph built
   using input from tinyCG2.txt that's based on tinyCG.txt modified to create an
   articulation point by removing the link between vertices 1 and 2 so that 1 has only 
   a link to 0 and 1 would be disconnected if 0 is removed making 0 an articulation point.
   
   Here are the contents of tinyCG2.txt:
    6
    7
    0 5
    2 4
    2 3
    0 1
    3 4
    3 5
    0 2
 */

public class Ex4110ProveConnectedGraphHasAVertexWhoseRemovelDoesntDisconnectTheGraph {
  
  public static void main(String[] args) {
    
    System.out.print("joinPoints="); par(joinPoints("tinyCG2.txt"));
    // joinPoints=[1,2,3,4,5]
    
    System.out.print("articulationPoints="); par(articulationPoints("tinyCG2.txt"));
    // articulationPoints=[0]
    
  }

}

