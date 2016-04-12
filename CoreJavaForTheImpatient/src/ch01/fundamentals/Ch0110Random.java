package ch01.fundamentals;

import java.util.Random;

// 10. Write a program that produces a random string of letters and digits by generating a
// random long value and printing it in base 36.

public class Ch0110Random {

  public static void main(String[] args) {

    Random r = new Random(493837221);
    String s = Long.toString(r.nextLong(), 36);
    System.out.println(s); // 1h4kr6i84ivts

  }

}
