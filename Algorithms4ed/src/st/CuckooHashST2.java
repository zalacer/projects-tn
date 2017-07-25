package st;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

import v.Tuple2;

public class CuckooHashST2<Key, Value> {
  // tables are defined to include keys and values and are implemented
  // using multiple separate arrays of each
  private static final int NT = 3; // default number of tables
  private static final int INITCAPACITY = 12; // default initial table capacity
  private static final float LOADFACTOR = 0.9f; // default table load factor, N/M
  private static final int MAXTABLESIZE = 2147483579;
  private static final int MAXPRIME = 2147483587;

  private int N = 0; // total number of entries
  private int M; // overall tables size
  private int[] na; // per table number of entries          
  private int m; // size per table
  private Tuple2<Key,Value>[][] entries;
  private Function<Key,Integer>[] ha;
//  private int[] primes = {3,5,11,17,23,31,61,127,251,509,1021,2039,4093,8191,16381,32749, //p478
//      65521,131071,262139,524287,1048573,2097143,4194301,8388593,16777213,33554393,
//      67108859,134217689,268435399,536870909,1073741789,2147483579};
  private Boolean noresize = false;
  private Class<?> kclass = null; // class of Key
  private Class<?> vclass = null; // class of Value
  private SecureRandom r = new SecureRandom();
  private boolean lock = false; // write lock for rehash()
  private int probes = 0;
  
  public CuckooHashST2() { this(INITCAPACITY); }

  public CuckooHashST2(int capacity) {
    M = capacity + NT - capacity % NT;
    m = M/NT;
//    m = capacity;
//    M = m * NT;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m); 
    ha = generateHashFuns();
  }
  
  public CuckooHashST2(int capacity, boolean noresize) {
    M = capacity + NT - capacity % NT;
    m = M/NT;
//    m = capacity;
//    M = m * NT;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m); 
    ha = generateHashFuns();
    if (noresize == true) this.noresize = true;
  }
  
  public Function<Key,Integer>[] generateHashFuns() {
    int[] p = { getPrime() };
    Function<Key,Integer>[] hf = ofDim(Function.class, NT);
    for (int i = 0; i < NT; i++) {
      r.setSeed(System.currentTimeMillis());
      int x = r.nextInt(); int y = r.nextInt();
      for (int j = 0; j < 10000; j++) r.nextInt();
      hf[i] = (k) -> {
        int h = k.hashCode();
        int u = h >>> 16;
        int l = h & 0xFFFF;
        int v = (u * x + l * y + 3) & 0x7fffffff;
        return (v % p[0]) % m;
      };
    }
    return hf;
  }

//  public int getPrime2() {
//    // return the first prime in primes greater than m 
//    for (int i = 1; i < primes.length; i++) {
//      if (primes[i] > m) return primes[i];
//    }
//    return MAXPRIME;
//  }
  
  public int getPrime() {
    //return first prime number greater than m and less than Integer.MAX_VALUE.
    for (int i = m + 1; i < Integer.MAX_VALUE; i++) {
      int f = 0;
      for (int j = 2; j <= (int) sqrt(i); j++)
        if (i % j == 0) f++;
      if (f == 0) return i;
    }
    return MAXPRIME;
  }

  public int size() { return N; }

  public boolean isEmpty() { return size() == 0; }
  
  public double alpha() { return 1.*N/M; }
  
  public double[] alphas() {
    double[] a = new double[NT];
    for (int i = 0; i < NT; i++) a[i] = 1.*na[i]/m;
    return a;
  }
  
  public int sizePerTable() { return m; }
  
  public int m() { return m; }
  
  public int[] na() { return na.clone(); }
  
  public int M() { return M; }
  
  public int N() { return N; }
  
  public int probes() { return probes; }
  
  public void zeroProbes() { probes = 0; }
  
  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    return get(key) != null;
  }

  private void resize(int capacity) {
    // resizes all entries arrays to least M > capacity && M % NT == 0.
    if (noresize == true || m >= MAXTABLESIZE) return;
    CuckooHashST2<Key, Value> tmp = new CuckooHashST2<Key, Value>(capacity);
    for (int i = 0; i < entries.length; i++)
      for (int j = 0; j < entries[i].length; j++)
        if (entries[i][j] != null && entries[i][j]._1 != null && entries[i][j]._2 != null)
          tmp.put(entries[i][j]._1, entries[i][j]._2);
    entries = tmp.entries;
    m = tmp.m;
    M = tmp.M;
    N = tmp.N;
    ha = tmp.ha;
    na = tmp.na;
  }
  
  public void rehash() {
    System.out.println("enter rehash");
    resize(M); 
    System.out.println("exit rehash");
  }
 
//  public void rehash() {
//    int l, sz = size();
//    int[] na2 = new int[NT];
//    Tuple2<Key,Value>[][] entries2;
//    lock = true;
//    LOOP: while (true) {
//      entries2 = ofDim(Tuple2.class, NT, m);
//      Function<Key,Integer>[] hf = generateHashFuns();   
//      for (int i = 0; i < entries.length; i++) {
//        for (int j = 0; j < entries[i].length; j++) {
//          Tuple2<Key,Value> en = entries[i][j];
//          for (int t = 0; t < sz + 1; ++t) {
//            l = t % NT;
//            int h = hf[l].apply(en._1);
//            Tuple2<Key,Value> e = entries2[l][h];
//            if (e == null || e._1 == null) {
//              entries2[l][h] = en; na2[l]++; continue; 
//            }
//            if (t == sz) continue LOOP;
//            en = e;
//          }
//        }
//      }
//      break;
//    }
//    na = na2;
//    entries = entries2;
//    lock = false;
//  }

  public void put(Key key, Value val) {
    if (key == null) 
      throw new IllegalArgumentException("first argument to put() is null");
    if (kclass == null)  kclass = key.getClass();
    if (val == null) { delete(key); return; }
    if (vclass == null) vclass = val.getClass();
    if (lock) { System.err.println("put: locked: put failed"); return; }
    for (int i = 0; i < NT; i++) {
      int h = ha[i].apply(key);
      Tuple2<Key,Value> e = entries[i][h];
      if (e != null && e._1 != null && e._1.equals(key)) {      
        e._2 = val;
        return;
      }
    }   
    if (alpha() >= LOADFACTOR) resize(2*M); 
    int j, sz = size();
    Tuple2<Key,Value> en = new Tuple2<>(key,val);
    while (true) {      
      for (int t = 0; t < sz + 1; ++t) {
        j = t % NT;
        int h = ha[j].apply(en._1);
        Tuple2<Key,Value> e = entries[j][h];
        entries[j][h] = en;      
        if (e == null || e._1 == null) { na[j]++; N++; return; }
        en = e;        
      }
      System.out.println("before rehash N="+N);
      rehash();
      System.out.println("after rehash N="+N);

    }
  }
  
//  private Tuple2<Key,Value> tryInsertEntry(Tuple2<Key,Value> toInsert) {
//    /* Starting at the initial position, bounce back and forth between the
//     * hash tables trying to insert the value.  During this process, keep
//     * a counter that keeps growing until it reaches the a value above the
//     * size.  If this is ever hit, we give up and return the element that 
//     * was last bounced.
//     *
//     * We also use numTries as an odd/even counter so we know which hash
//     * table we're inserting into.
//     */
//    for (int numTries = 0; numTries < size() + 1; ++numTries) {
//      /* Compute the hash code and see what's at that position. */
//      int hash = ha[numTries % NT].apply(toInsert._1);
//      Key k = keys[numTries % 2][hash];
//      Value v = vals[numTries % 2][hash];
//
//      /* If the entry is null, the slot is open and we just write the
//       * element there.
//       */
//      if (k == null) {
//        keys[numTries % 2][hash] = toInsert._1;
//        vals[numTries % 2][hash] = toInsert._2;
//        return null;
//      }
//
//      /* Otherwise displace this element with the element to insert,
//       * then try inserting the bumped element into the other array.
//       */
//      keys[numTries % 2][hash] = toInsert._1;
//      vals[numTries % 2][hash] = toInsert._2;
//      toInsert = new Tuple2<>(k,v);
//    }
//
//    return toInsert;
//  }

  public Value get(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    for (int i = 0; i < NT; i++) {
      int h = ha[i].apply(key);
      Tuple2<Key,Value> e = entries[i][h];
      if (e != null && e._1 != null && e._1.equals(key)) {
        probes++;
        return e._2 ;      
      }
      probes++;
    }
    return null;
  }

  public void delete(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to delete() is null");
    if (lock) { System.err.println("delete: locked: delete failed"); return; }
    for (int i = 0; i < NT; i++) {
      int h = ha[i].apply(key);
      Tuple2<Key,Value> e = entries[i][h];
      if (e != null && e._1 != null && e._1.equals(key)) {      
        e = null; na[i]--; N--; 
        if (N > 0 && N <= M/8) resize(M/2);
        return;
      }
    }
  }
  
  public Tuple2<Key,Value>[][] entryArrays() {
    return entries.clone();
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2<Key,Value>[] toEntryArray() {
    if (N == 0) return null;
    Tuple2<Key,Value>[] ea = ofDim(Tuple2.class,N); int c = 0;
    for (int i = 0; i < entries.length; i++)
      for (int j = 0; j < entries[i].length; j++)
        if (entries[i][j] != null) ea[c++] = entries[i][j];
    Comparator<Tuple2<Key,Value>> cmp;
    if (Comparable.class.isAssignableFrom(kclass)
        && Comparable.class.isAssignableFrom(vclass)) {
      cmp = (e1,e2) -> {     
        int d = ((Comparable<Key>)e1._1).compareTo(e2._1);
        return d != 0 ? d : ((Comparable<Value>)e1._2).compareTo(e2._2);
      };  
      Arrays.sort(ea,cmp);
      return ea;
    } else if (Comparable.class.isAssignableFrom(kclass)) {
      cmp = (e1,e2) -> { return ((Comparable<Key>)e1._1).compareTo(e2._1); };
      Arrays.sort(ea,cmp);
      return ea;  
    } else return ea;
  }
  
  @SuppressWarnings("unchecked")
  public Key[] toKeyArray() {
    if (N == 0) return null;
    Key[] ka = ofDim(kclass,N); int c = 0;
    for (int i = 0; i < entries.length; i++)
      for (int j = 0; j < entries[i].length; j++)
        if (entries[i][j] != null && entries[i][j]._1 != null)
          ka[c++] = entries[i][j]._1;
    if (c < N) ka = take(ka,c);
    Comparator<Key> cmp;
    if (Comparable.class.isAssignableFrom(kclass)) {
      cmp = (k1,k2) -> { return ((Comparable<Key>)k1).compareTo(k2); };
      Arrays.sort(ka,cmp);
      return ka;  
    }
    return ka;
  }
  
  public Value[] toValueArray() {
    if (N == 0) return null;
    Tuple2<Key,Value>[] ea = ofDim(Tuple2.class,N); int c = 0;
    for (int i = 0; i < entries.length; i++)
      for (int j = 0; j < entries[i].length; j++)
        if (entries[i][j] != null) ea[c++] = entries[i][j];
    Value[] va = ofDim(vclass,ea.length); c = 0;    
    for (int i = 0; i < ea.length; i++)
        va[c++] = ea[i]._2;
    return va;
  }

  public Iterable<Tuple2<Key,Value>> entries() {
    return new Iterable<Tuple2<Key,Value>>() {
      public Iterator<Tuple2<Key,Value>> iterator() {
        return Arrays.stream(toEntryArray()).iterator();
      }
    };
  }
  
  public Iterable<Key> keys() {
    return new Iterable<Key>() {
      public Iterator<Key> iterator() {
        return Arrays.stream(toKeyArray()).iterator();
      }
    };
  }
  
  public Iterable<Value> values() {
    return new Iterable<Value>() {
      public Iterator<Value> iterator() {
        return Arrays.stream(toValueArray()).iterator();
      }
    };
  }
  
  public Iterator<Tuple2<Key,Value>> entryIterator() {
    return Arrays.stream(toEntryArray()).iterator();
  }
  
  public Iterator<Key> keyIterator() {
    return Arrays.stream(toKeyArray()).iterator();
  }
  
  public Iterator<Value> valueIterator() {
    return Arrays.stream(toValueArray()).iterator();
  }
  
  public boolean check() {
    boolean check = true;
    // check that hash table is at most 50% full
    double a = alpha();
    if (a >= LOADFACTOR) {
      System.err.println("alpha = "+a+" is greater than LOADFACTOR = "+LOADFACTOR);
      check = false;
    }
    // check that each key in entries can be found by get()
    for (int i = 0; i < entries.length; i++) {
      for (int j = 0; j < entries[i].length; j++) {
        if (entries[i][j] != null) {
          Key k = entries[i][j]._1;
          Value v = entries[i][j]._2;
          Value v2 = get(k);
          if (v != v2) {
            System.err.println("entries["+i+"]["+j+"] = "+entries[i][j]
                + " but get("+k+") = "+v2);
            check = false;
          }
        }
      }
    }
    return check;
  }
  
  public void show() {
    for (int i = 0; i < entries.length; i++) {
      System.out.print("entries["+i+"]: ");
      for (int j = 0; j < entries[i].length; i++) {
        Tuple2<Key,Value> e = entries[i][j];
        System.out.print(e._1+":"+e._2+" ");
      }
      System.out.println();     
    }
  }
  
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    String x = N < 51 ? ") " : ")\n";
    for (int i = 0; i < entries.length; i++) {
     sb.append("entries["+i+"]=(");
      for (int j = 0; j < entries[i].length; i++) {
        Tuple2<Key,Value> e = entries[i][j];
        sb.append(e._1+":"+e._2+",");
      }
      sb.deleteCharAt(sb.length()-1);
      sb.append(x);     
    }
    sb.deleteCharAt(sb.length()-1);
    return sb.toString();
  }
 
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + M;
    result = prime * result + N;
    result = prime * result + java.util.Arrays.deepHashCode(entries);
    result = prime * result + ((kclass == null) ? 0 : kclass.hashCode());
    result = prime * result + ((vclass == null) ? 0 : vclass.hashCode());
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CuckooHashST2 other = (CuckooHashST2) obj;
    if (M != other.M)
      return false;
    if (N != other.N)
      return false;
    if (kclass == null) {
      if (other.kclass != null)
        return false;
    } else if (!kclass.equals(other.kclass))
      return false;
    if (vclass == null) {
      if (other.vclass != null)
        return false;
    } else if (!vclass.equals(other.vclass))
      return false;
//    if (!java.util.Arrays.deepEquals(entries, other.entries))
//      return false;
    if (N > 0) {
      Tuple2<Key,Value>[] ea = toEntryArray();
      for (Tuple2<Key,Value> e : ea) {
        if (!other.get(e._1).equals(e._2)) return false;
      }
    }
    return true;
  }

  public static void main(String[] args) { 
    
    SecureRandom r = new SecureRandom(); int c = 0, n = 1000000;
    CuckooHashST2<Integer, Integer> h = new CuckooHashST2<Integer, Integer>();
    while (c < n) {
      int M = h.M();
      int x = r.nextInt(5000);
      for (int i = 0; i < 15; i++)  h.put(x + M*i, 1);
      c++;
    }
    System.out.println("overall alpha = "+h.alpha());
    System.out.print("per table alphas = "); par(h.alphas());
    System.out.println("size = "+h.size());
    System.out.println("per table size = "+h.m());
    System.out.println("total table size = "+h.M());
    Integer[] keys = h.toKeyArray();
    assert keys.length == h.size();
    h.zeroProbes();
    for (Integer k : keys) {
      Integer g = h.get(k);
      if (g == null) System.out.println("get returned null");
    }
    System.out.println("avgProbes/get = "+(1.*h.probes()/keys.length));
    System.out.println("check = "+h.check());
    
 
  }
}
