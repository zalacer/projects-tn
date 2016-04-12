package ch10.concurrency;

//23. What is wrong with this code snippet?
//  public class Stack {
//      private Object[] values = new Object[10];
//      private int size;
//      public void push(Object newValue) {
//          synchronized (values) {
//              if (size == values.length)
//                  values = Arrays.copyOf(values, 2 * size);
//              values[size] = newValue;
//              size++;
//          }
//      }
//      ...
//  }

// Two issues with the code are (1) synchronizing on an array only
// locks it but not its individal elements and (2) the values object
// created by Array.copyOf is a new object which is not synchronized. 
// A way to fix these issues is to wrap the array as the value of 
// a private field of an object declared final in which all methods 
// that access its array are synchronized on it and synchronize on 
// that object in the push method.


public class Ch1023ReplacingLockObjectNoNo {

  public static void main(String[] args) {
 
  }

}
