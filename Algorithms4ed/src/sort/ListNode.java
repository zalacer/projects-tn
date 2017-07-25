package sort;

// http://penguin.ewu.edu/~trolfe/NaturalMerge/ListNode.java

@SuppressWarnings("rawtypes")
public class ListNode implements Comparable
{
  static int count = 0;
  int  data;
  ListNode next;

  public ListNode ( int data )
  {
    this.data = data;
    this.next = null;
  }

  public ListNode next()
  {  return this.next;  }

  public int get()
  {  return data;  }

  public void insertBefore ( ListNode node )
  {
    // Swap contents of data fields to interchange identities
    int hold = this.data;
    this.data = node.data;
    node.data = hold;
    // Now do the list insertion
    node.next = this.next;
    this.next = node;
  }

  public void insertAfter ( ListNode node )
  {
    node.next = this.next;
    this.next = node;
  }

  // Since we are INSIDE the ListNode class, we know the basis for
  // comparison, so we won't bother using the compareTo method.
  static public ListNode merge ( ListNode left, ListNode right )
  {
    ListNode head, current, tail;

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

      current.next = null;     // Not strictly needed.
      tail = tail.next = current;
    }

    tail.next = ( left != null ) ? left : right;

    return head;
  }

  static public void traverse ( ListNode current )
  {
    while ( current != null )
    {
      System.out.print ( " " + current.get());
      current = current.next;
    }
    System.out.println (" --- finished");
  }

  // Since the data field is itself an int, just return the difference
  public int compareTo ( Object r )
  {
    ListNode right = (ListNode) r;

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
    // TODO Auto-generated method stub

  }

}
