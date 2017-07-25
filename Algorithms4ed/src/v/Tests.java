package v;


import static v.Darray.*;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

import u.PrimitiveIterator.OfChar;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Stream;
import java.util.PrimitiveIterator.OfInt;

@SuppressWarnings("unused")
public class Tests {
  
  @FunctionalInterface
  private static interface Con1<T> extends Consumer<T>{
    void accept(T t);
  }
  
  @FunctionalInterface
  public interface CharConsumer {
    void accept(char value);
    default CharConsumer andThen(CharConsumer after) {
      Objects.requireNonNull(after);
      return (char t) -> {accept(t); after.accept(t);};
    }
  }
  
  public static interface Greet {
    public String greet(String x);
  }
  
  public static String HelloWorld() {
    Greet greeter = new Greet(){
      public String greet(String x) {
        return x;
       }
    };
    return greeter.greet("hello");
  }
  
  public static <T> Iterable<T> toIterable2(T[] a) {
    Iterator<T> it = new Iterator<T>() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() {return c < len;}
      public T next() {return a[c++];}
    };
    Iterable<T> itbl = new Iterable<T>() {
      public Iterator<T> iterator() {return it;}
    };
    return itbl;
  }
  
  public static <T> Iterable<T> toIterable(T[] a) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return Arrays.stream(a).iterator();
        //return new Iterator<T>() {
        //  int len = a.length;
        //  int c = 0;
        //  public boolean hasNext() {return c < len;}
        //  public T next() {return a[c++];}
        //};
      }
    };
  }
  
  public static Iterable<Integer> toIterable(int[] a) {
    return new Iterable<Integer>() {
      public OfInt iterator() {
        return new OfInt() {
          int len = a.length;
          int c = 0;
          public boolean hasNext() {return c < len;}
          public int nextInt() {return a[c++];}
        };
      }
    };
  }
  
  public static Iterable<Integer> toIterable3(int[] a) {
    return new Iterable<Integer>() {
      public OfInt iterator() {
        return Arrays.stream(a).iterator();
      }
    };
  }
  
  public static Iterable<Character> toIterable(char[] a) {
    return new Iterable<Character>() {
      public OfChar iterator() {
        return new OfChar() {
          int len = a.length;
          int c = 0;
          public boolean hasNext() {return c < len;}
          public char nextChar() {return a[c++];}
//          public char nextChar() {
//            if (hasNext()) {
//              return a[c++];
//            } else {
//              throw new NoSuchElementException();
//            }
//          }
//          @Override
//          public void forEachRemaining(CharConsumer action) {
//            if (action == null) throw new NullPointerException();
//            if (c >= 0 && c < a.length) {
//                do { action.accept(a[c]); } while (++c < a.length);
//            }
//          }
        };
      }
    };
  }

  
  //Ex1113print2DArrayTranspose
  public static void printTransposition(int[][] m) {
    int n = m.length;
    int o = m[0].length;
    int max = 0;
    int len = 0;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
        len = (""+m[i][j]).length();
        if (len > max) max = len;    
      }
    }
    max = max+1;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
        System.out.printf("%-"+max+"d", m[i][j]);       
      }
      System.out.println();
    }
  }

  public static void main(String[] args) {
    
    Darray<Integer> da6 = new Darray<Integer>();
    System.out.println(da6.size()); //16
    System.out.println(da6.elements.length);
    da6.add(1); da6.add(2); da6.add(3); da6.add(4); da6.add(5);
    System.out.println(da6);
    System.out.println(da6.elements.length);
    da6.resize();
    System.out.println(da6);
    System.out.println(da6.elements.length);
    Iterator<Integer> it = da6.iterator();
    while(it.hasNext()) System.out.print(it.next()); System.out.println(); //12345
    
//    Darray<Integer> da3 = new Darray<Integer>();
//    System.out.println(da3);
//    Darray<Integer> da4 = new Darray<Integer>(da3);
//    System.out.println(da4);
//    Darray<Integer> da5 = new Darray<Integer>(new ArrayList<Integer>());
//    System.out.println(da5);
    
//    Darray<Integer> da1 = new Darray<Integer>(new Integer[]{null,null,null,null,null});
//    System.out.println(da1);
//    da1.add(1);
//    System.out.println(da1); //Darray(null,null,null,null,null,1)
//    da1.removeNulls(); //Darray(1)
////    da1.convertNulls(); //Darray(0,0,0,0,0,1)
//    System.out.println(da1); 
//    Darray<Integer> da2 = new Darray<Integer>(da1);
//    System.out.println(da2); //Darray(1)
//    Integer[] ia17 = {1};
//    Integer[] ia18 = (Integer[]) Darray.clone(ia17);
//    pa(ia18); //Integer[1]
// 
    
////    //Darray test
//    Darray<Integer> ar1 = new Darray<Integer>(new Integer[]{1,2,3,4,5});
//    System.out.println("ar1.getSize="+ar1.size());
//    System.out.println(ar1); //Darray(1,2,3,4,5)
//    System.out.println(ar1.foldLeft((x,y)->x+y, 0)); //15
//    Darray<Integer> ar2 = new Darray<Integer>(new Integer[]{6,7,8,9,10});
//    System.out.println("ar2.size="+ar2.size());
//    Darray<Darray<Integer>> ar3 = new Darray<Darray<Integer>>();
//    System.out.println("ar3.size="+ar3.size()); //0
//    ar3.add(ar1); ar3.add(ar2);
//    System.out.println("ar3.size="+ar3.size()); //2
//    System.out.println(ar3); //Aray(Aray(1,2,3,4,5),Aray(6,7,8,9,10))
//    
//    
//    //tabulate test
//    Integer[] ia15 = tabulate(5, x -> x+1);
//    pa(ia15);
//    Integer[][] ia16 = tabulate(3, 3, (x,y)->x+y);
//    pa(ia16);
//    
//    //range test
//    int[] ia7 = range(5,25,3);
//    pa(ia7); //int[5,8,11,14,17,20,23]
//    int[] ia8 = range(3,7);
//    pa(ia8); //int[3,4,5,6]
//    int[] ia9 = range(-3,7);
//    pa(ia9); //int[-3,-2,-1,0,1,2,3,4,5,6]
//    int[] ia10 = range(-5,25,3);
//    pa(ia10); //int[-5,-2,1,4,7,10,13,16,19,22]
//    int[] ia11 = range(-5,-12,-3);
//    pa(ia11); //int[-5,-8,-11]
//    int[] ia12 = range(-3,-7);
//    pa(ia12); //int[]
//    int[] ia13 = range(-5,-12, 3);
//    pa(ia13); //int[]
//    int[] ia14 = range(-7, -3);
//    pa(ia14);
//    
//    //iterate test
//    Integer[] itia4 = iterate(new Integer(1), 5, x->x*2);
//    pa(itia4); //Integer[1,2,4,8,16]
//    Integer[] itia5 = iterate(new Integer(1), 5, x->x+2);
//    pa(itia5); //Integer[1,3,5,7,9]
//    int[] itia6 = iterateInt(1, 5, x->x+2);
//    pa(itia6); //int[1,3,5,7,9]
// 
//    
//    //empty test
//    Integer[] ia3 = empty(Integer.class);
//    pa(ia3); //Integer[]
//    
//    //repeat test
//    System.out.println(repeat('J', -2));
//    
//    // reverse test
//    Integer[] ia2 = {1,2,3,4,5};
//    pa(reverse(ia2));
//    
//    //distinct test
//    Integer[] ia1 = {1,1,2,2,2,3,3,3,4,4,4,4,5,5,5,5,5};
//    pa(distinct(ia1)); //
//    
//    System.out.println(int.class.isPrimitive()); //true
//    
//    // scanLeft and scanRight tests
//    int[] a1 = {1,2,3,4,5};
//    int[] scla1 = scanLeft(a1, (y, z) -> y*z, 2); 
//    pa(scla1); ////int[2,2,4,12,48,240]
//    int[] scra1 = scanRight(a1, (y, z) -> y*z, 2); 
//    pa(scra1); ////int[240,240,120,40,10,2]
//    int[] scla2 = scanLeft(a1, (y, z) -> y+z, 2); 
//    pa(scla2); //int[2,3,5,8,12,17]
//    int[] scra2 = scanRight(a1, (y, z) -> y+z, 2); 
//    pa(scra2); //int[17,16,14,11,7,2]
    
    //aggregate and aggregateMulti tests
//    Character[] ca37 = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
//        'n','o','p','q','r','s','t','u','v','w','x','y','z'};
//    Integer aca37 = 
//        aggregateMulti(ca37, 0, (sum,ch)->sum+(int)ch, (p1,p2)->p1+p2, 7);
//    System.out.println(aca37); //2847
//    Integer amca37 = 
//        aggregateMulti(ca37, 0, (sum,ch)->sum+(int)ch, (p1,p2)->p1+p2, 7);
//    System.out.println(amca37); //2847
//    int sum = 0;
//    for (int i = 0; i < ca37.length; i++) sum+=(int)ca37[i];
//    System.out.println(sum); //2847  
    
    
    // iterator tests
//    OfBoolean it = iterator(new boolean[]{true,false,true});
//    while (it.hasNext()) System.out.println(it.next());
    
    
//    System.out.println(Long.MAX_VALUE > 255L*Integer.MAX_VALUE);
// 
//    String x = "[[[0,1,2";
////    if (x.startsWith("[")) System.out.println(true); //true
//    int dim = 5;
//    int indent = 0;
//    Matcher matcher = Pattern.compile("(^[\\[]+)").matcher(x);
//    if (matcher.find()) indent = 1 + dim - matcher.group(1).length();
//    System.out.println(indent); //3
//    int[] a1 = {1,2,3};
//    System.out.println(a1.getClass().getComponentType().getSimpleName()); //int
//    
//    int[] ia19 = {1,2};
//    int[] ia20 = {3,4};
//    int[] ia21 = {5,6};
//    int[] ia22 = {7,8}; 
//    int[][] ia23 = {ia19,ia20};
//    int[][] ia24 = {ia21,ia22};
//    int[][][] ia25 = {ia23,ia24};
//    
//    System.out.println(Arrays.deepToString(ia23));
    
    
//    char[] c1 = {'a','b','c'};
//    Iterable<?> itc1 = toIterable(c1);
//    OfChar itc = (OfChar) itc1.iterator();
////    Stream<Character> x = Stream.generate(() -> itc.nextChar()).limit(c1.length);
////    System.out.println(itc.nextChar().getClass().getName());
//    while (itc.hasNext()) System.out.print(itc.next()+","); System.out.println();
//    
//    int[] a1 = {1,2,3,4,5};
//    Iterable<?> ital = toIterable(a1);
//    OfInt ita = (OfInt) ital.iterator();
////    System.out.println(ita.nextInt().getClass().getName();
//    while (ita.hasNext()) System.out.print(ita.nextInt()+","); System.out.println();
//    
//    Integer[] ia1 = {1,2,3};
//    Iterable<Integer> itbl = toIterable(ia1);
//    Iterator<Integer> it = itbl.iterator();
//    while (it.hasNext()) System.out.print(it.next()+","); System.out.println();
//    
    

  }

}
