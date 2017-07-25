package ex22;

import static v.ArrayUtils.*;
import static java.lang.reflect.Array.newInstance;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.PriorityQueue;
import java.util.Random;


public class Ex2225MultiwayMergeSort {

  /* p287
  2.2.25 Multiway mergesort. Develop a mergesort implementation based on the idea 
  of doing k-way merges (rather than 2-way merges). Analyze your algorithm, develop 
  an hypothesis regarding the best value of k, and run experiments to validate it.
  
  In my method using a priority queue there isn't a best k as far as I know. Looking at
  the source code at http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/PriorityQueue.java/
  add() calls offer() calls siftUp() which is equivalent to swim() in the text. And poll()
  calls siftDown(), if the decremented size>0, which is equivalent to sink() in the text. So 
  they both take log time according to http://algs4.cs.princeton.edu/24pq/IndexMinPQ.java.html 
  and the inputting one element with add extracting the least element with poll each takes 
  ~lgCurrentSize time so overall it takes 2lg((N-1)!) ~ lg((N-1)*lg(N-1) ~ lg(N-1) ~ lg(N)
  time using Stirling's approximation for N! for large N where N is the cumulative size of 
  all input arrays.
   */ 

 @SafeVarargs
 public static <T extends Comparable<? super T>> T[] multiwayMergeSort(T[]...t) {
   if (t == null) return null; 
   PriorityQueue<T> p = new PriorityQueue<>();
   for (int i = 0; i < t.length; i++) 
     for (int j = 0; j < t[i].length; j++) p.add(t[i][j]);
   @SuppressWarnings("unchecked")
   T[] x = (T[])newInstance(t[0].getClass().getComponentType(), p.size());
   T q; int c = 0;
   while (true) { q = p.poll(); if (q != null) x[c++] = q; else break; }
   return x;
 }
  
  public static void main(String[] args) throws NoSuchAlgorithmException  {
    
    Integer[] a = rangeInteger(0,5); Integer[] b = rangeInteger(5,10); 
    Integer[] c = rangeInteger(10,15); 
    Random r = SecureRandom.getInstanceStrong();
    shuffle(a,r); shuffle(b,r); shuffle(c,r);
    Integer[] x = multiwayMergeSort(c,b,a);
    pa(x,-1); //[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]

    

    
  }
  
}

