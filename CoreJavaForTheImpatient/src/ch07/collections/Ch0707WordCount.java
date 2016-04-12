package ch07.collections;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.TreeMap;

// 7. Write a program that reads all words in a file and prints 
// out how often each wordoccurred. Use a TreeMap<String,Integer>.

public class Ch0707WordCount {

  public static void main(String[] args) throws IOException {

    TreeMap<String, Integer> tm = new TreeMap<>();
    String f = "books/AlicesAdventuresInWonderland3339.txt";
    Scanner s = new Scanner(Paths.get(f));
    while (s.hasNextLine()) {
      for (String e : s.nextLine().toLowerCase().split("[\\P{L}]+")) {
        if (e.matches("\\s*")) continue;
        tm.merge(e, 1, Integer::sum);
      }
    }
    s.close();

    OutputStream out = Files.newOutputStream(Paths.get("Ch0707TreeMap.out"));
    String x;
    for (String k : tm.keySet()) {
      x = String.format("%1$-16s : %2$d\n", k, tm.get(k));
      out.write(x.getBytes(StandardCharsets.UTF_8));
    }
    out.close();
  }
}
