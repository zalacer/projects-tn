package ex14;

/*
  p213
  1.4.36 Space usage for pushdown stacks. Justify the entries in the table below, which
  shows typical space usage for various pushdown stack implementations. Use a static
  nested class for linked-list nodes to avoid the non-static nested class overhead.

  Space usage in pushdown stacks (various implementations)
  data structure  item type  space usage for N int values (bytes)
  linked list     int        ~ 32 N
                  Integer    ~ 64 N
  resizing array  int        between ~4 N and ~16 N
                  Integer    between ~32 N and ~56 N

  1. For the linked list IntStack implementation with int items on a 64bit platform each 
     item reqires a Node which takes 16B for object overhead, 8B for the next reference 
     and 4B for an int, which adds up to 28B, but this must be padded to a multiple of 8 
     which makes it 32. For N nodes and N ints the space usage is ~32N.
     
  2. For the pushdown stack (linked-list implementation) on p149 with Integer items on a 
     64bit platform each Integer takes 24B and a node with an object reference instead of 
     an int takes 40B (according p201). For N Nodes and N Inegers that comes to ~64B.
     
  3. For the ResizingIntArrayStack implementation with int items on a 64bit platform each 
     int takes 4B and an int array takes a constant amount + N*4B. At one point during 
     resizing upwards a new array of length 2N has been created and filled with ints while 
     the old array of size N still exists. That takes N*4B + 2N*4B = N*12B. My conclusion 
     is that the space usage ranges from ~4B to ~12B and find no justification for an 
     upper limit of ~16B.
     
  4. For the pushdown stack (resizing array implementation) on p141 with Integer items on a 
     64bit platform each Integer takes 24B and N of them take N*24B. An Integer array takes
     a constant amount + at most N*8B, since it contains object references (or nulls). As 
     described in (3) that amount may be tripled at one point during upwards resizing giving 
     an upper limit of N*32B + N*24 = N*56. Overall, the space usage is from ~32N to ~56N.

*/
  public class Ex1436SpaceUsagePushdownStacks {
    
  public static void main(String[] args) {

  }

}
