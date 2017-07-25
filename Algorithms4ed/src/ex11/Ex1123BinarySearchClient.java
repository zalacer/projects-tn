package ex11;

import java.util.Arrays;

import edu.princeton.cs.algs4.BinarySearch;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

//  1.1.23  Add to the  BinarySearch test client the ability to respond to a second argu-
//  ment:  + to print numbers from standard input that are not in the whitelist,  - to print
//  numbers that are in the whitelist.

public class Ex1123BinarySearchClient {

  public static int rank(int key, int[] a) {
    return rank(key, a, 0, a.length - 1, 0); 
  }

  public static int rank(int key, int[] a, int lo, int hi, int depth) { 
    // Index of key in a[], if present, is not smaller than lo
    // and not larger than hi.
    System.out.println("depth "+depth);
    if (lo > hi) return -1;
    int mid = lo + (hi - lo) / 2;
    if (key < a[mid]) return rank(key, a, lo, mid - 1, ++depth);
    else if (key > a[mid]) return rank(key, a, mid + 1, hi, ++depth);
    else return mid;
  }

  public static void main(String[] args) {

    // read the integers from a file
    In in = new In(args[0]);
    int[] whitelist = in.readAllInts();

    boolean printWhiteList = false;
    String control = args[1];
    if (control.equals("-")) {
      printWhiteList = true;
    }


    // sort the array
    Arrays.sort(whitelist);

    // read integer key from standard input; 
    // if  printWhiteList = false, print key if it's not in whitelist
    // if  printWhiteList = true, print key if it's in whitelist
    while (!StdIn.isEmpty()) {
      int key = StdIn.readInt();
      if (!printWhiteList) {
        if (BinarySearch.indexOf(whitelist, key) == -1)
          StdOut.println(key);
      } else {
        if (BinarySearch.indexOf(whitelist, key) != -1)
          StdOut.println(key);
      }
    }

    // whitelist is in the file whitelist.txt which contains keys 1, 2, 3, 4, 5
    // in both tests keys 1, 3, 5, 7, 9, 11 are provided in StdIn
    // test 1 with arg[1] == "+", output: 7, 9, 11
    // test 2 with arg[1] == "-", output: 1, 3, 5

  }

}
