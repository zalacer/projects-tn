package ch05.exceptions.assertions.logging;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// 6. Section 5.1.6, “The finally Clause,” on p. 181 has an example of a broken try
// statement with catch and finally clauses. Fix the code with (a) catching the
// exception in the finally clause, (b) a try/catch statement containing a
// try/finally statement, and (c) a try-with-resources statement with a catch
// clause.

public class Ch0506Finally {

  public static void brokenFileReader(String filename) {
    BufferedReader in = null;
    try {
      in = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8);
      in.lines().forEach(System.out::println);
    } catch (IOException ex) {
      System.err.println("Caught IOException: " + ex.getMessage());
    } finally {
      if (in != null) {
        // in.close(); // Unhandled exception type IOException
      }
    }
  }

  // traditional closing resource and catching exception in finally after try/catch
  public static void fileReader1(String filename) {
    BufferedReader in = null;
    try {
      in = Files.newBufferedReader(Paths.get(filename), UTF_8);
      in.lines().forEach(System.out::println);
    } catch (IOException ex) {
      System.err.println("IOException on BufferedReader");
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          System.err.println("problem closing BufferedReader");
        }
      }
    }
  }

  // contorted nesting try/finally to close inside of try/catch
  public static void fileReader2(String filename) {
    BufferedReader in = null;
    try {
      try {
        in = Files.newBufferedReader(Paths.get(filename), UTF_8);
        in.lines().forEach(System.out::println);
      }
      finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ex) {
            System.err.println("problem closing BufferedReader");
          }
        }
      }
    } catch (IOException ex2) {
      System.err.println("IOException on BufferedReader");
    } 
  }
  
  // best way try-with-resources with catch 
  public static void fileReader3(String filename) {
    try (BufferedReader in = Files.newBufferedReader(Paths.get(filename), UTF_8);) {
      in.lines().forEach(System.out::println);
    } catch (IOException ex) {
      System.err.println("Caught IOException: " + ex.getMessage());
    }
  }

  public static void main(String[] args) {
    
    fileReader1("config.txt");
    System.out.println();
    fileReader2("config.txt");
    System.out.println();
    fileReader3("config.txt");

  }

}
