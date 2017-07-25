package ex44;

/* p687
  4.4.20  Find a currency-conversion table online or in a newspaper. Use it 
  to build an arbitrage table. Note: Avoid tables that are derived (calculated) 
  from a few values and that therefore do not give sufficiently accurate con-
  version information to be interesting. Extra credit : Make a killing in the 
  money-exchange market!
  
  from http://www.x-rates.com/ at May 20, 2017 18:10 UTC
      USD     GBP     CAD     EUR     AUD
  USD 1       0.76726 1.35121 0.89240 1.34090
  GBP 1.30334 1       1.76109 1.16310 1.74765
  CAD 0.74008 0.56783 1       0.66044 0.99236
  EUR 1.12057 0.85977 1.51413 1       1.50257
  AUD 0.74577 0.57220 1.00769 0.66553 1
  
  input file xrates.txt contains:
  5
  USD 1       0.76726 1.35121 0.89240 1.34090
  GBP 1.30334 1       1.76109 1.16310 1.74765
  CAD 0.74008 0.56783 1       0.66044 0.99236
  EUR 1.12057 0.85977 1.51413 1       1.50257
  AUD 0.74577 0.57220 1.00769 0.66553 1
  # http://www.x-rates.com/ at May 20, 2017 18:10 UTC
  
  Running this through ArbitrageX.cyclesAnalyzer() and ArbitrageX.bellmanFord()
  gives results qualitatively similar to those for exercise 4.4.19, but far less 
  profitable as demonstrated below.
  
  So far all arbitrage opportunities that I've seen are due to deficiencies in
  double arithmetic and not due to discrepencies in currency exchange rates since
  all the exchange rate tables that I've seen are symmetrical for all pairs of
  currencies. I think a real-time trading platform is needed to see discrepencies
  in currency exchange rates and profit can't be made by expoiting double arithmetic.
  
 */  

public class Ex4420CurrencyArbitrageWithRealData {

  public static void main(String[] args) {

    graph.ArbitrageX.cyclesAnalyzer("xrates.txt",false);
    graph.ArbitrageX.bellmanFord("xrates.txt",false);
/*
    Overall the cycle with min weight is:
    0>4>3>1>2>0  weight:-1.5044678963016533E-5
    profitPerExchange = 3.7611414481233865E-4%
    
    BellmanFordSPX negativeCycle arbitrage opportunity found:
    3>4>3  weight:-5.412085354539187E-6
    profitPerExchange = 5.412070709276191E-4%
*/   
    
  }

}


