package ch03.interfaces.lambdas;

import java.util.Iterator;

// 4. Implement a static of method of the IntSequence class that yields a sequence
// with the arguments. For example, IntSequence.of(3, 1, 4, 1, 5, 9)
// yields a sequence with six values. Extra credit if you return an instance of an
// anonymous inner class.

public class Ch0304IntSequenceOf {

  public interface IntSequence {
    boolean hasNext();
    int next();
    static IntSequence of(int...seq) {
      return new IntSequence() {
        int i = -1;
        public int next() {
          i++;
          return seq[i];
        }
        public boolean hasNext() {
          return i < seq.length - 1 ? true : false;
        } 
      };
    }
  }

  public interface IntegerIterator extends Iterator<Integer> {
    boolean hasNext();
    Integer next();
    static IntegerIterator of(int...seq) {
      return new IntegerIterator() {
        int i = -1;
        public Integer next() {
          i++;
          return seq[i];
        }
        public boolean hasNext() {
          return i < seq.length - 1 ? true : false;
        } 
      };
    }
  }

  public interface IntegerIterable extends Iterable<Integer> {
    static IntegerIterable of(int...seq) {
      return new IntegerIterable() {     
        @Override
        public Iterator<Integer> iterator() {
          return IntegerIterator.of(seq);
        };
      };
    }
  }

  public static void main(String[] args) {

    IntSequence x = IntSequence.of(1,2,3,4,5);
    while (x.hasNext()) {
      System.out.print(x.next()+", "); // 1, 2, 3, 4, 5, 
    }

    System.out.println();

    IntegerIterator y = IntegerIterator.of(1,2,3,4,5);
    while (y.hasNext()) {
      System.out.print(y.next()+", "); // 1, 2, 3, 4, 5, 
    }

    System.out.println();

    IntegerIterable z = IntegerIterable.of(1,2,3,4,5);
    for (int i : z) System.out.print(i+", "); // 1, 2, 3, 4, 5, 

  }

}
