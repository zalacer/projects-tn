package st;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import analysis.Timer;
import ds.Queue;
import pq.IntervalHeap;

// based on st.IntervalSET with Double keys
// not fully compliant with st.OrderedSET since it
//    has delMin() and delMax() but not delete()

@SuppressWarnings("unused")
public class IntervalSETD implements Iterable<Double> {
  public static final int DEFAULTCAPACITY = 9;
  private int CurrentSize;  // number of keys of type K
  private int MaxSize;      // max number of keys of type K
  private int N;            // length of heap = 1 + max number of intervals
  private Interval[] heap;
  private Class<?> kclass = Double.class;
  private boolean resizable = false;
  private boolean oneTime = false; // enable resizable for one resizing

  public IntervalSETD(int capacity) {
    if (capacity < 1) throw new IllegalArgumentException("capacity must be  > 0");
    MaxSize = capacity; // max # elements
    N = MaxSize / 2 + MaxSize % 2 + 1;
    heap = ofDim(Interval.class, N);
    for (int i = 0; i < N; i++)  heap[i] = new Interval();
    CurrentSize = 0;
  }

  public IntervalSETD(int capacity, boolean resizable) {
    if (capacity < 1) throw new IllegalArgumentException(
        "capacity must be  > 0");
    MaxSize = capacity; // max # elements
    N = MaxSize / 2 + MaxSize % 2 + 1;
    System.out.println("N="+N);
    heap = ofDim(Interval.class, N);
    for (int i = 0; i < N; i++)  heap[i] = new Interval();
    CurrentSize = 0;
    if (resizable) this.resizable = true;
  }

  @SafeVarargs
  public IntervalSETD(Double...z) {
    if (z == null || z.length < 1) throw new IllegalArgumentException(
        "IntervalHeap(K[] z): z must not be non null and have length > 0");
    MaxSize = z.length;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    if (resizable) this.resizable = true;
    heap = ofDim(Interval.class, N);
    for (int i = 0; i < N; i++) {
      if (z[i] == null) continue;
      heap[i] = new Interval();
    }
    for (int i = 0; i < MaxSize; i++) add(z[i]);
  }

  @SafeVarargs
  public IntervalSETD(boolean resizable, Double...z) {
    if (z == null || z.length < 1) throw new IllegalArgumentException(
        "IntervalHeap(K[] z): z must not be non null and have length > 0");
    MaxSize = z.length;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    heap = ofDim(Interval.class, N);
    for (int i = 1; i < N; i++) heap[i] = new Interval();
    for (int i = 0; i < MaxSize; i++) {
      if (z[i] == null) continue;
      add(z[i]);
    }
    if (resizable) this.resizable = true;
  }
  
  public IntervalSETD(IntervalSETD h) {
    if (h == null) {
      MaxSize = DEFAULTCAPACITY-1;
      N = MaxSize / 2 + MaxSize % 2 + 1;
      heap = ofDim(Interval.class, N);
      for (int i = 0; i < N; i++)  heap[i] = new Interval();
      CurrentSize = 0;
      return;
    }
    Double[] z = h.toArray();
    MaxSize = z.length;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    if (h.resizable) this.resizable = true;
    heap = ofDim(Interval.class, N);
    for (int i = 1; i < N; i++) heap[i] = new Interval();
    for (int i = 0; i < MaxSize; i++) {
      if (z[i] == null) continue;
      add(z[i]);
    }  
  }

  public boolean isEmpty() {
    return CurrentSize == 0;
  }

  public boolean isFull() {
    return CurrentSize == MaxSize;
  }
  
  public void clear() {
    for (int i = 0; i < heap.length; i++) heap[i] = new Interval();
    CurrentSize = 0;
  }
  
  public boolean contains(Double k) {
    if (isEmpty() || k.compareTo(min()) < 0 || k.compareTo(max()) > 0) return false;
    for (int i = 0; i < heap.length; i++) 
      if (heap[i] != null && (heap[i].left != null && k.equals(heap[i].left)
           || heap[i].right != null && k.equals(heap[i].right))) return true;
    return false;
  }
  
  public Double ceiling(Double x) {
    if (x == null || isEmpty()) return null;
    Double[] a = toArray();
    for (int i = 0; i < a.length; i++)
      if (a[i].compareTo(x) >= 0) return a[i];
    return null;
  }
  
  public Double floor(Double x) {
    if (x == null || isEmpty()) return null;
    Double[] a = toArray();
    for (int i = a.length-1; i > -1; i--)
      if (a[i].compareTo(x) <= 0) return a[i];
    return null;
  }

  public int size() { return CurrentSize; }
  
  public int capacity() { return MaxSize; }

  public int maxSize() { return MaxSize; }

  public Double min() {
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    return heap[1].left;
  }

  public Double max() {
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    return heap[1].right;
  }

  public boolean add(Double x) {
    if (x == null || contains(x)) return false;
    if (kclass == null) kclass = x.getClass();
    if (CurrentSize == MaxSize) {
      if (resizable) { 
        resize(MaxSize*2);
        if (oneTime) resizeOff();
      }
      else System.err.println("no capacity left in IntervalHeap and resizing "
          + "is off: enable it by running resizeOn()");
      return false;
    }
    if (Objects.isNull(heap[CurrentSize/2+CurrentSize%2])) 
      heap[CurrentSize+CurrentSize%2] = new Interval();
    
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
    assert capacity > CurrentSize;
    MaxSize = capacity;
    N = MaxSize / 2 + MaxSize % 2 + 1;
    Interval[] temp = ofDim(Interval.class, N);
    int len = CurrentSize / 2 + CurrentSize % 2;
    for (int i = 1; i <= len; i++) temp[i] = heap[i];
    for (int i = len+1; i < temp.length; i++) temp[i] = new Interval();
    heap = temp;
  }

  public void resizeOn() { resizable = true; }

  public void resizeOneTime() { resizable = true; oneTime = true; }

  public void resizeOff() { resizable = false; }

  public Double delMin() {
    // Set x to min element and delete
    // min element from interval heap.
    // check if interval heap is empty
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    Double x = heap[1].left; // min element

    // restructure min heap part
    int LastNode = CurrentSize / 2 + CurrentSize % 2;
    Double y; // element removed from last node
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
        Double t = y; y = heap[ci].right; heap[ci].right = t; // exchange
      }
      i = ci; // move down a level
      ci *= 2;
    }
    if(CurrentSize > 1) heap[i].left = y;
    if (resizable && CurrentSize > 0 && CurrentSize == MaxSize/4) {
      resize(MaxSize/2+1); 
      if (oneTime) resizeOff();
    }
    return x;
  }

  public Double delMax() {
    // Set x to max element and delete
    // max element from interval heap.
    if (CurrentSize == 0) {
      System.err.println("IntervalHeap underflow"); return null;
    }
    Double x = heap[1].right; // max element
    // restructure max heap part
    int LastNode = CurrentSize / 2 + CurrentSize % 2;
    Double y; // element removed from last node
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
        Double t = y; y = heap[ci].left; heap[ci].left = t; // exchange
      }
      i = ci; // move down a level
      ci *= 2;
    }
    if(CurrentSize > 1) heap[i].right = y;
    if (resizable && CurrentSize > 0 && CurrentSize == MaxSize/4) {
      resize(MaxSize/2+1); 
      if (oneTime) resizeOff();
    }
    return x;    
  }
  
  public IntervalSETD clone() { return new IntervalSETD(this); }

  // private methods

  private boolean less(double a, double b) {
    return a - b < 0;
  }

  private boolean lessOrEqual(double a, double b) {
    return a - b <= 0;
  }

  private boolean greater(double a, double b) {
    return a - b > 0;
  }

  private boolean greaterOrEqual(double a, double b) {
    return a - b >= 0;
  }

  public Double[] toArray() {
    if (heap.length == 0) return ofDim(kclass,0);
    else if (heap.length == 0) return new Double[0];
    Double[] z = ofDim(kclass, CurrentSize); int i = 0, c = 0;
    for (i = 0; i < CurrentSize/2+CurrentSize%2-1; i++) {
      z[c++] = heap[i+1].left; z[c++] = heap[i+1].right;
    }
    z[c++] = heap[i+1].left;
    if (heap[i+1].left != heap[i+1].right) z[c] =  heap[i+1].right;
    return z;
  }
  
  public Double[] toOrderedArray() { Double[] a = toArray(); Arrays.sort(a); return a; }
  
  public Iterator<Double> iterator() { return v.ArrayUtils.iterator(toOrderedArray()); }
  
  public Iterable<Double> iterable = () -> iterator();
  
  public Interval[] toIntervalArray() { return heap.clone(); }
  
  public Double[][] toMinMaxArrays() {
    if (heap.length == 0 && kclass != null) return ofDim(kclass,2,0);
    else if (heap.length == 0) return (Double[][]) (new Double[2][0]);
    Double[][] z = ofDim(kclass, 2, CurrentSize/2+CurrentSize%2);
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
  
  public void showOrdered() {
    for (Double k : this) System.out.print(k+" "); System.out.println();
  }

  public void showAll() {
    for (int i = 0; i < heap.length; i++)
      System.out.print(heap[i]+" ");
    System.out.println();
  }
  
  @Override
  public String toString() {
    if (isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    sb.append("("); int i;
    for (i = 1; i <= CurrentSize/2+CurrentSize%2-1; i++) sb.append(heap[i]+",");
    sb.append(heap[i]+")");
    return sb.toString();
  }
  
  public String toOrderedString() {
    if (isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (Double k : this) sb.append(k+",");
    return sb.replace(sb.length()-1, sb.length(), ")").toString();
  }
  
  private final class Interval {
    public Double left; 
    public Double right; 
      
    public Interval(){}
    
//    @Override
//    public int hashCode() {
//      return Objects.hash(left, right);
//    }
    
    @Override
    public int hashCode() {
      HashFunction hf = Hashing.murmur3_32();
      final int nullHash = 1763263686; // also used in SeparateChainingHashSTSETID.hash
      int hc1 = left == null ? nullHash : left.hashCode();
      int hc2 = right == null ? nullHash : right.hashCode();
      return hf.newHasher().putInt(hc1).putInt(hc2).hash().asInt() & 0x7fffffff;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
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

    private IntervalSETD getOuterType() {
      return IntervalSETD.this;
    }
  }

  public static void main(String[] args) {
    
    IntervalSETD h2;
    Double[] d = rangeDouble(0.,1000000.);
    Timer t = new Timer();
    h2 = new IntervalSETD(d);
    System.out.println(t.elapsed()); //60
//    System.out.println(h2.max());
//    System.out.println(h2.min());
//    h2.delMax(); h2.delMin();
//    System.out.println(h2.max());
//    System.out.println(h2.min());
//    System.out.println(t.elapsed());
    t.reset();
    System.out.println(h2.contains(500001.));
    System.out.println(t.elapsed());
    System.exit(0);
    
    
    IntervalSETD h;
    Double[] a = rangeDouble(1.,33.);
    h = new IntervalSETD(true,a);
    int c = 0;
    System.out.println("alternating delMin() and delMax() output:");
    while(!h.isEmpty()) {
      c++;
      if (c%2==0) System.out.print(h.delMax()+" ");
      else System.out.print(h.delMin()+" ");
      h.showAll();
      System.out.println("size="+h.size()+" maxSize="+h.maxSize());
    }
    System.out.println("\nisEmpty="+h.isEmpty());
    h.showAll();
    System.out.println("capacity="+h.capacity());
    for (double i : a) {
      System.out.println("insert("+i+")");h.add(i);
      h.showAll();
      System.out.println("size="+h.size()+" maxSize="+h.maxSize());
    }
    System.exit(0);
    System.out.println("isFull="+h.isFull());
    if (h.isFull()) { h.resizeOneTime(); h.add(33.); }
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
    System.out.println(h.toOrderedString());
    System.out.println("delMin"); double x = h.delMin();
    System.out.println(h.toOrderedString());
    System.out.println(h.contains(19.));
    System.out.println(h.contains(26.));
    h.clear();
    System.out.println(h.toOrderedString());
    for (double i : a) System.out.print(i+" "); System.out.println();
    for (double i : a) h.add(i);
    System.out.println(h.toOrderedString());
    par(h.toOrderedArray());
  }

}
