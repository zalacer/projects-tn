package ch03.interfaces.lambdas;

// 8. Implement a class Greeter that implements Runnable and whose run method
// prints n copies of "Hello, " + target, where n and target are set in the
// constructor. Construct two instances with different messages and execute them
// concurrently in two threads.

// see Ch0308Test.java for the test class for this class

public class Ch0308RunnableGreeter implements Runnable {

  private int n = 0;
  private String target = "moe";

  Ch0308RunnableGreeter(int n, String target) {
    this.n = n;
    this.target = target;
  }

  @Override
  public void run() {
    for (int i  = 0; i < n; i++) {
      System.out.println("Hello, " + target);
    }

  }

}
