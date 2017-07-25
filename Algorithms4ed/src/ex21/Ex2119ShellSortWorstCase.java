package ex21;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import sort.Shell;

/* p265
  2.1.19 Shellsort worst case. Construct an array of 100 elements containing the num-
  bers 1 through 100 for which shellsort, with the increments  1 4 13 40 , uses as large a
  number of compares as you can find.
  
  Out of six semi-manually constructed tests I found that shell sort did the greatest number 
  of comparisons when sorting an array with all elements in odd index positions greater than 
  the median and with even and odd positions independently reverse sorted. In this case the 
  number of comparisons was 714 and it also had the greatest number of exchanges which was 445.
  The combined total is 1159 and the worst case time complexity for the increments used is 
  O(N**3/2) which is 1000 for N == 100.
  
  Doing random tests after I few minutes I got 
    {56,62,85,82,31,33,6,11,95,20,97,42,3,26,58,83,66,29,51,8,45,80,96,24,16,
     36,88,4,74,1,67,13,34,22,81,71,17,41,37,77,93,35,55,39,40,60,21,12,68,25,
     44,32,47,57,72,86,28,9,38,65,48,2,91,14,78,92,90,49,52,76,100,64,19,5,69,
     79,18,50,98,94,54,61,70,87,46,7,99,63,84,15,10,75,30,59,73,89,53,23,27,43}
  which causes shell sort to do 932 comparisons and 625 exchanges for a total of 1557.
  
 */

public class Ex2119ShellSortWorstCase {

  @SuppressWarnings("unused")
  public static void main(String[] args) {

    // instrumented Shell.sortMod() to return an int array [compares, writes] where
    // compares is the number of less() executions and writes is the number of exch() executions

    System.out.println("worst case time complexity for intervals 1 4 13 40... according to");
    System.out.println("https://en.wikipedia.org/wiki/Shellsort#Gap_sequences is O(N**1.5");
    System.out.println("           (including comparisons and exchanges)");
    System.out.println("Math.pow(100, 1.5)="+Math.pow(100, 1.5)); // 1000.0
    System.out.println();

    System.out.println("1. sorted array with range [1,101)");
    Integer[] a1 = (Integer[]) box(range(1,101)); // sorted [1,101)
    int[] s1 = Shell.sortMod(a1);
    System.out.println("comparisons="+s1[0]); //342
    System.out.println("  exchanges="+s1[1]); //0
    System.out.println("      total="+sum(s1)); //342

    System.out.println("\n2. reverse sorted array with range [100,0)");
    Integer[] a2 = (Integer[]) box(range(100,0)); // reverse [100,1)
    int[] s2 = Shell.sortMod(a2);
    System.out.println("comparisons="+s2[0]); //500
    System.out.println("  exchanges="+s2[1]); //230
    System.out.println("      total="+sum(s2)); //730

    System.out.println("\n3. array with all elements in even index positions greater than median");
    System.out.println("with even and odd positions independently reverse sorted");
    int[] c = range(100,50);
    int[] d = range(50,0);
    int[] g = new int[100];
    int q = 0;
    for (int i = 0; i < 50; i++) {
      g[q++] = c[i]; g[q++] = d[i]; 
    }
    System.out.println("g.length="+g.length); //100
    System.out.println("median(g)="+median(g)); //50.5
    Integer[] a3 = (Integer[]) box(g);
    int[] s3 = Shell.sortMod(a3);
    System.out.println("comparisons="+s3[0]); //703
    System.out.println("  exchanges="+s3[1]); //434
    System.out.println("      total="+sum(s3)); //1138

    System.out.println("\n4. array with all elements in odd index positions greater than median");
    System.out.println("with even and odd positions independently reverse sorted");
    g = new int[100];
    q = 0;
    for (int i = 0; i < 50; i++) {
      g[q++] = d[i]; g[q++] = c[i]; 
    }
    System.out.println("g.length="+g.length); //100
    System.out.println("median(g)="+median(g)); //50.5
    Integer[] a4 = (Integer[]) box(g);
    int[] s4 = Shell.sortMod(a4);
    System.out.println("comparisons="+s4[0]); //714
    System.out.println("  exchanges="+s4[1]); //445
    System.out.println("      total="+sum(s4)); //1159
    
    System.out.println("\n5. array with all elements in odd index positions greater than median");
    System.out.println("with even and odd positions independently sorted");
    d = range(1,51);
    c = range(51,101);
    g = new int[100];
    q = 0;
    for (int i = 0; i < 50; i++) {
      g[q++] = d[i]; g[q++] = c[i]; 
    }
    System.out.println("g.length="+g.length); //100
    System.out.println("median(g)="+median(g)); //50.5
    Integer[] a5 = (Integer[]) box(g);
    int[] s5 = Shell.sortMod(a5);
    System.out.println("comparisons="+s5[0]); //595
    System.out.println("  exchanges="+s5[1]); //259
    System.out.println("      total="+sum(s5)); //854
    
    System.out.println("\n6. array with all elements in even index positions greater than median");
    System.out.println("with even and odd positions independently sorted");
    g = new int[100];
    q = 0;
    for (int i = 0; i < 50; i++) {
      g[q++] = c[i]; g[q++] = d[i]; 
    }
    System.out.println("g.length="+g.length); //100
    System.out.println("median(g)="+median(g)); //50.5
    Integer[] a6 = (Integer[]) box(g);
    int[] s6 = Shell.sortMod(a6);
    System.out.println("comparisons="+s6[0]); //599
    System.out.println("  exchanges="+s6[1]); //265
    System.out.println("      total="+sum(s6)); //864
    
    // random test results
 
    //maxcomps=900
    Integer[] r1 = {13,21,22,88,64,36,35,6,43,77,3,98,58,12,49,91,71,61,42,89,76,27,55,75,
        85,10,83,47,33,68,52,56,82,24,73,100,51,20,67,31,18,60,86,8,72,81,2,15,57,78,41,19,
        59,11,14,79,17,62,93,34,53,23,99,44,29,26,74,38,7,25,87,39,69,65,84,94,1,97,96,28,
        54,80,66,45,92,50,48,90,95,46,70,9,30,37,4,5,16,32,63,40};
    
    //maxcomps=887 exchanges=587
    Integer[] r2 = {24,29,3,39,46,68,2,36,60,77,99,33,74,71,22,14,49,52,51,27,6,44,19,90,25,
        53,95,100,28,55,32,50,54,59,57,34,69,26,92,65,79,96,17,62,97,8,58,12,78,41,93,73,72,
        66,7,56,61,85,43,35,91,31,42,9,98,84,94,81,21,70,23,64,5,15,75,4,89,48,63,18,47,82,
        40,11,76,20,16,38,88,45,80,67,87,86,1,30,37,83,10,13};
    
    //maxcomps=889 exchanges=599
    Integer[] r3 = {4,78,87,34,73,30,96,59,57,29,62,35,3,94,32,45,77,8,44,63,93,97,37,7,52,33,
        85,40,100,50,42,89,66,84,61,4,90,13,53,88,5,36,38,28,18,6,10,98,56,65,26,95,2,11,86,
        68,46,31,24,27,76,67,23,54,17,20,80,39,60,72,51,58,43,49,92,41,21,75,9,70,22,15,74,
        71,64,91,55,83,69,12,81,82,99,79,47,19,48,16,1,25};
    
    //maxcomps=908 exchanges=612
    Integer[] r4 = {67,61,49,70,95,94,87,23,39,2,86,78,34,6,33,77,84,68,12,93,71,37,13,42,90,
        45,1,31,89,92,81,22,38,75,10,47,3,14,48,7,9,35,55,76,99,18,66,29,80,17,11,64,56,85,
        32,21,26,96,63,98,88,40,25,16,52,54,19,36,58,57,73,8,82,69,24,44,28,30,62,5,51,50,53,
        60,20,100,83,46,41,15,59,91,72,27,43,65,97,79,74,4};
    
    //maxcomps=911 exchanges=611
    Integer[] r5 = {94,13,48,56,74,29,73,8,38,72,97,36,100,58,66,39,99,57,4,92,25,63,61,46,32,
        60,64,17,28,14,70,41,35,3,90,96,79,87,50,44,95,81,20,89,84,40,51,76,34,93,86,88,62,83,
        71,65,21,5,7,19,33,37,85,91,52,49,80,31,10,45,75,43,9,22,6,54,26,77,30,1,78,42,47,12,
        15,27,11,59,98,18,53,24,16,69,68,82,2,55,67,23};
    
    //maxcomps=932 exchanges=625
    Integer[] r6 = {56,62,85,82,31,33,6,11,95,20,97,42,3,26,58,83,66,29,51,8,45,80,96,24,16,
        36,88,4,74,1,67,13,34,22,81,71,17,41,37,77,93,35,55,39,40,60,21,12,68,25,44,32,47,57,
        72,86,28,9,38,65,48,2,91,14,78,92,90,49,52,76,100,64,19,5,69,79,18,50,98,94,54,61,70,
        87,46,7,99,63,84,15,10,75,30,59,73,89,53,23,27,43};
    
    int[] sr6 = Shell.sortMod(r6);
    pa(sr6,1000,1,1); //[932,625]
    
    // Random test
    
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("cannot instantiate Random");
    }
    
    Set<String> set = new HashSet<>();
    Integer[] ar = (Integer[]) box(range(1,101));
    int[] pr;
    int maxcomps = 714;
    Integer[] ar2; 
    String s;

    while (true) {
      shuffle(ar, r);
      s = arrayToString(ar,500,1,1);
      if (set.contains(s)) {
        continue;
      } else set.add(s);
      ar2 = Arrays.copyOf(ar, ar.length);
      pr = Shell.sortMod(ar);
      if (pr[0] > maxcomps) {
        maxcomps = pr[0];
        System.out.println("maxcomps="+maxcomps+" exchanges="+pr[1]);
        pa(ar2,1000,1,1);
      }
    }


  }

}
