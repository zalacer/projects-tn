package u;
//package utils;
//
//import java.util.NoSuchElementException;
////import java.util.PrimitiveIterator;
//import java.util.function.Consumer;
//import java.util.function.IntConsumer;
//import utils.PrimitiveIterator;
//import static utils.PrimitiveIterator.OfChar;
//import utils.Tests.CharConsumer;
//
////http://www.programcreek.com/java-api-examples/index.php?class=java.util.PrimitiveIterator&method=OfInt
////http://www.programcreek.com/java-api-examples/index.php?source_dir=openjdk8-jdk-master/src/share/classes/java/lang/CharSequence.java
//class CharIterator implements PrimitiveIterator.OfInt {
////  String a = "hello";
////  int cur = 0;
////
////  public boolean hasNext() {
////    return cur < a.length();
////  }
////
////  public int nextInt() {
////    if (hasNext()) {
////      return a.charAt(cur++);
////    } else {
////      throw new NoSuchElementException();
////    }
////  }
////
////  @Override
////  public void forEachRemaining(IntConsumer block) {
////    for (; cur < a.length(); cur++) {
////      block.accept(a.charAt(cur));
////    }
////  }
//
//  class CharArrayIterator implements PrimitiveIterator.OfChar {
//    String a = "hello";
//    int cur = 0;
//
//    public boolean hasNext() {
//      return cur < a.length();
//    }
//
//    public char nextChar() {
//      if (hasNext()) {
//        return a.charAt(cur++);
//      } else {
//        throw new NoSuchElementException();
//      }
//    }
//
//    @Override
//    public void forEachRemaining(CharConsumer action) {
//      for (; cur < a.length(); cur++) {
//        action.accept(a.charAt(cur));
//      }
//    }
//  }
//
//
//    public static void main(String[] args) {
//
//    }
//
//  }
