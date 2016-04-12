package ch06.generics;

//4. In the previous exercise, make Entry into a nested class. Should that class be
//generic?

// Perhaps it's best to make Entry non-generic for simplicity and maybe it compiles 
// or performs slightly faster by removal of a layer of typing. Table3 is 
// implemented with a non-generic Entry nested class that's named Entry3 to avoid 
// confusion. Below is a demo of Table3.

import java.util.ArrayList;

import utils.Table3;

public class Ch0604Table3Test {

  public static void main(String[] args) {

    Table3<String, Double> t1 = new Table3<>();
    //        ArrayList<Table3<String, Double>.Entry3> t1al = t1.getA();

    t1.put("bacon",  new Double(7.31));
    t1.put("bread",  new Double(5.15));
    t1.put("wine",   new Double(19.73));
    t1.put("wine",   new Double(31.56));
    t1.put("bread",  new Double(7.06));
    t1.put("cheese", new Double(8.11));
    t1.put("bacon",  new Double(9.28));

    System.out.println(t1.listEntries());
    // Entry3 [key=bacon, value=7.31]
    // Entry3 [key=bread, value=5.15]
    // Entry3 [key=wine, value=19.73]
    // Entry3 [key=wine, value=31.56]
    // Entry3 [key=bread, value=7.06]
    // Entry3 [key=cheese, value=8.11]
    // Entry3 [key=bacon, value=9.28]

    Object bacon = t1.getAllEntries("bacon");
    if (bacon.getClass() == ArrayList.class) 
      System.out.println(bacon);
    // [Entry3 [key=bacon, value=7.31], Entry3 [key=bacon, value=9.28]]

    boolean r1 = t1.removeFirst("bacon");
    if (r1)
      System.out.println(t1.getAllEntries("bacon"));
    // [Entry3 [key=bacon, value=9.28]]

    Object jello = t1.getAllEntries("jello");
    if (jello.getClass() == Boolean.class)
      System.out.println("jello == "+ jello); // jello == false

    t1.put("jello", 1.36);
    Object jello2 = t1.getAllEntries("jello");
    if (jello2.getClass() == ArrayList.class) 
      System.out.println(t1.getAllEntries("jello")); 
    // [Entry3 [key=jello, value=1.36]]



  }

}
