//package st;
//
//import java.util.Iterator;
//
//import ds.Queue;
//
//// same as SeparateChainingHashST but with keys only and no values 
//
//public class SeparateChainingHashSTK<Key> {
//    private static final int INIT_CAPACITY = 4;
//
//    private int n;                                // number of keys
//    private int m;                                // hash table size
//    private SequentialSearchST<Key>[] st;  // array of linked-list symbol tables
//
//    public SeparateChainingHashSTK() { this(INIT_CAPACITY); } 
//
//    @SuppressWarnings("unchecked")
//    public SeparateChainingHashSTK(int m) {
//        this.m = m;
//        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[m];
//        for (int i = 0; i < m; i++)
//            st[i] = new SequentialSearchST<Key, Value>();
//    } 
//
//    private void resize(int chains) {
//        SeparateChainingHashSTK<Key, Value> temp = new SeparateChainingHashSTK<Key, Value>(chains);
//        for (int i = 0; i < m; i++) {
//            for (Key key : st[i].keys()) {
//                temp.put(key, st[i].get(key));
//            }
//        }
//        this.m  = temp.m;
//        this.n  = temp.n;
//        this.st = temp.st;
//    }
//
//    private int hash(Key key) { return (key.hashCode() & 0x7fffffff) % m; } 
//
//    public int size() { return n; } 
//
//    public boolean isEmpty() { return size() == 0; }
//
//    public boolean contains(Key key) {
//        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
//        return get(key) != null;
//    } 
//
//    public Value get(Key key) {
//        if (key == null) 
//          throw new IllegalArgumentException("argument to get() is null");
//        int i = hash(key);
//        return st[i].get(key);
//    } 
//
//    public void put(Key key, Value val) {
//        if (key == null) 
//          throw new IllegalArgumentException("first argument to put() is null");
//        if (val == null) {
//            delete(key);
//            return;
//        }
//        if (n >= 10*m) resize(2*m);
//        int i = hash(key);
//        if (!st[i].contains(key)) n++;
//        st[i].put(key, val);
//    } 
//
//    public void delete(Key key) {
//        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
//        int i = hash(key);
//        if (st[i].contains(key)) n--;
//        st[i].delete(key);
//        if (m > INIT_CAPACITY && n <= 2*m) resize(m/2);
//    } 
//
//    public Iterable<Key> keys() {
//        Queue<Key> queue = new Queue<Key>();
//        for (int i = 0; i < m; i++) {
//            for (Key key : st[i].keys())
//                queue.enqueue(key);
//        }
//        return queue;
//    } 
//    
//    @Override public int hashCode() {
//      int h = 0;
//      Iterator<Key> it = keys().iterator();
//      while (it.hasNext()) {
//        Key k = it.next(); Value v = get(k);
//        h += (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
//      }
//      return h;
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    @Override public boolean equals(Object o) {
//      if (o == this) return true;
//      if (!(o instanceof SeparateChainingHashSTK)) return false;
//      SeparateChainingHashSTK x = (SeparateChainingHashSTK) o;
//      if (x.size() != size()) return false;
//      Iterator<Key> it = keys().iterator();
//      while (it.hasNext()) {
//        Key k = it.next();
//        Value value = get(k);
//        if (value == null) {            
//          if (!(x.get(k)==null && x.contains(k))) return false;
//        } else if (!value.equals(x.get(k))) return false;
//      }
//      return true;
//    }
//
//    public static void main(String[] args) { 
//       
////      SeparateChainingHashSTK<String> st = new SeparateChainingHashSTK<String>();
////      st.put("hello",1);
//      
//      
////      SeparateChainingHashST<String, Integer> st = new SeparateChainingHashST<String, Integer>();
////        for (int i = 0; !StdIn.isEmpty(); i++) {
////            String key = StdIn.readString();
////            st.put(key, i);
////        }
////
////        // print keys
////        for (String s : st.keys()) 
////            System.out.println(s + " " + st.get(s)); 
//
//    }
//}
//
