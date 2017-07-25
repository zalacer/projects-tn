package ex22;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.append;
import static v.ArrayUtils.drop;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;
import static v.ArrayUtils.take;

import java.util.Arrays;
import java.util.Random;

import ds.Queue;
import sort.BottomUpMerge;

public class Ex2214MergingSortedQueues {

/* p285
  2.2.14 Merging sorted queues. Develop a static method that takes two queues of sorted
  items as arguments and returns a queue that results from merging the queues into
  sorted order.
*/ 

  public static <T extends Comparable<? super T>> Queue<T> mergeQueuesLong(Queue<T> q1, Queue<T> q2) {
    Queue<T> q = new Queue<T>();
    while(!q1.isEmpty()) q.enqueue(q1.dequeue());
    while(!q2.isEmpty()) q.enqueue(q2.dequeue());
    @SuppressWarnings("unchecked")
    T[] a = (T[])newInstance(q.peek().getClass(), q.size());
    int c = 0;
    while(!q.isEmpty()) a[c++] = q.dequeue();
    BottomUpMerge.sort(a);
    for (T t : a) q.enqueue(t);
    return q;
  }
    
  public static <T extends Comparable<? super T>> Queue<T> mergeQueuesShort(Queue<T> q1, Queue<T> q2) {
    T[]a = append(q1.toArray(q1.peek()),q2.toArray(q2.peek()));
    BottomUpMerge.sort(a);
    return new Queue<T>(a);
  }  
  
  public static void main(String[] args) {

    Random r = new Random(System.currentTimeMillis()); 
    Integer[] w, x, y, z; Queue<Integer> q1, q2, q3, q4, q5, q6;
    w = rangeInteger(0,11); // [0,1,2,3,4,5,6,7,8,9,10]
    int n = w.length;
    x = w.clone();
    
    shuffle(x,r);
    q1 = new Queue<Integer>(take(x,n/2)); q2 = new Queue<Integer>(drop(x,n/2)); 
    q3 = mergeQueuesShort(q1,q2);
    y = q3.toArray(q3.peek());
    assert Arrays.equals(y,w);
 
    shuffle(y,r);
    q4 = new Queue<Integer>(take(y,n/2)); q5 = new Queue<Integer>(drop(y,n/2)); 
    q6 = mergeQueuesLong(q4,q5);
    z = q6.toArray(q6.peek());
    assert Arrays.equals(z,w);
  }

}
