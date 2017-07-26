package ex11;

// 1.1.19  Run the following program on your computer:
//    public class Fibonacci {
//      public static long F(int N) {
//        if (N == 0) return 0,
//        if (N == 1) return 1,
//        return F(N-1) + F(N-2),
//      }
//    
//      public static void main(String[] args) {
//        for (int N = 0; N < 100; N++)
//          System.out.println(N + " " + F(N));
//      }
//    }

public class Ex1119Fibonacci {

  public static long F(int N) {
    // this takes too long to run
    if (N == 0) return 0;
    if (N == 1) return 1;
    return F(N-1) + F(N-2);
  }
  
  public static double[][] matrixMul(double[][] a, double[][] b) {
    // for square 2D arrays with same dimensions
    int N = a.length;
    double[][] c = new double[N][N];
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++) { 
        for (int k = 0; k < N; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }
  
  public static double fibMatrix(int n) {
    // exceeds double precision at about n = 85
    // could improve performance by caching results
    if (n == 0) return 0;
    if (n == 1) return 1;
    double[][] a = new double[2][2];
    a[0] = new double[]{1,1};
    a[1] = new double[]{1,0};
    double[][] r = new double[2][2];
    r[0] = new double[]{1,0};
    r[1] = new double[]{0,1};
    for (int i = 1; i < n; i++) r = matrixMul(r,a);
    return r[0][0];
  }

  public static void main(String[] args) {
    
    // this takes too long to run to 99
    for (int N = 0; N < 15; N++)
      System.out.println(N + " " + F(N));

    System.out.println("\nusing fibMatrix:");
    for (int N = 0; N < 100; N++)
      System.out.printf("%d %-19.0f\n", N, fibMatrix(N));

  }

}
