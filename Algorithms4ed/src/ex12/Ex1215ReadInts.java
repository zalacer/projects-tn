package ex12;

import static java.nio.charset.StandardCharsets.UTF_8;
import static v.ArrayUtils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//  1.2.15 File input. Develop a possible implementation of the static readInts() meth-
//  od from  In (which we use for various test clients, such as binary search on page 47) that
//  is based on the  split() method in  String .
//  Solution:
//    public static int[] readInts(String name) {
//      In in = new In(name);
//      String input = StdIn.readAll();
//      String[] words = input.split("\\s+");
//      int[] ints = new int[words.length;
//      for int i = 0; i < word.length; i++)
//        ints[i] = Integer.parseInt(words[i]);
//      return ints;
//    }

public class Ex1215ReadInts {
  
  public static int[] readInts(String name) {
    String[] words = null;
    try { words = (new String(Files.readAllBytes(Paths.get(name)),UTF_8)).split("\\s+");
    } catch (IOException e) {e.printStackTrace();}
    return (int[]) unbox(map(words, x->Integer.parseInt(x)));
  }
 
  public static void main(String[] args) {
    pa(readInts("Ex1215ReadInts.txt")); 
    //int[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]
  
  }


}
