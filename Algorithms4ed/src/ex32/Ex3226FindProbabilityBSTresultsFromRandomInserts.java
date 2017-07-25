package ex32;

import static v.ArrayUtils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import st.BSTX;

/* p419
  3.2.26 Exact probabilities. Find the probability that each of the trees 
  in Exercise 3.2.9 is the result of inserting N random distinct elements 
  into an initially empty tree.
  
  This means find the normalized frequencies of each shape and assuming
  the random distinct elements are 2, 3, 4, 5 and 6.
  
*/             

public class Ex3226FindProbabilityBSTresultsFromRandomInserts {
  
  public static void findProbabilitiesOfUniqueShapes() {
    Integer[] a = {2,3,4,5,6}, b = rangeInteger(0,5), 
        d = new Integer[5], e; String es;
    int[] c;
    Map<String, Integer> map1 = new HashMap<>();
    Map<String,BSTX<Integer,Integer>> map2 = new HashMap<>();
    BSTX<Integer,Integer> bst;
    Iterator<int[]> it = permutations(range(0,5));
    while(it.hasNext()) {
      c = it.next();
      for (int i = 0; i < d.length; i++) d[i] = a[c[i]];
      bst = new BSTX<Integer,Integer>(d,b);
      e = bst.toPreOrderArray();
      es = arrayToString(e,80,1,1);
      if (map1.containsKey(es)) map1.put(es, map1.get(es)+1);
      else map1.put(es, 1);
      if (!map2.containsKey(es)) map2.put(es, bst);
    }
    assert map1.size() == map2.size();
    System.out.println("there are "+map1.size()+" unique shapes with the following probabilities:\n"); 
    int sum = 0; double p;
    for (String q : map1.keySet()) sum += map1.get(q);
    for (String q : map1.keySet()) {
      p = 1.*map1.get(q)/sum;
      System.out.println("probability "+p);
      BSTX<Integer,Integer> bx = map2.get(q);
      bx.printTree(); System.out.println();
    }
  }

  public static void main(String[] args) {
    
    findProbabilitiesOfUniqueShapes();
/*
    there are 42 unique shapes with the following probabilities:
    
    probability 0.008333333333333333
     \-----6
           |                     /-----5
           |              /-----4
           |       /-----3
            \-----2
    
    probability 0.008333333333333333
     \-----6
           |              /-----5
           |             |       \-----4
           |       /-----3
            \-----2
    
    probability 0.025
     \-----6
           |       /-----5
            \-----4
                   \-----3
                          \-----2
    
    probability 0.03333333333333333
    |       /-----6
    |      |      |       /-----5
    |      |       \-----4
     \-----3
            \-----2
    
    probability 0.03333333333333333
    |       /-----6
    |      |       \-----5
    |      |              \-----4
     \-----3
            \-----2
    
    probability 0.025
    |              /-----6
    |       /-----5
    |      |      |       /-----4
    |      |       \-----3
     \-----2
    
    probability 0.008333333333333333
    |       /-----6
    |      |       \-----5
    |      |             |       /-----4
    |      |              \-----3
     \-----2
    
    probability 0.008333333333333333
    |       /-----6
    |      |       \-----5
    |      |              \-----4
    |      |                     \-----3
     \-----2
    
    probability 0.03333333333333333
    |       /-----6
     \-----5
            \-----4
                  |       /-----3
                   \-----2
    
    probability 0.025
     \-----6
           |       /-----5
            \-----4
                  |       /-----3
                   \-----2
    
    probability 0.06666666666666667
    |              /-----6
    |       /-----5
    |      |       \-----4
     \-----3
            \-----2
    
    probability 0.008333333333333333
     \-----6
            \-----5
                   \-----4
                         |       /-----3
                          \-----2
    
    probability 0.008333333333333333
     \-----6
            \-----5
                   \-----4
                          \-----3
                                 \-----2
    
    probability 0.03333333333333333
    |                     /-----6
    |              /-----5
    |       /-----4
     \-----3
            \-----2
    
    probability 0.008333333333333333
    |              /-----6
    |             |      |       /-----5
    |             |       \-----4
    |       /-----3
     \-----2
    
    probability 0.05
    |              /-----6
    |       /-----5
     \-----4
            \-----3
                   \-----2
    
    probability 0.03333333333333333
    |              /-----6
    |             |       \-----5
    |       /-----4
     \-----3
            \-----2
    
    probability 0.008333333333333333
    |              /-----6
    |             |       \-----5
    |             |              \-----4
    |       /-----3
     \-----2
    
    probability 0.05
    |       /-----6
    |      |       \-----5
     \-----4
            \-----3
                   \-----2
    
    probability 0.016666666666666666
    |       /-----6
    |      |      |       /-----5
    |      |       \-----4
    |      |              \-----3
     \-----2
    
    probability 0.008333333333333333
    |       /-----6
    |      |      |       /-----5
    |      |      |      |       \-----4
    |      |       \-----3
     \-----2
    
    probability 0.016666666666666666
     \-----6
            \-----5
                  |       /-----4
                   \-----3
                          \-----2
    
    probability 0.06666666666666667
    |       /-----6
     \-----5
           |       /-----4
            \-----3
                   \-----2
    
    probability 0.025
    |                     /-----6
    |              /-----5
    |       /-----4
    |      |       \-----3
     \-----2
    
    probability 0.025
    |              /-----6
    |             |       \-----5
    |       /-----4
    |      |       \-----3
     \-----2
    
    probability 0.008333333333333333
    |       /-----6
    |      |      |              /-----5
    |      |      |       /-----4
    |      |       \-----3
     \-----2
    
    probability 0.008333333333333333
     \-----6
           |       /-----5
           |      |      |       /-----4
           |      |       \-----3
            \-----2
    
    probability 0.008333333333333333
     \-----6
           |       /-----5
           |      |       \-----4
           |      |              \-----3
            \-----2
    
    probability 0.03333333333333333
    |       /-----6
     \-----5
           |       /-----4
           |      |       \-----3
            \-----2
    
    probability 0.016666666666666666
    |                     /-----6
    |              /-----5
    |             |       \-----4
    |       /-----3
     \-----2
    
    probability 0.008333333333333333
    |                            /-----6
    |                     /-----5
    |              /-----4
    |       /-----3
     \-----2
    
    probability 0.008333333333333333
    |                     /-----6
    |                    |       \-----5
    |              /-----4
    |       /-----3
     \-----2
    
    probability 0.025
    |              /-----6
    |       /-----5
    |      |       \-----4
    |      |              \-----3
     \-----2
    
    probability 0.025
     \-----6
           |              /-----5
           |       /-----4
            \-----3
                   \-----2
    
    probability 0.025
     \-----6
           |       /-----5
           |      |       \-----4
            \-----3
                   \-----2
    
    probability 0.05
    |              /-----6
    |       /-----5
     \-----4
           |       /-----3
            \-----2
    
    probability 0.05
    |       /-----6
    |      |       \-----5
     \-----4
           |       /-----3
            \-----2
    
    probability 0.008333333333333333
     \-----6
            \-----5
                  |              /-----4
                  |       /-----3
                   \-----2
    
    probability 0.008333333333333333
     \-----6
            \-----5
                  |       /-----4
                  |      |       \-----3
                   \-----2
    
    probability 0.03333333333333333
    |       /-----6
     \-----5
            \-----4
                   \-----3
                          \-----2
    
    probability 0.016666666666666666
     \-----6
           |              /-----5
           |       /-----4
           |      |       \-----3
            \-----2
    
    probability 0.03333333333333333
    |       /-----6
     \-----5
           |              /-----4
           |       /-----3
            \-----2

    
*/  
  }

}

