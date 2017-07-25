package st;

import v.ArrayUtils;

import java.util.Iterator;

public class HashSETX<X> implements UnorderedSET<X>, Iterable<X> {
  private SeparateChainingHashSTX<X,Integer> st;
  
  public HashSETX() { st = new SeparateChainingHashSTX<>(); }
  
  @SafeVarargs
  public HashSETX(X...xa) {
    st = new SeparateChainingHashSTX<>();
    if (xa == null || xa.length == 0) return;
    for (X x : xa) add(x);
  }
  
  public HashSETX(HashSETX<X> set) {
    st = new SeparateChainingHashSTX<>();
    if (set.isEmpty()) return;
    X[] xa = set.toArray();
    for (X x : xa) add(x);
  }

  @Override public void add(X x) { st.put(x,0); }
  
  @SafeVarargs
  public final void addArray(X...xa) {
    if (xa == null || xa.length == 0) return;
    for (X x : xa) st.put(x,0); 
  }
  
  @SafeVarargs
  public final void insert(X...xa) {
    if (xa == null || xa.length == 0) return;
    for (X x : xa) add(x);
  }
  
  public final void addIterable(Iterable<X> z) {
    if (z == null) return;
    for (X x : z) add(x);
  }
  
  public void clear() { st = new SeparateChainingHashSTX<>(); }
  
  @Override public void delete(X x) { st.delete(x); }
  
  public void remove(X x) { st.delete(x); }

  
  @Override public boolean contains(X x) { return st.contains(x); }
  
  @SafeVarargs public final boolean containsAny(X...x) { return st.containsAny(x); }

  @SafeVarargs public final boolean containsAll(X...x) { return st.containsAll(x); }

  @Override public boolean isEmpty() { return st.isEmpty(); }

  @Override public int size() { return st.size(); }

  @Override public Iterator<X> iterator() { return st.keys().iterator(); }
  
  public X[] toArray() { return ArrayUtils.toArray(iterator()); }
  
  public HashSETX<X> union(HashSETX<X> that) {
    if (that == null) return null;
    HashSETX<X> set = new HashSETX<>();
    for (X x : this) { set.add(x); }
    for (X x : that) { set.add(x); }
    return set;
  }
 
  public HashSETX<X> intersection(HashSETX<X> that) {
    if (that == null) return null;
    HashSETX<X> set = new HashSETX<>();
    if (this.size() < that.size()) {
        for (X x : this) { if (that.contains(x)) set.add(x); }
    } else for (X x : that) { if (this.contains(x)) set.add(x); }
    return set;
  }

  @Override public int hashCode() {
    int h = 0;
    Iterator<X> it = iterator();
    while (it.hasNext()) {
      X x = it.next();
      h += x == null ? 0 : x.hashCode();
    }
    return h;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" }) 
  @Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    HashSETX other = (HashSETX) obj;
    if (st == null) if (other.st != null) return false;
    if (size() != other.size()) return false;
    Iterator<X> it = iterator();
    while (it.hasNext()) if (!other.contains(it.next())) return false;
    return true;
  }
  
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("("); if (isEmpty()) { sb.append(")"); return sb.toString(); }
    Iterator<X> it = iterator();
    while (it.hasNext()) sb.append(it.next()+",");
    sb.replace(sb.length()-1, sb.length(),")");
    return sb.toString();
  }
  
  public static void main(String[] args) {
    
    HashSETX<Integer> s1 = new HashSETX<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) s1.add(i);
    System.out.println(s1);
    
    HashSETX<Integer> s2 = new HashSETX<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) s2.add(i);
    System.out.println(s2);
    
    HashSETX<Integer> s3 = s1.union(s2);
    System.out.println(s3);
    
    HashSETX<Integer> s4 = s1.intersection(s2);
    System.out.println(s4);

  }

}
