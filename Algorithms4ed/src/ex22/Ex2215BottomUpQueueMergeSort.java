package ex22;

import static v.ArrayUtils.append;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Arrays;
import java.util.Random;

import ds.Queue;
import sort.BottomUpMerge;

public class Ex2215BottomUpQueueMergeSort {

/* p285
  2.2.15 Bottom-up queue mergesort.Develop a bottom-up mergesort implementation
  based on the following approach: Given N items, create N queues, each containing one
  of the items. Create a queue of the N queues. Then repeatedly apply the merging opera-
  tion of Exercise 2.2.14 to the first two queues and reinsert the merged queue at the end.
  Repeat until the queue of queues contains only one queue.
*/ 
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> T[] bottomUpQueueMergeSort(T...t) {
    Queue<Queue<T>> q = new Queue<Queue<T>>();  
    for (T e : t) q.enqueue(new Queue<T>(e));
    while (q.size() > 1) q.enqueue(mergeQueuesShort(q.dequeue(), q.dequeue()));
    return q.peek().toArray(t);  
  }
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> Queue<T> bottomUpQueueMergeSort2(T...t) {
    Queue<Queue<T>> q = new Queue<Queue<T>>();  
    for (T e : t) q.enqueue(new Queue<T>(e));
    while (q.size() > 1) q.enqueue(mergeQueuesShort(q.dequeue(), q.dequeue()));
    return q.dequeue();  
  }
    
  public static <T extends Comparable<? super T>> Queue<T> mergeQueuesShort(Queue<T> q1, Queue<T> q2) {
    T[]a = append(q1.toArray(q1.peek()),q2.toArray(q2.peek()));
    BottomUpMerge.sort(a);
    return new Queue<T>(a);
  }  
  
  public static void main(String[] args) {

    Random r = new Random(System.currentTimeMillis()); 
    Integer[] w, x, y, z; 
    w = rangeInteger(0,11); // [0,1,2,3,4,5,6,7,8,9,10]
    x = w.clone();
    
    shuffle(x,r);
    y = bottomUpQueueMergeSort(x);
    assert Arrays.equals(y,w);
    
    shuffle(y,r);
    z = bottomUpQueueMergeSort2(y).toArray(y[0]);
    assert Arrays.equals(z,w);

  }

}
