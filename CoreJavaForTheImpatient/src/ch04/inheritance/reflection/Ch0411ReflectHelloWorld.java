package ch04.inheritance.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 11. Write the “Hello, World” program, using reflection to look up the out field of
// java.lang.System and using invoke to call the println method.

public class Ch0411ReflectHelloWorld {

  public static void helloWorldReflection() 
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, 
      IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Class<System> system = java.lang.System.class;
    Field out = system.getField("out");
    Object value = out.get(null);
    Method stringPrintln = value.getClass()
        .getMethod("println", new  Class<?>[]{java.lang.String.class});
    stringPrintln.invoke(value, "hello world");
  }

  public static void main(String[] args) {

    try {
      helloWorldReflection();
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
        IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
    // hello world

  }

}
