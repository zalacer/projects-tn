package ch03.interfaces.lambdas;

import java.io.File;

// 11. Using the list(FilenameFilter) method of the java.io.File class,
// write a method that returns all files in a given directory with a given
// extension. Use a lambda expression, not a FilenameFilter. Which variable 
// from the enclosing scope does it capture?

// The lambda expression captures the variable of the given directory in the 
// enclosing scope, since it acts as a FilenameFilter which accepts the 
// directory in which a file is found and the name of the file.

public class Ch0311FilenameFilter {

  public static void main(String[] args) {

    String s2 = "dirtest/dir4";
    File d2 = new File(s2);
    String [] r = d2.list((x, name) -> name.endsWith(".txt"));
    for (String e : r) System.out.println(e);
 
  }

}
