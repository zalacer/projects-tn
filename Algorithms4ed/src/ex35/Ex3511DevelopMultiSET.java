package ex35;

import st.SequentialSearchMultiSET;

/* p507
  3.5.11  Develop a MultiSET class that is like SET, but allows 
  equal keys and thus implements a mathematical multiset.
  
  One implementation is st.SequentialSearchMultiSET. This is implemented
  by adding actual duplicate keys because with a linked list that's the
  easiest approach, although it takes extra space and delete() is somewhat
  tricky. SequentialSearchMultiSET is equipped with union(), multiDiff(),
  multiIntersect() and the normal intersect named incorrectIntersect().
  This class and its methods are tested below.
  
 */

public class Ex3511DevelopMultiSET {

  public static void main(String[] args) {
    
    Integer[] a = {1,2,2,3,3,3,4,4,4,4,4,4,4,4,5};

    SequentialSearchMultiSET<Integer> set1 = new SequentialSearchMultiSET<>(a);
    System.out.println("set1 = "+set1);
    
    Integer[] b = {5,5,5,5,5,6,6,6,6,7,7,8,8,9};
    
    SequentialSearchMultiSET<Integer> set2 = new SequentialSearchMultiSET<>(b);
    System.out.println("set2 = "+set2);
    
    SequentialSearchMultiSET<Integer> union12 = set1.union(set2);
    System.out.println("union12 = "+union12);
    
    SequentialSearchMultiSET<Integer> multiIntersect12 = set1.multiIntersect(set2);
    System.out.println("multiIntersect12 = "+multiIntersect12);
    
    SequentialSearchMultiSET<Integer> incorrectIntersect12 = set1.incorrectIntersect(set2);
    System.out.println("incorrectIntersect12 = "+incorrectIntersect12);
    
    SequentialSearchMultiSET<Integer> multiDiff12 = set1.multiDiff(set2);
    System.out.println("multiDiff12 = "+multiDiff12);
    
    System.out.println("union12.delete(5);"); union12.delete(5);
    System.out.println("union12 = "+union12);
    
    Integer[] c = {1,2,4,5};
    
    SequentialSearchMultiSET<Integer> set3 = new SequentialSearchMultiSET<>(c);
    System.out.println("set3 = "+set3);
    
    SequentialSearchMultiSET<Integer> multiIntersect13 = set1.multiIntersect(set3);
    System.out.println("multiIntersect13 = "+multiIntersect13);
    
    SequentialSearchMultiSET<Integer> multiDiff13 = set1.multiDiff(set3);
    System.out.println("multiDiff13 = "+multiDiff13);

    SequentialSearchMultiSET<Integer> multiIntersect31 = set3.multiIntersect(set1);
    System.out.println("multiIntersect31 = "+multiIntersect31);
    
    SequentialSearchMultiSET<Integer> multiDiff31 = set3.multiDiff(set1);
    System.out.println("multiDiff31 = "+multiDiff31);
    
    SequentialSearchMultiSET<Integer> set4 = new SequentialSearchMultiSET<>();
    
    SequentialSearchMultiSET<Integer> set5 = new SequentialSearchMultiSET<>();
    
    SequentialSearchMultiSET<Integer> multiIntersect45 = set4.multiIntersect(set5);
    System.out.println("multiIntersect45 = "+multiIntersect45);
    
    SequentialSearchMultiSET<Integer> multiDiff45 = set4.multiDiff(set5);
    System.out.println("multiDiff45 = "+multiDiff45);

/*
    set1 = {1,2,2,3,3,3,4,4,4,4,4,4,4,4,5}
    set2 = {5,5,5,5,5,6,6,6,6,7,7,8,8,9}
    union12 = {1,2,2,3,3,3,4,4,4,4,4,4,4,4,5,5,5,5,5,5,6,6,6,6,7,7,8,8,9}
    multiIntersect12 = {5}
    incorrectIntersect12 = {5,5,5,5,5}
    multiDiff12 = {1,2,2,3,3,3,4,4,4,4,4,4,4,4}
    union12.delete(5);
    union12 = {1,2,2,3,3,3,4,4,4,4,4,4,4,4,6,6,6,6,7,7,8,8,9}
    set3 = {1,2,4,5}
    multiIntersect13 = {1,2,4,5}
    multiDiff13 = {2,3,3,3,4,4,4,4,4,4,4}
    multiIntersect31 = {1,2,4,5}
    multiDiff31 = {}
    multiIntersect45 = {}
    multiDiff45 = {}

 
*/

  }

}

