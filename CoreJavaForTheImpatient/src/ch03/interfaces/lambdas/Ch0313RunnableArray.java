package ch03.interfaces.lambdas;

// 13. Write a method that takes an array of Runnable instances and returns a
// Runnable whose run method executes them in order. Return a lambda
// expression.

public class Ch0313RunnableArray {

  public static Runnable runInOrder2(Runnable...r) {
    return new Runnable() { 
      public void run() {
        for (Runnable e : r)
          new Thread(e).start();
      }           
    };    
  }

  public static Runnable runInOrderWithLambda(Runnable...r) {
    return ( () -> { for (Runnable e : r) e.run();});
  }

  public static void main(String[] args) {

    Ch0308RunnableGreeter x = new Ch0308RunnableGreeter(3, "x");
    Ch0308RunnableGreeter y = new Ch0308RunnableGreeter(5, "y");
    Ch0308RunnableGreeter z = new Ch0308RunnableGreeter(7, "z");
    Runnable[] r = new Runnable[] { x, y, z };

    new Thread(runInOrderWithLambda(r)).start();
 
    //  Hello, x
    //  Hello, x
    //  Hello, x
    //  Hello, y
    //  Hello, y
    //  Hello, y
    //  Hello, y
    //  Hello, y
    //  Hello, z
    //  Hello, z
    //  Hello, z
    //  Hello, z
    //  Hello, z
    //  Hello, z
    //  Hello, z

  }

}
