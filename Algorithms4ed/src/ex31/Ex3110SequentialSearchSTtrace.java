package ex31;

import static v.ArrayUtils.*;

import st.SequentialSearchSTex3110;

/* p389
  3.1.10  Give a trace of the process of inserting the keys  
  E A S Y Q U E S T I O N  into an initially empty table using  
  SequentialSearchST. How many compares are involved?
       
 */

public class Ex3110SequentialSearchSTtrace {
 
  
  public static void main(String[] args) {
    
    String[] keys = "E A S Y Q U E S T I O N".split("\\s+");
    System.out.print("keys:   "); show(keys);
    int[] values = range(0, keys.length);
    System.out.print("values: "); show(values);
    System.out.println("\nkey  value  first");
    SequentialSearchSTex3110<String,Integer> st = new SequentialSearchSTex3110<>();
    for (int  i = 0; i < keys.length; i++) {
      st.put(keys[i], values[i]);
      System.out.println(st.trace(keys[i] ,values[i]));
    }
    System.out.println("\nnumber of equals() executions = "+st.getEquals());
/*
    keys:   E A S Y Q U E S T I O N 
    values: 0 1 2 3 4 5 6 7 8 9 10 11 
    
    key  value  first
    E    0      |E:0|
    A    1      |A:1|-->|E:0|
    S    2      |S:2|-->|A:1|-->|E:0|
    Y    3      |Y:3|-->|S:2|-->|A:1|-->|E:0|
    Q    4      |Q:4|-->|Y:3|-->|S:2|-->|A:1|-->|E:0|
    U    5      |U:5|-->|Q:4|-->|Y:3|-->|S:2|-->|A:1|-->|E:0|
    E    6      |U:5|-->|Q:4|-->|Y:3|-->|S:2|-->|A:1|-->|E:6|
    S    7      |U:5|-->|Q:4|-->|Y:3|-->|S:7|-->|A:1|-->|E:6|
    T    8      |T:8|-->|U:5|-->|Q:4|-->|Y:3|-->|S:7|-->|A:1|-->|E:6|
    I    9      |I:9|-->|T:8|-->|U:5|-->|Q:4|-->|Y:3|-->|S:7|-->|A:1|-->|E:6|
    O   10      |O:10|->|I:9|-->|T:8|-->|U:5|-->|Q:4|-->|Y:3|-->|S:7|-->|A:1|-->|E:6|
    N   11      |N:11|->|O:10|->|I:9|-->|T:8|-->|U:5|-->|Q:4|-->|Y:3|-->|S:7|-->|A:1|-->|E:6| 
    
    number of equals() executions = 55
*/    
    
  }

}
