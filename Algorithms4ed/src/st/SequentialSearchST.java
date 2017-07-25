package st;

import static v.ArrayUtils.*;
import java.util.Iterator;
import ds.Stack;
import v.Tuple2;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/31elementary/SequentialSearchST.java
 *  http://algs4.cs.princeton.edu/31elementary/SequentialSearchST.java.html
 *  Compilation:  javac SequentialSearchST.java
 *  Execution:    java SequentialSearchST
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/31elementary/tinyST.txt  
 *  
 *  Symbol table implementation with sequential search in an
 *  unordered linked list of key-value pairs.
 *
 *  % more tinyST.txt
 *  S E A R C H E X A M P L E
 *
 *  % java SequentialSearchST < tiny.txt 
 *  L 11
 *  P 10
 *  M 9
 *  X 7
 *  H 5
 *  C 4
 *  R 3
 *  A 8
 *  E 12
 *  S 0
 *
 ******************************************************************************/

/**
 *  The {@code SequentialSearchST} class represents an (unordered)
 *  symbol table of generic key-value pairs.
 *  It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 *  It also provides a <em>keys</em> method for iterating over all of the keys.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  The class also uses the convention that values cannot be {@code null}. Setting the
 *  value associated with a key to {@code null} is equivalent to deleting the key
 *  from the symbol table.
 *  <p>
 *  This implementation uses a singly-linked list and sequential search.
 *  It relies on the {@code equals()} method to test whether two keys
 *  are equal. It does not call either the {@code compareTo()} or
 *  {@code hashCode()} method. 
 *  The <em>put</em> and <em>delete</em> operations take linear time; the
 *  <em>get</em> and <em>contains</em> operations takes linear time in the worst case.
 *  The <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction takes constant time.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/31elementary">Section 3.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class SequentialSearchST<Key, Value> {
    private int n;           // number of key-value pairs
    private Node first;      // the linked list of key-value pairs
    private Class<?> kclass = null; // Key class
    private Class<?> vclass = null; // Value class

    // a helper linked list data type
    private class Node {
        private Key key;
        private Value val;
        private Node next;

        public Node(Key key, Value val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public SequentialSearchST(){}
    
    /**
     * Construct symbol table from arrays.
     */
    public SequentialSearchST(Key[] ka, Value[] va) {
      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
      int n = Math.min(ka.length, va.length); int c = 0;
      Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,n);
      for (int i = 0; i < n; i++) 
        if (ka[c] != null && va[c] != null) 
          ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
      if (c == 0) return;
      ta = take(ta,c); n = ta.length;
      kclass = ka.getClass().getComponentType();
      vclass = va.getClass().getComponentType();
      Key[] kz = ofDim(kclass, n);
      Value[] vz = ofDim(vclass, n); c = 0;
      for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
      for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
    }
//    public SequentialSearchST(Key[] ka, Value[] va) {
//      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
//      int n = Math.min(ka.length, va.length); int c = 0; 
//      Key[] kz = ofDim(ka.getClass().getComponentType(), n);
//      for (int  i = 0; i < n; i++) {
//        if (c == n) break;
//        if (ka[i] != null) { kz[c++] = ka[i]; }
//      }
//      if (c == 0) return;
//      if (c < n) { n = c; kz = take(kz,c); }
//      Value[] vz = ofDim(va.getClass().getComponentType(), n); c = 0;
//      for (int  i = 0; i < n; i++) {
//        if (c == n) break;
//        if (va[i] != null) { vz[c++] = va[i]; }
//      }
//      if (c == 0) return;
//      if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
//      for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
//    }

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
     * Returns true if this symbol table contains the specified key.
     *
     * @param  key the key
     * @return {@code true} if this symbol table contains {@code key};
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
     *     and {@code null} if the key is not in the symbol table
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Value get(Key key) {
        if (key == null) throw new NullPointerException("argument to get() is null"); 
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key))
                return x.val;
        }
        return null;
    }

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param  key the key
     * @param  val the value
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void put(Key key, Value val) {
        if (key == null) throw new NullPointerException("first argument to put() is null"); 
        if (val == null) {
            delete(key);
            return;
        }

        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        first = new Node(key, val, first);
        n++;
    }

    /**
     * Removes the specified key and its associated value from this symbol table     
     * (if the key is in this symbol table).    
     *
     * @param  key the key
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void delete(Key key) {
        if (key == null) throw new NullPointerException("argument to delete() is null"); 
        first = delete(first, key);
    }

    // delete key in linked list beginning at Node x
    // warning: function call stack too large if table is large
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            n--;
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }


    /**
     * Returns all keys in the symbol table as an {@code Iterable}.
     * To iterate over all of the keys in the symbol table named {@code st},
     * use the foreach notation: {@code for (Key key : st.keys())}.
     *
     * @return all keys in the symbol table
     */
    public Iterable<Key> keys()  {
        Stack<Key> stack = new Stack<Key>();
        for (Node x = first; x != null; x = x.next)
          stack.push(x.key);
        return stack;
    }
    
    /**
     * Return the symbol table as a string.
     *
     * @return a String
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (n == 0) return "{}";
      sb.append("{"); Node x = first;
      while(x != null) { sb.append(x.key+":"+x.val+","); x = x.next; }
      return sb.substring(0,sb.length()-1)+"}";
    }

    /**
     * Unit tests the {@code SequentialSearchST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
      
      String[]  a = "one two three four five six seven eight nine".split("\\s+");
      Integer[] b = rangeInteger(1,10);

      SequentialSearchST<String,Integer> st =  new SequentialSearchST<>(a,b);
      Iterator<String> it = st.keys().iterator();
      while (it.hasNext()) {
        String k = it.next();
        System.out.print(k+":"+st.get(k)+" ");
      }
      System.out.println();

//        SequentialSearchST<String, Integer> st = new SequentialSearchST<String, Integer>();
//        for (int i = 0; !StdIn.isEmpty(); i++) {
//            String key = StdIn.readString();
//            st.put(key, i);
//        }
//        for (String s : st.keys())
//            StdOut.println(s + " " + st.get(s));
      
    }
}
