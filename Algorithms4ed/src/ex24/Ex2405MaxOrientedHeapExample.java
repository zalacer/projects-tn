package ex24;

import pq.MaxPQ;

/* p329
  2.4.5  Give the heap that results when the keys E A S Y Q U E S T I O N  
  are inserted in that order into an initially empty max-oriented heap.
  
  The level ordered array created is [Y,T,U,S,Q,S,E,E,A,I,O,N] which as a
  binary tree looks like:
  
                         Y
                    T         U
                 S    Q     S   E
                E A  I O    N
 */

public class Ex2405MaxOrientedHeapExample {

  public static void main(String[] args) {

    MaxPQ<String> pq = new MaxPQ<String>("EASYQUESTION".split(""));
    pq.show(); // Y T U S Q S E E A I O N 
    
                          
                         
  
  }

}
