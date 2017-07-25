package ds;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DequeWithThreeStacks<Item> implements Iterable<Item> {
  // For visualization suppose a DequeWithThreeStacks consists of a stack on the left
  // in the middle and on the right. Then its items are ordered as those in the stack on
  // the left (from top to bottom) and the reverse of that on the right (from bottom to
  // top) while the one in the middle acts as a transient buffer for balancing the number
  // of elements between the left and the right during popLeft and popRight and is
  // effectively empty which could be enfored with synchronize.
  //
  // PushLeft is implemented as stack1.push and pushRight is implemented as stack3.push.
  //
  // PopLeft does a stack1.pop iff stack1 isn't empty otherwise it does a balancing
  // maneuver in which its bottom half except for the last item is popped and pushed to 
  // stack1 and then its last item is popped and returned. 
  
  // PopRight similarly does a stack3.pop iff stack3 isn't empty otherwise it does a
  // balancing maneuver in which its bottom half except for the last item is popped and 
  // pushed to stack3 and then its last item is popped and returned. 
  //
  // When popLeft or popRight does a balancing maneuver it takes ~1.5N stack operations 
  // where N is DequeWithThreeStacks.size(), but this is amortizable over N successive 
  // DequeWithThreeStacks output operations that then take just one stack operation each 
  // so overall the cost is ~2.5 stack operations per DequeWithThreeStacks output 
  // operation. For each successful output operation there must have been an input 
  // operation each of which correlates to one DequeWithThreeStacks (input) operation. 
  // This results in a combined input and output cost of ~1.75 stack operations per 
  // DequeWithThreeStacks operation. This could be reduced if balancing maneuvers are 
  // omitted by transferring all of stack3 to stack1 during popLeft when stack1 is empty
  // and transferring all of stack1 to stack3 during popRight when stack3 is empty. Then 
  // the cost of popLeft and popRight are both just ~N stack operations, wich is again 
  // amortizable over N successive DequeWithThreeStacks output operations that then take 
  // just one stack operation each so overall the cost if ~2 stack operations per 
  // DequeWithThreeStacks output operation. Together with input operations this results 
  // in a combined input and output cost of ~1.5 stackoperations per DequeWithThreeStacks 
  // operation.
  
  // DequeWithThreeStacks queue and stack modes are demonstrated in 
  // Ex1431DequeWithThreeStacks.java.
  
  private Stack<Item> stack1;
  private Stack<Item> stack2;
  private Stack<Item> stack3;

  public DequeWithThreeStacks() {
    stack1 = new Stack<Item>();
    stack2 = new Stack<Item>();
    stack3 = new Stack<Item>();
  }

  @SafeVarargs
  public DequeWithThreeStacks(Item...items) {
    Item[][] a = splitAt(items, items.length/2);
    stack1 = new Stack<Item>(reverse(a[0]));
    stack2 = new Stack<Item>();
    stack3 = new Stack<Item>(a[1]);
  }

  public boolean isEmpty() { 
    return stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty(); 
  }

  public int size() { 
    return stack1.size() + stack2.size()+ stack3.size(); }

  public void pushLeft(Item item) { 
    stack1.push(item);
  }

  public Item popLeft() { 
    // remove and return an item from the left end, i.e. the first item.
    // rebalance the allocation of items between stack1 and stack2 if appropriate
    if (isEmpty()) throw new NoSuchElementException("DequeWithThreeStacks underflow");
    if (!stack1.isEmpty()) {
      return stack1.pop();
    } else {
      int s = stack3.size(); 
      if (s == 1) return stack3.pop();
      if (s - 1 > 1) { // balance stack1 and stack3 (excuse to use stack2)
        for (int i = 0; i < (s - 1)/2; i++) stack2.push(stack3.pop());
        s = stack3.size();
        for (int i = 0; i < s - 1; i++) stack1.push(stack3.pop());
        Item first = stack3.pop();
        s = stack2.size();
        for (int i = 0; i < s; i++) stack3.push(stack2.pop());
        return first;
      } else {
        for (int i = 0; i < s - 1; i++) stack1.push(stack3.pop());
        return stack3.pop();
      }
    }
  }

  public void pushRight(Item item) { 
    // add item to the right end, i.e. make it the last item
    stack3.push(item); 
  }

  public Item popRight() { 
    // remove and return an item from the right end, i.e. the last item
    // rebalance the allocation of items between stack1 and stack2 if appropriate
    if (isEmpty()) throw new NoSuchElementException("DequeWithThreeStacks underflow");
    if (!stack3.isEmpty()) {
      return stack3.pop();
    } else {
      int s = stack1.size();
      if (s == 1) return stack1.pop();
      if (s - 1 > 1) { // balance stack1 and stack3 (excuse to use stack2)
        for (int i = 0; i < (s - 1)/2; i++) stack2.push(stack1.pop());
        s = stack1.size();
        for (int i = 0; i < s - 1; i++) stack3.push(stack1.pop());
        Item first = stack1.pop();
        s = stack2.size();
        for (int i = 0; i < s; i++) stack1.push(stack2.pop());
        return first;
      } else {
        for (int i = 0; i < s - 1; i++) stack3.push(stack1.pop());
        return stack1.pop();
      }  
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
    return append(stack1.toArray(), reverse(stack3.toArray()));
  }

  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return append(stack1.toArray(items), reverse(stack3.toArray(items)));
  }

  public Object[] stack1ToArray() {
    return stack1.toArray();
  }

  @SafeVarargs
  public final Item[] stack1ToArray(Item...items) {
    return stack1.toArray(items);
  }

  public Object[] stack2ToArray() {
    return stack2.toArray();
  }

  @SafeVarargs
  public final Item[] stack2ToArray(Item...items) {
    return stack2.toArray(items);
  }
  
  public Object[] stack3ToArray() {
    return stack3.toArray();
  }

  @SafeVarargs
  public final Item[] stack3ToArray(Item...items) {
    return stack3.toArray(items);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("DequeWithThreeStacks(");
    Object[] a = toArray();
    if (a.length==0) return sb.append(")").toString();
    for (Object i : a) sb.append(i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {



  }

}

