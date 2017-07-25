package ex15;

import static java.lang.Math.*;
import static java.math.BigInteger.*;
import static v.ArrayUtils.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/*
  p.237
  1.5.15 Binomial trees. Show that the number of nodes at each level in the worst-case
  trees for weighted quick-union are binomial coefficients. Compute the average depth of
  a node in a worst-case tree with N = 2**n nodes.
  
  That the number of nodes at each level are binomial coefficients follows from the
  definition of worst-case for weighted quick-union given on p227 and the mechanics of
  Pascal's triangle. The picture of weighted quick-union worst case tree formation 
  on p229 helps too.
  
  First, the definition of worst-case for weighted quick-union is "when the sizes of the 
  trees to be merged by union() are always equal (and a power of 2)". An implication of
  this is that for the worst-case to play out all the way, the total number of nodes must
  be a power of 2 and the final result of merging is a single tree of height n and 
  average node depth (1.*n)/2 given 2**n nodes.
   
  Inductive proof that for weighted quick-union the number of nodes at each level in each
  tree in the worst-case are binomial coefficients:
  1. When the number of nodes is 2**0 = 1 the number of nodes at level 0 is 1, which is the
     first binomial coefficient. I'm calling it level 0 to correspond to power of 2 for
     the total number of nodes and to the depth of a node by itself.
  2. Suppose there are 2**k nodes with multiple trees built from them in the worst-case way 
      and the number of nodes at each level in each tree are binomial coefficients. Merging        two of these trees in the worst-case requires that they have the same size which is a 
      power of two, say 2**i for some i < k. Such worst-case merging produces a tree with 
      binomial coefficients, because they have the same number of levels and the same number        (a binomial coefficient) of nodes at each level, since they were constructed in the 
      same worst-case way and merging them is done by linking the root of one to that of
      the other, the resulting number of nodes at each level is given by adding the values 
      of row i+1 in Pascal's triangle* to itself shifted to the right by one column, which 
      results in the values of row i+2 in that triangle. For example,  the values of the 4th 
      row in Pascal's triangle are: 1 3 3 1.  Adding columnwise 1 3 3 1 0 to 0 1 3 3 1 
      results in 1 4 6 4 1, which is the 5th row in Pascal's triangle. Note that the 
      correspondence of a row in Pascal's triangle to the number of nodes at each level in a
      worst-case weighted quick-union tree is by column to number of nodes at a depth with
      the same value as the ordinal number of the column minus one. For example, 1 3 3 1 
      corresponds to one node at depth 0, 3 nodes at depth 1, 3 nodes at depth 2 and 1 node 
      at depth 3.
  3.  Therefore all trees built in the worst-case way with weighted quick-union have a number
      of nodes at each level given by binomial coefficients.
       
   * For binomial coefficients from Pascal's triangle see "The Art and Craft of Problem 
     Solving, 2ed, Paul Zeitz, 2007, John Wiley & Sons, Inc.
       
  The average depth of a node in a worst-case tree with N = 2**n nodes is (1.*n)/2 because
  binomial coefficients are palindromic, e.g. 1 3 3 1. To compute it the long way see
  averageDepth() below.
 
  In the text, pp 227 and 229 cover worst case analyisis for weighted quick-union.
 
 */

public class Ex1515BinomialTrees {
  
  public static double averageDepth(int n) {
    // compute the average depth of a node in a worst-case weighted quick-union
    // tree with 2**n nodes. This is limited to n < 21 because it's necessary to
    // calculate factorial(n) which overflows as a long for n > 20.
    // for n > 20 use averageDepthBIBD.
    if (n < 0 ) throw new IllegalArgumentException("averageDepth: n must be > 0");
    if (n > 20 ) return averageDepthBIBD(n);
      
    double sum = 0;
    for (int i = 0; i <= n; i++) sum += (factorial(n)*i)/(factorial(i)*factorial(n-i));
    return sum/pow(2,n);
  }
  
  public static double averageDepthBIBD(int n) {
    // compute the average depth of a node in a worst-case weighted quick-union
    // tree with 2**n nodes
    MathContext mc = MathContext.DECIMAL128;
    BigInteger factn = factorial(new BigInteger(""+n));
    BigDecimal sum = new BigDecimal(ZERO);
    BigInteger y; BigInteger m; BigDecimal u; BigDecimal v; BigDecimal w;
    for (int i = 0; i <= n; i++) {
      y = new BigInteger(""+i);
      m = new BigInteger(""+(n-i));
      u = new BigDecimal(factn.multiply(y));
      v = new BigDecimal(factorial(y).multiply(factorial(m)));
      w = u.divide(v, mc); 
      sum = sum.add(w, mc);
    }
    return sum.doubleValue()/pow(2,n);
  }

  public static void main(String[] args) {
    System.out.println(averageDepth(3)); // 1.5
    System.out.println(averageDepth(30)); //15.0
  
  }
}
