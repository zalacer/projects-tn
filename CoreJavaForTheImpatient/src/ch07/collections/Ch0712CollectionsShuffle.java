package ch07.collections;

import static utils.StringUtils.capitalizeFirstLetter;
import static utils.StringUtils.unCapitalizeFirstLetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 12. Using Collections.shuffle, write a program that reads a sentence, shuffles
// the words, and prints the result. Fix the capitalization of the initial word and the
// punctuation of the last word (before and after the shuffle). Hint: Don’t shuffle the
// words.

public class Ch0712CollectionsShuffle {

  public static void main(String[] args) {

    // method 1: leave first and last words including punctuation fixed

    String sentence = "Red fox pups leave the den when they are about seven months old.";
    ArrayList<String> a = new ArrayList<>(Arrays.asList(sentence.split("\\s+")));
    Collections.shuffle(a.subList(1, a.size() - 1));
    int c = 0;
    for (String w : a) {
      if (c == a.size() - 1) {
        System.out.println(w);
      } else {
        System.out.print(w + " ");
      }
      c++;
    }
    // Red about when they seven are the pups months leave fox den old. 

    // method 2: shuffle all words after uncapitalizing the first letter of the first
    // word if it is capitalized and removing punctuation from the last if it has any; 
    // after shuffling restore capitalization of the first letter of the new first word 
    // and restore the punctuation from the end of the original last word to the new last word

    ArrayList<String> b = new ArrayList<>(Arrays.asList(sentence.split("\\s+")));
    String first = b.get(0);
    String firstUnCap = unCapitalizeFirstLetter(first);
    boolean capitalize = false;
    if (! first.equals(firstUnCap)) {
      capitalize = true;
      b.set(0, firstUnCap);
    }
    String last = b.get(a.size() - 1);
    String regex = "^([^\\P{L}]+)([\\P{L}]+)\\z"; 
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(last);
    String lastHead = "";
    String trailPunc = "";
    if (matcher.matches()) {
      lastHead = matcher.group(1);
      trailPunc = matcher.group(2);
    }
    if (! lastHead.equals(""))
      b.set(a.size() - 1, lastHead);
    Collections.shuffle(b);
    if (capitalize)
      b.set(0, capitalizeFirstLetter(b.get(0)));
    b.set(a.size() - 1, b.get(a.size() - 1) + trailPunc);
    c = 0;
    for (String w : b) {
      if (c == b.size() - 1) {
        System.out.println(w);
      } else {
        System.out.print(w + " ");
      }
      c++;
    }
    // Seven red leave months are pups fox when about old the den they.
  }

}
