package ch08.streams;

import java.util.stream.IntStream;

// 6. Use the String.codePoints method to implement a method that tests whether
// a string is a word, consisting only of letters. (Hint: Character.isAlphabetic.) 
// Using the same approach, implement a method that tests whether a string is a 
// valid Java identifier.

public class Ch0806StringCodePoints {

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  // found that closing stream instead of finalizing it 
  // prevented  report of false results
  public static boolean isIdentifier(String s) {
    final boolean[] stat = new boolean[]{false, true};
    IntStream strm = s.codePoints();
    strm.filter(c -> {
      if (stat[0] == false) {
        if (! Character.isJavaIdentifierStart(c)) {
          stat[1] = false;
        }
        stat[0] = true;
      } else {
        if (! Character.isJavaIdentifierPart(c)) {
          stat[1] = false;
        }
      }
      return false;          
    }).count();
    return stat[1];
  }

  public static void main(String[] args) {

    System.out.println(isWord("hello"));  // true
    System.out.println();
    System.out.println(isWord("he^lo"));  // false
    System.out.println(isWord("he^lo"));  // false
    System.out.println();
    System.out.println(isWord("   "));    // false
    System.out.println(isWord(""));       // false
    System.out.println();
    System.out.println(isIdentifier("Node"));    // true
    System.out.println(isIdentifier("_Node"));   // true
    System.out.println(isIdentifier("$Node"));   // true
    System.out.println(isIdentifier("5Node"));   // false
    System.out.println(isIdentifier("$Node5"));  // true
    System.out.println(isIdentifier("N_o$de5")); // true 
    System.out.println(isIdentifier("No^de"));   // false

  }

}
