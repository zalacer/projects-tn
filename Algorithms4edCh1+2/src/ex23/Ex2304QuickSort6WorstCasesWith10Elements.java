package ex23;

import static sort.Quicks.printArraysOfLength10WithMaxNumberOfCompares;

public class Ex2304QuickSort6WorstCasesWith10Elements {

  /* p303  
  2.3.4  Suppose that the initial random shuffle is omitted. Give six arrays of
  ten elements for which Quick.sort() uses the worst-case number of compares.
  
  I found empirically the actual worst case number of compares for arrays of length
  ten with all distinct values is 63 and there are 512 of them.
  
  Here are the top 3 and the bottom 3 in permutation order as int arrays:
  
    [1,2,3,4,5,6,7,8,9,10]
    [1,2,3,4,5,6,7,8,10,9]
    [1,2,3,4,5,6,7,10,8,9]
    [10,2,5,4,3,1,6,7,8,9]
    [10,4,2,3,1,5,6,7,8,9]
    [10,2,1,3,4,5,6,7,8,9]
    
  To list all of them, run printArraysOfLength10WithMaxNumberOfCompares() in main().
 
  */ 

  public static void main(String[] args) {
    
    printArraysOfLength10WithMaxNumberOfCompares();

  }
                      
}

