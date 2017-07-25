package ex44;

/* p688
  4.4.29 General optimality. Complete the proof of Proposition W by showing that if
  there exists a directed path from s to v and no vertex on any path from s to v is 
  on a negative cycle, then there exists a shortest path from s to v. 
  (Hint: See Proposition P.)
  
  If there is a directed path from s to v with no vertex on negative cycle, one can 
  be formed where no vertex occurs more than once, since if a vertex x repeats there 
  must be a non-negative loop from x to itself that can be removed without increasing 
  the weight. Thus all possible shortest paths have at most n-1 vertices, where n is 
  the finite number of vertices in the graph. The set of such paths is finite so the 
  their weights have a minimum corresponding the shortest.
 
 */  

public class Ex4429GeneralOptimality {

  public static void main(String[] args) {

  }

}


