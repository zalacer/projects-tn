package analysis;


// Algorithms-4ed p185
//http://www.giannistsakiris.com/2010/01/09/base-2-and-base-n-logarithm-calculation-in-java/
//https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
//https://stackoverflow.com/questions/13831150/logarithm-algorithm
//https://stackoverflow.com/questions/739532/logarithm-of-a-bigdecimal

public class Log {
  
  private Log(){}
  
  public static double log(int x, int b) {
    // return the log base b of x
    return Math.log(x)/Math.log(b);
  }
  
  public static double log(long x, int b) {
    // return the log base b of x
    return Math.log(x)/Math.log(b);
  }
  
  public static double log(double x, int b) {
    // return the log base b of x
    return Math.log(x)/Math.log(b);
  }
  
  public static double logE(int x) {
    // return the log base e of x
    return Math.log(x)/Math.log(Math.E);
  }
  
  public static double ln(int x) {
    // return the log base e of x
    return Math.log(x)/Math.log(Math.E);
  }
  
  public static double log2(int x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static double lg(int x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static double log2(long x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static double lg(long x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static double log2(double x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static double lg(double x) {
    // return the log base 2 of x
    return Math.log(x)/Math.log(2);
  }
  
  public static int ilog2(int x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }
  
  public static int ilg(int x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }
  
  public static int ilog2(long x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }
  
  public static int ilg(long x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }
  
  public static int ilog2(double x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }

  public static int ilg(double x) {
    // return the integer binary log of x
    return (int) (Math.log(x)/Math.log(2));
  }

  public static void main(String[] args) {

    System.out.println(ilog2(7)); //2
    System.out.println(log2(7));  //2.807354922057604


  }

}
