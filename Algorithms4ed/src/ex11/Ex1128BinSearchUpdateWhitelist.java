package ex11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Arrays;

import edu.princeton.cs.algs4.BinarySearch;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

//  1.1.28 Remove duplicates. Modify the test client in  BinarySearch to remove any 
//  duplicate keys in the whitelist after the sort.

public class Ex1128BinSearchUpdateWhitelist {

  public static boolean hasDups(int[] a) {
    int[] c = Arrays.copyOf(a, a.length);
    Arrays.sort(c);
    for (int i = 0; i < c.length-1; i++)
      if (c[i] == c[i+1]) return true;
    return false;
  }

  public static int[] removedups(int[] a) {
    if (a == null || a.length < 2) return a;
    Arrays.sort(a);
    int c = 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length-1; i++)
      if (a[i] == a[i+1]) {
        if (i+1 == a.length-1) { b[c] = a[i]; c++; }
        continue;
      } else { b[c] = a[i]; c++; }
    return Arrays.copyOf(b, c);
  }

  public static void updateWhiteList(String f, int[] a) {
    if (a == null) a = new int[0];
    String ls = System.lineSeparator();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++)
      sb.append(""+a[i]+ls);
    try {
      Files.write(Paths.get(f), sb.toString().getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void printFile(String f) {
    String content = null;
    try {
      content = new String(Files.readAllBytes(Paths.get(f)), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(content);
  }

  public static void main(String[] args) {

    // read the integers from a file
    In in = new In(args[0]);
    int[] whitelist = in.readAllInts();

    Arrays.sort(whitelist);

    // read integer key from standard input and print it if it's not in whitelist.txt
    while (!StdIn.isEmpty()) {
      int key = StdIn.readInt();
      if (BinarySearch.indexOf(whitelist, key) == -1)
        StdOut.println(key);
    }

    // provided in StdIn
    //  1
    //  3
    //  5
    //  7
    //  9
    //  11
    
    // printed by the client on StdOut:
    //  7
    //  9
    //  11

    // initially whitelist.txt contained:
    //  1
    //  2
    //  2
    //  3
    //  3
    //  3
    //  4
    //  4
    //  4
    //  4
    //  5
    //  5
    //  5
    //  5
    //  5

    if (hasDups(whitelist)) {
      int[] newWhitelist = removedups(whitelist);
      updateWhiteList("whitelist.txt", newWhitelist);
      printFile("whitelist.txt");
    }
    
    // finally whitelist.txt contained:
    //  1
    //  2
    //  3
    //  4
    //  5

  }

}
