package ex35;

import static v.ArrayUtils.*;

import st.BSTM;

/* p507
  3.5.9  Modify  BST to keep duplicate keys in the tree. Return any value 
  associated with the given key for get(), and remove all nodes in the tree 
  that have keys equal to the given key for delete() .

  Since any value for a given key can be returned by get(), I interpret the
  exercise statement to be satisfiable by retaining one value for each key,
  and storing a count of the number of times a key has been put(). In other 
  words duplicate keys won't be stored literally but represented by a count. 
  Since it isn't specified what value to return for a key when it has multiple 
  values, the last value assigned to each key will be returned by get(). It
  would be simple to retain all values for each key and have delete remove
  them one at a time in order of insertion or not, but none of that was
  specified.

  Maybe this misses the point, but I'm uninterested in storing multiple equal
  keys for no stated purpose and don't appreciate that there's any point, i.e.
  any reason to do it instead of using the approach outlined above. Plus 
  duplicate keys can screw up tree structures, not especially for ordinary BSTs 
  but certainly for those supporting rotations that in fact BSTM does, since it's 
  an extension of BSTX.

  The class is implemented as st.BSTM. Since it already had an inner node
  class, it was sufficient to add an int field c and an int field dSize to
  it to track the number of instances of a given key. dSize (size with
  duplicates) parallels size in put and delete except x.c is added instead 
  of 1 (where x is the current node) and a mess of dSize methods were added 
  including dSize(), dSize(Key), dSize(Node), dSize(Key,Key), dSizeI(Key), 
  dSizeI(Node), dSizeR(Key) and dSizeR(Node), where the dSizeI's are iterative 
  methods to calculate dSize using Node.c instead of Node.dSize and the 
  dSizeR's are recursive methods to do the same thing. Then the rank and 
  select methods required modification, however since they continued to work 
  well for unique keys, a set of parallel methods accomodating duplicate keys 
  was developed. They are similarly named except prefixed with "d" and are 
  dRank(Key), dRank(Key, Node), dSelect(int) and dSelect(Node, int) and they 
  preserve the meaning of rank as the number of keys less than a given key. 
  This means when a node has duplicate keys, the rank between it and then next 
  greater unique key jumps by more than 1, and that causes isRankConsistent() 
  to fail so isDrankConsistent() was developed along with isDsizeConsistent() 
  to verify dSizes and both were inluded in check(). 
  
  The overall advantage of this approach is that it preserves existing func-
  tionality while adding new functionality for little overhead. If it's 
  desired to dissallow duplicates set the dups field to false and this is 
  supported by a constructor or can be done with setDups() while the status
  of dups can be obtained with getDups().

  A few basic methods of BSTM are demonstrated below.

 */

public class Ex3509ModifyBST2KeepDuplicateKeys {

  public static void main(String[] args) {

    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0,sa.length);
    
    BSTM<Character, Integer> h1 = new BSTM<>(ca,ia);
    
    System.out.println("h1"); h1.printTree();
    System.out.println("\n(Nodes are printed in the format (key,val,c,size,dSize))\n");
    System.out.print("h1 = "+h1.tos()+"\n");
    System.out.println("check() = "+h1.check());
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("put('E', 11)"); h1.put('E', 11); 
    System.out.print("h1 = "+h1.tos()+"\n"); 
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("h.delete('E')"); h1.delete('E');
    System.out.print("h1 = "+h1.tos()+"\n"); 
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("h1"); h1.printTree();
    System.out.println("check() = "+h1.check());


/*
    h1
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |             |       /-----O
    |      |             |      |       \-----N
    |      |              \-----I
     \-----E
            \-----A
    
    (Nodes are printed in the format (key,val,c,size,dSize))
    
    h1 = (A,1,1,1,1),(E,0,1,10,10),(I,7,1,3,3),(N,9,1,1,1),(O,8,1,2,2),(Q,4,1,4,4),(S,2,1,8,8),(T,6,1,1,1),(U,5,1,2,2),(Y,3,1,3,3)}
    check() = true
    get('E') = 0
    count of 'E' = 1
    put('E', 11)
    h1 = (A,1,1,1,1),(E,11,2,10,11),(I,7,1,3,3),(N,9,1,1,1),(O,8,1,2,2),(Q,4,1,4,4),(S,2,1,8,8),(T,6,1,1,1),(U,5,1,2,2),(Y,3,1,3,3)}
    get('E') = 11
    count of 'E' = 2
    h.delete('E')
    h1 = (A,1,1,1,1),(I,7,1,9,9),(N,9,1,1,1),(O,8,1,2,2),(Q,4,1,3,3),(S,2,1,7,7),(T,6,1,1,1),(U,5,1,2,2),(Y,3,1,3,3)}
    get('E') = null
    count of 'E' = 0
    h1
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |              \-----O
    |      |                     \-----N
     \-----I
            \-----A
    check() = true

*/

  }

}

