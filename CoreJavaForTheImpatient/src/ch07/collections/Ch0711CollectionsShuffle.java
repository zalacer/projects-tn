package ch07.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

// 11. Write a program that reads a sentence into an array list. Then, using
// Collections.shuffle, shuffle all but the first and last word, without copying
// the words into another collection.

public class Ch0711CollectionsShuffle {

  public static void main(String[] args) {

    String sentence = "Red fox pups leave the den when they are about seven months old.";
    ArrayList<String> a = new ArrayList<>(Arrays.asList(sentence.split("\\s+"))); 
    System.out.println(a);
    // [Red, fox, pups, leave, the, den, when, they, are, about, seven, months, old.]
    System.out.println("first: "+a.get(0)+", last: "+a.get(a.size()-1));
    // first: Red, last: old.
    Collections.shuffle(a.subList(1, a.size()-1));
    System.out.println(a);
    // [Red, about, fox, pups, seven, they, are, leave, den, the, months, when, old.]
    System.out.println("first: "+a.get(0)+", last: "+a.get(a.size()-1));
    // first: Red, last: old.

  }

}
