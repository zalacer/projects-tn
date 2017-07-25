package ex35;

import static v.ArrayUtils.*;

import st.BinarySearchSET;

/* p507
  3.5.3  Develop a SET implementation BinarySearchSET by starting with 
  the code for BinarySearchST and eliminating all of the code involving 
  values.
  
  This is implemented as st.BinarySearchSET.  There is a demo of it below.
 
*/

public class Ex3503ImplementBinarySearchSETusingBinarySearchST {
  
  public static void main(String[] args) {
    
    System.out.println("BinarySearchSET demo:");

    BinarySearchSET<Integer> set1 = new BinarySearchSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) set1.add(i);
    System.out.println("set1 = "+set1);

    BinarySearchSET<Integer> set2 = new BinarySearchSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) set2.add(i);
    System.out.println("set2 = "+set2);

    BinarySearchSET<Integer> union1 = set1.union(set2);
    System.out.println("set1 union set2 = "+union1);

    for (int i = 1; i < 16; i++) assert union1.contains(i);

    BinarySearchSET<Integer> intersection1 = set1.intersection(set2);
    System.out.println("set1 intersect set2 = "+intersection1);

    for (int i = 6; i < 11; i++) assert intersection1.contains(i);
    
    System.out.println("set1 min = "+set1.min());
    
    System.out.println("set2 max = "+set2.max());
    
    BinarySearchSET<Integer> set3 = new BinarySearchSET<>();
    int[] a = range(0,12,3); for (int i : a) set3.add(i);
    System.out.println("set3 = "+set3);
    
    System.out.println("ceiling of 5 in set3 = "+set3.ceiling(5));
    
    System.out.println("floor of 7 in set3 = "+set3.floor(7));

/*
    BinarySearchSET demo:
    set1 = 1 2 3 4 5 6 7 8 9 10
    set2 = 6 7 8 9 10 11 12 13 14 15
    set1 union set2 = 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
    set1 intersect set2 = 6 7 8 9 10
    set1 min = 1
    set2 max = 15
    set3 = 0 3 6 9
    ceiling of 5 in set3 = 6
    floor of 7 in set3 = 6
 
*/

  }

}

