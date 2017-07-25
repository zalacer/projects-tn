package st;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.par;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.take;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/31elementary/BinarySearchST.java
 *  http://algs4.cs.princeton.edu/31elementary/BinarySearchST.java.html
 *  Compilation:  javac BinarySearchST.java
 *  Execution:    java BinarySearchST
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/31elementary/tinyST.txt  
 *  
 *  Symbol table implementation with binary search in an ordered array.
 *
 *  % more tinyST.txt
 *  S E A R C H E X A M P L E
 *  
 *  % java BinarySearchST < tinyST.txt
 *  A 8
 *  C 4
 *  E 12
 *  H 5
 *  L 11
 *  M 9
 *  P 10
 *  R 3
 *  S 0
 *  X 7
 *
 ******************************************************************************/

import java.util.NoSuchElementException;

import ds.Queue;
import edu.princeton.cs.algs4.StdIn;
import v.ArrayUtils;

/**
 *  The {@code BST} class represents an ordered symbol table of generic
 *  key-value pairs.
 *  It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 *  It also provides ordered methods for finding the <em>minimum</em>,
 *  <em>maximum</em>, <em>floor</em>, <em>select</em>, and <em>ceiling</em>.
 *  It also provides a <em>keys</em> method for iterating over all of the keys.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  Unlike {@link java.util.Map}, this class uses the convention that
 *  values cannot be {@code null}—setting the
 *  value associated with a key to {@code null} is equivalent to deleting the key
 *  from the symbol table.
 *  <p>
 *  This implementation uses a sorted array. It requires that
 *  the key type implements the {@code Comparable} interface and calls the
 *  {@code compareTo()} and method to compare two keys. It does not call either
 *  {@code equals()} or {@code hashCode()}.
 *  The <em>put</em> and <em>remove</em> operations each take linear time in
 *  the worst case; the <em>contains</em>, <em>ceiling</em>, <em>floor</em>,
 *  and <em>rank</em> operations take logarithmic time; the <em>size</em>,
 *  <em>is-empty</em>, <em>minimum</em>, <em>maximum</em>, and <em>select</em>
 *  operations take constant time. Construction takes constant time.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/31elementary">Section 3.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  For other implementations, see {@link ST}, {@link BST},
 *  {@link SequentialSearchST}, {@link RedBlackBST},
 *  {@link SeparateChainingHashST}, and {@link LinearProbingHashST},
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */

@SuppressWarnings({"unchecked","unused"})
public class BinarySearchST<Key extends Comparable<Key>, Value> {
    private static final int INIT_CAPACITY = 2;
    private Key[] keys;
    private Value[] vals;
    private int n = 0;

    /**
     * Initializes an empty symbol table.
     */
    public BinarySearchST() {
        this(INIT_CAPACITY);
    }

    /**
     * Initializes an empty symbol table with the specified initial capacity.
     * @param capacity the maximum capacity
     */
    public BinarySearchST(int capacity) { 
        keys = (Key[]) new Comparable[capacity]; 
        vals = (Value[]) new Object[capacity]; 
    }
    
    /**
     * Initializes a symbol table from arrays.
     * @param ka an array of Value
     * @param va an array of Key 
     */
    public BinarySearchST(Key[] ka, Value[] va) {
      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
      int n = Math.min(ka.length, va.length); int c = 0; 
      Key[] kz = ofDim(ka.getClass().getComponentType(), n);
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (ka[i] != null) { kz[c++] = ka[i]; }
      }
      if (c == 0) {
        keys = (Key[]) new Comparable[INIT_CAPACITY]; 
        vals = (Value[]) new Object[INIT_CAPACITY]; 
        return;
      }
      if (c < n) { n = c; kz = take(kz,c); }
      Value[] vz = ofDim(va.getClass().getComponentType(), n); c = 0;
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (va[i] != null) { vz[c++] = va[i]; }
      }
      if (c == 0) {
        keys = (Key[]) new Comparable[INIT_CAPACITY]; 
        vals = (Value[]) new Object[INIT_CAPACITY]; 
        return;
      }
      if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
      keys = (Key[]) new Comparable[2*n];
      vals = (Value[]) new Object[2*n]; 
      for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
    }

    // resize the underlying arrays
    private void resize(int capacity) {
        assert capacity >= n;
        Key[]   tempk = (Key[])   new Comparable[capacity];
        Value[] tempv = (Value[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            tempk[i] = keys[i];
            tempv[i] = vals[i];
        }
        vals = tempv;
        keys = tempk;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }


    /**
     * Does this symbol table contain the given key?
     *
     * @param  key the key
     * @return {@code true} if this symbol table contains {@code key} and
     *         {@code false} otherwise
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public boolean contains(Key key) {
        if (key == null) throw new NullPointerException("argument to contains() is null");
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key in this symbol table.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *         and {@code null} if the key is not in the symbol table
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Value get(Key key) {
        if (key == null) throw new NullPointerException("argument to get() is null"); 
        if (isEmpty()) return null;
        int i = rank(key); 
        if (i < n && keys[i].compareTo(key) == 0) return vals[i];
        return null;
    } 

    /**
     * Returns the number of keys in this symbol table strictly less than {@code key}.
     *
     * @param  key the key
     * @return the number of keys in the symbol table strictly less than {@code key}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public int rank(Key key) {
        if (key == null) throw new NullPointerException("argument to rank() is null"); 
        int lo = 0, hi = n-1; 
        while (lo <= hi) { 
            int mid = lo + (hi - lo) / 2; 
            int cmp = key.compareTo(keys[mid]);
            if      (cmp < 0) hi = mid - 1; 
            else if (cmp > 0) lo = mid + 1; 
            else return mid; 
        } 
        return lo;
    } 


    /**
     * Removes the specified key and its associated value from this symbol table
     * (if the key is in this symbol table).
     *
     * @param  key the key
     * @param  val the value
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void put(Key key, Value val)  {
        if (key == null) throw new NullPointerException("first argument to put() is null"); 

        if (val == null) {
            delete(key);
            return;
        }

        int i = rank(key);

        // key is already in table
        if (i < n && keys[i].compareTo(key) == 0) {
            vals[i] = val;
            return;
        }

        // insert new key-value pair
        if (n == keys.length) resize(2*keys.length);

        for (int j = n; j > i; j--)  {
            keys[j] = keys[j-1];
            vals[j] = vals[j-1];
        }
        keys[i] = key;
        vals[i] = val;
        n++;

        assert check();
    } 

    /**
     * Removes the specified key and associated value from this symbol table
     * (if the key is in the symbol table).
     *
     * @param  key the key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void delete(Key key) {
        if (key == null) throw new NullPointerException("argument to delete() is null"); 
        if (isEmpty()) return;

        // compute rank
        int i = rank(key);

        // key not in table
        if (i == n || keys[i].compareTo(key) != 0) {
            return;
        }

        for (int j = i; j < n-1; j++)  {
            keys[j] = keys[j+1];
            vals[j] = vals[j+1];
        }

        n--;
        keys[n] = null;  // to avoid loitering
        vals[n] = null;

        // resize if 1/4 full
        if (n > 0 && n == keys.length/4) resize(keys.length/2);

        assert check();
    } 

    /**
     * Removes the smallest key and associated value from this symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow error");
        delete(min());
    }

    /**
     * Removes the largest key and associated value from this symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow error");
        delete(max());
    }


   /***************************************************************************
    *  Ordered symbol table methods.
    ***************************************************************************/

   /**
     * Returns the smallest key in this symbol table.
     *
     * @return the smallest key in this symbol table
     * @throws NoSuchElementException if this symbol table is empty
     */
    public Key min() {
        if (isEmpty()) return null;
        return keys[0]; 
    }

    /**
     * Returns the largest key in this symbol table.
     *
     * @return the largest key in this symbol table
     * @throws NoSuchElementException if this symbol table is empty
     */
    public Key max() {
        if (isEmpty()) return null;
        return keys[n-1];
    }

    /**
     * Return the kth smallest key in this symbol table.
     *
     * @param  k the order statistic
     * @return the kth smallest key in this symbol table
     * @throws IllegalArgumentException unless {@code k} is between 0 and
     *        <em>n</em> &minus; 1
     */
    public Key select(int k) {
        if (k < 0 || k >= n) return null;
        return keys[k];
    }

    /**
     * Returns the largest key in this symbol table less than or equal to {@code key}.
     *
     * @param  key the key
     * @return the largest key in this symbol table less than or equal to {@code key}
     * @throws NoSuchElementException if there is no such key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Key floor(Key key) {
        if (key == null) throw new NullPointerException("argument to floor() is null"); 
        int i = rank(key);
        if (i < n && key.compareTo(keys[i]) == 0) return keys[i];
        if (i == 0) return null;
        else return keys[i-1];
    }

    /**
     * Returns the smallest key in this symbol table greater than or equal to {@code key}.
     *
     * @param  key the key
     * @return the smallest key in this symbol table greater than or equal to {@code key}
     * @throws NoSuchElementException if there is no such key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Key ceiling(Key key) {
        if (key == null) throw new NullPointerException("argument to ceiling() is null"); 
        int i = rank(key);
        if (i == n) return null; 
        else return keys[i];
    }

    /**
     * Returns the number of keys in this symbol table in the specified range.
     *
     * @param lo minimum endpoint
     * @param hi maximum endpoint
     * @return the number of keys in this symbol table between {@code lo} 
     *         (inclusive) and {@code hi} (inclusive)
     * @throws NullPointerException if either {@code lo} or {@code hi}
     *         is {@code null}
     */
    public int size(Key lo, Key hi) {
        if (lo == null) throw new NullPointerException("first argument to size() is null"); 
        if (hi == null) throw new NullPointerException("second argument to size() is null"); 

        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else              return rank(hi) - rank(lo);
    }

    /**
     * Returns all keys in this symbol table as an {@code Iterable}.
     * To iterate over all of the keys in the symbol table named {@code st},
     * use the foreach notation: {@code for (Key key : st.keys())}.
     *
     * @return all keys in this symbol table
     */
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    /**
     * Returns all keys in this symbol table in the given range,
     * as an {@code Iterable}.
     *
     * @param lo minimum endpoint
     * @param hi maximum endpoint
     * @return all keys in this symbol table between {@code lo} 
     *         (inclusive) and {@code hi} (inclusive)
     * @throws NullPointerException if either {@code lo} or {@code hi}
     *         is {@code null}
     */
    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new NullPointerException("first argument to keys() is null"); 
        if (hi == null) throw new NullPointerException("second argument to keys() is null"); 

        Queue<Key> queue = new Queue<Key>(); 
        if (lo.compareTo(hi) > 0) return queue;
        for (int i = rank(lo); i < rank(hi); i++) 
            queue.enqueue(keys[i]);
        if (contains(hi)) queue.enqueue(keys[rank(hi)]);
        return queue; 
    }


   /***************************************************************************
    *  Check internal invariants.
    ***************************************************************************/

    private boolean check() {
        return isSorted() && rankCheck();
    }

    // are the items in the array in ascending order?
    private boolean isSorted() {
        for (int i = 1; i < size(); i++)
            if (keys[i].compareTo(keys[i-1]) < 0) return false;
        return true;
    }

    // check that rank(select(i)) = i
    private boolean rankCheck() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (int i = 0; i < size(); i++)
            if (keys[i].compareTo(select(rank(keys[i]))) != 0) return false;
        return true;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      for (int i = 0; i < n; i++) sb.append(keys[i]+":"+vals[i]+",");
      return sb.substring(0,sb.length()-1)+"}";      
    }
    
    public void show() {
      for (int i = 0; i < n; i++) System.out.print(keys[i]+"="+vals[i]+" ");
      System.out.println();
    }
    
    public Key[] keyArray() {
      return keys;
    }
    
    public Value[] valArray() {
      return vals;
    }



    /**
     * Unit tests the {@code BinarySearchST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) { 
      
      String[] s = "a b c d e f g h i j k l m n".split("\\s+");
      Integer[] z = rangeInteger(1,s.length+1);
      BinarySearchST<Integer,String> st = new BinarySearchST<>(z,s);
      System.out.print("st.show "); st.show();
      System.out.println("st.toString "+st);
      //1=a 2=b 3=c 4=d 5=e 6=f 7=g 8=h 9=i 10=j 11=k 12=l 13=m 14=n 15=o 16=p 17=q 
      System.out.println("st.min "+st.min()); //1
      System.out.println("st.max "+st.max()); //17

      System.out.print("keys "); ArrayUtils.show(z);
      System.out.print("values ");  ArrayUtils.show(s);

      st.put(17,"q"); st.put(16,"p");  st.put(15,"o");

      System.out.print("get[1..17] ");
      for (int i = 1; i < 18; i++) System.out.print(st.get(i)+" "); System.out.println();
      //a b c d e f g h i j k l m n o p q 

      System.out.print("rank[1..17] ");
      for (int i = 1; i < 18; i++) System.out.print(st.rank(i)+" "); System.out.println();
      //0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

      System.out.print("select[0..17] ");
      for (int i = 0; i < 18; i++) System.out.print(st.select(i)+" "); System.out.println();
      //2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 null 

      System.out.print("floor[1..17] ");
      for (int i = 1; i < 18; i++) System.out.print(st.floor(i)+" "); System.out.println();
      //1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17

      System.out.print("ceiling[1..17] ");
      for (int i = 1; i < 18; i++) System.out.print(st.ceiling(i)+" "); System.out.println();
      //1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 

      System.out.println("size[5..11] "+st.size(5,11)); //7

      System.out.print("keys queue[5..11] ");
      Queue<Integer> q = (Queue<Integer>)st.keys(5,11);
      par(q.toArray()); //keys queue[5..11] [5,6,7,8,9,10,11]

      System.out.print("keys queue  ");
      q = (Queue<Integer>)st.keys();
      par(q.toArray()); //keys queue  [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17]

      System.out.println(st.isEmpty()); //false
      System.out.println(st.size()); //17

      System.out.print("st.show "); st.show();
      st.deleteMin(); System.out.print("delMin; st.show "); st.show();
      st.deleteMax(); System.out.print("delMax; st.show "); st.show();
      System.out.println("st.toString "+st);
      System.out.print("st.keyArray "); par(st.keyArray());

      System.out.println();

      s = "a b".split("\\s+");
      System.out.println("s.length="+s.length);
      z = rangeInteger(1,s.length+1);
      st = new BinarySearchST<>(z,s);
      System.out.print("st.show "); st.show();
      st.deleteMax(); System.out.print("delMax; st.show "); st.show(); 
      st.deleteMax(); System.out.print("delMax; st.show "); st.show();
      System.out.println();

      s = "a b".split("\\s+");
      System.out.println("s.length="+s.length);
      z = rangeInteger(1,s.length+1);
      st = new BinarySearchST<>(z,s);
      System.out.print("st.show "); st.show();
      st.deleteMin(); System.out.print("delMin; st.show "); st.show();
      st.deleteMin(); System.out.print("delMin; st.show "); st.show();
      
      
//        BinarySearchST<String, Integer> st = new BinarySearchST<String, Integer>();
//        for (int i = 0; !StdIn.isEmpty(); i++) {
//            String key = StdIn.readString();
//            st.put(key, i);
//        }
//        for (String s : st.keys())
//            System.out.println(s + " " + st.get(s));
    }
}
