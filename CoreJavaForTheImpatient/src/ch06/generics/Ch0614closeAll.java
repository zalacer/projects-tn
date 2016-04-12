package ch06.generics;

import static utils.StackTraceUtils.shortenedStackTrace;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import utils.ChainedException;

// 14. Implement an improved version of the closeAll method in Section 6.3, “Type
// Bounds,” on p. 202. Close all elements even if some of them throw an exception. In
// that case, throw an exception afterwards. If two or more calls throw an exception,
// chain them together.

public class Ch0614closeAll {

  // from text p205
  public static <T extends AutoCloseable> void closeAll(ArrayList<T> elems) 
      throws Exception {
    for (T elem : elems)
      elem.close();
  }

  // improved using ChainedException
  public static <T extends AutoCloseable> void closeAll2(ArrayList<T> elems) 
      throws ChainedException {
    ChainedException ce = null;
    int c = 0;
    for (T elem : elems)
      try {
        elem.close();
      } catch (Exception e) {
        c++;
        if (ce == null) {
          ce = new ChainedException(shortenedStackTrace(e, 1), e);
        } else {
          ce = new ChainedException(shortenedStackTrace(ce, c), e);
        }
      }
    if (ce != null)
      throw ce;
  }

  // better approach with StringBuilder
  public static <T extends AutoCloseable> void closeAll3(ArrayList<T> elems) 
      throws ChainedException {
    StringBuilder b = new StringBuilder();
    Exception ex = null;
    for (T elem : elems) {
      try {
        elem.close();
      } catch (Exception e) {
        b.append(shortenedStackTrace(e, 1));
        ex = e;
      }
    }
    String bstring = b.toString();
    if (bstring.length() > 0) {
      if (ex != null) {
        throw new ChainedException(bstring, ex);
      } else {
        throw new ChainedException(bstring);
      }
    }
  }

  public static Closeable getCloseable(int i) {
    return new Closeable(){ 
      public void close() throws IOException {
        System.err.println(i);
        throw new IOException(""+i);
      };
    };
  }

  public static void main(String[] args) throws Exception {

    ArrayList<Closeable> al = new ArrayList<>();
    al.add(getCloseable(1)); al.add(getCloseable(2)); al.add(getCloseable(3));
    closeAll3(al);
    //        1
    //        2
    //        3
    //        Exception in thread "main" ch06.ChainedException: 
    //            java.io.IOException: 1
    //            java.io.IOException: 2
    //            java.io.IOException: 3
    //
    //            at ch06.Ch0614closeAll.closeAll3(Ch0614closeAll.java:57)
    //            at ch06.Ch0614closeAll.main(Ch0614closeAll.java:77)
    //        Caused by: java.io.IOException: 3
    //            at ch06.Ch0614closeAll$1.close(Ch0614closeAll.java:68)
    //            at ch06.Ch0614closeAll.closeAll3(Ch0614closeAll.java:48)
    //            ... 1 more

    System.out.println();

    closeAll2(al);
    //        1
    //        2
    //        3
    //        Exception in thread "main" 
    //            ch06.ChainedException: 
    //            ch06.ChainedException: 
    //            ch06.ChainedException: 
    //            java.io.IOException: 1
    //
    //            at ch06.Ch0614closeAll.closeAll2(Ch0614closeAll.java:35)
    //            at ch06.Ch0614closeAll.main(Ch0614closeAll.java:97)
    //        Caused by: java.io.IOException: 3
    //            at ch06.Ch0614closeAll$1.close(Ch0614closeAll.java:68)
    //            at ch06.Ch0614closeAll.closeAll2(Ch0614closeAll.java:29)
    //            ... 1 more   

    System.out.println();

    closeAll(al);
    //        1
    //        Exception in thread "main" java.io.IOException: 1
    //            at ch06.Ch0614closeAll$1.close(Ch0614closeAll.java:68)
    //            at ch06.Ch0614closeAll.closeAll(Ch0614closeAll.java:21)
    //            at ch06.Ch0614closeAll.main(Ch0614closeAll.java:114)

  }

}
