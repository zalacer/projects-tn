package ex44;

/* p686
  4.4.18  Write a CPM client that prints all critical paths.
  
  This is done with graph.CPMX.criticalPaths(String file) that's 
  demonstrated below;
  
 */  

public class Ex4418CPMCientThatPrintsAllCriticalPaths {

  public static void main(String[] args) {

    graph.CPMX.criticalPaths("jobsPC.txt");
    // critical path: 0->9->6->8->2
    
  }

}


