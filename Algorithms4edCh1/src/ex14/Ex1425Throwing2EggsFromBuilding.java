package ex14;

import static v.ArrayUtils.*;
import java.util.function.IntPredicate;

//  p211
//  1.4.25 Throwing two eggs from a building. Consider the previous question, but now
//  suppose you only have two eggs, and your cost model is the number of throws. Devise
//  a strategy to determine F such that the number of throws is at most 2√N, then find a
//  way to reduce the cost to ~c √F. This is analogous to a situation where search hits (egg
//  intact) are much cheaper than misses (egg broken).

//  From http://algs4.cs.princeton.edu/14analysis/:
//  Solution to Part 1: To achieve 2 * sqrt(N), drop eggs at floors 
//  sqrt(N), 2 * sqrt(N), 3 * sqrt(N), ..., sqrt(N) * sqrt(N). 
//  (For simplicity, we assume here that sqrt(N) is an integer.) Let assume that the egg broke
//  at level k * sqrt(N). With the second egg you should then perform a linear search in the 
//  interval (k-1) * sqrt(N) to k * sqrt(N). In total you will be able to find the floor F in 
//  at most 2 * sqrt(N) trials.
//  
//  Hint for Part 2: 1 + 2 + 3 + ... k ~ 1/2 k^2. 

//  Based on the hint for part 2, a simple solution is to test floors 1, 1+2, 1+2+3,..., N
//  and when an egg breaks linearly test the floors between the current and previous ones
//  starting with the lowest (previous+1). If F is the number of first floor from which an 
//  egg broke, then the worst case total number of tests is ~c*sqrt(F) for some constant c 
//  because the hint shows that a similar series summing to sqrt(k) has ~sqrt(k)/2 terms.
//  This strategy is implemented findFloorCsqrtF() below.

public class Ex1425Throwing2EggsFromBuilding {

  public static int findFloorCsqrtF(int[] z, IntPredicate eggBreaks) {
    // return the smallest index of an element in z satisfying eggBreaks
    // if any, otherwise return -1. assumes z has been sorted in increasing
    // order. O(C*sqrt(F)) worst case number of tests.
    if (z == null || z.length == 0 || eggBreaks == null) return -1;
    if (z.length == 1 && eggBreaks.test(z[0])) return 0;
    int n = z.length;
    int previous = 1; // previous floor tested
    int last = 1; // last floor tested
    int i = 1; // a number that after incrementation increments s
    int s = 1; // index of floor to be tested with values of partial sums of 1+2+3+4...
    boolean broken = false; // true if an egg broke when dropped from the last floor else false
    while (true) {
      if (previous == n-1 || s > n-1) return -1;
      s+=++i;  
      if (s > n-1) s = n-1;
      // if an egg breaks here one may still break on a lower floor above previous
      if (eggBreaks.test(z[s])) {
        last = s;
        broken = true;
        break;
      }
      previous = s;
    }
    if (broken) {
      // test if an egg breaks on a floor between previous and last
      for (int j = previous+1; j < last; j++)
        if (eggBreaks.test(z[j])) return j;
      return last;
    }
    return -1;
  }

  public static void main(String[] args) {

    int[] building = {1,2,3,4,5,6,7,8,9,10,11,12,14,15};
    // suppose floor 11 is the lowest floor from which an egg breaks
    int f = findFloorCsqrtF(building, x -> x >= 11);
    System.out.println(f); //10
    System.out.println(building[f]); //11

    building = append(range(1,13),range(14,1001));
    // building = {1,2,3,4,5,6,7,8,9,10,11,12,14...,1000};
    f = findFloorCsqrtF(building, x -> x >= 531);
    System.out.println(f); //529
    System.out.println(building[f]); //531
  }

}
