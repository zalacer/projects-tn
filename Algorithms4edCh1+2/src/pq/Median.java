package pq;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Comparator;

public class Median<Key> {
  private MinPQ<Key> minpq;
  private MaxPQ<Key> maxpq;
  private int N;
  private Comparator<Key> comparator;
  private Class<?> kclass = null;

  public Median() {
    minpq = new MinPQ<Key>();
    maxpq = new MaxPQ<Key>();
    N = 0;
  }
  
  public Median(Comparator<Key> c) {
    minpq = new MinPQ<Key>();
    maxpq = new MaxPQ<Key>();
    N = 0;
    comparator = c;
  }
  
  @SafeVarargs
  public Median(Key...k) {
    minpq = new MinPQ<Key>();
    maxpq = new MaxPQ<Key>();
    N = 0;
    for (int i = 0; i < k.length; i++) insert(k[i]);
  }
  
  @SafeVarargs
  public Median(Comparator<Key> c, Key...k) {
    minpq = new MinPQ<Key>();
    maxpq = new MaxPQ<Key>();
    N = 0;
    comparator = c;
    for (int i = 0; i < k.length; i++) insert(k[i]);
  }
  
  public boolean isEmpty() { return N == 0; }
  
  public int size() { return N; }
  
  public void insert(Key x) {
    if (isEmpty()) maxpq.insert(x);
    if (kclass == null) {
      kclass = x.getClass();
      if (!(Comparable.class.isAssignableFrom(kclass) || comparator != null)) 
        throw new IllegalArgumentException(
            "Key type must extend Comparable or a Comparator<Key> must be used");
    }
    else if (less(maxpq.max(), x)) minpq.insert(x);
    else maxpq.insert(x);
    N++;
    rebalance();
  }
  
  public Key median() {
    if (isEmpty()) return null;
    return maxpq.max();
  }
  
  public Key delMedian() {
    if (isEmpty()) return null;
    else --N;
    Key m = maxpq.delMax();
    rebalance();
    return m;
  }
  
  public void rebalance() {
    // this recycles keys between minpq and maxpq to ensure their sizes
    // don't vary by more than 2 and the current median is always maxpq.max(),
    // although that's off when size()%2==0 since all types of allowed keys
    // can't be averaged. If keys could be averaged, then when size()%2==0
    // the median would be (minpq.min+maxpq.max)/2.
    if (N % 2 == 1){
      while(maxpq.size() - minpq.size() > 1) minpq.insert(maxpq.delMax());
      while(minpq.size() - maxpq.size() > -1) maxpq.insert(minpq.delMin());
    } else {
      while(minpq.size() - maxpq.size() > 0) maxpq.insert(minpq.delMin());
      while(maxpq.size() - minpq.size() > 0) minpq.insert(maxpq.delMax());
    }
  }

  @SuppressWarnings("unchecked")
  private boolean less(Key i, Key j) {
    if (comparator == null) {
      return ((Comparable<Key>)i).compareTo(j) < 0;
    } else {
      return comparator.compare(i, j) < 0;
    }
  }
  
  public void showMaxPQ() {
    // for debugging
    maxpq.show();
  }
  
  public void showMinPQ() {
    // for debugging
    minpq.show();
  }
  
  public Key[] toSortedArray() {
    Key[] a = append(minpq.toArray(),maxpq.toArray());
    if (comparator != null) Arrays.sort(a, comparator);
    else Arrays.sort(a);
    return a;
  }
  
  public static void main(String[] args) {
    
    Median<Integer> m = new Median<>();
    Integer[] a = rangeInteger(1,20);
    System.out.println("testing find median for odd numbered datasets:");
    System.out.println("median  toSortedArray() after new Key added ");
    for (int i = 0; i < a.length; i++) {
      m.insert(a[i]);
      if (m.size() % 2 == 1)
        System.out.printf("%2s      %1s\n", ""+m.median(), 
            arrayToString(m.toSortedArray(),80,1,1));
    }
    System.out.println("\ntesting median deletion for odd numbered datasets:");
    System.out.println("median");
    System.out.println("deleted toSortedArray() after median deleted");
    Integer median; int c = 0;
    while (!m.isEmpty()) {
      if (++c%2==1) {
        median = m.delMedian();
        System.out.printf("%2s      %1s\n", ""+median, 
            arrayToString(m.toSortedArray(),80,1,1));
      }
      else m.delMedian();
    }

  }

}
