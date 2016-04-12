package ch09.io;

import java.util.ArrayList;
import java.util.Arrays;

// 11. Using regular expressions, extract the directory path names 
// (as an array of strings), the file name, and the file extension 
// from an absolute or relative path such as /home/cay/myfile.txt.

public class Ch0911Regex {

  public static void main(String[] args) {

    String  p = "/home/cay/myfile.txt";
    // assuming path p represents a file not a directory 
    // since this is a regex exercise
    String[] components = p.substring(1).split("/");
    System.out.println(Arrays.toString(components));
    ArrayList<String> directoryPaths = new ArrayList<>();
    String tmp = null;
    for(int i = 0; i < components.length -1; i++) {
      tmp = "/";
      for(int j = 0; j < i; j++) tmp += components[j] + "/";
      tmp += components[i];
      directoryPaths.add(tmp);
    }
    String[] directoryPathNames = directoryPaths.toArray(new String[0]);
    System.out.println("the directory path names: "
        +Arrays.toString(directoryPathNames).replaceAll("[\\[\\]]+",""));
    System.out.println("the file name: "
        +components[components.length-1]);
    System.out.println("the file extension: "
        +components[components.length-1].split("\\.")[1]);

  }

}
