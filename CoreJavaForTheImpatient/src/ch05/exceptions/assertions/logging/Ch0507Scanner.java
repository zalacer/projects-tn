package ch05.exceptions.assertions.logging;

import static utils.StackTraceUtils.shortenedStackTrace;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

// 7. For this exercise, you’ll need to read through the source code of the
// java.util.Scanner class. If input fails when using a Scanner, the Scanner
// class catches the input exception and closes the resource from which it consumes
// input. What happens if closing the resource throws an exception? How does this
// implementation interact with the handling of suppressed exceptions in the try-with-
// resources statement?

// Scanner.java source for Java version 8u40-b25 from 
// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/Scanner.java/
// is in Scanner.java in this package.
// Reviewing it, scanner creates a buffer and reads source into it with try/catch 
// on each read (line 790) so in process IO errors are immediately caught and 
// retained in the lastException variable. The close() method starts at line 1076 
// and its code shows that an exception arising during its execution of 
// ((Closeable)source).close() is caught and saved in lastException which can be 
// retrieved by calling Scanner.ioException().
// Thus Scanner provides an independent means of handling and retrieving exceptions
// that occur during resource closing. Testing this shows it works well. Tests were
// performed using parseData() with an externally caused exception and with an 
// internally configured deliberate exception. These tests and the code for parseDate()
// are included below.

public class Ch0507Scanner {

  public static void parseData(String inputFileName) {
    //int c = 0;
    try (Scanner in = new Scanner(Paths.get(inputFileName))) {
      while (in.hasNextLine()) {
        in.nextLine().split("\\s+");
        Thread.sleep(100);
        //if (c > 100) throw new IOException("deliberate IOException");
        //c++;
      }
      in.close();
      IOException iox = in.ioException();
      if (iox != null) {
        System.err.println("in.ioException(): "+shortenedStackTrace(iox, 1));
      } else {
        System.err.println("in.ioException() is null");
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("caughtException: "+shortenedStackTrace(e, 1));
      Throwable[] suppressed = e.getSuppressed();
      if (suppressed.length == 0) {
        System.err.println("no suppressed exceptions");
      } else {
        System.err.println("suppressed exceptions:");
        for (Throwable t : suppressed)
          System.err.println(shortenedStackTrace(t, 1));
      }
    }
  }

  public static void main(String[] args) {

    String i = "E:/save/test/Ch0507ScannerData.txt"; 
    // this file consists of several lines of numeric data repeated many times.
    // a representative sample of it is in Ch0507ScannerData.txt in this project.
    // E was a flash drive.
    parseData(i); 
    // when parseData() was configured without a deliberate IOException:
    //   removing the flash drive E while data on it was being parsed resulted only
    //   in the following output from the Scanner's ioException() method:
    //     java.io.IOException: The volume for a file has been externally altered so 
    //       that the opened file is no longer valid
    // when parseData() was configured with a deliberate IOException:
    //   it was caught, ioException() was skipped, there were no suppressed exceptions
    //   and only the following was printed:
    //     caughtException: java.io.IOException: deliberate IOException
    //     no suppressed exceptions
  }

}
