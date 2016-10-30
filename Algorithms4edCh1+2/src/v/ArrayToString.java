package v;
//package vutils;
//
//import static vutils.ArrayUtils.*;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ArrayToString {
//  
//  private static int maxLineLength = 80;
//  
//  public static String arrayToString(Object a, int...options) {
//    // represent a as a string formatted to max line length given
//    // by options[0] if it exists or  maxLineLength by default if 
//    // options.length == 0 or options[0] < 1 where maxLineLength 
//    // is a global static int. rootComponentType(a).getSimpleName() 
//    // is prefixed to the output by default. if options.length == 2, 
//    // rootComponentType(a).getName() is prefixed and if 
//    // options.length == 3 there is no prefix.
//    if( a == null) throw new IllegalArgumentException("arrayToString: "
//        + "argument can't be null");
//    if (!a.getClass().isArray()) throw new IllegalArgumentException("arrayToString: "
//        + "argument must be an array"); 
//    int dim = dim(a);
//    String prefix = rootComponentType(a).getSimpleName();
//    if (options.length == 2) prefix = rootComponentType(a).getName();
//    else if (options.length == 3) prefix = "";
//    // must box 1D arrays of primitives since using Arrays.deepToString(Object[])
//    if (rootComponentType(a).isPrimitive() && dim < 2) a = box(a);
//    int maxlen;
//    if (options.length == 0 || options[0] < 1) {
//      maxlen = maxLineLength;
//    } else maxlen = options[0];
//    
//    String b;
//    String s;
//    String[] sa;
//    boolean ok;
//    int indent;
//    Pattern pat = Pattern.compile("(^[\\[]+)");
//    Matcher matcher;
//    String d = Arrays.deepToString((Object[]) a).replaceAll(" ", "");
//    
//    // try alternatives until one has max line length <= maxlen 
//    // or return the one with the shortest max line length
//    s = prefix + d;
//    if (s.length() <= maxlen) return s;
//    
//    if (!prefix.equals("")) {
//      s = prefix+"[\n  "+d.substring(1,d.length());
//      if (s.length() <= maxlen) return s;
//    }
//    
//    for (int i = dim-1; i > -1; i--) {
//      b = i > 0 ? repeat(']',i)+"," : ",";
//      if (prefix.equals("")) {
//        sa = d.replaceAll(b, b+"\n").split("\n");
//      } else {
//        sa = (prefix+"[\n"+d.replaceAll(b, b+"\n")).split("\n");
//      }
//      ok = true;
//      for (int j = 1; j < sa.length; j++) {
//        if (!prefix.equals("") && j == 1) sa[1] = sa[1].substring(1,sa[1].length());
//        matcher = pat.matcher(sa[j]);
//        if (matcher.find()) {
//          int len = matcher.group(1).length();
//          if (prefix.equals("")) {
//            indent = dim - len;
//          } else {
//            indent = dim + 1 - len;
//          }
//        } else {
//          if (prefix.equals("")) {
//            indent = dim;
//          } else {
//            indent = dim + 1;
//          }
//        }
//        sa[j] = repeat(' ', indent) + sa[j];
//        if (sa[j].length() > maxlen) ok = false;
//      }
//      if (i == 0 || ok) return String.join("\n", sa);
//    }
//    
//    return "none";
//  }
//
//  public static void main(String[] args) {
// //2345678901234567890123456789012345678901234567890123456789012345678901234567890123456789 //90
// 
//    ArrayList<Integer> l01 = new ArrayList<>(Arrays.asList(0,1,2));
//    ArrayList<Integer> l02 = new ArrayList<>(Arrays.asList(1,2));
//    ArrayList<Integer> l03 = new ArrayList<>(Arrays.asList(2,3,4));
//    ArrayList<Integer> l04 = new ArrayList<>(Arrays.asList(3,4));
//    ArrayList<Integer> l05 = new ArrayList<>(Arrays.asList(4,5,6));
//    
//    ArrayList<ArrayList<Integer>> l21 = new ArrayList<>();
//    l21.add(l01); l21.add(l02); l21.add(l03);
////    (Arrays.asList(l01,l02,103));
//    ArrayList<ArrayList<Integer>> l22 = new ArrayList<>(Arrays.asList(l02,l03));
//    ArrayList<ArrayList<Integer>> l23 = new ArrayList<>();
//    l23.add(l03); l23.add(l04); l23.add(l05);
////    (Arrays.asList(l03,l04,105));
//    ArrayList<ArrayList<Integer>> l24 = new ArrayList<>(Arrays.asList(l04,l05));
//    
//    ArrayList<ArrayList<ArrayList<Integer>>> l31 = new ArrayList<>(Arrays.asList(l21,l22));
//    ArrayList<ArrayList<ArrayList<Integer>>> l32 = new ArrayList<>(Arrays.asList(l22,l23));
//    ArrayList<ArrayList<ArrayList<Integer>>> l33 = new ArrayList<>(Arrays.asList(l23,l24));
//    
//    ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> l41 = new ArrayList<>(Arrays.asList(l31,l32));
//    ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> l42 = new ArrayList<>();
//    l42.add(l32); l42.add(l33); l42.add(l31);
////    (Arrays.asList(l32,l33,131));
//   
//    ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>> l51 = 
//        new ArrayList<>(Arrays.asList(l41,l42));
//    
////    Object a = makeArrayFromList(l51);
////    System.out.println(a.getClass().getSimpleName());
////    System.out.println(a.getClass().getComponentType().getSimpleName()); 
////    System.out.println(arrayToString(a,80,1,2));
//    //  [[[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]],
//    //    [[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]]],
//    //   [[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]],
//    //    [[[2,3,4],[3,4],[4,5,6]],[[3,4],[4,5,6]]],
//    //    [[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]]]]
////    System.out.println();
////    System.out.println(arrayToString(a,80,1));  
//    //  java.lang.Integer[
//    //    [[[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]],
//    //     [[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]]],
//    //    [[[[1,2],[2,3,4]],[[2,3,4],[3,4],[4,5,6]]],
//    //     [[[2,3,4],[3,4],[4,5,6]],[[3,4],[4,5,6]]],
//    //     [[[0,1,2],[1,2],[2,3,4]],[[1,2],[2,3,4]]]]]
// 
//    
//    Integer[] ia19 = {1,2};
//    Integer[] ia20 = {3,4};
//    Integer[] ia21 = {5,6};
//    Integer[] ia22 = {7,8}; 
//    Integer[][] ia23 = {ia19,ia20};
//    Integer[][] ia24 = {ia21,ia22};
//    Integer[][][] ia25 = {ia23,ia24};
//
//    int[] a19 = {1,2};
//    int[] a20 = {3,4};
//    int[] a21 = {5,6};
//    int[] a22 = {7,8}; 
//    int[][] a23 = {a19,a20};
//    int[][] a24 = {a21,a22};
//    int[][][] a25 = {a23,a24};
//    
////    
////    System.out.println(arrayToString(ia25)); //Integer[[[1,2],[3,4]],[[5,6],[7,8]]]
//    System.out.println(arrayToString(a25,5,1,1));
////    System.out.println();
////    System.out.println(arrayToString(ia25,20));
////    //  Integer[
////    //    [[1,2],[3,4]],
////    //    [[5,6],[7,8]]]
////    System.out.println(arrayToString(a25,20));
////    System.out.println();
////    System.out.println(arrayToString(a19));
////    
////    int[][][] v = new int[3][4][5];
////    System.out.println(v.getClass()); // class [[[I
////    System.out.println(v.getClass().getName()); //[[[I
////    try {
////      Class<?> y = Class.forName("[[[I"); //ok
////    } catch (ClassNotFoundException e) {
////      e.printStackTrace();
////    }
//    
//    
//    
//    
//    
//
//  }
//
//}
