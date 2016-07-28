package ex11;

//  1.1.25  Use mathematical induction to prove that Euclid’s algorithm computes the
//  greatest common divisor of any pair of nonnegative integers p and q.

// First note that Euclid's algorithm for computing the gcd (greatest common divisor) is 
// usually defined for positive integers, not nonnegative integers. Second note that even
// when it's defined for nonnegative integers the result for 0 and 0 is usually not 
// defined, or not supported or gives an error. In contrast to this, the implementation
// of what is called "Euclid's algorithm" in the text on page 4 returns 0 as the gcd of 0
// and 0.

// Proof of an assertion by mathematical induction over the natural numbers requires (1) a 
// base where the assertion is shown to be true for the first number and (2) an inductive
// step where it's shown that if it's true for any number then its true for the next. The
// natural numbers may or may not include 0. In the following 0 is included in them, 0 is
// the first such number and the version of Euclid's algorithm given in the text on page 4,
// which will be simply called "the algorithm", will be proved true by mathematical
// induction over the nonnegative integers, which is just another name for the natural
// numbers including 0, assuming the greatest common divisor of 0 and 0 is 0.

// 0. Assertion: Euclid’s algorithm as described on page 4 of the text computes the
//    greatest common divisor of any pair of nonnegative integers p and q, assuming the
//    greatest common divisor of 0 and 0 is 0.

// 1. Base case:  The algorithm computes the gcd for all combinations of nonnegative 
//    integers smaller than or equal to 0 since it returns 0 as the gcd of 0 and 0, which
//    is correct by assumption. 
//    (If that's not satisfactory, then, assuming the gcd of 0 and 0 is undefined and the
//    implementation of the algorithm is modified with a statement excluding that
//    combination such as "assert !(p==0 && q==0);", it's true for all nonnegative 
//    integers <= 1 where it's defined, since by inspection the gcd of 1 and 0 is 1, the gcd
//    of 0 and 1 is 1 and the gcd of 1 and 1 is 1. Note that when computing the gcd of 1 and 
//    0 the algorithm returns 1 in its first step and when computing the gcd of 0 and 1 and
//    that of 1 and 1 the algorithm returns the gcd of 1 and 0 in its third step.)
//
// 2. Inductive step: Suppose the algorithm computes the gcd for all pairs of nonnegative
//    integers up to and including z. For any nonnegative integer x, the algorithm computes
//    the gcd of x and x as x, since when doing that it returns the gcd of x and 0 in its
//    third step which results in x in its first step. Therefore the algorithm computes
//    the gcd of z+1 and z+1 and the gcd of z+1 and 0. Consider the computation of the gcd
//    of z+1 with z. In that case, z+1 % z is 1 since z+1-z = 1 and the result returned is
//    the gcd of z and 1, which it computes by the supposition. Similarly for the 
//    computation of the gcd of z+1 with each x: z+1 > x > 0. For computations of the gcd
//    for each such x with z+1, the algorithm computes the same results, since when the
//    first argument is less than the second it returns the gcd with the arguments reversed.
//    Finally the algorithm computes the gcd of 0 and z+1, since when doing that it returns
//    the gcd of z+1 and z+1 which it can compute as has already been shown. Therefore the
//    algorithm computes the gcd for all pairs of nonnegative integers up to and including
//    z+1.
//
// 3. Q.E.D.: Since the assertion (0) has a basis (1) and an inductive step (2), it's 
//    proven true by mathematical induction.

public class Ex1125EuclidGcdProof {
  
  public static int gcd(int p, int q) {
    // Implementation of Euclid's algorithm from p4
     assert !(p==0 && q==0);
    if (q == 0) return p;
    int r = p % q;
    return gcd(q, r);
  }
  
  
  public static void main(String[] args) {
    
//    Math.floorMod(0, 0); //ArithmeticException: / by zero
//    System.out.println(0 % 0); //ArithmeticException: / by zero
    System.out.println(1 % 1);
    System.out.println(gcd(0,0)); //0
  
  }

}
