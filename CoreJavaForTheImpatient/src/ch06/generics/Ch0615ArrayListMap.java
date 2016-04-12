package ch06.generics;

import java.util.ArrayList;
import java.util.function.Function;

// 15. Implement a method map that receives an array list and a Function<T, R>
// object (see Chapter 3), and that returns an array list consisting of the results of
// applying the function to the given elements.


public class Ch0615ArrayListMap {

  public static <T, R> ArrayList<R> map(ArrayList<T> a, Function<T, R> f) {
    if (a.size() == 0) return null;
    ArrayList<R> b = new ArrayList<>(a.size());
    for (T t : a) b.add(f.apply(t));   
    return b;
  }

  public static void main(String[] args) {
    
    ArrayList<Integer> ali = new ArrayList<>();
    ali.add(20); ali.add(40); ali.add(80); 
    ArrayList<Double> ald = map(ali, x -> new Double(x.doubleValue()/Math.E));
    System.out.println(ald); // [7.357588823428847, 14.715177646857693, 29.430355293715387]

  } 

}
