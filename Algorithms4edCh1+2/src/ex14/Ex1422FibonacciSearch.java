package ex14;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static v.ArrayUtils.append;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.quickSort;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.LongStream;

import analysis.Timer;

//  p211
//  1.4.22 Binary search with only addition and subtraction. [Mihai Patrascu] Write a
//  program that, given an array of N distinct  int values in ascending order, determines
//  whether a given integer is in the array. You may use only additions and subtractions
//  and a constant amount of extra memory. The running time of your program should be
//  proportional to log N in the worst case.
//  Answer : Instead of searching based on powers of two (binary search), use Fibonacci
//  numbers (which also grows exponentially). Maintain the current search range to be the
//  interval [i, i + F(k) ] and keep F(k) and F(k–1) in two variables. At each step compute 
//  F(k–2) via subtraction, check element i + F(k–2), and update the current range to either 
//  [i, i + F(k–2)] or [i + (k–2), i + F(k–2) + F(k–1)].

// an advantage of fibonacci over binary search is that it takes about 44% fewer lookups:
// phi = 1.618033988749894848204586834 (https://en.wikipedia.org/wiki/Phi).
// log(n,2)/log(n,phi) = log(n,2)/log(n,2)*log(2,phi) = 1/log(2, phi) = 1.4404200904125564.
// fewer table lookups combined with reduction of average storage access time means fibonacci 
// search should usually run faster than binary search.

public class Ex1422FibonacciSearch {

  public static boolean fibContains(int z[], int key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }
  
  public static boolean fibContains(char z[], char key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }

  public static  <T extends Comparable<? super T>> boolean fibContains(T z[], T key) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key) != -1;
  }
  
  public static  <T> boolean fibContains(T z[], T key, Comparator<T> c) {
    // using fibIndexOf return true if z contains key else return false.
    // z must be sorted in ascending order.
    return fibIndexOf(z, key, c) != -1;
  }
  
  public static int fibIndexOf(int z[], int key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key < z[mid]) continue;
      else if (key > z[mid]) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }

  public static int fibIndexOf(char z[], char key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key < z[mid]) continue;
      else if (key > z[mid]) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }
  
  public static <T extends Comparable<? super T>> int fibIndexOf(T z[], T key) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key.compareTo(z[mid]) < 0) continue;
      else if (key.compareTo(z[mid]) > 0) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }
  
  public static <T> int fibIndexOf(T z[], T key, Comparator<T> c) {
    // return the index of key in z if z contains key else return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0; 
    int mid = 0;
    int i = 0;

    // find smallest fib index with value < n
    while(fib[i] < n) i++;

    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || c.compare(key,z[mid]) < 0) continue;
      else if (c.compare(key,z[mid]) > 0) {
        low = mid + 1;
        i--;
      }
      else return mid;
    }

    return -1;
  }
  
  // for ex2431
  public static <T extends Comparable<? super T>> int highestIndexOfEqualOrLess(T z[], T key) {
    // return the highest index i in z such that z[i].compareTo(key) <= 0,
    // else if all elements in z are greater than key return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0, mid = 0, i = 0, ret = 0;
    while(fib[i] < n) i++;
    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || key.compareTo(z[mid])<0) continue;
      else if (key.compareTo(z[mid])>0) { ret = mid; low = mid+1; i--; }
      else {
        while(mid < n-1 && z[mid].compareTo(z[mid+1])==0) mid++;
        return mid;
      }
    }
    if (ret == 0) { return z[0].compareTo(key)< 0 ? 0 : -1; }
    else return ret;
  }
  
  // for ex2431
  public static <T> int highestIndexOfEqualOrLess(T z[], T key, Comparator<T> c) {
    // return the highest index i in z such that c.compare(z[i],key) <= 0,
    // else if all elements in z are greater than key return -1.
    // z must be sorted in ascending order. uses fibonacci search.
    if (z == null || z.length == 0) return -1;
    int n = z.length;
    if (n == 1) return z[0] == key ? 0 : -1;
    int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
        4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
        832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
        39088169,63245986,102334155,165580141};
    int low = 0, mid = 0, i = 0, ret = 0;
    while(fib[i] < n) i++;
    while(i > 0) {
      mid = low + fib[--i];
      if (mid >= n || c.compare(key,z[mid])<0) continue;
      else if (c.compare(key,z[mid])>0) { ret = mid; low = mid+1; i--; }
      else {
        while(mid < n-1 && c.compare(z[mid],z[mid+1])==0) mid++;
        return mid;
      }
    }
    if (ret == 0) { return c.compare(z[0],key)< 0 ? 0 : -1; }
    else return ret;
  }

  public static long fibonacci(int n) {
    // iterative fibonacci generator - fast and no risk of stack overflow.
    // long overflow occurs after the first 93 fibonacci numbers.
    if (n < 0) {
      return -1;
    } else if (n <= 1) {
      return n;
    } else if (n == 2) {
      return 1;
    } else {
      long m1 = 0; long m2 = 1;
      long t;
      for (int i = 0; i < n; i++) {
        t = m1; m1+=m2; m2 = t;
      }
      return m1;
    }
  }

  public static BigInteger fibonaccib(int n) {
    // iterative fibonacci generator - fast and no risk of stack overflow.
    // long overflow occurs after the first 93 fibonacci numbers.
    if (n < 0) {
      return ONE.negate();
    } else if (n == 0) {
      return ZERO;
    } else if (n == 1) {
      return ONE;
    } else if (n == 2) {
      return ONE;
    } else {
      BigInteger m1 = ZERO; BigInteger m2 = ONE;
      BigInteger t;
      for (int i = 0; i < n; i++) {
        t = m1; m1 = m1.add(m2); m2 = t;
      }
      return m1;
    }
  }

  public static long fibonaccif(int n) {
    // fast table-based fibonacci number generator.
    // long overflow occurs after the first 93 fibonacci numbers.
    // fibonacci numbers can be extended below zero using  F(-n) = ((−1)**n+1)*Fn.
    // see http://dictionary.sensagent.com/negafibonacci/en-en/
    // and to https://en.wikipedia.org/wiki/Fibonacci_number#Negafibonacci
    long[] fibs93 = {
        0L,1L,1L,2L,3L,5L,8L,13L,21L,34L,55L,89L,144L,233L,377L,610L,987L,1597L,2584L,4181L,
        6765L,10946L,17711L,28657L,46368L,75025L,121393L,196418L,317811L,514229L,832040L,
        1346269L,2178309L,3524578L,5702887L,9227465L,14930352L,24157817L,39088169L,63245986L,
        102334155L,165580141L,267914296L,433494437L,701408733L,1134903170L,1836311903L,
        2971215073L,4807526976L,7778742049L,12586269025L,20365011074L,32951280099L,53316291173L,
        86267571272L,139583862445L,225851433717L,365435296162L,591286729879L,956722026041L,
        1548008755920L,2504730781961L,4052739537881L,6557470319842L,10610209857723L,
        17167680177565L,27777890035288L,44945570212853L,72723460248141L,117669030460994L,
        190392490709135L,308061521170129L,498454011879264L,806515533049393L,1304969544928657L,
        2111485077978050L,3416454622906707L,5527939700884757L,8944394323791464L,
        14472334024676221L,23416728348467685L,37889062373143906L,61305790721611591L,
        99194853094755497L,160500643816367088L,259695496911122585L,420196140727489673L,
        679891637638612258L,1100087778366101931L,1779979416004714189L,2880067194370816120L,
        4660046610375530309L,7540113804746346429L};
//    if (n < 0) {
//      System.err.println("fibonaccif: cannot go lower than 0 without array underflow."
//          + "\nreturning -1");
//      return -1;
//    }
    if (n >= 0 && n <= 93) {  
      return fibs93[n];
    } else if (n < 0 && n >= -93) {
      return -n % 2 == 0 ? -fibs93[-n] : fibs93[-n];
    } else {
      System.err.println("fibonaccif: cannot go higher than 93 or lower than -93 "
          + " without long overflow.\nreturning -1");
      return -1;
    }  
  }
  
  //http://introcs.cs.princeton.edu/java/23recursion/Fibonacci.java.html
  public static long fibonaccir(int n) {
    // recursive fibonacci number generator.
    // long overflow occurs after the first 93 fibonacci numbers.
    if (n < 0) {
      return -1;
    } else if (n <= 1) {
      return n;
    } else {
      return fibonaccir(n-1) + fibonaccir(n-2);
    }
  }

  public static class FibonacciIterator implements PrimitiveIterator.OfLong {
    // this iterator cuts off after providing the first 93 fibonacci numbers
    // since beyond that long overflow occurs.
    private long m1 = 1; private long m2 = 1; private long t = 0; int c = 0;
    @Override
    public boolean hasNext() {
      return c < 93 ? true : false;
    }
    @Override
    public long nextLong() {
      if (!hasNext()) throw new NoSuchElementException();
      if (c == 0) { 
        c++; return 0; 
      } else if (c == 1) {
        c++; return 1; 
      } else if (c == 2) {
        c++; return 1; 
      } else {
        t = m1; m1+=m2; m2 = t; c++;
        return m1;
      }
    }
  }

  public static LongStream fibStream() {
    // due to the iterator on which it's based this stream is
    // is limited to 93 elements.
    PrimitiveIterator.OfLong fibit = new FibonacciIterator();
    return LongStream.generate(()->fibit.nextLong()).limit(93);
  }

  public static LongStream fibStreamUnsafe() {
    // due to the iterator on which it's based this stream throws
    // NoSuchElementException after the 93rd element.
    PrimitiveIterator.OfLong fibit = new FibonacciIterator();
    return LongStream.generate(()->fibit.nextLong());
  }
  
  public static int binIndexOf(int[] a, int key) { 
    // binary search.
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) { 
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  

  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    System.out.println(fibonaccif(5)); //5
    System.out.println(fibonaccif(-5)); //5
    System.out.println(fibonaccif(8)); //21
    System.out.println(fibonaccif(-8)+"\n"); //-21

    for (int i = 0; i <= 20; i++) System.out.print(fibonaccir(i)+","); System.out.println();
    //0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765 //21 elements
    System.out.println(fibonaccir(20)); //6765
    System.out.println(fibonacci(20));  //6765
    System.out.println(fibonaccif(20)+"\n"); //6765

    long[] fiba = fibStream().toArray();
    pa(fiba,10000,1,1);
    //  [0,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765,10946,17711,28657,
    //   46368,75025,121393,196418,317811,514229,832040,1346269,2178309,3524578,5702887,
    //   9227465,14930352,24157817,39088169,63245986,102334155,165580141,267914296,433494437,
    //   701408733,1134903170,1836311903,2971215073,4807526976,7778742049,12586269025,
    //   20365011074,32951280099,53316291173,86267571272,139583862445,225851433717,365435296162,
    //   591286729879,956722026041,1548008755920,2504730781961,4052739537881,6557470319842,
    //   10610209857723,17167680177565,27777890035288,44945570212853,72723460248141,
    //   117669030460994,190392490709135,308061521170129,498454011879264,806515533049393,
    //   1304969544928657,2111485077978050,3416454622906707,5527939700884757,8944394323791464,
    //   14472334024676221,23416728348467685,37889062373143906,61305790721611591,
    //   99194853094755497,160500643816367088,259695496911122585,420196140727489673,
    //   679891637638612258,1100087778366101931,1779979416004714189,2880067194370816120,
    //   4660046610375530309,7540113804746346429]
    System.out.println();

    int[] a = new int[]{-1,2,3,4,5,6,7};
    System.out.println(fibIndexOf(a,-1)); //0
    for (int i : a) System.out.print(fibIndexOf(a,i)+" "); System.out.println();
    //0 1 2 3 4 5 6 
    for (int i : a) System.out.print(fibContains(a,i)+" "); System.out.println("\n");
    //true true true true true true true 
    
    Random rnd = SecureRandom.getInstanceStrong(); // gives different results each time
    a = rnd.ints(20, 1, 8).toArray();
    quickSort(a);
    pa(a); 
    System.out.println(fibIndexOf(a,5));
    System.out.println(fibContains(a,5)+"\n");

    char[] ca = {'a','a','b','b','c','d','d','d','d','e','e','e','e','e','e','f','f','g'};
    System.out.println(fibIndexOf(ca,'c'));
    System.out.println(fibContains(ca,'c')+"\n");
    
    Integer[] ia = {1,1,2,2,2,2,3,3,3,3,3,3,4,4,5,6,7,7,7,7};
    System.out.println(fibIndexOf(ia,4));
    System.out.println(fibContains(ia,5));
    
    rnd = new Random(776531479L);
    int[] x = rnd.ints(5500000,-1000,-2).toArray();
    int[] y = rnd.ints(15500000,2,1000).toArray();
    y[0] = 1;
    int[] z = append(x,y);
    Arrays.sort(z);
    Timer t = new Timer();
    int ifib = fibIndexOf(z,1);
    t.stop(); // 0 ms
    System.out.println("ifib="+ifib); //ifib=5500000
    t.reset();
    int ibin = binIndexOf(z,1);
    t.stop(); // 0 ms
    System.out.println("ibin="+ibin); //ibin=5500000
    
    
    
    
  }

}
