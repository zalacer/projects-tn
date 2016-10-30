package ex13;

import java.util.Scanner;

import ds.ResizingArrayQueue;

//  p163
//  1.3.15  Write a  Queue client that takes a command-line argument  k and prints the  k th
//  from the last string found on standard input (assuming that standard input has  k or
//  more strings).

public class Ex1315QueueKthFromLast {

 public static <T> T kthFromLast(ResizingArrayQueue<T> queue, int k) {
   return queue.kthFromLast(k);
 }
    
  // this is the kth from last Queue client
  public static void main(String[] args) {
    
    int k = Integer.parseInt(args[0]);
    System.out.println("k="+k); // k=5
    ResizingArrayQueue<String> r = new ResizingArrayQueue<String>();
    Scanner sc = new Scanner(System.in);
    while(sc.hasNext()) r.enqueue(sc.next()); sc.close();
    // stdinput is: 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
    System.out.println(kthFromLast(r, k)); //11
   
  }

}
