package ch10.concurrency;

import java.util.Random;
import java.util.concurrent.atomic.LongAccumulator;

// 9. Use a LongAccumulator to compute the maximum or minimum of the
// accumulated elements.

public class Ch1009LongAccumulator {

  private static final LongAccumulator max = 
      new LongAccumulator(Long::max, Long.MIN_VALUE);
  
  private static final LongAccumulator min = 
      new LongAccumulator(Long::min, Long.MAX_VALUE);

  public static void main(String[] args) {

    Random r = new Random(5779);
    r.longs(1000000).parallel().forEach(x -> {max.accumulate(x); min.accumulate(x);});
    System.out.println("max = "+max.longValue()); // max = 9223294315420457382
    System.out.println("min = "+min.longValue()); // min = -9223348691663641236

  }

}
