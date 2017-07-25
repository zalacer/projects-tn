package ds;

import static v.ArrayUtils.append;
import static v.ArrayUtils.reverse;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DequeWithStackAndSteque<Item> implements Iterable<Item> {
  // For visualization suppose a DequeWithStackAndSteque consists of a steque on the left
  // and a stack on the right. Then its items are ordered as those in the steque followed
  // by those in the stack reversed. The preference for pushLeft is steque.push, but if the 
  // stack isn't empty then it's necessary to pop each item in the stack and push it to the 
  // steque to reverse the order and then push the new item to the top of the steque. The 
  // preference for popLeft is steque.pop, but if the stack isn't empty it's necessary to
  // to pop its items and push them into the steque, except for the last which is returned. 
  // The preference for popRight is stack.pop, but that's possible only if the stack isn't 
  // empty. If it's empty, then the last item is at the bottom of the steque. To get it it's 
  // necessary to pop all items except the last from the steque pushing them to the stack, 
  // then pop and return the last. PushRight is implemented just with steque.enqueue which 
  // is possible due to the transfers of items that may be done by all the other operations.
  // 
  // Queue operation with pushRight and popLeft:
  // -------------------------------------------
  // A DequeWithStackAndSteque can be operated as a queue using pushRight to enqueue and 
  // popLeft to dequeue. If it's initially empty, enqueueing each item with pushRight takes
  // one steque.enqueue operation and dequeing each item with popLeft takes one steque.pop 
  // operation. The overall cost is one steque operation for one DequeWithStackAndSteque 
  // operation.

  // Queue operation with pushLeft and popRight:
  // -------------------------------------------
  // A DequeWithStackAndSteque can be operated as a queue using pushLeft to enqueue and
  // popRight to dequeue. if it's initially empty, enqueuing N item's with pushLeft takes one 
  // steque.push operation each. The initial dequeing operation with popRight requires transfer
  // of all N items in the steque to the stack requiring N steque.pop and N-1 stack.push 
  // operation followed by returning the final steque.pop. After this the remaining N-1 items 
  // in the stack can be dequeued with popRight using one stack.pop each. The overall cost is 
  // (4N-2)/2N ~ 2 steque and stack operations per DequeWithStackAndSteque operation.

  // Stack operation with pushLeft and popLeft:
  // -------------------------------------------
  // A DequeWithStackAndSteque can be operated as a stack using pushLeft to push and popLeft
  // to pop. If it's initially empty, pushing each item with pushLeft takes one steque push 
  // operation. Then each can be popped with popLeft using one steque.pop operation. The
  //  overall cost is one steque operation for one DequeWithStackAndSteque operation.

  // Stack operation with pushRight and popRight:
  // -------------------------------------------
  // A DequeWithStackAndSteque can be operated as a stack using pushRight to push and popRight
  // to pop. If it's initially empty, pushing N items with pushRight takes N steque.enqueue 
  // operations. Since all items are now in the steque, the first popRight first requires 
  // transferral of N-1 of them to the stack taking N-1 steque.pop and N-1 stack.push
  //  operations so the last item can be popped with steque.pop and returned. Then the N-1 
  // items now in the stack be popped with stack.pop. The overall cost is (4N-2)/2N ~ 2 steque 
  // and stack operations per DequeWithStackAndSteque operation.

  // Overall amortized cost of a DequeWithStackAndSteque operation:
  // --------------------------------------------------------------
  // Weighting the four high level operating modes described above equally, averaging their
  // costs results in ~ 1.5 steque and stack operations per DequeWithStackAndSteque operation. 
  // Depending on actual use and assuming it's end-to-end, this cost ranges from 1 to 2, while 
  // a given DequeWithStackAndSteque operation may incur at most a cost proportional to the 
  // number of items in the steque or the stack in terms of their push and pop operations.

  private Stack<Item> stack;
  private Steque<Item> steque;

  public DequeWithStackAndSteque() {
    stack = new Stack<Item>();
    steque = new  Steque<Item>();
  }

  @SafeVarargs
  public DequeWithStackAndSteque(Item...items) {
    stack = new Stack<Item>(items);
    steque = new  Steque<Item>();
  }

  public boolean isEmpty() { return stack.isEmpty() && steque.isEmpty(); }

  public int size() { return stack.size() + steque.size(); }

  public void pushLeft(Item item) { 
    // add item to the left end, i.e. the first item.
    if (stack.isEmpty()) {
      steque.push(item);
    } else {
      int s = stack.size();
      for (int i = 0; i < s; i++) steque.push(stack.pop());
      steque.push(item);
    }
  }

  public Item popLeft() { 
    // remove and return an item from the left end, i.e. the first item.
    if (isEmpty()) throw new NoSuchElementException("DequeWithStackAndSteque underflow");
    if(stack.isEmpty()) {
      return steque.pop();
    } else {
      int s = stack.size();
      for (int i = 0; i < s - 1; i++) steque.push(stack.pop());
      return stack.pop();
    }
  }

  public void pushRight(Item item) { 
    // add item to the right end, i.e. make it the last item
    steque.enqueue(item);
  }

  public Item popRight() { 
    // remove and return an item from the right end, i.e. the last item
    if (isEmpty()) throw new NoSuchElementException("DequeWithStackAndSteque underflow");
    if(!stack.isEmpty()) {
      return stack.pop();
    } else {
      int s = steque.size();
      for (int i = 0; i < s - 1; i++) stack.push(steque.pop());
      return steque.pop();
    }
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
    return append(steque.toArray(), reverse(stack.toArray()));
  }

  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return append(steque.toArray(items), reverse(stack.toArray(items)));
  }

  public Object[] stackToArray() {
    return stack.toArray();
  }

  @SafeVarargs
  public final Item[] stackToArray(Item...items) {
    return stack.toArray(items);
  }

  public Object[] stequeToArray() {
    return steque.toArray();
  }

  @SafeVarargs
  public final Item[] stequeToArray(Item...items) {
    return steque.toArray(items);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((stack == null) ? 0 : stack.hashCode());
    result = prime * result + ((steque == null) ? 0 : steque.hashCode());
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
    DequeWithStackAndSteque other = (DequeWithStackAndSteque) obj;
    if (stack == null) {
      if (other.stack != null)
        return false;
    } else if (!stack.equals(other.stack))
      return false;
    if (steque == null) {
      if (other.steque != null)
        return false;
    } else if (!steque.equals(other.steque))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("DequeWithStackAndSteque(");
    Object[] a = toArray();
    if (a.length==0) return sb.append(")").toString();
    for (Object i : a) sb.append(i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {



  }

}

