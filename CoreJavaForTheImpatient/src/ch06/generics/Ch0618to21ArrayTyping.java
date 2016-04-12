package ch06.generics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 18. Consider the method
// public static <T> T[] repeat(int n, T obj, IntFunction<T[]> constr)
// in Section 6.6.3, “You Cannot Instantiate Type Variables,” on p. 213. The call
// Arrays.repeat(10, 42, int[]::new) will fail. Why? How can you fix
// that? What do you need to do for the other primitive types?

// The fundamental problem is that a primitive array cannot be assigned a generic array
// type so a method with a generic array return type can't return a primitive array.
// Another issue is that primitive array classes don't extend the Object[] class and the
// only superclass they share is Object. For these reasons its generally practical and
// necessary to write separate methods for each primitive type to achieve the equivalent 
// functionality over all. The effect of this can be seen in Java libraries. For example 
// in java.util.Array there are separate set and get methods for each primitive type and 
// Object, similar replication occurs for several series of methods in java.util.Arrays 
// and a 3rd party example is the series of apache.commons.lang3.ArrayUtils.toPrimitive 
// methods.
// A repeat method could be coded for int simply as:
//  public static int[] repeat(int n, int v) {
//    int[] result = new int[n];
//    for (int i = 0; i < n; i++)
//        result[i] = v;
//    return result;
//  }
// This could be augmented with similar methods for all the other primitive types.

// 19. Consider the method
// public static <T> ArrayList<T> repeat(int n, T obj)
// in Section 6.6.3, “You Cannot Instantiate Type Variables,” on p. 213. This method
// had no trouble constructing an ArrayList<T> which contains an array of T
// values. Can you produce a T[] array from that array list without using a Class
// value or a constructor reference? If not, why not?

// The following method can do it:
//      @SuppressWarnings("unchecked")
//      public static <T> T[] repeat1(int n, T obj) {
//        ArrayList<T> result = new ArrayList<>();
//        for (int i = 0; i < n; i++) result.add(obj);
//        return result.toArray((T[]) Array.newInstance(obj.getClass(), n));
//      }
// A demo of this method is given below. 

// 20. Implement the method
// @SafeVarargs public static final <T> T[] repeat(int n, T… objs)
// Return an array with n copies of the given objects. Note that no Class value or
// constructor reference is required since you can reflectively increase objs.

// The following method can do it:
//      @SafeVarargs
//      public static final <T> T[] repeat2(int n, T...objs) {
//        T[] t = objs;
//        @SuppressWarnings("unchecked")
//        T[] r = (T[]) Array.newInstance(t.getClass().getComponentType(), n * t.length);
//        for (int i = 0; i < n; i++) 
//          System.arraycopy(t, 0, r, i*t.length, t.length);
//        return r;
//      }
// A demo of this is given below

// 21. Using the @SafeVarargs annotation, write a method that can construct arrays of
// generic types. For example,
// List<String>[] result = Arrays.<List<String>>construct(10);
// Sets result to a List<String>[] of size 10
//
// Here is a way it can be done:
//      @SafeVarargs
//      public static <T> T[] construct(int n, T...t) {
//        return Arrays.copyOf(t, n);
//      }

public class Ch0618to21ArrayTyping {

  @SuppressWarnings("unchecked")
  public static <T> T[] repeat1(int n, T obj) {
    ArrayList<T> result = new ArrayList<>();
    for (int i = 0; i < n; i++) result.add(obj);
    return result.toArray((T[]) Array.newInstance(obj.getClass(), n));
  }
  
  @SafeVarargs
  public static final <T> T[] repeat2(int n, T...objs) {
    T[] t = objs;
    @SuppressWarnings("unchecked")
    T[] r = (T[]) Array.newInstance(t.getClass().getComponentType(), n * t.length);
    for (int i = 0; i < n; i++) 
      System.arraycopy(t, 0, r, i*t.length, t.length);
    return r;
  }
  
  @SafeVarargs
  public static <T> T[] construct(int n, T...t) {
    return Arrays.copyOf(t, n);
  }

  public static void main(String[] args) {
    
    // repeat1 tests
    Double[] arrDouble = repeat1(5, new Double(7.3));
    System.out.println("arrDouble="+Arrays.toString(arrDouble));  
    //arrDouble=[7.3, 7.3, 7.3, 7.3, 7.3]
    
    Character[] arrCharacter = repeat1(5, new Character('z'));
    System.out.println("arrCharacter="+Arrays.toString(arrCharacter)); 
    //arrCharacter=[z, z, z, z, z]

    Boolean[] arrBoolean = repeat1(5, new Boolean(true));
    System.out.println("arrBoolean="+Arrays.toString(arrBoolean)); 
    //arrBoolean=[true, true, true, true, true]

    Integer[] arrInteger = repeat1(5, new Integer(7));
    System.out.println("arrInteger="+Arrays.toString(arrInteger)); 
    //arrInteger=[7, 7, 7, 7, 7]
    
    // repeat2 tests
    Integer[] ri = repeat2(3, 1, 2, 3);
    System.out.println(Arrays.toString(ri));
    
    Boolean[] rb = repeat2(3, true, false, true); 
    System.out.println(Arrays.toString(rb));
    
    Character[] rc = repeat2(3, 'x', 'y', 'z');
    System.out.println(Arrays.toString(rc));

    // construct tests
    Integer[] ia7 = Ch0618to21ArrayTyping.<Integer>construct(5);
    System.out.println(ia7.length); //5
    System.out.println(ia7.getClass().getComponentType().getName()); //java.lang.Integer
    
    Boolean[] ba7 = Ch0618to21ArrayTyping.<Boolean>construct(7);
    System.out.println(ba7.length); //7
    System.out.println(ba7.getClass().getComponentType().getName()); //java.lang.Boolean
    
    List<String>[] lsa7 = Ch0618to21ArrayTyping.<List<String>>construct(9);
    System.out.println(lsa7.length); //9
    System.out.println(lsa7.getClass().getComponentType().getName()); //java.util.List
    
  }

}
