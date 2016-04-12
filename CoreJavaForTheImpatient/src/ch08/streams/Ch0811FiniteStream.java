package ch08.streams;

import java.util.stream.Stream;

//11. Your manager asks you to write a method public static <T> boolean
//isFinite(Stream<T> stream). Why isn’t that such a good idea? Go ahead
//and write it anyway.

// It's not a good idea since such a method can   
// only return true or not return at all.

public class Ch0811FiniteStream {
    
  public static <T >boolean isFiniteStream(Stream<T> stream) {
    stream.forEach(x -> System.out.print(""));
    return true;
  }

  public static void main(String[] args) {
    System.out.println(isFiniteStream(Stream.iterate(1, x -> 1)));
  }

}
