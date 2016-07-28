package ex11;

//  1.1.17  Criticize the following recursive function:
//    public static String exR2(int n) {
//      String s = exR2(n-3) + n + exR2(n-2) + n;
//      if (n <= 0) return "";
//      return s;
//   }
//  Answer : The base case will never be reached. A call to  exR2(3) will result in calls to
//  exR2(0) ,  exR2(-3) ,  exR3(-6) , and so forth until a StackOverflowError occurs.

// the terminating condition must come first

public class Ex1117CriticizeRecursiveFunction {
  
  public static String exR1(int n) {
    if (n <= 0) return "";
    return exR1(n-3) + n + exR1(n-2) + n;
  }
  
  public static String exR2(int n) {
    String s = exR2(n-3) + n + exR2(n-2) + n;
    if (n <= 0) return "";
    return s;
  }
 
  public static void main(String[] args) {
    
    System.out.println(exR2(2)); //Exception in thread "main" java.lang.StackOverflowError
                                 //at ex11.Ex1117.exR2(Ex1117.java:20)
 
  }

}
