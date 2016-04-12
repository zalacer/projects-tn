package ch1108.annotations.testcase;

public class Factorial {
  
  @TestCase(params="4", expected="24")
  @TestCase(params="0,3", expected="1,6")
  public static long factorial(int n) {
    if (n == 0) return 1;   
    if (n < 0) n = -n;
    if (n == 1) return 1;  
    if (n == 2) return 2;
    long r = 1;
    for(int i = 2; i<=n; i++)  r = r * i;
    return r;
  }

}
