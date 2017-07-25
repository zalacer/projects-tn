package u;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

import u.PrimitiveIterator.OfChar;

//import java.util.stream.Stream;
import java.util.PrimitiveIterator.OfInt;


public class Tests {
  
  @FunctionalInterface
  private static interface Con1<T> extends Consumer<T>{
    void accept(T t);
  }
  
  @FunctionalInterface
  public interface CharConsumer {
    void accept(char value);
    default CharConsumer andThen(CharConsumer after) {
      Objects.requireNonNull(after);
      return (char t) -> {accept(t); after.accept(t);};
    }
  }
  
  public static interface Greet {
    public String greet(String x);
  }
  
  public static String HelloWorld() {
    Greet greeter = new Greet(){
      public String greet(String x) {
        return x;
       }
    };
    return greeter.greet("hello");
  }
  
  public static <T> Iterable<T> toIterable2(T[] a) {
    Iterator<T> it = new Iterator<T>() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public T next() {return a[c++];}
    };
    Iterable<T> itbl = new Iterable<T>() {
      public Iterator<T> iterator() {return it;}
    };
    return itbl;
  }
  
  public static <T> Iterable<T> toIterable(T[] a) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return Arrays.stream(a).iterator();
        //return new Iterator<T>() {
        //  int len = a.length;
        //  int c = 0;
        //  public boolean hasNext() {return c < len;}
        //  public T next() {return a[c++];}
        //};
      }
    };
  }
  
  public static Iterable<Integer> toIterable(int[] a) {
    return new Iterable<Integer>() {
      public OfInt iterator() {
        return new OfInt() {
          int len = a.length;
          int c = 0;
          public boolean hasNext() {return c < len;}
          public int nextInt() {return a[c++];}
        };
      }
    };
  }
  
  public static Iterable<Integer> toIterable3(int[] a) {
    return new Iterable<Integer>() {
      public OfInt iterator() {
        return Arrays.stream(a).iterator();
      }
    };
  }
  
  public static Iterable<Character> toIterable(char[] a) {
    return new Iterable<Character>() {
      public OfChar iterator() {
        return new OfChar() {
          int len = a.length;
          int c = 0;
          public boolean hasNext() {return c < len;}
          public char nextChar() {return a[c++];}
//          public char nextChar() {
//            if (hasNext()) {
//              return a[c++];
//            } else {
//              throw new NoSuchElementException();
//            }
//          }
//          @Override
//          public void forEachRemaining(CharConsumer action) {
//            if (action == null) throw new NullPointerException();
//            if (c >= 0 && c < a.length) {
//                do { action.accept(a[c]); } while (++c < a.length);
//            }
//          }
        };
      }
    };
  }

  public static void main(String[] args) {
    
    char[] c1 = {'a','b','c'};
    Iterable<?> itc1 = toIterable(c1);
    OfChar itc = (OfChar) itc1.iterator();
//    Stream<Character> x = Stream.generate(() -> itc.nextChar()).limit(c1.length);
//    System.out.println(itc.nextChar().getClass().getName());
    while (itc.hasNext()) System.out.print(itc.next()+","); System.out.println();
    
    int[] a1 = {1,2,3,4,5};
    Iterable<?> ital = toIterable(a1);
    OfInt ita = (OfInt) ital.iterator();
//    System.out.println(ita.nextInt().getClass().getName();
    while (ita.hasNext()) System.out.print(ita.nextInt()+","); System.out.println();
    
    Integer[] ia1 = {1,2,3};
    Iterable<Integer> itbl = toIterable(ia1);
    Iterator<Integer> it = itbl.iterator();
    while (it.hasNext()) System.out.print(it.next()+","); System.out.println();
    
    

  }

}
