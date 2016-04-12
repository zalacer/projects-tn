package ch03.interfaces.lambdas;

import java.util.Arrays;
import java.util.Iterator;

// 7. In this exercise, you will try out what happens when a method is added to an
// interface. In Java 7, implement a class DigitSequence that implements
// Iterator<Integer>, not IntSequence. Provide methods hasNext, next,
// and a do-nothing remove. Write a program that prints the elements of an instance.
// In Java 8, the Iterator class gained another method, forEachRemaining.
// Does your code still compile when you switch to Java 8? If you put your Java 7 class
// in a JAR file and don’t recompile, does it work in Java 8? What if you call the
// forEachRemaining method? Also, the remove method has become a default
// method in Java 8, throwing an UnsupportedOperationException. What
// happens when remove is called on an instance of your class?

// The code compiles with java 7 and 8. The runnable jar runs with java 7 and 8 without 
// recompilation. forEachRemaining() works when run with java 8, which means it's also
// compiled with java 8 since this method does not exist in java 7 Iterator. Execution 
// of remove() in java 8 did nothing because it runs my no-op remove() as verified by 
// enabling it to print "myremove" when executed.

public class DigitSequence implements Iterator<Integer> {

  public static void main(String[] args) {
    DigitSequence d = new DigitSequence(new int[] {1,2,3,4,5});
    System.out.println(d); // DigitSequence(a=[1, 2, 3, 4, 5], i=-1)

    while(d.hasNext()) 
      System.out.print(d.next()+", "); // 1, 2, 3, 4, 5,
    System.out.println(); 

    System.out.println(d); // DigitSequence(a=[1, 2, 3, 4, 5], i=4)

    d.remove(); // myremove
    System.out.println(d); // DigitSequence(a=[1, 2, 3, 4, 5], i=4)

    System.out.println();
    DigitSequence e= new DigitSequence(new int[] {11,12,13,14,15});
    System.out.println(e);  // DigitSequence(a=[11, 12, 13, 14, 15], i=-1)
    int i = e.next();       
    System.out.println(e);  // DigitSequence(a=[11, 12, 13, 14, 15], i=0)
    System.out.println("i = "+i); // i = 11
    e.remove(); // myremove
    System.out.println(e);  // DigitSequence(a=[11, 12, 13, 14, 15], i=0)
    e.forEachRemaining(x -> System.out.print(x+", ")); // 12, 13, 14, 15,
  }

  private int[] a = new int[0];
  private int i = -1;

  DigitSequence(int[] a) {
    this.a = a;
  }

  public Integer next() {
    i++;
    return a[i];
  }

  public boolean hasNext() {
    return i < a.length - 1 ? true : false;
  }

  public void remove() {
    System.out.println("myremove");
  }

  /**
   * @return the a
   */
  public int[] getA() {
    return a;
  }

  public void setA(int[] a) {
    this.a = a;
  }

  public int getI() {
    return i;
  }

  public void setI(int i) {
    this.i = i;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(a);
    result = prime * result + i;
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DigitSequence other = (DigitSequence) obj;
    if (!Arrays.equals(a, other.a))
      return false;
    if (i != other.i)
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DigitSequence(a=");
    builder.append(Arrays.toString(a));
    builder.append(", i=");
    builder.append(i);
    builder.append(")");
    return builder.toString();
  };

}

//  output with Java 8 at compiler compliance level 1.8: 
//  DigitSequence(a=[1, 2, 3, 4, 5], i=-1)
//  1, 2, 3, 4, 5, 
//  DigitSequence(a=[1, 2, 3, 4, 5], i=4)
//  myremove
//  DigitSequence(a=[1, 2, 3, 4, 5], i=4)
//  
//  DigitSequence(a=[11, 12, 13, 14, 15], i=-1)
//  DigitSequence(a=[11, 12, 13, 14, 15], i=0)
//  i = 11
//  myremove
//  DigitSequence(a=[11, 12, 13, 14, 15], i=0)
//  12, 13, 14, 15, 

// same output with Java 8 at compiler compliance level 1.7 after replacing 
//     e.forEachRemaining(x -> System.out.print(x+", "));
// with 
//     while(e.hasNext()) System.out.print(e.next()+", "); :
//
//  DigitSequence(a=[1, 2, 3, 4, 5], i=-1)
//  1, 2, 3, 4, 5, 
//  DigitSequence(a=[1, 2, 3, 4, 5], i=4)
//  myremove
//  DigitSequence(a=[1, 2, 3, 4, 5], i=4)
//  
//  DigitSequence(a=[11, 12, 13, 14, 15], i=-1)
//  DigitSequence(a=[11, 12, 13, 14, 15], i=0)
//  i = 11
//  myremove
//  DigitSequence(a=[11, 12, 13, 14, 15], i=0)
//  12, 13, 14, 15, 

