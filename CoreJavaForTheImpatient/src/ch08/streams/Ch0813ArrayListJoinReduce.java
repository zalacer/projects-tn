package ch08.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//13. Join all elements in a Stream<ArrayList<T>> to one ArrayList<T>. Show
//how to do this with each of the three forms of reduce.

public class Ch0813ArrayListJoinReduce {

  public static <T> ArrayList<T> arrayListJoin1(Stream<ArrayList<T>> s) {       
    ArrayList<T> i = new ArrayList<T>();
    ArrayList<T> r = s.reduce(i, (x, y)-> {x.addAll(y); return x;});
    return r;
  }

  public static <T> ArrayList<T> arrayListJoin2(Stream<ArrayList<T>> s) {       
    Optional<ArrayList<T>> r = s.reduce((x, y)-> {x.addAll(y); return x;});
    return r.orElse(new ArrayList<T>());
  }

  public static <T> ArrayList<T> arrayListJoin3(Stream<ArrayList<T>> s) {       
    ArrayList<T> i = new ArrayList<T>();
    ArrayList<T> r = s.reduce(i, (x, y)-> {x.addAll(y); return x;}, 
        (x, y)-> {x.addAll(y); return x;});
    return r;
  }

  // essentially same as arrayListJoin3
  public static <T> ArrayList<T> arrayListJoin4(Stream<ArrayList<T>> s) {       
    ArrayList<T> r = s.reduce(new ArrayList<>(),
        (x, y)->{ ArrayList<T> z = new ArrayList<>(x);z.addAll(y); return z;},
        (x, y)->{ ArrayList<T> z = new ArrayList<>(x);z.addAll(y); return z;});
    return r;
  }

  // just for interest, another way to get the same result but without reduce
  public static <T> ArrayList<T> arrayListJoin5(Stream<ArrayList<T>> s) {
    return new ArrayList<T>(s.flatMap(Collection::stream).collect(Collectors.toList()));     
  }

  public static void main(String[] args) {

    ArrayList<Integer> a1 = new ArrayList<>(Arrays.asList(1,2,3));
    ArrayList<Integer> a2 = new ArrayList<>(Arrays.asList(4,5,6));
    ArrayList<Integer> a3 = new ArrayList<>(Arrays.asList(7,8,9));

    Stream<ArrayList<Integer>> s1 = Stream.of(a1,a2,a3);
    ArrayList<Integer> r1 = arrayListJoin1(s1);
    System.out.println("\n\narrayListJoin1(s1):");
    System.out.println(r1.size()); // 9
    System.out.println(r1); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

    Stream<ArrayList<Integer>> s2 = Stream.of(a1,a2,a3);
    ArrayList<Integer> r2 = arrayListJoin2(s2);
    System.out.println("\n\narrayListJoin2(s2):");
    System.out.println(r2.size()); // 9
    System.out.println(r2); // [1, 2, 3, 4, 5, 6, 7, 8, 9]
    s2.close();

    // found that without redefining a1, a2, a3 or commenting out r2 definition
    // r3, r4 and r5 resulted in [1, 2, 3, 4, 5, 6, 7, 8, 9, 4, 5, 6, 7, 8, 9]
    // this seems to be related to use of Optional in arrayListJoin2
    a1 = new ArrayList<>(Arrays.asList(1,2,3));
    a2 = new ArrayList<>(Arrays.asList(4,5,6));
    a3 = new ArrayList<>(Arrays.asList(7,8,9));
    Stream<ArrayList<Integer>> s3 = Stream.of(a1,a2,a3);
    List<Integer> r3 = arrayListJoin3(s3);
    System.out.println("\n\narrayListJoin3(s3):");
    System.out.println(r3.size()); // 9
    System.out.println(r3); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

    Stream<ArrayList<Integer>> s4 = Stream.of(a1,a2,a3);
    List<Integer> r4 = arrayListJoin4(s4);
    System.out.println("\n\narrayListJoin4(s4):");
    System.out.println(r4.size()); // 9
    System.out.println(r4); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

    Stream<ArrayList<Integer>> s5 = Stream.of(a1,a2,a3);
    List<Integer> r5 = arrayListJoin5(s5);
    System.out.println("\n\narrayListJoin5(s5):");
    System.out.println(r5.size()); // 9
    System.out.println(r5); // [1, 2, 3, 4, 5, 6, 7, 8, 9]

  }

}
