package ch08.streams;

import java.io.StringWriter;
import java.util.Objects;
import java.util.stream.Stream;

//17. How can you eliminate adjacent duplicates from a stream? Would your method
//work if the stream was parallel?

// An external array can be used to store the last stream value and then compared with
// the current one.  If they are the same, the current value is a duplicate and can
// be discarded by a filter that does the comparison.

// My method won't work for a parallel stream since it explicitly depends on the order
// of stream values which can be broken by the parallelization process of splitting the 
// intial stream into multiple streams.

public class Ch0817AdjacentDuplicates {

  // this method will not work for parallel streams

  public static <T> Stream<T> adjacentDeDup(Stream<T> s) {
    Object[] previous = new Object[]{null};
    Stream<T> r = s.filter(x -> {
      @SuppressWarnings("unchecked")
      boolean isDuplicate = Objects.nonNull(x) && x.equals((T) previous[0])
          || Objects.isNull(x) && Objects.isNull(previous[0]);
      if (! isDuplicate) previous[0] = x;
      return ! isDuplicate; // discard duplicates
    });
    return r;
  }

  public static void main(String[] args) {

    Stream<String> s1 = Stream.of("one","one","two","two","three","three");
    StringWriter u = new StringWriter();
    adjacentDeDup(s1).forEach(x -> u.write(x+","));
    System.out.println(u.toString().replaceFirst(".$","")+"\n");
    // one,two,three

    Stream<String> s2 = Stream.of("one","two","three","one","two","three");
    StringWriter v = new StringWriter();
    adjacentDeDup(s2).forEach(x -> v.write(x+","));
    System.out.println(v.toString().replaceFirst(".$","")+"\n");
    // one,two,three,one,two,three

    Stream<String> s3 = Stream.of("one","two", null, null, null,"three");
    StringWriter w = new StringWriter();
    adjacentDeDup(s3).forEach(x -> w.write(x+","));
    System.out.println(w.toString().replaceFirst(".$","")+"\n");
    // one,two,null,three
  }

}
