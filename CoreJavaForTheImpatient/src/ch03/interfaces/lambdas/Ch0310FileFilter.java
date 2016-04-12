package ch03.interfaces.lambdas;

import java.io.File;
import java.io.FileFilter;

// 10. Using the listFiles(FileFilter) and isDirectory methods of the
// java.io.File class, write a method that returns all subdirectories of a given
// directory. Use a lambda expression instead of a FileFilter object. Repeat with a
// method expression and an anonymous inner class.

public class Ch0310FileFilter {

  public static FileFilter directoryFilter = new FileFilter() {
    public boolean accept (File x) {
      return x.isDirectory();
    }
  };

  public static FileFilter buildDirectoryFilter() {
    return new FileFilter() {
      public boolean accept (File x) {
        return x.isDirectory();
      }
    };
  }

  public static void main(String[] args) {

    String s = "dirtest";
    File d = new File(s);

    System.out.println("using FileFilter object");
    File[] f = d.listFiles(directoryFilter);
    for (File e : f) System.out.println(e);

    System.out.println("\nusing generated FileFilter object");
    File[] g = d.listFiles(buildDirectoryFilter());
    for (File e : g) System.out.println(e);

    System.out.println("\nusing lambda expression");
    File[] h = d.listFiles(x -> x.isDirectory());
    for (File e : h) System.out.println(e);

    System.out.println("\nusing method expression");
    File[] i = d.listFiles(File::isDirectory);
    for (File e : i) System.out.println(e);

    System.out.println("\nusing anonymous inner class");
    File[] j = d.listFiles(new FileFilter() {
      public boolean accept (File x) {
        return x.isDirectory();
      }
    });
    for (File e : j) System.out.println(e);

  }

}
