package ch03.interfaces.lambdas;

// 8. Implement a class Greeter that implements Runnable and whose run method
// prints n copies of "Hello, " + target, where n and target are set in the
// constructor. Construct two instances with different messages and execute them
// concurrently in two threads.

public class Ch0308Test {

  public static void main(String[] args) {

    Ch0308RunnableGreeter x = new Ch0308RunnableGreeter(5, "x");
    Ch0308RunnableGreeter y = new Ch0308RunnableGreeter(7, "y");

    new Thread(x).start();
    new Thread(y).start();

    //      first run
    //      Hello, x
    //      Hello, y
    //      Hello, x
    //      Hello, x
    //      Hello, x
    //      Hello, x
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //
    //      second run
    //      Hello, x
    //      Hello, x
    //      Hello, x
    //      Hello, x
    //      Hello, x
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y
    //      Hello, y



  }

}
