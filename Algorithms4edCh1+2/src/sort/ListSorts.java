package sort;

// http://penguin.ewu.edu/~trolfe/NaturalMerge/List.java

import sort.ListNode;

public class ListSorts {

  ListNode head = null,
      tail = null;
  int      size = 0;

  public void empty()
  {  head = tail = null;  size = 0;  }

  public void addFront ( int value )
  {
    ListNode node = new ListNode ( value );

    size++;
    if ( head == null )
      head = tail = node;
    else
      head.insertBefore ( node );
  }

  public void addBack ( int value )
  {
    ListNode node = new ListNode ( value );

    size++;
    if ( head == null )
      head = tail = node;
    else
    {
      tail.insertAfter ( node );
      tail = tail.next;
    }
  }

  public void addSorted ( int value )
  {
    ListNode node = new ListNode ( value );

    size++;
    if ( head == null )
      head = tail = node;
    else
    {
      ListNode current = head;

      if ( head.compareTo(node) > 0 )
        head.insertBefore ( node );
      else
      {
        while ( current.next != null && current.next.compareTo(node) < 0 )
          current = current.next;
        if ( current.next != null )
          current.insertAfter ( node );
        else
        {
          current.insertAfter ( node );
          tail = node;
        }
      }
    }
  }

  public void traverse ( )
  {  ListNode.traverse(head);  }

  /* Public entry point:  call the package method */
  public void mergeRecur ( )
  {
    if ( size < 1 ) return;     // Protect against call with an empty list
    head = mergeRecur ( head, size );
    // Tail reference has probably gone invalid.  Correct it.
    for ( tail = head; tail.next != null; tail = tail.next )
      ;
  }

  /* Recursive merge sort
   *
   * Split the list in two.
   * Recursively mergeRecur those two lists
   * Merge the sorted result to generate the new list.
   */
  ListNode mergeRecur ( ListNode head, int n )
  {
    int k,
    ltN = n/2,
    rtN = n - ltN;
    ListNode current = head,
        left = head,
        right;

    if ( n < 2 )
      return head;

    for ( k = 1; k < ltN; k++ )
      current = current.next;

    right = current.next;
    current.next = null;

    //**/  System.out.print ("Left:   "); ListNode.traverse(left);
    //**/  System.out.print ("Right:  "); ListNode.traverse(right);

    left  = mergeRecur ( left,  ltN );
    right = mergeRecur ( right, rtN );
    head  = ListNode.merge ( left, right );
    //**/  System.out.print ("Result: "); ListNode.traverse(head);
    return head;
  }

  /* Iterative merge sort
   *
   * (A) Generate an array of length-one lists, ending with an extra
   *     null reference
   *
   * (B) Repeatedly merge pairs of lists and reinsert them into the front
   *     of the array.  If the current logical size of the array is odd,
   *     the last list will be merged with a null list to generate the
   *     last entry in the new array.  After each pass, a null reference
   *     is appended to the array in case its new logical size should be
   *     odd.
   *
   * (C) When the logical size of the array comes down to 1, we're finished.
   *     At that point, update the head pointer, then traverse the list to
   *     set the tail pointer correctly.
   */
  public void mergeIter ( )
  {
    ListNode[] work;
    int j,       // Destination index at the front
    k,       // Source index advanced by twos
    lim;     // Current logical size

    if ( size < 2 ) return;

    // Allow space for the appended null reference at the end.
    work = new ListNode[size+1];

    // (A) Generate the initial array of size-one lists.
    for ( k = 0; k < size; k++ )
    {
      work[k] = head;
      head = head.next;
      if ( work[k] == null )   // This should NEVER be true!
      {  System.err.println ("Invalid list"); System.exit(-1);  }
      work[k].next = null;
    }
    work[k] = null;        // Extra null reference for odd cases

    // (B) Repeatedly merge pairs of lists . . .
    for ( lim = size ; lim > 1; lim = (lim+1)/2 )
    {
      for ( j = k = 0; k < lim; j++, k += 2 )
        work[j] = ListNode.merge ( work[k], work[k+1] );
      work[j] = null;     // Extra null reference for odd cases
    }

    // (C) Update the head and tail references
    head = work[0];
    for ( tail = head; tail.next != null; tail = tail.next )
      ;
  }

  public void mergeNatural ( )
  {
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
    for ( ; lim > 1; lim = (lim+1)/2 )
    {
      for ( j = k = 0; k < lim; j++, k += 2 )
        work[j] = ListNode.merge ( work[k], work[k+1] );
      work[j] = null;     // Extra null reference for odd cases
    }

    // (C) Update the head and tail references
    head = work[0];
    for ( tail = head; tail.next != null; tail = tail.next )
      ;
  }

  /**
   * QuickSort for lists
   *
   * (A) Generate an array of nodes
   *
   * (B) QuickSort that list
   *
   * (C) Traverse the ARRAY, connecting the nodes together
   */
  public void quickSort ( )
  {
    int k,          // Loop variable
    size;       // List size
    ListNode[] work;
    ListNode   current = head;

    // Preliminary:  find the size of the list
    for ( size = 0 ; current != null ; size++ )
      current = current.next;

    if ( size < 2 ) return;

    work = new ListNode[size+1];

    // (A) Generate the initial array of node pointers.
    for ( k = 0; k < size; k++ )
    {
      work[k] = head;
      head = head.next;
      //    work[k].next = null;  // Not needed in this context
    }

    // (B) QuickSort that array
    qSortOpt (work, size);

    // (C) Traverse the ARRAY, connecting the nodes together
    for ( k = 1; k < size; k++ )
      work[k-1].next = work[k];

    // Update the head and tail references
    head = work[0];
    tail = work[size-1];
    tail.next = null;      // Important:  END the list!
  }

  /**
   * Public access to optimized quick sort
   */
  @SuppressWarnings("rawtypes")
  public static void qSortOpt (Comparable [] x, int n)
  {  qSortO(0, n-1, x);  }

  /**
   * Optimized quick sort --- insertion sort for segments < CUTOFF
   */
  static final int CUTOFF  = 20; // Length to shift to inSort

  @SuppressWarnings("rawtypes")
  static void qSortO(int lo, int hi, Comparable [] x)
  {/* Basic QuickSort algorithm for larger segments:

            1)  Check for exit condition:  if hi does not come after lo,
                there is nothing left to sort.
            2)  Let the "partition" function position one element to its
                exact position:  everything to its left belongs on the
                left, everything to its right belongs on its right.
            3)  QuickSort can then call itself to sort everything to the
                left as a sub-array.
            4)  Rather than recursively calling itself for the right
                sub-array, QuickSort can just update lo and stay within
                the current call, thus removing the tail recursion.

         Note:  Almost _all_ of the work for qSort is embedded in the
                partition routine.
   */
    int mid;

    while (lo < hi)   // while allows for removing tail recursion
    {  if ( hi-lo < CUTOFF )            // Small case: Insert Sort
    {  inSort(lo, hi, x); break;  }

    mid = partition(lo, hi, x);
    qSortO(lo, mid - 1, x);          // Recursive part for left
    lo = mid + 1;                    // "Tail" recursion on right
    } // end while
  }

  /**
   * Swap two elements in an array --- used by partition
   */
  static void swap (int lt, int rt, Object [] x)
  {  Object tmp = x[lt];  x[lt] = x[rt];  x[rt] = tmp;  }

  /**
   * Partition method used by quick sort methods.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  static int partition (int lo, int hi, Comparable [] x)
  {/*Rearrange the x[] array so that a single element is properly
        positioned:  all elements to the left of the "partitioning
        element" (or pivot) belong on the left; all to the right
        belong on the right.  The position of this partitioning
        element is then the value of the partition function.

        This version of partition based on Thomas Naps, "Introduction
        to Data Structures and Algorithm Analysis"; the Median of Three
        improvement is taken from Robert Sedgewick, "Algorithms."
   */
    // "Median of Three" --- middle element becomes the actual element.
    //                       Insure that ends up in x[lo].
    int mid;
    Comparable hold;

    if (lo >= hi) return hi;
    mid = (lo + hi) / 2;
    // We wish to move the median -- b in "abc" -- to position lo
    if (x[lo].compareTo(x[hi]) < 0)        // abc acb bac
      if (x[lo].compareTo(x[mid]) < 0)    // abc acb
        if (x[mid].compareTo(x[hi]) < 0) // abc
          swap(lo, mid, x);
        else                             // acb
          swap(lo, hi, x);
      else ;                              // bac --- null statement
    else                                   // bca cab cba
      if (x[lo].compareTo(x[mid]) > 0)    // otherwise bca and no action
        if (x[mid].compareTo(x[hi]) > 0) // cba
          swap(lo, mid, x);
        else                             // cab
          swap(lo, hi, x);
    // Open up a "hole" at x[lo], with median as pivot value
    hold = x[lo];
    // The loop has two exits:  the two places where the lo and the hi
    // indexes have come together.  In lieu of extra flags or logical
    // comparisons, this code uses an explicit "break" at those two places.
    while (true)      // Note:  "break" out when (lo == hi)
    {//Search for the value from hi downward to plug the hole at x[lo]
      while (hold.compareTo(x[hi]) < 0 && lo < hi)
        hi = hi - 1;
      //    If we've come together, we're done
      if (lo == hi) break;
      //    Otherwise plug the hole at lo with the value at hi
      x[lo] = x[hi];
      //    The hole is now at hi and lo is guaranteed good w.r.t. Pivot
      lo = lo + 1;

      //    Search for the value from lo upward to plug the hole at x[hi]
      while (x[lo].compareTo(hold) < 0 && lo < hi)
        lo = lo + 1;
      //    If we've come together, we're done
      if (lo == hi) break;
      //    Otherwise plug the hole at hi with the value at lo
      x[hi] = x[lo];
      //    The hole is now at lo and hi is guaranteed good w.r.t. Pivot
      hi = hi - 1;
    } // end "infinite" while loop --- (lo == hi) became true
    // Plug the remaining hole (which is guaranteed to be in hi = lo)
    // with the pivot value, making this the partitioning element.

    x[hi] = hold;

    return hi;
  }

  /**
   * Insertion sort method, used by qSortO
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void inSort ( int lo, int hi, Comparable[] x )
  {  int  lim,    // start of unsorted region
    hole;   // insertion point in sorted region

  for ( lim = lo+1 ; lim <= hi ; lim++ )
  {  Comparable save = x[lim];

  for ( hole = lim ;
      hole > lo && save.compareTo(x[hole-1]) < 0 ;
      hole-- )
  {  x[hole] = x[hole-1];  }

  x[hole] = save; } 
  // end for (lim...
  } // end inSort()



public static void main(String[] args) {

}

}
