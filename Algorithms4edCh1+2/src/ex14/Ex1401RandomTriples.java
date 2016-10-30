package ex14;

//  1.4.1  Show that the number of different triples that can be chosen from N items is
//  precisely N(N-1)(N-2)/6. Hint : Use mathematical induction.

// I am substituting combination for triple in the proof below since it usually implies
// disregard of order of components* than the latter, especially in programming contexts 
// in which triple can be a synonym for a type of tuple which is a finite ordered list 
// of elements**.
// e.g. v.Tuple3(1,2,3).equals(v.Tuple3(3,2,1)) == false;
// * according to  https://en.wikipedia.org/wiki/Combination#Number_of_k-combinations_for_all_k
// ** https://en.wikipedia.org/wiki/Tuple

// The number of ways of selecting an item from N >= 3 items is N, and given that an 
// item has already been selected the number of ways selecting another distinct item is
// N-1, and given that two distinct items have already been selected the number of ways
// of selecting a third distinct item is N-2, so the number of ways of selecting three 
// distinct items is N*(N-1)(N-2). The order of items in a combination doesn't matter, so
// the number of combinations of three distinct items that can be selected is the number
// of ways of selecting three distinct items divided by the number of ways of ordering
// them, that is, the number of their permutations. The number of permutations of three 
// distinct items can be determined by the same process: since there are three items the 
// number of ways of selecting the first is three, for each first item two unselected 
// items remain so there are two ways of selecting one of them and for each second item 
// selected there is one item remaining and only one way of selecting it. This means there 
// are 3*2*1 = 6 ways of ordering three items and therefore N*(N-1)*(N-2)/6 ways of 
// selecting combinations of three items from N. This formula can be rewritten as
// N!/3!*(N-3)! and, since the choice of number of items to select is arbitrary up to N it
// generalizes easily and directly to N!/K!*(N-K)! as the number of combinations of K >= 0 
// items from N >= K items.

public class Ex1401RandomTriples {

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
