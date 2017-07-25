package ex14;

import static v.ArrayUtils.*;
import static analysis.BitonicMax.bitonicMax;

//  p210
//  1.4.20 Bitonic search. An array is bitonic if it is comprised of an increasing sequence
//  of integers followed immediately by a decreasing sequence of integers. Write a program
//  that, given a bitonic array of N distinct  int values, determines whether a given integer
//  is in the array. Your program should use ~3lg N compares in the worst case.

//  http://algs4.cs.princeton.edu/14analysis/
//  Answer: Use a version of binary search, as in BitonicMax.java, to find the maximum 
//  (in ~ 1 lg N compares); then use binary search to search in each piece (in ~ 1 lg N 
//  compares per piece). 

// BitonicMax.bitonicMax doesn't explicity find the max.

public class Ex1420BitonicSearch {
  
  public static void main(String[] args) {
    int[] bitonic = new int[]{0,1,2,8,17,25,27,34,39,47,43,36,32,28,27,19,12,4,0,-4,-9};
    System.out.println(bitonicMax(bitonic, 0, bitonic.length-1)); //9
    System.out.println(bitonic[bitonicMax(bitonic, 0, bitonic.length-1)]); //47
    
    int[] b2 = append(range(0,11),range(5,-5));
    pa(b2); //int[0,1,2,3,4,5,6,7,8,9,10,5,4,3,2,1,0,-1,-2,-3,-4]
    System.out.println(b2[bitonicMax(b2, 0, b2.length-1)]); //10
    
    int[] b3 = new int[]{1,2};
    System.out.println(b3[bitonicMax(b3, 0, b3.length-1)]); //2
    
    b3 = new int[]{2,1};
    System.out.println(b3[bitonicMax(b3, 0, b3.length-1)]); //2
    
    b3 = new int[]{1};
    System.out.println(b3[bitonicMax(b3, 0, b3.length-1)]); //1
    
  }

}
