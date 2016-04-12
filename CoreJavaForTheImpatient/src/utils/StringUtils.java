package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StringUtils {

  //http://stackoverflow.com/questions/2297347/splitting-a-string-at-every-n-th-character
  public static List<String> getParts(String string, int partitionSize) {
    List<String> parts = new ArrayList<String>();
    int len = string.length();
    for (int i=0; i<len; i+=partitionSize)
    {
      parts.add(string.substring(i, Math.min(len, i + partitionSize)));
    }
    return parts;
  }

  public static String capitalizeFirstLetter(String original){
    if(original.length() == 0)
      return original;
    return original.substring(0, 1).toUpperCase() + original.substring(1);
  }

  public static String unCapitalizeFirstLetter(String original){
    if(original.length() == 0)
      return original;
    return original.substring(0, 1).toLowerCase() + original.substring(1);
  }

  public static String title(String original){
    if(original.length() == 0)
      return original;
    return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
  }

  public static String untitle(String original){
    if(original.length() == 0)
      return original;
    return original.substring(0, 1).toLowerCase() + original.substring(1).toLowerCase();
  }

  public static final String repeatChar(char c, int length) {
    char[] data = new char[length];
    Arrays.fill(data, c);
    return new String(data);
  }

  public static final String repeatChar(String c, int length) {
    char[] data = new char[length];
    Arrays.fill(data, c.toCharArray()[0]);
    return new String(data);
  }

  public static final String repeat(char c, int length) {
    char[] data = new char[length];
    Arrays.fill(data, c);
    return new String(data);
  }

  public static final String repeat(String c, int length) {
    char[] data = new char[length];
    Arrays.fill(data, c.toCharArray()[0]);
    return new String(data);
  }

  public static final String space(int length) {
    // create a new String consisting of space repeated length times
    char[] data = new char[length];
    Arrays.fill(data, ' ');
    return new String(data);
  }

  public static String getMaxCommonPrefix(String s1, String s2) { // fast
    if (s1.length() == 0 || s2.length() == 0) return "";
    if (s1.equals(s2)) return s1;
    int min = s1.length() <= s2.length() ? s1.length() : s2.length();
    int index = 0;
    for (int i = 0; i < min; i++) 
      if(s1.codePointAt(i) == s2.codePointAt(i)) {index++;} else break;  
    return s1.substring(0, index);
  }

  public static String getMaxCommonPrefixCharAt(String s1, String s2) { // faster
    if (s1.length() == 0 || s2.length() == 0) return "";
    if (s1.equals(s2)) return s1;
    int min = s1.length() <= s2.length() ? s1.length() : s2.length();
    int index = 0;
    for (int i = 0; i < min; i++) 
      if(s1.charAt(i) == s2.charAt(i)) {index++;} else break;  
    return s1.substring(0, index);
  }

  public static String getMaxCommonPrefixBitSet(String...s) { // slow
    if (s.length <= 1) return "";
    if (s.length == 2) return getMaxCommonPrefix(s[0], s[1]);
    int min = Integer.MAX_VALUE;
    int index = 0;
    for (String e : s) if (e.length() < min) {min = e.length();}
    BitSet b = new BitSet(1114112); // 1,114,112 = Unicode code space
    for(int i = 0; i < min; i++) {
      b.clear();
      for(int j = 0; j < s.length; j++) b.set(s[j].codePointAt(i));      
      if (b.cardinality() == 1) {index++;} else break;
    }
    return s[0].substring(0, index);
  }

  public static String getMaxCommonPrefix(String...s) { // fast
    if (s.length <= 1) return "";
    if (s.length == 2) return getMaxCommonPrefix(s[0], s[1]);
    int min = Integer.MAX_VALUE;
    int index = 0;
    for (String e : s) if (e.length() < min) {min = e.length();}
    LOOP:
      for(int i = 0; i < min; i++) {
        for(int j = 0; j < s.length - 1; j++) {
          if (s[j].codePointAt(i) != s[j+1].codePointAt(i)) break LOOP;
        }
        index++;
      }
    return s[0].substring(0, index);
  }

  public static String getMaxCommonPrefixChar(String...s) { // faster
    if (s.length <= 1) return "";
    if (s.length == 2) return getMaxCommonPrefixChar(s[0], s[1]);
    int min = Integer.MAX_VALUE;
    int index = 0;
    for (String e : s) if (e.length() < min) {min = e.length();}
    LOOP:
      for(int i = 0; i < min; i++) {
        for(int j = 0; j < s.length - 1; j++) {
          if (s[j].charAt(i) != s[j+1].charAt(i)) break LOOP;
        }
        index++;
      }
    return s[0].substring(0, index);
  }

  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
  }

  public static String roundBrackets(Object s) {
    return s.toString().replaceAll("\\[", "(").replaceAll("\\]", ")");
  }

  public static String squareBrackets(Object s) {
    return s.toString().replaceAll("(", "\\[").replaceAll(")", "\\]");
  }

  public static String removeBrackets(Object s) {
    return s.toString().replaceAll("\\[", "").replaceAll("\\]", "");
  }

  public static String removeTrailingLineFeed(String s) {
    return s.replaceAll("[\n\r]+$", "");
  }

  public static String rtlf(String s) {
    return removeTrailingLineFeed(s);
  }

  public static String trimLineFeed(String s) {
    return s.replaceAll("[\n\r\\f\\h\\s]+$", "").replaceAll("^[\n\r\\f\\h\\s]+", "");
  }

  public static String tlf(String s) {
    return trimLineFeed(s) ;
  }

  public static boolean checkBrackets(String s) {
    int mis = 0;
    for(char ch : s.toCharArray()){
      if(ch == '[') {
        mis++;
      } else if(ch == ']') {
        mis--;
      } 

      if (mis < 0) {
        return false;
      }
    }
    return mis == 0;
  }

  public static int[] getIndices(String toFind, String s) {  
    List<Integer> clist = new ArrayList<>();
    String c = ",";
    int index = -1;
    while (true) {
      index = s.indexOf(c, index+1);
      if (index >= 0) {
        clist.add(index);
      } else {
        if (clist.size() == 0) {
          return new int[0];
        } else {
          int[] a = new int[clist.size()];
          for (int i = 0; i < clist.size(); i++) {
            a[i] = clist.get(i);
          }
          return a;
        }
      }
    } 
  }

  public static String indent(String s, int i) {
    String q = s.replaceAll("\r\n", "\n");
    q = q.replaceAll("\n", "\n"+repeat(" ", i));
    return q;
  }

  public static String indentAll(String s, int i) {
    String q = s.replaceAll("\r\n", "\n");
    q = q.replaceAll("\n", "\n"+repeat(" ", i));
    q = repeat(" ", i) + q;
    return q;
  }

  public static int[] findIndents(String s) {
    // the indent of a line is defined as its leading spaces.
    // this function finds the number of leading spaces in the 
    // first line of s and reports it in ia[0]. then its finds
    // minimum indent length of its remaining lines and reports
    // it in ia[1]. in both cases a negative indent length is
    // reported as 0; The usefulness of this for universalToString
    // is that the multiline formatted strings it produces should 
    // typically have a zero or small indent length for the first 
    // line compared to its others and its extraIndent can usually 
    // be determined as ia[0] while its baseIndent can usally be 
    // determined as ia[1], which can be useful for adjusting the
    // indents of such strings in new contexts.

    int[] ia = new int[2];
    String[] sa = s.split("\n");

    if (s.matches("^\n.*")) {
      ia[0] = 0;
    } else {
      String b = sa[0].replaceAll("[^\\s]", "x");
      ia[0] = b.indexOf('x');
      if (ia[0] < 0) ia[0] = 0;
    }

    int min = Integer.MAX_VALUE;
    int j = 0;

    for (String e : sa) {
      j  = e.replaceAll("[^\\s]+", "x").indexOf('x');
      if (j <= 0) continue;
      if (j < min) min = j;
    }

    if (min < 0) min = 0;

    ia[1] = min;
    return ia;
  }

  public static void main(String... args) {

    String h = "one\ntwo\nthree";
    System.out.println(h);
    System.out.println(indent(h,10));
    System.out.println();
    System.out.println(indentAll(h,10));

  }


}
