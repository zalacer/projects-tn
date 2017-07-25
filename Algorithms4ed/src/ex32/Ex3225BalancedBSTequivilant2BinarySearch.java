package ex32;

import static v.ArrayUtils.*;

import st.BSTX;

/* p419
  3.2.25 Perfect balance. Write a program that inserts a set of keys into 
  an initially empty BST such that the tree produced is equivalent to binary 
  search, in the sense that the sequence of compares done in the search for 
  any key in the BST is the same as the sequence of compares used by binary 
  search for the same set of keys.
  
  This is done the st.BSTX constructor with signature 
    
    public BSTX(Key[] ka, Value[] va, boolean balanced)
    
  When balanced == true it does the following:
  
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,kz.length);
    for (int i = 0; i < kz.length; i++) ta[i] = new Tuple2<Key,Value>(kz[i],vz[i]);
    Comparator<Tuple2<Key,Value>> comp = (t1,t2) -> {return t1._1.compareTo(t2._1);};
    Arrays.sort(ta,comp);
    for (int i = 0; i < kz.length; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
    int start = 0, end = kz.length-1;
    arrayToBalancedBST(kz, vz, start, end); 
    
  where
    
    kz and ka are derived from ka and va excluding nulls.
    
  and arrayToBalancedBST() is defined as:
  
    void arrayToBalancedBST(Key[] ka, Value[] va, int start, int end) {
      // assumes ka is sorted
      if (start > end) return;
      int mid = (start + end) / 2;
      put(ka[mid], va[mid]);
      arrayToBalancedBST(ka, va, start, mid-1);
      arrayToBalancedBST(ka, va, mid+1, end);
    }
    
    This constructor produces a BST where the number of compares used by get 
    is the same as binary search indexOf() as given below and is demonstrated
    below.
    
 */             

public class Ex3225BalancedBSTequivilant2BinarySearch {
  
  public static int compares;
  
  public static int indexOf(Integer[] a, Integer key) {
    // modified from edu.princeton.cs.algs4.BinarySearch
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      int c = key.compareTo(a[mid]); compares++;
      if      (c < 0) hi = mid - 1;
      else if (c > 0) lo = mid + 1;
      else return mid;
    }
    return -1;
  }

  public static void main(String[] args) {
    
    Integer[] u = rangeInteger(1,32);
//    Integer[] u = rangeInteger(1,16);
    Integer[] v = rangeInteger(0,u.length);
    BSTX<Integer, Integer> bst = new BSTX<>(u,v,true);
    System.out.println("ipl="+bst.ipl());
    System.out.println("epl="+bst.epl());
    System.out.println("bst"); bst.printTree();
/*      
    bst
    |                     /-----15
    |              /-----14
    |             |       \-----13
    |       /-----12
    |      |      |       /-----11
    |      |       \-----10
    |      |              \-----9
     \-----8
           |              /-----7
           |       /-----6
           |      |       \-----5
            \-----4
                  |       /-----3
                   \-----2
                          \-----1
*/
    
    int c = 1, bstCompares, bsCompares;

    while (c < u.length+1) {
      bst.setCompares(0);
      bst.get(c);
      bstCompares = (int) bst.getCompares();
      compares = 0;
      indexOf(u, c);
      bsCompares = compares;
      assert bstCompares == bsCompares;
      c++;
    }
    
  }

}

