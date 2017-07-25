package ex41;

import graph.DegreesOfSeparationOnePass;
import graph.DegreesOfSeparationS;
import graph.DegreesOfSeparationSB;

/* p562
  4.1.34 Symbol graph. Implement a one-pass SymbolGraph (it need not be a
  Graph client). Your implementation may pay an extra log V factor for graph 
  operations, for symbol-table lookups.
  
  This is done with DegreesOfSeparationSB using SymbolGraphSB that uses GraphSB  
  that uses a Seq<BagX<Integer>> instead of a BagX<Integer>[] for adj to enable 
  the graph to dynamically increase its size whenever an edge is to be added with 
  a vertex that has a value > adj.size()-1. When all edges have been added the
  trim() method of GraphSB is called causing the graph to truncate adj to size V 
  that is maintained as max vertex value. SymbolGraphSB adds keys to its ST and
  edges to its graph in a filter of Stream<String> of the input file created using 
  java.nio.file.Files.lines(Paths.get(filename)).
  
  DegreesOfSeparationSB like SymbolGraphS but uses GraphS that uses a Seq<Seq<Integer>> 
  for adj. This produces different results because elements are appended to the inner 
  Seqs with Seq.add while Bag.add prepends elements.
  
  Another approach is SymbolGraphOnePass that initially loads the file (movies.txt)
  linewise into an array while counting all separators. Then it initializes the graph
  with V equal to the separator count that should be an overestimate since some keys
  may be repeated. Then it processes the lines in the array, puts new keys into the ST
  keeping count of the number of puts and creates new graph edges. When that's done it 
  sets graph.V to the number of ST puts and truncates graph.adj to size V.
  
  Pragmatically I tend to prefer the approach of estimating an upper bound on the 
  number of vertices and then trimming adj if memory usage is an issue instead of
  writing new versions of Graph and SymbolGraph. The number of vertices can be 
  estimated in simple ways such as by counting the number of separators in one or
  a few lines of the input file and scaling it by the total number of lines. If the
  initial estimate is too low keep on doubling it until it works.
  
  All three implementations assume that no keys are repeated in the input file. If
  that's not true then the hasEdge(int,int) method of all the graph implementations
  could be used to eliminate repeated edges.
  
  The three implmentations can be demonstrated below.
  
 */                                                   

@SuppressWarnings("unused")
public class Ex4134ImplementOnePassSymbolGraph {

  public static void main(String[] args) {

    String[] vargs = {"movies.txt", "/", "Bacon, Kevin"};
    
    DegreesOfSeparationSB.main(vargs);
        
//    DegreesOfSeparationS.main(vargs);
    
//    DegreesOfSeparationOnePass.main(vargs);

/* some test names
Portman, Natalie
Huppert, Isabelle
Mortensen, Viggo
Affleck, Casey
*/ 
    
  }

}



