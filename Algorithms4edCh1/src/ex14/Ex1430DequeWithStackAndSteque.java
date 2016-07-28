package ex14;

import static v.ArrayUtils.*;
import ds.DequeWithStackAndSteque;

//  p212
//  1.4.30 Deque with a stack and a steque. Implement a deque with a stack and a steque
//  (see Exercise 1.3.32) so that each deque operation takes a constant amortized number
//  of stack and steque operations.

//  I'm interpreting this to mean that each deque operation, for a reasonable notion of 
//  characteristic end-to-end usage on average, should take a constant amortized number of 
//  stack and steque operations. Note also the exercise statement doesn't say the constant
//  must be the same for each deque operation and amortizing a 1:1 ratio results in the
//  same ratio, which is a constant. From this it difficult to understand offhand how a
//  sane implementation could be done without each deque operation taking some constant
//  amortized number of stack and steque operations.

public class Ex1430DequeWithStackAndSteque {

  public static void main(String[] args) {

    DequeWithStackAndSteque<Integer> d = new DequeWithStackAndSteque<Integer>(1,2,3,4,5);
    System.out.println(d); // DequeWithStackAndSteque(1,2,3,4,5)
    d.pushLeft(0); d.pushRight(6); d.pushLeft(-1);
    System.out.println(d); //DequeWithStackAndSteque(-1,0,1,2,3,4,5,6)
    d.pushRight(7); d.pushLeft(-2); d.pushRight(8); 
    System.out.println(d); //DequeWithStackAndSteque(-2,-1,0,1,2,3,4,5,6,7,8)
    System.out.println(d.popLeft());  //-2
    System.out.println(d.popRight()); //8
    System.out.println(d.popLeft());  //-1
    System.out.println(d.popRight()); //7
    System.out.println(d.popLeft());  //0
    System.out.println(d.popRight()); //6
    System.out.println(d); //DequeWithStackAndSteque(1,2,3,4,5)
    for (Integer i : d) System.out.print(i+" "); System.out.println(); //1 2 3 4 5 
    pa(d.toArray(1)); //Integer[1,2,3,4,5]
    System.out.println();

    // Queue operation with pushRight and popLeft
    d = new DequeWithStackAndSteque<Integer>();
    for (int i = 1; i < 6; i++) d.pushRight(i);
    System.out.println(d); //DequeWithStackAndSteque(1,2,3,4,5)
    pa(d.stackToArray()); //Object[]
    pa(d.stequeToArray()); //Integer[1,2,3,4,5]
    while (!d.isEmpty()) System.out.print(d.popLeft()+" "); System.out.println(); //1 2 3 4 5 
    System.out.println(d+"\n");

    // Queue operation with pushLeft and popRight
    d = new DequeWithStackAndSteque<Integer>();
    for (int i = 1; i < 6; i++) d.pushLeft(i);
    System.out.println(d); //DequeWithStackAndSteque(5,4,3,2,1)
    pa(d.stackToArray()); //Object[]
    pa(d.stequeToArray()); //Integer[5,4,3,2,1]
    System.out.println(d.popRight()); //1
    pa(d.stackToArray()); //Integer[2,3,4,5]
    pa(d.stequeToArray()); //Integer[]
    while (!d.isEmpty()) System.out.print(d.popRight()+" "); System.out.println(); //2 3 4 5 
    System.out.println(d+"\n"); //DequeWithStackAndSteque()

    // Stack operation with pushLeft and popLeft:
    d = new DequeWithStackAndSteque<Integer>();
    for (int i = 1; i < 6; i++) d.pushLeft(i);
    System.out.println(d); //DequeWithStackAndSteque(5,4,3,2,1)
    while (!d.isEmpty()) System.out.print(d.popLeft()+" "); System.out.println(); //5 4 3 2 1 
    System.out.println(d+"\n"); //DequeWithStackAndSteque()

    // Stack operation with pushRight and popRight:
    d = new DequeWithStackAndSteque<Integer>();
    for (int i = 1; i < 6; i++) d.pushRight(i);
    System.out.println(d); //DequeWithStackAndSteque(1,2,3,4,5)
    pa(d.stackToArray()); //Object[]
    pa(d.stequeToArray()); //Integer[1,2,3,4,5]
    System.out.println(d.popRight()); //5
    pa(d.stackToArray()); //Integer[4,3,2,1]
    pa(d.stequeToArray()); //Integer[]
    while (!d.isEmpty()) System.out.print(d.popRight()+" "); System.out.println(); //4 3 2 1 
    System.out.println(d); //DequeWithStackAndSteque()

  }

}
