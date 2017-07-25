package ex24;

import static pq.MaxPQex2409.printHeaps;

/* p329
  2.4.9  Draw all of the different heaps that can be made from the five keys
  A B C D E, then draw all of the different heaps that can be made from the 
  five keys  A A A B B.
  
  The heaps in array format and drawn can be printed by running main.
  
 */

public class Ex2409DrawAllHeapsWith5GivenKeys {

  public static void main(String[] args) {
    
    printHeaps(new String[]{"A","B","C","D","E"});
    System.out.println();
    printHeaps(new String[]{"A","A","A","B","B"});  
    
/*
    [A,B,C,D,E] has 8 heaps:
    
    [E,C,D,B,A]
    [E,D,C,A,B]
    [E,D,B,A,C]
    [E,C,D,A,B]
    [E,D,A,C,B]
    [E,D,B,C,A]
    [E,D,C,B,A]
    [E,D,A,B,C]
    
       E       
      / \   
     /   \  
     C   D   
    / \     
    B A     
                    
       E       
      / \   
     /   \  
     D   C   
    / \     
    A B     
                    
       E       
      / \   
     /   \  
     D   B   
    / \     
    A C     
                    
       E       
      / \   
     /   \  
     C   D   
    / \     
    A B     
                    
       E       
      / \   
     /   \  
     D   A   
    / \     
    C B     
                    
       E       
      / \   
     /   \  
     D   B   
    / \     
    C A     
                    
       E       
      / \   
     /   \  
     D   C   
    / \     
    B A     
                    
       E       
      / \   
     /   \  
     D   A   
    / \     
    B C     
                    
    
    [A,A,A,B,B] has 2 heaps:
    
    [B,B,A,A,A]
    [B,A,B,A,A]
    
       B       
      / \   
     /   \  
     B   A   
    / \     
    A A     
                    
       B       
      / \   
     /   \  
     A   B   
    / \     
    A A     
                
*/
    
  }

}
