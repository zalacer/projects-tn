package ex25;

import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/* p353
  2.5.2  Write a program that reads a list of words from standard input
  and prints all two-word compound words in the list. For example, if 
  after, thought, and afterthought are in the list, then  afterthought 
  is a compound word.

  For a list of common compound words see:
  http://www.learningdifferences.com/Main%20Page/Topics/Compound%20Word%20Lists/Compound_Word_%20Lists_complete.htm

 */

public class Ex2502FindCompoundWords {

  public static String[] compoundWords() {
    List<String> w = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
    while (sc.hasNext()) w.add(sc.next());
    sc.close(); 
    Set<String> s = new HashSet<>(); Set<String> r = new HashSet<>();
    for (String s1 : w) s.add(s1);
    for (String s1 : w)
      for (String s2 : w)
        if (s.contains(s1+s2)) r.add(s1+s2);  
    return r.toArray(new String[0]);
  }

  public static void main(String[] args) {

    show(compoundWords()); 

  }

  /* test words to be input on System.in:
sunflower
sun
flower
honeycomb
honey
comb
rainwater
rain
water
sunbathe
flowerpot
grassland
rose
brush
moon
stream  
   */
}
