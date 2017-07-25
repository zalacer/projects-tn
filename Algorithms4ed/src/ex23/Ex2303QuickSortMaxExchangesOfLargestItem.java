package ex23;

import static sort.Quicks.printNumberOfExchangesOfMaxElement;

public class Ex2303QuickSortMaxExchangesOfLargestItem {

  /* p303  
  2.3.3  What is the maximum number of times during the execution of Quick.sort()
  that the largest item can be exchanged, for an array of length N ?
  
  For algorithm 2.5 it's N/2 (floor(N/2), extrapolated from empirical results from output
  of the method below for arrays with all distinct elements. 
  
  
  */ 

  public static void main(String[] args) {   
    
    printNumberOfExchangesOfMaxElement();
    /*
        arrayLength    #exchangesOfMaxElement
         1             0
         2             1
         3             1
         4             2
         5             2
         6             3
         7             3
         8             4
         9             4
        10             5
        11             5
        12             6

  
  */
  }
                      
}

