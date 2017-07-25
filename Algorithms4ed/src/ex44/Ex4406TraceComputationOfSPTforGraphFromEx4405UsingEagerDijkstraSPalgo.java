package ex44;

import static graph.DijkstraSPXtrace.*;

import analysis.Draw;
import ds.Seq;


/* p685
  4.4.6  Give a trace that shows the process of computing the SPT of the 
  digraph defined in Exercise 4.4.5 with the eager version of Dijkstra’s 
  algorithm.

  This is done with a sequence of Drawings by 
    graph.DijkstraSPXtrace.traceModTinyEWDex4406()
  that's demonstrated below.

 */  

public class Ex4406TraceComputationOfSPTforGraphFromEx4405UsingEagerDijkstraSPalgo {

  
	public static void main(String[] args) {
	  
	  Seq<Draw> draw = traceModTinyEWDex4406();
    dispose(draw);

	
	}

}


