package ds;

import ds.Stack;

//  p170
//  1.3.44 Text editor buffer. Develop a data type for a buffer in a text 
//  editor that implements the following API:
//    public class Buffer
//    Buffer()  create an empty buffer
//    void insert(char c)  insert c at the cursor position
//    char delete()  delete and return the character at the cursor
//    void left(int k)  move the cursor k positions to the left
//    void right(int k)  move the cursor k positions to the right
//    int size()  number of characters in the buffer
//    API for a text buffer
//    Hint : Use two stacks.

public class Buffer {
  private Stack<Character> leftStack; // buffer to left of cursor
  private Stack<Character> rightStack; // buffer at and to right of cursor
  
  public Buffer() {
    leftStack = new Stack<Character>();
    rightStack = new Stack<Character>();
  }
  
  public Buffer(String s) {
    leftStack = new Stack<Character>();
    rightStack = new Stack<Character>();
    if (s != null)  for (char c : s.toCharArray()) insert(c);
  }
  
  public Buffer(String s1, String s2) {
    leftStack = new Stack<Character>();
    rightStack = new Stack<Character>();
    if (s1 != null) for (char c : s1.toCharArray()) leftStack.push(c);
    char[] ca = s2.toCharArray();
    if (s2 != null) for (int i = ca.length-1; i > -1; i--) rightStack.push(ca[i]);
  }
  
  public void insert(char c) {
    // insert c just before cursor position, nulls disallowed
    leftStack.push(c);
  }
  
  public char delete() {
    // delete and return the character just after cursor
    return rightStack.isEmpty() ? '\0' : rightStack.pop();
  }

  public void left(int k) {
    // move the cursor k positions to the left
    while(!leftStack.isEmpty() && k-- > 0) rightStack.push(leftStack.pop());
  }

  public void right(int k) {
    // move the cursor k positions to the right
    while(!rightStack.isEmpty() && k-- > 0) leftStack.push(rightStack.pop());
  }
  
  public int size() {
    return leftStack.size() + rightStack.size();
  }
  
  @Override
  public String toString() {
    return leftStack.reverseRawString()+"•"+rightStack.rawString();
  }
  
  public static void main(String[] args) {
    
//    Buffer b = new Buffer();
//    System.out.println(b);
//    b.insert('a');
//    System.out.println(b);
    Buffer b2 = new Buffer("Hello","World");
    System.out.println(b2); //Hello*World
    System.out.println(b2.delete()); //W
    System.out.println(b2); //Hello*orld
    System.out.println(b2.delete()); //o
    System.out.println(b2); //Hello*rld
    System.out.println(b2.delete()); //r
    System.out.println(b2); //Hello*ld
    System.out.println(b2.delete()); //l
    System.out.println(b2); //Hello*d
    System.out.println(b2.delete()); //d
    System.out.println(b2); //Hello*
    System.out.println(b2.delete() == '\0'); //true
    System.out.println(b2); //Hello*
    
//    Buffer b3 = new Buffer("HelloWorld");
//    System.out.println(b3);
//    b3.left(5);
//    System.out.println(b3);
  }

}
