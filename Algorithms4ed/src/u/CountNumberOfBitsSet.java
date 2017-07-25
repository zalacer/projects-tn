package u;


//import java.util.HashMap;
//import java.util.Map;
//import java.util.TreeMap;

public class CountNumberOfBitsSet {

  public static void main(String[] args) {

    // count bits fast
    //https://llvm.org/bugs/show_bug.cgi?id=1488
    //http://lemire.me/blog/2016/05/23/the-surprising-cleverness-of-modern-compilers/
    int v = 8191; int j = 0;
    for (j = 0; v!=0; j++) {
      v &= v - 1; // clear the least significant bit set
    }
    System.out.println(j); //13
    
    // vs naive counter of number of bits set
    v = 8191; j = 0;
    for (j = 0; v!= 0; v >>= 1) {
      j += v & 1;
    }
    System.out.println(j); //13
    
//    int i = 5;
//    int x = i &= i - 1;
//    System.out.println(x); //4
//    int y = x &= x - 1;
//    System.out.println(y); //0
//    System.out.println();
//    int j = 100;
//    while (j != 0) {
//      j &= j - 1;
//      System.out.println(j); // 96 64 0
//    }
//    System.out.println();
//    j = 99;
//    while (j != 0) {
//      j &= j - 1;
//      System.out.println(j); // 98 96 64 0
//    }
//    System.out.println();
//    j = 97; //prime
//    while (j != 0) {
//      j &= j - 1;
//      System.out.println(j); // 96 64 0
//    }
    
//    TreeMap<Integer,Integer> map = new TreeMap<>();
//    
//    int count = 0; int y = 0;
//    for (int x = 1; x < 10001; x++) {
//      count = 0;
//      y = x;
//      while (y != 0) {
//        y &= y - 1;
//        count++;
//      }
//      map.putIfAbsent(count,x);
////      System.out.println(""+x+"     "+count);
//    }
//    
//    int c = 0;
//    for (Integer t : map.descendingKeySet()) {
//      System.out.println(""+t+"       "+map.get(t));
//      c++;
//      if (c == 100) break;
//    }
//    //  13       8191
//    //  12       4095
//    //  11       2047
//    //  10       1023
//    //  9       511
//    //  8       255
//    //  7       127
//    //  6       63
//    //  5       31
//    //  4       15
//    //  3       7
//    //  2       3
//    //  1       1
//    System.out.println();
//    
//    Map<Integer,Integer> map2 = new HashMap<>();
//    count = 0; y = 0;
//    for (int x = 10001; x > 0; x--) {
//      count = 0;
//      y = x;
//      while (y != 0) {
//        y &= y - 1;
//        count++;
//      }
//      map2.put(count,x);
////      System.out.println(""+x+"     "+count);
//    }
//    
//    c = 0;
//    for (Integer t : map2.keySet()) {
//      System.out.println(""+t+"       "+map2.get(t));
//      c++;
//      if (c == 100) break;
//    }
//    System.out.println();
//
//    count = 0; y = 0;
//    for (int x = 1; x < 250; x++) {
//      count = 0;
//      y = x;
//      while (y != 0) {
//        y &= y - 1;
//        count++;
//      }
//     System.out.println(""+x+"      "+count);
//    }
    
  }

}
