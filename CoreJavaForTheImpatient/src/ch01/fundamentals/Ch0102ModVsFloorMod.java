package ch01.fundamentals;

import java.util.Objects;
import java.util.Scanner;

// 2. Write a program that reads an integer angle (which may be positive or negative) and
// normalizes it to a value between 0 and 359 degrees. Try it first with the % operator,
// then with floorMod.

public class Ch0102ModVsFloorMod {

  public static void main(String[] args) {

    int n, n1, n2;
    Scanner in = new Scanner(System.in);
    while (true) {
      System.out.println("enter an integer: ");
      n = in.nextInt();
      if (Objects.nonNull(n)) break;
    }
    in.close();
    System.out.println("input n = "+n); // -970,361,-1,-100
    n1 = ((n % 360) + 360) % 360;
    n2 = Math.floorMod(n, 360);
    System.out.println("mod result n1 = "+n1); // 110,1,359,260
    System.out.println("floorMod result n1 = "+n2); // 110,1,359,260

  }

}
