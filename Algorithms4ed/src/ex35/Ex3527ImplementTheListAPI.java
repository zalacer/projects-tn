package ex35;

import st.HashList;
import ds.XarrayList;

import ds.Seq;

/* p511
  3.5.27 List. Develop an implementation of the following API.
  
  API for a list data type:
  public class List<Item> implements Iterable<Item>
  List() create a list
  void addFront(Item item)  add  item to the front
  void addBack(Item item)  add  item to the back
  Item deleteFront()  remove from the front
  Item deleteBack()  remove from the back
  void delete(Item item)  remove  item from the list
  void add(int i, Item item)  add  item as the  i th in the list
  Item delete(int i)  remove the  i th item from the list
  boolean contains(Item item)  is  key in the list?
  boolean isEmpty()  is the list empty?
  int size()  number of items in the list
  
  Hint : Use two symbol tables, one to find the ith item in the list 
  efficiently, and the other to efficiently search by item. (Java’s  
  java.util.List interface contains methods like these but does not 
  supply any implementation that efficiently supports all operations.)
  
  Suggest renaming delete(int i) to remove(int i) to prevent confusion 
  between it and delete(Item item) for Integer items.
  
  Using two top-level symbol tables this is done in st.HashList that's
  constructed with an st.RedBlackBSTD and an st.SeparateChainingHashSTSETD.
  
  The RedBlackBSTD has Double keys and generic Values and it provides 
  "virtual" list indices with its rank() method. The reason for using 
  Double keys is so additional elements can be inserted between existing 
  ones without reindexing up. Using Doubles this allows up to about 100 
  insertions between keys with a difference of 1. With BigDecimals an
  indefinitely large though theoritically unlimited number of insertions
  are possible with no reindexing, however calculating new keys becomes
  prohibitively slow after a few thousand. Using BigDecimal with DECIMAL128
  MathContext or a 3rd party floating point library with higher precision
  could be feasible.
  
  The SeparateChainingHashSTSETD is used to index the Values of the 
  RedBlackBSTD to their Double indices and maps Value to st.RedBlackSETD 
  that's a RedBlack tree implementation of SET with Double keys.
  
  HashList is fine for small lists say under a few thousand entries, but the
  use of trees (RedBlack or BinarySearch) slows it down excessively over that,
  starting with initialization from a large array or Collection. It has the ad-
  vantage of enabling easy programming of lots of functionality including that 
  in the API given above (encapsulated as st.SequenceInterface), all methods in 
  java.util.ArrayList and more. This shows that efficiency in terms of order of
  growth doesn't equate to real performance, doesn't account for storage overhead 
  and shows the advantage of ArrayList in using an array for direct indexing, 
  storage minimization and fast array operations such as System.arraycopy for 
  bulk transfers. The bottom line is there's always a tradeoff between speed,
  storage and functionality and a lot of the functionality even in the small API
  shown above isn't needed most of the time, so why optimize for it? Having used
  ArrayLists for years I find that I have not needed most of its methods, including 
  add(int index, E e) and get(int index), since when I need indexed access I almost 
  always prefer using arrays directly. In fact my primary use of ArrayList is to
  capture data of indeterminate length and then put it into an array or use it to
  construct another type of Collection.
  
  A array-based solution is ds.XarrayList and this is close to what I would prefer to
  use for performance and functionality. It's complicated by using wraparound indexing 
  that makes it more difficult than necessary to implement most array operations and 
  generally increases its opacity to the point of being prohibitive as an array wrapper 
  scalable to multidimensional arrays, however many methods in v.ArrayUtils could be 
  integrated into it.
  
  My last solution is a refinement and extension of XarrayList called ds.Seq. It's simpler
  to implement since it uses straight indexing -- 0 is always the first index and N is 
  always the last. It conforms to the List interface given in this exercise as well as 
  java.util.List and java.util.Collection and has most of the methods of scala.Array plus
  some extra methods such as those supporting conversions between Seqs and arrays of any
  dimensionality allowed for the latter in Java. In short, it's what I think ArrayList
  ought to provide. Seq doesn't support concurrent modification detection yet since it's
  meant for single process use and I've never encountered concurrent modifications when
  using ArrayList even when somewhat trying to cause them. One method in Seq uses 4 
  concurrent threads, namely aggregate, however it doesn't change the state of this. An
  effort was made to inform the user when state is changed by returning true if so or false
  if not as with some of the add, update, delete and remove methods (that mostly are synonyms
  of delete methods put in for interface conformance). Those that don't return true/false
  return the deleted or updated element or are presumed to work if no exception was thrown.
  The bottom line is that Seq is mutable but with a tendency to preserve state or announce
  modifications in some way but not absolutely always. None of the static methods change the
  state of their object argument(s) and most generate new Seqs or arrays.
  
  HashList, XarrayList and Seq are demonstrated below for the methods of the List interface
  given in this exercise. More extensive tests/demos of Seq are in ds.Seq.main();
  
 */

public class Ex3527ImplementTheListAPI {
  
  public static void hashListTest() {
    System.out.println("\nHashList test:");
    System.out.println("==============");
    
    HashList<Integer> h = new HashList<>();
    System.out.println("\nHashList<Integer> h initialized with no elements.");
    
    System.out.println("\ntesting public void add(T t):");
    h.add(1);  h.add(5);  h.add(9); 
    System.out.println("h.add(1);  h.add(5);  h.add(9);");
    System.out.print("h = "); h.show();
    
    System.out.println("\npublic void add(int i, T t):");
    h.add(1,2); h.add(2,4); h.add(4,6); h.add(5,8); 
    System.out.println("h.add(1,2); h.add(2,4); h.add(4,6); h.add(5,8);");
    System.out.print("h = "); h.show();
    
    System.out.println("\npublic void addFront(T t):");
    h.addFront(0); h.addFront(-1); 
    System.out.println("h.addFront(0); h.addFront(-1); ");
    System.out.print("h = "); h.show();

    System.out.println("\npublic void addBack(T t):");
    h.addBack(11); h.addBack(15); 
    System.out.println("h.addBack(11); h.addBack(15); ");
    System.out.print("h = "); h.show();

    System.out.println("\npublic void delete(T t):");
    h.delete(new Integer(-1)); h.delete(new Integer(8)); h.delete(new Integer(15)); 
    System.out.println("h.delete(new Integer(-1)); h.delete(new Integer(8)); h.delete(new Integer(15)); ");
    System.out.print("h = "); h.show();

    System.out.println("\npublic T delete(int i):");
    h.delete(2); h.delete(4);
    System.out.println("h.delete(2); h.delete(4);");
    System.out.println("(note after delete(2) the index of 6 is shifted down to 4)");
    System.out.print("h = "); h.show();
    
    System.out.println("\npublic T deleteFront():");
    h.deleteFront();
    System.out.println("h.deleteFront()");
    System.out.print("h = "); h.show();
    
    System.out.println("\npublic T deleteBack():");
    h.deleteBack();
    System.out.println("h.deleteBack()");
    System.out.print("h = "); h.show();
    
    System.out.println("\npublic boolean contains(T t):");
    System.out.println("for (Integer i : h) System.out.println(h.contains(i));");
    for (Integer i : h) System.out.println("h.contains("+i+") = "+h.contains(i));
    System.out.println("now testing contains() on keys that aren't in h:");
    System.out.println("h.contains(0) = "+h.contains(0));
    System.out.println("h.contains(3) = "+h.contains(3));
    System.out.println("h.contains(7) = "+h.contains(7));
    System.out.println("h.contains(11) = "+h.contains(11));
    
    System.out.println("\npublic boolean isEmpty():");
    System.out.println("h.isEmpty = "+h.isEmpty());
    System.out.print("h = "); h.show();

    System.out.println("\npublic int size():");
    System.out.println("h.size() = "+h.size());
    System.out.print("h = "); h.show();

  }
  
  public static void xArrayListTest() {
    System.out.println("\nXarrayList test:");
    System.out.println("================");
    
    XarrayList<Integer> x = new XarrayList<>();
    System.out.println("\nXarrayList<Integer> x initialized with no elements.");
    
    System.out.println("\ntesting public void add(T t):");
    x.add(1);  x.add(5);  x.add(9); 
    System.out.println("x.add(1);  x.add(5);  x.add(9);");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic void add(int i, T t):");
    x.add(1,2); x.add(2,4); x.add(4,6); x.add(5,8); 
    System.out.println("x.add(1,2); x.add(2,4); x.add(4,6); x.add(5,8);");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic void addFront(T t):");
    x.addFront(0); x.addFront(-1); 
    System.out.println("x.addFront(0); x.addFront(-1); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic void addBack(T t):");
    x.addBack(11); x.addBack(15); 
    System.out.println("x.addBack(11); x.addBack(15); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic void delete(T t):");
    x.delete(new Integer(-1)); x.delete(new Integer(8)); x.delete(new Integer(15)); 
    System.out.println("x.delete(new Integer(-1)); x.delete(new Integer(8)); x.delete(new Integer(15)); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic T delete(int i):");
    x.delete(2); x.delete(4);
    System.out.println("x.delete(2); x.delete(4);");
    System.out.println("(note after delete(2) the index of 6 is shifted down to 4)");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic T deleteFront():");
    x.deleteFront();
    System.out.println("x.deleteFront()");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic T deleteBack():");
    x.deleteBack();
    System.out.println("x.deleteBack()");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic boolean contains(T t):");
    System.out.println("for (Integer i : x) System.out.println(x.contains(i));");
    for (Integer i : x) System.out.println("x.contains("+i+") = "+x.contains(i));
    System.out.println("now testing contains() on keys that aren't in x:");
    System.out.println("x.contains(0) = "+x.contains(0));
    System.out.println("x.contains(3) = "+x.contains(3));
    System.out.println("x.contains(7) = "+x.contains(7));
    System.out.println("x.contains(11) = "+x.contains(11));
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic boolean isEmpty():");
    System.out.println("x.isEmpty = "+x.isEmpty());
    System.out.print("x = "); x.show();

    System.out.println("\npublic int size():");
    System.out.println("x.size() = "+x.size());
    System.out.print("x = "); x.show();

  }
  
  public static void seqTest() {
    System.out.println("\nSeq test:");
    System.out.println("================");
    
    Seq<Integer> x = new Seq<>();
    System.out.println("\nSeq<Integer> x initialized with no elements.");
    
    System.out.println("\ntesting public void add(T t):");
    x.add(1);  x.add(5);  x.add(9); 
    System.out.println("x.add(1);  x.add(5);  x.add(9);");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic void add(int i, T t):");
    x.add(1,2); x.add(2,4); x.add(4,6); x.add(5,8); 
    System.out.println("x.add(1,2); x.add(2,4); x.add(4,6); x.add(5,8);");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic void addFront(T t):");
    x.addFront(0); x.addFront(-1); 
    System.out.println("x.addFront(0); x.addFront(-1); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic void addBack(T t):");
    x.addBack(11); x.addBack(15); 
    System.out.println("x.addBack(11); x.addBack(15); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic void delete(T t):");
    x.delete(new Integer(-1)); x.delete(new Integer(8)); x.delete(new Integer(15)); 
    System.out.println("x.delete(new Integer(-1)); x.delete(new Integer(8)); x.delete(new Integer(15)); ");
    System.out.print("x = "); x.show();

    System.out.println("\npublic T delete(int i):");
    x.delete(2); x.delete(4);
    System.out.println("x.delete(2); x.delete(4);");
    System.out.println("(note after delete(2) the index of 6 is shifted down to 4)");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic T deleteFront():");
    x.deleteFront();
    System.out.println("x.deleteFront()");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic T deleteBack():");
    x.deleteBack();
    System.out.println("x.deleteBack()");
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic boolean contains(T t):");
    System.out.println("for (Integer i : x) System.out.println(x.contains(i));");
    for (Integer i : x) System.out.println("x.contains("+i+") = "+x.contains(i));
    System.out.println("now testing contains() on keys that aren't in x:");
    System.out.println("x.contains(0) = "+x.contains(0));
    System.out.println("x.contains(3) = "+x.contains(3));
    System.out.println("x.contains(7) = "+x.contains(7));
    System.out.println("x.contains(11) = "+x.contains(11));
    System.out.print("x = "); x.show();
    
    System.out.println("\npublic boolean isEmpty():");
    System.out.println("x.isEmpty = "+x.isEmpty());
    System.out.print("x = "); x.show();

    System.out.println("\npublic int size():");
    System.out.println("x.size() = "+x.size());
    System.out.print("x = "); x.show();

  }

  public static void main(String[] args) {
    
    hashListTest();

    xArrayListTest();
    
    seqTest();
    
  }

}

