package ch06.generics;

import static utils.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Arrays;

import utils.Pair;

// 9. In a utility class Arrays, supply a method
// public static <E> Pair<E> firstLast(ArrayList<___> a)
// that returns a pair consisting of the first and last element of a. 
// Supply an appropriate type argument.
//
//10. Provide generic methods min and max in an Arrays utility class that yield the
//smallest and largest element in an array.
//
//11. Continue the preceding exercise and provide a method minMax that yields a Pair
//with the minimum and maximum.

// Unsure if the requested methods should operate on arrays or ArrayLists so will
// implement both.

public class Ch0609to11Arrays {

  public static void main(String[] args) {

    // for arrays
    Integer[] ia = new Integer[]{new Integer(1),new Integer(2),new Integer(3)};
    System.out.println(Arrays.toString(ia)); //[1, 2, 3]
    Pair<Integer> pia = firstLast(ia);
    System.out.println("pia="+pia); //Pair(first=1, second=3)
    
    String[] sa = new String[]{"one","two","three"};
    System.out.println(Arrays.toString(sa)); //[one, two, three]
    Pair<String> psa = firstLast(sa); 
    System.out.println(psa); //Pair(first=one, second=three)
    
    System.out.println("ia min = "+min(ia)); // 1
    System.out.println("ia max = "+max(ia)); // 3
    System.out.println("sa min = "+min(sa)); // one
    System.out.println("sa max = "+max(sa)); // two
    System.out.println();
    
    Pair<Integer> piamm = pairMinMax(ia);
    System.out.println("piamm="+piamm); // Pair(first=1, second=3)
    Pair<String> psamm = pairMinMax(sa);
    System.out.println("psamm="+psamm); // Pair(first=one, second=two)
    System.out.println();

    // for ArrayLists
    ArrayList<Integer> ali = new ArrayList<>();
    ali.add(1);
    ali.add(2);
    ali.add(3);
    System.out.println(ali); //[1, 2, 3]
    Pair<Integer> p = firstLast(ali); 
    System.out.println("p="+p); //Pair(first=1, second=3)

    ArrayList<String> als = new ArrayList<>();
    als.add("one");
    als.add("two");
    als.add("three");
    System.out.println(als); //[one, two, three]
    Pair<String> p2 = firstLast(als); 
    System.out.println("p2="+p2);
    System.out.println(); //Pair(first=one, second=three)

    System.out.println("ali min = "+min(ali)); // 1
    System.out.println("ali max = "+max(ali)); // 3
    System.out.println("als min = "+min(als)); // one
    System.out.println("als max = "+max(als)); // two
    System.out.println();

    Pair<Integer> p3 = pairMinMax(ali);
    System.out.println("p3="+p3); // Pair(first=1, second=3)
    Pair<String> p4 = pairMinMax(als);
    System.out.println("p4="+p4); // Pair(first=one, second=two)

  }

}

