package ex43;

import java.security.SecureRandom;

import ds.Seq;
import graph.BridgeX;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;
import graph.GraphGeneratorX;
import graph.GraphX;
import st.HashSET;
import v.Tuple2;

/* p633
  4.3.24 Reverse-delete algorithm. Develop an implementation that computes the
  MST as follows: Start with a graph containing all of the edges. Then repeat-
  edly go through the edges in decreasing order of weight. For each edge, check 
  if deleting that edge will disconnect the graph; if not, delete it. Prove that 
  this algorithm computes the MST. What is the order of growth of the number of 
  edge-weight compares performed by your implementation?
  
  This is implemented and demonstrated below. For each edge reverseDeleteMST()
  checks if it's a bridge in the underlying GraphX and if not removes it. Edge-
  weight comparisons are used only in the initial sorting and final sorting and
  uses Arrays.sort that's probably a version of MergeSort for object vs primitive
  sorts so for such comparisons it's growth is nominally ElgE for runtime and E
  for space (according to text p342). The algorithm is sensible in that it removes
  the maximum number of heaviest edges possible without disconnecting the graph
  that should leave a MST by definition. A proof of correcteness is at 
  https://en.wikipedia.org/wiki/Reverse-delete_algorithm#Proof_of_correctness.
  
  Another approach is to delete the next edge in a clone of the current MST and 
  promote the clone to the current MST if the component count didn't increase else 
  retain the MST from which the clone was made. This is implemented as
  generalReverseDeleteMST() and demonstrated below.
  
 */  

public class Ex4324ReverseDeleteMSTAlgorithm {
  
  public static Seq<EdgeX> reverseDeleteMST(EdgeWeightedGraphX G) {
    // works when G is connected
    if (G == null) throw new IllegalArgumentException("reverseDeleteMST: G == null");
    EdgeWeightedGraphX mst = new EdgeWeightedGraphX(G);
    Seq<EdgeX> edges = (new Seq<>(G.edges())).sort().reverse();
    HashSET<Tuple2<Integer,Integer>> bridges = (new BridgeX(mst.graph())).bridges();
    for (EdgeX e : edges) {
      Tuple2<Integer,Integer> t = new Tuple2<>(e.u(),e.v());
      if (!bridges.contains(t)) {
        mst.removeEdge(e);
        bridges = (new BridgeX(mst.graph())).bridges();
      }
    }
    return new Seq<EdgeX>(mst.edges());
  }
  
  public static Seq<EdgeX> generalReverseDeleteMST(EdgeWeightedGraphX G) {
    // works even if G is disconnected
    if (G == null) throw new IllegalArgumentException("reverseDeleteMST: G == null");
    EdgeWeightedGraphX mst = new EdgeWeightedGraphX(G), tmp = null;
    Seq<EdgeX> edges = (new Seq<>(G.edges())).sort().reverse();
    for (EdgeX e : edges) {
      tmp = new EdgeWeightedGraphX(mst);
      int cc = tmp.graph().count();
      mst.removeEdge(e);
      mst.graph().search();
      int cd = mst.graph().count();
      if (cd > cc) mst = tmp;
    }
    return new Seq<EdgeX>(mst.edges());
  }
  
  public static double weight(Seq<EdgeX> seq) {
    double weight = 0.0;
    for (EdgeX e : seq) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    
    // V and edges for tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93"; 
    
    System.out.println("demo of reverseDeleteMST() using tinyEWG.txt:");
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    Seq<EdgeX> mst = reverseDeleteMST(G);   
    System.out.println("MST:");
    for (EdgeX e : mst) System.out.println(e);
    System.out.println("weight: "+weight(mst));
    
    System.out.println("\ndemo of generalReverseDeleteMST() using tinyEWG.txt:");
    G = new EdgeWeightedGraphX(V,edges);
    mst = generalReverseDeleteMST(G);   
    System.out.println("MST:");
    for (EdgeX e : mst) System.out.println(e);
    System.out.println("weight: "+weight(mst));
    
    // preparation for demo of disconnected cases
    V = 15; double p = 1;
    GraphX g = GraphGeneratorX.simple(V,p);
    g.search();
    while (g.count() < 3) {
      p-=.01;
      g = GraphGeneratorX.simple(V,p);
      g.search();
      if (g.count() == 3 ) break;
    }
//    System.out.println("gen count = "+g.count()+" p = "+p);
    Seq<Tuple2<Integer,Integer>> edgs = g.edgesWithNoSelfLoops();
    Seq<EdgeX> edgseq = new Seq<>(edgs.size());
    HashSET<Double> dbls =  new HashSET<>(); double d;
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 119563; i++) r.nextInt(90);
    for (Tuple2<Integer,Integer> t : edgs)  {
      d = new Double("."+(r.nextInt(90)+10));
      if (!dbls.contains(d)) {
        edgseq.add(new EdgeX(t._1,t._2,d));
        dbls.add(d);
      }
    }
//  for (EdgeX e : edgseq) System.out.println(e);
    
    System.out.println("\ndemo of reverseDeleteMST() using a simple disconnected "
        + "graph with "+g.count()+" components:");
    G = new EdgeWeightedGraphX(V,edgseq);
    mst = reverseDeleteMST(G);   
    System.out.println("MST:");
    for (EdgeX e : mst) System.out.println(e);
    System.out.println("weight: "+weight(mst));
    
    System.out.println("\ndemo of generalReverseDeleteMST() using a simple disconnected "
        + "graph with "+g.count()+" components:");    
    G = new EdgeWeightedGraphX(V,edgseq);
    mst = generalReverseDeleteMST(G);   
    System.out.println("MST:");
    for (EdgeX e : mst) System.out.println(e);
    System.out.println("weight: "+weight(mst));
 
/*   
    demo of reverseDeleteMST() using tinyEWG.txt:
    MST:
    (5-7,0.28000)
    (4-5,0.35000)
    (2-3,0.17000)
    (6-2,0.40000)
    (1-7,0.19000)
    (0-7,0.16000)
    (0-2,0.26000)
    weight: 1.81
    
    demo of generalReverseDeleteMST() using tinyEWG.txt:
    MST:
    (5-7,0.28000)
    (4-5,0.35000)
    (2-3,0.17000)
    (6-2,0.40000)
    (1-7,0.19000)
    (0-7,0.16000)
    (0-2,0.26000)
    weight: 1.81
    
    demo of reverseDeleteMST() using a simple disconnected graph with 3 components:
    MST:
    (11-14,0.93000)
    (9-14,0.52000)
    (8-10,0.29000)
    (7-9,0.21000)
    (5-8,0.38000)
    (3-14,0.36000)
    (3-10,0.69000)
    (3-6,0.48000)
    (2-12,0.27000)
    (1-10,0.77000)
    (1-12,0.67000)
    (1-13,0.60000)
    weight: 6.17
    
    demo of generalReverseDeleteMST() using a simple disconnected graph with 3 components:
    MST:
    (11-14,0.93000)
    (9-14,0.52000)
    (8-10,0.29000)
    (7-9,0.21000)
    (5-8,0.38000)
    (3-14,0.36000)
    (3-10,0.69000)
    (3-6,0.48000)
    (2-12,0.27000)
    (1-10,0.77000)
    (1-12,0.67000)
    (1-13,0.60000)
    weight: 6.17

*/    
     
  }

}


