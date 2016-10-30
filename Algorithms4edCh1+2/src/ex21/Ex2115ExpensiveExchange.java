package ex21;

import static sort.Cycle.*;
import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import sort.Selection;
import sort.Insertion;
import sort.Shell;

/* p265
  2.1.15 Expensive exchange. A clerk at a shipping company is charged with the task of
  rearranging a number of large crates in order of the time they are to be shipped out.
  Thus, the cost of compares is very low (just look at the labels) relative to the cost of ex-
  changes (move the crates). The warehouse is nearly full—there is extra space sufficient
  to hold any one of the crates, but not two. What sorting method should the clerk use?

  Of the sorts so far presented, for an array of length N, selection sort uses
  N exchanges (p248) while insertion uses ~(N**2)/2 worst case, zero best case and
  ~(N**2)/4  average for random arrays (p250) and nothing is said about shell sort
  insertions/exchanges/swaps, but it's probably usually less than insertion sort's.
  Based on this and actual testing shown below, with no information about how many 
  crates are already in correct position selection sort is the best option. However,
  cycle sort absolutely minimizes the number of exchanges to N minus the number of 
  crates already in correct position, so if more than a few crates are in correct 
  position to start, cycle sort would be preferable. Using a randomized array with
  100 unique elements in the range [0,100) cycle sort averages 99.004 exchanges,
  while insertion sort consistently does more than twice this with an average of 
  2478.866 and shell sort averages 432.328 all over 1000 trials using an array with 
  the same element values and positions for all sorts each trial and shuffled with 
  SecureRandom.getInstanceStrong() prior running the sorts during each trial. 

 */

public class Ex2115ExpensiveExchange {

  public static void main(String[] args) {

    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("cannot instantiate Random");
    }

    int n = 1000; int[] z = range(0,100);
    int[] cycle = new int[n];
    int[] selection = new int[n];
    int[] shell = new int[n];
    int[] insertion = new int[n];
    
    for (int i = 0; i < n; i++) {
      shuffle(z, r);
      Integer[] generic1 = (Integer[]) box(z);
      Integer[] generic2 = Arrays.copyOf(generic1, generic1.length);
      Integer[] generic3 = Arrays.copyOf(generic1, generic1.length);
      cycle[i] = cycleSort(z);
      selection[i] = Selection.sort(generic1);
      shell[i] = Shell.sort(generic2);
      insertion[i] = Insertion.sort(generic3);
    }
    
    System.out.printf("    cycle average: %5.3f\n", mean(cycle));     // 99.004
    System.out.printf("selection average: %5.3f\n", mean(selection)); // 100.000
    System.out.printf("    shell average: %5.3f\n", mean(shell));     // 433.010
    System.out.printf("insertion average: %5.3f\n", mean(insertion)); // 2471.240
  }

}
