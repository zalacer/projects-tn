package st;

import static java.lang.Math.*;
import static analysis.Log.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import analysis.Timer;
import ds.Queue;
import ds.QueueN;
import v.Tuple2;
import st.RedBlackSETD;
import st.SequentialSearchSTSETD;

// based on SeparateChainingHashST with RedBlackSETD values
@SuppressWarnings("unused")
public class SeparateChainingHashSTSETD<Key> implements Iterable<Key> {
  private static final int INIT_CAPACITY = 4;
  private static final int TOLERANCE = 10;

  private int n;  // number of key-value pairs
  private int m;  // hash table size
  private int ma[] = new int[1]; // for hash Function
  private SequentialSearchSTSETD<Key>[] st;
  private Class<?> kclass = null; // class of Key
  private Class<?> vclass = RedBlackSETD.class; // class of Value
  private Integer[] primes = {3,3,5,11,17,31,61,127,251,509,1021,2039,4093,8191,16381,32749, //p478
      65521,131071,262139,524287,1048573,2097143,4194301,8388593,16777213,33554393,
      67108859,134217689,268435399,536870909,1073741789,2147483647};
  private Function<Key,Integer> hash = (k) -> {
    int t = k == null ? 1763263686 : k.hashCode() & 0x7fffffff;
    if (lg(ma[0]) < 27) t = t % primes[(int)lg(ma[0])+5];
    return t % ma[0];
  };
  private int deleteProbes = 0;
  private int getProbes = 0;
  private int putProbes = 0;
  private double tolerance = -1; 

  public SeparateChainingHashSTSETD() { 
    this(INIT_CAPACITY);
  } 

  public SeparateChainingHashSTSETD(String t) {
    this(INIT_CAPACITY); ma[0] = m;
    if (t.matches("[0-9.]+")) {
      double u = Double.parseDouble(t);
      if (u < 1.01) this.tolerance = 1.01;
      else if (u > Integer.MAX_VALUE) this.tolerance = Integer.MAX_VALUE;
      else this.tolerance = u;
    }
  } 

  public SeparateChainingHashSTSETD(int m) {
    this.m = m; ma[0] = m;
    st = ofDim(SequentialSearchSTSETD.class, m);
    for (int i = 0; i < m; i++)
      st[i] = new SequentialSearchSTSETD<Key>();
  }

  public SeparateChainingHashSTSETD(Key[] ka, RedBlackSETD[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,RedBlackSETD> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,RedBlackSETD>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); m = ta.length;
    m = calcMadjustment(); ma[0] = m;
    kclass = ka.getClass().getComponentType();
    st = ofDim(SequentialSearchSTSETD.class, m);
    for (int i = 0; i < m; i++)
      st[i] = new SequentialSearchSTSETD<Key>();
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  public int probes() { return deleteProbes+getProbes+putProbes; }

  public void zeroProbes() { deleteProbes = getProbes = putProbes = 0; }
  
  public int getDeleteProbes() {  return deleteProbes; }
  
  public int getGetProbes() {  return getProbes; }

  public int getPutProbes() {  return putProbes; }
  
  public double avgProbes() { return (1.+floor(1.*n/m)); }

  public int calcMadjustment() {
    double ap = avgProbes();
//    int adj = tolerance > 5 ? (int) (1.3*ceil(ap)-tolerance) : (int) ceil(ap);
//    if (ap > tolerance) return adj > 2 ? adj*m : 2*m;
     if (tolerance >= 5 && ap >= tolerance) return (int)round(2*lg(tolerance)*m);
     else if (ap >= tolerance) return 2*m;
    return m;
  }

  private void resize(int chains) {
    SeparateChainingHashSTSETD<Key> temp = 
        new SeparateChainingHashSTSETD<Key>(chains);
    for (int i = 0; i < m; i++) {
      for (Key key : st[i].keys()) {
        temp.put(key, st[i].get(key));
      }
    }
    this.m  = temp.m; ma[0] = m;
    this.n  = temp.n;
    this.st = temp.st;
  }

  //    private int hash(Key key) { return (key.hashCode() & 0x7fffffff) % m;} 

  //    private int hash2(Key x) {
  //      // p474 search times are proportional to 1+N/M
  //      int t = x.hashCode() & 0x7fffffff;
  //      if (lg(m) < 26) t = t % primes[(int)lg(m)+5];
  //      return t % m;
  //    }

  public int getM() { return m; }

  public int getN() { return n; }

  public double getTolerance() { return tolerance; }

  public int size() { return n; } 

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    return get(key) != null;
  } 

  public RedBlackSETD get(Key key) {
//    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    int i = hash.apply(key);
    st[i].zeroProbes();
    RedBlackSETD v = st[i].get(key);
    getProbes += st[i].probes();
    return v;
  }
  
  public void put(Key key, Double d) {
//    if (key == null) throw new IllegalArgumentException("first argument to put() is null");
    if (kclass == null && key != null) kclass = key.getClass();
    if (tolerance == -1 && n >= 10*m) resize(2*m);
    else if (tolerance > 0) {
      int m2 = calcMadjustment();
      if (m2 > m) {
        if (tolerance == 1) resize(8*n);
        else resize(m2);
      }
    }
    int i = hash.apply(key);
    st[i].zeroProbes();
    if (!st[i].contains(key))  n++;
    st[i].put(key, d);
    putProbes += st[i].probes();
  }

  public void put(Key key, RedBlackSETD val) {
//    if (key == null) throw new IllegalArgumentException("first argument to put() is null");
    if (kclass == null && key != null) kclass = key.getClass();
    if (val == null) {  delete(key); return;  }
    if (tolerance == -1 && n >= 10*m) resize(2*m);
    else if (tolerance > 0) {
      int m2 = calcMadjustment();
      if (m2 > m) {
        if (tolerance == 1) resize(8*n);
        else resize(m2);
      }
    }
    int i = hash.apply(key);
    st[i].zeroProbes();
    if (!st[i].contains(key))  n++;
    st[i].put(key, val);
    putProbes += st[i].probes();
  } 

  public void delete(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to delete() is null");
    int i = hash.apply(key);
    st[i].zeroProbes();
    if (st[i].contains(key)) n--;    
    st[i].delete(key);
    deleteProbes += st[i].probes();
    if (tolerance == -1 && m > INIT_CAPACITY && n <= 2*m) resize(m/2);
    else if (tolerance > 0) {
      int m2 = calcMadjustment();
      if (m2 <= 3*m/4) resize(m/2);
    }
  } 
  
  public double chi2() {
    // return χ² = (M/N)((f(0) - N/M)² + (f(1) - N/M)² + . . . (f(M-1) - N/M)²)
    // ex3430
    double mn = 1.*m/n;
    double nm = 1.*n/m;
    double sum = 0;
    for (int i = 0; i < st.length; i++) sum+=pow(st[i].size()-nm,2);
    return mn*sum;
  }

  public Iterable<Key> keys() {
    QueueN<Key> queue = new QueueN<Key>();
    for (int i = 0; i < m; i++) {
      for (Key key : st[i].keys())
        queue.enqueue(key);
    }
    return queue;
  }
  
  public Iterator<Key> iterator() { return keys().iterator(); }

  public Key[] toKeyArray() {
    QueueN<Key> queue = new QueueN<Key>();
    for (int i = 0; i < m; i++) {
      for (Key key : st[i].keys())
        queue.enqueue(key);
    }
    Key[] ka = queue.toArray(ofDim(kclass,n));
    Arrays.sort(ka);
    return ka;
  }

  public RedBlackSETD[] toValArray() {
    Key[] ka = toKeyArray();
    RedBlackSETD[] va = ofDim(RedBlackSETD.class,ka.length);
    for (int i = 0; i < ka.length; i++) va[i] = get(ka[i]);
    return va;
  }
  
  @Override
  public String toString() {
    if (isEmpty()) return "{}";
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (Key k : this) sb.append(k+":"+get(k)+",");
    return sb.replace(sb.length()-1, sb.length(), "}").toString();
  }
  
  @Override public int hashCode() {
    int h = 0;
    Iterator<Key> it = keys().iterator();
    while (it.hasNext()) {
      Key k = it.next(); RedBlackSETD v = get(k);
      h += (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
    }
    return h;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SeparateChainingHashST)) return false;
    SeparateChainingHashST x = (SeparateChainingHashST) o;
    if (x.size() != size()) return false;
    Iterator<Key> it = keys().iterator();
    while (it.hasNext()) {
      Key k = it.next();
      RedBlackSETD value = get(k);
      if (value == null) {            
        if (!(x.get(k)==null && x.contains(k))) return false;
      } else if (!value.equals(x.get(k))) return false;
    }
    return true;
  }

  public int emptyLists() {
    int e = 0;
    for (int i = 0; i < st.length; i++)
      if (st[i].isEmpty()) e++;
    return e;
  }

  public static void main(String[] args) { 
    
    Timer t = new Timer();
    SecureRandom r = new SecureRandom();
    Double[] d = {null, 1., 2., 3., 4.};
//    d = rangeDouble(0.,5.);
//    shuffle(d,r);
    RedBlackSETD[] e = ofDim(RedBlackSETD.class, 10);
    e[0] = new RedBlackSETD(rangeDouble(0.,3.));
    e[1] = new RedBlackSETD(rangeDouble(3.,6.));
    e[2] = new RedBlackSETD(rangeDouble(6.,9.));
    e[3] = new RedBlackSETD(rangeDouble(9.,12.));
    e[4] = new RedBlackSETD(rangeDouble(12.,15.));
    t = new Timer();
    SeparateChainingHashSTSETD<Double> sv = new SeparateChainingHashSTSETD<>();
    for (int i = 0; i < d.length; i++) sv.put(d[i],e[i]);
    System.out.println(t.elapsed()); //63,78,62
    System.out.println("size="+sv.size());
    System.out.println(sv);
//    System.exit(0);

    String[]  a = "one two three four five six seven eight nine".split("\\s+");
    e[5] = new RedBlackSETD(rangeDouble(15.,18.));
    e[6] = new RedBlackSETD(rangeDouble(18.,21.));
    e[7] = new RedBlackSETD(rangeDouble(21.,24.));
    e[8] = new RedBlackSETD(rangeDouble(24.,27.));
    SeparateChainingHashSTSETD<String> h = 
        new SeparateChainingHashSTSETD<String>(a,e);
    par(h.toKeyArray());
    par(h.toValArray());
    Iterator<String> it = h.keys().iterator();
    while (it.hasNext()) {
      String s = it.next();
      System.out.println(s+":"+h.get(s)+" ");
    }
    System.out.println();
    

    


    //      SeparateChainingHashST<String, Integer> st = new SeparateChainingHashST<String, Integer>();
    //        for (int i = 0; !StdIn.isEmpty(); i++) {
    //            String key = StdIn.readString();
    //            st.put(key, i);
    //        }
    //
    //        // print keys
    //        for (String s : st.keys()) 
    //            System.out.println(s + " " + st.get(s)); 

  }
}

