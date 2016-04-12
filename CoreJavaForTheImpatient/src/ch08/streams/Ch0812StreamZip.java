package ch08.streams;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.stream.Stream;
import static java.util.stream.StreamSupport.*;

// 12. Write a method public static <T> Stream<T> zip(Stream<T>
// first, Stream<T> second) that alternates elements from the streams
// first and second (or null if the stream whose turn it is runs out of elements).

public class Ch0812StreamZip {

  public static <T> Stream<T> zip(Stream<T> first, Stream<T> second) {
    // results stream continues until both input streams stop
    Iterator<T> firstIterator = first.iterator();
    Iterator<T> secondIterator = second.iterator();
    // state of stat controls alternation
    final boolean[] stat = new boolean[]{true};

    Iterator<T> zipIterator = new Iterator<T>() {
      boolean next1;
      boolean next2; 
      @Override
      public boolean hasNext() { 
        next1 = firstIterator.hasNext();
        next2 = secondIterator.hasNext();
        return next1 || next2 ? true : false;
      }

      @Override
      public T next() {    
        if (next1 || next2) {
          if (stat[0]) {
            stat[0] = false;
            return next1 ? firstIterator.next() : null;
          } else {
            stat[0] = true;
            return next2 ? secondIterator.next() : null;
          }
        }
        return null; 
      }         
    };
    
    //for finite stream      
    Iterable<T> zipIterable = () -> zipIterator;
    return stream(zipIterable.spliterator(), false);
  }
  
  public static void main(String[] args) {

    Stream<Integer> s1 = Stream.of(1,2,3);
    Stream<Integer> s2 = Stream.of(4,5,6,7,8,9);
    Stream<Integer> z = zip(s1,s2);
    StringWriter u = new StringWriter();
    z.forEach(x -> u.write(x+",")); 
    System.out.println(u.toString().replaceFirst(".$","")+"\n");
    // 1,4,2,5,3,6,null,7,null,8,null,9
    
    s1 = Stream.of(4,5,6,7,8,9);
    s2 = Stream.of(1,2,3);
    z = zip(s1,s2);
    StringWriter v = new StringWriter();
    z.forEach(x -> v.write(x+",")); 
    System.out.println(v.toString().replaceFirst(".$","")+"\n");
    // 4,1,5,2,6,3,7,null,8,null,9
    
    s1 = Stream.of(1,3,5,7,9);
    s2 = Stream.of(2,4,6,8,10);
    z = zip(s1,s2);
    StringWriter w = new StringWriter();
    z.forEach(x -> w.write(x+",")); 
    System.out.println(w.toString().replaceFirst(".$",""));
    // 1,2,3,4,5,6,7,8,9,10
  }

}

