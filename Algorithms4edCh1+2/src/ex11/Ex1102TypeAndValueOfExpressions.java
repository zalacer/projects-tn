package ex11;

//  1.1.2  Give the type and value of each of the following expressions:
//  a.  (1 + 2.236)/2       double 1.618
//  b.  1 + 2 + 3 + 4.0     double 10.0
//  c.  4.1 >= 4            boolean true
//  d.  1 + 2 + "3"         String "33"

public class Ex1102TypeAndValueOfExpressions {

  public static void main(String[] args) {
    
    double a = (1 + 2.236)/2;
    System.out.println((a));
    
    double b = 1 + 2 + 3 + 4.0;
    System.out.println(b);
    
    boolean c = 4.1 >= 4;
    System.out.println(c);
    
    String d = 1 + 2 + "3";
    System.out.println(d);
    
  }

}
