package ex44;

/* p685
  4.4.7  Develop a version of DijkstraSP that supports a client method 
  that returns a second shortest path from s to t in an edge-weighted 
  digraph (and returns null if there is only one shortest path from s 
  to  t).
  
  This is done with graph.DijkstraSPXM that enhances DijkstraSPX by creating
  a new SPT whenever relax() encounters an edge for which the usual < condition
  is false but == is true for an edge that isn't an edgeTo, then draws all the
  SPTs created and for each prints the second distinct path for each vertex over 
  them or null is none is found or "source" for the source vertex.
  
  Basically this is a method for generating all shortest paths logically without
  attention to performance. It's demonstrated using DijkstraSPXM.secondPathsModTinyEWD()
  that first modifies tinyEWD.txt so that:
    1. edge 0->2 is reversed with constant weight
    2. the weights of 7->5 + 5->1 + 1->3 are altered to sum to that of 7->3
    3. the weight of 5->4 is altered so that the sum of the weights of
       2->7 + 7-5 + 5->4 equals the sum of the weights of 2->0 + 0->4.
       
  The result is four SPTs:
    1. one without edgeTo changes (and therefore path changes) from normal
    2. one with edgeTo[3] changed from 7->3 to 1->3 and pathTo(3) changed
       from 2->7->3 to 2->7->5->1->3    
    3. one with edgeTo[4] changed from 0->4 to 5->4 and pathTo(4) changed
       from 2->0->4 to 2->7->5->4 
    4. one with all changes in (2) and (3)
    
  secondPathsModTinyEWD displays all 4 SPT variations as drawings to you can
  easily see the differences
    
  An auxilliary method named secondPathOrNull() returns the first distinct-
  from-normal shortest path from source to each vertex if there is one else
  it returns null.
  
  Scaling it up, another demonstration using unmodified mediumEWD.txt is provided.
  It's called secondPathsMediumEWD, uses 0 as the source, creates 8 SPT variations
  and finds 11 second paths.

 */  

public class Ex4407DijkstraSPReturning2ndShortestPathElseNull {
  
	public static void main(String[] args) {
	  
	  // running this 1st since only prints results to stdout
	  graph.DijkstraSPXM.secondPathsMediumEWD();

	  // this prints results to stdout and draws SPT variations
	  graph.DijkstraSPXM.secondPathsModTinyEWD();
  
	  
	}

}


