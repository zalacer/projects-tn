package ch03.interfaces.lambdas;

// 9.Implement methods
// public static void runTogether(Runnable… tasks)
// public static void runInOrder(Runnable… tasks)
// The first method should run each task in a separate thread and then return. The
// second method should run all methods in the current thread and return when the last
// one has completed.

public class Ch0309runTogether {

  public static void runTogether(Runnable...tasks) {
    for (Runnable r : tasks) 
      new Thread(r).start();
  }

  public static void runInOrder(Runnable...tasks) {
    for (Runnable r : tasks)
      r.run();
  }


  public static void main(String[] args) {

    Ch0308RunnableGreeter x = new Ch0308RunnableGreeter(3, "x");
    Ch0308RunnableGreeter y = new Ch0308RunnableGreeter(5, "y");
    Ch0308RunnableGreeter z = new Ch0308RunnableGreeter(7, "z");

    //        runInOrder(x,y,z);
    //        Hello, x
    //        Hello, x
    //        Hello, x
    //        Hello, y
    //        Hello, y
    //        Hello, y
    //        Hello, y
    //        Hello, y
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z

    System.out.println();

    runTogether(x,y,z);
    //        Hello, x
    //        Hello, x
    //        Hello, x
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, z
    //        Hello, y
    //        Hello, y
    //        Hello, y
    //        Hello, y
    //        Hello, y

  }

}
