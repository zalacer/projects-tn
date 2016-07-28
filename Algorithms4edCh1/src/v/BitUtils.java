package v;

import static v.ArrayUtils.max;
import static v.ArrayUtils.printArray;

public class BitUtils {

  public static String int2BinString(int n) {
    return (""+Integer.toBinaryString(n));
  }

  public static String long2BinString(long n) {
    return (""+Long.toBinaryString(n));
  }

  public static void set(int b, int[] a) {
    assert a != null; assert a.length > b/32;
    a[b/32] |= 1 << b % 32;
  }

  public static boolean isSet(int b, int[] a) {
    assert a != null; assert a.length > b/32;
    return ((a[b/32] >> b % 32) & 1) == 1;
  }

  public static void unset(int b, int[] a) {
    assert a != null; assert a.length > b/32;
    a[b/32] &= ~(1 << b % 32); 
  }

  public static void toggle(int b, int[] a) {
    assert a != null; assert a.length > b/32;
    a[b/32] ^= 1 << b % 32;; 
  }

  public static int get(int b, int[] a) {
    assert a != null; assert a.length > b/32;
    if (isSet(b,a)) return 1;
    else return 0;
  }

  public static boolean contains(int b, int[] a) {
    return isSet(b,a);
  }

  public static int[] bits(int[] a) {
    assert a != null; 
    if (a.length == 0) return new int[0];
    int[] b = new int[a.length*32]; // max size
    int k = -1; // insertion index for b
    int v = 0; // calculated values
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < 32; j++) {
        v = i*32 + j;
        if (isSet(v,a)) b[++k] = v;
      }
    int[] c = new int[k+1];
    for (int i = 0; i <= k; i++) c[i] = b[i];
    return c;
  }

  public static int[] values(int[] a) {
    return bits(a);
  }
  
  public static int[] initialize(int[] a) {
    assert a != null;
    if (a.length == 0) return a;
    return new int[max(a)/32+1];
  }


  public static void main(String[] args) {

    int[] w = {1,20,45,301,5529};
    int[] r = new int[5529/32+1];
    for (int i : w) set(i,r);
    int[] b = bits(r);
    printArray(b); //[1, 20, 45, 301, 5529]


    //    int x = 161;
    //    int[] a = new int[x/32+1];
    //    System.out.println(a.length); //6
    //    set(x,a);
    //    System.out.println(isSet(x,a)); //true
    //    System.out.println(161 % 32); // 1
    //    System.out.println(int2BinString(a[5])); //10
    //    System.out.println(get(x,a)); //1
    //    int y = 160;
    //    set(y,a);
    //    System.out.println(isSet(y,a)); //false
    //    System.out.println(int2BinString(a[5])); //11
    //    System.out.println(get(y,a)); //0
    //    System.out.println(get(165,a)); //0
    //    toggle(y,a);
    //    System.out.println(int2BinString(a[5])); //10
    //    toggle(y,a);
    //    System.out.println(int2BinString(a[5])); //11
    //    unset(y,a);
    //    System.out.println(int2BinString(a[5])); //10
    //    unset(x,a);
    //    System.out.println(int2BinString(a[5])); //10

    //    nativeBitTest(); 
    //0xAAAAAAAAAAAAAAAAl;
    //    long mask = 2;
    //    long bits = 0;
    //    bits |= 2;
    //    
    //    System.out.println(mask);
    //    System.out.println(long2BinString(bits));
    //    if ((bits & mask) == mask) System.out.println(true);

    //    System.out.println(0xAl);
    //    System.out.println(long2BinString(0xAl));

    //    int bits = 0;
    //    bits |= 1 << 9;
    //    bits |= 1 << 3;
    //    bits |= 1 << 11;
    //    System.out.println(int2BinString(bits));
    //    if (((bits >> 9) & 1) == 1) {
    //      System.out.println(true);
    //    } else System.out.println(false);
    //    if (((bits >> 3) & 1) == 1) {
    //      System.out.println(true);
    //    } else System.out.println(false);
    //    if (((bits >> 11) & 1) == 1) {
    //      System.out.println(true);
    //    } else System.out.println(false);

    //    int bits = 0;
    //    for (int i = 0; i < 32; i++) {
    //      bits = 0;
    //      bits |= 1 << i;
    //      String s = int2BinString(bits);
    //      System.out.printf("%-3d %32s  %d  %d\n", i, s, s.length(), bits);
    //    }
    //  0                                  1  1  1
    //  1                                 10  2  2
    //  2                                100  3  4
    //  3                               1000  4  8
    //  4                              10000  5  16
    //  5                             100000  6  32
    //  6                            1000000  7  64
    //  7                           10000000  8  128
    //  8                          100000000  9  256
    //  9                         1000000000  10  512
    //  10                       10000000000  11  1024
    //  11                      100000000000  12  2048
    //  12                     1000000000000  13  4096
    //  13                    10000000000000  14  8192
    //  14                   100000000000000  15  16384
    //  15                  1000000000000000  16  32768
    //  16                 10000000000000000  17  65536
    //  17                100000000000000000  18  131072
    //  18               1000000000000000000  19  262144
    //  19              10000000000000000000  20  524288
    //  20             100000000000000000000  21  1048576
    //  21            1000000000000000000000  22  2097152
    //  22           10000000000000000000000  23  4194304
    //  23          100000000000000000000000  24  8388608
    //  24         1000000000000000000000000  25  16777216
    //  25        10000000000000000000000000  26  33554432
    //  26       100000000000000000000000000  27  67108864
    //  27      1000000000000000000000000000  28  134217728
    //  28     10000000000000000000000000000  29  268435456
    //  29    100000000000000000000000000000  30  536870912
    //  30   1000000000000000000000000000000  31  1073741824
    //  31  10000000000000000000000000000000  32  -2147483648





  }

}
