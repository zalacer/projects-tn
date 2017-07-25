package st;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

import v.Tuple2;

public class CuckooHashST<Key, Value> {
  // tables are defined to include keys and values and are implemented
  // using multiple separate arrays of each
  private static final long INITCAPACITY = 12;
  private static final float LOADFACTOR = 0.9f; // default table load factor, N/M
  private static final int NUMBEROFTABLES = 3; // default number of tables
  private static final int MINTABLES = 2;
  private static final int MAXTABLESIZE = 2147483579;
  private static final int MAXPRIME = 2147483587;

  private int NT = NUMBEROFTABLES; // default number of tables
  private long N = 0; // total number of entries
  private long M; // overall tables size
  private int[] na; // per table number of entries          
  private int m; // size per table
  private Tuple2<Key,Value>[][] entries;
  private Function<Key,Integer>[] ha;
  private Class<?> kclass = null; // class of Key
  private Class<?> vclass = null; // class of Value
  private SecureRandom r = new SecureRandom();
  private Boolean noresize = false;
  private boolean lock = false; // write lock for rehash()
  private int probes = 0;
  
  public CuckooHashST() { this(INITCAPACITY); }
  
  public CuckooHashST(boolean noresize) { this(INITCAPACITY,noresize); }

  public CuckooHashST(long capacity) {
    M = capacity + NT - capacity % NT;
    long x = M/NT;
    if (x > Integer.MAX_VALUE) throw new IllegalArgumentException();
    m = (int) x;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m); 
    ha = generateHashFuns();
  }
  
  public CuckooHashST(int nt) {
    if (nt == NT || nt < MINTABLES) {
      M = INITCAPACITY + NT - INITCAPACITY % NT;
    } else {
      NT = nt;
      int capacity = NT*4;
      M = capacity + NT - capacity % NT;
    }
    m = 4;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m);
    ha = generateHashFuns();
    assert ha.length == NT;
  }
  
  public CuckooHashST(long capacity, int nt) {
    if (nt == NT || nt < MINTABLES) {
      if (capacity < 0)  M = INITCAPACITY + NT - INITCAPACITY % NT;
      else M = capacity + NT - capacity % NT;
    } else {
      this.NT = nt;
      capacity = capacity < 0 ? INITCAPACITY : capacity;
      M = capacity + NT - capacity % NT;
    }
    long x = M/NT;
    if (x > Integer.MAX_VALUE) throw new IllegalArgumentException();
    m = (int) x;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m); 
    ha = generateHashFuns();
  }
  
  public CuckooHashST(long capacity, boolean noresize) {
    if (capacity < 0) throw new IllegalArgumentException();
    M = capacity + NT - capacity % NT;
    long x = M/NT;
    if (x > Integer.MAX_VALUE) throw new IllegalArgumentException();
    m = (int) x;
    na = new int[NT];
    entries = ofDim(Tuple2.class, NT, m); 
    ha = generateHashFuns();
    this.noresize = noresize;
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

  public int getPrime() {
    //return first prime number greater than m for use in hash funs.
    for (int i = m + 1; i < Integer.MAX_VALUE; i++) {
      int f = 0;
      for (int j = 2; j <= (int) sqrt(i); j++)
        if (i % j == 0) f++;
      if (f == 0) return i;
    }
    return MAXPRIME;
  }

  public long size() { return N; }

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
  
  public long M() { return M; }
  
  public long N() { return N; }
  
  public int NT() { return NT; }
  
  public int numberOfTables() { return NT; }
  
  public int probes() { return probes; }
  
  public void zeroProbes() { probes = 0; }
  
  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException("argument to contains() is null");
    return get(key) != null;
  }

  private void resize(long capacity) {
    // resizes all entries arrays to least M > capacity && M % NT == 0.
    if ((noresize == true || m >= MAXTABLESIZE) && capacity != M) return;
    CuckooHashST<Key,Value> tmp;
    if (NT != NUMBEROFTABLES) tmp = new CuckooHashST<Key,Value>(capacity, NT);
    else tmp = new CuckooHashST<Key,Value>(capacity);
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
  
  public void rehash() { resize(M); }

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
    int j; long sz = size();
    Tuple2<Key,Value> en = new Tuple2<>(key,val);
    while (true) {      
      for (long t = 0; t < sz + 1; ++t) {
        j = (int)(t % NT);
        int h = ha[j].apply(en._1);
        Tuple2<Key,Value> e = entries[j][h];
        entries[j][h] = en;      
        if (e == null || e._1 == null) { na[j]++; N++; return; }
        en = e;        
      }
      rehash();
    }
  }

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
    if (N > Integer.MAX_VALUE) throw new ArrayIndexOutOfBoundsException(
        "toEntryArray: N > Integer.MAX_VALUE : use toEntryArrays()");
    int X = (int)N;
    Tuple2<Key,Value>[] ea = ofDim(Tuple2.class,X); int c = 0;
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
  
  public Tuple2<Key,Value>[][] toEntryArrays() {
    if (N == 0) return null;
    Tuple2<Key,Value>[][] ea = ofDim(Tuple2.class,NT,0); 
    for (int i = 0; i < NT; i++) ea[i] = ofDim(Tuple2.class,na[i]);
    for (int i = 0; i < entries.length; i++) {
      int c = 0;
      for (int j = 0; j < entries[i].length; j++) {
        if (entries[i][j] != null) ea[i][c++] = entries[i][j];
      }
    }
    return ea;  
  }
  
  @SuppressWarnings("unchecked")
  public Key[] toKeyArray() {
    if (N == 0) return null;
    if (N > Integer.MAX_VALUE) throw new ArrayIndexOutOfBoundsException(
        "toKeyArray: N > Integer.MAX_VALUE : use toKeyArrays()");
    int X = (int)N;
    Key[] ka = ofDim(kclass,X); int c = 0;
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
  
  public Key[][] toKeyArrays() {
    if (N == 0) return null;
    Key[][] ka = ofDim(kclass,NT,0); 
    for (int i = 0; i < NT; i++) ka[i] = ofDim(kclass,na[i]);
    for (int i = 0; i < entries.length; i++) {
      int c = 0;
      for (int j = 0; j < entries[i].length; j++) {
        if (entries[i][j] != null) ka[i][c++] = entries[i][j]._1;
      }
    }
    return ka;  
  }
  
  public Value[] toValueArray() {
    if (N == 0) return null;
    if (N > Integer.MAX_VALUE) throw new ArrayIndexOutOfBoundsException(
        "toValueArray: N > Integer.MAX_VALUE : use toValueArrays()");
    int X = (int)N;
    Tuple2<Key,Value>[] ea = ofDim(Tuple2.class,X); int c = 0;
    for (int i = 0; i < entries.length; i++)
      for (int j = 0; j < entries[i].length; j++)
        if (entries[i][j] != null) ea[c++] = entries[i][j];
    Value[] va = ofDim(vclass,ea.length); c = 0;    
    for (int i = 0; i < ea.length; i++)
        va[c++] = ea[i]._2;
    return va;
  }
  
  public Value[][] toValueArrays() {
    if (N == 0) return null;
    Value[][] va = ofDim(vclass,NT,0); 
    for (int i = 0; i < NT; i++) va[i] = ofDim(vclass,na[i]);
    for (int i = 0; i < entries.length; i++) {
      int c = 0;
      for (int j = 0; j < entries[i].length; j++) {
        if (entries[i][j] != null) va[i][c++] = entries[i][j]._2;
      }
    }
    return va;  
  }
  
  @SuppressWarnings("unchecked")
  private <X> Iterator<X> itx(Iterator<X>[] ita) {
    // return an iterator of X spanning the iterators of X in ita.
    if (ita == null || ita.length == 0) 
      return (Iterator<X>)Arrays.stream(ofDim(Iterator.class,0)).iterator();
    return new Iterator<X>() {
      private int i = 0;
      public boolean hasNext() {
        while ( i < ita.length && !ita[i].hasNext()) i++;
        return i < ita.length;
      }
      public X next() {
        while ( i < ita.length && !ita[i].hasNext()) i++;
        return ita[i].next();
      }
    };
  }

  public Iterable<Tuple2<Key,Value>> entries() {
    if (N <= Integer.MAX_VALUE)
      return new Iterable<Tuple2<Key,Value>>() {
        public Iterator<Tuple2<Key,Value>> iterator() {
          return Arrays.stream(toEntryArray()).iterator();
        }
      };
    Tuple2<Key,Value>[][] ea = toEntryArrays();
    Iterator<Tuple2<Key,Value>>[] ita = ofDim(Iterator.class,ea.length);
    for (int i = 0; i < ea.length; i++) ita[i] = Arrays.stream(ea[i]).iterator();
    return new Iterable<Tuple2<Key,Value>>() {
      public Iterator<Tuple2<Key,Value>> iterator() { return itx(ita); }
    };    
  } 
  
  public Iterable<Key> keys() {
    if (N <= Integer.MAX_VALUE)
      return new Iterable<Key>() {
        public Iterator<Key> iterator() {
          return Arrays.stream(toKeyArray()).iterator();
        }
      };
    Key[][] ka = toKeyArrays();
    Iterator<Key>[] ita = ofDim(kclass,ka.length);
    for (int i = 0; i < ka.length; i++) ita[i] = Arrays.stream(ka[i]).iterator();
    return new Iterable<Key>() {
      public Iterator<Key> iterator() { return itx(ita); }
    };     
  }
  
  public Iterable<Value> values() {
    if (N <= Integer.MAX_VALUE)
      return new Iterable<Value>() {
        public Iterator<Value> iterator() {
          return Arrays.stream(toValueArray()).iterator();
        }
      };
    Value[][] va = toValueArrays();
    Iterator<Value>[] ita = ofDim(vclass,va.length);
    for (int i = 0; i < va.length; i++) ita[i] = Arrays.stream(va[i]).iterator();
    return new Iterable<Value>() {
      public Iterator<Value> iterator() { return itx(ita); }
    };   
  }
  
  public Iterator<Tuple2<Key,Value>> entryIterator() {
    return entries().iterator(); 
  }
  
  public Iterator<Key> keyIterator() { return keys().iterator(); }
  
  public Iterator<Value> valueIterator() { return values().iterator(); }
  
  public boolean check() {
    boolean check = true;
    // check that hash table is under 100.*alpha()% full
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

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (M ^ (M >>> 32));
    result = prime * result + (int) (N ^ (N >>> 32));
    result = prime * result + NT;
    result = prime * result + Arrays.deepHashCode(entries);
    result = prime * result + ((kclass == null) ? 0 : kclass.hashCode());
    result = prime * result + ((vclass == null) ? 0 : vclass.hashCode());
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" }) 
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CuckooHashST other = (CuckooHashST) obj;
    if (M != other.M)
      return false;
    if (N != other.N)
      return false;
    if (NT != other.NT)
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
    if (N > 0) {
      Iterator<Tuple2<Key,Value>> it = entryIterator();
      while (it.hasNext()) {
        Tuple2<Key,Value> e = it.next();
        if (!other.get(e._1).equals(e._2)) return false;
      }
    }
    return true;
  }

  public static void main(String[] args) { 
    
    SecureRandom r = new SecureRandom(); int c = 0, n = 1000000;
    CuckooHashST<Integer, Integer> h = new CuckooHashST<Integer, Integer>();
    while (c < n) {
      int M = (int)h.M();
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
    Iterator<Tuple2<Integer,Integer>> it = h.entryIterator();
    c = 0;
    while (it.hasNext() && c < 15) { System.out.println(it.next()); c++; }
    
 
  }
}
