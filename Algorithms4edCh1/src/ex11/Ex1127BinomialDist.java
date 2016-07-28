package ex11;

//  1.1.27 Binomial distribution. Estimate the number of recursive calls that would be
//  used by the code
//    public static double binomial(int N, int k, double p) {
//      if ((N == 0) || (k < 0)) return 1.0;
//      return (1.0 - p)*binomial(N-1, k) + p*binomial(N-1, k-1);
//    }
//  to compute binomial(100, 50). Develop a better implementation that is based on
//  saving computed values in an array.

// binomial(100, 50, .00001) will take about 4.84E24 recursive calls
// the better algorithm (binomial2) is also from the web site, see below

public class Ex1127BinomialDist {
  
  public static int count = 0;

  // this seems to always return 1.0
  public static double binomial(int N, int k, double p) {
    if ((N == 0) || (k < 0)) return 1.0;
    return (1.0 - p)*binomial(N-1, k, p) + p*binomial(N-1, k-1, p);
  }

  // http://algs4.cs.princeton.edu/11model/Binomial.java.html
  // slow
  public static double binomial1(int N, int k, double p) {
    ++count;
    if (N == 0 && k == 0) return 1.0;
    if (N < 0 || k < 0) return 0.0;
    return (1.0 - p) *binomial1(N-1, k, p) + p*binomial1(N-1, k-1, p);
  }

  // memoization - not really, it does precomputing
  public static double binomial2(int N, int k, double p) {
    double[][] b = new double[N+1][k+1];

    // base cases
    for (int i = 0; i <= N; i++)
      b[i][0] = Math.pow(1.0 - p, i);
    b[0][0] = 1.0;

    // recursive formula
    for (int i = 1; i <= N; i++) {
      for (int j = 1; j <= k; j++) {
        ++count;
        b[i][j] = p * b[i-1][j-1] + (1.0 - p) *b[i-1][j];
      }
    }
    return b[N][k];
  }

  public static void main(String[] args) {
//    int N = Integer.parseInt(args[0]);
//    int k = Integer.parseInt(args[1]);
//    double p = Double.parseDouble(args[2]);
//    StdOut.println(binomial1(N, k, p));
//    StdOut.println(binomial2(N, k, p));
    
//    System.out.println(binomial1(14, 7, .001)); //3.408047952000048E-18
//    System.out.println(count); //38773
//    
//    System.out.println(binomial1(14, 7, .00001)); //3.431759767207083E-32
//    System.out.println(count);  //38773
    
//    System.out.println(binomial1(16, 8, .00001)); //1.2868970436035293E-36
//    System.out.println(count);  //153951 = 3.97 * 38773
//    // factor = 3.9705723054703014984654269723777
//    
//    System.out.println(binomial1(18, 9, .00001)); //4.861562437502797E-41
//    System.out.println(count);  //611803 = 3.97 * 153951
//    // factor = 3.9740112113594585290124779962456
//
//    System.out.println(Math.pow(4., (100-18)/2)+611803); //4.8357032784585167E24
//    // binomial1(100, 50, .00001) will take about 4.84E24 recursive calls
      
//    System.out.println(binomial2(18, 9, .00001)); //4.861562437502797E-41
//    System.out.println(count); //162 = 18*9

      System.out.println(binomial2(100, 50, .001)); //9.596841476810664E-122 
      System.out.println(count); //5000
      
  }

}
