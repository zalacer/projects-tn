package ch10.concurrency;

//22. What is wrong with this code snippet?
//  public class Stack {
//      public void push(Object newValue) {
//          synchronized (new ReentrantLock()) {
//              ...
//          }
//      }
//      ...
//  }

// It doesn't make sense to synchronize on a ReentrantLock object
// in a  synchronized statement since ReentrantLock itself has a 
// synchronization  mechanism that's more flexible than synchronized 
// statements and blocks.  Synchronizing on a new Reentrant lock
// just uses it as an object for a private lock so may as well 
// synchronize on a new Object.

public class Ch1022SynchronizedReentrantLock {

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
