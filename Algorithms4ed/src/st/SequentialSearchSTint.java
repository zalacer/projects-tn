package st;

import static v.ArrayUtils.*;
import java.util.Iterator;
import ds.Stack;

public class SequentialSearchSTint {
  // Integer.MIN_VALUE takes the place of null in keys
    private int n;           // number of key-value pairs
    private Node first;      // the linked list of key-value pairs

    // a helper linked list data type
    private class Node {
        private int key;
        private int val;
        private Node next;

        public Node(int key, int val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    public SequentialSearchSTint(){}
   
    public SequentialSearchSTint(int[] ka, int[] va) {
      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
      int n = Math.min(ka.length, va.length);
      for (int  i = 0; i < n; i++) put(ka[i], va[i]);
    }

    public int size() { return n; }

    public boolean isEmpty() { return size() == 0; }

    public boolean contains(int key) { return get(key) != Integer.MIN_VALUE; }

    public int get(int key) {
        for (Node x = first; x != null; x = x.next) {
            if (key == x.key) return x.val;
        }
        return Integer.MIN_VALUE;
    }

    public void put(int key, int val) {
        if (val == Integer.MIN_VALUE) {
            delete(key);
            return;
        }

        for (Node x = first; x != null; x = x.next) {
            if (key == x.key) {
                x.val = val;
                return;
            }
        }
        first = new Node(key, val, first);
        n++;
    }

    public void delete(int key) {
        first = delete(first, key);
    }

 
    private Node delete(Node x, int key) {
        if (x == null) return null;
        if (key == x.key) {
            n--;
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }

    public Iterable<Integer> keys()  {
        Stack<Integer> stack = new Stack<Integer>();
        for (Node x = first; x != null; x = x.next)
          stack.push(x.key);
        return stack;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (n == 0) return "{}";
      sb.append("{"); Node x = first;
      while(x != null) { sb.append(x.key+":"+x.val+","); x = x.next; }
      return sb.substring(0,sb.length()-1)+"}";
    }

    public static void main(String[] args) {
      
      int[]  a = range(1,10);
      int[] b = range(1,10);

      SequentialSearchSTint st =  new SequentialSearchSTint(a,b);
      Iterator<Integer> it = st.keys().iterator();
      while (it.hasNext()) {
        int k = it.next();
        System.out.print(k+":"+st.get(k)+" ");
      }
      System.out.println();

    }
}
