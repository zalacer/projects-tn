package ex11;

import java.util.Arrays;

//  1.1.15  Write a static method histogram() that takes an array  a[] of  int values and
//  an integer M as arguments and returns an array of length M whose  i th entry is the 
//  number of times the integer i appeared in the argument array. If the values in  a[]
//  are all between  0 and  M–1, the sum of the values in the returned array should be 
//  equal to a.length 

public class Ex1115Histogram {
  
  public static int[] histogram(int[] a, int M) {
    int[] h = new int[M];
    int v = 0;
    for (int i = 0; i < a.length; i++) {
      v = a[i];
      if (v >= 0 && v <= M-1) h[v]++;
    }
    return h;
  }
  
  public static int arraySum(int[] a) {
    int sum = 0;
    for (int i = 0; i < a.length; i++) sum+=a[i];
    return sum;
  }
  
  public static void printArray(int[] a) {
    System.out.println(Arrays.toString(a));
  }
 
  public static void main(String[] args) {
    
    int[] a = {1,2,2,3,3,3,4,4,4,4,5,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,7,7,8,8,8,8,8,8,8,8};
    int[] h =  histogram(a, 9);
    printArray(h); //[0, 1, 2, 3, 4, 5, 6, 7, 8]
    System.out.println(a.length); //36
    assert arraySum(h) == a.length; 
       
  }

}
