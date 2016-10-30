package ex23;

import static analysis.Log.ln;

public class Ex2314ProveQuickSortUses2NlnNCompares {

  /* p304  
  2.3.14  Prove that when running quicksort on an array with N distinct items, the 
  probability of comparing the ith and jth largest items is 2/(j-i). Then use this
  result to prove Proposition K (for algo2.5).
  
  Let x be the array, x[k] be the kth largest element when its finally sorted, 
  assume that i < j and let X{x[i],...x[j} be the ordered set of elements from i to 
  j. Then x[i] and x[j] will be compared iff x[i] or x[j] is the first pivot selected
  from X. For if x[k], where i < k < j, is the first pivot selected from x, x[i] and
  x[j] will be split into separate partitions and never compared, while if x[i] or
  x[j] is the first pivot selected from x they will be compared currently, since they
  couldn't have been split and if no element in X is selected as the pivot no element 
  in X can be compared to any other element in X. Since the size of X is j-i+1, the 
  probability of randomly selecting any element from it is its inverse so the chance 
  of selecting either x[i] or x[j] is 2/(j-i+1), which is the probability of comparing 
  the ith and jth largest items.
  
  The expected number of comparisons is the summation of that probability over all pairs 
  (i,j) in x: 
     sum(i=1->N-1,(sum(j=i+1->N,2/(j-i+1)))) which is with k = j-1
     sum(i=1->N-1,(sum(k=1->(N-i),2/(k+1)))) <= sum(i=1->N-1,(sum(k=1->N,2/k)))
                                                = 2*(N-1)*(H(N)) ~ 2NlnN 
  where H(N) ~ lnN + O(1) is the Nth harmonic number. Calculation of the exact result and
  upper bound results can be done in Java as shown below. 
  
  Ref: NCSU CSC 505, Spring 2005, Lecture Notes, Week 5, available from:
         http://people.engr.ncsu.edu/mfms/Teaching/CSC505/wrap/Lectures/week05.pdf
         and in this project at NSCU-CSC505-Spring2005-Lecture5QuickSort.pdf
         see A Completely Different Approach on p5.
       CMU 15-451 (Algorithms), Fall 2011, Lecture Notes, Lecture 3, available from
         https://www.cs.cmu.edu/~avrim/451f11/lectures/lect0906.pdf
         and in this project at CMU-15-451-Fall-2011-Lecture3-ProbabilsticAnalysis.pdf
         for The Basics of Probabilistic Analysis on p2.
       Introduction to Algorithms; Cormen, Leiserson, Rivest, Stein; 3Ed; 2009; MIT
         for appendices on math.  
  */ 
  
  public static double comparisonsExact(int n) {
    // calculate exact number of comparisons for quicksort algo2.5
    double sum = 0;
    for (int i = 1; i <= n-1; i++)
      for (int k = 1; k <= n-i; k++)
        sum+=2./(k+1);
    return Math.ceil(sum);
  }
  
  public static double comparisonsUpperBound(int n) {
    // calculate an upper bound of number of comparisons for quicksort algo2.5
    double h = 1;
    for (int i = 2; i <= n; i++) h+=1./i;
    return Math.ceil(2.*(n-1)*h);
  }
  
  public static double comparisonsLogApprox(int n) {
    // calculate an upper bound log approximation of the number of comparisons
    // for quicksort algo2.5
    return Math.ceil(2*n*ln(n));
  }
  
  public static void main(String[] args) {
    
    int n = 100000;
    System.out.println(comparisonsExact(n));      // 2018054
    System.out.println(comparisonsUpperBound(n)); // 2418006
    System.out.println(comparisonsLogApprox(n));  // 2302586
 
  }

}

