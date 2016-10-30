package ex14;
 
import static v.FileUtils.*;
import static v.ArrayUtils.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//  p209
//  1.4.8  Write a program to determine the number pairs of values in an input file that
//  are equal. If your first try is quadratic, think again and use  Arrays.sort() to develop
//  a linearithmic solution.

public class Ex1408CountPairs {

  public static int countPairs(int[] a) {
    // count pairs of identical elements in a. if an element is repeated n times, n/2
    // is added to the count, for example 1,1,1,1 and 1,1,1,1,1 both have two pairs.
    // this method has linearithmic order of growth due to Arrays.sort mergesort
    if (a == null) throw new IllegalArgumentException("countPairs: the array must be non null");
    int N = a.length;
    if (N < 2) return 0;
    Arrays.sort(a); // NlogN
    int c = 0;
    for (int i = 0; i < N-1; i++)
      if (a[i] == a[i+1]) {c++; i++;} //N
    return c;
  }

  public static void main(String[] args) {
    // going thru exercise of generating an array, writing it to a file, then
    // reading it back into an array for processing, since exercise requires
    // reading data from a file
    String f = "Ex1408CountPairs.txt";
    updateFileWithArray(f, randomArray(1000, 1)); //writes 1000 1 digit ints to f
    ArrayList<Integer> al = new ArrayList<>();
    Scanner in = null;
    try {
      in = new Scanner(Paths.get(f), "UTF-8");
      while (in.hasNextInt()) al.add(in.nextInt());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      in.close();
    }
    int[] a = (int[]) unbox(al.toArray(new Integer[0]));
    System.out.println(countPairs(a)); //497

  }

}
