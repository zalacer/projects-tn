package qw7s;

import static qw7s.Utils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

//QueueWithSevenStacks is based on the description of a six list queue  lisp implementation 
//by Hood and Melville at 
//https://ecommons.cornell.edu/bitstream/handle/1813/6273/80-433.pdf?sequence=1&isAllowed=y.
//
//The goal of QueueWithSevenStacks is to achieve constant worst case performance
//of queue operations in terms of stack operations. This is easily done for isEmpty()
//and size(), at least for linked list implementations. an issue arises, however,
//particularly for the dequeue operation when the front stack is empty in a dual stack
//implementation, because that requires transferring the reversed contents of the rear 
//stack to the front taking amortized O(1) stack operations but possibly 0(N) for a 
//single qeueue operation. The problem then is how to distribute this transferral 
//or update process relatively evenly over a number of dequeue and enqueue operations
//so they individually take a constant worst case number of stack operations independent
//of the size of the queue. This can be useful in real time settings because it enables 
//better performance predictability  by enforcing an O(1) upper bound on the time 
//required for a queue operation, which may be desirable even though that bound could be 
//higher than the amortized operation cost of other queue implementations.
//a way of implementing such an update process that solves this problem is as follows:
//Given stacks s1, s2, s3, s4, r1, r2, r3 where s1 is the front stack, r1 is the rear
//stack and the remaining stacks are used to transfer the items in r1 to s1 when 
//s1.size()+1 = r1.size() over a consecutive sequence of 2N+1 steps, where N is s1.size() 
//just prior to beginning the transfer process and s3 is maintained as a copy of s1 
//before and after but not during transfer:
//1. When s1.size()+1 = r1().size the current enqueue or dequeue operation initializes
// an update process by calling updateinit() which does:
// a. s2.clear()  
// b. s4.clear()
// c. r2.clear()
// d. r3.clear()
// e. freeze s3 (disable synchronization of it with s1 by setting s3sync = false)
// f. set remaining = s1.size(). for each delete operation remaining is decremented by 
//    one and it's used to define the end of step 3 below.
// g. rtmp = r1; r1 = r3; r3 = rtmp (interchange r1 and r3)
// h. if r3 isn't empty run s4.push(r3.peek()); s2.push(r3.pop()); twice.
// i.  if s3 isn't empty run r2.push(s3.pop()); twice
// j: set various boolean control variable such as updateinit, update, update1...
//2. After update initialization, during each enqueue and dequeue operation until r3 and
// s3 are empty if update1 do:
// a. s4.push(r3.peek()); s2.push(r3.pop()); (done twice)
//    r2.push(s3.pop()); (done twice)
//3. After the update1 phase has completed, during each enqueue and dequeue operation if 
// update2 and until remaining==0 or r2.isEmpty() do:
// a. s4.push(r2.peek()); s2.push(r2.pop()); remaining--; (done twice)
//4. In the update2 phase, when remaining == 0 do:
// a. set update = false; update1 = false; update2 = false;
// b. s1 = s2; s2 = Stack.getStack();
// c. s3 = s4; s4 = Stack.getStack();
// d. s3sync = true;
// e. r2.clear();
//
//Proof that the update process as outlined above results in stack1.size >= rstack1.size:
//Suppose initially s1.size = n and r1.size = n+1. Disregarding 1.a-g as one time
//setup per update process, the operation in 1.h and 2.a is executed n+1 times and that in 1.i
//and 2.b is executed n times, but they are done in the same step which overall is done n+1
//times in n+1 queue operations. During this phase there may be a1 additions and d1 deletions
//such that a1+d1 <= n+1. Then step 3.a is done n times within n queue operations during which 
//at most a2 addition and d2 deletion operations may occur such that a2+d2 <= n. After the
//update is done stack1.size = 2n+1-d1-d2 and rstack1.size = a1+a2. Since 2n+1 >= a1+d1+a2+d2, 
//2n+1-d1-d2 >= a1+a2, proving that after the update stack1.size >= rstack1.size.

//QueueWithSevenStacks method implementation analysis in terms of Stack operations:
//---------------------------------------------------------------------------------
//By inspection of the code of its methods below:
//1. isEmpty() takes at most two Stack operations.
//2. size() takes two Stack operations.
//3. clear() takes seven Stack operations.
//3. peek() takes one Stack operation.
//4. enqueue takes at most 25 Stack operations (6 push, 4 pop, 2 peek, 6 isEmpty, 4 clear, 3 size)  
//5. dequeue takes at most 25 Stack operations. (6 push, 4 pop, 2 peek, 6 isEmpty, 4 clear, 3 size)

public class QueueWithSevenStacks<Item> implements Iterable<Item> {
  // for this queue enqueueing is done to a rear stack and dequeueing is from the 
  // front stack, since whenever the queue isn't empty there is always an item in 
  // the front due to an update process that transfers the contents of the rear to
  // the front, whenever it becomes longer than the front, with constant worst case 
  // number of stack operations for each dequeue and enqueue operation.  
  private Stack<Item> s1; // front of queue
  private Stack<Item> s2; // built to be the new s1 during update
  private Stack<Item> s3; // synced with s1 except during update when used to build a new s1
  private Stack<Item> s4; // used to build a new s3 during update
  private Stack<Item> r1; // back of queue
  private Stack<Item> r2; // used to reverse s3 during update
  private Stack<Item> r3; // swapped with r1 during update
  private transient Stack<Item> rtmp = null; // for swapping r1 and r3 in updateinit()
  private boolean s3sync = true; // used to disable s1-s3 sync during update
  private boolean updateinit = false; // when true updateinit() is active
  private boolean update = false; // when true update() is active
  private boolean update1 = false; // when true update() is active and in update1 phase
  private boolean update2 = false; // when true update() is active and in update2 phase
  private int remaining = 0; // for dequeue accounting during update
  private transient Item itmp = null; // temporary Item reference
 
  public QueueWithSevenStacks() {
    s1 = new Stack<Item>();
    s2 = new Stack<Item>();
    s3 = new Stack<Item>();
    s4 = new Stack<Item>();
    r1 = new Stack<Item>();
    r2 = new Stack<Item>();
    r3 = new Stack<Item>();
  }
  
  @SafeVarargs
  public QueueWithSevenStacks(Item...items) {
    s1 = new Stack<Item>();
    s2 = new Stack<Item>();
    s3 = new Stack<Item>();
    s4 = new Stack<Item>();
    r1 = new Stack<Item>();
    r2 = new Stack<Item>();
    r3 = new Stack<Item>();
    if (items != null && items.length > 0)
      for (int i = items.length-1; i > -1; i--) {
        s1.push(items[i]); s3.push(items[i]);
      }
  }
  
  private void updateinit() {
    // this is documented in the notes above
    if (update || r1.size() <= s1.size()) return;
    updateinit = true;
    s2.clear(); 
    s4.clear();
    r2.clear();
    r3.clear();
    s3sync = false;
    remaining = s1.size();
    rtmp = r1; r1 = r3; r3 = rtmp;
    if (!r3.isEmpty()) { s4.push(r3.peek()); s2.push(r3.pop()); }
    if (!r3.isEmpty()) { s4.push(r3.peek()); s2.push(r3.pop()); }
    if (!s3.isEmpty()) r2.push(s3.pop());
    if (!s3.isEmpty()) r2.push(s3.pop());
    if (r3.isEmpty() && s3.isEmpty()) {
      update1 = false; update2 = true;
    } else {
      update1 = true; update2 = false;
    }
    update = true;
    updateinit = false;
  }
  
  private void update() {
    // this is documented in the notes above
    if (update1) {
      if (!r3.isEmpty()) { s4.push(r3.peek()); s2.push(r3.pop()); }
      if (!r3.isEmpty()) { s4.push(r3.peek()); s2.push(r3.pop()); }
      if (!s3.isEmpty()) r2.push(s3.pop());
      if (!s3.isEmpty()) r2.push(s3.pop());
      if (r3.isEmpty() && s3.isEmpty()) {
        update1 = false; update2 = true;
      }
    } else if (update2) {
      if (remaining > 0 && !r2.isEmpty()) {
        s4.push(r2.peek()); s2.push(r2.pop()); remaining--; 
      }
      if (remaining > 0 && !r2.isEmpty()) {
        s4.push(r2.peek()); s2.push(r2.pop()); remaining--; 
      }
      if (remaining == 0) {
        update = false; update1 = false; update2 = false;
        s1 = s2; s2 = Stack.getStack();
        s3 = s4; s4 = Stack.getStack();
        s3sync = true;
        r2.clear();
      }
    }
  }
  
  public int updateLevel() {
    // for debugging
    if (update) {
      if (updateinit) {
        return 0;
      } else if (update1) {
        return 1;
      } else if (update2) {
        return 2;
      } else return -2;
    } else return -1;
  }

  public boolean isEmpty() {
    // return true only if r1 and s1 are empty else return false
    return r1.isEmpty() && s1.isEmpty();
  }

  public int size() {
    // return the total number of items in r1 and s1
    if (update) update();
    return r1.size() + s1.size();     
  }
  
  public void clear() {
    s1.clear();
    s2.clear();
    s3.clear();
    s4.clear();
    r1.clear();
    r2.clear();
    r3.clear();
    rtmp = null;
    s3sync = true;
    updateinit = false;
    update = false;
    update1 = false;
    update2 = false;
    remaining = 0;
    itmp = null;
  }

  public Item peek() {
    // return the head of s1 without popping it
    // in this queue design s1 is never empty unless the queue is empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    return s1.peek();
  }

  public void enqueue(Item item) {
    // push item to the top of r1
    r1.push(item);
//    System.out.println("s1.size="+s1.size());
//    System.out.println("r1.size="+r1.size());
    if (r1.size() > s1.size() && !update) {
//      System.out.println("updating");
      updateinit();
    } else if (update) update();
  }

  public Item dequeue() {
    // remove and return the head of s1, i.e. pop s1 
    // with this queue design s1 is never empty unless the queue is empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
//    if (front.isEmpty()) moveToFront();
    itmp = s1.pop(); 
    if (s3sync) s3.pop();
    if (r1.size() > s1.size() && !update) {
      updateinit();
    } else if (update) {
      remaining--;
      update();
    }
    return itmp;
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
  
  public Object[] s1a() {
    // for debugging
    if (s1 == null) return null;
    return s1.toArray();
  }
  
  public Object[] s2a() {
    // for debugging
    if (s2 == null) return null;
    return s2.toArray();
  }
  
  public Object[] s3a() {
    // for debugging
    if (s3 == null) return null;
    return s3.toArray();
  }
  
  public Object[] s4a() {
    // for debugging
    if (s4 == null) return null;
    return s4.toArray();
  }  
  
  public Object[] r1a() {
    // for debugging
    if (r1 == null) return null;
    return r1.toArray();
  }
  
  public Object[] r2a() {
    // for debugging
    if (r2 == null) return null;
    return r2.toArray();
  }
  
  public Object[] r3a() {
    // for debugging
    if (r3 == null) return null;
    return r3.toArray();
  }
  
  public Object[] toArray() {
    return append(s1.toArray(), reverse(r1.toArray()));
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return append(s1.toArray(items), reverse(r1.toArray(items)));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((r1 == null) ? 0 : r1.hashCode());
    result = prime * result + ((r2 == null) ? 0 : r2.hashCode());
    result = prime * result + ((r3 == null) ? 0 : r3.hashCode());
    result = prime * result + remaining;
    result = prime * result + ((s1 == null) ? 0 : s1.hashCode());
    result = prime * result + ((s2 == null) ? 0 : s2.hashCode());
    result = prime * result + ((s3 == null) ? 0 : s3.hashCode());
    result = prime * result + (s3sync ? 1231 : 1237);
    result = prime * result + ((s4 == null) ? 0 : s4.hashCode());
    result = prime * result + (update ? 1231 : 1237);
    result = prime * result + (update1 ? 1231 : 1237);
    result = prime * result + (update2 ? 1231 : 1237);
    result = prime * result + (updateinit ? 1231 : 1237);
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
    QueueWithSevenStacks other = (QueueWithSevenStacks) obj;
    if (r1 == null) {
      if (other.r1 != null)
        return false;
    } else if (!r1.equals(other.r1))
      return false;
    if (r2 == null) {
      if (other.r2 != null)
        return false;
    } else if (!r2.equals(other.r2))
      return false;
    if (r3 == null) {
      if (other.r3 != null)
        return false;
    } else if (!r3.equals(other.r3))
      return false;
    if (remaining != other.remaining)
      return false;
    if (s1 == null) {
      if (other.s1 != null)
        return false;
    } else if (!s1.equals(other.s1))
      return false;
    if (s2 == null) {
      if (other.s2 != null)
        return false;
    } else if (!s2.equals(other.s2))
      return false;
    if (s3 == null) {
      if (other.s3 != null)
        return false;
    } else if (!s3.equals(other.s3))
      return false;
    if (s3sync != other.s3sync)
      return false;
    if (s4 == null) {
      if (other.s4 != null)
        return false;
    } else if (!s4.equals(other.s4))
      return false;
    if (update != other.update)
      return false;
    if (update1 != other.update1)
      return false;
    if (update2 != other.update2)
      return false;
    if (updateinit != other.updateinit)
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("QueueWithSevenStacks(");
    Object[] a = toArray();
    if (a.length==0) return sb.append(")").toString();
    for (Object i : a) sb.append(i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    
    QueueWithSevenStacks<Integer> q = new QueueWithSevenStacks<Integer>(1,2,3,4,5,6,7);
    for (int i = 8; i < 15; i++) q.enqueue(i);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.enqueue(15);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.dequeue();
    q.enqueue(16);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.dequeue();
    q.enqueue(17);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.dequeue(); q.dequeue();
    q.enqueue(18);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.dequeue(); q.dequeue();
    q.enqueue(19);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.dequeue();
    q.enqueue(20);
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    q.clear();
    System.out.println(q);
    pa(q.s1a());
    pa(q.s2a());
    pa(q.s3a());
    pa(q.s4a());
    pa(q.r1a());
    pa(q.r2a());
    pa(q.r3a());
    System.out.println(q.updateLevel());
    
//    q = new QueueWithSevenStacks<Integer>();
//    System.out.println(q);
//    q.enqueue(1); //  q.enqueue(3);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    q.enqueue(2);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    q.enqueue(3);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    
//    q =  new QueueWithSevenStacks<Integer>(1,2,3,4,5);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    q.enqueue(6); q.enqueue(7);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    q.enqueue(8);q.enqueue(9);q.enqueue(10);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    q.enqueue(11);
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
//    System.out.println(q);
//    pa(q.s1a());
//    pa(q.s2a());
//    pa(q.s3a());
//    pa(q.s4a());
//    pa(q.r1a());
//    pa(q.r2a());
//    pa(q.r3a());
//    System.out.println(q.updateLevel());
  }
}

