package ex35;

import st.SequentialSearchSET;

/* p507
  3.5.2  Develop a SET implementation SequentialSearchSET by starting 
  with the code for SequentialSearchST and eliminating all of the code 
  involving values.
  
  This is implemented as st.SequentialSearchSET. There is a demo of it
  below.
 
*/

public class Ex3502ImplementSequentialSearchSETusingSequentialSearchST {
  
  public static void main(String[] args) {
    
    System.out.println("SequentialSearchSET demo:");
    
    SequentialSearchSET<Integer> set1 = new SequentialSearchSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) set1.add(i);
    System.out.println("set1 = "+set1);

    SequentialSearchSET<Integer> set2 = new SequentialSearchSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) set2.add(i);
    System.out.println("set2 = "+set2);

    SequentialSearchSET<Integer> union1 = set1.union(set2);
    System.out.println("set1 union set2 = "+union1);

    for (int i = 1; i < 16; i++) assert union1.contains(i);

    SequentialSearchSET<Integer> intersection1 = set1.intersection(set2);
    System.out.println("set1 intersect set2 = "+intersection1);

    for (int i = 6; i < 11; i++) assert intersection1.contains(i);
    
/*
    SequentialSearchSET demo:
    set1 = (1,2,3,4,5,6,7,8,9,10)
    set2 = (6,7,8,9,10,11,12,13,14,15)
    set1 union set2 = (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
    set1 intersect set2 = (6,7,8,9,10)

*/
    
  }

}

