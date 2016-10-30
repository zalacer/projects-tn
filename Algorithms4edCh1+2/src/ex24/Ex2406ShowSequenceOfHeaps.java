package ex24;

import static v.ArrayUtils.par;
import pq.MaxPQ;

/* p329
   2.4.6  Using the conventions of Exercise 2.4.1, give the sequence of heaps
   produced when the operations  
         P R I O * R * * I * T * Y * * * Q U E * * * U * E 
   are performed on an initially empty max-oriented heap.
   
    []
    <heap is empty>
    
    [P]
    P 
        
    [R,P]
     R   
    /   
    P   
            
    [R,P,I]
     R   
    / \ 
    P I 
            
    [R,P,I,O]
       R       
      / \   
     /   \  
     P   I   
    /       
    O       
                    
    [P,O,I]
     P   
    / \ 
    O I 
            
    [R,P,I,O]
       R       
      / \   
     /   \  
     P   I   
    /       
    O       
                    
    [P,O,I]
     P   
    / \ 
    O I 
            
    [O,I]
     O   
    /   
    I   
            
    [O,I,I]
     O   
    / \ 
    I I 
            
    [I,I]
     I   
    /   
    I   
            
    [T,I,I]
     T   
    / \ 
    I I 
            
    [I,I]
     I   
    /   
    I   
            
    [Y,I,I]
     Y   
    / \ 
    I I 
            
    [I,I]
     I   
    /   
    I   
            
    [I]
    I 
        
    []
    <heap is empty>
    
    [O]
    O 
        
    [U,O]
     U   
    /   
    O   
            
    [U,O,E]
     U   
    / \ 
    O E 
            
    [O,E]
     O   
    /   
    E   
            
    [E]
    E 
        
    []
    <heap is empty>


 */

public class Ex2406ShowSequenceOfHeaps {

  public static void main(String[] args) {

    MaxPQ<String> pq = new  MaxPQ<String>();
                     par(pq.toArray()); pq.printHeap();
    pq.insert("P");  par(pq.toArray()); pq.printHeap();
    pq.insert("R");  par(pq.toArray()); pq.printHeap();
    pq.insert("I");  par(pq.toArray()); pq.printHeap();
    pq.insert("O");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.insert("R");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.insert("I");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.insert("T");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.insert("Y");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.insert("O");  par(pq.toArray()); pq.printHeap();
    pq.insert("U");  par(pq.toArray()); pq.printHeap();
    pq.insert("E");  par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
    pq.delMax();     par(pq.toArray()); pq.printHeap();
  
  }

}
