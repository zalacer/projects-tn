package sort;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import analysis.Timer;

// from https://codereview.stackexchange.com/questions/63384/binary-heapsort-and-ternary-heapsort-implementation
// instrumented to count compares of array elements

public class BinaryAndTernaryHeapSort {
  private static int[] array;
  private static int heapSize;
  private static long compares;
  
  // This class should not be instantiated.
  private BinaryAndTernaryHeapSort(){}

  //=============================BINARY HEAPSORT=============================//

  private static void maxHeapify(int i) {
    int leftChild = left(i);
    int rightChild = right(i);
    int largest;

    //largest = leftChild <= heapSize && array[leftChild] > array[i] ? leftChild : i ;
    
    if (leftChild <= heapSize) {
      if (array[leftChild] > array[i]) largest = leftChild;
      else largest = i;
      compares++;
    } else largest = i;

    //if(rightChild <= heapSize && array[rightChild] > array[largest])
    //  largest = rightChild;
    
    if (rightChild <= heapSize) {
      if (array[rightChild] > array[largest]) largest = rightChild;
      compares++;
    }

    if(largest != i) {
      swap(i, largest);
      //Recursively heapify the subtree
      maxHeapify(largest);
    }
  }

  private static void binaryHeapSort(int[] toSort) {
    array = toSort;
    buildMaxHeap();
    // testing i >= 0 will be executed for i= array.length - 1, array.length - 2,......0,-1. 
    // So array.length + 1 times (n + 1)
    // i-- will be executed array.length times (n)
    for(int i = array.length - 1; i >= 0; i--){
      //swap executed n times (n)
      swap(0,i); 
      // heapSize - 1 executed n times (n)
      // assignment executed n times (n)
      heapSize = heapSize - 1; 
      //maxHeapify executed n times (n)
      maxHeapify(0); 
    }
  }

  private static void buildMaxHeap() {
    heapSize = array.length - 1;
    for(int i = (array.length - 1) / 2; i >= 0; i--)
      maxHeapify(i);
  }

  private static int left(int i) {
    return i << 1;
  }

  private static int right(int i) {
    return (i << 1) + 1;
  }

  //=============================TERNARY HEAPSORT=============================//

  private static void buildMaxHeapT() {
    heapSize = array.length - 1 ;
    for(int i = array.length - 1  / 3; i >= 0; i--)
      maxHeapifyT(i);
  }

  private static void maxHeapifyT(int i) {
    int leftChild = leftT(i);
    int rightChild = rightT(i);
    int middleChild = middleT(i);
    int largest;

    //largest = leftChild <= heapSize && array[leftChild] > array[i] ? leftChild : i;
    
    if (leftChild <= heapSize) {
      if (array[leftChild] > array[i]) largest = leftChild;
      else largest = i;
      compares++;
    } else largest = i;

    //if(rightChild <= heapSize && array[rightChild] > array[largest])
    //  largest = rightChild;
    
    if (rightChild <= heapSize) {
      if (array[rightChild] > array[largest]) largest = rightChild;
      compares++;
    }

    //if(middleChild <= heapSize && array[middleChild] > array[largest])
    //  largest = middleChild;
    
    if (middleChild <= heapSize) {
      if (array[middleChild] > array[largest]) largest = middleChild;
      compares++;
    }

    if(largest != i) {
      swap(i, largest);
      maxHeapifyT(largest);
    }
  }

  private static void ternaryHeapSort(int[] toSort) {
    array = toSort;
    buildMaxHeapT();

    for(int i = array.length - 1; i >= 0; i--) {
      swap(0,i); //add last element on array, i.e heap root
      heapSize = heapSize - 1; //shrink heap by 1
      maxHeapifyT(0);
    }
  }

  private static int leftT(int i) {
    return 3 * i + 1;
  }

  private static int middleT(int i) {
    return 3 * i + 2;
  }

  private static int rightT(int i) {
    return 3 * i + 3;
  }

  //==========================================================================//

  private static void swap(int i, int j) {
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  public static int[] toArray() {
    int[] a = new int[array.length];
    for (int i = 0; i < array.length ; i++) a[i-1] = array[i];
    return a;
  }

  public static void show() {
    if (array.length == 0) System.out.print("<nothing in heap>");
    for (int i = 1; i < array.length ; i++) System.out.print(array[i]+" ");
    System.out.println();
  }
  
  public static boolean isSorted(int[] a) {
    return isSorted(a, 0, a.length - 1);
  } 
  
  public static boolean isSorted(int[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static long sort (int[] toSort, boolean binaryHeap) {
    compares = 0;
    if (binaryHeap) {
      binaryHeapSort(toSort);
    } else { 
      ternaryHeapSort(toSort);
    }
    return compares;
  }

  public static void main(String[] args) {
    
    Timer t = new Timer();
    SecureRandom r = new SecureRandom();
    int[] a = range(1,1000059);
    shuffle(a,r);
    int[] b = a.clone();
    t.reset();
    System.out.println(sort(a,true)); // using binary heap 38796937 compares
    System.out.println(t.num()); //255 256 239 255 281 237 240 253
    System.out.println(isSorted(a));
    t.reset();
    System.out.println(sort(b,false)); // using ternary heap 35296021  compares
    System.out.println(t.num()); //236 249 244 247 244 244 250 242
    System.out.println(isSorted(b));
//    par(a);
    

  }

}
