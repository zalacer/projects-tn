package u;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// trying to figure out what scala.Array.deep means in java

public class Deep {
  
  // the following oulines a possible deep() implementation based
  // on the Java 8 implementation of deepHashCode found at
  // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/Arrays.java/
  public static class D {
    // this class is for creating dependencies required for deep.
    // D represents the type of data produced by deep.
    // when deep is void a different but similar structure would apply.
    private static D initialValue = new D();
    public D(){};
    public static D initialValue() {
      return initialValue;
    }
    public static D possibleResultAggregator(D d1, D d2) {
      return new D();
    }
    public static D genD(byte[] a) {
      return new D();
    }
    public static D genD(short[] a) {
      return new D();
    } 
    public static D genD(int[] a) {
      return new D();
    } 
    public static D genD(long[] a) {
      return new D();
    } 
    public static D genD(char[] a) {
      return new D();
    } 
    public static D genD(float[] a) {
      return new D();
    } 
    public static D genD(double[] a) {
      return new D();
    } 
    public static D genD(boolean[] a) {
      return new D();
    }
    public static D genD(Object a) {
      return new D();
    }
  } // end of class D definition
 
  // the shallow functions depend on D and are directly used in deep.
  // these functions should produce results depending on primitive elements.
  public static D shallow(byte[] a) {
    return D.genD(a);
  }
  public static D shallow(short[] a) {
    return D.genD(a);
  }
  public static D shallow(int[] a) {
    return D.genD(a);
  }
  public static D shallow(long[] a) {
    return D.genD(a);
  }
  public static D shallow(char[] a) {
    return D.genD(a);
  }
  public static D shallow(float[] a) {
    return D.genD(a);
  }
  public static D shallow(double[] a) {
    return D.genD(a);
  }
  public static D shallow(boolean[] a) {
    return D.genD(a);
  }
  public static D shallow(Object a) {
    return D.genD(a);
  }

  // deep handles multidimensional arrays recursively
  public static D deep(Object[] a, Function<Object[], D> f) {
    // to implement deep all the functions it uses also need 
    // to be defined for the case at hand
    if (a == null) return f.apply(null);
    D result = D.initialValue();
    for (Object e : a) {
      D d = null;
      if (e instanceof Object[])
        d = deep((Object[]) e, f); //recursion drills down into nest
      else if (e instanceof byte[])
        d = shallow((byte[]) e); //shallow computes using primitive elements
      else if (e instanceof short[])
        d = shallow((short[]) e);
      else if (e instanceof int[])
        d = shallow((int[]) e);
      else if (e instanceof long[])
        d = shallow((long[]) e);
      else if (e instanceof char[])
        d = shallow((char[]) e);
      else if (e instanceof float[])
        d = shallow((float[]) e);
      else if (e instanceof double[])
        d = shallow((double[]) e);
      else if (e instanceof boolean[])
        d = shallow((boolean[]) e);
      else if (e != null)
        d = shallow(e); // here e is a non-array object
      d = D.possibleResultAggregator(result, d);
    }
    return result;
    // end of structural outline of a possible deep() implementation 
  }
  
  public static Object[] deep(Object[] a) {
    // to implement deep all the functions it uses also need 
    // to be defined for the case at hand
    if (a == null) return null;
    List<Object> list = new ArrayList<>();
    for (Object e : a) {
//      D d = null;
      if (e instanceof Object[])
        deep((Object[]) e); //recursion drills down into nest
      else if (e instanceof byte[])    list.add(e);
      else if (e instanceof short[])   list.add(e);
      else if (e instanceof int[])     list.add(e);
      else if (e instanceof long[])    list.add(e);
      else if (e instanceof char[])    list.add(e);
      else if (e instanceof float[])   list.add(e);
      else if (e instanceof double[])  list.add(e);
      else if (e instanceof boolean[]) list.add(e);
      else if (e != null)              list.add(e);
      
    }
    return a;
  }


  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
