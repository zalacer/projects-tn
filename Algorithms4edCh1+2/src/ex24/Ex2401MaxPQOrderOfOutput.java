package ex24;

import pq.MaxPQ;

/* p329
  2.4.1  Suppose that the sequence P R I O * R * * I * T * Y * * * Q U E * * *
  U * E (where a letter means insert and an asterisk means remove the maximum) 
  is applied to an initially empty priority queue. Give the sequence of letters 
  returned by the remove the maximum operations.
  
  R R P O T Y I I U Q E
  
*/
public class Ex2401MaxPQOrderOfOutput {
  
  public static void main(String[] args) {

    MaxPQ<String> pq = new  MaxPQ<String>("P","R","I","O");
    System.out.print(pq.delMax()+" "); pq.insert("R");
    System.out.print(pq.delMax()+" "); System.out.print(pq.delMax()+" ");
    pq.insert("I"); System.out.print(pq.delMax()+" ");
    pq.insert("T"); System.out.print(pq.delMax()+" ");
    pq.insert("Y"); System.out.print(pq.delMax()+" ");
    System.out.print(pq.delMax()+" "); System.out.print(pq.delMax()+" ");
    pq.bulkInsert("Q","U","E"); System.out.print(pq.delMax()+" ");
    System.out.print(pq.delMax()+" "); System.out.println(pq.delMax());
    System.out.println(pq.isEmpty());
  }

}
