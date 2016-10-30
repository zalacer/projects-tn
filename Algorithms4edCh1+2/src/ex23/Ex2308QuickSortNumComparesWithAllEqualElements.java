package ex23;

import static sort.Quicks.ncei;

public class Ex2308QuickSortNumComparesWithAllEqualElements {

  /* p303  
  2.3.8  About how many compares will Quick.sort() make when 
  sorting an array of N items that are all equal?
  
  For algorithm 2.5 the number of comparisons c(N) when all elements are equal 
  is exactly given by the base case c(1) = 0 and the recursion relation 
            c(N) = N + N%2 + c(N/2) + c((N-1)/2).
  This was determined by inspection of the code and analysis of the actual 
  number of comparisons for several array lengths. For N>2 it's strictly less
  than but appears to eventually converge to N*lg(N) as shown below using
  ncei(N) to calculate the number of comparisons:
  
    N          ((1.*(N)*lg(N))-ncei(N))/ncei(N)
    10         0.2776646518797548
    100        0.1779886861302704
    1000       0.12000272922702718
    10000      0.09784956124307628
    100000     0.06394986160260911
    1000000    0.05914351893035099
    10000000   0.05467287181019356
    100000000  0.03737905177616354
   
  */ 
  
  public static void main(String[] args) {
    
    // print number of comparisons using algorithm 2.5 for arrays with equal
    // items and lengths [2..25]
    for (int i = 2; i < 26; i++)
      System.out.print(ncei(i)+" ");
    System.out.println();
    // 2 4 6 10 12 16 18 22 26 32 34 38 42 48 50 54 58 64 68 74 80 88 90 94
    
    
  }
                      
}

