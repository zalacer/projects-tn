package ch06.generics;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

// 22. Improve the method public static <V, T> V doWork(Callable<V> c, T ex) throws T 
// of Section 6.6.7, “Exceptions and Generics,” on p. 217 so
// that one doesn’t have to pass an exception object, which may never get used.
// Instead, accept a constructor reference for the exception class.

public class Ch0622Callable {

  // from the text p221: (added "extends Exception" to T to resolve errors)
  public static <V, T extends Exception> V doWork(Callable<V> c, T ex) throws T {
    try {
      return c.call();
    } catch (Throwable realEx) {
      ex.initCause(realEx);
      throw ex;
    }
  }

  public static <V> V doWork2(Callable<V> c, Supplier<Exception> s) throws Exception {
    Exception ex = s.get();
    try {
      return c.call();
    } catch (Throwable realEx) {
      ex.initCause(realEx);
      throw ex;
    }
  }

  public static void main(String[] args) {

    Callable<String> anonCallable = new Callable<String>() {
      public String call() {
        try {
          throw new IOException();
        } catch (Throwable ex) {
          throw new RuntimeException(ex);
        }
      }
    };

    Callable<String> lambdaCallable = () -> {throw new IOException();};

    try {
      doWork2(anonCallable, InterruptedIOException::new);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //  java.io.InterruptedIOException
    //      at ch06.Ch0622doWork.doWork2(Ch0622doWork.java:26)
    //      at ch06.Ch0622doWork.main(Ch0622doWork.java:51)
    //  Caused by: java.lang.RuntimeException: java.io.IOException
    //      at ch06.Ch0622doWork$1.call(Ch0622doWork.java:43)
    //      at ch06.Ch0622doWork$1.call(Ch0622doWork.java:1)
    //      at ch06.Ch0622doWork.doWork2(Ch0622doWork.java:28)
    //      ... 1 more
    //  Caused by: java.io.IOException
    //      at ch06.Ch0622doWork$1.call(Ch0622doWork.java:40)
    //      .. 3 more

    try {
      doWork2(lambdaCallable, InterruptedIOException::new);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //  java.io.InterruptedIOException
    //      at ch06.Ch0622doWork.doWork2(Ch0622doWork.java:26)
    //      at ch06.Ch0622doWork.main(Ch0622doWork.java:58)
    //  Caused by: java.io.IOException
    //      at ch06.Ch0622doWork.lambda$0(Ch0622doWork.java:49)
    //      at ch06.Ch0622doWork.doWork2(Ch0622doWork.java:28)
    //      ... 1 more
    
  }
}

