package ex11;

//  1.1.22  Write a version of  BinarySearch that uses the recursive  rank() given on page
//  25 and traces the method calls. Each time the recursive method is called, print the argu-
//  ment values  lo and  hi , indented by the depth of the recursion. Hint: Add an argument
//  to the recursive method that keeps track of the depth.

public class Ex1122BinarySearchTrace {

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
    
    int[] a1 = {0,1,2,3,4,5,6,7,8,9};
    int r11 = rank(3,a1);
    System.out.println(r11);
    int r12 = rank(6,a1);
    System.out.println(r12);
    int[] a2 = {9,8,7,6,5,4,3,2,1,0};
    int r21 = rank(3,a2);
    System.out.println(r21);
    int r22 = rank(6,a2);
    System.out.println(r22);

  }

}
