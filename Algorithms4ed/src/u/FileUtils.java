package u;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
  
  //Ex1128BinSearchUpdateWhitelist
  public static void printFile(String f) {
    String content = null;
    try {
      content = new String(Files.readAllBytes(Paths.get(f)), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(content);
  }
  
  //Ex1128BinSearchUpdateWhitelist
  public static void updateFileWithArray(String f, int[] a) {
    if (a == null) a = new int[0];
    String ls = System.lineSeparator();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++)
      sb.append(""+a[i]+ls);
    try {
      Files.write(Paths.get(f), sb.toString().getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  

  public static void main(String[] args) {

  }

}
