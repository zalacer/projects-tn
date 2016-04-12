package ch09.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// 2. Write a program that reads a text file and produces a file with the same name but
// extension .toc, containing an alphabetized list of all words in the input file
// together with a list of line numbers in which each word occurs. Assume that the
// file’s encoding is UTF-8.

public class Ch0902TOC {

  public static void makeTOC(String filename) throws IOException  {
    Set<String> exclude = new HashSet<>(Arrays
        .asList("the","and","of","or","until","am","any","are","be","is","have","to","at",
            "away","do","eh","em","get","got","had","has","into","out","it","its","iv",
            "ix","ll","lose","losing","lost","low","lower","my","new","never","nevertheless",
            "next","nice","non","none","not","oh","pg","say","saw","see","sh","she","he","her",
            "his","him","there","their","thus","too","try","txt","ut","we","way","ways","which",
            "while","whiles","who","whoever","whomever","whole","whom","whose","why","yes","yer",
            "your","yours","yourself","yet","yesterday","today","tomorrow","abcdefghijklmnop",
            "an","ask","asked","ye","you","yelled","yelp","act"));
    TreeMap<String,TreeSet<Integer>> tm = new TreeMap<>();
    Scanner s = new Scanner(Paths.get(filename));
    int c = 0;
    while(s.hasNextLine()) {
      for (String e : s.nextLine().toLowerCase().split("[\\P{L}]+")) {
        if (e.matches("\\s*")) continue;
        if (e.length() < 2) continue;
        if (e.matches("i+")) continue; // excluding roman numeral chapter numbers
        if (e.matches("vi+")) continue; 
        if (e.matches("xi+")) continue;
        if (exclude.contains(e)) continue;
        if (tm.get(e) == null) {
          tm.put(e, new TreeSet<Integer>(Arrays.asList(c)));
        } else {
          tm.get(e).add(c);
        }
      }
      c++;       
    }
    s.close();

    OutputStream out = Files.newOutputStream(Paths.get(filename.replaceFirst("\\.txt","") + ".toc"));
    String x;
    String tms;
    for (String k : tm.keySet()) {
      tms = tm.get(k).toString().replace("[", "").replace("]", "");
      x = String.format("%1$-16s : %2$s\n", k, tms);
      out.write(x.getBytes(StandardCharsets.UTF_8));
    }
    out.close();
  }

  public static void main(String[] args) throws IOException {

    makeTOC("books/AlicesAdventuresInWonderland3339.txt");

  }

}
