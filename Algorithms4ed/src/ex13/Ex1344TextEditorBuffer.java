package ex13;

import ds.Buffer;

// p170
//  1.3.44 Text editor buffer. Develop a data type for a buffer in a text editor that imple-
//  ments the following API:
//  public class Buffer
//  Buffer()  create an empty buffer
//  void insert(char c)  insert c at the cursor position
//  char delete()  delete and return the character at the cursor
//  void left(int k)  move the cursor k positions to the left
//  void right(int k)  move the cursor k positions to the right
//  int size()  number of characters in the buffer
//  API for a text buffer
//  Hint : Use two stacks.

public class Ex1344TextEditorBuffer {
  
  public static void main(String[] args) {
    // this demonstrates Buffer's empty constructor, insert, delete, left, right
    // and size methods.
    Buffer b1 = new Buffer();
    System.out.println(b1); // • (• represents the cursor)
    for (char c : "Hello World".toCharArray()) b1.insert(c);
    System.out.println(b1); //Hello World•
    System.out.println(b1.size()); //11
    b1.left(6);
    System.out.println(b1); //Hello• World
    System.out.println(b1.size()); //11
    b1.right(6);
    System.out.println(b1); //Hello World•
    b1.left(5);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 5; i++) sb.append(b1.delete());
    System.out.println(sb.toString()); //World
    System.out.println(b1.size()); //6
    for (char c : "Buffer".toCharArray()) b1.insert(c);
    b1.left(7); b1.delete();
    System.out.println(b1); //Hello•Buffer
    System.out.println(b1.size()); //11
        
    
    
    
    
  

  }

}
