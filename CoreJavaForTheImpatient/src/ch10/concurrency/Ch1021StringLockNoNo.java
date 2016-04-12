package ch10.concurrency;

//21. What is wrong with this code snippet?
//    public class Stack {
//        private Object myLock = "LOCK";
//        public void push(Object newValue) {
//            synchronized (myLock) {
//                ...
//            }
//        }
//        ...
//    }

// The Java ® Language Specification Java SE 8 Edition 2015-02-13 (and 
// previous editions) says on pp36-37 that (identical) string literals 
// "always refer to the same instance of class String" (for a given JVM) 
// whether they are in the same or different class or package. "LOCK" is
// such a string literal. Using a string literal as a lock object is 
// therefore hazardous because it might be locked or unlocked from a 
// location distant from where it's used in the Stack class. A better 
// practice would be to create a new Object  for a lock such as: 
// private final Object lock1 = new Object();  as shown at  
// https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html.
// However, if a string must be used then it should be created by 
// concatenation at run time since that always results in a distinct instance 
// according to the JLS reference given above.


public class Ch1021StringLockNoNo {

  public static void main(String[] args) {

  }

}
