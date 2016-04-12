package ch04.inheritance.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 12. Measure the performance difference between a regular method call and a method
// call via reflection.

public class Ch0412ReflectionBenchmark {
    
    public static void helloWorld() {
        System.out.print("");
    }
    
    public static void helloWorldReflection() 
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, 
                IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<System> system = java.lang.System.class;
        Field out = system.getField("out");
        Object value = out.get(null);
        Method stringPrint = value.getClass()
                .getMethod("print", new  Class<?>[]{java.lang.String.class});
        stringPrint.invoke(value, "");
    }
    

    public static void main(String[] args) {
        
        long sstart = System.nanoTime();
        for(int i = 0; i < 10000; i++) {
            try {
                helloWorldReflection();
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }      
        long sstop = System.nanoTime();
        long selapsed = sstop - sstart;                                   
        System.out.println("helloWorldReflection: "+selapsed); 
        
        long start = System.nanoTime();
        for(int i = 0; i < 10000; i++) {
            helloWorld();
        }      
        long stop = System.nanoTime();
        long elapsed = stop - start;
        System.out.println("helloWorld: "+elapsed); 
    }
}

//          results:
//          helloWorldReflection    helloWorld 
//          46050689                1686692
//          51474873                1745592
//          48885044                1732652
//  total:  146,410,606             5,164,936
//  avg:    48,803,535.33           1,721,645.33
//  conclusion: reflected method invocation is about 28 times slower than regular 
//
//}
