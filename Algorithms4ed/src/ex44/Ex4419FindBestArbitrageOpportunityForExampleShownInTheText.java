package ex44;

/* p686
  4.4.19  Find the lowest-weight cycle (best arbitrage opportunity) in 
  the example shown in the text.
  
  Overall I found the lowest weight simple cycle with 2-5 vertices and 
  one repeated vertex is 4>0>1>4 with weight -0.007119565639003955 compared 
  with 0>1>4>0 with weight -0.007119565639003939 found with BellmanFordSPX
  as shown below.
 */  

public class Ex4419FindBestArbitrageOpportunityForExampleShownInTheText {

  public static void main(String[] args) {

    graph.ArbitrageX.cyclesAnalyzer("rates.txt", false);
    graph.ArbitrageX.bellmanFord("rates.txt", false);
/*
    Overall the cycle with min weight is:
    4>0>1>4  weight:-0.007119565639003955
    profitPerExchange = 0.35471407855018416%
    
    BellmanFordSPX negativeCycle arbitrage opportunity found:
    0>1>4>0  weight:-0.007119565639003939
    profitPerExchange = 0.35471407855018977%
*/   
    
  }

}


