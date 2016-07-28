package ex14;

import static v.ArrayUtils.*;
import ds.DequeWithThreeStacks;

//  p212
//  1.4.31 Deque with three stacks. Implement a deque with three stacks so that each
//  deque operation takes a constant amortized number of stack operations.

// I found no need for a third stack and used it only for rebalancing the allocation
// of items in the other two during popLeft and popRight when one of those stacks
// is empty.  Balancing also occurs during initialization from an array and could
// be extended into push operations, but there has not been any demonstrated need
// or requirement for it.

public class Ex1431DequeWithThreeStacks {

  @SuppressWarnings("unused")
  public static void main(String[] args) {

    // demo of pushLeft, popLeft, pushRight and popRight showing stack internals
    DequeWithThreeStacks<Integer> d = new DequeWithThreeStacks<Integer>(1,2,3,4,5,6);
    System.out.println(d); // DequeWithThreeStacks(1,2,3,4,5,6)
    pa(d.toArray(1)); //Integer[1,2,3,4,5,6]
    pa(d.stack1ToArray(1)); //Integer[1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[6,5,4]
    d.pushLeft(0);  d.pushLeft(-1); 
    System.out.println(d); //DequeWithThreeStacks(-1,0,1,2,3,4,5,6)
    pa(d.stack1ToArray(1)); //Integer[-1,0,1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[6,5,4]
    System.out.println(d.popLeft()); //-1
    System.out.println(d.popLeft()); //0
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6)
    pa(d.stack1ToArray(1)); //Integer[1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[6,5,4]
    d.pushRight(7); d.pushRight(8);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6,7,8)
    pa(d.stack1ToArray(1)); //Integer[1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[8,7,6,5,4]
    System.out.println(d.popRight()); //8
    System.out.println(d.popRight()); //7
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6)
    pa(d.stack1ToArray(1)); //Integer[1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[6,5,4]
    System.out.println(d.popLeft()); //1
    System.out.println(d.popLeft()); //2
    System.out.println(d.popLeft()); //3
    System.out.println(d.popLeft()); //4
    System.out.println(d); //DequeWithThreeStacks(5,6)
    pa(d.stack1ToArray(1)); //Integer[5,6]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[]
    System.out.println(d.popRight()); //6
    System.out.println(d); //DequeWithThreeStacks(5)
    pa(d.stack1ToArray(1)); //Integer[5] // not worthwile to flip this until needed
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[]
    System.out.println();

    // demo of rebalancing built into popLeft
    d = new DequeWithThreeStacks<Integer>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)
    // balancing built into the constructor with array arg
    pa(d.stack1ToArray(1)); //Integer[1,2,3,4,5,6,7,8]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[16,15,14,13,12,11,10,9]
    for (int i : range(0,9)) System.out.print(d.popLeft()+" "); //1 2 3 4 5 6 7 8 9 
    System.out.println();
    System.out.println(d); //DequeWithThreeStacks(10,11,12,13,14,15,16)
    pa(d.stack1ToArray(1)); //Integer[10,11,12,13]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[16,15,14]
    System.out.println();

    // demo of rebalancing built into popRight
    d = new DequeWithThreeStacks<Integer>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)
    // balancing built into the constructor with array arg
    pa(d.stack1ToArray(1)); //Integer[1,2,3,4,5,6,7,8]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[16,15,14,13,12,11,10,9]
    for (int i : range(0,9)) System.out.print(d.popRight()+" "); //16 15 14 13 12 11 10 9 8 
    System.out.println();
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5,6,7)
    pa(d.stack1ToArray(1)); //Integer[1,2,3]
    pa(d.stack2ToArray(1)); //Integer[]
    pa(d.stack3ToArray(1)); //Integer[7,6,5,4]
    System.out.println();

    d = new DequeWithThreeStacks<Integer>(1,2,3,4,5);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5)
    d.pushLeft(0); d.pushRight(6); d.pushLeft(-1);
    System.out.println(d); //DequeWithThreeStacks(-1,0,1,2,3,4,5,6)
    d.pushRight(7); d.pushLeft(-2); d.pushRight(8); 
    System.out.println(d); //DequeWithThreeStacks(-2,-1,0,1,2,3,4,5,6,7,8)
    System.out.println(d.popLeft());  //-2
    System.out.println(d.popRight()); //8
    System.out.println(d.popLeft());  //-1
    System.out.println(d.popRight()); //7
    System.out.println(d.popLeft());  //0
    System.out.println(d.popRight()); //6
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5)
    for (Integer i : d) System.out.print(i+" "); System.out.println(); //1 2 3 4 5 
    pa(d.toArray(1)); //Integer[1,2,3,4,5]
    System.out.println();

    // Queue operation with pushRight and popLeft
    d = new DequeWithThreeStacks<Integer>();
    for (int i = 1; i < 6; i++) d.pushRight(i);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5)
    while (!d.isEmpty()) System.out.print(d.popLeft()+" "); //1 2 3 4 5 
    System.out.println("\n"+d+"\n"); ////DequeWithThreeStacks()

    // Queue operation with pushLeft and popRight
    d = new DequeWithThreeStacks<Integer>();
    for (int i = 1; i < 6; i++) d.pushLeft(i);
    System.out.println(d); //DequeWithThreeStacks(5,4,3,2,1)
    while (!d.isEmpty()) System.out.print(d.popRight()+" ");  //1 2 3 4 5 
    System.out.println("\n"+d+"\n"); //DequeWithThreeStacks()

    // Stack operation with pushLeft and popLeft:
    d = new DequeWithThreeStacks<Integer>();
    for (int i = 1; i < 6; i++) d.pushLeft(i);
    System.out.println(d); //DequeWithThreeStacks(5,4,3,2,1)
    while (!d.isEmpty()) System.out.print(d.popLeft()+" "); //5 4 3 2 1 
    System.out.println("\n"+d+"\n"); //DequeWithThreeStacks()

    // Stack operation with pushRight and popRight:
    d = new DequeWithThreeStacks<Integer>();
    for (int i = 1; i < 6; i++) d.pushRight(i);
    System.out.println(d); //DequeWithThreeStacks(1,2,3,4,5)
    while (!d.isEmpty()) System.out.print(d.popRight()+" ");//5 4 3 2 1 
    System.out.println("\n"+d+"\n"); //DequeWithThreeStacks()

  }

}
