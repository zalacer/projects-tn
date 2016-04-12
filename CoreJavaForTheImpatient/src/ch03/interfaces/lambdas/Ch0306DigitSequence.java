package ch03.interfaces.lambdas;

// 6. In this exercise, you will try out what happens when a method is added to an
// interface. In Java 7, implement a class DigitSequence that implements
// Iterator<Integer>, not IntSequence. Provide methods hasNext, next,
// and a do-nothing remove. Write a program that prints the elements of an instance.
// In Java 8, the Iterator class gained another method, forEachRemaining.
// Does your code still compile when you switch to Java 8? If you put your Java 7 class
// in a JAR file and don’t recompile, does it work in Java 8? What if you call the
// forEachRemaining method? Also, the remove method has become a default
// method in Java 8, throwing an UnsupportedOperationException. What
// happens when remove is called on an instance of your class?

// For the code used see DigitSequence.java in this package.  This code was compiled in a
// separate project with java 7 and with remove() configured as a no-op method.
// The code also compiles with java 8. The runnable jar runs with java 7 and 8 without 
// recompilation. forEachRemaining() works when run with java 8, of course this means 
// it's also  compiled with java 8 since this method does not exist in java 7 Iterator.
// Execution of remove() in java 8 did nothing because it runs my no-op remove() as
// verified by enabling it to print "myremove" when executed.

public class Ch0306DigitSequence {

}
