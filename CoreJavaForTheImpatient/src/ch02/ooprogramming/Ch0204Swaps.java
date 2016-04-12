package ch02.ooprogramming;

import java.lang.reflect.Field;
import java.util.Objects;

import org.omg.CORBA.IntHolder;

import utils.Ipair;

// 4. Why can’t you implement a Java method that swaps the contents of two int
// variables? Instead, write a method that swaps the contents of two IntHolder
// objects. (Look up this rather obscure class in the API documentation.) Can you swap
// the contents of two Integer objects?

// There is no way to pass primitive variables by reference to a method.
// However the values of two int variables can be swapped using a method provided
// the variables are accessible and mutable, for example when they are class or 
// instance variables. See swapClassVars() below for an example.

// IntHolder swap works since the IntHolder value is available as a mutable field, 
// see demo below.

// On the surface Integer swap isn't possible since an Integer is immutable and its 
// value is not available for setting since it has no setter and is private final. 
// However, it can be done with reflection by changing the accessibility of its value
// field to true and in Java 8 without side effects due to JVM caching of values for 
// values between -128 to 127.  This is demonstrated using swapIntegerWithReflection.
// It's also possible to safely swap Integers by encapsulating them in a mutable object,
// such as IntHolder or Ipair which is demonstrated also.

// http://stackoverflow.com/questions/3624525/how-to-write-a-basic-swap-function-in-java

public class Ch0204Swaps {
    
    static int a = 1;
    static int b = 2;
    
    public static void swapClassFields() {
        int c = a;
        a = b;
        b = c;
    }
    
    public static void swapIntHolder(IntHolder a, IntHolder b) {
        int c = a.value;
        a.value = b.value;
        b.value = c;   
    }
    
    public static void naiveIntegerSwap(Integer a, Integer b) {
        // this fails to swap the Integers
        int aval = a.intValue();
        a = b.intValue();
        b = aval;
    }
    
    public static void swapIntegerWithReflection(Integer a, Integer b){
      Field value = null;
      try {
        value = Integer.class.getDeclaredField("value");
      } catch (NoSuchFieldException | SecurityException e) {
        e.printStackTrace();
      }
      if (Objects.nonNull(value)) {
        value.setAccessible(true);
        int t = a;
        try {
          value.setInt(a, value.getInt(b));
          value.setInt(b, t);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
        value.setAccessible(false);
      }
    }

    public static void swapIpair(Ipair i) {
        Integer t = i.get1();
        i.set1(i.get2());
        i.set2(t);   
    }
    
    public static void printIdentityHashCode(Object x) {
        System.out.println("identityHashCode("+x+") = "+Integer.toHexString(System.identityHashCode(x)));
        // this method autoboxes primitives
        // Finding the location of a reference to an object is not something you do in Java. 
        // You can get the memory address from the hashCode method of java.lang.Object, 
        // but it is overridden in most classes so as to hide that information.
    }
    
    public static void printObjectsHashCode(Object x) {
        // this method autoboxes primitives, depends on the Object's internal state 
        // and is not simply an address
        System.out.println("objectsHashCode("+x+") = "+Integer.toHexString(Objects.hashCode(x)));
    }

    public static void main(String[] args) {

        // swap class fields demo
        System.out.println("a = "+a); // a = 1  
        System.out.println("b = "+b); // b = 2
        swapClassFields();
        System.out.println("a = "+a); // a = 2
        System.out.println("b = "+b); // b = 1 
        
        // swap IntHolder demo
        // this method swaps the Integer contents of two IntHolder instances
        // does not affect the IntHolder object identities as shown by their identityHashCodes
        // also does not affect the identities of the Integers they contain
        IntHolder ih1 = new IntHolder(5);
        printIdentityHashCode(ih1); //2a139a55
        System.out.println("ih1.value = "+ih1.value); // 5
        printIdentityHashCode(ih1.value); //15db9742
        IntHolder ih2 = new IntHolder(9);
        printIdentityHashCode(ih2); //6d06d69c
        System.out.println("ih2.value = "+ih2.value); // 9
        printIdentityHashCode(ih2.value); //7852e922
        swapIntHolder(ih1,ih2);      
        System.out.println("ih1.value = "+ih1.value); // 9
        System.out.println("ih2.value = "+ih2.value); // 5
        printIdentityHashCode(ih1); //2a139a55
        printIdentityHashCode(ih2); //6d06d69c
        printIdentityHashCode(ih1.value); //7852e922 (shows the Integer (cast from int) identies 
        printIdentityHashCode(ih2.value); //15db9742    have been swapped, not changed)

        // naive swap of Integers demo
        // doesn't work because method call swaps only copies of Integer references
        // that vanish when the method ends
        Integer p = 9;
        printIdentityHashCode(p); //6d06d69c
        Integer q = 7;
        printIdentityHashCode(q); //7852e922
        naiveIntegerSwap(p,q);
        System.out.println("p="+p); // 9
        System.out.println("q="+q); // 7
        // without method call the original references are swapped
        // same as doing Integer r = p; p = q; q = r; which just
        // interchanges their names without changing their values
        Integer r = p; p = q; q = r;
//        int aval = p.intValue();
//        p = q.intValue();
//        q = aval;
        System.out.println("p="+p); // 7 
        System.out.println("q="+q); // 9
        printIdentityHashCode(p); //7852e922
        printIdentityHashCode(q); //6d06d69c
        
        // swap Integers with reflection demo
        // this is the only that method directly swaps Integer values.
        // as with swapIntHolder it doen't affect the enapsulating 
        // object reference, however that's now an Integer
        Integer five = new Integer(5);
        printIdentityHashCode(five); //4e25154f
        Integer seven = new Integer(7);
        printIdentityHashCode(seven); //70dea4e
        Integer t = new Integer(5);
        Integer u = new Integer(7);
        int v= 5;
        int w = 7;
        swapIntegerWithReflection(five, seven);
        System.out.println("five="+five);   //7 (changed)
        System.out.println("seven="+seven); //5 (changed)
        printIdentityHashCode(five); //4e25154f (unchanged)
        printIdentityHashCode(seven); //70dea4e (unchanged)
        System.out.println("t="+t); // 5 (unchanged)
        System.out.println("u="+u); // y (unchanged)
        System.out.println("v="+v); // 5 (unchanged)
        System.out.println("w="+w); // 7 (unchanged)
   
        // Ipair swap demo
        // this method swaps the order of the two Integer fields in an Ipair
        // without affecting the Ipair references
        Ipair i = new Ipair(15,11);
        System.out.println(i); // (15,11)    
        printIdentityHashCode(i); //33909752
        swapIpair(i);
        System.out.println(i); // (11,15)
        printIdentityHashCode(i); //33909752

    }

}
