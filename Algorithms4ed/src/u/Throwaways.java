package u;

import static java.util.Arrays.*;
import static u.ArrayUtils.dim;
import static v.ArrayUtils.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import v.ArrayHasAllNullElementsException;
import v.ArrayUtils.F1;
import v.ArrayUtils.F2;
import v.ArrayUtils.F3;
import v.ArrayUtils.F4;

public class Throwaways {
  
  public static <T> List<?> toNestedList(T[] a) {
    // returns a nested collection from a multidimensional
    // array of dimensionality <= 5
    if (a == null) throw new IllegalArgumentException(
        "toCollection: argument can't be null");
    int dim = dim(a);
    if (dim > 5) throw new IllegalArgumentException(
        "toCollection: dimensionality over 5 isn't supported");

    if (dim == 1) {
      List<T> c = new ArrayList<>();
      for (int i = 0; i < a.length; i++) c.add(a[i]);
      return c;
    } 

    if (dim == 2) {
      @SuppressWarnings("unchecked")
      T[][] b = (T[][]) a;
      List<List<T>> c = new ArrayList<>();
      for (int i = 0; i < b.length; i++) {
        List<T> d = new ArrayList<>();
        for (int j = 0; j < b[i].length; j++) {
          d.add(b[i][j]);
        }
        c.add(d);
      }
      return c;
    }

    if (dim == 3) {
      @SuppressWarnings("unchecked")
      T[][][] b = (T[][][]) a;
      List<List<List<T>>> c = new ArrayList<>();

      for (int i = 0; i < b.length; i++) {
        List<List<T>> d = new ArrayList<>();
        for (int j = 0; j < b[i].length; j++) {
          List<T> e = new ArrayList<>();
          for (int k = 0; k < b[i][j].length; k++) {
            e.add(b[i][j][k]);
          } d.add(e);
        } c.add(d);
      }
      return c;
    }

    if (dim == 4) {
      @SuppressWarnings("unchecked")
      T[][][][] b = (T[][][][]) a;
      List<List<List<List<T>>>> c = new ArrayList<>();

      for (int i = 0; i < b.length; i++) {
        List<List<List<T>>> d = new ArrayList<>();
        for (int j = 0; j < b[i].length; j++) {
          List<List<T>> e = new ArrayList<>();
          for (int k = 0; k < b[i][j].length; k++) {
            List<T> f = (List<T>) new ArrayList<T>();
            for (int l = 0; l < b[i][j][k].length; l++) {
              f.add(b[i][j][k][l]);
            } e.add(f);
          } d.add(e);
        } c.add(d);
      }
      return c;
    }

    if (dim == 5) {
      @SuppressWarnings("unchecked")
      T[][][][][] b = (T[][][][][]) a;
      List<List<List<List<List<T>>>>> c = new ArrayList<>();

      for (int i = 0; i < b.length; i++) {
        List<List<List<List<T>>>> d = new ArrayList<>();
        for (int j = 0; j < b[i].length; j++) {
          List<List<List<T>>> e = new ArrayList<>();
          for (int k = 0; k < b[i][j].length; k++) {
            List<List<T>> f = new ArrayList<>();
            for (int l = 0; l < b[i][j][k].length; l++) {
              List<T> g = new ArrayList<>();
              for (int m = 0; m < b[i][j][k][l].length; m++) {
                g.add(b[i][j][k][l][m]);
              } f.add(g);
            } e.add(f);
          } d.add(e);
        } c.add(d);
      }
      return c;
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public static <T extends U, U> U[] toArray2(T[] a) {
    if (a == null) throw new IllegalArgumentException(
        "toArray: array can't be null");
    Object[] b = new Object[a.length];
    for (int i = 0; i < a.length; i++) b[i] = (U) a[i];
    return (U[]) b;
  }
 
  @SuppressWarnings("unchecked")
  public static <T extends U, U> U[] toArray(T[] a, U u)  {
    if( a == null) throw new IllegalArgumentException("clone: "
        + "argument can't be null");
    if (!a.getClass().isArray()) throw new IllegalArgumentException("clone: "
        + "argument must be an array");

    int dim = dim(a);
    int len = Array.getLength(a);
    U[] b = (U[]) Array.newInstance(u.getClass(), len);

    if (dim == 1) {
      for (int i = 0; i < len; i++) b[i] = (U) a[i];
      return (U[]) b;
    } else {
      U[][] c = (U[][]) Array.newInstance(b.getClass(), len);
      for (int i = 0; i < len; i++) c[i] = (U[]) toArray((T[]) Array.get(a,i),u);
      return (U[]) c;
    }
  }
 
  public static class A {
    char a = 'a';
    public A(){};
    public A(char a) {this.a = a;}
    @Override public String toString() {
      return "A("+a+")";
    }
  }
 
  public static class B extends A {
    char b = 'b';
    public B(){};
    public B(char b) {this.b = b;}
    @Override public String toString() {
      return "B("+b+")";
    }
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

  public static <T,R> R[] collectOptional(T[] a, Predicate<Optional<T>> p, Function<Optional<T>,R> f) {
    // builds R[] by applying f only to elements of a satisfying p.
    // this is meant to simulate the effect of applying a partial function,
    // that is one which is partially defined over its domain T 
    if (a == null) throw new IllegalArgumentException("collect: the array can't be null");
    Optional<T> topt = findFirstNonNull(a)._1;
    T tinst = null;
    if (topt.isPresent()) {
      tinst = topt.get();
    } else throw new ArrayHasAllNullElementsException("collect: "
        + "cannot find a non null element in the array");
    R rinst = f.apply(Optional.of(tinst));
    R[] r = ofDim(rinst.getClass(), a.length);

    int rindex = 0;
    for (int i = 0; i < a.length; i++)
      if (Optional.ofNullable(a[i]).isPresent())
        if (p.test(Optional.of(a[i]))) {
          r[rindex++] = f.apply(Optional.of(a[i]));
        } else r[rindex++] = null;
    if (rindex == a.length) {
      return r;
    } else return take(r, rindex);
  }
 
  public static <T,R> R[] collectOptional2(T[] a, Predicate<T> p, Function<T,R> f) {
    // builds R[] by applying f only to elements of a satisfying p.
    // this is meant to simulate the effect of applying a partial function,
    // that is one which is partially defined over its domain T 
    if (a == null) throw new IllegalArgumentException("collect: the array can't be null");
    Optional<T> topt = findFirstNonNull(a)._1;
    T tinst = null;
    if (topt.isPresent()) {
      tinst = topt.get();
    } else throw new ArrayHasAllNullElementsException("collect: "
        + "cannot find a non null element in the array");
    R rinst = f.apply(tinst);
    R[] r = ofDim(rinst.getClass(), a.length);

    int rindex = 0;
    for (int i = 0; i < a.length; i++)
      if (a[i] != null)
        if (p.test(a[i])) {
          r[rindex++] = f.apply(a[i]);
        } else r[rindex++] = null;
    if (rindex == a.length) {
      return r;
    } else return take(r, rindex);
  }
 
  public static int[] flatMap(int[] a, F1<Integer, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static long[] flatMap(int[] a, F2<Integer, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static double[] flatMap(int[] a, F3<Integer, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] flatMap(int[] a, F4<Integer, T[]> f) {
    if (a == null || a.length == 0) return null;

    Object[] b = new Object[0];
    for (int i = 0; i < a.length; i++) b=append(b,f.apply(a[i]));

    Optional<Object> oopt = findFirstNonNull(b)._1;
    T tinst = null;
    if (oopt.isPresent()) tinst = (T) oopt.get();
    else throw new ArrayHasAllNullElementsException(
        "flatMap: cannot find a non null element in the preliminary result array");
    T[] t = ofDim(tinst.getClass(), b.length);
    for (int i = 0; i < t.length; i++) t[i] = (T) b[i];
    return t;
  }

  public static int[] flatMap(long[] a, F1<Long, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static long[] flatMap(long[] a, F2<Long, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static double[] flatMap(long[] a, F3<Long, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static <R> R[] flatMap(long[] a, F4<Long, R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static int[] flatMap(double[] a, F1<Double, int[]> f) {
    if (a == null || a.length == 0) return null;
    int[] r = new int[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static long[] flatMap(double[] a, F2<Double, long[]> f) {
    if (a == null || a.length == 0) return null;
    long[] r = new long[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static double[] flatMap(double[] a, F3<Double, double[]> f) {
    if (a == null || a.length == 0) return null;
    double[] r = new double[0];
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }

  public static <R> R[] flatMap(double[] a, F4<Double, R[]> f) {
    if (a == null || a.length == 0) return null;
    R[] r = makeGen1D(0);
    for (int i = 0; i < a.length; i++) r=append(r,f.apply(a[i]));
    return r;
  }
 
  private static class FlatLine {
    // this class provides a method to flatten an array to 1D
    // regardless of its original dimensionality. 

    private Object r = null;
    private Class<?> nt = null;

    public FlatLine(){} 

    Object flatLine(Object a) {
      // if a is an array convert it to a 1D array discarding elements
      // that don't fit due to array length limit .
      if( a == null) throw new IllegalArgumentException("flatLine: "
          + "argument can't be null");
      if (!a.getClass().isArray()) throw new IllegalArgumentException("flatLine: "
          + "argument must be an array");

      int dim = dim(a);
      int alen = Array.getLength(a);

      if (dim == 1) { // base case of recursion

        String t = rootComponentName(a);
        //       System.out.println("flatline.rootComponentName="+t);
        nt = a.getClass().getComponentType();
        //       System.out.println("flatline.nt="+nt);

        if (r == null) r = Array.newInstance(nt, 0);
        //       System.out.println("flatLine r compT just after creation="+r.getClass().getComponentType().getName());

        int rlen = Array.getLength(r);

        switch (t) {
        case "byte":
          if ((long) rlen+alen > maxArrayLength) {
            return append((byte[]) r, dropRight((byte[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((byte[]) r, (byte[]) a);
            break;
          }
        case "char":
          if ((long) rlen+alen > maxArrayLength) {
            return append((char[]) r, dropRight((char[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((char[]) r, (char[]) a);
            break;
          }           
        case "double":
          if ((long) rlen+alen > maxArrayLength) {
            return append((double[]) r, dropRight((double[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((double[]) r, (double[]) a);
            break;
          }
        case "float":
          if ((long) rlen+alen > maxArrayLength) {
            return append((float[]) r, dropRight((float[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((float[]) r, (float[]) a);
            break;
          }           
        case "int":
          if ((long) rlen+alen > maxArrayLength) {
            return append((int[]) r, dropRight((int[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((int[]) r, (int[]) a);
            break;
          }
        case "long":
          if ((long) rlen+alen > maxArrayLength) {
            return append((long[]) r, dropRight((long[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((long[]) r, (long[]) a);
            break;
          }
        case "short":
          if ((long) rlen+alen > maxArrayLength) {
            return append((short[]) r, dropRight((short[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((short[]) r, (short[]) a);
            break;
          }
        case "boolean":
          if ((long) rlen+alen > maxArrayLength) {
            return append((boolean[]) r, dropRight((boolean[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((boolean[]) r, (boolean[]) a);
            break;
          }
        default :
          if ((long) rlen+alen > maxArrayLength) {
            return append((Object[]) r, dropRight((Object[]) a, -maxArrayLength+alen+rlen));
          } else {
            r = append((Object[]) r, (Object[]) a);
            //             System.out.println("flatLine r compT="+r.getClass().getComponentType().getName());
            //             System.out.println("flatLine a compT="+a.getClass().getComponentType().getName());
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
    // a is an array with dimensionality > 1 return a conversion
    // of it into into a new 1D array or return a clone of it
    return (new FlatLine()).flatLine(a);
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
    System.out.printf(repeat(' ', relen));
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
    // System.out.println("maxelwidth="+maxelwidth);
    // System.out.println("width="+width);
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

    System.out.print(repeat(' ', indexwidth+maxelwidth));
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
    // System.out.println("maxelwidth="+maxelwidth);
    // System.out.println("width="+width);
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

    System.out.print(repeat(' ',indexwidth+maxelwidth));
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

    System.out.print(""+repeat(' ',len2));
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
    if (t == null) return (T[]) (new Object[n]);
    T[] r = (T[]) Array.newInstance(t.getClass(), n);
    for (int i = 0; i < n; i++) r[i] = t;
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][] makeFromValue(T t, int n1, int n2) {
    // refinement of makeFromList
    if (t == null)  {
      System.out.println("makeFromValue: t cannot be null");
      return null;
    }
    T[][] r = (T[][]) Array.newInstance(t.getClass(), n1, n2);
    for (int i = 0; i < n1; i++) 
      for (int j = 0; j < n2; j++) 
        r[i][j] = t;
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][] makeFromValue(T t, int n1, int n2, int n3) {
    // refinement of makeFromList
    if (t == null)  {
      System.out.println("makeFromValue: t cannot be null");
      return null;
    }
    T[][][] r = (T[][][]) Array.newInstance(t.getClass(), n1, n2, n3);
    for (int i = 0; i < n1; i++) 
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          r[i][j][k] = t;
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][][] makeFromValue(T t, int n1, int n2, int n3, int n4) {
    // refinement of makeFromList
    if (t == null)  {
      System.out.println("makeFromValue: t cannot be null");
      return null;
    }
    T[][][][] r = (T[][][][]) Array.newInstance(t.getClass(), n1, n2, n3, n4);
    for (int i = 0; i < n1; i++) 
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            r[i][j][k][l] = t;
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[][][][][] makeFromValue(T t, int n1, int n2, int n3, int n4, int n5) {
    // refinement of makeFromList
    if (t == null)  {
      System.out.println("makeFromValue: t cannot be null");
      return null;
    }
    T[][][][][] r = (T[][][][][]) Array.newInstance(t.getClass(), n1, n2, n3, n4, n5);
    for (int i = 0; i < n1; i++) 
      for (int j = 0; j < n2; j++)
        for (int k = 0; k < n3; k++)
          for (int l = 0; l < n4; l++)
            for (int m = 0; l < n5; l++)
              r[i][j][k][l][m] = t;
    return r;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] makeFromArray(T[] t, int n) {

    String componentTypeName = rootComponentName(t);
    System.out.println("makeFromClass.componentTypeName="+componentTypeName);
    Class<?> componentType = null;
    try {
      componentType = Class.forName(componentTypeName);
    } catch (ClassNotFoundException e) {
      System.out.println("makeFromClass: class \""+componentType+"\"not found");
      return null;
    }
    T[] r = (T[]) Array.newInstance(componentType, n);
    //   for (int i = 0; i < n; i++) r[i] = t;
    return r;
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
 
  public static Object createArray(Class<?> componentType, int dimensions, int length)
      throws NegativeArraySizeException, ClassNotFoundException {
    //http://stackoverflow.com/questions/23230854/getting-unknown-number-of-dimensions-from-a-multidimensional-array-in-java
    if (dimensions == 0) return null;
    Object array = Array.newInstance(arrayType(componentType, dimensions-1), length);
    for (int i = 0; i < length; i++)
        Array.set(array, i, createArray(componentType, dimensions-1, length));
    return array;
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
    shellSort(r);
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
    shellSort(r);
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
    shellSort(r);
    return r;
  }
  

 public static void main(String[] args) {
    
  

  }

}
