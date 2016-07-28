package ex11;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

//  1.1.35 Dice simulation. The following code computes the exact probability distribu-
//  tion for the sum of two dice:
//    int SIDES = 6;
//    double[] dist = new double[2*SIDES+1];
//    for (int i = 1; i <= SIDES; i++)
//      for (int j = 1; j <= SIDES; j++)
//    dist[i+j] += 1.0;
//    for (int k = 2; k <= 2*SIDES; k++)
//    dist[k] /= 36.0;
//  The value  dist[i] is the probability that the dice sum to  k . Run experiments to vali-
//  date this calculation simulating N dice throws, keeping track of the frequencies of oc-
//  currence of each value when you compute the sum of two random integers between 1
//  and 6. How large does N have to be before your empirical results match the exact results
//  to three decimal places?

// it has to be over a million which matches all results to 2 decimal places
// takes to long to test over than that

public class Ex1135DiceSimulation {


  public static void main(String[] args) {

    int SIDES = 6;
    double[] dist = new double[2*SIDES+1];
    for (int i = 1; i <= SIDES; i++)
      for (int j = 1; j <= SIDES; j++)
        dist[i+j] += 1.0;
    for (int k = 2; k <= 2*SIDES; k++)
      dist[k] /= 36.0;
    pa(dist,1,1,1);
    //  [0.0,
    //   0.0,
    //   0.027777777777777776,
    //   0.05555555555555555,
    //   0.08333333333333333,
    //   0.1111111111111111,
    //   0.1388888888888889,
    //   0.16666666666666666,
    //   0.1388888888888889,
    //   0.1111111111111111,
    //   0.08333333333333333,
    //   0.05555555555555555,
    //   0.027777777777777776]
    
    SecureRandom r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    double[] t = new double[13];
    int min = 1; int max = 6;
    int a; int b;
    for (int i = 0; i < 1000000; i++) {
      a = r.nextInt((max - min) + 1) + min; b = r.nextInt((max - min) + 1) + min;
      t[a+b]+=1;
    }
    double[] d = new double[13];
    for (int i = 0; i< 13; i++) d[i] = t[i]/1000000;
    pa(d);
    //  [0.0,
    //   0.0,
    //   0.027828,
    //   0.055134,
    //   0.083466,
    //   0.110847,
    //   0.13887,
    //   0.166738,
    //   0.138892,
    //   0.11097,
    //   0.083944,
    //   0.055651,
    //   0.02766]


 
  }

}
