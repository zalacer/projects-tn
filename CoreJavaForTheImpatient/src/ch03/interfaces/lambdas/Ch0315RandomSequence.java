package ch03.interfaces.lambdas;

import java.util.Random;

import ch03.interfaces.lambdas.Ch0304IntSequenceOf.IntSequence;

// 15. Implement the RandomSequence in Section 3.9.1, “Local Classes,” on p. 122 as
// a nested class, outside the randomInts method.

public class Ch0315RandomSequence {

  private static Random generator = new Random();

  public static class RandomSequence implements IntSequence {
    private int low = 0;
    private int high = 0;

    RandomSequence(int low, int high) {
      this.low = low;
      this.high = high;
    }

    public int next() {
      return low + generator.nextInt(high - low + 1);
    }

    public boolean hasNext() {
      return true;
    }
  }

  public static IntSequence randomInts(int low, int high) {
    return new RandomSequence(low, high);
  }

  public static void main(String[] args) {

    IntSequence seq2 = randomInts(1,100);
    for(int i = 0; i < 10; i++) 
      System.out.print(seq2.next()+", "); 
    // 31, 99, 2, 59, 61, 6, 57, 18, 65, 57, 


  }

}
