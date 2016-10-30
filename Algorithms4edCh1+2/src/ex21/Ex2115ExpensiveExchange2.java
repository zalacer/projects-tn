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
  crates are already in correct position selection sort is is the best option.
  However, cycle sort absolutely minimines the number of exchanges to N minus the 
  number of crates already in correct position, so if more than a few crates are in
  correct position to start, cycle sort would be preferable. Using a randomized array 
  with unique elements in the range [0,100) cycle sort averages 98.455 exchanges, so
  its slightly better than selection sort's 100, while insertion sort consistently 
  does more than twice this with an average of 2493.643 and shell sort averages 424.214,
  all over 15 trials using an array with the same element values and positions for
  all sorts each trial and shuffled with SecureRandom.getInstanceStrong() prior to
  each trial. 

 */

public class Ex2115ExpensiveExchange2 {
  
  public static void main(String[] args) {
  
  Random r = null;
  try {
    r = SecureRandom.getInstanceStrong();
  } catch (NoSuchAlgorithmException e) {System.err.println("cannot instantiate Random");}
  
  int[] arr = range(0,100);
  shuffle(arr, r);
  Integer[] generic = (Integer[]) box(arr);
  Integer[] generic2 = Arrays.copyOf(generic, generic.length);
  Integer[] generic3 = Arrays.copyOf(generic, generic.length);
  
  int cycleSortWrites = cycleSort(arr);
  System.out.println("cycle sort writes: " + cycleSortWrites);  
  //96,100,100,98,99,100,97,98,98,99,98,97,99,98                                                                    
  
  int selectionSortWrites = Selection.sort(generic);
  System.out.println("selection sort writes: " + selectionSortWrites); 
  //100,100,100,100,100,100,100,100,100,100,100,100,100,100
  
  int shellSortWrites = Shell.sort(generic2);
  System.out.println("shell sort writes: " + shellSortWrites);
  //471,408,372,422,451,401,392,481,467,401,406,424,431,412
  
  int insertionSortWrites = Insertion.sort(generic3);
  System.out.println("insertion sort writes: " + insertionSortWrites);
  //2663,2463,2550,2341,2685,2449,2440,2627,2655,2393,2490,2270,2415,2470
  
  }

}
