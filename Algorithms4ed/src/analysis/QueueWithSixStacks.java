package analysis;

import static v.ArrayUtils.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.Stack;

//  p171
//  1.3.49 Queue with three stacks. Implement a queue with three stacks so that each
//  queue operation takes a constant (worst-case) number of stack operations. Warning :
//  high degree of difficulty.

//  see "solution" by flolo at:
//  https://stackoverflow.com/questions/5538192/how-to-implement-a-queue-with-three-stacks
//  and discussion of it with another "solution" at"
//  http://wizmann.tk/implement-queue-with-stacks.html


//  According to 
//  https://stackoverflow.com/questions/5538192/how-to-implement-a-queue-with-three-stacks
//  this question was withdrawn because Prof. Sedgewick knows of no solutions that don't use
//  lazy evaluation, which in practice creates extra intermediate data structures. It was 
//  replaced with:
//  Queue with a constant number of stacks. Implement a queue with a constant number of
//  stacks so that each queue operation takes a constant (worst-case) number of stack 
//  operations. Warning: Very high degree of difficulty. 
//  (Question 32 on http://algs4.cs.princeton.edu/13stacks/).

//  see lisp implementation at 
//  https://ecommons.cornell.edu/bitstream/handle/1813/6273/80-433.pdf?sequence=1&isAllowed=y
//  lisp stuff:
//  car == head (first element), cdr == tail (all elements except head)
//  cons == creat a cons cell == [car,cdr] == a node in a list
//  cons is short for construct, a cons b constructs a new list with head a and tail b
//  in scala cons == ::
//  queue has 3 ops: query - return first element without removing it
//                delete- return first element and remove it
//                insert- add an element to the end
//  using a list implementation a queue maintain 2 lists: a head list called list1 and a tail
//  list which initially is reverse of list1 and called rlist1
//                                                   lisp               traditional queue
//  this enables: query = return list1.getFirst();    car                peek
//               delete = return list1.remove();     cdr                dequeue
//               insert(item) =  rist1.add(item)     rlist cons item    enqueue
//  
//  list1 can be empty only if entire queue is empty
//  this means that when there is only 1 element on head list delete requires replacing list1 
//  with reverse(rlist1)
//  instead of reversing rlist1 all at once do one reversal step with every operation beginning
//  with the operation triggering reversal when list1.size+1 = rlist1.size
//  logical update process: list2 = A. reverse rlist1 forming tail of list2
//                              B. reverse (new list(list1)) forming rlist2
//                              C. reverse rlist2 onto front of list2 (minus count of deleted)
//  steps 1 and 2 are done at the same time
//  create rlist3 to handle new inserts 
//  create a copy of list1 for step 2, call this list3
//  update triggered when rlist1.size > list1.size
//  do 2 of each step on each pass so that when all done stack1.size+1 > rstack1.size
//  
//  implemented update process using 6 stacks:
//  given stacks f1, f2, f3, r1, r2, r3:
//  1. when s1.size+1 = r1.size in the current enqueue operation do:
//    a. f2.clear() 
//    b. r2.clear()
//    c. f3 = new Stack(f1) and retain f1
//    d. r3.clear()
//    e. rtmp = r1; r1 = r3; r3 = rtmp (interchange rstack1 and rstack3)
//    f. f2.push(r3.pop()); f2.push(r3.pop());
//    g. r2.push(f3.pop()); r2.push(f3.pop());
//    h. int remaining = f3.size. for each delete operation remaining is decremented by one
//       and it's used to define the end of step 3 below.
//  2. during each queue operation until r3 and f3 are empty do:
//    a. f2.push(r3.pop()); f2.push(r3.pop());
//    b. r2.push(f3.pop()); r2.push(f3.pop());
//  3. during each queue operation until remaining==0:
//    a. s2.push(r2.pop); remaining--; s2.push(r2.pop); remaining--;
//  4. when update process is done immediately do:
//    a. s1 = s2;
//  
//  Proof that the update process as outlined above results in stack1.size >= rstack1.size:
//  Suppose initially stack1.size = n and rstack1.size = n+1. Disregarding 1.a-e as one time
//  setup per update process, the operation in 1.e and 2.a is executed n+1 times and that in 1.f
//  and 2.b is executed n times, but they are done in the same step which overall is done n+1
//  times in n+1 queue operations. During this phase there may be a1 additions and d1 deletions
//  such that a1+d1 <= n+1. Then step 3.a is done n times within n queue operations during which 
//  at most a2 addition and d2 deletion operations may occur such that a2+d2 <= n. After the
//  update is done stack1.size = 2n+1-d1-d2 and rstack1.size = a1+a2. Since 2n+1 >= a1+d1+a2+d2, 
//  2n+1-d1-d2 >= a1+a2, proving that after the update stack1.size >= rstack1.size.
//  
//  Worst case cost analysis of queue operations in terms of stack operations
//  given that n is the size of the stack:
//  1. at worst peek takes 9 stack operations (when an update is in progress)
//  2. at worst enqueue takes n+9 stack operations if it initializes an update
//  3. at worst dequeue takes n+9 stack operations if it initializes an update

public class QueueWithSixStacks<Item> implements Iterable<Item> {
  // for this queue enqueueing is done to rear and dequeueing is from  front
  // when peek or dequeue is called and front is empty all in rear are moved 
  // to front. each queue operation takes a constant amortized number of stack 
  // operations
  
  private Stack<Item> front;
  private Stack<Item> rear;

//  private Stack<Item> f1; // f is for front
//  private Stack<Item> f2;
//  private Stack<Item> f3;
//  private Stack<Item> r1; // is is for rear and reverse
//  private Stack<Item> r2;
//  private Stack<Item> r3;

  public QueueWithSixStacks() {
    front = new Stack<Item>();
    rear = new Stack<Item>();
//    f1 = new Stack<Item>();
//    f2 = new Stack<Item>();
//    f3 = new Stack<Item>();
//    r1 = new Stack<Item>();
//    r2 = new Stack<Item>();
//    r3 = new Stack<Item>();
  }
  
  @SafeVarargs
  public QueueWithSixStacks(Item...items) {
    front = new Stack<Item>();
    if (items != null && items.length > 0)
      for (int i = items.length-1; i > -1; i--) front.push(items[i]);  
    rear = new Stack<Item>();
//    f1 = new Stack<Item>();
//    f2 = new Stack<Item>();
//    f3 = new Stack<Item>();
//    r1 = new Stack<Item>();
//    r2 = new Stack<Item>();
//    r3 = new Stack<Item>();
  }
  
  private void moveToFront() {
    // relocate all items in rear to front which reverses
    // their order a 2nd time so they come out FIFO
    while (!rear.isEmpty()) front.push(rear.pop());
  }

  public boolean isEmpty() {
    // return true only if rear and front are empty else return false
    return rear.isEmpty() && front.isEmpty();
  }

  public int size() {
    // return the total number of items in rear and front
    return rear.size() + front.size();     
  }

  public Item peek() {
    // return the head of front without popping it and after
    // relocating all items from rear to front if it's empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    if (front.isEmpty()) moveToFront();
    return front.peek();
  }

  public void enqueue(Item item) {
    //push item to the top of rear
    rear.push(item);
  }

  public Item dequeue() {
    // remove and return the head of front after relocating
    // all items from rear to front if it's empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    if (front.isEmpty()) moveToFront();
    return front.pop();
  }
  
  public Iterator<Item> iterator() {
    return new ArrayIterator();
  }
  
  private class ArrayIterator implements Iterator<Item> {
    private Object[] a = toArray();
    private int i = 0;
    private int n = a.length;
    public boolean hasNext() { return i < n; }
    @SuppressWarnings("unchecked")
    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      return (Item) a[i++]; 
    }
  }
    
  public Object[] toArray() {
    return append(front.toArray(), reverse(rear.toArray()));
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return append(front.toArray(items), reverse(rear.toArray(items)));
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((front == null) ? 0 : front.hashCode());
    result = prime * result + ((rear == null) ? 0 : rear.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    QueueWithSixStacks other = (QueueWithSixStacks) obj;
    if (front == null) {
      if (other.front != null)
        return false;
    } else if (!front.equals(other.front))
      return false;
    if (rear == null) {
      if (other.rear != null)
        return false;
    } else if (!rear.equals(other.rear))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("QueueWithTwoStacks(");
    Object[] a = toArray();
    if (a.length==0) return sb.append(")").toString();
    for (Object i : a) sb.append(i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {

  }
}

