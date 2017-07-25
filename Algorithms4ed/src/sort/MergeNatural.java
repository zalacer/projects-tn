package sort;

public class MergeNatural {
  
  ListNode head = null,
  tail = null;
  int size = 0;
  
  public void mergeNatural() {
    ListNode[] work;
    int j,       // Destination index at the front
    k,       // Source index advanced by twos
    lim;     // Current logical size

    if ( size < 2 ) return;

    // Allow space for the appended null reference at the end.
    work = new ListNode[size+1];

    // (A) Generate the initial array of lists.
    for ( lim = 0; head != null; lim++ )
    {
      ListNode parent = head,  // Find the control break
          child;
      work[lim] = parent;
      child = parent.next;
      while ( child != null && parent.compareTo(child) <= 0 )
      {
        parent = child;
        child = parent.next;
      }
      head = child;
      parent.next = null;
    }
    work[lim] = null;        // Extra null reference for odd cases

    // (B) Repeatedly merge pairs of lists . . .
    for ( ; lim > 1; lim = (lim+1)/2 ) {
      for ( j = k = 0; k < lim; j++, k += 2 )
        work[j] = merge ( work[k], work[k+1] );
      work[j] = null;     // Extra null reference for odd cases
    }

    // (C) Update the head and tail references
    head = work[0];
    for ( tail = head; tail.next != null; tail = tail.next )
      ;
  }
  
  static public ListNode merge ( ListNode left, ListNode right ) {
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


  public static void main(String[] args) {

  }

}
