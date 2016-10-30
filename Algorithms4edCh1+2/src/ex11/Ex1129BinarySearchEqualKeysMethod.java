package ex11;

//  1.1.29 Equal keys. Add to  BinarySearch a static method  rank() that takes a key and
//  a sorted array of  int values (some of which may be equal) as arguments and returns the
//  number of elements that are smaller than the key and a similar method  count() that
//  returns the number of elements equal to the key. Note : If  i and  j are the values 
//  returned by rank(key, a) and  count(key, a) respectively, then  a[i..i+j-1 ] are the
// values in the array that are equal to key 

public class Ex1129BinarySearchEqualKeysMethod {
  
  public static int z = 0;
  
  // from BinarySearch.java
  public static int indexOf(int[] a, int key) {
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      z++;
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  
  public static int rank(int[] a, int key) {
    // for sorted array returns number of elements < key
    // whether or not key is present - this is valid info and
    // enables using it in implementation of countsorted
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i] < key) c++;
      z++;
    }
    return c; // key not in a
  }
  
  public static int countsorted(int[] a, int key) {
    // for sorted array; counts number of occurrences of key in a
   return rank(a, key+1) - rank(a, key);
  }
  
  public static int count(int[] a, int key) {
    // for unsorted array counts number of occurrences of key in a
    int c = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] == key) c++;
    return c;
  }
  
  public static void printOccurencesOfValue(int[] a, int value) {
    // assumes a is sorted
    if (countsorted(a, value) == 0) return;
    for (int i = rank(a,value); i < rank(a,value)+countsorted(a,value)-1; i++)
      System.out.print(a[i]+",");
    System.out.println(a[rank(a,value)+countsorted(a,value)-1]);
  }

  public static void main(String[] args) {
    
    int[] a = {1,2,3,4,5,5,6,7};
    z = 0;
    System.out.println(indexOf(a,5)); //5
    System.out.println("z="+z); //2
    z = 0;
    System.out.println(rank(a,5)); //4
    System.out.println("z="+z); //4
    System.out.println(count(a,5)); //2
    System.out.println(countsorted(a,5)); //2
    printOccurencesOfValue(a,5); //5,5
    int[] b = {5,6,7,8,9};
    System.out.println(indexOf(b,5)); //0
    System.out.println(rank(b,5)); //0
    System.out.println(count(b,5)); //1
    int[] c = {1,5,2,5,2,5,5,3,4,2,5,4,3,1,3,5,3};
    for (int i = 0; i < a.length; i++)
      System.out.println("count of "+a[i]+" in c: "+count(c, a[i]));
    //  count of 1 in c: 2
    //  count of 2 in c: 3
    //  count of 3 in c: 4
    //  count of 4 in c: 2
    //  count of 5 in c: 6
    //  count of 6 in c: 0
    //  count of 7 in c: 0
    int[] d = {1,1,2,2,2,3,3,3,3,4,4,5,5,5,5,5};
    System.out.println(count(d,3)); //4
    System.out.println(countsorted(d,3)); //4
    System.out.println(count(d,1)); //2
    System.out.println(countsorted(d,1)); //2
    System.out.println(count(d,5)); //5
    System.out.println(countsorted(d,5)); //5
    System.out.println(rank(d,5)); //11
    printOccurencesOfValue(d,5); //5,5,5,5,5
    System.out.println(rank(d,6)); //16
    System.out.println(countsorted(d,6)); //0
    printOccurencesOfValue(d,6); // nothing printed
    System.out.println(countsorted(d,0)); //0
    printOccurencesOfValue(d,0); // nothing printed
    
  }

}
