package ex11;

import static java.lang.Math.log;

//  1.1.20  Write a recursive static method that computes the value of ln (N !)

public class Ex1120LogFactorial {
  
  public static double logfact(int n) { 
    assert n > 0;
    if (n == 1) return log(1);
    return log(n) + logfact(n-1);
  }

  public static void main(String[] args) {
    
    double sum = 0;
    
    assert logfact(1) == log(1.);
    System.out.println(logfact(3));
    //1.791759469228055
    assert logfact(3) == log(1) + log(2) + log(3);
    
    System.out.println(logfact(100));
    //363.7393755555636
    for (int i = 1; i <= 100; i++) sum+=log(i);
    assert logfact(100) == sum; 
    
    System.out.println(logfact(1000));
    //5912.128178488171
    sum = 0;
    for (int i = 1; i <= 1000; i++) sum+=log(i);
    assert logfact(1000) == sum;
    
    System.out.println(logfact(10000));
    //82108.92783681415
  
  }

}
