package ch03.interfaces.lambdas;

import java.io.File;
import java.util.Arrays;

// 12. Given an array of File objects, sort it so that directories come
// before files, and within each group, elements are sorted by path name. 
// Use a lambda expression to specify the Comparator.

public class Ch0312Comparator {

  public static void main(String[] args) {

    String s = "dirtest";
    File d = new File(s);
    File[] f = d.listFiles();
    // for (File e : f) System.out.println(e);
    Arrays.sort(f, (o1, o2) -> {
      File f1 = (File) o1;
      File f2 = (File) o2;
      if (f1.isDirectory() && f2.isFile()) {
        return -1;
      } else if (f1.isFile() && f2.isDirectory()) {
        return 1;
      } else {
        return f1.getName().compareTo(f2.getName());
      }
    });

    for (File e : f)
      System.out.println(e);
    
  }

}
