package ch05.exceptions.assertions.logging;

import static utils.StackTraceUtils.getStackTrace;

// 10. Write a recursive factorial method in which you print all stack frames before
// you return the value. Construct (but don’t throw) an exception object of any kind
// and get its stack trace, as described in Section 5.1.8, “The Stack Trace,” on p. 184.

// This works just as well by using a Throwable object.

public class Ch0510RecursiveFactorial {

  private static int c = 0;
  
  private static String getPackageName() {
    String cn = Ch0510RecursiveFactorial.class.getName();
    return cn.substring(0, cn.lastIndexOf('.')+1);
  }

  private static long recursiveFactorial(int n) {
    System.out.println("stack trace "+(++c)+":\n"
        + getStackTrace(new Exception()).replaceAll(getPackageName(), ""));
    if (n < 0)
      throw new IllegalArgumentException("factorial undefined for numbers less than zero");
    if (n <= 1)
      return 1L;
    return (long) n * recursiveFactorial(n - 1);
  }
 
  public static void main(String[] args) {

    long x = recursiveFactorial(5); // above 20 long overflow to negative result
    System.out.println("result = "+x+"\n");
        
    //    stack trace 1:
    //    java.lang.Exception
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:20)
    //      at Ch0510RecursiveFactorial.main(Ch0510RecursiveFactorial.java:30)
    //
    //    stack trace 2:
    //    java.lang.Exception
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:20)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.main(Ch0510RecursiveFactorial.java:30)
    //
    //    stack trace 3:
    //    java.lang.Exception
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:20)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.main(Ch0510RecursiveFactorial.java:30)
    //
    //    stack trace 4:
    //    java.lang.Exception
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:20)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.main(Ch0510RecursiveFactorial.java:30)
    //
    //    stack trace 5:
    //    java.lang.Exception
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:20)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.recursiveFactorial(Ch0510RecursiveFactorial.java:25)
    //      at Ch0510RecursiveFactorial.main(Ch0510RecursiveFactorial.java:30)
    //
    //    result = 120






  }

}
