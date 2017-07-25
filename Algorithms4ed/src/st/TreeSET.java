package st;

import static v.ArrayUtils.range;

import java.util.Iterator;

public class TreeSET<X extends Comparable<X>> implements OrderedSET<X> {
  private ST<X,Integer> st;
  
  public TreeSET() { st = new ST<>(); }
  
  @Override public void add(X x) { st.put(x, 0); }

  @Override public void delete(X x) { st.delete(x); }

  @Override public boolean contains(X x) { return st.contains(x); } 

  @Override public boolean isEmpty() { return st.isEmpty(); }

  @Override public int size() { return st.size(); }

  @Override public Iterator<X> iterator() { return st.iterator(); }

  @Override public X max() { return st.max(); }

  @Override public X min() { return st.min(); }

  @Override public X ceiling(X x) { return st.ceiling(x); }

  @Override public X floor(X x) { return st.floor(x); }

  public TreeSET<X> union(TreeSET<X> that) {
    if (that == null) return null;
    TreeSET<X> set = new TreeSET<>();
    for (X x : this) { set.add(x); }
    for (X x : that) { set.add(x); }
    return set;
  }
 
  public TreeSET<X> intersection(TreeSET<X> that) {
    if (that == null) return null;
    TreeSET<X> set = new TreeSET<>();
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
    TreeSET other = (TreeSET) obj;
    if (st == null) if (other.st != null) return false;
    if (size() != other.size()) return false;
    Iterator<X> it = iterator();
    while (it.hasNext()) if (!other.contains(it.next())) return false;
    return true;
  }
  
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    Iterator<X> it = iterator();
    while (it.hasNext()) sb.append(it.next()+",");
    sb.replace(sb.length()-1, sb.length(),")");
    return sb.toString();
  }

  public static void main(String[] args) {
    
    System.out.println("TreeSET demo:");
    
    TreeSET<Integer> set1 = new TreeSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) set1.add(i);
    System.out.println("set1 = "+set1);

    TreeSET<Integer> set2 = new TreeSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) set2.add(i);
    System.out.println("set2 = "+set2);

    TreeSET<Integer> union1 = set1.union(set2);
    System.out.println("set1 union set2 = "+union1);

    for (int i = 1; i < 16; i++) assert union1.contains(i);

    TreeSET<Integer> intersection1 = set1.intersection(set2);
    System.out.println("set1 intersect set2 = "+intersection1);

    for (int i = 6; i < 11; i++) assert intersection1.contains(i);
    
    System.out.println("set1 min = "+set1.min());
    
    System.out.println("set2 max = "+set2.max());
    
    TreeSET<Integer> set3 = new TreeSET<>();
    int[] a = range(0,12,3); for (int i : a) set3.add(i);
    System.out.println("set3 = "+set3);
    
    System.out.println("ceiling of 5 in set3 = "+set3.ceiling(5));
    
    System.out.println("floor of 7 in set3 = "+set3.floor(7));
    



    
  }

}
