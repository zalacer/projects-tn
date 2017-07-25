package ex32;

import st.SimpleBST;

/* p417
  3.2.12  Develop a BST implementation that omits rank() and select() 
  and does not use a count field in Node. 
  
  This is done in st.SimpleBST. Demo below.
*/

public class Ex3212BSTwithNoRankSelectOrNodeCount {
 
  public static void main(String[] args) {
    Integer[] c = {3,13, 9, 5, 8, 7,11,15, 2, 1,10, 4,12,14, 6};
    Integer[] e = {3, 4, 6,14,11,20,12,10,13, 1, 5, 2, 9, 8, 7};
    
    SimpleBST<Integer,Integer> bst = new SimpleBST<>();
    for (int i  = 0; i < c.length; i++) bst.put(c[i],e[i]);
    
    System.out.println(bst);
    // (1:1,2:13,3:3,4:2,5:14,6:7,7:20,8:11,9:6,10:5,11:12,12:9,13:4,14:8,15:10)
    
    System.out.println(bst.size()); //15
    
    System.out.println(bst.get(7)); //20
    bst.delete(15);
    System.out.println(bst.get(15)); //null
    System.out.println(bst.size()); //14
    System.out.println(bst);
    //(1:1,2:13,3:3,4:2,5:14,6:7,7:20,8:11,9:6,10:5,11:12,12:9,13:4,14:8)
    
    System.out.println(bst.getVal(9)); //6
    bst.setVal(9,19);
    System.out.println(bst.getVal(9)); //19
    System.out.println(bst);
    //(1:1,2:13,3:3,4:2,5:14,6:7,7:20,8:11,9:19,10:5,11:12,12:9,13:4,14:8)
   
    while (!bst.isEmpty()) bst.deleteMax();
    System.out.println(bst.isEmpty()); //true
    System.out.println(bst.size()); //0
    System.out.println(bst); //()

  }

}

