package ch01.fundamentals;

// import static utils1.GrepTools.*;

// 12. The Java Development Kit includes a file src.zip with the source code of the
// Java library. Unzip and, with your favorite text search tool, find usages of the
// labeled break and continue sequences. Take one and rewrite it without a labeled
// statement.

// using utils1.GrepTools.grep() found instances of labeled breaks and continues in 
// C:\\Program Files\\Java\\jdk1.8.0_66src.zip.
// Results are in Ch0112LabelledBreak.out.

public class Ch0112LabelledBreak {

  public static void main(String[] args) {

    // steps:
    // unbundled C:\\Program Files\\Java\\jdk1.8.0_66src.zip to "C:\\jsrc"
    // ran //grep("C:\\jsrc", "(break|continue)\\s+\\p{Alpha}\\p{Alnum}+\\s*;");
    // and saved output to Ch0112LabelledBreak.out
    // in lines 90-91 of Ch0112LabelledBreak.out found
    //  C:\jsrc\java\io\BufferedReader.java: 
    //                            break charLoop;
    // taking a look at C:\jsrc\java\io\BufferedReader.java found the corresponding code
    // in lines 341-348:
    //        charLoop:
    //            for (i = nextChar; i < nChars; i++) {
    //                c = cb[i];
    //                if ((c == '\n') || (c == '\r')) {
    //                    eol = true;
    //                    break charLoop;
    //                }
    //            }
    // instantiated this code and ran it as follows:

    int nChars = 5;
    char[] cb = {'a','b','\n','d','e'};
    boolean eol = false;
    char c;
    int i;
    int nextChar = 0;

    System.out.println("charLoop start");
    charLoop:
      for (i = nextChar; i < nChars; i++) {
        System.out.println("i = "+i);
        c = cb[i];
        if ((c == '\n') || (c == '\r')) {
          eol = true;
          break charLoop;
        }
      }
    System.out.println("charLoop stop");       
    System.out.println("eol ="+eol);
    //        charLoop start
    //        i = 0
    //        i = 1
    //        i = 2
    //        charLoop stop
    //        eol =true

    // in this code the use of a label is not necessary since there is 
    // no nesting of loops
    System.out.println("\nwith label removed:");
    // without label
    for (i = nextChar; i < nChars; i++) {
      System.out.println("i = "+i);
      c = cb[i];
      if ((c == '\n') || (c == '\r')) {
        eol = true;
        break;
      }
    }
    System.out.println("loop done");       
    System.out.println("eol ="+eol);
    //        with label removed:
    //            i = 0
    //            i = 1
    //            i = 2
    //            loop done
    //            eol =true

    // continue could be used instead of break 
    System.out.println("\ncould also use continue instead of break:");
    // without label
    for (i = nextChar; i < nChars; i++) {
      System.out.println("i = "+i);
      if (eol == false) {
        c = cb[i];
        if ((c == '\n') || (c == '\r'))
          eol = true;
      } else continue;
    }
    System.out.println("loop done");       
    System.out.println("eol ="+eol);
    //        without label:
    //            i = 0
    //            i = 1
    //            i = 2
    //            i = 3 // extra iterations that take a very small amount of
    //            i = 4 // time since they do nothing
    //            loop done
    //            eol =true

  }

}
