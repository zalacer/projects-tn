package pq;

import static v.ArrayUtils.*;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

//for ex2429 dev

@SuppressWarnings("unchecked")
public class MinMaxPQex2429A<Key> {
  private Key[] pqmin;
  private Key[] pqmax;
  private int nmin;
  private int nmax;
  private Comparator<Key> comparator;
  private Class<?> keyClass = null;
  
  public MinMaxPQex2429A(int initCapacity) {
    pqmin = (Key[]) new Object[initCapacity + 1];
    pqmax = (Key[]) new Object[initCapacity + 1];
    nmin = 0; nmax = 0;
  }
  
  public MinMaxPQex2429A(int initCapacity, Comparator<Key> comparator) {
    this.comparator = comparator;
    pqmin = (Key[]) new Object[initCapacity + 1];
    pqmax = (Key[]) new Object[initCapacity + 1];
    nmin = 0; nmax = 0;
  }
  
  public MinMaxPQex2429A(Comparator<Key> comparator) {
    this(1, comparator);
  }
  
  public MinMaxPQex2429A(Key...keys) {
    if (keys == null || keys.length == 0) {
      pqmin = (Key[]) new Object[1]; nmin = 0;
      pqmax = (Key[]) new Object[1]; nmax = 0; return;
    }
    nmin = keys.length; int i = 0; Class<?> kclass;
    pqmin = (Key[]) new Object[keys.length + 1]; 
    while (i < nmin)
      if (keys[i] != null) {
        pqmin[i+1] = keys[i];
        kclass = keys[i].getClass();
        if (keyClass == null) keyClass = kclass;
        else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
        i++;
      }
    for (int k = nmin/2; k >= 1; k--)
      sinkMin(k);
    assert isMinHeap();
    
    nmax = keys.length; i = 0;
    pqmax = (Key[]) new Object[keys.length + 1]; 
    while (i < nmax)
      if (keys[i] != null) {
        pqmax[i+1] = keys[i];
        i++;
      }
    for (int k = nmax/2; k >= 1; k--)
      sinkMax(k);
    assert isMaxHeap();
  }
  
  public MinMaxPQex2429A(Comparator<Key> comp, Key...keys) {
    comparator = comp;
    if (keys == null || keys.length == 0) {
      pqmin = (Key[]) new Object[1]; nmin = 0;
      pqmax = (Key[]) new Object[1]; nmax = 0; return;
    }
    nmin = keys.length; int i = 0; Class<?> kclass;
    pqmin = (Key[]) new Object[keys.length + 1]; 
    while (i < nmin)
      if (keys[i] != null) {
        pqmin[i+1] = keys[i];
        kclass = keys[i].getClass();
        if (keyClass == null) keyClass = kclass;
        else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
        i++;
      }
    for (int k = nmin/2; k >= 1; k--)
      sinkMin(k);
    assert isMinHeap();
    
    nmax = keys.length; i = 0;
    pqmax = (Key[]) new Object[keys.length + 1]; 
    while (i < nmax)
      if (keys[i] != null) {
        pqmax[i+1] = keys[i];
        i++;
      }
    for (int k = nmax/2; k >= 1; k--)
      sinkMax(k);
    assert isMaxHeap();
  }
  
  public Key min() {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    return pqmin[1];
  }

  public Key max() {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    return pqmax[1];
  }
  
  public Key delMin() {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    Key min = pqmin[1];
    exchMin(1, nmin--);
    sinkMin(1);
    removeByRefEqMax(min);
    pqmin[nmin+1] = null;
    if ((nmin > 0) && (nmin == (pqmin.length - 1) / 4)) resizeMin(pqmin.length  / 2);
    assert isMinHeap();
    assert isMaxHeap();
    return min;
}
  
  public Key delMax() {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    Key max = pqmax[1];
    exchMax(1, nmax--);
    sinkMax(1);
    removeByRefEqMin(max);
    pqmax[nmax+1] = null; 
    if ((nmax > 0) && (nmax == (pqmax.length - 1) / 4)) resizeMax(pqmax.length / 2);
    assert isMaxHeap();
    assert isMinHeap();
    return max;
  }
  
  public void insert(Key x) {
    insertMin(x);
    insertMax(x);
  }
  
  public void insertMin(Key x) {
    // double size of array if necessary
    if (x == null) { System.err.println("nulls not allowed"); return; }
    if (nmin == pqmin.length - 1) resizeMin(2 * pqmin.length);
    Class<?> kclass = x.getClass();
    if (keyClass == null) keyClass = kclass;
    else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
    // add x, and percolate it up to maintain heap invariant
    pqmin[++nmin] = x;
    swimMin(nmin);
    assert isMinHeap();
  }
  
  public void insertMax(Key x) {
    // double size of array if necessary
    if (x == null) { System.err.println("nulls not allowed"); return; }
    if (nmax >= pqmax.length - 1) resizeMax(2 * pqmax.length);
    // add x, and percolate it up to maintain heap invariant
    pqmax[++nmax] = x;
    swimMax(nmax);
    assert isMaxHeap();
  }
  
  public Key removeAtIndexMin(int v) {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    Key k = pqmin[v];
    exchMin(v, nmin--);
    Key l = pqmin[v];
    sinkMin(v);
    if (pqmin[v] == l) swimMin(v);
    pqmin[nmin+1] = null;
    if ((nmin > 0) && (nmin == (pqmin.length - 1) / 4)) resizeMin(pqmin.length / 2);
    assert isMinHeap();
    return k;
  }
  
  // based on delMax()
  public Key removeAtIndexMax(int v) {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    Key k = pqmax[v];
    exchMax(v, nmax--);
    Key l = pqmax[v];
    sinkMax(v);
    if (pqmax[v] == l) swimMax(v);
    pqmax[nmax+1] = null;
    if ((nmax > 0) && (nmax == (pqmax.length - 1) / 4)) resizeMax(pqmax.length / 2);
    assert isMaxHeap();
    return k;
  }
  
  public boolean removeMin(Key k) {
    return removeByValueMin(k);
  }
  
  public boolean removeMax(Key k) {
    return removeByValueMax(k);
  }
  
  //based on java.util.PriorityQueue.remove(Object)
  public boolean removeByValueMin(Key k) {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    int i = indexOfByValueMin(k);
    if (i != -1) { removeAtIndexMin(i); return true; }
    else return false;
  }
  
  //based on java.util.PriorityQueue.remove(Object)
  public boolean removeByValueMax(Key k) {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    int i = indexOfByValueMax(k);
    if (i != -1) { removeAtIndexMax(i); return true; }
    else return false;
  }
  
  //based on java.util.PriorityQueue.removeEq(Object)
  boolean removeByRefEqMin(Key k) {
    for (int i = 1; i <= sizeMin(); i++) {
        if (k == pqmin[i]) {
          removeAtIndexMin(i); return true;
        }
    }
    return false;
  }
  
  //based on java.util.PriorityQueue.removeEq(Object)
  boolean removeByRefEqMax(Key k) {
    for (int i = 1; i <= sizeMax(); i++) {
        if (k == pqmax[i]) {
          removeAtIndexMax(i); return true;
        }
    }
    return false;
  }
  
  public int indexOfMin(Key k) {
    return indexOfByValueMin(k);
  }
  
  public int indexOfMax(Key k) {
    return indexOfByValueMax(k);
  }
  
  // based on java.util.PriorityQueue.indexOf(Object)
  public int indexOfByValueMin(Key k) {
    if (k != null) {
      for (int i = 1; i < nmin; i++)
        if (k.equals(pqmin[i]))
          return i;
    }
    return -1;
  }
  
  // based on java.util.PriorityQueue.indexOf(Object)
  public int indexOfByValueMax(Key k) {
    if (k != null) {
      for (int i = 1; i < nmax; i++)
        if (k.equals(pqmax[i]))
          return i;
    }
    return -1;
  }
  
  public int indexOfByRefEqMin(Key k) {
    if (k != null) {
      for (int i = 1; i < nmin; i++)
        if (k == (pqmin[i]))
          return i;
    }
    return -1;
  }
  
  public int indexOfByRefEqMax(Key k) {
    if (k != null) {
      for (int i = 1; i < nmax; i++)
        if (k == (pqmax[i]))
          return i;
    }
    return -1;
  }
  
  public boolean containsMin(Key k) {
    return indexOfByValueMin(k) != -1;
  }
  
  public boolean containsMax(Key k) {
    return indexOfByValueMax(k) != -1;
  }
  
  //based on java.util.PriorityQueue.contains(Object)
  public boolean containsByValueMin(Key k) {
    return indexOfByValueMin(k) != -1;
  }
  
  //based on java.util.PriorityQueue.contains(Object)
  public boolean containsByValueMax(Key k) {
    return indexOfByValueMax(k) != -1;
  }
  
  public boolean containsByRefEqMin(Key k) {
    return indexOfByRefEqMin(k) != -1;
  }
  
  public boolean containsByRefEqMax(Key k) {
    return indexOfByRefEqMax(k) != -1;
  }

  private boolean isMinHeap() {
    return isMinHeap(1);
  }

  private boolean isMinHeap(int k) {
    if (k > nmin) return true;
    int left = 2*k;
    int right = 2*k + 1;
    if (left  <= nmin && greater(k, left))  return false;
    if (right <= nmin && greater(k, right)) return false;
    return isMinHeap(left) && isMinHeap(right);
  }
  
  private boolean isMaxHeap() {
    return isMaxHeap(1);
  }

  private boolean isMaxHeap(int k) {
    if (k > nmax) return true;
    int left = 2*k;
    int right = 2*k + 1;
    if (left  <= nmax && less(k, left))  return false;
    if (right <= nmax && less(k, right)) return false;
    return isMaxHeap(left) && isMaxHeap(right);
  }
  
  private void swimMin(int k) {
    while (k > 1 && greater(k/2, k)) {
        exchMin(k, k/2);
        k = k/2;
    }
  }
  
  private void swimMax(int k) {
    while (k > 1 && less(k/2, k)) {
      exchMax(k, k/2);
      k = k/2;
    }
  }
  
  private void sinkMin(int k) {
    while (2*k <= nmin) {
        int j = 2*k;
        if (j < nmin && greater(j, j+1)) j++;
        if (!greater(k, j)) break;
        exchMin(k, j);
        k = j;
    }
  }
  
  private void sinkMax(int k) {
    while (2*k <= nmax) {
      int j = 2*k;
      if (j < nmax && less(j, j+1)) j++;
      if (!less(k, j)) break;
      exchMax(k, j);
      k = j;
    }
  }

  public int sizeMin() {
    return nmin;
  }
  
  public int sizeMax() {
    return nmax;
  }
  
  public boolean isEmpty() {
    return isEmptyMin() && isEmptyMax();
  }
  
  public boolean isEmptyMin() {
    return nmin == 0;
  }
  
  public boolean isEmptyMax() {
    return nmax == 0;
  } 
  
  private void resizeMin(int capacity) {
    assert capacity > nmin;
    Key[] temp = (Key[]) new Object[capacity];
    for (int i = 1; i <= nmin; i++)
        temp[i] = pqmin[i];
    pqmin = temp;
  }
  
  private void resizeMax(int capacity) {
    assert capacity > nmax;
    Key[] temp = (Key[]) new Object[capacity];
    for (int i = 1; i <= nmax; i++)
      temp[i] = pqmax[i];
    pqmax = temp;
  }
  
  private boolean less(int i, int j) {
    if (comparator == null) return ((Comparable<Key>) pqmax[i]).compareTo(pqmax[j]) < 0;
    else return comparator.compare(pqmax[i], pqmax[j]) < 0;
  }
  
  private boolean greater(int i, int j) {
    if (comparator == null) return ((Comparable<Key>) pqmin[i]).compareTo(pqmin[j]) > 0;
    else return comparator.compare(pqmin[i], pqmin[j]) > 0;
}
   
  private void exchMin(int i, int j) {
    Key swap = pqmin[i]; pqmin[i] = pqmin[j]; pqmin[j] = swap;
  }
  
  private void exchMax(int i, int j) {
    Key swap = pqmax[i]; pqmax[i] = pqmax[j]; pqmax[j] = swap;
  }

  public void showMin() {
    if (nmin == 0) System.out.print("<nothing in pqmin>");
    for (int i = 1; i < nmin+1 ; i++) System.out.print(pqmin[i]+" ");
    System.out.println();
  }
  
  public void showMax() {
    if (nmax == 0) System.out.print("<nothing in pqmax>");
    for (int i = 1; i < nmax+1 ; i++) System.out.print(pqmax[i]+" ");
    System.out.println();
  }
  
  public void show() {
    showMin(); showMax();   
  }
  
  public void printHeapMin() {
    par(pqmin);
  }
  
  public void printHeapMax() {
    par(pqmax);
  }
 
  public void printHeaps() {
    par(pqmin); par(pqmax);
  }
  
  
  
  public static void main(String[] args) {
    
    MinMaxPQex2429A<Integer> pq;
    Random r = new Random(System.currentTimeMillis());
    System.out.println("insert tests");
    Integer[] a = rangeInteger(1,100);
    shuffle(a,r);
    pq = new MinMaxPQex2429A<>();
    for (int i = 0; i < a.length; i++) {
      pq.insert(a[i]);
      pq.printHeaps();
      System.out.println();
    }
    
    
    System.out.println("\ndelMin and delMax tests");
    shuffle(a,r);
    par(a);
//    a = new Integer[]{5,2,4,3,1,6};
    pq = new MinMaxPQex2429A<>(a);
    pq.printHeaps();
    while(!pq.isEmpty()) {
      System.out.println(pq.delMin());
      pq.printHeaps();
      if (!pq.isEmpty()) {
        System.out.println(pq.delMax());
        pq.printHeaps();
      }
    }
//    System.out.println(pq.min());
//    System.out.println(pq.max());
//    System.out.println(pq.delMin());
//    pq.printHeaps();
//    System.out.println(pq.delMax());
//    pq.printHeaps();
//    System.out.println(pq.delMin());
//    pq.printHeaps();
//    System.out.println(pq.delMax());
//    pq.printHeaps();
//    System.out.println(pq.delMin());
//    pq.printHeaps();
//    System.out.println(pq.delMax());
//    pq.printHeaps();
//    System.out.println(pq.delMin());
//    pq.printHeaps();
//    System.out.println(pq.delMax());
//    pq.printHeaps();
  }

}
