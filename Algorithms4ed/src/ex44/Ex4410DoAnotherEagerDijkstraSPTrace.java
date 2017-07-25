package ex44;

import static graph.DijkstraSPXtrace.dispose;
import static graph.DijkstraSPXtrace.traceModTinyEWDex4410;

import analysis.Draw;
import ds.Seq;

/* p686
  4.4.10  Consider the edges in the digraph defined in Exercise 4.4.4 
  to be undirected edges such that each edge corresponds to equal-weight 
  edges in both directions in the edge-weighted digraph. Answer Exercise 
  4.4.6 for this corresponding edge-weighted digraph.
  
  This means to give a trace of the process of computing the source 0 SPT 
  using the eager version of Dijkstra’s algorithm on tinyEWD.txt with vertex 
  7 removed and the reverse of all remaining edges added.
  
  It is done similarly to ex4.4.6 using graph.DijkstraSPXtrace and in par-
  ticular its static traceModTinyEWDex4410 client that's demoed below.

 */  

public class Ex4410DoAnotherEagerDijkstraSPTrace {

	public static void main(String[] args) {
	  
	  Seq<Draw> draw = traceModTinyEWDex4410();
    dispose(draw);
	
	}

}


