package ch01.fundamentals;

import java.util.ArrayList;

// 15. Write a program that stores Pascal’s triangle up to a given n in an
// ArrayList<ArrayList<Integer>>

public class Ch0115Pascal {

  public static ArrayList<ArrayList<Integer>> makePascalTriangle(int n) {
    ArrayList<ArrayList<Integer>> t = new ArrayList<>();
    if (n < 1) return t;
    for (int i = 0; i < n; i++) {
      t.add(new ArrayList<Integer>());
      t.get(i).add(new Integer(1));
      if (i > 0) {
        for (int j = 1; j < i; j++ )
          t.get(i).add(new Integer(0));
        t.get(i).add(new Integer(1));
        for (int k = 1; k < i; k++) {
          t.get(i).set(k,new Integer(t.get(i-1).get(k-1).intValue()+t.get(i-1).get(k).intValue()));
        }
      }
    }       
    return t;
  }

  public static void printPascalTriangle (ArrayList<ArrayList<Integer>> a) {
    if (a.size() == 0) {
      System.out.println("None");
    } else {
      for (ArrayList<Integer> e: a) System.out.println(e);
    }
    System.out.println();
  }

  public static void main(String[] args) {

    printPascalTriangle(makePascalTriangle(-15));
    //    None      

    printPascalTriangle(makePascalTriangle(1));     
    //    [1]

    printPascalTriangle(makePascalTriangle(7)); 
    //    [1]
    //    [1, 1]
    //    [1, 2, 1]
    //    [1, 3, 3, 1]
    //    [1, 4, 6, 4, 1]
    //    [1, 5, 10, 10, 5, 1]
    //    [1, 6, 15, 20, 15, 6, 1]

    printPascalTriangle(makePascalTriangle(15));
    //    [1]
    //    [1, 1]
    //    [1, 2, 1]
    //    [1, 3, 3, 1]
    //    [1, 4, 6, 4, 1]
    //    [1, 5, 10, 10, 5, 1]
    //    [1, 6, 15, 20, 15, 6, 1]
    //    [1, 7, 21, 35, 35, 21, 7, 1]
    //    [1, 8, 28, 56, 70, 56, 28, 8, 1]
    //    [1, 9, 36, 84, 126, 126, 84, 36, 9, 1]
    //    [1, 10, 45, 120, 210, 252, 210, 120, 45, 10, 1]
    //    [1, 11, 55, 165, 330, 462, 462, 330, 165, 55, 11, 1]
    //    [1, 12, 66, 220, 495, 792, 924, 792, 495, 220, 66, 12, 1]
    //    [1, 13, 78, 286, 715, 1287, 1716, 1716, 1287, 715, 286, 78, 13, 1]
    //    [1, 14, 91, 364, 1001, 2002, 3003, 3432, 3003, 2002, 1001, 364, 91, 14, 1]

  }

}
