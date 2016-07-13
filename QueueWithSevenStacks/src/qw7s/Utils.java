package qw7s;

import static java.util.Arrays.copyOf;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  
  // max array length due to VM limit
  public static final int maxArrayLength = Integer.MAX_VALUE - 8;

  // default max line length for ArrayToString() formatting
  // affects printArray() and pa() output
  private static int maxLineLength = 80;
  
  @FunctionalInterface
  public interface CharSupplier {
    char getAsChar();
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
  
  @SuppressWarnings("unchecked")
  public static <T> T[] ofDim(Class<?> c, int n) {
    if (c == null || n < 0)
      throw new IllegalArgumentException("ofDim: n must be > 0 " + "and c must not be null");
    return (T[]) Array.newInstance(c, n);
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

  public static char[] fillChar(int n, CharSupplier s) {
    if (n < 0 || s == null)
      throw new IllegalArgumentException("fillChar: " + "n must be > 0 and s cannot be null");
    char[] a = new char[n];
    for (int i = 0; i < a.length; i++)
      a[i] = s.getAsChar();
    return a;
  }
  
  public static final String repeat(char c, int length) {
    // this method is to support the box and unbox methods, etc.
    return new String(fillChar(length > 0 ? length : 0, () -> c));
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
  
  public static boolean isPrimitiveArray(Object a) {
    // return true if a is an array with primitive 
    // rootComponentType else return false.
    if (a == null)
      throw new IllegalArgumentException("isPrimitiveArray: argument can't be null");
    if (!a.getClass().isArray())
      throw new IllegalArgumentException("isPrimitiveArray: argument must be an array");
    return rootComponentType(a).isPrimitive();
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
    if (options.length == 0 || options[0] < 1) {
      maxlen = maxLineLength;
    } else
      maxlen = options[0];

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

  public static void pa(Object a, int... options) {
    System.out.println(arrayToString(a, options));
  }


}
