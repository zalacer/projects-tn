package ex11;

//  1.1.16  Give the value of  exR1(6) :
//    public static String exR1(int n) {
//      if (n <= 0) return "";
//      return exR1(n-3) + n + exR1(n-2) + n;
//    }

// 311361142246

public class Ex1116ValueOfFunction {
  
  public static String exR1(int n) {
    if (n <= 0) return "";
    return exR1(n-3) + n + exR1(n-2) + n;
  }
 
  public static void main(String[] args) {
    
    System.out.println(exR1(6)); //311361142246
 
  }

}
