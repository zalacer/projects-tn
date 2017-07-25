package ex13;

import ds.GeneralizedArrayQueue;
import ds.GeneralizedQueue;

// I am interpreting "least recently inserted" to mean oldest, so that the kth least 
// recently inserted item" means the kth from the oldest or last, which is the oldest, 1st 
// oldest or 1st least recently inserted item.


public class Ex1338GeneralizedQueue {
  
  // this is the client
  public static void main(String[] args) {
    // this demonstrates empty constructor, isEmpty(), insert() and delete()
    // for GeneralizedArrayQueue
    Integer[] ia = {1,2,3,4,5,6,7,8,9};
    GeneralizedArrayQueue<Integer> g = new GeneralizedArrayQueue<Integer>();
    System.out.println(g); //GeneralizedArrayQueue()
    System.out.println(g.isEmpty()); //true
    for (Integer i : ia) g.insert(i);
    System.out.println(g); //GeneralizedArrayQueue(1,2,3,4,5,6,7,8,9)
    System.out.println(g.isEmpty()); //false
    Integer last = g.delete(1);
    System.out.println(last); //9
    System.out.println(g); //GeneralizedArrayQueue(1,2,3,4,5,6,7,8)
    Integer fifthFromLast = g.delete(5); 
    System.out.println(fifthFromLast); //4
    System.out.println(g); //GeneralizedArrayQueue(1,2,3,5,6,7,8)
    Integer first = g.delete(g.size());
    System.out.println(first); //1
    System.out.println(g); //GeneralizedArrayQueue(2,3,5,6,7,8)
    //System.out.println(g.delete(g.size()+1));
    //NoSuchElementException: delete: kth from last element does not exist
    System.out.println(g.delete(g.size())); //2
    System.out.println(g); //GeneralizedArrayQueue(3,5,6,7,8)
    System.out.println(g.delete(g.size())); //3
    System.out.println(g); //GeneralizedArrayQueue(5,6,7,8)
    System.out.println(g.delete(g.size())); //5
    System.out.println(g); //GeneralizedArrayQueue(6,7,8)
    System.out.println(g.delete(g.size())); //6
    System.out.println(g); //GeneralizedArrayQueue(7,8)
    System.out.println(g.delete(g.size())); //7
    System.out.println(g); //GeneralizedArrayQueue(8)
    System.out.println(g.delete(g.size())); //8
    System.out.println(g); //GeneralizedArrayQueue()
    System.out.println(g.isEmpty()); //true
    //System.out.println(g.delete(g.size()));
    //NoSuchElementException: delete: queue underflow
    
    System.out.println();
    
    // this demonstrates empty constructor, isEmpty(), insert() and delete()
    // for the linked-list based GeneralizedQueue
    GeneralizedQueue<Integer> q = new GeneralizedQueue<Integer>();
    System.out.println(q); //GeneralizedQueue()
    System.out.println(q.isEmpty()); //true
    for (Integer i : ia) q.insert(i);
    System.out.println(q); //GeneralizedQueue(1,2,3,4,5,6,7,8,9)
    System.out.println(q.isEmpty()); //false
    last = q.delete(1);
    System.out.println(last); //9
    System.out.println(q); //GeneralizedQueue(1,2,3,4,5,6,7,8)
    fifthFromLast = q.delete(5); 
    System.out.println(fifthFromLast); //4
    System.out.println(q); //GeneralizedQueue(1,2,3,5,6,7,8)
    first = q.delete(q.size());
    System.out.println(first); //1
    System.out.println(q); //GeneralizedQueue(2,3,5,6,7,8)
    //System.out.println(q.delete(q.size()+1));
    //NoSuchElementException: remove: kth from last element does not exist
    System.out.println(q.delete(q.size())); //2
    System.out.println(q); //GeneralizedQueue(3,5,6,7,8)
    System.out.println(q.delete(q.size())); //3
    System.out.println(q); //GeneralizedQueue(5,6,7,8)
    System.out.println(q.delete(q.size())); //5
    System.out.println(q); //GeneralizedQueue(6,7,8)
    System.out.println(q.delete(q.size())); //6
    System.out.println(q); //GeneralizedQueue(7,8)
    System.out.println(q.delete(q.size())); //7
    System.out.println(q); //GeneralizedQueue(8)
    System.out.println(q.delete(q.size())); //8
    System.out.println(q); //GeneralizedQueue()
    System.out.println(q.isEmpty()); //true
    //System.out.println(g.delete(q.size()));
    // NoSuchElementException: remove: queue underflow
    
  }

}
