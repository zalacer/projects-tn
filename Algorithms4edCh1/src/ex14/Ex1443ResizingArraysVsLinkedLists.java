package ex14;

import static analysis.DoublingTest.*;

/*
  1.4.43 Resizing arrays versus linked lists. Run experiments to validate the hypothesis
  that resizing arrays are faster than linked lists for stacks (see Exercise 1.4.35 and Exer-
  cise 1.4.36). Do so by developing a version of DoublingRatio that computes the ratio
  of the running times of the two programs.
  
  It's better to do independent benchmarks to avoid fouling results with int and object
  caching which favors the test done last.
  
  I got more useful information from running the timeTrials independently than from 
  conglomerating them in pairs in a doublingTest comparing ratio/previous_ratio because
  each scales linearly with the others,  but that doesn't mean there is no difference --
  in fact there is a difference of a factor of two between the running times of
  Stack<Integer> and resizingIntArrayStack with ResizingArrayStack<Integer> runtimes
  closer to Stack<Integer> than to resizingIntArrayStack. In conclusion, the main cause
  of performance disadvantage is heavy use of objects vs primitives and using primitives
  as much as possible decreases runtime by 50%.
 */

public class Ex1443ResizingArraysVsLinkedLists {

  public static void main(String[] args) {
    
    //intStackVsResizingIntArrayStackDoublingRatio();
    //  IntStack vs ResizingArrayStack Doubling Ratios:
    //    N      ratio/previous-ratio
    //    250       1 
    //    500       1 
    //   1000       1 
    //   2000       1 
    //   4000       1 
    //   8000       1 
    //  16000       1 
    //  32000       1 
    //  64000       1 
    // 128000       1 
    // 256000       1 
    // 512000       1 
    //1024000       1 

    //stackIntegerVsResizingArrayStackIntegerDoublingRatio();
    //  StackInteger vs ResizingArrayStackInteger Doubling Ratios:
    //    N      ratio/previous-ratio
    //    250       1 
    //    500       1 
    //   1000       1 
    //   2000       1 
    //   4000       1 
    //   8000       1 
    //  16000       1 
    //  32000       1 
    //  64000       1 
    // 128000       1 
    // 256000       1 
    // 512000       1 
    //1024000       1 
    
    //stackIntegerVsResizingIntArrayStackDoublingRatio();
    //  StackInteger vs ResizingArrayStackInteger Doubling Ratios:
    //    N      ratio/previous-ratio
    //    250       1 
    //    500       1 
    //   1000       1 
    //   2000       1 
    //   4000       1 
    //   8000       1 
    //  16000       1 
    //  32000       1 
    //  64000       1 
    // 128000       1 
    // 256000       1 
    // 512000       1 
    //1024000       1 
    
    // Doubling test shows no useful information.
 
    int n = 1000000;
    System.out.println(resizingIntArrayStackTimeTrial(n));     //727     
    System.out.println(intStackTimeTrial(n));                  //1149 
    System.out.println(resizingArrayStackIntegerTimeTrial(n)); //1228
    System.out.println(stackIntegerTimeTrial(n));              //1593
    System.out.println();
    System.out.println(resizingIntArrayStackTimeTrial(n));     //785
    System.out.println(intStackTimeTrial(n));                  //952
    System.out.println(resizingArrayStackIntegerTimeTrial(n)); //1201
    System.out.println(stackIntegerTimeTrial(n));              //1583
    System.out.println();
    System.out.println(resizingIntArrayStackTimeTrial(n));     //786
    System.out.println(intStackTimeTrial(n));                  //980
    System.out.println(resizingArrayStackIntegerTimeTrial(n)); //1374
    System.out.println(stackIntegerTimeTrial(n));              //1509

    
    // timeTrial data consistently shows running time of
    // resizingIntArrayStack < IntStack < ResizingArrayStack<Integer> < Stack<Integer>
    // with overall performance difference of a factor of 2.
  }

}
