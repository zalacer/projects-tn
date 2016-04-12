package ch07.collections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import utils.Tuple2;

// 6. I encouraged you to use interfaces instead of concrete data structures, for example, 
// a Map instead of a TreeMap. Unfortunately, that advice goes only so far. Why can’t
// you use a Map<String, Set<Integer>> to represent a table of contents?
// (Hint: How would you initialize it?) What type can you use instead?

// I understand the advice, but haven't experienced much practical value from it and
// have often applied it but then had to put in a concrete class in order to get the 
// functionality that I needed, such as with TreeMap in particular. Even HashMap adds 
// quite a number of functions not in the specification of Map, such as clone, compute, 
// forEach, getOrDefault, merge, putIfAbsent, remove(Object key, Object value), and 
// replaceAll. The default implementation of Map is HashMap, so might as well declare 
// it as such in the first place to enable all its functionality and not try to obscure
// what it actually is.
// Regarding representation of a table of contents, ordering is important so for a
// major portion of it I might use 
//   LinkedHashMap<Integer,Tuple2<String,LinkedHashMap<Double,String>>>
// to allow for numbered chapter subsections as well as chapters.

public class Ch0706InterfacesVsConcrete {

  public static void main(String[] args) {

    // here is one way to initialize a Map<String, Set<Integer>>
    Map<String, Set<Integer>> ms = new HashMap<>();   
    Set<Integer> s1 = new HashSet<>(Arrays.asList(1,2,3));
    Set<Integer> s2 = new HashSet<>(Arrays.asList(4,5,6));
    Set<Integer> s3 = new HashSet<>(Arrays.asList(7,8,9));
    ms.put("one", s1);
    ms.put("two", s2);
    ms.put("three", s3);
    System.out.println(ms); 
    // ch07.collections.Ch0706InterfacesVsConcrete$1
    // {one=[1, 2, 3], two=[4, 5, 6], three=[7, 8, 9]}

    // this is how LinkedHashMap<Integer,Tuple2<String,LinkedHashMap<Double,String>>>
    // can be initially initialized
    LinkedHashMap<Integer,Tuple2<String,LinkedHashMap<Double,String>>> toc =
        new LinkedHashMap<>();
    System.out.println(toc); // just using toc to get rid of eclipse warning about not using it
    
 



  }

}
