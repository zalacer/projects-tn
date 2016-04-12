package ch08.streams;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//3. Suppose you have an array int[] values = { 1, 4, 9, 16 }. What is
//Stream.of(values)? How do you get a stream of int instead?

// Stream.of(values) is a stream of array(s). Arrays.stream(values) gives a
// stream of int as an IntStream.

public class Ch0803ArraysStream {

  @SuppressWarnings("unused")
  public static void main(String[] args) {

    int[] values = { 1, 4, 9, 16 };
    Stream<int[]> streamOfArrays = Stream.of(values);
    IntStream streamOfInt = Arrays.stream(values);

  }

}
