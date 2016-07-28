package ex11;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Random;

//  1.1.34 Filtering. Which of the following require saving all the values from standard
//  input (in an array, say), and which could be implemented as a filter using only a 
//  fixed number of variables and arrays of fixed size (not dependent on N)? For each,
//  the input comes from standard input and consists of N real numbers between 0 and 1.
//  1  Print the maximum and minimum numbers. filter
//  2  Print the median of the numbers. save
//  3  Print the k th smallest value, for k less than 100. filter - only k elements retained
//  4  Print the sum of the squares of the numbers. filter
//  5  Print the average of the N numbers. filter
//  6  Print the percentage of numbers greater than the average. save
//  7  Print the N numbers in increasing order. save
//  8  Print the N numbers in random order. filter - can be done in one pass

public class Ex1134Filtering {


  public static void main(String[] args) {

    double[] a = {.3,.6,.1,.8,.4,.9,.2,.7,.5};

    //1: max and min
    // it's necessary to encapsulate the maximum int in an object, in this case in
    // an array, since local variables visible in lambda expressions must be effectively
    // final, i.e. their values cannot be changed, and the filter contains such an
    // expression. count() is used only to terminate the stream which is necessary
    // to update max because intermediate stream operations such as filter are lazy.
    // the filter returns false since it must return a boolean and in this case it 
    // makes little difference except false drops the current value from the stream
    // and so can reduce overhead since it then never reaches count().
    double[] max = {Double.NEGATIVE_INFINITY};
    Arrays.stream(a).filter(x -> {if (x > max[0]) max[0] = x; return false;}).count();
    assert max[0] == .9;

    double[] min = {Double.POSITIVE_INFINITY};
    Arrays.stream(a).filter(x -> {if (x < min[0]) min[0] = x; return false;}).count();
    assert min[0] == .1;

    //2: median
    @SuppressWarnings("unused")
    double median = median(a); // requires the whole array
    //System.out.println(median); //0.4
    
    //3: kth smallest for k < 100
    final int N = 99;
    // mini test cases
//    a = new int[]{4,3,2,1,0}; // reverse sorted
//    a = new int[]{0,1,2,3,4}; // sorted
    a = new double[]{3.,1.,4.,0.,2.}; // unsorted
    final double[] b = new double[0];
    // using a 2d array because row 0 will be created multiple times
    // and hence won't be effectively final; this could be optimized out
    final double[][] kth = new double[1][N];
    final int smallest = 0; 
    kth[smallest] = b;
    // this stream filter builds a sorted array so that kth[i] is the ith smallest in a
    // but for a given k it could be written to retain only k elements
    Arrays.stream(a).filter(x -> {
      if (kth[smallest].length == 0) {
        kth[0] = new double[]{x};
        return false;
      } else {
        swapSort(kth[smallest]);
        if (x < kth[smallest][kth[smallest].length-1]) {
          // this could be optimized with binary search
          for (int i = 0; i < kth[smallest].length; i++) {
            if (kth[smallest][i] == x) return false;
            if (x < kth[smallest][i]) {
              double[] d = null;
              if (kth[smallest].length < N) {
                d = new double[kth[smallest].length+1];
              } else {
                d = new double[N];
              }
              for (int j = 0; j < i; j++) d[j] = kth[smallest][j];
              d[i] = x;
              for (int j = i+1; j < d.length; j++) d[j] = kth[smallest][j-1];
              kth[smallest] = d;
              return false;
            }
          }
        } else if (x > kth[smallest][kth[smallest].length-1]
            && kth[smallest].length < N) {
          double[] d = new double[kth[smallest].length+1];
          for (int i = 0; i < kth[smallest].length; i ++)
            d[i] = kth[smallest][i];
          d[kth[smallest].length] = x;
          kth[smallest] = d;
          return false;
        }
      }
      return false;
    }).count();

    //System.out.println(Arrays.toString(kth[smallest]));
    //[0.0, 1.0, 2.0, 3.0, 4.0] for all 3 cases
    double[] sorted = Arrays.copyOf(a, a.length);
    swapSort(sorted);
    for (int i = 0; i < kth[smallest].length; i++) 
      //since a has no duplicates and <N elements
      assert kth[smallest][i] == sorted[i]; 
    
    // 4. sum of squares
    a = new double[]{.3,.6,.1,.8,.4,.9,.2,.7,.5};
    double[] sum = {0.};
    Arrays.stream(a).filter(x -> {sum[0] = sum[0] + x*x; return true;}).count();
    //System.out.println(sum[0]); //2.85
    // this method also builds the sum of squares touching each element only once
    assert sum[0] == foldLeft(a,(x,y)->x+y*y,0);
    
    // 5. Print the average of the N numbers
    double[] sc = {0.,0.};
    Arrays.stream(a).filter(x ->{sc[0]=sc[0]+x;sc[1]=sc[1]+1;return true;}).count();
    double average = sc[0]/sc[1];
    assert average == mean(a);
    
    //6. Print the percentage of numbers greater than the average.
    //   given the average another filtering pass through the array is required:
    long count = Arrays.stream(a).filter(x ->{return x<=average ? false : true;}).count();
    @SuppressWarnings("unused")
    double percent = 100* count/a.length;
    //System.out.println(percent); //44.0
    
    //7. Print the N numbers in increasing order.
    swapSort(a);
    pa(a); //double[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9]
    
    //8. Print the N numbers in random order
    a = new double[]{.3,.6,.1,.8,.4,.9,.2,.7,.5};
    Random r = new Random(47130561);
    shuffle(a,r);
    pa(a); //double[0.5,0.7,0.1,0.6,0.9,0.8,0.3,0.2,0.4]
    // what shuffle does:
    // for (int i=a.length; i>1; i--) swap(a, i-1, r.nextInt(i));
    
    
  }

}
