package ex34;

import static v.ArrayUtils.*;

/* p480
  3.4.6  Suppose that keys are t-bit integers. For a modular hash 
  function with prime M, prove that each key bit has the property 
  that there exist two keys differing only in that bit that have 
  different hash values. 
  
  First show by induction that the series [0,1,2,3...n] consists of
  consecutive pairs differing by one bit when n is odd.
  
  1. If n is odd there are an even number of pairs in [0,1,2,3...n].
  2. Base case: n = 1. true since 0 and 1 differ by one bit.
  3. Suppose the hypotheses that [0,1,2,3...n] consists of
     consecutive pairs differing by one bit when n is odd is true.
     Then it's true for [0,1,2,3...n+2] since n+2 is odd since n is
     odd, so n+2's least significant bit is set and the two highest
     numbers in [0,1,2,3...n+2] differ only in the least signficant bit.
     Therefore since [0,1,2,3...n] satisfies the hypothesis by assumption,
     [0,1,2,3...n+2] satisfies the hypothesis.
  
  A series [0,...prime] is a series [0,1,2,3...n] when n is prime, since
  all primes are odd, and it consists of all the possible keys of a modular 
  hash with prime. Therefore for each key bit there exist two keys differing 
  in only one bit that have different hash values.
*/             

public class Ex3406ModularHashKeyDifferentiationBySingleBits {


  public static void main(String[] args) {
    
    int[] a = range(0,11);
    for (int i : a) System.out.println(Integer.toBinaryString(i));
/*
    0      
    1
    10
    11
    100
    101
    110
    111
    1000
    1001
    1010
 
*/
  }

}

