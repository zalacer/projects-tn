package ch01.fundamentals;

import java.util.Objects;
import java.util.Scanner;

// 7. Write a program that reads in two numbers between 0 and 65535, stores them in
// short variables, and computes their unsigned sum, difference, product, quotient,
// and remainder, without converting them to int.

public class Ch0107Unsigned {

  public static void main(String[] args) {
    
    short n1, n2, status;
    long l1, l2, sum, difference, product, quotient, remainder;
    Scanner in = new Scanner(System.in);
    while (true) {
      status = 0;
      System.out.println("enter two natural numbers between 0 and 65535: ");
      n1 = (short) in.nextInt();
      n2 = (short) in.nextInt();
      l1 = Short.toUnsignedLong(n1);
      l2 = Short.toUnsignedLong(n2);
      if (!(Objects.nonNull(l1) && l1 >= 0 && l1 <= 65535)) status += 1;
      if (!(Objects.nonNull(l2) && l2 >= 0 && l2 <= 65535)) status += 2;
      if (status == 0) {
        System.out.println("good!\n");
        break;
      } else if (status == 1) {
        System.out.println("The first integer was not in the specified range. Try again.\n");
      } else if (status == 2) {
        System.out.println("The second integer was not in the specified range. Try again.\n");
      } else {
        System.out.println("Both integers were not in the specified range. Try again.\n");
      }
    }
    in.close();
    
    sum = l1 + l2;
    difference = l1 - l2;
    product = l1 * l2;
    quotient = l1 / l2;
    remainder = Math.floorMod(l1, l2);
    System.out.println(
            "       sum = "+sum
        + "\ndifference = "+difference
        + "\n   product = "+product
        + "\n  quotient = "+quotient
        + "\n remainder = "+remainder);
    
    //  enter two natural numbers between 0 and 65535: 
    //    65001 397
    //    good!
    //
    //           sum = 65398
    //    difference = 64604
    //       product = 25805397
    //      quotient = 163
    //     remainder = 290
    
  }

}
