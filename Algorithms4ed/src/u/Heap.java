package u;

import static u.ArrayUtils.*;

//import java.time.Duration;
//import java.time.Instant;
//import java.util.Random;

//http://www.sanfoundry.com/java-program-implement-heap-sort/
// implements a max heap

public class Heap {

  private int N = 0;
  private boolean max = true;
  private int[] a = null;

  public Heap(){
    this.a = new int[0];
    this.max = true;
    this.N = 0;
  };
  
  public Heap(boolean max) {
    this.a = new int[0];
    this.max = max;
    this.N = 0;
  }

  public Heap(int[] a, boolean max) {
    this.a = a;
    this.max = max;
    this.N = a.length;
  }

  public int getN() {
    return N;
  }

  public void setN(int n) {
    N = n;
  }

  public boolean isMax() {
    return max;
  }

  public boolean getMax() {
    return max;
  }

  public void setMax(boolean max) {
    this.max = max;
  }

  public int[] getA() {
    return a;
  }

  public void setA(int[] a) {
    this.a = a;
  }

  public void heapSort() {       
    heapify();        
    for (int i = N; i > 0; i--) {
      swap(0, i);
      N = N-1;
      maxheap(0);
    }
  } 

//  public static void heapSort(int[] a) {
//    heapify(a);        
//    for (int i = M; i > 0; i--) {
//      swap(a, 0, i);
//      M = M-1;
//      maxheap(a, 0);
//    }
//  }

  public void reverseHeapSort() {       
    minHeapify();        
    for (int i = N; i > 0; i--) {
      swap(0, i);
      N = N-1;
      minheap(0);
    }
  } 

//  public static void reverseHeapSort(int[] a) {
//    minHeapify(a);        
//    for (int i = M; i > 0; i--) {
//      swap(a, 0, i);
//      M = M-1;
//      minheap(a, 0);
//    }
//  } 

  public void heapify() {
    N = a.length-1;
    for (int i = N/2; i >= 0; i--)
      maxheap(i);        
  }

  /* Function to build a heap */   
//  public static void heapify(int[] a) {
//    M = a.length-1;
//    for (int i = M/2; i >= 0; i--)
//      maxheap(a, i);        
//  }

  public void minHeapify() {
    N = a.length-1;
    for (int i = N/2; i >= 0; i--)
      minheap(i);        
  }

//  public static void minHeapify(int[] a) {
//    M = a.length-1;
//    for (int i = M/2; i >= 0; i--)
//      minheap(a, i);        
//  }

  public void maxheap(int i) { 
    int left = 2*i ;
    int right = 2*i + 1;
    int max = i;

    if (left <= N && a[left] > a[i])
      max = left;

    if (right <= N && a[right] > a[max])        
      max = right;

    if (max != i) {
      swap(i, max);
      maxheap(max);
    }
  }

  /* Function to swap largest element in heap */        
//  public static void maxheap(int[] a, int i) {
//    //    System.out.print("maxheap: ");
//    //    printArray(a);
//    int left = 2*i ;
//    int right = 2*i + 1;
//    int max = i;
//
//    if (left <= M && a[left] > a[i])
//      max = left;
//
//    if (right <= M && a[right] > a[max])        
//      max = right;
//
//    if (max != i) {
//      swap(a, i, max);
//      maxheap(a, max);
//    }
//  }

  public void minheap(int i) { 
    int left = 2*i ;
    int right = 2*i + 1;
    int min = i;

    if (left <= N && a[left] < a[i])
      min = left;

    if (right <= N && a[right] < a[min])        
      min = right;

    if (min != i) {
      swap(i, min);
      minheap(min);
    }
  }

  /* Function to swap smallest element in heap */        
//  public static void minheap(int[] a, int i) {
//    //    System.out.print("minheap: ");
//    //    printArray(a);
//    int left = 2*i ;
//    int right = 2*i + 1;
//    int min = i;
//
//    if (left <= M && a[left] < a[i])
//      min = left;
//
//    if (right <= M && a[right] < a[min])        
//      min = right;
//
//    if (min != i) {
//      swap(a, i, min);
//      minheap(a, min);
//    }
//  }

  public void swap(int i, int j) {
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp; 
  }

//  public static void swap(int[] a, int i, int j) {
//    int tmp = a[i];
//    a[i] = a[j];
//    a[j] = tmp; 
//  } 

  public int peek() {
    if (max) {
      heapSort();
    } else {
      reverseHeapSort();
    }
    return a[0];
  }

//  public static int peek(int[] a) {
//    return a[0];
//  }

  public int poll() {
    if (max) {
      heapSort();
    } else {
      reverseHeapSort();
    }
    int len = a.length;
    int b[] =  new int[len-1];
    for (int i = 0; i < len-1; i++) b[i] = a[i+1];
    int z = a[0];
    a = b;
    setN(getN()-1);
    return z;
  }

//  public static Object[] poll(int[] a) {
//    int len = a.length;
//    int b[] =  new int[len-1];
//    for (int i = 0; i < len-1; i++) b[i] = a[i];
//    return new Object[]{a[0],b};
//  }

  public void add(int v) {
    N = a.length;
    System.out.println("add.N="+N);
    printArray(a);
    int[] b = new int[N+1];
    for (int i=0; i < N; i++) b[i]=a[i];
    b[N] = v;
    if (max) {
      heapSort();
    } else {
      reverseHeapSort();
    }
    a = b;
    N = a.length;
  }

//  public static int[] add(int[] a, int v, boolean max) {
//    int len = a.length;
//    int[] b = new int[len+1];
//    for (int i=0;i<len;i++) b[i]=a[i];
//    b[len+1] = v;
//    if (max) {
//      heapSort(b);
//    } else {
//      reverseHeapSort(b);
//    }
//    return b;
//  }

//  public void add(int n, Heap that)   {
// // add n to a cluster of a minHeap and a maxHeap (a priority queue)
//    assert getMax() != that.getMax();
//    Heap minHeap = null; Heap maxHeap = null;
//    if (getMax()) {
//      maxHeap = this; minHeap = that;
//    } else {
//      maxHeap = that; minHeap = this;
//    }
//    int e = maxHeap.N+minHeap.N;
//    minHeap.add(n);   
//    if (e % 2 == 0)  {
//      if (maxHeap.N == 0) return;
//      if (minHeap.peek() > maxHeap.peek()) {
//        int maxHeapRoot = maxHeap.poll();
//        int minHeapRoot = minHeap.poll();
//        maxHeap.add(minHeapRoot);
//        minHeap.add(maxHeapRoot);
//      } 
//    } else {
//      maxHeap.add(minHeap.poll());
//    }
//  }
//  
//  public static void add(int n, Heap minHeap, Heap maxHeap) {
//    // add n to a cluster of a minHeap and a maxHeap (a priority queue)
//    assert minHeap.getMax() != maxHeap.getMax();
//    if (minHeap.getMax()) {
//      Heap tmp = maxHeap;
//      maxHeap = minHeap;
//      minHeap = tmp;
//    } 
//    int e = maxHeap.N+minHeap.N;
//    maxHeap.add(n);   
//    if (e % 2 == 0)  {
//      if (minHeap.N == 0) return;
//      if (maxHeap.peek() > minHeap.peek()) {
//        int maxHeapRoot = maxHeap.poll();
//        int minHeapRoot = minHeap.poll();
//        maxHeap.add(minHeapRoot);
//        minHeap.add(maxHeapRoot);
//      } 
//    } else {
//      minHeap.add(maxHeap.poll());
//    }
//  }
//  
//  public static void add(int n, Heap minHeap, Heap maxHeap) {
//    // add n to a cluster of a minHeap and a maxHeap (a priority queue)
//    assert minHeap.getMax() != maxHeap.getMax();
//    if (minHeap.getMax()) {
//      Heap tmp = maxHeap;
//      maxHeap = minHeap;
//      minHeap = tmp;
//    } 
//    int e = maxHeap.N+minHeap.N;
//    maxHeap.add(n);   
//    if (e % 2 == 0)  {
//      if (minHeap.N == 0) return;
//      if (maxHeap.peek() > minHeap.peek()) {
//        int maxHeapRoot = maxHeap.poll();
//        int minHeapRoot = minHeap.poll();
//        maxHeap.add(minHeapRoot);
//        minHeap.add(maxHeapRoot);
//      } 
//    } else {
//      minHeap.add(maxHeap.poll());
//    }
//  }
//
//  public double getMedian(Heap that) {
//    assert getMax() != that.getMax();
//    Heap minHeap = null; Heap maxHeap = null;
//    if (getMax()) {
//      maxHeap = this; minHeap = that;
//    } else {
//      maxHeap = that; minHeap = this;
//    }
//    if (minHeap.getN()+maxHeap.getN() % 2 != 0)
//      return (double) maxHeap.peek();
//    else
//      return (maxHeap.peek() + minHeap.peek()) / 2.0; 
//  }
//
//  public static double getMedian(Heap minHeap, Heap maxHeap) {
//    assert minHeap.getMax() != maxHeap.getMax();
//    if (minHeap.getMax()) {
//      Heap tmp = maxHeap;
//      maxHeap = minHeap;
//      minHeap = tmp;
//    } 
//    if (minHeap.getN()+maxHeap.getN() % 2 != 0)
//      return (double) minHeap.peek();
//    else
//      return (maxHeap.peek() + minHeap.peek()) / 2.0; 
//  }
//  
//  public static void printMedian(Heap minHeap, Heap maxHeap) {
//    System.out.println(getMedian(minHeap, maxHeap));
//  }

  public static void main(String[] args) throws InterruptedException {
    

    
//    int[] a = {5,2,7,3,19,1,5,8,21};
//    minHeapify(a);
//
    //    heapSort(a);
    //    System.out.println();
    //    printArray(a); //[1, 2, 3, 5, 5, 7, 8, 19, 21]
    //    reverseHeapSort(a);
    //    printArray(a); //[21, 19, 8, 7, 5, 5, 3, 2, 1]


    //    Instant start = null; Instant end = null;
    //    
    //    start = Instant.now();
    //    for (int i = 0; i < 10000; i++) swapSort(a);
    //    end = Instant.now();
    //    System.out.println(Duration.between(start, end).toMillis());
    //    //3 3 3 2 2  3 4 3 3 3
    //    
    //    Thread.sleep(2);
    //
    //    start = Instant.now();
    //    for (int i = 0; i < 10000; i++) heapSort(a);
    //    end = Instant.now();
    //    System.out.println(Duration.between(start, end).toMillis());
    //    //7 8 11 8 7  7 8 6 7 8
    // 
    //    Random r = new Random();
    //    a = r.ints(1, 1000).limit(1000).toArray();
    //    
    //    start = Instant.now();
    //    for (int i = 0; i < 1000; i++) heapSort(a);
    //    end = Instant.now();
    //    System.out.println(Duration.between(start, end).toMillis());
    //    //87 85 87   91 88 109
    //   
    //    Thread.sleep(2);
    //    
    //    start = Instant.now();
    //    for (int i = 0; i < 1000; i++) swapSort(a);
    //    end = Instant.now();
    //    System.out.println(Duration.between(start, end).toMillis());
    //    //315 314 315  235 234 247






  }

}
