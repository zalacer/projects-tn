package ch06.generics;

import java.util.ArrayList;
import java.util.Arrays;

import utils.StackEArr;
import utils.StackObjArr;

// 2. Reimplement the Stack<E> class, using an array to hold the elements. If
// necessary, grow the array in the push method. Provide two solutions, one with an
// E[] array and one with an Object[] array. Both solutions should compile without
// warnings. Which do you prefer, and why?

// The StackObjArr<E> implementation uses Object[] array which works well for 
// providing functionality such as cloneability, however for simplification dropped 
// all other extensions and implementations, i.e. no dependencies on AbstractList<E>, 
// List<E>, RandomAccess or Serializable. StackEArr<E> was derived by modifying 
// StackObjArr<E> and uses an E[] implemented by requiring a class argument (cl) in 
// all constructors, where the empty constructor throws an 
// UnsupportedOperationException, and in the remaining 3 viable constructors 
// the E[] is created using reflection with (E[]) java.lang.reflect.Array.newInstance(cl, 0) 
// All the viable constructors had to be annotated with @SuppressWarnings("unchecked").
// Demos of StackObjArr<E> and StackEArr<E> are below.

public class Ch0602StackTest2 {

  public static void main(String[] args) {

    // testing StackObjArr
    StackObjArr<String> stack = new StackObjArr<>();
    System.out.println(stack.isEmpty() ? "empty" : "not empty"); // empty
    stack.push("hello");
    stack.push("world");
    System.out.println(stack.isEmpty() ? "empty" : "not empty"); // not empty
    System.out.println(stack); // [hello, world]
    String s = stack.pop();
    System.out.println(s); // world
    System.out.println(stack); // [hello]

    ArrayList<String> als = new ArrayList<>();
    als.add("one");
    als.add("two");
    als.add("three");

    StackObjArr<String> stack2 = new StackObjArr<>(als);
    System.out.println(stack2); // [one, two, three]

    StackObjArr<Integer> stack3 = new StackObjArr<>(Arrays.asList(new Integer(1), new Integer(2)));
    System.out.println(stack3); // [1, 2]

    System.out.println();

    // testing StackEArr
    //  StackEArr<String> stack4 = new StackEArr<>(); 
    //  throws java.lang.UnsupportedOperationException: 
    //      must provide Class<E> argument for construction
    //          at utils1.StackEArr.<init>(StackEArr.java:29)

    StackEArr<String> stack5 = new StackEArr<>(String.class);

    System.out.println(stack5.isEmpty() ? "empty" : "not empty"); // empty
    stack5.push("hello");
    stack5.push("world");
    System.out.println(stack5.isEmpty() ? "empty" : "not empty"); // not empty
    System.out.println(stack5); // [hello, world]
    String s5 = stack5.pop();
    System.out.println(s5); // world
    System.out.println(stack5); // [hello]

    ArrayList<String> als6 = new ArrayList<>();
    als6.add("one");
    als6.add("two");
    als6.add("three");

    StackEArr<String> stack6 = new StackEArr<>(als6, String.class);
    System.out.println(stack6); // [one, two, three]

    StackEArr<Integer> stack7 = 
        new StackEArr<>(Arrays.asList(new Integer(1), new Integer(2)), Integer.class);
    System.out.println(stack7); // [1, 2]
  }

}
