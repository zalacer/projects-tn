package ex11;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;

//  1.1.38 Binary search versus brute-force search. Write a program  BruteForceSearch
//  that uses the brute-force search method given on page 48 and compare its running time
//  on your computer with that of  BinarySearch for  largeW.txt and  largeT.txt 

// on my first tests indexOfB was loaded from another class while rankB was in this file and
// was much faster runing than the former, perhaps due to a class loading factor. however, 
// after putting them both in this file indexOfB consistently takes 0 millisecs while rankB 
// takes 3-5. similar to indexOfB Arrays.binarySearch takes 0ms consistently -- even though 
// it's loaded from another file, although perhaps it's loaded faster since it's in a standard
// JDK library.

public class Ex1138BruteForceSearch {
  
  // from p48 
  public static int rankB(int key, int[] a) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == key) return i;
    return -1;
  }
  
  public static int indexOfB(int[] a, int key) {
    // assumes a is sorted
    if (a == null || a.length == 0) return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }


  public static void main(String[] args) {
    
    Scanner sc = null;
    try {
      sc = new Scanner(new File("LargeW.txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    Instant start = null;
    Instant end = null;
    long millis = 0;
    int[] n = new int[1000000];
    int loc = 0;
    
    start = Instant.now();
    while (sc.hasNextInt()) n[loc++] = sc.nextInt();
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("timeToReadData="+millis);
    
    start = Instant.now();
    Arrays.sort(n);
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("timeToSort="+millis);
    
    int v = 999999;
    System.out.println("v="+v);
    
    //Arrays.binarySearch
    start = Instant.now();
    int binsearchIndex = Arrays.binarySearch(n, v);
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("binsearchMillis="+millis); 
    System.out.println("binsearchIndex="+binsearchIndex);
    
    //indexOfB
    start = Instant.now();
    int bsearchIndex = indexOfB(n, v);
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("bsearchIndex="+millis); 
    System.out.println("bsearchIndex="+bsearchIndex);
    
    //rankB
    start = Instant.now();
    int rankIndex = rankB(v, n);
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("rankBMillis="+millis); 
    System.out.println("rankBIndex="+rankIndex);
  
    //  LargeW.txt test to search for 923282:
    //  timeToReadData=1011
    //  timeToSort=212
    //  bsearchMillis=20
    //  bsearchIndex=922785
    //  rankBMillis=3
    //  rankBIndex=922784
    
    //  LargeW.txt test to search for 923282:
    //  using Arrays.binarySearch
    //  v=923282
    //  rankBMillis=4
    //  rankBIndex=922784
    //  bsearchIndex=0
    //  binsearchIndex=922785
    //  bsearchMillis=0 //Arrays.binarySearch
    //  binsearchIndex=922785
    
    //  LargeW.txt test to search for 923282:
    //  including Arrays.binarySearch
    //  v=923282
    //  rankBMillis=4
    //  rankBIndex=922784
    //  bsearchIndex=0
    //  binsearchIndex=922785
    //  bsearchMillis=0 //Arrays.binarySearch
    //  binsearchIndex=922785
    
    //  LargeW.txt test to search for 999999 (last element in sorted array):
    //  including Arrays.binarySearch
    //  v=923282
    //  rankBMillis=3
    //  rankBIndex=999999
    //  bsearchIndex=0
    //  binsearchIndex=999999
    //  bsearchMillis=0 //Arrays.binarySearch
    //  binsearchIndex=999999
    
    //  LargeW.txt test to search for 999999 (last element in sorted array):
    //  including Arrays.binarySearch
    //  putting Arrays.binarySearch first, indexOfB 2nd and rankB last
    //  v=999999
    //  binsearchMillis=0
    //  binsearchIndex=999999
    //  bsearchIndex=0
    //  bsearchIndex=999999
    //  rankBMillis=4
    //  rankBIndex=999999
    
    
    //  LargeW.txt test to search for 999999 (last element in sorted array):
    //  timeToReadData=995
    //  timeToSort=200
    //  v=999999
    //  bsearchMillis=19
    //  bsearchIndex=999999
    //  rankBMillis=3
    //  rankBIndex=999999
    
    //  LargeW.txt test to search for 999999 (last element in sorted array):
    //  doing rankB search before bsearch
    //  timeToReadData=995
    //  timeToSort=200
    //  v=999999
    //  rankBMillis=4
    //  rankBIndex=999999
    //  bsearchMillis=19
    //  bsearchIndex=999999
    
    //  LargeW.txt test to search for 0 (first element in sorted array):
    //  doing rankB search before bsearch
    //  timeToReadData=995
    //  timeToSort=200
    //  v=0
    //  rankBMillis=0
    //  rankBIndex=0
    //  bsearchMillis=20
    //  bsearchIndex=2 //must be at least 2 0's in the array
    
    //  LargeT.txt test to search for 823164:
    //  timeToReadData=1010
    //  timeToSort=232
    //  bsearchMillis=19
    //  bsearchIndex=823114
    //  rankBMillis=5
    //  rankBIndex=823112 
    
    //  LargeT.txt test to search for 999999 (last element in sorted array):
    //  timeToReadData=1002
    //  timeToSort=166
    //  bsearchMillis=19
    //  bsearchIndex=999999
    //  rankBMillis=5
    //  rankBIndex=999999 
    
    //  LargeT.txt test to search for 0 (first element in sorted array):
    //  timeToReadData=1004
    //  timeToSort=177
    //  bsearchMillis=23
    //  bsearchIndex=0
    //  rankBMillis=0
    //  rankBIndex=0 
    
  
  }

}
