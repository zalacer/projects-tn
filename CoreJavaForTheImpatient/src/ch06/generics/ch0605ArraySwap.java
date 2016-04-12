package ch06.generics;

import java.util.Arrays;

import utils.Arrays2;

//5. Consider this variant of the swap method where the array can be supplied with varargs:
//public static <T> T[] swap(int i, int j, T… values) {
//    T temp = values[i];
//    values[i] = values[j];
//    values[j] = temp;
//    return values;
//}
//Now have a look at the call
//Double[] result = Arrays.swap(0, 1, 1.5, 2, 3);
//What error message do you get? Now call
//Double[] result = Arrays.<Double>swap(0, 1, 1.5, 2, 3);
//Has the error message improved? What do you do to fix the problem?

// Yes, the error message improved and eclipse prohibits execution.
// The problem can be fixed by changing all varargs params to double or by using Numeric 
// type without generics, converting all varargs parms to doubles and putting them in 
// a new Double[] for swap and return.

public class ch0605ArraySwap {

  public static void main(String[] args) {

    //  Double[] result = Arrays2.swap(0, 1, 1.5, 2, 3);
    //  eclipse prohibits execution and warns: Type mismatch: cannot convert from 
    //  Number&Comparable<?>[] to  Double[]; The method swap(int, int, double, int, int) 
    //  is undefined for the type Arrays2
      
    //  Double[] result = (Double[]) Arrays2.swap(0, 1, 1.5, 2, 3);
    //  eclipse allows execution but warns: Type safety: A generic array of 
    //  Number&Comparable<?> is created for a varargs parameter
    
    //  Double[] result = Arrays2.<Double>swap(0, 1, 1.5, 2, 3);
    //  eclipse prohibits execution and warns: The parameterized method 
    //  <Double>swap(int, int, Double...) of type Arrays2 is not applicable for 
    //  the arguments (int, int, Double, Integer, Integer)

    // to fix the problem change all varargs to double
    Double[] result = Arrays2.<Double>swap(0, 1, 1.5, 2., 3.);
    System.out.println(Arrays.toString(result)); // [2.0, 1.5, 3.0]

    // could use Numeric type and convert all varargs params to double with doubleValue()
    Double[] result4 = Arrays2.swap4(0, 1, 1.5, 2, 3);
    System.out.println(Arrays.toString(result4)); // [2.0, 1.5, 3.0]

    // additional tests below done without Arrays2
    // Double[] result = swap(0, 1, 1.5, 2, 3);
    // Type mismatch: cannot convert from Number&Comparable<?>[] to Double[]

    // works after making all values doubles
    // Double[] result = swap(0, 1, 1.5, 2., 3.);
    // System.out.println(Arrays.toString(result)); // [2.0, 1.5, 3.0]

    // Double[] result2 = swap(0, 1, 1.5, 2, 3);
    // Type mismatch: cannot convert from Number&Comparable<?>[] to Double[]

    // Double[] result3 = Arrays.<Double>swap(0, 1, 1.5, 2, 3);
    // The method swap(Object[], int, int) in the type Arrays is not applicable 
    // for the arguments (int, int, double, int, int)

    // Double[] result3 = <Double>swap(0, 1, 1.5, 2, 3);
    //Syntax error on token "<", delete this token
    //Type safety: A generic array of Number&Comparable<?> is created for a varargs parameter

    // Double[] result = (Double[]) swap(0, 1, 1.5, 2, 3);
    // Type safety: A generic array of Number&Comparable<?> is created for a varargs parameter
    // throws java.lang.ClassCastException

    // Double[] result4 = (Double[]) swap4(0, 1, 1.5, 2, 3);
    // System.out.println(Arrays.toString(result4)); // [2.0, 1.5, 3.0]
    // works fine with no generics by conversion to Double
    // remains to be fully tested; should also work with floatValue, intValue, longValue
    // but not without potential cutoff but that could happen with BigInt and BigDecimal
    // anyway; to avoid that mayber could convert to BigDecimal as ultimate Number subtype

  }

}
