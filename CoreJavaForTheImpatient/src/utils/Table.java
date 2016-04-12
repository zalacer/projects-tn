package utils;

import java.util.ArrayList;

// for Ch0603TableTest
//3. Implement a class Table<K, V> that manages an array list of Entry<K, V>
//elements. Supply methods to get the value associated with a key, to put a value for a
//key, and to remove a key.

public class Table<K, V> {

  private ArrayList<Entry<K, V>> a;

  public Table(){};

  public Table(ArrayList<Entry<K, V>> a) {
    this.a = a;
  }

  public String listEntries() {
    if (a.size() == 0) return "";
    StringBuilder builder = new StringBuilder();
    for (Entry<K, V> e : a)
      builder.append(e.toString()+"\n");
    return builder.toString();
  }

  // a factory
  public ArrayList<Entry<K, V>> getA() {
    if (a == null) a = new ArrayList<>();
    return a;
  }

  public void setA(ArrayList<Entry<K, V>> a) {
    this.a = a;
  }

  // return first match in a or null
  public Object getEntry(K key) {
    if (a == null) return false;
    for (Entry<K, V> e : a) {
      if (e.getKey().equals(key))
        return e;
    }
    return false;
  }

  // return all matches in a or null
  public Object getAllEntries(K key) {
    if (a == null) return false;
    ArrayList<Entry<K, V>> b = new ArrayList<>();
    a.forEach(x -> {if (x.getKey().equals(key)) b.add(x);});
    if (b.size() > 0) {
      return b;
    } else {
      return false;
    }
  }

  public boolean put(K key, V value) {
    Entry<K, V> e = new Entry<>(key, value);
    if (a == null) getA();
    a.add(e);
    return true;
  }

  public boolean put(Entry<K, V> e) {
    if (a == null) getA();
    a.add(e);
    return true;
  }

  public boolean removeFirst(K key) {
    if (a == null) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.get(i).getKey().equals(key)) { 
        a.remove(i);
        return true;  
      }
    }
    return false;
  }

  public boolean removeAll(K key) {
    if (a == null) return false;
    return a.removeIf(x -> x.getKey().equals(key));
  }

  public boolean remove(Entry<K, V> e) {
    if (a == null) return false;
    return a.remove(e);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((a == null) ? 0 : a.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Table))
      return false;
    @SuppressWarnings("rawtypes")
    Table other = (Table) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    } else if (!a.equals(other.a))
      return false;
    return true;
  }










}
