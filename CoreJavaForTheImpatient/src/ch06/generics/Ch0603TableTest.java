package ch06.generics;

import java.util.ArrayList;

import utils.Entry;
import utils.Table;

//3. Implement a class Table<K, V> that manages an array list of Entry<K, V>
// elements. Supply methods to get the value associated with a key, to put a value for a
// key, and to remove a key.

public class Ch0603TableTest {

  public static void main(String[] args) {
    Entry<String, Double> e1 = new Entry<>("bacon",  new Double(7.31));
    Entry<String, Double> e2 = new Entry<>("bread",  new Double(5.15));
    Entry<String, Double> e3 = new Entry<>("wine",   new Double(19.73));
    Entry<String, Double> e4 = new Entry<>("wine",   new Double(31.56));
    Entry<String, Double> e5 = new Entry<>("bread",  new Double(7.06));
    Entry<String, Double> e6 = new Entry<>("cheese", new Double(8.11));
    Entry<String, Double> e7 = new Entry<>("bacon",  new Double(9.28));
    Table<String, Double> t1 = new Table<>();
    t1.put(e1);
    t1.put(e2);
    t1.put(e3);
    t1.put(e4);
    t1.put(e5);
    t1.put(e6);
    t1.put(e7);
    for (Entry<String, Double> e : t1.getA())
      System.out.println(e);
    // Entry [key=bacon, value=7.31]
    // Entry [key=bread, value=5.15]
    // Entry [key=wine, value=19.73]
    // Entry [key=wine, value=31.56]
    // Entry [key=bread, value=7.06]
    // Entry [key=cheese, value=8.11]
    // Entry [key=bacon, value=9.28]

    @SuppressWarnings("unchecked")
    ArrayList<Entry<String, Double>> bacon = (ArrayList<Entry<String, Double>>) t1.getAllEntries("bacon");
    System.out.println(bacon);
    // [Entry [key=bacon, value=7.31], Entry [key=bacon, value=9.28]]

    boolean r1 = t1.removeFirst("bacon");
    if (r1)
      System.out.println(t1.getAllEntries("bacon"));
    // [Entry [key=bacon, value=9.28]]

    Object jello = t1.getAllEntries("jello");
    if (jello.getClass() == Boolean.class)
      System.out.println("jello == "+ jello); // jello == false

    t1.put("jello", 1.36);
    Object jello2 = t1.getAllEntries("jello");
    if (jello2.getClass() == ArrayList.class) 
      System.out.println(t1.getAllEntries("jello")); 
    // [Entry [key=jello, value=1.36]]


  }

}
