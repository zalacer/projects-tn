package st;

// API for a list data type from ex3527 p511

public interface SeqInterface<T> {  
  public void add(T t);
  public void add(int i, T t);
  public void addFront(T t);
  public void addBack(T t);
  public void delete(T t);
  public T delete(int i);
  public T deleteFront();
  public T deleteBack();
  public boolean contains(T t);
  public boolean isEmpty();
  public int size();
  
//  List() create a list


//  boolean contains(Item item)  is  key in the list?
//  boolean isEmpty()  is the list empty?
//  int size()  number of items in the list
  
  
//void add(int i, Item item)  add  item as the  ith in the list
//void addFront(Item item)  add  item to the front
//void addBack(Item item)  add  item to the back
//Item deleteFront()  remove from the front
//Item deleteBack()  remove from the back
//void delete(Item item)  remove  item from the list
//Item delete(int i)  remove the  i th item from the list  
  
//  
//  Hint : Use two symbol tables, one to find the ith item in the list 
//  efficiently, and the other to efficiently search by item. (Java’s  
//  java.util.List interface contains methods like these but does not 
//  supply any implementation that efficiently supports all operations.)
  

}
