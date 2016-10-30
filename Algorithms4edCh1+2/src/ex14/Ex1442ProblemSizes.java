package ex14;

/*
  1.4.42 Problem sizes. Estimate the size of the largest value of P for which you can run
  TwoSumFast, TwoSum, ThreeSumFast, and ThreeSum on your computer to solve the
  problems for a file of 2**P thousand numbers. Use  DoublingRatio to do so.
  
  From previous testing I know the limit for TwoSumFast is N = 2**28, P = 18, which is due
  to the space taken by the array created by twoSumFastTimeTrial consuming more memory than 
  is configured for my JVM. Since all the timeTrial methods create an int array, that limit 
  applies to all of them, regardless of the doubling ratio. However, for ThreeSum the limit
  is based on the amount of time I will wait for it to run and for it that's up to N = 16000
  for which P = 4. The rest fall in between these two. 
 */

public class Ex1442ProblemSizes {

  public static void main(String[] args) {
 
  }

}
