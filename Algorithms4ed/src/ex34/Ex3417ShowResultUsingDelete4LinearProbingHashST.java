package ex34;

import static v.ArrayUtils.*;

import st.LinearProbingHashSTX;

/* p482
  3.4.17  Show the result of using the  delete() method on page 471 to 
  delete C from the table resulting from using LinearProbingHashST with 
  our standard indexing client (shown on page 469).

  delete() from p469 is already in LinearProbingHashSTX.

*/             

public class Ex3417ShowResultUsingDelete4LinearProbingHashST {

  public static void main(String[] args) {

    Character[] a = (Character[]) box("SEARCHEXAMPLE".toCharArray());
    Integer[] b = rangeInteger(0,a.length);
    LinearProbingHashSTX<Character,Integer> h = new LinearProbingHashSTX<>(a,b);
    System.out.println(h.keysToString());
    // [null,null,P,null,R,S,null,null,null,null,X,null,null,A,null,C,null,E,
    //  null,null,H,null,null,null,L,M]
    h.delete('C');
    System.out.println(h.keysToString());
    // [null,null,P,null,R,S,null,null,null,null,X,null,null,A,null,null,null,E,
    //  null,null,H,null,null,null,L,M]

  }

}

