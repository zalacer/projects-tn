package ch04.inheritance.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.DoubleFunction;

// 13. Write a method that prints a table of values for any Method representing a static
// method with a parameter of type double or Double. Besides the Method object,
// accept a lower bound, upper bound, and step size. Demonstrate your metzhod by
// printing tables for Math.sqrt and Double.toHexString. Repeat, using a
// DoubleFunction<Object> instead of a Method (see Section 3.6.2,
// “Choosing a Functional Interface,” on p. 113). Contrast the safety, efficiency, and
// convenience of both approaches.

// Clearly using a functional interface is much easier than reflection, it appears to be much 
// safer since it throws no checked exceptions and surely it runs faster since the previous 
// exercise demonstrated that reflected method invocation performs poorly.

public class Ch0413MethodTables {

  public static void genTable(Method method, double lower, double upper, double step) {
    if (lower >= upper)
      throw new IllegalArgumentException("lower must be < than upper");
    if (! Modifier.isStatic(method.getModifiers()))
      throw new IllegalArgumentException("method must be static");
    try {
      for (double i = lower; i < upper; i += step)
        System.out.println(method.invoke(null, i));
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public static void calc(DoubleFunction<Object> df, double lower, double upper, double step) {
    for (double d = lower; d < upper; d += step)
      System.out.println(df.apply(d));    
  }

  public static void main(String[] args) {

    Class<Math> math = java.lang.Math.class;
    // Method[] methods = math.getMethods();
    // for (Method m : methods)
    // System.out.println(m.getName() + Arrays.toString(m.getParameters()));
    Method sqrt = null;
    try {
      sqrt = math.getMethod("sqrt", new Class<?>[] { double.class });
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
    System.out.println(sqrt); // public static double java.lang.Math.sqrt(double)
    System.out.println("Math.sqrt from 1 to 5 by .5:");
    genTable(sqrt, 1., 5., .5);
    //        1.0
    //        1.224744871391589
    //        1.4142135623730951
    //        1.5811388300841898
    //        1.7320508075688772
    //        1.8708286933869707
    //        2.0
    //        2.1213203435596424

    Class<Double> dub = java.lang.Double.class;
    Method toHexString = null;
    try {
      toHexString = dub.getMethod("toHexString", new Class<?>[] { double.class });
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
    System.out.println("\n"+toHexString); // public static String java.lang.Double.toHexString
    System.out.println("Double.toHexString from 1 to 5 by .5:");
    genTable(toHexString, 1., 5., .5);
    //        0x1.0p0
    //        0x1.8p0
    //        0x1.0p1
    //        0x1.4p1
    //        0x1.8p1
    //        0x1.cp1
    //        0x1.0p2
    //        0x1.2p2

    System.out.println("\nlambda Math.sqrt from 1 to 5 by .5:");
    calc(x -> Math.sqrt(x), 1., 5., .5);
    //        1.0
    //        1.224744871391589
    //        1.4142135623730951
    //        1.5811388300841898
    //        1.7320508075688772
    //        1.8708286933869707
    //        2.0
    //        2.1213203435596424

    System.out.println("\nlambda Double.toHexString from 1 to 5 by .5:");
    calc(x -> Double.toHexString(x), 1., 5., .5);
    //        0x1.0p0
    //        0x1.8p0
    //        0x1.0p1
    //        0x1.4p1
    //        0x1.8p1
    //        0x1.cp1
    //        0x1.0p2
    //        0x1.2p2  


  }

}
