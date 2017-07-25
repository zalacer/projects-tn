package sort;

import static v.ArrayUtils.*;
// http://penguin.ewu.edu/~trolfe/NaturalMerge/ListNode.java

@SuppressWarnings({"unused", "rawtypes"})
public class Lode implements Comparable
{
  static int count = 0;
  int  data;
  Lode next;

  public Lode ( int data )
  {
    this.data = data;
    this.next = null;
  }

  public Lode next()
  {  return this.next;  }

  public int get()
  {  return data;  }

  public void insertBefore ( Lode node )
  {
    // Swap contents of data fields to interchange identities
    int hold = this.data;
    this.data = node.data;
    node.data = hold;
    // Now do the list insertion
    node.next = this.next;
    this.next = node;
  }

  public void insertAfter ( Lode node )
  {
    node.next = this.next;
    this.next = node;
  }

  // Since we are INSIDE the ListNode class, we know the basis for
  // comparison, so we won't bother using the compareTo method.
  static public Lode merge ( Lode left, Lode right )
  {
    Lode head, current, tail;

    if ( left == null )
      return right;
    if ( right == null )
      return left;

    if ( right.data > left.data )
    {  head = tail = left;  left = left.next;  }
    else
    {  head = tail = right;  right = right.next;  }

    while ( left != null && right != null )
    {
      if ( right.data > left.data )
      {  current = left;  left = left.next;  }
      else
      {  current = right; right = right.next;  }
//      traverse(current);
      current.next = null;     // Not strictly needed.
      traverse(tail);
      tail = tail.next = current;
    }

    tail.next = ( left != null ) ? left : right;

    return head;
  }

  static public void traverse ( Lode current )
  {
    while ( current != null )
    {
      System.out.print ( " " + current.get());
      current = current.next;
    }
    System.out.println ();
  }

  // Since the data field is itself an int, just return the difference
  public int compareTo ( Object r )
  {
    Lode right = (Lode) r;

    count++;
    return this.data - right.data;
  }

  static public int nCompares()
  {
    int rtn = count;

    count = 0;
    return rtn;
  }

  public String toString()
  {  return Integer.toString(this.data);  }

  public static void main(String[] args) {
    Lode n1 = new Lode(1);  Lode n2 = new Lode(2);  Lode n3 = new Lode(3);
    Lode n4 = new Lode(4);  Lode n5 = new Lode(5);  Lode n6 = new Lode(6);
    n1.next = n2; n2.next = n3; n4.next = n5; n5.next = n6;
//    traverse(n1); traverse(n4);
    Lode m = merge(n4,n1);
//    traverse(m);
  }

}
