package ch01.fundamentals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

// 8. Write a program that reads a string and prints all of its nonempty substrings.

public class Ch0108Substrings {

  public static void main(String[] args) {

    String s = "this is a string";
    List<String> a = new ArrayList<String>();
    int len = s.length();
    for (int i=0; i<len; i++) {
      for (int j=i+1; j<len+1; j++) {
        a.add(s.substring(i,j));
      }
    }
    System.out.println("# substrings = "+a.size()); // 136

    // get unique substrings
    SortedSet<String> set = new TreeSet<>(a);
    System.out.println("# unique substrings = "+set.size()); // 126

    // order by length
    Map<Integer,TreeSet<String>> map = new HashMap<>();
    int l = 0;
    for (String e: set) {
      l = e.length();
      if (map.containsKey(l)) {
        map.get(l).add(e);
      } else {
        TreeSet<String> t = new TreeSet<>();
        t.add(e);
        map.put(l, t);
      }
    }

    TreeSet<Integer> keys =  new TreeSet<>(map.keySet());
    for (Integer k: keys) {
      ArrayList<String> al = new ArrayList<>(map.get(k));
      for (int i=0; i < al.size()-1; i++) {
        System.out.print("\""+ al.get(i) + "\", ");
      }
      System.out.println("\""+ al.get(al.size()-1) + "\", ");
    }

  }

}

//  " ", "a", "g", "h", "i", "n", "r", "s", "t", 
//  " a", " i", " s", "a ", "hi", "in", "is", "ng", "ri", "s ", "st", "th", "tr", 
//  " a ", " is", " st", "a s", "his", "ing", "is ", "rin", "s a", "s i", "str", "thi", "tri", 
//  " a s", " is ", " str", "a st", "his ", "is a", "is i", "ring", "s a ", "s is", "stri", "this", "trin", 
//  " a st", " is a", " stri", "a str", "his i", "is a ", "is is", "s a s", "s is ", "strin", "this ", "tring", 
//  " a str", " is a ", " strin", "a stri", "his is", "is a s", "is is ", "s a st", "s is a", "string", "this i", 
//  " a stri", " is a s", " string", "a strin", "his is ", "is a st", "is is a", "s a str", "s is a ", "this is", 
//  " a strin", " is a st", "a string", "his is a", "is a str", "is is a ", "s a stri", "s is a s", "this is ", 
//  " a string", " is a str", "his is a ", "is a stri", "is is a s", "s a strin", "s is a st", "this is a", 
//  " is a stri", "his is a s", "is a strin", "is is a st", "s a string", "s is a str", "this is a ", 
//  " is a strin", "his is a st", "is a string", "is is a str", "s is a stri", "this is a s", 
//  " is a string", "his is a str", "is is a stri", "s is a strin", "this is a st", 
//  "his is a stri", "is is a strin", "s is a string", "this is a str", 
//  "his is a strin", "is is a string", "this is a stri", 
//  "his is a string", "this is a strin", 
//  "this is a string", 
