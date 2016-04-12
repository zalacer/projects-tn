package ch07.collections;

import java.util.LinkedHashMap;
import java.util.Map;

// 9. You can update the counter in a map of counters as
//   counts.merge(word, 1, Integer::sum);
// Do the same without the merge method, (a) by using contains, (b) by using
//  get and a null check, (c) by using getOrDefault, (d) by using putIfAbsent.

public class Ch0709MapCounterUpdate {

  public static void main(String[] args) {

    Map<String,Integer> counts = new LinkedHashMap<>();
    counts.put("one", 1); counts.put("two", 1); counts.put("three", 1);
    System.out.println("0: "+counts);
    // 0: {one=1, two=1, three=1}

    // 1 merge with existing key
    String word = "one";
    counts.merge(word, 1, Integer::sum);
    System.out.println("\n1: "+counts);
    // 1: {one=2, two=1, three=1}

    // 2 merge with nonexisting key
    counts.merge("four", 1, Integer::sum);
    System.out.println("\n2: "+counts);
    // 2: {one=2, two=1, three=1, four=1}

    // 3 contains with existing key
    String key = "two";
    if (counts.containsKey(key)) {
      if (counts.get(key) == null) {
        counts.put(key, 1);
      } else {
        counts.put(key, counts.get(key) + 1);
      }
    } else {
      counts.put(key, 1);
    }
    System.out.println("\n3: "+counts);
    // 3: {one=2, two=2, three=1, four=1}

    // 4 contains with nonexisting key
    key = "five";
    if (counts.containsKey(key)) {
      if (counts.get(key) == null) {
        counts.put(key, 1);
      } else {
        counts.put(key, counts.get(key) + 1);
      }
    } else {
      counts.put(key, 1);
    }
    System.out.println("\n4: "+counts);
    // 4: {one=2, two=2, three=1, four=1, five=1}

    // 5 get and null check with existing key
    key = "three";
    if (counts.get(key) == null) {
      counts.put(key, 1);
    } else {
      counts.put(key, counts.get(key) + 1);
    }
    System.out.println("\n5: "+counts);
    // 5: {one=2, two=2, three=2, four=1, five=1}

    // 6 get and null check with nonexisting key
    key = "six";
    if (counts.get(key) == null) {
      counts.put(key, 1);
    } else {
      counts.put(key, counts.get(key) + 1);
    }
    System.out.println("\n6: "+counts);
    // 6: {one=2, two=2, three=2, four=1, five=1, six=1}

    // 7 getOrDefault with existing key
    // assuming -1 is not an allowed value for a count
    key = "four";
    if (counts.getOrDefault(key, -1) == -1) {
      counts.put(key, 1);
    } else {
      counts.put(key, counts.get(key) + 1);
    }
    System.out.println("\n7: "+counts);
    // 7: {one=2, two=2, three=2, four=2, five=1, six=1}

    // 8 getOrDefault with nonexisting key
    // assuming -1 is not an allowed value for a count
    key = "seven";
    if (counts.getOrDefault(key, -1) == -1) {
      counts.put(key, 1);
    } else {
      counts.put(key, counts.get(key) + 1);
    }
    System.out.println("\n8: "+counts);
    // 8: {one=2, two=2, three=2, four=2, five=1, six=1, seven=1}

    // 9 putIfAbsent with existing key
    key = "five";
    if (counts.putIfAbsent(key, 1) != null) 
      counts.put(key, counts.get(key) + 1);         
    System.out.println("\n9: "+counts);
    // 9: {one=2, two=2, three=2, four=2, five=2, six=1, seven=1}

    // 10 putIfAbsent with nonexisting key
    key = "eight";
    if (counts.putIfAbsent(key, 1) != null) 
      counts.put(key, counts.get(key) + 1);         
    System.out.println("\n10: "+counts);
    // 10: {one=2, two=2, three=2, four=2, five=2, six=1, seven=1, eight=1}

  }

}
