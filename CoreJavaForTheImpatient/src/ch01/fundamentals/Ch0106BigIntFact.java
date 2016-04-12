package ch01.fundamentals;

import static java.math.BigInteger.ONE;
import static utils.StringUtils.getParts;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// 6. Write a program that computes the factorial n! = 1 × 2 × … × n, using
// BigInteger. Compute the factorial of 1000.

public class Ch0106BigIntFact {

  public static BigInteger factorial(int n) {
    if (n == 0) return ONE;        
    n = Math.abs(n);
    BigInteger r = ONE;
    for(int i = 1; i<=n; i++) {
      r = r.multiply(new BigInteger(Integer.toString(i)));
    }
    return r;
  }

  public static void main(String[] args) {

    System.out.println("factorial(3) = "+factorial(3)); //6
    System.out.println("factorial(100) = "+factorial(100)); 
    //93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000
    // System.out.println("factorial(1000) = "+factorial(1000)); // too big for eclipse console

    String f = factorial(1000).toString();
    List<String> fparts = getParts(f,70);

    try {
      Files.write(Paths.get("factorial1000"), fparts);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
