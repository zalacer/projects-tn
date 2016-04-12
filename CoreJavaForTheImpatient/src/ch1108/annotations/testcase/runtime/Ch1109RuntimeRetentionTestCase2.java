package ch1108.annotations.testcase.runtime;

import java.lang.annotation.Annotation;
//import static java.lang.annotation.ElementType.METHOD;
//import static java.lang.annotation.RetentionPolicy.RUNTIME;
//
//import java.lang.annotation.Repeatable;
//import java.lang.annotation.Retention;
//import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

//import ch1108.annotations.testcase.MyMath;
//import ch1108.annotations.testcase.TestCase;
//import ch1108.annotations.testcase.TestCases;


public class Ch1109RuntimeRetentionTestCase2 {

//  @Target(METHOD)
//  @Retention(SOURCE)
//  @Repeatable(TestCases.class)
//  public @interface TestCase {
//    String params() default "";
//    String expected() default "";
//  }
//  
//  @Target(METHOD)
//  @Retention(SOURCE)
//  public @interface TestCases {
//    TestCase[] value();
//  }
//  
  public static String runTestCases(Class<?> c) throws Exception {
//    String cname = c.getName();
    Class<TestCase> anno =  TestCase.class;
    Method[] methods = c.getDeclaredMethods();
    System.out.println("methods.length: "+methods.length); // 1
//    String params, expected, name;
    for (Method m : methods) {
      System.out.println(m.getName());
      Annotation[] annos2 = m.getAnnotations();
      System.out.println("annos2.length: "+annos2.length); // 1
      Annotation[] annos = m.getAnnotationsByType(anno);
      System.out.println("annos.length: "+annos.length); // 4
      Parameter[] parameters = m.getParameters();
      Class<?>[]  paramTypes = m.getParameterTypes();
      System.out.println("parameters");
      System.out.println(Arrays.toString(parameters));
      System.out.println("paramTypes");
      System.out.println(Arrays.toString(paramTypes));
        if (m.isAnnotationPresent(ch1108.annotations.testcase.TestCases.class)) {
          System.out.println("here");
//          TestCases testcase = m.getAnnotation(TestCases.class);
//            params = testcase.params();
//            expected = testcase.expected();
//            name = m.getName();
 
            
        }
    }
    return "l";
}


  
  public static void main(String[] args) throws Exception {

    runTestCases(MyMath.class);

  }

}
