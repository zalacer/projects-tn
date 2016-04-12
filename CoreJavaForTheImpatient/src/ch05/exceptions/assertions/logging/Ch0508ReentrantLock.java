package ch05.exceptions.assertions.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 8. Design a helper method so that one can use a ReentrantLock in a try-with-
// resources statement. Call lock and return an AutoCloseable whose close
// method calls unlock and throws no exceptions.

public class Ch0508ReentrantLock {

  public static Lock rl = new ReentrantLock();

  public static AutoCloseable ac(Lock rl) {
    rl.lock();
    return () -> rl.unlock();
  }

  public static int count;

  public static void testIntUpdateWithAutoCloseableLock()  {
    ExecutorService executor = Executors.newCachedThreadPool();
    for (int i = 1; i <= 100; i++) {
      Runnable task = () -> {
        for (int k = 1; k <= 1000; k++) {
          try (AutoCloseable a = ac(rl);) { 
            count++;           
          } catch (Exception e) {
            e.printStackTrace();
          }
        }                
      };
      executor.execute(task);
    }
    executor.shutdown();
    try {
      executor.awaitTermination(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Final value: " + count);
    assert count == 100000;
  }


  public static void main(String[] args) throws InterruptedException {

    testIntUpdateWithAutoCloseableLock(); // Final value: 100000

  }

}
