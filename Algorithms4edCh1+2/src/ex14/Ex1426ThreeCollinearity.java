package ex14;

//import static v.ArrayUtils.*;
//import java.util.function.IntPredicate;

//  p211
//  1.4.26 3-collinearity. Suppose that you have an algorithm that takes as input N 
//  distinct points in the plane and can return the number of triples that fall on the
//  same line. Show that you can use this algorithm to solve the 3-sum problem. Strong 
//  hint : Use algebra to show that (a, a**3), (b, b**3), and (c, c**3) are collinear 
//  if and only if a + b + c = 0.

//  If three two dimensional points aren't distinct then they form a line, perhaps of zero 
//  length, if they're all the same. Now imagine three distinct two dimensional points 
//  P1(x1,y1), P2(x2,y2) andP3(x3,y3): 
//                      P2(x2,y2)
//                       /     \
//                    L1/       \L2
//                     /         \
//              P1(x1,y1)---L3---P3(x3,y3)
// These points will be collinear iff the slope of any two of the lines L1, L2 and L3
// connecting them have the same slope, for example if (y2-y1)/(x2-x1) = (y3-y1)/(x3-x1)
// (using an algebraic equals). Substituting, a = x1, b = x2, c = x3, a**3 = y1, 
// b**3 = y2, c**3 = y3, this becomes [1] (b**3 - a**3)/(b - a) = (c**3 - a**3)/(c - a).
// However (b**2 + ab + a**2)(b-a) = b**3 - a**3 and (c**2 + ac +a**2)(c - a) = c**3 - a**3
// so [1] reduces to b**2 + ab + a**2 = c**2 + ac +a**2 which simplifies to
// b**2 − c**2 = a(c−b) = -a(b-c) and since c isn't identical to b by assumption, dividing 
// both sides by b-c results in b + c = -a or a + b + c = 0.  This process can simply be 
// reversed to show that if a + b + c = 0 then two of the lines have the same slope and the
// points are collinear. Therfore an algorithm taking as input N distinct points in the plane
// and returning the number of triples that fall on the same line can be used to solve the 
// 3-sum problem by giving it points of the form (x, x**3).

public class Ex1426ThreeCollinearity {

  public static void main(String[] args) {

  
  }

}
