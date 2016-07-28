package analysis;

public class Harmonic {
  
  public static double harmonic(int n) {
    //return the value of the nth harmonic number
    if (n == 1) return 1;
    double sum = 0;
    for (int i = 1; i <= n; i++) sum += 1.0 / i;
    return sum;
  }

  public static void main(String[] args) {

  }

}
