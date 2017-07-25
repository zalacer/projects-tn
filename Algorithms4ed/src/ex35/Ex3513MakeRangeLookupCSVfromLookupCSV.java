package ex35;

import java.io.FileNotFoundException;

/* p508
  3.5.13  Modify LookupCSV to make a program RangeLookupCSV that takes 
  two key values from the standard input and prints all key-value pairs 
  in the .csv file such that the key falls within the range specified.
  
  This is done in RangeLookupCSVB that uses RedBlackBSTB that's enhanced
  with rangeLookup(Key,Key) that relies on toArray(Iterator<T>) (from
  v.ArrayUtils) to convert the Iterator<Key) returned by keys(Key,Key) in 
  RangeLookupCSVB to a Key[] then uses that to iterate over all Values in 
  the array of Values from each BagQ<Value> in the Node produced for each 
  key by getNode(Key). RangeLookupCSVB is demonstrated below.
  
 */

public class Ex3513MakeRangeLookupCSVfromLookupCSV {

  public static void main(String[] args) throws FileNotFoundException {
    
    st.RangeLookupCSVB.main(new String[]{"ex3513.csv", "0", "1"});
 
/*
    verifying input from ex3513.csv:
    st = (A,[1],1,1,1),(B,[2,20],2,3,6),(C,[3,30,31],3,1,3),(D,[4,40,41,42,43],5,5,17),
             (E,[5,50,51,52,53,54],6,1,6)}
    st.size() = 5
    key  values
    A    [1]
    B    [2,20]
    C    [3,30,31]
    D    [4,40,41,42,43]
    E    [5,50,51,52,53,54]
    
    enter a pair of keys for range lookup:
    A C
    rangeLookup(A,C) = [(A,1),(B,2),(B,20),(C,3),(C,30),(C,31)]
    
    enter another pair of keys for range lookup:
    C E
    rangeLookup(C,E) = [(C,3),(C,30),(C,31),(D,4),(D,40),(D,41),(D,42),(D,43),(E,5),(E,50),
                          (E,51),(E,52),(E,53),(E,54)]
    
    enter another pair of keys for range lookup:
    B D
    rangeLookup(B,D) = [(B,2),(B,20),(C,3),(C,30),(C,31),(D,4),(D,40),(D,41),(D,42),(D,43)]

*/
  }

}

