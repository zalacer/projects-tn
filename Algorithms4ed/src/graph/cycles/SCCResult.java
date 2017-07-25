package graph.cycles;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class SCCResult {
  private Set<Integer> nodeIDsOfSCC = null;
  private Vector<Integer>[] adjList = null;
  private int lowestNodeId = -1;
  
  public SCCResult(Vector<Integer>[] adjList, int lowestNodeId) {
    this.adjList = adjList;
    this.lowestNodeId = lowestNodeId;
    this.nodeIDsOfSCC = new HashSet<Integer>();
    if (this.adjList != null) {
      for (int i = this.lowestNodeId; i < this.adjList.length; i++) {
        if (this.adjList[i].size() > 0) {
          this.nodeIDsOfSCC.add(new Integer(i));
        }
      }
    }
  }

  public Vector<Integer>[] getAdjList() {
    return adjList;
  }

  public int getLowestNodeId() {
    return lowestNodeId;
  }
}
