package ch01.fundamentals;
 
import java.util.Objects;
import java.util.Scanner;

// 1. Write a program that reads an integer and prints it in binary, octal, and hexadecimal.
// Print the reciprocal as a hexadecimal floating-point number.

public class Ch0101NumConv {

  public static void main(String[] args) {
    int n;
    Scanner in = new Scanner(System.in);
    while (true) {
      System.out.println("enter an integer: ");
      n = in.nextInt();
      if (Objects.nonNull(n)) break;
    }
    in.close();
    System.out.println(n+" unvarnished: "+n); // 17
    System.out.printf(n+" in decimal: %d\n", n); // 17
    System.out.println(n+" in binary: "+Integer.toString(n,2)); // 10001
    System.out.println(n+" in octal: "+Integer.toString(n,8)); // 21
    System.out.printf(n+" in octal: %o\n",n); // 21
    System.out.println(n+" in hex: "+Integer.toString(n,16)); // 11
    System.out.printf(n+" in hex: %x\n",n); // 11
    System.out.println("1./"+n+" as float: "+((float)1./n)); // 0.05882353
    System.out.println("1./"+n+" as double: "+(1./n)); // 0.058823529411764705
    System.out.printf("(float)1./"+n+" as hex float with %%a: %a\n", ((float)(1./n)));
    // 0x1.e1e1e2p-5
    System.out.printf("1./"+n+" as hex float with %%A: %A\n", ((float)(1./n))); 
    // 0X1.E1E1E2P-5
    System.out.printf("1./"+n+" as hex float with %%a: %a\n", (1./n)); // 0x1.e1e1e1e1e1e1ep-5
    System.out.printf("1./"+n+" as hex float with %%A: %A\n", (1./n)); // 0X1.E1E1E1E1E1E1EP-5        
    System.out.println("1./"+n+" using Float.toHexString: "+Float.toHexString((float)1./n)); 
    // 0x1.e1e1e2p-5
    System.out.println("1./"+n+" using Double.toHexString: "+Double.toHexString(1./n));
    // 0x1.e1e1e1e1e1e1ep-5
  }

}
