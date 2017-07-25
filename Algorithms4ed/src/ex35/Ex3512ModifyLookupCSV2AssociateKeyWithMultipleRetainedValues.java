package ex35;

import java.io.FileNotFoundException;

/* p508
  3.5.12  Modify LookupCSV to associate with each key all values that
  appear in a key value pair with that key in the input (not just the 
  most recent, as in the associative-array abstraction).
  
  This was implemented using st.RedBlackSTB as the ST in st.LookUPCSVB
  where the former is based on st.RedBlackSTM that was developed for 
  ex 3.5.10 (see ex35.Ex3510ModifyRedBlackBST2KeepDuplicateKeys), but with
  Node.val replaced with Node.b == BagQ<Value> to store all values in-
  cluding possible duplicates. ds.BagQ is an enhanced version of 
  http://algs4.cs.princeton.edu/13stacks/Bag.java with add(Item) implemented
  identically to Queue.enqueue(Item) plusA B C toArray() and show() methods.
  LookUPCSVB is demonstrated below.
  
 */

public class Ex3512ModifyLookupCSV2AssociateKeyWithMultipleRetainedValues {

  public static void main(String[] args) throws FileNotFoundException {
    
    st.LookupCSVB.main(new String[]{"ex3512.csv", "0", "1"});
 
/*
    verifying input from ex3512.csv:
    st = (A,[1],1,1,1),(B,[2,20],2,3,6),(C,[3,30,31],3,1,3),(D,[4,40,41,42,43],5,5,17),
              (E,[5,50,51,52,53,54],6,1,6)}
    st.size() = 5
    key  values
    A    [1]
    B    [2,20]
    C    [3,30,31]
    D    [4,40,41,42,43]
    E    [5,50,51,52,53,54]
    
    scanning System.in for keys:
    A B C D E
    input key = A:  values = [1]
    input key = B:  values = [2,20]
    input key = C:  values = [3,30,31]
    input key = D:  values = [4,40,41,42,43]
    input key = E:  values = [5,50,51,52,53,54]
    
    contents of ex3512.csv:
    A,1
    B,2
    C,3
    D,4
    E,5
    B,20
    C,30
    C,31
    D,40
    D,41
    D,42
    D,43
    E,50
    E,51
    E,52
    E,53
    E,54

*/
  }

}

