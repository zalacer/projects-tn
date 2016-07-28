package ex13;

import ds.Queue;
import ds.Stack;
import ds.Steque;

//  1.3.47 Catenable queues, stacks, or steques. Add an extra operation catenation that (de-
//  structively) concatenates two queues, stacks, or steques (see Exercise 1.3.32). Hint : Use
//  a circular linked list, maintaining a pointer to the last item.

// It would be useful to explain what are the requirements for catenability somewhere in your
// text (4ed), since currently this exercise contains the only reference to "catenation". 
// Web searching revealed that Robert Tarjan has done work in this area however it's applied
// to "purely functional, real-time" deques which don't seem to be necessary requirements.
// Also, a requirement for destructivity seems unnecessary and perhaps unadvantageous, although
// it could be circumstantially useful, because the same result could be done by nondestructive
// catenation follow by setting the second collection to null. Based on this I have taken a 
// pragmatic approach that does not use a circular list, since that appears to be an optimization
// strategy and not a requirement.

//  Queue catenator
//  public void cat(Queue<? extends Item> q2) {
//    if (q2 == null) throw new IllegalArgumentException("cat: q2 must be non null");
//    while (!q2.isEmpty()) enqueue(q2.dequeue());
//  }

//  Stack catenator
//  public void cat(Stack<? extends Item> s2) {
//    if (s2 == null) throw new IllegalArgumentException("cat: s2 must be non null");
//    Stack<Item> s3 = new Stack<Item>();
//    while (!s2.isEmpty()) s3.push(s2.pop());
//    while(!s3.isEmpty()) push(s3.pop());
//    s3 = null;
//  }

//  Steque catenator
//  public void cat(Steque<? extends Item> stq2) {
//    if (stq2 == null) throw new IllegalArgumentException("cat: stq2 must be non null");
//    while (!stq2.isEmpty()) enqueue(stq2.pop());
//  }


public class Ex1347CatanableQueuesStacksSteques {

  public static void main(String[] args) {
      
    // Queue catenation
    Queue<String> q1 = new Queue<String>("q11", "q12", "q13");
    System.out.println(q1); //Queue(q11,q12,q13)
    Queue<String> q2 = new Queue<String>("q21", "q22", "q23");
    System.out.println(q2); //Queue(q21,q22,q23)
    // catenate q2 to the end of q1
    q1.cat(q2); 
    System.out.println(q1); //Queue(q11,q12,q13,q21,q22,q23)
    System.out.println(q2); //Queue() 
    
    // Stack catenation
    Stack<String> s1 = new Stack<String>("s11", "s12", "s13");
    System.out.println(s1); //Stack(s13,s12,s11)
    Stack<String> s2 = new Stack<String>("s21", "s22", "s23");
    System.out.println(s2); //Stack(s23,s22,s21)
    // catenate s2 to the end of s1
    s1.cat(s2); 
    System.out.println(s1); //Stack(s23,s22,s21,s13,s12,s11)
    System.out.println(s2); //Stack() 
    
    // Steque catenation
    Steque<String> stq1 = new Steque<String>("stq11", "stq12", "stq13");
    System.out.println(stq1); //Steque(stq13,stq12,stq11)
    Steque<String> stq2 = new Steque<String>("stq21", "stq22", "stq23");
    System.out.println(stq2); //Steque(stq23,stq22,stq21)
    // catenate stq2 to the end of stq1
    stq1.cat(stq2); 
    System.out.println(stq1); //Steque(stq13,stq12,stq11,stq23,stq22,stq21)
    System.out.println(stq2); //Steque() 

  }

}
