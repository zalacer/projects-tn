package ex42;

import java.util.Comparator;

import ds.Seq;
import graph.DigraphGeneratorX;
import graph.KosarajuSharirSCCX;

/* p597
  4.2.16  What happens if you run Kosaraju’s algorithm on a DAG?

  It identifies exactly the vertices of the graph as the strong components. 
  See example below. Not much point running it on a DAG.

 */                                                   

public class Ex4216WhatHappensWhenYouRunKosarajusAlgorithmOnADAG {

  public static void main(String[] args) {

    KosarajuSharirSCCX scc = new KosarajuSharirSCCX(DigraphGeneratorX.dag(11, 10));
    // number of connected components
    Comparator<Seq<Integer>> cmp = (s1,s2) -> { return s1.get(0).compareTo(s2.get(0)); };
    System.out.println(scc.count() + " strong components = "
        + scc.components().sortWithComparator(cmp));
    // 11 strong components = ((0),(1),(2),(3),(4),(5),(6),(7),(8),(9),(10))

  }

}



