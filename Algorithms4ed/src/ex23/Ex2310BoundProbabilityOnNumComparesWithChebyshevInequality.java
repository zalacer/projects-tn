package ex23;

import static analysis.Log.*;

public class Ex2310BoundProbabilityOnNumComparesWithChebyshevInequality {

  /* p303  
  2.3.10  Chebyshev’s inequality says that the probability that a random variable 
  is more than k standard deviations away from the mean is less than 1/(k^2). For 
  N = 1 million, use Chebyshev’s inequality to bound the probability that the number 
  of compares used by quicksort is more than 100 billion (.1*N^2 ).
    
  From proposition K on p293 "Quicksort uses ~2N*ln(N) compares on average to sort 
  an array of length N with distinct keys and from p295 "the standard deviation of the 
  number of compares is about .65*N
  
  From http://www.informit.com/articles/article.aspx?p=2017754&seqNum=7 that's based on 
  Introduction to the Analysis of Algorithms, 2nd Edition, a more accurate approximation
  to the standard deviation of the number of compares is:
  sqrt((21 - 2*(PI^2)/(3*N) ≅ 0.6482776*N
  
  Based on this, the probability that the number of compares is >1B is <0.00000044449.  
  
  */ 
  
  public static void main(String[] args) {
    /* calculation of probability that the number of compares used by quicksort in
       sorting an array of length one million is greater than one billion. */
    
    int N = 1000000;
    double stddev = 1.*N*0.6482776; 
    double compares = 2.*N*ln(N); 
    double deviationsFrom1B = (1000000000 - compares)/stddev;
    double probOfDeviations = 1./(deviationsFrom1B * deviationsFrom1B);
    System.out.printf("%21.19f\n", probOfDeviations);
    // 0.0000004444877954067

  }
                      
}

