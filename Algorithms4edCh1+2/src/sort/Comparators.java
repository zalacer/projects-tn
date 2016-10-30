package sort;

import static java.lang.System.identityHashCode;

import java.util.Comparator;

public class Comparators {

  public static Comparator<Object> stringLengthDoubleComparator =
      // designed primarily to sort Strings with some Doubles
      (Object o1, Object o2) -> {
        if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else  if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof Double) {
          return ((Double)(1.*((String)o1).length())).compareTo((Double)o2);
        } else if (o1 instanceof Double && o2 instanceof String) {
          return ((Double)o1).compareTo((Double)(1.*((String)o2).length()));
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };

  public static Comparator<Object> stringLengthDoubleLastComparator =
      // designed primarily to sort Strings with some Doubles
      (Object o1, Object o2) -> {
        if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else  if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof Double) {
          return -1;
        } else if (o1 instanceof Double && o2 instanceof String) {
          return 1;
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };

  public static Comparator<Object> stringLengthDoubleFirstComparator =
      // designed primarily to sort Strings with some Doubles
      (Object o1, Object o2) -> {
        if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else  if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof Double) {
          return 1;
        } else if (o1 instanceof Double && o2 instanceof String) {
          return -1;
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };

  public static Comparator<Object> doubleStringLengthComparator =
      // designed primarily to sort Doubles with some Strings
      (Object o1, Object o2) -> {
        if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else if (o1 instanceof Double && o2 instanceof String) {
          return ((Double)o1).compareTo((Double)(1.*((String)o2).length()));  
        } else if (o1 instanceof String && o2 instanceof Double) {
          return ((Double)(1.*((String)o1).length())).compareTo((Double)o2);           
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };
      
  public static Comparator<Object> doubleFirstStringLengthComparator =
      // designed primarily to sort Doubles with some Strings
      (Object o1, Object o2) -> {
        if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else if (o1 instanceof Double && o2 instanceof String) {
          return -1;  
        } else if (o1 instanceof String && o2 instanceof Double) {
          return 1;           
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };
      
  public static Comparator<Object> doubleLastStringLengthComparator =
      // designed primarily to sort Doubles with some Strings
      (Object o1, Object o2) -> {
        if (o1 instanceof Double && o2 instanceof Double) {
          return ((Double)o1).compareTo((Double)o2);
        } else if (o1 instanceof String && o2 instanceof String) {
          return ((String)o1).compareTo((String)o2);
        } else if (o1 instanceof Double && o2 instanceof String) {
          return 1;  
        } else if (o1 instanceof String && o2 instanceof Double) {
          return -1;           
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };

  public static Comparator<Object> intIntArrayLengthComparator =
      // designed primarily to sort Integers with some int[]s
      (Object o1, Object o2) -> {
        if (o1 instanceof Integer && o2 instanceof Integer) {
          return ((Integer)o1).compareTo((Integer)o2);
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
          return ((Integer)(((int[])o1).length)).compareTo((Integer)(((int[])o2).length));
        } else if (o1 instanceof Integer && o2 instanceof int[]) {
          return ((Integer)o1).compareTo((Integer)(((int[])o2).length));
        } else if (o1 instanceof int[] && o2 instanceof Integer) {
          return ((Integer)(((int[])o1).length)).compareTo((Integer)o2);
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };
      
  public static Comparator<Object> intFirstIntArrayLengthComparator =
      // designed primarily to sort Integers with some int[]s
      (Object o1, Object o2) -> {
        if (o1 instanceof Integer && o2 instanceof Integer) {
          return ((Integer)o1).compareTo((Integer)o2);
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
          return ((Integer)(((int[])o1).length)).compareTo((Integer)(((int[])o2).length));
        } else if (o1 instanceof Integer && o2 instanceof int[]) {
          return -1;
        } else if (o1 instanceof int[] && o2 instanceof Integer) {
          return 1;
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };

  public static Comparator<Object> intLastIntArrayLengthComparator =
      // designed primarily to sort Integers with some int[]s
      (Object o1, Object o2) -> {
        if (o1 instanceof Integer && o2 instanceof Integer) {
          return ((Integer)o1).compareTo((Integer)o2);
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
          return ((Integer)(((int[])o1).length)).compareTo((Integer)(((int[])o2).length));
        } else if (o1 instanceof Integer && o2 instanceof int[]) {
          return 1;
        } else if (o1 instanceof int[] && o2 instanceof Integer) {
          return -1;
        } else { // catchall
          return ((Integer)identityHashCode(o1)).compareTo((Integer)identityHashCode(o2));
        }
      };
      
  public static void main(String[] args) {

  }

}
