package v;

import static java.lang.System.identityHashCode;
import static java.lang.reflect.Array.newInstance;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Comparator.comparing;
import static v.BitUtils.bits;
import static v.BitUtils.set;

import java.lang.Integer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfDouble;
import java.util.PrimitiveIterator.OfInt;
import java.util.PrimitiveIterator.OfLong;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import ds.Queue;
import v.BooleanBuffer;
import v.PrimitiveIterator.OfBoolean;
import v.PrimitiveIterator.OfByte;
import v.PrimitiveIterator.OfChar;
import v.PrimitiveIterator.OfFloat;
import v.PrimitiveIterator.OfShort;

public class ArrayUtils<E> {
  
  // not to be instantiated
  private ArrayUtils(){}

  // max array length due to VM limit
  public static final int maxArrayLength = Integer.MAX_VALUE - 8;

  // default max line length for ArrayToString() formatting
  // affects printArray() and pa() output
  private static int maxLineLength = 80;

  // many of these FunctionalInterfaces are used to avoid 
  // method signature contention

  @FunctionalInterface
  public static interface F1<T, R> {
    // used for flatMap
    R apply(T t);
  }

  @FunctionalInterface
  public static interface F2<T, R> {
    // used for flatMap
    R apply(T t);
  }

  @FunctionalInterface
  public static interface F3<T, R> {
    // used for flatMap
    R apply(T t);
  }

  @FunctionalInterface
  public static interface F4<T, R> {
    // used for flatMap
    R apply(T t);
  }

  @FunctionalInterface
  public static interface B1<T, R> {
    // used for scanLeft
    R apply(R r, T t);
  }

  @FunctionalInterface
  public static interface C1<T, R> {
    // used for scanRight
    R apply(T t, R r);
  }

  @FunctionalInterface
  public static interface C2<T, R> {
    // used for scanRight
    R apply(T t, R r);
  }

  @FunctionalInterface
  public interface ByteConsumer {
    void accept(byte value);

    default ByteConsumer andThen(ByteConsumer after) {
      Objects.requireNonNull(after);
      return (byte t) -> {
        accept(t);
        after.accept(t);
      };
    }
  }

  @FunctionalInterface
  public interface ShortConsumer {
    void accept(short value);

    default ShortConsumer andThen(ShortConsumer after) {
      Objects.requireNonNull(after);
      return (short t) -> {
        accept(t);
        after.accept(t);
      };
    }
  }

  @FunctionalInterface
  public interface FloatConsumer {
    void accept(float value);

    default FloatConsumer andThen(FloatConsumer after) {
      Objects.requireNonNull(after);
      return (float t) -> {
        accept(t);
        after.accept(t);
      };
    }
  }

  @FunctionalInterface
  public interface CharConsumer {
    void accept(char value);

    default CharConsumer andThen(CharConsumer after) {
      Objects.requireNonNull(after);
      return (char t) -> {
        accept(t);
        after.accept(t);
      };
    }
  }

  @FunctionalInterface
  public interface BooleanConsumer {
    void accept(boolean value);

    default BooleanConsumer andThen(BooleanConsumer after) {
      Objects.requireNonNull(after);
      return (boolean t) -> {
        accept(t);
        after.accept(t);
      };
    }
  }
  
  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }

  @FunctionalInterface
  public interface ByteSupplier {
    byte getAsByte();
  }

  @FunctionalInterface
  public interface ShortSupplier {
    byte getAsShort();
  }

  @FunctionalInterface
  public interface FloatSupplier {
    float getAsFloat();
  }

  @FunctionalInterface
  public interface CharSupplier {
    char getAsChar();
  }

  @FunctionalInterface
  public interface BooleanSupplier {
    boolean getAsBoolean();
  }

  @FunctionalInterface
  public interface TriFunction<A, B, C, D> {
    D apply(A a, B b, C c);
  }

  @FunctionalInterface
  public interface QuadFunction<A, B, C, D, E> {
    E apply(A a, B b, C c, D d);
  }

  @FunctionalInterface
  public interface PentFunction<A, B, C, D, E, F> {
    F apply(A a, B b, C c, D d, E e);
  }

  public static double mean(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }
  
  public static double mean(Integer[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }

  public static double mean(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }
  
  public static double mean(Long[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }

  public static double mean(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }
  
  public static double mean(Double[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
  }

  public static double mean(int[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }
  
  public static double mean(Integer[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }

  public static double mean(long[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }
  
  public static double mean(Long[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }

  public static double mean(double[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }

  public static double mean(Double[][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum / numel(a);
  }

  public static double mean(int[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }
  
  public static double mean(Integer[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }

  public static double mean(long[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }

  public static double mean(Long[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }

  public static double mean(double[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }

  public static double mean(Double[][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum / numel(a);
  }

  public static double mean(int[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }

  public static double mean(Integer[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }

  public static double mean(long[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }
  
  public static double mean(Long[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }

  public static double mean(double[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }

  public static double mean(Double[][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            sum += a[i][j][k][l];
    return sum / numel(a);
  }
  
  public static double mean(int[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }
  
  public static double mean(Integer[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }

  public static double mean(long[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }

  public static double mean(Long[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }
  
  public static double mean(double[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }
  
  public static double mean(Double[][][][][] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    double sum = 0.;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          for (int l = 0; l < a[i][j][k].length; l++)
            for (int m = 0; m < a[i][j][k][m].length; l++)
              sum += a[i][j][k][l][m];
    return sum / numel(a);
  }

  public static double abmean(int[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return absum(a) * 1. / a.length;
  }

  public static double abmean(long[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return absum(a) * 1. / a.length;
  }

  public static double abmean(double[] a) {
    // return the mean of the sum of the absolute values of the elements of a
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return absum(a) * 1. / a.length;
  }

  public static double median(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("median: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("avg: array must have at least one element");
    int m = a.length / 2;
    if (a.length % 2 == 1)
      return a[m];
    return (a[m - 1] + a[m]) * 1. / 2;
  }

  public static double median(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("median: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("avg: array must have at least one element");
    int m = a.length / 2;
    if (a.length % 2 == 1)
      return a[m];
    return (a[m - 1] + a[m]) * 1. / 2;
  }

  public static double median(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("median: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("avg: array must have at least one element");
    int m = a.length / 2;
    if (a.length % 2 == 1)
      return a[m];
    return (a[m - 1] + a[m]) / 2;
  }

  //Ex1128BinSearchUpdateWhitelist
  public static boolean hasDups(int[] a) {
    int[] c = Arrays.copyOf(a, a.length);
    Arrays.sort(c);
    for (int i = 0; i < c.length - 1; i++)
      if (c[i] == c[i + 1])
        return true;
    return false;
  }

  public static int dim(Object o) {
    // if o has an array type return its number of dimensions
    // else if it's null return -1 else return 0
    if (o == null)
      return -1;
    if (!o.getClass().isArray())
      return 0;
    return dim(o.getClass());
  }

  public static int dim(Class<?> type) {
    // if type is an array type return its number of dimensions
    // else if it's null return -1 else return 0
    if (type == null)
      return -1;
    if (!type.isArray())
      return 0;
    //return dim(type.getComponentType()) + 1; // works 
    //return type.getName().replaceAll("^\\[","").length(); // works
    Matcher matcher = Pattern.compile("(^[\\[]+)").matcher(type.getName());
    if (matcher.find())
      return matcher.group(1).length();
    return 0;
  }

  public static String rootComponentName(Object o) {
    // returns a string representing the base component type of an array.
    // for example if o is an int[][][] its rootComponentName is int
    // whereas its componentType is int[][].
    if (o == null)
      return "null";
    int dim = dim(o);
    if (dim == 0)
      return o.getClass().getName();
    if (!o.getClass().isArray())
      throw new IllegalArgumentException("rootComponentName: argument must be an array");
    String name = o.getClass().getName().replaceFirst("^[\\[]+", "");
    switch (name) {
    case "B":
      return "byte";
    case "C":
      return "char";
    case "D":
      return "double";
    case "F":
      return "float";
    case "I":
      return "int";
    case "J":
      return "long";
    case "S":
      return "short";
    case "Z":
      return "boolean";
    default:
      return name.substring(1, name.length() - 1);
    }
  }

  public static Class<?> rootComponentType(Object o) {
    // returns the base component type of an array.
    // for example if o is an int[][][] its rootComponentType 
    // is int.class whereas its componentType is [[I.
    if (o == null)
      return null;
    int dim = dim(o);
    if (dim == 0)
      return o.getClass();
    if (!o.getClass().isArray())
      throw new IllegalArgumentException("rootComponentType: argument must be an array");
    String name = o.getClass().getName().replaceFirst("^[\\[]+", "");
    switch (name) {
    case "B":
      return byte.class;
    case "C":
      return char.class;
    case "D":
      return double.class;
    case "F":
      return float.class;
    case "I":
      return int.class;
    case "J":
      return long.class;
    case "S":
      return short.class;
    case "Z":
      return boolean.class;
    default:
      try {
        name = name.substring(1, name.length() - 1);
        return Class.forName(name);
      } catch (ClassNotFoundException e) {
        System.out.println("rootComponentType: class not found for " + name);
        return null;
      }
    }
  }

  @SafeVarargs
  public static int[] dims(Object o, int[]... d) {
    // if o is an array, returns an int[] array containing the length of
    // each of its dimensions from first to last, assuming that all elements
    // of each dimension have the same length; if the first element of any
    // dimension has zero length insert 0 for it and successive dimensions.
    // for example: given double[][][][][] a = new double[1][2][3][4][5];
    // dims(a) returns int[1,2,3,4,5].
    // Note the use of d is for recursive calls to dims() within it.

    int dim = dim(o);
    if (!(dim > 0 && o.getClass().isArray()))
      throw new IllegalArgumentException("dims: argument must be an array");

    Object[] a = null;
    if (isPrimitiveArray(o)) {
      a = (Object[]) box(o);
    } else
      a = (Object[]) o;

    int[] r = null;
    if (d.length == 0) {
      r = new int[dim];
    } else
      r = d[0]; // if it exists d[0] is an int[]

    r[r.length - dim] = a.length;

    if (a.length == 0) {
      for (int i = dim; i < r.length; i++)
        r[i] = 0;
      return r;
    }

    if (dim > 1)
      dims(a[0], r);

    return r;
  }

  //Ex1134Filtering
  public static int[] bitSortUnique(int[] a) {
    // functional: does not modify a
    // returns only the unique elements in a sorted
    // uses bits in a new int[] to track existing elements without presort
    assert a != null;
    if (a.length == 0)
      return a;
    int[] b = new int[max(a) / 32 + 1]; // b max size for use as bit set
    for (int i = 0; i < a.length; i++)
      set(a[i], b);
    return bits(b);
  }

  //http://www.programcreek.com/2012/11/quicksort-array-in-java/
  public static void quickSort(int[] a, int low, int high) {
    // not functional: in place sort may modify a
    // called by quickSort(int[] a) and  quickSortCopy(int[] a)
    assert a != null;
    assert a.length > 0;
    assert low < high;
    // pick the pivot
    int middle = low + (high - low) / 2;
    int pivot = a[middle];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot)
        i++;
      while (a[j] > pivot)
        j--;
      if (i <= j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j)
      quickSort(a, low, j);
    if (high > i)
      quickSort(a, i, high);
  }

  public static void quickSort(int[] a) {
    // not functional: in place sort may modify a
    // calls quickSort(int[] a, int low, int high)
    assert a != null;
    assert a.length > 0;
    if (a.length < 2)
      return;
    int low = 0;
    int high = a.length - 1;
    assert low < high;
    // pick the pivot
    int pivot = a[low + (high - low) / 2];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot)
        i++;
      while (a[j] > pivot)
        j--;
      if (i <= j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j)
      quickSort(a, low, j);
    if (high > i)
      quickSort(a, i, high);
  }

  public static int[] quickSortCopy(int[] a) {
    // quickSorts a copy of a and returns it
    // calls quickSort(int[] a, int low, int high)
    if (a == null || a.length < 1) 
      throw new IllegalArgumentException("quickSortCopy: a can't be null or have length < 1");
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++) b[i] = a[i]; // faster  than Arrays.copyOf
    if (a.length < 2) return b;
    quickSort(b, 0, b.length-1);
    return b;
  }
//    int low = 0;
//    int high = a.length - 1;
//    assert low < high;
//    // pick the pivot
//    int pivot = b[low + (high - low) / 2];
//    // make left < pivot and right > pivot
//    int i = low, j = high;
//    while (i <= j) {
//      while (b[i] < pivot)
//        i++;
//      while (b[j] > pivot)
//        j--;
//      if (i <= j) {
//        int temp = b[i];
//        b[i] = b[j];
//        b[j] = temp;
//        i++;
//        j--;
//      }
//    return b;
//  }
    // recursively sort two sub parts
//    if (low < j)
//      quickSort(b, low, j);
//    if (high > i)
//      quickSort(b, i, high);
//    return b;
//  }

  //http://www.programcreek.com/2012/11/quicksort-array-in-java/
  public static void quickSort(double[] a, int low, int high) {
    // not functional: in place sort may modify a
    // called by quickSort(double[] a) and quickSortCopy(double[] a)
    assert a != null;
    assert a.length > 0;
    assert low < high;
    // pick the pivot
    int middle = low + (high - low) / 2;
    double pivot = a[middle];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot)
        i++;
      while (a[j] > pivot)
        j--;
      if (i <= j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j)
      quickSort(a, low, j);
    if (high > i)
      quickSort(a, i, high);
  }

  public static double[] quickSort(double[] a) {
    // functional: a sorted clone of a is returned
    // calls quickSort(double[] a, int low, int high)
    assert a != null;
    assert a.length > 0;
    if (a.length < 2)
      return (double[]) clone(a);
    int low = 0;
    int high = a.length - 1;
    assert low < high;
    a = (double[]) clone(a);
    // pick the pivot
    double pivot = a[low + (high - low) / 2];
    // make left < pivot and right > pivot
    int i = low, j = high;
    while (i <= j) {
      while (a[i] < pivot)
        i++;
      while (a[j] > pivot)
        j--;
      if (i <= j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
        i++;
        j--;
      }
    }
    // recursively sort two sub parts
    if (low < j)
      quickSort(a, low, j);
    if (high > i)
      quickSort(a, i, high);
    return a;
  }

  public static void swap(byte a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    byte tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(short a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    short tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(int a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(long a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    long tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(float a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    float tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(double a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    double tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(char a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    char tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static void swap(boolean a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    boolean tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static <T> void swap(T a[], int i, int j) {
    assert a != null;
    assert a.length != 0;
    assert i >= 0 && i <= a.length - 1;
    assert j >= 0 && j <= a.length - 1;
    T tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public static double determinant(int[][] a) {
    // a must be a square matrix
    if (a == null)
      throw new IllegalArgumentException("determinant: the array must be not be null");
    int n = order(a);
    if (n < 0)
      throw new IllegalArgumentException("determinant: a must be a square array");
    if (n == 0)
      throw new IllegalArgumentException("determinant: a must have at least one element");

    double det = 0;
    if (n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0] * a[1][1] - a[1][0] * a[0][1];
    } else {
      det = 0;
      for (int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n - 1][];
        for (int k = 0; k < n - 1; k++) {
          m[k] = new double[n - 1];
        }
        for (int i = 1; i < n; i++) {
          int j2 = 0;
          for (int j = 0; j < n; j++) {
            if (j == j1)
              continue;
            m[i - 1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0, 1.0 + j1 + 1.0) * a[0][j1] * determinant(m);
      }
    }
    return det;
  }

  public static double determinant(long[][] a) {
    // a must be a square matrix
    if (a == null)
      throw new IllegalArgumentException("determinant: the array must be not be null");
    int n = order(a);
    if (n < 0)
      throw new IllegalArgumentException("determinant: a must be a square array");
    if (n == 0)
      throw new IllegalArgumentException("determinant: a must have at least one element");

    double det = 0;
    if (n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0] * a[1][1] - a[1][0] * a[0][1];
    } else {
      det = 0;
      for (int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n - 1][];
        for (int k = 0; k < n - 1; k++) {
          m[k] = new double[n - 1];
        }
        for (int i = 1; i < n; i++) {
          int j2 = 0;
          for (int j = 0; j < n; j++) {
            if (j == j1)
              continue;
            m[i - 1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0, 1.0 + j1 + 1.0) * a[0][j1] * determinant(m);
      }
    }
    return det;
  }

  // http://www.sanfoundry.com/java-program-compute-determinant-matrix/
  public static double determinant(double[][] a) {
    // a must be a square matrix
    if (a == null)
      throw new IllegalArgumentException("determinant: the array must be not be null");
    int n = order(a);
    if (n < 0)
      throw new IllegalArgumentException("determinant: a must be a square array");
    if (n == 0)
      throw new IllegalArgumentException("determinant: a must have at least one element");

    double det = 0;
    if (n == 1) {
      det = a[0][0];
    } else if (n == 2) {
      det = a[0][0] * a[1][1] - a[1][0] * a[0][1];
    } else {
      det = 0;
      for (int j1 = 0; j1 < n; j1++) {
        double[][] m = new double[n - 1][];
        for (int k = 0; k < n - 1; k++) {
          m[k] = new double[n - 1];
        }
        for (int i = 1; i < n; i++) {
          int j2 = 0;
          for (int j = 0; j < n; j++) {
            if (j == j1)
              continue;
            m[i - 1][j2] = a[i][j];
            j2++;
          }
        }
        det += Math.pow(-1.0, 1.0 + j1 + 1.0) * a[0][j1] * determinant(m);
      }
    }
    return det;
  }

  public static int order(int[][] a) {
    // return order of a if it's square else return -1
    if (a == null)
      throw new IllegalArgumentException("order: the array must be not be null");
    int n = a.length;
    if (n == 0)
      return 0;
    int a0len = a[0].length;
    if (n != a0len)
      return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len)
        return -1;
    return n;
  }

  public static int order(long[][] a) {
    // return order of a if it's square else return -1
    if (a == null)
      throw new IllegalArgumentException("order: the array must be not be null");
    int n = a.length;
    if (n == 0)
      return 0;
    int a0len = a[0].length;
    if (n != a0len)
      return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len)
        return -1;
    return n;
  }

  public static int order(double[][] a) {
    // return order of a if it's square else return -1
    if (a == null)
      throw new IllegalArgumentException("order: the array must be not be null");
    int n = a.length;
    if (n == 0)
      return 0;
    int a0len = a[0].length;
    if (n != a0len)
      return -1;
    for (int i = 1; i < n; i++)
      if (a[i].length != a0len)
        return -1;
    return n;
  }

  public static boolean isSquare(int[][] a) {
    if (order(a) == -1)
      return false;
    return true;
  }

  public static boolean isSquare(long[][] a) {
    if (order(a) == -1)
      return false;
    return true;
  }

  public static boolean isSquare(double[][] a) {
    if (order(a) == -1)
      return false;
    return true;
  }

  //http://www.sanfoundry.com/java-program-find-inverse-matrix/
  public static double[][] invert(double a[][]) {
    // returns the inverse of a if it has one
    int n = a.length;
    double x[][] = new double[n][n];
    double b[][] = new double[n][n];
    int index[] = new int[n];
    for (int i = 0; i < n; ++i)
      b[i][i] = 1;

    // Transform the matrix into an upper triangle
    gaussian(a, index);

    // Update the matrix b[i][j] with the ratios stored
    for (int i = 0; i < n - 1; ++i)
      for (int j = i + 1; j < n; ++j)
        for (int k = 0; k < n; ++k)
          b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];

    // Perform backward substitutions
    for (int i = 0; i < n; ++i) {
      x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
      for (int j = n - 2; j >= 0; --j) {
        x[j][i] = b[index[j]][i];
        for (int k = j + 1; k < n; ++k) {
          x[j][i] -= a[index[j]][k] * x[k][i];
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
    for (int i = 0; i < n; ++i)
      index[i] = i;

    // Find the rescaling factors, one from each row
    for (int i = 0; i < n; ++i) {
      double c1 = 0;
      for (int j = 0; j < n; ++j) {
        double c0 = Math.abs(a[i][j]);
        if (c0 > c1)
          c1 = c0;
      }
      c[i] = c1;
    }

    // Search the pivoting element from each column
    int k = 0;
    for (int j = 0; j < n - 1; ++j) {
      double pi1 = 0;
      for (int i = j; i < n; ++i) {
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
      for (int i = j + 1; i < n; ++i) {
        double pj = a[index[i]][j] / a[index[j]][j];
        // Record pivoting ratios below the diagonal
        a[index[i]][j] = pj;
        // Modify other elements accordingly
        for (int l = j + 1; l < n; ++l)
          a[index[i]][l] -= pj * a[index[j]][l];
      }
    }
  }

  //http://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java

  // translating scala.Array methods for int and double Java arrays

  public static void scalaArrayStuff() {
    // marker method
  }

  public static long absum(int[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < 0) {
        sum -= a[i];
      } else
        sum += a[i];
    return sum;
  }

  public static long absum(long[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < 0) {
        sum -= a[i];
      } else
        sum += a[i];
    return sum;
  }

  public static double absum(double[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < 0) {
        sum -= a[i];
      } else
        sum += a[i];
    return sum;
  }

  public static BigInteger absum(BigInteger[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      sum = sum.add(a[i].abs());
    return sum;
  }

  public static BigDecimal absum(BigDecimal[] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      sum = sum.add(a[i].abs());
    return sum;
  }

  public static long absum(int[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {
          sum -= a[i][j];
        } else
          sum += a[i][j];
    return sum;
  }

  public static long absum(long[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {
          sum -= a[i][j];
        } else
          sum += a[i][j];
    return sum;
  }

  public static double absum(double[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        if (a[i][j] < 0) {
          sum -= a[i][j];
        } else
          sum += a[i][j];
    return sum;
  }

  public static BigInteger absum(BigInteger[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j].abs());
    return sum;
  }

  public static BigDecimal absum(BigDecimal[][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j].abs());
    return sum;
  }

  public static long absum(int[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {
            sum -= a[i][j][k];
          } else
            sum += a[i][j][k];
    return sum;
  }

  public static long absum(long[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {
            sum -= a[i][j][k];
          } else
            sum += a[i][j][k];
    return sum;
  }

  public static double absum(double[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          if (a[i][j][k] < 0) {
            sum -= a[i][j][k];
          } else
            sum += a[i][j][k];
    return sum;
  }

  public static BigInteger absum(BigInteger[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k].abs());
    return sum;
  }

  public static BigDecimal absum(BigDecimal[][][] a) {
    // return the sum of the absolute value of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k].abs());
    return sum;
  }

  public static byte[] append(byte[] a, byte... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (b == null || b.length == 0)
      return a;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    byte[] c = new byte[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static short[] append(short[] a, short... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (b == null || b.length == 0)
      return a;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    short[] c = new short[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static int[] append(int[] a, int... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (b == null || b.length == 0)
      return a;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    int[] c = new int[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static long[] append(long[] a, long... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    long[] c = new long[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static float[] append(float[] a, float... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    float[] c = new float[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static double[] append(double[] a, double... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    double[] c = new double[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static char[] append(char[] a, char... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    char[] c = new char[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static boolean[] append(boolean[] a, boolean... b) {
    // append b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    boolean[] c = new boolean[a.length + b.length];
    for (int i = 0; i < a.length; i++)
      c[i] = a[i];
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  @SafeVarargs
  public static <T> T[] append(T[] a, T... b) {
    // add b to the end of a
    if (a == null && b == null)
      throw new IllegalArgumentException("append: both arrays can't be null");
    if (a == null || a.length == 0)
      return b;
    if (a.length + b.length > maxArrayLength)
      throw new IllegalArgumentException("append: the sum of the length of both arrays exceeds the VM limit");
    T[] c = copyOf(a, a.length + b.length);
    for (int i = 0; i < b.length; i++)
      c[a.length + i] = b[i];
    return c;
  }

  public static <T> StringBuilder addString(T[] a, StringBuilder sb) {
    // append all elements of a to sb and then return sb
    if (a == null)
      throw new IllegalArgumentException("addString: " + "argument can't be null");
    if (sb == null)
      sb = new StringBuilder();
    for (int i = 0; i < a.length; i++)
      sb.append(a[i]);
    return sb;
  }

  public static <T> StringBuilder addString(T[] a, StringBuilder sb, String sep) {
    // append all elements of a separated by sep to sb and then return sb
    if (a == null)
      throw new IllegalArgumentException("addString: " + "argument can't be null");
    if (sb == null)
      sb = new StringBuilder();
    if (a.length == 0)
      return sb;
    for (int i = 0; i < a.length - 1; i++)
      sb.append(a[i] + sep);
    sb.append(a[a.length - 1]);
    return sb;
  }

  public static <T> StringBuilder addString(T[] a, StringBuilder sb, String start, String sep, String end) {
    // append all elements of a to sbusing start, separator and end strings and 
    // return sb
    if (a == null)
      throw new IllegalArgumentException("addString: " + "argument can't be null");
    if (sb == null)
      sb = new StringBuilder();
    sb.append(start);
    if (a.length == 0) {
      sb.append(end);
      return sb;
    }
    for (int i = 0; i < a.length - 1; i++)
      sb.append(a[i] + sep);
    sb.append(a[a.length - 1] + end);
    return sb;
  }

  public static <R, T> R aggregate(T[] a, R z, BiFunction<R, T, R> accumulator, BinaryOperator<R> combiner,
      int partitionSize) {
    // aggregate elements of a with accumulator, combine intermediate results
    // for each partition with combiner and return the final result. 
    // aggregation for each partition is initialized with z, typically the 
    // neutral element for the accumulator. the purpose of partitioning is to
    // enable parallel processing, see aggregateMulti for example.
    if (a == null)
      throw new IllegalArgumentException("aggregate: " + "the array argument can't be null");
    if (accumulator == null)
      throw new IllegalArgumentException("aggregate: " + "the accumulator argument can't be null");
    if (combiner == null)
      throw new IllegalArgumentException("aggregate: " + "combiner argument can't be null");
    if (partitionSize < 1)
      throw new IllegalArgumentException("aggregate: " + "the partitionSize must be > 0");

    int size = partitionSize;
    int parts = a.length % size == 0 ? a.length / size : 1 + a.length / size;

    T[][] partitions = ofDim(a.getClass().getComponentType(), parts, size);

    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < size; j++) {
        if (i + j < a.length) {
          partitions[i / size][j] = a[i + j];
        } else
          break;
      }
      i += size - 1; // the loop adds 1 on each iteration
    }

    @SuppressWarnings("unchecked")
    R[] r = (R[]) Array.newInstance(z.getClass(), parts);
    for (int i = 0; i < parts; i++)
      r[i] = z;

    for (int i = 0; i < parts; i++)
      // this loop could run in a new thread for each partition (partitions[i])
      for (int j = 0; j < size; j++)
        if (partitions[i][j] != null)
          r[i] = accumulator.apply(r[i], partitions[i][j]);

    R result = z;
    for (int i = 0; i < r.length; i++)
      if (r[i] != null)
        result = combiner.apply(result, r[i]);
    return result;
  }

  public static <R, T> R aggregateMulti(T[] a, R z, BiFunction<R, T, R> accumulator, BinaryOperator<R> combiner,
      int partitionSize) {
    // aggregate elements of a with accumulator, combine intermediate results
    // for each partition with combiner and return the final result. 
    // aggregation for each partition is initialized with z, typically the 
    // neutral element for the accumulator and done in a separate thread.
    if (a == null)
      throw new IllegalArgumentException("aggregateMulti: " + "the array argument can't be null");
    if (accumulator == null)
      throw new IllegalArgumentException("aggregateMulti: " + "the accumulator argument can't be null");
    if (combiner == null)
      throw new IllegalArgumentException("aggregateMulti: " + "combiner argument can't be null");
    if (partitionSize < 1)
      throw new IllegalArgumentException("aggregateMulti: " + "the partitionSize must be > 0");

    int size = partitionSize;
    int parts = a.length % size == 0 ? a.length / size : 1 + a.length / size;

    T[][] partitions = ofDim(a.getClass().getComponentType(), parts, size);

    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < size; j++) {
        if (i + j < a.length) {
          partitions[i / size][j] = a[i + j];
        } else
          break;
      }
      i += size - 1; // the loop adds 1 on each iteration
    }
    @SuppressWarnings("unchecked")
    R[] r = (R[]) Array.newInstance(z.getClass(), parts);
    for (int i = 0; i < parts; i++)
      r[i] = z;

    Thread[] threads = new Thread[parts];

    for (int i = 0; i < parts; i++) {
      int[] g = new int[] { i }; //encapsulate i to access it in lambda
      threads[i] = new Thread(() -> {
        for (int j = 0; j < size; j++)
          if (partitions[g[0]][j] != null)
            r[g[0]] = accumulator.apply(r[g[0]], partitions[g[0]][j]);
      });
    }

    for (Thread t : threads)
      t.start();
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    R result = z;
    for (int i = 0; i < r.length; i++)
      if (r[i] != null)
        result = combiner.apply(result, r[i]);
    return result;
  }

  public static <T> boolean allNull(T[] a) {
    if (a == null)
      throw new IllegalArgumentException("allNull: " + "the array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("allNull: " + "the array must have length > 0");
    for (int i = 0; i < a.length; i++)
      if (a[i] != null)
        return false;
    return true;
  }

  public static <T> T apply(T[] a, int i) {
    // return a[i] or throw an ArrayIndexOutOfBoundsException 
    // if i < 0 or length <= i
    if (a == null)
      throw new IllegalArgumentException("apply: " + "the array argument can't be null");
    if (i < 0 || i >= a.length)
      throw new ArrayIndexOutOfBoundsException("apply: the index i is not available in the array");
    return a[i];
  }

  public static String arrayToString(Object a, int... options) {
    // represent a as a string formatted to max line length given
    // by options[0] if it exists or maxLineLength by default if 
    // options.length == 0 or options[0] < 1 where maxLineLength 
    // is a global static int. rootComponentType(a).getSimpleName() 
    // is prefixed to the output by default. if options.length == 2, 
    // rootComponentType(a).getName() is prefixed and if 
    // options.length == 3 there is no prefix.
    if (a == null)
      throw new IllegalArgumentException("arrayToString: " + "argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("arrayToString: " + "argument must be an array");
    int dim = dim(a);
    String prefix = rootComponentType(a).getSimpleName();
    if (options.length == 2)
      prefix = rootComponentType(a).getName();
    else if (options.length == 3)
      prefix = "";
    // must box 1D arrays of primitives since using Arrays.deepToString(Object[])
    if (rootComponentType(a).isPrimitive() && dim < 2)
      a = box(a);
    int maxlen;
    if (options.length == 0 || options[0] < -1) {
      maxlen = maxLineLength;
    } else {
      maxlen = options[0];
      if (options[0] == -1) {
        prefix = "";
        maxlen = Integer.MAX_VALUE-8;
      }
    }

    String b;
    String s;
    String[] sa;
    boolean ok;
    int indent;
    Pattern pat = Pattern.compile("(^[\\[]+)");
    Matcher matcher;
    String d = Arrays.deepToString((Object[]) a).replaceAll(" ", "");

    // try alternatives until one has max line length <= maxlen 
    // or return the one with the shortest max line length
    s = prefix + d;
    if (s.length() <= maxlen)
      return s;

    if (!prefix.equals("")) {
      s = prefix + "[\n  " + d.substring(1, d.length());
      if (s.length() <= maxlen)
        return s;
    }

    for (int i = dim - 1; i > -1; i--) {
      b = i > 0 ? repeat(']', i) + "," : ",";
      if (prefix.equals("")) {
        sa = d.replaceAll(b, b + "\n").split("\n");
      } else {
        sa = (prefix + "[\n" + d.replaceAll(b, b + "\n")).split("\n");
      }
      ok = true;
      for (int j = 1; j < sa.length; j++) {
        if (!prefix.equals("") && j == 1)
          sa[1] = sa[1].substring(1, sa[1].length());
        matcher = pat.matcher(sa[j]);
        if (matcher.find()) {
          int len = matcher.group(1).length();
          if (prefix.equals("")) {
            indent = dim - len;
          } else {
            indent = dim + 1 - len;
          }
        } else {
          if (prefix.equals("")) {
            indent = dim;
          } else {
            indent = dim + 1;
          }
        }
        sa[j] = repeat(' ', indent) + sa[j];
        if (sa[j].length() > maxlen)
          ok = false;
      }
      if (i == 0 || ok)
        return String.join("\n", sa);
    }

    return "none";
  }

  public static Object box(Object a) {
    // copies an array with primitive rootComponentName into an array
    // with corresponding boxed rootComponentName and identical structure
    if (a == null)
      throw new IllegalArgumentException("box: " + "argument can't be null");
    if (!isPrimitiveArray(a))
      throw new IllegalArgumentException("box: " + "argument must be an array with primitive type rootComponentName");
    int dim = dim(a);
    if (dim < 1)
      throw new IllegalArgumentException("box: " + "dimension of argument must be > 0");

    String t = rootComponentName(a);

    if (dim == 1) { // base case of recursion
      Class<?> nt = null;

      switch (t) {
      case "byte":
        nt = java.lang.Byte.class;
        break;
      case "char":
        nt = java.lang.Character.class;
        break;
      case "double":
        nt = java.lang.Double.class;
        break;
      case "float":
        nt = java.lang.Float.class;
        break;
      case "int":
        nt = java.lang.Integer.class;
        break;
      case "long":
        nt = java.lang.Long.class;
        break;
      case "short":
        nt = java.lang.Short.class;
        break;
      case "boolean":
        nt = java.lang.Boolean.class;
        break;
      default:
        throw new IllegalArgumentException("box: " + "unidentified primitive type " + t);
      }

      int len = Array.getLength(a);
      Object array = Array.newInstance(nt, len);

      switch (t) {
      case "byte": {
        Byte[] array2 = (Byte[]) array;
        byte[] b = (byte[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "char": {
        Character[] array2 = (Character[]) array;
        char[] b = (char[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "double": {
        Double[] array2 = (Double[]) array;
        double[] b = (double[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "float": {
        Float[] array2 = (Float[]) array;
        float[] b = (float[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "int": {
        Integer[] array2 = (Integer[]) array;
        int[] b = (int[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "long": {
        Long[] array2 = (Long[]) array;
        long[] b = (long[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "short": {
        Short[] array2 = (Short[]) array;
        short[] b = (short[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      case "boolean": {
        Boolean[] array2 = (Boolean[]) array;
        boolean[] b = (boolean[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        break;
      }
      default:
        throw new IllegalArgumentException("box: " + "primitive type " + t + " not found");
      }

      return array;
      // end of dim=1 base recursion case
    } else {
      String nt = null;
      String prefix = repeat('[', dim - 1);

      switch (t) {
      case "byte":
        nt = prefix + "Ljava.lang.Byte;";
        break;
      case "char":
        nt = prefix + "Ljava.lang.Character;";
        break;
      case "double":
        nt = prefix + "Ljava.lang.Double;";
        break;
      case "float":
        nt = prefix + "Ljava.lang.Float;";
        break;
      case "int":
        nt = prefix + "Ljava.lang.Integer;";
        break;
      case "long":
        nt = prefix + "Ljava.lang.Long;";
        break;
      case "short":
        nt = prefix + "Ljava.lang.Short;";
        break;
      case "boolean":
        nt = prefix + "Lava.lang.Boolean;";
        break;
      default:
        throw new IllegalArgumentException("box: " + "unidentified primitive type " + t);
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
        Array.set(array, i, box(Array.get(a, i)));

      return array;
    }
  }

  public static boolean canEqual(Object a, Object b) {
    // for arrays only, determines if a can equal b using 
    // run time types and ignoring element values
    if (a == null && b == null)
      return true;
    if (a == null && b != null || a != null && b == null)
      return false;

    if (!(a.getClass().isArray() && b.getClass().isArray()))
      throw new IllegalArgumentException("canEqual: both arguments must be arrays");

    if (a.getClass().getComponentType().isPrimitive() && !b.getClass().getComponentType().isPrimitive()
        || !a.getClass().getComponentType().isPrimitive() && b.getClass().getComponentType().isPrimitive())
      return false;

    // dim does not define structure in detail 
    int dima = dim(a);
    int dimb = dim(b);
    if (dima != dimb)
      return false;

    if (a.getClass().getComponentType().isPrimitive() && b.getClass().getComponentType().isPrimitive()) {
      if (a.getClass().getComponentType() == b.getClass().getComponentType()) {
        return true;
      } else {
        return false;
      }
    }

    if (!(a.getClass().getComponentType().isPrimitive() && b.getClass().getComponentType().isPrimitive())) {
      if (a.getClass().getComponentType() == b.getClass().getComponentType()
          || a.getClass().getComponentType().isAssignableFrom(b.getClass().getComponentType())
          || b.getClass().getComponentType().isAssignableFrom(a.getClass().getComponentType())) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  public static char charAt(char[] a, int i) {
    // return a[i] or throw an ArrayIndexOutOfBoundsException 
    // if i < 0 or length <= i
    if (a == null)
      throw new IllegalArgumentException("charAt: " + "the array argument can't be null");
    if (i < 0 || i >= a.length)
      throw new ArrayIndexOutOfBoundsException("charAt: the index i is not available in the array");
    return a[i];
  }

  public static Object clone(Object a) {
    // deep copies an array of up to system max 255 dimension.   
    // if rootComponentName is not Object.class but an Object 
    // subclass with a working clone method the clone will contain
    // all new objects. elements of arrays of boxed types, String,
    // BigInteger and BigDecimal are copied since they're immutable.
    if (a == null)
      throw new IllegalArgumentException("clone: " + "argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("clone: " + "argument must be an array");
    int dim = dim(a);
    if (dim < 1)
      throw new IllegalArgumentException("clone: " + "dimension of argument must be > 0");

    int len = Array.getLength(a);

    if (dim == 1) { // base case of recursion

      String t = rootComponentName(a);
      Class<?> nt = a.getClass().getComponentType();
      Object array = Array.newInstance(nt, len);

      switch (t) {
      case "byte": {
        byte[] array2 = (byte[]) array;
        byte[] b = (byte[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "char": {
        char[] array2 = (char[]) array;
        char[] b = (char[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "double": {
        double[] array2 = (double[]) array;
        double[] b = (double[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "float": {
        float[] array2 = (float[]) array;
        float[] b = (float[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "int": {
        int[] array2 = (int[]) array;
        int[] b = (int[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "long": {
        long[] array2 = (long[]) array;
        long[] b = (long[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "short": {
        short[] array2 = (short[]) array;
        short[] b = (short[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "boolean": {
        boolean[] array2 = (boolean[]) array;
        boolean[] b = (boolean[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Byte": {
        Byte[] array2 = (Byte[]) array;
        Byte[] b = (Byte[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Character": {
        Character[] array2 = (Character[]) array;
        Character[] b = (Character[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Double": {
        Double[] array2 = (Double[]) array;
        Double[] b = (Double[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Float": {
        Float[] array2 = (Float[]) array;
        Float[] b = (Float[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Integer": {
        Integer[] array2 = (Integer[]) array;
        Integer[] b = (Integer[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Long": {
        Long[] array2 = (Long[]) array;
        Long[] b = (Long[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Short": {
        Short[] array2 = (Short[]) array;
        Short[] b = (Short[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.Boolean": {
        Boolean[] array2 = (Boolean[]) array;
        Boolean[] b = (Boolean[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.lang.String": {
        String[] array2 = (String[]) array;
        String[] b = (String[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.math.BigInteger": {
        BigInteger[] array2 = (BigInteger[]) array;
        BigInteger[] b = (BigInteger[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      case "java.math.BigDecimal": {
        BigDecimal[] array2 = (BigDecimal[]) array;
        BigDecimal[] b = (BigDecimal[]) a;
        for (int i = 0; i < len; i++)
          array2[i] = b[i];
        return array2;
      }
      default:
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
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                  System.out.println("clone: failed on " + e.getClass().getName() + " when " + "invoking "
                      + a.getClass().getComponentType().getName() + ".clone() " + "on element at index " + i
                      + " with value " + b[i]);
                  array2[i] = b[i];
                }
              }
            }
          } else {
            for (int i = 0; i < len; i++)
              array2[i] = b[i];
          }
        } else {
          for (int i = 0; i < len; i++) {
            if (b[i] == null) {
              array2[i] = null;
            } else {
              mClone = getCloneMethod(b[i].getClass());
              if (mClone != null) {
                try {
                  array2[i] = mClone.invoke(b[i]);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                  System.out.println("clone: failed on " + e.getClass().getName() + " when " + "invoking "
                      + a.getClass().getComponentType().getName() + ".clone() " + "on element at index " + i
                      + " with value " + b[i]);
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
      for (int i = 0; i < len; i++)
        array[i] = clone(Array.get(a, i));
      return array;
    }
  }

  public static <T, R> R[] collect(T[] a, Predicate<T> p, Function<T, R> f) {
    // builds R[] by applying f only to elements of a satisfying p and assuming
    // null elements should not be processed. this is meant to simulate the effect 
    // of applying a partial function, namely one partially defined over its domain,
    // however f is presumed to apply to all of its domain. requires the input array 
    // to have at least one non null element in order to construct the output array
    if (a == null)
      throw new IllegalArgumentException("collect: the array can't be null");
    R[] r = ofDim(a.getClass().getComponentType(), a.length);
    int rindex = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] != null && p.test(a[i]))
        r[rindex++] = f.apply(a[i]);
    return rindex == a.length ? r : take(r, rindex);
  }

  public static <T, R> Optional<R> collectFirst(T[] a, Predicate<T> p, Function<T, R> f) {
    // returns an Optional.ofNullable containing the result of applying f to the first
    // non-null element of a satisfying p or Optional.empty if no such element is in a.
    if (a == null)
      throw new IllegalArgumentException("collectFirst: " + " array can't be null");
    for (T t : a) {
      if (t != null && p.test(t))
        return Optional.of(f.apply(t));
      break;
    }
    return Optional.empty();
  }

  private static class Combinator<T> implements Iterable<T[]> {
    private T[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public Combinator(T[] items, int choose) {
      if (items == null || items.length == 0)
        throw new IllegalArgumentException("Combinator constructor: " + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length)
        throw new IllegalArgumentException(
            "Combinator constructor: " + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }

    public Combinator(List<T> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("Combinator constructor: items " + "can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException(
            "Combinator constructor: choose " + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] makeArray(final List<T> obj) {
      if (obj == null || obj.isEmpty())
        return null;
      final T t = obj.get(0);
      final T[] res = (T[]) Array.newInstance(t.getClass(), obj.size());
      for (int i = 0; i < obj.size(); i++)
        res[i] = obj.get(i);
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
        if (!hasNext())
          throw new NoSuchElementException();

        if (current == null) {
          current = new int[choose];
          for (int i = 0; i < choose; i++)
            current[i] = i;
        }

        T[] result = copyOf(items, choose);
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

  private static class CombinatorInt implements Iterable<int[]> {
    private int[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorInt(int[] items, int choose) {
      if (items == null || items.length == 0)
        throw new IllegalArgumentException("IntCombinator constructor: " + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length)
        throw new IllegalArgumentException(
            "IntCombinator constructor: " + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }

    @SuppressWarnings("unused")
    public CombinatorInt(List<Integer> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("IntCombinator constructor: items " + "can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException(
            "IntCombinator constructor: choose " + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }

    public static int[] makeArray(final List<Integer> obj) {
      if (obj == null || obj.isEmpty())
        return null;
      int[] res = new int[obj.size()];
      for (int i = 0; i < obj.size(); i++)
        res[i] = obj.get(i);
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
        if (!hasNext())
          throw new NoSuchElementException();

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

  private static class CombinatorLong implements Iterable<long[]> {
    private long[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorLong(long[] items, int choose) {
      if (items == null || items.length == 0)
        throw new IllegalArgumentException("LongCombinator constructor: " + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length)
        throw new IllegalArgumentException(
            "LongCombinator constructor: " + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }

    @SuppressWarnings("unused")
    public CombinatorLong(List<Long> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException("LongCombinator constructor: items " + "can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException(
            "LongCombinator constructor: choose " + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }

    public static long[] makeArray(final List<Long> obj) {
      if (obj == null || obj.isEmpty())
        return null;
      long[] res = new long[obj.size()];
      for (int i = 0; i < obj.size(); i++)
        res[i] = obj.get(i);
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
        if (!hasNext())
          throw new NoSuchElementException();

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

  private static class CombinatorDouble implements Iterable<double[]> {
    private double[] items;
    private int choose;
    private boolean finished;
    private int[] current;

    public CombinatorDouble(double[] items, int choose) {
      if (items == null || items.length == 0)
        throw new IllegalArgumentException("DoubleCombinator constructor: " + "items can't be null or have 0 length");
      if (choose <= 0 || choose > items.length)
        throw new IllegalArgumentException(
            "DoubleCombinator constructor: " + "choose must be between 0 and items.length inclusive");
      this.items = items;
      this.choose = choose;
      this.finished = false;
    }

    @SuppressWarnings("unused")
    public CombinatorDouble(List<Double> itemsList, int choose) {
      if (itemsList == null || itemsList.size() == 0)
        throw new IllegalArgumentException(
            "DoubleCombinator constructor: items " + "can't be null or have no elements");
      if (choose <= 0 || choose > itemsList.size())
        throw new IllegalArgumentException(
            "DoubleCombinator constructor: choose " + "must be between 0 and itemsList.size() inclusive");
      this.items = makeArray(itemsList);
      this.choose = choose;
      this.finished = false;
    }

    public static double[] makeArray(final List<Double> obj) {
      if (obj == null || obj.isEmpty())
        return null;
      double[] res = new double[obj.size()];
      for (int i = 0; i < obj.size(); i++)
        res[i] = obj.get(i);
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
        if (!hasNext())
          throw new NoSuchElementException();

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
      throw new IllegalArgumentException("combinations: " + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinations: " + "int n must be between 0 and the array length inclusive");
    return (new CombinatorInt(a, n)).iterator();
  }

  public static Stream<int[]> combinationStream(int[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationsStream: the array " + "can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException(
          "combinationsStream: int n " + "must be between 0 and the array length inclusive");
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
      throw new IllegalArgumentException("combinations: " + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinations: " + "int n must be between 0 and the array length inclusive");
    return (new CombinatorLong(a, n)).iterator();
  }

  public static Stream<long[]> combinationStream(long[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationsStream: the array " + "can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException(
          "combinationStream: int n " + "must be between 0 and the array length inclusive");
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
      throw new IllegalArgumentException("combinations: " + "the array can't be null or have 0 length");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinations: " + "int n must be between 0 and the array length inclusive");
    return (new CombinatorDouble(a, n)).iterator();
  }

  public static Stream<double[]> combinationStream(double[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationStream: the list " + "can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException(
          "combinationStream: int n " + "must be between 0 and the array length inclusive");
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
      throw new IllegalArgumentException("combinations: " + "the array can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException("combinations: " + "int n must be between 0 and the array length inclusive");
    return (new Combinator<T>(a, n)).iterator();
  }

  public static <T> Iterator<T[]> combinations(List<T> a, int n) {
    if (a == null || a.size() == 0)
      throw new IllegalArgumentException("combinations: " + "the list can't be null or have no elements");
    if (n <= 0 || n > a.size())
      throw new IllegalArgumentException("combinations: " + "int n must be between 0 and the list size inclusive");
    return (new Combinator<T>(a, n)).iterator();
  }

  public static <T> Stream<T[]> combinationStream(T[] a, int n) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("combinationStream: the array " + "can't be null or have no elements");
    if (n <= 0 || n > a.length)
      throw new IllegalArgumentException(
          "combinationStream: int n " + "must be between 0 and the array length inclusive");
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
      throw new IllegalArgumentException("combinationStream: the list " + "can't be null or have no elements");
    if (n <= 0 || n > a.size())
      throw new IllegalArgumentException("combinationStream: int n " + "must be between 0 and the list size inclusive");
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
  
  public static int compareTo(boolean b1, boolean b2) {
    // returns the same result as Boolean.compareTo but for boolean.
    // for use in sorting boolean arrays.
    return b1 == b2 ? 0 : b1 == true && b2 == false ? 1 : -1;
  }
  
  public static boolean contains(int[] a, int key) {
    // assumes a is sorted
    int i = indexOf(a, key);
    if (i == -1)
      return false;
    return true;
  }

  public static boolean contains(long[] a, long key) {
    // assumes a is sorted
    long i = indexOf(a, key);
    if (i == -1)
      return false;
    return true;
  }

  public static boolean contains(double[] a, double key) {
    // assumes a is sorted
    double i = indexOf(a, key);
    if (i == -1)
      return false;
    return true;
  }

  public static <T extends Comparable<? super T>> boolean contains(T[] a, T key) {
    // assumes a is sorted
    int i = indexOf(a, key);
    if (i == -1)
      return false;
    return true;
  }

  public static boolean containsSlice(int[] a, int... t) {
    // if a contains s as a slice return true else return false
    if (a == null)
      throw new IllegalArgumentException("containsSlice: the array can't be null");
    if (t.length > a.length)
      return false;
    if (t.length == a.length)
      return equals(a, t);

    int tlen = t.length;
    LOOP: for (int i = 0; i < a.length - tlen; i++) {
      for (int j = 0; j < tlen; j++) {
        if (a[i + j] != t[j])
          continue LOOP;
      }
      return true;
    }

    return false;
  }

  public static boolean containsSlice(long[] a, long... t) {
    // if a contains s as a slice return true else return false
    if (a == null)
      throw new IllegalArgumentException("containsSlice: the array can't be null");
    if (t.length > a.length)
      return false;
    if (t.length == a.length)
      return equals(a, t);

    int tlen = t.length;
    LOOP: for (int i = 0; i < a.length - tlen; i++) {
      for (int j = 0; j < tlen; j++) {
        if (a[i + j] != t[j])
          continue LOOP;
      }
      return true;
    }

    return false;
  }

  public static boolean containsSlice(double[] a, double... t) {
    // if a contains s as a slice return true else return false
    if (a == null)
      throw new IllegalArgumentException("containsSlice: the array can't be null");
    if (t.length > a.length)
      return false;
    if (t.length == a.length)
      return equals(a, t);

    int tlen = t.length;
    LOOP: for (int i = 0; i < a.length - tlen; i++) {
      for (int j = 0; j < tlen; j++) {
        if (a[i + j] != t[j])
          continue LOOP;
      }
      return true;
    }

    return false;
  }

  @SafeVarargs
  public static <T> boolean containsSlice(T[] a, T... t) {
    // if a contains s as a slice return true else return false
    if (a == null)
      throw new IllegalArgumentException("containsSlice: the array can't be null");
    if (t.length > a.length)
      return false;
    if (t.length == a.length)
      return equals(a, t);

    int tlen = t.length;
    LOOP: for (int i = 0; i < a.length - tlen; i++) {
      for (int j = 0; j < tlen; j++) {
        if (a[i + j] != t[j])
          continue LOOP;
      }
      return true;
    }

    return false;
  }

  public static byte[] copySort(byte[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    byte[] b = new byte[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static short[] copySort(short[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    short[] b = new short[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static int[] copySort(int[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static long[] copySort(long[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static float[] copySort(float[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    float[] b = new float[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static double[] copySort(double[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static char[] copySort(char[] a) {
    // does shellSort on a copy of a
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    char[] b = new char[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static boolean[] copySort(boolean[] a) {
    // does shellSort on a copy of a assuming true > false
    // functional: preserves a
    assert a != null;
    assert a.length != 0;
    boolean[] b = new boolean[a.length];
    for (int i = 0; i < a.length; i++) b[i] = a[i];
    if (b.length < 2) return b;
    shellSort(b);
    return b;
  }

  public static void copyToArray(int[] a, int[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(int[] a, long[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(int[] a, double[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(long[] a, long[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(long[] a, double[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(double[] a, double[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static <T extends U, U> void copyToArray(T[] a, U[] b, int start, int len) {
    // fills b with at most len elements from a starting
    // a index 0 in a and index start in b
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (len < 0)
      throw new IllegalArgumentException("copyToArray: len must be >= 0");
    if (len == 0 || b.length == 0 || a.length == 0)
      return;
    int alen = len < a.length ? len : a.length;
    int blen = start + len < b.length ? start + len : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(int[] a, int[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(int[] a, long[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(int[] a, double[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(long[] a, long[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(long[] a, double[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(double[] a, double[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static <T extends U, U> void copyToArray(T[] a, U[] b) {
    // fills b with elements of a starting at index 0 in both arrays
    // and stopping when the end of the shortest is reached
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can't be null");
    if (b.length == 0 || a.length == 0)
      return;
    int len = a.length <= b.length ? a.length : b.length;
    for (int i = 0; i < len; i++)
      b[i] = a[i];
  }

  public static void copyToArray(int[] a, int[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(int[] a, long[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(int[] a, double[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can't be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(long[] a, long[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(long[] a, double[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static void copyToArray(double[] a, double[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static <T extends U, U> void copyToArray(T[] a, U[] b, int start) {
    // fills b with elements of a starting index 0 in a and index
    // start in b and stopping when the end of either is reached   
    if (a == null || b == null)
      throw new IllegalArgumentException("copyToArray: neither array can be null");
    if (start > b.length - 1 || start < 0)
      throw new IllegalArgumentException("copyToArray: start is out of range in array b");
    if (b.length == 0 || a.length == 0)
      return;
    int alen = b.length - start < a.length ? b.length - start : a.length;
    int blen = start + a.length < b.length ? start + a.length : b.length;
    for (int i = 0, j = start; i < alen && j < blen; i++, j++)
      b[j] = a[i];
  }

  public static <T extends Comparable<? super T>> T[] copySort(T[] a) {
    // does shellSort on a copy of a
    if (a == null)
      throw new IllegalArgumentException("copySort: the array can't be null");
    if (a.length == 0)
      return copyOf(a, a.length);
    Tuple2<Optional<T>, Optional<Integer>> firstNonNull = findFirstNonNull(a);
    if (!firstNonNull._1.isPresent())
      return copyOf(a, a.length);
    T[] b = ofDim(firstNonNull._1.get().getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      b[i] = a[i];
    if (b.length < 2)
      return b;
    T t;
    for (int i = 0; i < b.length - 1; i++)
      for (int j = i + 1; j < b.length; j++)
        if (b[i].compareTo(b[j]) > 0) {
          t = b[i];
          b[i] = b[j];
          b[j] = t;
        }
    return b;
  }

  public static ByteBuffer copyToBuffer(byte[] a) {
    return ByteBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static ShortBuffer copyToBuffer(short[] a) {
    return ShortBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static IntBuffer copyToBuffer(int[] a) {
    return IntBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static LongBuffer copyToBuffer(long[] a) {
    return LongBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static FloatBuffer copyToBuffer(float[] a) {
    return FloatBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static DoubleBuffer copyToBuffer(double[] a) {
    return DoubleBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static CharBuffer copyToBuffer(char[] a) {
    return CharBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static BooleanBuffer copyToBuffer(boolean[] a) {
    return BooleanBuffer.allocate(a.length).put(a, 0, a.length);
  }

  public static <T, U> boolean corresponds(T[] t, U[] u, BiPredicate<T, U> p) {
    // tests where every element of t relates to the corresponding element of u
    // by satisfying p
    if (t == null || u == null)
      throw new IllegalArgumentException("corresponds: neither array can be null");
    if (t.length == 0 && u.length == 0)
      return true;
    if (t.length != u.length)
      return false;
    for (int i = 0; i < t.length; i++)
      if (!p.test(t[i], u[i]))
        return false;
    return true;
  }

  public static <T> int count(T[] a, Predicate<T> p) {
    // return the number of elements in a satisfying p
    if (a == null)
      throw new IllegalArgumentException("corresponds: the array can be null");
    int r = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        r++;
    return r;
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static int count(int[] a, int key) {
    // for unsorted array counts number of occurrences of key in a
    int c = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] == key)
        c++;
    return c;
  }

  //Ex1129BinarySearchEqualKeysMethod
  public static int countsorted(int[] a, int key) {
    // counts number of occurrences of key in a
    return rank(a, key + 1) - rank(a, key);
  }

  public static <T> Collection<?> deep(Object a) {
    // returns a possibly nested collection corresponding to a
    // provided a is an array
    return toNestedList(a);
  }

  public static <T> T[] diff(T[] a, T[] b) {
    // returns the multiset difference between a and b which includes
    // elements occurring only in a plus those occuring in both repeated
    // by the number of appearences in a minus those in b down to zero
    // and excluding all elements occuring only in b.
    if (a == null || b == null)
      throw new IllegalArgumentException("diff: neither array can be null");
    boolean allNulla = allNull(a);
    if (allNulla && allNull(b)) {
      int diff = a.length - b.length;
      if (diff <= 0)
        return copyOf(a, 0);
      else
        return copyOf(a, diff);
    }
    Map<T, Integer> map = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      map.merge(b[i], 1, Integer::sum);
    Tuple2<Optional<T>, Optional<Integer>> aNonNullT = findFirstNonNull(allNulla ? b : a);
    T[] t = ofDim(aNonNullT._1.get().getClass(), a.length);
    int tindex = 0;
    for (int i = 0; i < a.length; i++)
      if (map.containsKey(a[i])) {
        if (map.get(a[i]) > 0) {
          map.put(a[i], map.get(a[i]) - 1);
        } else
          t[tindex++] = a[i];
      } else
        t[tindex++] = a[i];
    return take(t, tindex);
  }
  
  public static boolean fibContains(int z[], int key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }
  
  public static boolean fibContains(char z[], char key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }

  public static  <T extends Comparable<? super T>> boolean fibContains(T z[], T key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }
  
  public static  <T> boolean fibContains(T z[], T key, Comparator<T> c) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key, c) != -1;
  }
  
  public static int fibIndexOf(int z[], int key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key < z[mid]) continue;
      else if (key > z[mid]) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }

  public static int fibIndexOf(char z[], char key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key < z[mid]) continue;
      else if (key > z[mid]) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }
  
  public static <T extends Comparable<? super T>> int fibIndexOf(T z[], T key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key.compareTo(z[mid]) < 0) continue;
      else if (key.compareTo(z[mid]) > 0) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }
  
  public static <T> int fibIndexOf(T z[], T key, Comparator<T> c) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || c.compare(key,z[mid]) < 0) continue;
      else if (c.compare(key,z[mid]) > 0) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }

  public static long fibonacci(int n) {
    // iterative fibonacci generator - fast and no risk of stack overflow.
    // long overflow occurs after the first 93 fibonacci numbers.
    if (n < 0) {
      return -1;
    } else if (n <= 1) {
      return n;
    } else if (n == 2) {
      return 1;
    } else {
      long m1 = 0; long m2 = 1;
      long t;
      for (int i = 0; i < n; i++) {
        t = m1; m1+=m2; m2 = t;
      }
      return m1;
    }
  }

  public static BigInteger fibonaccib(int n) {
    // iterative fibonacci generator - fast and no risk of stack overflow.
    if (n < 0) {
      return ONE.negate();
    } else if (n == 0) {
      return ZERO;
    } else if (n == 1) {
      return ONE;
    } else if (n == 2) {
      return ONE;
    } else {
      BigInteger m1 = ZERO; BigInteger m2 = ONE;
      BigInteger t;
      for (int i = 0; i < n; i++) {
        t = m1; m1 = m1.add(m2); m2 = t;
      }
      return m1;
    }
  }

  public static long fibonaccif(int n) {
    // fast table-based fibonacci number generator.
    // long overflow occurs after the first 93 fibonacci numbers.
    // fibonacci numbers can be extended below zero using  F(-n) = ((−1)**n+1)*Fn.
    // see http://dictionary.sensagent.com/negafibonacci/en-en/
    // and to https://en.wikipedia.org/wiki/Fibonacci_number#Negafibonacci
    long[] fibs93 = {
        0L,1L,1L,2L,3L,5L,8L,13L,21L,34L,55L,89L,144L,233L,377L,610L,987L,1597L,2584L,4181L,
        6765L,10946L,17711L,28657L,46368L,75025L,121393L,196418L,317811L,514229L,832040L,
        1346269L,2178309L,3524578L,5702887L,9227465L,14930352L,24157817L,39088169L,63245986L,
        102334155L,165580141L,267914296L,433494437L,701408733L,1134903170L,1836311903L,
        2971215073L,4807526976L,7778742049L,12586269025L,20365011074L,32951280099L,53316291173L,
        86267571272L,139583862445L,225851433717L,365435296162L,591286729879L,956722026041L,
        1548008755920L,2504730781961L,4052739537881L,6557470319842L,10610209857723L,
        17167680177565L,27777890035288L,44945570212853L,72723460248141L,117669030460994L,
        190392490709135L,308061521170129L,498454011879264L,806515533049393L,1304969544928657L,
        2111485077978050L,3416454622906707L,5527939700884757L,8944394323791464L,
        14472334024676221L,23416728348467685L,37889062373143906L,61305790721611591L,
        99194853094755497L,160500643816367088L,259695496911122585L,420196140727489673L,
        679891637638612258L,1100087778366101931L,1779979416004714189L,2880067194370816120L,
        4660046610375530309L,7540113804746346429L};
    if (n >= 0 && n <= 93) {  
      return fibs93[n];
    } else if (n < 0 && n >= -93) {
      return -n % 2 == 0 ? -fibs93[-n] : fibs93[-n];
    } else {
      System.err.println("fibonaccif: cannot go higher than 93 or lower than -93 "
          + " without long overflow.\nreturning -1");
      return -1;
    }  
  }
  
  public static long fibonaccir(int n) {
    //http://introcs.cs.princeton.edu/java/23recursion/Fibonacci.java.html
    // recursive fibonacci number generator.
    // long overflow occurs after the first 93 fibonacci numbers.
    if (n < 0) {
      return -1;
    } else if (n <= 1) {
      return n;
    } else {
      return fibonaccir(n-1) + fibonaccir(n-2);
    }
  }

  public static class FibonacciIterator implements PrimitiveIterator.OfLong {
    // this iterator cuts off after providing the first 93 fibonacci numbers
    // since beyond that long overflow occurs.
    private long m1 = 1; private long m2 = 1; private long t = 0; int c = 0;
    @Override
    public boolean hasNext() {
      return c < 93 ? true : false;
    }
    @Override
    public long nextLong() {
      if (!hasNext()) throw new NoSuchElementException();
      if (c == 0) { 
        c++; return 0; 
      } else if (c == 1) {
        c++; return 1; 
      } else if (c == 2) {
        c++; return 1; 
      } else {
        t = m1; m1+=m2; m2 = t; c++;
        return m1;
      }
    }
  }

  public static LongStream fibStream() {
    // due to the iterator on which it's based this stream is
    // is limited to 93 elements.
    PrimitiveIterator.OfLong fibit = new FibonacciIterator();
    return LongStream.generate(()->fibit.nextLong()).limit(93);
  }


  public static byte[] fillByte(int n, ByteSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillByte: " + "n must be > 0 and s cannot be null");
    byte[] a = new byte[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsByte();
    return a;
  }

  public static short[] fillShort(int n, ShortSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillShort: " + "n must be > 0 and s cannot be null");
    short[] a = new short[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsShort();
    return a;
  }

  public static int[] fillInt(int n, IntSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillInt: " + "n must be > 0 and s cannot be null");
    int[] a = new int[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsInt();
    return a;
  }

  public static long[] fillLong(int n, LongSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillLong: " + "n must be > 0 and s cannot be null");
    long[] a = new long[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsLong();
    return a;
  }

  public static float[] fillFloat(int n, FloatSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillFloat: " + "n must be > 0 and s cannot be null");
    float[] a = new float[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsFloat();
    return a;
  }

  public static double[] fillDouble(int n, DoubleSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillDouble: " + "n must be > 0 and s cannot be null");
    double[] a = new double[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsDouble();
    return a;
  }

  public static char[] fillChar(int n, CharSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillChar: " + "n must be > 0 and s cannot be null");
    char[] a = new char[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsChar();
    return a;
  }

  public static boolean[] fillBoolean(int n, BooleanSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillBoolean: " + "n must be > 0 and s cannot be null");
    boolean[] a = new boolean[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsBoolean();
    return a;
  }

  public static <T> T[] fill(int n, Supplier<T> s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fill: " + "n must be > 0 and s cannot be null");
    T tv = s.get();
    @SuppressWarnings("unchecked")
    T[] t = (T[]) Array.newInstance(tv.getClass(), n);
    t[0] = tv;
    for (int i = 1; i < t.length; i++)
      t[i] = s.get();
    return t;
  }

  public static byte[][] fillByte(int n1, int n2, ByteSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillByte: " + "n1 and n2 must be > 0 and s cannot be null");
    byte[][] a = new byte[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsByte();
    return a;
  }

  public static short[][] fillShort(int n1, int n2, ShortSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillShort: " + "n1 and n2 must be > 0 and s cannot be null");
    short[][] a = new short[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsShort();
    return a;
  }

  public static int[][] fillInt(int n1, int n2, IntSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillInt: " + "n1 and n2 must be > 0 and s cannot be null");
    int[][] a = new int[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsInt();
    return a;
  }

  public static long[][] fillLong(int n1, int n2, LongSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillLong: " + "n1 and n2 must be > 0 and s cannot be null");
    long[][] a = new long[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsLong();
    return a;
  }

  public static float[][] fillFloat(int n1, int n2, FloatSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillFloat: " + "n1 and n2 must be > 0 and s cannot be null");
    float[][] a = new float[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsFloat();
    return a;
  }

  public static double[][] fillDouble(int n1, int n2, DoubleSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillDouble: " + "n1 and n2 must be > 0 and s cannot be null");
    double[][] a = new double[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsDouble();
    return a;
  }

  public static char[][] fillChar(int n1, int n2, CharSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillChar: " + "n1 and n2 must be > 0 and s cannot be null");
    char[][] a = new char[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsChar();
    return a;
  }

  public static boolean[][] fillBoolean(int n1, int n2, BooleanSupplier s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fillBoolean: " + "n1 and n2 must be > 0 and s cannot be null");
    boolean[][] a = new boolean[n1][n2];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = s.getAsBoolean();
    return a;
  }

  public static <T> T[][] fill(int n1, int n2, Supplier<T> s) {
    if (n1 < 0 || n2 < 0 || s == null)
      throw new IllegalArgumentException("fill: " + "n1 and n2 must be > 0 and s cannot be null");
    T tv = s.get();
    @SuppressWarnings("unchecked")
    T[][] t = (T[][]) Array.newInstance(tv.getClass(), n1, n2);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        t[i][j] = s.get();
    return t;
  }

  public static byte[][][] fillByte(int n1, int n2, int n3, ByteSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillByte: n1, n2 and n3 must be > 0 and s cannot be null");
    byte[][][] a = new byte[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsByte();
    return a;
  }

  public static short[][][] fillShort(int n1, int n2, int n3, ShortSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillShort: n1, n2 and n3 must be > 0 and s cannot be null");
    short[][][] a = new short[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsShort();
    return a;
  }

  public static int[][][] fillInt(int n1, int n2, int n3, IntSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillInt: n1, n2 and n3 must be > 0 and s cannot be null");
    int[][][] a = new int[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsInt();
    return a;
  }

  public static long[][][] fillLong(int n1, int n2, int n3, LongSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillLong: n1, n2 and n3 must be > 0 and s cannot be null");
    long[][][] a = new long[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsLong();
    return a;
  }

  public static float[][][] fillFloat(int n1, int n2, int n3, FloatSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillFloat: n1, n2 and n3 must be > 0 and s cannot be null");
    float[][][] a = new float[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsFloat();
    return a;
  }

  public static double[][][] fillDouble(int n1, int n2, int n3, DoubleSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillDouble: n1, n2 and n3 must be > 0 and s cannot be null");
    double[][][] a = new double[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsDouble();
    return a;
  }

  public static char[][][] fillChar(int n1, int n2, int n3, CharSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillChar: n1, n2 and n3 must be > 0 and s cannot be null");
    char[][][] a = new char[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsChar();
    return a;
  }

  public static boolean[][][] fillBoolean(int n1, int n2, int n3, BooleanSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fillBoolean: n1, n2 and n3 must be > 0 and s cannot be null");
    boolean[][][] a = new boolean[n1][n2][n3];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = s.getAsBoolean();
    return a;
  }

  public static <T> T[][][] fill(int n1, int n2, int n3, Supplier<T> s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || s == null)
      throw new IllegalArgumentException("fill: n1, n2 and n3 must be > 0 and s cannot be null");
    T tv = s.get();
    @SuppressWarnings("unchecked")
    T[][][] t = (T[][][]) Array.newInstance(tv.getClass(), n1, n2, n3);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          t[i][j][k] = s.get();
    return t;
  }

  public static byte[][][][] fillByte(int n1, int n2, int n3, int n4, ByteSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillByte: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    byte[][][][] a = new byte[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsByte();
    return a;
  }

  public static short[][][][] fillShort(int n1, int n2, int n3, int n4, ShortSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillShort: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    short[][][][] a = new short[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsShort();
    return a;
  }

  public static int[][][][] fillInt(int n1, int n2, int n3, int n4, IntSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillInt: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    int[][][][] a = new int[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsInt();
    return a;
  }

  public static long[][][][] fillLong(int n1, int n2, int n3, int n4, LongSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillLong: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    long[][][][] a = new long[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsLong();
    return a;
  }

  public static float[][][][] fillFloat(int n1, int n2, int n3, int n4, FloatSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillFloat: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    float[][][][] a = new float[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsFloat();
    return a;
  }

  public static double[][][][] fillDouble(int n1, int n2, int n3, int n4, DoubleSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillDouble: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    double[][][][] a = new double[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsDouble();
    return a;
  }

  public static char[][][][] fillChar(int n1, int n2, int n3, int n4, CharSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillChar: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    char[][][][] a = new char[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsChar();
    return a;
  }

  public static boolean[][][][] fillBoolean(int n1, int n2, int n3, int n4, BooleanSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fillBoolean: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    boolean[][][][] a = new boolean[n1][n2][n3][n4];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = s.getAsBoolean();
    return a;
  }

  public static <T> T[][][][] fill(int n1, int n2, int n3, int n4, Supplier<T> s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || s == null)
      throw new IllegalArgumentException("fill: n1, n2, n3 and n4 must be > 0 " + "and s cannot be null");
    T tv = s.get();
    @SuppressWarnings("unchecked")
    T[][][][] t = (T[][][][]) Array.newInstance(tv.getClass(), n1, n2, n3, n4);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            t[i][j][k][l] = s.get();
    return t;
  }

  public static byte[][][][][] fillByte(int n1, int n2, int n3, int n4, int n5, ByteSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillByte: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    byte[][][][][] a = new byte[n1][n2][n3][n4][n5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsByte();
    return a;
  }

  public static short[][][][][] fillShort(int n1, int n2, int n3, int n4, int n5, ShortSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillShort: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    short[][][][][] a = new short[n1][n2][n3][n4][n5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsShort();
    return a;
  }

  public static int[][][][][] fillInt(int n1, int n2, int n3, int n4, int n5, IntSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillInt: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    int[][][][][] a = new int[n1][n2][n3][n4][5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsInt();
    return a;
  }

  public static long[][][][][] fillLong(int n1, int n2, int n3, int n4, int n5, LongSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillLong: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    long[][][][][] a = new long[n1][n2][n3][n4][n5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsLong();
    return a;
  }

  public static float[][][][][] fillFloat(int n1, int n2, int n3, int n4, int n5, FloatSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillFloat: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    float[][][][][] a = new float[n1][n2][n3][n4][n5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsFloat();
    return a;
  }

  public static double[][][][][] fillDouble(int n1, int n2, int n3, int n4, int n5, DoubleSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillDouble: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    double[][][][][] a = new double[n1][n2][n3][n4][5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsDouble();
    return a;
  }

  public static char[][][][][] fillChar(int n1, int n2, int n3, int n4, int n5, CharSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillChar: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    char[][][][][] a = new char[n1][n2][n3][n4][n5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsChar();
    return a;
  }

  public static boolean[][][][][] fillBoolean(int n1, int n2, int n3, int n4, int n5, BooleanSupplier s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fillBoolean: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    boolean[][][][][] a = new boolean[n1][n2][n3][n4][5];
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = s.getAsBoolean();
    return a;
  }

  public static <T> T[][][][][] fill(int n1, int n2, int n3, int n4, int n5, Supplier<T> s) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0 || s == null)
      throw new IllegalArgumentException("fill: n1, n2, n3, n4 and n5 must be > 0 " + "and s cannot be null");
    T tv = s.get();
    @SuppressWarnings("unchecked")
    T[][][][][] t = (T[][][][][]) Array.newInstance(tv.getClass(), n1, n2, n3, n4, n5);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              t[i][j][k][l][m] = s.get();
    return t;
  }

  public static <T> T[] distinct(T[] a) {
    //return an array with the distinct elements of a determined by equals
    Map<T, Integer> map = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      map.putIfAbsent(a[i], 1);
    return map.keySet().toArray(ofDim(a.getClass().getComponentType(), 0));
  }

  //Ex1133MatrixLibrary
  public static int dot(int[] x, int[] y) {
    // dot product
    assert x != null;
    assert y != null;
    int n = x.length;
    assert n != 0;
    assert n == y.length;

    int sum = 0;
    for (int i = 0; i < x.length; i++)
      sum += x[i] * y[i];
    return sum;
  }

  //Ex1133MatrixLibrary
  public static double dot(double[] x, double[] y) {
    // dot product
    assert x != null;
    assert y != null;
    int n = x.length;
    assert n != 0;
    assert n == y.length;

    double sum = 0;
    for (int i = 0; i < x.length; i++)
      sum += x[i] * y[i];
    return sum;
  }

  public static int[] drop(int[] a, int n) {
    // return an int array without the 1st n elements of a
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n > a.length)
      n = a.length;
    return copyOfRange(a, n, a.length);
  }

  public static long[] drop(long[] a, int n) {
    // return a long array without the 1st n elements of a
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n > a.length)
      n = a.length;
    return copyOfRange(a, n, a.length);
  }

  public static double[] drop(double[] a, int n) {
    // return a double array without the 1st n elements of a
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n > a.length)
      n = a.length;
    return copyOfRange(a, n, a.length);
  }

  public static <T> T[] drop(T[] a, int n) {
    // return a T array without the 1st n elements of a
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n > a.length)
      n = a.length;
    return copyOfRange(a, n, a.length);
  }

  public static byte[] dropRight(byte[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new byte[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static short[] dropRight(short[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new short[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static int[] dropRight(int[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new int[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static long[] dropRight(long[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new long[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static float[] dropRight(float[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new float[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static double[] dropRight(double[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new double[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static char[] dropRight(char[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new char[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static boolean[] dropRight(boolean[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      return new boolean[0];
    return copyOfRange(a, 0, a.length - n);
  }

  public static <T> T[] dropRight(T[] a, int n) {
    // returns an array with all elements in a except the last n
    if (a == null)
      throw new IllegalArgumentException("drop: the array can't be null");
    if (n < 0)
      throw new IllegalArgumentException("drop: cannot remove a negative number of elements from an array");
    if (n >= a.length)
      n = a.length;
    return copyOfRange(a, 0, a.length - n);
  }

  public static int[] dropWhile(int[] a, Predicate<Integer> p) {
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else
        break;
    return copyOfRange(a, bindex, a.length);
  }

  public static long[] dropWhile(long[] a, Predicate<Long> p) {
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else
        break;
    return copyOfRange(a, bindex, a.length);
  }

  public static double[] dropWhile(double[] a, Predicate<Double> p) {
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else
        break;
    return copyOfRange(a, bindex, a.length);
  }

  public static <T> T[] dropWhile(T[] a, Predicate<T> p) {
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else
        break;
    return copyOfRange(a, bindex, a.length);
  }

  public static <T> T[] empty(Class<T> c) {
    return ofDim(c, 0);
  }

  public static byte[] emptyByteArray() {
    return new byte[0];
  }

  public static short[] emptyShortArray() {
    return new short[0];
  }

  public static int[] emptyIntArray() {
    return new int[0];
  }

  public static long[] emptyLongArray() {
    return new long[0];
  }

  public static float[] emptyFloatArray() {
    return new float[0];
  }

  public static double[] emptyDoubleArray() {
    return new double[0];
  }

  public static char[] emptyCharArray() {
    return new char[0];
  }

  public static boolean[] emptyBooleanArray() {
    return new boolean[0];
  }

  public static Object[] emptyObjectArray() {
    return new Object[0];
  }

  public static boolean endsWith(int[] a, int... b) {
    // return the answer to "does a end with the values of b?"
    assert a != null;
    assert b != null;
    if (b.length > a.length)
      return false;
    if (b.length == a.length && equals(a, b))
      return true;
    int bindex = 0;
    for (int i = a.length - b.length; i < a.length; i++)
      if (!(a[i] == b[bindex++]))
        return false;
    return true;
  }

  public static boolean endsWith(double[] a, double... b) {
    // return the answer to "does a end with the values of b?"
    assert a != null;
    assert b != null;
    if (b.length > a.length)
      return false;
    if (b.length == a.length && equals(a, b))
      return true;
    int bindex = 0;
    for (int i = a.length - b.length; i < a.length; i++)
      if (!(a[i] == b[bindex++]))
        return false;
    return true;
  }

  public static boolean equals(byte[] a, byte[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(short[] a, short[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(int[] a, int[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(long[] a, long[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(float[] a, float[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(double[] a, double[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(char[] a, char[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static boolean equals(boolean[] a, boolean[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }

  public static <T> boolean equals(T[] a, T[] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++)
      if (!(a[i].equals(b[i])))
        return false;
    return true;
  }

  public static boolean equals(byte[][] a, byte[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(short[][] a, short[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(int[][] a, int[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(long[][] a, long[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(float[][] a, float[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(double[][] a, double[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(char[][] a, char[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static boolean equals(boolean[][] a, boolean[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j] != b[i][j])
          return false;
      }
    }
    return true;
  }

  public static <T> boolean equals(T[][] a, T[][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (!(a[i][j].equals(b[i][j])))
          return false;
      }
    }
    return true;
  }

  public static boolean equals(byte[][][] a, byte[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(short[][][] a, short[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(int[][][] a, int[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(long[][][] a, long[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(float[][][] a, float[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(double[][][] a, double[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(char[][][] a, char[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static boolean equals(boolean[][][] a, boolean[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k] != b[i][j][k])
            return false;
        }
      }
    }
    return true;
  }

  public static <T> boolean equals(T[][][] a, T[][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (!(a[i][j][k].equals(b[i][j][k])))
            return false;
        }
      }
    }
    return true;
  }

  public static <T> boolean equals(T[][][][] a, T[][][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length)
      return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length)
        return false;
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length)
          return false;
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k].length != b[i][j][k].length)
            return false;
          for (int l = 0; l < a[i][j][k].length; l++) {
            if (!(a[i][j][k][l].equals(b[i][j][k][l])))
              return false;
          }
        }
      }
    }
    return true;
  }

  public static <T> boolean equals(T[][][][][] a, T[][][][][] b) {
    if (a == null && b == null)
      return true;
    if (a == null && b != null || b == null && a != null)
      return false;
    if (a.length != b.length) {
      return false;
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i].length != b[i].length) {
        return false;
      }
      for (int j = 0; j < a[i].length; j++) {
        if (a[i][j].length != b[i][j].length) {
          return false;
        }
        for (int k = 0; k < a[i][j].length; k++) {
          if (a[i][j][k].length != b[i][j][k].length) {
            return false;
          }
          for (int l = 0; l < a[i][j][k].length; l++) {
            if (a[i][j][k][l].length != b[i][j][k][l].length) {
              return false;
            }
            for (int m = 0; m < a[i][j][k][l].length; m++) {
              if (!(a[i][j][k][l][m].equals(b[i][j][k][l][m]))) {
                return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

  public static boolean exists(byte[] a, Predicate<Byte> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(short[] a, Predicate<Short> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(int[] a, Predicate<Integer> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(long[] a, Predicate<Long> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(float[] a, Predicate<Float> p) {
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(double[] a, Predicate<Double> p) {
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(char[] a, Predicate<Character> p) {
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static boolean exists(boolean[] a, Predicate<Boolean> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static <R, T extends R> boolean exists(T[] a, Predicate<R> p) {
    // returns whether or not a contains an element satisfying p
    if (a == null)
      throw new IllegalArgumentException("exists: " + "the array cannot be null");
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return true;
    return false;
  }

  public static byte[] filter(byte[] a, Predicate<Byte> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    byte[] b = new byte[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static short[] filter(short[] a, Predicate<Short> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    short[] b = new short[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static int[] filter(int[] a, Predicate<Integer> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    int[] b = new int[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static long[] filter(long[] a, Predicate<Long> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    long[] b = new long[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static float[] filter(float[] a, Predicate<Float> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    float[] b = new float[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static double[] filter(double[] a, Predicate<Double> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static char[] filter(char[] a, Predicate<Character> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    char[] b = new char[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static boolean[] filter(boolean[] a, Predicate<Boolean> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    boolean[] b = new boolean[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static <R, T extends R> T[] filter(T[] a, Predicate<R> p) {
    // returns a new array containing the elements of a satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    T[] b = copyOf(a, a.length);
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static byte[] filterNot(byte[] a, Predicate<Byte> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    byte[] b = new byte[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static short[] filterNot(short[] a, Predicate<Short> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    short[] b = new short[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static int[] filterNot(int[] a, Predicate<Integer> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    int[] b = new int[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static long[] filterNot(long[] a, Predicate<Long> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    long[] b = new long[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static float[] filterNot(float[] a, Predicate<Float> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    float[] b = new float[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static double[] filterNot(double[] a, Predicate<Double> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static char[] filterNot(char[] a, Predicate<Character> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    char[] b = new char[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static boolean[] filterNot(boolean[] a, Predicate<Boolean> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    boolean[] b = new boolean[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static <R, T extends R> T[] filterNot(T[] a, Predicate<R> p) {
    // returns a new array containing the elements of a not satisfying p
    if (a == null)
      throw new IllegalArgumentException("filter: " + "the array cannot be null");
    T[] b = copyOf(a, a.length);
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static Optional<Byte> find(byte[] a, Predicate<Byte> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Short> find(short[] a, Predicate<Short> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Integer> find(int[] a, Predicate<Integer> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Long> find(long[] a, Predicate<Long> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Float> find(float[] a, Predicate<Float> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Double> find(double[] a, Predicate<Double> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Character> find(char[] a, Predicate<Character> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static Optional<Boolean> find(boolean[] a, Predicate<Boolean> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static <R, T extends R> Optional<T> find(T[] a, Predicate<R> p) {
    // returns an Optional of the first element in a satisfying p
    if (a == null || a.length == 0)
      return Optional.empty();
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return Optional.of(a[i]);
    return Optional.empty();
  }

  public static <T> Tuple2<Optional<T>, Optional<Integer>> findFirstNonNull(T[] a) {
    // returns a Tuple2 containing an Optional of the first non null element in a and
    // an Optional of its index where both Optionals are empty if all elements are null
    if (a == null || a.length == 0)
      return new Tuple2<Optional<T>, Optional<Integer>>(Optional.empty(), Optional.empty());
    for (int i = 0; i < a.length; i++)
      if (a[i] != null)
        return new Tuple2<Optional<T>, Optional<Integer>>(Optional.of(a[i]), Optional.of(i));
    return new Tuple2<Optional<T>, Optional<Integer>>(Optional.empty(), Optional.empty());
  }
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> T firstInAll(T[]...t) {
    // return the first T in all lists else returns null.
    // taking first to mean first in the only or shortest list.
    if (t == null || t.length == 0) return null;
    if (t.length == 1 && t[0].length > 0) return t[0][0];
    int len = Integer.MAX_VALUE; // length of smallest array in t
    int l = 0; // index of smallest array in t
    for (int i = 0; i < t.length; i++)
      if (t[i].length == 0) return null;
      else if (t[i].length < len) { len = t[i].length; l = i; }
    BiFunction<T[],T,Integer> search = (a, key)-> {
      int lo = 0;
      int hi = a.length - 1;
      while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if      (key.compareTo(a[mid])<0) hi = mid - 1;
        else if (key.compareTo(a[mid])>0) lo = mid + 1;
        else return mid;
      }
      return -1;
    };
    Consumer<T[]> mergesort = (z) -> {
      // natural in-place mergesort 
      if (z == null) return;
      int N = z.length; if (N < 2) return;
      @SuppressWarnings("unchecked")
      T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
      Function<T[],Queue<Integer>> getRuns = (x) -> {
        int m = x.length;
        Queue<Integer> runs = new Queue<Integer>();
        int i = 0, c= 0;
        while(i < m) {
          c = i;
          if (i < m -1) 
            while(i < m -1 && x[i].compareTo(x[i+1]) < 0) i++;
          runs.enqueue(++i - c); 
        }
        return runs;
      };
      Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) 
          b[k] = a[k];
        for (int k = lo; k <= hi; k++)
          if (i > mid) a[k] = b[j++];
          else if (j > hi ) a[k] = b[i++];
          else if (b[j].compareTo(b[i]) < 0) a[k] = b[j++];
          else a[k] = b[i++];
      }; 
      Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
      int rlen = 0; // offset in z
      while (runs.size() > 1) {
        run1 = runs.dequeue();
        // ensure one pass through z at a time with no overlap
        if (run1 + rlen == N) { runs.enqueue(run1); rlen = 0; continue; }
        run2 = runs.dequeue();
        merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
        runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
      } 
    };
    for (int i = 0; i < t.length; i++)
      if (i == l) continue;
      else mergesort.accept(t[i]);
    LOOP:
    for (int i = 0; i < len; i++) {
      for (int k = 0; k < t.length; k++)
        if (k == l) continue;
        else if (search.apply(t[k], t[l][i]) == -1) continue LOOP;
      return t[l][i];
    }
    return null;
  } 
  
  public static int firstIndexOf(int[] a, int key) {
    // return the smallest index of key in a if possible otherwise return -1
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);    
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOf(long[] a, long key) {
    // return the smallest index of key in a if possible otherwise return -1
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);    
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOf(double[] a, double key) {
    // return the smallest index of key in a if possible otherwise return -1
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);    
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOf(char[] a, char key) {
    // return the smallest index of key in a if possible otherwise return -1
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);    
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static <T extends Comparable<? super T>> int firstIndexOf(T[] a, T key) {
    // return the smallest index of key in a if possible otherwise return -1
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);    
    if (key != null && a[0] == null 
        || a[a.length-1] != null && key == null 
        || key != null && a[0] != null && key.compareTo(a[0]) < 0 
        || key != null && a[a.length-1] != null && a[a.length-1].compareTo(key) < 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) == 0) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key != null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) < 0) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  

  public static int firstIndexOf(byte[] a, int s, byte v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(short[] a, int s, short v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(int[] a, int s, int v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(long[] a, int s, long v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(float[] a, int s, float v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(double[] a, int s, double v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(char[] a, int s, char v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static int firstIndexOf(boolean[] a, int s, boolean v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s)
      return -1;
    for (int i = s; i < a.length; i++)
      if (a[i] == v)
        return i;
    return -1;
  }

  public static <T> int firstIndexOf(T[] a, int s, T v) {
    // returns the index of 1st occurrence of v in a at or 
    // after index s or -1 if v is not found and when a is
    // not necessarily sorted.
    if (a == null || a.length - 1 < s) return -1;
    for (int i = s; i < a.length; i++)
      if (v == null && a[i] == null || a[i] != null && a[i].equals(v))
        return i;
    return -1;
  }

  public static int firstIndexOfSlice(int[] a, int s, int... v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    // a is not assumed to be sorted
    if (a == null || a.length - 1 < s) return -1;
    LOOP: for (int i = s; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      return i;
    }
    return -1;
  }

  public static int firstIndexOfSlice(long[] a, int s, long... v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    // a is not assumed to be sorted
    if (a == null || a.length - 1 < s) return -1;
    LOOP: for (int i = s; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      return i;
    }
    return -1;
  }

  public static int firstIndexOfSlice(double[] a, int s, double... v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    // a is not assumed to be sorted
    if (a == null || a.length - 1 < s) return -1;
    LOOP: for (int i = s; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      return i;
    }
    return -1;
  }

  @SafeVarargs
  public static <T> int firstIndexOfSlice(T[] a, int s, T... v) {
    // returns the index of 1st occurrence of v in a 
    // at or after index s or -1 if v not found
    // a is not assumed to be sorted
    if (a == null || a.length - 1 < s) return -1;
    LOOP: for (int i = s; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      return i;
    }
    return -1;
  }
  
  public static int firstIndexOfSorted(int[] a, int key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOfSorted(long[] a, long key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOfSorted(double[] a, double key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int firstIndexOfSorted(char[] a, char key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static <T extends Comparable<? super T>> int firstIndexOfSorted(T[] a, T key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order with a nullsLast convention
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key != null && a[0] == null 
        || a[a.length-1] != null && key == null || key != null && a[0] != null
        && key.compareTo(a[0]) < 0 || key != null && a[a.length-1] != null
        && a[a.length-1].compareTo(key) < 0) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) == 0) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key != null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) < 0) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }

  private static class FlatLine {
    // this class provides a method to flatten an array to 1D
    // for any allowed dimensionality > 1 

    private final int len;
    public final Object r;
    private final String rootComponentName;
    private final boolean flatLined;
    private int rindex = 0;

    public FlatLine(Object a) {
      if (a == null)
        throw new IllegalArgumentException("flatLine constructor: " + "argument can't be null");
      if (!a.getClass().isArray())
        throw new IllegalArgumentException("flatLine " + "constructor : argument must be an array");
      long numel = numel(a);
      len = numel <= Integer.MAX_VALUE ? (int) numel : Integer.MAX_VALUE;
      r = Array.newInstance(rootComponentType(a), len);
      rootComponentName = rootComponentName(a);
      flatLined = dim(a) < 2 ? true : false;
    }

    public Object flatLine(Object a) {
      if (flatLined)
        return ArrayUtils.clone(a);
      int dim = dim(a);
      int alen = Array.getLength(a);
      int tlen = 0;

      if (dim == 1) { // base case of recursion
        switch (rootComponentName) {
        case "byte": {
          byte[] b = (byte[]) a;
          byte[] s = (byte[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "short": {
          short[] b = (short[]) a;
          short[] s = (short[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "int": {
          int[] b = (int[]) a;
          int[] s = (int[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "long": {
          long[] b = (long[]) a;
          long[] s = (long[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "float": {
          float[] b = (float[]) a;
          float[] s = (float[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "double": {
          double[] b = (double[]) a;
          double[] s = (double[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "char": {
          char[] b = (char[]) a;
          char[] s = (char[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        case "boolean": {
          boolean[] b = (boolean[]) a;
          boolean[] s = (boolean[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
          break;
        }
        default: {
          Object[] b = (Object[]) a;
          Object[] s = (Object[]) r;
          tlen = rindex + alen > len ? len - rindex : alen;
          for (int i = 0; i < tlen; i++)
            s[rindex++] = b[i];
        }
        }
      } else {
        for (int i = 0; i < alen; i++)
          flatLine(Array.get(a, i));
      }
      return r;
    }
  }

  public static Object flatLine(Object a) {
    // a is an array with dimensionality == 1 return a clone of
    // it, else if its dimensionality > 1 flatten it to a new ID
    // array and return that.
    return (new FlatLine(a)).flatLine(a);
  }

  public static <T, R> R[] flatMap(T[] a, F1<T, R[]> f) {
    if (a == null || a.length == 0 || allNull(a))
      throw new IllegalArgumentException(
          "flatMap: the array cannot be null, have length of 0 or have all null elements");
    Tuple2<Optional<T>, Optional<Integer>> nonNullT = findFirstNonNull(a);
    R[] r = copyOf(f.apply(nonNullT._1.get()), 0);
    for (T t : a)
      r = append(r, f.apply(t));
    return r;
  }

  public static Object flatten(Object a) {
    // flattens an array of arrays of X to an array of X 
    // here is a scala flatten demo to show how flatten should work:
    // scala> a7
    // res1: Array[Array[Array[Int]]] = 
    //     Array(Array(Array(1, 2), Array(3, 4)), Array(Array(5, 6), Array(7, 8)))
    // scala> a7 flatten
    // res2: Array[Array[Int]] = 
    //     Array(Array(1, 2), Array(3, 4), Array(5, 6), Array(7, 8))
    // in other words flatten takes an array of arrays of X and flattens it to an 
    // array of X where X can be a non-array or an array of any allowed dimensionality
    // (up to 254 since its enclosed in an array). flatten does not create a 1D array 
    // of rootComponentName unless the original array is 2D. 
    // for always creating a 1D array from an array of any dimensionality, use the 
    // flatline method.
    if (a == null)
      throw new IllegalArgumentException("flatten: " + "argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("flatten: " + "argument must be an array");
    int dim = dim(a);
    if (dim < 2)
      throw new IllegalArgumentException("flatten: " + "argument must be an array with dimension >= 2");

    if (dim == 2 && isPrimitiveArray(a)) {
      // special case since only for 2D arrays with primitive rootComponentName
      // a.getClass().getComponentType().getComponentType() isn't an object
      String rct = rootComponentName(a);
      switch (rct) {
      case "byte": {
        byte[][] b = (byte[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        byte[] r = new byte[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "short": {
        short[][] b = (short[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        short[] r = new short[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "int": {
        int[][] b = (int[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        int[] r = new int[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "long": {
        long[][] b = (long[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        long[] r = new long[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "float": {
        float[][] b = (float[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        float[] r = new float[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "double": {
        double[][] b = (double[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        double[] r = new double[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "char": {
        char[][] b = (char[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        char[] r = new char[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      case "boolean": {
        boolean[][] b = (boolean[][]) a;
        long rlen = 0;
        for (int i = 0; i < b.length; i++)
          rlen += b[i].length;
        if (rlen > maxArrayLength)
          throw new ArrayIndexOutOfBoundsException(
              "flatten: " + "flattened array if produced would exceed max allowed array length");
        boolean[] r = new boolean[(int) rlen];
        int rindex = 0;
        for (int i = 0; i < b.length; i++)
          for (int j = 0; j < b[i].length; j++)
            r[rindex++] = b[i][j];
        return r;
      }
      default:
        throw new NoSuchElementException("flatten: invalid primitive " + "type " + rct);
      }
    }

    int alen = Array.getLength(a);
    int[] acollen = new int[alen];
    for (int i = 0; i < alen; i++)
      acollen[i] = Array.getLength(Array.get(a, i));
    long rlen = sum(acollen);
    if (rlen > maxArrayLength)
      throw new ArrayIndexOutOfBoundsException(
          "flatten2: " + "flattened array if produced would exceed max allowed array length");
    Object[] r = (Object[]) Array.newInstance(a.getClass().getComponentType().getComponentType(), (int) rlen);
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
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    int r = op.applyAsInt(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsInt(r, a[i]);
    return r;
  }

  public static long foldLeft(long[] a, LongBinaryOperator op, long z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    long r = op.applyAsLong(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsLong(r, a[i]);
    return r;
  }

  public static double foldLeft(double[] a, DoubleBinaryOperator op, double z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    double r = op.applyAsDouble(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.applyAsDouble(r, a[i]);
    return r;
  }

  public static <T> T foldLeft(T[] a, BinaryOperator<T> op, T z) {
    // this folds from the left
    // z is left identity element for op, i.e. op(z,e) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    T r = op.apply(z, a[0]);
    for (int i = 1; i < a.length; i++)
      r = op.apply(r, a[i]);
    return r;
  }

  public static int foldRight(int[] a, IntBinaryOperator op, int z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    int r = op.applyAsInt(a[a.length - 1], z);
    for (int i = a.length - 2; i > -1; i--)
      r = op.applyAsInt(a[i], r);
    return r;
  }

  public static long foldRight(long[] a, LongBinaryOperator op, long z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    long r = op.applyAsLong(a[a.length - 1], z);
    for (int i = a.length - 2; i > -1; i--)
      r = op.applyAsLong(a[i], r);
    return r;
  }

  public static double foldRight(double[] a, DoubleBinaryOperator op, double z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null)
      throw new IllegalArgumentException("foldLeft: " + "argument can't be null");
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    double r = op.applyAsDouble(a[a.length - 1], z);
    for (int i = a.length - 2; i > -1; i--)
      r = op.applyAsDouble(a[i], r);
    return r;
  }

  public static <T> T foldRight(T[] a, BinaryOperator<T> op, T z) {
    // this folds from the right
    // z is right identity element for op, i.e. op(e,z) = e;
    // e.g. if op is addition z=0, if op is multiplication z=1.
    if (a == null || op == null)
      throw new IllegalArgumentException("foldLeft: " + "none of the arguments can be null");
    if (a.length < 1)
      throw new IllegalArgumentException("foldLeft: " + "argument must have a length > 0");
    T r = op.apply(a[a.length - 1], z);
    for (int i = a.length - 2; i > -1; i--)
      r = op.apply(a[i], r);
    return r;
  }

  public static boolean forAll(int[] a, Predicate<Integer> p) {
    // returns true if p is true for all elements of a else false
    if (a == null)
      throw new IllegalArgumentException("foldLeft: " + "argument can't be null");
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        return false;
    return true;
  }

  public static boolean forAll(long[] a, Predicate<Long> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        return false;
    return true;
  }

  public static boolean forAll(double[] a, Predicate<Double> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        return false;
    return true;
  }

  public static <T> boolean forAll(T[] a, Predicate<T> p) {
    // returns true if p is true for all elements of a else false
    assert a != null;
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        return false;
    return true;
  }

  public static void forEach(int[] a, IntConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++)
      ic.accept(a[i]);
  }

  public static void forEach(long[] a, LongConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++)
      ic.accept(a[i]);
  }

  public static void forEach(double[] a, DoubleConsumer ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++)
      ic.accept(a[i]);
  }

  public static <T> void forEach(T[] a, Consumer<T> ic) {
    // ic operates by creating side effects so it can do things like
    // (x) -> System.out.println(x);
    assert a != null;
    for (int i = 0; i < a.length; i++)
      ic.accept(a[i]);
  }

  public static Object fromList(List<?> list) {
    // returns an array corresponding (isomorphic) to list
    // and with the same elements (data)
    if (list == null)
      throw new IllegalArgumentException("fromList " + "the list cannot be null");

    // determine nesting depth and rootElementType of the list
    int dim = 0; // nesting depth
    Class<?> c = null; // rootElementType
    boolean foundNonNull;
    Object o = list;
    while (true) {
      foundNonNull = false;
      if (o instanceof List) {
        dim++;
      } else {
        c = o.getClass();
        break;
      }
      for (Object e : (List<?>) o) {
        if (e != null) {
          o = e;
          foundNonNull = true;
          break;
        }
      }
      if (!foundNonNull)
        throw new NonTerminalNestingLevelWithAllNullElementsException(
            "fromList: cannot determine nesting depth or rootElementType of the list");
    }

    if (dim > 255)
      throw new ListNestingTooHighException(
          "fromList: list nesting " + "over 255 levels isn't supported since that's the limit on array dimensionality");

    // dimensions array for Array.newInstance
    int[] dimensions = new int[dim];
    dimensions[0] = list.size();

    Object a = Array.newInstance(c, dimensions); // to be the output array

    int i = 0;
    Object[] b = (Object[]) a;

    if (dim == 1) { // recursion termination condition
      for (Object e : list)
        b[i++] = e;
    } else {
      for (Object e : list)
        b[i++] = fromList((List<?>) e);
    }

    return a;
  }
  
  public static int fuzzyIndexOf(double[] z, double key, double t) {
    // return the index of any element of z within t of key or -1 if no such element
    // is found given that z is sorted in ascending order. t may be specified safely 
    // with 10**-10 precision (on my platform but I can't guarantee that on all, however
    // it could be better on some). t is made positive if given negative and accomodates
    // positive and negative variations around key. this algorithm is the same as regular 
    // binary search except when mid has been selected for output it's filtered for 
    // inclusion in [z[mid] - abs(t), z[mid] + abs(t)] or -1 is returned.
    if (z == null || z.length == 0) return -1;
    if (t < 0) t = -t;
    double fudge = 0.00000000001; //fudge factor for double arithmetic errors
    int lo = 0;
    int hi = z.length - 1;
    while (lo <= hi) { 
      int mid = lo + (hi - lo) / 2;
      if (key < z[mid]-t) {
        hi = mid - 1;
      } else if (key > z[mid]+t) {
        lo = mid + 1;
      } else {
        double dif = key - z[mid];
        if (dif < 0) dif = -dif;
        return dif <= t+fudge ? mid : -1;
      }
    }
    return -1;
  }


  public static Method getCloneMethod(Class<?> c) {
    // returns c.clone() method if it exists else null.
    // helper for clone(Object) method
    if (c == null)
      throw new IllegalArgumentException("getCloneMethod: " + "argument can't be null");

    Method clone = null;

    try {
      clone = c.getDeclaredMethod("clone");
      return clone;
    } catch (NoSuchMethodException | SecurityException e) {
    }

    try {
      clone = c.getMethod("clone");
      return clone;
    } catch (NoSuchMethodException | SecurityException e) {
    }

    return null;
  }

  public static <K> Map<K, int[]> groupBy(int[] a, Function<Integer, K> f) {
    Map<K, int[]> m = new HashMap<>();
    K k;
    int[] t;
    int[] t2;
    for (int i = 0; i < a.length; i++) {
      k = f.apply(a[i]);
      if (m.containsKey(k)) {
        t = m.get(k);
        t2 = append(t, a[i]);
        m.replace(k, t2);
      } else {
        t = new int[] { a[i] };
        m.put(k, t);
      }
    }
    return m;
  }

  public static <K> Map<K, long[]> groupBy(long[] a, Function<Long, K> f) {
    Map<K, long[]> m = new HashMap<>();
    K k;
    long[] t;
    long[] t2;
    for (int i = 0; i < a.length; i++) {
      k = f.apply(a[i]);
      if (m.containsKey(k)) {
        t = m.get(k);
        t2 = append(t, a[i]);
        m.replace(k, t2);
      } else {
        t = new long[] { a[i] };
        m.put(k, t);
      }
    }
    return m;
  }

  public static <K> Map<K, double[]> groupBy(double[] a, Function<Double, K> f) {
    Map<K, double[]> m = new HashMap<>();
    K k;
    double[] t;
    double[] t2;
    for (int i = 0; i < a.length; i++) {
      k = f.apply(a[i]);
      if (m.containsKey(k)) {
        t = m.get(k);
        t2 = append(t, a[i]);
        m.replace(k, t2);
      } else {
        t = new double[] { a[i] };
        m.put(k, t);
      }
    }
    return m;
  }

  public static <T, K> Map<K, T[]> groupBy(T[] a, Function<T, K> f) {
    Map<K, T[]> m = new HashMap<>();
    K k;
    T[] t;
    T[] t2;
    for (int i = 0; i < a.length; i++) {
      k = f.apply(a[i]);
      if (m.containsKey(k)) {
        t = m.get(k);
        t2 = append(t, a[i]);
        m.replace(k, t2);
      } else {
        t = copyOf(a, 1);
        t[0] = a[i];
        m.put(k, t);
      }
    }
    return m;
  }
  
  public static <T> boolean hasNull(T[] z) {
    // returns true if z has a null element else return false
    if (z == null) throw new IllegalArgumentException("hasNull: arg can't be null");
    for (int i = 0; i < z.length; i++) if (z[i] == null) return true;
    return false;
  }

  public static int head(int[] a) {
    // returns the 1st element of a
    assert a != null;
    assert a.length > 0;
    return a[0];
  }

  public static long head(long[] a) {
    // returns the 1st element of a
    assert a != null;
    assert a.length > 0;
    return a[0];
  }

  public static double head(double[] a) {
    // returns the 1st element of a
    assert a != null;
    assert a.length > 0;
    return a[0];
  }

  public static <T> T head(T[] a) {
    // returns the 1st element of a
    assert a != null;
    assert a.length > 0;
    return a[0];
  }

  public static <T> Optional<T> headOption(T[] a) {
    // if a[0]== null returns Optional.empty() else returns Optional.of(a[0])
    assert a != null;
    assert a.length > 0;
    return Optional.ofNullable(a[0]);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> int highestIndexOfEqualOrLess(T z[], T key) {
    // return the highest index i in z such that z[i].compareTo(key) <= 0,
    // else if all elements in z are greater than key return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    // assumes z[] contains no null elements and T extends Comparable.
    if (z == null || z.length == 0 || z[0] == null) return -1;
    if (!Comparable.class.isAssignableFrom(z[0].getClass())) 
      throw new IllegalArgumentException("type T must extend Comparable");
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0, mid = 0, i = 0, ret = 0;
    while(fib[i] < n) i++;
    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || ((Comparable<T>)key).compareTo(z[mid])<0) continue;
      else if (((Comparable<T>)key).compareTo(z[mid])>0) { ret = mid; low = mid+1; i--; }
      else {
        while(mid < n-1 && ((Comparable<T>)z[mid]).compareTo(z[mid+1])==0) mid++;
        return mid;
      }
    }
    if (ret == 0) { return ((Comparable<T>)z[0]).compareTo(key)< 0 ? 0 : -1; }
    else return ret;
  }
  
  public static <T> int highestIndexOfEqualOrLess(T z[], T key, Comparator<T> c) {
    // return the highest index i in z such that c.compare(z[i],key) <= 0,
    // else if all elements in z are greater than key return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0, mid = 0, i = 0, ret = 0;
    while(fib[i] < n) i++;
    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || c.compare(key,z[mid])<0) continue;
      else if (c.compare(key,z[mid])>0) { ret = mid; low = mid+1; i--; }
      else {
        while(mid < n-1 && c.compare(z[mid],z[mid+1])==0) mid++;
        return mid;
      }
    }
    if (ret == 0) { return c.compare(z[0],key)< 0 ? 0 : -1; }
    else return ret;
  }
  
  public static int howManyOfSorted(int[] a, int key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static int howManyOfSorted(long[] a, long key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static int howManyOfSorted(double[] a, double key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }

  public static int howManyOfSorted(char[] a, char key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static <T extends Comparable<? super T>> int howManyOfSorted(T[] a, T key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key != null && a[0] == null 
        || a[a.length-1] != null && key == null || key != null && a[0] != null
        && key.compareTo(a[0]) < 0 || key != null && a[a.length-1] != null
        && a[a.length-1].compareTo(key) < 0) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static int howManyOf(int[] a, int key) {
    // return the number of occurrences of key in a
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time (due to sort)
    if (a == null || a.length == 0) return 0;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static int howManyOf(long[] a, long key) {
    // return the number of occurrences of key in a
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time (due to sort)
    if (a == null || a.length == 0) return 0;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static int howManyOf(double[] a, double key) {
    // return the number of occurrences of key in a
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time (due to sort)
    if (a == null || a.length == 0) return 0;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }

  public static int howManyOf(char[] a, char key) {
    // return the number of occurrences of key in a
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time (due to sort)
    if (a == null || a.length == 0) return 0;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static <T extends Comparable<? super T>> int howManyOf(T[] a, T key) {
    // return the number of occurrences of key in a
    // does not assume a has been sorted
    // O(a.length log a.length) worst case running time (due to sort)
    if (a == null || a.length == 0) return 0;
    Arrays.sort(a);
    if( key != null && a[0] == null 
        || a[a.length-1] != null && key == null 
        || key != null && a[0] != null && key.compareTo(a[0]) < 0 
        || key != null && a[a.length-1] != null && a[a.length-1].compareTo(key) < 0)
      return 0;
    int s = firstIndexOfSorted(a, key);
    if (s == -1) return 0;
    else return lastIndexOfSorted(a, key) - s + 1;
  }
  
  public static <T> int[] identityHashCodes(T[] z) {
    // return an int[] with the hashcodes of the elements in z.
    if (z == null) throw new IllegalArgumentException("toIdentityHashCodes: z can't be null");
    int[] a = new int[z.length];
    for (int i = 0; i < z.length; i++) a[i] = identityHashCode(z[i]);
    return a;
  }

  public static boolean in(int[] a, int v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(byte[] a, byte v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(short[] a, short v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(long[] a, long v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(float[] a, float v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(double[] a, double v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(char[] a, char v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static boolean in(boolean[] a, boolean v) {
    for (int i = 0; i < a.length; i++)
      if (a[i] == v)
        return true;
    return false;
  }

  public static <T> boolean in(T[] a, T v) {
    for (int i = 0; i < a.length; i++)
      if (v == null || a[i] == null) {
        if (v == a[i])
          return true;
      } else if (a[i].equals(v))
        return true;
    return false;
  }
  
  public static boolean in(int[][] a, int[] v) {
    for (int i = 0; i < a.length; i++)
      if (v == null || a[i] == null) {
        if (v == a[i])
          return true;
      } else if (Arrays.equals(a[i], v))
        return true;
    return false;
  }
  
  public static <T> boolean in(T[][] a, T[] v) {
    for (int i = 0; i < a.length; i++)
      if (v == null || a[i] == null) {
        if (v == a[i])
          return true;
      } else if (Arrays.equals(a[i], v))
        return true;
    return false;
  }

  // from BinarySearch.java
  public static int indexOf(int[] a, int key) {
    // assumes a is sorted
    if (a == null || a.length == 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid])
        hi = mid - 1;
      else if (key > a[mid])
        lo = mid + 1;
      else
        return mid;
    }
    return -1;
  }

  public static int indexOf(long[] a, long key) {
    // assumes a is sorted
    if (a == null || a.length == 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid])
        hi = mid - 1;
      else if (key > a[mid])
        lo = mid + 1;
      else
        return mid;
    }
    return -1;
  }

  public static int indexOf(double[] a, double key) {
    // assumes a is sorted
    if (a == null || a.length == 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid])
        hi = mid - 1;
      else if (key > a[mid])
        lo = mid + 1;
      else
        return mid;
    }
    return -1;
  }

  public static int indexOf(char[] a, char key) {
    // assumes a is sorted
    if (a == null || a.length == 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid])
        hi = mid - 1;
      else if (key > a[mid])
        lo = mid + 1;
      else
        return mid;
    }
    return -1;
  }

  public static <T extends Comparable<? super T>> int indexOf(T[] a, T key) {
    // assumes a is sorted
    if (a == null || a.length == 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key.compareTo(a[mid]) < 0)
        hi = mid - 1;
      else if (key.compareTo(a[mid]) > 0)
        lo = mid + 1;
      else
        return mid;
    }
    return -1;
  }
  
  public static int indexOfMax(int[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMax: " 
          + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    int max = Integer.MIN_VALUE;
    int index = 0;
    for (int i = a.length-1; i > -1 ; i--)
      if (a[i] > max) {
        max = a[i];
        index = i;
      }
    return index;
  }
  
  public static int indexOfMax(long[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMax: " 
          + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    long max = Long.MIN_VALUE;
    int index = 0;
    for (int i = a.length-1; i > -1 ; i--)
      if (a[i] > max) {
        max = a[i];
        index = i;
      }
    return index;
  }
  
  public static int indexOfMax(double[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMax: " 
          + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    double max = Double.MIN_VALUE;
    int index = 0;
    for (int i = a.length-1; i > -1 ; i--)
      if (a[i] > max) {
        max = a[i];
        index = i;
      }
    return index;
  }
  
  public static <T extends Comparable<? super T>> int indexOfMax(T[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMax: " 
          + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    T max = a[a.length-1];
    int index = 0;
    for (int i = a.length-2; i > -1 ; i--)
      if (a[i].compareTo(max)>0) {
        max = a[i];
        index = i;
      }
    return index;
  }
  
  public static <T> int indexOfMax(T[] a, Comparator<T> c) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMax: " 
          + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    T max = a[a.length-1];
    int index = 0;
    for (int i = a.length-2; i > -1 ; i--)
      if (c.compare(a[i],max)>0) {
        max = a[i];
        index = i;
      }
    return index;
  }
  
  public static int indexOfMin(int[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMin: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    int min = Integer.MAX_VALUE;
    int index = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min) {
        min = a[i];
        index = i;
      }
    return index;
  }
  
  public static int indexOfMin(long[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMin: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    long min = Long.MAX_VALUE;
    int index = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min) {
        min = a[i];
        index = i;
      }
    return index;
  }
  
  public static int indexOfMin(double[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMin: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    double min = Double.MAX_VALUE;
    int index = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min) {
        min = a[i];
        index = i;
      }
    return index;
  }
  
  public static <T extends Comparable<? super T>> int indexOfMin(T[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMin: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    T min = a[0];
    int index = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i].compareTo(min)<0) {
        min = a[i];
        index = i;
      }
    return index;
  }
  
  public static <T> int indexOfMin(T[] a, Comparator<T> c) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("indexOfMin: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return 0;
    T min = a[0];
    int index = 0;
    for (int i = 0; i < a.length; i++)
      if (c.compare(a[i],min)<0) {
        min = a[i];
        index = i;
      }
    return index;
  }

  public static int indexWhere(int[] a, Predicate<Integer> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return i;
    return -1;
  }

  public static int indexWhere(long[] a, Predicate<Long> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return i;
    return -1;
  }

  public static int indexWhere(double[] a, Predicate<Double> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return i;
    return -1;
  }

  public static <T> int indexWhere(T[] a, Predicate<T> p) {
    //return index of 1st element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        return i;
    return -1;
  }

  public static int[] indices(int[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++)
      b[i] = i;
    return b;
  }

  public static int[] indices(long[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++)
      b[i] = i;
    return b;
  }

  public static int[] indices(double[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++)
      b[i] = i;
    return b;
  }

  public static <T> int[] indices(T[] a) {
    assert a != null;
    //return an array with the indices of a
    int[] b = new int[a.length];
    for (int i = 0; i < b.length; i++)
      b[i] = i;
    return b;
  }

  public static int[] init(int[] a) {
    // returns an array with all elements of a except the last
    if (a == null)
      throw new IllegalArgumentException("init: the array cannot be be null");
    return take(a, a.length - 1);
  }

  public static long[] init(long[] a) {
    // returns an array with all elements of a except the last
    if (a == null)
      throw new IllegalArgumentException("init: the array cannot be be null");
    return take(a, a.length - 1);
  }

  public static double[] init(double[] a) {
    // returns an array with all elements of a except the last
    if (a == null)
      throw new IllegalArgumentException("init: the array cannot be be null");
    return take(a, a.length - 1);
  }

  public static <T> T[] init(T[] a) {
    // returns an array with all elements of a except the last
    if (a == null)
      throw new IllegalArgumentException("init: the array cannot be be null");
    return take(a, a.length - 1);
  }
  
  public static int[] intersect(int[] a, int[] b) {
    // return an array containing the elements of the intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new int[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    int[] c = a.length <= b.length ? new int[a.length] : new int[b.length];
    int k = 0; //counter for c
    LOOP:
    while( i < a.length-1 && j < b.length-1) {
      //skip duplicates in a
      while(i < a.length-1) {
        if (a[i] == a[i+1]) {
          i++;
        } else break;
      }
      //skip duplicates in b
      while(j < b.length-1) {
        if (b[j] == b[j+1]) {
          j++;
        } else break;
      }
      if (a[i] == b[j]) {
        c[k++] = a[i];
        i++; j++;
        continue;
      } else if (a[i] < b[j]) {
        while(i < a.length-1) {
          if (a[++i] == b[j]) {
            c[k++] = a[i];
            i++; j++;
            continue LOOP;
          } else if (a[i] > b[j]) {
            j++;
            continue LOOP;
          }
        }
      } else if (b[j] < a[i]) {
        while(j < b.length-1) {
          if (b[++j] == a[i]) {
            c[k++] = b[j];
            i++; j++;
            continue LOOP;
          } else if (b[j] > a[i]) {
            i++;
            continue LOOP;
          }
        }
      }
    }
    return take(c, k);
  }
  
  public static long[] intersect(long[] a, long[] b) {
    // return an array containing the elements of the intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new long[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    long[] c = a.length <= b.length ? new long[a.length] : new long[b.length];
    int k = 0; //counter for c
    LOOP:
    while( i < a.length-1 && j < b.length-1) {
      //skip duplicates in a
      while(i < a.length-1) {
        if (a[i] == a[i+1]) {
          i++;
        } else break;
      }
      //skip duplicates in b
      while(j < b.length-1) {
        if (b[j] == b[j+1]) {
          j++;
        } else break;
      }
      if (a[i] == b[j]) {
        c[k++] = a[i];
        i++; j++;
        continue;
      } else if (a[i] < b[j]) {
        while(i < a.length-1) {
          if (a[++i] == b[j]) {
            c[k++] = a[i];
            i++; j++;
            continue LOOP;
          } else if (a[i] > b[j]) {
            j++;
            continue LOOP;
          }
        }
      } else if (b[j] < a[i]) {
        while(j < b.length-1) {
          if (b[++j] == a[i]) {
            c[k++] = b[j];
            i++; j++;
            continue LOOP;
          } else if (b[j] > a[i]) {
            i++;
            continue LOOP;
          }
        }
      }
    }
    return take(c, k);
  }
  
  public static double[] intersect(double[] a, double[] b) {
    // return an array containing the elements of the intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new double[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    double[] c = a.length <= b.length ? new double[a.length] : new double[b.length];
    int k = 0; //counter for c
    LOOP:
    while( i < a.length-1 && j < b.length-1) {
      //skip duplicates in a
      while(i < a.length-1) {
        if (a[i] == a[i+1]) {
          i++;
        } else break;
      }
      //skip duplicates in b
      while(j < b.length-1) {
        if (b[j] == b[j+1]) {
          j++;
        } else break;
      }
      if (a[i] == b[j]) {
        c[k++] = a[i];
        i++; j++;
        continue;
      } else if (a[i] < b[j]) {
        while(i < a.length-1) {
          if (a[++i] == b[j]) {
            c[k++] = a[i];
            i++; j++;
            continue LOOP;
          } else if (a[i] > b[j]) {
            j++;
            continue LOOP;
          }
        }
      } else if (b[j] < a[i]) {
        while(j < b.length-1) {
          if (b[++j] == a[i]) {
            c[k++] = b[j];
            i++; j++;
            continue LOOP;
          } else if (b[j] > a[i]) {
            i++;
            continue LOOP;
          }
        }
      }
    }
    return take(c, k);
  }
  
  public static <T extends Comparable<? super T>> T[] intersect(T[] a, T[] b) {
    // return an array containing the elements of the intersection
    // of a and b, exluding nulls. a and b are not assumed to be sorted,
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || b == null) throw new IllegalArgumentException("intersectMultiset: "
        + "neither array argument can be null");
    if (a.length == 0 || b.length == 0) return copyOf(a, 0);
    Arrays.sort(a, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    Arrays.sort(b, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    int i = 0; //counter for a
    int j = 0; //counter for b
    T[] c = a.length <= b.length ? ofDim(a.getClass().getComponentType(), a.length) 
        : ofDim(b.getClass().getComponentType(), b.length);
    int k = 0; //counter for c
    LOOP:
    while( i < a.length-1 && j < b.length-1) {
      if (a[i] == null || b[i] == null) break;
      //skip duplicates in a up to the first null
      while(i < a.length-1) {
        if (a[i+1] == null) break;
        if (a[i].compareTo(a[i+1]) == 0) {
          i++;
        } else break;
      }
      //skip duplicates in b up to the first null
      while(j < b.length-1) {
        if (b[i+1] == null) break;
        if (b[j].compareTo(b[j+1]) == 0) {
          j++;
        } else break;
      }
      if (a[i].compareTo(b[j]) == 0) {
        c[k++] = a[i];
        i++; j++;
        continue;
      } else if (a[i].compareTo(b[j]) < 0) {
        while(i < a.length-1) {
          if (a[++i] == null) break LOOP;
          if (a[i].compareTo(b[j]) == 0) {
            c[k++] = a[i];
            i++; j++;
            continue LOOP;
          } else if (a[i].compareTo(b[j]) > 0) {
            j++;
            continue LOOP;
          }
        }
      } else if (b[j].compareTo(a[i]) < 0) {
        while(j < b.length-1) {
          if (b[++j] == null) break LOOP;
          if (b[j].compareTo(a[i]) == 0) {
            c[k++] = b[j];
            i++; j++;
            continue LOOP;
          } else if (b[j].compareTo(a[i]) > 0) {
            i++;
            continue LOOP;
          }
        }
      }
    }
    return take(c, k);
  }

  public static int[] intersectSet(int[] a, int[] b) {
    // returns a sorted array containing the intersection
    // of a and b with no element repeated.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new int[] {};
    Set<Integer> set = toSet(a);
    set.retainAll(toSet(b));
    int[] c = (int[]) unbox(set.toArray(new Integer[] {}));
    Arrays.sort(c);
    return c;
  }

  public static long[] intersectSet(long[] a, long[] b) {
    // returns a sorted array containing the intersection
    // of a and b with no element repeated.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new long[] {};
    Set<Long> set = toSet(a);
    set.retainAll(toSet(b));
    long[] c = (long[]) unbox(set.toArray(new Long[] {}));
    Arrays.sort(c);
    return c;
  }

  public static double[] intersectSet(double[] a, double[] b) {
    // returns a sorted array containing the intersection
    // of a and b with no element repeated.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new double[] {};
    Set<Double> set = toSet(a);
    set.retainAll(toSet(b));
    double[] c = (double[]) unbox(set.toArray(new Long[] {}));
    Arrays.sort(c);
    return c;
  }

  public static <T> T[] intersectSet(T[] a, T[] b) {
    // returns an array containing the intersection
    // of a and b with no element repeated.
    if (a == null && b == null)
      throw new IllegalArgumentException("intersect: both array arguments can't be null");
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return a == null ? copyOf(b, 0) : copyOf(a, 0);
    if (a.length == 0 || b.length == 0)
      return null;
    Set<T> set = toSet(a);
    set.retainAll(toSet(b));
    return set.toArray(copyOf(a, 0));
  }
  
  public static int[] intersectMultiset(int[] a, int[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays.
    if (a == null || a.length == 0 || b == null || b.length == 0) return new int[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    int[] c = a.length <= b.length ? new int[a.length] : new int[b.length];
    int k = 0; //counter for c
    //    if (b.length > a.length) {int[] t = a; a = b; b = t;}
    LOOP:
      while(i < a.length -1 && j < b.length-1) {
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  public static long[] intersectMultiset(long[] a, long[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays.
    if (a == null || a.length == 0 || b == null || b.length == 0) return new long[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    long[] c = a.length <= b.length ? new long[a.length] : new long[b.length];
    int k = 0; //counter for c
    //    if (b.length > a.length) {int[] t = a; a = b; b = t;}
    LOOP:
      while(i < a.length -1 && j < b.length-1) {
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  public static double[] intersectMultiset(double[] a, double[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new double[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    double[] c = a.length <= b.length ? new double[a.length] : new double[b.length];
    int k = 0; //counter for c
    //    if (b.length > a.length) {int[] t = a; a = b; b = t;}
    LOOP:
      while(i < a.length -1 && j < b.length-1) {
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  public static char[] intersectMultiset(char[] a, char[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, which are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays
    if (a == null || a.length == 0 || b == null || b.length == 0) return new char[0];
    Arrays.sort(a);
    Arrays.sort(b);
    int i = 0; //counter for a
    int j = 0; //counter for b
    char[] c = a.length <= b.length ? new char[a.length] : new char[b.length];
    int k = 0; //counter for c
    //    if (b.length > a.length) {int[] t = a; a = b; b = t;}
    LOOP:
      while(i < a.length -1 && j < b.length-1) {
        if (a[i] == b[j]) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i] < b[j]) {
          while(i < a.length-1) {
            if (a[++i] == b[j]) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i] > b[j]) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j] < a[i]) {
          while(j < b.length-1) {
            if (b[++j] == a[i]) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j] > a[i]) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    if (i == a.length-1) {
      for (; j < b.length; j++)
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
    } else if (j == b.length-1) {
      for (; i < a.length; i++)
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
    }
    return take(c, k);
  }
  
  public static <T extends Comparable<? super T>> T[] intersectMultiset(T[] a, T[] b) {
    // return an array containing the elements of the multiset intersection
    // of a and b, exluding nulls. a and b are not assumed to be sorted.
    // O(N log N) worst case running time due to sorting the arrays.
    if (a == null || b == null) throw new IllegalArgumentException("intersectMultiset: "
        + "neither array argument can be null");
    if (a.length == 0 || b.length == 0) return copyOf(a, 0);
    // to allow inclusion of nulls it's necessary to use a null friendly Comparator
    // i.e. one that doesn't throw a NullPointerException from Comparator.compare
    Arrays.sort(a, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    Arrays.sort(b, Comparator.nullsLast((x,y) -> x.compareTo(y)));
    int i = 0; //counter for a
    int j = 0; //counter for b
    T[] c = a.length <= b.length ? ofDim(a.getClass().getComponentType(), a.length) 
        : ofDim(b.getClass().getComponentType(), b.length);
    //    if (a.length > b.length) {T[] t = a; a = b; b = t;}
    int k = 0; //counter for c
    LOOP:
      while(i < a.length-1 && j < b.length-1) {
        //      if (a[i] == null || b[j] == null) break;
        // skip nulls in both arrays until a[i] != null && b[j] != null
        if (a[i] == null && b[j] == null) {
          i++; j++; continue;
        } else if (a[i] == null) {
          i++; continue;
        } else if (b[j] == null ) {
          j++; continue;
        }
        // compare non null elements 
        if (a[i].compareTo(b[j]) == 0) {
          c[k++] = a[i];
          i++; j++;
          continue;
        } else if (a[i].compareTo(b[j]) < 0) {
          while(i < a.length-1) {
            if (a[++i] == null) continue LOOP;
            if (a[i].compareTo(b[j]) == 0) {
              c[k++] = a[i];
              i++; j++;
              continue LOOP;
            } else if (a[i].compareTo(b[j]) > 0) {
              j++;
              continue LOOP;
            }
          }
        } else if (b[j].compareTo(a[i]) < 0) {
          while(j < b.length-1) {
            if (b[++j] == null) continue LOOP;
            if (b[j].compareTo(a[i]) == 0) {
              c[k++] = b[j];
              i++; j++;
              continue LOOP;
            } else if (b[j].compareTo(a[i]) > 0) {
              i++;
              continue LOOP;
            }
          }
        }
      }
    // finish up last element comparison
    if (i == a.length-1 && a[i] != null) {
      for (; j < b.length; j++) {
        if (b[j] == null) continue;
        if (b[j] == a[i]) {
          c[k++] = a[i];
          break;
        }
      }
    } else if (j == b.length-1 && b[j] != null) {
      for (; i < a.length; i++) {
        if (a[i] == null) continue;
        if (a[i] == b[j]) {
          c[k++] = b[j];
          break;
        }
      }
    }
    return take(c, k);
  }

  public static int[] intersectMultisetMapped(int[] a, int[] b) {
    // return an int[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection regardless of 
    // how many times it occurs in a.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new int[] {};
    Map<Integer, Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      mapa.merge(a[i], 1, Integer::sum);
    Map<Integer, Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    int[] c = new int[len];
    int cindex = 0;
    Integer ia;
    Integer ib;
    int count;
    for (Integer e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e);
        ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++)
          c[cindex++] = e;
      }
    }
    int[] d = take(c, cindex);
    Arrays.sort(d);
    return d;
  }

  public static long[] intersectMultisetMapped(long[] a, long[] b) {
    // return a long[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection regardless of 
    // how many times it occurs in a.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new long[] {};
    Map<Long, Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      mapa.merge(a[i], 1, Integer::sum);
    Map<Long, Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    long[] c = new long[len];
    int cindex = 0;
    Integer ia;
    Integer ib;
    int count;
    for (Long e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e);
        ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++)
          c[cindex++] = e;
      }
    }
    long[] d = take(c, cindex);
    Arrays.sort(d);
    return d;
  }

  public static double[] intersectMultisetMapped(double[] a, double[] b) {
    // return a double[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection regardless of 
    // how many times it occurs in a regardless of 
    // how many times it occurs in a.
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return new double[] {};
    Map<Double, Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      mapa.merge(a[i], 1, Integer::sum);
    Map<Double, Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    double[] c = new double[len];
    int cindex = 0;
    Integer ia;
    Integer ib;
    int count;
    for (Double e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e);
        ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++)
          c[cindex++] = e;
      }
    }
    double[] d = take(c, cindex);
    Arrays.sort(d);
    return d;
  }

  public static <T> T[] intersectMultisetMapped(T[] a, T[] b) {
    // return a double[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection regardless of 
    // how many times it occurs in a.
    if (a == null && b == null)
      throw new IllegalArgumentException("intersectMultiset: both array arguments can't be null");
    if (a == null || b == null || a.length == 0 || b.length == 0)
      return a == null ? copyOf(b, 0) : copyOf(a, 0);
    Map<T, Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      mapa.merge(a[i], 1, Integer::sum);
    Map<T, Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++)
      mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    T[] c = ofDim(a.getClass().getComponentType(), len);
    int cindex = 0;
    Integer ia;
    Integer ib;
    int count;
    for (T e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e);
        ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++)
          c[cindex++] = e;
      }
    }
    return take(c, cindex);
  }

  public static boolean isBoxedArray(Object a) {
    // return true if a is an array with boxed type ultimate
    // component type else return false
    if (a == null)
      throw new IllegalArgumentException("isBoxedArray: argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("isBoxedArray: argument must be an array");
    String[] p = { "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float",
        "java.lang.Double", "java.lang.Character", "java.lang.Boolean" };
    return in(p, rootComponentName(a));
  }

  public static boolean isDefinedAt(int[] a, int n) {
    // returns true if a contains an element at index n
    return n > -1 && n < a.length;
  }

  public static boolean isDefinedAt(long[] a, int n) {
    // returns true if a contains an element at index n
    if (a == null || n < 0)
      return false;
    return n > -1 && n < a.length;
  }

  public static boolean isDefinedAt(double[] a, int n) {
    // returns true iff a contains an element at index n else false
    if (a == null || n < 0)
      return false;
    return n > -1 && n < a.length;
  }

  public static <T> boolean isDefinedAt(T[] a, int n) {
    // returns true iff a contains an element at index n else false
    if (a == null || n < 0)
      return false;
    return n > -1 && n < a.length;
  }

  public static boolean isEmpty(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("isEmpty: argument can't be null");
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }

  public static boolean isEmpty(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("isEmpty: argument can't be null");
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }

  public static boolean isEmpty(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("isEmpty: argument can't be null");
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }

  public static <T> boolean isEmpty(T[] a) {
    if (a == null)
      throw new IllegalArgumentException("isEmpty: argument can't be null");
    // returns true iff the length of a is 0 else false
    return a.length == 0;
  }

  public static boolean isPrimitive(Class<?> c) {
    // return c.isPrimitive()
    if (c == null)
      throw new IllegalArgumentException("isPrimitive: argument can't be null");
    return c.isPrimitive();
  }

  public static boolean isPrimitiveArray(Object a) {
    // return true if a is an array with primitive 
    // rootComponentType else return false.
    if (a == null)
      throw new IllegalArgumentException("isPrimitiveArray: argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("isPrimitiveArray: argument must be an array");
    return rootComponentType(a).isPrimitive();
  }
  
  public static boolean isSorted(byte[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(short[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(int[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(long[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(double[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(float[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(char[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(boolean[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
      if (a[i] == false && a[i-1] == true) return false;
    return true;
  }
    
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
        if (a[i].compareTo(a[i-1])<0) return false;
    return true;
  }
  
  public static <T> boolean isSorted(T[] a, Comparator<T> c) {
    if (a == null) throw new IllegalArgumentException("isSorted: argument can't be null");
    if (a.length == 0) return true;
    for (int i = 1; i < a.length; i++)
        if (c.compare(a[i], a[i-1])<0) return false;
    return true;
  }

  public static int[] iterateInt(int start, int length, IntFunction<Integer> f) {
    // return an int[] beginning with start and containing successive applications
    // of f in the form start, f(start), f(f(start))... up to length
    if (f == null)
      throw new IllegalArgumentException("iterate: the function f must not be null");
    if (length == 0)
      return new int[0];
    int[] a = new int[length];
    a[0] = start;
    for (int i = 1; i < length; i++)
      a[i] = f.apply(a[i - 1]);
    return a;
  }

  public static long[] iterateLong(long start, int length, LongFunction<Long> f) {
    // return a long[] beginning with start and containing successive applications
    // of f in the form start, f(start), f(f(start))... up to length
    if (f == null)
      throw new IllegalArgumentException("iterate: the function f must not be null");
    if (length == 0)
      return new long[0];
    long[] a = new long[length];
    a[0] = start;
    for (int i = 1; i < length; i++)
      a[i] = f.apply(a[i - 1]);
    return a;
  }

  public static double[] iterateDouble(double start, int length, DoubleFunction<Double> f) {
    // return a double[] beginning with start and containing successive applications
    // of f in the form start, f(start), f(f(start))... up to length
    if (f == null)
      throw new IllegalArgumentException("iterate: the function f must not be null");
    if (length == 0)
      return new double[0];
    double[] a = new double[length];
    a[0] = start;
    for (int i = 1; i < length; i++)
      a[i] = f.apply(a[i - 1]);
    return a;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] iterate(T start, int length, Function<T, T> f) {
    // return a T[] beginning with start and containing successive applications
    // of f in the form start, f(start), f(f(start))... up to length
    if (start == null)
      throw new IllegalArgumentException("iterate: the start value must not be null");
    if (f == null)
      throw new IllegalArgumentException("iterate: the function f must not be null");
    if (length == 0)
      return (T[]) newInstance(start.getClass(), 0);
    T[] a = (T[]) newInstance(start.getClass(), length);
    a[0] = start;
    for (int i = 1; i < length; i++)
      a[i] = f.apply(a[i - 1]);
    return a;
  }

  public static OfByte iterator(byte[] a) {
    return new OfByte() {
      int len = a.length;
      int c = 0;

      public boolean hasNext() {
        return c < len;
      }

      public byte nextByte() {
        return a[c++];
      }
    };
  }

  public static OfShort iterator(short[] a) {
    return new OfShort() {
      int len = a.length;
      int c = 0;

      public boolean hasNext() {
        return c < len;
      }

      public short nextShort() {
        return a[c++];
      }
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

      public boolean hasNext() {
        return c < len;
      }

      public float nextFloat() {
        return a[c++];
      }
    };
  }

  public static OfChar iterator(char[] a) {
    return new OfChar() {
      int len = a.length;
      int c = 0;

      public boolean hasNext() {
        return c < len;
      }

      public char nextChar() {
        return a[c++];
      }
    };
  }

  public static OfBoolean iterator(boolean[] a) {
    return new OfBoolean() {
      int len = a.length;
      int c = 0;

      public boolean hasNext() {
        return c < len;
      }

      public boolean nextBoolean() {
        return a[c++];
      }
    };
  }

  public static <T> Iterator<T> iterator(T[] a) {
    return Arrays.stream(a).iterator();
  }

  public static int[] josephus(int n, int m) {
    // solves the Josephus problem for n and m by returning an array with elements 
    // whose values are the indices in order of eliminination of every mth element 
    // from an array of length n until none are left. the value of the last element 
    // in the returned array is the answer to the Josephus problem.
    int[] a = range(0, n);
    int[] b = new int[a.length];
    int id = 0;
    int i = 0;
    while (a.length > 0) {
      id = (id + m - 1) % a.length;
      b[i++] = a[id];
      a = remove(a, id);
    }
    return b;
  }
  
  public static int last(int[] a) {
    assert a != null;
    assert a.length > 0;
    // returns the last element of a
    return a[a.length - 1];
  }

  public static long last(long[] a) {
    assert a != null;
    assert a.length > 0;
    // returns the last element of a
    return a[a.length - 1];
  }

  public static double last(double[] a) {
    assert a != null;
    assert a.length > 0;
    // returns the last element of a
    return a[a.length - 1];
  }

  public static <T> T last(T[] a) {
    assert a != null;
    assert a.length > 0;
    // returns the last element of a
    return a[a.length - 1];
  }
  
  public static int lastIndexOf(int[] a, int key) {
    // return the largest index of key in a if possible otherwise return -1
    // does not assume a has been
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOf(long[] a, long key) {
    // return the largest index of key in a if possible otherwise return -1
    // does not assume a has been
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOf(double[] a, double key) {
    // return the largest index of key in a if possible otherwise return -1
    // does not assume a has been
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOf(char[] a, char key) {
    // return the largest index of key in a if possible otherwise return -1
    // does not assume a has been
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);
    if (key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static <T extends Comparable<? super T>> int lastIndexOf(T[] a, T key) {
    // return the largest index of key in a if possible otherwise return -1
    // does not assume a has been
    // O(a.length log a.length) worst case running time
    if (a == null || a.length == 0) return -1;
    Arrays.sort(a);
    if (key != null && a[0] == null 
        || a[a.length-1] != null && key == null 
        || key != null && a[0] != null && key.compareTo(a[0]) < 0 
        || key != null && a[a.length-1] != null && a[a.length-1].compareTo(key) < 0)
      return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) == 0) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key != null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) < 0) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOfSorted(int[] a, int key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOfSorted(long[] a, long key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOfSorted(double[] a, double key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int lastIndexOfSorted(char[] a, char key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static <T extends Comparable<? super T>> int lastIndexOfSorted(T[] a, T key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order with a nullsLast convention
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key != null && a[0] == null 
        || a[a.length-1] != null && key == null || key != null && a[0] != null
        && key.compareTo(a[0]) < 0 || key != null && a[a.length-1] != null
        && a[a.length-1].compareTo(key) < 0) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) == 0) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key != null && a[mid] == null || key != null && a[mid] != null
          && key.compareTo(a[mid]) < 0) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }

  public static int lastIndexOfSlice(int[] a, int... v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP: for (int i = 0; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      index = i;
    }
    return index;
  }  

  public static int lastIndexOfSlice(long[] a, long... v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP: for (int i = 0; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      index = i;
    }
    return index;
  }

  public static int lastIndexOfSlice(double[] a, double... v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP: for (int i = 0; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      index = i;
    }
    return index;
  }

  @SafeVarargs
  public static <T> int lastIndexOfSlice(T[] a, T... v) {
    // returns the index of last occurrence of v in a or -1 if v not int a
    assert a != null;
    int index = -1;
    LOOP: for (int i = 0; i < a.length - v.length + 1; i++) {
      for (int j = i; j < i + v.length; j++) {
        if (!(a[j] == v[j - i]))
          continue LOOP;
      }
      index = i;
    }
    return index;
  }

  public static int lastIndexWhere(int[] a, Predicate<Integer> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        index = i;
    return index;
  }

  public static int lastIndexWhere(long[] a, Predicate<Long> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        index = i;
    return index;
  }

  public static int lastIndexWhere(double[] a, Predicate<Double> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        index = i;
    return index;
  }

  public static <T> int lastIndexWhere(T[] a, Predicate<T> p) {
    //return index of last element for which p is true or -1 if none
    assert a != null;
    if (a.length == 0)
      return -1;
    int index = -1;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i]))
        index = i;
    return index;
  }

  public static int[] map(int[] a, IntFunction<Integer> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("map: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the IntFunction must be non null");
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).intValue();
    return b;
  }

  public static long[] map(long[] a, LongFunction<Long> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("map: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the LongFunction must be non null");
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).longValue();
    return b;
  }

  public static double[] map(double[] a, DoubleFunction<Double> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("map: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the DoubleFunction must be non null");
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[i]).doubleValue();
    return b;
  }

  public static double[] mapToDouble(int[] a, IntToDoubleFunction f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("mapToDouble: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the IntToDoubleFunction must be non null");
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[i]);
    return b;
  }

  public static double[] mapToDouble(long[] a, LongToDoubleFunction f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("mapToDouble: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the LongToDoubleFunction must be non null");
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.applyAsDouble(a[i]);
    return b;
  }

  public static <R> R[] mapToR(int[] a, Function<Integer, R> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("mapToR: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the Function must be non null");
    @SuppressWarnings("unchecked")
    R[] r = (R[]) newInstance(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[i]);
    return r;
  }

  public static <R> R[] mapToR(long[] a, Function<Long, R> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("mapToR: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the Function must be non null");
    @SuppressWarnings("unchecked")
    R[] r = (R[]) newInstance(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[i]);
    return r;
  }

  public static <R> R[] mapToR(double[] a, Function<Double, R> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("mapToR: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the Function must be non null");
    @SuppressWarnings("unchecked")
    R[] r = (R[]) newInstance(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[i]);
    return r;
  }

  public static <T, R> R[] map(T[] a, Function<T, R> f) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("map: " + "the array must be non null and have at least one element");
    if (f == null)
      throw new IllegalArgumentException("map: " + "the Function must be non null");
    if (allNull(a))
      throw new IllegalArgumentException("map: " + "the array must not have all null elements");
    R[] r = ofDim(f.apply(findFirstNonNull(a)._1.get()).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[i]);
    return r;
  }
  
  public static byte max(byte[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    byte max = Byte.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }
  
  public static short max(short[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    short max = Short.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }

  public static int max(int[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }

  public static long max(long[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    long max = Long.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }

  public static float max(float[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    float max = Float.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }
  
  public static double max(double[] a) {
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("max: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    double max = Double.MIN_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] > max)
        max = a[i];
    return max;
  }
  
  public static char max(char[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    char max  = a[0];
    for (int i = 1; i < a.length; i++)
      if (Character.compare(a[i], max)>0)
        max = a[i];
    return max;
  }

  public static boolean max(boolean[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    boolean max  = a[0];
    for (int i = 1; i < a.length; i++)
      if (Boolean.compare(a[i], max)>0)
        max = a[i];
    return max;
  }
  
  public static <T extends Comparable<? super T>> T max(T[] a) {
    // return max nonnull element of a unless all elements are null 
    // in which case return null.
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    T max = null; int i = 0;
    for (; i < a.length; i++) if (a[i] != null) { max = a[i]; break; }
    if (i >= a.length-1) return max;
    for (++i; i < a.length; i++) if (a[i] != null && a[i].compareTo(max)>0) max = a[i];
    return max; 
  }
  
  public static <T> T max(T[] a, Comparator<T> c) {
    // return max nonnull element of a unless all elements are null 
    // in which case return null.
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    T max = null; int i = 0;
    for (; i < a.length; i++) if (a[i] != null) { max = a[i]; break; }
    if (i >= a.length-1) return max;
    for (++i; i < a.length; i++) if (a[i] != null && c.compare(a[i],max)>0) max = a[i];
    return max; 
  }

  public static <R extends Comparable<? super R>> int maxBy(int[] a, IntFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("maxBy: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    int max = a[0];
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

  public static <R extends Comparable<? super R>> long maxBy(long[] a, LongFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("maxBy: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
    if (a.length == 1)
      return a[0];
    long max = a[0];
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

  public static <R extends Comparable<? super R>> double maxBy(double[] a, DoubleFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("maxBy: " + "the array must be non null and have at least one element");
    if (a.length == 1)
      return a[0];
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

  public static <T, R extends Comparable<? super R>> T maxBy(T[] a, Function<T, R> f) {
    // returns the 1st (non null) element x in a which has the largest f(x)
    if (a == null || a.length == 0)
      throw new IllegalArgumentException("maxBy: " + "the array must be non null and have at least one element");
    if (allNull(a))
      throw new IllegalArgumentException("map: " + "the array must not have all null elements");
    if (a.length == 1)
      return a[0];
    T max = findFirstNonNull(a)._1.get();
    R rmax = f.apply(max);
    R tmp = null;
    for (int i = findFirstNonNull(a)._2.get() + 1; i < a.length; i++) {
      if (a[i] != null) {
        tmp = f.apply(a[i]);
        if (tmp.compareTo(rmax) > 0) {
          rmax = tmp;
          max = a[i];
        }
      }
    }
    return max;
  }
  
  public static byte min(byte[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    byte min = Byte.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min) min = a[i];
    return min;
  }
  
  public static short min(short[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    short min = Short.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min) min = a[i];
    return min;
  }

  public static int min(int[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    int min = Integer.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min)
        min = a[i];
    return min;
  }

  public static long min(long[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    long min = Long.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min)
        min = a[i];
    return min;
  }
  
  public static float min(float[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    float min = Float.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min)
        min = a[i];
    return min;
  }

  public static double min(double[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    double min = Double.MAX_VALUE;
    for (int i = 0; i < a.length; i++)
      if (a[i] < min)
        min = a[i];
    return min;
  }
  
  public static char min(char[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    char min  = a[0];
    for (int i = 1; i < a.length; i++)
      if (Character.compare(a[i], min)<0)
        min = a[i];
    return min;
  }
  
  public static boolean min(boolean[] a) {
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    boolean min  = a[0];
    for (int i = 1; i < a.length; i++)
      if (Boolean.compare(a[i], min)<0)
        min = a[i];
    return min;
  }

  public static <T extends Comparable<? super T>> T min(T[] a) {
    // return min nonnull element of a unless all elements are null 
    // in which case return null.
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    T min = null; int i = 0;
    for (; i < a.length; i++) if (a[i] != null) { min = a[i]; break; }
    if (i >= a.length-1) return min;
    for (++i; i < a.length; i++) if (a[i] != null && a[i].compareTo(min)<0) min = a[i];
    return min; 
  }
  
  public static <T> T min(T[] a, Comparator<T> c) {
    // return max nonnull element of a unless all elements are null 
    // in which case return null.
    if (a == null || a.length == 0) throw new IllegalArgumentException("max: " 
        + "the array must be non null and have at least one element");
    if (a.length == 1) return a[0];
    T min = null; int i = 0;
    for (; i < a.length; i++) if (a[i] != null) { min = a[i]; break; }
    if (i >= a.length-1) return min;
    for (++i; i < a.length; i++) if (a[i] != null && c.compare(a[i],min)<0) min = a[i];
    return min; 
  }
  
  public static <R extends Comparable<? super R>> int minBy(int[] a, IntFunction<R> f) {
    // returns the 1st element x which has the smallest f(x)
    assert a != null;
    assert a.length > 0;
    if (a.length == 1)
      return a[0];
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

  public static <R extends Comparable<? super R>> double minBy(double[] a, DoubleFunction<R> f) {
    // returns the 1st element x which has the largest f(x)
    assert a != null;
    assert a.length > 0;
    if (a.length == 1)
      return a[0];
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
    if (a.length == 0)
      return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 1; i < a.length - 1; i++)
      sb.append(a[i] + ",");
    sb.append(a[a.length - 1] + "]");
    return sb.toString();
  }

  public static String mkString(double[] a) {
    // returns a String representation of a
    assert a != null;
    if (a.length == 0)
      return "[]";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 1; i < a.length - 1; i++)
      sb.append(a[i] + ",");
    sb.append(a[a.length - 1] + "]");
    return sb.toString();
  }

  //Ex1133MatrixLibrary
  public static int[][] matrixProduct(int[][] a, int[][] b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length;
    int a0len = a[0].length;
    int blen = b.length;
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    int[][] c = new int[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) {
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k] * b[k][j];
      }
    return c;
  }

  //Ex1133MatrixLibrary
  public static int[] matrixProduct(int[] a, int[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (alen != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    int[] c = new int[b0len];
    for (int i = 0; i < b0len; i++)
      for (int k = 0; k < alen; k++)
        c[i] += a[k] * b[k][i];
    return c;
  }

  //Ex1133MatrixLibrary
  public static int[] matrixProduct(int[][] a, int[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all rows of the first array (a) must have the same length");

    int[] c = new int[alen];
    for (int i = 0; i < alen; i++)
      for (int k = 0; k < blen; k++)
        c[i] += a[i][k] * b[k];
    return c;
  }

  //Ex1133MatrixLibrary
  public static long[][] matrixProduct(long[][] a, long[][] b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length;
    int a0len = a[0].length;
    int blen = b.length;
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    long[][] c = new long[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) {
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k] * b[k][j];
      }
    return c;
  }

  //Ex1133MatrixLibrary
  public static long[] matrixProduct(long[] a, long[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (alen != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    long[] c = new long[b0len];
    for (int i = 0; i < b0len; i++)
      for (int k = 0; k < alen; k++)
        c[i] += a[k] * b[k][i];
    return c;
  }

  //Ex1133MatrixLibrary
  public static long[] matrixProduct(long[][] a, long[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all rows of the first array (a) must have the same length");

    long[] c = new long[alen];
    for (int i = 0; i < alen; i++)
      for (int k = 0; k < blen; k++)
        c[i] += a[i][k] * b[k];
    return c;
  }

  //Ex1133MatrixLibrary
  public static double[][] matrixProduct(double[][] a, double[][] b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length;
    int a0len = a[0].length;
    int blen = b.length;
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    double[][] c = new double[alen][b0len];
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) {
        for (int k = 0; k < blen; k++)
          c[i][j] += a[i][k] * b[k][j];
      }
    return c;
  }

  //Ex1133MatrixLibrary
  public static double[] matrixProduct(double[] a, double[][] b) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (alen != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    double[] c = new double[b0len];
    for (int i = 0; i < b0len; i++)
      for (int k = 0; k < alen; k++)
        c[i] += a[k] * b[k][i];
    return c;
  }

  //Ex1133MatrixLibrary
  public static double[] matrixProduct(double[][] a, double[] b) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all rows of the first array (a) must have the same length");

    double[] c = new double[alen];
    for (int i = 0; i < alen; i++)
      for (int k = 0; k < blen; k++)
        c[i] += a[i][k] * b[k];
    return c;
  }

  //Ex1133MatrixLibrary
  public static <T> T[][] matrixProduct(T[][] a, T[][] b, BinaryOperator<T> add, BinaryOperator<T> mult) {
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length;
    int a0len = a[0].length;
    int blen = b.length;
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all columns of the first array (a) must have the same length");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    T[][] c = ofDim(b.getClass().getComponentType(), alen, b0len);
    for (int i = 0; i < alen; i++)
      for (int j = 0; j < b0len; j++) {
        for (int k = 0; k < blen; k++)
          c[i][j] = add.apply(c[i][j], mult.apply(a[i][k], b[k][j]));
      }
    return c;
  }

  //Ex1133MatrixLibrary
  public static <T> T[] matrixProduct(T[] a, T[][] b, BinaryOperator<T> add, BinaryOperator<T> mult) {
    // taking a as a row vector so its length is its number of columns
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of columns since it's 1D
    int blen = b.length; // number of rows since it's 2D
    int b0len = b[0].length;
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (alen != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < blen; i++)
      if (b[i].length != b0len)
        throw new IllegalArgumentException("all rows of the second array (a) must have the same length");

    T[] c = ofDim(b.getClass().getComponentType().getComponentType(), b0len);
    for (int i = 0; i < b0len; i++)
      for (int k = 0; k < alen; k++)
        c[i] = add.apply(c[i], mult.apply(a[k], b[k][i]));
    return c;
  }

  //Ex1133MatrixLibrary
  public static <T> T[] matrixProduct(T[][] a, T[] b, BinaryOperator<T> add, BinaryOperator<T> mult) {
    // taking a as a column vector so its length is its number of rows
    if (a == null || b == null)
      throw new IllegalArgumentException("matrixProduct: both arrays must be not be null");
    int alen = a.length; // number of rows since it's 2D
    int a0len = a[0].length;
    int blen = b.length; // number of columns since it's 2D
    if (alen == 0 || blen == 0)
      throw new IllegalArgumentException("matrixProduct: both arrays must have length > 0");
    if (a0len != blen)
      throw new IllegalArgumentException("matrixProduct: the number of columns of the first array (a) "
          + "must equal the number of rows of the second array (b)");
    // iff the length of all rows is the same so is the length of all columns
    for (int i = 1; i < alen; i++)
      if (a[i].length != a0len)
        throw new IllegalArgumentException("all rows of the first array (a) must have the same length");

    T[] c = ofDim(a.getClass().getComponentType().getComponentType(), alen);
    for (int i = 0; i < alen; i++)
      for (int k = 0; k < blen; k++)
        c[i] = add.apply(c[i], mult.apply(a[i][k], b[k]));
    return c;
  }
  
  public static <T extends Comparable<? super T>> void naturalMergeSort(T[] z){
    // in-place natural mergesort 
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
    Function<T[],Queue<Integer>> getRuns = (x) -> {
      int m = x.length;
      Queue<Integer> runs = new Queue<Integer>();
      int i = 0, c= 0;
      while(i < m) {
        c = i;
        if (i < m -1) 
          while(i < m -1 && x[i].compareTo(x[i+1]) < 0) i++;
        runs.enqueue(++i - c); 
      }
      return runs;
    };
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      for (int k = lo; k <= hi; k++) 
        b[k] = a[k];
      for (int k = lo; k <= hi; k++)
        if (i > mid) a[k] = b[j++];
        else if (j > hi ) a[k] = b[i++];
        else if (b[j].compareTo(b[i]) < 0) a[k] = b[j++];
        else a[k] = b[i++];
    }; 
    Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == N) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    } 
  }

  public static boolean nonEmpty(int[] a) {
    // iff a.length == 0 returns true else false
    assert a != null;
    if (a.length == 0)
      return true;
    return false;
  }

  public static boolean nonEmpty(double[] a) {
    // iff a.length == 0 returns true else false
    assert a != null;
    if (a.length == 0)
      return true;
    return false;
  }

  public static long numberOfCombinationsLong(int n, int r) {
    // returns the number of possible combinations of r objects 
    // from a set of n objects
    if (n <= 1)
      throw new IllegalArgumentException("numberOfCombinations: n can't be less than 1");
    //    if (n > 21) throw new IllegalArgumentException(
    //        "numberOfCombinations: cannot compute for n > 21");
    if (r > n)
      throw new IllegalArgumentException("numberOfCombinations: r can't be greater than n");
    return (long) factorial(n) / (factorial(r) * factorial(n - r));
  }

  public static BigInteger numberOfCombinations(int n, int r) {
    // returns the number of possible combinations of r objects 
    // from a set of n objects
    if (n <= 1)
      throw new IllegalArgumentException("numberOfCombinations: n can't be less than 1");
    if (r > n)
      throw new IllegalArgumentException("numberOfCombinations: r can't be greater than n");
    MathContext mc = MathContext.DECIMAL128;
    BigInteger bn = new BigInteger("" + n);
    BigInteger br = new BigInteger("" + r);
    BigInteger bnr = new BigInteger("" + (n - r));
    BigDecimal factn = new BigDecimal(factorial(bn), mc);
    BigDecimal factr = new BigDecimal(factorial(br), mc);
    BigDecimal factnr = new BigDecimal(factorial(bnr), mc);
    BigInteger nc = factn.divide(factr.multiply(factnr, mc), mc).round(mc).toBigInteger();
    return nc;
  }

  private static class Numel {
    // this class provides a method to return the number of 
    // elements in any array. this number fits in a long since
    // (Long.MAX_VALUE > 255L*Integer.MAX_VALUE) == true

    private long r = 0;

    public Numel() {
    }

    long numel(Object a) {
      // provided a is an array return its total number of elements
      if (a == null)
        throw new IllegalArgumentException("numel: " + "argument can't be null");
      if (!a.getClass().isArray())
        throw new IllegalArgumentException("numel: " + "argument must be an array");

      int dim = dim(a);
      int alen = Array.getLength(a);

      if (dim == 1) { // base case of recursion
        r += alen;
      } else {
        for (int i = 0; i < alen; i++)
          numel(Array.get(a, i));
      }
      return r;
    }
  }

  public static long numel(Object a) {
    return (new Numel()).numel(a);
  }

  public static byte[] ofDimByte(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimByte: n must be >= 0");
    return new byte[n];
  }

  public static short[] ofDimShort(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimShort: n must be >= 0");
    return new short[n];
  }

  public static int[] ofDimInt(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimInt: n must be >= 0");
    return new int[n];
  }

  public static long[] ofDimLong(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimLong: n must be >= 0");
    return new long[n];
  }

  public static float[] ofDimFloat(int n) {
    if (n < 0)
      throw new IllegalArgumentException("fillFloat: n must be >= 0");
    return new float[n];
  }

  public static double[] ofDimDouble(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimDouble: n must be >= 0");
    return new double[n];
  }

  public static char[] ofDimChar(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimChar: n must be >= 0");
    return new char[n];
  }

  public static boolean[] ofDimBoolean(int n) {
    if (n < 0)
      throw new IllegalArgumentException("ofDimBoolean: n must be >= 0");
    return new boolean[n];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] ofDim(Class<?> c, int n) {
    if (c == null || n < 0)
      throw new IllegalArgumentException("ofDim: n must be >= 0 " + "and c must not be null");
    return (T[]) Array.newInstance(c, n);
  }

  public static byte[][] ofDimByte(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimByte: n1, and n2 must both be >= 0");
    return new byte[n1][n2];
  }

  public static short[][] ofDimShort(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimShort: n1, and n2 must both be >= 0");
    return new short[n1][n2];
  }

  public static int[][] ofDimInt(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimInt: n1, and n2 must both be >= 0");
    return new int[n1][n2];
  }

  public static long[][] ofDimLong(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimLong: n1, and n2 must both be >= 0");
    return new long[n1][n2];
  }

  public static float[][] ofDimFloat(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimFloat: n1, and n2 must both be >= 0");
    return new float[n1][n2];
  }

  public static double[][] ofDimDouble(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimDouble: n1, and n2 must both be >= 0");
    return new double[n1][n2];
  }

  public static char[][] ofDimChar(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimChar: n1, and n2 must both be >= 0");
    return new char[n1][n2];
  }

  public static boolean[][] ofDimBoolean(int n1, int n2) {
    if (n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDimBoolean: n1, and n2 must both be >= 0");
    return new boolean[n1][n2];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][] ofDim(Class<?> c, int n1, int n2) {
    if (c == null || n1 < 0 || n2 < 0)
      throw new IllegalArgumentException("ofDim: n1, and n2 must both be >= 0 and c must not be null");
    return (T[][]) Array.newInstance(c, n1, n2);
  }

  public static byte[][][] ofDimByte(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimByte: n1, n2, and n3 must all be >= 0");
    return new byte[n1][n2][n3];
  }

  public static short[][][] ofDimShort(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimShort: n1, n2, and n3 must all be >= 0");
    return new short[n1][n2][n3];
  }

  public static int[][][] ofDimInt(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimInt: n1, n2, and n3 must all be >= 0");
    return new int[n1][n2][n3];
  }

  public static long[][][] ofDimLong(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimLong: n1, n2, and n3 must all be >= 0");
    return new long[n1][n2][n3];
  }

  public static float[][][] ofDimFloat(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimFloat: n1, n2, and n3 must all be >= 0");
    return new float[n1][n2][n3];
  }

  public static double[][][] ofDimDouble(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimDouble: n1, n2, and n3 must all be >= 0");
    return new double[n1][n2][n3];
  }

  public static char[][][] ofDimChar(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimChar: n1, n2, and n3 must all be >= 0");
    return new char[n1][n2][n3];
  }

  public static boolean[][][] ofDimBoolean(int n1, int n2, int n3) {
    if (n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDimBoolean: n1, n2, and n3 must all be >= 0");
    return new boolean[n1][n2][n3];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][] ofDim(Class<?> c, int n1, int n2, int n3) {
    if (c == null || n1 < 0 || n2 < 0 || n3 < 0)
      throw new IllegalArgumentException("ofDim: n1, n2, and n3 must all be >= 0 and c must not be null");
    return (T[][][]) Array.newInstance(c, n1, n2, n3);
  }

  public static byte[][][][] ofDimByte(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimByte: n1, n2, n3 and n4 must all be >= 0");
    return new byte[n1][n2][n3][n4];
  }

  public static short[][][][] ofDimShort(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimShort: n1, n2, n3 and n4 must all be >= 0");
    return new short[n1][n2][n3][n4];
  }

  public static int[][][][] ofDimInt(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimInt: n1, n2, n3 and n4 must all be >= 0");
    return new int[n1][n2][n3][n4];
  }

  public static long[][][][] ofDimLong(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimLong: n1, n2, n3 and n4 must all be >= 0");
    return new long[n1][n2][n3][n4];
  }

  public static float[][][][] ofDimFloat(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimFloat: n1, n2, n3 and n4 must all be >= 0");
    return new float[n1][n2][n3][n4];
  }

  public static double[][][][] ofDimDouble(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimDouble: n1, n2, n3 and n4 must all be >= 0");
    return new double[n1][n2][n3][n4];
  }

  public static char[][][][] ofDimChar(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimChar: n1, n2, n3 and n4 must all be >= 0");
    return new char[n1][n2][n3][n4];
  }

  public static boolean[][][][] ofDimBoolean(int n1, int n2, int n3, int n4) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDimBoolean: n1, n2, n3 and n4 must all be >= 0");
    return new boolean[n1][n2][n3][n4];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][][] ofDim(Class<?> c, int n1, int n2, int n3, int n4) {
    if (c == null || n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0)
      throw new IllegalArgumentException("ofDim: n1, n2, n3 and n4 must all be >= 0 and c must not be null");
    return (T[][][][]) Array.newInstance(c, n1, n2, n3, n4);
  }

  public static byte[][][][][] ofDimByte(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimByte: n1, n2, n3, n4 and n5 must all be >= 0");
    return new byte[n1][n2][n3][n4][n5];
  }

  public static short[][][][][] ofDimShort(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimShort: n1, n2, n3, n4 and n5 must all be >= 0");
    return new short[n1][n2][n3][n4][n5];
  }

  public static int[][][][][] ofDimInt(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimInt: n1, n2, n3, n4 and n5 must all be >= 0");
    return new int[n1][n2][n3][n4][n5];
  }

  public static long[][][][][] ofDimLong(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimLong: n1, n2, n3, n4 and n5 must all be >= 0");
    return new long[n1][n2][n3][n4][n5];
  }

  public static float[][][][][] ofDimFloat(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimFloat: n1, n2, n3, n4 and n5 must all be > 0");
    return new float[n1][n2][n3][n4][n5];
  }

  public static double[][][][][] ofDimDouble(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimDouble: n1, n2, n3, n4 and n5 must all be >= 0");
    return new double[n1][n2][n3][n4][n5];
  }

  public static char[][][][][] ofDimChar(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimChar: n1, n2, n3, n4 and n5 must all be >= 0");
    return new char[n1][n2][n3][n4][n5];
  }

  public static boolean[][][][][] ofDimBoolean(int n1, int n2, int n3, int n4, int n5) {
    if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDimBoolean: n1, n2, n3, n4 and n5 must all be >= 0");
    return new boolean[n1][n2][n3][n4][n5];
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][][][] ofDim(Class<?> c, int n1, int n2, int n3, int n4, int n5) {
    if (c == null || n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0 || n5 < 0)
      throw new IllegalArgumentException("ofDim: n1, n2, n3, n4 and n5 must all be >= 0 and c must not be null");
    return (T[][][][][]) Array.newInstance(c, n1, n2, n3, n4, n5);
  }

  public static Object ofDim(Class<?> c, int... n) {
    // this handles creating of all types of arrays for all possible dimenensionalities
    if (n.length == 0)
      throw new IllegalArgumentException("ofDim: n.length must be >= 0");
    int[] r = n.length <= 255 ? n : take(n, 255);
    for (int i = 0; i < r.length; i++)
      if (r[i] < 0)
        throw new IllegalArgumentException("ofDim: all elements of n " + "must be  >= 0");
    return Array.newInstance(c, r);
  }

  public static void pa(Object a, int... options) {
    System.out.println(arrayToString(a, options));
  }
  
  public static void par(Object a, int... options) {
    System.out.println(arrayToString(a, -1));
  }
  
  public static int[] padTo(int[] a, int len, int v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len)
      return (int[]) clone(a);
    int[] b = new int[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else
        b[i] = v;
    return b;
  }

  public static long[] padTo(long[] a, int len, long v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len)
      return (long[]) clone(a);
    long[] b = new long[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else
        b[i] = v;
    return b;
  }

  public static double[] padTo(double[] a, int len, double v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len)
      return (double[]) clone(a);
    double[] b = new double[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else
        b[i] = v;
    return b;
  }

  public static char[] padTo(char[] a, int len, char v) {
    assert a != null;
    // returns a copy of a padded to length len with v
    if (a.length <= len)
      return (char[]) clone(a);
    char[] b = new char[len];
    for (int i = 1; i < len; i++)
      if (i < a.length) {
        b[i] = a[i];
      } else
        b[i] = v;
    return b;
  }

  public static int[][] partition(int[] a, Predicate<Integer> p) {
    // partitions the elements of a into two arrays according to p
    // the first row of the result contains values of a for which p(a) is true
    // the second row of the result contains values of a for which p(a) is false
    assert a != null;
    assert a.length > 0;
    int[] q0 = new int[a.length];
    int q0index = 0;
    int[] q1 = new int[a.length];
    int q1index = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        q0[q0index++] = a[i];
      } else
        q1[q1index++] = a[i];
    int[] r0 = new int[q0index];
    for (int i = 0; i < q0index; i++)
      r0[i] = q0[i];
    int[] r1 = new int[q1index];
    for (int i = 0; i < q1index; i++)
      r1[i] = q1[i];
    int[][] r = new int[2][];
    r[0] = r0;
    r[1] = r1;
    return r;
  }

  public static double[][] partition(double[] a, Predicate<Double> p) {
    // partitions the elements of a into two arrays according to p
    // the first row of the result contains values of a for which p(a) is true
    // the second row of the result contains values of a for which p(a) is false
    assert a != null;
    assert a.length > 0;
    double[] q0 = new double[a.length];
    int q0index = 0;
    double[] q1 = new double[a.length];
    int q1index = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        q0[q0index++] = a[i];
      } else
        q1[q1index++] = a[i];
    double[] r0 = new double[q0index];
    for (int i = 0; i < q0index; i++)
      r0[i] = q0[i];
    double[] r1 = new double[q1index];
    for (int i = 0; i < q1index; i++)
      r1[i] = q1[i];
    double[][] r = new double[2][];
    r[0] = r0;
    r[1] = r1;
    return r;
  }

  public static int[] patch(int[] a, int from, int replaced, int... g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null;
    assert from >= 0;
    assert replaced >= 0;
    if (a.length == 0)
      return new int[0];
    if (a.length - 1 < from)
      return (int[]) clone(a);
    if (g.length == 0 && replaced == 0)
      return (int[]) clone(a);
    int[] b = new int[a.length - replaced + g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from + g.length) {
        b[i] = g[i - from];
      } else
        b[i] = a[i + replaced - g.length];
    return b;
  }

  public static long[] patch(long[] a, int from, int replaced, int... g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null;
    assert from >= 0;
    assert replaced >= 0;
    if (a.length == 0)
      return new long[0];
    if (a.length - 1 < from)
      return (long[]) clone(a);
    if (g.length == 0 && replaced == 0)
      return (long[]) clone(a);
    long[] b = new long[a.length - replaced + g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from + g.length) {
        b[i] = g[i - from];
      } else
        b[i] = a[i + replaced - g.length];
    return b;
  }

  public static double[] patch(double[] a, int from, int replaced, double... g) {
    // return a copy of a with replaced number of elements starting at
    // from dropped and replaced with the elements of g 
    assert a != null;
    assert from >= 0;
    assert replaced >= 0;
    if (a.length == 0)
      return new double[0];
    if (a.length - 1 < from)
      return (double[]) clone(a);
    if (g.length == 0 && replaced == 0)
      return (double[]) clone(a);
    double[] b = new double[a.length - replaced + g.length];
    for (int i = 0; i < b.length; i++)
      if (i < from) {
        b[i] = a[i];
      } else if (i < from + g.length) {
        b[i] = g[i - from];
      } else
        b[i] = a[i + replaced - g.length];
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
        for (int i = 0; i < n; i++) {
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
      for (int i = 0; i < n; i++)
        z[i] = real[r[i]];
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
      for (int j = 0; j < n; j++)
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
      if ((k == 0) || (k == n - 1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for (int j = 0; j < n; j++)
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

    public PermutatorLong(long[] real) {
      n = real.length;
      this.real = real;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for (int i = 0; i < n; i++) {
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
      for (int i = 0; i < n; i++)
        z[i] = real[r[i]];
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
      for (int j = 0; j < n; j++)
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
      if ((k == 0) || (k == n - 1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for (int j = 0; j < n; j++)
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

    public PermutatorDouble(double[] real) {
      n = real.length;
      this.real = real;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for (int i = 0; i < n; i++) {
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
      for (int i = 0; i < n; i++)
        z[i] = real[r[i]];
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
      for (int j = 0; j < n; j++)
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
      if ((k == 0) || (k == n - 1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for (int j = 0; j < n; j++)
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
        throw new IllegalArgumentException("PermIterator(Y[] rarray) constructor: " + "rarray can't be null");
      this.rlist = makeList(rarray);
      n = rarray.length;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for (int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }
      next = perm;
    }

    public Permutator(List<Y> rlist) {
      if (rlist == null)
        throw new IllegalArgumentException("PermIterator(List<Y> rlist) " + "constructor: rlist can't be null");
      n = rlist.size();
      this.rlist = rlist;
      if (n <= 0) {
        perm = (dirs = null);
      } else {
        perm = new int[n];
        dirs = new int[n];
        for (int i = 0; i < n; i++) {
          perm[i] = i;
          dirs[i] = -1;
        }
        dirs[0] = 0;
      }
      next = perm;
    }

    private final List<Y> makeList(Y[] y) {
      if (y == null)
        throw new IllegalArgumentException("makeList: y can't be null");
      List<Y> list = new ArrayList<>();
      for (int i = 0; i < y.length; i++)
        list.add(y[i]);
      return list;
    }

    @SafeVarargs
    private final Y[] makeArray(int[] r, Y... y) {
      if (r == null)
        throw new IllegalArgumentException("makeArray: r can't be null");
      List<Y> q = new ArrayList<>();
      for (int i = 0; i < n; i++)
        q.add(rlist.get(r[i]));
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
      for (int j = 0; j < n; j++)
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
      if ((k == 0) || (k == n - 1) || (perm[k + dirs[k]] > e))
        dirs[k] = 0;

      // set directions to all greater elements
      for (int j = 0; j < n; j++)
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
    if (n.compareTo(ZERO) < 0)
      throw new IllegalArgumentException("factorial: " + "n must be >= 0");
    if (n.equals(ZERO) || n.equals(ONE)) return ONE;
    BigInteger f = ONE;
    BigInteger g = ONE;

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
      if (!p.test(a[i]))
        return i;
    return a.length;
  }

  public static int prefixLength(double[] a, Predicate<Double> p) {
    if (a == null)
      throw new IllegalArgumentException("prefixLength: a can't be null");
    if (p == null)
      throw new IllegalArgumentException("prefixLength: p can't be null");
    for (int i = 0; i < a.length; i++)
      if (!p.test(a[i]))
        return i;
    return a.length;
  }

  public static int[] prepend(int[] a, int... p) {
    // returns a new array with p prepended to a
    if (a == null)
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0)
      return (int[]) clone(a);
    int[] b = copyOf(p, a.length + p.length);
    for (int i = 0; i < a.length; i++)
      b[p.length + i] = a[i];
    return b;
  }

  public static long[] prepend(long[] a, long... p) {
    // returns a new array with p prepended to a
    if (a == null)
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0)
      return (long[]) clone(a);
    long[] b = copyOf(p, a.length + p.length);
    for (int i = 0; i < a.length; i++)
      b[p.length + i] = a[i];
    return b;
  }

  public static double[] prepend(double[] a, double... p) {
    // returns a new array with p prepended to a
    if (a == null)
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0)
      return (double[]) clone(a);
    double[] b = copyOf(p, a.length + p.length);
    for (int i = 0; i < a.length; i++)
      b[p.length + i] = a[i];
    return b;
  }

  @SafeVarargs
  public static <T> T[] prepend(T[] a, T... p) {
    // returns a new array with p prepended to a
    if (a == null)
      throw new IllegalArgumentException("prepend: a can't be null");
    if (p.length == 0)
      return (T[]) a.clone();
    T[] b = copyOf(p, a.length + p.length);
    for (int i = 0; i < a.length; i++)
      b[p.length + i] = a[i];
    return b;
  }

  public static Class<?> primitiveClassForName(String s) {
    // returns the Class for primitives else null
    if (s == null)
      return null;
    switch (s) {
    case "byte":
      return byte.class;
    case "char":
      return char.class;
    case "double":
      return double.class;
    case "float":
      return float.class;
    case "int":
      return int.class;
    case "long":
      return long.class;
    case "short":
      return short.class;
    case "boolean":
      return boolean.class;
    default:
      return null;
    }
  }

  public static int product(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("product: a can't have zero length");
    int product = 1;
    for (int i = 0; i < a.length; i++) {
      product *= a[i];
      if (product == 0)
        return 0;
    }
    return product;
  }

  public static void printArray(Object a, int... options) {
    System.out.println(arrayToString(a, options));
  }

  public static long product(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("product: a can't have zero length");
    int product = 1;
    for (int i = 0; i < a.length; i++) {
      product *= a[i];
      if (product == 0)
        return 0;
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
      product *= a[i];
      if (product == 0)
        return 0;
    }
    return product;
  }
  
  public static int[] randomArray(int size, int digits) {
    // return a random array of ints with digits digits of length size
    if (0 >= size) throw new IllegalArgumentException("randomArray: "
        + "size must be > 0");
    int maxDigits = (""+Integer.MAX_VALUE).length();
    if (0 >= digits || digits > maxDigits) throw new IllegalArgumentException("randomArray: "
        + "digits must be > 0  and <= "+maxDigits);
    int maxSize4MaxDigits = Integer.MAX_VALUE - 8 - (int)Math.pow(10, maxDigits-1) + 1;
    if (digits == maxDigits && size > maxSize4MaxDigits) throw new IllegalArgumentException(
        "randomArray: with digits == "+maxDigits+" the max allowable size is "+maxSize4MaxDigits);
        
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(735632797);
    int min = (int) Math.pow(10,digits-1);
    return r.ints(size, min, min*10).toArray();
  }

  public static int[] range(int start, int end, int step) {
    // return an int[] beginning with start and followed by elements
    // successively incremented by step up to but not including end
    if (step == 0)
      throw new IllegalArgumentException("range: step cannot be 0");
    int len = (end - 1 - start) / step + 1;
    if (len <= 0)
      return new int[0];
    int[] a = new int[len];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = a[i - 1] + step;
    return a;
  }

  public static int[] range(int start, int end) {
    // return an int[] beginning with start and followed by elements
    // successively incremented or decremented by 1 up to but not including end
    if (end == start) return new int[0];
    int[] a;
    a = end > start ? new int[end - start] : new int[start - end];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = end > start ? a[i - 1] + 1 : a[i - 1] - 1;
    return a;
  }
  
  public static double[] range(double start, double end, double step) {
    // return a double[] beginning with start and followed by elements
    // successively incremented by step up to but not including end
    if (step == 0)
      throw new IllegalArgumentException("range: step cannot be 0");
    int len = (int)((end - 1 - start) / step + 1);
    if (len <= 0)
      return new double[0];
    double[] a = new double[len];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = a[i - 1] + step;
    return a;
  }
  
  public static double[] range(double start, double end) {
    // return a double[] beginning with start and followed by elements
    // successively incremented or decremented by 1 up to but not including end
    if (end == start) return new double[0];
    double[] a;
    int len = end > start ? (int)(end - start + 1) : (int)(start - end + 1);
    if (start + len - 1 >= end) len--;
    a = new double[len];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = end > start ? a[i - 1] + 1 : a[i - 1] - 1;
    return a;
  }
  
  public static Integer[] rangeInteger(Integer start, Integer end, Integer step) {
    // return a Integer[] beginning with start and followed by elements
    // successively incremented by step up to but not including end
    if (step == 0)
      throw new IllegalArgumentException("range: step cannot be 0");
    int len = (int)((end - start) / step + 1);
    if (start + (len-1)*step >= end) len--;
    if (len <= 0) return new Integer[0];
    Integer[] a = new Integer[len];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = a[i - 1] + step;
    return a;
  }
  
  public static Integer[] rangeInteger(Integer start, Integer end) {
    // return an Integer[] beginning with start and followed by elements
    // successively incremented or decremented by 1 up to but not including end
    if (end == start) return new Integer[0];
    Integer[] a;
    int len = end > start ? (int)(end - start + 1) : (int)(start - end + 1);
    if (start + len - 1 >= end) len--;
    a = new Integer[len];
    
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = end > start ? a[i - 1] + 1 : a[i - 1] - 1;
    return a;
  }
  
  public static Double[] rangeDouble(Double start, Double end) {
    // return a Double[] beginning with start and followed by elements
    // successively incremented or decremented by 1 up to but not including end
    if (end == start) return new Double[0];
    Double[] a;
    int len = end > start ? (int)(end - start + 1) : (int)(start - end + 1);
    if (start + len - 1 >= end) len--;
    a = new Double[len];
    
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = end > start ? a[i - 1] + 1 : a[i - 1] - 1;
    return a;
  }
  
  public static Double[] rangeDouble(Double start, Double end, Double step) {
    // return a Double[] beginning with start and followed by elements
    // successively incremented by step up to but not including end
    if (step == 0)
      throw new IllegalArgumentException("range: step cannot be 0");
    int len = (int)((end - start) / step + 1);
    if (start + (len-1)*step >= end) len--;
    if (len <= 0) return new Double[0];
    Double[] a = new Double[len];
    a[0] = start;
    for (int i = 1; i < a.length; i++)
      a[i] = a[i - 1] + step;
    return a;
  }
  
  public static Double[] rangeNumeric(Number start, Number end) {
    // return a Double[] beginning with start.doubleValue() and followed by
    // elements successively incremented or decremented by 1 up to but not 
    // including end.doubleValue()
    if (end == start) return new Double[0];
    Double[] a;
    double endv = end.doubleValue(); double startv = start.doubleValue();
    int len = endv > startv ? (int)(endv - startv + 1) : (int)(startv - endv + 1);
    if (startv + len - 1 >= endv) len--;
    a = new Double[len];
    a[0] = startv;
    for (int i = 1; i < a.length; i++)
      a[i] = endv > startv ? a[i - 1] + 1 : a[i - 1] - 1;
    return a;
  }
  
  public static Double[] rangeNumeric(Number start, Number end, Number step) {
    // return a Double[] beginning with start.doubleValue() and followed by 
    // elements successively incremented by step.doubleValue() up to but not 
    // including end.doubleValue()
    double stepv = step.doubleValue();
    if (stepv == 0)
      throw new IllegalArgumentException("range: step cannot be 0");
    double endv = end.doubleValue(); double startv = start.doubleValue();
    int len = (int)((endv - startv) / stepv + 1);
    if (startv + (len-1)*stepv >= endv) len--;
    if (len <= 0) return new Double[0];
    Double[] a = new Double[len];
    a[0] = startv;
    for (int i = 1; i < a.length; i++)
      a[i] = a[i - 1] + stepv;
    return a;
  }
  
  public static int rank(int[] a, int key) {
    // for sorted array counts number of occurrences of elements < key
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i] < key)
        c++;
    }
    return c;
  }

  public static int rank(long[] a, long key) {
    // for sorted array counts number of occurrences of elements < key
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i] < key)
        c++;
    }
    return c;
  }

  public static int rank(double[] a, double key) {
    // for sorted array counts number of occurrences of elements < key
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i] < key)
        c++;
    }
    return c;
  }

  public static <T extends Comparable<? super T>> int rank(T[] a, T key) {
    // for sorted array counts number of occurrences of elements < key
    int c = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] == key) {
        return c;
      } else if (a[i].compareTo(key) < 0)
        c++;
    }
    return c;
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
    int r = f.applyAsInt(a[a.length - 1], a[a.length - 2]);
    for (int i = a.length - 3; i > 0; i--)
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
    double r = f.apply(a[0] * 1., a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.apply(r, a[i]); //.applyAsInt(r, a[i]);
    return r;
  }

  public static double reduceRight(int[] a, BiFunction<Integer, Double, Integer> f) {
    if (a == null)
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2)
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[a.length - 2], a[a.length - 1] * 1.);
    for (int i = a.length - 3; i > 0; i--)
      r = f.apply(a[i], r); //.applyAsInt(r, a[i]);
    return r;
  }

  public static double reduceLeft(long[] a, BiFunction<Double, Long, Double> f) {
    if (a == null)
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2)
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[0] * 1., a[1]);
    for (int i = 2; i < a.length; i++)
      r = f.apply(r, a[i]); //.applyAsInt(r, a[i]);
    return r;
  }

  public static double reduceRight(long[] a, BiFunction<Long, Double, Long> f) {
    if (a == null)
      throw new IllegalArgumentException("product: a can't be null");
    if (a.length >= 2)
      throw new IllegalArgumentException("product: a's length must be >=2");
    double r = f.apply(a[a.length - 2], a[a.length - 1] * 1.);
    for (int i = a.length - 3; i > 0; i--)
      r = f.apply(a[i], r); //.applyAsInt(r, a[i]);
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
    double r = f.applyAsDouble(a[a.length - 2], a[a.length - 1]);
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
    U r = f.apply(a[a.length - 2], a[a.length - 1]);
    for (int i = a.length - 3; i > 0; i--)
      r = f.apply(a[i], r);
    return r;
  }

  public static byte[] remove(byte[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    byte[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static short[] remove(short[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    short[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static int[] remove(int[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    int[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static long[] remove(long[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    long[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static float[] remove(float[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    float[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static double[] remove(double[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    double[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static char[] remove(char[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    char[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static boolean[] remove(boolean[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    boolean[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static <T> T[] remove(T[] a, int n) {
    // return a new int[] with element at index n removed
    if (a == null)
      throw new IllegalArgumentException("remove: a can't be null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException("remove: a must be >= 0 and < a.length");
    if (a.length == 0 || a.length == 1)
      return copyOf(a, 0);
    T[] b = copyOf(a, a.length - 1);
    int j = 0;
    for (int i = 0; i < n; i++)
      b[j++] = a[i];
    for (int i = n + 1; i < a.length; i++)
      b[j++] = a[i];
    return b;
  }

  public static final String repeat(char c, int length) {
    // this method is to support the box and unbox methods, etc.
    return new String(fillChar(length > 0 ? length : 0, () -> c));
  }
  
  public static final String repeat(String s, int length) {
    // this method is to support the box and unbox methods, etc.
    if (s == null || s.length() == 0) {
      return new String(fillChar(length > 0 ? length : 0, () -> ' '));
    } else {
      return new String(fillChar(length > 0 ? length : 0, () -> s.charAt(0)));
    }    
  }

  public static byte[] reverse(byte[] a) {
    if (a == null)
      return null;
    byte[] b = new byte[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static short[] reverse(short[] a) {
    if (a == null)
      return null;
    short[] b = new short[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static int[] reverse(int[] a) {
    if (a == null)
      return null;
    int[] b = new int[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static long[] reverse(long[] a) {
    if (a == null)
      return null;
    long[] b = new long[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static float[] reverse(float[] a) {
    if (a == null)
      return null;
    float[] b = new float[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static double[] reverse(double[] a) {
    if (a == null)
      return null;
    double[] b = new double[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static char[] reverse(char[] a) {
    if (a == null)
      return null;
    char[] b = new char[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static boolean[] reverse(boolean[] a) {
    if (a == null)
      return null;
    boolean[] b = new boolean[a.length];
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static <T> T[] reverse(T[] a) {
    if (a == null)
      return null;
    if (a.length == 0)
      return copyOf(a, 0);
    T[] b = copyOf(a, a.length);
    if (b.length < 2)
      return b;
    int n = b.length;
    for (int i = 0; i < n / 2; i++) {
      b[i] = a[n - 1 - i];
      b[n - i - 1] = a[i];
    }
    if (n % 2 == 1)
      b[n / 2] = a[n / 2];
    return b;
  }

  public static void reverseInPlace(int[] a) {
    if (a == null || a.length < 2)
      return;
    int n = a.length;
    for (int i = 0; i < n / 2; i++) {
      int temp = a[i];
      a[i] = a[n - 1 - i];
      a[n - i - 1] = temp;
    }
  }

  public static void reverseInPlace(long[] a) {
    if (a == null || a.length < 2)
      return;
    int n = a.length;
    for (int i = 0; i < n / 2; i++) {
      long temp = a[i];
      a[i] = a[n - 1 - i];
      a[n - i - 1] = temp;
    }
  }

  public static void reverseInPlace(double[] a) {
    if (a == null || a.length < 2)
      return;
    int n = a.length;
    for (int i = 0; i < n / 2; i++) {
      double temp = a[i];
      a[i] = a[n - 1 - i];
      a[n - i - 1] = temp;
    }
  }

  public static <T> void reverseInPlace(T[] a) {
    if (a == null || a.length < 2)
      return;
    int n = a.length;
    for (int i = 0; i < n / 2; i++) {
      T temp = a[i];
      a[i] = a[n - 1 - i];
      a[n - i - 1] = temp;
    }
  }

  public static OfInt reverseIterator(int[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static OfLong reverseIterator(long[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static OfDouble reverseIterator(double[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static <T> Iterator<T> reverseIterator(T[] a) {
    return Arrays.stream(reverse(a)).iterator();
  }

  public static int[] reverseMap(int[] a, IntFunction<Integer> f) {
    assert a != null;
    assert a.length != 0;
    int[] b = new int[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length - 1 - i]).intValue();
    return b;
  }

  public static long[] reverseMap(long[] a, LongFunction<Long> f) {
    assert a != null;
    assert a.length != 0;
    long[] b = new long[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length - 1 - i]).longValue();
    return b;
  }

  public static double[] reverseMap(double[] a, DoubleFunction<Double> f) {
    assert a != null;
    assert a.length != 0;
    double[] b = new double[a.length];
    for (int i = 0; i < a.length; i++)
      b[i] = f.apply(a[a.length - 1 - i]).doubleValue();
    return b;
  }

  public static <R> R[] reverseMap(int[] a, Function<Integer, R> f) {
    assert a != null;
    assert a.length != 0;
    R[] r = ofDim(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[a.length - 1 - i]);
    return r;
  }

  public static <R> R[] reverseMap(long[] a, Function<Long, R> f) {
    assert a != null;
    assert a.length != 0;
    R[] r = ofDim(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[a.length - 1 - i]);
    return r;
  }

  public static <R> R[] reverseMap(double[] a, Function<Double, R> f) {
    assert a != null;
    assert a.length != 0;
    R[] r = ofDim(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[a.length - 1 - i]);
    return r;
  }

  public static <T, R> R[] reverseMap(T[] a, Function<T, R> f) {
    assert a != null;
    assert a.length != 0;
    R[] r = ofDim(f.apply(a[0]).getClass(), a.length);
    for (int i = 0; i < a.length; i++)
      r[i] = f.apply(a[a.length - 1 - i]);
    return r;
  }
  
  public static void reverseShellSort(byte[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    byte t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void reverseShellSort(short[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    short t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void reverseShellSort(int[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    int t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void reverseShellSort(long[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    long t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void reverseShellSort(float[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    float t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void reverseShellSort(double[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    double t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void reverseShellSort(char[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    char t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] > a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void reverseShellSort(boolean[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    boolean t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] == true && a[j-h] == false; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static <T extends Comparable<? super T>> void reverseShellSort(T[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    T t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j].compareTo(a[j-h]) > 0; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }


  public static boolean sameElements(int[] a, int[] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(long[] a, long[] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(double[] a, double[] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static <T> boolean sameElements(T[] a, T[] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(int[][] a, int[][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(long[][] a, long[][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(double[][] a, double[][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static <T> boolean sameElements(T[][] a, T[][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(int[][][] a, int[][][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(long[][][] a, long[][][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static boolean sameElements(double[][][] a, double[][][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static <T> boolean sameElements(T[][][] a, T[][][] b) {
    if (a == null && b == null)
      throw new IllegalArgumentException("" + "sameElements: both arrays can't be null");
    return equals(a, b);
  }

  public static int[] scanLeft(int[] a, B1<Integer, Integer> op, int z) {
    // this returns an int[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any int.
    if (a == null)
      throw new IllegalArgumentException("scanLeft: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    int[] result = new int[a.length + 1];
    result[0] = z;
    int r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i + 1] = r;
    }
    return result;
  }

  public static long[] scanLeft(long[] a, B1<Long, Long> op, long z) {
    // this returns a long[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // index 1 forward. z can be any long.
    if (a == null)
      throw new IllegalArgumentException("scanLeft: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    long[] result = new long[a.length + 1];
    result[0] = z;
    long r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i + 1] = r;
    }
    return result;
  }

  public static double[] scanLeft(double[] a, B1<Double, Double> op, double z) {
    // this returns a double[] initialized with z at index 0 and then
    // folds from the left inserting intermediate and final results
    // from index 1 forward. z can be any double.
    if (a == null)
      throw new IllegalArgumentException("scanLeft: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    double[] result = new double[a.length + 1];
    result[0] = z;
    double r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i + 1] = r;
    }
    return result;
  }

  public static <T, R> R[] scanLeft(T[] a, B1<T, R> op, R z) {
    // this returns an R[] initialized with z at index 0 and then 
    // folds from the left inserting intermediate and final results
    // from index 1 forward into that array. z can be any T.
    if (a == null || a.length == 0 || allNull(a))
      throw new IllegalArgumentException(
          "scanLeft: the array cannot be null, have length of 0 or have all null values");
    R[] result = ofDim(op.apply(z, findFirstNonNull(a)._1.get()).getClass(), a.length + 1);
    result[0] = z;
    R r = z;
    for (int i = 0; i < a.length; i++) {
      r = op.apply(r, a[i]);
      result[i + 1] = op.apply(r, a[i]);
    }
    return result;
  }

  public static int[] scanRight(int[] a, C1<Integer, Integer> op, int z) {
    // this returns an int[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any int.
    if (a == null)
      throw new IllegalArgumentException("scanRight: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    int[] result = copyOf(a, a.length + 1);
    int r = z;
    result[result.length - 1] = z;
    for (int i = a.length - 1; i > -1; i--) {
      r = op.apply(a[i], r);
      result[i] = r;
    }
    return result;
  }

  public static long[] scanRight(long[] a, C1<Long, Long> op, long z) {
    // this returns a long[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // results from index t.length-2 backward into that array. z can
    // be any long.
    if (a == null)
      throw new IllegalArgumentException("scanRight: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    long[] result = copyOf(a, a.length + 1);
    long r = z;
    result[result.length - 1] = z;
    for (int i = a.length - 1; i > -1; i--) {
      r = op.apply(a[i], r);
      result[i] = r;
    }
    return result;
  }

  public static double[] scanRight(double[] a, C1<Double, Double> op, double z) {
    // this returns a double[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and final
    // final results from index t.length-2 backward into that array.
    // z can be any double.
    if (a == null)
      throw new IllegalArgumentException("scanRight: the array cannot be null");
    if (a.length == 0)
      return copyOf(a, 0);
    double[] result = copyOf(a, a.length + 1);
    double r = z;
    result[result.length - 1] = z;
    for (int i = a.length - 1; i > -1; i--) {
      r = op.apply(a[i], r);
      result[i] = r;
    }
    return result;
  }

  public static <R, T> R[] scanRight(T[] a, C1<T, R> op, R z) {
    // this returns a T[] initialized with z at index t.length-1
    // and then folds from the right inserting intermediate and 
    // final results from index t.length-2 backward into that
    // array. z can be any T.
    if (a == null || a.length == 0 || allNull(a))
      throw new IllegalArgumentException(
          "scanLeft: the array cannot be null, have length of 0 or have all null values");
    R[] result = ofDim(op.apply(findFirstNonNull(a)._1.get(), z).getClass(), a.length + 1);
    R r = z;
    result[result.length - 1] = z;
    for (int i = a.length - 1; i > -1; i--) {
      r = op.apply(a[i], r);
      result[i] = r;
    }
    return result;
  }

  public static int segmentLength(int[] a, Predicate<Integer> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length - 1)
      return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++;
        continue;
      } else if (started && !p.test(a[i])) {
        if (c > max)
          max = c;
        started = false;
        c = 0;
        continue;
      } else if (!started && p.test(a[i])) {
        started = true;
        c++;
        continue;
      }
    }
    return max;
  }

  public static int segmentLength(long[] a, Predicate<Long> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length - 1)
      return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++;
        continue;
      } else if (started && !p.test(a[i])) {
        if (c > max)
          max = c;
        started = false;
        c = 0;
        continue;
      } else if (!started && p.test(a[i])) {
        started = true;
        c++;
        continue;
      }
    }
    return max;
  }

  public static int segmentLength(double[] a, Predicate<Double> p, int s) {
    // returns the length of the longest segment satisfying p 
    // starting at index s.
    if (a == null || a.length == 0 || s > a.length - 1)
      return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++;
        continue;
      } else if (started && !p.test(a[i])) {
        if (c > max)
          max = c;
        started = false;
        c = 0;
        continue;
      } else if (!started && p.test(a[i])) {
        started = true;
        c++;
        continue;
      }
    }
    return max;
  }

  public static <T> int segmentLength(T[] a, Predicate<T> p, int s) {
    // returns the length of the longest segment
    // satisfying p starting at index s.
    if (a == null || a.length == 0 || s > a.length - 1)
      return 0;
    int max = 0;
    int c = 0;
    boolean started = false;
    for (int i = s; i < a.length; i++) {
      if (started && p.test(a[i])) {
        c++;
        continue;
      } else if (started && !p.test(a[i])) {
        if (c > max) {
          max = c;
          started = false;
          c = 0;
          continue;
        }
      } else if (!started && p.test(a[i])) {
        started = true;
        c++;
        continue;
      }
    }
    return max;
  }

  public static void shift(byte[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(short[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(int[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(long[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(float[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(double[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = 0;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = 0;
    }
  }

  public static void shift(char[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = '0';
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = '0';
    }
  }

  public static void shift(boolean[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = false;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = false;
    }
  }

  public static <T> void shift(T[] a, int n) {
    // shift each element in a by at most n positions in place 
    // dropping elements on either end depending on the sign of k
    if (a == null || a.length == 0 || n == 0)
      return;
    int k = n > 0 ? n : -n;
    if (k > a.length)
      k = a.length;
    if (n > 0) {
      for (int i = a.length - k - 1; i > -1; i--)
        a[i + k] = a[i];
      for (int i = 0; i < k; i++)
        a[i] = null;
    } else {
      for (int i = k; i < a.length; i++)
        a[i - k] = a[i];
      for (int i = a.length - k; i < a.length; i++)
        a[i] = null;
    }
  }

  public static void shuffle(byte[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j; byte t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(short[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j; short t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(int[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j, t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(long[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j; long t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(float[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j; float t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(double[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
          + "both the array and the Random argument must not be null");
    int n = a.length, j; double t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(char[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
          + "both the array and the Random argument must not be null");
    int n = a.length, j; char t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static void shuffle(boolean[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
          + "both the array and the Random argument must not be null");
    int n = a.length, j; boolean t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
  }

  public static <T> void shuffle(T[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle " 
        + "both the array and the Random argument must not be null");
    int n = a.length, j; T t;
    for (int i=n; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
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
    if (a == null)
      throw new IllegalArgumentException("slice: " + "a = null but it must be non null");
    if (from < 0)
      throw new IllegalArgumentException("slice: " + "from=" + from + " but it must be >= 0");
    if (until <= from || until > a.length)
      throw new IllegalArgumentException(
          "slice: " + "until=" + until + " but it must be > from=" + from + " and <= a.length=" + a.length);
    int[] b = new int[until - from];
    for (int i = from; i < until; i++)
      b[i - from] = a[i];
    return b;
  }

  public static double[] slice(long[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null)
      throw new IllegalArgumentException("slice: " + "a = null but it must be non null");
    if (from < 0)
      throw new IllegalArgumentException("slice: " + "from=" + from + " but it must be >= 0");
    if (until <= from || until > a.length)
      throw new IllegalArgumentException(
          "slice: " + "until=" + until + " but it must be > from=" + from + " and <= a.length=" + a.length);
    double[] b = new double[until - from];
    for (int i = from; i < until; i++)
      b[i - from] = a[i];
    return b;
  }

  public static double[] slice(double[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null)
      throw new IllegalArgumentException("slice: " + "a = null but it must be non null");
    if (from < 0)
      throw new IllegalArgumentException("slice: " + "from=" + from + " but it must be >= 0");
    if (until <= from || until > a.length)
      throw new IllegalArgumentException(
          "slice: " + "until=" + until + " but it must be > from=" + from + " and <= a.length=" + a.length);
    double[] b = new double[until - from];
    for (int i = from; i < until; i++)
      b[i - from] = a[i];
    return b;
  }

  public static <T> T[] slice(T[] a, int from, int until) {
    // returns an array of the same type as a and containing
    // the elements in a with indices >= from and < until
    if (a == null)
      throw new IllegalArgumentException("slice: " + "a = null but it must be non null");
    if (from < 0)
      throw new IllegalArgumentException("slice: " + "from=" + from + " but it must be >= 0");
    if (until <= from || until > a.length)
      throw new IllegalArgumentException(
          "slice: " + "until=" + until + " but it must be > from=" + from + " and <= a.length=" + a.length);
    T[] b = ofDim(a.getClass().getComponentType(), until - from);
    for (int i = from; i < until; i++)
      b[i - from] = a[i];
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
      if (a == null)
        throw new IllegalArgumentException("SlidingInt: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingInt: " + "size=" + size + " but it must be > 0");
      if (step < 1)
        throw new IllegalArgumentException("SlidingInt: " + "step=" + step + " it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }

    SlidingInt(int[] a, int size) {
      if (a == null)
        throw new IllegalArgumentException("SlidingInt: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingInt: " + "size=" + size + " but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }

    @Override
    public boolean hasNext() {
      return current + size <= n;
    }

    @Override
    public int[] next() {
      t = new int[size];
      for (int j = 0; j < size; j++)
        t[j] = a[current + j];
      current += step;
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
      if (a == null)
        throw new IllegalArgumentException("SlidingLong: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingLong: " + "size=" + size + " but it must be > 0");
      if (step < 1)
        throw new IllegalArgumentException("SlidingLong: " + "step=" + step + " it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }

    SlidingLong(long[] a, int size) {
      if (a == null)
        throw new IllegalArgumentException("SlidingLong: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingLong: " + "size=" + size + " but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }

    @Override
    public boolean hasNext() {
      return current + size <= n;
    }

    @Override
    public long[] next() {
      t = new long[size];
      for (int j = 0; j < size; j++)
        t[j] = a[current + j];
      current += step;
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
      if (a == null)
        throw new IllegalArgumentException("SlidingDouble: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingDouble: " + "size=" + size + " but it must be > 0");
      if (step < 1)
        throw new IllegalArgumentException("SlidingDouble: " + "step=" + step + " it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }

    SlidingDouble(double[] a, int size) {
      if (a == null)
        throw new IllegalArgumentException("SlidingDouble: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("SlidingDouble: " + "size=" + size + " but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }

    @Override
    public boolean hasNext() {
      return current + size <= n;
    }

    @Override
    public double[] next() {
      t = new double[size];
      for (int j = 0; j < size; j++)
        t[j] = a[current + j];
      current += step;
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
      if (a == null)
        throw new IllegalArgumentException("Sliding: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("Sliding: " + "size=" + size + " but it must be > 0");
      if (step < 1)
        throw new IllegalArgumentException("Sliding: " + "step=" + step + " it must be > 0");
      this.a = a;
      this.size = size;
      this.step = step;
      this.n = a.length;
    }

    Sliding(T[] a, int size) {
      if (a == null)
        throw new IllegalArgumentException("Sliding: " + "a = null but it must be non null");
      if (size < 1)
        throw new IllegalArgumentException("Sliding: " + "size=" + size + " but it must be > 0");
      this.a = a;
      this.size = size;
      this.step = 1;
      this.n = a.length;
    }

    @Override
    public boolean hasNext() {
      return current + size <= n;
    }

    public T[] next() {
      t = ofDim(a.getClass().getComponentType(), size);
      for (int j = 0; j < size; j++)
        t[j] = a[current + j];
      current += step;
      return t;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public static Iterator<int[]> sliding(int[] a, int size, int step) {
    // helper method for slidingStream(int[])
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    return new SlidingInt(a, size, step);
  }

  public static Iterator<int[]> sliding(int[] a, int size) {
    // helper method for slidingStream(int[])
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    return new SlidingInt(a, size);
  }

  public static Iterator<long[]> sliding(long[] a, int size, int step) {
    // helper method for slidingStream(long[])
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    return new SlidingLong(a, size, step);
  }

  public static Iterator<long[]> sliding(long[] a, int size) {
    // helper method for slidingStream(long[])
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    return new SlidingLong(a, size);
  }

  public static Iterator<double[]> sliding(double[] a, int size, int step) {
    // helper method for slidingStream(double[])
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    return new SlidingDouble(a, size, step);
  }

  public static Iterator<double[]> sliding(double[] a, int size) {
    // helper method for slidingStream(double[])
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    return new SlidingDouble(a, size);
  }

  public static <T> Iterator<T[]> sliding(T[] a, int size, int step) {
    // helper method for slidingStream(T[])
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    return new Sliding<T>(a, size, step);
  }

  public static <T> Iterator<T[]> sliding(T[] a, int size) {
    // helper method for slidingStream(T[])
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    return new Sliding<T>(a, size);
  }

  public static Stream<int[]> slidingStream(int[] a, int size, int step) {
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    Iterator<int[]> i = sliding(a, size, step);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, step));
  }

  public static Stream<int[]> slidingStream(int[] y, int size) {
    // step defaults to 1
    if (y == null)
      throw new IllegalArgumentException("slidingStream: y can't be null");
    Iterator<int[]> i = sliding(y, size);
    return Stream.generate(() -> i.next()).limit(slidingCount(y.length, size, 1));
  }

  public static Stream<long[]> slidingStream(long[] a, int size, int step) {
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    Iterator<long[]> i = sliding(a, size, step);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, step));
  }

  public static Stream<long[]> slidingStream(long[] a, int size) {
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    Iterator<long[]> i = sliding(a, size);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, 1));
  }

  public static Stream<double[]> slidingStream(double[] a, int size, int step) {
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    Iterator<double[]> i = sliding(a, size, step);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, step));
  }

  public static Stream<double[]> slidingStream(double[] a, int size) {
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    Iterator<double[]> i = sliding(a, size);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, 1));
  }

  public static <T> Stream<T[]> slidingStream(T[] a, int size, int step) {
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    if (step < 1)
      throw new IllegalArgumentException("sliding: " + "step=" + step + " it must be > 0");
    Iterator<T[]> i = sliding(a, size, step);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, step));
  }

  public static <T> Stream<T[]> slidingStream(T[] a, int size) {
    // step defaults to 1
    if (a == null)
      throw new IllegalArgumentException("sliding: " + "a = null but it must be non null");
    if (size < 1)
      throw new IllegalArgumentException("sliding: " + "size=" + size + " but it must be > 0");
    Iterator<T[]> i = sliding(a, size);
    return Stream.generate(() -> i.next()).limit(slidingCount(a.length, size, 1));
  }

  public static int slidingCount(int n, int size, int step) {
    // count number of sliding windows for array length n, size and step
    if (n < 0 || size < 1 || step < 1)
      throw new IllegalArgumentException("slidingCount: " + "n=" + n + ", size=" + size + " and step=" + step
          + " but requires n >= 0, " + "size > 0 and step > 0");
    int i = 0;
    int c = 0;
    while (c + size <= n) {
      i++;
      c += step;
    }
    return i;
  }
  
  public static <U extends Comparable<? super U>> int[] sortBy(int[] a, Function<? super Integer, ? extends U> e,
      Comparator<? super U> c) {
    // example usage:
    // int[] c = {1,2,3,4,5};
    // String[] d = {"B","A","E","D","C"};
    // printArray(sortBy(c,x->d[(d.length+2)%x],(s,t)->s.compareTo(t)));
    // result: [2,3,1,4,5]
    if (a == null || e == null || c == null)
      throw new IllegalArgumentException("sortBy: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    return (int[]) unbox(sortBy((Integer[]) box(a), e, c));
  }

  public static <U extends Comparable<? super U>> long[] sortBy(long[] a, Function<? super Long, ? extends U> e,
      Comparator<? super U> c) {
    if (a == null || e == null || c == null)
      throw new IllegalArgumentException("sortBy: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    return (long[]) unbox(sortBy((Long[]) box(a), e, c));
  }

  public static <U extends Comparable<? super U>> double[] sortBy(double[] a, Function<? super Double, ? extends U> e,
      Comparator<? super U> c) {
    if (a == null || e == null || c == null)
      throw new IllegalArgumentException("sortBy: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    return (double[]) unbox(sortBy((Double[]) box(a), e, c));
  }

  public static <T, U extends Comparable<? super U>> T[] sortBy(T[] a, Function<? super T, ? extends U> e,
      Comparator<? super U> c) {
    // example usage:
    // Integer[] c = {1,2,3,4,5};
    // String[] d = {"B","A","E","D","C"};
    // printArray(sortBy(c,x->d[(d.length+2)%x],(s,t)->t.compareTo(s)));
    // result: [5,4,1,2,3]
    if (a == null || e == null || c == null)
      throw new IllegalArgumentException("sortBy: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    T[] b = copyOf(a, a.length);
    Arrays.sort(b, comparing(e, c));
    return b;
  }

  public static int[] sortWith(int[] a, BiPredicate<Integer, Integer> b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("sortWith: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    Integer[] d = (Integer[]) box(a);
    Arrays.sort(d, (t1, t2) -> t1.equals(t2) ? 0 : (b.test(t1, t2) ? -1 : 1));
    return (int[]) unbox(d);
  }

  public static long[] sortWith(long[] a, BiPredicate<Long, Long> b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("sortWith: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    Long[] d = (Long[]) box(a);
    Arrays.sort(d, (t1, t2) -> t1.equals(t2) ? 0 : (b.test(t1, t2) ? -1 : 1));
    return (long[]) unbox(d);
  }

  public static double[] sortWith(double[] a, BiPredicate<Double, Double> b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("sortWith: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    Double[] d = (Double[]) box(a);
    Arrays.sort(d, (t1, t2) -> t1.equals(t2) ? 0 : (b.test(t1, t2) ? -1 : 1));
    return (double[]) unbox(d);
  }

  public static <T> T[] sortWith(T[] a, BiPredicate<T, T> b) {
    if (a == null || b == null)
      throw new IllegalArgumentException("sortWith: " + "all arguments must be non null");
    if (a.length < 2)
      return copyOf(a, a.length);
    T[] d = copyOf(a, a.length);
    Arrays.sort(d, (t1, t2) -> t1.equals(t2) ? 0 : (b.test(t1, t2) ? -1 : 1));
    return d;
  }

  public static int[] sorted(int[] a, Comparator<? super Integer> c) {
    if (a == null || c == null)
      throw new IllegalArgumentException("sorted: " + "all arguments must be non null");
    Integer[] d = (Integer[]) box(a);
    Arrays.sort(d, c);
    return (int[]) unbox(d);
  }

  public static long[] sorted(long[] a, Comparator<? super Long> c) {
    if (a == null || c == null)
      throw new IllegalArgumentException("sorted: " + "all arguments must be non null");
    Long[] d = (Long[]) box(a);
    Arrays.sort(d, c);
    return (long[]) unbox(d);
  }

  public static double[] sorted(double[] a, Comparator<? super Double> c) {
    if (a == null || c == null)
      throw new IllegalArgumentException("sorted: " + "all arguments must be non null");
    Double[] d = (Double[]) box(a);
    Arrays.sort(d, c);
    return (double[]) unbox(d);
  }

  public static <T> T[] sorted(T[] a, Comparator<? super T> c) {
    if (a == null || c == null)
      throw new IllegalArgumentException("sorted: " + "all arguments must be non null");
    T[] d = copyOf(a, a.length);
    Arrays.sort(d, c);
    return d;
  }
  
  public static final String space(int length) {
    // this method is to support the box and unbox methods, etc.
    return new String(fillChar(length > 0 ? length : 0, () -> ' '));
  }

  public static int[][] span(int[] a, Predicate<Integer> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a == null)
      throw new IllegalArgumentException("span: " + "the array must be non null");
    int[] b = new int[a.length];
    int bindex = 0;
    int[] c = new int[a.length];
    int cindex = 0;
    Integer[] d = (Integer[]) box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    int[][] r = new int[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static long[][] span(long[] a, Predicate<Long> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a == null)
      throw new IllegalArgumentException("span: " + "the array must be non null");
    long[] b = new long[a.length];
    int bindex = 0;
    long[] c = new long[a.length];
    int cindex = 0;
    Long[] d = (Long[]) box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    long[][] r = new long[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static double[][] span(double[] a, Predicate<Double> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a == null)
      throw new IllegalArgumentException("span: " + "the array must be non null");
    double[] b = new double[a.length];
    int bindex = 0;
    double[] c = new double[a.length];
    int cindex = 0;
    Double[] d = (Double[]) box(a);
    for (int i = 0; i < d.length; i++)
      if (p.test(d[i])) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    double[][] r = new double[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static <T> T[][] span(T[] a, Predicate<T> p) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a for which p is true and the second row contains those
    // values for which p is false.
    if (a == null)
      throw new IllegalArgumentException("span: " + "the array must be non null");
    T[] b = ofDim(a.getClass().getComponentType(), a.length);
    int bindex = 0;
    T[] c = ofDim(a.getClass().getComponentType(), a.length);
    int cindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    T[][] r = ofDim(a.getClass().getComponentType(), 2, 0);
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static int[][] splitAt(int[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a == null)
      throw new IllegalArgumentException("splitAt: " + "the array must be non null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException(
          "splitAt: " + "the int index n must be >= 0 and less than the array's length ");
    int[] b = new int[a.length];
    int bindex = 0;
    int[] c = new int[a.length];
    int cindex = 0;
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    int[][] r = new int[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static long[][] splitAt(long[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a == null)
      throw new IllegalArgumentException("splitAt: " + "the array must be non null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException(
          "splitAt: " + "the int index n must be >= 0 and less than the array's length ");
    long[] b = new long[a.length];
    int bindex = 0;
    long[] c = new long[a.length];
    int cindex = 0;
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    long[][] r = new long[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static double[][] splitAt(double[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a == null)
      throw new IllegalArgumentException("splitAt: " + "the array must be non null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException(
          "splitAt: " + "the int index n must be >= 0 and less than the array's length ");
    double[] b = new double[a.length];
    int bindex = 0;
    double[] c = new double[a.length];
    int cindex = 0;
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    double[][] r = new double[2][];
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static <T> T[][] splitAt(T[] a, int n) {
    // this returns a 2D array with 2 rows. the first row is filled with
    // values of a with indices < n and the second row contains those
    // with indices >= n.
    if (a == null)
      throw new IllegalArgumentException("splitAt: " + "the array must be non null");
    if (n < 0 || n > a.length - 1)
      throw new IllegalArgumentException(
          "splitAt: " + "the int index n must be >= 0 and less than the array's length ");
    T[] b = ofDim(a.getClass().getComponentType(), a.length);
    int bindex = 0;
    T[] c = ofDim(a.getClass().getComponentType(), a.length);
    int cindex = 0;
    for (int i = 0; i < a.length; i++)
      if (i < n) {
        b[bindex++] = a[i];
      } else
        c[cindex++] = a[i];
    T[][] r = ofDim(a.getClass().getComponentType(), 2, 0);
    r[0] = take(b, bindex);
    r[1] = take(c, cindex);
    return r;
  }

  public static boolean startsWith(int[] a, int offset, int... b) {
    // returns true if a contains b starting at offset
    if (a == null || a.length == 0 || offset < 0 | b.length == 0 || offset + b.length > a.length)
      return false;
    for (int i = offset; i < offset + b.length; i++)
      if (a[i] != b[i - offset])
        return false;
    return true;
  }

  public static boolean startsWith(long[] a, int offset, long... b) {
    // returns true if a contains b starting at offset
    if (a == null || a.length == 0 || offset < 0 | b.length == 0 || offset + b.length > a.length)
      return false;
    for (int i = offset; i < offset + b.length; i++)
      if (a[i] != b[i - offset])
        return false;
    return true;
  }

  public static boolean startsWith(double[] a, int offset, double... b) {
    // returns true if a contains b starting at offset
    if (a == null || a.length == 0 || offset < 0 | b.length == 0 || offset + b.length > a.length)
      return false;
    for (int i = offset; i < offset + b.length; i++)
      if (a[i] != b[i - offset])
        return false;
    return true;
  }

  @SafeVarargs
  public static <T> boolean startsWith(T[] a, int offset, T... b) {
    // returns true if a contains b starting at offset
    if (a == null || a.length == 0 || offset < 0 | b.length == 0 || offset + b.length > a.length)
      return false;
    for (int i = offset; i < offset + b.length; i++)
      if (a[i] != b[i - offset])
        return false;
    return true;
  }

  public static String stringPrefix(Object o) {
    if (o == null)
      throw new IllegalArgumentException("stringPrefix: " + "argument can't be null");
    return o.getClass().getName();
  }

  public static double stddev(int[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddev: " + "the array must not be null or have zero length");
    return Math.sqrt(var(a));
  }

  public static double stddev(long[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddev: " + "the array must not be null or have zero length");
    return Math.sqrt(var(a));
  }

  public static double stddev(double[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddev: " + "the array must not be null or have zero length");
    return Math.sqrt(var(a));
  }

  public static double stddevp(int[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddevp: " + "the array must not be null or have zero length");
    return Math.sqrt(varp(a));
  }

  public static double stddevp(long[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddevp: " + "the array must not be null or have zero length");
    return Math.sqrt(varp(a));
  }

  public static double stddevp(double[] a) {
    // returns the population standard deviation of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("stddevp: " + "the array must not be null or have zero length");
    return Math.sqrt(varp(a));
  }

  public static CharSequence subSequence(char[] a, int start, int end) {
    // returns a CharSequence of chars of a from index start through index
    // end-1, however if index < 0 it's corrected to 0 and |index| is added 
    // to end; and if end > a.length it's corrected to a.length
    if (a == null)
      throw new IllegalArgumentException("subSequence: " + "argument can't be null");
    if (end <= start || start > a.length)
      return "".subSequence(0, 0);
    if (start < 0) {
      end -= start;
      start = 0;
    }
    if (end > a.length)
      end = a.length;
    char[] b = new char[end - start];
    for (int i = start; i < b.length; i++)
      b[i - start] = a[i];
    return String.valueOf(b).subSequence(0, b.length);
  }

  public static CharSequence subSequence(Character[] a, int start, int end) {
    // returns a CharSequence of chars of a from index start through index
    // end-1, however if index < 0 it's corrected to 0 and |index| is added
    // to end; and if end > a.length it's corrected to a.length
    if (a == null)
      throw new IllegalArgumentException("subSequence: " + "argument can't be null");
    if (end <= start || start > a.length)
      return "".subSequence(0, 0);
    if (start < 0) {
      end -= start;
      start = 0;
    }
    if (end > a.length)
      end = a.length;
    char[] b = (char[]) unbox(a);
    char[] c = new char[end - start];
    for (int i = start; i < c.length; i++)
      c[i - start] = b[i];
    return String.valueOf(c).subSequence(0, c.length);
  }

  public static long sum(int[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }
  
  public static long sum(Integer[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }

  public static long sum(long[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }
  
  public static long sum(Long[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }

  public static double sum(double[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }
  
  public static double sum(Double[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
  }

  public static BigInteger sum(BigInteger[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      sum = sum.add(a[i]);
    return sum;
  }

  public static BigDecimal sum(BigDecimal[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      sum = sum.add(a[i]);
    return sum;
  }

  public static long sum(int[][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum;
  }

  public static long sum(long[][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum;
  }

  public static double sum(double[][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum += a[i][j];
    return sum;
  }

  public static BigInteger sum(BigInteger[][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j]);
    return sum;
  }

  public static BigDecimal sum(BigDecimal[][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        sum = sum.add(a[i][j]);
    return sum;
  }

  public static long sum(int[][][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum;
  }

  public static long sum(long[][][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    long sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum;
  }

  public static double sum(double[][][] a) {
    // return an array with all elements of a except the first
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum += a[i][j][k];
    return sum;
  }

  public static BigInteger sum(BigInteger[][][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigInteger sum = BigInteger.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k]);
    return sum;
  }

  public static BigDecimal sum(BigDecimal[][][] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    BigDecimal sum = BigDecimal.ZERO;
    for (int i = 0; i < a.length; i++)
      for (int j = 0; j < a[i].length; j++)
        for (int k = 0; k < a[i][j].length; k++)
          sum = sum.add(a[i][j][k]);
    return sum;
  }
  
  public static void shellSort(byte[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    byte t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void shellSort(short[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    short t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void shellSort(int[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    int t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void shellSort(long[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    long t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void shellSort(float[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    float t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void shellSort(double[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    double t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void shellSort(char[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    char t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void shellSort(boolean[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    boolean t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] == false && a[j-h] == true; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static <T extends Comparable<? super T>> void shellSort(T[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    T t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j].compareTo(a[j-h]) < 0; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }
  
  public static void show(byte[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(short[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(int[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(long[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(float[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(double[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(char[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static void show(boolean[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static <T> void show(T[] z) {
    // print z to stdout
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public static <T> void showIdentityHashCodes(T[] z) {
    // print the identity hash codes of the elements of z
    if (z == null) throw new IllegalArgumentException("showIdentityHashCodes: z can't be null");
    for (T t : z) System.out.print(identityHashCode(t)+" ");  System.out.println();
  }

  public static <T> T[] tabulate(int n, Function<Integer, T> f) {
    T[] a = ofDim(f.apply(1).getClass(), n);
    for (int i = 0; i < a.length; i++)
      a[i] = f.apply(i);
    return a;
  }

  public static <T> T[][] tabulate(int n1, int n2, BiFunction<Integer, Integer, T> f) {
    T[][] a = ofDim(f.apply(1, 2).getClass(), n1, n2);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        a[i][j] = f.apply(i, j);
    return a;
  }

  public static <T> T[][][] tabulate(int n1, int n2, int n3, TriFunction<Integer, Integer, Integer, T> f) {
    T[][][] a = ofDim(f.apply(1, 2, 3).getClass(), n1, n2, n3);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          a[i][j][k] = f.apply(i, j, k);
    return a;
  }

  public static <T> T[][][][] tabulate(int n1, int n2, int n3, int n4,
      QuadFunction<Integer, Integer, Integer, Integer, T> f) {
    T[][][][] a = ofDim(f.apply(1, 2, 3, 4).getClass(), n1, n2, n3, n4);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            a[i][j][k][l] = f.apply(i, j, k, l);
    return a;
  }

  public static <T> T[][][][][] tabulate(int n1, int n2, int n3, int n4, int n5,
      PentFunction<Integer, Integer, Integer, Integer, Integer, T> f) {
    T[][][][][] a = ofDim(f.apply(1, 2, 3, 4, 5).getClass(), n1, n2, n3, n4, n5);
    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; m < n5; m++)
              a[i][j][k][l][m] = f.apply(i, j, k, l, m);
    return a;
  }

  public static int[] tail(int[] a) {
    // return an array with all elements of a except the first
    if (a == null)
      throw new IllegalArgumentException("tail: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tail: array must have at least one element");
    return copyOfRange(a, 1, a.length);
  }

  public static long[] tail(long[] a) {
    // return an array with all elements of a except the first
    if (a == null)
      throw new IllegalArgumentException("tail: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tail: array must have at least one element");
    return copyOfRange(a, 1, a.length);
  }

  public static double[] tail(double[] a) {
    // return an array with all elements of a except the first
    if (a == null)
      throw new IllegalArgumentException("tail: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tail: array must have at least one element");
    return copyOfRange(a, 1, a.length);
  }

  public static <T> T[] tail(T[] a) {
    // return an array with all elements of a except the first
    if (a == null)
      throw new IllegalArgumentException("tail: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tail: array must have at least one element");
    return copyOfRange(a, 1, a.length);
  }

  public static Iterator<int[]> tails(int[] a) {
    // return an iterator over the tails of a starting with it
    // e.g. Iterator<int[]> it = tails(new int[]{1,2,3});
    //      while(it.hasNext()) pa(it.next());
    // prints: int[1,2,3]
    //         int[2,3]
    //         int[3]
    //         int[]

    if (a == null)
      throw new IllegalArgumentException("tails: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tails: array must have at least one element");
    int[] b = prepend(a, 1);
    int[][] c = new int[1][];
    c[0] = b;
    return Stream.generate(() -> {
      c[0] = tail(c[0]);
      return c[0];
    }).limit(a.length + 1).iterator();
  }

  public static Iterator<long[]> tails(long[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null)
      throw new IllegalArgumentException("tails: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tails: array must have at least one element");
    long[] b = prepend(a, 1);
    long[][] c = new long[1][];
    c[0] = b;
    return Stream.generate(() -> {
      c[0] = tail(c[0]);
      return c[0];
    }).limit(a.length + 1).iterator();
  }

  public static Iterator<double[]> tails(double[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null)
      throw new IllegalArgumentException("tails: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tails: array must have at least one element");
    double[] b = prepend(a, 1);
    double[][] c = new double[1][];
    c[0] = b;
    return Stream.generate(() -> {
      c[0] = tail(c[0]);
      return c[0];
    }).limit(a.length + 1).iterator();
  }

  public static <T> Iterator<T[]> tails(T[] a) {
    // return an iterator over the tails of a starting with it
    if (a == null)
      throw new IllegalArgumentException("tails: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("tails: array must have at least one element");
    T[] b = prepend(a, a[0]);
    T[][] c = ofDim(a.getClass().getComponentType(), 1, b.length);
    c[0] = b;
    return Stream.generate(() -> {
      c[0] = tail(c[0]);
      return c[0];
    }).limit(a.length + 1).iterator();
  }

  public static byte[] take(byte[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (byte[]) clone(a);
    if (n <= 0)
      return new byte[0];
    byte[] b = new byte[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static short[] take(short[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (short[]) clone(a);
    if (n <= 0)
      return new short[0];
    short[] b = new short[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static int[] take(int[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (int[]) clone(a);
    if (n <= 0)
      return new int[0];
    int[] b = new int[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static long[] take(long[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (long[]) clone(a);
    if (n <= 0)
      return new long[0];
    long[] b = new long[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static float[] take(float[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (float[]) clone(a);
    if (n <= 0)
      return new float[0];
    float[] b = new float[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static double[] take(double[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (double[]) clone(a);
    if (n <= 0)
      return new double[0];
    double[] b = new double[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static char[] take(char[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (char[]) clone(a);
    if (n <= 0)
      return new char[0];
    char[] b = new char[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static boolean[] take(boolean[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (boolean[]) clone(a);
    if (n <= 0)
      return new boolean[0];
    boolean[] b = new boolean[n];
    for (int i = 0; i < n; i++)
      b[i] = a[i];
    return b;
  }

  public static <T> T[] take(T[] a, int n) {
    // return an array with the first n elements
    if (a == null)
      throw new IllegalArgumentException("take: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("take: array must have at least one element");
    if (n >= a.length)
      return (T[]) a.clone();
    if (n <= 0)
      return copyOf(a, 0);
    return copyOf(a, n);
  }
  
  public static byte[] takeRight(byte[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) copyOf(a, a.length);
    if (n <= 0) return new byte[0];
    return copyOfRange(a, a.length - n, a.length);
  }
  
  public static short[] takeRight(short[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return new short[0];
    return copyOfRange(a, a.length - n, a.length);
  }

  public static int[] takeRight(int[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return new int[0];
    return copyOfRange(a, a.length - n, a.length);
  }

  public static long[] takeRight(long[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return (long[]) copyOf(a, a.length);
    if (n <= 0) return new long[0];
    return copyOfRange(a, a.length - n, a.length);
  }
  
  public static float[] takeRight(float[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return (float[]) copyOf(a, a.length);
    if (n <= 0) return new float[0];
    return copyOfRange(a, a.length - n, a.length);
  }

  public static double[] takeRight(double[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return new double[0];
    return copyOfRange(a, a.length - n, a.length);
  }
  
  public static char[] takeRight(char[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return new char[0];
    return copyOfRange(a, a.length - n, a.length);
  }
  
  public static boolean[] takeRight(boolean[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return new boolean[0];
    return copyOfRange(a, a.length - n, a.length);
  }

  public static <T> T[] takeRight(T[] a, int n) {
    // return an array with the last n elements
    if (a == null)
      throw new IllegalArgumentException("takeRight: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeRight: array must have at least one element");
    if (n >= a.length) return copyOf(a, a.length);
    if (n <= 0) return copyOf(a, 0);
    return copyOfRange(a, a.length - n, a.length);
  }

  public static int[] takeWhile(int[] a, Predicate<Integer> p) {
    // return longest prefix with elements satisfying p
    if (a == null)
      throw new IllegalArgumentException("takeWhile: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("takeWhile: array must have at least one element");
    assert a != null;
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (p.test(a[i])) {
        bindex++;
      } else
        break;
    return copyOfRange(a, 0, bindex);
  }

  public static char[] toCharArray(int[] a) {
    // convert an array of ints to a corresponding array of chars
    // if possible and return the char array or throw an exception.
    // assuming UTF-8, all elements in a must be >= 0 && <= 1114111.
    if (a == null)
      return null;
    if (a.length == 0)
      return new char[0];
    char[] b = new char[a.length];
    for (int i = 0; i < a.length; i++) {
      if (a[i] >= 0 && a[i] <= 1114111) {
        Character.toChars(a[i], b, i);
      } else
        throw new ArrayStoreException("toCharArray: invalid codePoint " + a[i] + " at index " + i);
    }
    return b;
  }

  public static char[] toCharArray(Integer[] a) {
    // convert an array of Integers to a corresponding array of chars
    // if possible and return the char array or throw an exception.
    // assuming UTF-8, all elements in a must be >= 0 && <= 1114111.
    if (a == null)
      return null;
    if (a.length == 0)
      return new char[0];
    char[] b = new char[a.length];
    for (int i = 0; i < a.length; i++) {
      if (a[i] != null && a[i] >= 0 && a[i] <= 1114111) {
        Character.toChars(a[i], b, i);
      } else
        throw new ArrayStoreException("toCharArray: invalid codePoint " + a[i] + " at index " + i);
    }
    return b;
  }

  public static Collection<Byte> toCollection(byte[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Byte> c = (Collection<Byte>) new ArrayList<Byte>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Short> toCollection(short[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Short> c = (Collection<Short>) new ArrayList<Short>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Integer> toCollection(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Integer> c = (Collection<Integer>) new ArrayList<Integer>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Long> toCollection(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Long> c = (Collection<Long>) new ArrayList<Long>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Float> toCollection(float[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Float> c = (Collection<Float>) new ArrayList<Float>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Double> toCollection(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Double> c = (Collection<Double>) new ArrayList<Double>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Character> toCollection(char[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Character> c = (Collection<Character>) new ArrayList<Character>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static Collection<Boolean> toCollection(boolean[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<Boolean> c = (Collection<Boolean>) new ArrayList<Boolean>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
    return c;
  }

  public static <T> Collection<T> toCollection(T[] a) {
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    Collection<T> c = (Collection<T>) new ArrayList<T>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
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

          public boolean hasNext() {
            return c < len;
          }

          public byte nextByte() {
            return a[c++];
          }
        };
      }
    };
  }

  public static Iterable<Short> toIterable(short[] a) {
    // implemented using PrimitiveIterator.OfShort
    // and ShortConsumer
    return new Iterable<Short>() {
      public OfShort iterator() {
        return new OfShort() {
          int len = a.length;
          int c = 0;

          public boolean hasNext() {
            return c < len;
          }

          public short nextShort() {
            return a[c++];
          }
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

          public boolean hasNext() {
            return c < len;
          }

          public float nextFloat() {
            return a[c++];
          }
        };
      }
    };
  }

  public static Iterable<Character> toIterable(char[] a) {
    // implemented using PrimitiveIterator.OfChar
    // and CharConsumer
    return new Iterable<Character>() {
      public OfChar iterator() {
        return new OfChar() {
          int len = a.length;
          int c = 0;

          public boolean hasNext() {
            return c < len;
          }

          public char nextChar() {
            return a[c++];
          }
        };
      }
    };
  }

  public static Iterable<Boolean> toIterable(boolean[] a) {
    // implemented using PrimitiveIterator.OfBoolean
    // and BooleanConsumer
    return new Iterable<Boolean>() {
      public OfBoolean iterator() {
        return new OfBoolean() {
          int len = a.length;
          int c = 0;

          public boolean hasNext() {
            return c < len;
          }

          public boolean nextBoolean() {
            return a[c++];
          }
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
    if (a == null)
      throw new IllegalArgumentException("toCollection: array can't be null");
    List<T> c = (List<T>) new ArrayList<T>();
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
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
    if (a == null)
      throw new IllegalArgumentException("toNestedList: argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toNestedList: argument must be an array");

    int dim = dim(a);

    if (dim == 1 && isPrimitiveArray(a)) {
      String rct = rootComponentName(a);
      switch (rct) {
      case "byte": {
        byte[] c = (byte[]) a;
        int len = c.length;
        ArrayList<Byte> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "short": {
        short[] c = (short[]) a;
        int len = c.length;
        ArrayList<Short> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "int": {
        int[] c = (int[]) a;
        int len = c.length;
        ArrayList<Integer> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "long": {
        long[] c = (long[]) a;
        int len = c.length;
        ArrayList<Long> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "float": {
        float[] c = (float[]) a;
        int len = c.length;
        ArrayList<Float> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "double": {
        double[] c = (double[]) a;
        int len = c.length;
        ArrayList<Double> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "char": {
        char[] c = (char[]) a;
        int len = c.length;
        ArrayList<Character> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      case "boolean": {
        boolean[] c = (boolean[]) a;
        int len = c.length;
        ArrayList<Boolean> d = new ArrayList<>();
        for (int i = 0; i < len; i++)
          d.add(c[i]);
        return d;
      }
      default:
        throw new NoSuchElementException("toNestedList: invalid primitive " + "type " + rct);
      }
    } else {
      Object[] b = (Object[]) a;
      int len = b.length;
      @SuppressWarnings("rawtypes")
      ArrayList d = new ArrayList();
      if (dim == 1) {
        for (int i = 0; i < len; i++)
          d.add(b[i]);
      } else {
        for (int i = 0; i < len; i++)
          d.add(toNestedList(b[i]));
      }
      return d;
    }
  }

  public static <K, V> Map<K, V> toMap(K[] k, V[] v) {
    if (k == null || v == null)
      throw new IllegalArgumentException("toMap: the array arguments can't be null");
    if (!(k.getClass().isArray() && v.getClass().isArray()))
      throw new IllegalArgumentException("toMap: both arguments must be arrays");
    Map<K, V> map = new HashMap<>();
    if (k.length == 0 || v.length == 0)
      return map;
    int min = k.length <= v.length ? k.length : v.length;
    for (int i = 0; i < min; i++)
      map.put(k[i], v[i]);
    return map;
  }

  public static <K, V> Map<K, V> toMap(Tuple2<K, V>[] a) {
    if (a == null)
      throw new IllegalArgumentException("toMap: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toMap: the argument must be an array");
    Map<K, V> map = new HashMap<>();
    for (int i = 0; i < a.length; i++)
      map.put(a[i]._1, a[i]._2);
    return map;
  }

  public static Set<Integer> toSet(int[] a) {
    if (a == null)
      throw new IllegalArgumentException("toSet: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toSet: the argument must be an array");
    Set<Integer> set = new HashSet<>();
    for (int i = 0; i < a.length; i++)
      set.add(a[i]);
    return set;
  }

  public static Set<Long> toSet(long[] a) {
    if (a == null)
      throw new IllegalArgumentException("toSet: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toSet: the argument must be an array");
    Set<Long> set = new HashSet<>();
    for (int i = 0; i < a.length; i++)
      set.add(a[i]);
    return set;
  }

  public static Set<Double> toSet(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("toSet: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toSet: the argument must be an array");
    Set<Double> set = new HashSet<>();
    for (int i = 0; i < a.length; i++)
      set.add(a[i]);
    return set;
  }

  public static <T> Set<T> toSet(T[] a) {
    if (a == null)
      throw new IllegalArgumentException("toSet: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toSet: the argument must be an array");
    Set<T> set = new HashSet<>();
    for (int i = 0; i < a.length; i++)
      set.add(a[i]);
    return set;
  }

  public static <T> Stream<T> toStream(T[] a) {
    if (a == null)
      throw new IllegalArgumentException("toStream: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("toStream: the argument must be an array");
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
    if (a == null)
      throw new IllegalArgumentException("transpose: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("transpose: the argument must be an array");
    if (a.length == 0)
      throw new IllegalArgumentException("transpose: the argument must have a length > 0");
    int dim = dim(a);
    if (dim != 2)
      throw new IllegalArgumentException("transpose: the argument must have dimension 2");

    int n = a.length;
    int o = a[0].length;
    int[][] t = new int[o][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++)
        t[j][i] = a[i][j];

    return t;
  }

  public static long[][] transpose(long[][] a) {
    // interchange rows and columns or reflect over main diagonal
    if (a == null)
      throw new IllegalArgumentException("transpose: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("transpose: the argument must be an array");
    if (a.length == 0)
      throw new IllegalArgumentException("transpose: the argument must have a length > 0");
    int dim = dim(a);
    if (dim != 2)
      throw new IllegalArgumentException("transpose: the argument must have dimension 2");

    int n = a.length;
    int o = a[0].length;
    long[][] t = new long[o][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++)
        t[j][i] = a[i][j];

    return t;
  }

  public static double[][] transpose(double[][] a) {
    // interchange rows and columns or reflect over main diagonal
    if (a == null)
      throw new IllegalArgumentException("transpose: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("transpose: the argument must be an array");
    if (a.length == 0)
      throw new IllegalArgumentException("transpose: the argument must have a length > 0");
    int dim = dim(a);
    if (dim != 2)
      throw new IllegalArgumentException("transpose: the argument must have dimension 2");

    int n = a.length;
    int o = a[0].length;
    double[][] t = new double[o][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++)
        t[j][i] = a[i][j];

    return t;
  }

  public static <T> T[][] transpose(T[][] a) {
    // interchange rows and columns or reflect over main diagonal
    if (a == null)
      throw new IllegalArgumentException("transpose: the array argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("transpose: the argument must be an array");
    if (a.length == 0)
      throw new IllegalArgumentException("transpose: the argument must have a length > 0");
    int dim = dim(a);
    if (dim != 2)
      throw new IllegalArgumentException("transpose: the argument must have dimension 2");

    int n = a.length;
    int o = a[0].length;
    @SuppressWarnings("unchecked")
    T[][] t = (T[][]) clone(a);
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++)
        t[j][i] = a[i][j];

    return t;
  }

  public static Object unbox(Object a) {
    // copies an array with boxed rootComponentName into an array with 
    // corresponding primitive rootComponentName and identical structure
    if (a == null)
      throw new IllegalArgumentException("unbox: " + "argument can't be null");
    if (!isBoxedArray(a))
      throw new IllegalArgumentException("unbox: " + "argument must be an array of primitive components");
    int dim = dim(a);
    if (dim < 1)
      throw new IllegalArgumentException("box " + "dimension of argument must be > 0");

    String t = rootComponentName(a);

    if (dim == 1) { // base case of recursion
      Class<?> nt = null;

      switch (t) {
      case "java.lang.Byte":
        nt = byte.class;
        break;
      case "java.lang.Character":
        nt = char.class;
        break;
      case "java.lang.Double":
        nt = double.class;
        break;
      case "java.lang.Float":
        nt = float.class;
        break;
      case "java.lang.Integer":
        nt = int.class;
        break;
      case "java.lang.Long":
        nt = long.class;
        break;
      case "java.lang.Short":
        nt = short.class;
        break;
      case "java.lang.Boolean":
        nt = boolean.class;
        break;
      default:
        throw new IllegalArgumentException("unbox: " + "cannot unbox a " + t);
      }

      int len = Array.getLength(a);
      Object array = Array.newInstance(nt, len);

      switch (t) {
      case "java.lang.Byte": {
        byte[] array2 = (byte[]) array;
        Byte[] b = (Byte[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Character": {
        char[] array2 = (char[]) array;
        Character[] b = (Character[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = '0';
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Double": {
        double[] array2 = (double[]) array;
        Double[] b = (Double[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Float": {
        float[] array2 = (float[]) array;
        Float[] b = (Float[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Integer": {
        int[] array2 = (int[]) array;
        Integer[] b = (Integer[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Long": {
        long[] array2 = (long[]) array;
        Long[] b = (Long[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Short": {
        short[] array2 = (short[]) array;
        Short[] b = (Short[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = 0;
          } else
            array2[i] = b[i];
        break;
      }
      case "java.lang.Boolean": {
        boolean[] array2 = (boolean[]) array;
        Boolean[] b = (Boolean[]) a;
        for (int i = 0; i < len; i++)
          if (b[i] == null) {
            array2[i] = false;
          } else
            array2[i] = b[i];
        break;
      }
      default:
        throw new IllegalArgumentException("unbox: " + "cannot unbox a " + t);
      }

      return array;
      // end of dim=1 base recursion case
    } else {
      String nt = null;
      String prefix = repeat('[', dim - 1);

      switch (t) {
      case "java.lang.Byte":
        nt = prefix + "B";
        break;
      case "java.lang.Character":
        nt = prefix + "C";
        break;
      case "java.lang.Double":
        nt = prefix + "D";
        break;
      case "java.lang.Float":
        nt = prefix + "F";
        break;
      case "java.lang.Integer":
        nt = prefix + "I";
        break;
      case "java.lang.Long":
        nt = prefix + "J";
        break;
      case "java.lang.Short":
        nt = prefix + "S";
        break;
      case "java.lang.Boolean":
        nt = prefix + "Z";
        break;
      default:
        throw new IllegalArgumentException("unbox: " + "cannot unbox a " + t);
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
        Array.set(array, i, unbox(Array.get(a, i)));

      return array;
    }
  }

  private static class UniformComponentType {
    // this class is for providing its find method to report the componentType
    // of an array if it's uniform else report "false"

    private Class<?> r = null;
    private boolean found = false;

    public UniformComponentType() {}

    String find(Object a) {
      // if a is an array and all its components have the  
      // same type return its name else return "false"
      if (a == null)
        throw new IllegalArgumentException("find: " + "argument can't be null");
      if (!a.getClass().isArray())
        throw new IllegalArgumentException("find: " + "argument must be an array");

      String t = rootComponentName(a);
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
            if (b[i] != null && b[i].getClass() != r)
              return "false";
        }
        // end of dim=1 base recursion case
      } else {
        for (int i = 0; i < len; i++)
          find(Array.get(a, i));
      }
      return r.getName();
    }
  }

  public static String uniformComponentType(Object a) {
    // if a is an array with a uniform actual componentType return
    // the name of that componentType else return "false"
    return (new UniformComponentType()).find(a);
  }

  public static byte[] union(byte[] a, byte... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static short[] union(short[] a, short... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static int[] union(int[] a, int... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static long[] union(long[] a, long... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static float[] union(float[] a, float... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static double[] union(double[] a, double... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static char[] union(char[] a, char... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static boolean[] union(boolean[] a, boolean... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  @SafeVarargs
  public static <T> T[] union(T[] a, T... b) {
    // return a new array with the unique elements in a and b 
    return unique(append(a, b));
  }

  public static byte[] unique(byte[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (byte[]) clone(a);
    byte[] b = new byte[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static short[] unique(short[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (short[]) clone(a);
    short[] b = new short[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static int[] unique(int[] a) {
    // returns a new int[] with the unique elements in a which is preserved
    if (a == null) throw new IllegalArgumentException("unique: a can't be null");
    int[] b = quickSortCopy(a);
    if (a.length < 2) return b;
    int j = 0;
    int i = 1;
    while(i < b.length)
        if(b[i] == b[j]) {
          i++;
        } else b[++j] = b[i++];
    return take(b, j+1);
  }
  
  public static long[] unique(long[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (long[]) clone(a);
    long[] b = new long[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static float[] unique(float[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (float[]) clone(a);
    float[] b = new float[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static double[] unique(double[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (double[]) clone(a);
    double[] b = new double[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static char[] unique(char[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (char[]) clone(a);
    char[] b = new char[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static boolean[] unique(boolean[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (boolean[]) clone(a);
    boolean[] b = new boolean[a.length];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] unique(T[] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (T[]) clone(a);
    T[] b = ofDim(a.getClass().getComponentType(), a.length);
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }
  
  public static int[][] unique(int[][] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (int[][]) clone(a);
    int[][] b = new int[a.length][];
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[][] unique(T[][] a) {
    // removes duplicate elements, retains order
    // functional: preserves a
    assert a != null;
    if (a.length < 2)
      return (T[][]) clone(a);
    T[][] b = ofDim(a.getClass().getComponentType(), a.length);
    int bindex = 0;
    for (int i = 0; i < a.length; i++)
      if (!in(b, a[i]))
        b[bindex++] = a[i];
    return take(b, bindex);
  }

  public static int[] uniqueSort(int[] a) {
    // functional: preserves a
    if (a == null)
      throw new IllegalArgumentException("uniqueSort: a can't be null");
    if (a.length < 2)
      return (int[]) clone(a);
    return unique(copySort(a));
  }

  public static long[] uniqueSort(long[] a) {
    // functional: preserves a
    if (a == null)
      throw new IllegalArgumentException("uniqueSort: a can't be null");
    if (a.length < 2)
      return (long[]) clone(a);
    return unique(copySort(a));
  }

  public static double[] uniqueSort(double[] a) {
    // functional: preserves a
    if (a == null)
      throw new IllegalArgumentException("uniqueSort: a can't be null");
    if (a.length < 2)
      return (double[]) clone(a);
    return unique(copySort(a));
  }

  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> T[] uniqueSort(T[] a) {
    // functional: preserves a
    if (a == null)
      throw new IllegalArgumentException("uniqueSort: a can't be null");
    if (a.length < 2)
      return (T[]) clone(a);
    return unique(copySort(a));
  }

  public static void update(byte[] g, byte u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static void update(short[] g, short u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static void update(int[] g, int u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static void update(long[] g, long u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }
  
  public static void update(float[] g, float u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static void update(double[] g, double u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static void update(char[] g, char u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }
  
  public static void update(boolean[] g, boolean u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static <T> void update(T[] g, T u, int p) {
    // change the value of g[p] to u in place
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "update: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("update: index p must be > 0 and < g.length");
    g[p] = u;
  }

  public static byte[] updated(byte[] g, byte u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    byte[] b = new byte[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static short[] updated(short[] g, short u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    short[] b = new short[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static int[] updated(int[] g, int u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    int[] b = new int[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static long[] updated(long[] g, long u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    long[] b = new long[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static float[] updated(float[] g, float u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    float[] b = new float[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static double[] updated(double[] g, double u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    double[] b = new double[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }
  
  public static char[] updated(char[] g, char u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    char[] b = new char[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static boolean[] updated(boolean[] g, boolean u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    boolean[] b = new boolean[g.length];
    for (int i = 0; i < g.length; i++) b[i] = g[i];
    b[p] = u;
    return b;
  }

  public static <T> T[] updated(T[] g, T u, int p) {
    // return a copy of g with the value g[p] changed to u
    if (g == null || g.length == 0) throw new IllegalArgumentException(
        "updated: the array argument can't be null or have length 0");
    if (p < 0 || p > g.length - 1)
      throw new ArrayIndexOutOfBoundsException("updated: index p must be > 0 and < g.length");
    T[] b = copyOf(g, g.length);
    b[p] = u;
    return b;
  }
  public static <A, B> Tuple2<A[], B[]> unzip(Tuple2<A, B>[] t) {
    // return a tuple of arrays from an array of tuples
    int len = t.length;
    A ainst = null;
    B binst = null;
    for (int i = 0; i < t.length; i++) {
      if (ainst == null && t[i]._1 != null)
        ainst = t[i]._1;
      if (binst == null && t[i]._2 != null)
        binst = t[i]._2;
      if (ainst != null && binst != null)
        break;
    }
    if (ainst == null || binst == null)
      throw new ArrayHasAllNullElementsException("unzip: the same component in all of the array elements is null");
    else {
      A[] a = ofDim(ainst.getClass(), len);
      B[] b = ofDim(binst.getClass(), len);
      for (int i = 0; i < len; i++) {
        a[i] = t[i]._1;
        b[i] = t[i]._2;
      }
      return new Tuple2<A[], B[]>(a, b);
    }
  }

  public static <A, B, C> Tuple3<A[], B[], C[]> unzip3(Tuple3<A, B, C>[] t) {
    // return a tuple of arrays from an array of tuples
    int len = t.length;
    A ainst = null;
    B binst = null;
    C cinst = null;
    for (int i = 0; i < t.length; i++) {
      if (ainst == null && t[i]._1 != null)
        ainst = t[i]._1;
      if (binst == null && t[i]._2 != null)
        binst = t[i]._2;
      if (cinst == null && t[i]._3 != null)
        cinst = t[i]._3;
      if (ainst != null && binst != null && cinst != null)
        break;
    }
    if (ainst == null || binst == null || cinst == null)
      throw new ArrayHasAllNullElementsException(
          "unzip3: the same component in all " + "of the array elements is null");
    else {
      A[] a = ofDim(ainst.getClass(), len);
      B[] b = ofDim(binst.getClass(), len);
      C[] c = ofDim(cinst.getClass(), len);
      for (int i = 0; i < len; i++) {
        a[i] = t[i]._1;
        b[i] = t[i]._2;
        c[i] = t[i]._3;
      }
      return new Tuple3<A[], B[], C[]>(a, b, c);
    }
  }

  public static double var(int[] a) {
    // returns the variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("var: " + "the array must not be null or have zero length");
    if (a.length == 0)
      return Double.NaN;
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / (a.length - 1);
  }

  public static double var(long[] a) {
    // returns the variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("var: " + "the array must not be null or have zero length");
    if (a.length == 0)
      return Double.NaN;
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / (a.length - 1);
  }

  public static double var(double[] a) {
    // returns the variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("var: " + "the array must not be null or have zero length");
    if (a.length == 0)
      return Double.NaN;
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / (a.length - 1);
  }

  public static double varp(int[] a) {
    // returns the population variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("varp: " + "the array must not be null or have zero length");
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / a.length;
  }

  public static double varp(long[] a) {
    // returns the population variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("varp: " + "the array must not be null or have zero length");
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / a.length;
  }

  public static double varp(double[] a) {
    // returns the population variance of a
    if (a == null || a.length == 0)
      throw new ArrayHasAllNullElementsException("varp: " + "the array must not be null or have zero length");
    double avg = mean(a);
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      sum += (a[i] - avg) * (a[i] - avg);
    }
    return sum / a.length;
  }

  public static <A, B> Tuple2<A, B>[] zip(A[] a, B[] b) {
    // returns an array of tuples formed from corresponding elements
    // of a and b with length the shortest of the two
    int len = a.length <= b.length ? a.length : b.length;
    Tuple2<A, B>[] t = ofDim(Tuple2.class, len);
    for (int i = 0; i < len; i++)
      t[i] = new Tuple2<A, B>(a[i], b[i]);
    return t;
  }

  public static <A, B> Tuple2<A, B>[] zip(A[] a, Collection<B> b) {
    // returns an array of tuples formed from corresponding elements
    // of a and b with length the shortest of the two
    int len = a.length <= b.size() ? a.length : b.size();
    Tuple2<A, B>[] t = ofDim(Tuple2.class, len);
    Iterator<B> it = b.iterator();
    int i = 0;
    while (i < len)
      t[i] = new Tuple2<A, B>(a[i++], it.next());
    return t;
  }

  public static <A, B> Tuple2<A, B>[] zipAll(A[] a, B[] b, A pa, B pb) {
    // returns an array of tuples formed from corresponding elements
    // of a and b with length the longest of the two and placeholder
    // pa or pb used to fill missing elements of the shortest
    int alen = a.length;
    int blen = b.length;
    int len = alen >= blen ? alen : blen;
    Tuple2<A, B>[] t = ofDim(Tuple2.class, len);
    for (int i = 0; i < len; i++)
      t[i] = new Tuple2<A, B>(i < alen ? a[i] : pa, i < blen ? b[i] : pb);
    return t;
  }

  public static <A, B> Tuple2<A, B>[] zipAll(A[] a, Collection<B> b, A pa, B pb) {
    // returns an array of tuples formed from corresponding elements
    // of a and b with length the longest of the two and placeholder
    // pa or pb used to fill missing elements of the shortest
    int alen = a.length;
    int blen = b.size();
    int len = alen >= blen ? alen : blen;
    Tuple2<A, B>[] t = ofDim(Tuple2.class, len);
    Iterator<B> it = b.iterator();
    int i = 0;
    while (i < len) {
      t[i] = new Tuple2<A, B>(i < alen ? a[i] : pa, i < blen ? it.next() : pb);
      i++;
    }
    return t;
  }

  public static <A, B> Tuple2<A, Integer>[] zipWithIndex(A[] a) {
    // returns an array of tuples formed from the elements of a 
    // and their indices
    int len = a.length;
    Tuple2<A, Integer>[] t = ofDim(Tuple2.class, len);
    for (int i = 0; i < len; i++)
      t[i] = new Tuple2<A, Integer>(a[i], i);
    return t;
  }

  public static void main(String[] args) {
    
    //mapToR(int[]) test
    int[] ia58 = { 1, 2, 3, 4, 5 };
    Double[] xa58 = mapToR(ia58, x -> new Double(x));
    pa(xa58); //Double[1.0,2.0,3.0,4.0,5.0]

    System.exit(0);

    //    int[] ia57 = {0};
    //    int[] ia56 = fillInt(5, () -> {return ia57[0]++;});
    //    printIt(ia56); //[0,1,2,3,4]
    //    
    //    Integer[] ia54 = {1,2,3,4,5};
    //    Integer[] ia55 = {null,2,3,null,null};
    //    Integer[] ia55t = take(ia55,3); //[null,2,3]
    //    printIt(ia55t);
    //    Integer[] ia54t = take(ia54,0);
    //    printIt(ia54t); //[]
    //    
    // collect test
    //public static <T,R> R[] collect(T[] a, Predicate<T> p, Function<T,R> f) 

    //    Double[] da53 = {null,2.2,3.3,4.4,5.5};
    //    Integer[] x53 = collect(da53, x -> x>3, x -> x.intValue());
    //    printIt(x53);
    //    Integer[] xopt53 = collectOptional(da53, x -> {
    //      if (x.isPresent()) {return x.get() > 3;} else return false;} ,
    //        z -> z.get().intValue());
    //    printIt(xopt53);  

    //  List<Integer> al52 = new ArrayList<>(Arrays.asList(1,2,3,4,5));
    ////Integer[] i15 = list2Array(al);
    ////printArray(i15);
    ////Integer[] ala = new Integer[]{1,2,3,4,5};
    //Iterable<Integer[]> ci52 = new Combinator<Integer>(al52,3);
    ////Iterator<Integer[]> cii = ci.iterator();
    ////Iterable<Integer[]> p10 = (Iterable<Integer[]>) new Permutator<Integer>(ala); //   List<Integer[]> list1 = new ArrayList<>();
    //ci52.forEach(x -> printArray(x));
    ////for (Integer[] ia : ci) printArray((Integer[]) ia);

    //while(ci.hasNext()) printArray(ci.next());
    //System.out.println(numberOfCombinations(5,3));

    //    int[] ia51 = {1,2,3,4,5};
    //    OfInt x = Arrays.stream(ia51).iterator();
    //    Iterator<int[]> ci51it = combinations(ia51,3);
    //    while(ci51it.hasNext()) printArray(ci51it.next());
    //    Iterable<int[]> ci2 = new IntCombinator(i16,3);
    //    ci2.forEach(x -> printArray(x));
    //    Iterator<int[]> ci2it = ci2.iterator();
    //    while(ci2it.hasNext()) printArray(ci2it.next());
    //    
    //    System.exit(0);
    //    
    //    Integer[][] ia50 = new Integer[2][3];
    //    ia50[0] = new Integer[]{1,2,3};
    //    ia50[1] = new Integer[]{4,5,6};
    //    Integer[][] x50 = (Integer[][]) ofDim(ia50[0].getClass(), 3,1);   
    //    printIt(x50);
    //    
    //    Short[] ia49 = ofDim(java.lang.Short.class,3);
    //    ia49[0] = 1; ia49[1] = 2; ia49[2] = 3;
    //    printIt(ia49);
    //    System.out.println(ia49.getClass().getName());
    //    
    //    byte[] x = fillByte(0, ()->5);
    //    printIt(x);
    //    
    //    Integer[] ia48 = {1,2,3,3,4,5,5,5};
    //    Integer[] test1 = makeFromClass(ia48[0],2);
    //    System.out.println(rootComponentName(ia48));
    //    printIt(test1);
    //    Integer[] ia49 = {null,2,3,3,4,5,5,5};
    //    Integer[] test2 = (Integer[]) makeFromArray(ia49,2);
    //    System.out.println(rootComponentName(ia49));
    //    printIt(test2);
    ////    System.out.println(Arrays.equals(test1,test2));
    //    
    //    System.exit(0);
    //    
    //    SecureRandom r = new SecureRandom(new byte[]{16});
    //    int max=9; int min = 0;
    //    int[][][] ia47 = null;
    //    try { // this gives different results each time
    //      //https://www.cigital.com/blog/proper-use-of-javas-securerandom/
    //      SecureRandom r = SecureRandom.getInstanceStrong();
    //      ia47 =  fillInt(3,3,3,()-> r.nextInt(max-min+1)+min);
    //    } catch (NoSuchAlgorithmException e) {
    //      e.printStackTrace();
    //    }
    //    int[][][] ia46 = fillInt(3,3,3,()-> r.nextInt(10));
    //    printIt(ia46);
    //[[[4,3,2],[1,5,2],[6,4,4]],[[8,0,9],[9,8,1],[2,3,2]],[[6,8,0],[9,6,0],[5,1,8]]]
    //    System.out.println(mean(ia46)); //4.296296296296297
    //    int[][][] ia47 =  fillInt(3,3,3,()-> r.nextInt(max-min+1)+min);
    //    pa(ia47); //int max=50; int min =1;
    //[[[33,18,35],[39,40,36],[26,24,1]],[[32,19,14],[16,20,4],[45,9,16]],[[30,47,40],[32,44,42],[12,36,21]]]
    //int max=9; int min = 0;
    //[[[2,7,4],[8,9,5],[5,3,0]],[[1,8,3],[5,9,3],[4,8,5]],[[9,6,9],[1,3,1],[1,5,0]]]
    //[[[3,6,2],[1,5,2],[1,6,9]],[[8,2,3],[3,6,5],[2,6,9]],[[3,2,6],[9,8,1],[3,4,8]]]
    // SecureRandom.getInstanceStrong()
    //[[[4,5,6],[5,4,6],[4,9,4]],[[8,1,1],[7,2,5],[3,5,5]],[[0,1,7],[9,9,6],[8,5,8]]]
    //[[[3,6,2],[1,5,2],[1,6,9]],[[8,2,3],[3,6,5],[2,6,9]],[[3,2,6],[9,8,1],[3,4,8]]]
    //[[[9,7,0],[4,1,9],[2,5,6]],[[8,7,6],[1,1,0],[9,8,7]],[[2,3,1],[4,6,9],[8,3,3]]]

    //    System.out.println(mean(ia47)); //27.074074074074073 int max=50; int min =1;
    //4.592592592592593  int max=9; int min = 0;
    //5.074074074074074  SecureRandom.getInstanceStrong()
    //4.555555555555555  SecureRandom.getInstanceStrong()
    //4.777777777777778  SecureRandom.getInstanceStrong()

    System.exit(0);

    //    Integer[] ia44 = {1,2,3,3,4,5,5,5};
    //    Integer[] ia45 = {3,3,5,6,7,8,9};
    //    printIt(diff(ia44,ia45));
    //    
    //    System.exit(0);
    //
    //    int[] ia43 = (int[]) fillInt(5, () -> 5);
    //    printIt(ia43);
    //    byte[] ba43 = fillByte(5, () -> (byte) 129);
    //    printIt(ba43);
    //    System.exit(0);
    //
    //    // copyToBuffer
    //    int[] ia42 = {1,2,3,4,5,6,7,8,9};
    //    IntBuffer ibuf = copyToBuffer(ia42);
    ////    printIt(ibuf.array());
    ////    System.out.println(ibuf); //java.nio.HeapIntBuffer[pos=9 lim=9 cap=9]
    //    boolean[] ba42 = {true,false,true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    //    System.out.println("ba42.length="+ba42.length); //17
    //    BooleanBuffer bbuf = copyToBuffer(ba42); 
    //    System.out.println(bbuf);
    //    //[true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true]
    //    //size = 24 // since allocated in byte chunks since using underlying byte buffer
    //    System.out.println(equals(ba42, bbuf.toArray()));
    //    System.exit(0);
    //    
    //    
    //    
    ////    copyToArray
    //    int[] ia38 = {1,2,3,4,5,6,7,8,9};
    //    int[] ia39 = new int[5];
    //    int[] ia40 = {1,2,3};
    //    copyToArray(ia40,ia39,1,5);//[0,1,2,3,0]
    ////    printIt(ia39);
    //    ia39 = new int[5];
    //    copyToArray(ia38,ia39,0,5); //[1,2,3,4,5]
    ////    printIt(ia39);
    ////    copyToArray(ia38,ia39,8,1); //IllegalArgumentException
    //    copyToArray(ia38,ia39,2,5); 
    ////    printIt(ia39); //[1,2,1,2,3]
    //    ia39 = new int[5];
    //    copyToArray(ia38,ia39,2,9);
    ////    printIt(ia39); //[0,0,1,2,3]
    //    ia39 = new int[5];
    //    copyToArray(ia38,ia39);
    ////    printIt(ia39); //[1,2,3,4,5]
    //    int[] ia41 = {0,0,0,0,0,0,0,0,0,0,0,0};
    //    copyToArray(ia39,ia41);
    ////    printIt(ia41); //[1,2,3,4,5,0,0,0,0,0,0,0]
    //    ia39 = new int[5];
    //    copyToArray(ia38,ia39,1);
    //    printIt(ia39); //[0,1,2,3,4]
    //    copyToArray(ia39,ia38,3);
    //    printIt(ia38); //[1,2,3,0,1,2,3,4,9]
    //    copyToArray(ia39,ia38,8);
    //    printIt(ia38); //[1,2,3,0,1,2,3,4,0]
    //    System.exit(0);
    //
    //    Character[] ca37 = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
    //        'n','o','p','q','r','s','t','u','v','w','x','y','z'};
    //    Integer x37 = 
    //        aggregateMulti(ca37, 0, (sum,ch)->sum+(int)ch, (p1,p2)->p1+p2, 7);
    //    System.out.println(x37); //2847
    //    int sum = 0;
    //    for (int i = 0; i < ca37.length; i++) sum+=(int)ca37[i];
    //    System.out.println(sum); //2847  
    //    
    //    System.exit(0);
    //
    //    Integer[] ia36 = {1,2,null,4,5};
    //    System.out.println(addString(ia36,null,"List(",",","]").toString());
    //    
    //    Integer[] ia34 = {1,2,3};
    //    Character[] ca34 = {'a','b','c'};
    //    List<Character> l34 = new ArrayList<>(Arrays.asList('a','b','c'));
    //    List<Character> l35 = new ArrayList<>(Arrays.asList('a','b','c','d','e'));
    //    Tuple2<Integer, Character>[] x34 = zip(ia34, l34);
    ////    printIt(x34);
    //    Tuple2<Integer, Character>[] xx34 = zip(ia34, ca34);
    ////    printIt(xx34);
    //    Tuple2<Character,Integer>[] xxx34 = zipWithIndex(ca34);
    ////    printIt(xxx34);
    //    Integer[] ia35 = {1,2,3,4,5};
    ////    printIt(zipAll(ia35,ca34, 9, 'Z'));
    ////    printIt(zipAll(ca34,ia35, 'Z', 9));
    //    printIt(zipAll(ia34,l35, 9, 'Z'));
    //    
    // 
    ////    Object[][] oa1 = new Object[3][2];
    ////    oa1[0] = new Object[]{new Integer(1), "one"};
    ////    oa1[1] = new Object[]{new Integer(2), "two"};
    ////    oa1[2] = new Object[]{new Integer(3), "three"};
    ////    printIt(oa1);
    //    
    ////    System.exit(0);
    //    
    ////    Double d1 = new Double(999.99);
    ////    byte b1 = d1.byteValue();
    ////    System.out.println(b1); //-25
    ////    System.out.println((byte)999); //25
    //    
    //    
    //    int[] ia33 = {1,2,3,4,5};
    //    System.out.println("toNesteArray(ia33)="+toNesteArray(ia33));
    //    //[1, 2, 3, 4, 5]
    //
    //    Integer[] ia19 = {1,2};
    //    Integer[] ia20 = {3,4};
    //    Integer[] ia21 = {5,6};
    //    Integer[] ia22 = {7,8}; 
    //    Integer[][] ia23 = {ia19,ia20};
    //    Integer[][] ia24 = {ia21,ia22};
    //    Integer[][][] ia25 = {ia23,ia24};
    //    @SuppressWarnings("unchecked")
    //    ArrayList<ArrayList<ArrayList<Integer>>> x1list = 
    //        (ArrayList<ArrayList<ArrayList<Integer>>>) toNesteArray(ia25);
    //    System.out.println("toNesteArray(ia25)="+x1list); 
    //    //[[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
    //    int[] ia26 = {1,2};
    //    int[] ia27 = {3,4};
    //    int[] ia28 = {5,6};
    //    int[] ia29 = {7,8}; 
    //    int[][] ia30 = {ia26,ia27};
    //    int[][] ia31 = {ia28,ia29};
    //    int[][][] ia32 = {ia30,ia31};
    //    @SuppressWarnings("unchecked")
    //    Collection<Collection<Collection<Integer>>> x2list = 
    //        (Collection<Collection<Collection<Integer>>>) toNesteArray(ia32);
    //    System.out.println("toNesteArray(ia32)="+x2list);
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
    //    System.out.println(primordialComponentType(d55));
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
    //    int[] a9 = {1,2,3,4,5,6,7,8,9};
    //    System.out.print("flatline(a9)="); printIt(flatLine(a9));
    //    printIt(a9);
    //    System.out.print("clone(a9)="); printIt(clone(a9));
    //    System.out.println("numel(a9)="+numel(a9));
    ////    int[] a9c = (int[]) clonea(a9);
    ////    printIt(a9c);
    ////    System.out.println(dim(a9));
    //    Integer[] a9i = (Integer[]) box(a9); 
    //    System.out.println("numel(a9i)="+numel(a9i));
    //    Object[] a9io = (Object[]) a9i;
    ////    printArray(a9i);
    //    System.out.print("clone(a9i)="); printIt(clone(a9i));
    ////    System.out.println("a9i.getClass().getName()="+a9i.getClass().getName());//[Ljava.lang.Integer
    ////    Integer[] a92i = {1,2,3,4,5,6,7,8,9};
    //    System.out.print("flatline(a9io)="); printIt((Integer[])flatLine(a9io));
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
    //    
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
    //    int[][] a2b = new int[3][3];
    //    a2b[0] = new int[]{-2,2,-3};
    //    a2b[1] = new int[]{-1,1,3};
    //    a2b[2] = new int[]{2,0,-1};
    ////////    System.out.println((dim(a2)));
    //    printIt(a2b);
    //    System.out.print("clone(a2b)="); printIt(clone(a2b));
    //    System.out.println("numel(a2b)="+numel(a2b));
    //    System.out.print("flatten2(a2b)="); printIt(flatten(a2b));
    //    
    //    int[][] a2c = (int[][]) clonea(a2b);
    //    printIt(a2c);
    //    System.out.println();
    //    printArray(a2c);
    //    System.out.print("flatline(a2b)="); printIt(flatLine(a2b));
    ////
    //    Integer[][] a2i = (Integer[][]) box(a2b);
    //    System.out.print("flatline(a2i)="); printIt(flatLine(a2i));
    //    System.out.println("numel(a2i)="+numel(a2i));
    ////
    //////    System.out.println("dim(a2i)="+dim(a2i));
    ////    printIt(a2i);
    //    System.out.print("clone(a2i)="); printIt(clone(a2i));
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
    //    
    //    int[][][] r62 = new int[2][3][4];
    //    r62[0] = new int[3][4];
    //    r62[0][0] = new int[]{1,2,3,4};
    //    r62[0][1] = new int[0];
    //    r62[0][2] = new int[]{9,10,11,12,13,14};
    //    r62[1] = new int[3][4];
    //    r62[1][0] = new int[]{15};
    //    r62[1][1] = new int[]{17,18,19,20,21};
    //    r62[1][2] = new int[]{21,22,23,24};
    //    printIt(r62);
    //    System.out.println("numel(r62)="+numel(r62));
    //    System.out.print("clone(r62)="); printIt(clone(r62));
    //    System.out.print("flatline(r62)="); printIt(flatLine(r62));
    //    System.out.print("flatten2(r62)="); printIt(flatten(r62));
    //
    //    Integer[][][] r62i = (Integer[][][]) box(r62);
    //    System.out.println("numel(r62i)="+numel(r62i));
    //    System.out.print("clone(r62i)="); printIt(clone(r62i));
    ////    printIt(r62i);
    //    System.out.print("flatline(r62i)="); printIt(flatLine(r62i));
    //    System.out.print("flatten2(r62i)="); printIt(flatten(r62i));
    //
    ////    int[][][] r62ii = ( int[][][]) unbox(r62i);
    //    printIt(r62ii);
    //    Integer[][][] r62ic = (Integer[][][]) cloneArray(r62i);
    //    printIt(r62ic);
    //    System.out.println("uniformComponentType(r62ic)="+uniformComponentType(r62ic)); 
    //    System.out.println("rootComponentName(r62ic)="+rootComponentName(r62ic)); 
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
