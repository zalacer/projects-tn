package ex23;

public class Ex2315NutsAndBolts {

  /* p305  
  2.3.15 Nuts and bolts. (G. J. E. Rawlins) You have a mixed pile of N nuts and N bolts
  and need to quickly find the corresponding pairs of nuts and bolts. Each nut matches
  exactly one bolt, and each bolt matches exactly one nut. By fitting a nut and bolt 
  together, you can see which is bigger, but it is not possible to directly compare two 
  nuts or two bolts. Give an efficient method for solving the problem.
  
  This can be done like quicksort with NlgN average time complexity N^2 worst case,
  calibrated to your metabolic rate.
  1. Find a medium size bolt, not the smallest or the biggest.
  2. Try matching it with the nuts one by one putting smaller nuts in one pile and bigger
     nuts in another. Continue this even after the matching nut has been found. When done
     put the matching bolt in the done pile.
  3. Now take the nut that matched the first bolt and use it to separate all the bolts into
     two piles smaller and bigger. When done take that nut and screw it onto it matching bolt
     and put them in the done pile.
  4. Take a look at the two piles of unmatched nuts and pick the larger of them. If it's the
     pile of smaller nuts take a bolt from the pile of smaller bolts. If it's the pile of
     bigger nuts take a bolt from the pile of bigger bolts. In either case chose a bolt that
     medium sized for its pile. 
  5. Repeat 2-4 until all the nuts and bolts are matched. Each step may double the number of
     piles of bolts and double the number of piles of nuts so its important to keep track of
     which matches which in terms of size ranges.
  */ 
  
  public static void main(String[] args) {
 
  }

}

