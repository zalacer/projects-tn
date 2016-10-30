package ex14;

//  p209
//  1.4.13  Using the assumptions developed in the text, give the amount of memory need-
//  ed to represent an object of each of the following types:
//  a.  Accumulator
//  b.  Transaction
//  c.  FixedCapacityStackOfStrings with capacity C and N entries
//  d.  Point2D
//  e.  Interval1D
//  f.  Interval2D
//  g.  Double

//  a.  Accumulator
//      From p93 implementation it has a double and int instance variables that require
//      4+8 = 12, +16 for OO (object overhead) comes to 28 + 4 padding totals to 32.
//      Accumulator has several methods. Surely these take resources somewhere so that
//      there is something to be called when the time comes.The text only mentions what
//      happens when they are called (p204). According to 
//        https://stackoverflow.com/questions/8376953/how-are-instance-methods-stored
//      methods are stored in the singular class object, aren't copied for new instances.
//      Details are in the JVM specification and http://www.artima.com/insidejvm/ed2/jvmP.html
//      is another reference.

//  b.  Transaction
//      Using the implementation in Ex1213Transaction.java, a Transaction instance has a
//      double, a Date and a String variable. Memory for the double and Date are 8+32 = 40
//      (32 is given for date on p201). The string adds 40 (excluding the chars in its
//       array (p202) giving 80 which is already a multiple of 8 so the total is 80.

//  c.  FixedCapacityStackOfStrings with capacity C and N entries
//      Based on the FixedCapacityStackOfStrings implementation from p133, is has one
//      int instance variable for 4 and a String[] of length C N of which are filled
//      which amounts to 24 (array overhead) + C*8 (references including null references) 
//      + N*40 (String objects) + 4 to make the total divisible by 8. In short the total is:
//      32 + C*8 + N*40 (excluding the characters in the Strings in the array).

//  d.  Point2D
//      Taking a look at the implementation of Point2D at 
//        http://algs4.cs.princeton.edu/12oop/Point2D.java
//      it has two double instance variables at 8 each so the total is
//      16 + 16(object overhead) = 32.

//  e.  Interval1D
//      Taking a look at the implementation of Interval1D at
//        http://algs4.cs.princeton.edu/12oop/Interval1D.java
//      it has just two double instance variables so like Point2D its
//      total is 32.

//  f.  Interval2D
//      From the implementation at http://algs4.cs.princeton.edu/12oop/Interval2D.java,
//      Interval2D has two Interval1D instance variables which comes to 64 + 16 for
//      OO comes 80 which is the total since it's divisible by 8.

//  g.  Double
//      Taking a look at the Double implementation at 
//        http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/lang/Double.java/
//      a Double has only a double instance variable so it requires 8 + 16 = 24 same as Integer.

// p200
//  type       bytes
//  boolean     1
//  byte        1
//  char        2
//  int         4
//  float       4
//  long        8
//  double      8
//  memoryAddress/objectReference     8
//  objects     instanceVariables + 16 overhead + padding to %8==0 
//  Integer       24
//  Double        24
//  Date(p91)     32
//  Counter(p89)  32
//  Node(p142)    40 (including Item and Node next references (8 each)
//  String(p202)  40 (not including including chars in its array since these are shared) 
//  Array primitive(p202)  24 + N*X (24 header = 16 (Ooverhead) + 4(length) + 4 (padding)
//  Array object(p202)     24 + N*8 (references) + N*X + padding

// 1GB ~= 32M ints || 16M doubles


public class Ex1413ObjectMemoryRequirements {

  public static void main(String[] args) throws InterruptedException {


  }

}
