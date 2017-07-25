package st;

//import static v.ArrayUtils.*;
import java.util.Iterator;
import ds.Stack;

// based st.SequentialSearchST but with Key only and no Value


public class SequentialSearchMultiST<Key> implements UnorderedMultiST<Key>{
  private int n;           // number of keys
  private Node first;      // the linked list of keys
  private Class<?> kclass; // Key class

  // a helper linked list data type
  private class Node {
    private Key key;
    private Node next;

    public Node(Key key, Node next)  {
      this.key  = key;
      this.next = next;
    }
  }

  public SequentialSearchMultiST(){}

  public SequentialSearchMultiST(Key[] a) {
    if (a == null || a.length == 0) return;
    kclass = a.getClass().getComponentType();
    for (int i = 0; i < a.length ; i++) if (a[i] != null) put(a[i]);
  }

  public int size() { return n; }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(Key key) { return get(key); }

  private boolean get(Key key) {
    if (key == null) return false; 
    for (Node x = first; x != null; x = x.next) if (key.equals(x.key)) return true;
    return false;
  }

  public void put(Key key) {
    //  puts any non-null key even if duplicate.
    if (key == null) return;
    if (kclass == null) kclass = key.getClass();
    //    for (Node x = first; x != null; x = x.next) if (key.equals(x.key)) return;
    first = new Node(key, first);
    n++;
  }
  
  public void delete(Key key) {
    // deletes all Keys k.equal(key).
    if (key == null || first == null) return;
    while (first != null && first.key.equals(key)) { first = first.next; n--; }
    if (first == null) { assert n == 0; return; }
    Node p = first, t = p.next;
    while (t != null) {
      while (t != null && !t.key.equals(key)) { p = t; t = t.next; } 
      if (t == null) return;
      p.next = t.next; n--; t = p.next;
    }
  }

  private Stack<Key> stack()  {
    Stack<Key> stack = new Stack<Key>();
    for (Node x = first; x != null; x = x.next)
      stack.push(x.key);
    return stack;
  }
  
  public Iterable<Key> keys() { return stack(); }

  public Iterator<Key> iterator() { return stack().iterator(); }

  public Key[] toArray() { return stack().toArray(); }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    if (n == 0) return "{}";
    sb.append("{"); 
    Node node = first; sb.append(node.key+",");
    while (node.next != null) { node =  node.next; sb.append(node.key+","); }
    return sb.replace(sb.length()-1,sb.length(),"}").toString();
  }

  public static void main(String[] args) {

    Integer[] a = {5,6,2,3,4,1};
    //    a = new Integer[]{5,3,5,4,5,5};
    SequentialSearchMultiST<Integer> st =  new SequentialSearchMultiST<>(a);
    System.out.println(st);
    System.out.println("size = "+st.size());
    st.delete(5);
    System.out.println(st);
    System.out.println("size = "+st.size());


    //        SequentialSearchST<String, Integer> st = new SequentialSearchST<String, Integer>();
    //        for (int i = 0; !StdIn.isEmpty(); i++) {
    //            String key = StdIn.readString();
    //            st.put(key, i);
    //        }
    //        for (String s : st.keys())
    //            StdOut.println(s + " " + st.get(s));

  }
}
