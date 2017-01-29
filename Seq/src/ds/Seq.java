package ds;

import static java.lang.System.identityHashCode;
import static java.lang.reflect.Array.newInstance;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Comparator.nullsLast;
import static v.ArrayUtils.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.security.SecureRandom;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import exceptions.InappropriateSeqException;
import exceptions.SeqIndexOutOfBoundsException;
import exceptions.SeqNestingTooHighException;
import v.ArrayUtils;
import v.ArrayUtils.B1;
import v.ArrayUtils.C1;
import v.ArrayUtils.Combinator;
import v.ArrayUtils.PentFunction;
import v.ArrayUtils.Permutator;
import v.ArrayUtils.QuadFunction;
import v.ArrayUtils.Sliding;
import v.ArrayUtils.TriFunction;
import v.Tuple2;
import v.Tuple3;

public class Seq<T> extends AbstractList<T> 
        implements List<T>, RandomAccess, Serializable, Cloneable {
  private static final long serialVersionUID = 1L;
  private static final int DEFAULTSIZE = 17;
  @SuppressWarnings("unchecked")
  private T[] a = (T[]) new Object[0];
  private int N = 0; // number of items and next index
  private int capacity = 0; // length of a when it has one
  private Class<?> rclass = null; // rootElementType, usually set by analyzeNesting()
  private Class<?> tclass = null; // class of T, determined when a non-null element is added
  private int dim = -1; // dimension, set by analyzeNesting()

  public Seq() { capacity = DEFAULTSIZE; }

  public Seq(int i) { capacity = i <= 0 ? DEFAULTSIZE : i; }

  @SafeVarargs
  public Seq(T...z) {
    if (z == null) { this.capacity = DEFAULTSIZE; return; }
    tclass = z.getClass().getComponentType();
    if (!Seq.class.isAssignableFrom(tclass)) { rclass = tclass; dim = 1; }
    else analyzeNesting();
    a = z.clone();
    N = capacity = z.length;
  }

  public Seq(Collection<? extends T> c) {
    if (c == null) { this.capacity = DEFAULTSIZE; return; }
    for (T t : c) add(t);
  }

  @SuppressWarnings("unchecked")
  public boolean add(T t) {
    // Add t to the end of the list
    // return true if this Seq changed else return false
    // except for signature identical to addObject(Object t)
    if (t != null && tclass == null) {
      tclass = t.getClass();
      if (!(t instanceof Seq)) { rclass = tclass; dim = 1; }
      else analyzeNesting();
    }
    T[] x = null;
    if (a == null) {
      int len = capacity > 0 ? capacity : DEFAULTSIZE;
      x = tclass != null ? ArrayUtils.ofDim(tclass,len) : (T[]) (new Object[len]);
      a = x;
    } else if (tclass != null && a.getClass().getComponentType() != tclass) {
      x = ArrayUtils.ofDim(tclass, a.length);
      for (int i = 0; i < N; i++) x[i] = a[i];
      a = x; 
    }
    if (a.length == 0) resize(4);
    else if (N == a.length) resize(2*a.length);  
    a[N++] = t;
    return true;
  }
  
  public void addBack(T t) { add(t); } 
  
  public void addFront(T t) { add(0,t); } 
  
  @SuppressWarnings("unchecked")
  public boolean addObject(Object t) { 
    // Add t to the end of the list
    // return true if this Seq changed else return false
    // for toNestedList() that's for tabulate()
    // except for signature identical to add(T t)
    if (t != null && tclass == null) {
      tclass = t.getClass();
      if (!(t instanceof Seq)) { rclass = tclass; dim = 1; }
      else analyzeNesting();
    }
    T[] x = null;
    if (a == null) {
      int len = capacity > 0 ? capacity : DEFAULTSIZE;
      x = tclass != null ? ArrayUtils.ofDim(tclass,len) : (T[]) (new Object[len]);
      a = x;
    } else if (tclass != null && a.getClass().getComponentType() != tclass) {
      x = ArrayUtils.ofDim(tclass, a.length);
      for (int i = 0; i < N; i++) x[i] = a[i];
      a = x; 
    }
    if (a.length == 0) resize(4);
    else if (N == a.length) resize(2*a.length);  
    a[N++] = (T)t;
    return true;
  }

  public void add(int index, T t) { 
    // inserts t at index
    if (index < 0) throw new IllegalArgumentException("add: index < 0");
    if (index > N-1) throw new IndexOutOfBoundsException("add: index > N-1");
    if (N == a.length) resize(2*a.length);
    if (N == 0) { add(t); return; }
    if (t != null && tclass == null) {
      tclass = t.getClass();
      if (!(t instanceof Seq)) { rclass = tclass; dim = 1; }
      else analyzeNesting();
    }
    for (int i = N-1; i >= index; i--) a[i+1] = a[i];   
    a[index] = t;
    N++;
  }

  public boolean addAll(Collection<? extends T> c) {
    // add the elements of c to the end of this
    // return true if this Seq changed else return false
    if (c == null) return false;
    boolean changed = false;
    for (T x : c) { add(x); changed = true; }
    return changed;
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    // add the elements of c starting a index to the end of this
    // return true if this Seq changed else return false
    if (c == null || index > c.size()-1) return false;
    int i = 0; boolean changed = false;
    for (T x : c) if (i < index) i++; else { add(x); changed = true; }
    return changed;
  }

  public boolean addArray(T[] ta) { 
    // add the elements of ta to the end of this
    // return true if this Seq changed else return false
    if (ta == null) return false;
    //    getClasses(ta);
    if (tclass == null) tclass = ta.getClass().getComponentType();
    T[] x = null;
    if (a == null) {
      int len = capacity > 0 ? capacity : DEFAULTSIZE;
      x = ArrayUtils.ofDim(tclass,len);
      a = x;
    } else if (a.getClass().getComponentType() != tclass) {
      x = ArrayUtils.ofDim(tclass, a.length);
      for (int i = 0; i < N; i++) x[i] = a[i];
      a = x; 
    }
    boolean changed = false;
    for (T t : ta) { add(t); changed = true; }
    return changed;
  }

  public StringBuilder addString(StringBuilder sb) {
    // append all elements of a to sb and then return sb
    sb = new StringBuilder();
    for (int i = 0; i < N; i++) sb.append(a[i]);
    return sb;
  }

  public StringBuilder addString(StringBuilder sb, String sep) {
    // append all elements of a separated by sep to sb and then return sb
    if (sb == null) sb = new StringBuilder();
    if (isEmpty()) return sb;
    for (int i = 0; i < N - 1; i++) sb.append(a[i] + sep);
    sb.append(a[N - 1]);
    return sb;
  }

  public StringBuilder addString(StringBuilder sb, String start, 
      String sep, String end) {
    // append all elements of this to sb using start, separator and end strings 
    // and return sb
    if (sb == null) sb = new StringBuilder();
    sb.append(start);
    if (isEmpty()) { sb.append(end); return sb; }
    for (int i = 0; i < N - 1; i++) sb.append(a[i] + sep);
    sb.append(a[N - 1] + end);
    return sb;
  }

  public <R> R aggregate(R z, BiFunction<R, T, R> accumulator, 
      BinaryOperator<R> combiner, int partitionSize) {
    // aggregate elements of this with accumulator, combine intermediate results
    // for each partition with combiner and return the final result. 
    // aggregation for each partition is initialized with z, typically the 
    // neutral element for the accumulator and done in a separate thread.
    if (isEmpty()) throw new InappropriateSeqException("allNull: Seq is empty");
    if (z == null) throw new IllegalArgumentException("aggregate: z is null");
    if (accumulator == null) throw new IllegalArgumentException("aggregate: accumulator is null");
    if (combiner == null) throw new IllegalArgumentException("aggregate: combiner is null");
    if (partitionSize < 1) throw new IllegalArgumentException("aggregate: partitionSize is < 1");

    int size = partitionSize;
    int parts = N % size == 0 ? N / size : 1 + N / size;

    T[][] partitions = ArrayUtils.ofDim(tclass, parts, size);

    for (int i = 0; i < N; i++) {
      for (int j = 0; j < size; j++) {
        if (i + j < N) {
          partitions[i / size][j] = a[i + j];
        } else break;
      }
      i += size - 1; // the loop adds 1 on each iteration
    }
    R[] r = ArrayUtils.ofDim(z.getClass(), parts);
    for (int i = 0; i < parts; i++) r[i] = z;

    Thread[] threads = new Thread[parts];

    for (int i = 0; i < parts; i++) {
      int[] g = new int[] { i }; //encapsulate i to access it in lambda
      threads[i] = new Thread(() -> {
        for (int j = 0; j < size; j++)
          if (partitions[g[0]][j] != null)
            r[g[0]] = accumulator.apply(r[g[0]], partitions[g[0]][j]);
      });
    }

    for (Thread t : threads) t.start();
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    R result = z;
    for (int i = 0; i < r.length; i++)
      if (r[i] != null)
        result = combiner.apply(result, r[i]);
    return result;
  }

  public boolean allNull() { return anyNonNull() == false; }
  
  public int analyzeNesting() {
    // set dim and the rootElementClass rclass and return the depth of nesting 
    // if possible else return -1. if rclass isn't determined that means all 
    // elements at a level are null
    if (isEmpty()) return -1;
    int[] p = {0}; Class<?> sclass = rclass; rclass = null; int sim = dim;
    dim = -1;
    analyzeNesting(this, 0, p);
    dim = (p[0] > sim) ? p[0] : sim;
    if ( rclass == null && sclass != null) rclass = sclass;
    return dim;
  }
  
  private void analyzeNesting(Object o, int dim, int[] p) {
    if (o instanceof Seq) dim++;
    else { rclass = o.getClass(); return; }
    for (Object e : (Seq<?>) o) if (e != null) analyzeNesting(e,dim,p);
    if (dim > p[0]) p[0] = dim;
  }

  
  public boolean anyNonNull() {
    if (isEmpty()) return false;
    for (int i = 0; i < N; i++) if (a[i] != null) return true;
    return false;
  }

  public T apply(int i) { return get(i); }
  
  @SafeVarargs
  public static <U> Seq<U> apply(U... ua) {
    // return a Seq<U> with the elements of ua
    Seq<U> x = new Seq<U>();
    if  (ua == null) return x;
    x.addArray(ua);
    return x;
  }

  public void append(T t) { add(t); } 

  public int capacity() { return capacity; }

  public char charAt(int n) {
    if (isEmpty()) throw new InappropriateSeqException("chars: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "this is not a Seq<Character>");
    if (n > N) throw new SeqIndexOutOfBoundsException("n > N");
    return ((Character)a[n]).charValue();
  }

  public IntStream chars() {
    // return an IntStream from this iff it's a Seq<Character>
    if (isEmpty()) throw new InappropriateSeqException("chars: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "this is not a Seq<Character>");
    return (new String((char[])unbox(to()))).chars();
  }

  public void clear() {
    if (isEmpty()) return;
    for (int i = 0; i < a.length; i++) a[i] = null;
    N = 0; 
  }

  @SuppressWarnings("unchecked")
  public Seq<T> clone() {
    // return a clone of this by serializing and deserializing
    Object x = null;
    byte[] oBytes = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try {
      out = new ObjectOutputStream(bos); // implements ObjectOutput
      out.writeObject(this); 
      oBytes = bos.toByteArray(); // this now serialized to a byte array 
    } catch (IOException e) { return clone2(); } 
    finally {
      try {
        if (out != null) out.close();
      } catch (IOException ex) { return clone2(); }
      try {
        bos.close();
      } catch (IOException ex) { return clone2(); }
    }
    // deserialization from byte[]
    ByteArrayInputStream bis = new ByteArrayInputStream(oBytes);
    ObjectInput in = null;
    try {
      in = new ObjectInputStream(bis); // implements ObjectInput
      x = in.readObject(); // oBytes now deserialized to x
    } catch (ClassNotFoundException | IOException e) { return clone2(); } 
    finally {
      try {
        if (in != null) in.close();
      } catch (IOException ex) { return clone2(); }
      try {
        bis.close();
      } catch (IOException ex) { return clone2(); }
    }
    // return cloned object
    return (Seq<T>) x;
  }

  private Seq<T> clone2() { 
    Seq<T> s = new Seq<T>();
    s.a = a.clone();
    s.N = N;
    s.capacity = capacity; 
    s.tclass = tclass; 
    s.dim = dim; 
    return s; 
  }

  IntStream codePoints() {
    if (isEmpty()) throw new InappropriateSeqException("codePoints: Seq is empty");
    if (tclass != Character.class)  throw new InappropriateSeqException(
        "codePoints: Seq element class isn't Character");
    return java.nio.CharBuffer.wrap((char[])unbox(toArray())).codePoints();
  }

  public <R> Seq<R> collect(Predicate<T> p, Function<T, R> f) {
    // returns a Seq<R> built by applying f only to elements of this satisfying p and
    // assuming null elements should not be processed. this is meant to simulate the 
    // effect of applying a partial function, namely one partially defined over its domain,
    // however f is presumed to apply to all of its domain.
    Seq<R> r = new Seq<R>();
    if (isEmpty()) return r;   
    for (T t : this) if (p.test(t)) r.add(f.apply(t));
    return r;
  }
  
  public static <U, V extends U> void copy(Seq<V> src, int srcPos, Seq<U> dest, 
      int destPos, int length) { // like System.arrayCopy for Seq
    if (src == null) throw new  NullPointerException("copy: src is null");
    if (dest == null) throw new  NullPointerException("copy: dest is null");
    if (srcPos < 0) throw new  IndexOutOfBoundsException ("copy: srcPos < 0");
    if (destPos < 0) throw new  IndexOutOfBoundsException ("copy: destPos < 0");
    if (length < 0) throw new  IndexOutOfBoundsException ("copy: length < 0");
    int Ns = src.size(), Nd = dest.size();
    if (srcPos+length > Ns) throw new  IndexOutOfBoundsException (
        "copy: srcPos+length > src.size()");
    if (destPos+length > Nd) throw new  IndexOutOfBoundsException (
        "copy: destPos+length > dest.size()");
    for (int i = 0; i < length; i++) dest.update(destPos+i, src.get(srcPos+i));
  }

  public Iterator<Seq<T>> combinations(int n) {
    if (isEmpty()) throw new InappropriateSeqException("combinations: Seq is empty");
    if (n <= 0 || n > size()) throw new IllegalArgumentException(
        "combinations: " + "int n isn't between 0 and this Seq size inclusive");
    Iterator<T[]> it = new Combinator<T>(to(), n).iterator();
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };
  }

  public Stream<Seq<T>> combinationStream(int n) {
    if (isEmpty()) throw new InappropriateSeqException("combinationStream: Seq is empty");
    if (n <= 0 || n > size()) throw new IllegalArgumentException(
        "combinationStream: " + "int n isn't between 0 and this Seq size inclusive");
    Iterator<Seq<T>> it = combinations(n);
    BigInteger nc = numberOfCombinations(a.length, n);
    double len = nc.doubleValue(); long lim = 0; boolean infinite = false;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) infinite = true;
    else lim = (long) len;
    if (!infinite) return Stream.generate(() -> it.next()).limit(lim);
    return Stream.generate(() -> it.next());
  }
  
  @SafeVarargs
  public static <U> Seq<U> concat(Seq<U>... sa) {
    // return a Seq<U> with the concatenation of all Seq<U>s in s
    Seq<U> x = new Seq<U>();
    if (sa == null) return x;
    for (Seq<U> s : sa) x.addAll(s);
    return x;  
  }

  public boolean contains(Object o) {
    if (isEmpty()) return false;
    if (tclass != null && o != null && !tclass.isAssignableFrom(o.getClass())) return false;
    int i = -1;
    while (++i < N) {
      T u = a[i];
      if (u == o || u != null && u.equals(o)) return true;
    }
    return false;
  }

  public boolean containsAll(Collection<?> c) {
    // required for Collection interface conformance
    for (Object x : c) if (!contains(x)) return false;
    return true;
  }

  @SafeVarargs
  public final boolean containsSlice(T... t) {
    // if this contains the elements of t as a slice return true else return false
    if (isEmpty() || t == null || t.length == 0 || t.length > N) return false;
    if (t.length == N) return Arrays.equals(toArray(), t);
    int tlen = t.length;
    LOOP: 
      for (int i = 0; i < N - tlen + 1; i++) {
        for (int j = 0; j < tlen; j++) {
          if (!(a[i+j] == null && t[j] == null || a[i+j] != null && a[i+j].equals(t[j])))
            continue LOOP;
        }
        return true;
      }
    return false;
  }

  public <R> boolean corresponds(Seq<R> s, BiPredicate<T,R> p) {
    // return true if every element of this relates to the corresponding
    // element of s by satisfying b else return false
    if (s == null) throw new IllegalArgumentException("count: Seq<R> s is null");
    if (N > s.N) return false;
    T[] ta = to(); R[] ra = s.to();
    for (int i = 0; i < N; i++) if (!p.test(ta[i], ra[i])) return false;
    return true;
  }

  public int count(Predicate<T> p) {
    // return the number of elements in this that satisfy p
    if (isEmpty()) return 0;
    if (p == null) throw new IllegalArgumentException("count: Predicate is null");
    int c = 0;
    for (T t : this) if (p.test(t)) c++;
    return c;
  }
  
  public T delete() { // remove and return the last element
    if (isEmpty()) throw new NoSuchElementException("Seq underflow");
    T t = (T) a[--N];
    a[N] = null;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }

  public T delete(int i) { return deleteAtIndex(i); }

  public boolean delete(Object o) { return deleteObject(o); }
  
  public boolean deleteAll(T t) { 
    // remove all occurrences of t
    if (isEmpty()) throw new NoSuchElementException("Seq underflow");
    int i = -1; boolean found = false;
    while (++i < N) {
      T x = a[i];
      if (x == t || x != null && x.equals(t)) { delete(i); found = true; }
    }
    return found;
  }

  public boolean deleteAll(Collection<?> c) {
    if (c == null) return true;
    for (Object o : c) remove(o);
    return true;
  }

  public T deleteAtIndex(int i) {
    if (isEmpty()) throw new NoSuchElementException("deleteAtIndex: Seq underflow");
    if (i < 0 || i > N-1) throw new IndexOutOfBoundsException(
        "deleteAtIndex: index i is out of bounds"); 
    T t = (T) a[i];
    a[i] = null;
    int j = i;
    while (j < N-1) a[j] = a[++j];
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }
  
  public boolean deleteObject(Object o) {
    // remove the first occurrence of o
    if (isEmpty() || tclass != null && o != null && !tclass.isAssignableFrom(o.getClass())) 
      return false;
    int i = -1;
    while (++i < N) {
      T x = a[i];
      if (x == o || x != null && x.equals(o)) { delete(i); return true; }
    }
    return false;
  }

  public T deleteBack() { return delete(); }
  
  public boolean deleteIf(Predicate<? super T> p) {
    // remove all elements in this satisfying p
    // return true iff any element was removed else return false
    if (isEmpty()) return false; boolean change = false;
    for (int i = 0; i < N; i++) if (p.test(a[i])) { deleteAtIndex(i); change = true; }
    return change;
  }
  
  public void deleteRange(int start, int end) {
    // remove all elements from start to end-1
    if (isEmpty() || end-1 <= start || end < 1 || start > N-1) return;
    if (start < 0) start = 0;  if (end > N) end = N;
    int len = N - end + start;
    System.arraycopy(a, end, a, start, N-end);
    for (int i = len; i < N; i++) a[i] = null;
    N = len;
  }
  
  public T deleteFront() { return delete(0); }

  public Seq<T> diff(Seq<T> x) {
    // returns the multiset difference between this and x and that includes elements
    // occurring only in this plus those occuring in both repeated by the number of 
    // appearences in this minus those in x down to zero and excluding all elements 
    // occuring only in x. nulls are included as valid element values
    if (x == null) throw new IllegalArgumentException("diff: Seq x is null");
    if (isEmpty()) return new Seq<T>();
    if (x.isEmpty()) return clone();
    T[] b = x.a, c = ArrayUtils.ofDim(tclass,N);
    Map<T, Integer> map = new HashMap<>();
    for (int i = 0; i < x.N; i++) map.merge(b[i], 1, Integer::sum);
    int cindex = 0;
    for (int i = 0; i < N; i++)
      if (map.containsKey(a[i])) {
        if (map.get(a[i]) > 0) map.put(a[i], map.get(a[i]) - 1);
        else c[cindex++] = a[i];
      } else c[cindex++] = a[i];
    return new Seq<T>(ArrayUtils.take(c, cindex));
  }

  public int dim() { return dim; }

  public Seq<T> distinct() { return new Seq<T>(new HashSet<>(this)); }

  public Seq<T> drop(int n) {
    // return a Seq<T> without the 1st n elements of this
    if (isEmpty()) throw new InappropriateSeqException("drop: Seq is empty");
    if (n <= 0) return new Seq<T>(copyOf(a,N));
    if (n >= N) return new Seq<T>();
    return new Seq<T>(copyOfRange(a,n,N));
  }

  public Seq<T> dropRight(int n) {
    // returns an Seq<T> without the last n elements of this
    if (isEmpty()) throw new InappropriateSeqException("dropRight: Seq is empty");
    if (n <= 0) return new Seq<T>(copyOf(a,N));
    if (n >= N) return new Seq<T>(copyOf(a,0));
    return new Seq<T>(copyOfRange(a,0,N-n));
  }

  public Seq<T> dropWhile(Predicate<T> p) {
    // return a Seq<T> without the longest prefix of elements that satisfy p
    if (isEmpty()) throw new InappropriateSeqException("dropWhile: Seq is empty");
    if (p == null) throw new IllegalArgumentException("dropWhile: Predicate is null");
    if (isEmpty()) return new Seq<T>(a.clone()); 
    int bindex = 0;
    for (int i = 0; i < N; i++)
      if (p.test(a[i])) bindex++; else break;
    return new Seq<T>(copyOfRange(a,bindex,N));
  }
  
  public static <U> Seq<U> empty() {
    // return an empty Seq<U>
    return new Seq<U>();
  }

  public boolean endsWith(Seq<T> s) {
    // return true if this ends with the elments in s else return false
    if (s == null) throw new IllegalArgumentException("endsWith: Seq<T> s is null");
    if (s.isEmpty()) throw new IllegalArgumentException("endsWith: Seq<T> s is empty");
    if (s.N > N) return false;
    return Arrays.deepEquals(s.to(), ArrayUtils.takeRight(to(),s.size()));
  }

  public boolean exists(Predicate<? super T> p) {
    // returns whether or not this contains an element satisfying p
    if (isEmpty()) throw new InappropriateSeqException("exists: Seq is empty");
    if (p == null) throw new IllegalArgumentException("exists: Predicate is null");
    for (int i = 0; i < N; i++) if (p.test(a[i])) return true;
    return false;
  }
  
  public static <U> Seq<U> fill(int n, Supplier<U> sp) {
    // return a 1D Seq of size n filled with the result of sp
    // e.g. a Supplier<Integer> lambda is () -> 5
    if (n < 0) throw new IllegalArgumentException("fill: n < 0");
    if (sp == null) throw new IllegalArgumentException("fill: Supplier is null");
    Seq<U> s = new Seq<>(n);
    for (int i = 0; i < n; i++) s.add(sp.get());
    return s;
  }
  
  public static <U> Seq<Seq<U>> fill(int n1, int n2, Supplier<U> sp) {
    // return a 2D Seq with dimensions (n1,n2) filled with the result of sp
    // e.g. a Supplier<Integer> lambda is () -> 5
    if (n1 < 0) throw new IllegalArgumentException("fill: n1 < 0");
    if (n2 < 0) throw new IllegalArgumentException("fill: n2 < 0");
    if (sp == null) throw new IllegalArgumentException("fill: Supplier is null");
    Seq<Seq<U>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(fill(n2,sp));
    return s;
  }
  
  public static <U> Seq<Seq<Seq<U>>> fill(int n1, int n2, int n3, Supplier<U> sp) {
    // return a 3D Seq with dimensions (n1,n2,n3) filled with the result of sp
    // e.g. a Supplier<Integer> lambda is () -> 5
    if (n1 < 0) throw new IllegalArgumentException("fill: n1 < 0");
    if (n2 < 0) throw new IllegalArgumentException("fill: n2 < 0");
    if (n3 < 0) throw new IllegalArgumentException("fill: n3 < 0");
    if (sp == null) throw new IllegalArgumentException("fill: Supplier is null");
    Seq<Seq<Seq<U>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(fill(n2,n3,sp));
    return s;
  }
  
  public static <U> Seq<Seq<Seq<Seq<U>>>> fill(int n1, int n2, int n3, int n4,
      Supplier<U> sp) {
    // return a 4D Seq with dimensions (n1,n2,n3,n4) filled with the result of sp
    // e.g. a Supplier<Integer> lambda is () -> 5
    if (n1 < 0) throw new IllegalArgumentException("fill: n1 < 0");
    if (n2 < 0) throw new IllegalArgumentException("fill: n2 < 0");
    if (n3 < 0) throw new IllegalArgumentException("fill: n3 < 0");
    if (n4 < 0) throw new IllegalArgumentException("fill: n4 < 0");
    if (sp == null) throw new IllegalArgumentException("fill: Supplier is null");
    Seq<Seq<Seq<Seq<U>>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(fill(n2,n3,n4,sp));
    return s;
  }
  
  @Override
  public boolean equals(Object o) {
    // this is taken from openjdk/8u40-b25/java/util/AbstractList.java
    // at http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/AbstractList.java#AbstractList.equals%28java.lang.Object%29
    // with slight modification
    if (o == this) return true;
    if (!(o instanceof Seq)) return false;
    Iterator<T> t1 = iterator();
    Iterator<?> t2 = ((Seq<?>) o).iterator();
    while (t1.hasNext() && t2.hasNext()) {
      T o1 = t1.next();
      Object o2 = t2.next();
      if (!(o1==null ? o2==null : o1.equals(o2))) return false;
    }
    return !(t1.hasNext() || t2.hasNext());
  }
  
  public static <U> Seq<Seq<Seq<Seq<Seq<U>>>>> fill(int n1, int n2, int n3, int n4, 
      int n5, Supplier<U> sp) {
    // return a 5D Seq with dimensions (n1,n2,n3,n4,n5) filled with the result of sp
    // e.g. a Supplier<Integer> lambda is () -> 5
    if (n1 < 0) throw new IllegalArgumentException("fill: n1 < 0");
    if (n2 < 0) throw new IllegalArgumentException("fill: n2 < 0");
    if (n3 < 0) throw new IllegalArgumentException("fill: n3 < 0");
    if (n4 < 0) throw new IllegalArgumentException("fill: n4 < 0");
    if (n5 < 0) throw new IllegalArgumentException("fill: n4 < 0");
    if (sp == null) throw new IllegalArgumentException("fill: Supplier is null");
    Seq<Seq<Seq<Seq<Seq<U>>>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(fill(n2,n3,n4,n5,sp));
    return s;
  }

  public Seq<T> filter(Predicate<? super T> p) {
    // returns a new Seq<T> with only the elements of this satisfying p
    if (isEmpty()) throw new InappropriateSeqException("filter: Seq is empty");
    if (p == null) throw new IllegalArgumentException("filter: Predicate is null");
    T[] b = copyOf(a,N);
    int j = 0;
    for (int i = 0; i < a.length; i++) if (p.test(a[i])) b[j++] = a[i];
    return new Seq<T>(ArrayUtils.take(b,j));
  }

  public Seq<T> filterNot(Predicate<? super T> p) {
    // returns a new Seq<T> with only the elements of this not satisfying p
    if (isEmpty()) throw new InappropriateSeqException("filterNot: Seq is empty");
    if (p == null) throw new IllegalArgumentException("filterNot: Predicate is null");
    T[] b = copyOf(a,N);
    int j = 0;
    for (int i = 0; i < a.length; i++) if (!p.test(a[i])) b[j++] = a[i];
    return new Seq<T>(ArrayUtils.take(b,j));
  }

  public Optional<T> find(Predicate<? super T> p) {
    // returns an Optional of the first element in this satisfying p
    if (p == null) throw new IllegalArgumentException("find: Predicate is null");
    if (isEmpty()) return Optional.empty();
    for (int i = 0; i < N; i++)
      if (p.test(a[i])) return Optional.of(a[i]);
    return Optional.empty();
  }

  public Tuple2<Optional<T>,Optional<Integer>> findFirstNonNull() {
    // returns a Tuple2 containing an Optional of the first non null element in this and
    // an Optional of its index where both Optionals are empty if all elements are null
    if (isEmpty())
      return new Tuple2<Optional<T>,Optional<Integer>>(Optional.empty(), Optional.empty());
    for (int i = 0; i < N; i++)
      if (a[i] != null)
        return new Tuple2<Optional<T>,Optional<Integer>>(Optional.of(a[i]), Optional.of(i));
    return new Tuple2<Optional<T>,Optional<Integer>>(Optional.empty(), Optional.empty());
  }

  public T first() { 
    if (a == null) throw new InappropriateSeqException("first: Seq array is null");
    if (isEmpty()) throw new InappropriateSeqException("first: Seq is empty");
    return a[0]; 
  }

  public int firstIndex() { return 0; }

  public int firstIndexOf(int s, T v) {
    // returns the index of 1st occurrence of v in this at or after s 
    // or -1 if v is not found
    if (a == null || N - 1 < s) return -1;
    for (int i = s; i < N; i++)
      if (v == null && a[i] == null || a[i] != null && a[i].equals(v))
        return i;
    return -1;
  }

  public int firstIndexOf(T v) {
    // returns the index of 1st occurrence of v in this or -1 if v is not found
    if (isEmpty()) return -1;
    for (int i = 0; i < N; i++)
      if (v == null && a[i] == null || a[i] != null && a[i].equals(v))
        return i;
    return -1;
  }

  public <R> Seq<R> flatMap(Function<T,Seq<R>> f) {
    // returns a Seq<R> by applying f to all elements of this
    // used to build a new collection from a more complex one by decomposition
    if (isEmpty()) throw new InappropriateSeqException("flatMap: Seq is empty");
    if (f == null) throw new InappropriateSeqException("flatMap: Function is null");
    Seq<R> r = new Seq<R>();
    for (T t : a) r.addAll(f.apply(t));
    return r;
  }

  @SuppressWarnings("unchecked")
  public Seq<T> flatten() {
    // if this is a Seq<Seq<T>> returns its conversion to a Seq<T> 
    // with the same elements of type T
    if (isEmpty()) return new Seq<T>();
    if (!Seq.class.isAssignableFrom(tclass)) throw new InappropriateSeqException("flatten: "
        + "this Seq is not nested");
    Seq<T> s = new Seq<T>(), x = null;
    for (int i = 0; i < N; i++) {
      if (a[i] != null) x = (Seq<T>)a[i];
      for (int j = 0; j < x.N; j++) s.add(x.a[j]);
    }
    return s;
  }

  public Seq<T> flatline() {
    // flatten this all the way to a Seq<T> if it's nested and return the result
    if (isEmpty()) return new Seq<T>();
    Seq<T> s = new Seq<T>();
    if (!Seq.class.isAssignableFrom(tclass)) throw new InappropriateSeqException("flatline: +"
        + "this Seq is not nested");
    flatline(this,s);
    return s;
  }

  @SuppressWarnings("unchecked")
  private void flatline(Seq<?> s, Seq<T> w) {
    if (s == null || s.isEmpty()) return;
    if (!Seq.class.isAssignableFrom(s.tclass)) {
      Seq<T> x = (Seq<T>) s;
      for (int i = 0; i < x.N; i++) w.add(x.a[i]);
    } else {
      for (int i = 0; i < s.N; i++) flatline((Seq<?>)s.a[i],w);
    }
  }

  public static <U, V extends U> U fold(Seq<V> s, BinaryOperator<U> op, U z) {
    // folds this randomly using z as the start value
    // z is usually the left identity element for op, i.e. op(z,e) = e
    // e.g. if op is addition z=0, if op is multiplication z=1
    if (s == null) throw new IllegalArgumentException("fold: Seq<V> s is null");
    if (op == null) throw new IllegalArgumentException("fold: BinaryOperator is null");
    V[] b = s.to();
    U u = op.apply(z, s.a[0]);
    for (int i = 1; i < b.length; i++) u = op.apply(u, b[i]);
    return u;
  }

  public <R> R foldLeft(BiFunction<R,T,R> op, R z) {
    // folds this from the left using z as the start value
    // z is usually the left identity element for op, i.e. op(z,e) = e
    // e.g. if op is addition z=0, if op is multiplication z=1
    if (isEmpty()) throw new InappropriateSeqException("foldLeft: Seq is empty");
    if (op == null) throw new IllegalArgumentException("foldLeft: BiFunction is null");
    R r = op.apply(z, a[0]);
    for (int i = 1; i < N; i++) r = op.apply(r, a[i]);
    return r;
  }

  public <R> R foldRight(BiFunction<T,R,R> op, R z) {
    // folds this from the right using z as the start value
    // z is usually the right identity element for op, i.e. op(e,z) = e
    // e.g. if op is addition z=0, if op is multiplication z=1
    if (isEmpty()) throw new InappropriateSeqException("foldRight: Seq is empty");
    if (op == null) throw new IllegalArgumentException("foldRight: BiFunction is null");
    R r = op.apply(a[N - 1], z);
    for (int i = N - 2; i > -1; i--) r = op.apply(a[i], r);
    return r;
  }

  public boolean forAll(Predicate<T> p) {
    // returns true if p is true for all elements of this else false
    if (isEmpty()) throw new InappropriateSeqException("forAll: Seq is empty");
    if (p == null) throw new IllegalArgumentException("forAll: Predicate is null");
    for (int i = 0; i < N; i++) if (!p.test(a[i])) return false;
    return true;
  }
  
  public void forEach(Consumer<? super T> cons) {
    if (cons == null) throw new IllegalArgumentException("forEach: Consumer is null");
    final T[] d = a;
    final int size = N;
    for (int i=0; i < size; i++) cons.accept(d[i]);
  }

  public Seq<T> fromByteArray(byte[] b) {
    // deserialize a Seq<T> from a byte[]
    ByteArrayInputStream bis = new ByteArrayInputStream(b);
    ObjectInput in = null;
    Object x = null;
    try {
      in = new ObjectInputStream(bis); // implements ObjectInput
      x = in.readObject(); // oBytes now deserialized to x
      if (x.getClass() != Seq.class) throw new IllegalArgumentException(
          "from: byte[] isn't a serialized Seq");
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) in.close();
      } catch (IOException ex) {}
      try {
        bis.close();
      } catch (IOException ex) {}
    }
    // return cloned object
    Seq<?> s = (Seq<?>) x;
    if (!s.isEmpty() && s.tclass != tclass) throw new IllegalArgumentException(
        "from: byte[] isn't a serialized Seq<"+tclass.getSimpleName()+">");
    @SuppressWarnings("unchecked")
    Seq<T> t = (Seq<T>) x;
    return t;
  }
  
  public Object toArrayObject() { return toArrayObject2(this); }
    
  @SafeVarargs
  private final Object toArrayObject2(Seq<?>... s) {
    // returns an object corresponding to seq and that
    // can be appropriately cast as an array 
    if (s == null) throw new IllegalArgumentException("toArrayObject2: the s arg is null");
    if (s.length == 0) throw new IllegalArgumentException("toArrayObject2: s.length = 0");
    if (s[0] == null) throw new IllegalArgumentException("toArrayObject2: s[0] = null");
    Seq<?> seq = s[0];
    int dim = 0; // nesting depth
    Class<?> c = null;
    seq.analyzeNesting();
    dim = seq.dim;
    c = seq.rclass;  
    if (dim > 255) throw new SeqNestingTooHighException("toArrayObject2: array " 
        + "dimensionality is limited to 255 by Java");
    int[] dimensions = new int[dim];
    dimensions[0] = seq.size();
    Object o = c != null ? newInstance(c, dimensions)
        : newInstance(Object.class, dimensions);
    Object[] b = (Object[]) o; int i = 0; 
    if (dim == 1) { // recursion termination condition
      for (Object e : seq) b[i++] = e;
    } else for (Object e : seq) b[i++] = toArrayObject2((Seq<?>) e);
    return o;
  }
  
  public static Object toArrayObject(Seq<?> seq) {
    // returns an object corresponding to seq and that
    // can be appropriately cast as an array 
    if (seq == null) throw new IllegalArgumentException("toArrayObject: seq vararg is null");
    int dim = 0; // nesting depth
    Class<?> c = null;
    seq.analyzeNesting();
    dim = seq.dim;
    c = seq.rclass;  
    if (dim > 255) throw new SeqNestingTooHighException("toArrayObject: array " 
        + "dimensionality is limited to 255 by Java");
    int[] dimensions = new int[dim];
    dimensions[0] = seq.size();
    Object o = c != null ? newInstance(c, dimensions)
        : newInstance(Object.class, dimensions);
    Object[] b = (Object[]) o; int i = 0; 
    if (dim == 1) { // recursion termination condition
      for (Object e : seq) b[i++] = e;
    } else for (Object e : seq) b[i++] = toArrayObject((Seq<?>) e);
    return o;
  }

  public T get(int k) {
    // return the element at index k if possible
    if (isEmpty()) throw new InappropriateSeqException("getKthFromFirst: Seq is empty");
    if (k < 0) throw new IllegalArgumentException("kthFromlastIndex: k must be > -1");
    if (N-1 < k) throw new NoSuchElementException("kthFromFirst element does not exist");
    return a[k];
  }

  public T getKthFromFirst(int k) {
    // return the kth from first element in the Seq where zeroth from first = first
    return get(k);
  }

  public T getKthFromLast(int k) {
    // return the kth from last element in the Seq where zeroth from last = last
    if (isEmpty()) throw new InappropriateSeqException("kthFromlastIndex: Seq is empty");
    if (k < 0) throw new IllegalArgumentException("kthFromlastIndex: k must be > -1");
    if (this.size()-1 < k) throw new NoSuchElementException("kthFromLast element does not exist");
    return a[N-1-k];
  }

  public <K> Map<K,Seq<T>> groupBy(Function<T,K> f) {
    // returns a map of this partitioned by f
    if (isEmpty()) throw new InappropriateSeqException("groupBy: Seq is empty");
    Map<K,Seq<T>> m = new HashMap<>(); K k;
    for (int i = 0; i < N; i++) {
      k = f.apply(a[i]);
      if (m.containsKey(k)) m.get(k).add(a[i]);
      else m.put(k, new Seq<T>(a[i]));
    }
    return m;
  }

  public Iterator<Seq<T>> grouped (int n) {
    // return an Iterator<Seq<T>>  of this partitioned into groups of n
    // elements except the last group that may have less than n elements
    if (isEmpty()) throw new InappropriateSeqException("grouped: Seq is empty");
    if (n < 1) throw new IllegalArgumentException("grouped: group size is < 1");
    if (n >= N) return new Seq<Seq<T>>(new Seq<T>(this)).iterator();
    Seq<T> s = null;  Seq<Seq<T>> ss = new Seq<Seq<T>>();
    int iterations = N / n, remainder = N % n;   
    for (int i = 0; i < iterations; i++) {
      s = new Seq<>();
      for (int j = i*n; j < (i+1)*n; j++) s.add(a[j]);
      ss.add(s);
    }
    if (remainder > 0) {
      s = new Seq<>();
      for (int j = iterations*n; j < (iterations*n)+remainder; j++) s.add(a[j]);
      ss.add(s);
    }
    return ss.iterator();  
  }
  
  @Override
  public int hashCode() {
    // this is based on the documentation of java.util.List.hashCode() at
    // https://docs.oracle.com/javase/8/docs/api/java/util/List.html#hashCode--
    int hashCode = 1;
    for (T t : this) hashCode = 31*hashCode + (t == null ? 0 : t.hashCode());
    return hashCode;
  }

  public boolean hasDups() {
    // return true if this has a duplicated element ese return false
    if (isEmpty()) throw new InappropriateSeqException("hasDups: Seq is empty");
    Set<T> set = new HashSet<>();
    for (int i = 0; i < a.length - 1; i++) set.add(a[i]);
    return set.size() < a.length ? true : false;
  }

  public boolean hasNull() {
    // returns true if this has a null element else return false
    if (isEmpty()) return false;
    for (int i = 0; i < N; i++) if (a[i] == null) return true;
    return false;
  } 

  public T head() {
    // return the 1st element of this if any
    if (isEmpty()) throw new InappropriateSeqException("head: Seq is empty");
    return a[0];
  }

  public Optional<T> headOption(T[] a) {
    return isEmpty() ? Optional.empty() : Optional.ofNullable(a[0]);
  }

  public int howManyOf(T t) { return indices(t).length; }

  public int[] identityHashCodes() {
    // return an int[] with the identityHashcodes of the elements in this
    if (isEmpty()) return new int[0]; 
    int[] z = new int[N];
    for (int i = 0; i < N; i++) z[i] = identityHashCode(a[i]);
    return z;
  }

  public int indexOf(Object o) {
    // return the index of the first occurrence of o if possible else return -1
    if (isEmpty()) return -1;
    if (tclass != null && o != null && !tclass.isAssignableFrom(o.getClass())) return -1; 
    int i = -1;
    while (++i < N) {
      T t = a[i];
      if (t == o || t != null && t.equals(o)) return i;
    }
    return -1;
  }

  public int indexOf(Object o, int from) {
    // return the index of the first occurrence of o at or after from 
    // if possible else return -1
    if (isEmpty()) return -1;
    int start = from < 0 ? 0 : from;  
    if (isEmpty() || from > N-1) return -1;
    if (tclass != null && o != null && !tclass.isAssignableFrom(o.getClass())) return -1; 
    int i = start-1;
    while (++i < N) {
      T t = a[i];
      if (t == o || t != null && t.equals(o)) return i;
    }
    return -1;
  }

  @SafeVarargs
  public final int indexOfSlice(T... v) {
    // returns the index of the start of the first occurrence of v in a 
    // or -1 if v not in a
    if (v == null) throw new IllegalArgumentException("indexOfSlice: v is null");
    if (isEmpty() || v.length > N) return -1;
    LOOP: 
      for (int i = 0; i < N - v.length + 1; i++) {
        for (int j = i; j < i + v.length; j++) {
          if (!(a[j] == null && v[j-i] == null || a[j] != null && a[j].equals(v[j-i])))
            continue LOOP;
        }
        return i;
      }
    return -1;
  }

  public int indexOfSlice(T[] v, int from) {
    // returns the index of the start of the first occurrence of v at 
    // or after from or return -1 if v not in a
    if (v == null) throw new IllegalArgumentException("indexOfSlice: v is null");
    int start = from < 0 ? 0 : from;  
    if (isEmpty() || start + v.length > N) return -1;
    LOOP: 
      for (int i = start; i < N - v.length + 1; i++) {
        for (int j = i; j < i + v.length; j++) {
          if (!(a[j] == null && v[j-i] == null || a[j] != null && a[j].equals(v[j-i])))
            continue LOOP;
        }
        return i;
      }
    return -1;

  }


  public int indexWhere(Predicate<T> p) {
    // return index of first element for which p is true or return -1 if none
    if (isEmpty()) return -1;
    if (p == null) throw new IllegalArgumentException("indexWhere: Predicate is null");
    for (int i = 0; i > N; i++) if (p.test(a[i])) return i;
    return -1;
  }

  public int indexWhere(Predicate<T> p, int from) {
    // return index of first element for which p is true at or after from 
    // or return -1 if none
    if (isEmpty() || from > N-1) return -1;
    if (p == null) throw new IllegalArgumentException("indexWhere: Predicate is null");
    int start = from > 0 ? from : 0;
    for (int i = start; i < N; i++) if (p.test(a[i])) return i;
    return -1;
  }

  public int[] indices(T t) {
    // return all the indices of t in an int[] or return an int[] of zero length
    if (a == null || isEmpty()) return new int[0];
    int[] z = new int[N]; int i = -1, c = 0;
    while (++i < N) {
      T u = a[i];
      if (u == t || u != null && u.equals(t)) z[c++] = i;
    }
    if (c == 0) return new int[0];
    return ArrayUtils.take(z,c);   
  }

  public int[] indices() { return ArrayUtils.range(0,N); }

  public Seq<T> init() {
    // returns a Seq<T> with all elements of this except the last
    if (isEmpty()) throw new InappropriateSeqException("init: Seq is empty");
    return new Seq<T>(copyOfRange(a, 0, N-1));
  }

  public Iterator<Seq<T>> inits() {
    // return an iterator of Seq<T> over the inits of the elements of this
    // where the first value is all the elements and subseqent values are
    // the results of repeated applications of init()
    if (isEmpty()) throw new InappropriateSeqException("inits: Seq is empty");
    T[] d = to();
    T[] b = ArrayUtils.append(d, a[N-1]);
    T[][] c = ArrayUtils.ofDim(d.getClass().getComponentType(), 1, b.length);
    for (int i = 0; i < b.length; i++) c[0][i] = b[i];
    Iterator<T[]> it = Stream.generate(() -> {
      c[0] = ArrayUtils.init(c[0]);
      return c[0];
    }).limit(N+1).iterator();
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };

  }

  public Seq<T> intersect(Seq<T> s) {
    // return a Seq<T> that is the set intersection of this and s (no duplicates)
    if (isEmpty() || s == null || s.isEmpty()) return new Seq<T>();
    T[] a1 = to(); Map<T,Integer> m1 = new HashMap<>();
    T[] a2 = s.to(); Map<T,Integer> m2 = new HashMap<>();
    for (T t : a1) m1.merge(t, 1, Integer::sum);
    for (T t : a2) m2.merge(t, 1, Integer::sum);
    Tuple2<Map<T,Integer>,Map<T,Integer>> tp;
    tp = m1.size() < m2.size() ? new Tuple2<>(m1,m2) : new Tuple2<>(m2,m1);
    Seq<T> seq = new Seq<T>();
    System.out.println("intersect: tclass="+tclass.getSimpleName());
    if (Comparable.class.isAssignableFrom(tclass)) System.out.println("intersect: tclass comparable");
    for (T t : tp._1.keySet()) if (tp._2.containsKey(t)) seq.add(t);
    if (Comparable.class.isAssignableFrom(tclass)) seq.sorted();
    return seq;
  }

  public Seq<T> intersectMultiset(Seq<T> s) {
    // return a Seq<T> that is the intersection of this and s with repeated elements
    if (isEmpty() || s == null || s.isEmpty()) return new Seq<T>();
    T[] a1 = to(); Map<T,Integer> m1 = new HashMap<>();
    T[] a2 = s.to(); Map<T,Integer> m2 = new HashMap<>();
    for (T t : a1) m1.merge(t, 1, Integer::sum);
    for (T t : a2) m2.merge(t, 1, Integer::sum);
    Tuple2<Map<T,Integer>,Map<T,Integer>> tp;
    tp = m1.size() < m2.size() ? new Tuple2<>(m1,m2) : new Tuple2<>(m2,m1);
    Seq<T> seq = new Seq<T>();
    for (T t : tp._1.keySet()) {
      if (tp._2.containsKey(t)) {
        int c = Math.min(tp._1.get(t),tp._2.get(t));
        for (int i = 0; i < c; i++) seq.add(t);
      }
    }
    if (Comparable.class.isAssignableFrom(tclass)) return new Seq<T>(seq.sorted());
    return seq;
  }

  public boolean isBoxedOrString(String s) {
    if (s == null || s.length() == 0) return false;
    return s.matches("|java.lang.Integer|java.lang.Long|java.lang.Double"
        + "|java.lang.Byte|java.lang.Short|java.lang.Float|java.lang.Boolean"
        + "|java.lang.Character|java.lang.String") ? true : false;
  }

  public boolean isDefinedAt(int n) {
    // returns true iff a contains an element at index n else false
    if (a == null || n < 0 || N == 0) return false;
    return  n < N;
  }

  public boolean isEmpty() { return N == 0; }

  public boolean isFull() { return N == a.length; }

  @SuppressWarnings("unchecked")
  public boolean isReverseSorted() {
    if (isEmpty()) return true;
    if (!Comparable.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
        "isSorted: "+tclass.getSimpleName()+" doesn't extend Comparable");
    for (int i = N-2; i > -1; i++)
      if (((Comparable<T>)a[i+1]).compareTo(a[i])<0) return false;
    return true;
  }

  public boolean isReverseSorted(Comparator<T> cmp) {
    if (isEmpty()) return true;
    if (cmp == null) throw new IllegalArgumentException("isReverseSorted: Comparator is null");
    for (int i = N-2; i < -1; i++)
      if (cmp.compare(a[i+1],a[i])<0) return false;
    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean isSorted() {
    if (isEmpty()) return true;
    if (!Comparable.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
        "isSorted: "+tclass.getSimpleName()+" doesn't extend Comparable");
    for (int i = 1; i < N; i++)
      if (((Comparable<T>)a[i]).compareTo(a[i-1])<0) return false;
    return true;
  }

  public boolean isSorted(T[] a, Comparator<T> cmp) {
    if (isEmpty()) return true;
    if (cmp == null) throw new IllegalArgumentException("isSorted: Comparator is null");
    for (int i = 1; i < N; i++)
      if (cmp.compare(a[i],a[i-1])<0) return false;
    return true;
  }

  public Iterable<T> iterable() { return () -> iterator(); }

  public static <U> Seq<U> iterate(U start, int length, Function<U, U> f) {
    // return a Seq<U> with elements beginning with start and followed successive 
    // applications of f in the form start, f(start), f(f(start))... up to length
    if (start == null)
      throw new IllegalArgumentException("iterate: the start value must not be null");
    if (f == null)
      throw new IllegalArgumentException("iterate: the function f must not be null");
    if (length == 0)
      return new Seq<U>(ArrayUtils.ofDim(start.getClass(), 0));
    U[] b = ArrayUtils.ofDim(start.getClass(), length);
    b[0] = start;
    for (int i = 1; i < length; i++)
      b[i] = f.apply(b[i - 1]);
    return new Seq<U>(b);
  }

  public Iterator<T> iterator() { return new ArrayIterator(); }

  public int last() { return N; }

  public int lastIndex() { return N; }

  public int lastIndexOf(Object v) { return lastIndexOf(v,0); }

  public int lastIndexOf(Object v, int s) {
    // returns the index of last occurrence of v in this at or before s 
    // or -1 if v is not found
    if (N - 1 < s) return -1;
    for (int i = N-1; i > s-1; i--)
      if (v == null && a[i] == null || a[i] != null && a[i].equals(v))
        return i;
    return -1;
  }

  @SafeVarargs
  public final int lastIndexOfSlice(T... v) {
    // returns the index of the start of the last occurrence of v in a 
    // or -1 if v not in a
    if (v == null) throw new IllegalArgumentException("lastIndexOfSlice: v is null");
    if (isEmpty() || v.length > N) return -1;
    LOOP: 
      for (int i = N - v.length; i > -1; i--) {
        for (int j = i; j < i + v.length; j++) {
          if (!(a[j] == null && v[j-i] == null || a[j] != null && a[j].equals(v[j-i])))
            continue LOOP;
        }
        return i;
      }
    return -1;
  }

  public int lastIndexOfSlice(T[] v, int end) {
    // returns the index of the start of the last occurrence of v before or
    // at end or return -1 if v doesn't occcur by N > end+1 ? end+1 : N
    if (v == null) throw new IllegalArgumentException("lastIndexOfSlice: v is null");
    if (isEmpty() || v.length > end + 1) return -1;
    int len = N > end+1 ? end+1 : N;
      LOOP: 
        for (int i = len - v.length; i > -1; i--) {
          for (int j = i; j < i + v.length; j++) {
            if (!(a[j] == null && v[j-i] == null || a[j] != null && a[j].equals(v[j-i])))
              continue LOOP;
          }
          return i;
        }
      return -1;
  }

  public int lastIndexWhere(Predicate<T> p) {
    //return index of last element for which p is true or return -1 if none
    if (isEmpty()) return -1;
    if (p == null) throw new IllegalArgumentException("lastIndexWhere: Predicate is null");
    for (int i = N-1; i > -1; i--) if (p.test(a[i])) return i;
    return -1;
  }

  public int lastIndexWhere(Predicate<T> p, int end) {
    //return index of last element for which p is true before or at end 
    // or return -1 if none
    if (isEmpty() || end < 0) return -1;
    if (p == null) throw new IllegalArgumentException("lastIndexWhere: Predicate is null");
    int len = N > end+1 ? end+1 : N;
    for (int i = len-1; i > -1; i--) if (p.test(a[i])) return i;
    return -1;
  }

  public Optional<T> lastOption() {
    if (N == 0) return Optional.empty();
    return Optional.ofNullable(a[N-1]);
  }

  public int length() { return size(); }

  public int lengthCompare(int x) { return x > N ? 1 : x < N ? -1 : 0; }

  public ListIterator<T> listIterator() { return Arrays.asList(to()).listIterator(); }

  public ListIterator<T> listIterator(int i) { return Arrays.asList(to()).listIterator(i); }

  public <R> Seq<R> map(Function<T,R> f) {
    if (isEmpty()) throw new InappropriateSeqException("map: Seq is empty");
    if (f == null) throw new IllegalArgumentException("map: Function is null");
    Seq<R> s = new Seq<>();
    for (int i = 0; i < N; i++) s.add(f.apply(a[i]));
    return s;
  }

  @SuppressWarnings("unchecked")
  public T max() {
    // return max non-null element of a unless all elements are null then return null
    if (isEmpty()) throw new InappropriateSeqException("max: Seq is empty");
    if (a.length == 1 && a[0] != null) return a[0];
    if (!Comparable.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
        "max: "+tclass.getSimpleName()+" doesn't extend Comparable");        
    T max = null;  boolean found = false;
    for (int i = 0; i < N; i++)
      if (a[i] != null) {
        if (found == false) {max = a[i]; found = true; }
        else if (((Comparable<T>)a[i]).compareTo(max)>0) max = a[i];
      }
    return found ? max : null; 
  }

  public T max(Comparator<T> cmp) {
    // return max non-null element of a unless all elements are null then return null
    if (isEmpty()) throw new InappropriateSeqException("max: Seq is empty");
    if (a.length == 1 && a[0] != null) return a[0];
    if (cmp == null) throw new IllegalArgumentException("max: Comparator is null");   
    T max = null;  boolean found = false;
    for (int i = 0; i < N; i++)
      if (a[i] != null) {
        if (found == false) {max = a[i]; found = true; }
        else if (cmp.compare(a[i],max)>0) max = a[i];
      }
    return found ? max : null; 
  }

  public <R extends Comparable<? super R>> T maxBy(Function<T, R> f) {
    // return the non-null element x in this that has the greatest f(x) else return null
    if (isEmpty()) throw new InappropriateSeqException("maxBy: Seq is empty");
    if (N == 1 && a[0] != null) return a[0];
    Tuple2<Optional<T>, Optional<Integer>> fnn = findFirstNonNull();
    if (!fnn._1.isPresent()) return null;
    T max = fnn._1.get();
    R rmax = f.apply(max);
    R tmp = null;
    for (int i = fnn._2.get() + 1; i < N; i++) {
      if (a[i] != null) {
        tmp = f.apply(a[i]);
        if (tmp.compareTo(rmax) > 0) {
          rmax = tmp;
          max = a[i];
        }
      }
    }
    return max;
  }

  public <R> T maxBy(Function<T, R> f, Comparator<R> cmp) {
    // return the non-null element x in this that has the greatest f(x) else return null
    if (isEmpty()) throw new InappropriateSeqException("maxBy: Seq is empty");
    if (N == 1 && a[0] != null) return a[0];
    Tuple2<Optional<T>, Optional<Integer>> fnn = findFirstNonNull();
    if (!fnn._1.isPresent()) return null;
    T max = fnn._1.get();
    R rmax = f.apply(max);
    R tmp = null;
    for (int i = fnn._2.get() + 1; i < N; i++) {
      if (a[i] != null) {
        tmp = f.apply(a[i]);
        if (cmp.compare(tmp,rmax) > 0) {
          rmax = tmp;
          max = a[i];
        }
      }
    }
    return max;
  }

  @SuppressWarnings("unchecked")
  public T min() {
    // return min non-null element of a unless all elements are null then return null
    if (isEmpty()) throw new InappropriateSeqException("min: Seq is empty");
    if (a.length == 1 && a[0] != null) return a[0];
    if (!Comparable.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
        "min: "+tclass.getSimpleName()+" doesn't extend Comparable");        
    T min = null;  boolean found = false;
    for (int i = 0; i < N; i++)
      if (a[i] != null) {
        if (found == false) {min = a[i]; found = true; }
        else if (((Comparable<T>)a[i]).compareTo(min)<0) min = a[i];
      }
    return found ? min : null; 
  }

  public T min(Comparator<T> cmp) {
    // return min non-null element of a unless all elements are null then return null
    if (isEmpty()) throw new InappropriateSeqException("min: Seq is empty");
    if (a.length == 1 && a[0] != null) return a[0];
    if (cmp == null) throw new IllegalArgumentException("min: Comparator is null");        
    T min = null;  boolean found = false;
    for (int i = 0; i < N; i++)
      if (a[i] != null) {
        if (found == false) {min = a[i]; found = true; }
        else if (cmp.compare(a[i],min)<0) min = a[i];
      }
    return found ? min : null; 
  }

  public <R extends Comparable<? super R>> T minBy(Function<T, R> f) {
    // return the non null element x in this that has the least f(x) else return null
    if (isEmpty()) throw new InappropriateSeqException("minBy: Seq is empty");
    if (N == 1 && a[0] != null) return a[0];
    Tuple2<Optional<T>, Optional<Integer>> fnn = findFirstNonNull();
    if (!fnn._1.isPresent()) return null;
    T min = fnn._1.get();
    R rmin = f.apply(min);
    R tmp = null;
    for (int i = fnn._2.get() + 1; i < N; i++) {
      if (a[i] != null) {
        tmp = f.apply(a[i]);
        if (tmp.compareTo(rmin) < 0) {
          rmin = tmp;
          min = a[i];
        }
      }
    }
    return min;
  }

  public <R> T minBy(Function<T, R> f, Comparator<R> cmp) {
    // return the non null element x in this that has the least f(x) else return null
    if (isEmpty()) throw new InappropriateSeqException("minBy: Seq is empty");
    if (N == 1 && a[0] != null) return a[0];
    Tuple2<Optional<T>, Optional<Integer>> fnn = findFirstNonNull();
    if (!fnn._1.isPresent()) return null;
    T min = fnn._1.get();
    R rmin = f.apply(min);
    R tmp = null;
    for (int i = fnn._2.get() + 1; i < N; i++) {
      if (a[i] != null) {
        tmp = f.apply(a[i]);
        if (cmp.compare(tmp,rmin) < 0) {
          rmin = tmp;
          min = a[i];
        }
      }
    }
    return min;
  }

  public String mkString() { 
    return addString(new StringBuilder()).toString(); 
  }

  public String mkString(String sep) {
    return addString(new StringBuilder(), sep).toString(); 
  }

  public String mkString(String start, String sep, String end) {
    return addString(new StringBuilder(), start, sep, end).toString(); 
  }
 
  public boolean nonEmpty() { return !isEmpty(); }
  
  public static <U> Seq<U> ofDim(int n) {
    // return a 1D Seq of capacity n
    return new Seq<U>(n);
  }
  
  public static <U> Seq<Seq<U>> ofDim(int n1, int n2) {
    // return a 2D Seq with capacity n1*n2)
    Seq<Seq<U>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(ofDim(n2));
    return s;
  }
  
  public static <U> Seq<Seq<Seq<U>>> ofDim(int n1, int n2, int n3) {
    // return a 3D Seq with capacity n1*n2*n3
    Seq<Seq<Seq<U>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(ofDim(n2,n3));
    return s;
  }
  
  public static <U> Seq<Seq<Seq<Seq<U>>>> ofDim(int n1, int n2, int n3, int n4) {
    // return a 4D Seq with capacity n1*n2*n3*n4
    Seq<Seq<Seq<Seq<U>>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(ofDim(n2,n3,n4));
    return s;
  }

  public static <U> Seq<Seq<Seq<Seq<Seq<U>>>>> ofDim(int n1, int n2, int n3, int n4, int n5) {
    // return a 4D Seq with capacity n1*n2*n3*n4*n5
    Seq<Seq<Seq<Seq<Seq<U>>>>> s = new Seq<>(n1);
    for (int i = 0; i < n1; i++) s.add(ofDim(n2,n3,n4,n5));
    return s;
  }
  
  public Seq<T> padTo(int len, T v) {
    // returns a copy of this with len-size() elements v appended
    Seq<T> s = clone();
    if (len <= N) return s;
    for (int i = 0; i < len-N; i++) s.add(v);
    return s;
  }

  public Seq<Seq<T>> partition(Predicate<T> p) {
    // returns a Seq<Seq<T>> with two Seq<T> elements. the first has values
    // for which p is true and the second has values for which p is false.
    if (isEmpty()) return new Seq<Seq<T>>();
    if (p == null) new IllegalArgumentException("partition: Predicate is null");
    T[] t = ArrayUtils.ofDim(tclass,N), f = ArrayUtils.ofDim(tclass,N); 
    int tindex = 0, findex = 0;
    for (int i = 0; i < N; i++)
      if (p.test(a[i])) t[tindex++] = a[i];
      else f[findex++] = a[i];
    Seq<T> st = new Seq<T>(ArrayUtils.take(t,tindex));
    Seq<T> sf = new Seq<T>(ArrayUtils.take(f,findex));
    return new Seq<Seq<T>>(st,sf);
  }

  public Seq<T> patch(int from, int replaced, Seq<T> s) {
    // return a copy of this with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    if (from > N-1) return clone();
    Seq<T> t = new Seq<T>(ArrayUtils.take(a,from));
    t.addArray(s.to());
    for (int i = from+replaced; i < N; i++) t.add(a[i]);
    return t;
  }

  public Iterator<Seq<T>> permutations() {
    if (isEmpty()) throw new InappropriateSeqException("permutations: Seq is empty");
    Iterator<T[]> it = new Permutator<T>(to());
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };
  }

  public Stream<Seq<T>> permutationStream() {
    if (isEmpty()) throw new InappropriateSeqException("permutationStream: Seq is empty");
    Iterator<Seq<T>> it = permutations();
    if (N < 21) return Stream.generate(() -> it.next()).limit(factorial(N));
    return Stream.generate(() -> it.next());
  }

  public int prefixLength(Predicate<T> p) {
    // return the length of the longest prefix in this satisfying p
    if (isEmpty()) throw new InappropriateSeqException("prefixLength: Seq is empty");
    if (p == null) new IllegalArgumentException("prefixLength: Predicate is null");
    for (int i = 0; i < N; i++) if (!p.test(a[i])) return i;
    return N;
  }

  public void prepend(T t) { add(0,t); }

  public boolean prependAll(Collection<? extends T> c) {
    // prepend the elements of c to the end of this
    // return true if this Seq changed else return false
    if (c == null || c.isEmpty()) return false;
    T[] b = to(); clear(); for (T x : c) add(x); addArray(b);
    return true;
  }
  
  public void printClasses() {
    if (rclass == null) System.out.println("rclass = null");
    else System.out.println("rclass = "+rclass.getSimpleName());
    if (tclass == null) System.out.println("tclass = null");
    else System.out.println("tclass = "+tclass.getSimpleName());
  }

  public double product() {
    if (isEmpty()) throw new InappropriateSeqException("product: Seq is empty");
    if (!(tclass == Character.class || Number.class.isAssignableFrom(tclass)))
      throw new InappropriateSeqException(
          "product: Seq element class isn't Character and doesn't extend Number");
    double product = 1.;
    if (tclass == Character.class) {
      for (int i = 0; i < N; i++) {
        product *= Character.digit((char) a[i], 10);
        if (product == 0) return 0;
      }
    }
    else {
      for (int i = 0; i < a.length; i++) {
        product *= ((Number)a[i]).doubleValue();
        if (product == 0) return 0;
      }
    }
    return product;
  }

  public static <U, V extends U> U reduce(Seq<V> s, BiFunction<U, U, U> f) {
    // return the application of f to all elements of s in random order
    if (s == null) throw new IllegalArgumentException("reduceLeft: Seq<V> s is null");
    if (f == null) throw new IllegalArgumentException("reduceLeft: BiFunction is null");
    if (s.size() < 2) throw new IllegalArgumentException("reduceLeft: Seq<V> s size is < 2");
    V[] b = s.to(); SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    ArrayUtils.shuffle(b, r);
    U u = f.apply(b[0], b[1]);
    for (int i = 2; i < b.length; i++) u = f.apply(u, b[i]);
    return u;
  }
  
  public static Seq<Integer> range(int start, int end) {
    // return a Seq<Integer> with elements incremented by 1 from start to end-1
    return new Seq<Integer>(ArrayUtils.rangeInteger(start, end));
  }
  
  public static Seq<Integer> range(int start, int end, int step) {
    // return a Seq<Integer> with elements equally spaced by step from start to end-1
    return new Seq<Integer>(ArrayUtils.rangeInteger(start, end, step));
  }
  
  public static Seq<Long> range(long start, long end) {
    // return a Seq<Long> with elements incremented by 1 from start to end-1
    return new Seq<Long>(ArrayUtils.rangeLong(start, end));
  }
  
  public static Seq<Long> range(Long start, Long end, Long step) {
    // return a Seq<Long> with elements equally spaced by step from start to end-1
    return new Seq<Long>(ArrayUtils.rangeLong(start, end, step));
  }
  
  public static Seq<Double> range(double start, double end) {
    // return a Seq<Double> with elements incremented by 1 from start to end-1
    return new Seq<Double>(ArrayUtils.rangeDouble(start, end));
  }
  
  public static Seq<Double> range(double start, double end, double step) {
    // return a Seq<Double> with elements equally spaced by step from start to end-1
    return new Seq<Double>(ArrayUtils.rangeDouble(start, end, step));
  }
  
  public static Seq<Double> range(Number start, Number end) {
    // return a Seq<Double> with elements incremented by 1 from start to end-1
    return new Seq<Double>(ArrayUtils.rangeNumeric(start, end));
  }
  
  public static Seq<Double> range(Number start, Number end, Number step) {
    // return a Seq<Double> with elements equally spaced by step from start to end-1
    return new Seq<Double>(ArrayUtils.rangeNumeric(start, end, step));
  }

  public T reduceLeft(BiFunction<T, T, T> f) {
    // return the application of f to all elements of s going left to right
    if (isEmpty()) throw new InappropriateSeqException("reduceLeft: Seq is empty");
    if (N < 2) throw new InappropriateSeqException("reduceLeft: Seq size is < 2");
    T t = f.apply(a[0], a[1]);
    for (int i = 2; i < N; i++) t = f.apply(t, a[i]);
    return t;
  }

  public static <U, V extends U> U reduceLeft(Seq<V> s, BiFunction<U, V, U> f) {
    // return the application of f to all elements of s going right to left
    if (s == null) throw new IllegalArgumentException("reduceLeft: Seq<V> s is null");
    if (f == null) throw new IllegalArgumentException("reduceLeft: BiFunction is null");
    if (s.size() < 2) throw new IllegalArgumentException("reduceLeft: Seq<T> s size is < 2");
    U u = f.apply(s.a[0], s.a[1]);
    for (int i = 2; i < s.N; i++) u = f.apply(u, s.a[i]);
    return u;
  }

  public Optional<T> reduceLeftOption(BiFunction<T, T, T> f) {
    // return the application of f to all elements of s going left to right
    if (isEmpty() || N < 2) return Optional.empty();
    T t = f.apply(a[0], a[1]);
    for (int i = 2; i < a.length; i++) t = f.apply(t, a[i]);
    return Optional.ofNullable(t);
  }

  public static <U, V extends U> Optional<U> reduceLeftOption(Seq<V> s, BiFunction<U, V, U> f) {
    // return the application of f to all elements of s going right to left
    if (s == null || s.size() < 2) return Optional.empty();
    if (f == null) throw new IllegalArgumentException("reduceLeft: BiFunction is null");
    U u = f.apply(s.a[0], s.a[1]);
    for (int i = 2; i < s.N; i++) u = f.apply(u, s.a[i]);
    return Optional.ofNullable(u);
  }

  public T reduceRight(T[] a, BiFunction<T, T, T> f) {
    // return the application of f to all elements of s going right to left
    if (isEmpty()) throw new InappropriateSeqException("reduceLeft: Seq is empty");
    if (N < 2) throw new InappropriateSeqException("reduceLeft: Seq size is < 2");
    T t = f.apply(a[a.length - 1], a[a.length - 2]);
    for (int i = a.length - 3; i > 0; i--) t = f.apply(a[i], t);
    return t;
  }

  public static <U, V extends U> U reduceRight(Seq<V> s, BiFunction<V, U, U> f) {
    // return the application of f to all elements of s going right to left
    if (s == null) throw new IllegalArgumentException("reduceLeft: Seq<T> s is null");
    if (f == null) throw new IllegalArgumentException("reduceLeft: BiFunction is null");
    if (s.size() < 2) throw new IllegalArgumentException("reduceLeft: Seq<T> s size is < 2");
    U u = f.apply(s.a[s.N - 1], s.a[s.N - 2]);
    for (int i = s.N - 3; i > 0; i--) u = f.apply(s.a[i], u);
    return u;
  }

  public Optional<T> reduceRightOption(T[] a, BiFunction<T, T, T> f) {
    // return the application of f to all elements of s going right to left
    if (isEmpty() || N < 2) return Optional.empty();
    T t = f.apply(a[a.length - 1], a[a.length - 2]);
    for (int i = a.length - 3; i > 0; i--) t = f.apply(a[i], t);
    return Optional.ofNullable(t);
  }

  public static <U, V extends U> Optional<U> reduceRightOption(Seq<V> s, BiFunction<V, U, U> f) {
    // return the application of f to all elements of s going right to left
    if (s == null || s.size() < 2) return Optional.empty();
    if (f == null) throw new IllegalArgumentException("reduceLeft: BiFunction is null");
    U u = f.apply(s.a[s.N - 1], s.a[s.N - 2]);
    for (int i = s.N - 3; i > 0; i--) u = f.apply(s.a[i], u);
    return Optional.ofNullable(u);
  }

  public T remove() { return delete(); }

  public boolean remove(Object o) { return deleteObject(o); }

  public T remove(int i) { return delete(i); }

  public boolean removeAll(T t) { return deleteAll(t); }

  public boolean removeAll(Collection<?> c) { return deleteAll(c); }
  
  public boolean removeIf(Predicate<? super T> p) { return deleteIf(p); }
  
  public void removeRange(int start, int end) { deleteRange(start, end); }
  
  public void replace(int i, T u) { update(i,u); }
  
  public void replaceAll(UnaryOperator<T> op) { updateAll(op); }
  
  public void replaceAll(Predicate<T> p, UnaryOperator<T> op) { updateAll(p,op); }

  private void resize(int newSize) { 
    if (newSize < N) throw new IllegalArgumentException(
        "resize: newSize is less than current size");
    int m = newSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : newSize;
    @SuppressWarnings("unchecked")
    T[] temp = tclass != null ? ArrayUtils.ofDim(tclass,m) : (T[]) (new Object[m]);
    for (int i = 0; i < N; i++) temp[i] = a[i];
    a = temp; capacity = m;
  }

  public boolean retainAll(Collection<?> c) {
    if (c == null) return true;
    Set<?> set = new HashSet<>(c);
    for (T t : this) if (!set.contains(t)) remove(t);
    return true;
  }

  public Seq<T> reverse() {
    // return a new Seq<T> with the elements of this in reverse order
    if (isEmpty()) return new Seq<T>();
    return new Seq<T>(ArrayUtils.reverse(to()));
  }

  public Iterable<T> reverseIterable() { return () -> reverseIterator(); }

  public Iterator<T> reverseIterator() {  return new ReverseArrayIterator(); }

  public <R> Seq<R> reverseMap(Function<T, R> f) {
    // does map() and reverses the order of the elements
    if (isEmpty()) throw new InappropriateSeqException("reverseMap: Seq is empty");
    if (f == null) throw new IllegalArgumentException("reverseMap: Function is null");
    Seq<R> r = new Seq<>();
    for (int i = N; i > -1; i--) r.add(f.apply(a[i]));
    return r;
  }

  public boolean sameElements(Seq<T> s) {
    // return true if this has the same elements in the same order as s
    // else return false
    if (isEmpty()) throw new InappropriateSeqException("sameElements: Seq is empty");
    if (s == null) throw new IllegalArgumentException("sameElements: target Seq is null");
    if (s.isEmpty()) throw new IllegalArgumentException("sameElements: target Seq is empty");
    T[] b = to(); T[] c = s.to();
    return Arrays.deepEquals(b, c);
  }

  public static <U, V extends U> Seq<U> scan(Seq<V> s, U z, B1<U,U> op) {
    // returns a Seq<U> by doing a scanLeft of s where U is a superclass of T
    if (s.isEmpty()) return new Seq<U>();
    Seq<U> seq = new Seq<U>();
    int n = s.N;
    U u = z; 
    for (int i = 0; i < n; i++) { u = op.apply(u, s.a[i]); seq.add(u); }
    return seq;
  }


  public <R> Seq<R> scanLeft(R z, B1<T,R> op) {
    // return a Seq<R> containing an R[] of length size()+1 initialized with z at
    // index 0 and then filled left to right by foldLeft on the elements of this. 
    // z can be any R.
    if (isEmpty()) return new Seq<R>();
    Seq<R> seq = new Seq<R>(z);
    int n = N;
    R r = z; 
    for (int i = 0; i < n; i++) { r = op.apply(r, a[i]); seq.add(r); }
    return seq;
  }

  public <R> Seq<R> scanRight(R z, C1<T, R> op) {
    // return a Seq<R> containing an R[] of length size()+1 initialized with z at its
    // index N-1 and then filled right to left by foldRight on the elements of this. 
    // z can be any R.
    if (isEmpty()) return new Seq<R>();
    int n = N;
    R[] ra = ArrayUtils.ofDim(z.getClass(), n+1);
    R r = z;
    ra[n] = z;
    for (int i = n - 1; i > -1; i--) { r = op.apply(a[i], r); ra[i] = r; } 
    return new Seq<R>(ra);
  }

  public int segmentLength(Predicate<T> p, int start) {
    // returns the length of the longest segment of elements in this
    // satisfying p and starting at index start
    if (isEmpty()) return 0;
    if (start > N-1) throw new IllegalArgumentException(
        "segmentLength: start is > size()-1");
    T[] b = to(); int c = 0;
    for (int i = start; i < N; i++) if (p.test(b[i])) c++; else break;
    return c;
  }

  public T set(int i, T u) { return update(i, u); }
  
  public void showArray() {
    // print the elements of the entire array a.
    if (isEmpty()) return;
    for (T t : a) System.out.print(t+" "); System.out.println();
  }

  public void show() {
    // print the active elements of a (the first N) space-separated0
    for (int i = 0; i < N; i++) System.out.print(a[i]+" "); System.out.println();
  }

  public Seq<T> shuffle(Random r) {
    if (isEmpty()) return new Seq<T>();
    if (size() == 1) return new Seq<T>(first());
    T[] a = to();
    ArrayUtils.shuffle(a, r);
    return new Seq<T>(a);
  }

  public int size() { return N; }

  public Seq<T> slice(int from, int until) {
    // returns a Seq<T> containing the elements of this
    // with indices >= from and < until
    if (isEmpty()) throw new InappropriateSeqException("sliding: Seq is empty");
    if (from < 0) throw new IllegalArgumentException("slice: from is < 0");
    if (until <= from) throw new IllegalArgumentException("slice: from until is <= from");
    if (until > N) throw new IllegalArgumentException("slice: from until is > size()");  
    T[] b = to();
    for (int i = from; i < until; i++) b[i - from] = a[i];
    return new Seq<T>(b);
  }

  public Iterator<Seq<T>> sliding(int size) {
    // return an Iterator<Seq<T> over the elements of this using a
    // sliding window of length size and step 1
    if (isEmpty()) throw new InappropriateSeqException("sliding: Seq is empty");
    if (size < 1) throw new IllegalArgumentException("sliding: size is < 1");
    if (size > N) throw new IllegalArgumentException("sliding: size is > N");
    Iterator<T[]> it = new Sliding<T>(to(), size);
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };
  }

  public Iterator<Seq<T>> sliding(int size, int step) {
    // return an Iterator<Seq<T> over the elements of this using a
    // sliding window of length size and step step
    if (isEmpty()) throw new InappropriateSeqException("sliding: Seq is empty");
    if (size < 1) throw new IllegalArgumentException("sliding: size is < 1");
    if (size > N) throw new IllegalArgumentException("sliding: size is > N");
    if (step < 1) throw new IllegalArgumentException("sliding: step is < 1");
    Iterator<T[]> it = new Sliding<T>(a, size, step);
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };
  }

  public Stream<Seq<T>> slidingStream(int size) {
    // return an Stream<Seq<T> over the elements of this using a
    // sliding window of length size and step 1
    if (isEmpty()) throw new InappropriateSeqException("slidingStream: Seq is empty");
    if (size < 1) throw new IllegalArgumentException("slidingStream: size is < 1");
    if (size > N) throw new IllegalArgumentException("slidingStream: size is > N");
    Iterator<Seq<T>> i = sliding(size);  
    return Stream.generate(() -> i.next()).limit(slidingCount(N, size, 1));
  }

  public Stream<Seq<T>> slidingStream(int size, int step) {
    // return an Stream<Seq<T> over the elements of this using a
    // sliding window of length size and step step
    if (isEmpty()) throw new InappropriateSeqException("sliding: Seq is empty");
    if (size < 1) throw new IllegalArgumentException("sliding: size is < 1");
    if (size > N) throw new IllegalArgumentException("sliding: size is > N");
    if (step < 1) throw new IllegalArgumentException("sliding: step is < 1");
    Iterator<Seq<T>> i = sliding(size, step);
    return Stream.generate(() -> i.next()).limit(slidingCount(N, size, step));
  }

  public Seq<T> sortBy(Comparator<T> cmp) {
    // return a Seq<T> with elements of this sorted using c and nullsLast() 
    if (isEmpty()) return new Seq<T>();
    if (cmp == null) throw new IllegalArgumentException(
        "sort: "+tclass.getSimpleName()+" doesn't extend Comparable");
    T[] x = to(); Arrays.sort(x,cmp);
    return new Seq<T>(x);
  }

  public Seq<T> sorted() {
    // return a Seq<T> with nulls last sorted elements of this provided 
    // T extends Comparable
    //    System.out.println("sorted tclass="+tclass.getSimpleName());
    if (isEmpty()) return new Seq<T>();
    //    if (!Comparable.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
    //        "sorted: "+tclass.getSimpleName()+" doesn't extend Comparable");
    @SuppressWarnings("unchecked")
    Comparator<T> c = (t1, t2) -> { return ((Comparable<T>)t1).compareTo(t2); };
    T[] x = to(); Arrays.sort(x,nullsLast(c));
    //    System.out.println("sorted x compType="+x.getClass().getComponentType().getSimpleName());
    return new Seq<T>(x);
  }

  public Seq<T> sortWith(BiPredicate<T, T> b) {
    if (size() < 2) return new Seq<T>();
    if (b == null) throw new IllegalArgumentException("sortWith: BiPredicate is null");
    T[] d = to();
    Arrays.sort(d, (t1, t2) -> t1.equals(t2) ? 0 : (b.test(t1, t2) ? 1 : -1));
    return new Seq<T>(d);
  }

  public Seq<Seq<T>> span(Predicate<T> p) { return partition(p); }

  @SuppressWarnings("unchecked")
  public Seq<Seq<T>> splitAt(int n) {
    // this returns a Seq<Seq<T>> formed by splitting this at index n
    if (isEmpty()) throw new InappropriateSeqException("splitAt: Seq is empty");
    if (n < 0 || n > N - 1) throw new IllegalArgumentException(
        "splitAt: " + "index n isn't in this Seq");
    T[] b = tclass != null ? ArrayUtils.ofDim(tclass,N) : (T[]) (new Object[N]);
    int bindex = 0;
    T[] c = tclass != null ? ArrayUtils.ofDim(tclass,N) : (T[]) (new Object[N]);
    int cindex = 0;
    for (int i = 0; i < N; i++)
      if (i < n) b[bindex++] = a[i];
      else c[cindex++] = a[i];
    Seq<T>[] r = ArrayUtils.ofDim(Seq.class, 2);
    r[0] = new Seq<T>(ArrayUtils.take(b,bindex));
    r[1] = new Seq<T>(ArrayUtils.take(c,cindex));
    return new Seq<Seq<T>>(r);
  }

  public Spliterator<T> spliterator() { return iterable().spliterator(); }

  @SafeVarargs
  public final boolean startsWith(int offset, T... b) {
    // returns true if this contains b starting at offset
    if (isEmpty() || offset < 0 | b.length == 0 || offset + b.length > N) return false;
    for (int i = offset; i < offset + b.length; i++)
      if (!a[i].equals(b[i - offset])) return false;
    return true;
  }

  public List<T> subList(int fromIndex, int toIndex) {
    return slice(fromIndex,toIndex).toList();
  }

  public Seq<T> subSeq(int fromIndex, int toIndex) { return slice(fromIndex,toIndex); }

  public CharSequence subSequence(int start, int end) {
    // returns a CharSequence of the chars in this from index start through 
    // index end-1. if start < 0 it's corrected to 0 and |start| is added to 
    // end; and if end > N it's corrected to N
    if (isEmpty()) throw new InappropriateSeqException("subSequence: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "subSequence: Seq element type isn't Character");
    if (end <= start || start > N) return "".subSequence(0, 0);
    if (start < 0) { end -= start; start = 0; }
    if (end > a.length) end = a.length;
    char[] b = (char[]) unbox(to());
    char[] c = new char[end - start];
    for (int i = start; i < c.length; i++) c[i - start] = b[i];
    return String.valueOf(c).subSequence(0, c.length);
  }

  public double sum() {
    if (isEmpty()) throw new InappropriateSeqException("product: Seq is empty");
    if (!(tclass == Character.class || Number.class.isAssignableFrom(tclass)))
      throw new InappropriateSeqException(
          "product: Seq element class isn't Character and doesn't extend Number");
    double sum = 0.;
    if (tclass == Character.class)
      for (int i = 0; i < N; i++) sum += Character.digit((char) a[i], 10);
    else 
      for (int i = 0; i < a.length; i++) sum *= ((Number)a[i]).doubleValue();
    return sum;
  }

  public Seq<T> tail() {
    // return an Seq<T> with all elements of this except the first
    if (isEmpty()) throw new InappropriateSeqException("tail: Seq is empty");
    return new Seq<T>(copyOfRange(a, 1, N));
  }

  public Iterator<Seq<T>> tails() {
    // return an iterator of Seq<T> over the tails of the elements of this
    // where the first value is all the elements and subseqent values are
    // the results of repeated applications of tail()
    if (isEmpty()) throw new InappropriateSeqException("tails: Seq is empty");
    T[] d = to();
    T[] b = ArrayUtils.prepend(d, a[0]);
    T[][] c = ArrayUtils.ofDim(tclass, 1, b.length);
    for (int i = 0; i < b.length; i++) c[0][i] = b[i];
    Iterator<T[]> it = Stream.generate(() -> {
      c[0] = ArrayUtils.tail(c[0]);
      return c[0];
    }).limit(N+1).iterator();
    return new Iterator<Seq<T>>() {
      public boolean hasNext() { return it.hasNext(); }
      public Seq<T> next() { return new Seq<T>(it.next()); }
    };
  }

  public Seq<T> take(int n) {
    // return a Seq<T> with the 1st n elements of this 
    if (isEmpty()) return new Seq<T>();
    if (n <= 0) return new Seq<T>(copyOf(a,0));
    if (n >= N) return new Seq<T>(copyOf(a,N));
    return new Seq<T>(copyOf(a,n));
  }

  public Seq<T> takeRight(int n) {
    // return a Seq<T> with the last n elements of this 
    if (isEmpty()) return new Seq<T>(a.clone());
    if (n <= 0) return new Seq<T>(copyOf(a,0));
    if (n >= N) return new Seq<T>(copyOf(a,N));
    return new Seq<T>(copyOfRange(a,N-n,N));
  }

  public Seq<T> takeWhile(Predicate<T> p) {
    // return a Seq<T> with the longest prefix of elements satisfying p
    if (isEmpty()) return new Seq<T>(a.clone());
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) bindex++; else break;
    return new Seq<T>(copyOfRange(a,0,bindex));
  }

  public Class<?> tclass() { return tclass; }

  public T[] to() { return ArrayUtils.take(a,N); }

  public byte[] toByteArray() {
    // return a byte[] by serializing this
    byte[] b = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try {
      out = new ObjectOutputStream(bos); // implements ObjectOutput
      out.writeObject(this); 
      b = bos.toByteArray(); // this now serialized to a byte array 
    } catch (IOException ex) { ex.printStackTrace(); } 
    finally {
      try {
        if (out != null) out.close();
      } catch (IOException ex) { ex.printStackTrace(); }
      try {
        bos.close();
      } catch (IOException ex) { ex.printStackTrace(); }
    }
    return b;
  }
  
  public static <U> Seq<U> tabulate(int n, Function<Integer,U> f) {
    // return a 1D Seq with values of a Function over a range of Integer 
    // values starting at 0
    return new Seq<U>(ArrayUtils.tabulate(n, f));
  }
  
  @SuppressWarnings("unchecked")
  public static <U> Seq<Seq<U>> tabulate(int n1, int n2, BiFunction<Integer, Integer, U> f) {
    // return a 2D Seq with values of a Function over a ranges of Integer 
    // values starting at 0
    return (Seq<Seq<U>>)toNestedSeq(ArrayUtils.tabulate(n1,n2,f));
  }

  @SuppressWarnings("unchecked")
  public static <U> Seq<Seq<Seq<U>>> tabulate(int n1, int n2, int n3, 
      TriFunction<Integer, Integer, Integer, U> f) {
    // return a 3D Seq with values of a Function over a ranges of Integer 
    // values starting at 0
    return (Seq<Seq<Seq<U>>>)toNestedSeq(ArrayUtils.tabulate(n1,n2,n3,f));
  }
  
  @SuppressWarnings("unchecked")
  public static <U> Seq<Seq<Seq<Seq<U>>>> tabulate(int n1, int n2, int n3, 
      int n4, QuadFunction<Integer, Integer, Integer, Integer, U> f) {
    // return a 4D Seq with values of a Function over a ranges of Integer 
    // values starting at 0
    return (Seq<Seq<Seq<Seq<U>>>>)toNestedSeq(ArrayUtils.tabulate(n1,n2,n3,n4,f));
  }
  
  @SuppressWarnings("unchecked")
  public static <U> Seq<Seq<Seq<Seq<Seq<U>>>>> tabulate(int n1, int n2, int n3, 
      int n4, int n5, PentFunction<Integer, Integer, Integer, Integer, Integer, U> f) {
    // return a 5D Seq with values of a Function over a ranges of Integer 
    // values starting at 0
    return (Seq<Seq<Seq<Seq<Seq<U>>>>>)toNestedSeq(ArrayUtils.tabulate(n1,n2,n3,n4,n5,f));
  }
   
  public Object[] toArray() { 
    // required for List and Collection interfaces conformance
    return ArrayUtils.take(a,N); 
  }

  @SuppressWarnings("unchecked")
  public <E> E[] toArray(E[] ea) {
    // required for List and Collection interfaces conformance
    // based on java.util.ArrayList.toArray(T[] a)
   if (ea == null) throw new IllegalArgumentException("toArray: E[] is null");
   if (ea.length < N) return (E[]) Arrays.copyOf(a, N, ea.getClass());
   System.arraycopy(a, 0, ea, 0, N);
   if (ea.length > N) ea[N] = null;
   return ea;
  }
  
  public ByteBuffer toByteBuffer() {
    // return the elements of this wrapped in a ByteBuffer iff they're Bytes
    if (isEmpty()) throw new InappropriateSeqException("toByteBuffer: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "toByteBuffer: Seq element type isn't Byte");
    return java.nio.ByteBuffer.wrap((byte[])unbox(toArray()));
  }

  public CharBuffer toCharBuffer() {
    // return the elements of this wrapped in a CharBuffer iff they're Characters
    if (isEmpty()) throw new InappropriateSeqException("toCharBuffer: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "toCharBuffer: Seq element type isn't Character");
    return java.nio.CharBuffer.wrap((char[])unbox(toArray()));
  }

  public Collection<T> toCollection() { return (Collection<T>) Arrays.asList(to()); }

  public DoubleBuffer toDoubleBuffer() {
    // return the elements of this wrapped in a DoubleBuffer iff they're Doubles
    if (isEmpty()) throw new InappropriateSeqException("toDoubleBuffer: Seq is empty");
    if (tclass != Character.class) throw new InappropriateSeqException(
        "toDoubleBuffer: Seq element type isn't Double");
    return java.nio.DoubleBuffer.wrap((double[])unbox(toArray()));
  }

  public FloatBuffer toFloatBuffer() {
    // return the elements of this wrapped in a CharBuffer iff they're Floats
    if (isEmpty()) throw new InappropriateSeqException("toFloatBuffer: Seq is empty");
    if (tclass != Float.class) throw new InappropriateSeqException(
        "toFloatBuffer: Seq element type isn't Float");
    return java.nio.FloatBuffer.wrap((float[])unbox(toArray()));
  }

  public IntBuffer toIntBuffer() {
    // return the elements of this wrapped in an IntBuffer iff they're Integers
    if (isEmpty()) throw new InappropriateSeqException("toIntBuffer: Seq is empty");
    if (tclass != Integer.class) throw new InappropriateSeqException(
        "toIntBuffer: Seq element type isn't Integer");
    return java.nio.IntBuffer.wrap((int[])unbox(toArray()));
  }

  public Iterable<T> toIterable() { return iterable(); }

  public Iterator<T> toIterator() { return iterator(); }

  public List<T> toList() { return Arrays.asList(to()); }

  public ListIterator<T> toListIterator() { return listIterator(); }

  public ListIterator<T> toListIterator(int i) { return listIterator(i); }

  public LongBuffer toLongBuffer() {
    // return the elements of this wrapped in a LongBuffer iff they're Longs
    if (isEmpty()) throw new InappropriateSeqException("toLongBuffer: Seq is empty");
    if (tclass != Float.class) throw new InappropriateSeqException(
        "toLongBuffer: Seq element type isn't Long");
    return java.nio.LongBuffer.wrap((long[])unbox(toArray()));
  }

  public Map<Object,Object> toMap() {
    if (tclass != Tuple2.class) throw new InappropriateSeqException(
        "toMap: Seq doesn't have Tuple2 elements");
    Map<Object,Object> map = new HashMap<>();
    for (T t : this) {
      @SuppressWarnings("unchecked")
      Tuple2<Object,Object> x = (Tuple2<Object,Object>) t;
      map.put(x._1, x._2);   
    }
    return map;
  }
  
  public static Seq<?> toNestedSeq(Object o) {
    // returns a possibly nested Seq from any array with the
    // nesting depth equal to the array's dimensionality
    // cast returned Seq<?> as needed
    // for use in tabulate(), etc.
    if (o == null) throw new IllegalArgumentException("toNested: Object is null");
    if (!o.getClass().isArray()) throw new IllegalArgumentException(
        "toNestedList: o is not an array");
    Object[] b = (Object[]) o;
    int len = b.length;
    int dim = ArrayUtils.dim(b);
    Seq<?> s = new Seq<>();
    if (dim == 1) {
      for (int i = 0; i < len; i++) s.addObject(b[i]);
    } else {
      for (int i = 0; i < len; i++)
        s.addObject(toNestedSeq(b[i]));
    }
    return s;
  }

  public Set<T> toSet() {
    Set<T> set = new HashSet<>();
    for (T t : this) set.add(t);
    return set;
  }

  public Iterable<T> toReverseIterable() { return reverseIterable(); }

  public Iterator<T> toReverseIterator() { return reverseIterator(); } 

  public Seq<T> toSeq() {
    // return a new Seq<T> with a copy of the elements of this.
    return new Seq<T>(this);
  }

  public ShortBuffer toShortBuffer() {
    // return the elements of this wrapped in a ShortBuffer iff they're Shorts
    if (isEmpty()) throw new InappropriateSeqException("toShortBuffer: Seq is empty");
    if (tclass != Float.class) throw new InappropriateSeqException(
        "toShortBuffer: Seq element type isn't Short");
    return java.nio.ShortBuffer.wrap((short[])unbox(toArray()));
  }

  public Stream<T> toStream() { return StreamSupport.stream(spliterator(), false); }

  public Vector<T> toVector()  {  
    Vector<T> v = new Vector<>();
    for (T t : this) v.add(t);
    return v;
  }

  @Override
  public String toString() {
    if (isEmpty()) return "()";
    StringBuilder sb = new StringBuilder("(");
    for (int i = 0; i < N; i++) sb.append(a[i]+",");
    return sb.replace(sb.length()-1, sb.length(), ")").toString();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Seq<T> transpose() {
    // if this is a Seq<Seq<T>> return its transpose and is valid only for
    // Seq<Seq<T>s that have N elements in each sub-Seq
    if (isEmpty()) throw new InappropriateSeqException("transpose: Seq is empty");
    analyzeNesting();
    if (dim < 2) throw new InappropriateSeqException(
        "transpose: this Seq is not nested");
//    if (dim > 2) throw new InappropriateSeqException(
//        "transpose: this Seq is nested beyond depth 2");
    if (tclass == null) throw new InappropriateSeqException("transpose: Seq tclass == null");
//    if (rclass == null) throw new InappropriateSeqException("transpose: Seq rclass == null");
    if (!Seq.class.isAssignableFrom(tclass)) throw new InappropriateSeqException(
        "transpose: this Seq is not nested");
    Object[][] oa = new Object[N][N];
    for (int i = 0; i < N; i++) {
      oa[i] = ((Seq<?>)a[i]).toArray();
      if (oa[i].length != N) throw new InappropriateSeqException(
          "transpose: an element in this has size() != N");
    }
    if (dim == 2) {
      switch (tclass.getName()) {
      case "java.lang.Integer":
        {Integer[][] z = new Integer[N][N];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Integer[]{};
          else z[i] = ((Seq<Integer>)a[i]).to();
        Integer[][] y = new Integer[N][N];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Integer>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Integer>(y[i]);
        return (Seq<T>) new Seq<Seq<Integer>>(sa);}
      case "java.lang.Double":
        {Double[][] z = new Double[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Double[]{};
          else z[i] = ((Seq<Double>)a[i]).to();
        Double[][] y = new Double[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Double>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Double>(y[i]);
        return (Seq<T>) new Seq<Seq<Double>>(sa);}  
      case "java.lang.String":
        {String[][] z = new String[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new String[]{};
          else z[i] = ((Seq<String>)a[i]).to();
        String[][] y = new String[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<String>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<String>(y[i]);
        return (Seq<T>) new Seq<Seq<String>>(sa);}  
      case "java.lang.Character":
        {Character[][] z = new Character[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Character[]{};
          else z[i] = ((Seq<Character>)a[i]).to();
        Character[][] y = new Character[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Character>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Character>(y[i]);
        return (Seq<T>) new Seq<Seq<Character>>(sa);}   
      case "java.lang.Long":
        {Long[][] z = new Long[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Long[]{};
          else z[i] = ((Seq<Long>)a[i]).to();
        Long[][] y = new Long[N][]; 
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Long>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Long>(y[i]);
        return (Seq<T>) new Seq<Seq<Long>>(sa);}   
      case "java.lang.Byte":
        {Byte[][] z = new Byte[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Byte[]{};
          else z[i] = ((Seq<Byte>)a[i]).to();
        Byte[][] y = new Byte[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Byte>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Byte>(y[i]);
        return (Seq<T>) new Seq<Seq<Byte>>(sa);}  
      case "java.lang.Short":
        {Short[][] z = new Short[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Short[]{};
          else z[i] = ((Seq<Short>)a[i]).to();
        Short[][] y = new Short[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++)  y[j][i] = z[i][j];
        Seq<Short>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Short>(y[i]);
        return (Seq<T>) new Seq<Seq<Short>>(sa);}  
      case "java.lang.Float":
        {Float[][] z = new Float[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Float[]{};
          else z[i] = ((Seq<Float>)a[i]).to();
        Float[][] y = new Float[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Float>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Float>(y[i]);
        return (Seq<T>) new Seq<Seq<Float>>(sa);}  
      case "java.lang.Boolean":
        {Boolean[][] z = new Boolean[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Boolean[]{};
          else z[i] = ((Seq<Boolean>)a[i]).to();
        Boolean[][] y = new Boolean[N][];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<Boolean>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq<Boolean>(y[i]);
        return (Seq<T>) new Seq<Seq<Boolean>>(sa);} 
      default: 
        {Object[][] z = new Object[N][];
        for (int i = 0; i < N; i++)
          if (a[i] == null) z[i] = new Object[]{};
          else z[i] = ((Seq<?>)a[i]).to();
        Object[][] y = new Object[N][N];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
        Seq<?>[] sa = ArrayUtils.ofDim(Seq.class, N);
        for (int i = 0; i < N; i++) sa[i] = new Seq(y[i]);
        return (Seq<T>) new Seq<Seq<?>>(sa);}
      }
    } else { // > 2D   
      {Seq<?>[][] z = ArrayUtils.ofDim(Seq.class, N, 0);
      for (int i = 0; i < N; i++)
        if (a[i] == null) z[i] = ArrayUtils.ofDim(Seq.class,0);
        else  z[i] = ((Seq<Seq<?>>)a[i]).to();
      Seq<?>[][] y = ArrayUtils.ofDim(Seq.class, N, N);
      for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) y[j][i] = z[i][j];
      Seq<Seq<?>>[] sa = ArrayUtils.ofDim(Seq.class, N);
      for (int i = 0; i < N; i++) sa[i] = new Seq<Seq<?>>(y[i]);
      return (Seq<T>) new Seq<Seq<Seq<?>>>(sa);}   
    }
  }

  public <U> Seq<Tuple2<T,U>> zip(Seq<U> u) {
    // returns a Seq<Tuple2<T,U>> formed from corresponding elements 
    // of this and u and with size the least of the two
    if (isEmpty() || u == null || u.isEmpty()) return new Seq<Tuple2<T,U>>();
    int size = N, usize = u.N, len = size <= usize ? size : usize;
    Tuple2<T,U>[] tu = ArrayUtils.ofDim(Tuple2.class, len);
    for (int i = 0; i < len; i++) tu[i] = new Tuple2<T,U>(a[i], u.a[i]);
    return new Seq<Tuple2<T,U>>(tu);
  }

  @SafeVarargs
  public final Seq<T> union(T...b) {
    // return a new array with the unique elements in a and b 
    return new Seq<T>(new HashSet<T>(toList()));
  }

  public static <U,V> Tuple2<Seq<U>, Seq<V>> unzip(Seq<Tuple2<U, V>> t) {
    // return a tuple of Seqs from a Seq of tuples
    if (t == null)  return new Tuple2<Seq<U>,Seq<V>>(null,null);
    int len = t.N;
    Class<?> uclass = null;
    Class<?> vclass  = null;
    for (int i = 0; i < len; i++) {
      if (uclass == null && t.a[i]._1 != null) uclass = t.a[i]._1.getClass();
      if (uclass == null && t.a[i]._2 != null) uclass = t.a[i]._2.getClass();
      if (uclass != null && uclass != null) break;
    }
    @SuppressWarnings("unchecked")
    U[] a = uclass != null ? ArrayUtils.ofDim(uclass,len ) : (U[]) new Object[len];
    @SuppressWarnings("unchecked")
    V[] b = vclass != null ? ArrayUtils.ofDim(vclass,len ) : (V[]) new Object[len];
    for (int i = 0; i < len; i++) {
      a[i] = uclass != null ? t.a[i]._1 : null;
      b[i] = vclass != null ? t.a[i]._2 : null;
    }
    return new Tuple2<Seq<U>,Seq<V>>(new Seq<U>(a), new Seq<V>(b));
  } 

  public static <U, V, W> Tuple3<Seq<U>,Seq<V>,Seq<W>> unzip3(Seq<Tuple3<U,V,W>> t) {
    // return a tuple of Seqs from a Seq of tuples
    if (t == null) return new Tuple3<Seq<U>,Seq<V>,Seq<W>>(null,null,null);
    int len = t.N;
    Class<?> aclass = null;
    Class<?> bclass = null;
    Class<?> cclass = null;
    for (int i = 0; i < len; i++) { 
      if (aclass == null && t.a[i]._1 != null) aclass = t.a[i]._1.getClass();
      if (bclass == null && t.a[i]._2 != null) bclass = t.a[i]._2.getClass();
      if (cclass == null && t.a[i]._3 != null) cclass = t.a[i]._3.getClass();
      if (aclass != null && bclass != null && cclass != null) break;
    }
    @SuppressWarnings("unchecked")
    U[] a = aclass != null ? ArrayUtils.ofDim(aclass,len ) : (U[]) new Object[len];
    @SuppressWarnings("unchecked")
    V[] b = bclass != null ? ArrayUtils.ofDim(bclass,len ) : (V[]) new Object[len];
    @SuppressWarnings("unchecked")
    W[] c = cclass != null ? ArrayUtils.ofDim(cclass,len ) : (W[]) new Object[len];
    for (int i = 0; i < len; i++) {
      a[i] = aclass != null ? t.a[i]._1 : null;
      b[i] = bclass != null ? t.a[i]._2 : null;
      c[i] = cclass != null ? t.a[i]._3 : null;
    }
    return new Tuple3<Seq<U>,Seq<V>,Seq<W>>(new Seq<U>(a), new Seq<V>(b), new Seq<W>(c));
  }

  public T update(int i, T u) {
    // change the value of this.a[i] to u in place and return the previous value
    if (isEmpty()) throw new InappropriateSeqException("update: Seq is empty");
    if (i < 0 || i > N - 1) throw new SeqIndexOutOfBoundsException("update: index i not in Seq");
    T t = a[i];
    a[i] = u;
    return t;
  }
  
  public boolean updateAll(Predicate<T> p, UnaryOperator<T> op) {
    if (op == null) throw new IllegalArgumentException("replaceAll: UnaryOperator is null");
    boolean change = false;
    for (int i = 0; i < N; i++) if (p.test(a[i])) { a[i] = op.apply(a[i]); change = true; }
    return change;
  }
  
  public boolean updateAll(UnaryOperator<T> op) {
    if (op == null) throw new IllegalArgumentException("replaceAll: UnaryOperator is null");
    boolean change = false;
    for (int i = 0; i < N; i++) { a[i] = op.apply(a[i]); change = true; }
    return change;
  }

  public Seq<T> updated(T u, int p) {
    // return a copy of this updated by changing its a[p] to u (this is not updated)
    if (isEmpty()) throw new InappropriateSeqException("updated: Seq is empty");
    if (p < 0 || p > N - 1) throw new SeqIndexOutOfBoundsException("updated: index p not in Seq");
    Seq<T> b = this.clone();
    b.a[p] = u;
    return b;
  }

  public Seq<T> view(int start, int end) {
    // returns the subsequence of elements in this from index start through 
    // index end-1. if start < 0 it's corrected to 0 and |start| is added to 
    // end; and if end > N it's corrected to N
    if (isEmpty()) throw new InappropriateSeqException("subSequence: Seq is empty");
    if (end <= start || start > N) return new Seq<T>();
    if (start < 0) { end -= start; start = 0; }
    if (end > a.length) end = a.length;
    T[] b = ArrayUtils.ofDim(tclass, end - start);
    for (int i = start; i < end; i++) b[i - start] = a[i];
    return new Seq<T>(b);
  }

  public <U> Seq<Tuple2<T,U>> zip(Collection<U> u) {
    // return a Seq<Tuple2<T,U>> formed from corresponding elements
    // of this and u with size the least of the two
    if (isEmpty() || u == null || u.isEmpty()) return new Seq<Tuple2<T,U>>();
    int size = N, usize = u.size(), len = size <= usize ? size : usize;
    Tuple2<T,U>[] tu = ArrayUtils.ofDim(Tuple2.class, len);
    Iterator<U> it = u.iterator(); int i = 0;
    while (i < len) tu[i] = new Tuple2<T,U>(a[i++], it.next());
    return new Seq<Tuple2<T,U>>(tu);
  }

  public <U> Seq<Tuple2<T,U>> zipAll(Seq<U> u, T pt, U pu) {
    // return a Seq<Tuple2<T,U>> formed from corresponding elements
    // of this and u with length the longest of the two and placeholder
    // pt or pb used to fill missing elements of the shortest
    if (isEmpty() && u == null || u.isEmpty()) return new Seq<Tuple2<T,U>>();
    int size = N, usize = u.size(), len = size >= usize ? size : usize;
    Tuple2<T,U>[] tu = ArrayUtils.ofDim(Tuple2.class, len);
    for (int i = 0; i < len; i++)
      tu[i] = new Tuple2<T,U>(i < size ? a[i] : pt, i < usize ? u.a[i] : pu);
    return new Seq<Tuple2<T,U>>(tu);
  }

  public <U> Seq<Tuple2<T,U>> zipAll(Collection<U> u, T pt, U pu) {
    // return a Seq<Tuple2<T,U>> formed from corresponding elements
    // of this and u with length the longest of the two and placeholder
    // pt or pb used to fill missing elements of the shortest
    if (isEmpty() && u == null || u.isEmpty()) return new Seq<Tuple2<T,U>>();
    int size = N, usize = u.size(), len = size >= usize ? size : usize;
    Tuple2<T,U>[] tu = ArrayUtils.ofDim(Tuple2.class, len);
    Iterator<U> it = u.iterator();
    int i = 0;
    while (i < len)
      tu[i] = new Tuple2<T,U>(i < size ? a[i] : pt, i++ < usize ? it.next() : pu);
    return new Seq<Tuple2<T,U>>(tu);
  }

  private class ArrayIterator implements Iterator<T> {
    T[] b = to();
    private int i = 0;
    public boolean hasNext() { return i < b.length; }
    public T next() { return b[i++]; }
    public void remove() {}
  }

  private class ReverseArrayIterator implements Iterator<T> {
    T[] b = to();
    private int i = b.length;
    public boolean hasNext() { return i > 0; }
    public T next() { return b[--i]; }
    public void remove() {}
  }

  public static void main(String[] args) {

    System.out.println("analyzeNesting tests");
    Seq<Seq<Integer>> s02 = new Seq<>(new Seq<Integer>(new Integer[]{null}));
    s02 = new Seq<>(new Seq<Integer>(new Integer[]{null,null}));
    System.out.println(s02);
    s02.analyzeNesting();
    System.out.println("s02.dim="+s02.dim);
    System.out.println("s02.rclass="+s02.rclass);
    System.out.println("s02.tclass="+s02.tclass);
    System.out.println("s02.className="+s02.getClass().getName());
    
    System.out.println("constructor and basic ops tests");
    Seq<Seq<Integer>> s30 = new Seq<>();
    Seq<Seq<Integer>> s31 =  new Seq<>(new Seq<Integer>(new Integer[]{null}));
    Seq<Seq<Integer>> s32 = new Seq<>(new Seq<Integer>(new Integer[]{null,1,null}));
    Seq<Seq<Seq<Integer>>> s03 = new Seq<>(null,s30,s31,s32);
    System.out.println("\n"+s03);
    s03.analyzeNesting();
    System.out.println("s03.dim="+s03.dim);
    System.out.println("s03.rclass="+s03.rclass);
    System.out.println("s03.tclass="+s03.tclass);
    System.out.println("s03.className="+s03.getClass().getName());

    Seq<Integer> s = new Seq<>(rangeInteger(1,6));
    System.out.println("s.size="+s.size());
    System.out.println("s.capacity="+s.capacity);
    System.out.println("s.add(6);"); s.add(6);
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.capacity="+s.capacity);
    System.out.println("s.remove();"); s.remove();
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.capacity="+s.capacity);
    System.out.println("s.delete(4)="+s.delete(4));
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.delete(1)="+s.delete(1));
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.add(5);"); s.add(5);
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.delete(0)="+s.delete(0));   
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.delete(0)="+s.delete(0));   
    s.showArray();
    System.out.println("s.size="+s.size());
    System.out.println("s.delete(0)="+s.delete(0));   
    s.showArray();   
    System.out.println("s.size="+s.size());
    System.out.println("s.delete(0)="+s.delete(0));   
    s.showArray();          
    System.out.println("s.size="+s.size());
    //    System.out.println("s.delete(0)="+s.delete(0));   

    System.out.println("s.add(7);"); s.add(7);
    s.showArray();          
    System.out.println("s.size="+s.size());
    System.out.println("s.add(8);"); s.add(8);
    s.showArray();          
    System.out.println("s.size="+s.size());
    System.out.println("s.add(9);"); s.add(9);
    s.showArray();          
    System.out.println("s.size="+s.size());
    System.out.println("s.add(1);"); s.add(1);
    s.showArray();          
    System.out.println("s.size="+s.size());
    System.out.println("s.add(2);"); s.add(2);
    s.showArray();          
    System.out.println("s.size="+s.size());
    System.out.println("s.remove(9);"); s.remove(new Integer(9));
    s.showArray();          
    System.out.println("s.size="+s.size());
    s.add(0,9); s.add(2,9); s.add(9); System.out.println("s.add(0,9); s.add(2,9); s.add(9);");
    s.add(1,null); s.add(3,null); s.add(null); System.out.println("s.add(1,9); s.add(3,9); s.add(null);");
    s.showArray();          
    System.out.println("s.size="+s.size());
    
    System.out.println("removeAll demo");
    System.out.println("s.removeAll(9)="+s.removeAll(9));
    s.showArray(); par(s.toArray()); s.show();          
    System.out.println("s.size="+s.size());
    //    System.out.println("s.clear()"); s.clear();
    //    s.showArray(); par(s.toArray());           
    //    System.out.println("s.size="+s.size());
    
    System.out.println("containsSlice demo");
    System.out.println(s.containsSlice(null,7,null));
    
    System.out.println("sorted demo");
    System.out.println(s.sorted());
    par(s.toArray());
    System.out.println(s);
    
    System.out.println("intersect demo");
    System.out.println("intersect="+s.intersect(new Seq<Integer>(2,null,8,null,9)));
    
    System.out.println("intersectMultiset demo");
    System.out.println("intersectMultiset="+s.intersectMultiset(new Seq<Integer>(2,null,8,null,9)));
   
    System.out.println("dim test");
    Seq<Integer> s1 = new Seq<>(new Integer[]{1});  
    Seq<Integer> s2 = new  Seq<>(new Integer[]{2});
    Seq<Integer> s3 = new  Seq<>(new Integer[]{3});
    Seq<Integer> s4 = new Seq<>(new Integer[]{4});  
    Seq<Integer> s5 = new  Seq<>(new Integer[]{5});
    Seq<Integer> s6 = new  Seq<>(new Integer[]{6});
    Seq<Integer> s7 = new Seq<>(new Integer[]{7});  
    Seq<Integer> s8 = new  Seq<>(new Integer[]{8});
    Seq<Integer> s9 = new  Seq<>(new Integer[]{9});
    Seq<Seq<Integer>> ssa = new Seq<>(s1,s2,s3);
    System.out.println("ssa.dim="+ssa.dim());
    System.out.println("ssa.rclass="+ssa.rclass);
    System.out.println("ssa.tclass="+ssa.tclass);
    
    System.out.println("toArrayObject 2D demo"); par(toArrayObject(ssa)); 
    System.out.println("ssa = "+ssa);
    Seq<Seq<Integer>> ssb = new Seq<>(s4,s5,s6);
    Seq<Seq<Integer>> ssc = new Seq<>(s7,s8,s9);
    Seq<Seq<Seq<Integer>>> sw = new Seq<>(ssa,ssb,ssc);
    
    System.out.println("toArrayObject 3D demo"); 
    par(toArrayObject(sw));
    System.out.println("sw = "+sw);
    
    System.out.println("printClasses test");
    sw.printClasses();
    
    System.out.println("analyzeNesting 3D test");
    Seq<Seq<Seq<Integer>>> sv = new Seq<>();
    sv.analyzeNesting(); System.out.println("sv.dim="+sv.dim);
    System.out.println("ssa="+ssa);
    
    System.out.println("flatten demo"); 
    System.out.println("ssa before flatten="+ssa);
    System.out.println("ssa after flatten="+ssa.flatten());
    
    System.out.println("flatline demo"); 
    System.out.println("sw before flatline="+sw);
    System.out.println("sw after flatline="+sw.flatline());
    
    System.out.println("zip demo");
    Seq<Integer> sp = new Seq<>(0,1,null,3,4); 
    Seq<Integer> sq = new Seq<>(5,6,7,null,9,8,2);
    System.out.println(sp.zip(sq));
    
    System.out.println("zip Seq with List demo");
    List<Integer> list = new ArrayList<>(Arrays.asList(4,3,5,1,9,7,2,8));
    System.out.println(sp.zip(list)); 
    
    System.out.println("zipall demo");
    System.out.println(sp.zipAll(sq,-1,5));
    
    System.out.println("zipall Seq with List demo");
    System.out.println(sp.zipAll(list,-1,5));
    
    System.out.println("scanRight demo");
    Seq<Integer> sr = new Seq<>(1,2,3,4);
    System.out.println(sr.scanRight(0,(x,y) -> x+y));
    
    System.out.println("scanLeft demo");
    System.out.println(sr.scanLeft(0,(x,y) -> x+y));
    
    System.out.println("combinations demo:");
    Iterator<Seq<Integer>> it = sr.combinations(2);
    while (it.hasNext()) System.out.println(it.next());
    Stream<Seq<Integer>> stc = sr.combinationStream(3);
    stc.forEach(System.out::println);
    
    System.out.println("permutations demo:");
    Iterator<Seq<Integer>> ip = sr.permutations();
    while (ip.hasNext()) System.out.println(ip.next());
    Stream<Seq<Integer>> stp = sr.permutationStream();
    stp.forEach(System.out::println);
    
    System.out.println("clone tests 1D:");
    Seq<Integer> srclone = sr.clone();
    assert sr.equals(srclone);
    assert Arrays.equals(sr.a, srclone.a);
    assert sr.N == srclone.N;
    assert sr.capacity == srclone.capacity;
    assert sr.rclass == srclone.rclass;
    assert sr.tclass == srclone.tclass;
    assert sr.dim == srclone.dim;
    
    System.out.println("clone tests 2D:");
    Seq<Seq<Integer>> ssaclone = ssa.clone();
    assert ssa.equals(ssaclone);
    Integer[][] ssaAr = (Integer[][])ssa.toArrayObject();
    Integer[][] ssacloneAr = (Integer[][])ssaclone.toArrayObject();
    assert Arrays.deepEquals(ssaAr, ssacloneAr);
    assert ssa.N == ssaclone.N;
    assert ssa.capacity == ssaclone.capacity;
    assert ssa.rclass == ssaclone.rclass;
    assert ssa.tclass == ssaclone.tclass;
    assert ssa.dim == ssaclone.dim;
    
    System.out.println("clone tests 3D:");
    Seq<Seq<Seq<Integer>>> swclone = sw.clone();
    assert sw.equals(swclone);
    Integer[][][] swAr = (Integer[][][])sw.toArrayObject();
    Integer[][][] swcloneAr = (Integer[][][])swclone.toArrayObject();
    assert Arrays.deepEquals(swAr, swcloneAr);
    assert sw.N == ssaclone.N;
    assert sw.capacity == swclone.capacity;
    assert sw.rclass == swclone.rclass;
    assert sw.tclass == swclone.tclass;
    assert sw.dim == swclone.dim;
    System.out.println("toByteArray and fromByteArray demo");
    byte[] b = sr.toByteArray();
    System.out.println(sr.fromByteArray(b));
    
    System.out.println("transpose 2D demo");
    Seq<Integer> ss1 = new Seq<>(new Integer[]{1,2,3});  
    Seq<Integer> ss2 = new Seq<>(new Integer[]{4,5,6});
    Seq<Integer> ss3 = new Seq<>(new Integer[]{7,8,9});
    Seq<Seq<Integer>> sss = new Seq<>(ss1,ss2,ss3);
    System.out.println("original = "+sss);
    System.out.println("transpose = "+sss.transpose());
    System.out.println("transpose 2D with some nulls demo");
    Seq<Integer> ss1n = new Seq<>(new Integer[]{null,2,3});  
    Seq<Integer> ss2n = new Seq<>(new Integer[]{4,null,6});
    Seq<Integer> ss3n = new Seq<>(new Integer[]{7,8,null});
    Seq<Seq<Integer>> sssn = new Seq<>(ss1n,ss2n,ss3n);
    System.out.println("original = "+sssn);
    System.out.println("transpose = "+sssn.transpose());
    
    System.out.println("transpose 3D demo");
    Seq<Integer> ss4 = new Seq<>(new Integer[]{10,11,12});
    Seq<Integer> ss5 = new Seq<>(new Integer[]{13,14,15});
    Seq<Integer> ss6 = new Seq<>(new Integer[]{16,17,18});
    Seq<Integer> ss7 = new Seq<>(new Integer[]{19,20,21});
    Seq<Integer> ss8 = new Seq<>(new Integer[]{22,23,24});
    Seq<Integer> ss9 = new Seq<>(new Integer[]{25,26,27});
    Seq<Seq<Integer>> sss1 = new Seq<>(ss1,ss2,ss3);
    Seq<Seq<Integer>> sss2 = new Seq<>(ss4,ss5,ss6);
    Seq<Seq<Integer>> sss3 = new Seq<>(ss7,ss8,ss9);
    Seq<Seq<Seq<Integer>>> ssss = new Seq<>(sss1,sss2,sss3);
    System.out.println("original = "+ssss);
    System.out.println("transpose = "+ssss.transpose());
    
    System.out.println("transpose 4D demo");
    Seq<Integer> ss10 = new Seq<>(new Integer[]{28,29,30});
    Seq<Integer> ss11 = new Seq<>(new Integer[]{31,32,33});
    Seq<Integer> ss12 = new Seq<>(new Integer[]{34,35,36});
    Seq<Integer> ss13 = new Seq<>(new Integer[]{37,38,39});
    Seq<Integer> ss14 = new Seq<>(new Integer[]{40,41,42});
    Seq<Integer> ss15 = new Seq<>(new Integer[]{43,44,45});
    Seq<Integer> ss16 = new Seq<>(new Integer[]{46,47,48});
    Seq<Integer> ss17 = new Seq<>(new Integer[]{49,50,51});
    Seq<Integer> ss18 = new Seq<>(new Integer[]{52,53,54});
    Seq<Integer> ss19 = new Seq<>(new Integer[]{55,56,57});
    Seq<Integer> ss20 = new Seq<>(new Integer[]{58,59,60});
    Seq<Integer> ss21 = new Seq<>(new Integer[]{61,62,63});
    Seq<Integer> ss22 = new Seq<>(new Integer[]{64,65,66});
    Seq<Integer> ss23 = new Seq<>(new Integer[]{67,68,69});
    Seq<Integer> ss24 = new Seq<>(new Integer[]{70,71,72});
    Seq<Integer> ss25 = new Seq<>(new Integer[]{73,74,76});
    Seq<Integer> ss26 = new Seq<>(new Integer[]{76,77,78});
    Seq<Integer> ss27 = new Seq<>(new Integer[]{79,80,81});
    Seq<Seq<Integer>> sss4 = new Seq<>(ss10,ss11,ss12);
    Seq<Seq<Integer>> sss5 = new Seq<>(ss13,ss14,ss15);
    Seq<Seq<Integer>> sss6 = new Seq<>(ss16,ss17,ss18);
    Seq<Seq<Integer>> sss7 = new Seq<>(ss19,ss20,ss21);
    Seq<Seq<Integer>> sss8 = new Seq<>(ss22,ss23,ss24);
    Seq<Seq<Integer>> sss9 = new Seq<>(ss25,ss26,ss27);
    Seq<Seq<Seq<Integer>>> ssss1 = new Seq<>(sss1,sss2,sss3);
    Seq<Seq<Seq<Integer>>> ssss2 = new Seq<>(sss4,sss5,sss6);
    Seq<Seq<Seq<Integer>>> ssss3 = new Seq<>(sss7,sss8,sss9);
    Seq<Seq<Seq<Seq<Integer>>>> sssss = new Seq<>(ssss1,ssss2,ssss3);
    System.out.println("original = "+sssss);
    System.out.println("transpose = "+sssss.transpose());
/*
  original = 
  ((((1,2,3),(4,5,6),(7,8,9)),((10,11,12),(13,14,15),(16,17,18)),((19,20,21),(22,23,24),(25,26,27))),
  (((28,29,30),(31,32,33),(34,35,36)),((37,38,39),(40,41,42),(43,44,45)),((46,47,48),(49,50,51),(52,53,54))),
  (((55,56,57),(58,59,60),(61,62,63)),((64,65,66),(67,68,69),(70,71,72)),((73,74,76),(76,77,78),(79,80,81))))
  nesting p[0]=4
  transpose = 
  ((((1,2,3),(4,5,6),(7,8,9)),((28,29,30),(31,32,33),(34,35,36)),((55,56,57),(58,59,60),(61,62,63))),
  (((10,11,12),(13,14,15),(16,17,18)),((37,38,39),(40,41,42),(43,44,45)),((64,65,66),(67,68,69),(70,71,72))),
  (((19,20,21),(22,23,24),(25,26,27)),((46,47,48),(49,50,51),(52,53,54)),((73,74,76),(76,77,78),(79,80,81))))
*/
    
    
    
    
    System.out.println("tails demo");
    Seq<Integer> st = new Seq<Integer>(1,2,3,4,5,6,7);
    Iterator<Seq<Integer>> ita = st.tails();
    while(ita.hasNext()) System.out.println(ita.next());
    
    System.out.println("inits demo");
    ita = st.inits();
    while(ita.hasNext()) System.out.println(ita.next());
    
    System.out.println("iterate demo");
    System.out.println(iterate(3,5,x->2*x));
    
    System.out.println("partition vs span demo");
    System.out.println(st.span(x->x>3));
    System.out.println(st.partition(x->x>3));
    
    System.out.println("sortWith demo");
    Seq<String> sstr = new  Seq<String>("Steve", "Tom", "John", "Bob");
    System.out.println(sstr.sortWith((x,y)-> {return x.compareTo(y) > 1 ? true : false;}));
    
    System.out.println("sliding demo");
    ita = st.sliding(3);
    while(ita.hasNext()) System.out.println(ita.next());
    
    System.out.println("slidingStream demo with size 3 step1");
    Stream<Seq<Integer>> stm2 = st.slidingStream(3);
    stm2.forEach(System.out::println);
    
    System.out.println("slidingStream demo with size 3 step2");
    stm2 = st.slidingStream(3,2);
    stm2.forEach(System.out::println);
    
    System.out.println("segmentLength demo");
    System.out.println(st.segmentLength((q)-> q > 3,3));
    
    System.out.println("prependAll<Collection> demo:");
    Seq<Integer> stpa = new Seq<Integer>(5,6,7);
    System.out.println(stpa);
    List<Integer> list2 = new ArrayList<>(Arrays.asList(1,2,3,4));
    System.out.println(stpa.prependAll(list2));
    System.out.println(stpa);
    
    System.out.println("reverseIterator demo");
    Iterator<Integer> itt = stpa.reverseIterator();
    while(itt.hasNext()) System.out.print(itt.next()); System.out.println();
    
    System.out.println("padTo demo");
    Seq<Integer> stpb = new Seq<Integer>(1,2,3);
    System.out.println(stpb);
    System.out.println(stpb.padTo(7, 9));
    
    System.out.println("iterator and reverseIterator testing:");
    Seq<Integer> se = new Seq<Integer>(new Integer[]{1,2,3});
    Iterator<Integer> itr = se.iterator();
    while(itr.hasNext()) System.out.print(itr.next()); System.out.println();
    itr = se.reverseIterator();
    while(itr.hasNext()) System.out.print(itr.next()); System.out.println();
    
    System.out.println("toList testing");
    System.out.println(se.toList());
    se.clear(); System.out.println(se.toList());
    se = new Seq<Integer>();  System.out.println(se.toList());
    
    System.out.println("construction from Collection test");
    se = new Seq<Integer>(new ArrayList<>(Arrays.asList(1,2,3,4)));
    System.out.println(se);
    se = new Seq<Integer>(new Seq<Integer>(new ArrayList<>(Arrays.asList(1,2,3,4))));
    System.out.println(se);
    
    System.out.println("lastIndexOf test");
    se = new Seq<Integer>(new ArrayList<>(Arrays.asList(1,2,3,1,2,3,1,2,3)));
    System.out.println(se.lastIndexOf(1));
    System.out.println(se.lastIndexOf(new Integer(1),7));
    
    System.out.println("lastIndexOfSlice test");
    System.out.println(se.lastIndexOfSlice(1,2,3));
    System.out.println(se.lastIndexOfSlice(new Integer[]{1,2,3},5));
    
    System.out.println("indexOfSlice tests");
    System.out.println(se.indexOfSlice(1,2,3));
    se = new Seq<Integer>(new ArrayList<>(Arrays.asList(7,7,7,7,7,7,1,2,3)));
    System.out.println(se.indexOfSlice(1,2,3));
    System.out.println(se.indexOfSlice(new Integer[]{1,2,3}, 6));
    
    System.out.println("indexOf tests");
    se = new Seq<Integer>(new ArrayList<>(Arrays.asList(1,2,3,7,1,2,3,7,7)));
    System.out.println(se.indexOf(7));
    System.out.println(se.indexOf(new Integer(7), 8));
    
    System.out.println("grouped demo");
    Iterator<Seq<Integer>> iss = se.grouped(2);
    while(iss.hasNext()) System.out.print(iss.next()+" "); System.out.println();
    se = new Seq<Integer>(new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10)));
    iss = se.grouped(3);
    while(iss.hasNext()) System.out.print(iss.next()+" "); System.out.println();
    
    System.out.println("distinct demo");
    Seq<Integer>sed = new Seq<Integer>(1,1,1,2,2,2,3,3,3,4,4,4,5,5,5);
    System.out.println(sed.distinct());
    sed = new Seq<Integer>();
    System.out.println(sed.distinct());
    
    System.out.println("aggregate demo");   
    Character[] ca = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
        'n','o','p','q','r','s','t','u','v','w','x','y','z'};
    Seq<Character> sec = new Seq<>(ca);
    System.out.println(sec.aggregate(0, (sum,ch)->sum+(int)ch, (p1,p2)->p1+p2, 7));
    
    System.out.println("copy test");
    Seq<Integer> aa = new Seq<>(9,7,8,17,1,2,3,6,14,22);
    System.out.println("aa="+aa);
    Seq<Integer> bb = new Seq<>(0,9,7,11,4,5,6,11,12);
    System.out.println("bb="+bb);
    copy(aa,4,bb,1,3);
    System.out.println("after copy bb="+bb);
    
    System.out.println("fill tests");
    System.out.println(fill(3, ()->3));
    System.out.println(fill(3, 3, ()->3));
    System.out.println(fill(3, 3, 3, ()->3));
    System.out.println(fill(3, 3, 3, 3, ()->3));
    System.out.println(fill(3, 3, 3, 3, 3, ()->3));
    
    System.out.println("toNestedSequence test");
    Integer[][][] www = new Integer[4][][];
    
    Integer[] w00 = {1,2};
    Integer[] w01 = {3,4};
    Integer[] w02 = {5,6};
    
    Integer[] w10 = {7,8};
    Integer[] w11 = {9,10};
    Integer[] w12 = {11,12};
    
    Integer[] w20 = {13,14};
    Integer[] w21 = {15,16};
    Integer[] w22 = {17,18};
    
    Integer[] w30 = {19,20};
    Integer[] w31 = {21,22};
    Integer[] w32 = {23,24};
    
    Integer[][] w1 = {w00,w01,w02};
    Integer[][] w2 = {w10,w11,w12};
    Integer[][] w3 = {w20,w21,w22};
    Integer[][] w4 = {w30,w31,w32};
    
    www[0] = w1;
    www[1] = w2;
    www[2] = w3;
    www[3] = w4;
    @SuppressWarnings("unchecked")
    Seq<Seq<Seq<Integer>>> s3d = (Seq<Seq<Seq<Integer>>>)toNestedSeq(www);
    System.out.println(s3d);
    
    System.out.println("tabulate tests");
    System.out.println("tabulate(5,x->x*2):");
    System.out.println(tabulate(5,x->x*2));
    System.out.println("tabulate(2,1,(x,y)->x+y)");
    System.out.println(tabulate(2,1,(x,y)->x+y));
    System.out.println("tabulate(3,2,1,(x,y,z)->x+y+z)");
    System.out.println(tabulate(3,2,1,(x,y,z)->x+y+z));
    System.out.println("tabulate(4,3,2,1,(w,x,y,z)->w+x+y+z)");
    System.out.println(tabulate(4,3,2,1,(w,x,y,z)->w+x+y+z));
    System.out.println("tabulate(5,4,3,2,1,(v,w,x,y,z)->v+w+x+y+z)");
    System.out.println(tabulate(5,4,3,2,1,(v,w,x,y,z)->v+w+x+y+z));
    
    System.out.println("deleteRange tests");
    Seq<Integer> sdr = new Seq<>(1,2,3,4,5,6,7,8,9);
    System.out.println(sdr);
    sdr.deleteRange(-1,15);
    System.out.println(sdr);
    System.out.println(sdr.size());
    
    System.out.println("toArrayObject tests");
    System.out.println("using tabulate(2,1,(x,y)->x+y) to generate 2 arrays");
    par(toArrayObject(tabulate(2,1,(x,y)->x+y)));
    par(tabulate(2,1,(x,y)->x+y).toArrayObject());
    Seq<Integer> sn1 = new Seq<>(new Integer[]{1,2,3});  
    Seq<Integer> sn2 = new Seq<>(new Integer[]{null,null,null});
    Seq<Integer> sn3 = new Seq<>(new Integer[]{7,8,9});
    Seq<Seq<Integer>> snn1 = new Seq<>(sn1,sn2,sn3);
    System.out.println("using "+snn1+" to generate 2 arrays");
    par(toArrayObject(snn1)); par(snn1.toArrayObject());
    Seq<Integer> sn4 = new Seq<>(new Integer[]{null,null,null});
    Seq<Integer> sn5 = new Seq<>(new Integer[]{null,null,null});
    Seq<Integer> sn6 = new Seq<>(new Integer[]{null,null,null});
    Seq<Seq<Integer>> snn2 = new Seq<>(sn4,sn5,sn6);
    System.out.println("using "+snn2+" to generate 2 arrays");
    par(toArrayObject(snn2));
    Object[][] ia2 = (Object[][])snn2.toArrayObject();
    par(ia2);
    System.out.println("using tabulate(3,2,1,(x,y,z)->x+y+z) to generate 2 arrays");
    Object[][][] ia3 = (Object[][][])tabulate(3,2,1,(x,y,z)->x+y+z).toArrayObject();
    par(ia3);
    Integer[][][] ia32 = (Integer[][][])tabulate(3,2,1,(x,y,z)->x+y+z).toArrayObject();
    par(ia32);
    System.out.println("using tabulate(4,3,2,1,(w,x,y,z)->w+x+y+z) to generate 2 arrays");
    par(toArrayObject(tabulate(4,3,2,1,(w,x,y,z)->w+x+y+z)));
    Integer[][][][] ta4 = (Integer[][][][])tabulate(4,3,2,1,(w,x,y,z)->w+x+y+z).toArrayObject();
    par(ta4);
    System.out.println("using new Seq<>(1,2,3,4,5,6,7,8,9) to generate 2 arrays");
    Seq<Integer> seq1 = new Seq<>(1,2,3,4,5,6,7,8,9);
    Integer[] ia1 = (Integer[])toArrayObject(seq1);  par(ia1);
    Integer[] ia11 = (Integer[])seq1.toArrayObject(); par(ia11);
    System.out.println("using tabulate(5,4,3,2,1,(v,w,x,y,z)->v+w+x+y+z) to generate 3 arrays");
    System.out.println("1st:");
    par(toArrayObject(tabulate(5,4,3,2,1,(v,w,x,y,z)->v+w+x+y+z)));
    Seq<Seq<Seq<Seq<Seq<Integer>>>>> seq5 = tabulate(5,4,3,2,1,(v,w,x,y,z)->v+w+x+y+z);
    System.out.println("2nd");
    Integer[][][][][] ia5 = (Integer[][][][][]) toArrayObject(seq5); par(ia5);
    System.out.println("3rd");
    Integer[][][][][] ia55 = (Integer[][][][][])seq5.toArrayObject();
    par(ia55);

  }

}
