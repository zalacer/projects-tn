package ex11;

//  1.1.9  Write a code fragment that puts the binary representation of a positive integer N
//  into a  String s 

public class Ex1109int2BinString {
  
  public static String int2BinString(int n) {
    return (""+Integer.toBinaryString(n));
  }
  
  public static String int2BinString2(int n) {
    // from exercise statement p.55 in text
    String s = "";
    for (int i = n; i > 0; i /= 2) s = (i % 2) + s;
    return s;
  }
    
  public static String int2BinString3(int n) {
    // extend int2BinString2 for negative ints
    String s = "";
    if (n == 0) {
      return "0";
    } else if (n > 0) {
      for (int i = n; i > 0; i /= 2) s = (i % 2) + s;
      return s;
    } else {
      long c = (long) Math.pow(2, 32) + n; 
      for (long i = c; i > 0; i /= 2) s = (i % 2) + s;
      return s; 
    }
  }
  
  public static void main(String[] args) {
    System.out.println(int2BinString(9));   //1001
    System.out.println(int2BinString(0));   //0
    System.out.println(int2BinString(1));   //1
    System.out.println(int2BinString2(9));  //1001
    System.out.println(int2BinString2(0));  //no output
    System.out.println(int2BinString2(1));  //1
    System.out.println(int2BinString3(9));  //1001
    System.out.println(int2BinString3(0));  //0
    System.out.println(int2BinString3(1));  //1
    System.out.println();
    System.out.println(int2BinString(-9));  //1111 1111 1111 1111 1111 1111 1111 0111
    System.out.println(int2BinString(-0));  //0
    System.out.println(int2BinString(-1));  //11111111111111111111111111111111
    System.out.println(int2BinString2(-9)); //no output 
    System.out.println(int2BinString2(-0)); //no output
    System.out.println(int2BinString2(-1)); //no output
    System.out.println(int2BinString3(-9)); //11111111111111111111111111110111
    System.out.println(int2BinString3(-0)); //0
    System.out.println(int2BinString3(-1)); //11111111111111111111111111111111
  }

 

}

