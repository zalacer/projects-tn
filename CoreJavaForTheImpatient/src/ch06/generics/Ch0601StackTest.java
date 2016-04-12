package ch06.generics;

import java.util.ArrayList;
import java.util.Arrays;

import utils.Stack;
import utils.Stack2;

// 1. Implement a class Stack<E> that manages an array list of elements of type E.
// Provide methods push, pop, and isEmpty. 

// For Stack cloned sources of version 8u40-b25 AbstractList and ArrayList.
// In AbstractList changed modCount on line 612 from protected to public
// and changed removeRange on line 677 from protected to public.
// In ArrayList changed occurrences of "ArrayList" to "Stack", defined
// push(e) to return add(e), defined pop(e) to return remove(size -1), and
// added all necessary imports including utils1.AbstractList but not
// java.util.AbstractList. isEmpty was already defined in ArrayList.  This
// method carries through all ArrayList functionality to Stack.

// For Stack2 simply extended ArrayList and added push and pop methods as 
// described above. This method has some deficiencies such as all ArrayList
// constructors don't work for Stack including Stack(Collection<? extends E> c),
// however it works well enough to answer the question.

public class Ch0601StackTest {

  public static void main(String[] args) {

    // testing Stack
    Stack<String> stack = new Stack<>();
    System.out.println(stack.isEmpty() ? "empty" : "not empty"); // empty
    stack.push("hello"); stack.push("world");
    System.out.println(stack.isEmpty() ? "empty" : "not empty"); // not empty
    System.out.println(stack); // [hello, world]
    String s = stack.pop();  
    System.out.println(s); // world
    System.out.println(stack); // [hello]

    ArrayList<String> als = new ArrayList<>();
    als.add("one"); als.add("two"); als.add("three"); 

    Stack<String> stack2 = new Stack<>(als); 
    System.out.println(stack2); // [one, two, three]

    Stack<Integer> stack3 = new Stack<>(Arrays.asList(new Integer(1), new Integer(2)));  
    System.out.println(stack3); // [1, 2]

    System.out.println();

    // testing Stack2
    Stack2<String> stack4 = new Stack2<>();
    System.out.println(stack4.isEmpty() ? "empty" : "not empty"); // empty
    stack4.push("hello"); stack4.push("world");
    System.out.println(stack4.isEmpty() ? "empty" : "not empty"); // not empty
    System.out.println(stack4); // [hello, world]
    String s2 = stack4.pop();  
    System.out.println(s2); // world
    System.out.println(stack4); // [hello]

  }

}
