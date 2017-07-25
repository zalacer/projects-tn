package ex34;

import static java.lang.Math.*;
import static analysis.ChiSquareUtils.pochisq;
import static v.ArrayUtils.*;

import java.security.SecureRandom;

import st.SeparateChainingHashSTX;

/* p483
  3.4.30 Chi-square statistic. Add a method to  SeparateChainingST to 
  compute the χ² statistic for the hash table. With N keys and table 
  size M, this number is defined by the equation
    χ² = (M/N)( (f(0) - N/M)² + (f(1) - N/M)² + . . . (f(M-1) - N/M)²)
  where f(i) is the number of keys with hash value i. This statistic is 
  one way of checking our assumption that the hash function produces 
  random values. If so, this statistic, for N > cM, should be between 
  M-sqrt(M) and M+sqrt(M) with probability 1-1/c.
  
  This makes no sense because N <= M -> c <= 1 -> 1-1/c <= 0 but 
  probabilities must be >= 0.
    
*/

@SuppressWarnings("unused")
public class Ex3430ChiSquareStatistic {
  
  public static void testChi2() {
    SeparateChainingHashSTX<Integer,Integer> h = new SeparateChainingHashSTX<>();
    SecureRandom r = new SecureRandom();
    int c = 0;
    while (c < 1000000) {
      h.put(r.nextInt(100000), 1); c++;
    }
    double chi2 = h.chi2();
    System.out.println("chi2 = "+chi2);
    System.out.println("probability = "+pochisq(chi2,h.size()-1)); 
    System.out.println("M-sqrt(M) = "+(1.*h.getM()-sqrt(h.getM())));
    System.out.println("M+sqrt(M) = "+(1.*h.getM()+sqrt(h.getM())));
    
  }
    
  public static void main(String[] args) {
    
    testChi2();
    // chi2 = 250.70997679814386
    // probability = 0.9999999928307761
    // M-sqrt(M) = 16256.0
    // M+sqrt(M) = 16512.0
    
  }

}

