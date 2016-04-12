package horstmann.ch06.sec06.exceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.lang.reflect.ParameterizedType;

//ch 6 23. In the cautionary note at the end of Section 6.6.7, “Exceptions and Generics,” on p.
//217, the throwAs helper method is used to “cast” ex into a
//RuntimeException and rethrow it. Why can’t you use a regular cast, i.e. throw
//(RuntimeException) ex?

// Actually in my test, throwAs didn't cast ex to RuntimeException and readAll threw a 
// java.nio.file.NoSuchFileException. Evidence for this is that getClass().getCanonicalName()) 
// reported java.nio.file.NoSuchFileException for it in the catch block. I also examined all the
// StackTraceElements and found no trace of a RunTimeException or any subclass of it, and 
// determined there were no suppressed exceptions since e.getSuppressed().length == 0. 
// Oddly the cast attempt didn't throw a ClassCastException, which is a bug. Maybe somehow 
// Eclipse interfered with this.
//
// According to my testing a NoSuchFileException cannot be cast to RuntimeException and 
// trying to do so should throw a java.lang.ClassCastException which is an (unchecked) 
// RuntimeException subclass. A ClassCastException happens when a cast of one class to 
// another is attempted when the first class is not a subclass of the second class which 
// is true for NoSuchFileException and RuntimeException as shown by inheritance trees:
//
//java.nio.file.NoSuchFileException inheritance tree: 
//java.lang.Object
//    java.lang.Throwable
//        java.lang.Exception
//            java.io.IOException
//                java.nio.file.FileSystemException
//                    java.nio.file.NoSuchFileException
//
//java.lang.RuntimeException inheritance tree:
//java.lang.Object
//    java.lang.Throwable
//        java.lang.Exception
//            java.lang.RuntimeException
//
// There is no way to get around this and the right way to do it is to rethrow the 
// original exception as the argument (cause) in the constructor of a new exception
// which is demonstrated with throwAs2, doWork2 and readAll2 and where throwAs2
// throws only a RunTimeException(e) where e is the argument passed to it.

public class Exceptions {
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void throwAs(Throwable e) throws T {
    System.out.println("from throwAs"); // printed
    throw (T) e; 
  }

  private static void throwAs2(Throwable e) {
    //System.out.println("from throwAs2"); // printed
    throw new RuntimeException(e); // 
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void throwAs3(Throwable e) throws T {
    System.out.println("from throwAs3"); // printed
    System.out.println(((ParameterizedType) e.getClass().getGenericSuperclass())
        .getActualTypeArguments()[0]); 
    // java.lang.ClassCastException: java.lang.Class cannot be cast to 
    // java.lang.reflect.ParameterizedType after exception
    throw (T) e;
  }

  public static <V> V doWork(Callable<V> c) {
    try {
      return c.call();
    } catch (Throwable ex) {
      System.out.println("from doWork catch"); //  printed
      Exceptions.<RuntimeException>throwAs(ex);
      return null;
    }
  }

  public static <V> V doWork2(Callable<V> c) {
    try {
      return c.call();
    } catch (Throwable ex) {
      // System.out.println("got here 1"); //  printed
      Exceptions.throwAs2(ex);
      return null;
    }
  }

  public static <V> V doWork3(Callable<V> c) throws IOException {
    try {
      return c.call();
    } catch (Throwable ex) {
      //System.out.println("got here 1"); //  printed
      Exceptions.<IOException>throwAs(ex);
      return null;
    }
  }

  public static <V> V doWork4(Callable<V> c) throws IOException {
    try {
      return c.call();
    } catch (Throwable ex) {
      //System.out.println("got here 1"); //  printed
      Exceptions.<IOException>throwAs3(ex);
      return null;
    }
  }

  public static String readAll(Path path) {
    return doWork(() -> new String(Files.readAllBytes(path))); 
  }

  public static String readAll2(Path path) {
    return doWork2(() -> new String(Files.readAllBytes(path))); 
  }

  public static String readAll3(Path path) throws IOException {
    return doWork3(() -> new String(Files.readAllBytes(path))); 
  }

  public static String readAll4(Path path) throws IOException {
    return doWork4(() -> new String(Files.readAllBytes(path))); 
  }
  public static void main(String[] args) {

    //        Exception e1 = (RuntimeException) new Exception("this1");
    //        try {
    //            throw e1;
    //        // throws java.lang.ClassCastException: java.lang.Exception cannot be cast to 
    //        // java.lang.RuntimeException
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }

    try {
      String result = readAll(Paths.get("/tmp/quuqux")); 
      System.out.println(result);
    } catch (Exception e) {
      Throwable[] yyy = e.getSuppressed();
      System.out.println("e.getSuppressed().length = "+yyy.length); // 0
      //            for (Throwable t : yyy) System.out.println(t.getMessage());
      //            StackTraceElement[] xxx = e.getStackTrace();
      //            for(StackTraceElement ste : xxx) System.out.println(ste.toString());
      e.printStackTrace();
      //System.out.println(e.getClass().getCanonicalName());
      //java.nio.file.NoSuchFileException
    }
    // throws checked java.nio.file.NoSuchFileException: \tmp\quuqux
    System.out.println("after exception"); 


    //        String result2 = readAll2(Paths.get("/tmp/quuqux"));
    //        // throws Exception in thread "main" java.lang.RuntimeException: 
    //        //     java.nio.file.NoSuchFileException: \tmp\quuqux
    //        System.out.println(result2);

    //        try {
    //            String result3 = readAll3(Paths.get("/tmp/quuqux"));
    //            System.out.println(result3);
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        System.out.println("after checked exception"); // printed

    //        at sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:79)
    //        at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:97)
    //        at sun.nio.fs.WindowsException.rethrowAsIOException(WindowsException.java:102)


    //        try {
    //            throw new RuntimeException();
    //        } catch (RuntimeException e) {
    //            e.printStackTrace();
    //        }
    //        System.out.println("after RuntimeException"); // printed



  }
}

