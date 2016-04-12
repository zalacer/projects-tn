package ch01.fundamentals;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

// 3. Using only the conditional operator, write a program that reads three integers and
// prints the largest. Repeat with Math.max.

public class Ch0103CondOp {

  public static void main(String[] args) {

    int[] n = new int[3]; int max = 0; int count = 0;
    Scanner in = new Scanner(System.in);
    while (true) {
      System.out.println("enter an integer: ");
      n[count] = in.nextInt();
      if (Objects.nonNull(n[count])) count++;
      if (count == 3) break;
    }
    in.close();

    System.out.println("n[] = "+Arrays.toString(n)); // [23, 5, 77], 
    max = n[1] > n[0] ? n[1] : n[0];    
    max = n[2] > max ? n[2] : max;
    System.out.println("max by cond = "+max); // 77
    max = Math.max(n[1], n[0]);
    max = Math.max(n[2], max);
    System.out.println("max by Math.max = "+max); // 77
    max = Math.max(n[2],Math.max(n[1], n[0]));
    System.out.println("max by Math.max(Math.max) = "+max); // 77
  }

}
