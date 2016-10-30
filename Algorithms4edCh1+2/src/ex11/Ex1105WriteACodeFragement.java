package ex11;

// 1.1.5  Write a code fragment that prints  true if  the  double variables
// x and  y are both strictly between  0 and  1 and  false otherwise.

public class Ex1105WriteACodeFragement {

  public static void main(String[] args) {
    
    double x = .2; double y = .3;
    
    if (x > 0 && x < 1 && y > 0 && y < 1) {
      System.out.println("true");
    } else System.out.println("false");
    //true
    x = 0.0;
    
    if (x > 0 && x < 1 && y > 0 && y < 1) {
      System.out.println("true");
    } else System.out.println("false");
    //false

  }

}

