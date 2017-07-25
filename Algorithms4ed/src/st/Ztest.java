package st;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

@SuppressWarnings("unused")
public class Ztest {

  public static void main(String[] args) {
    
    Integer x = -2147483646;
    System.out.println("x.hashCode="+x.hashCode());
    
    System.out.println(1 & 0x7fffffff);
    System.out.println(2 & 0x7fffffff);
    System.out.println(-2 & 0x7fffffff);
    System.out.println(2147483646 & 0x7fffffff);

    System.exit(0);

    
    long max = Integer.MAX_VALUE;
    long min = Integer.MIN_VALUE;
    System.out.println(max);
    System.out.println(min);
    long M = (long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE + 1;
    System.out.println(M);
    System.exit(0);

    
    SecureRandom r = new SecureRandom();
    int c = 0;
    while (c < 100) {
      System.out.println(r.nextInt()); c++;
    }
    
    
    System.out.println(Integer.toBinaryString(-7));
    System.out.println(Integer.toBinaryString(-7 & 0x7fffffff));
    System.out.println(-7);
    System.out.println(-7 & 0x7fffffff);   //2147483641
    System.out.println(Integer.MAX_VALUE); //2147483647
    //Integer.MAX_VALUE = 111 1111 1111 1111 1111 1111 1111 1111    
    // -7 = 1111 1111 1111 1111 1111 1111 1111 1001
    // 2,147,483,641 = 111 1111 1111 1111 1111 1111 1111 1001
    String S = "S";
    System.out.println(S.hashCode()); //83
    System.out.println(('S' & 0x7fffffff) %5); //2
    System.out.println(('S' & 0x7fffffff) %16); //3
    System.out.println(('S' & 0x7fffffff) %15);
    System.out.println((S.hashCode() & 0x7fffffff) % 15);
    
//    BSTX<Integer,Integer> bst = new BSTX<>(rangeInteger(1,16),rangeInteger(1,16));
//    Iterator<Integer> it = bst.keys(5,10).iterator();
//    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println();
//
//    it = bst.keys2(5,10).iterator();
//    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println();

  }

}
