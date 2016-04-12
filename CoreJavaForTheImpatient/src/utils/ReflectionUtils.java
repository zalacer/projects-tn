package utils;

import static utils.StringUtils.repeatChar;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionUtils {

  public static void printClassMethods(Object obj) {
    Class<?> cl = null;
    cl = obj.getClass();

    String className = cl.getCanonicalName();

    String s1 = "Declared methods in " + className + ":";
    System.out.println(s1 + "\n" + repeatChar('=', s1.length()));
    for (Method m : cl.getDeclaredMethods()) {
      System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName() + " "
          + m.getName() + Arrays.toString(m.getParameters()));
    }
    System.out.println();

    Class<?> cs = cl;
    while (true) {
      cs = cs.getSuperclass();
      if (cs == null)
        break;
      String s2 = "Declared methods in super" + cs + ":";
      System.out.println(s2 + "\n" + repeatChar('=', s2.length()));
      for (Method m : cs.getDeclaredMethods()) {
        System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName()
            + " " + m.getName() + Arrays.toString(m.getParameters()));
      }
      System.out.println();
    }

    String s3 = "Declared methods in " + className + " including superclasses:";
    System.out.println(s3 + "\n" + repeatChar('=', s3.length()));
    while (cl != null) {
      for (Method m : cl.getDeclaredMethods()) {
        System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName()
            + " " + m.getName() + Arrays.toString(m.getParameters()));
      }
      cl = cl.getSuperclass();
    }
  }

  public static void printClassMethods(String className) {
    Class<?> cl = null;
    try {
      cl = Class.forName(className);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    String s1 = "Declared methods in " + className + ":";
    System.out.println(s1 + "\n" + repeatChar('=', s1.length()));
    for (Method m : cl.getDeclaredMethods()) {
      System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName() + " "
          + m.getName() + Arrays.toString(m.getParameters()));
    }
    System.out.println();

    Class<?> cs = cl;
    while (true) {
      cs = cs.getSuperclass();
      if (cs == null)
        break;
      String s2 = "Declared methods in super" + cs + ":";
      System.out.println(s2 + "\n" + repeatChar('=', s2.length()));
      for (Method m : cs.getDeclaredMethods()) {
        System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName()
            + " " + m.getName() + Arrays.toString(m.getParameters()));
      }
      System.out.println();
    }

    String s3 = "Declared methods in " + className + " including superclasses:";
    System.out.println(s3 + "\n" + repeatChar('=', s3.length()));
    while (cl != null) {
      for (Method m : cl.getDeclaredMethods()) {
        System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getCanonicalName()
            + " " + m.getName() + Arrays.toString(m.getParameters()));
      }
      cl = cl.getSuperclass();
    }
  }

  public static void testNameMethods(Object o) {
    @SuppressWarnings("rawtypes")
    Class[] n = new Class[0];
    String[] methods = new String[] { "getName", "getCanonicalName", "getSimpleName", "getTypeName",
        "toGenericString", "toString" };
    String s2 = "testing " + o.getClass().getCanonicalName() + ": ";
    System.out.println(s2 + "\n" + repeatChar('=', s2.length()));
    for (String s : methods) {
      Method m = null;
      try {
        m = (o.getClass()).getClass().getMethod(s, n);
        System.out.printf("%-18s: %s\n", s, m.invoke(o.getClass()));
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public static void printConstructors(String className) {
    Class<?> clazz = null;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
    for (Constructor<?> c : declaredConstructors)
      System.out.println(c);

  }

  public static void printClassFields(String className) {
    Class<?> clazz = null;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    StringBuilder builder = new StringBuilder();
    for (Field f : clazz.getDeclaredFields()) {
      if (builder.length() > 0) builder.delete(0, builder.length());
      f.setAccessible(true);
      String name = f.getName();
      int modifiers = f.getModifiers();
      Object value = null;
      if (Modifier.isStatic(modifiers)) {
        builder.append(",static");
        try {
          value = f.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      if (Modifier.isPublic(modifiers)) builder.append(",public");;
      if (Modifier.isPrivate(modifiers)) builder.append(",private");
      if (Modifier.isProtected(modifiers)) builder.append(",protected");
      String b = builder.toString();
      if (value != null) {
        System.out.println("name="+name+b+",value="+value);
      } else {
        System.out.println("name="+name+b);
      }
    }
  }

  public static void printObjectFields(Object obj) {
    for (Field f : obj.getClass().getDeclaredFields()) {
      f.setAccessible(true);
      String name = f.getName();
      Object value = null;
      try {
        value = f.get(obj);
      } catch (IllegalArgumentException e1) {
        // e1.printStackTrace();
        System.err.println("could not get value of " + name + " due to IllegalArgumentException");
      } catch (IllegalAccessException e2) {
        // e2.printStackTrace();
        System.err.println("could not get value of " + name + " due to IllegalAccessException");
      }
      System.out.println(f.getName() + ":" + value);
    }
  }

  public static Object growArray(Object array, int increment) {
    Class<?> cl = array.getClass();
    if (!cl.isArray())
      return null;
    Class<?> componentType = cl.getComponentType();
    int length = Array.getLength(array);
    Object newArray = Array.newInstance(componentType, length + increment);
    for (int i = 0; i < Math.min(length, length + increment); i++)
      Array.set(newArray, i, Array.get(array, i));
    return newArray;
  }

  public static Optional<Object> growArrayOptional(Object array, int increment) {
    Class<?> cl = array.getClass();
    if (!cl.isArray())
      return Optional.ofNullable(null);
    Class<?> componentType = cl.getComponentType();
    int length = Array.getLength(array);
    Object newArray = Array.newInstance(componentType, length + increment);
    for (int i = 0; i < Math.min(length, length + increment); i++)
      Array.set(newArray, i, Array.get(array, i));
    return Optional.ofNullable(newArray);
  }

  public static Optional<Object> growArrayOptional2(Object array, int increment) {
    Class<?> cl = array.getClass();
    if (!cl.isArray())
      return null;
    Class<?> componentType = cl.getComponentType();
    int length = Array.getLength(array);
    Object newArray = Array.newInstance(componentType, length + increment);
    for (int i = 0; i < Math.min(length, length + increment); i++)
      Array.set(newArray, i, Array.get(array, i));
    return Optional.ofNullable(newArray);
  }

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

}
