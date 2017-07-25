package ex12;

//  p115
//  1.2.9  Instrument  BinarySearch (page 47) to use a  Counter to count the total number
//  of keys examined during all searches and then print the total after all searches are com-
//  plete. Hint : Create a  Counter in  main() and pass it as an argument to  rank() 


public class Ex1209BinarySearchCounter {
  
  // from p47 in text
  public static int rank(int key, int[] a)
  { // Array must be sorted.
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi)
    { // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  
  public static int c = 0; // individual run counter for all keys
  public static int cAccumulator = 0; // accumulator for c
  public static int keysFound = 0; // individual run counter for found keys
  public static int allKeysFound = 0; // accumulator for d
  public static int allKeysExamined = 0; // counter for all keys over all runs

  public static int rankWithCounters(int key, int[] a)
  { // Array must be sorted.
    c = 0; keysFound = 0;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi)
    { // Key is in a[lo..hi] or not present.
      c++; allKeysExamined++;
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) {
        hi = mid - 1;
      } else if (key > a[mid]) {
        lo = mid + 1;
      } else {
        keysFound++;
        System.out.printf("key=%2d c=%2d\n", key, c);
        return mid;
      }
    }
    return -1;
  }  
 
  public static void main(String[] args) {
    
    int[] a = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};
    for (Integer i : a) {
      rankWithCounters(i, a);
      cAccumulator+=c;
      allKeysFound+=keysFound;
    }
    System.out.println("cAccumulator="+cAccumulator);
    System.out.println("allKeysFound="+allKeysFound+"\n");
    int[]b = {3,5,7,9,11,13,15,17,19};
    for (Integer i : a) {
      rankWithCounters(i, b);
      cAccumulator+=c;
      allKeysFound+=keysFound;
    }
    System.out.println("cAccumulator="+cAccumulator);
    System.out.println("allKeysFound="+allKeysFound+"\n");
    System.out.println("allKeysExamined="+allKeysExamined);
    //  key= 1 c= 4
    //  key= 2 c= 3
    //  key= 3 c= 4
    //  key= 4 c= 5
    //  key= 5 c= 2
    //  key= 6 c= 4
    //  key= 7 c= 5
    //  key= 8 c= 3
    //  key= 9 c= 4
    //  key=10 c= 5
    //  key=11 c= 1
    //  key=12 c= 4
    //  key=13 c= 3
    //  key=14 c= 4
    //  key=15 c= 5
    //  key=16 c= 2
    //  key=17 c= 4
    //  key=18 c= 5
    //  key=19 c= 3
    //  key=20 c= 4
    //  key=21 c= 5
    //  cAccumulator=79
    //  allKeysFound=21
    //
    //  key= 3 c= 3
    //  key= 5 c= 2
    //  key= 7 c= 3
    //  key= 9 c= 4
    //  key=11 c= 1
    //  key=13 c= 3
    //  key=15 c= 2
    //  key=17 c= 3
    //  key=19 c= 4
    //  cAccumulator=145
    //  allKeysFound=30
    //
    //  allKeysExamined=145





  }

}
