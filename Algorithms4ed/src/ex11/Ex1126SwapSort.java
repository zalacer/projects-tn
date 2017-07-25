package ex11;

import static u.ArrayUtils.*;

//  1.1.26 Sorting three numbers. Suppose that the variables  a ,  b ,  c , and  t
//  are all of the same numeric primitive type. Show that the following code puts
//  a ,  b , and  c in ascending order:
//    if (a > b) { t = a; a = b; b = t; }
//    if (a > c) { t = a; a = c; c = t; }
//    if (b > c) { t = b; b = c; c = t; }

// The code puts a, b and c in ascending order by inspection: statements
// 1 and 2 ensures a <= b && a <= c, then statement 3 ensures b <= c.
// Only when a > b > c are all 3 swaps done.

// if (a > b) { t = a; a = b; b = t; }
// at this point a <= b
// if (a > c) { t = a; a = c; c = t; }
// at this point a <= b && a <= c
// if (b > c) { t = b; b = c; c = t; }
// at this point a <= b && a <= c && b <= c

// As a generalization for sorting a 1D array of any length see swapSort below.
// It appears to be the same as insertionSort with obvious indexing.

public class Ex1126SwapSort {
  
  public static void swapSort(int[] a) {
    if (a == null || a.length < 2) return;
    int t;
    for (int i = 0; i < a.length - 1; i++)
      for (int j = i+1; j < a.length; j++)
        if (a[i] > a[j]) {t = a[i]; a[i] = a[j]; a[j] = t;}
  }

  public static void main(String[] args) {
    
    int a = 3; int b = 2; int c = 1; int t = 0;

    if (a > b) { 
      t = a; a = b; b = t;
      System.out.println("swapped a with b; "+a+","+b+","+c);
    }

    if (a > c) {
      t = a; a = c; c = t; 
      System.out.println("swapped a with c; "+a+","+b+","+c);
    }

    if (b > c) {
      t = b; b = c; c = t; 
      System.out.println("swapped b with c; "+a+","+b+","+c);
    }
    
    System.out.println(a+","+b+","+c);
    
    //  swapped a with b; 2,3,1
    //  swapped a with c; 1,3,2
    //  swapped b with c; 1,2,3
    //  1,2,3
    
    int[] y = {2,1};
    printArray(y); //[2, 1]
    swapSort(y);
    printArray(y); //[1, 2]
    
    int[] z = {9,8,7,6,5,4,3,2,1};
    printArray(z); //[9, 8, 7, 6, 5, 4, 3, 2, 1]
    swapSort(z);
    printArray(z); //[1, 2, 3, 4, 5, 6, 7, 8, 9]
    
    int[] w = new int[9]; w[0] = 3; w[1] = 2; w[4] = 1;
    swapSort(w);
    printArray(w); //[0, 0, 0, 0, 0, 0, 1, 2, 3]


  }

}
