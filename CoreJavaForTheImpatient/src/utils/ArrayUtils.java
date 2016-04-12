package utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.Pair;

// for ch6 exercises 9-11 + 20-21
//
//9. In a utility class Arrays, supply a method
//Click here to view code image
//public static <E> Pair<E> firstLast(ArrayList<___> a)
//that returns a pair consisting of the first and last element of a. Supply an appropriate
//type argument.
//
//10. Provide generic methods min and max in an Arrays utility class that yield the
//smallest and largest element in an array.

//11. Continue the preceding exercise and provide a method minMax that yields a Pair
//with the minimum and maximum.

//20. Implement the method
//@SafeVarargs public static final <T> T[] repeat(int n, T… objs)
//Return an array with n copies of the given objects. Note that no Class value or
//constructor reference is required since you can reflectively increase objs.
// implemented as repeat7

//21. Using the @SafeVarargs annotation, write a method that can construct arrays of
//generic types. For example,
//List<String>[] result = Arrays.<List<String>>construct(10);
////Sets result to a List<String>[] of size 10
// implemented as construct

public class ArrayUtils {
  
  // for arrays
  public static <E> Pair<E> firstLast(E[] a) {
    if (a.length == 0)
      return null;
    return new Pair<E>(a[0], a[a.length - 1]);
  }

  public static <E extends Comparable<? super E>> E max(E[] a) {
    if (a.length == 0)
      return null;
    return Arrays.stream(a).max((x,y) -> x.compareTo(y)).orElse(null);
  }

  public static <E extends Comparable<? super E>> E min(E[] a) {
    if (a.length == 0)
      return null;
    return Arrays.stream(a).min((x,y) -> x.compareTo(y)).orElse(null);
  }
  
  public static <E extends Comparable<? super E>> Pair<E> pairMinMax(E[] a) {
    if (a.length == 0) return new Pair<E>(null, null);
    return new Pair<E>(min(a), max(a));
  }

  // for ArrayLists
  public static <E> Pair<E> firstLast(ArrayList<E> a) {
    if (a.size() == 0)
      return null;
    return new Pair<E>(a.get(0), a.get(a.size() - 1));
  }

  public static <E extends Comparable<? super E>> E max(ArrayList<E> a) {
    if (a.size() == 0)
      return null;
    E m = a.get(0);
    for (int i = 1; i < a.size(); i++) {
      E n = a.get(i);
      if (n.compareTo(m) > 0)
        m = n;
    }
    return m;
  }
  
  public static <E extends Comparable<? super E>> E min(ArrayList<E> a) {
    if (a.size() == 0)
      return null;
    E m = a.get(0);
    E n = null;
    for (int i = 1; i < a.size(); i++) {
      n = a.get(i);
      if (n.compareTo(m) < 0)
        m = n;
    }
    return m;
  }

  public static <E extends Comparable<? super E>> Pair<E> pairMinMax(ArrayList<E> a) {
    if (a.size() == 0) return new Pair<E>(null, null);
    return new Pair<E>(min(a), max(a));
  }
  
  public static <E extends Comparable<? super E>> Pair<E> pairMinMax2(ArrayList<E> a) {
    if (a.size() == 0)
      return new Pair<E>(null, null);
    E min = a.get(0);
    E max = min;
    E n = null;
    for (int i = 1; i < a.size(); i++) {
      n = a.get(i);
      if (n.compareTo(min) < 0) {
        min = n;
      } else if (n.compareTo(max) > 0) {
        max = n;
      }
    }
    return new Pair<E>(min, max);
  }

  //ch6 20. Implement the method
  //@SafeVarargs public static final <T> T[] repeat(int n, T… objs)
  //Return an array with n copies of the given objects. Note that no Class value or
  //constructor reference is required since you can reflectively increase objs.
  @SafeVarargs
  public static final <T> T[] repeat7(int n, T... objs) {
    @SuppressWarnings("unchecked")
    T[] a = (T[]) Array.newInstance(objs[0].getClass(), n * objs.length);
    int index = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < objs.length; j++) {
        Array.set(a, index, objs[i]);
        index++;
      }
    }
    return ((T[]) a);
  }

  // ch6 21. Using the @SafeVarargs annotation, write a method that can construct
  // arrays of generic types. For example,
  // List<String>[] result = Arrays.<List<String>>construct(10);
  // Sets result to a List<String>[] of size 10

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T[] construct(int n, T...objs) {
    if (objs.length == 0) {
      List<String> ls = new ArrayList<String>(java.util.Arrays.asList(""));
      List<String>[] a = (List<String>[]) Array.newInstance(ls.getClass(), n);
      return (T[]) a;
    } else {
      int length = Math.min(n, objs.length);
      T[] a = (T[]) Array.newInstance(objs[0].getClass(), length);
      for (int i = 0; i < length; i++)
        Array.set(a, i, objs[i]);
      return ((T[]) a);
    }
  }

  @SafeVarargs
  public static <T> T[] constructArray(T...objs) {
    return objs;
  }
}
