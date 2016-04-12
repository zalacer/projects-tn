package ch02.ooprogramming;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utils.RandomlySelectableArrayList;

// 10. In the RandomNumbers class, provide two static methods randomElement that
// get a random element from an array or array list of integers. (Return zero if the array
// or array list is empty.) Why couldn’t you make these methods into instance methods
// of int[] or ArrayList<Integer>?

// I wouldn't make them into instance methods of int[] or ArrayList<Integer> since I 
// don't own their sources, but it would be easy to embed an array into a custom class
// and get random elements from it or something similar for a class that extends
// ArrayList. An example of the latter is utils1.RandomlySelectableArrayList.

public class Ch0210RandomNumbers {

  public static void main(String[] args) {

    int[] a = {1,2,3,4,5};
    for (int i = 0; i < 20; i++) 
      System.out.print(randomElement(a)+", ");
    System.out.println(); //1, 3, 4, 5, 3, 5, 5, 1, 1, 1, 2, 1, 1, 2, 2, 3, 5, 5, 5, 3

    Integer[] b = {new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5)};
    for (int i = 0; i < 20; i++) 
      System.out.print(randomElement(b)+", ");  
    System.out.println(); // 4, 2, 4, 4, 1, 1, 5, 2, 2, 3, 1, 4, 1, 1, 2, 4, 3, 1, 1, 2

    List<Integer> c = new ArrayList<>();
    c.add(1); c.add(2); c.add(3); c.add(4); c.add(5); 
    for (int i = 0; i < 20; i++) 
      System.out.print(randomElement(b)+", ");  
    System.out.println(); // 2, 3, 3, 4, 3, 1, 3, 1, 2, 4, 2, 2, 5, 2, 2, 3, 1, 3, 2, 2

    RandomlySelectableArrayList<Integer> e = new RandomlySelectableArrayList<Integer>();
    e.add(1); e.add(2); e.add(3); e.add(4); e.add(5); 
    for (int i = 0; i < 20; i++) 
      System.out.print(e.randomElement()+", ");  
    System.out.println(); // 2, 1, 5, 2, 5, 5, 4, 2, 4, 3, 4, 2, 2, 1, 3, 5, 1, 3, 1, 1

  }

  private static Random generator = new Random();

  public static int nextInt(int low, int high) {
    return low + generator.nextInt(high - low + 1);
  }

  public static int randomElement(int[] a) {
    if (a.length == 0) return 0;         
    return a[nextInt(0,a.length - 1)];
  }

  public static int randomElement(Integer[] a) {
    if (a.length == 0) return 0;
    return a[nextInt(0,a.length - 1)];
  }

  public static Integer randomElement(List<Integer> a) {
    if (a.size() == 0) return 0;
    return a.get(nextInt(0,a.size() - 1));
  }

}
