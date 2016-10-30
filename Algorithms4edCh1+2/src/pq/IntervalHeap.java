package pq;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.par;
import static v.ArrayUtils.rangeInteger;

import java.util.Objects;

// based on http://www.mhhe.com/engcs/compsci/sahni/enrich/c9/interval.pdf
// which is locally available at IntervalHeaps.pdf.
public class IntervalHeap<K extends Comparable<K>> {

  private int CurrentSize;  // number of keys of type K
  private int MaxSize;      // max number of keys of type K
  private int N;            // length of heap = 1 + max number of intervals
  private Interval<K,K>[] heap;
  private Class<?> kclass = null;
  private boolean resizeable = false;
  private boolean oneTime = false; // enable resizeable for one resizing

  public IntervalHeap(int capacity) {
    if (capacity < 1) throw new IllegalArgumentException("capacity must be  > 0");
    MaxSize = capacity; // max # elements
    N = MaxSize / 2 + MaxSize % 2 + 1;
    System.out.println("N="+N);
    heap = ofDim(Interval.class, N);
    CurrentSize = 0;
  }

  public IntervalHeap(int capacity, boolean resizeable) {
    if (capacity < 1) throw new IllegalArgumentException(
        "capacity must be  > 0");
    MaxSize = capacity; // max # elements
    N = MaxSize / 2 + MaxSize % 2 + 1;
    System.out.println("N="+N);
    heap = ofDim(Interval.class, N);
    CurrentSize = 0;
    if (resizeable) this.resizeable = true;
  }

  @SafeVarargs
  public IntervalHeap(K...z) {
    if (z == null || z.length < 1) throw new IllegalArgumentException(
        "IntervalHeap(K[] z): z must not be non null and have length > 0");
    MaxSize = z.length;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    heap = ofDim(Interval.class, N);
    for (int i = 0; i < N; i++) {
      if (z[i] == null) continue;
      heap[i] = new Interval<K,K>();
    }
    for (int i = 0; i < MaxSize; i++) insert(z[i]);
  }

  @SafeVarargs
  public IntervalHeap(boolean resizeable, K...z) {
    if (z == null || z.length < 1) throw new IllegalArgumentException(
        "IntervalHeap(K[] z): z must not be non null and have length > 0");
    MaxSize = z.length;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    heap = ofDim(Interval.class, N);
    for (int i = 1; i < N; i++) heap[i] = new Interval<K,K>();
    for (int i = 0; i < MaxSize; i++) {
      if (z[i] == null) continue;
      insert(z[i]);
    }
    if (resizeable) this.resizeable = true;
  }

  public boolean isEmpty() {
    return CurrentSize == 0;
  }

  public boolean isFull() {
    return CurrentSize == MaxSize;
  }

  public int size() {
    return CurrentSize;
  }

  public int capacity() {
    return MaxSize;
  }

  public int maxSize() {
    return MaxSize;
  }

  public K min() {
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    return heap[1].left;
  }

  public K max() {
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    return heap[1].right;
  }

  public boolean insert(K x) {
    if (x == null) return false;
    if (kclass == null) kclass = x.getClass();
    if (CurrentSize == MaxSize) {
      if (resizeable) { 
        resize(MaxSize*2);
        if (oneTime) resizeOff();
      }
      else System.err.println("no capacity left in IntervalHeap and resizing "
          + "is off: enable it by running resizeOn()");
      return false;
    }
    if (Objects.isNull(heap[CurrentSize/2+CurrentSize%2])) 
      heap[CurrentSize+CurrentSize%2] = new Interval<K,K>();
    
    // handle CurrentSize < 2 as a special case
    if (CurrentSize < 2) {
      if (CurrentSize == 1) // CurrentSize is 1
        if (less(x, heap[1].left)) heap[1].left = x;
        else heap[1].right = x;
      else {// CurrentSize is 0
        heap[1].left = x;
        heap[1].right = x;
      }
      CurrentSize++;
      return true;
    }
    // CurrentSize >= 2
    int LastNode = CurrentSize / 2 + CurrentSize % 2;
    boolean minHeap; // true iff x is to be
    // inserted in the min heap part
    // of the interval heap
    if (CurrentSize % 2 == 1)
      // odd number of elements
      if (less(x, heap[LastNode].left))
        // x will be an interval left end
        minHeap = true;
      else minHeap = false;
    else {// even number of elements
      LastNode++;
      if (lessOrEqual(x, heap[LastNode / 2].left))
        minHeap = true;
      else minHeap = false;
    }

    if (minHeap) {// fix min heap of interval heap
      // find place for x
      // i starts at LastNode and moves up tree
      int i = LastNode;
      while (i != 1 && less(x, heap[i/2].left)) {
        // cannot put x in heap[i]
        // move left element down
        heap[i].left = heap[i/2].left;
        i /= 2; // move to parent
      }
      heap[i].left = x;
      CurrentSize++;
      if (CurrentSize % 2 != 0)
        // new size is odd, put dummy in LastNode
        heap[LastNode].right = heap[LastNode].left;
    } else { // fix max heap of interval heap
      // find place for x
      // i starts at LastNode and moves up tree
      int i = LastNode;
      while (i != 1 && greater(x, heap[i/2].right)) {
        // cannot put x in heap[i]
        // move right element down
        heap[i].right = heap[i/2].right;
        i /= 2; // move to parent
      }
      heap[i].right = x;
      CurrentSize++;
      if (CurrentSize % 2 != 0)
        // new size is odd, put dummy in LastNode
        heap[LastNode].left = heap[LastNode].right;
    }
    return true;   
  }

  private void resize(int capacity) {
    assert capacity > MaxSize;
    MaxSize = capacity;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    Interval<K,K>[] temp = ofDim(Interval.class, N);
    int len = CurrentSize / 2 + CurrentSize % 2;
    for (int i = 1; i <= len; i++) temp[i] = heap[i];
    heap = temp;
  }

  public void resizeOn() { resizeable = true; }

  public void resizeOneTime() { resizeable = true; oneTime = true; }

  public void resizeOff() { resizeable = false; }

  public K delMin() {
    // Set x to min element and delete
    // min element from interval heap.
    // check if interval heap is empty
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    K x = heap[1].left; // min element

    // restructure min heap part
    int LastNode = CurrentSize / 2 + CurrentSize % 2;
    K y; // element removed from last node
    if (CurrentSize % 2 == 1) {// size is odd
      y = heap[LastNode].left;
      heap[LastNode].left = heap[LastNode].right = null;
      LastNode--;
      //      show();
    }
    else {// size is even    
      y = heap[LastNode].left;
      heap[LastNode].left = heap[LastNode].right;     
    }
    CurrentSize--;  
    //find place for y starting at root
    int i = 1, // current node of heap
        ci = 2; // child of i
    while (ci <= LastNode) {// find place to put y
      //heap[ci].left should be smaller child of i
      if (ci < LastNode && greater(heap[ci].left, heap[ci+1].left)) ci++;
      //can we put y in heap[i]?
      if (lessOrEqual(y, heap[ci].left)) break; // yes
      //no
      heap[i].left = heap[ci].left; // move child up
      if (greater(y, heap[ci].right)) {
        K t = y; y = heap[ci].right; heap[ci].right = t; // exchange
      }
      i = ci; // move down a level
      ci *= 2;
    }
    //    show();
    if(CurrentSize > 1) heap[i].left = y;
    return x;
  }

  public K delMax() {
    // Set x to max element and delete
    // max element from interval heap.
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    K x = heap[1].right; // max element
    // restructure max heap part
    int LastNode = CurrentSize / 2 + CurrentSize % 2;
    K y; // element removed from last node
    if (CurrentSize % 2 == 1) {// size is odd
      y = heap[LastNode].left;
      LastNode--;
    }
    else {// size is even
      y = heap[LastNode].right;
      heap[LastNode].right = heap[LastNode].left;
    }
    CurrentSize--;
    // find place for y starting at root
    int i = 1, // current node of heap
        ci = 2; // child of i
    while (ci <= LastNode) {// find place to put y
      // heap[ci].right should be larger child of i
      if (ci < LastNode && less(heap[ci].right, heap[ci+1].right)) ci++;
      // can we put y in heap[i]?
      if (greaterOrEqual(y, heap[ci].right)) break; // yes
      // no
      heap[i].right = heap[ci].right; // move child up
      if (less(y, heap[ci].left)) {
        K t = y; y = heap[ci].left; heap[ci].left = t; // exchange
      }
      i = ci; // move down a level
      ci *= 2;
    }
    if(CurrentSize > 1) heap[i].right = y;
    return x;    
  }

  // private methods

  private boolean less(K a, K b) {
    return a.compareTo(b) < 0;
  }

  private boolean lessOrEqual(K a, K b) {
    return a.compareTo(b) <= 0;
  }

  private boolean greater(K a, K b) {
    return a.compareTo(b) > 0;
  }

  private boolean greaterOrEqual(K a, K b) {
    return a.compareTo(b) >= 0;
  }

  @SuppressWarnings("unchecked")
  public K[] toArray() {
    if (heap.length == 0 && kclass != null) return ofDim(kclass,0);
    else if (heap.length == 0) return (K[]) (new Object[0]);
    K[] z = ofDim(kclass, CurrentSize); int i = 0, c = 0;
    for (i = 0; i < CurrentSize/2+CurrentSize%2-1; i++) {
      z[c++] = heap[i+1].left; z[c++] = heap[i+1].right;
    }
    z[c++] = heap[i+1].left;
    if (heap[i+1].left != heap[i+1].right) z[c] =  heap[i+1].right;
    return z;
  }
  
  public Interval<K,K>[] toIntervalArray() {
    return heap;
  }
  
  @SuppressWarnings("unchecked")
  public K[][] toMinMaxArrays() {
    if (heap.length == 0 && kclass != null) return ofDim(kclass,2,0);
    else if (heap.length == 0) return (K[][]) (new Object[2][0]);
    K[][] z = ofDim(kclass, 2, CurrentSize/2+CurrentSize%2);
    for (int i = 1; i <= z[1].length; i++) {
      z[0][i-1] = heap[i].left;  z[1][i-1] = heap[i].right;
    }
    return z;
  }
  
  public void show() {
    for (int i = 1; i <= CurrentSize/2+CurrentSize%2; i++)
      System.out.print(heap[i]+" ");
    System.out.println();
  }

  public void showAll() {
    for (int i = 0; i < heap.length; i++)
      System.out.print(heap[i]+" ");
    System.out.println();
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("("); int i;
    for (i = 1; i <= CurrentSize/2+CurrentSize%2-1; i++) sb.append(heap[i]+",");
    sb.append(heap[i]+")");
    return sb.toString();
  }
  
  private final class Interval<L,R> {
    public L left; 
    public R right; 
      
    public Interval(){}
    
    @Override
    public int hashCode() {
      return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      @SuppressWarnings("rawtypes")
      Interval other = (Interval) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (left == null) { if (other.left != null) return false; } 
      else if (!left.equals(other.left)) return false;
      if (right == null) { if (other.right != null) return false; } 
      else if (!right.equals(other.right)) return false;
      return true;
    }

    @Override public String toString() {
      return "("+left+","+right+")";
    }

    @SuppressWarnings("rawtypes")
    private IntervalHeap getOuterType() {
      return IntervalHeap.this;
    }
  }

  public static void main(String[] args) {

    IntervalHeap<Integer> h;
    Integer[] a = rangeInteger(1,33);
    h = new IntervalHeap<>(a);
    int c = 0;
    System.out.println("alternating delMin() and delMax() output:");
    while(!h.isEmpty()) {
      c++;
      if (c%2==0) System.out.print(h.delMax()+" ");
      else System.out.print(h.delMin()+" ");
    }
    System.out.println("\nisEmpty="+h.isEmpty());
    System.out.println("capacity="+h.capacity());
    for (int i : a) h.insert(i);
    System.out.println("isFull="+h.isFull());
    if (h.isFull()) { h.resizeOneTime(); h.insert(33); }
    System.out.println("capacity="+h.capacity());
    System.out.println("running delMax() and delMin() 7 times each with no output");
    for (int i = 0; i < 7; i++) {h.delMax(); h.delMin(); }
    System.out.println("max="+h.max());
    System.out.println("min="+h.min());
    System.out.println("show() output showing the intervals:");
    h.show();
    System.out.println("toArray() output:");
    par(h.toArray());
    System.out.println("toMinMaxArrays() output:");
    par(h.toMinMaxArrays());
    System.out.println("toString() output:");
    System.out.println(h);
    Integer x = h.delMin();
    if (x != null) ;
  }

}