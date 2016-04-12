package utils;

import java.util.ArrayList;

// for Ch0604Table2Test
//3. Implement a class Table<K, V> that manages an array list of Entry<K, V>
//elements. Supply methods to get the value associated with a key, to put a value for a
//key, and to remove a key.
//4. In the previous exercise, make Entry into a nested class. Should that class be
//generic?

public class Table2<K, V> {

  public static class Entry2<K, V> {

    private K key;
    private V value;

    public Entry2(){};

    public Entry2(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return key;
    }

    public void setKey(K key) {
      this.key = key;
    }

    public V getValue() {
      return value;
    }

    public void setValue(V value) {
      this.value = value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (!(obj instanceof Entry2))
        return false;
      @SuppressWarnings("rawtypes")
      Entry2 other = (Entry2) obj;
      if (key == null) {
        if (other.key != null)
          return false;
      } else if (!key.equals(other.key))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Entry2 [key=");
      builder.append(key);
      builder.append(", value=");
      builder.append(value);
      builder.append("]");
      return builder.toString();
    }

  }

  private ArrayList<Entry2<K, V>> a;

  public Table2(){};

  public Table2(ArrayList<Entry2<K, V>> a) {
    this.a = a;
  }

  // a factory
  public ArrayList<Entry2<K, V>> getA() {
    if (a == null) a = new ArrayList<>();
    return a;
  }

  public void setA(ArrayList<Entry2<K, V>> a) {
    this.a = a;
  }

  public String listEntries() {
    if (a.size() == 0) return "";
    StringBuilder builder = new StringBuilder();
    for (Entry2<K, V> e : a)
      builder.append(e.toString()+"\n");
    return builder.toString();
  }

  // return first match in a or null
  public Object getEntry(K key) {
    if (a == null) return false;
    for (Entry2<K, V> e : a) {
      if (e.getKey().equals(key))
        return e;
    }
    return false;
  }

  // return all matches in a or null
  public Object getAllEntries(K key) {
    if (a == null) return false;
    ArrayList<Entry2<K, V>> b = new ArrayList<>();
    a.forEach(x -> {if (x.getKey().equals(key)) b.add(x);});
    if (b.size() > 0) {
      return b;
    } else {
      return false;
    }
  }

  public boolean put(K key, V value) {
    Entry2<K, V> e = new Entry2<>(key, value);
    if (a == null) getA();
    a.add(e);
    return true;
  }

  public boolean put(Entry2<K, V> e) {
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

  public boolean remove(Entry2<K, V> e) {
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
    if (!(obj instanceof Table2))
      return false;
    @SuppressWarnings("rawtypes")
    Table2 other = (Table2) obj;
    if (a == null) {
      if (other.a != null)
        return false;
    } else if (!a.equals(other.a))
      return false;
    return true;
  }










}
