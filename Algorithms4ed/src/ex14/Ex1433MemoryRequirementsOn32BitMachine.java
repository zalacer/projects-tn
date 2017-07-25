package ex14;

//  p212
//  1.4.33 Memory requirements on a 32-bit machine. Give the memory requirements
//  for  Integer ,  Date ,  Counter ,  int[] ,  double[] ,  double[][] ,  String ,
//  Node , and  Stack (linked-list representation) for a 32-bit machine. Assume that 
//  references are 4 bytes, object overhead is 8 bytes, and padding is to a multiple
//  of 4 bytes.

// This differs from text regarding references where its 8 bytes, object overhead where 
// its 16 bytes and padding where it's to a multiple of 8 bytes (p201).

// a. Integer: 4 for one int + 8 overhead = 12 (vs 24 on 64bit)
// b. Date: 8 overhead + 4*3 for 3 ints  = 20 (vs 32 for 64bit
// c. Counter:  8 overhead + 4 string reference + 4 int = 16 (vs 32 for 64bit)
// d. int[]: 8 overhead + 4 length + length*4 = 12 + length*4 (vs 24+length*4 for 64bit)
// e. double[]: 8 overhead + 4 length + length*8 = 12 * length*8
// f. double[][]: 8 overhead + 4 length + length*12 + totalLengthOfAllRows*8
// g. Node: 8 overhead + 2*4 references  = 16 (vs 40 for 64bit)
// h. Stack: 8 overhead + 16*#nodes + ItemSize*#nodes + 4 int instance variable
//      empty stack (with first = null) requires 12

public class Ex1433MemoryRequirementsOn32BitMachine {

  public static void main(String[] args) {

  }

}
