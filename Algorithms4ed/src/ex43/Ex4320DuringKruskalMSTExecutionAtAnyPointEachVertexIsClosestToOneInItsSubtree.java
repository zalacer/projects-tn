package ex43;

/* p632
  4.3.20  True or false: At any point during the execution of Kruskal’s algorithm, 
  each vertex is closer to some vertex in its subtree than to any vertex not in its 
  subtree. Prove your answer.
  
  True: 
  If a vertex has some other vertex in its subtree then it's true that it's closer
  to "some vertex in its subtree than to any vertex not in its subtree" because at
  each step Kruskal's algorithm always assigns the edge with least distance to a 
  vertex in a MST subtree if it has a vertex not yet in one. If this wasn't true then
  a contradiction arises, namely if vertex v is closer to vertex w not in v's subtree
  than it is to all vertices in v's subtree then w would have been the first one put 
  in v's subtree by the edge selection process just described.
  
 */  

public class Ex4320DuringKruskalMSTExecutionAtAnyPointEachVertexIsClosestToOneInItsSubtree {
 
  public static void main(String[] args) {
     
  }

}


