package ex21;

import static v.ArrayUtils.*;
import sort.Comparators;
import sort.Selection;

/* p269
  2.1.38 Various types of items. Write a client that generates arrays of items of various
  types with random key values, including the following:
  * String key (at least ten characters), one double value
  * double key, ten String values (all at least ten characters)
  * int key, one int[20] value
  Develop and test hypotheses about the effect of such input on the performance of the
  algorithms in this section.
  
  If key signifies the component type of an array, what you ask for can't be done since mixed 
  types require an array of a supertype which is Object for String and Double or Integer and 
  int[]. For arrays of type Object primitive values will be boxed, so with them it doesn't
  exactly make sense to think of primitive valued keys. It's easy to do sorting of arrays with 
  mixed types using a custom comparator, especially since the Comparator class provides a
  functional interface. The first step is to specify the basis for comparison. For example 
  Strings and arrays could be compared with doubles and ints on the basis of length vs. value 
  or object memory sizes, given that the primitives would be autoboxed. In order to safeguard 
  sorts, a comparator could be implemented that puts all non key type objects first or last like 
  Comparator.nullsFirst(Comparator.naturalOrder()). Using a comparator would require a different 
  implementation of sort methods, since the current sort methods are implemented to use a key 
  type extending Comparable. Therefore my hypothesis is that the current sort methods developed 
  in this course will fail on input of arrays of mixed types. Experiments testing that are below, 
  as well as demonstrations of new sort methods accepting Object[] input and using custom 
  Comparator methods that handle mixed types.
 */

@SuppressWarnings("unused")
public class Ex2138VariousTypesOfItems {

  public static void main(String[] args) {
    
/*  Demonstrations of sort methods using custom Comparators for mixed types

    The comparators demonstrated are:  
 
    Comparators.stringLengthDoubleComparator - optimized to sort Strings in natural order, 
      sorts Doubles in natural order and sorts Strings and Doubles using String length cast
      to Double as the basis of comparison with Double value.
                                                 
    Comparators.stringLengthDoubleFirstComparator - optimized to sort Strings in natural order, 
      sorts Doubles in natural order, sorts Strings and Doubles using String length as the basis 
      of comparison and puts all Doubles before all Strings.
      
    Comparators.stringLengthDoubleLastComparator - optimized to sort Strings in natural order, 
      sorts Doubles in natural order, sorts Strings and doubles using String length as the basis 
      of comparison and puts all Doubles after Strings.
      
    Comparators.doubleStringLengthComparator - optimized to sort Doubles in natural order, sorts
      Strings in natural order and sorts Doubles and Strings using String length as the basis of 
      comparison giving the same results as stringLengthDoubleComparator but faster for arrays
      with mostly Doubles.
      
    Comparators.doubleFirstStringLengthComparator - optimized to sort Doubles in natural order, 
      sorts Strings in natural order, sorts Doubles and Strings using String length as the basis 
      of comparison and puts all Doubles before all Strings giving the same results as 
      stringLengthDoubleFirstComparator but faster for arrays with mostly Doubles.
      
    Comparators.doubleLastStringLengthComparator - optimized to sort Doubles in natural order, 
      sorts Strings in natural order, sorts Doubles and Strings using String length as the basis 
      of comparison and puts all Doubles after all Strings giving the same results as 
      stringLengthDoubleLastComparator but faster for arrays with mostly Doubles.
      
   Comparators.intIntArrayLengthComparator - optimized to sort Integers in natural order, 
      sorts int[]s on the basis of array lengths and sorts Integers and int[]s using array length 
      as the basis of comparison with Integer value.
      
   Comparators.intFirstIntArrayLengthComparator - optimized to sort Integers in natural order, 
      sorts int[]s on the basis of array lengths, sorts Integers and int[]s using array length 
      as the basis of comparison and puts all Integers before all int[]s.
      
   Comparators.intLastIntArrayLengthComparator - optimized to sort Integers in natural order, 
      sorts int[]s on the basis of array lengths, sorts Integers and int[]s using array length 
      as the basis of comparison and puts all Integers after all int[]s.   
           
    All the Comparators listed above as a catchall finally compare Objects that haven't already
    been compared using the int returned by System.identityHashCode(Object) as the basis of 
    comparison. None of them handle null.  
*/
    Object[] ob = {"zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx", 9., 11., 13., 15.};
    Selection.sort(ob, Comparators.stringLengthDoubleComparator);
    System.out.println(Selection.isSorted(ob, Comparators.stringLengthDoubleComparator));
    // true
    Selection.showObjectArray(ob);
    // 9.0 xxxxxxxxxx 11.0 yyyyyyyyyyyy 13.0 zzzzzzzzzzzzzz 15.0 
    
    ob = new Object[]{"zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx", 9., 11., 13., 15.};
    Selection.sort(ob, Comparators.stringLengthDoubleFirstComparator);
    System.out.println("\n"+Selection.isSorted(ob, Comparators.stringLengthDoubleFirstComparator));
    // true
    Selection.showObjectArray(ob);
    // 9.0 11.0 13.0 15.0 xxxxxxxxxx yyyyyyyyyyyy zzzzzzzzzzzzzz 
    
    Object[] oc = {9., 11., 13., 15., "zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx"};
    Selection.sort(oc, Comparators.stringLengthDoubleLastComparator);
    System.out.println("\n"+Selection.isSorted(oc, Comparators.stringLengthDoubleLastComparator));
    // true
    Selection.showObjectArray(oc);
    // xxxxxxxxxx yyyyyyyyyyyy zzzzzzzzzzzzzz 9.0 11.0 13.0 15.0
    
    Object[] od = {"zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx", 9., 11., 13., 15.};
    Selection.sort(od, Comparators.doubleStringLengthComparator);
    System.out.println(Selection.isSorted(od, Comparators.doubleStringLengthComparator));
    // true
    Selection.showObjectArray(od);
    // 9.0 xxxxxxxxxx 11.0 yyyyyyyyyyyy 13.0 zzzzzzzzzzzzzz 15.0 
    
    Object[] oe = new Object[]{"zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx", 9., 11., 13., 15.};
    Selection.sort(oe, Comparators.doubleFirstStringLengthComparator);
    System.out.println("\n"+Selection.isSorted(oe, Comparators.doubleFirstStringLengthComparator));
    // true
    Selection.showObjectArray(oe);
    // 9.0 11.0 13.0 15.0 xxxxxxxxxx yyyyyyyyyyyy zzzzzzzzzzzzzz 
    
    Object[] of = new Object[]{9., 11., 13., 15., "zzzzzzzzzzzzzz", "yyyyyyyyyyyy", "xxxxxxxxxx"};
    Selection.sort(of, Comparators.doubleLastStringLengthComparator);
    System.out.println("\n"+Selection.isSorted(of, Comparators.doubleLastStringLengthComparator));
    // true
    Selection.showObjectArray(of);
    // xxxxxxxxxx yyyyyyyyyyyy zzzzzzzzzzzzzz 9.0 11.0 13.0 15.0 
        
    int[] ia = fillInt(3,()->3); int[] ib = fillInt(8,()->8); int[] ic = fillInt(17,()->17);
    int[] id = fillInt(35,()->35);
    Object[] og = {47, 23, 11, 5, ia, ib, ic, id };
    Selection.sort(og, Comparators.intIntArrayLengthComparator);
    System.out.println("\n"+Selection.isSorted(og, Comparators.intIntArrayLengthComparator));
    // true
    Selection.showObjectArray(og);
    // [3,...,3] 5 [8,...,8] 11 [17,...,17] 23 [35,...,35] 47
    
    Object[] oh = {ia, ib, ic, id, 47, 23, 11, 5};
    Selection.sort(oh, Comparators.intFirstIntArrayLengthComparator);
    System.out.println("\n"+Selection.isSorted(oh, Comparators.intFirstIntArrayLengthComparator));
    // true
    Selection.showObjectArray(oh);
    // 5 11 23 47 [3,...,3] [8,...,8] [17,...,17] [35,...,35] 
    
    Object[] oi = {47, 23, 11, 5, ia, ib, ic, id };
    Selection.sort(oi, Comparators.intLastIntArrayLengthComparator);
    System.out.println("\n"+Selection.isSorted(oi, Comparators.intLastIntArrayLengthComparator));
    // true
    Selection.showObjectArray(oi);
    // [3,...,3] [8,...,8] [17,...,17] [35,...,35] 5 11 23 47 
    
    
//  Tests of hypothesis that current sort methods developed in this course 
//  fail on input of arrays of mixed types:
    
    //String[] sa = new String[]{"tenletters", 3.5}; // Type mismatch: cannot convert 
                                                     // from double to String
    //Double[] da = new Double[]{"tenletters", 3.5}; // Type mismatch: cannot convert 
                                                     // from String to Double
  
    Object[] oa = new Object[]{"tenletters", 3.5};
    
//  sort.Insertion.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                             // in the type Insertion is not applicable for the 
                             // arguments (Object[])
    
//  sort.Selection.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                             // in the type Insertion is not applicable for the 
                             // arguments (Object[])  
    
//  sort.Shell.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                         // in the type Insertion is not applicable for the arguments 
                         // (Object[])
    
    //int[] ia = new int[]{1, new int[20]}; // Type mismatch: cannot convert from int[] 
                                            // to int
    //Integer[] ia2 =  new Integer[]{1, new int[20]}; // Type mismatch: cannot convert 
                                                      // from int[] to Integer
    Object oa2 = new Object[]{1, new int[20]}; 
    
// sort.Insertion.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                            // in the type Insertion is not applicable for the arguments 
                            // (Object[])

// sort.Selection.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                            // in the type Insertion is not applicable for the arguments
                            // (Object[])  

// sort.Shell.sort(oa); // Eclipse won't run this because "The method sort(T[]) 
                        // in the type Insertion is not applicable for the arguments
                        // (Object[])

 
  }

}
