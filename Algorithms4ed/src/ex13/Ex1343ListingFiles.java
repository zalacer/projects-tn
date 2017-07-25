package ex13;

import static v.ArrayUtils.repeat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


//  1.3.43 Listing files. A folder is a list of files and folders. Write a program that takes the
//  name of a folder as a command-line argument and prints out all of the files contained
//  in that folder, with the contents of each folder recursively listed (indented) under that
//  folder’s name. Hint : Use a queue, and see  java.io.File 

// I don't understand the utility of a queue for this application and found it easy to
// do the indentation by tracking and adjusting its current level with a one element int[] 
// combined with a recursive Stream filter.

public class Ex1343ListingFiles {
  
  public static void listFiles(String d) {
    // front end for listFiles(String d, Integer...ind)
    System.out.println("listing all files and directories in "+d);
    System.out.println("====================================="+repeat('=', d.length()));
    listFiles(d, 0);
  }
  
  private static void listFiles(String d, Integer...indent) {
    int indentLevel = 2; // 2 space added indentation for each subdirectory level
    try (Stream<Path> entries = Files.list(Paths.get(d))) {
      entries.filter(
          x -> {
            System.out.println(repeat(' ', indent[0])+x);
            if (x.toFile().isDirectory()){
              listFiles(x.toString(), indent[0]+=indentLevel);
              indent[0]-=indentLevel;
            }
            return false; // nothing passes the filter
          }).count(); // still need to terminate the Stream
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
     String d = "C:/dirtest";
     listFiles(d);
     //  listing all files and directories in C:/dirtest
     //  ===============================================
     //  C:\dirtest\.hiddendir1
     //    C:\dirtest\.hiddendir1\dir5
     //      C:\dirtest\.hiddendir1\dir5\file8
     //  C:\dirtest\.hiddendir2
     //  C:\dirtest\.hiddenfile1
     //  C:\dirtest\dir1
     //    C:\dirtest\dir1\dir3
     //      C:\dirtest\dir1\dir3\file6
     //      C:\dirtest\dir1\dir3\file7.java
     //    C:\dirtest\dir1\file3
     //  C:\dirtest\dir2
     //    C:\dirtest\dir2\file4
     //    C:\dirtest\dir2\file5
     //  C:\dirtest\dir4
     //    C:\dirtest\dir4\filea.txt
     //    C:\dirtest\dir4\fileb.txt
     //    C:\dirtest\dir4\filec.txt
     //    C:\dirtest\dir4\filed
     //    C:\dirtest\dir4\filef
     //  C:\dirtest\file1
     //  C:\dirtest\file2

  }

}
