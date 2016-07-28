package ex11;

//  1.1.18  Consider the following recursive function:
//    public static int mystery(int a, int b) {
//      if (b == 0) return 0;
//      if (b % 2 == 0) return mystery(a+a, b/2);
//      return mystery(a+a, b/2) + a;
//    }
//  What are the values of mystery(2, 25) and  mystery(3, 11) ? Given positive integers
//  a and  b , describe what value  mystery(a, b) computes. Answer the same question, but
//  replace  + with  *  and replace  return 0 with  return 1 

// mystery(2, 25) == 50 == 2 * 25
// mystery(3, 11) == 33 == 3 * 11
// mystery(77, 1001)) == 77077 == 77 * 1001
// apparently mystery computes the product of its args
// mystery2 like mystery but int replaced with long, + replaced with * 
//   and return 0 replaced with return 1:
// mystery2(2, 25) == 33554432 == 2**25
// mystery2(3, 11) == 177147 == 3**11
// mystery2(77, 7) == 16048523266853 = 77**7
// apparently mystery2 computes (long) Math.pow(arg0, arg1)

public class Ex1118ValuesOfRecursiveFunction {
  
  public static int mystery(int a, int b) {
    if (b == 0) return 0;
    if (b % 2 == 0) return mystery(a+a, b/2);
    return mystery(a+a, b/2) + a;
  }
  
  public static long mystery2(long a, long b) {
    if (b == 0) return 1;
    if (b % 2 == 0) return mystery2(a*a, b/2);
    return mystery2(a*a, b/2) * a;
  }
  
 
  public static void main(String[] args) {
    
    System.out.println(mystery(2, 25)); //50
    System.out.println(mystery(3, 11)); //33
    // 3 + mystery(6, 5), 3+6+mystery(12, 2), 3+6+mystery(24, 1),
    // 3+6+24+mystery(48, 0), 33+0, 33
    System.out.println(mystery(3, 12)); //36
    System.out.println(mystery(77, 1001)); //77077
    System.out.println();
    System.out.println(mystery2(2, 25)); //33554432 = 2**25
    System.out.println(mystery2(3, 11)); //177147 = 3**11
    // 3*mystery2(9,5),3*9*mystery2(81,2),3*9*mystery2(81*81,1),3*9*81*81*1 = 3**11
    System.out.println(mystery2(3, 12)); //531441 = 2**12
    System.out.println(mystery2(77, 7)); //16048523266853 = 77**7
    
    
  }

}
