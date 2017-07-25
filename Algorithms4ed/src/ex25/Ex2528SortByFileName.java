package ex25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/* p357
  2.5.28 Sort files by name. Write a program FileSorter that takes the 
  name of a directory as a command-line argument and prints out all of 
  the files in the current directory, sorted by file name. 
  Hint : Use the  File data type.
 */

public class Ex2528SortByFileName { 
  
  public static void fileSorter(String dir) {
    // print the names of all files in directory dir in sorted order.
    // directories are considered as a type of file.
    // taking "C:\\dirtest" as command line arg.
    Path pathToDirectory = Paths.get(dir);
    // this automatically lists all files in dir in sorted order.
    try (Stream<Path> entries = Files.list(pathToDirectory)) {
      entries.forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
           
    fileSorter(args[0]);
    /*
    C:\dirtest\.hiddendir1
    C:\dirtest\.hiddendir2
    C:\dirtest\.hiddenfile1
    C:\dirtest\dir1
    C:\dirtest\dir2
    C:\dirtest\dir4
    C:\dirtest\file1
    C:\dirtest\file2      
    */    
    
    
  }

}


