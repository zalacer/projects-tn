package ch01.fundamentals;

import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// 13. Write a program that prints a lottery combination, picking six distinct numbers
// between 1 and 49. To pick six distinct numbers, start with an array list filled with 1
// … 49. Pick a random index and remove the element. Repeat six times. Print the
// result in sorted order.

public class Ch0113Random {

  public static void main(String[] args) {

    List<Integer> a = IntStream.range(1,50).boxed().collect(Collectors.toList());
    TreeSet<Integer> result = new TreeSet<>();
    Random r = new Random(521);
    IntStream.range(0, 6).forEach(x -> result.add(a.remove(r.nextInt(a.size()))));
    System.out.println(result); // [2, 3, 9, 19, 30, 46]

  }

}
