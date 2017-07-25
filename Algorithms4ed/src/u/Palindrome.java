package u;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Palindrome {
  
  //http://stackoverflow.com/questions/3664881/how-does-this-java-regex-detect-palindromes
  static String assertEntirety(String pattern) {
    return "(?<=(?=^pattern$).*)".replace("pattern", pattern);
    // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    // (special constructs(named capturing and non-capturing))
    // (?<=X) X, via zero-width positive lookbehind
    // (?=X)  X, via zero-width positive lookahead
  }
  
  static String palindromeRegex() {
    return "(?x) | (?:(.) add)+ chk"
        .replace("add", assertEntirety(".*? (\\1 \\2?)"))
        .replace("chk", assertEntirety("\\2"));
    // http://www.regular-expressions.info/modifiers.html
    // (?x) is a mode modifier that turns on free-spacing mode
    //https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#COMMENTS
    // (?x) in java Permits whitespace and comments in pattern.
    // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    // (?:X) X, as a non-capturing group (special constructs(named capturing and non-capturing))
  }
  
  static boolean palindrome(String s) {
    return s.matches(palindromeRegex());
  }
  
  
  public static boolean recursivePalindrome(String str) {
    // We need two patterns: one that checks the degenerate solution (a
    // string with zero or one [a-z]) and one that checks that the first and
    // last [a-z] characters are the same. To avoid compiling these two
    // patterns at every level of recursion, we compile them once here and
    // pass them down thereafter.
    Pattern degeneratePalindrome = Pattern.compile("^[^a-z]*[a-z]?[^a-z]*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Pattern potentialPalindrome  = Pattern.compile("^[^a-z]*([a-z])(.*)\\1[^a-z]*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    return recursivePalindrome(str, degeneratePalindrome, potentialPalindrome);
  }
  
  public static boolean recursivePalindrome(String str, Pattern d, Pattern p) {
    // Check for a degenerate palindrome.
    if (d.matcher(str).find()) return true;

    Matcher m = p.matcher(str);

    // Check whether the first and last [a-z] characters match.
    if (!m.find()) return false;

    // If they do, recurse using the characters captured between.
    return recursivePalindrome(m.group(2), d, p);
  }
  
  public static  Pattern p = Pattern.compile("(.*)(.*)\\1");
  
  public static boolean recursivePalindrome2(String s) {
    if (s == null || s.length() <= 1) return true;
    Matcher m = p.matcher(s);
    return m.find() ? recursivePalindrome2(m.group(2)) : false;
  }
  
  public static boolean palindrome2(String s) {
    Pattern p = Pattern.compile("(.)(.*)(.)");
    Matcher m = null;
    if (s == null || s.equals("")) return true;
    String t = s;
    while(!(t.equals("") || t.length()==1)) {
      m = p.matcher(t);
      if (m.matches()) {
        if (!m.group(1).equals(m.group(3))) return false;
        t = m.group(2);
      }
    }
    return true;
  }

  public static void main(String[] args) {
    
    //System.out.println(palindromeRegex());
    //(?x) | (?:(.) (?<=(?=^.*? (\1 \2?)$).*))+ (?<=(?=^\2$).*)
    System.out.println(palindrome("aoxomoxoa"));
    System.out.println(recursivePalindrome("aoxomoxoa"));
    System.out.println(recursivePalindrome2("aoxomoxoa"));
    
//    String h = "hello";
//    Matcher matcher = Pattern.compile("(.)(.*)(.)").matcher(h);
//    if (matcher.matches()) {
//      System.out.println(matcher.group(1));
//      System.out.println(matcher.group(2));
//      System.out.println(matcher.group(3));
//    }
//    item = matcher.group(1);
//    currency = matcher.group(3);
//    price = matcher.group(4);
//    if (matcher.find()) return matcher.group(1).length();
//    if (matcher.matches()) {
//      item = matcher.group(1);
//      currency = matcher.group(3);
//      price = matcher.group(4);
  }

}
