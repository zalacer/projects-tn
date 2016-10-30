package v;

import static v.ArrayUtils.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class List2Array {
  
  public static int dimOfList(List<?> list) {
    int c = 0;
    Object o = list;
    while (true) {
      if (o instanceof List) {  
        c++;
      } else break;
      o = ((List<?>) o).get(0);
    }
    return c;
  }
  
  public static String rootElementTypeNameOfList(List<?> list) {
    Object o = list;
    while (true) {
      if (o instanceof List) { 
        o = ((List<?>) o).get(0);
      } else return o.getClass().getName();
    }
  }
  
  public static Class<?> rootElementTypeOfList(List<?> list) {
    Object o = list;
    while (true) {
      if (o instanceof List) { 
        o = ((List<?>) o).get(0); // assumes 0th element isn't null
      } else return o.getClass();
    }
  }
  
  public static Object makeArrayFromList(List<?> list) {
    if (list == null) throw new IllegalArgumentException("makeArrayFromList "
        + "the list cannot be null");
    
    // determine nesting depth and rootElementType of the list
    int dim = 0;       // nesting depth
    Class<?> c = null; // rootElementType
    boolean foundNonNull;
    Object o = list;
    while (true) {
      foundNonNull = false;
      if (o instanceof List) {  
        dim++;
      } else {
        c = o.getClass();
        break;
      }
      for (Object e : (List<?>) o) {
        if (e != null) {
          o = e;
          foundNonNull = true;
          break;
        }
      }
      if (!foundNonNull) throw new NonTerminalNestingLevelWithAllNullElementsException(
          "makeArrayFromList: cannot determine nesting depth or rootElementType of the list");
    }
    
//    if (c == null) throw new UnknownListRootElementTypeException(
//        "makeArrayFromList: cannot determine root element type of the list");

    if (dim > 255) throw new ListNestingTooHighException("makeArrayFromList: list nesting "
        + "over 255 levels isn't supported since that's the limit on array dimensionality");
    
    // dimensions array for Array.newInstance
    int[] dimensions = new int[dim];
    dimensions[0] = list.size();
    
    Object a = Array.newInstance(c, dimensions); // to be the output array
    
    int i = 0; Object[] b = (Object[]) a;
   
    if (dim == 1) { // recursion termination condition
      for (Object e : list) b[i++] = e;
    } else {
      for (Object e : list) b[i++] = makeArrayFromList((List<?>) e);
    }
    
    return a;
  }
  
  public static void main(String[] args) {
    
    ArrayList<Integer> l01 = new ArrayList<>(Arrays.asList(0,1,2));
    ArrayList<Integer> l02 = new ArrayList<>(Arrays.asList(1,2));
    ArrayList<Integer> l03 = new ArrayList<>(Arrays.asList(2,3,4));
    ArrayList<Integer> l04 = new ArrayList<>(Arrays.asList(3,4));
    ArrayList<Integer> l05 = new ArrayList<>(Arrays.asList(4,5,6));
    
    ArrayList<ArrayList<Integer>> l21 = new ArrayList<>();
    l21.add(l01); l21.add(l02); l21.add(l03);
//    (Arrays.asList(l01,l02,103));
    ArrayList<ArrayList<Integer>> l22 = new ArrayList<>(Arrays.asList(l02,l03));
    ArrayList<ArrayList<Integer>> l23 = new ArrayList<>();
    l23.add(l03); l23.add(l04); l23.add(l05);
//    (Arrays.asList(l03,l04,105));
    ArrayList<ArrayList<Integer>> l24 = new ArrayList<>(Arrays.asList(l04,l05));
    
    ArrayList<ArrayList<ArrayList<Integer>>> l31 = new ArrayList<>(Arrays.asList(l21,l22));
    ArrayList<ArrayList<ArrayList<Integer>>> l32 = new ArrayList<>(Arrays.asList(l22,l23));
    ArrayList<ArrayList<ArrayList<Integer>>> l33 = new ArrayList<>(Arrays.asList(l23,l24));
    
    ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> l41 = new ArrayList<>(Arrays.asList(l31,l32));
    ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> l42 = new ArrayList<>();
    l42.add(l32); l42.add(l33); l42.add(l31);
//    (Arrays.asList(l32,l33,131));
   
    ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>> l51 = 
        new ArrayList<>(Arrays.asList(l41,l42));
    
//    int nesting = 5;
//    ArrayList<?> alx = l51;
//
//    // how high can list nesting go?
//    while (true) {
//      alx = new ArrayList<>(Arrays.asList(alx));
//      nesting++;
//      System.out.println(nesting); //3,549,760 without exception
//    }
    
//    System.out.println(l51.toString().replaceAll(" ", ""));
//    //[[[[[0,1],[1,2]],[[1,2],[2,3]]],[[[1,2],[2,3]],[[2,3],[3,4]]]],[[[[1,2],[2,3]],[[2,3],[3,4]]],[[[2,3],[3,4]],[[3,4],[4,5]]]]]
//
    Object[] a = l51.toArray();
    System.out.println(a.getClass().getName()); //[Ljava.lang.Object;
    pa(a);
    //[[[[[0,1],[1,2]],[[1,2],[2,3]]],[[[1,2],[2,3]],[[2,3],[3,4]]]],[[[[1,2],[2,3]],[[2,3],[3,4]]],[[[2,3],[3,4]],[[3,4],[4,5]]]]]
    // after introducing irregularities:
    //[[[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]],[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]]],[[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]],[[[2,3,4],[3,4],[4,5,6]],[[3,4],[4,5,6]]],[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]]]]
    System.out.println("dim(a)="+dim(a)); // 1
    for (int i = 0; i < l51.size(); i++) System.out.println(l51.get(i).size()); //2 2
    System.out.println(l51.get(1).getClass().getName());
    int c = 0;
    Object o = l51;
    while (true) {
//      if (o.getClass().getName().equals("java.util.ArrayList")) {
      if (o instanceof List) {  
        c++;
      } else break;
      o = ((List<?>) o).get(0);
    }
    System.out.println(("c="+c)); //5
    System.out.println(dimOfList(l51));  
    System.out.println(rootElementTypeNameOfList(l51)); //java.lang.Integer
      
    Object a1 = Array.newInstance(rootElementTypeOfList(l51),5,0,0,0,0);
    pa(a1);
    System.out.println(a1.getClass().getSimpleName()); //Integer[][][][][]
    
    Object a2 = makeArrayFromList(l51);
    pa(a2);
    //[[[[[0,1],[1,2]],[[1,2],[2,3]]],[[[1,2],[2,3]],[[2,3],[3,4]]]],[[[[1,2],[2,3]],[[2,3],[3,4]]],[[[2,3],[3,4]],[[3,4],[4,5]]]]]
    // after introducing irregularities
    //[[[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]],[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]]],[[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]],[[[2,3,4],[3,4],[4,5,6]],[[3,4],[4,5,6]]],[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]]]]
   
    System.out.println(a2.getClass().getName()); //[[[[[Ljava.lang.Integer;
    System.out.println();
    List<?> l2 = toNestedList(a2);
    System.out.println(l2.equals(l51)); //true
    Object a3 = makeArrayFromList(l2);
    System.out.println(Arrays.equals((Integer[][][][][]) a2, (Integer[][][][][]) a3)); //true
    System.out.println(Arrays.deepEquals((Object[]) a2, (Object[]) a3)); //true
    
    System.out.println(numel(a2)); //65
    Integer[] flata2 = (Integer[]) flatLine(a2);
    System.out.println(flata2.length);
    System.out.print("flata2=");pa(flata2);
////  [0,1,2,1,2,2,3,4,1,2,2,3,4,1,2,2,3,4,2,3,4,3,4,4,5,6,1,2,2,3,4,2,3,4,3,4,4,5,6,2,3,4,3,4,4,5,6,3,4,4,5,6,0,1,2,1,2,2,3,4,1,2,2,3,4]

      int[][][][][] a4 = (int[][][][][]) unbox(a2);
      int[] flata4 = (int[]) flatLine(a4);
      System.out.println(flata4.length);
      System.out.print("flata4=");pa(flata4);
      
//      long[] a5 = new long[0];
//      long[] a6 = (long[]) ArrayUtils.clone(a5);
//      System.out.println(Arrays.equals(a5,a6)); //true
      
  }

}
