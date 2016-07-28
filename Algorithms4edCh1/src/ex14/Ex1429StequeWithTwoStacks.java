package ex14;

import static v.ArrayUtils.*;
import ds.StequeWithTwoStacks;

//  p212
//  1.4.29 Steque with two stacks. Implement a steque with two stacks so that each steque
//  operation (see Exercise 1.3.32) takes a constant amortized number of stack operations.

public class Ex1429StequeWithTwoStacks {

  public static void main(String[] args) {
    
    StequeWithTwoStacks<Integer> s = new StequeWithTwoStacks<Integer>(1,2,3,4,5);
    System.out.println(s); // StequeWithTwoStacks(5,4,3,2,1)
    s.push(6); s.push(7); s.push(8);
    System.out.println(s); // StequeWithTwoStacks(8,7,6,5,4,3,2,1)
    System.out.println(s.peek()); //8
    // pook returns the bottom item without removing it
    System.out.println(s.pook()); //1
    s.enqueue(0);
    System.out.println(s); // StequeWithTwoStacks(8,7,6,5,4,3,2,1,0)
    System.out.println(s.pop()); //8
    System.out.println(s.pop()); //7
    System.out.println(s); //StequeWithTwoStacks(6,5,4,3,2,1,0)
    s.enqueue(-1);  s.enqueue(-2);
    System.out.println(s.peek()); //6
    System.out.println(s.pook()); //-2
    System.out.println(s); // StequeWithTwoStacks(6,5,4,3,2,1,0,-1,-2)
    System.out.println(s.pop()); //6
    System.out.println(s.pop()); //5
    System.out.println(s.pop()); //4
    System.out.println(s); // StequeWithTwoStacks(3,2,1,0,-1,-2)
    s.enqueue(-3);
    for (Integer i : s) System.out.print(i+" "); System.out.println(); // 3 2 1 0 -1 -2 -3
    pa(s.toArray(1)); // Integer[3,2,1,0,-1,-2,-3]
  
  }

}
