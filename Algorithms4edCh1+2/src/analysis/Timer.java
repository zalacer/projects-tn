package analysis;

import java.time.Duration;
import java.time.Instant;

public class Timer {
  private Instant start;
  
  public Timer(){
    start = Instant.now();
  }
  
//  public static void start() {
//    start = Instant.now();
//  }
  
  public void reset() {
    start = Instant.now();
  }
  
  public long elapsed() {
    return Duration.between(start, Instant.now()).toMillis();
  }
  
  public void stop() {
    System.out.println(Duration.between(start, Instant.now()).toMillis()+" ms");
  }
  
  public long finish() {
    return Duration.between(start, Instant.now()).toMillis();
  }
  
  public long num() {
   return Duration.between(start, Instant.now()).toMillis();
  }
  
  public static void main(String[] args) {

//    private final long start;
//    public Stopwatch()
//    { start = System.currentTimeMillis(); }
//    public double elapsedTime()
//    {
//    long now = System.currentTimeMillis();
//    return (now - start) / 1000.0;
//    }

  }

}
