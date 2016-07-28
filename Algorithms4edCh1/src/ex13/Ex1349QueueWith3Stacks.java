package ex13;

//  p171
//  1.3.49 Queue with three stacks. Implement a queue with three stacks so that each
//  queue operation takes a constant (worst-case) number of stack operations. Warning :
//  high degree of difficulty.

// According to 
//  https://stackoverflow.com/questions/5538192/how-to-implement-a-queue-with-three-stacks
// this question was withdrawn because Prof. Sedgewick knows of no solutions that don't use
// lazy evaluation, which in practice creates extra intermediate data structures. It was 
// replaced with:
//   Queue with a constant number of stacks. Implement a queue with a constant number of
//   stacks so that each queue operation takes a constant (worst-case) number of stack 
//   operations. Warning: Very high degree of difficulty. 
//   (Question 32 on http://algs4.cs.princeton.edu/13stacks/).

// Regarding the latter exercise , I considered the queue with six lists by Robert Hood and
// Robert Melville described in Real Time Queue Operations Implemented in Pure Lisp available 
// at https://ecommons.cornell.edu/bitstream/handle/1813/6273/80-433.pdf?sequence=1&isAllowed=y.
// its update mode, however, requires copying the entire front list in order to reverse it 
// during one list operation making it fail to take a constant number of list operations. Yet by
// maintaining two identical copies of the front list there is no need for copying it all at once
// and all queue operations can be implemented with a constant number of list operations.
//
// In QueueWithSevenStacks I translated the description of a queue with six lists to java 
// by replacing lists with stacks and using a total of seven stacks with the result that all
// necessary queue operations are implemented to execute a constant worst case number of stack 
// operations independent of the length of the queue.
//
// QueueWithSevenStacks is at analysis.QueueWithSevenStacks.java which includes additional
// documentation about it.


public class Ex1349QueueWith3Stacks {

  public static void main(String[] args) {
 
  }

}
