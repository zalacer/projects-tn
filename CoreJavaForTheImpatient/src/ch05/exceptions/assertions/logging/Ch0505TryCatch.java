package ch05.exceptions.assertions.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

// 5. Implement a method that contains the code with a Scanner and a PrintWriter
// in Section 5.1.5, “The Try-with-Resources Statement,” on p. 179. But don’t use the
// try-with-resources statement. Instead, just use catch clauses. Be sure to close both
// objects, provided they have been properly constructed. You need to consider the
// following conditions:
// • The Scanner constructor throws an exception.
// • The PrintWriter constructor throws an exception.
// • hasNext, next, or println throw an exception.
// • out.close() throws an exception.
// • in.close() throws an exception.

public class Ch0505TryCatch {

  public static void printFile(String inputFileName, String outputFileName) {

    Scanner in = null;
    PrintWriter out = null;
    try {
      in = new Scanner(Paths.get(inputFileName));
      out = new PrintWriter(outputFileName);
      while (in.hasNext())
        out.println(in.next().toLowerCase());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) { 
          e.printStackTrace();  
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }

  }

  // using Try-with-Resources for comparison
  public static void printFileTryWithResources(String inputFileName, String outputFileName) {

    try (Scanner in = new Scanner(Paths.get(inputFileName)); 
        PrintWriter out = new PrintWriter(outputFileName)) {
      while (in.hasNext())
        out.println(in.next().toLowerCase());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {

    // config.txt is in the project
    printFileTryWithResources("config.txt", "out1.txt");
    printFile("config.txt", "out2.txt");

  }

}
