package ex22;

public class Ex2229NaturalMergeSortNumberOfPasses {

  /* p287
   2.2.29 Natural mergesort. Determine empirically the number of passes needed in a
  natural mergesort (see Exercise 2.2.16) for random Long keys with N=10^3 , 10^6,
  and 10^9. Hint: You do not need to implement a sort (or even generate full 64-bit 
  keys) to complete this exercise.
  
  Theoretically, for natural mergesort, given an average run length m, there is a
  reduction in number of passes by at least floor(lg(m)) compared to bottomUp resulting 
  in at most ceil(lg(N)-floor(lg(m))) passes. Random data generated by Random.nextLong() 
  has an average run length of 2, resulting in at most ceil(lg(N/2)) average number of
  of passes. This has been validated empirically for Long arrays with lengths of powers 
  of 10 from [1..7]. In particular the expected and actual number of passes is 9 for 
  N=10^3 and 19 for N=10^6 and the expected number of passes is 29 N=10^9 .
  */ 

  public static void main(String[] args) {   

  }
                      
}

