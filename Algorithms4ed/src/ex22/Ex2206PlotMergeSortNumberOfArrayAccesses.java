package ex22;

import static sort.BottomUpMerge.plotBottomUpMergeSortArrayAccesses;
import static sort.TopDownMerge.plotTopDownMergeSortArrayAccesses;

public class Ex2206PlotMergeSortNumberOfArrayAccesses {
  
/* p284
  2.2.6  Write a program to compute the exact value of the number of array accesses
  used by top-down mergesort and by bottom-up mergesort. Use your program to plot the 
  values for N from 1 to 512, and to compare the exact values with the upper bound 
  6*N*lg(N).
    
*/
  
  public static void pause(long sec) {
    try {
      Thread.sleep(sec*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) {
    
    plotTopDownMergeSortArrayAccesses(512);
    
    pause(3);
    
    plotBottomUpMergeSortArrayAccesses(512);

  }

}
