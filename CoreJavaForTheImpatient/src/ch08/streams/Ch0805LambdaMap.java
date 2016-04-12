package ch08.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.IntStream;

//5. The letters method in Section 8.3, “The filter, map, and flatMap
//Methods,” on p. 252 was a bit clumsy, first filling an array list and then turning it
//into a stream. Write a stream-based one-liner instead. Map a stream of int values
//from 0 to s.length() - 1 with an appropriate lambda expression.

public class Ch0805LambdaMap {

  // from text section 8.3 p251
  public static Stream<String> letters(String s) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < s.length(); i++)
      result.add(s.substring(i, i + 1));
    return result.stream();
  }

  // equivalently
  public static Stream<String> letters2(String s) {
    return IntStream.range(0, s.length()).mapToObj(x -> s.substring(x, x + 1));
  }


  public static void main(String[] args) {

    letters("hello").forEach(x -> System.out.print(x+", "));
    // h, e, l, l, o, 

    System.out.println();

    letters2("hello").forEach(x -> System.out.print(x+", "));
    // h, e, l, l, o, 
  }

}
