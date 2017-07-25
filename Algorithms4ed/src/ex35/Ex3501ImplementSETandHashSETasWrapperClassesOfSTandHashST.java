package ex35;

import st.HashSET;
import st.TreeSET;

/* p507
  3.5.1 Implement SET and HashSET as “wrapper class” clients of ST
  and HashST, respectively (provide dummy values and ignore them).
  
  Why say "clients" and not just "wrappers of"?
  
  SET is implemented as st.TreeSET since its uses ST that uses TreeSet
  and it implements st.OrderedSET.
  
  HashSET is implemented as st.HashSET, uses SeparateChainingHashST and 
  implements st.UnorderedSET.
  
  st.TreeSET and st.HashSET are demonstrated below.
 
*/


public class Ex3501ImplementSETandHashSETasWrapperClassesOfSTandHashST {
  
  public static void main(String[] args) {
    
    System.out.println("TreeSET demo:");
    
    TreeSET<Integer> t1 = new TreeSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) t1.add(i);
    System.out.println("t1 = "+t1);
    
    TreeSET<Integer> t2 = new TreeSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) t2.add(i);
    System.out.println("t2 = "+t2);
    
    TreeSET<Integer> union1 = t1.union(t2);
    System.out.println("t1 union t2 = "+union1);
    
    TreeSET<Integer> intersection1 = t1.intersection(t2);
    System.out.println("t1 intersect t2 = "+intersection1);
    
    System.out.println("\nHashSET demo:");
    
    HashSET<Integer> h1 = new HashSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) h1.add(i);
    System.out.println("h1 = "+h1);
    
    HashSET<Integer> h2 = new HashSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) h2.add(i);
    System.out.println("h2 = "+h2);
    
    HashSET<Integer> union2 = h1.union(h2);
    System.out.println("h1 union h2 = "+union2);
    
    HashSET<Integer> intersection2 = h1.intersection(h2);
    System.out.println("h1 intersect h2 = "+intersection2);
    
/*
    TreeSET demo:
    t1 = (1,2,3,4,5,6,7,8,9,10)
    t2 = (6,7,8,9,10,11,12,13,14,15)
    t1 union t2 = (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
    t1 intersect t2 = (6,7,8,9,10)
    
    HashSET demo:
    h1 = (4,8,1,5,9,2,6,10,3,7)
    h2 = (8,12,9,13,6,10,14,7,11,15)
    h1 union h2 = (4,8,12,1,5,9,13,2,6,10,14,3,7,11,15)
    h1 intersect h2 = (8,9,6,10,7)    
*/    
  }

}

