package u;


import static u.ArrayUtils.*;

import u.Heap;

public class HeapTest {

  public static Heap minHeap = new Heap();
  public static Heap maxHeap = new Heap(); //
  public static int numOfElements = 0;

  public static void add(int n) {
    //    // add n to a cluster of a minHeap and a maxHeap (a priority queue)
    //    assert minHeap.getMax() != maxHeap.getMax();
    //    if (minHeap.getMax()) {
    //      Heap tmp = maxHeap;
    //      maxHeap = minHeap;
    //      minHeap = tmp;
    //    } 
    maxHeap.add(n);   
    if (numOfElements % 2 == 0)  {
      if (minHeap.getN() == 0) {
        numOfElements++;
        return;
      }
      if (maxHeap.peek() > minHeap.peek()) {
        int maxHeapRoot = maxHeap.poll();
        int minHeapRoot = minHeap.poll();
        maxHeap.add(minHeapRoot);
        minHeap.add(maxHeapRoot);
      } 
    } else {
      System.out.println("running  minHeap.add(maxHeap.poll())");
      minHeap.add(maxHeap.poll());
    }
  }

  public static Double getMedian() {
    //  System.out.println("min="+minHeap);
    //  System.out.println("max="+maxHeap);
    minHeap.heapSort(); maxHeap.reverseHeapSort();
    if (numOfElements%2 != 0)
      return new Double(maxHeap.peek());
    return (maxHeap.peek() + minHeap.peek()) / 2.0; 
  }

  public static void main(String[] args) {

    //    maxHeap.add(1);
    //    minHeap.add(maxHeap.poll());
    //    maxHeap.add(5);
    //    minHeap.add(maxHeap.poll());

    add(1);
    add(5);

    System.out.println("minHeap.a=");
    printArray(minHeap.getA());
    System.out.println("maxHeap.a=");
    printArray(maxHeap.getA());
    //    System.out.println(getMedian()); 
    //    add(5);
    //    printArray(minHeap.getA());
    //    printArray(maxHeap.getA());
    add(10);
    add(12);
    add(2);
    System.out.println("minHeap.a=");
    printArray(minHeap.getA());
    System.out.println("maxHeap.a=");
    printArray(maxHeap.getA());  
    System.out.println(getMedian());

  }

}
