package ex11;

import java.util.Arrays;

//  1.1.10  What is wrong with the following code fragment?
//    int[] a;
//    for (int i = 0; i < 10; i++)
//      a[i] = i * i;
//  Solution: It does not allocate memory for  a[] with  new . This code results in a
//  variable a might not have been initialized  compile-time error.

// a is not initialized

public class Ex1110EvaluateCodeFragment {

  public static void main(String[] args) {
    
    int[] a = new int[10];
    for (int i = 0; i < 10; i++)
      a[i] = i * i;
    System.out.println(Arrays.toString(a)); //[0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
 
  }

 

}

