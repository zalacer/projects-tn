package ex13;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Scanner;

import ds.Stack;

//  p169
//  1.3.40 Move-to-front. Read in a sequence of characters from standard input and
//  maintain the characters in a linked list with no duplicates. When you read in a previ-
//  ously unseen character, insert it at the front of the list. When you read in a duplicate
//  character, delete it from the list and reinsert it at the beginning. Name your program
//  MoveToFront : it implements the well-known move-to-front strategy, which is useful for
//  caching, data compression, and many other applications where items that have been
//  recently accessed are more likely to be reaccessed.

public class Ex1340Move2Front {
  
  public static void main(String[] args) {
    
    StringBuilder sb = new StringBuilder();
    Scanner sc = new Scanner(System.in);
    sc.useDelimiter("");
    while(sc.hasNext()) sb.append(sc.next());
    sc.close();
    String s = sb.toString();
    System.out.println(s); //Read in a sequence of characters from standard input
    char[] ca = s.replaceAll("\\s+","").toCharArray(); // removing whitespaces
    Stack<Character> st = new Stack<Character>(); //Stack implemented with singly linked list
    for (char c : ca) st.move2Front(c); // implements the well-known move-to-front strategy 
    System.out.println(st); //Stack(t,u,p,n,i,d,r,a,s,m,o,f,e,c,h,q,R)
    assert Arrays.equals((char[])unbox(st.toArray()), unique(reverse(ca)));
    // what this means is that the well-known move-to-front strategy is equivalant to
    // reversing the order of the input data and retaining only the first occurrence
    // of each item based on equality
    
    // another way is by converting the input chars to codePoints and using a Stack<Integer>
    Stack<Integer> st2 = new Stack<Integer>();
    for (int i = 0; i < ca.length; i++) st2.move2Front(Character.codePointAt(ca, i));
    System.out.println(st2);//Stack(116,117,112,110,105,100,114,97,115,109,111,102,101,99,104,113,82)
    char[] output = toCharArray(st2.toArray());
    pa(output,120,1,1); // [t,u,p,n,i,d,r,a,s,m,o,f,e,c,h,q,R]
    assert Arrays.equals(output, unique(reverse(ca)));
    
  }

}
