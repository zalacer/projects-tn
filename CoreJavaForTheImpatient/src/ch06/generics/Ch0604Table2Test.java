package ch06.generics;

import java.util.ArrayList;

import utils.Table2;
import utils.Table2.Entry2;

//4. In the previous exercise, make Entry2 into a nested class. Should that class be
//generic?

// Given that Entry has already been created its easiest to just drop it into Table as a 
// static nested class. This is shown below with Entry renamed to Entry2 to avoid
// confusion and it passes the same tests as in Ch0603TableTest.  Entry2 is generic. It
// would be simpler to make it non-generic and that's done with Table3.


public class Ch0604Table2Test {

  public static void main(String[] args) {

    Entry2<String, Double> e1 = new Entry2<>("bacon",  new Double(7.31));
    Entry2<String, Double> e2 = new Entry2<>("bread",  new Double(5.15));
    Entry2<String, Double> e3 = new Entry2<>("wine",   new Double(19.73));
    Entry2<String, Double> e4 = new Entry2<>("wine",   new Double(31.56));
    Entry2<String, Double> e5 = new Entry2<>("bread",  new Double(7.06));
    Entry2<String, Double> e6 = new Entry2<>("cheese", new Double(8.11));
    Entry2<String, Double> e7 = new Entry2<>("bacon",  new Double(9.28));
    Table2<String, Double> t1 = new Table2<>();
    t1.put(e1);
    t1.put(e2);
    t1.put(e3);
    t1.put(e4);
    t1.put(e5);
    t1.put(e6);
    t1.put(e7);
    for (Entry2<String, Double> e : t1.getA())
      System.out.println(e);
    // Entry2 [key=bacon, value=7.31]
    // Entry2 [key=bread, value=5.15]
    // Entry2 [key=wine, value=19.73]
    // Entry2 [key=wine, value=31.56]
    // Entry2 [key=bread, value=7.06]
    // Entry2 [key=cheese, value=8.11]
    // Entry2 [key=bacon, value=9.28]

    @SuppressWarnings("unchecked")
    ArrayList<Entry2<String, Double>> bacon = (ArrayList<Entry2<String, Double>>) t1.getAllEntries("bacon");
    System.out.println(bacon);
    // [Entry2 [key=bacon, value=7.31], Entry2 [key=bacon, value=9.28]]

    boolean r1 = t1.removeFirst("bacon");
    if (r1)
      System.out.println(t1.getAllEntries("bacon"));
    // [Entry2 [key=bacon, value=9.28]]

    Object jello = t1.getAllEntries("jello");
    if (jello.getClass() == Boolean.class)
      System.out.println("jello == "+ jello); // jello == false

    t1.put("jello", 1.36);
    Object jello2 = t1.getAllEntries("jello");
    if (jello2.getClass() == ArrayList.class) 
      System.out.println(t1.getAllEntries("jello")); 
    // [Entry2 [key=jello, value=1.36]]



  }

}
