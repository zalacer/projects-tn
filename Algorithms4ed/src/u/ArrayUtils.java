package u;

//import static utils.ArrayUtils.max;
//import static java.lang.System.identityHashCode;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Optional.*;
import static u.BitUtils.bits;
import static u.BitUtils.set;
import static u.StringUtils.space;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.deepToString;
import static java.util.Comparator.comparing;
//import static java.util.Optional.empty;

//import java.math.BigInteger;
import java.lang.Integer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
//import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator.OfDouble;
import java.util.PrimitiveIterator.OfInt;
import java.util.PrimitiveIterator.OfLong;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
//import java.util.function.DoubleToIntFunction;
//import java.util.function.DoubleToLongFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
//import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;
//import java.util.function.LongToIntFunction;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import u.PrimitiveIterator.OfBoolean;
import u.PrimitiveIterator.OfByte;
import u.PrimitiveIterator.OfChar;
import u.PrimitiveIterator.OfFloat;
import u.PrimitiveIterator.OfShort;

public class ArrayUtils {
  
  // max array length due to VM limit
  public static final int maxlen = Integer.MAX_VALUE-2;
  
  // these FunctionalInterfaces are to avoid method signature contention
  
  @FunctionalInterface
  private static interface F1<T, R> {
    // used for flatMap
      R apply(T t);
  }
  
  @FunctionalInterface
  private static interface F2<T, R> {
    // used for flatMap
    R apply(T t);
  }
  
  @FunctionalInterface
  private static interface F3<T, R> {
    // used for flatMap
    R apply(T t);
  }
  
  @FunctionalInterface
  private static interface F4<T, R> {
    // used for flatMap
    R apply(T t);
  }
  
  @FunctionalInterface
  private static interface B1<T, R> {
    // used for scanLeft
    R apply(R r, T t);
  }
  
  @FunctionalInterface
  private static interface C1<T, R> {
 // used for scanRight
    R apply(T t, R r);
  }
  
  @FunctionalInterface
  private static interface C2<T, R> {
 // used for scanRight
    R apply(T t, R r);
  }
  
//  @FunctionalInterface
//  private static interface Con1<T> {
//    void accept(T t);
//  }
  @FunctionalInterface
  public interface ByteConsumer {
    void accept(byte value);
    default ByteConsumer andThen(ByteConsumer after) {
      Objects.requireNonNull(after);
      return (byte t) -> {accept(t); after.accept(t);};
    }
  }
  
  @FunctionalInterface
  public interface ShortConsumer {
    void accept(short value);
    default ShortConsumer andThen(ShortConsumer after) {
      Objects.requireNonNull(after);
      return (short t) -> {accept(t); after.accept(t);};
    }
  }
  
  @FunctionalInterface
  public interface FloatConsumer {
    void accept(float value);
    default FloatConsumer andThen(FloatConsumer after) {
      Objects.requireNonNull(after);
      return (float t) -> {accept(t); after.accept(t);};
    }
  }
  
  @FunctionalInterface
  public interface CharConsumer {
    void accept(char value);
    default CharConsumer andThen(CharConsumer after) {
      Objects.requireNonNull(after);
      return (char t) -> {accept(t); after.accept(t);};
    }
  }
  
  @FunctionalInterface
  public interface BooleanConsumer {
    void accept(boolean value);
    default BooleanConsumer andThen(BooleanConsumer after) {
      Objects.requireNonNull(after);
      return (boolean t) -> {accept(t); after.accept(t);};
    }
  }

  public static Object unbox(Object a) {
    // copies an array with boxed type rootComponentType into an array with 
    // primitive type rootComponentType and identical structure
    if( a == null) throw new IllegalArgumentException("unbox: "
        + "argument can't be null");
    if (!isBoxedArray(a)) throw new IllegalArgumentException("unbox: "
        + "argument must be an array of primitive components");
    int dim = dim(a);
    if (dim < 1) throw new IllegalArgumentException("box "
        + "dimension of argument must be > 0");
    
    String t = rootComponentType(a);
    
    if (dim == 1) { // base case of recursion
      Class<?> nt = null;
      
      switch (t) {
        case "java.lang.Byte":      nt = byte.class;    break;
        case "java.lang.Character": nt = char.class;    break;
        case "java.lang.Double":    nt = double.class;  break;
        case "java.lang.Float":     nt = float.class;   break;
        case "java.lang.Integer":   nt = int.class;     break;
        case "java.lang.Long":      nt = long.class;    break;
        case "java.lang.Short":     nt = short.class;   break;
        case "java.lang.Boolean":   nt = boolean.class; break;
        default : throw new IllegalArgumentException("unbox: "
            + "cannot unbox a "+t);
      }

      int len = Array.getLength(a);
      Object array = Array.newInstance(nt, len);

      switch (t) {
        case "java.lang.Byte":
          {byte[] array2 = (byte[]) array;
          Byte[] b = (Byte[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Character":
          {Character[] array2 = (Character[]) array;
          char[] b = (char[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Double":
          {double[] array2 = (double[]) array;
          Double[] b = (Double[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Float":
          {float[] array2 = (float[]) array;
          Float[] b = (Float[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Integer":
          {int[] array2 = (int[]) array;
          Integer[] b = (Integer[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Long":
          {long[] array2 = (long[]) array;
          Long[] b = (Long[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Short":
          {short[] array2 = (short[]) array;
          Short[] b = (Short[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        case "java.lang.Boolean":
          {boolean[] array2 = (boolean[]) array;
          Boolean[] b = (Boolean[]) a;
          for (int i = 0; i < len; i++) array2[i] = b[i];
          break;}
        default : throw new IllegalArgumentException("unbox: "
            + "cannot unbox a "+t);
      }

      return array;
      // end of dim=1 base recursion case
    } else {
      String nt = null;
      String prefix = repeat('[', dim-1);
      
      switch (t) {
        case "java.lang.Byte":      nt = prefix+"B";  break;
        case "java.lang.Character": nt = prefix+"C";  break;
        case "java.lang.Double":    nt = prefix+"D";  break;
        case "java.lang.Float":     nt = prefix+"F";  break;
        case "java.lang.Integer":   nt = prefix+"I";  break;
        case "java.lang.Long":      nt = prefix+"J";  break;
        case "java.lang.Short":     nt = prefix+"S";  break;
        case "java.lang.Boolean":   nt = prefix+"Z";  break;
        default : throw new IllegalArgumentException("unbox: "
            + "cannot unbox a "+t);
      }
    
      Class<?> c;
      try {
        c = Class.forName(nt);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return null;
      }
    
      int len = Array.getLength(a);
      Object array = Array.newInstance(c, len);
      for (int i = 0; i < len; i++)
        Array.set(array,i,unbox(Array.get(a,i)));

      return array;
    }
  }
  
  public static double mean(int[] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./a.length;
  }
 
  public static double mean(long[] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./a.length;
  }
 
  public static double mean(double[] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./a.length;
  }
  
  public static double mean(int[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }
  
  public static double mean(long[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }

  public static double mean(double[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }

   
  public static double mean(int[][][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }
  
  public static double mean(long[][][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }
 
  public static double mean(double[][][] a) {
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return sum(a)*1./numel(a);
  }
    
  public static double abmean(int[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./a.length;
  }
 
  public static double abmean(long[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./a.length;
  }
 
  public static double abmean(double[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./a.length;
  }
  
  public static double abmean(int[][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }
  
  public static double abmean(long[][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }

  public static double abmean(double[][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }

   
  public static double abmean(int[][][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }
  
  public static double abmean(long[][][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }
 
  public static double abmean(double[][][] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null) throw new IllegalArgumentException(
        "mean: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "mean: array must have at least one element");
    return absum(a)*1./numel(a);
  }
  
  public static double median(int[] a) {
    if (a == null) throw new IllegalArgumentException(
        "median: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "avg: array must have at least one element");
    int m = a.length/2;
    if (a.length % 2 == 1) return a[m];
    return (a[m-1] + a[m])*1./2;
  }
  
  public static double median(long[] a) {
    if (a == null) throw new IllegalArgumentException(
        "median: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "avg: array must have at least one element");
    int m = a.length/2;
    if (a.length % 2 == 1) return a[m];
    return (a[m-1] + a[m])*1./2;
  }
  
  public static double median(double[] a) {
    if (a == null) throw new IllegalArgumentException(
        "median: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "avg: array must have at least one element");
    int m = a.length/2;
    if (a.length % 2 == 1) return a[m];
    return (a[m-1] + a[m])/2;
  }

  //Ex1115Histogram
  public static void printArray(int[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArray: array can't be null");
    System.out.println(Arrays.toString(a).replaceAll(" ", ""));
  }

  public static void printArrayInCol(int[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArrayInCol: array can't be null");
    for (int i = 0; i < a.length; i++) System.out.printf("%-3d  %d\n",i,a[i]);
  }
  
  public static void printArray(long[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArray: array can't be null");
    System.out.println(Arrays.toString(a).replaceAll(" ", ""));
  }
  
  public static void printArrayInCol(long[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArrayInCol: array can't be null");
    for (int i = 0; i < a.length; i++) System.out.printf("%-3d  %d\n",i,a[i]);
  }

  public static void printArray(double[] a) {
    System.out.println(Arrays.toString(a));
  }

  public static void printArrayInCol(double[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArrayInCol: array can't be null");
    for (double o : a) System.out.println(o);
  }

  public static void printArray(Object[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArrayInCol: array can't be null");
    System.out.println(Arrays.toString(a).replaceAll(" ", ""));
  }

  public static void printArrayInCol(Object[] a) {
    if (a == null) throw new IllegalArgumentException(
        "printArrayInCol: array can't be null");
    for (Object o : a) System.out.println(o);
  }

  //Ex1111print2DArray
  public static void printArray(boolean[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "print2DBooleanArray: array can't be null");
    // assumes all rows have the same number of elements
    int r = a.length;
    String rs = "" +r;
    int relen = rs.length(); // left indent width
    int c = 0;
    String cs = null;
    int celen = 0;
    if (r > 0) {
      c = a[0].length;
      cs = "" + c;
      celen = cs.length(); // col width
    }
    if (r == 0 && c == 0) {
      System.out.println("empty array");
      return;
    }
    System.out.printf(space(relen));
    for (int j = 0; j < c; j++) {
      System.out.printf(" %"+celen+"d", j);
    }
    System.out.println();
    for (int i = 0; i < r; i++) {
      System.out.printf("%"+relen+"d", i);
      for (int j = 0; j < c; j++) {
        if (a[i][j] == true) {
          System.out.printf(" %"+celen+"s", "*");
        } else {
          System.out.printf(" %"+celen+"s", " ");
        }
      }
      System.out.println();
    }
    System.out.println();
  }

  //Ex1131PlotCircleRandomConnections
  public static void printArray(int[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "print2DIntArray: array can't be null");
    // assumes all rows have the same number of elements
    int len = a.length;
    int indexwidth = 0;
    int width = 0; // max number of elements in a row
    int maxelwidth = 0;
    if (len>0) {
      indexwidth = (""+a[0].length).length();
      for (int i = 0; i < len; i++) {
        if (a[i].length > width) width = a[i].length;
        for (int j = 0; j < a[i].length; j++) {
          int tmp = (""+a[i][j]).length();
          if (tmp > maxelwidth) maxelwidth = tmp;
        }
      }
    }
//    System.out.println("maxelwidth="+maxelwidth);
//    System.out.println("width="+width);
    if (len == 0 && maxelwidth == 0) {
      System.out.println("empty array");
      return;
    }

    int t = 0; // tmp data column width
    int c = 1; // data column width
    for (int i = 0; i < len; i++)
      for (int j = 0; j < a[i].length; j++) {
        t = String.format("%d", a[i][j]).length();
        if (t > c) c = t;
      }

    System.out.print(space(indexwidth+maxelwidth));
    for (int j = 0; j < width; j++) 
      System.out.printf(" %-"+c+"d", j);
    System.out.println();

    for (int i = 0; i < len; i++) {
      System.out.printf("%-"+c+"d", i);
      for (int j = 0; j < a[i].length; j++) 
        System.out.printf(" %"+c+"d", a[i][j]);
      System.out.println();
    }
    System.out.println();
  }

  public static void printArray(long[][] a) {
    if (a == null) throw new IllegalArgumentException(
        "print2DIntArray: array can't be null");
    // assumes all rows have the same number of elements
    int len = a.length;
    int indexwidth = 0;
    int width = 0; // max number of elements in a row
    int maxelwidth = 0;
    if (len>0) {
      indexwidth = (""+a[0].length).length();
      for (int i = 0; i < len; i++) {
        if (a[i].length > width) width = a[i].length;
        for (int j = 0; j < a[i].length; j++) {
          int tmp = (""+a[i][j]).length();
          if (tmp > maxelwidth) maxelwidth = tmp;
        }
      }
    }
//    System.out.println("maxelwidth="+maxelwidth);
//    System.out.println("width="+width);
    if (len == 0 && maxelwidth == 0) {
      System.out.println("empty array");
      return;
    }

    int t = 0; // tmp data column width
    int c = 0; // data column width
    for (int i = 0; i < len; i++)
      for (int j = 0; j < a[i].length; j++) {
        t = String.format("%d", a[i][j]).length();
        if (t > c) c = t;
      }

    System.out.print(space(indexwidth+maxelwidth));
    for (int j = 0; j < width; j++) 
      System.out.printf(" %-"+c+"d", j);
    System.out.println();

    for (int i = 0; i < len; i++) {
      System.out.printf("%-"+c+"d", i);
      for (int j = 0; j < a[i].length; j++) 
        System.out.printf(" %"+c+"d", a[i][j]);
      System.out.println();
    }
    System.out.println();
  }

  //Ex1131PlotCircleRandomConnections
  public static void printArray(double[][] a) {
    if (a == null) {
      System.out.println("empty array");
      return;
    }

    int len = a.length;
    int len2 = 0; // left index column width
    if (len>0) len2 = a[0].length;
    if (len == 0 && len2 == 0) {
      System.out.println("empty array");
      return;
    }

    int t = 0; // tmp data column width
    int c = 0; // data column width
    for (int i = 0; i < len; i++)
      for (int j = 0; j < len2; j++) {
        t = String.format("%.20f", a[i][j]).length();
        if (t > c) c = t;
      }

    System.out.print(""+space(len2));
    for (int j = 0; j < len2; j++) 
      System.out.printf(" %-"+c+"d", j);
    System.out.println();

    for (int i = 0; i < len; i++) {
      System.out.printf("%-"+len2+"d", i);
      for (int j = 0; j < len2; j++) 
        System.out.printf(" %"+c+".20f", a[i][j]);
      System.out.println();
    }
    System.out.println();
  }
  
  public static void printArray(Object[][] a) {
    System.out.println(deepToString(a).replaceAll(" ", "").replaceAll("],", "]\n "));
  }
  
  public static void printArray(int[][][] a) {
    System.out.println(deepToString(a).replaceAll(" ", "").replaceAll("]],", "]\n "));
  }
  
  public static void printArray(long[][][] a) {
    System.out.println(deepToString(a).replaceAll(" ", "").replaceAll("]],", "]\n "));
  }
  
  public static void printArray(double[][][] a) {
    System.out.println(deepToString(a).replaceAll(" ", "").replaceAll("]],", "]\n "));
  }
  
  public static void printArray(Object[][][] a) {
    System.out.println(deepToString(a).replaceAll(" ", "").replaceAll("]],", "]\n "));
  }

  public static void printDeep(Object[] a) {
    if (dim(a)>0) {
      System.out.println(Arrays.deepToString(a).replaceAll(" ", ""));
    } else throw new IllegalArgumentException("printDeep: "
        + "array must have more than 1 dimension");
  }
  
  public static void printIt(Object a) {
    int dim = dim(a);
    if (dim == 0) { // this handles a=null, primitives and non array objects 
      System.out.println(a);
      return;
    }
    if (dim > 1) {
      System.out.println(Arrays.deepToString((Object[])a).replaceAll(" ", ""));
      return;
    }
    // dim = 1
    switch (a.getClass().getComponentType().getName()) {
      case "int":
        {int[] q = (int[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "long":
        {long[] q = (long[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "double":
        {double[] q = (double[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "byte":
        {byte[] q = (byte[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "short":
        {short[] q = (short[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "float":
        {float[] q = (float[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "boolean":
        {boolean[] q = (boolean[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}    
      case "char":
        {char[] q = (char[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}
      default: 
        {Object[] q = (Object[]) a;
        System.out.println(Arrays.toString(q).replaceAll(" ", ""));
        break;}
    }
  } 


  //Ex1113print2DArrayTranspose
  public static void printTransposition(int[][] m) {
    int n = m.length;
    int o = m[0].length;
    int max = 0;
    int len = 0;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
        len = (""+m[i][j]).length();
        if (len > max) max = len;    
      }
    }
    max = max+1;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
        System.out.printf("%-"+max+"d", m[i][j]);       
      }
      System.out.println();
    }
  }

  //Ex1128BinSearchUpdateWhitelist
  public static boolean hasDups(int[] a) {
    int[] c = Arrays.copyOf(a, a.length);
    Arrays.sort(c);
    for (int i = 0; i < c.length-1; i++)
      if (c[i] == c[i+1]) return true;
    return false;
  }
  
  public static int dim (Object o) {
    // if o has an array type return its number of dimensions
    // else if it's null return -1 else return 0
    if (o == null ) return -1;
    if (!o.getClass().isArray()) return 0;
    return dim(o.getClass());
  }
  
  public static int dim (Class<?> type) {
    // if type is an array type return its number of dimensions
    // else if it's null return -1 else return 0
    if (type == null) return -1;
    if (!type.isArray()) return 0;
    //return dim(type.getComponentType()) + 1; // works 
    //return type.getName().replaceAll("^\\[","").length(); // works
    Matcher matcher = Pattern.compile("(^[\\[]+)").matcher(type.getName());
    if (matcher.find()) return matcher.group(1).length();
    return 0;
  }
  
  public static Class<?> arrayType(Class<?> componentType, int dimensions)
      throws ClassNotFoundException {
   //http://stackoverflow.com/questions/23230854/getting-unknown-number-of-dimensions-from-a-multidimensional-array-in-java
    if (dimensions == 0) return componentType;
    String rawName = componentType.getName();
    switch (rawName) {
      case "byte":    rawName = "B"; break;
      case "char":    rawName = "C"; break;
      case "double":  rawName = "D"; break;
      case "float":   rawName = "F"; break;
      case "int":     rawName = "I"; break;
      case "long":    rawName = "J"; break;
      case "short":   rawName = "S"; break;
      case "boolean": rawName = "Z"; break;
      default:        rawName = "L" + rawName + ";"; break;
    }
    for (int i = 0; i < dimensions; i++) rawName = "[" + rawName;
    return Class.forName(rawName);
  }
 
  public static String rootComponentType(Object o) {
    // returns a string representing the base component type of an array.
    // for example if o is an int[][][] its rootComponentType is int
    // whereas its componentType is int[][].
    if (o == null) return "null";
    int dim = dim(o);
    if (dim == 0) return o.getClass().getName();
    if (!o.getClass().isArray()) throw new IllegalArgumentException(
        "rootComponentType: argument must be an array");
    String w = o.getClass().getName().replaceFirst("^[\\[]+","");
    switch (w) {
      case "B": return "byte";
      case "C": return "char";
      case "D": return "double";
      case "F": return "float";
      case "I": return "int";
      case "J": return "long";
      case "S": return "short";
      case "Z": return "boolean";
      default : return w.substring(1,w.length()-1);
    }
  }
  
  
  public static Object createArray(Class<?> componentType, int dimensions, int length)
      throws NegativeArraySizeException, ClassNotFoundException {
    //http://stackoverflow.com/questions/23230854/getting-unknown-number-of-dimensions-from-a-multidimensional-array-in-java
    if (dimensions == 0) return null;
    Object array = Array.newInstance(arrayType(componentType, dimensions-1), length);
    for (int i = 0; i < length; i++)
        Array.set(array, i, createArray(componentType, dimensions-1, length));
    return array;
  }
  
  @SafeVarargs
  public static int[] dims(Object o, int[]...d) {
    // assume all elements of each dimension except the bottom have 
    // the same non zero length;
    // if any dimension except the bottom has zero length insert 0
    // in the returned array and stop the recursion
    int dim = dim(o);
//    System.out.println("dim="+dim);
    if (!(dim>0 && o.getClass().isArray())) throw new IllegalArgumentException(
        "dims: argument must be an array");
    
    Object[] a = null;
    if (isPrimitiveArray(o)) {
      a = (Object[]) box(o);
    } else a = (Object[]) o;
    
    int[] r = null;
    if (d.length == 0) {
      r = new int[dim];
    } else r = d[0];
    
    r[r.length-dim] = a.length;
    
    if (a.length == 0) {
      for (int i = dim; i < r.length; i++) r[i] = -1;
      return r;
    }
    
    if (dim == 1) {
      return r;  
    } else {
      dims(a[0],r);
    }
   
    return new int[]{-1};
  }
  
  
  //Ex1128BinSearchUpdateWhitelist
  //  public static int[] removeDups(int[] a) {
  //    if (a == null || a.length < 2) return a;
  //    Arrays.sort(a);
  //    int c = 0;
  //    int[] b = new int[a.length];
  //    for (int i = 0; i < a.length-1; i++)
  //      if (a[i] == a[i+1]) {
  //        if (i+1 == a.length-1) b[c++] = a[i];
  //        continue;
  //      } else b[c++] = a[i];
  //    return Arrays.copyOf(b, c);
  //  }

  public static boolean in(int[] a, int v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) return true;
    return false;
  }
  
  public static boolean in(long[] a, long v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) return true;
    return false;
  }

  public static boolean in(double[] a, double v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) return true;
    return false;
  }
  
  public static boolean in(char[] a, char v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) return true;
    return false;
  }
  
  public static <T> boolean in(T[] a, T v) {
    for (int i = 0; i < a.length; i++)
      if (v == null || a[i] == null) {
        if (v == a[i]) return true;
      } else if (a[i].equals(v)) return true;
    return false;
  }

  public static int[] unique(int[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2) return (int[]) clone(a);
    int[] b = new int[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b,a[i])) b[bindex++] = a[i];
    int[] c = new int[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }
  
  public static long[] unique(long[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2) return (long[]) clone(a);
    long[] b = new long[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b,a[i])) b[bindex++] = a[i];
    long[] c = new long[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static double[] unique(double[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2) return (double[]) clone(a);
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (!in(b,a[i])) b[bindex++] = a[i];
    double[] c = new double[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] unique(T[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2) return (T[]) clone(a);
    T[] b = makeGen1D(a.length);
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (!in(b,a[i])) b[bindex++] = a[i];
    T[] c = makeGen1D(bindex);
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static int[] uniqueSort(int[] a) {
    // functional: preserves a
    if (a == null) throw new IllegalArgumentException(
        "uniqueSort: a can't be null");
    if (a.length < 2) return (int[]) clone(a);
    return unique(copySort(a));
  }
  
  public static long[] uniqueSort(long[] a) {
    // functional: preserves a
    if (a == null) throw new IllegalArgumentException(
        "uniqueSort: a can't be null");
    if (a.length < 2) return (long[]) clone(a);
    return unique(copySort(a));
  }

  public static double[] uniqueSort(double[] a) {
    // functional: preserves a
    if (a == null) throw new IllegalArgumentException(
        "uniqueSort: a can't be null");
    if (a.length < 2) return (double[]) clone(a);
    return unique(copySort(a));
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> T[] uniqueSort(T[] a) {
    // functional: preserves a
    if (a == null) throw new IllegalArgumentException(
        "uniqueSort: a can't be null");
    if (a.length < 2) return (T[]) clone(a);
    return unique(copySort(a));
  }
  
  // from BinarySearch.java
  public static int indexOf(int[] a, int key) {
    // assumes a is sorted
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  
  public static int indexOf(long[] a, long key) {
    // assumes a is sorted
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }

  public static int indexOf(double[] a, double key) {
    // assumes a is sorted
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  
  public static <T extends Comparable<? super T>> int indexOf(T[] a, T key) {
    // assumes a is sorted
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key.compareTo(a[mid]) < 0) hi = mid - 1;
      else if (key.compareTo(a[mid]) > 0) lo = mid + 1;
      else return mid;
    }
    return -1;
  }

  public static boolean contains(int[] a, int key) {
    int i = indexOf(a, key);
    if (i == -1) return false;
    return true;
  }

  public static boolean contains(long[] a, long key) {
    long i = indexOf(a, key);
    if (i == -1) return false;
    return true;
  }
  //<T extends Comparable<? super T>>
  public static boolean contains(double[] a, double key) {
    double i = indexOf(a, key);
    if (i == -1) return false;
    return true;
  }
  
  public static <T extends Comparable<? super T>> boolean contains(T[] a, T key) {
    int i = indexOf(a, key);
    if (i == -1) return false;
    return true;
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static int rank(int[] a, int key) {
    // counts number of occurrences of elements < key
    // for sorted array
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i] < key) c++;
    }
    return c; // key not in a
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static int countsorted(int[] a, int key) {
    // counts number of occurrences of key in a
    return rank(a, key+1) - rank(a, key);
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static int count(int[] a, int key) {
    // for unsorted array counts number of occurrences of key in a
    int c = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] == key) c++;
    return c;
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static void printOccurencesOfValue(int[] a, int v) {
    // assumes a is sorted
    int c = countsorted(a, v);
    if (c==0) return;
    for (int i = 0; i < c-1; i++)
      System.out.print(v+",");
    System.out.println(v);
  }

  //  public static void printOccurencesOfValue(int[] a, int value) {
  //    // assumes a is sorted
  //    if (countsorted(a, value) == 0) return;
  //    for (int i = rank(a,value); i < rank(a,value)+countsorted(a,value)-1; i++)
  //      System.out.print(a[i]+",");
  //    System.out.println(a[rank(a,value)+countsorted(a,value)-1]);
  //  }

  //Ex1133MatrixLibrary
  public static int dot(int[] x, int[] y) {
    // dot product
    assert x != null;  assert y!= null;
    int n = x.length;  assert n != 0;
    assert n == y.length;

    int sum = 0;
    for (int i = 0; i < x.length; i++) 
      sum+=x[i]*y[i];
    return sum;
  }

  //Ex1133MatrixLibrary
  public static double dot(double[] x, double[] y) {
    // dot product
    assert x != null;  assert y!= null;
    int n = x.length;  assert n != 0;
    assert n == y.length;

    double sum = 0;
    for (int i = 0; i < x.length; i++) 
      sum+=x[i]*y[i];
    return sum;
  }

  //Ex1133MatrixLibrary
  public static int[][] matrixProduct(int[][] a, int[][] b) {
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; int a0len = a[0].length;
    int blen = b.length; int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    int[][] c = new int[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) { 
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }

  //Ex1133MatrixLibrary
  public static int[] matrixProduct(int[] a, int[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (alen != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    int[] c = new int[b0len];
    for (int i = 0; i < b0len; i++)
        for (int k = 0; k < alen; k++)
          c[i] += a[k]*b[k][i];
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static int[] matrixProduct(int[][] a, int[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all rows of the first array (a) must have the same length");

    int[] c = new int[alen];
    for (int i = 0; i < alen; i++)
        for (int k = 0; k < blen; k++)
          c[i] += a[i][k]*b[k];
    return c;
  }
  
//Ex1133MatrixLibrary
  public static long[][] matrixProduct(long[][] a, long[][] b) {
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; int a0len = a[0].length;
    int blen = b.length; int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    long[][] c = new long[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) { 
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static long[] matrixProduct(long[] a, long[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (alen != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    long[] c = new long[b0len];
    for (int i = 0; i < b0len; i++)
        for (int k = 0; k < alen; k++)
          c[i] += a[k]*b[k][i];
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static long[] matrixProduct(long[][] a, long[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all rows of the first array (a) must have the same length");

    long[] c = new long[alen];
    for (int i = 0; i < alen; i++)
        for (int k = 0; k < blen; k++)
          c[i] += a[i][k]*b[k];
    return c;
  }

//Ex1133MatrixLibrary
  public static double[][] matrixProduct(double[][] a, double[][] b) {
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; int a0len = a[0].length;
    int blen = b.length; int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    double[][] c = new double[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) { 
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static double[] matrixProduct(double[] a, double[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (alen != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    double[] c = new double[b0len];
    for (int i = 0; i < b0len; i++)
        for (int k = 0; k < alen; k++)
          c[i] += a[k]*b[k][i];
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static double[] matrixProduct(double[][] a, double[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all rows of the first array (a) must have the same length");

    double[] c = new double[alen];
    for (int i = 0; i < alen; i++)
        for (int k = 0; k < blen; k++)
          c[i] += a[i][k]*b[k];
    return c;
  }
  
//Ex1133MatrixLibrary
  public static <T> T[][] matrixProduct(T[][] a, T[][] b,
      BinaryOperator<T> add,  BinaryOperator<T> mult ) {
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; int a0len = a[0].length;
    int blen = b.length; int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    T[][] c = makeGen2D(alen, b0len);
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) { 
        for (int k = 0; k < blen; k++)
          c[i][j] = add.apply(c[i][j], mult.apply(a[i][k], b[k][j]));
      }
    return c;
  }
  
  //Ex1133MatrixLibrary
  public static <T> T[] matrixProduct(T[] a, T[][] b,
      BinaryOperator<T> add,  BinaryOperator<T> mult ) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (alen != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len) throw new IllegalArgumentException(
          "all rows of the second array (a) must have the same length");

    T[] c = makeGen1D(b0len, b[0]);
    for (int i = 0; i < b0len; i++)
        for (int k = 0; k < alen; k++)
          c[i] = add.apply(c[i], mult.apply(a[k], b[k][i]));
    return c;
  }
  
  
  //Ex1133MatrixLibrary
  public static <T> T[] matrixProduct(T[][] a, T[] b, 
      BinaryOperator<T> add,  BinaryOperator<T> mult ) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null) throw new IllegalArgumentException(
        "matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0) throw new IllegalArgumentException(
        "matrixProduct: both arrays must have length > 0");
    if (a0len != blen) throw new IllegalArgumentException(
        "matrixProduct: the number of columns of the first array (a) "
        + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len) throw new IllegalArgumentException(
          "all rows of the first array (a) must have the same length");

    T[] c = makeGen1D(alen, a[0]);
    for (int i = 0; i < alen; i++)
        for (int k = 0; k < blen; k++)
          c[i] = add.apply(c[i], mult.apply(a[i][k], b[k]));
    return c;
  }

 


  //Ex1133MatrixLibrary
  public static double[] mult(double[][] a, double[] x) {
    // matrix-vector product
    assert a != null; assert x != null;
    int n = a.length; assert n != 0;
    int o = x.length; assert o != 0;
    assert a[0].length == o;

    double[] b = new double[n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++) 
        b[i] += a[i][j]*x[j];
    return b;
  }

  //Ex1133MatrixLibrary
  public static double[] mult(double[] y, double[][] a) {
    //vector-matrix product
    assert a != null; assert y != null;
    int n = a.length; assert n != 0;
    int o = y.length; assert o != 0;
    assert n == o;    assert a[0].length != 0;

    double[] b = new double[a[0].length];
    for (int i = 0; i < a[0].length; i++)
      for (int j = 0; j < o; j++)
        b[i] +=  y[j]*a[j][i];
    return b; 
  }

  //Ex1126SwapSort
  public static void swapSort(int[] a) {
    // not functional: modifies a
    assert a!= null; assert a.length != 0;
    int t;
    for (int i = 0; i < a.length - 1; i++)
      for (int j = i+1; j < a.length; j++)
        if (a[i] > a[j]) {t = a[i]; a[i] = a[j]; a[j] = t;}
  }

  public static void swapSort(long[] a) {
    // not functional: modifies a
    assert a!= null; assert a.length != 0;
    long t;
    for (int i = 0; i < a.length - 1; i++)
      for (int j = i+1; j < a.length; j++)
        if (a[i] > a[j]) {t = a[i]; a[i] = a[j]; a[j] = t;}
  }
  
  public static void swapSort(double[] a) {
    // not functional: modifies a
    assert a!= null; assert a.length != 0;
    double t;
    for (int i = 0; i < a.length - 1; i++)
      for (int j = i+1; j < a.length; j++)
        if (a[i] > a[j]) {t = a[i]; a[i] = a[j]; a[j] = t;}
  }

  public static int[] copySort(int[] a) {
    // does swapSort on a copy of a
    // functional: preserves a
    assert a!= null; assert a.length != 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    int t;
    for (int i = 0; i < b.length - 1; i++)
      for (int j = i+1; j < b.length; j++)
        if (b[i] > b[j]) {t = b[i]; b[i] = b[j]; b[j] = t;}
    return b;
  }

  public static long[] copySort(long[] a) {
    // does swapSort on a copy of a
    // functional: preserves a
    assert a!= null; assert a.length != 0;
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    long t;
    for (int i = 0; i < b.length - 1; i++)
      for (int j = i+1; j < b.length; j++)
        if (b[i] > b[j]) {t = b[i]; b[i] = b[j]; b[j] = t;}
    return b;
  }
  
  public static double[] copySort(double[] a) {
    // does swapSort on a copy of a
    // functional: preserves a
    assert a!= null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    double t;
    for (int i = 0; i < b.length - 1; i++)
      for (int j = i+1; j < b.length; j++)
        if (b[i] > b[j]) {t = b[i]; b[i] = b[j]; b[j] = t;}
    return b;
  }
  
  public static <T extends Comparable<? super T>> T[] copySort(T[] a) {
    // does swapSort on a copy of a
    // functional: preserves a
    assert a!= null; assert a.length != 0;
    T[] b = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    T t;
    for (int i = 0; i < b.length - 1; i++)
      for (int j = i+1; j < b.length; j++)
        if (b[i].compareTo(b[j]) > 0) {t = b[i]; b[i] = b[j]; b[j] = t;}
    return b;
  }

  //Ex1134Filtering
  public static int[] bitSortUnique(int[] a) {
    // functional: does not modify a
    // uses bits in a new int[] to track existing elements without presort
    assert a != null;
    if (a.length == 0) return a;
    int[] b = new int[max(a)/32+1]; // b max size for use as bit set
    for (int i = 0; i < a.length; i++) set(a[i],b);
    return bits(b);
  }

  //http://www.programcreek.com/2012/11/quicksort-array-in-java/
  public static void quickSort(int[] a, int low, int high) {
    // not functional: in place sort may modify a
    // called by quickSort(int[] a) and  quickSortCopy(int[] a)
    assert a != null;  assert a.length > 0;
    assert low < high;
    // pick the pivot
    int middle = low + (high - low) / 2;
    int pivot = a[middle];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot) i++;
      while (a[j] > pivot) j--; 
      if (i <= j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    } 
    // recursively sort two sub parts
    if (low < j) quickSort(a, low, j);
    if (high > i) quickSort(a, i, high);
  }

  public static void quickSort(int[] a) {
    // not functional: in place sort may modify a
    // calls quickSort(int[] a, int low, int high)
    assert a != null;  assert a.length > 0;
    if (a.length < 2) return;
    int low = 0; int high = a.length-1;
    assert low < high;  
    // pick the pivot
    int pivot = a[low + (high - low) / 2];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot) i++;
      while (a[j] > pivot) j--; 
      if (i <= j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j) quickSort(a, low, j);
    if (high > i) quickSort(a, i, high);
  }

  public static int[] quickSortCopy(int[] a) {
    // functional: does not modify a
    // calls quickSort(int[] a, int low, int high)
    assert a != null;  assert a.length > 0;
    if (a.length < 2) return (int[]) clone(a);
    int low = 0; int high = a.length-1;
    assert low < high;  
    a = (int[]) clone(a);
    // pick the pivot
    int pivot = a[low + (high - low) / 2];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot) i++;
      while (a[j] > pivot) j--; 
      if (i <= j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j) quickSort(a, low, j);
    if (high > i) quickSort(a, i, high);
    return a;
  }

  //http://www.programcreek.com/2012/11/quicksort-array-in-java/
  public static void quickSort(double[] a, int low, int high) {
    // not functional: in place sort may modify a
    // called by quickSort(double[] a) and quickSortCopy(double[] a)
    assert a != null;  assert a.length > 0;
    assert low < high;
    // pick the pivot
    int middle = low + (high - low) / 2;
    double pivot = a[middle];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot) i++;
      while (a[j] > pivot) j--; 
      if (i <= j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    } 
    // recursively sort two sub parts
    if (low < j) quickSort(a, low, j);
    if (high > i) quickSort(a, i, high);
  }
  public static double[] quickSort(double[] a) {
    // functional: a sorted clone of a is returned
    // calls quickSort(double[] a, int low, int high)
    assert a != null;  assert a.length > 0;
    if (a.length < 2) return (double[]) clone(a);
    int low = 0; int high = a.length-1;
    assert low < high;
    a = (double[]) clone(a); 
    // pick the pivot
    double pivot = a[low + (high - low) / 2];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot) i++;
      while (a[j] > pivot) j--; 
      if (i <= j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j) quickSort(a, low, j);
    if (high > i) quickSort(a, i, high);
    return a;
  }

  public static void swap(int a[], int i, int j) {
    assert a != null; assert a.length != 0;
    assert i >= 0 &&  i <= a.length-1;
    assert j >= 0 &&  j <= a.length-1;
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp; 
  }
  
  public static void swap(long a[], int i, int j) {
    assert a != null; assert a.length != 0;
    assert i >= 0 &&  i <= a.length-1;
    assert j >= 0 &&  j <= a.length-1;
    long tmp = a[i];
    a[i] = a[j];
    a[j] = tmp; 
  }

  public static void swap(double a[], int i, int j) {
    assert a != null; assert a.length != 0;
    assert i >= 0 &&  i <= a.length-1;
    assert j >= 0 &&  j <= a.length-1;
    double tmp = a[i];
    a[i] = a[j];
    a[j] = tmp; 
  }

  public static int[] make(int...e) {
    return e;
  }

  public static double[] make(double...e) {
    return e;
  }

  public static int[] make(int len, int fill) {
    // fill is the value to fill (not a number of elements to not fill)
    int[] a = new int[len];
    for (int i = 0; i < len; i++) a[i] = fill;
    return a;
  }

  public static double[] make(int len, double fill) {
    // fill is the value to fill (not a number of elements to not fill)
    double[] a = new double[len];
    for (int i = 0; i < len; i++) a[i] = fill;
    return a;
  }

  public static int[] make(int len, int...e) {
    // puts e[]'s elements at beginning
    int elen = e.length;
    if (elen == len) return e;
    int[] a = new int[len];
    int lim = elen < len ? elen : len;
    for (int i = 0; i < lim; i++) a[i] = e[i];
    return a;
  }

  public static int[] makebefore(int len, int...e) {
    return make(len, e);
  }

  public static double[] make(int len, double...e) {
    // puts e[]'s elements at beginning
    int elen = e.length;
    if (elen == len) return e;
    double[] a = new double[len];
    int lim = elen < len ? elen : len;
    for (int i = 0; i < lim; i++) a[i] = e[i];
    return a;
  }

  public static double[] makebefore(int len, double...e) {
    return make(len, e);
  }

  public static int[] make(int len, boolean before, int...e) {
    // before means put unfilled elements at beginning if elen < len
    int elen = e.length;
    if (elen == len) return e;
    int[] a = new int[len];
    if (elen < len) {
      if (before) {
        for (int i = len-elen; i < len ; i++)
          a[i] = e[i-len+elen];
      } else  {
        for (int i = 0; i < elen ; i++)
          a[i] = e[i];
      }
    } else if (elen > len) {
      for (int i = 0; i < len; i++) 
        a[i] = e[i];
    }
    return a;
  }

  public static int[] makeafter(int len, int...e) {
    return make(len, true, e);
  }

  public static double[] make(int len, boolean before, double...e) {
    // before means to put e after unfilled elements if elen < len
    int elen = e.length;
    if (elen == len) return e;
    double[] a = new double[len];
    if (elen < len) {
      if (before) {
        for (int i = len-elen; i < len ; i++)
          a[i] = e[i-len+elen];
      } else  {
        for (int i = 0; i < elen ; i++)
          a[i] = e[i];
      }
    } else if (elen > len) {
      for (int i = 0; i < len; i++) 
        a[i] = e[i];
    }
    return a;
  }

  public static double[] makeafter(int len, double...e) {
    return  make(len, true, e);
  }

  public static int[] make(int len, int fill, int...e) {
    // fill is the value to fill (not a number of elements to not fill)
    // puts e[]'s elements at beginning
    int elen = e.length;
    if (elen == len) return e;
    int[] a = new int[len];
    //    int lim = elen < len ? elen : len;
    if (elen < len) {
      for (int i = 0; i < elen; i++) a[i] = e[i];
      for (int i = elen; i < len; i++) a[i] = fill;
    } else if (elen > len)
      for (int i = 0; i < len; i++) a[i] = e[i];
    return a;
  }

  public static double[] make(int len, double fill, double...e) {
    // fill is the value to fill (not a number of elements to not fill)
    // puts e[]'s elements at beginning
    int elen = e.length;
    if (elen == len) return e;
    double[] a = new double[len];
    //    int lim = elen < len ? elen : len;
    if (elen < len) {
      for (int i = 0; i < elen; i++) a[i] = e[i];
      for (int i = elen; i < len; i++) a[i] = fill;
    } else if (elen > len)
      for (int i = 0; i < len; i++) a[i] = e[i];
    return a;
  }

  public static int[] make(int len, boolean before, int fill, int...e) {
    // fill is the value to fill (not a number of elements to not fill)
    // before means put fill elements at beginning if elen < len
    int elen = e.length;
    if (elen == len) return e;
    int[] a = new int[len];
    if (elen < len) {
      if (before) {
        for (int i = 0; i < len-elen-1 ; i++)
          a[i] = fill;
        for (int i = len-elen; i < len ; i++)
          a[i] = e[i-len+elen];
      } else  {
        for (int i = 0; i < elen ; i++)
          a[i] = e[i];
        for (int i = elen; i < len ; i++)
          a[i] = fill;
      }
    } else if (elen > len) {
      for (int i = 0; i < len; i++) 
        a[i] = e[i];
    }
    return a;
  }
  
  @SafeVarargs
  public static <Q> Q[] makeGen1D(int n, Q...q) {
    if (n < 0) throw new IllegalArgumentException(
        "makeGen1D: n must be >= 0");
    // this creates an Object[] if q is empty
    return copyOf(q, n);
  }
  
  @SafeVarargs
  public static <Q> Q[][] makeGen2D(int r, int c, Q[]...q) {
    if (r < 0) throw new IllegalArgumentException(
        "makeGen2D: r must be >= 0");
    // this creates an Object[] if q is empty
    Q[][] v = copyOf(q, r);
    for (int i = 0; i < r; i++) v[i] = makeGen1D(c);
    return v;
  }
  
  @SafeVarargs
  public static <Q> Q[][][] makeGen3D(int r, int c, int z, Q[][]...q) {
    if (r < 0) throw new IllegalArgumentException(
        "makeGen3D: n must be >= 0");
    // this creates an Object[] if q is empty
    Q[][][] v = copyOf(q, r);
    for (int i = 0; i < r; i++) v[i] = makeGen2D(c, z);
    return v;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] makeFromList(final List<T> obj) {
    if (obj == null || obj.isEmpty()) return null;
    final T t = obj.get(0);
    final T[] res = (T[]) Array.newInstance(t.getClass(), obj.size());
    for (int i = 0; i < obj.size(); i++) res[i] = obj.get(i);
    return res;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] makeFromValue(T t, int n) {
    // refinement of makeFromList
    if (t == null)  {
      System.out.println("makeFromValue t is null");
      return null;
    }
    final T[] r = (T[]) Array.newInstance(t.getClass(), n);
    for (int i = 0; i < n; i++) r[i] = t;
    return r;
  }
  
  public static double determinant(int[][] a) {
    // a must be a square matrix
    if (a == null) throw new IllegalArgumentException(
        "determinant: the array must be not be null");
    int n = order(a);
    if (n < 0) throw new IllegalArgumentException(
        "determinant: a must be a square array");
    if (n == 0) throw new IllegalArgumentException(
        "determinant: a must have at least one element");
    
    double det=0;
    if(n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0]*a[1][1] - a[1][0]*a[0][1];
    } else {
      det=0;
      for(int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n-1][];
        for(int k = 0; k < n-1; k++) {
          m[k] = new double[n-1];
        }
        for(int i = 1; i < n; i++) {
          int j2=0;
          for(int j = 0; j < n; j++) {
            if(j == j1) continue;
            m[i-1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0,1.0+j1+1.0)* a[0][j1] * determinant(m);
      }
    }
    return det;
  }
  
  public static double determinant(long[][] a) {
    // a must be a square matrix
    if (a == null) throw new IllegalArgumentException(
        "determinant: the array must be not be null");
    int n = order(a);
    if (n < 0) throw new IllegalArgumentException(
        "determinant: a must be a square array");
    if (n == 0) throw new IllegalArgumentException(
        "determinant: a must have at least one element");
    
    double det=0;
    if(n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0]*a[1][1] - a[1][0]*a[0][1];
    } else {
      det=0;
      for(int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n-1][];
        for(int k = 0; k < n-1; k++) {
          m[k] = new double[n-1];
        }
        for(int i = 1; i < n; i++) {
          int j2=0;
          for(int j = 0; j < n; j++) {
            if(j == j1) continue;
            m[i-1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0,1.0+j1+1.0)* a[0][j1] * determinant(m);
      }
    }
    return det;
  }

  // http://www.sanfoundry.com/java-program-compute-determinant-matrix/
  public static double determinant(double[][] a) {
    // a must be a square matrix
    if (a == null) throw new IllegalArgumentException(
        "determinant: the array must be not be null");
    int n = order(a);
    if (n < 0) throw new IllegalArgumentException(
        "determinant: a must be a square array");
    if (n == 0) throw new IllegalArgumentException(
        "determinant: a must have at least one element");
   
    double det=0;
    if(n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0]*a[1][1] - a[1][0]*a[0][1];
    } else {
      det=0;
      for(int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n-1][];
        for(int k = 0; k < n-1; k++) {
          m[k] = new double[n-1];
        }
        for(int i = 1; i < n; i++) {
          int j2=0;
          for(int j = 0; j < n; j++) {
            if(j == j1) continue;
            m[i-1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0,1.0+j1+1.0)* a[0][j1] * determinant(m);
      }
    }
    return det;
  }

  public static int order(int[][] a) {
    // return order of a if it's square else return -1
    if (a == null) throw new IllegalArgumentException(
        "order: the array must be not be null");
    int n = a.length;
    if (n == 0) return 0;
    int a0len = a[0].length;
    if (n != a0len) return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len) return -1;
    return n;
  }
  
  public static int order(long[][] a) {
    // return order of a if it's square else return -1
    if (a == null) throw new IllegalArgumentException(
        "order: the array must be not be null");
    int n = a.length;
    if (n == 0) return 0;
    int a0len = a[0].length;
    if (n != a0len) return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len) return -1;
    return n;
  }
  
  public static int order(double[][] a) {
    // return order of a if it's square else return -1
    if (a == null) throw new IllegalArgumentException(
        "order: the array must be not be null");
    int n = a.length;
    if (n == 0) return 0;
    int a0len = a[0].length;
    if (n != a0len) return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len) return -1;
    return n;
  }
  
  public static boolean isSquare(int[][] a) {
    if (order(a) == -1) return false;
    return true;
  }
  
  public static boolean isSquare(long[][] a) {
    if (order(a) == -1) return false;
    return true;
  }
  
  public static boolean isSquare(double[][] a) {
    if (order(a) == -1) return false;
    return true;
  }
  
  //http://www.sanfoundry.com/java-program-find-inverse-matrix/
  public static double[][] invert(double a[][]) {
    // returns the inverse of a if it has one
    int n = a.length;
    double x[][] = new double[n][n];
    double b[][] = new double[n][n];
    int index[] = new int[n];
    for (int i=0; i<n; ++i) b[i][i] = 1;

    // Transform the matrix into an upper triangle
    gaussian(a, index);
    
    // Update the matrix b[i][j] with the ratios stored
    for (int i=0; i<n-1; ++i)
      for (int j=i+1; j<n; ++j)
        for (int k=0; k<n; ++k)
          b[index[j]][k]-= a[index[j]][i]*b[index[i]][k];

    // Perform backward substitutions
    for (int i=0; i<n; ++i) {
      x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
      for (int j=n-2; j>=0; --j) {
        x[j][i] = b[index[j]][i];
        for (int k=j+1; k<n; ++k) {
          x[j][i] -= a[index[j]][k]*x[k][i];
        }
        x[j][i] /= a[index[j]][j];
      }
    }
    return x;
  }

  //http://www.sanfoundry.com/java-program-find-inverse-matrix/
  public static void gaussian(double a[][], int index[]) {
  // helper method for inverse to carry out the partial-pivoting Gaussian 
  // elimination. index[] stores pivoting order.
    int n = index.length;
    double c[] = new double[n];

    // Initialize the index
    for (int i=0; i<n; ++i) index[i] = i;

    // Find the rescaling factors, one from each row
    for (int i=0; i<n; ++i) {
      double c1 = 0;
      for (int j=0; j<n; ++j) {
        double c0 = Math.abs(a[i][j]);
        if (c0 > c1) c1 = c0;
      }
      c[i] = c1;
    }

    // Search the pivoting element from each column
    int k = 0;
    for (int j=0; j<n-1; ++j) {
      double pi1 = 0;
      for (int i=j; i<n; ++i) {
        double pi0 = Math.abs(a[index[i]][j]);
        pi0 /= c[index[i]];
        if (pi0 > pi1) {
          pi1 = pi0;
          k = i;
        }
      }

      // Interchange rows according to the pivoting order
      int itmp = index[j];
      index[j] = index[k];
      index[k] = itmp;
      for (int i=j+1; i<n; ++i) {
        double pj = a[index[i]][j]/a[index[j]][j];
        // Record pivoting ratios below the diagonal
        a[index[i]][j] = pj;
        // Modify other elements accordingly
        for (int l=j+1; l<n; ++l)
          a[index[i]][l] -= pj*a[index[j]][l];
      }
    }
  }
  
  //http://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java

  
  public static long numberOfCombinationsLong(int n, int r) {
    // returns the number of possible combinations of r objects 
    // from a set of n objects
    if (n <= 1) throw new IllegalArgumentException(
        "numberOfCombinations: n can't be less than 1");
//    if (n > 21) throw new IllegalArgumentException(
//        "numberOfCombinations: cannot compute for n > 21");
    if (r > n) throw new IllegalArgumentException(
        "numberOfCombinations: r can't be greater than n");
    return (long) factorial(n)/(factorial(r)*factorial(n-r));
  }
  
  public static BigInteger numberOfCombinations(int n, int r) {
  // returns the number of possible combinations of r objects 
  // from a set of n objects
  if (n <= 1) throw new IllegalArgumentException(
      "numberOfCombinations: n can't be less than 1");
  if (r > n) throw new IllegalArgumentException(
      "numberOfCombinations: r can't be greater than n");
    MathContext mc = MathContext.DECIMAL128;
    BigInteger bn = new BigInteger(""+n);
    BigInteger br = new BigInteger(""+r);
    BigInteger bnr = new BigInteger(""+(n-r));
    BigDecimal factn = new BigDecimal(factorial(bn), mc);
    BigDecimal factr = new BigDecimal(factorial(br), mc);
    BigDecimal factnr = new BigDecimal(factorial(bnr), mc);
    BigInteger nc = factn.divide(factr.multiply(factnr, mc), mc)
        .round(mc).toBigInteger();
    return nc;
  }
  
  // translating scala.Array methods for int and double Java arrays

  public static void scalaArrayStuff() {
    // marker method
  }
  
  public static byte[] add(byte[] a, byte...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (b == null || b.length == 0) return a;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    byte[] c = new byte[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  public static short[] add(short[] a, short...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (b == null || b.length == 0) return a;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    short[] c = new short[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  public static int[] add(int[] a, int...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (b == null || b.length == 0) return a;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    int[] c = new int[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }

  public static long[] add(long[] a, long...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    long[] c = new long[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }

  public static float[] add(float[] a, float...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    float[] c = new float[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  public static double[] add(double[] a, double...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    double[] c = new double[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  public static char[] add(char[] a, char...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    char[] c = new char[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  public static boolean[] add(boolean[] a, boolean...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    if (a.length + b.length > maxlen) throw new IllegalArgumentException(
        "add: the sum of the length of both arrays exceeds the VM limit");
    boolean[] c = new boolean[a.length + b.length];
    for (int i = 0; i < a.length ; i++) c[i] = a[i];
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
  @SafeVarargs
  public static <T> T[] add(T[] a, T...b) {
    // add b to the end of a
    if (a == null && b == null) throw new IllegalArgumentException(
        "add: both arrays can't be null");
    if (a == null || a.length == 0) return b;
    T[] c = copyOf(a, a.length + b.length);
    for (int i = 0; i < b.length ; i++) c[a.length+i] = b[i];
    return c;  
  }
  
//  public static Object addObjects(Object a, Object b) {
//    // if a and b are 1D arrays with the same component type
//    // return a new array with b added to the end of a
//    if (a == null && b == null) throw new IllegalArgumentException(
//        "add: both arrays can't be null");
//    if (!(a.getClass().isArray() && a.getClass().isArray()))
//      throw new IllegalArgumentException("add: both arguments must be arrays");
//    if (!(dim(a)==1 && dim(b)==1)) throw new IllegalArgumentException(
//        "add: both arguments must be one dimensional arrays");
//    int alen = Array.getLength(a);
//    int blen = Array.getLength(b);
//    if ((long) alen+blen > maxlen) throw new IllegalArgumentException(
//        "add: the sum of the length of both arrays exceeds the VM limit");
//    String art = rootComponentType(a);
//    String brt = rootComponentType(b);
//    if (!art.equals(brt)) throw new IllegalArgumentException(
//        "add: both arguments must have the same rootComponentType");
//    
////    boolean primitive = false;
////    if (primitiveClassForName(art) != null) primitive = true;
//    
//    Object[] a2 = (Object[]) a;
//    Object[] b2 = (Object[]) b;
//    Object[] c = new Object[alen+blen];
//    for (int i = 0; i < alen ; i++) c[i] = a2[i];
//    for (int i = 0; i < blen ; i++) c[alen+i] = b2[i]; 
//    return  c;
//  }
  
  public static Object box(Object a) {
    // copies an array with primitive type rootComponentType into an array
    // with  primitive type rootComponentType and identical structure
    if( a == null) throw new IllegalArgumentException("box: "
        + "argument can't be null");
    if (!isPrimitiveArray(a)) throw new IllegalArgumentException("box: "
        + "argument must be an array with primitive type rootComponentType");
    int dim = dim(a);
    if (dim < 1) throw new IllegalArgumentException("box: "
        + "dimension of argument must be > 0");
    
    String t = rootComponentType(a);
    
    if (dim == 1) { // base case of recursion
      Class<?> nt = null;
      
      switch (t) {
      case "byte":    nt = java.lang.Byte.class;      break;
      case "char":    nt = java.lang.Character.class; break;
      case "double":  nt = java.lang.Double.class;    break;
      case "float":   nt = java.lang.Float.class;     break;
      case "int":     nt = java.lang.Integer.class;   break;
      case "long":    nt = java.lang.Long.class;      break;
      case "short":   nt = java.lang.Short.class;     break;
      case "boolean": nt = java.lang.Boolean.class;   break;
      default : throw new IllegalArgumentException("box: "
          + "unidentified primitive type "+t);
      }

      int len = Array.getLength(a);
      Object array = Array.newInstance(nt, len);

      switch (t) {
      case "byte":
        {Byte[] array2 = (Byte[]) array;
        byte[] b = (byte[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "char":
        {Character[] array2 = (Character[]) array;
        char[] b = (char[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "double":
        {Double[] array2 = (Double[]) array;
        double[] b = (double[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "float":
        {Float[] array2 = (Float[]) array;
        float[] b = (float[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "int":
        {Integer[] array2 = (Integer[]) array;
        int[] b = (int[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "long":
        {Long[] array2 = (Long[]) array;
        long[] b = (long[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "short":
        {Short[] array2 = (Short[]) array;
        short[] b = (short[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      case "boolean":
        {Boolean[] array2 = (Boolean[]) array;
        boolean[] b = (boolean[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        break;}
      default : throw new IllegalArgumentException("box: "
          + "primitive type "+t+" not found");
      }

      return array;
      // end of dim=1 base recursion case
    } else {
      String nt = null;
      String prefix = repeat('[', dim-1);
      
      switch (t) {
      case "byte":    nt = prefix+"Ljava.lang.Byte;";      break;
      case "char":    nt = prefix+"Ljava.lang.Character;"; break;
      case "double":  nt = prefix+"Ljava.lang.Double;";    break;
      case "float":   nt = prefix+"Ljava.lang.Float;";     break;
      case "int":     nt = prefix+"Ljava.lang.Integer;";   break;
      case "long":    nt = prefix+"Ljava.lang.Long;";      break;
      case "short":   nt = prefix+"Ljava.lang.Short;";     break;
      case "boolean": nt = prefix+"Lava.lang.Boolean;";    break;
      default : throw new IllegalArgumentException("box: "
          + "unidentified primitive type "+t);
    }
    
    Class<?> c;
    try {
      c = Class.forName(nt);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    
    int len = Array.getLength(a);
    Object array = Array.newInstance(c, len);
    for (int i = 0; i < len; i++)
      Array.set(array,i,box(Array.get(a,i)));
     
    return array;
    }
  }
  
  public static Method getCloneMethod(Class<?> c) {
    //  returns c.clone() method if it exists else null.
    if( c == null) throw new IllegalArgumentException("getCloneMethod: "
        + "argument can't be null");
    
    Method clone = null;
    
    try {
      clone = c.getDeclaredMethod("clone");
      return clone;
    } catch (NoSuchMethodException | SecurityException e) {} 
    
    try {
      clone = c.getMethod("clone");
      return clone;
    } catch (NoSuchMethodException | SecurityException e) {}
    
    return null;
  }
  
  public static Class<?> primitiveClassForName(String s) {
    // returns the Class for primitives else null
    if (s == null) return null;
    switch (s) {
      case "byte":    return byte.class;
      case "char":    return char.class;
      case "double":  return double.class;
      case "float":   return float.class;
      case "int":     return int.class;
      case "long":    return long.class;
      case "short":   return short.class;
      case "boolean": return boolean.class;
      default :       return null;
    }
  }
  
  private static class FlatLine {
    // this class is for providing a method to flatten an array to 1D
    // regardless of its original dimensionality. 
    
    private Object r = null;
    private Class<?> nt = null;
    
    public FlatLine(){} 
            
    Object flatLine(Object a) {
      // if a is an array convert it to a 1D array discarding elements
      // that don't fit due to array length limit maxArrayLength.
      if( a == null) throw new IllegalArgumentException("flatLine: "
          + "argument can't be null");
      if (!a.getClass().isArray()) throw new IllegalArgumentException("flatLine: "
          + "argument must be an array");
            
      int dim = dim(a);
      int alen = Array.getLength(a);

      if (dim == 1) { // base case of recursion
        
        String t = rootComponentType(a);
//        System.out.println("flatline.rootComponentType="+t);
        nt = a.getClass().getComponentType();
//        System.out.println("flatline.nt="+nt);
   
        if (r == null) r = Array.newInstance(nt, 0);
//        System.out.println("flatLine r compT just after creation="+r.getClass().getComponentType().getName());

        int rlen = Array.getLength(r);

        switch (t) {
          case "byte":
            if ((long) rlen+alen > maxlen) {
              return add((byte[]) r, dropRight((byte[]) a, -maxlen+alen+rlen));
            } else {
              r = add((byte[]) r, (byte[]) a);
              break;
            }
          case "char":
            if ((long) rlen+alen > maxlen) {
              return add((char[]) r, dropRight((char[]) a, -maxlen+alen+rlen));
            } else {
              r = add((char[]) r, (char[]) a);
              break;
            }           
          case "double":
            if ((long) rlen+alen > maxlen) {
              return add((double[]) r, dropRight((double[]) a, -maxlen+alen+rlen));
            } else {
              r = add((double[]) r, (double[]) a);
              break;
            }
          case "float":
            if ((long) rlen+alen > maxlen) {
              return add((float[]) r, dropRight((float[]) a, -maxlen+alen+rlen));
            } else {
              r = add((float[]) r, (float[]) a);
              break;
            }           
          case "int":
            if ((long) rlen+alen > maxlen) {
              return add((int[]) r, dropRight((int[]) a, -maxlen+alen+rlen));
            } else {
              r = add((int[]) r, (int[]) a);
              break;
            }
          case "long":
            if ((long) rlen+alen > maxlen) {
              return add((long[]) r, dropRight((long[]) a, -maxlen+alen+rlen));
            } else {
              r = add((long[]) r, (long[]) a);
              break;
            }
          case "short":
            if ((long) rlen+alen > maxlen) {
              return add((short[]) r, dropRight((short[]) a, -maxlen+alen+rlen));
            } else {
              r = add((short[]) r, (short[]) a);
              break;
            }
          case "boolean":
            if ((long) rlen+alen > maxlen) {
              return add((boolean[]) r, dropRight((boolean[]) a, -maxlen+alen+rlen));
            } else {
              r = add((boolean[]) r, (boolean[]) a);
              break;
            }
          default :
            if ((long) rlen+alen > maxlen) {
              return add((Object[]) r, dropRight((Object[]) a, -maxlen+alen+rlen));
            } else {
              r = add((Object[]) r, (Object[]) a);
//              System.out.println("flatLine r compT="+r.getClass().getComponentType().getName());
//              System.out.println("flatLine a compT="+a.getClass().getComponentType().getName());
              break;
            }
        }
      } else {
        for (int i = 0; i < alen; i++) flatLine(Array.get(a,i));
      }   
      return r;
    }  
  }
  
  public static Object flatLine(Object a) {
    // if a is an array convert with dimensionality > 1
    // convert it into a new 1D array.
    // 
    return (new FlatLine()).flatLine(a);
  }
  
  private static class UniformComponentType {
    // this class is for providing its find method to report the componentType
    // of an array if it's uniform else report "false"
    
    private Class<?> r = null;
    private  boolean found = false;
    
    // to prevent attempted instantiation
    public UniformComponentType(){} 
            
    String find(Object a) {
      // if a is an array and all its components have the  
      // same type return its name else return "false"
      if( a == null) throw new IllegalArgumentException("find: "
          + "argument can't be null");
      if (!a.getClass().isArray()) throw new IllegalArgumentException("find: "
          + "argument must be an array");
      
      String t = rootComponentType(a);
      if (primitiveClassForName(t) != null) {
        r = primitiveClassForName(t);
        return t;
      }
      
      int dim = dim(a);
      int len = Array.getLength(a);

      if (dim == 1) { // base case of recursion
        Object[] b = (Object[]) a;
        if (!found)
          for (int i = 0; i < len; i++)
            if (b[i] != null) {
              r = b[i].getClass();
              found = true;
              break;
            }
        if (found) {
          for (int i = 0; i < len; i++)
            if (b[i] != null && b[i].getClass() != r) return "false";
        } 
        // end of dim=1 base recursion case
      } else {
        for (int i = 0; i < len; i++) find(Array.get(a,i));
      }   
      return r.getName();
    }  
  }
 
  public static String uniformComponentType(Object a) {
    // if a is an array with a uniform actual componentType return
    // the name of that componentType else return "false"
    return (new UniformComponentType()).find(a);
  }
  
  public static Object clone(Object a) {
    // deep copies an array of up to system max 255 dimension.
    // if rootComponentType is not Object.class but an Object 
    // subclass with a working clone method the clone will contain
    // all new objects. elements of arrays of boxed types, String,
    // BigInteger and BigDecimal are copied since they're immutable.
    if( a == null) throw new IllegalArgumentException("clone: "
        + "argument can't be null");
    if (!a.getClass().isArray()) throw new IllegalArgumentException("clone: "
        + "argument must be an array");
    int dim = dim(a);
    if (dim < 1) throw new IllegalArgumentException("clone: "
        + "dimension of argument must be > 0");
    
    int len = Array.getLength(a);

    if (dim == 1) { // base case of recursion
      
      String t = rootComponentType(a);
      Class<?> nt = a.getClass().getComponentType();
      Object array = Array.newInstance(nt, len);

      switch (t) {
      case "byte":
        {byte[] array2 = (byte[]) array;
        byte[] b = (byte[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "char":
        {char[] array2 = (char[]) array;
        char[] b = (char[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "double":
        {double[] array2 = (double[]) array;
        double[] b = (double[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "float":
        {float[] array2 = (float[]) array;
        float[] b = (float[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "int":
        {int[] array2 = (int[]) array;
        int[] b = (int[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "long":
        {long[] array2 = (long[]) array;
        long[] b = (long[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "short":
        {short[] array2 = (short[]) array;
        short[] b = (short[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "boolean":
        {boolean[] array2 = (boolean[]) array;
        boolean[] b = (boolean[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;} 
      case "java.lang.Byte":
        {Byte[] array2 = (Byte[]) array;
        Byte[] b = (Byte[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Character":
        {Character[] array2 = (Character[]) array;
        Character[] b = (Character[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Double":
        {Double[] array2 = (Double[]) array;
        Double[] b = (Double[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Float":
        {Float[] array2 = (Float[]) array;
        Float[] b = (Float[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Integer":
        {Integer[] array2 = (Integer[]) array;
        Integer[] b = (Integer[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Long":
        {Long[] array2 = (Long[]) array;
        Long[] b = (Long[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Short":
        {Short[] array2 = (Short[]) array;
        Short[] b = (Short[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.Boolean":
        {Boolean[] array2 = (Boolean[]) array;
        Boolean[] b = (Boolean[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.lang.String":
        {String[] array2 = (String[]) array;
        String[] b = (String[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.math.BigInteger":
        {BigInteger[] array2 = (BigInteger[]) array;
        BigInteger[] b = (BigInteger[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}
      case "java.math.BigDecimal":
        {BigDecimal[] array2 = (BigDecimal[]) array;
        BigDecimal[] b = (BigDecimal[]) a;
        for (int i = 0; i < len; i++) array2[i] = b[i];
        return array2;}  
      default :
        Object[] b = (Object[]) a;
        Object[] array2 = (Object[]) array;
        Method mClone = null;
        if (!uniformComponentType(a).equals("false")) {
          mClone = getCloneMethod(nt);
          if (mClone != null) {          
            for (int i = 0; i < len; i++) {
              if (b[i] == null) {
                array2[i] = null;
              } else {
                try {
                  array2[i] = mClone.invoke(b[i]);
                } catch (IllegalAccessException | IllegalArgumentException 
                    | InvocationTargetException e) {
                  System.out.println("clone: failed on "+e.getClass().getName()+" when "
                      + "invoking "+a.getClass().getComponentType().getName()+".clone() "
                      + "on element at index "+i+" with value "+b[i]);
                  array2[i] = b[i];
                }
              }
            }
          } else { 
            for (int i = 0; i < len; i++) array2[i] = b[i];
          }
        } else { 
          for (int i = 0; i < len; i++) {           //array2[i] = b[i];
            if (b[i] == null) {
              array2[i] = null;
            } else {
              mClone = getCloneMethod(b[i].getClass());
              if (mClone != null) {
                try {
                  array2[i] = mClone.invoke(b[i]);
                } catch (IllegalAccessException | IllegalArgumentException 
                    | InvocationTargetException e) {
                  System.out.println("clone: failed on "+e.getClass().getName()+" when "
                      + "invoking "+a.getClass().getComponentType().getName()+".clone() "
                      + "on element at index "+i+" with value "+b[i]);
                  array2[i] = b[i];
                }  
              } else {
                array2[i] = b[i];
              }
            }           
          }
        }      
        return array2;
      }
      // end of dim=1 base recursion case
    } else {
      Object[] array = (Object[]) Array.newInstance(a.getClass().getComponentType(), len);
      for (int i = 0; i < len; i++) array[i] = clone(Array.get(a,i));
      return array;
    }
  }
  
  public static final String repeat(char c, int length) {
    // this method is to support the box and unbox methods
    if( length < 1) throw new IllegalArgumentException("repeat "
        + "length must be > 0");
    char[] d = new char[length];
    for (int i = 0; i < length; i++) d[i] = c;
    return new String(d);
  }
  
  private static class Combinator<T> implements Iterable<T[]>  {
    private T[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public Combinator(T[] items, int choose) {
      if (items == null || items.length == 0) 
        throw new IllegalArgumentException("Combinator constructor: "
            + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length) 
        throw new IllegalArgumentException("Combinator constructor: "
            + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }
    
    public Combinator(List<T> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("Combinator constructor: items "
            +"can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException("Combinator constructor: choose "
            + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] makeArray(final List<T> obj) {
      if (obj == null || obj.isEmpty()) return null;
      final T t = obj.get(0);
      final T[] res = (T[]) Array.newInstance(t.getClass(), obj.size());
      for (int i = 0; i < obj.size(); i++) res[i] = obj.get(i);
      return res;
    }

    public Iterator<T[]> iterator() {
      return new ArrayIterator();
    }

    public class ArrayIterator implements Iterator<T[]> {

      public boolean hasNext() {
        return !finished;
      }

      public T[] next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (current == null) {
          current = new int[choose];
          for (int i = 0; i < choose; i++)
            current[i] = i;
        }

        T[] result = makeGen1D(choose, items);
        for (int i = 0; i < choose; i++)
          result[i] = items[current[i]];

        int n = items.length;
        finished = true;
        for (int i = choose - 1; i >= 0; i--)
          if (current[i] < n - choose + i) {
            current[i]++;
            for (int j = i + 1; j < choose; j++)
              current[j] = current[i] - i + j;
            finished = false;
            break;
          }

        return result;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
  
  private static class CombinatorInt implements Iterable<int[]>  {
    private int[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorInt(int[] items, int choose) {
      if (items == null || items.length == 0) 
        throw new IllegalArgumentException("IntCombinator constructor: "
            + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length) 
        throw new IllegalArgumentException("IntCombinator constructor: "
            + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }
    
    @SuppressWarnings("unused")
    public CombinatorInt(List<Integer> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("IntCombinator constructor: items "
            +"can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException("IntCombinator constructor: choose "
            + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }
    
    public static int[] makeArray(final List<Integer> obj) {
      if (obj == null || obj.isEmpty()) return null;
      int[] res = new int[obj.size()];
      for (int i = 0; i < obj.size(); i++) res[i] = obj.get(i);
      return res;
    }

    public Iterator<int[]> iterator() {
      return new ArrayIterator();
    }

    public class ArrayIterator implements Iterator<int[]> {

      public boolean hasNext() {
        return !finished;
      }

      public int[] next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (current == null) {
          current = new int[choose];
          for (int i = 0; i < choose; i++)
            current[i] = i;
        }

        
        int[] result = new int[choose];
        for (int i = 0; i < choose; i++)
          result[i] = items[current[i]];

        int n = items.length;
        finished = true;
        for (int i = choose - 1; i >= 0; i--)
          if (current[i] < n - choose + i) {
            current[i]++;
            for (int j = i + 1; j < choose; j++)
              current[j] = current[i] - i + j;
            finished = false;
            break;
          }

        return result;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
  
  private static class CombinatorLong implements Iterable<long[]>  {
    private long[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorLong(long[] items, int choose) {
      if (items == null || items.length == 0) 
        throw new IllegalArgumentException("LongCombinator constructor: "
            + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length) 
        throw new IllegalArgumentException("LongCombinator constructor: "
            + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }
    
    @SuppressWarnings("unused")
    public CombinatorLong(List<Long> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("LongCombinator constructor: items "
            +"can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException("LongCombinator constructor: choose "
            + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }
    
    public static long[] makeArray(final List<Long> obj) {
      if (obj == null || obj.isEmpty()) return null;
      long[] res = new long[obj.size()];
      for (int i = 0; i < obj.size(); i++) res[i] = obj.get(i);
      return res;
    }

    public Iterator<long[]> iterator() {
      return new ArrayIterator();
    }

    public class ArrayIterator implements Iterator<long[]> {

      public boolean hasNext() {
        return !finished;
      }

      public long[] next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (current == null) {
          current = new int[choose];
          for (int i = 0; i < choose; i++)
            current[i] = i;
        }

        long[] result = new long[choose];
        for (int i = 0; i < choose; i++)
          result[i] = items[current[i]];

        int n = items.length;
        finished = true;
        for (int i = choose - 1; i >= 0; i--)
          if (current[i] < n - choose + i) {
            current[i]++;
            for (int j = i + 1; j < choose; j++)
              current[j] = current[i] - i + j;
            finished = false;
            break;
          }

        return result;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }   
  
  private static class CombinatorDouble implements Iterable<double[]>  {
    private double[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorDouble(double[] items, int choose) {
      if (items == null || items.length == 0) 
        throw new IllegalArgumentException("DoubleCombinator constructor: "
            + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length) 
        throw new IllegalArgumentException("DoubleCombinator constructor: "
            + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }
    
    @SuppressWarnings("unused")
    public CombinatorDouble(List<Double> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("DoubleCombinator constructor: items "
            +"can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException("DoubleCombinator constructor: choose "
            + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }
    
    public static double[] makeArray(final List<Double> obj) {
      if (obj == null || obj.isEmpty()) return null;
      double[] res = new double[obj.size()];
      for (int i = 0; i < obj.size(); i++) res[i] = obj.get(i);
      return res;
    }

    public Iterator<double[]> iterator() {
      return new ArrayIterator();
    }

    public class ArrayIterator implements Iterator<double[]> {

      public boolean hasNext() {
        return !finished;
      }

      public double[] next() {
        if (!hasNext()) throw new NoSuchElementException();

        if (current == null) {
          current = new int[choose];
          for (int i = 0; i < choose; i++)
            current[i] = i;
        }

        double[] result = new double[choose];
        for (int i = 0; i < choose; i++)
          result[i] = items[current[i]];

        int n = items.length;
        finished = true;
        for (int i = choose - 1; i >= 0; i--)
          if (current[i] < n - choose + i) {
            current[i]++;
            for (int j = i + 1; j < choose; j++)
              current[j] = current[i] - i + j;
            finished = false;
            break;
          }

        return result;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
  
  public static Iterator<int[]> combinations(int[] a, int n) {
    if (a == null || a.length == 0) 
      throw new IllegalArgumentException("combinations: "
          + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length) 
      throw new IllegalArgumentException("combinations: "
          + "int n must be between 0 and the array length inclusive");
    return (new CombinatorInt(a,n)).iterator();
  }
  
  public static Stream<int[]> combinationStream(int[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationsStream: the array "
          +"can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinationsStream: int n "
          + "must be between 0 and the array length inclusive");
    Iterator<int[]> i = combinations(a, n);
    boolean infinite = false;
    BigInteger nc = numberOfCombinations(a.length, n);
    double len = nc.doubleValue();
    //System.out.println("len="+len);
    long lim = 0;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) {
      infinite = true; 
    } else if (len <= Long.MAX_VALUE) {
      lim = (long) len;
      //System.out.println("lim="+lim);
    }
    if (!infinite) {
      return Stream.generate(() -> i.next()).limit(lim);
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static Iterator<long[]> combinations(long[] a, int n) {
    if (a == null || a.length == 0) 
      throw new IllegalArgumentException("combinations: "
          + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length) 
      throw new IllegalArgumentException("combinations: "
          + "int n must be between 0 and the array length inclusive");
    return (new CombinatorLong(a,n)).iterator();
  }
  
  public static Stream<long[]> combinationStream(long[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationsStream: the array "
          + "can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinationStream: int n "
          + "must be between 0 and the array length inclusive");
    Iterator<long[]> i = combinations(a, n);
    boolean infinite = false;
    BigInteger nc = numberOfCombinations(a.length, n);
    double len = nc.doubleValue();
    //System.out.println("len="+len);
    long lim = 0;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) {
      infinite = true; 
    } else if (len <= Long.MAX_VALUE) {
      lim = (long) len;
      //System.out.println("lim="+lim);
    }
    if (!infinite) {
      return Stream.generate(() -> i.next()).limit(lim);
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static Iterator<double[]> combinations(double[] a, int n) {
    if (a == null || a.length == 0) 
      throw new IllegalArgumentException("combinations: "
          + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length) 
      throw new IllegalArgumentException("combinations: "
          + "int n must be between 0 and the array length inclusive");
    return (new CombinatorDouble(a,n)).iterator();
  }
  
  
  public static Stream<double[]> combinationStream(double[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationStream: the list "
          +"can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinationStream: int n "
          + "must be between 0 and the array length inclusive");
    Iterator<double[]> i = combinations(a, n);
    boolean infinite = false;
    BigInteger nc = numberOfCombinations(a.length, n);
    double len = nc.doubleValue();
    //System.out.println("len="+len);
    long lim = 0;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) {
      infinite = true; 
    } else if (len <= Long.MAX_VALUE) {
      lim = (long) len;
      //System.out.println("lim="+lim);
    }
    if (!infinite) {
      return Stream.generate(() -> i.next()).limit(lim);
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static <T> Iterator<T[]> combinations(T[] a, int n) {
    if (a == null || a.length == 0) 
      throw new IllegalArgumentException("combinations: "
          + "the array can't be null or have no elements");
    if (n <= 0 || n > a.length) 
      throw new IllegalArgumentException("combinations: "
          + "int n must be between 0 and the array length inclusive");
    return (new Combinator<T>(a,n)).iterator();
  }
  
  public static <T> Iterator<T[]> combinations(List<T> a, int n) {
    if (a == null || a.size() == 0) 
      throw new IllegalArgumentException("combinations: "
          + "the list can't be null or have no elements");
    if (n <= 0 || n > a.size()) 
      throw new IllegalArgumentException("combinations: "
          + "int n must be between 0 and the list size inclusive");
    return (new Combinator<T>(a,n)).iterator();
  }
  
  
  public static <T> Stream<T[]> combinationStream(T[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationStream: the array "
          +"can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinationStream: int n "
          + "must be between 0 and the array length inclusive");
    Iterator<T[]> i = combinations(a, n);
    boolean infinite = false;
    BigInteger nc = numberOfCombinations(a.length, n);
    double len = nc.doubleValue();
    //System.out.println("len="+len);
    long lim = 0;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) {
      infinite = true; 
    } else if (len <= Long.MAX_VALUE) {
      lim = (long) len;
      //System.out.println("lim="+lim);
    }
    if (!infinite) {
      return Stream.generate(() -> i.next()).limit(lim);
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static <T> Stream<T[]> combinationStream(List<T> a, int n) {
    if (a == null || a.size() == 0)
      throw new IllegalArgumentException("combinationStream: the list "
          +"can't be null or have no elements");
    if (n <= 0 || n > a.size())
      throw new IllegalArgumentException("combinationStream: int n "
          + "must be between 0 and the list size inclusive");
    Iterator<T[]> i = combinations(a, n);
    boolean infinite = false;
    BigInteger nc = numberOfCombinations(a.size(), n);
    double len = nc.doubleValue();
    //System.out.println("len="+len);
    long lim = 0;
    if (len > Long.MAX_VALUE || len == Double.POSITIVE_INFINITY) {
      infinite = true; 
    } else if (len <= Long.MAX_VALUE) {
      lim = (long) len;
      //System.out.println("lim="+lim);
    }
    if (!infinite) {
      return Stream.generate(() -> i.next()).limit(lim);
    } else {
      return Stream.generate(() -> i.next());
    }
  }

  public static int[] drop(int[] a, int n) {
    // return an int array without the 1st n elements of a
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n > a.length) n = a.length;
    return copyOfRange(a, n, a.length);   
  }
  
  public static long[] drop(long[] a, int n) {
    // return a long array without the 1st n elements of a
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n > a.length) n = a.length;
   return copyOfRange(a, n, a.length);   
  }

  public static double[] drop(double[] a, int n) {
    // return a double array without the 1st n elements of a
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n > a.length) n = a.length;
    return copyOfRange(a, n, a.length);   
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] drop(T[] a, int n) {
    // return a double array without the 1st n elements of a
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n > a.length)
      return (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
//    return copyOfRange(a, n, a.length); 
    T[] b = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length-n);
    for (int i = n; i < a.length; i++) b[i] = a[i];
    return b;
  }
  
  public static byte[] dropRight(byte[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new byte[0];
    byte[] b = new byte[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static short[] dropRight(short[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new short[0];
    short[] b = new short[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static int[] dropRight(int[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new int[0];
    int[] b = new int[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static long[] dropRight(long[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new long[0];
    long[] b = new long[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }

  public static float[] dropRight(float[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new float[0];
    float[] b = new float[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static double[] dropRight(double[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new double[0];
    double[] b = new double[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static char[] dropRight(char[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new char[0];
    char[] b = new char[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  public static boolean[] dropRight(boolean[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length) return new boolean[0];
    boolean[] b = new boolean[a.length-n];
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;   
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] dropRight(T[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null) throw new IllegalArgumentException(
        "drop: the array can't be null");
    if (n < 0) throw new IllegalArgumentException(
        "drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return (T[]) Array.newInstance(a.getClass().getComponentType(), 0);
    T[] b = (T[]) Array.newInstance(a.getClass().getComponentType(), a.length-n);
    for (int i = 0; i < b.length; i++) b[i] = a[i];
    return b;    
  }

  public static int[] dropWhile(int[] a, Predicate<Integer> p) {
    assert a != null; 
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else break;
    return copyOfRange(a, bindex, a.length);
  }
  
  public static long[] dropWhile(long[] a, Predicate<Long> p) {
    assert a != null; 
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else break;
    return copyOfRange(a, bindex, a.length);
  }
  
  public static double[] dropWhile(double[] a, Predicate<Double> p) {
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else break;
    return copyOfRange(a, bindex, a.length);
  }

  public static <T> T[] dropWhile(T[] a, Predicate<T> p) {
    assert a != null; 
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else break;
    return copyOfRange(a, bindex, a.length);
  }
  
  public static boolean endsWith(int[] a, int...b) {
    // return the answer to "does a end with the values of b?"
    assert a != null; assert b != null;
    if (b.length > a.length) return false;
    if (b.length == a.length && equals(a,b)) return true;
    int bindex = 0;
    for (int i = a.length-b.length; i < a.length; i++)
      if (!(a[i] == b[bindex++])) return false;
    return true;
  }

  public static boolean endsWith(double[] a, double...b) {
    // return the answer to "does a end with the values of b?"
    assert a != null; assert b != null;
    if (b.length > a.length) return false;
    if (b.length == a.length && equals(a,b)) return true;
    int bindex = 0;
    for (int i = a.length-b.length; i < a.length; i++)
      if (!(a[i] == b[bindex++])) return false;
    return true;
  }

  public static boolean equals(int[] a, int[] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i]) return false;
    return true;
  }
  
  public static boolean equals(long[] a, long[] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i]) return false;
    return true;
  }

  public static boolean equals(double[] a, double[] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i]) return false;
    return true;
  }
  
  public static boolean equals(boolean[] a, boolean[] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i]) return false;
    return true;
  }
  
  public static <T> boolean equals(T[] a, T[] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++)
      if (!(a[i].equals(b[i]))) return false;
    return true;
  }
  
  public static boolean equals(int[][] a, int[][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j]) return false;
      }
    }
    return true;
  }
  
  public static boolean equals(long[][] a, long[][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j]) return false;
      }
    }
    return true;
  }

  public static boolean equals(double[][] a, double[][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j]) return false;
      }
    }
    return true;
  }
  
  public static  <T> boolean equals(T[][] a, T[][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (!(a[i][j].equals(b[i][j]))) return false;
      }
    }
    return true;
  }
  
  public static boolean equals(int[][][] a, int[][][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length) return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k]) return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(long[][][] a, long[][][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length) return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k]) return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(double[][][] a, double[][][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length) return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k]) return false;
        }
      }
    }
    return true;
  }
  
  public static <T> boolean equals(T[][][] a, T[][][] b) {
    if (a == null && b == null) return true;
    if (a == null && b != null || b == null && a != null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length) return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (!(a[i][j][k].equals(b[i][j][k]))) return false;
        }
      }
    }
    return true;
  }

  public static boolean exists(int[] a, Predicate<Integer> p) {
    assert a != null;
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return true;
    return false;
  }

  public static boolean exists(double[] a, Predicate<Double> p) {
    assert a != null;
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return true;
    return false;
  }

  public static int[] filter(int[] a, Predicate<Integer> p) {
    assert a != null;
    int[] b = new int[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) b[bindex++] = a[i];
    int[] c = new int[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static double[] filter(double[] a, Predicate<Double> p) {
    assert a != null;
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) b[bindex++] = a[i];
    double[] c = new double[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static int[] filterNot(int[] a, Predicate<Integer> p) {
    assert a != null;
    int[] b = new int[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) b[bindex++] = a[i];
    int[] c = new int[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static double[] filterNot(double[] a, Predicate<Double> p) {
    assert a != null;
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) b[bindex++] = a[i];
    double[] c = new double[bindex];
    for (int i = 0; i < bindex; i++) c[i] = b[i];
    return c;
  }

  public static Optional<Integer> find(int[] a, Predicate<Integer> p) {
    if (a == null || a.length == 0) return empty();
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return Optional.of(a[i]);
    return Optional.empty();
  }
  
  public static Optional<Long> find(long[] a, Predicate<Long> p) {
    if (a == null || a.length == 0) return empty();
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return Optional.of(a[i]);
    return empty();
  }

  public static Optional<Double> find(double[] a, Predicate<Double> p) {
    if (a == null || a.length == 0) return empty();
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return Optional.of(a[i]);
    return empty();
  }
  
  public static <T> Optional<T> find(T[] a, Predicate<T> p) {
    if (a == null || a.length == 0) return empty();
    for (int i = 0; i < a.length; i++) 
      if (p.test(a[i])) return Optional.of(a[i]);
    return empty();
  }

  public static int firstIndexOf(int[] a, int s, int v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null || a.length-1 < s) return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v) return i;
    return -1;
  }
  
  public static int firstIndexOf(long[] a, int s, long v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null || a.length-1 < s) return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v) return i;
    return -1;
  }

  public static int firstIndexOf(double[] a, int s, double v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null || a.length-1 < s) return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v) return i;
    return -1;
  }
  
  public static <T> int firstIndexOf(T[] a, int s, T v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null || a.length-1 < s) return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v) return i;
    return -1;
  }

  public static int firstIndexOfSlice(int[] a, int s, int...v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null) return -1;
    LOOP:
      for (int i = s; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        return i;
      }
    return -1;
  }
  
  public static int firstIndexOfSlice(long[] a, int s, long...v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null) return -1;
    LOOP:
      for (int i = s; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        return i;
      }
    return -1;
  }

  public static int firstIndexOfSlice(double[] a, int s, double...v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null) return -1;
    LOOP:
      for (int i = s; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        return i;
      }
    return -1;
  }
  
  @SafeVarargs
  public static <T> int firstIndexOfSlice(T[] a, int s, T...v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    if (a == null) return -1;
    LOOP:
      for (int i = s; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        return i;
      }
    return -1;
  }
  
  public static int[] flatMap(int[] a, F1<Integer, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static long[] flatMap(int[] a, F2<Integer, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static double[] flatMap(int[] a, F3<Integer, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static <R> R[] flatMap(int[] a, F4<Integer, R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static int[] flatMap(long[] a, F1<Long, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static long[] flatMap(long[] a, F2<Long, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static double[] flatMap(long[] a, F3<Long, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static <R> R[] flatMap(long[] a, F4<Long, R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static int[] flatMap(double[] a, F1<Double, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static long[] flatMap(double[] a, F2<Double, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static double[] flatMap(double[] a, F3<Double, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static <R> R[] flatMap(double[] a, F4<Double, R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=add(r,f.apply(a[i]));
    return r;
  }
  
  public static <T,R> R[] flatMap(T[] a, F1<T,R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=add(r, f.apply(a[i]));
    return r;
  }
  
  public static Object flatten(Object a) {
    // flattens an array of arrays of X to an array of X 
    // scala flatten demo to show how flatten should work:
    // scala> a7
    // res1: Array[Array[Array[Int]]] = Array(Array(Array(1, 2), Array(3, 4)), Array(Array(5, 6), Array(7, 8)))
    // scala> a7 flatten
    // res2: Array[Array[Int]] = Array(Array(1, 2), Array(3, 4), Array(5, 6), Array(7, 8))
    // in other words flatten takes an array of arrays of X and
    // flattens it to an array of X where X can be a non-array 
    // or an array of any allowed dimensionality. flatten does 
    // not create a 1D array of rootComponentType unless the 
    // original array is 2D. for always creating a 1D array from 
    // an array of any dimensionality use the flatline method.
    if( a == null) throw new IllegalArgumentException("flatten: "
        + "argument can't be null");
    if (!a.getClass().isArray()) throw new IllegalArgumentException("flatten: "
        + "argument must be an array");
    int dim = dim(a);
    if (dim < 2) throw new IllegalArgumentException("flatten: "
        + "argument must be an array with dimension >= 2");
    
    if (dim == 2 && isPrimitiveArray(a)) {
      // special case since only for 2D arrays with primitive rootComponentType
      // a.getClass().getComponentType().getComponentType() isn't an object
      String rct = rootComponentType(a);
      switch (rct) {
        case "byte":
          {byte[][] b = (byte[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          byte[] r = new byte[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        case "short":
          {short[][] b = (short[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          short[] r = new short[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        case "int":
          {int[][] b = (int[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          int[] r = new int[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        case "long":
          {long[][] b = (long[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          long[] r = new long[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        case "double":
          {double[][] b = (double[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          double[] r = new double[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        case "float":
          {float[][] b = (float[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          float[] r = new float[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}       
        case "char":
          {char[][] b = (char[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          char[] r = new char[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
            return r;}
        case "boolean":
          {boolean[][] b = (boolean[][]) a;
          long rlen = 0;
          for (int i = 0; i < b.length; i++) rlen+=b[i].length;
          if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten: "
              + "flattened array if produced would exceed max allowed array length");
          boolean[] r = new boolean[(int) rlen];
          int rindex = 0;
          for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b[i].length; j++)
              r[rindex++] = b[i][j];
          return r;}
        default: throw new NoSuchElementException("flatten: invalid primitive "
            + "type "+ rct);
      }      
    }
    
    int alen = Array.getLength(a);
    int[] acollen = new int[alen];
    for (int i = 0; i < alen; i++) acollen[i] = Array.getLength(Array.get(a,i)); 
    long rlen = sum(acollen);
    if (rlen > maxlen) throw new ArrayIndexOutOfBoundsException("flatten2: "
        + "flattened array if produced would exceed max allowed array length");
    Object[] r = (Object[]) Array.newInstance(a.getClass().getComponentType()
        .getComponentType(), (int) rlen);
    Object[][] b = (Object[][]) a;
    int rindex = 0;
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b[i].length; j++)
        r[rindex++] = b[i][j];
    return r;
  }
  
  public static int foldLeft(int[] a, IntBinaryOperator op, int z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    int r = op.applyAsInt(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsInt(r, a[i]);
    return r;
  }

  public static long foldLeft(long[] a, LongBinaryOperator op, long z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    long r = op.applyAsLong(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsLong(r, a[i]);
    return r;
  }

  public static double foldLeft(double[] a, DoubleBinaryOperator op, double z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    double r = op.applyAsDouble(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsDouble(r, a[i]);
    return r;
  }

  public static <T> T foldLeft(T[] a, BinaryOperator<T> op, T z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    T r = op.apply(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.apply(r, a[i]);
    return r;
  }
  
  public static int foldRight(int[] a, IntBinaryOperator op, int z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    int r = op.applyAsInt(a[a.length-1],z);
    for (int i = a.length-2; i > -1; i--)
      r = op.applyAsInt(a[i],r);
    return r;
  }
  
  public static long foldRight(long[] a, LongBinaryOperator op, long z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    long r = op.applyAsLong(a[a.length-1],z);
    for (int i = a.length-2; i > -1; i--)
      r = op.applyAsLong(a[i],r);
    return r;
  }
  
  public static double foldRight(double[] a, DoubleBinaryOperator op, double z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null) throw new IllegalArgumentException("foldLeft: "
        + "argument can't be null");
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    double r = op.applyAsDouble(a[a.length-1],z);
    for (int i = a.length-2; i > -1; i--)
      r = op.applyAsDouble(a[i],r);
    return r;
  }
  
  public static <T> T foldRight(T[] a, BinaryOperator<T> op, T z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null) throw new IllegalArgumentException("foldLeft: "
        + "none of the arguments can be null");
    if (a.length < 1) throw new IllegalArgumentException("foldLeft: "
        + "argument must have a length > 0");
    T r = op.apply(a[a.length-1],z);
    for (int i = a.length-2; i > -1; i--)
      r = op.apply(a[i],r);
    return r;
  }
  
  public static boolean forAll(int[] a, Predicate<Integer> p) {
    // returns true if p is true for all elements of a else false
    if (a == null) throw new IllegalArgumentException("foldLeft: "
        + "argument can't be null");
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) return false;
    return true;
  }
  
  public static boolean forAll(long[] a, Predicate<Long> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) return false;
    return true;
  }

  public static boolean forAll(double[] a, Predicate<Double> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) return false;
    return true;
  }
  
  public static <T> boolean forAll(T[] a, Predicate<T> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++) 
      if (!p.test(a[i])) return false;
    return true;
  }

  public static void forEach(int[] a, IntConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++) ic.accept(a[i]);
  }
  
  public static void forEach(long[] a, LongConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++) ic.accept(a[i]);
  }

  public static void forEach(double[] a, DoubleConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++) ic.accept(a[i]);
  }
  
  public static <T> void forEach(T[] a, Consumer<T> ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++) ic.accept(a[i]);
  }
  
  public static <K> Map<K,int[]> groupBy(int[] a, Function<Integer, K> f) {
    Map<K,int[]> m = new HashMap<>();
    K k;
    int[] t; int[] t2;
    for (int i = 0; i < a.length; i++) {
       k = f.apply(a[i]);
       if (m.containsKey(k)) {
         t = m.get(k);
         t2 = add(t,a[i]);
         m.replace(k, t2);
       } else {
         t = new int[]{a[i]};
         m.put(k, t);
       }
    }
    return m;
  }
  
  public static <K> Map<K,long[]> groupBy(long[] a, Function<Long, K> f) {
    Map<K,long[]> m = new HashMap<>();
    K k;
    long[] t; long[] t2;
    for (int i = 0; i < a.length; i++) {
       k = f.apply(a[i]);
       if (m.containsKey(k)) {
         t = m.get(k);
         t2 = add(t,a[i]);
         m.replace(k, t2);
       } else {
         t = new long[]{a[i]};
         m.put(k, t);
       }
    }
    return m;
  }
  
  public static <K> Map<K,double[]> groupBy(double[] a, Function<Double, K> f) {
    Map<K,double[]> m = new HashMap<>();
    K k;
    double[] t; double[] t2;
    for (int i = 0; i < a.length; i++) {
       k = f.apply(a[i]);
       if (m.containsKey(k)) {
         t = m.get(k);
         t2 = add(t,a[i]);
         m.replace(k, t2);
       } else {
         t = new double[]{a[i]};
         m.put(k, t);
       }
    }
    return m;
  }

  public static <T,K> Map<K,T[]> groupBy(T[] a, Function<T, K> f) {
    Map<K,T[]> m = new HashMap<>();
    K k;
    T[] t; T[] t2;
    for (int i = 0; i < a.length; i++) {
       k = f.apply(a[i]);
       if (m.containsKey(k)) {
         t = m.get(k);
         t2 = add(t,a[i]);
         m.replace(k, t2);
       } else {
         t = makeFromValue(a[i],1);
         m.put(k, t);
       }
    }
    return m;
  }
  
  public static int head(int[] a) {
    // returns the 1st element of a
    assert a != null; assert a.length > 0;
    return a[0];
  }
  
  public static long head(long[] a) {
    // returns the 1st element of a
    assert a != null; assert a.length > 0;
    return a[0];
  }

  public static double head(double[] a) {
    // returns the 1st element of a
    assert a != null; assert a.length > 0;
    return a[0];
  }
  
  public static <T> T head(T[] a) {
    // returns the 1st element of a
    assert a != null; assert a.length > 0;
    return a[0];
  }
  
  public static <T> Optional<T> headOption(T[] a) {
    // if a[0]== null returns Optional.empty() else returns Optional.of(a[0])
    assert a != null; assert a.length > 0;
    return Optional.ofNullable(a[0]);
  }
  
  public static int indexWhere(int[] a,  Predicate<Integer> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  return i;
    return -1;
  }
  
  public static int indexWhere(long[] a,  Predicate<Long> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  return i;
    return -1;
  }

  public static int indexWhere(double[] a,  Predicate<Double> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  return i;
    return -1;
  }
  
  public static <T> int indexWhere(T[] a,  Predicate<T> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  return i;
    return -1;
  }

  public static int[] indices(int[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++) b[i] = i;
    return b;
  }
  
  public static int[] indices(long[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++) b[i] = i;
    return b;
  }

  public static int[] indices(double[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++) b[i] = i;
    return b;
  }
  
  public static <T> int[] indices(T[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++) b[i] = i;
    return b;
  }

  public static int[] init(int[] a) {
    // returns an array with all elements of a except the last
    assert a != null;
    int[] b = new int[a.length-1];
    for (int i = 0; i < b.length; i++) b[i] = a[i];    
    return b;
  }

  public static long[] init(long[] a) {
    // returns an array with all elements of a except the last
    assert a != null;
    long[] b = new long[a.length-1];
    for (int i = 0; i < b.length; i++) b[i] = a[i];    
    return b;
  }
  
  public static double[] init(double[] a) {
    // returns an array with all elements of a except the last
    assert a != null;
    double[] b = new double[a.length-1];
    for (int i = 0; i < b.length; i++) b[i] = a[i];    
    return b;
  }
  
  public static <T> T[] init(T[] a) {
    // returns an array with all elements of a except the last
    assert a != null;
    T[] b = makeFromValue(a[0], a.length-1);
    for (int i = 0; i < b.length; i++) b[i] = a[i];    
    return b;
  }

  public static int[] intersect(int[] a, int[] b) {
    // returns a sorted array containing the intersection of a and b
    // no element will be repeated
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new int[0];
    int[] as = uniqueSort(a);
    int[] bs = uniqueSort(b);
    int[] cs1 = as;
    int[] cs2 = bs;
    if (as.length > bs.length) {cs1 = bs; cs2 = as;}
    //     int[] q = new int[as.length+bs.length];
    int[] q = new int[cs1.length];
    int qindex = 0;
    for (int i = 0; i < cs1.length; i++)
      if (contains(cs2,cs1[i])) q[qindex++] = cs1[i];
    int[] r = new int[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    return r;
  }
  
  public static long[] intersect(long[] a, long[] b) {
    // returns a sorted array containing the intersection of a and b
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new long[0];
    long[] as = uniqueSort(a);
    long[] bs = uniqueSort(b);
    long[] cs1 = as;
    long[] cs2 = bs;
    if (as.length > bs.length) {cs1 = bs; cs2 = as;}
    long[] q = new long[as.length+bs.length];
    int qindex = 0;
    for (int i = 0; i < cs1.length; i++)
      if (contains(cs2,cs1[i])) q[qindex++] = cs1[i];
    long[] r = new long[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    return r;
  }

  public static double[] intersect(double[] a, double[] b) {
    // returns a sorted array containing the intersection of a and b
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new double[0];
    double[] as = uniqueSort(a);
    double[] bs = uniqueSort(b);
    double[] cs1 = as;
    double[] cs2 = bs;
    if (as.length > bs.length) {cs1 = bs; cs2 = as;}
    double[] q = new double[as.length+bs.length];
    int qindex = 0;
    for (int i = 0; i < cs1.length; i++)
      if (contains(cs2,cs1[i])) q[qindex++] = cs1[i];
    double[] r = new double[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    return r;
  }
  
  public static <T extends Comparable<? super T>> T[] intersect(T[] a, T[] b) {
    // returns a sorted array containing the intersection of a and b
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return makeGen1D(0);
    T[] as = uniqueSort(a);
    T[] bs = uniqueSort(b);
    T[] cs1 = as;
    T[] cs2 = bs;
    if (as.length > bs.length) {cs1 = bs; cs2 = as;}
    T[] q = makeGen1D(as.length+bs.length);
    int qindex = 0;
    for (int i = 0; i < cs1.length; i++)
      if (contains(cs2,cs1[i])) q[qindex++] = cs1[i];
    T[] r = makeGen1D(qindex);
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    return r;
  }
  
  public static boolean isBoxedArray(Object a) {
    // return true if a is an array with boxed type ultimate
    // component type else return false
    if (a == null) throw new IllegalArgumentException(
        "isBoxedArray: argument can't be null");
    if (!a.getClass().isArray()) throw new IllegalArgumentException(
        "isBoxedArray: argument must be an array");
    String[] p = {"java.lang.Byte","java.lang.Short","java.lang.Integer",
                  "java.lang.Long","java.lang.Float","java.lang.Double",
                  "java.lang.Character","java.lang.Boolean"};
    return in(p,rootComponentType(a));
  }
  
  public static boolean isDefinedAt(int[] a, int n) {
    assert a != null;
    // returns true if a contains an element at index n
    return n > -1 && n < a.length;
  }
  
  public static boolean isDefinedAt(long[] a, int n) {
    assert a != null;
    // returns true if a contains an element at index n
    return n > -1 && n < a.length;
  }

  public static boolean isDefinedAt(double[] a, int n) {
    assert a != null;
    // returns true iff a contains an element at index n else false
    return n > -1 && n < a.length;
  }
  
  public static <T> boolean isDefinedAt(T[] a, int n) {
    assert a != null;
    // returns true iff a contains an element at index n else false
    return n > -1 && n < a.length;
  }

  public static boolean isEmpty(int[] a) {
    assert a != null;
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }
  
  public static boolean isEmpty(long[] a) {
    assert a != null;
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }

  public static boolean isEmpty(double[] a) {
    assert a != null;
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }
  
  public static <T> boolean isEmpty(T[] a) {
    assert a != null;
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }
  
  public static boolean isPrimitiveArray(Object a) {
    // return true if a is an array with primitive type ultimate
    // component type else return false
    if (a == null) throw new IllegalArgumentException(
        "isPrimitiveArray: argument can't be null");
    if (!a.getClass().isArray()) throw new IllegalArgumentException(
        "isPrimitiveArray: argument must be an array");
    String[] p = {"byte","short","int","long","float","double","char","boolean"};
    return in(p,rootComponentType(a));
  }
  
  public static OfByte iterator(byte[] a) {
    return new OfByte() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public byte nextByte() {return a[c++];}
    };
  }
  
  public static OfShort iterator(short[] a) {
    return new OfShort() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public short nextShort() {return a[c++];}
    };
  }
  
  public static OfInt iterator(int[] a) {
    return Arrays.stream(a).iterator();
  }
  
  public static OfLong iterator(long[] a) {
    return Arrays.stream(a).iterator();
  }
  
  public static OfDouble iterator(double[] a) {
    return Arrays.stream(a).iterator();
  }
  
  public static OfFloat iterator(float[] a) {
    return new OfFloat() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public float nextFloat() {return a[c++];}
    };
  }
  
  public static OfChar iterator(char[] a) {
    return new OfChar() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public char nextChar() {return a[c++];}
    };
  }
  
  public static OfBoolean iterator(boolean[] a) {
    return new OfBoolean() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public boolean nextBoolean() {return a[c++];}
    };
  }
  
  public static <T> Iterator<T> iterator(T[] a) {
    return Arrays.stream(a).iterator();
  }

  public static int last(int[] a) {
    assert a != null; assert a.length > 0;
    // returns the last element of a
    return a[a.length-1];
  }
  
  public static long last(long[] a) {
    assert a != null; assert a.length > 0;
    // returns the last element of a
    return a[a.length-1];
  }

  public static double last(double[] a) {
    assert a != null; assert a.length > 0;
    // returns the last element of a
    return a[a.length-1];
  }
  
  public static <T> T last(T[] a) {
    assert a != null; assert a.length > 0;
    // returns the last element of a
    return a[a.length-1];
  }

  public static int lastIndexOf(int[] a, int v) {
    // returns the last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) index = i;
    return index;
  }
  
  public static int lastIndexOf(long[] a, long v) {
    // returns the last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) index = i;
    return index;
  }

  public static int lastIndexOf(double[] a, double v) {
    // returns the last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) index = i;
    return index;
  }
  
  public static <T> int lastIndexOf(T[] a, T v) {
    // returns the last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (a[i] == v) index = i;
    return index;
  }

  public static int lastIndexOfSlice(int[] a, int...v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP:
      for (int i = 0; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        index = i;
      }
    return index;
  }
  
  public static int lastIndexOfSlice(long[] a, long...v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP:
      for (int i = 0; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        index = i;
      }
    return index;
  }

  public static int lastIndexOfSlice(double[] a, double...v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP:
      for (int i = 0; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        index = i;
      }
    return index;
  }

  @SafeVarargs
  public static <T> int lastIndexOfSlice(T[] a, T...v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP:
      for (int i = 0; i < a.length-v.length+1; i++) {
        for (int j = i; j < i+v.length; j++) {
          if (!(a[j] == v[j-i])) continue LOOP;
        }
        index = i;
      }
    return index;
  }
  
  public static int lastIndexWhere(int[] a,  Predicate<Integer> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  index = i;
    return index;
  }
  
  public static int lastIndexWhere(long[] a,  Predicate<Long> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  index = i;
    return index;
  }

  public static int lastIndexWhere(double[] a,  Predicate<Double> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  index = i;
    return index;
  }
  
  public static <T >int lastIndexWhere(T[] a,  Predicate<T> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0) return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))  index = i;
    return index;
  }

  public static int[] map(int[] a, IntFunction<Integer> f) {
    assert a != null; assert a.length != 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).intValue();
    return b;
  }
  
  public static long[] map(long[] a, LongFunction<Long> f) {
    assert a != null; assert a.length != 0;
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).longValue();
    return b;
  }

  public static double[] map(double[] a, DoubleFunction<Double> f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).doubleValue();
    return b;
  }

  public static double[] mapToDouble(int[] a, IntToDoubleFunction f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[i]);
    return b;
  }
  
  public static double[] mapToDouble(long[] a, LongToDoubleFunction f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[i]);
    return b;
  }

  public static <R> R[] mapToR(int[] a, Function<Integer, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[i]);
    return r;    
  }
  
  public static <R> R[] mapToR(long[] a, Function<Long, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[i]);
    return r;    
  }

  public static <R> R[] mapToR(double[] a, Function<Double, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[i]);
    return r;    
  }
  
  public static <T, R> R[] map(T[] a, Function<T, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[i]);
    return r;    
  }

  public static int max(int[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]>max) max = a[i];
    return max;
  }
  
  public static long max(long[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    long max = Long.MIN_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]>max) max = a[i];
    return max;
  }

  public static double max(double[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    double max = Double.MIN_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]>max) max = a[i];
    return max;
  }

  public static <R extends Comparable<? super R>> int maxBy(int[] a, IntFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    int max = a[0];
    R rmax = f.apply(a[0]);
    R tmp = null;
    for (int i = 1; i < a.length; i++) {
      tmp = f.apply(a[i]);
      if (tmp.compareTo(rmax) > 0) {
        rmax=tmp;
        max = a[i];
      }
    }
    return max;
  }

  public static <R extends Comparable<? super R>> double maxBy(double[] a, 
      DoubleFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    double max = a[0];
    R rmax = f.apply(a[0]);
    R tmp = null;
    for (int i = 1; i < a.length; i++) {
      tmp = f.apply(a[i]);
      if (tmp.compareTo(rmax) > 0) {
        rmax = tmp;
        max = a[i];
      }
    }
    return max;
  }

  public static int min(int[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    int min = Integer.MAX_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]<min) min = a[i];
    return min;
  }
  
  public static long min(long[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    long min = Long.MAX_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]<min) min = a[i];
    return min;
  }

  public static double min(double[] a) {
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    double min = Double.MAX_VALUE;
    for (int i = 0; i < a.length; i++) 
      if (a[i]<min) min = a[i];
    return min;
  }

  public static <R extends Comparable<? super R>> int minBy(int[] a, IntFunction<R> f) {
    // returns the 1st element x which has the smallest f(x)
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    int min = a[0];
    R rmin = f.apply(a[0]);
    R tmp = null;
    for (int i = 1; i < a.length; i++) {
      tmp = f.apply(a[i]);
      if (tmp.compareTo(rmin) < 0) {
        rmin = tmp;
        min = a[i];
      }
    }
    return min;
  }
  
  public static <R extends Comparable<? super R>> double minBy(double[] a,
      DoubleFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    assert a != null; assert a.length > 0;
    if (a.length == 1) return a[0];
    double min = a[0];
    R rmin = f.apply(a[0]);
    R tmp = null;
    for (int i = 1; i < a.length; i++) {
      tmp = f.apply(a[i]);
      if (tmp.compareTo(rmin) < 0) {
        rmin = tmp;
        min = a[i];
      }
    }
    return min;
  }
  
  public static String mkString(int[] a) {
    // returns a String representation of a
    assert a != null;
    if (a.length == 0) return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 1; i < a.length-1; i++)
      sb.append(a[i]+",");
    sb.append(a[a.length-1]+"]");
    return sb.toString();
  }
    
  public static String mkString(double[] a) {
    // returns a String representation of a
    assert a != null;
    if (a.length == 0) return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 1; i < a.length-1; i++)
      sb.append(a[i]+",");
    sb.append(a[a.length-1]+"]");
    return sb.toString();
  }
  
  public static int[] intersectMultiset(int[] a, int[] b) {
    // returns a sorted array containing the multiset intersection of a and b
    // iff an element is in a and b all occurrences of it in b are retained
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new int[0];
    int[] as = uniqueSort(a);
    int[] q = new int[b.length];
    int qindex = 0;
    for (int i = 0; i < q.length; i++)
      if (contains(as,b[i])) q[qindex++] = b[i];
    int[] r = new int[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    swapSort(r);
    return r;
  }

  public static long[] intersectMultiset(long[] a, long[] b) {
    // returns a sorted array containing the multiset intersection of a and b
    // iff an element is in a and b all occurrences of it in b are retained
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new long[0];
    long[] as = uniqueSort(a);
    long[] q = new long[b.length];
    int qindex = 0;
    for (int i = 0; i < q.length; i++)
      if (contains(as,b[i])) q[qindex++] = b[i];
    long[] r = new long[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    swapSort(r);
    return r;
  }
  public static double[] intersectMultiset(double[] a, double[] b) {
    // returns a sorted array containing the multiset intersection of a and b
    // iff an element is in a and b all occurrences of it in b are retained
    assert a != null && b != null;
    if (a.length == 0 || b.length == 0) return new double[0];
    double[] as = uniqueSort(a);
    double[] q = new double[b.length];
    int qindex = 0;
    for (int i = 0; i < q.length; i++)
      if (contains(as,b[i])) q[qindex++] = b[i];
    double[] r = new double[qindex];
    for (int i = 0; i < qindex; i++) r[i] = q[i];
    swapSort(r);
    return r;
  }
  
  public static boolean nonEmpty(int[] a) {
    // iff a.length == 0 returns true else false
    assert a != null;
    if (a.length==0) return true;
    return false;
  }

  public static boolean nonEmpty(double[] a) {
    // iff a.length == 0 returns true else false
    assert a != null;
    if (a.length==0) return true;
    return false;
  }
  
  private static class Numel {
    // this class is for providing a method to flatten an array to 1D
    // regardless of its original dimensionality. 
    
    private long r = 0;
    
    public Numel(){} 
            
    long numel(Object a) {
      // if a is an array convert it to a 1D array discarding elements
      // that don't fit due to array length limit maxArrayLength.
      if( a == null) throw new IllegalArgumentException("numel: "
          + "argument can't be null");
      if (!a.getClass().isArray()) throw new IllegalArgumentException("numel: "
          + "argument must be an array");
            
      int dim = dim(a);
      int alen = Array.getLength(a);

      if (dim == 1) { // base case of recursion
        r+=alen;
      } else {
        for (int i = 0; i < alen; i++) numel(Array.get(a,i));
      }   
      return r;
    }  
  }
  
  public static long numel(Object a) {
    return (new Numel()).numel(a);
  }
  
  public static int[] padTo(int[] a, int len, int v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len) return (int[]) clone(a);
    int[] b = new int[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else b[i] = v;
    return b;
  }
  
  public static long[] padTo(long[] a, int len, long v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len) return (long[]) clone(a);
    long[] b = new long[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else b[i] = v;
    return b;
  }
  
  public static double[] padTo(double[] a, int len, double v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len) return (double[]) clone(a);
    double[] b = new double[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else b[i] = v;
    return b;
  }
  
  public static char[] padTo(char[] a, int len, char v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len) return (char[]) clone(a);
    char[] b = new char[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else b[i] = v;
    return b;
  }
  
  public static int[][] partition(int[] a, Predicate<Integer> p) {
    // partitions the elements of a into two arrays according to p
    // the first row of the result contains values of a for which p(a) is true
    // the second row of the result contains values of a for which p(a) is false
    assert a != null; assert a.length > 0;
    int[] q0 = new int[a.length];
    int q0index = 0;
    int[] q1 = new int[a.length];
    int q1index = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        q0[q0index++] = a[i];
      } else q1[q1index++] = a[i];
    int[] r0 = new int[q0index];
    for (int i = 0; i < q0index; i++) r0[i] = q0[i];
    int[] r1 = new int[q1index];
    for (int i = 0; i < q1index; i++) r1[i] = q1[i];
    int[][] r = new int[2][];
    r[0] = r0;
    r[1] = r1;
    return r;
  }
  
  public static double[][] partition(double[] a, Predicate<Double> p) {
    // partitions the elements of a into two arrays according to p
    // the first row of the result contains values of a for which p(a) is true
    // the second row of the result contains values of a for which p(a) is false
    assert a != null; assert a.length > 0;
    double[] q0 = new double[a.length];
    int q0index = 0;
    double[] q1 = new double[a.length];
    int q1index = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        q0[q0index++] = a[i];
      } else q1[q1index++] = a[i];
    double[] r0 = new double[q0index];
    for (int i = 0; i < q0index; i++) r0[i] = q0[i];
    double[] r1 = new double[q1index];
    for (int i = 0; i < q1index; i++) r1[i] = q1[i];
    double[][] r = new double[2][];
    r[0] = r0;
    r[1] = r1;
    return r;
  }
  
  public static int[] patch(int[] a, int from, int replaced, int...g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null; assert from >= 0; assert replaced >= 0;
    if (a.length == 0) return new int[0];
    if (a.length-1 < from) return (int[]) clone(a);
    if (g.length == 0 && replaced == 0) return (int[]) clone(a);
    int [] b = new int[a.length-replaced+g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from+g.length) {
        b[i] = g[i-from];
      } else b[i] = a[i+replaced-g.length];
    return b;
  }
  
  public static long[] patch(long[] a, int from, int replaced, int...g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null; assert from >= 0; assert replaced >= 0;
    if (a.length == 0) return new long[0];
    if (a.length-1 < from) return (long[]) clone(a);
    if (g.length == 0 && replaced == 0) return (long[]) clone(a);
    long [] b = new long[a.length-replaced+g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from+g.length) {
        b[i] = g[i-from];
      } else b[i] = a[i+replaced-g.length];
    return b;
  }
  
  public static double[] patch(double[] a, int from, int replaced, double...g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null; assert from >= 0; assert replaced >= 0;
    if (a.length == 0) return new double[0];
    if (a.length-1 < from) return (double[]) clone(a);
    if (g.length == 0 && replaced == 0) return (double[]) clone(a);
    double [] b = new double[a.length-replaced+g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from+g.length) {
        b[i] = g[i-from];
      } else b[i] = a[i+replaced-g.length];
    return b;
  }
  
  //Based on http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946
  //Based on https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup
  private static class PermutatorInt implements Iterator<int[]> {
    // helper method for permutations(int[])
    private int[] next = null;
    private final int n;
    private int[] perm;
    private int[] dirs;
    private int[] real;

    public PermutatorInt(int[] real) {
      n = real.length;
      this.real = real;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for(int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }

      next = perm;
    }

    @Override
    public int[] next() {
      int[] r = makeNext();
      // set z from real according to r
      int[] z = new int[n];
      for (int i = 0; i < n; i++) z[i] = real[r[i]];
      next = null;
      return z;
    }

    @Override
    public boolean hasNext() {
      return (makeNext() != null);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    private int[] makeNext() {
      if (next != null)
        return next;
      if (perm == null)
        return null;

      // find the largest element with != 0 direction
      int i = -1, e = -1;
      for(int j = 0; j < n; j++)
        if ((dirs[j] != 0) && (perm[j] > e)) {
          e = perm[j];
          i = j;
        }

      if (i == -1) // no such element -> no more premutations
        return (next = (perm = (dirs = null))); // no more permutations

      // swap with the element in its direction
      int k = i + dirs[i];
      swap(dirs, i, k);
      swap(perm, i, k);
      // if it's at the start/end or the next element in the direction
      // is greater, reset its direction.
      if ((k == 0) || (k == n-1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for(int j = 0; j < n; j++)
        if (perm[j] > e)
          dirs[j] = (j < k) ? +1 : -1;
      
      return (next = perm);
    }
  }

  //Based on http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946
  //Based on https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup
  private static class PermutatorLong implements Iterator<long[]> {
    // helper method for permutations(double[])
    private int[] next = null;
    private final int n;
    private int[] perm;
    private int[] dirs;
    private long[] real;

    public  PermutatorLong(long[] real) {
      n = real.length;
      this.real = real;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for(int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }

      next = perm;
    }

    @Override
    public long[] next() {
      int[] r = makeNext();
      // set z from real according to r
      long[] z = new long[n];
      for (int i = 0; i < n; i++) z[i] = real[r[i]];
      next = null;
      return z;
    }

    @Override
    public boolean hasNext() {
      return (makeNext() != null);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    private int[] makeNext() {
      if (next != null)
        return next;
      if (perm == null)
        return null;

      // find the largest element with != 0 direction
      int i = -1, e = -1;
      for(int j = 0; j < n; j++)
        if ((dirs[j] != 0) && (perm[j] > e)) {
          e = perm[j];
          i = j;
        }

      if (i == -1) // no such element -> no more premutations
        return (next = (perm = (dirs = null))); // no more permutations

      // swap with the element in its direction
      int k = i + dirs[i];
      swap(dirs, i, k);
      swap(perm, i, k);
      // if it's at the start/end or the next element in the direction
      // is greater, reset its direction.
      if ((k == 0) || (k == n-1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for(int j = 0; j < n; j++)
        if (perm[j] > e)
          dirs[j] = (j < k) ? +1 : -1;
      
      return (next = perm);
    }
  }
  
  //Based on http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946
  //Based on https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup
  private static class PermutatorDouble implements Iterator<double[]> {
    // helper method for permutations(double[])
    private int[] next = null;
    private final int n;
    private int[] perm;
    private int[] dirs;
    private double[] real;

    public  PermutatorDouble(double[] real) {
      n = real.length;
      this.real = real;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for(int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }

      next = perm;
    }

    @Override
    public double[] next() {
      int[] r = makeNext();
      // set z from real according to r
      double[] z = new double[n];
      for (int i = 0; i < n; i++) z[i] = real[r[i]];
      next = null;
      return z;
    }

    @Override
    public boolean hasNext() {
      return (makeNext() != null);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    private int[] makeNext() {
      if (next != null)
        return next;
      if (perm == null)
        return null;

      // find the largest element with != 0 direction
      int i = -1, e = -1;
      for(int j = 0; j < n; j++)
        if ((dirs[j] != 0) && (perm[j] > e)) {
          e = perm[j];
          i = j;
        }

      if (i == -1) // no such element -> no more premutations
        return (next = (perm = (dirs = null))); // no more permutations

      // swap with the element in its direction
      int k = i + dirs[i];
      swap(dirs, i, k);
      swap(perm, i, k);
      // if it's at the start/end or the next element in the direction
      // is greater, reset its direction.
      if ((k == 0) || (k == n-1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for(int j = 0; j < n; j++)
        if (perm[j] > e)
          dirs[j] = (j < k) ? +1 : -1;
      
      return (next = perm);
    }
  }
  
  // deepEquals, deepHashCode and deepToString 
  // are implemented in java.util.Arrays
  
  //Based on http://stackoverflow.com/questions/2000048/stepping-through-all-permutations-one-swap-at-a-time/11916946#11916946
  //Based on https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even.27s_speedup
  private static class Permutator<Y> implements Iterable<Y[]>, Iterator<Y[]> {
    // helper method for permutations(List<Y>) and  permutations(Y[])
    private int[] next = null;
    private final int n;
    private int[] perm;
    private int[] dirs;
    private final List<Y> rlist;
    
    public Permutator(Y[] rarray) {
      if (rarray == null) 
        throw new IllegalArgumentException("PermIterator(Y[] rarray) constructor: "
            +"rarray can't be null");
      this.rlist = makeList(rarray);
      n = rarray.length;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for(int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }
      next = perm;
    }

    public Permutator(List<Y> rlist) {
      if (rlist == null) 
        throw new IllegalArgumentException("PermIterator(List<Y> rlist) "
            +"constructor: rlist can't be null");
      n = rlist.size();
      this.rlist = rlist;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for(int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }
      next = perm;
    }

    private final List<Y> makeList(Y[] y) {
      if (y == null) throw new IllegalArgumentException(
          "makeList: y can't be null");
      List<Y> list = new ArrayList<>();
      for (int i = 0; i < y.length; i++) list.add(y[i]);
      return list;
    }
    
    @SafeVarargs
    private final Y[] makeArray(int[] r, Y...y) {
      if (r == null) 
        throw new IllegalArgumentException("makeArray: r can't be null");
      List<Y> q = new ArrayList<>();
      for (int i = 0; i < n; i++) q.add(rlist.get(r[i]));
      return q.toArray(y);
    }
    
    public Iterator<Y[]> iterator() {
      return this;
    }
  
    @Override
    public Y[] next() {
      int[] r = makeNext();
      next = null;
      return makeArray(r);
    }

    @Override
    public boolean hasNext() {
      return (makeNext() != null);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    private int[] makeNext() {
      if (next != null)
        return next;
      if (perm == null)
        return null;

      // find the largest element with != 0 direction
      int i = -1, e = -1;
      for(int j = 0; j < n; j++)
        if ((dirs[j] != 0) && (perm[j] > e)) {
          e = perm[j];
          i = j;
        }

      if (i == -1) // no such element -> no more premutations
        return (next = (perm = (dirs = null))); // no more permutations

      // swap with the element in its direction
      int k = i + dirs[i];
      swap(dirs, i, k);
      swap(perm, i, k);
      // if it's at the start/end or the next element in the direction
      // is greater, reset its direction.
      if ((k == 0) || (k == n-1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for(int j = 0; j < n; j++)
        if (perm[j] > e)
          dirs[j] = (j < k) ? +1 : -1;
      
      return (next = perm);
    }
  }
  
  public static Iterator<int[]> permutations(int[] y) {
    // helper method for permutationStream(int[])
    if (y == null) 
      throw new IllegalArgumentException("permutations: y can't be null");
    return new PermutatorInt(y);
  }
  
  public static Iterator<long[]> permutations(long[] y) {
    // helper method for permutationStream(long[])
    if (y == null) 
      throw new IllegalArgumentException("permutations: y can't be null");
    return new PermutatorLong(y);
  }
  
  public static Iterator<double[]> permutations(double[] y) {
    // helper method for permutationStream(double[])
    if (y == null) 
      throw new IllegalArgumentException("permutations: y can't be null");
    return new PermutatorDouble(y);
  }
  
  public static <Y> Iterator<Y[]> permutations(Y[] y) {
    // helper method for permutationStream(Y[])
    if (y == null) 
      throw new IllegalArgumentException("permutations: y can't be null");
    return new Permutator<Y>(y);
  }
  
  public static <Y> Iterator<Y[]> permutations(List<Y> y) {
 // helper method for permutationStream(Y[])
    if (y == null) 
      throw new IllegalArgumentException("permutations: y can't be null");
    return new Permutator<Y>(y);
  }
  
  public static Stream<int[]> permutationStream(int[] y) {
    if (y == null) 
      throw new IllegalArgumentException("permutationStream: y can't be null");
    Iterator<int[]> i = permutations(y);
    if (y.length < 21) {
      return Stream.generate(() -> i.next()).limit(factorial(y.length));
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static Stream<long[]> permutationStream(long[] y) {
    if (y == null) 
      throw new IllegalArgumentException("permutationStream: y can't be null");
    Iterator<long[]> i = permutations(y);
    if (y.length < 21) {
      return Stream.generate(() -> i.next()).limit(factorial(y.length));
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static Stream<double[]> permutationStream(double[] y) {
    if (y == null) 
      throw new IllegalArgumentException("permutationStream: y can't be null");
    Iterator<double[]> i = permutations(y);
    if (y.length < 21) {
      return Stream.generate(() -> i.next()).limit(factorial(y.length));
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static <Y> Stream<Y[]> permutationStream(Y[] y) {
    if (y == null) 
      throw new IllegalArgumentException("permutationStream: y can't be null");
    Iterator<Y[]> i = permutations(y);
    if (y.length < 21) {
      return Stream.generate(() -> i.next()).limit(factorial(y.length));
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static <Y> Stream<Y[]> permutationStream(List<Y> y) {
    if (y == null) 
      throw new IllegalArgumentException("permutationStream: y can't be null");
    Iterator<Y[]> i = permutations(y);
    if (y.size() < 21) {
      return Stream.generate(() -> i.next()).limit(factorial(y.size()));
    } else {
      return Stream.generate(() -> i.next());
    }
  }
  
  public static long factorial(int n) {
    // factorial(20) = 2,432,902,008,176,640,000 <
    //    Long.MAX_VALUE = Math.pow(2,63)-1 = 9,223,372,036,854,775,807 <
    //        factorial(21) = 51,090,942,171,709,440,000
    if (n > 20 || n < 0) 
      throw new IllegalArgumentException("factorial: n is out of range 0-20");
    return LongStream.rangeClosed(2, n).reduce(1, (a, b) -> a * b);
  }
  
  public static BigInteger factorial(BigInteger n) {
    // helper method for combinations
    if (n.compareTo(ZERO) < 0) throw new IllegalArgumentException("factorial: "
        + "n must be >= 0");
    if (n.equals(ZERO) || n.equals(ONE)) return ONE;
    BigInteger f = ONE;
    BigInteger g = ONE;
    
    //System.out.println("n.longValueExact()="+n.longValueExact());
    for (long i = 2; i <= n.longValueExact(); i++) {
      g = g.add(ONE);
      f = f.multiply(g);
    }
    return f;
  }
  
//   public static Iterator<Integer> (int[] a) {
//     return Arrays.stream(a).iterator();
//   }
  
  public static int prefixLength(int[] a, Predicate<Integer> p) {
    if (a == null) 
      throw new IllegalArgumentException("prefixLength: a can't be null");
    if (p == null) 
      throw new IllegalArgumentException("prefixLength: p can't be null");
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i])) return i;
    return a.length;
  }
  
  public static int prefixLength(double[] a, Predicate<Double> p) {
    if (a == null) 
      throw new IllegalArgumentException("prefixLength: a can't be null");
    if (p == null) 
      throw new IllegalArgumentException("prefixLength: p can't be null");
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i])) return i;
    return a.length;
  }
  
  public static int[] prepend(int[] a, int...p) {
    // returns a new array with p prepended to a
    if (a == null) 
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0) return (int[]) clone(a);
    int[] b = copyOf(p, a.length+p.length);
    for (int i = 0; i < a.length ; i++) b[p.length+i] = a[i];
    return b;  
  }
  
  public static long[] prepend(long[] a, long...p) {
    // returns a new array with p prepended to a
    if (a == null) 
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0) return (long[]) clone(a);
    long[] b = copyOf(p, a.length+p.length);
    for (int i = 0; i < a.length ; i++) b[p.length+i] = a[i];
    return b;  
  }
  
  public static double[] prepend(double[] a, double...p) {
    // returns a new array with p prepended to a
    if (a == null) 
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0) return (double[]) clone(a);
    double[] b = copyOf(p, a.length+p.length);
    for (int i = 0; i < a.length ; i++) b[p.length+i] = a[i];
    return b;  
  }
  
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T[] prepend(T[] a, T...p) {
    // returns a new array with p prepended to a
    if (a == null) 
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0) return (T[]) clone(a);
    T[] b = copyOf(p, a.length+p.length);
    for (int i = 0; i < a.length ; i++) b[p.length+i] = a[i];
    return b;  
  }
  
  public static int product(int[] a) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length == 0) 
      throw new IllegalArgumentException("product: a can't have zero length");
    int product = 1;
    for (int i = 0; i < a.length; i++) {
      product*=a[i];
      if (product == 0) return 0;
    }
    return product;
  }
  
  public static long product(long[] a) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length == 0) 
      throw new IllegalArgumentException("product: a can't have zero length");
    int product = 1;
    for (int i = 0; i < a.length; i++) {
      product*=a[i];
      if (product == 0) return 0;
    }
    return product;
  }
  
  public static double product(double[] a) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length == 0) 
      throw new IllegalArgumentException("product: a can't have zero length");
    double product = 1;
    for (int i = 0; i < a.length; i++) {
      product*=a[i];
      if (product == 0) return 0;
    }
    return product;
  }
  
  public static int reduceLeft(int[] a, IntBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    int r = f.applyAsInt(a[0], a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.applyAsInt(r, a[i]);
    return r;
  }
  
  public static int reduceRight(int[] a, IntBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    int r = f.applyAsInt(a[a.length-1], a[a.length-2]);
    for (int i = a.length-3; i > 0; i--)
      r = f.applyAsInt(a[i], r);  
    return r;
  }
  
  public static long reduceLeft(long[] a, LongBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    long r = f.applyAsLong(a[0], a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.applyAsLong(r, a[i]);
    return r;
  }
  
  public static long reduceRight(long[] a, LongBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    long r = f.applyAsLong(a[1], a[0]);
    for (int i = 2; i < a.length; i++)
      r = f.applyAsLong(a[i], r);
    return r;
  }
  
  public static double reduceLeft(int[] a, BiFunction<Double, Integer, Double> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[0]*1., a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.apply(r, a[i]);   //.applyAsInt(r, a[i]);
    return r;
  }

  public static double reduceRight(int[] a, BiFunction<Integer, Double, Integer> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[a.length-2], a[a.length-1]*1.);
    for (int i = a.length-3; i > 0; i--)
      r = f.apply(a[i], r);   //.applyAsInt(r, a[i]);
    return r;
  }
  
  public static double reduceLeft(long[] a, BiFunction<Double, Long, Double> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[0]*1., a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.apply(r, a[i]);   //.applyAsInt(r, a[i]);
    return r;
  }
  
  public static double reduceRight(long[] a, BiFunction<Long, Double, Long> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[a.length-2], a[a.length-1]*1.);
    for (int i = a.length-3; i > 0; i--)
      r = f.apply(a[i], r);   //.applyAsInt(r, a[i]);
    return r;
  }
  
  public static double reduceLeft(double[] a, DoubleBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.applyAsDouble(a[0], a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.applyAsDouble(r, a[i]);
    return r;
  }
  
  public static double reduceRight(double[] a, DoubleBinaryOperator f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.applyAsDouble(a[a.length-2], a[a.length-1]);
    for (int i = 2; i < a.length; i++)
      r = f.applyAsDouble(a[i], r);
    return r;
  }
  
  public static <U, T extends U> U reduceLeft(T[] a, BiFunction<U, T, U> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    U r = f.apply(a[0], a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.apply(r, a[i]);
    return r;
  }
  
  public static <U, T extends U> U reduceRight(T[] a, BiFunction<T, U, T> f) {
    if (a == null) 
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2) 
      throw new IllegalArgumentException("product: a's length must be >=2");
    U r = f.apply(a[a.length-2], a[a.length-1]);
    for (int i = a.length-3; i > 0; i--)
      r = f.apply(a[i], r);  
    return r;
  }
  
  public static int[] reverse(int[] a) {
    if (a == null) return null;
    int[] b = (int[]) clone(a);
    if (b.length < 2) return b;
    int n = b.length;
    for (int i = 0; i < n/2; i++) {
      b[i] = a[n-1-i];
      b[n-i-1] = a[i];
    }
    return b;
  }

  public static long[] reverse(long[] a) {
    if (a == null) return null;
    long[] b = (long[]) clone(a);
    if (b.length < 2) return b;
    int n = b.length;
    for (int i = 0; i < n/2; i++) {
      b[i] = a[n-1-i];
      b[n-i-1] = a[i];
    }
    return b;
  }
  
  public static double[] reverse(double[] a) {
    if (a == null) return null;
    double[] b = (double[]) clone(a);
    if (b.length < 2) return b;
    int n = b.length;
    for (int i = 0; i < n/2; i++) {
      b[i] = a[n-1-i];
      b[n-i-1] = a[i];
    }
    return b;
  }
  
  public static <T> T[] reverse(T[] a) {
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    // filling b with median value of a in case a.length is odd
    T[] b = makeFromValue(a[a.length/2], a.length); 
    if (b.length < 2) return b;
    int n = b.length;
    for (int i = 0; i < n/2; i++) {
      // skips median when a.length is odd
      b[i] = a[n-1-i];
      b[n-i-1] = a[i];
    }
    return b;
  }
  
  public static void reverseInPlace(int[] a) {
    if (a == null || a.length < 2) return;
    int n = a.length;
    for (int i = 0; i < n/2; i++) {
      int temp = a[i];
      a[i] = a[n-1-i];
      a[n-i-1] = temp;
    }
  }
  
  public static void reverseInPlace(long[] a) {
    if (a == null || a.length < 2) return;
    int n = a.length;
    for (int i = 0; i < n/2; i++) {
      long temp = a[i];
      a[i] = a[n-1-i];
      a[n-i-1] = temp;
    }
  }
  
  public static void reverseInPlace(double[] a) {
    if (a == null || a.length < 2) return;
    int n = a.length;
    for (int i = 0; i < n/2; i++) {
      double temp = a[i];
      a[i] = a[n-1-i];
      a[n-i-1] = temp;
    }
  }
  
  public static <T> void reverseInPlace(T[] a) {
    if (a == null || a.length < 2) return;
    int n = a.length;
    for (int i = 0; i < n/2; i++) {
      T temp = a[i];
      a[i] = a[n-1-i];
      a[n-i-1] = temp;
    }
  }
  
  public static OfInt ReverseIterator(int[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }
  
  public static OfLong ReverseIterator(long[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }
  
  public static OfDouble ReverseIterator(double[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static <T> Iterator<T> ReverseIterator(T[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static int[] reverseMap(int[] a, IntFunction<Integer> f) {
    assert a != null; assert a.length != 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length-1-i]).intValue();
    return b;
  }
  
  public static int[] reverseMapToInt(int[] a, IntFunction<Integer> f) {
    return map(a,f);
  }
  
  public static long[] reverseMap(long[] a, LongFunction<Long> f) {
    assert a != null; assert a.length != 0;
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length-1-i]).longValue();
    return b;
  }
  
  public static long[] reverseMapToLong(long[] a, LongFunction<Long> f) {
    return reverseMap(a, f);
  }

  public static double[] reverseMap(double[] a, DoubleFunction<Double> f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length-1-i]).doubleValue();
    return b;
  }
  
  public static double[] reverseMapToDouble(double[] a, DoubleFunction<Double> f) {
    return map(a, f);
  }

  public static double[] reverseMapToDouble(int[] a, IntToDoubleFunction f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[a.length-1-i]);
    return b;
  }
  
  public static double[] reverseMapToDouble(long[] a, LongToDoubleFunction f) {
    assert a != null; assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[a.length-1-i]);
    return b;
  }

  public static <R> R[] reverseMapToR(int[] a, Function<Integer, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[a.length-1-i]);
    return r;    
  }
  
  public static <R> R[] reverseMapToR(long[] a, Function<Long, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[a.length-1-i]);
    return r;    
  }

  public static <R> R[] reverseMapToR(double[] a, Function<Double, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[a.length-1-i]);
    return r;    
  }
  
  public static <T, R> R[] reverseMap(T[] a, Function<T, R> f) {
    assert a != null; assert a.length != 0;
    R[] r = makeGen1D(a.length);
    for (int i = 0; i < a.length; i++) r[i] = f.apply(a[a.length-1-i]);
    return r;    
  }
  
  public static boolean sameElements(int[] a, int[] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(long[] a, long[] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(double[] a, double[] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static <T> boolean sameElements(T[] a, T[] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(int[][] a, int[][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(long[][] a, long[][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(double[][] a, double[][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static <T> boolean sameElements(T[][] a, T[][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(int[][][] a, int[][][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(long[][][] a, long[][][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static boolean sameElements(double[][][] a, double[][][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static <T> boolean sameElements(T[][][] a, T[][][] b) {
    if (a == null && b == null) throw new IllegalArgumentException(""
        + "sameElements: both arrays can't be null");
    return equals(a,b); 
  }
  
  public static int[] scanLeft(int[] a, B1<Integer,Integer> op, int z) {
    // this returns an int[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any int.
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    int[] result = new int[a.length+1];
    result[0] = z;
    int r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }

  public static long[] scanLeft(int[] a, B1<Integer,Long> op, long z) {
    // this returns a long[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any long.
    if (a == null) return null;
    if (a.length == 0) return new long[0];
    long[] result = new long[a.length+1];
    result[0] = z;
    long r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }
 
  public static double[] scanLeft(int[] a, B1<Integer,Double> op, double z) {
    // this returns a double[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any double.
    if (a == null) return null;
    if (a.length == 0) return new double[0];
    double[] result = new double[a.length+1];
    result[0] = z;
    double r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }
  
  public static long[] scanLeft(long[] a, B1<Long,Long> op, long z) {
    // this returns a long[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any long.
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    long[] result = new long[a.length+1];
    result[0] = z;
    long r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }
  
  public static double[] scanLeft(long[] a, B1<Long,Double> op, double z) {
    // this returns a double[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any double.
    if (a == null) return null;
    if (a.length == 0) return new double[0];
    double[] result = new double[a.length+1];
    result[0] = z;
    double r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }

  public static double[] scanLeft(double[] a, B1<Double,Double> op, double z) {
    // this returns a double[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // from index 1 forward. z can be any double.
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    double[] result = new double[a.length+1];
    result[0] = z;
    double r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }
 
  public static <T,R> R[] scanLeft(T[] a, B1<T,R> op, R z) {
    // this returns a R[] initialized with z at index 0 and then 
    // folds from the left inserting intermediate and final results
    // from index 1 forward into that array. z can be any T.
    if (a == null) return null;
    if (a.length == 0) return makeGen1D(0);
    R[] result = makeGen1D(a.length+1); 
    result[0] = z;
    R r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i+1] = r;
    }
    return result;
  }
  
  public static int[] scanRight(int[] a, C1<Integer,Integer> op, int z) {
    // this returns an int[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any int.
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    int[] result = copyOf(a, a.length+1);
    int r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static long[] scanRight(int[] a, C2<Integer,Long> op, long z) {
    // this returns a long[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any long.
    if (a == null) return null;
    if (a.length == 0) return new long[0];
    long[] result = new long[a.length+1];
    long r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static double[] scanRight(int[] a, C2<Integer,Double> op, double z) {
    // this returns a double[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any double.
    if (a == null) return null;
    if (a.length == 0) return new double[0];
    double[] result = new double[a.length+1];
    double r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static long[] scanRight(long[] a, C1<Long,Long> op, long z) {
    // this returns a long[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any long.
    if (a.length == 0) return copyOf(a,0);
    long[] result = copyOf(a, a.length+1);
    long r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static double[] scanRight(long[] a, C1<Long,Double> op, double z) {
    // this returns a double[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any double.
    if (a.length == 0) return new double[0];
    double[] result = new double[a.length+1];
    double r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static double[] scanRight(double[] a, C1<Double,Double> op, double z) {
    // this returns a double[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // final results from index t.length-2 backward into that array.
    // z can be any double.
    if (a == null) return null;
    if (a.length == 0) return copyOf(a,0);
    double[] result = copyOf(a, a.length+1);
    double r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static <R,T> R[] scanRight(T[] a, C1<T,R> op, R z) {
    // this returns a T[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and 
    // final results from index t.length-2 backward into that
    // array. z can be any T.
 
    if (a == null) return null;
    if (a.length == 0) return makeGen1D(0);
    R[] result = makeGen1D(a.length+1);
    R r = z;
    result[result.length-1] = z;
    for (int i = a.length-1; i > -1; i--) {
      r = op.apply(a[i],r);
      result[i] = r;
    }
    return result;
  }
  
  public static int segmentLength(int[] a, Predicate<Integer> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length-1) return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++; continue;
      } else if (started && !p.test(a[i])) {
        if (c > max) max = c; started = false; c=0; continue;
      } else if (!started && p.test(a[i])) {
        started = true; c++; continue;
      }
    }
    return max;
  }
 
  public static int segmentLength(long[] a, Predicate<Long> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length-1) return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++; continue;
      } else if (started && !p.test(a[i])) {
        if (c > max) max = c; started = false; c=0; continue;
      } else if (!started && p.test(a[i])) {
        started = true; c++; continue;
      }
    }
    return max;
  }
  
  public static int segmentLength(double[] a, Predicate<Double> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length-1) return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++; continue;
      } else if (started && !p.test(a[i])) {
        if (c > max) max = c; started = false; c=0; continue;
      } else if (!started && p.test(a[i])) {
        started = true; c++; continue;
      }
    }
    return max;
  }
  
  public static <T> int segmentLength(T[] a, Predicate<T> p, int s) {
    // returns the length of the longest segment
    // satisfying p starting at index s.
    if (a == null || a.length == 0 || s > a.length-1) return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++; continue;
      } else if (started && !p.test(a[i])) {
        if (c > max) {max = c; started = false; c=0; continue;}
      } else if (!started && p.test(a[i])) {
        started = true; c++; continue;
      }
    }
    return max;
  }
  
  public static int size(int[] a) {
    return a.length;
  }
  
  public static int size(long[] a) {
    return a.length;
  }
  
  public static int size(double[] a) {
    return a.length;
  }
  
  public static <T> int size(T[] a) {
    return a.length;
  }
  
  public static int[] slice(int[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null) throw new IllegalArgumentException("slice: "
        + "a = null but it must be non null");
    if (from < 0 ) throw new IllegalArgumentException("slice: "
        + "from="+from+" but it must be >= 0");
    if (until<=from||until>a.length) throw new IllegalArgumentException("slice: "
        + "until="+until+" but it must be > from="+from+" and <= a.length="+a.length);
    int[] b = new int[until-from];
    for (int i = from; i < until; i++) b[i-from]=a[i];
    return b;
  }
  
  public static double[] slice(long[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null) throw new IllegalArgumentException("slice: "
        + "a = null but it must be non null");
    if (from < 0 ) throw new IllegalArgumentException("slice: "
        + "from="+from+" but it must be >= 0");
    if (until<=from||until>a.length) throw new IllegalArgumentException("slice: "
        + "until="+until+" but it must be > from="+from+" and <= a.length="+a.length);
    double[] b = new double[until-from];
    for (int i = from; i < until; i++) b[i-from]=a[i];
    return b;
  }
  
  public static double[] slice(double[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null) throw new IllegalArgumentException("slice: "
        + "a = null but it must be non null");
    if (from < 0 ) throw new IllegalArgumentException("slice: "
        + "from="+from+" but it must be >= 0");
    if (until<=from||until>a.length) throw new IllegalArgumentException("slice: "
        + "until="+until+" but it must be > from="+from+" and <= a.length="+a.length);
    double[] b = new double[until-from];
    for (int i = from; i < until; i++) b[i-from]=a[i];
    return b;
  }
  
  public static <T> T[] slice(T[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null) throw new IllegalArgumentException("slice: "
        + "a = null but it must be non null");
    if (from < 0 ) throw new IllegalArgumentException("slice: "
        + "from="+from+" but it must be >= 0");
    if (until<=from||until>a.length) throw new IllegalArgumentException("slice: "
        + "until="+until+" but it must be > from="+from+" and <= a.length="+a.length);
    T[] b = makeFromValue(a[0],until-from);
    for (int i = from; i < until; i++) b[i-from]=a[i];
    return b;
  }
 
  private static class SlidingInt implements Iterator<int[]> {
    private final int size; // number of elements per group
    private final int step; // distance between the first elements of successive groups
    private int current = 0; // current step location in a
    private final int[] a; // the array to slide
    private final int n; // length of a
    private int[] t; // array of size a.length for the window

    SlidingInt(int[] a, int size, int step) {
      if (a == null) throw new IllegalArgumentException("SlidingInt: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingInt: "
          + "size="+size+" but it must be > 0");
      if (step < 1 ) throw new IllegalArgumentException("SlidingInt: "
          + "step="+step+" it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }
    
    SlidingInt(int[] a, int size) {
      if (a == null) throw new IllegalArgumentException("SlidingInt: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingInt: "
          + "size="+size+" but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }
    
    @Override
    public  boolean hasNext() {
      return current+size <= n;
    }

    @Override
    public int[] next() {
      t = new int[size];
      for (int j = 0; j < size; j++) t[j] = a[current+j];
      current+=step;
      return t;
    }
    
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class SlidingLong implements Iterator<long[]> {
    private final int size; // number of elements per group
    private final int step; // distance between the first elements of successive groups
    private int current = 0; // current step location in a
    private final long[] a; // the array to slide
    private final int n; // length of a
    private long[] t; // array of size a.length for the window

    SlidingLong(long[] a, int size, int step) {
      if (a == null) throw new IllegalArgumentException("SlidingLong: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingLong: "
          + "size="+size+" but it must be > 0");
      if (step < 1 ) throw new IllegalArgumentException("SlidingLong: "
          + "step="+step+" it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }
    
    SlidingLong(long[] a, int size) {
      if (a == null) throw new IllegalArgumentException("SlidingLong: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingLong: "
          + "size="+size+" but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }
    
    @Override
    public  boolean hasNext() {
      return current+size <= n;
    }

    @Override
    public long[] next() {
      t = new long[size];
      for (int j = 0; j < size; j++) t[j] = a[current+j];
      current+=step;
      return t;
    }
    
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class SlidingDouble implements Iterator<double[]> {
    private final int size; // number of elements per group
    private final int step; // distance between the first elements of successive groups
    private int current = 0; // current step location in a
    private final double[] a; // the array to slide
    private final int n; // length of a
    private double[] t; // array of size a.length for the window

    SlidingDouble(double[] a, int size, int step) {
      if (a == null) throw new IllegalArgumentException("SlidingDouble: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingDouble: "
          + "size="+size+" but it must be > 0");
      if (step < 1 ) throw new IllegalArgumentException("SlidingDouble: "
          + "step="+step+" it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }
    
    SlidingDouble(double[] a, int size) {
      if (a == null) throw new IllegalArgumentException("SlidingDouble: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("SlidingDouble: "
          + "size="+size+" but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }
    
    @Override
    public  boolean hasNext() {
      return current+size <= n;
    }

    @Override
    public double[] next() {
      t = new double[size];
      for (int j = 0; j < size; j++) t[j] = a[current+j];
      current+=step;
      return t;
    }
    
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static class Sliding<T> implements Iterator<T[]> {
    private final int size; // number of elements per group
    private final int step; // distance between the first elements of successive groups
    private int current = 0; // current step location in a
    private final T[] a; // the array to slide
    private final int n; // length of a
    private T[] t; // array of size a.length for the window

    Sliding(T[] a, int size, int step) {
      if (a == null) throw new IllegalArgumentException("Sliding: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("Sliding: "
          + "size="+size+" but it must be > 0");
      if (step < 1 ) throw new IllegalArgumentException("Sliding: "
          + "step="+step+" it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }
    
    Sliding(T[] a, int size) {
      if (a == null) throw new IllegalArgumentException("Sliding: "
          + "a = null but it must be non null");
      if (size < 1 ) throw new IllegalArgumentException("Sliding: "
          + "size="+size+" but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }
    
    @Override
    public  boolean hasNext() {
      return current+size <= n;
    }

    public T[] next() {
      t = makeFromValue(a[0], size);
      for (int j = 0; j < size; j++) t[j] = a[current+j];
      current+=step;
      return t;
    }
    
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public static Iterator<int[]> sliding(int[] a, int size, int step) {
    // helper method for slidingStream(int[])
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    return new SlidingInt(a, size, step);
  }
  
  public static Iterator<int[]> sliding(int[] a, int size) {
    // helper method for slidingStream(int[])
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    return new SlidingInt(a, size);
  }
  
  public static Iterator<long[]> sliding(long[] a, int size, int step) {
    // helper method for slidingStream(long[])
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    return new SlidingLong(a, size, step);
  }
  
  public static Iterator<long[]> sliding(long[] a, int size) {
    // helper method for slidingStream(long[])
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    return new SlidingLong(a, size);
  }
  
  public static Iterator<double[]> sliding(double[] a, int size, int step) {
    // helper method for slidingStream(double[])
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    return new SlidingDouble(a, size, step);
  }
  
  public static Iterator<double[]> sliding(double[] a, int size) {
    // helper method for slidingStream(double[])
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    return new SlidingDouble(a, size);
  }
  
  public static <T> Iterator<T[]> sliding(T[] a, int size, int step) {
    // helper method for slidingStream(T[])
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    return new Sliding<T>(a, size, step);
  }
  
  public static <T> Iterator<T[]> sliding(T[] a, int size) {
    // helper method for slidingStream(T[])
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    return new Sliding<T>(a, size);
  }

  public static Stream<int[]> slidingStream(int[] a, int size, int step) {
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    Iterator<int[]> i = sliding(a, size, step);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,step));
  }
  
  public static Stream<int[]> slidingStream(int[] y, int size) {
    // step defaults to 1
    if (y == null) 
      throw new IllegalArgumentException("slidingStream: y can't be null");
    Iterator<int[]> i = sliding(y, size);
      return Stream.generate(() -> i.next()).limit(slidingCount(y.length,size,1));
  }
  
  public static Stream<long[]> slidingStream(long[] a, int size, int step) {
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    Iterator<long[]> i = sliding(a, size, step);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,step));
  }
  
  public static Stream<long[]> slidingStream(long[] a, int size) {
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    Iterator<long[]> i = sliding(a, size);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,1));
  }
  
  public static Stream<double[]> slidingStream(double[] a, int size, int step) {
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    Iterator<double[]> i = sliding(a, size, step);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,step));
  }
  
  public static Stream<double[]> slidingStream(double[] a, int size) {
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    Iterator<double[]> i = sliding(a, size);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,1));
  }
  
  public static <T> Stream<T[]> slidingStream(T[] a, int size, int step) {
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    if (step < 1 ) throw new IllegalArgumentException("sliding: "
        + "step="+step+" it must be > 0");
    Iterator<T[]> i = sliding(a, size, step);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,step));
  }
  
  public static <T> Stream<T[]> slidingStream(T[] a, int size) {
    // step defaults to 1
    if (a == null) throw new IllegalArgumentException("sliding: "
        + "a = null but it must be non null");
    if (size < 1 ) throw new IllegalArgumentException("sliding: "
        + "size="+size+" but it must be > 0");
    Iterator<T[]> i = sliding(a, size);
      return Stream.generate(() -> i.next()).limit(slidingCount(a.length,size,1));
  }
  
  public static int slidingCount(int n, int size, int step) {
    // count number of sliding windows for array length n, size and step
    if (n<0||size<1||step<1) throw new IllegalArgumentException("slidingCount: "
        + "n="+n+", size="+size+" and step="+step+" but requires n >= 0, "
        + "size > 0 and step > 0");
    int i = 0;
    int c = 0;
    while(c+size <= n) {
      i++; c+=step;
    }
    return i;
  }
  
  public static <U extends Comparable<? super U>> int[] sortBy(int[] a,
      Function<? super Integer, ? extends U> e, Comparator<? super U> c) {
    // example usage:
    // int[] c = {1,2,3,4,5};
    // String[] d = {"B","A","E","D","C"};
    // printArray(sortBy(c,x->d[(d.length+2)%x],(s,t)->s.compareTo(t)));
    // result: [2,3,1,4,5]
    if (a==null||e==null||c==null) throw new IllegalArgumentException("sortBy: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length); 
    return (int[])unbox(sortBy((Integer[])box(a),e,c));
  }
  
  public static <U extends Comparable<? super U>> long[] sortBy(long[] a,
      Function<? super Long, ? extends U> e, Comparator<? super U> c) {
    if (a==null||e==null||c==null) throw new IllegalArgumentException("sortBy: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    return (long[])unbox(sortBy((Long[])box(a),e,c));
  }
  
  public static <U extends Comparable<? super U>> double[] sortBy(double[] a,
      Function<? super Double, ? extends U> e, Comparator<? super U> c) {
    if (a==null||e==null||c==null) throw new IllegalArgumentException("sortBy: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    return (double[])unbox(sortBy((Double[])box(a),e,c));
  }

  public static <T,U extends Comparable<? super U>> T[] sortBy(T[] a,
      Function<? super T,? extends U> e, Comparator<? super U> c) {
    // example usage:
    // Integer[] c = {1,2,3,4,5};
    // String[] d = {"B","A","E","D","C"};
    // printArray(sortBy(c,x->d[(d.length+2)%x],(s,t)->t.compareTo(s)));
    // result: [5,4,1,2,3]
    if (a==null||e==null||c==null) throw new IllegalArgumentException("sortBy: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    T[] b = copyOf(a,a.length);
    Arrays.sort(b,comparing(e, c));
    return b;
  }
  
  public static int[] sortWith(int[] a, BiPredicate<Integer,Integer> b) {
    if (a==null||b==null) throw new IllegalArgumentException("sortWith: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    Integer[] d = (Integer[])box(a);
    Arrays.sort(d, (t1,t2) -> t1.equals(t2) ? 0 : (b.test(t1,t2) ? -1 : 1));
    return (int[])unbox(d);
  }
  
  public static long[] sortWith(long[] a, BiPredicate<Long,Long> b) {
    if (a==null||b==null) throw new IllegalArgumentException("sortWith: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    Long[] d = (Long[])box(a);
    Arrays.sort(d, (t1,t2) -> t1.equals(t2) ? 0 : (b.test(t1,t2) ? -1 : 1));
    return (long[])unbox(d);
  }
  
  public static double[] sortWith(double[] a, BiPredicate<Double,Double> b) {
    if (a==null||b==null) throw new IllegalArgumentException("sortWith: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    Double[] d = (Double[])box(a);
    Arrays.sort(d, (t1,t2) -> t1.equals(t2) ? 0 : (b.test(t1,t2) ? -1 : 1));
    return (double[])unbox(d);
  }
  
  public static <T> T[] sortWith(T[] a, BiPredicate<T,T> b) {
    if (a==null||b==null) throw new IllegalArgumentException("sortWith: "
        + "all arguments must be non null");
    if (a.length < 2) return copyOf(a, a.length);
    T[] d = copyOf(a,a.length);
    Arrays.sort(d, (t1,t2) -> t1.equals(t2) ? 0 : (b.test(t1,t2) ? -1 : 1));
    return d;
  }
  
  public static int[] sorted(int[] a, Comparator<? super Integer> c) {
    if (a==null||c==null) throw new IllegalArgumentException("sorted: "
        + "all arguments must be non null");
    Integer[] d = (Integer[])box(a);
    Arrays.sort(d,c);
    return (int[])unbox(d);
  }
  
  public static long[] sorted(long[] a, Comparator<? super Long> c) {
    if (a==null||c==null) throw new IllegalArgumentException("sorted: "
        + "all arguments must be non null");
    Long[] d = (Long[])box(a);
    Arrays.sort(d,c);
    return (long[])unbox(d);
  }
  
  public static double[] sorted(double[] a, Comparator<? super Double> c) {
    if (a==null||c==null) throw new IllegalArgumentException("sorted: "
        + "all arguments must be non null");
    Double[] d = (Double[])box(a);
    Arrays.sort(d,c);
    return (double[])unbox(d);
  }
  
  public static <T> T[] sorted(T[] a, Comparator<? super T> c) {
    if (a==null||c==null) throw new IllegalArgumentException("sorted: "
        + "all arguments must be non null");
    T[] d = copyOf(a,a.length);
    Arrays.sort(d,c);
    return d;
  }
  
  public static int[][] span(int[] a, Predicate<Integer> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a==null) throw new IllegalArgumentException("span: "
        + "the array must be non null");
    int[] b = new int[0]; int[] c = new int[0];
    Integer[] d = (Integer[])box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    int[][] r = new int[2][];
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static long[][] span(long[] a, Predicate<Long> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a==null) throw new IllegalArgumentException("span: "
        + "the array must be non null");
    long[] b = new long[0]; long[] c = new long[0];
    Long[] d = (Long[])box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    long[][] r = new long[2][];
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static double[][] span(double[] a, Predicate<Double> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a==null) throw new IllegalArgumentException("span: "
        + "the array must be non null");
    double[] b = new double[0]; double[] c = new double[0];
    Double[] d = (Double[])box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    double[][] r = new double[2][];
    r[0] = b; r[1] = c;
    return r;
  }

  public static <T> T[][] span(T[] a, Predicate<T> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a==null) throw new IllegalArgumentException("span: "
        + "the array must be non null");
    T[] b = copyOf(a,0); T[] c = copyOf(a,0);
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    T[][] r = makeGen2D(2,0);
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static int[][] splitAt(int[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a==null) throw new IllegalArgumentException("splitAt: "
        + "the array must be non null");
    int[] b = new int[0]; int[] c = new int[0];
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    int[][] r = new int[2][];
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static double[][] splitAt(double[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a==null) throw new IllegalArgumentException("splitAt: "
        + "the array must be non null");
    double[] b = new double[0]; double[] c = new double[0];
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    double[][] r = new double[2][];
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static <T> T[][] splitAt(T[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a==null) throw new IllegalArgumentException("splitAt: "
        + "the array must be non null");
    T[] b = copyOf(a,0); T[] c = copyOf(a,0);
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b = add(b,a[i]);
      } else c = add(c,a[i]);
    T[][] r = makeGen2D(2,0);
    r[0] = b; r[1] = c;
    return r;
  }
  
  public static boolean startsWith(int[] a, int offset, int...b) {
    // returns true if a contains b starting at offset
    if (a==null||a.length==0||offset<0|b.length==0||offset+b.length>a.length) 
      return false;
    for (int i = offset; i < offset+b.length; i++)
      if (a[i]!=b[i-offset]) return false;
    return true;
  }
  
  public static boolean startsWith(long[] a, int offset, long...b) {
    // returns true if a contains b starting at offset
    if (a==null||a.length==0||offset<0|b.length==0||offset+b.length>a.length) 
      return false;
    for (int i = offset; i < offset+b.length; i++)
      if (a[i]!=b[i-offset]) return false;
    return true;
  }
  
  public static boolean startsWith(double[] a, int offset, double...b) {
    // returns true if a contains b starting at offset
    if (a==null||a.length==0||offset<0|b.length==0||offset+b.length>a.length) 
      return false;
    for (int i = offset; i < offset+b.length; i++)
      if (a[i]!=b[i-offset]) return false;
    return true;
  }
  
  @SafeVarargs
  public static <T> boolean startsWith(T[] a, int offset, T...b) {
    // returns true if a contains b starting at offset
    if (a==null||a.length==0||offset<0|b.length==0||offset+b.length>a.length) 
      return false;
    for (int i = offset; i < offset+b.length; i++)
      if (a[i]!=b[i-offset]) return false;
    return true;
  }
  
  public static String stringPrefix(Object o) {
    if (o == null) throw new IllegalArgumentException("stringPrefix: "
        + "argument can't be null");
    return o.getClass().getName();
  }
  
  public static CharSequence subSequence(char[] a, int start, int end) {
    // returns a CharSequence of chars of a from index start through index
    // end-1, however if index < 0 it's corrected to 0 and |index| is added 
    // to end; and if end > a.length it's corrected to a.length
     if (a== null) throw new IllegalArgumentException("subSequence: "
         + "argument can't be null"); 
     if (end<=start||start>a.length) return "".subSequence(0,0);
     if (start < 0) {end-=start; start=0;}
     if (end>a.length) end=a.length;
     char[] b = new char[end-start];
     for (int i = start; i < b.length; i++) b[i-start] = a[i];
     return String.valueOf(b).subSequence(0,b.length);
  }
  
  public static CharSequence subSequence(Character[] a, int start, int end) {
    // returns a CharSequence of chars of a from index start through index
    // end-1, however if index < 0 it's corrected to 0 and |index| is added
    // to end; and if end > a.length it's corrected to a.length
     if (a== null) throw new IllegalArgumentException("subSequence: "
         + "argument can't be null"); 
     if (end<=start||start>a.length) return "".subSequence(0,0);
     if (start < 0) {end-=start; start=0;}
     if (end>a.length) end=a.length;
     char[] b = (char[]) unbox(a);
     char[] c = new char[end-start];
     for (int i = start; i < c.length; i++) c[i-start] = b[i];
     return String.valueOf(c).subSequence(0,c.length);
  }
  
  public static long sum(int[] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++) sum+=a[i];
    return sum;
  }
  
  public static long sum(long[] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++) sum+=a[i];
    return sum;
  }

  public static double sum(double[] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++) sum+=a[i];
    return sum;
  }
  
  public static BigInteger sum(BigInteger[] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++) sum = sum.add(a[i]);
    return sum;
  }
  
  public static BigDecimal sum(BigDecimal[] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++) sum = sum.add(a[i]);
    return sum;
  }
  
  public static long sum(int[][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum+=a[i][j];
    return sum;
  }
  
  public static long sum(long[][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum+=a[i][j];
    return sum;
  }
  
  public static double sum(double[][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum+=a[i][j];
    return sum;
  }
  
  public static BigInteger sum(BigInteger[][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j]);
    return sum;
  }
  
  public static BigDecimal sum(BigDecimal[][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j]);
    return sum;
  }

  public static long sum(int[][][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum+=a[i][j][k];
    return sum;
  }
  
  public static long sum(long[][][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum+=a[i][j][k];
    return sum;
  }
  
  public static double sum(double[][][] a) {
    // return an array with all elements of a except the first
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum+=a[i][j][k];
    return sum;
  }
  
  public static BigInteger sum(BigInteger[][][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k]);
    return sum;
  }
  
  public static BigDecimal sum(BigDecimal[][][] a) {
    // return the sum of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k]);
    return sum;
  }
  
  //***************************************************************************
  public static long absum(int[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++) 
      if (a[i] < 0) {sum-=a[i];} else sum+=a[i];
    return sum;
  }
  
  public static long absum(long[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < 0) {sum-=a[i];} else sum+=a[i];
    return sum;
  }

  public static double absum(double[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < 0) {sum-=a[i];} else sum+=a[i];
    return sum;
  }
  
  public static BigInteger absum(BigInteger[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++) sum = sum.add(a[i].abs());
    return sum;
  }
  
  public static BigDecimal absum(BigDecimal[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++) sum = sum.add(a[i].abs());
    return sum;
  }
  
  public static long absum(int[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {sum-=a[i][j];} else sum+=a[i][j];
    return sum;
  }
  
  public static long absum(long[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {sum-=a[i][j];} else sum+=a[i][j];
    return sum;
  }
  
  public static double absum(double[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {sum-=a[i][j];} else sum+=a[i][j];
    return sum;
  }
  
  public static BigInteger absum(BigInteger[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j].abs());
    return sum;
  }
  
  public static BigDecimal absum(BigDecimal[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j].abs());
    return sum;
  }

  public static long absum(int[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {sum-=a[i][j][k];} else sum+=a[i][j][k];
    return sum;
  }
  
  public static long absum(long[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {sum-=a[i][j][k];} else sum+=a[i][j][k];
    return sum;
  }
  
  public static double absum(double[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {sum-=a[i][j][k];} else sum+=a[i][j][k];
    return sum;
  }
  
  public static BigInteger absum(BigInteger[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k].abs());
    return sum;
  }
  
  public static BigDecimal absum(BigDecimal[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null) throw new IllegalArgumentException(
        "sum: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k].abs());
    return sum;
  }
    
  public static int[] tail(int[] a) {
    // return an array with all elements of a except the first
    if (a == null) throw new IllegalArgumentException(
        "tail: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tail: array must have at least one element");
    return copyOfRange(a,1,a.length);
  }
  
  public static long[] tail(long[] a) {
    // return an array with all elements of a except the first
    if (a == null) throw new IllegalArgumentException(
        "tail: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tail: array must have at least one element");
    return copyOfRange(a,1,a.length);
  }
  
  public static double[] tail(double[] a) {
    // return an array with all elements of a except the first
    if (a == null) throw new IllegalArgumentException(
        "tail: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tail: array must have at least one element");
    return copyOfRange(a,1,a.length);
  }
  
  public static <T> T[] tail(T[] a) {
    // return an array with all elements of a except the first
    if (a == null) throw new IllegalArgumentException(
        "tail: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tail: array must have at least one element");
    return copyOfRange(a,1,a.length);
  }
  
  public static Iterator<int[]> tails(int[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null) throw new IllegalArgumentException(
        "tails: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tails: array must have at least one element");
    int[] b = prepend(a,1);
    int[][] c = new int[1][];
    c[0] = b;
    return Stream.generate(()->{c[0]=tail(c[0]);return c[0];})
        .limit(a.length+1).iterator();
  }
  
  public static Iterator<long[]> tails(long[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null) throw new IllegalArgumentException(
        "tails: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tails: array must have at least one element");
    long[] b = prepend(a,1);
    long[][] c = new long[1][];
    c[0] = b;
    return Stream.generate(()->{c[0]=tail(c[0]);return c[0];})
        .limit(a.length+1).iterator();
  }
  
  public static Iterator<double[]> tails(double[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null) throw new IllegalArgumentException(
        "tails: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tails: array must have at least one element");
    double[] b = prepend(a,1);
    double[][] c = new double[1][];
    c[0] = b;
    return Stream.generate(()->{c[0]=tail(c[0]);return c[0];})
        .limit(a.length+1).iterator();
  }
  
  public static <T> Iterator<T[]> tails(T[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null) throw new IllegalArgumentException(
        "tails: array can't be null");
    if (a.length == 0) throw new IllegalArgumentException(
        "tails: array must have at least one element");
    T[] b = prepend(a,a[0]);
    T[][] c = makeGen2D(1,b.length);
    c[0] = b;
    return Stream.generate(()->{c[0]=tail(c[0]);return c[0];})
        .limit(a.length+1).iterator();
  }
  
   public static int[] take(int[] a, int n) {
     // return an array with the first n elements
     if (a == null) throw new IllegalArgumentException(
         "take: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "take: array must have at least one element");
     if (n>=a.length) return (int[]) clone(a);
     if (n <= 0) return new int[0];
     return copyOf(a, n); 
   }
   
   public static long[] take(long[] a, int n) {
     // return an array with the first n elements
     if (a == null) throw new IllegalArgumentException(
         "take: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "take: array must have at least one element");
     if (n>=a.length) return (long[]) clone(a);
     if (n <= 0) return new long[0];
     return copyOf(a, n); 
   }
   
   public static double[] take(double[] a, int n) {
     // return an array with the first n elements
     if (a == null) throw new IllegalArgumentException(
         "take: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "take: array must have at least one element");
     if (n>=a.length) return (double[]) clone(a);
     if (n <= 0) return new double[0];
     return copyOf(a, n); 
   }
   
   @SuppressWarnings("unchecked")
  public static <T> T[] take(T[] a, int n) {
     // return an array with the first n elements
     if (a == null) throw new IllegalArgumentException(
         "take: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "take: array must have at least one element");
     if (n>=a.length) return (T[]) clone(a);
     if (n <= 0) return makeFromValue(a[0],0);
     return copyOf(a, n); 
   }
   
   public static int[] takeRight(int[] a, int n) {
     // return an array with the last n elements
     if (a == null) throw new IllegalArgumentException(
         "takeRight: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "takeRight: array must have at least one element");
     if (n>=a.length) return (int[]) clone(a);
     if (n <= 0) return new int[0];
     return copyOfRange(a, a.length-n, a.length);
   }
   
   public static long[] takeRight(long[] a, int n) {
     // return an array with the last n elements
     if (a == null) throw new IllegalArgumentException(
         "takeRight: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "takeRight: array must have at least one element");
     if (n>=a.length) return (long[]) clone(a);
     if (n <= 0) return new long[0];
     return copyOfRange(a, a.length-n, a.length);
   }
   
   public static double[] takeRight(double[] a, int n) {
     // return an array with the last n elements
     if (a == null) throw new IllegalArgumentException(
         "takeRight: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "takeRight: array must have at least one element");
     if (n>=a.length) return (double[]) clone(a);
     if (n <= 0) return new double[0];
     return copyOfRange(a, a.length-n, a.length);
   }
   
   @SuppressWarnings("unchecked")
  public static <T> T[] takeRight(T[] a, int n) {
     // return an array with the last n elements
     if (a == null) throw new IllegalArgumentException(
         "takeRight: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "takeRight: array must have at least one element");
     if (n>=a.length) return (T[]) clone(a);
     if (n <= 0) return copyOf(a,0);
     return copyOfRange(a, a.length-n, a.length);
   }
   
   public static int[] takeWhile(int[] a, Predicate<Integer> p) {
     // return longest prefix with elements satisfying p
     if (a == null) throw new IllegalArgumentException(
         "takeWhile: array can't be null");
     if (a.length == 0) throw new IllegalArgumentException(
         "takeWhile: array must have at least one element");
     assert a != null; 
     int bindex = 0;
     for (int i = 0; i < a.length; i++)
       if (p.test(a[i])) {
         bindex++;
       } else break;
     return copyOfRange(a, 0, bindex);
   }
   
   public static Collection<Byte> toCollection(byte[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Byte> c = (Collection<Byte>) new ArrayList<Byte>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Short> toCollection(short[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Short> c = (Collection<Short>) new ArrayList<Short>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Integer> toCollection(int[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Integer> c = (Collection<Integer>) new ArrayList<Integer>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Long> toCollection(long[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Long> c = (Collection<Long>) new ArrayList<Long>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Float> toCollection(float[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Float> c = (Collection<Float>) new ArrayList<Float>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Double> toCollection(double[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Double> c = (Collection<Double>) new ArrayList<Double>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Character> toCollection(char[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Character> c = (Collection<Character>) new ArrayList<Character>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Collection<Boolean> toCollection(boolean[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     Collection<Boolean> c = (Collection<Boolean>) new ArrayList<Boolean>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   public static Iterable<Byte> toIterable(byte[] a) {
     // implemented using utils.PrimitiveIterator.OfByte
     // and ArrayUtils.ByteConsumer
     return new Iterable<Byte>() {
       public OfByte iterator() {
         return new OfByte() {
           int len = a.length;
           int c = 0;
           public boolean hasNext() {return c < len;}
           public byte nextByte() {return a[c++];}
         };
       }
     };
   }
   
   public static Iterable<Short> toIterable(short[] a) {
     // implemented using utils.PrimitiveIterator.OfShort
     // and ArrayUtils.ShortConsumer
     return new Iterable<Short>() {
       public OfShort iterator() {
         return new OfShort() {
           int len = a.length;
           int c = 0;
           public boolean hasNext() {return c < len;}
           public short nextShort() {return a[c++];}
         };
       }
     };
   }
   
   public static Iterable<Integer> toIterable(int[] a) {
     return new Iterable<Integer>() {
       public OfInt iterator() {
         return Arrays.stream(a).iterator();
         //return new OfInt() {
         //  int len = a.length;
         //  int c = 0;
         //  public boolean hasNext() {return c < len;}
         //  public int nextInt() {return a[c++];}
         //};
       }
     };
   }
   
   public static Iterable<Long> toIterable(long[] a) {
     return new Iterable<Long>() {
       public OfLong iterator() {
         return Arrays.stream(a).iterator();
         //return new OfLong() {
         //  int len = a.length;
         //  int c = 0;
         //  public boolean hasNext() {return c < len;}
         //  public int nextInt() {return a[c++];}
         //};
       }
     };
   }
   
   public static Iterable<Double> toIterable(double[] a) {
     return new Iterable<Double>() {
       public OfDouble iterator() {
         return Arrays.stream(a).iterator();
         //return new OfDouble() {
         //  int len = a.length;
         //  int c = 0;
         //  public boolean hasNext() {return c < len;}
         //  public int nextInt() {return a[c++];}
         //};
       }
     };
   }
   
   public static Iterable<Float> toIterable(float[] a) {
     // implemented using utils.PrimitiveIterator.OfFloat
     // and ArrayUtils.FloatConsumer
     return new Iterable<Float>() {
       public OfFloat iterator() {
         return new OfFloat() {
           int len = a.length;
           int c = 0;
           public boolean hasNext() {return c < len;}
           public float nextFloat() {return a[c++];}
         };
       }
     };
   }
   
   public static Iterable<Character> toIterable(char[] a) {
     // implemented using utils.PrimitiveIterator.OfChar
     // and ArrayUtils.CharConsumer
     return new Iterable<Character>() {
       public OfChar iterator() {
         return new OfChar() {
           int len = a.length;
           int c = 0;
           public boolean hasNext() {return c < len;}
           public char nextChar() {return a[c++];}
         };
       }
     };
   }
   
   public static Iterable<Boolean> toIterable(boolean[] a) {
     // implemented using utils.PrimitiveIterator.OfBoolean
     // and ArrayUtils.BooleanConsumer
     return new Iterable<Boolean>() {
       public OfBoolean iterator() {
         return new OfBoolean() {
           int len = a.length;
           int c = 0;
           public boolean hasNext() {return c < len;}
           public boolean nextBoolean() {return a[c++];}
         };
       }
     };
   }
   
   public static <T> Iterable<T> toIterable(T[] a) {
     return new Iterable<T>() {
       public Iterator<T> iterator() {
         return Arrays.stream(a).iterator();
         //return new Iterator<T>() {
         //  int len = a.length;
         //  int c = 0;
         //  public boolean hasNext() {return c < len;}
         //  public T next() {return a[c++];}
         //};
       }
     };
   }
   
   public static OfByte toIterator(byte[] a) {
     return iterator(a);
   }
   
   public static OfShort toIterator(short[] a) {
     return iterator(a);
   }
   
   public static OfInt toIterator(int[] a) {
     return iterator(a);
   }
   
   public static OfLong toIterator(long[] a) {
     return iterator(a);
   }
   
   public static OfDouble toIterator(double[] a) {
     return iterator(a);
   }
   
   public static OfFloat toIterator(float[] a) {
     return iterator(a);
   }
   
   public static OfChar toIterator(char[] a) {
     return iterator(a);
   }
   
   public static OfBoolean toIterator(boolean[] a) {
     return iterator(a);
   }

   public static <T> Iterator<T> toIterator(T[] a) {
     return iterator(a);
   }
   
   public static <T> List<T> toList(T[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toCollection: array can't be null");
     List<T> c = (List<T>) new ArrayList<T>();
     for (int i = 0; i < a.length; i++) c.add(a[i]);
     return c;
   }
   
   @SuppressWarnings("unchecked")
  public static List<?> toNestedList(Object a) {
     // returns a possibly nested ArrayList from any array with the
     // nesting depth equal to the array's dimensionality.
     // not using Object[] as arg type to allow primitive 1D arrays.
     // cast returned List<?> as needed, e.g. for input 3D int array
     // it could be cast to ArrayList<ArrayList<ArrayList<Integer>>>,
     // List<List<List<Integer>>> or Collection<Collection<Collection<Integer>>>
     if (a == null) throw new IllegalArgumentException(
         "toNestedList: argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "toNestedList: argument must be an array");
     
     int dim = dim(a); 

     if (dim == 1 && isPrimitiveArray(a)) {
       String rct = rootComponentType(a);
       switch (rct) {
         case "byte":
           {byte[] c = (byte[]) a;
           int len = c.length;
           ArrayList<Byte> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}
         case "short":
           {short[] c = (short[]) a;
           int len = c.length;
           ArrayList<Short> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}
         case "int":
           {int[] c = (int[]) a;
           int len = c.length;
           ArrayList<Integer> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}
         case "long":
           {long[] c = (long[]) a;
           int len = c.length;
           ArrayList<Long> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}  
         case "float":
           {float[] c = (float[]) a;
           int len = c.length;
           ArrayList<Float> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}  
         case "double":
           {double[] c = (double[]) a;
           int len = c.length;
           ArrayList<Double> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}  
         case "char":
           {char[] c = (char[]) a;
           int len = c.length;
           ArrayList<Character> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}  
         case "boolean":
           {boolean[] c = (boolean[]) a;
           int len = c.length;
           ArrayList<Boolean> d = new ArrayList<>();
           for (int i = 0; i < len; i++) d.add(c[i]);
           return d;}
         default: throw new NoSuchElementException("toNestedList: invalid primitive "
             + "type "+ rct);
       }
     } else {
       Object[] b = (Object[]) a;
       int len = b.length;
       @SuppressWarnings("rawtypes")
       ArrayList d = new ArrayList();
       if (dim == 1) {
         for (int i = 0; i < len; i++) d.add(b[i]);
       } else {
         for (int i = 0; i < len; i++) d.add(toNestedList(b[i]));
       }
       return d;
     }
   }
    
   public static <K,V> Map<K,V> toMap(K[] k, V[] v) {
     if (k == null || v == null) throw new IllegalArgumentException(
         "toMap: the array arguments can't be null");
     if (!(k.getClass().isArray() && v.getClass().isArray())) 
       throw new IllegalArgumentException("toMap: both arguments must be arrays");
     Map<K,V> map = new HashMap<>();
     if (k.length == 0 || v.length == 0) return map;
     int min = k.length <= v.length ? k.length : v.length;
     for (int i = 0; i < min; i++) map.put(k[i],v[i]);
     return map;
   }
   
   public static class Tuple2<K,V> {
     K _1; V _2; 
     public Tuple2(K k, V v) {
       this._1 = k; this._2 = v;
     }
     public Tuple2<V,K> swap(Tuple2<K,V> t) {
       return new Tuple2<V,K>(t._2,t._1);
     }
     @Override public String toString() {
       return "("+_1+","+_2+")";
     }
   }

   public static <K,V> Map<K,V> toMap(Tuple2<K,V>[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toMap: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "toMap: the argument must be an array");
     Map<K,V> map = new HashMap<>();
     for (int i = 0; i < a.length; i++) map.put(a[i]._1, a[i]._2);
     return map;
   }
   
   public static <T> Set<T> toSet(T[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toSet: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "toSet: the argument must be an array");
     Set<T> set = new HashSet<>();
     for (int i = 0; i < a.length; i++) set.add(a[i]);
     return set;
   }
   
   public static <T> Stream<T> toStream(T[] a) {
     if (a == null) throw new IllegalArgumentException(
         "toStream: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "toStream: the argument must be an array");
     return Arrays.stream(a).limit(a.length);
   }
   
   public static <T> Vector<T> toVector(T[] a) {
     return new Vector<T>(toList(a));
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static Vector<?> toNestedVector(Object a) {
     return new Vector(toNestedList(a));
   }
   
   public static int[][] transpose(int[][] a) {
     // interchange rows and columns or reflect over main diagonal
     if (a == null) throw new IllegalArgumentException(
         "transpose: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "transpose: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "transpose: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=2) throw new IllegalArgumentException(
         "transpose: the argument must have dimension 2");

     int n = a.length; int o = a[0].length;
     int[][] t = new int[o][n];
     for (int i = 0; i < n; i++)
       for (int j = 0; j < o; j++)
         t[j][i] = a[i][j];

     return t;
   }
   
   public static long[][] transpose(long[][] a) {
     // interchange rows and columns or reflect over main diagonal
     if (a == null) throw new IllegalArgumentException(
         "transpose: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "transpose: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "transpose: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=2) throw new IllegalArgumentException(
         "transpose: the argument must have dimension 2");

     int n = a.length; int o = a[0].length;
     long[][] t = new long[o][n];
     for (int i = 0; i < n; i++)
       for (int j = 0; j < o; j++)
         t[j][i] = a[i][j];

     return t;
   }

   public static double[][] transpose(double[][] a) {
     // interchange rows and columns or reflect over main diagonal
     if (a == null) throw new IllegalArgumentException(
         "transpose: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "transpose: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "transpose: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=2) throw new IllegalArgumentException(
         "transpose: the argument must have dimension 2");

     int n = a.length; int o = a[0].length;
     double[][] t = new double[o][n];
     for (int i = 0; i < n; i++)
       for (int j = 0; j < o; j++)
         t[j][i] = a[i][j];

     return t;
   }
    
   public static <T> T[][] transpose(T[][] a) {
     // interchange rows and columns or reflect over main diagonal
     if (a == null) throw new IllegalArgumentException(
         "transpose: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "transpose: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "transpose: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=2) throw new IllegalArgumentException(
         "transpose: the argument must have dimension 2");

     int n = a.length; int o = a[0].length;
     @SuppressWarnings("unchecked")
     T[][] t = (T[][]) clone(a);
     for (int i = 0; i < n; i++)
       for (int j = 0; j < o; j++)
         t[j][i] = a[i][j];

     return t;
   }
   
   
   public static byte[] union(byte[] a, byte...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static short[] union(short[] a, short...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static int[] union(int[] a, int...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static long[] union(long[] a, long...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static float[] union(float[] a, float...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static double[] union(double[] a, double...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static char[] union(char[] a, char...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static boolean[] union(boolean[] a, boolean...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   @SafeVarargs
   public static <T> T[] union(T[] a, T...b) {
     // return a new array with the elements of a followed by those of b
     return add(a,b);
   }
   
   public static void update(byte[] a, byte u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(short[] a, short u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(int[] a, int u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(long[] a, long u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(float[] a, float u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(double[] a, double u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(char[] a, char u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static void update(boolean[] a, boolean u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static <T> void update(T[] a, T u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     a[p] = u;  
   }
   
   public static byte[] updated(byte[] a, byte u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     byte[] b = new byte[a.length];
     b[p] = u;  
     return b;
   }
   
   public static short[] updated(short[] a, short u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     short[] b = new short[a.length];
     b[p] = u;  
     return b;
   }
   
   public static int[] updated(int[] a, int u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "updated: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     int[] b = new int[a.length];
     b[p] = u;  
     return b;
   }
   
   public static long[] updated(long[] a, long u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     long[] b = new long[a.length];
     b[p] = u;  
     return b;
   }
   
   public static float[] updated(float[] a, float u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "updated: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     float[] b = new float[a.length];
     b[p] = u;  
     return b;
   }
   
   public static double[] updated(double[] a, double u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "updated: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     double[] b = new double[a.length];
     b[p] = u;  
     return b;
   }
   
   public static char[] updated(char[] a, char u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "updated: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     char[] b = new char[a.length];
     b[p] = u;  
     return b;
   }
   
   public static boolean[] updated(boolean[] a, boolean u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "updated: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "updated: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "updated: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "updated: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     boolean[] b = new boolean[a.length];
     b[p] = u;  
     return b;
   }
   
   public static <T> T[] updated(T[] a, T u, int p) {
     // change the value of a[p] to u in place
     if (a == null) throw new IllegalArgumentException(
         "update: the array argument can't be null");
     if (!a.getClass().isArray()) throw new IllegalArgumentException(
         "update: the argument must be an array");
     if (a.length==0) throw new IllegalArgumentException(
         "update: the argument must have a length > 0");
     int dim = dim(a);
     if (dim!=1) throw new IllegalArgumentException(
         "update: the argument must have dimension 1");
     if (p > a.length-1) throw new ArrayIndexOutOfBoundsException(
         "index p is greater than a.length-1");
     @SuppressWarnings("unchecked")
    T[] b = (T[]) clone(a);
     b[p] = u;  
     return b;
   }
   
//   public static <A,B> Tuple2<A,B>[] zip(A[] a, B[] b) {
//     
//     
//   }
   
   
   
   
   
   
   
   
   
   
 
   
   
   
  public static void main(String[] args) {
    
    Object[][] oa1 = new Object[3][2];
    oa1[0] = new Object[]{new Integer(1), "one"};
    oa1[1] = new Object[]{new Integer(2), "two"};
    oa1[2] = new Object[]{new Integer(3), "three"};
    printIt(oa1);
    
    System.exit(0);
    
//    Double d1 = new Double(999.99);
//    byte b1 = d1.byteValue();
//    System.out.println(b1); //-25
//    System.out.println((byte)999); //25
    
    
    int[] ia33 = {1,2,3,4,5};
    System.out.println("toNestedList(ia33)="+toNestedList(ia33));
    //[1, 2, 3, 4, 5]

    Integer[] ia19 = {1,2};
    Integer[] ia20 = {3,4};
    Integer[] ia21 = {5,6};
    Integer[] ia22 = {7,8}; 
    Integer[][] ia23 = {ia19,ia20};
    Integer[][] ia24 = {ia21,ia22};
    Integer[][][] ia25 = {ia23,ia24};
    @SuppressWarnings("unchecked")
    ArrayList<ArrayList<ArrayList<Integer>>> x1list = 
        (ArrayList<ArrayList<ArrayList<Integer>>>) toNestedList(ia25);
    System.out.println("toList(ia25)="+x1list); 
    //[[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
    int[] ia26 = {1,2};
    int[] ia27 = {3,4};
    int[] ia28 = {5,6};
    int[] ia29 = {7,8}; 
    int[][] ia30 = {ia26,ia27};
    int[][] ia31 = {ia28,ia29};
    int[][][] ia32 = {ia30,ia31};
    @SuppressWarnings("unchecked")
    Collection<Collection<Collection<Integer>>> x2list = 
        (Collection<Collection<Collection<Integer>>>) toNestedList(ia32);
    System.out.println("toList(ia32)="+x2list);
    //[[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
//    printIt(ia25); //[[[1,2],[3,4]],[[5,6],[7,8]]]
//    Object[] oa25 = ia25;
//    printIt(oa25);
//    printIt(oa25[0]); //[[1,2],[3,4]]
//    printIt(ia25[0]); //[[1,2],[3,4]]
//    
//    ArrayList<Integer> ali = new ArrayList<>(Arrays.asList(1,2,3));
//    HashSet<Integer> hi = new HashSet<>(Arrays.asList(1,2,3));
//    Object[] oa1 = {"hello", "world", ali, "one", hi};
//    System.out.println("uniformComponentType(oa1)="+uniformComponentType(oa1)); //false
//         
//    int[] a = {1,2,3};
//    Class<?> c = a.getClass().getComponentType(); //int
//    int[] az = (int[]) Array.newInstance(c, 0);
//    printArray(az);
//    
//    System.out.println("uniformComponentType(a)="+uniformComponentType(a)); //int
//    Class<Integer> x = int.class;
//    System.out.println(x.getName()); //int
//    System.out.println(x == c); //true
//    Class<Integer> y = Integer.class;
//    System.out.println(int.class == Integer.class); //false
//    
//    System.out.println(int.class.equals(Integer.class)); //false
//    System.out.println(c.getName()); //int
//    c = null;
//    try {
//      c = Class.forName("int");
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//    System.out.println(c.getName());
//    Integer ni1 = new Integer(""+1);
//    Integer ni2 = new Integer(ni1.intValue());
//    System.out.println(identityHashCode(ni1));
//    System.out.println(identityHashCode(ni2));
//    System.out.println();
//    
//      Integer[] ia17 = {1,2,3};
//      Integer[] ia18 = copyOf(ia17, ia17.length);
//      printArray(ia17);
//      printArray(ia18);
//      for (int i=0; i<ia17.length; i++) {
//        System.out.println(System.identityHashCode(ia17[i]));
//        System.out.println(System.identityHashCode(ia18[i]));
//        System.out.println();
//      }
//      System.out.println();
//    
//    ArrayList<String> list = new ArrayList<>(Arrays.asList("one","two","three"));
//    Object listClone = null;
//    Method clone = null;
//    try {
//      clone = list.getClass().getDeclaredMethod("clone");
//    } catch (NoSuchMethodException | SecurityException e) {
//      e.printStackTrace();
//    }
//    try {
//      listClone = clone.invoke(list);
//    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//      e.printStackTrace();
//    }
//    System.out.println("listClone="+listClone);
//      
//    int[][][] a16 = new int[1][3][5];
//    System.out.println(a16.getClass().getComponentType().getName());//[[I
//    System.out.println(a16.getClass().getComponentType().getSimpleName());//int[][]
//   
//    Class<?> cl = Class.forName("[Ljava.lang.Integer;");
//    System.out.println(cl.getName());
//    
//    Object[][][] s15 = new Object[2][][];
//    System.out.println(ultimateComponentType(s15));
//    int[][][] a15 = new int[2][][];
//    System.out.println(ultimateComponentType(a15));
//    int[][][] a13 = new int[0][1][2];
//    System.out.println(dim(a13)); //3
//    int[][][] a14 = new int[0][5][6];
//    System.out.println(Arrays.equals(a13,a14)); //true
//    a13[0] = new int[1][2];//ArrayIndexOutOfBoundsException
//    a13[0][0] = new int[]{1,2,3};
//    a13[0][1] = new int[]{4,5,6};
//        
//    System.out.println(Arrays.deepToString(a13));//[]
//    Integer[] ia12 = {1,2,3,4,5};
//    printArray(dims(ia12));
//    Iterator<Integer[]> it6 = tails(ia12);
//    while (it6.hasNext()) printArray(it6.next());
// 
//    int[] a12 = {1,2,3,4,5};
////    Object[] o12 = a12; //type mismatch
//    printArray(takeWhile(a12,x->x<4));
//    printArray(takeRight(a12,4));
//    Iterator<int[]> it5 = tails(a12);
//    while (it5.hasNext()) printArray(it5.next());
//    printArray(tail(tail(a12)));
    
//    char[] ca1 = {'a','b','c'};
//    System.out.println(subSequence(ca1,-1,9));
//    System.out.println("hello".subSequence(0,5));
    
//    Pattern pattern = Pattern.compile("(?<item>[\\[]+)");
//    Matcher matcher = pattern.matcher("[[[D");
//    while (matcher.find()) {
//      matcher.group("item");
//      System.out.print("match="+match);
//    }
//  *********************************************************************************
//    double[][][][][] d55 = new double[1][2][3][4][5];
//    System.out.println(rootComponentType(d55));
//    printArray(dims(d55));
//
//    System.out.println(stringPrefix(d55));
//    System.out.println(dim(d55));
//    System.out.println(d55.getClass());
//    try {
//      System.out.println(getArrayType(double.class,5));
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//    int[] a11 = {1,2,3,4,5,6,7,8,9};
//    System.out.println(StringPrefix(a11));
//    System.out.println(startsWith(a11,0,1,2,3));
//    Integer[] a10i = {1,2,3,4,5,6,7,8,9};
//    printIt(span(a11, x->x>5));
//    printIt(span(a10i, x->x>5));

//    int[][][][][] d5 = new int[1][2][3][4][5];
//    System.out.println(dim(d5));
//    
//    Integer[] c12 = {1,2,3,4,5};
////    int[] c11 = {1,2,3,4,5};
//    String[] s11 = {"B","A","E","D","C"};
//    sort(s11, (s,t)->s.compareTo(t));
//    printArray(s11);
//    printArray(unique(c11));
//    //[B,A,A,D,C]
//    printArray(sortBy(c12,x->s11[(s11.length+2)%x], (s,t)->t.compareTo(s)));
//       
//    int[] a10 = new int[0];
//    System.out.println(isPrimitiveArray(a10));
    int[] a9 = {1,2,3,4,5,6,7,8,9};
    System.out.print("flatline(a9)="); printIt(flatLine(a9));
    printIt(a9);
    System.out.print("clone(a9)="); printIt(clone(a9));
    System.out.println("numel(a9)="+numel(a9));
//    int[] a9c = (int[]) clonea(a9);
//    printIt(a9c);
//    System.out.println(dim(a9));
    Integer[] a9i = (Integer[]) box(a9); 
    System.out.println("numel(a9i)="+numel(a9i));
    Object[] a9io = (Object[]) a9i;
//    printArray(a9i);
    System.out.print("clone(a9i)="); printIt(clone(a9i));
//    System.out.println("a9i.getClass().getName()="+a9i.getClass().getName());//[Ljava.lang.Integer
//    Integer[] a92i = {1,2,3,4,5,6,7,8,9};
    System.out.print("flatline(a9io)="); printIt((Integer[])flatLine(a9io));
//    Integer[] a9ic = (Integer[]) cloneArray(a9i);
//    printArray(a9ic);
//    for (int i = 0; i < a9i.length; i++) {
//      System.out.println(identityHashCode(a9i[i]));
//      System.out.println(identityHashCode(a9ic[i]));
//    }
//    int[] a9ii = (int[]) unbox(a9i);
//    printArray(a9ii);
//    System.out.println();
//    int[] a9b = (int[]) toBoxedOrPrimitive(a9i);
//    printArray(a9b);
//    System.out.println(isPrimitiveArray(a9));
//    printArray(slice(a9,a9.length-1,a9.length));
//    slidingStream(a9,2,3).forEach(x -> printArray(x));
//    System.out.println(slidingCount(a9.length,2,3));
//    Iterator<int[]> sl1 = sliding(a9,3,2);
//    List<int[]> l9 = new ArrayList<>();
//    while(sl1.hasNext()) l9.add(sl1.next()); // printArray(sl1.next());
//    System.out.println(l9);
//    for (int[] ia : l9) printArray(ia);
    
//    int[] a8 = {1,1,1,5,5,5,6,6,2,3,7,7,7,7,8,8,1,9,3,2,5,6,7};
//    System.out.println(dim(a8));
//    System.out.println(segmentLength(a8,x->x>=5,0));
//    System.out.println(segmentLength(a8,x->x<=5,0));
//    
//    String[] sa1 = {"A","B","C"};
//    printArray(scan(sa1, (a,b) -> a+b, "z"));
//    printArray(scanRight(sa1, (a,b) -> a+b, "z"));
//    
//    int[] a7 = {1,2,3,4,5,6};
//    printArray(map(a7, x -> x*x));
//    printArray(reverseMap(a7, x -> x*x));
//    reverseInPlace(a7);
//    printArray(a7);
//    printArray(reverse(a7));
//
//    
//    Integer[] a6 = new Integer[]{1,2,3,4,5};
//    printIt(a6);
//    System.out.println(dim(a6));
//    reverseInPlace(a6);
//    printArray(a6);
//    printArray((Integer[])reverse(a6));
//    Integer[] b = makeGen1D(a6.length, a);
//    Integer[] c = copyOf(a6, a6.length);
//    printArray(c);
//    System.out.println(factorial(9));
//    System.out.println(factorial(new BigInteger(""+9)));
    
//    System.out.println(numberOfCombinations(2000,1000));
//    System.out.println(numberOfCombinationsLong(20,10));
//    
//      int[] i18 = new int[1000];
//      for (int i = 0; i < 1000; i++)
//        i18[i] = 999 - i + 1;
//      combinationStream(i18, 5).limit(10).forEach(y-> printArray(y));
//      
//    long[] i17 = {1,2,3,4,5};
//    Iterable<long[]> ci3 = new LongCombinator(i17,3);
//    ci3.forEach(x -> printArray(x));
//    Iterator<int[]> ci2it = ci2.iterator();
//    while(ci2it.hasNext()) printArray(ci2it.next());
    
//    double[] d3 = {1.1,2.2,3.3,4.4,5.5};
//    Iterable<double[]> cd3 = new DoubleCombinator(d3,3);
//    cd3.forEach(x -> printArray(x));
//    Iterator<double[]> cd3it = cd3.iterator();
//    while(cd3it.hasNext()) printArray(cd3it.next());
//    System.out.println();
    
//    int[] i16 = {1,2,3,4,5};
//    OfInt x = Arrays.stream(i16).iterator();
//    Iterator<int[]> ci4it = combinations(i16,3);
//    while(ci4it.hasNext()) printArray(ci4it.next());
//    Iterable<int[]> ci2 = new IntCombinator(i16,3);
//    ci2.forEach(x -> printArray(x));
//    Iterator<int[]> ci2it = ci2.iterator();
//    while(ci2it.hasNext()) printArray(ci2it.next());
    
//    List<Integer> al = new ArrayList<>(Arrays.asList(1,2,3,4,5));
////    Integer[] i15 = list2Array(al);
////    printArray(i15);
////    Integer[] ala = new Integer[]{1,2,3,4,5};
//    Iterable<Integer[]> ci = new Combinator<Integer>(al,3);
////    Iterator<Integer[]> cii = ci.iterator();
////    Iterable<Integer[]> p10 = (Iterable<Integer[]>) new Permutator<Integer>(ala); //   List<Integer[]> list1 = new ArrayList<>();
//    ci.forEach(x -> printArray(x));
////    for (Integer[] ia : ci) printArray((Integer[]) ia);
    
//    while(ci.hasNext()) printArray(ci.next());
//    System.out.println(numberOfCombinations(5,3));
    
//    int[] a5 = {11,2,11,3,3,9,9,3,1,1,2,1};
//    printArray(uniqueSort(a5));
//    int[][] a7 = combinations(3);
//    printArray(a7);
//    int[] a6 = prepend(a5, 4,5,6);
//    printArray(a6);
    
//    int[] a4 = {1,2,3,4,5};
//    printArray(dropWhile(a4, x -> x<=1));
//    printArray(dropRight(a4,2)); //[1,2,3]
//    
    int[][] a2b = new int[3][3];
    a2b[0] = new int[]{-2,2,-3};
    a2b[1] = new int[]{-1,1,3};
    a2b[2] = new int[]{2,0,-1};
//////    System.out.println((dim(a2)));
    printIt(a2b);
    System.out.print("clone(a2b)="); printIt(clone(a2b));
    System.out.println("numel(a2b)="+numel(a2b));
    System.out.print("flatten2(a2b)="); printIt(flatten(a2b));
    
//    int[][] a2c = (int[][]) clonea(a2b);
//    printIt(a2c);
//    System.out.println();
//    printArray(a2c);
    System.out.print("flatline(a2b)="); printIt(flatLine(a2b));
//
    Integer[][] a2i = (Integer[][]) box(a2b);
    System.out.print("flatline(a2i)="); printIt(flatLine(a2i));
    System.out.println("numel(a2i)="+numel(a2i));
//
////    System.out.println("dim(a2i)="+dim(a2i));
//    printIt(a2i);
    System.out.print("clone(a2i)="); printIt(clone(a2i));
//    int[][] a2ii = (int[][]) unbox(a2i);
//    printIt(a2ii);
//    Integer[][] a2ic = (Integer[][]) cloneArray(a2i);
//    printIt(a2ic);
//    for (int i = 0; i < a2i.length; i++) {
//      for (int j = 0; j < a2i[i].length; j++) {
//        System.out.println(a2i[i][j]);
//        System.out.println(a2ic[i][j]);
//        System.out.println(identityHashCode(a2i[i][j]));
//        System.out.println(identityHashCode(a2ic[i][j]));
//      }
//    }
//    System.out.println();
//    
//    long[][] al = new long[3][3];
//  al[0] = new long[]{-2,2,-3};
//  al[1] = new long[]{-1,1,3};
//  al[2] = new long[]{2,0,-1};
////  System.out.println((dim(a2)));
////  printIt(a2);
//    double[][] a3 = new double[3][3];
//    a3[0] = new double[]{-2.,2.,-3.};
//    a3[1] = new double[]{-1.,1.,3.};
//    a3[2] = new double[]{2.,0.,-1.};
////   
//    System.out.println(determinant(a2)); //18
//    System.out.println(determinant(al));
//    System.out.println(determinant(a3)); //18
//    
//    double[][] inverse = invert(a3);
//    printArray(inverse);
    //       0                       1                       2                      
    //  0   -0.05555555555555555000  0.11111111111111110000  0.50000000000000000000
    //  1    0.27777777777777780000  0.44444444444444440000  0.50000000000000000000
    //  2   -0.11111111111111110000  0.22222222222222220000  0.00000000000000000000
//    printArray(matrixProduct(a3,inverse));
    //       0                       1                       2                      
    //  0    1.05555555555555560000 -0.11111111111111116000  0.50000000000000000000
    //  1   -0.33333333333333330000  1.16666666666666670000  0.00000000000000000000
    //  2    0.00000000000000000000  0.00000000000000000000  1.00000000000000000000
    
//    int[][] A = new int[2][2];
//    A[0] = new int[]{1,3};
//    A[1] = new int[]{5,7};
//    int det = A[0][0]*A[1][1] - A[1][0]*A[0][1];
//    System.out.println("det="+det); //-8
//    
//    int[] t1 = {3,4,2};
//    int[][] t2 = new int[3][4];
//    t2[0] = new int[]{13,9,7,15};
//    t2[1] = new int[]{8,7,4,6};
//    t2[2] = new int[]{6,4,0,3};
//    Arrays.stream(t2).limit(t2.length).forEach(x -> printArray(x));
    //  [2,3,4,5]
    //  [13,9,7,15]
    //  [8,7,4,6]
    //  [6,4,0,3]  
//    
//    int[] t3 = {3,4,2,1};
//    printArray(t2);
//    printArray(matrixProduct(t1,t2)); //[83,63,37,75]
//    printArray(matrixProduct(t2,t3)); //[104,66,37]
    
//    int[][] r9 = new int[2][3];
//    r9[0] = new int[]{1,2,3};
//    r9[1] = new int[]{4,5,6};
//    int[][] r10 = new int[3][2];
//    r10[0] = new int[]{7,8};
//    r10[1] = new int[]{9,10};
//    r10[2] = new int[]{11,12};
//    printArray(matrixProduct(r9,r10)); 
//    
    //  58  64
    //  139 154
//    printArray(r10);
//    
//    double[][] r8 = new double[3][];
//    r8[0] = new double[]{1,2,3};
//    r8[1] = new double[]{4,5,6};
//    r8[2] = new double[]{7,8,9};
////    printArray(r8);
//    System.out.println(deepToString(r8).replaceAll(" ", "").replaceAll("],", "]\n "));
//
//    double[][] r81 = flatMap(r8, x -> x*2);
//    print2DDoubleArray(r81);
//    int[] r7 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
//    printIt(r7);
//    System.out.println(sum(r7));
    
    int[][][] r62 = new int[2][3][4];
    r62[0] = new int[3][4];
    r62[0][0] = new int[]{1,2,3,4};
    r62[0][1] = new int[0];
    r62[0][2] = new int[]{9,10,11,12,13,14};
    r62[1] = new int[3][4];
    r62[1][0] = new int[]{15};
    r62[1][1] = new int[]{17,18,19,20,21};
    r62[1][2] = new int[]{21,22,23,24};
    printIt(r62);
    System.out.println("numel(r62)="+numel(r62));
    System.out.print("clone(r62)="); printIt(clone(r62));
    System.out.print("flatline(r62)="); printIt(flatLine(r62));
    System.out.print("flatten2(r62)="); printIt(flatten(r62));

    Integer[][][] r62i = (Integer[][][]) box(r62);
    System.out.println("numel(r62i)="+numel(r62i));
    System.out.print("clone(r62i)="); printIt(clone(r62i));
//    printIt(r62i);
    System.out.print("flatline(r62i)="); printIt(flatLine(r62i));
    System.out.print("flatten2(r62i)="); printIt(flatten(r62i));

//    int[][][] r62ii = ( int[][][]) unbox(r62i);
//    printIt(r62ii);
//    Integer[][][] r62ic = (Integer[][][]) cloneArray(r62i);
//    printIt(r62ic);
//    System.out.println("uniformComponentType(r62ic)="+uniformComponentType(r62ic)); 
//    System.out.println("rootComponentType(r62ic)="+rootComponentType(r62ic)); 
//    for (int i = 0; i < r62i.length; i++) {
//      for (int j = 0; j < r62i[i].length; j++) {
//        for (int k = 0; k < r62i[i][j].length; k++) {
////          System.out.println(r62i[i][j][k]);
////          System.out.println(r62ic[i][j][k]);
//          System.out.println(identityHashCode(r62i[i][j][k]));
//          System.out.println(identityHashCode(r62ic[i][j][k]));
//        }
//      }
//    }
//    
//    int[][][] r6 = new int[2][3][4];
//    r6[0] = new int[3][4];
//    r6[0][0] = new int[]{1,2,3,4};
//    r6[0][1] = new int[]{5,6,7,8};
//    r6[0][2] = new int[]{9,10,11,12};
//    r6[1] = new int[3][4];
//    r6[1][0] = new int[]{13,14,15,16};
//    r6[1][1] = new int[]{17,18,19,20};
//    r6[1][2] = new int[]{21,22,23,24};
//    printIt(r6);
//    Integer[][][] r6i = (Integer[][][]) box(r6);
//    printIt(r6i);
//    int[][][] r6ii = ( int[][][]) unbox(r6i);
//    printIt(r6ii);
//    System.out.println(deepToString(r6).replaceAll(" ", "").replaceAll("]],", "]\n "));
//
//    int[][][] r61 = clone(r6);
//    System.out.println(); printIt(r61);
//    System.out.println("equals(r6,r61)="+equals(r6,r61));
//    System.out.println("numel(r6)="+numel(r6));
//    System.out.println("numel(r61)="+numel(r61));
//    System.out.println("sum(r6)="+sum(r6));
//    System.out.println("sum(r61)="+sum(r61));
//    System.out.println("avg(r6)="+avg(r6));
//    System.out.println("avg(r61)="+avg(r61));
//    
//    System.out.println("numel(r7)="+numel(r7));
//    System.out.println("sum(r7)="+sum(r7));
//    System.out.println("avg(r7)="+avg(r7));
//    
//    int[] r5 = {1,2,3,4,5,6,7,8}; //36
//    System.out.println(sum(r5));
//    
//    int[][] r4 = new int[3][];
//    r4[0] = new int[]{1,2};
//    r4[1] = new int[]{3,4,5};
//    r4[2] = new int[]{6,7,8};
//    System.out.println(sum(r4)); //36
//    int[][] r41 = clone(r4);
//    System.out.println(equals(r4,r41));
//    System.out.println(sum(r41));
// 
//    System.out.println(r4[0].length); //2
    
//    int x = Integer.MAX_VALUE * 2;
//    System.out.println(x);
        
//    double[] p3 = {1.5,2.5,3.5};
//    Iterator<double[]> itd = permutations(p3);
//    while (itd.hasNext()) printArray(itd.next());
    
//    int[] p1 = {1,2,3};
//    int[] p11 = {5,7};
//    int[] p111 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};
//    permutationStream(p1).forEach(x -> printArray(x));
//    
//    Iterator<int[]> itint = permutations(p1);
//    while (itint.hasNext()) printArray(itint.next());
//    
//    Integer[] p2 = {1,2,3};
//    List<Integer> list = Arrays.asList(1,2,3);
//    Iterator<Integer[]> it = new Permutator<Integer>(list);
//    Iterator<Integer[]> it = new Permutator<Integer>(p2);
//    Iterator<Integer[]> it = permutations(p2);
//    while (it.hasNext()) {
////      list.add(it.next());
//      printArray(it.next());
//      int[] n = it.next();
//      int[] x = new int[p2.length];
//      for (int i = 0; i < p2.length; i++) x[i] = p2[n[i]];
//      printArray(x);
//    }
//    System.out.println(list.size()); //120

   
//    ArrayList<ArrayList<Integer>> lli = new ArrayList<>();
//    ArrayList<ArrayList<Integer>> p3 = permute(p2);
//    for (List<Integer> li : p3) System.out.println(li);
    //  [3, 2, 1]
    //  [2, 3, 1]
    //  [2, 1, 3]
    //  [3, 1, 2]
    //  [1, 3, 2]
    //  [1, 2, 3]

//    int[] u1 = {0,1,2,3,3,4,19,18,5,6,7,14,13,3,4,19,12,5,5,5,6,7};
//    printArray(patch(u1,4,3,20,20,20));
           //[0,1,2,3,20,20,20,18,5,6,7,14,13,3,4,19,12,5,5,5,6,7]
//    patch(int[] a, int from, int replaced, int...g)
//    int[][] ur = partition(u1, x -> x%2==0);
//    printArray(ur[0]); //[0,2,4,18,6,14,4,12,6]
//    printArray(ur[1]); //[1,3,3,19,5,7,13,3,19,5,5,5,7]
//    System.out.println(maxBy(u1, (x) -> x*x));
    //    System.out.println("u1.length="+u1.length);
    //    System.out.println(lastIndexOfSlice(u1,3,4,19)); //13
    //    System.out.println(lastIndexOfSlice(u1,5,6,7)); //19
    //    System.out.println(lastIndexOfSlice(u1,0,1,2)); //0
    //    System.out.println(firstIndexOfSlice(u1,19,18,17)); //6
    //    System.out.println(firstIndexOfSlice(u1,0,1,3)); //-1
    //    System.out.println(firstIndexOfSlice(u1,0,1,2)); //0
    //    System.out.println(firstIndexOfSlice(u1,5,6,7)); //16
    //    System.out.println(firstIndexOfSlice(u1,5,6)); //16
    //    System.out.println(firstIndexOfSlice(u1,12,5,5)); //13
    //    System.out.println("u1.length="+u1.length);
    //    System.out.println("firstIndexOf(u1,3)="+firstIndexOf(u1,3)); //3
    //    System.out.println("firstIndexOf(u1,5)="+firstIndexOf(u1,5)); //14
    //    System.out.println("lastIndexOf(u1,3)="+lastIndexOf(u1,3)); //3
    //    System.out.println("lastIndexOf(u1,5)="+lastIndexOf(u1,5)); //14
    //    
    //    int[][] t = new int[2][3];
    //    t[0] = new int[]{1,2,3};
    //    t[1] = new int[]{4,11,6};
    //    int[][] u = clone(t);
    //    print2DIntArray(u);
    //    printArray(flatten(u));
    //
    //    int[] v = {9,1,11,14,5,17,7};
    //    int[] u = {9,8,3,7,6,9,3,7,5,4,3,9,2,3,1,0,7};
    //    printArray(intersect(v,u)); //[1,5,7,9]
    //    printArray(mintersect(v,u));//[1,5,7,7,7,9,9,9]
    //    int[] q = quickSortCopy(u);
    //    printArray(q);
    //    printArray(u);
    //    IntBinaryOperator op = (x,y) -> x+y;
    //    System.out.println(fold(r,op,0)); //45
    //    System.out.println(foldRight(r,op,0));
    //    forEach(r, (x)->System.out.print(x+", ")); System.out.println();
    //    System.out.println(endsWith(r,9));
    //    Predicate<Integer> p = x -> x < 4;
    //    int[] s = dropWhile(r,p);
    //    printArray(s); //[0, 1, 2, 3]
    //    
    //    int[] k = {1,5,5,5,3,9,1,2,8,7,1,1};
    //    int[] l = unique(k);
    //    printArray(l); //[1, 5, 3, 9, 2, 8, 7]
    //    
    //    int[] a = add(new int[0],1,2,3);
    //    printArray(a);
    //    int[] b = add(a,4);
    //    printArray(b);
    //    int[] c = add(a,5,6,7);
    //    printArray(c);
    //    int[] d = add(c, a);
    //    printArray(d); // [1, 2, 3, 5, 6, 7, 1, 2, 3]
    //    int[] e = drop(d,4);
    //    printArray(e);
    //    int[] f = drop(e,0);
    //    printArray(f);
    //    int[] g = drop(e,1);
    //    printArray(g);
    //    int[] h = drop(g,5);
    //    printArray(h);
    //    
    //    int[] a = {7,3,5591,3,7,17,5591,2,19,17,7,9};
    //    printArray(removeDups(a));
    //    
    //    printArray(sortUnique(a)); //[2, 3, 7, 17, 19, 5591]
    //    
    //    a = new int[]{7,3,5591,3,7,17,5591,2,19,17,7};
    //    BigInteger[] ba = transform(a, (x) -> new BigInteger(""+x), BigInteger.ZERO);
    //    printArray(ba); //[7, 3, 5591, 3, 7, 17, 5591, 2, 19, 17, 7]

  }




}
