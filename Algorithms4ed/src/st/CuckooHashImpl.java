package st;

import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

/**
 * from https://github.com/camreon/Cuckoo-Hashing with several fixes
 * Cuckoo hashing allows closer to perfect hashing by using 2 different hashcodes
 * that allow 2 positions in the table for each key-value pair
 * 
 * @author Cam
 * @version 1.0, 7/20/2012
 */
public class CuckooHashImpl<K, V> {

  private int CAPACITY;
  private int a = 37, b = 17;
  private Bucket[] table;

  /**
   * test meth
   */
  public static void main(String[] args) {

    CuckooHashImpl<String, String> table = new CuckooHashImpl<String, String>(10);
    table.put("A", "AA");
    System.out.println(table.toString() + "    " + table.size());
    table.put("A", "LL");
    System.out.println(table.toString() + "    " + table.size());
    table.put("B", "BB");
    System.out.println(table.toString() +"    " + table.size());
    table.put("C", "CC");
    System.out.println(table.toString() +"    " + table.size());
    table.put("C", "HH");
    System.out.println(table.toString() +"    " + table.size());
    table.put("A", "CC");
    System.out.println(table.toString() +"    " + table.size());
    table.put("S", "SS");
    System.out.println(table.toString() +"    " + table.size());
    table.put("A", "AA");
    System.out.println(table.toString() +"    " + table.size());

    System.out.println();
    System.out.println("KEYS: " + table.keys());
    System.out.println("VALUES: " + table.values());

    System.out.println();
    table.remove("A", "AA");
    table.remove("A", "CC");
    System.out.println(table.toString() +"    " + table.size());

    //table.rehash();
    //System.out.println(table.toString());

    table.clear();
    System.out.println(table.toString() +"    " + table.size());
  }

  /**
   * Inner bucket class
   * @param <K> - type of key
   * @param <V> - type of value
   */
  private class Bucket {
    private K bucKey = null;
    private V value = null;

    public Bucket(K k, V v) {
      bucKey = k; 
      value = v;
    }

    /**
     * Getters and Setters
     */
    private K getBucKey() {
      return bucKey;
    }
    //private void setBucKey(K key) {
    //bucKey = key;
    //}
    private V getValue() {
      return value;
    }
    //private void setValue(V value) {
    //this.value = value;
    //}
  }

  /**
   * a two parameter constructor that sets capacity and the loadfactor limit 
   * @param size user input multimap capacity
   * @param lf user input load factor limit
   */
  public CuckooHashImpl (int size) {
    CAPACITY = size;
    table = ofDim(Bucket.class, CAPACITY);
  }             

  /**
   * Get the number of elements in the table
   * @return total key-value pairs
   */
  public int size() {
    int count = 0;
    for (int i=0; i<CAPACITY; ++i) {
      if (table[i] != null)
        count++;  
    }
    return count;
  }

  /**
   * Removes all elements in the table
   */
  public void clear() {
    table = ofDim(Bucket.class, CAPACITY);
  }

  /**
   * Get a list of all values in the table
   * @return the values as a list
   */
  public List<V> values() {
    List<V> allValues = new ArrayList<V>(); 
    for (int i=0; i<CAPACITY; ++i) {
      if (table[i] != null) {
        allValues.add(table[i].getValue());
      }
    }
    return allValues;
  }

  /**
   * Get all the keys in the table
   * @return a set of keys
   */
  public Set<K> keys() {
    Set<K> allKeys = new HashSet<K>();
    for (int i=0; i<CAPACITY; ++i) {
      if (table[i] != null) {
        allKeys.add(table[i].getBucKey());
      }
    }
    return allKeys;
  }

  /**
   * Adds a key-value pair to the table by means of cuckoo hashing.
   * Insert into either of 2 separate hash indices, if both are full loop to
   * next hashed index and displace the key-value pair there, else loop for
   * n = size times.
   * @param key the key of the element to add
   * @param value the value of the element to add
   */
  public void put(K key, V value) {
    int index = key.hashCode() % CAPACITY;
    int index2 = altHashCode(key) % CAPACITY;

    if (table[index] != null && table[index].getValue().equals(value))
      return;
    if (table[index2] != null && table[index2].getValue().equals(value))
      return;
    int pos = index;
    Bucket buck = new Bucket(key, value);
    for (int i=0; i<3; i++) {
      if (table[pos] == null) {
        table[pos] = buck;
        return;
      }
      else {
        Bucket copy = table[pos];
        table[pos] = buck;
        buck = copy;
      }
      if (pos == index)
        pos = index2;
      else
        pos = index;
    }
    rehash();
    put(key, value);
  }

  /**
   * Retrieve a value in O(1) time based on the key b/c it can only be in 1 of 2 locations
   * @param key Key to search for
   * @return the found value or null if it doesn't exist
   */
  public V get(K key) {
    int index = key.hashCode() % CAPACITY;
    int index2 = altHashCode(key) % CAPACITY;
    if (table[index] != null && table[index].getBucKey().equals(key))
      return table[index].getValue();
    else if (table[index2] != null && table[index2].getBucKey().equals(key))
      return table[index2].getValue();
    return null;
  }

  /**
   * Secondary custom hashcoder for cuckoo hashing
   * @param key The key to be hashed
   * @return hashcode
   */
  public int altHashCode(K key) {
    return a * b + key.hashCode();
  }

  /**
   * Removes this key value pair from the table
   * @param key the key to remove
   * @param value the value to remove
   * @return successful removal
   */
  public boolean remove(K key, V value) {
    int index = key.hashCode() % CAPACITY;
    int index2 = altHashCode(key) % CAPACITY;
    if (table[index] != null && table[index].getValue().equals(value)) {
      table[index] = null;
      return true;
    }
    else if (table[index2] != null && table[index2].getValue().equals(value)) {
      table[index2] = null;
      return true;
    }
    return false;
  }

  /**
   * String representaiton of the table
   * @return the table's contents
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for (int i=0; i<CAPACITY; ++i) {
      if (table[i] != null) {
        sb.append("<");
        sb.append(table[i].getBucKey()); //key
        sb.append(", ");
        sb.append(table[i].getValue()); //value
        sb.append("> ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  /**
   * a method that regrows the hashtable to capacity: 2*old capacity + 1 and 
   * reinserts all the key->value pairs.
   */
  public void rehash() {
    Bucket[] tableCopy = table.clone();
    int OLD_CAPACITY = CAPACITY;
    CAPACITY = (CAPACITY * 2) + 1;
    table = ofDim(Bucket.class, CAPACITY);

    for (int i=0; i<OLD_CAPACITY; ++i) {
      if (tableCopy[i] != null) {
        put(tableCopy[i].getBucKey(), tableCopy[i].getValue());
      }
    }
    //reset alt hash func
    Random gen = new Random();
    a = gen.nextInt(37);
    b = gen.nextInt(17);
  }

}


