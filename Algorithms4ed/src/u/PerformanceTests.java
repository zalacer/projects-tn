package u;

import java.lang.reflect.Array;

//import static java.util.Arrays.copyOf;

//import java.lang.reflect.Array;
//import java.time.Duration;
//import java.time.Instant;

public class PerformanceTests {

  public static void main(String[] args) {
    
    Integer[] ia1 = {1,2,3};
//    String t = rootComponentType(a);
//    System.out.println("flatline.rootComponentType="+t);
    Class<?> nt = ia1.getClass().getComponentType();

    Object r = Array.newInstance(nt, 0);
    System.out.println(r.getClass().getComponentType().getName());
    
//    System.out.println(255*(Integer.MAX_VALUE-2) < Long.MAX_VALUE); //true
//    float diff = (float) Long.MAX_VALUE - 255*(Integer.MAX_VALUE-2);
//    System.out.println(diff); //9.223372E18
//    System.out.println((float) Long.MAX_VALUE); //9.223372E18
//    System.out.println((float) Integer.MAX_VALUE-2); //2.14748365E9
//    System.out.println((float) Double.MAX_VALUE); //Infinity
//    System.out.println((float) Double.MAX_EXPONENT); //1023
//    
//    int[] a = new int[Integer.MAX_VALUE-2];
//    System.out.println(a);

//    int n = 100000;
//    int[] a = new int[n]; 
//    for (int i = 0; i < n; i++) a[i] = i;
//    Integer[] b = new Integer[n];
//    for (int i = 0; i < n; i++) b[i] = i;
//
//    int m = 100000;
//    int[] c =  new int[n];
//    Integer[] d = new Integer[n];
//    Instant start = null;
//    Instant stop = null;
//
//    start = Instant.now();
//    for (int i = 0; i < m; i++)
//      System.arraycopy(a, 0, c, 0, n);
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    //3277,3378,3390,3377
//
//    start = Instant.now();
//    for (int i = 0; i < m; i++)
//      copyOf(a, a.length);
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    //5361,5370,5370
//
//    start = Instant.now();
//    for (int j = 0; j < m; j++)
//      for (int i = 0; i < n; i++) c[i] = a[i];
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    //3135,3143,3138
//
//    start = Instant.now();
//    for (int j = 0; j < m; j++)
//      for (int i = 0; i < n; i++) Array.set(c,i, Array.getInt(a,i));
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    // took too long to wait
//
////    start = Instant.now();
////    for (int i = 0; i < m; i++)
////      System.arraycopy(b, 0, c, 0, n); //ArrayStoreException
////    stop = Instant.now();
////    System.out.println(Duration.between(start, stop).toMillis()); 
//
//    start = Instant.now();
//    for (int j = 0; j < m; j++)
//      for (int i = 0; i < n; i++) c[i] = b[i];
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    //12081,11520,11544    
//
//    start = Instant.now();
//    for (int j = 0; j < m; j++)
//      for (int i = 0; i < n; i++) d[i] = a[i];
//    stop = Instant.now();
//    System.out.println(Duration.between(start, stop).toMillis());
//    //53594,74007,60020: object creation overhead = 0.0006ms/object


  }

}
