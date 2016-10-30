package ex14;


//  p212
//  1.4.32 Amortized analysis. Prove that, starting from an empty stack, the number of ar-
//  ray accesses used by any sequence of M operations in the resizing array implementation
//  of  Stack is proportional to M.

// The implementation of ResizingArrayStack (ds.ResizingArrayStack.java) consists of an
// array which is accessed by push and pop operations. Int N is the number of items in the
// stack and is the index of the next item to be added to it with push and and one less
// than the index of the next item to be removed from it with pop. 

// When N reaches the end of the array, push doubles its length which requires creation 
// of a new empty array and copying N items from the old one into it prior to inserting a
// new item into it at position N. 

// When N is nonzero and smaller than the length of the array divided by four, pop 
// decreases the length of the array by a factor of two, which requires creation of a new
// empty array and copying the N items items from the old one into it. If N is zero before
// execution of pop. it's execution throws a run time exception.
//
// Using the potential method of amortization, after the ith operation, let: 
// n(i) be the number of elements in the stack
// size(i) = n(i) > 0 ? length of the array : 0 be the size of the array
// l(i) = n(i) > 0 ? n(i)/size(i) : 1 be the load factor of the array
// P(i) = l(i) >= 1/2 ? 2n(i) - size(i) : size(i)/2 - n(i) be the potential function
//
// Note that P(i) is never less than zero, equals zero immediately after the array has 
// been resized up or down and increases as l(i) increases to 1 or decreases to 1/4. 
// Therefore total amortized cost based on it is an upper bound of the total actual cost.
//
// additionally let:
// a(i) = actual cost of the ith operation in terms of array accesses
// u(i) = amortized cost of the ith operation in terms of array accesses
//
// and given:
// the actual cost of push without array expansion = 1
// the actual cost of push with array expansion = n(i) + 1
// the actual cost of pop without array contraction = 1
// the actual cost of pop with array contraction = n(i) + 1
// the general relation between P, a and u is: u(i) = a(i) + P(i) - P(i-1)
//
// case 1: the ith operation is push (array contraction is impossible)
//   if l(i-1) >= 1/2: 
//     if ith operation does not cause array expansion: 
//       size(i) == size(i-1), 2n(i-1) = 2n(i)-1
//       u(i) = a(i) + P(i) - P(i-1)
//       u(i) = 1 + 2n(i) - size(i) - 2n(i) + 2 + size(i) = 3
//     if ith operation causes array expansion: 
//       size(i-1) = n(i-1) = n(i)-1; size(i) = 2size(i-1) = 2(n(i)-1); 2n(i-1) = 2n(i)-1
//       u(i) = a(i) + P(i) - P(i-1)
//       u(i) = n(i) + 1 + 2n(i) - size(i) - 2n(i-1) + size(i-1)
//       u(i) = 3n(i) - 2(n(i)-1) - 2n(i) + 2 + n(i) = 4
//   if l(i-1) < 1/2: (array expansion is impossible)
//     if (l(i) < 1/2)):
//       size(i) == size(i-1), n(i-1) = n(i)-1
//       u(i) = a(i) + P(i) - P(i-1)
//       u(i) = 1 + size(i)/2 - n(i) - size(i)/2 + n(i)-1 = 0
//     if (l(i) >= 1/2)):
//       size(i) == size(i-1)
//       u(i) = a(i) + P(i) - P(i-1)
//       u(i) = 1 + 2n(i) - size(i) - size(i-1)/2 + n(i-1)
//       u(i) = 1 + 2(n(i-1)+1) - size(i-1) - size(i-1)/2 + n(i-1)
//       u(i) = 3n(i-1) - 3size(i-1)/2 + 3
//       u(i) = 3l(i-1)size(i-1) - 3size(i-1)/2 + 3
//       u(i) < 3size(i-1)/2 - 3size(i-1)/2 + 3 = 3;
//
// case 2: the ith operation is pop (array expansion is impossible)
//     if l(i-1) < 1/2:
//       if ith operation does not cause array contraction:
//         n(i) = n(i-1)-1; size(i) = size(i-1)
//         u(i) = a(i) + P(i) - P(i-1) 
//         u(i) = 1 + size(i)/2 - n(i) - size(i-1)/2 + n(i-1) = 2
//     if ith operation causes array contraction:
//         n(i-1) = n(i)+1 = size(i-1)/4; size(i) = size(i-1)/2
//         u(i) = a(i) + P(i) - P(i-1)
//         u(i) = n(i) + 1 + size(i)/2 - n(i) - size(i-1)/2 + n(i-1)
//         u(i) = 1 - n(i) - 1 + n(i) + 1 = 1; 
//     if l(i-1) >= 1/2: (array contraction is impossible)
//         n(i) = n(i-1)-1; size(i) = size(i-1)
//         u(i) = a(i) + P(i) - P(i-1)
//         1 + size(i)/2 - n(i) - size(i-1)/2 + n(i-1) = 2
//
// In conclusion, since the amortized cost for all resizing array stack operations in 
// all cases is bounded above by a constant, the actual time for any sequence of M
// operations is proportional to M




import static java.lang.Math.pow;
import static analysis.Log.*;

public class Ex1432AmortizedAnalysisResizingArrayStack {
  
  public static boolean isExactPowerOf2(int x) {
    // return true if x is an exact power of 2 else return false
    return (x & (x-1)) == 0;
  }
  
  public static int costOfNPushes(int n) {
    int log2 = (int) log2(n);
    double sum = 0;
    for (int i = 0; i < log2; i++) sum+=pow(2,i);
    return (int) sum;
  }

  public static void main(String[] args) {

  }

}
