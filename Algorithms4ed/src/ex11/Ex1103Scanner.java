package ex11;

//import edu.princeton.cs.algs4.In;
//import java.io.BufferedInputStream;
import java.util.Scanner;

//  1.1.3  Write a program that takes three integer command-line arguments and prints
//  equal if all three are equal, and not equal otherwise.

public class Ex1103Scanner {

  public static void main(String[] args) {

    int[] ia = new int[3];
    Scanner sc = new Scanner(System.in);
    // for large amount of input can use BufferedInputStream:
    // Scanner sc = new Scanner(new BufferedInputStream(System.in), "UTF-8");
    // using Eclipse configured with -Dfile.encoding=UTF-8
    int count = 0;
    while (count < 3) {
      if (sc.hasNextInt()) {
        ia[count] = sc.nextInt();
        count++; 
      }
    }
    sc.close();

    if (ia[0] == ia[1] && ia[1] == ia[2]) {
      System.out.println("equal");
    } else System.out.println("not equal");

    // using edu.princeton.cs.algs4.In
//    int[] ia2 = new int[3];
//    In input = new In();
//    int count2 = 0;
//    while (count2 < 3) {
//      ia2[count2] = input.readInt();
//      count2++; 
//    }
//    input.close();
//
//    if (ia2[0] == ia2[1] && ia2[1] == ia2[2]) {
//      System.out.println("equal");
//    } else System.out.println("not equal");

  }

}

