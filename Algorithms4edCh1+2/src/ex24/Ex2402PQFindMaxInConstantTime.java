package ex24;

/* p329
  2.4.2  Criticize the following idea: To implement find the maximum in 
  constant time, why not use a stack or a queue, but keep track of the 
  maximum value inserted so far, then return that value for find the maximum?
  
  The idea is incomplete. It would work for finding the max the first time
  but not the second time without additional provisions. There is also the
  matter of removing the max which would be cumbersome if it isn't at the
  top of the stack or beginning of the queue. It's best to keep all the data
  in a structure that's efficient for always making the max key retrievable
  and removable.
  
*/
public class Ex2402PQFindMaxInConstantTime {
  
  public static void main(String[] args) {

  }

}
