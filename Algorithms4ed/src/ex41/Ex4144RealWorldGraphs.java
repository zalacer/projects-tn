package ex41;

import static graph.RandomRealGraph.createGraph;

import graph.SymbolGraphSB;

/* p564
  4.1.44 Real-world graphs. Find a large weighted graph on the web—perhaps 
  a map with distances, telephone connections with costs, or an airline rate 
  schedule. Write a program RandomRealGraph that builds a graph by choosing
  V vertices at random and E edges at random from the subgraph induced by 
  those vertices.
  
  This is done in graph.RandomRealGraph with three static methods:
  1. readData(String f) that reads the data from a file named f and returns
     a Tuple2<String[],Tuple2<String,String>[]> containing a String[] of all
     the unique vertices and a Tuple2<String,String>[] of the edges discard-
     ing their weights.
  2. processData(Tuple2<String[],Tuple2<String,String>[]> t2, int v, int e) 
     takes the output of readData and selects up to v unique vertices from
     t2._1 and and selects and returns up to e unique edges, with vertices in 
     t2._1, as a Tuple2<String,String>[]>.
  3. createGraph(String filename, int v, int e) runs readData with filename
     then runs processData with the output of readData and the v and e args,
     creates a SymbolGraphSB from the Tuple2<String,String>[] of edges, plots 
     an EuclidianGraph with random coordinates using the SymbolGraphSB's GraphSB, 
     prints the number of its connected components and returns the SymbolGraphSB.
     
  To get more interesting results self-edges were discarded.
  edges, those were discarded in readData() and processData().
     
  These methods are demonstrated below using the weighted graph data locally
  in higgs-reply_network.edgelist.txt from 
    https://snap.stanford.edu/data/higgs-reply_network.edgelist.gz
  and downloaded from https://snap.stanford.edu/data/higgs-twitter.html.
  
  A picture of the results showing 5 separate components with otherwise random
  coordinates is Ex4144-5ConnectedComponents.jpg. For more than 5 components
  EuclidianGraph.showc() passes the job over to showp() that doesn't separate 
  components graphically.

 */                                                   

public class Ex4144RealWorldGraphs {

  public static void main(String[] args) {

    String filename = "Ex4144-higgs-reply_network.edgelist.txt";
    SymbolGraphSB sg = createGraph(filename, 37000, 700);
    System.out.println(sg);
  }

}



