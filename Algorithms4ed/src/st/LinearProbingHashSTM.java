package st;

import static java.lang.Math.*;
import static java.lang.System.identityHashCode;
import static analysis.Log.ln;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import ds.Queue;
import exceptions.LinearProbingHashConfigurationException;
import v.Tuple2;

// based on st.LinearProbingHashSTX to keep multiple values of a key for ex3508

public class LinearProbingHashSTM<Key, Value> {
  private class V {
    Value v; int c; 
    V(Value w,int d){v=w;c=d;}
    @Override public String toString() { return "("+v+","+c+")"; }
  }
  private static final int INIT_CAPACITY = 4;
  private static final int SEARCHHITSAMPLESIZE = 503;
  private int n = 0; // number of keys in the symbol table
  private int m = 0; // size of linear probing table
  private int ma[] = new int[1]; // for hash Function
  private Integer pa[] = {null}; // for hash Function
  private Key[] keys = null; // the keys
  private V[] vals = null; // the values
  private Class<?> kclass = null; // class of Key
  private Class<?> vclass = null; // class of Value
  private Function<Key,Integer> hash = null;
  private Function<Key,Integer> hash2 = null; // ex3428 - double hashing
  private int[] prime = {0}; // ex3428 - double hashing
  private Boolean noresize = false;
  private String s = null; // ex3412, etc. hash function input control flag
  private char[][] ca = { // ex3412, for generateFunctions in put to set hash
      ("E F G A C B D".replaceAll("\\s+","").toCharArray()),
      ("C E B G F D A".replaceAll("\\s+","").toCharArray()),
      ("B D F A C E G".replaceAll("\\s+","").toCharArray()),
      ("C G B A D E F".replaceAll("\\s+","").toCharArray()),
      ("F G B D A C E".replaceAll("\\s+","").toCharArray()),
      ("G E C A D B F".replaceAll("\\s+","").toCharArray()) }; 
  private int deleteProbes = 0;
  private int getProbes = 0;
  private int putProbes = 0;
  private int equals = 0;
  private int rehashEquals = 0;
  private int resizeEquals = 0;
  // identity hashmap for keys requiring multiple probes
  private IdentityHashMap<Integer,Integer> pmap = new IdentityHashMap<>(21);
  private int ss = SEARCHHITSAMPLESIZE;
  private int[] primes = {3,5,11,17,23,31,61,127,251,509,1021,2039,4093,8191,16381,32749, //p478
    65521,131071,262139,524287,1048573,2097143,4194301,8388593,16777213,33554393,
    67108859,134217689,268435399,536870909,1073741789,2147483647}; //for double hashing
   
  public LinearProbingHashSTM() { this(INIT_CAPACITY); }
  
  public LinearProbingHashSTM(boolean noresize) { this(INIT_CAPACITY,noresize); }
  
  public LinearProbingHashSTM(int capacity) {
    if (capacity < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int): capacity < 1");
    m = capacity; ma[0] = m; 
    hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
  }
  
  public LinearProbingHashSTM(String s) {
    m = INIT_CAPACITY; ma[0] = m;
    if (s.equals("cnst")) {
      this.s = s;
      hash = (k) -> { return 7; };
    } else if (s.matches("[0-9]*")) {
      this.s = s;
      hash = (k) -> { return Integer.parseInt(s); };
    }
  }
  
  public LinearProbingHashSTM(int capacity, String s) {
    if (capacity < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int,String): capacity < 1");
    m = capacity; ma[0] = m;
    if (s.equals("cnst")) {
      this.s = s;
      hash = (k) -> { return 7; };
    } else if (s.matches("[0-9]*")) {
      this.s = s; Integer si = Integer.parseInt(s);
      hash = (k) -> { return si; };
    } else if (s.equals("dhash2")) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; }; 
      prime[0] = getPrime();
      hash2 = (k) -> { return prime[0] - hash.apply(k) % prime[0]; };
      this.s = s;
    } else if (s.equals("dhash")) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
      this.s = s;
    } else hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
  }
  
  public LinearProbingHashSTM(int capacity, boolean noresize) {
    if (capacity < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int,boolean): capacity < 1");
    m = capacity; ma[0] = m; 
    hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    this.noresize = noresize;
  } 
  
  public LinearProbingHashSTM(int capacity, String s, boolean noresize) {
    if (capacity < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int,String,boolean): capacity < 1");
    m = capacity; ma[0] = m;
    this.noresize = noresize;
    this.s = s;
    if (s.equals("cnst")) {
      this.s = s;
      hash = (k) -> { return 7; };
    } else if (s.matches("[0-9]*")) {
      this.s = s; Integer si = Integer.parseInt(s);
      hash = (k) -> { return si; };
    } else if (s.equals("dhash2")) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; }; 
      prime[0] = getPrime();
      hash2 = (k) -> { return prime[0] - hash.apply(k) % prime[0]; };
      this.s = s;
    } else if (s.equals("dhash")) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
      this.s = s;
    } else hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
  }

  public LinearProbingHashSTM(int capacity, int p) {
    if (capacity < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int,int): capacity < 1");
    if (p < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(int,int,Class<?>) constructor 2nd arg < 1");
    m = capacity; ma[0] = m; pa[0] = p;
  }
  
  public LinearProbingHashSTM(int capacity, int p, boolean noresize) {
    this(capacity,p);
    this.noresize = noresize;
  }

  public LinearProbingHashSTM(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    keys = ofDim(kclass, m);
    vals = ofDim(V.class, m);
    hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  public LinearProbingHashSTM(Key[] ka, Value[] va, String s) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); len = m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    if (s == null) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    } else {
      if (s.equals("distinctEven")) {
        this.s = s;
//        keys = ofDim(kclass, 4*m);
//        vals = ofDim(vclass, 4*m);
//        m = 4*m; ma[0] = m;
        hash = (k) -> { return ((k.hashCode()) % ma[0])*2; };
      } else if (s.equals("cnst")) {
        this.s = s;
        hash = (k) -> { return 7; };
      } else if (s.matches("[0-9]*")) {
        this.s = s;
        hash = (k) -> { return Integer.parseInt(s); };
      } else if (s.equals("dhash2")) {
        hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; }; 
        prime[0] = getPrime();
        hash2 = (k) -> { return prime[0] - hash.apply(k) % prime[0]; };
        this.s = "dhash2";
      } else if (s.equals("dhash")) {
        this.s = s;
      } else hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };      
    }
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  public LinearProbingHashSTM(Key[] ka, Value[] va, String s, boolean noresize) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); len = m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    this.noresize = noresize;
    if (s == null) {
      hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    } else {
      if (s.equals("distinctEven")) {
        this.s = s;
//        keys = ofDim(kclass, 4*m);
//        vals = ofDim(vclass, 4*m);
//        m = 4*m; ma[0] = m;
        hash = (k) -> { return ((k.hashCode()) % ma[0])*2; };
      } else  if (s.equals("cnst")) {
        this.s = s;
        hash = (k) -> { return 7; };
      } else if (s.matches("[0-9]*")) {
        this.s = s; Integer si = Integer.parseInt(s);
        hash = (k) -> { return si; };
      } else if (s.equals("dhash2")) {
        hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; }; 
        prime[0] = getPrime();
        hash2 = (k) -> { return prime[0] - hash.apply(k) % prime[0]; };
        this.s = s;
      } else if (s.equals("dhash")) {
        hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
        this.s = s;
      } else hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    }
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  public LinearProbingHashSTM(Key[] ka, Value[] va, boolean noresize) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    keys = ofDim(kclass, m);
    vals = ofDim(V.class, m);
    hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    this.noresize = noresize; 
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
 
  public LinearProbingHashSTM(Key[] ka, Value[] va, int p) {
    if (p < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(Key[],Value[],int) constructor 3rd arg < 1");
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    keys = ofDim(kclass, m); vals = ofDim(V.class, m);
    pa[0] = p;
    hash = (k) -> {
      if (kclass != null && kclass == Character.class)
        return (pa[0] * (char)k) % ma[0];
      else return (pa[0] * k.hashCode()) % ma[0];
    };
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  public LinearProbingHashSTM(Key[] ka, Value[] va, int p, boolean noresize) {
    if (p < 1) throw new IllegalArgumentException(
        "LinearProbingHashSTX(Key[],Value[],int) constructor 3rd arg < 1");
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int len = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,len);
    for (int i = 0; i < len; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); m = ta.length; ma[0] = m;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    keys = ofDim(kclass, m); vals = ofDim(V.class, m);
    pa[0] = p;
    hash = (k) -> {
      if (kclass != null && kclass == Character.class)
        return (pa[0] * (char)k) % ma[0];
      else return (pa[0] * k.hashCode()) % ma[0];
    };
    this.noresize = noresize;
    for (int  i = 0; i < len; i++) put(ta[i]._1, ta[i]._2);
  }
  
  private Function<Key,Integer> generateFunction(Key[] ca) {
    // ex3412
    Function<Key,Integer> h = (c) -> {
      SequentialSearchST<Key,Integer> st = new SequentialSearchST<>();
      for (int i = 0; i < ca.length; i++) st.put(ca[i], i);
      if (st.contains(c)) return st.get(c);
      else return -1;          
    };
    return h;
  }
  
  private Function<Key,Integer> generateFunction2(Key[] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    Function<Key,Integer> h = (c) -> {
      SequentialSearchST<Key,Integer> st = new SequentialSearchST<>();
      for (int i = 0; i < ca.length; i++) st.put(ca[i], 0);
      if (st.contains(c)) return st.get(c);
      else return -1;          
    };
    return h;
  }
  
  private Function<Key,Integer>[] generateFunctions(Key[][] ca) {
    // ex3412
    Function<Key,Integer>[] ha = ofDim(Function.class, ca.length);
    for (int i = 0; i < ca.length; i++) ha[i] = generateFunction(ca[i]);
    return ha;
  }
  
  private Function<Key,Integer>[] generateFunctions2(Key[][] ca) {
    // this has been included as private non-static method LinearProbingHashSTX
    Function<Key,Integer>[] ha = ofDim(Function.class, ca.length);
    for (int i = 0; i < ca.length; i++) ha[i] = generateFunction2(ca[i]);
    return ha;
  }

  public void setHash(Object p) {
    // change the hash function and rehash all K-V pairs with it 
    if (p == null) { 
      hash = (k) -> {return (k.hashCode() & 0x7fffffff) % ma[0]; };
      return;
    }
    if (p instanceof Integer) {
      Integer a = (Integer) p;
      hash = (k) -> { return ((k.hashCode() & 0x7fffffff) % a) % ma[0]; };
      return;
    }      
    if (p instanceof int[]) {
      int[] a = (int[]) p;
      if (a.length > 0) {
        hash = (k) -> { return (a[0] * k.hashCode() & 0x7fffffff) % ma[0]; };
        return;
      } else {
        throw new IllegalArgumentException(
            "setHash: arg is an int array but it's length or its first element is < 1");
      }      
    }  
    if (p instanceof String) {
      String r = (String) p;
      if (r.matches("[0-9]*")) {
        hash = (k) -> { return Integer.parseInt(r); };
        return;
      }
      if (r.equals("distinctEven")) {
        hash = (k) -> { return (2*k.hashCode() & 0x7fffffff) % ma[0]; };
      }      
    }
    if (p instanceof Character) {
      boolean a = false; boolean b = false; int index = -1;
      for (int i = 0; i < 6; i++) {
        if (s != null) {
          if (s.equals("hash"+i)) { a = true; index = i; break; }
          if (s.equals("bash"+i)) { b = true; index = i; break; }
        }
      }
      if (s != null && a) {
        @SuppressWarnings("unchecked")
        Function<Key,Integer>[] ha = generateFunctions((Key[][])box(ca));
        hash = ha[index];
      } else if (s != null && b) {
        @SuppressWarnings("unchecked")
        Function<Key,Integer>[] hb = generateFunctions2((Key[][])box(ca));
        hash = hb[index];
      } else hash = (k) -> { return (k.hashCode() & 0x7fffffff) % ma[0]; };
    } else {
      hash = (k) -> {
        if (pa[0] == null) return (k.hashCode() & 0x7fffffff) % ma[0];
        else if (kclass != null && kclass == Character.class)
          return (pa[0] * (char)k) % ma[0];
        else return (pa[0] * k.hashCode()) % ma[0];
      };
    }
    if (n == 0) return;
    Key[] ka = keys;
    V[] va = vals;
    keys = ofDim(kclass, m); vals = ofDim(V.class,m); n = 0;
    for (int i = 0; i < ka.length; i++) 
      if (ka[i] != null) put(ka[i], va[i].v, va[i].c);
  }
  
  public double alpha() { return new Double(String.format("%1.4f", 1.*n/m)); }
  
  public double searchHitWorstCase() {
    // return the search hit worst case cost assuming uniform probing.
    // from Introduction to Algorithms; Cormen, Leiserson, Rivest, Stein; 
    // 3Ed; 2009; MIT; page 276.
    if (m == 0) return -1;
    double alpha = 1.*n/m;
    return (1./alpha) + ln(1./(1.-alpha));
  }

  public double searchHitAvgCost() {
    // calculate and return the average search hit by sampling.
    if (m == 0 || n == 0 || keys == null || keys.length == 0) return -1;
    if (m < n) throw new LinearProbingHashConfigurationException("m < n");
    int i = 0, j = 0; double sum = 0;
    if (n <= ss) {
      while (i < m && j < n) {
        if (keys[i] != null) {
          zeroProbes();
          get(keys[i]);
          sum += probes();
          j++;
        }
        i++;        
      }
      return sum/j;
    } else {
      // reservoir sampling
      SecureRandom r = new SecureRandom();
      // fill the reservoir
      int[] a = new int[ss];  i = 0; 
      while (i < m && j < ss) {
        if (keys[i] != null) {
          a[j++] = i;
        }
        i++;
      }
      // update the reservoir
      while (i < m  && j < n) {
        int c = r.nextInt(Integer.MAX_VALUE) % (j+1);
        if (keys[i] != null) {
          if (c < ss) a[c] = i;
          j++;
        }
        i++;
      }
      // use reservoir elements to reference keys 
      // to compute search get hit cost
      sum = 0;
      for (i = 0; i < ss; i++) {
          zeroProbes();
          get(keys[a[i]]);
          sum += probes();
      }
      return sum/ss;    
    }
  }
  
  public double searchHitAvgCost2() {
    if (pmap.isEmpty()) return 1;
    int s = pmap.size();
    long o = 0;
    Integer[] a = pmap.keySet().toArray(new Integer[0]);
    for (Integer k : a) o += pmap.get(k);
    return (1.*o + n - s)/n;
  }
  
  public double searchMissWorstCase() {
    // return the search miss worst case cost assuming uniform probing.
    // from Introduction to Algorithms; Cormen, Leiserson, Rivest, Stein; 
    // 3Ed; 2009; MIT; page 274.
    if (m == 0) return -1;
    double alpha = 1.*n/m;
    return (1./(1.-alpha));
  }
  
  public double searchMissAvgCost() {
    // calculate search miss average cost based on the discussion of 
    // Proposition M in the text on page 473.
    double sum = sumAsArithmeticSeries(clusterLengths());
//    for (int i : cl) sum += cl[i]*cl[i];
    return 1 + (1.*(m-n) + sum)/m;
  }
  
  public double searchMissAvgCost2() {
    // calculate search miss average cost based on the formula given
    // in the statement of Proposition M in the text on page 473.
    double alpha = 1.*n/m;
    return (1.+(1./((1.-alpha)*(1.-alpha))))/2;
  }
  
  public int[] clusterLengths() {
    // return an array containing the nonzero cluster lengths in z
    // where clusters are contiguous elements separated by nulls.
    if (keys == null) throw new IllegalArgumentException("clusterLengths: keys is null");
    int i = 0, c = 0; Queue<Integer> q = new Queue<>();
    while (i < keys.length) {
      if (keys[i] ==  null) {
        if (c != 0) { q.enqueue(c); c = 0; }
      } else c++;
      i++;
    }
    if (c != 0) q.enqueue(c);
    return (int[]) unbox(q.toArray(1));
  }
  
  public static double sumAsArithmeticSeries(int[] a) {
    // return the sum of the elements in a where each represents
    // an arithmetic series, i.e. 3 represents {1+2+3}
    if (a == null) throw new IllegalArgumentException("sumofArithmeticSeries: arg is null");
    double sum = 0;
    for (int i : a) sum += (1.*i*(i+1))/2;
    return sum;
  }
  
  public int getSearchHitSampleSize() { return ss; }
  
  public void setSearchHitSampleSize(int x) { ss = x; }

  public int getEquals() { return equals; }
  
  public int getRehashEquals() { return rehashEquals; }
  
  public int getResizeEquals() { return resizeEquals; }
  
  public int probes() { return deleteProbes+getProbes+putProbes; }
  
  public void zeroProbes() { deleteProbes = getProbes = putProbes = 0; }
  
  public int getDeleteProbes() {  return deleteProbes; }
  
  public int getGetProbes() {  return getProbes; }

  public int getPutProbes() {  return putProbes; }

  public int getHash(Key k) { return hash.apply(k) ; };
  
  public int size() {  return n;  }
  
  public int getM() { return m;  }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    return get(key) != null;
  }
  
  public int numberFilled(Predicate<Integer> pred) {
    int sum = 0;
    for (int i = 0; i < keys.length; i++)
      if (keys[i] != null && pred.test(i)) sum++;
    return sum;
  }

  private void resize(int capacity) {
    if (noresize || capacity == m) return;
    int adjcapacity = capacity;
    if (s != null && s.equals("dhash")) {
      if (capacity < m) {
        for (int i = 0; i < primes.length; i++) 
          if (primes[i] > capacity) {
            adjcapacity = primes[i-1]; break;
          }
      } else {
        if (capacity == 22) {
          adjcapacity = 23;
        } else {
          for (int i = 0; i < primes.length; i++) 
            if (primes[i] >= capacity) {
              adjcapacity = primes[i]; break;
            }
        }
      }
    }
    LinearProbingHashSTM<Key, Value> temp;
    if (s != null && !s.equals("dhash")) {
      temp = new LinearProbingHashSTM<>(adjcapacity,"dhash");
    } else if (s != null && !s.equals("dhash2")) {
      temp = new LinearProbingHashSTM<>(adjcapacity,"dhash2");
    } else if (pa[0] != null) {
      temp = new LinearProbingHashSTM<>(adjcapacity, pa[0]);
    } else temp = new LinearProbingHashSTM<>(adjcapacity);
    for (int i = 0; i < m; i++)
      if (keys[i] != null) {
        resizeEquals++;
        temp.put(keys[i], vals[i].v, vals[i].c); 
      }
    resizeEquals++;
    keys = temp.keys; vals = temp.vals; m = temp.m; ma[0] = m;
    deleteProbes += temp.deleteProbes;
    getProbes += temp.getProbes;
    putProbes += temp.putProbes;
    equals += temp.equals;
    rehashEquals += temp.rehashEquals;
    resizeEquals += temp.resizeEquals;
    //System.out.println("  table size adjusted to "+m);
  }
  
  public void put(Key key, Value val) { put(key,val,-1); }

  private void put(Key key, Value val, int c) {
    if (key == null) throw new IllegalArgumentException("first argument to put() is null");
    if (kclass == null) kclass = key.getClass();
    if (val == null) { delete(key); return; }
    if (vclass == null) vclass = val.getClass();
    if (hash == null) {
      if (s != null && !s.equals("dhash") && !s.equals("dhash2")) {
        if (s.matches("[0-9]*")) { 
          hash = (k) -> { return Integer.parseInt(s); }; }
        else if (s.equals("distinctEven")) {
          hash = (k) -> { return (2*k.hashCode() & 0x7fffffff) % ma[0]; }; }
        else if (kclass == Character.class) {
          for (int i = 0; i < 6; i++) {
            if (s.equals("hash"+i)) { 
              @SuppressWarnings("unchecked")
              Function<Key,Integer>[] ha = generateFunctions((Key[][])box(ca));
              hash = ha[i];
              break;
            }
            if (s.equals("bash"+i)) { 
              @SuppressWarnings("unchecked")
              Function<Key,Integer>[] hb = generateFunctions2((Key[][])box(ca));
              hash = hb[i];
              break;
            }
          }
        }
      } else {
        hash = (k) -> {
          if (pa[0] == null) return (k.hashCode() & 0x7fffffff) % ma[0];
          else if (kclass != null && kclass == Character.class)
            return (pa[0] * (char)k) % ma[0];
          else return (pa[0] * k.hashCode()) % ma[0];
        };
      }
    }
    if (keys == null) keys = ofDim(kclass, m);
    if (vals == null) vals = ofDim(V.class, m);
    if (n >= m/2) resize(2*m);
    int i, ptmp = 0;
    if (s != null && s.equals("dhash")) {
      for (i = hash.apply(key); keys[i] != null; i = (i + (key.hashCode()%(m-1))) % m) {
        if (keys[i].equals(key)) {
          equals++;
          if (vals[i] == null) vals[i] = new V(val,1);
          else { vals[i].v = val; vals[i].c++; }
          return;
        }
        equals++;
        ptmp++;
      }
    } else if (s != null && s.equals("dhash2")) {
      i = hash.apply(key);
      int h2 = hash2.apply(key);
      while (keys[i] != null) {
        if (keys[i].equals(key)) {
          equals++;
          ptmp++;
          if (vals[i] == null) vals[i] = new V(val,1);
          else { vals[i].v = val; vals[i].c++; }
          return;
        }
        equals++;
        ptmp++;
        i += h2; i %= m;
      }
    } else {
      for (i = hash.apply(key); keys[i] != null; i = (i + 1) % m) {
        if (keys[i].equals(key)) {
          equals++;
          ptmp++;
          if (vals[i] == null) vals[i] = new V(val,1);
          else { vals[i].v = val; vals[i].c++; }
          return;
        }
        equals++;
        ptmp++;
      } 
    } // pmap is only for keys with #probes (ptmp) > 1
    if (ptmp > 1) pmap.put(identityHashCode(key), ptmp);
    putProbes += ptmp;
    keys[i] = key; 
    if (c > 0) { 
      if (vals[i] == null) vals[i] = new V(val,c);
      else { vals[i].v = val; vals[i].c = c; }
    }
    else if (vals[i] == null) vals[i] = new V(val,1);
    else { vals[i].v = val; vals[i].c = 1; }
    n++; 
  }
  
  public Value get(Key key) { V v = getV(key); return v == null ? null : v.v; }
  
  public int getKeyCount(Key key) { V v = getV(key); return v == null ? 0 : v.c; }

  private V getV(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    if (isEmpty()) throw new NoSuchElementException("LinearProbingHashSTX underflow");
    if (s != null && s.equals("dhash2")) {
      int i = hash.apply(key);
      int h2 = hash2.apply(key);
      while (keys[i] != null) {
        if (keys[i].equals(key)) {
          equals++;
          getProbes++;
          return vals[i]; 
        }
        equals++;
        getProbes++;
        i += h2; i %= m;
      }
    } else if (s != null && s.equals("dhash")) {
      for (int i = hash.apply(key); keys[i] != null; i = (i + (key.hashCode()%(m-1))) % m) {
        if (keys[i].equals(key)) {
          equals++;
          getProbes++;
          return vals[i];
        }
        equals++;
        getProbes++;
      }
    } else {
      for (int i = hash.apply(key); keys[i] != null; i = (i + 1) % m) {
        if (keys[i].equals(key)) {
          equals++;
          getProbes++;
          return vals[i];
        }
        equals++;
        getProbes++;
      }
    }
    equals++;
    getProbes++;
    return null;
  }

  public void delete(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to delete() is null");
    if (!contains(key)) return;
    int i = hash.apply(key), ptmp = 0;
    if (s != null && !s.equals("dhash2")) {
      int h2 = hash2.apply(key);
      while (!key.equals(keys[i])) {
        equals++;
        i += h2; i %= m;
        ptmp++;
      }    
    }  
    if (s != null && !s.equals("dhash")) {
      while (!key.equals(keys[i])) {
        equals++;
        i = (i + (key.hashCode()%(m-1))) % m;
        ptmp++;
      }
    } else {
      while (!key.equals(keys[i])) {
        equals++;
        i = (i + 1) % m;
        ptmp++;
      }
    }
    equals++;
    ptmp++;
    if (ptmp > 1) pmap.remove(identityHashCode(key));
    deleteProbes += ptmp;
    keys[i] = null; vals[i] = null;
    i = (i + 1) % m;
    while (keys[i] != null) {
      rehashEquals++;
      Key keyToRehash = keys[i];  Value valToRehash = vals[i].v; int c = vals[i].c;
      keys[i] = null; vals[i] = null;  n--;
      pmap.remove(identityHashCode(keyToRehash));
      put(keyToRehash, valToRehash, c);
      i = (i + 1) % m;
    }
    rehashEquals++;
    n--;
    if (n > 0 && n <= m/8) resize(m/2);
    assert check();
  }

  public Iterable<Key> keys() {
    if (keys == null) new Queue<Key>();
    return new Queue<Key>(toKeyArray());
  }

  private boolean check() {
    // check that hash table is at most 50% full
    if (m < 2*n) {
      System.err.println("Hash table size m = " + m + "; array size n = " + n);
      return false;
    }
    // check that each key in table can be found by get()
    for (int i = 0; i < m; i++) {
      if (keys[i] == null) continue;
      else if (get(keys[i]) != vals[i].v) {
        System.err.println("get["+keys[i]+"] = "+get(keys[i])+"; vals[i].v = "+vals[i].v);
        return false;
      }
    }
    return true;
  }
  
  public boolean isPrime(int N) {
    if (N < 2) return false;
    for (int i = 2; i*i <= N; i++)
      if (N % i == 0) return false;
    return true;
  }
  
  public int getPrime() {
    //get prime number less than m for hash2 function
    for (int i = m - 1; i >= 1; i--) {
      int f = 0;
      for (int j = 2; j <= (int) sqrt(i); j++)
        if (i % j == 0) f++;
      if (f == 0) return i;
    }
    return 3;
  }

  public Key[] toKeyArray() {
    if (keys == null) return null;
    Key[] ka = ofDim(kclass, m);
    int c = 0;
    for (Key k : keys) if (k != null) ka[c++] = k;
    ka = take(ka,c);
    Arrays.sort(ka); 
    return ka;
  }

  public Key[] toUnsortedKeyArray() {
    if (keys == null) return null;
    Key[] ka = ofDim(kclass, m);
    int c = 0;
    for (Key k : keys) {
      if (k != null) ka[c++] = k;
    }
    ka = take(ka,c);
    return ka;
  }
  
  public Key[] getKeys() { return keys; }
  
  public V[] toVArray() {
    if (vals == null) return null;
    V[] va = ofDim(V.class, m);
    int c = 0;
    for (V v : vals) {
      if (v != null) va[c++] = v;
    }
    va = take(va,c);
    return va;
  }

  public Value[] toValArray() {
    if (vals == null) return null;
    Value[] va = ofDim(vclass, m);
    int c = 0;
    for (V v : vals) {
      if (v != null) va[c++] = v.v;
    }
    va = take(va,c);
    Arrays.sort(va); 
    return va;
  }

  public Value[] toUnsortedValArray() {
    if (vals == null) return null;
    Value[] va = ofDim(vclass, m);
    int c = 0;
    for (V v : vals) {
      if (v != null) va[c++] = v.v;
    }
    va = take(va,c);
    return va;
  }
  
  public V[] getVals() { return vals; }
  
  public String keysToString() {
    return arrayToString(keys,9000,0,0);
  }

  public void showKeys() {
    if (keys == null || keys.length == 0) { System.out.println("empty"); return; }
    for (Key k : keys) if (k != null) System.out.print(k+" ");
    System.out.println();
  }

  public void show() {
    if (keys == null || keys.length == 0) { System.out.println("empty"); return; }
    for (int i = 0; i < keys.length; i++) 
      if (keys[i] != null) System.out.print(keys[i]+":"+vals[i]+" ");
    System.out.println();
  }
 
  public void showget() {
    if (keys == null || keys.length == 0) { System.out.println("empty"); return; }
    for (int i = 0; i < keys.length; i++) 
      if (keys[i] != null) System.out.print(keys[i]+":"+get(keys[i])+" ");
    System.out.println();
  }
  
  public void printKeyArray() { par(keys); }
  
  public void printValArray() { par(vals); }
  
  public void printPmap() { System.out.println(pmap); }
  
  public int pMapSize() { return pmap.size(); }
  
  @Override public String toString() {
    if (keys == null || keys.length == 0) return "{}";
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (int i = 0; i < keys.length; i++) 
      if (keys[i] != null) sb.append(keys[i]+":"+vals[i]+",");
    return sb.replace(sb.length()-1,sb.length(),"}").toString();
  }
 
  public static void main(String[] args) { 

    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0,sa.length);
    LinearProbingHashSTM<Character, Integer> ht = new LinearProbingHashSTM<>(ca,ia);
    System.out.print("ht = "+ht+"\n");
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("put('E', 11)"); ht.put('E', 11); 
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("put('E', 12)"); ht.put('E', 12);
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));
    System.out.println("ht.delete('E')"); ht.delete('E');
    System.out.print("ht = "+ht+"\n"); 
    System.out.println("get('E') = "+ht.get('E'));
    System.out.println("count of 'E' = "+ht.getKeyCount('E'));

  }
}
