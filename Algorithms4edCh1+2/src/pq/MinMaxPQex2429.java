package pq;

import static v.ArrayUtils.*;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

//for ex2429 dev

@SuppressWarnings("unchecked")
public class MinMaxPQex2429<K> {
  private Pair<K,Integer>[] pqmin;
  private Pair<K,Integer>[] pqmax;
  private int nmin;
  private int nmax;
  private Comparator<K> comparator;
  private Class<?> KClass = null;
  
  public MinMaxPQex2429(int initCapacity) {
    pqmin = ofDim(Pair.class, initCapacity + 1);
    pqmax = ofDim(Pair.class, initCapacity + 1);
    nmin = 0; nmax = 0;
  }
  
  public MinMaxPQex2429(int initCapacity, Comparator<K> comparator) {
    this.comparator = comparator;
    pqmin = ofDim(Pair.class, initCapacity + 1);
    pqmax = ofDim(Pair.class, initCapacity + 1);
    nmin = 0; nmax = 0;
  }
  
  public MinMaxPQex2429(Comparator<K> comparator) {
    this(1, comparator);
  }
  
  public MinMaxPQex2429(K...z) {
    if (z == null || z.length == 0) {
      pqmin = ofDim(Pair.class, 1); nmin = 0;
      pqmax = ofDim(Pair.class, 1); nmax = 0; return;
    }
    nmin = z.length; int i = 0; Class<?> kclass;
    pqmin = ofDim(Pair.class, z.length + 1); 
    while (i < nmin)
      if (z[i] != null) {
        pqmin[i+1] = new Pair<K,Integer>(z[i], i+1) ;
        kclass = z[i].getClass();
        if (KClass == null) KClass = kclass;
        else if (kclass.isAssignableFrom(KClass)) KClass = kclass;
        i++;
      }
    for (int k = nmin/2; k >= 1; k--)
      sinkMin(k);
    assert isMinHeap();
    
    nmax = z.length; i = 0;
    pqmax = ofDim(Pair.class, z.length + 1); 
    while (i < nmax)
      if (z[i] != null) {
        pqmax[i+1] = new Pair<K,Integer>(z[i], i+1) ;
        i++;
      }
    for (int k = nmax/2; k >= 1; k--)
      sinkMax(k);
    assert isMaxHeap();
  }
  
  public MinMaxPQex2429(Comparator<K> comp, K...z) {
    comparator = comp;
    if (z == null || z.length == 0) {
      pqmin = ofDim(Pair.class, 1); nmin = 0;
      pqmax = ofDim(Pair.class, 1); nmax = 0; return;
    }
    nmin = z.length; int i = 0; Class<?> kclass;
    pqmin = ofDim(Pair.class, z.length + 1); 
    while (i < nmin)
      if (z[i] != null) {
        pqmin[i+1] = new Pair<K,Integer>(z[i], i+1) ;
        kclass = z[i].getClass();
        if (KClass == null) KClass = kclass;
        else if (kclass.isAssignableFrom(KClass)) KClass = kclass;
        i++;
      }
    for (int k = nmin/2; k >= 1; k--)
      sinkMin(k);
    assert isMinHeap();
    
    nmax = z.length; i = 0;
    pqmax = ofDim(Pair.class, z.length + 1); 
    while (i < nmax)
      if (z[i] != null) {
        pqmax[i+1] = new Pair<K,Integer>(z[i], i+1) ;
        i++;
      }
    for (int k = nmax/2; k >= 1; k--)
      sinkMax(k);
    assert isMaxHeap();
  }
  
  public K min() {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    return pqmin[1].k;
  }

  public K max() {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    return pqmax[1].k;
  }
  
  public K delMin() {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    K min = pqmin[1].k;
    exchMin(1, nmin--);
    sinkMin(1);
    removeByRefEqMax(min);
    pqmin[nmin+1] = null;
    if ((nmin > 0) && (nmin == (pqmin.length - 1) / 4)) resizeMin(pqmin.length  / 2);
    assert isMinHeap();
    assert isMaxHeap();
    return min;
}
  
  public K delMax() {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    K max = pqmax[1].k;
    exchMax(1, nmax--);
    sinkMax(1);
    removeByRefEqMin(max);
    pqmax[nmax+1] = null; 
    if ((nmax > 0) && (nmax == (pqmax.length - 1) / 4)) resizeMax(pqmax.length / 2);
    assert isMaxHeap();
    assert isMinHeap();
    return max;
  }
  
  public void insert(K x) {
    insertMin(x);
    insertMax(x);
  }
  
  public void insertMin(K x) {
    // double size of array if necessary
    if (x == null) { System.err.println("nulls not allowed"); return; }
    if (nmin == pqmin.length - 1) resizeMin(2 * pqmin.length);
    Class<?> kclass = x.getClass();
    if (KClass == null) KClass = kclass;
    else if (kclass.isAssignableFrom(KClass)) KClass = kclass;
    // add x, and percolate it up to maintain heap invariant
    pqmin[++nmin] = new Pair<K,Integer>(x, nmin);
    swimMin(nmin);
    assert isMinHeap();
  }
  
  public void insertMax(K x) {
    // double size of array if necessary
    if (x == null) { System.err.println("nulls not allowed"); return; }
    if (nmax >= pqmax.length - 1) resizeMax(2 * pqmax.length);
    // add x, and percolate it up to maintain heap invariant
    pqmax[++nmax] = new Pair<K,Integer>(x, nmax);
    swimMax(nmax);
    assert isMaxHeap();
  }
  
  public K removeAtIndexMin(int v) {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    K k = pqmin[v].k;
    exchMin(v, nmin--);
    K l = pqmin[v].k;
    sinkMin(v);
    if (pqmin[v] == l) swimMin(v);
    pqmin[nmin+1] = null;
    if ((nmin > 0) && (nmin == (pqmin.length - 1) / 4)) resizeMin(pqmin.length / 2);
    assert isMinHeap();
    return k;
  }
  
  // based on delMax()
  public K removeAtIndexMax(int v) {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    K k = pqmax[v].k;
    exchMax(v, nmax--);
    K l = pqmax[v].k;
    sinkMax(v);
    if (pqmax[v] == l) swimMax(v);
    pqmax[nmax+1] = null;
    if ((nmax > 0) && (nmax == (pqmax.length - 1) / 4)) resizeMax(pqmax.length / 2);
    assert isMaxHeap();
    return k;
  }
  
  public boolean removeMin(K k) {
    return removeByValueMin(k);
  }
  
  public boolean removeMax(K k) {
    return removeByValueMax(k);
  }
  
  //based on java.util.PriorityQueue.remove(Object)
  public boolean removeByValueMin(K k) {
    if (isEmptyMin()) throw new NoSuchElementException("Priority queue underflow");
    int i = indexOfByValueMin(k);
    if (i != -1) { removeAtIndexMin(i); return true; }
    else return false;
  }
  
  //based on java.util.PriorityQueue.remove(Object)
  public boolean removeByValueMax(K k) {
    if (isEmptyMax()) throw new NoSuchElementException("Priority queue underflow");
    int i = indexOfByValueMax(k);
    if (i != -1) { removeAtIndexMax(i); return true; }
    else return false;
  }
  
  //based on java.util.PriorityQueue.removeEq(Object)
  boolean removeByRefEqMin(K k) {
    for (int i = 1; i <= sizeMin(); i++) {
        if (k == pqmin[i].k) {
          removeAtIndexMin(i); return true;
        }
    }
    return false;
  }
  
  //based on java.util.PriorityQueue.removeEq(Object)
  boolean removeByRefEqMax(K k) {
    for (int i = 1; i <= sizeMax(); i++) {
        if (k == pqmax[i].k) {
          removeAtIndexMax(i); return true;
        }
    }
    return false;
  }
  
  public int indexOfMin(K k) {
    return indexOfByValueMin(k);
  }
  
  public int indexOfMax(K k) {
    return indexOfByValueMax(k);
  }
  
  // based on java.util.PriorityQueue.indexOf(Object)
  public int indexOfByValueMin(K k) {
    if (k != null) {
      for (int i = 1; i < nmin; i++)
        if (k.equals(pqmin[i].k))
          return i;
    }
    return -1;
  }
  
  // based on java.util.PriorityQueue.indexOf(Object)
  public int indexOfByValueMax(K k) {
    if (k != null) {
      for (int i = 1; i < nmax; i++)
        if (k.equals(pqmax[i].k))
          return i;
    }
    return -1;
  }
  
  public int indexOfByRefEqMin(K k) {
    if (k != null) {
      for (int i = 1; i < nmin; i++)
        if (k == (pqmin[i].k))
          return i;
    }
    return -1;
  }
  
  public int indexOfByRefEqMax(K k) {
    if (k != null) {
      for (int i = 1; i < nmax; i++)
        if (k == (pqmax[i].k))
          return i;
    }
    return -1;
  }
  
  public boolean containsMin(K k) {
    return indexOfByValueMin(k) != -1;
  }
  
  public boolean containsMax(K k) {
    return indexOfByValueMax(k) != -1;
  }
  
  //based on java.util.PriorityQueue.contains(Object)
  public boolean containsByValueMin(K k) {
    return indexOfByValueMin(k) != -1;
  }
  
  //based on java.util.PriorityQueue.contains(Object)
  public boolean containsByValueMax(K k) {
    return indexOfByValueMax(k) != -1;
  }
  
  public boolean containsByRefEqMin(K k) {
    return indexOfByRefEqMin(k) != -1;
  }
  
  public boolean containsByRefEqMax(K k) {
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
    Pair<K,Integer>[] temp = ofDim(Pair.class, capacity);
    for (int i = 1; i <= nmin; i++)
        temp[i] = pqmin[i];
    pqmin = temp;
  }
  
  private void resizeMax(int capacity) {
    assert capacity > nmax;
    Pair<K,Integer>[] temp = ofDim(Pair.class, capacity);
    for (int i = 1; i <= nmax; i++)
      temp[i] = pqmax[i];
    pqmax = temp;
  }
  
  private boolean less(int i, int j) {
    if (comparator == null) 
      return ((Comparable<K>) pqmax[i].k).compareTo(pqmax[j].k) < 0;
    else return comparator.compare(pqmax[i].k, pqmax[j].k) < 0;
  }
  
  private boolean greater(int i, int j) {
    if (comparator == null) 
      return ((Comparable<K>) pqmin[i].k).compareTo(pqmin[j].k) > 0;
    else return comparator.compare(pqmin[i].k, pqmin[j].k) > 0;
}
   
  private void exchMin(int i, int j) {
    Pair<K,Integer>swap = pqmin[i]; pqmin[i] = pqmin[j]; pqmin[j] = swap;
    pqmin[i].i = j; pqmin[j].i = i;
  }
  
  private void exchMax(int i, int j) {
    Pair<K,Integer> swap = pqmax[i]; pqmax[i] = pqmax[j]; pqmax[j] = swap;
    pqmax[i].i = j; pqmax[j].i = i;
  }

  public void showMin() {
    if (nmin == 0) System.out.print("<nothing in pqmin>");
    for (int i = 1; i < nmin+1 ; i++) System.out.print(pqmin[i].k+" ");
    System.out.println();
  }
  
  public void showMax() {
    if (nmax == 0) System.out.print("<nothing in pqmax>");
    for (int i = 1; i < nmax+1 ; i++) System.out.print(pqmax[i].k+" ");
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
  
  private final class Pair<L,R> {
    public L k; 
    public R i; 
      
    @SuppressWarnings("unused")
    public Pair(){}
    
    public Pair(L l, R r) {
      this.k= l; this.i = r;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(k, i);
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
      Pair other = (Pair) obj;
      if (k == null) {
        if (other.k != null)
          return false;
      } else if (!k.equals(other.k))
        return false;
      if (i == null) {
        if (other.i != null)
          return false;
      } else if (!i.equals(other.i))
        return false;
      return true;
    }

    @Override public String toString() {
      return "("+k+","+i+")";
    }
  }
  
  public static void main(String[] args) {
    
    MinMaxPQex2429<Integer> pq;
    Random r = new Random(System.currentTimeMillis());
    System.out.println("insert tests");
    Integer[] a = rangeInteger(1,100);
    shuffle(a,r);
    pq = new MinMaxPQex2429<>();
    for (int i = 0; i < a.length; i++) {
      pq.insert(a[i]);
      pq.printHeaps();
      System.out.println();
    }
    
    
    System.out.println("\ndelMin and delMax tests");
    shuffle(a,r);
    par(a);
//    a = new Integer[]{5,2,4,3,1,6};
    pq = new MinMaxPQex2429<>(a);
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
