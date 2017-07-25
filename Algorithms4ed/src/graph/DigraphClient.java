package graph;

import static v.ArrayUtils.par;

import edu.princeton.cs.algs4.In;

public class DigraphClient {

  public static void main(String[] args) {
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
    System.out.println(d);
    System.out.print("adj=");par(d.adj());
    int selfLoopCount = d.selfLoopCount();
    System.out.println("maxIndegree="+d.maxIndegree());
    System.out.println("minIndegree="+d.minIndegree());
    System.out.println("avgIndegree="+d.avgIndegree());
    System.out.println("maxOutdegree="+d.maxOutdegree());
    System.out.println("minOutdegree="+d.minOutdegree());
    System.out.println("avgOutdegree="+d.avgOutdegree());
    System.out.println("selfLoopCount="+selfLoopCount);
    if (selfLoopCount > 0) System.out.println("allSelfLoops="+d.allSelfLoops());
    int parallelEdgeCount = d.parallelEdgeCount();
    System.out.println("parallelEdgeCount="+parallelEdgeCount);
    if (parallelEdgeCount > 0) System.out.println("parallelEdges="+d.parallelEdges());
    if (d.hasCycle()) System.out.println("cycle="+d.cycle());
    else System.out.println("graph doesn't have a cycle");
    int count = d.count();
    System.out.println("count="+count);
    if (count > 0) System.out.println("scc="+d.scc());
  }

}

