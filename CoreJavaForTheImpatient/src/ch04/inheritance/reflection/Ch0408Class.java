package ch04.inheritance.reflection;

import static utils.StringUtils.repeatChar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

// 8. The class Class has six methods that yield a string representation of the type
// represented by the Class object. How do they differ when applied to arrays,
// generic types, inner classes, and primitive types?

// these are the six methods of interest:
// getName()
// getCanonicalName()
// getSimpleName()
// getTypeName()
// toGenericString()
// toString()

public class Ch0408Class {

  public class Inner {
    private int i;
    private int[] a;

    Inner(int i, int[] a) {
      this.i = i;
      this.a = a;
    }
  }

  Inner inner = new Inner(7, new int[]{1,2,3});


  public static void testNameMethods(Object o) {
    @SuppressWarnings("rawtypes")
    Class[] n = new Class[0];
    String[] methods = new String[] { 
        "getName",
        "getCanonicalName", 
        "getSimpleName",
        "getTypeName",
        "toGenericString",
    "toString" };
    String s2 = "testing "+ o.getClass().getCanonicalName() +": ";
    System.out.println(s2 + "\n" + repeatChar('=', s2.length()));
    for (String s : methods)  {
      Method m = null;
      try {
        m = (o.getClass()).getClass().getMethod(s, n);
        System.out.printf("%-18s: %s\n", s, m.invoke(o.getClass()));
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException |
          IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      } 
    }
  }

  public static void main(String[] args) {
    // array
    int[] a = new int[] { 1, 2, 3, 4, 5 };
    testNameMethods(a);
    //            testing int[]: 
    //            ===============
    //            getName           : [I
    //            getCanonicalName  : int[]
    //            getSimpleName     : int[]
    //            getTypeName       : int[]
    //            toGenericString   : public abstract final class [I
    //            toString          : class [I

    System.out.println();

    // generic
    ArrayList<String> ar = new ArrayList<>();
    testNameMethods(ar);
    //            testing java.util.ArrayList: 
    //            =============================
    //            getName           : java.util.ArrayList
    //            getCanonicalName  : java.util.ArrayList
    //            getSimpleName     : ArrayList
    //            getTypeName       : java.util.ArrayList
    //            toGenericString   : public class java.util.ArrayList<E>
    //            toString          : class java.util.ArrayList

    System.out.println();

    // inner
    Ch0408Class x = new Ch0408Class();
    assert x.inner.i == 7;
    assert Arrays.equals(x.inner.a, new int[]{1,2,3});
    testNameMethods(x.inner);
    //            testing ch04.Ch0408Class.Inner: 
    //            ================================
    //            getName           : ch04.Ch0408Class$Inner
    //            getCanonicalName  : ch04.Ch0408Class.Inner
    //            getSimpleName     : Inner
    //            getTypeName       : ch04.Ch0408Class$Inner
    //            toGenericString   : public class ch04.Ch0408Class$Inner
    //            toString          : class ch04.Ch0408Class$Inner

    System.out.println();

    // primitive
    double d = 3.14;
    testNameMethods(d);
    //           testing java.lang.Double: 
    //           ==========================
    //           getName           : java.lang.Double
    //           getCanonicalName  : java.lang.Double
    //           getSimpleName     : Double
    //           getTypeName       : java.lang.Double
    //           toGenericString   : public final class java.lang.Double
    //           toString          : class java.lang.Double           

    System.out.println();

    // primitive
    char c = 'z';
    testNameMethods(c);
    //            testing java.lang.Character: 
    //            =============================
    //            getName           : java.lang.Character
    //            getCanonicalName  : java.lang.Character
    //            getSimpleName     : Character
    //            getTypeName       : java.lang.Character
    //            toGenericString   : public final class java.lang.Character
    //            toString          : class java.lang.Character

    System.out.println();

    // primitive
    boolean b = true;
    testNameMethods(b);
    //            testing java.lang.Boolean: 
    //            ===========================
    //            getName           : java.lang.Boolean
    //            getCanonicalName  : java.lang.Boolean
    //            getSimpleName     : Boolean
    //            getTypeName       : java.lang.Boolean
    //            toGenericString   : public final class java.lang.Boolean
    //            toString          : class java.lang.Boolean        

  }


}
