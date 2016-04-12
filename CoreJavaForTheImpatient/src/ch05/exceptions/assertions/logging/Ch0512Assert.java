package ch05.exceptions.assertions.logging;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Random;

// 12. Write a method int min(int[] values) that, just before returning the
// smallest value, asserts that it is indeed ≤ all values in the array. Use a private helper
// method or, if you already peeked into Chapter 8, Stream.allMatch. Call the
// method repeatedly on a large array and measure the runtime with assertions enabled,
// disabled, and removed.

// conclusion: disabling assertions but leaving one in code can degrade performance
// by about 4.2%. This could pile up for many assertions resulting in excessive 
// degredation. For best performance assertions should be removed in code for production.

public class Ch0512Assert {

  public static int min(int[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("values must contain at least one element");

    OptionalInt m = Arrays.stream(values).min();
    int mi = m.getAsInt();
    assert Arrays.stream(values).allMatch(x -> x >= mi);  // expensive verification
    return mi;      
  }

  public static void main(String[] args) {
    int[] a =  (new Random()).ints().limit(10000000).toArray();
    long start = System.nanoTime();
    int x = min(a);    
    long stop = System.nanoTime();
    double elapsed = (stop - start)*1E-9;
    System.out.println(elapsed);                                 // multiple of baseline
    // 0.09314141100000001 with assertions enabled and in place + 4.2287231006957951084394073430387
    // 0.022994194000000003 with assertions disabled but not removed  + 1.0439618458182971906476445111538
    // 0.022148606 with assertions enabled but removed + 1.0055712151537996526361357847207 
    // 0.022025895 with assertions disabled and removed = baseline == 1.0

    System.out.println(x); // -2147483010
  }

}
