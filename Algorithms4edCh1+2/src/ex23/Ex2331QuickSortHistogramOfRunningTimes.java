package ex23;

import static sort.Quicks.histogramOfRunningTimes;
import static sort.Quicks.pause;

public class Ex2331QuickSortHistogramOfRunningTimes {

  /* p307  
  2.3.31 Histogram of running times. Write a program that takes command-line 
  arguments N and T, does T trials of the experiment of running quicksort on 
  an array of N random  Double values, and plots a histogram of the observed 
  running times. Run your program for N = 10^3, 10^4, 10^5 and 10^6, with T 
  as large as you can afford to make the curves smooth. Your main challenge 
  for this exercise is to appropriately scale the experimental results.

  */
  
  public static void main(String[] args) {

    histogramOfRunningTimes(1000,100);

    pause(3000);

    histogramOfRunningTimes(10000,100);

    pause(3000);

    histogramOfRunningTimes(100000,100);

    histogramOfRunningTimes(1000000,25);    
    
  }

}

