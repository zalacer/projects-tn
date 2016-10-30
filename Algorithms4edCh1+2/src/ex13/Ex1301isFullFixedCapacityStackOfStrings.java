package ex13;

import static v.ArrayUtils.*;

//  p161
//  1.3.1  Add a method  isFull() to  FixedCapacityStackOfStrings 

public class Ex1301isFullFixedCapacityStackOfStrings {
  
  // using implementation provided on p133 of the text
  public static class FixedCapacityStackOfStrings {
    private String[] a; // stack entries
    private int N; // size
    public FixedCapacityStackOfStrings(int cap) { a = new String[cap]; }
    public boolean isEmpty() { return N == 0; }
    public int size() { return N; }
    public void push(String item) { a[N++] = item; }
    public String pop() { return a[--N]; }
    public boolean isFull() { return N == a.length; }
    @Override
    public String toString() {
      return "FixedCapacityStackOfStrings"+arrayToString(take(a,N),75,1,1);
    }
  }

  public static void main(String[] args) {
    
    FixedCapacityStackOfStrings fcss = new FixedCapacityStackOfStrings(5);
    int c = 1;
    while (!fcss.isFull()) fcss.push(""+c++);
    System.out.println(fcss); //FixedCapacityStackOfStrings[1,2,3,4,5]
  }

}
