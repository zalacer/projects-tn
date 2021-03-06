package ex32;

import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.permutations;
import static v.ArrayUtils.range;
import static v.ArrayUtils.rangeInteger;

import java.util.Iterator;

import st.BSTX;
import v.Tuple2;

/* p417
  3.2.9  Draw all the different BST shapes that can result when 
  N keys are inserted into an initially empty tree, for  
  N = 2, 3, 4, 5 and  6.
  
  This is done below using preorder to differentiate the shapes
  and inorder to print them out.
 
*/

public class Ex3209AllBSTShapesWith5Keys {
  
  public static void drawAllUniqueShapes() {
    Integer[] a = {2,3,4,5,6}, b = rangeInteger(0,5), d = new Integer[5]; 
    int[] c; String es, ds;
    BSTX<String,Tuple2<String,BSTX<Integer,Integer>>> map = new BSTX<>();
    BSTX<Integer,Integer> bst;
    Iterator<int[]> it = permutations(range(0,5));
    while(it.hasNext()) {
      c = it.next();
      for (int i = 0; i < d.length; i++) d[i] = a[c[i]];
      bst = new BSTX<Integer,Integer>(d,b);
      ds = arrayToString(d,80,1,1);
      es = arrayToString(bst.toPreOrderArray(),80,1,1);
      if (map.contains(es)) map.get(es)._1 += " "+ds;
      else map.put(es, new Tuple2<String,BSTX<Integer,Integer>>(ds,bst));
    }
    System.out.println("there are "+map.size()+" unique shapes as follows:\n");
    Iterator<String> ti = map.inOrder().iterator();
    while (ti.hasNext()){
      Tuple2<String,BSTX<Integer,Integer>> g = map.get(ti.next());
      System.out.println(g._1);
      g._2.printTree(); System.out.println();
    } 
  }
  
  public static void main(String[] args) {
    
    drawAllUniqueShapes();
/*
    there are 42 unique shapes as follows:
    
    [2,3,4,5,6]
    |                            /-----6
    |                     /-----5
    |              /-----4
    |       /-----3
     \-----2
    
    [2,3,4,6,5]
    |                     /-----6
    |                    |       \-----5
    |              /-----4
    |       /-----3
     \-----2
    
    [2,3,5,6,4] [2,3,5,4,6]
    |                     /-----6
    |              /-----5
    |             |       \-----4
    |       /-----3
     \-----2
    
    [2,3,6,4,5]
    |              /-----6
    |             |      |       /-----5
    |             |       \-----4
    |       /-----3
     \-----2
    
    [2,3,6,5,4]
    |              /-----6
    |             |       \-----5
    |             |              \-----4
    |       /-----3
     \-----2
    
    [2,4,5,3,6] [2,4,5,6,3] [2,4,3,5,6]
    |                     /-----6
    |              /-----5
    |       /-----4
    |      |       \-----3
     \-----2
    
    [2,4,6,5,3] [2,4,6,3,5] [2,4,3,6,5]
    |              /-----6
    |             |       \-----5
    |       /-----4
    |      |       \-----3
     \-----2
    
    [2,5,3,4,6] [2,5,3,6,4] [2,5,6,3,4]
    |              /-----6
    |       /-----5
    |      |      |       /-----4
    |      |       \-----3
     \-----2
    
    [2,5,6,4,3] [2,5,4,6,3] [2,5,4,3,6]
    |              /-----6
    |       /-----5
    |      |       \-----4
    |      |              \-----3
     \-----2
    
    [2,6,3,4,5]
    |       /-----6
    |      |      |              /-----5
    |      |      |       /-----4
    |      |       \-----3
     \-----2
    
    [2,6,3,5,4]
    |       /-----6
    |      |      |       /-----5
    |      |      |      |       \-----4
    |      |       \-----3
     \-----2
    
    [2,6,4,5,3] [2,6,4,3,5]
    |       /-----6
    |      |      |       /-----5
    |      |       \-----4
    |      |              \-----3
     \-----2
    
    [2,6,5,3,4]
    |       /-----6
    |      |       \-----5
    |      |             |       /-----4
    |      |              \-----3
     \-----2
    
    [2,6,5,4,3]
    |       /-----6
    |      |       \-----5
    |      |              \-----4
    |      |                     \-----3
     \-----2
    
    [3,4,2,5,6] [3,4,5,6,2] [3,4,5,2,6] [3,2,4,5,6]
    |                     /-----6
    |              /-----5
    |       /-----4
     \-----3
            \-----2
    
    [3,4,2,6,5] [3,4,6,2,5] [3,4,6,5,2] [3,2,4,6,5]
    |              /-----6
    |             |       \-----5
    |       /-----4
     \-----3
            \-----2
    
    [3,5,4,2,6] [3,5,4,6,2] [3,5,6,4,2] [3,5,6,2,4] [3,5,2,6,4] [3,5,2,4,6] 
    [3,2,5,4,6] [3,2,5,6,4]
    |              /-----6
    |       /-----5
    |      |       \-----4
     \-----3
            \-----2
    
    [3,6,4,2,5] [3,6,4,5,2] [3,6,2,4,5] [3,2,6,4,5]
    |       /-----6
    |      |      |       /-----5
    |      |       \-----4
     \-----3
            \-----2
    
    [3,6,5,4,2] [3,6,5,2,4] [3,2,6,5,4] [3,6,2,5,4]
    |       /-----6
    |      |       \-----5
    |      |              \-----4
     \-----3
            \-----2
    
    [4,2,3,5,6] [4,2,5,6,3] [4,2,5,3,6] [4,5,2,3,6] [4,5,2,6,3] [4,5,6,2,3]
    |              /-----6
    |       /-----5
     \-----4
           |       /-----3
            \-----2
    
    [4,2,3,6,5] [4,2,6,3,5] [4,6,2,3,5] [4,6,2,5,3] [4,2,6,5,3] [4,6,5,2,3]
    |       /-----6
    |      |       \-----5
     \-----4
           |       /-----3
            \-----2
    
    [4,5,6,3,2] [4,5,3,6,2] [4,5,3,2,6] [4,3,5,2,6] [4,3,5,6,2] [4,3,2,5,6]
    |              /-----6
    |       /-----5
     \-----4
            \-----3
                   \-----2
    
    [4,6,5,3,2] [4,3,6,5,2] [4,6,3,5,2] [4,6,3,2,5] [4,3,6,2,5] [4,3,2,6,5]
    |       /-----6
    |      |       \-----5
     \-----4
            \-----3
                   \-----2
    
    [5,6,2,3,4] [5,2,6,3,4] [5,2,3,6,4] [5,2,3,4,6]
    |       /-----6
     \-----5
           |              /-----4
           |       /-----3
            \-----2
    
    [5,2,4,3,6] [5,2,4,6,3] [5,2,6,4,3] [5,6,2,4,3]
    |       /-----6
     \-----5
           |       /-----4
           |      |       \-----3
            \-----2
    
    [5,6,3,4,2] [5,3,6,4,2] [5,3,4,6,2] [5,3,4,2,6] [5,3,2,4,6] [5,3,2,6,4] 
    [5,3,6,2,4] [5,6,3,2,4]
    |       /-----6
     \-----5
           |       /-----4
            \-----3
                   \-----2
    
    [5,6,4,2,3] [5,4,6,2,3] [5,4,2,6,3] [5,4,2,3,6]
    |       /-----6
     \-----5
            \-----4
                  |       /-----3
                   \-----2
    
    [5,4,3,2,6] [5,4,3,6,2] [5,4,6,3,2] [5,6,4,3,2]
    |       /-----6
     \-----5
            \-----4
                   \-----3
                          \-----2
    
    [6,2,3,4,5]
     \-----6
           |                     /-----5
           |              /-----4
           |       /-----3
            \-----2
    
    [6,2,3,5,4]
     \-----6
           |              /-----5
           |             |       \-----4
           |       /-----3
            \-----2
    
    [6,2,4,5,3] [6,2,4,3,5]
     \-----6
           |              /-----5
           |       /-----4
           |      |       \-----3
            \-----2
    
    [6,2,5,3,4]
     \-----6
           |       /-----5
           |      |      |       /-----4
           |      |       \-----3
            \-----2
    
    [6,2,5,4,3]
     \-----6
           |       /-----5
           |      |       \-----4
           |      |              \-----3
            \-----2
    
    [6,3,4,2,5] [6,3,4,5,2] [6,3,2,4,5]
     \-----6
           |              /-----5
           |       /-----4
            \-----3
                   \-----2
    
    [6,3,5,4,2] [6,3,5,2,4] [6,3,2,5,4]
     \-----6
           |       /-----5
           |      |       \-----4
            \-----3
                   \-----2
    
    [6,4,2,3,5] [6,4,2,5,3] [6,4,5,2,3]
     \-----6
           |       /-----5
            \-----4
                  |       /-----3
                   \-----2
    
    [6,4,5,3,2] [6,4,3,5,2] [6,4,3,2,5]
     \-----6
           |       /-----5
            \-----4
                   \-----3
                          \-----2
    
    [6,5,2,3,4]
     \-----6
            \-----5
                  |              /-----4
                  |       /-----3
                   \-----2
    
    [6,5,2,4,3]
     \-----6
            \-----5
                  |       /-----4
                  |      |       \-----3
                   \-----2
    
    [6,5,3,4,2] [6,5,3,2,4]
     \-----6
            \-----5
                  |       /-----4
                   \-----3
                          \-----2
    
    [6,5,4,2,3]
     \-----6
            \-----5
                   \-----4
                         |       /-----3
                          \-----2
    
    [6,5,4,3,2]
     \-----6
            \-----5
                   \-----4
                          \-----3
                                 \-----2

*/
  
  }

}

