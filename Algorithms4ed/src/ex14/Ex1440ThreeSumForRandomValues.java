package ex14;

import static java.lang.Math.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.LongStream;

/*
  1.4.40 3-sum for random values. Formulate and validate a hypothesis describing the
  number of triples of N random  int values that sum to 0. If you are skilled in math-
  ematical analysis, develop an appropriate mathematical model for this problem, where
  the values are uniformly distributed between –M and M, where M is not small.
  
  Hypothesis: the average number of triples that sum to 0 in a sequence of n integers
  uniformly distributed in the range [-m,m] is 
    ((f(n))/(f(3)*f(n-3)) * (3*m*m + 3*m + 1) * pow(2*m+1, n-3))/pow(2*m+1, n)
  where f is the factorial method.
  
  This has been lightly confirmed by several tests below and is based on the answer in:
    https://math.stackexchange.com/questions/671362/sum-of-three-numbers-from-unformly-distributed-set-equals-to-zero
  which has been saved in this project at:
    Sum of three numbers from unformly distributed set equals to zero.html
    Sum of three numbers from unformly distributed set equals to zero.pdf
 */

public class Ex1440ThreeSumForRandomValues {

  public static int count(int m, int n) throws NoSuchAlgorithmException { 
    // count triples that sum to 0 in a sequence of n ints uniformly 
    // distributed in the range [-m,m].
    SecureRandom r = SecureRandom.getInstanceStrong();
    int[] a = r.ints(n, -m, m+1).toArray();
    int cnt = 0;
    for (int i = 0; i < n; i++)
      for (int j = i+1; j < n; j++)
        for (int k = j+1; k < n; k++)
          if (a[i] + a[j] + a[k] == 0)
            cnt++;
    return cnt;
  }
  
  public static double predictedCount(int m, int n) {
   double f = (1.*factorial(n))/(factorial(3)*factorial(n-3));
   double x = (3*m*m + 3*m + 1) * pow(2*m+1, n-3);
   double v = pow(2*m+1, n);
   return f*x/v;
  }
  
  public static double countTest(int m, int n, int t) throws NoSuchAlgorithmException {
    double sum = 0;
    for (int i = 0; i < t; i ++) sum += count(m,n);
    return sum/t;
  }
  
  public static long factorial(int n) {
    // factorial(20) = 2,432,902,008,176,640,000 <
    //    Long.MAX_VALUE = Math.pow(2,63)-1 = 9,223,372,036,854,775,807 <
    //        factorial(21) = 51,090,942,171,709,440,000
    if (n > 20 || n < 0)
      throw new IllegalArgumentException("factorial: n is out of range 0-20");
    return LongStream.rangeClosed(2, n).reduce(1, (a, b) -> a * b);
  }

  public static void main(String[] args) throws NoSuchAlgorithmException {
      
//    System.out.println(count(5,10)); //13,9,2,10,11,5,8,4
//    System.out.println(countTest(5,10,100000)); //8.154:1000,8.2146:10000,8.19655:100000
//    System.out.println(predictedCount(5,10)); //8.20435762584523

//    System.out.println(countTest(7,15,10000)); //22.8002,22.8569,22.8077
//    System.out.println(predictedCount(7,15)); //22.7837037037037, 22.7837037037037
    
//    System.out.println(countTest(11,20,10000)); //37.0156
//    System.out.println(predictedCount(11,20)); //37.19733705925865

    
    
    
    
  }

}
