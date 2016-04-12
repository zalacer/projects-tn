package ch09.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import static java.nio.file.StandardOpenOption.*;

// 4. Using a Scanner is convenient, but it is a bit slower than using a
// BufferedReader. Read in a long file a line at a time, counting the number of
// input lines, with (a) a Scanner and hasNextLine/nextLine, (b) a
// BufferedReader and readLine, (c) a bufferedReader and lines.
// Which is the fastest? The most convenient?

// I found buffered reader and readline was the fastest, then buffered reader 
// and lines and finally Scanner was the slowest. But buffered read with an 8096
// char buffer was way faster than buffered reader and readline, and 
// Files.readAllLines was faster too but not by as much. For convenience, I prefer
// buffered reader and lines, but Scanner is handy for parsing data such as user
// input. There's a tradeoff between speed vs. convenience. From that viewpoint
// buffered reader and lines is in the middle temperate zone. It seems odd that it
// doesn't run faster with an 8196 char buffer which is the same size that I used
// with buffer read which runs almost 4 times faster. For large files that can be
// a high price for streaming convenience.

public class Ch0904ScannerVsBufferedReader {

  final static String lineSep = System.getProperty("line.separator");

  public static void main(String[] args) throws IOException {

    String book = "books/WarAndPeace.txt";
    String bookCopy = "books/WarAndPeace.copy";

    // Scanner and hasNextLine/nextLine
    long start, stop, elapsed;
    long count = 0;
    Scanner sc = new Scanner(new File(book));
    start = System.currentTimeMillis();
    while(sc.hasNextLine()) {
      sc.nextLine();
      count++;
    }
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("sc elapsed = "+elapsed); // 418
    System.out.println("sc count = "+count); // 64620 (correct)
    sc.close();

    // BufferedReader and readLine
    BufferedReader br1 = Files.newBufferedReader(Paths.get(book));
    count = 0;
    @SuppressWarnings("unused")
    String line = null;
    start = System.currentTimeMillis();
    while ((line = br1.readLine()) != null) {
      count++;
    }
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("br1 elapsed = "+elapsed); // 42
    System.out.println("br1 count = "+count); // 64620
    br1.close();   

    // BufferedReader and lines
    BufferedReader br2 = Files.newBufferedReader(Paths.get(book));
    start = System.currentTimeMillis();
    // count = br2.lines().count();
    br2.lines().forEach(x -> {});
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("br2 elapsed = "+elapsed); // 113 with count, 94 with forEach
    System.out.println("br2 count = "+count); // 64620
    br2.close();

    // BufferedReader using read(char[] cbuf, int off, int len)    
    BufferedReader br3 = Files.newBufferedReader(Paths.get(book));
    count = 0;
    char[] cbuf3 = new char[8192];
    start = System.currentTimeMillis();
    while (true) {
      int r = br3.read(cbuf3, 0, 8192);
      if (r == -1) {
        break;
      } 
      count++;
    }
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("br3 elapsed = "+elapsed); // 16
    System.out.println("br3 count = "+count); // 404
    br3.close();

    // BufferedReader.lines() verification 
    BufferedWriter bw4 = Files.newBufferedWriter(Paths.get(bookCopy), CREATE);
    BufferedReader br4 = Files.newBufferedReader(Paths.get(book));
    br4.lines().forEach(x -> {
      try {
        bw4.write(x+lineSep);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    bw4.close();
    // diffing book and bookCopy showed they were identical

  // Files.readAllLines test 
  start = System.currentTimeMillis();
  List<String> allLines = Files.readAllLines(Paths.get(book));
  stop = System.currentTimeMillis();
  elapsed = stop - start;
  System.out.println("allLines elapsed = "+elapsed); // 31
  System.out.println("allLines size = "+allLines.size()); // 64620

  
  }

}
