package ch08.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;

// 8. Read the words from /usr/share/dict/words (or a similar word list) into a
// stream and produce an array of all words containing five distinct vowels.

public class Ch0808FindWords {

  public static BitSet getVowelsBitSet() {
    BitSet v = new BitSet(117);
    "aeiou".codePoints().forEach(i -> v.set(i));
    return v;
  }

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  public static boolean havAllVowells(String s) {
    return s.matches(".*(?=[^a]*a)(?=[^e]*e)(?=[^i]*i)(?=[^o]*o)(?=[^u]*u).*");
  }

  public static boolean havAllVowells2(String s, BitSet vowels) {
    BitSet w = new BitSet(172);
    s.codePoints().forEach(i -> w.set(i));
    w.and(vowels);
    return w.equals(vowels);

  }

  // regex filter for matching words
  public static Object[] findWordsWithAllVowels(String fileName) throws IOException {
    String r = ".*(?=[^a]*a)(?=[^e]*e)(?=[^i]*i)(?=[^o]*o)(?=[^u]*u).*";
    return  Files.newBufferedReader(Paths.get(fileName)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .filter(w -> w.matches(r)).toArray();
  }

  // using BitSet filter 4-8X faster than regex
  public static Object[] findWordsWithAllVowels2(String fileName) throws IOException {
    final BitSet v = new BitSet(117);
    "aeiou".codePoints().forEach(i -> v.set(i));
    final BitSet b = new BitSet(172);
    return  Files.newBufferedReader(Paths.get(fileName)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .filter(w -> {
          b.clear();
          w.codePoints().forEach(i -> b.set(i));
          b.and(v); 
          return b.equals(v);})
        .toArray();
  }

  public static void main(String[] args) throws IOException {

    long start = System.nanoTime();
    Object[] oa = findWordsWithAllVowels("words");
    long stop =  System.nanoTime();
    long elapsed = stop - start;                 
    System.out.println("elapsed = "+elapsed); // 2361949949, 2319596406, 2289275540 
    System.out.println("number of matching words: "+oa.length);
    System.out.println("first 10 matching words:");
    for(int i = 0; i < 10; i++) System.out.println(oa[i]);

    //        number of matching words: 9665
    //        first 10 matching words:
    //        abdomino-uterotomy
    //        abevacuation
    //        abietineous
    //        abiogenous
    //        aboideau
    //        aboideaus
    //        aboideaux
    //        aboiteau
    //        aboiteaus
    //        aboiteaux

    long start2 = System.nanoTime();
    Object[] ob = findWordsWithAllVowels2("words");
    long stop2 =  System.nanoTime();
    long elapsed2 = stop2 - start2;                              
    System.out.println("\nelapsed2 = "+elapsed2);  // 348546494, 338982287, 350216237
    System.out.println("number of matching words: "+ob.length);
    System.out.println("first 10 matching words:");
    for(int i = 0; i < 10; i++) System.out.println(ob[i]);

    //      number of matching words: 9665
    //      first 10 matching words:
    //      abdomino-uterotomy
    //      abevacuation
    //      abietineous
    //      abiogenous
    //      aboideau
    //      aboideaus
    //      aboideaux
    //      aboiteau
    //      aboiteaus
    //      aboiteaux
  }

}
