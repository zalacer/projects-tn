package u;

import static u.ArrayUtils.*;

//import java.util.Scanner;

// http://www.sanfoundry.com/java-program-implement-heap-sort/

// This Java program is to implement max heap. A Heap data structure is a 
// Tree based data structure that satisfies the HEAP Property "If A is a
// parent node of B then key(A) is ordered with respect to key(B) with the
// same ordering applying across the heap." So in a min heap this property
// will be "If A is a parent node of B then key(A) is less than key(B) with
// the same ordering applying across the heap; and in a max heap the key(A)
// will be greater than Key(B).

// When a heap is stored in a zero-based array, the children of the node at
// index i are in indices 2i+1 and 2i+2.  For example:

//  int[] a = {1,2,3,4,5,6,7,8,9};
//  maxheapify(a); results in [9, 8, 6, 7, 5, 2, 1, 4, 3] 
// 
// In the result, the children of 9 are 8 and 6, the children of 8 are 7 and 
// 5, the children of 6 are 2 and 1 and the children of 7 are 4 and 3.
//
// Sorting, however, acts by extracting the root element, swapping it with 
// the element at iteration index i and then rebuilding the remaining elements
// into a heap. Since in the case of a max heap the index holds the largest
// value (always >= to that of its children) the result is an ascending in
// place sort, but the reverse for a min heap. For example:
//
//  int[] a = {1,2,3,4,5,6,7,8,9};
//  maxsort(a); printArray(a);    //[1, 2, 3, 4, 5, 6, 7, 8, 9]
//  maxheapify(a); printArray(a); //[9, 8, 6, 7, 5, 2, 1, 4, 3]
//  
//  int[] b = {9,8,7,6,5,4,3,2,1};
//  minsort(b); printArray(b);    //[9, 8, 7, 6, 5, 4, 3, 2, 1]
//  minheapify(b); printArray(b); //[1, 2, 4, 3, 5, 8, 9, 6, 7]

public class HeapSort {    

  private static int M; // for min heaps
  private static int N; // for max heaps

  public static void maxsort(int a[]) {       
    maxheapify(a);        
    for (int i = N; i > 0; i--) {
      swap(a, 0, i);
      N = N-1;
      maxheap(a, 0);
    }
  }
  
  public static void minsort(int[] a) {
    minheapify(a);        
    for (int i = M; i > 0; i--) {
      swap(a, 0, i);
      M = M-1;
      minheap(a, 0);
    }
  } 

  /* Function to build a max heap */   
  public static void maxheapify(int a[]) {
    N = a.length-1;
    for (int i = N/2; i >= 0; i--) maxheap(a, i);        
  }

  /* Function to swap largest element in max heap */        
  public static void maxheap(int a[], int i) { 
    int left = 2*i ;
    int right = 2*i + 1;
    int max = i;
    if (left <= N && a[left] > a[i]) max = left;
    if (right <= N && a[right] > a[max]) max = right;
    if (max != i) {
      swap(a, i, max);
      maxheap(a, max);
    }
  }
  
  /* Function to build a min heap */  
  public static void minheapify(int[] a) {
    M = a.length-1;
    for (int i = M/2; i >= 0; i--) minheap(a, i);        
  }
  
  /* Function to swap smallest element in a min heap */        
  public static void minheap(int[] a, int i) {
    //    System.out.print("minheap: "); printArray(a);
    int left = 2*i ;
    int right = 2*i + 1;
    int min = i;
    if (left <= M && a[left] < a[i]) min = left;
    if (right <= M && a[right] < a[min]) min = right;
    if (min != i) {
      swap(a, i, min);
      minheap(a, min);
    }
  }

  /* Function to swap two numbers in an array */
  public static void swap(int a[], int i, int j) {
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp; 
  }
  

  /* Main method */
  public static void main(String[] args) {
    
    int[] a = {1,2,3,4,5,6,7,8,9};
    maxsort(a); printArray(a);    //[1, 2, 3, 4, 5, 6, 7, 8, 9]
    maxheapify(a); printArray(a); //[9, 8, 6, 7, 5, 2, 1, 4, 3]
    
    int[] b = {9,8,7,6,5,4,3,2,1};
    minsort(b); printArray(b);    //[9, 8, 7, 6, 5, 4, 3, 2, 1]
    minheapify(b); printArray(b); //[1, 2, 4, 3, 5, 8, 9, 6, 7]
    
    System.out.println(M);
    System.out.println(N);

//    Scanner scan = new Scanner( System.in );        
//
//    System.out.println("Heap Sort Test\n");
//
//    int n, i;    
//
//    /* Accept number of elements */
//
//    System.out.println("Enter number of integer elements");
//
//    n = scan.nextInt();    
//
//    /* Make aay of n elements */
//
//    int a[] = new int[ n ];
//
//    /* Accept elements */
//
//    System.out.println("\nEnter "+ n +" integer elements");
//
//    for (i = 0; i < n; i++)
//
//      a[i] = scan.nextInt();
//
//    /* Call method sort */
//
//    maxsort(a);
//
//    /* Print sorted aay */
//
//    System.out.println("\nElements after sorting ");        
//
//    for (i = 0; i < n; i++)
//
//      System.out.print(a[i]+" ");            
//
//    System.out.println();            

  }    

}
