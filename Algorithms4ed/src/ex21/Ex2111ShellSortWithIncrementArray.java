package ex21;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.stddev;
import static v.ArrayUtils.unbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import analysis.Timer;


/* p264
  2.1.11  Implement a version of shellsort that keeps the increment sequence in an array,
  rather than computing it.

  A version of shellsort using an increment array called shellSortWithIncrementArray is 
  included below. The increment array was generated with createIncrementArray(), included
  below, which uses pow(4,k) + 3.*pow(2,k-1) + 1) with k starting at 0 and incremented by
  one until the result reaches Integer.Max_Value. It's based on a gap sequence from
  https://en.wikipedia.org/wiki/Shellsort#Gap_sequences, which is O(N**(4/3)) but omits
  the value 3, which I included. The resulting sequence is:
    {1, 3, 8, 23, 77, 281, 1073, 4193, 16577, 65921, 262913, 1050113, 4197377, 
      16783361, 67121153, 268460033, 1073790977}
      
  I did a doubling test comparing shellSortWithIncrementArray with the "standard" shellsort, 
  that uses increments 1,  4, 13, 40, ....  The results are below and in them Sort Type 
  "ShellInc" refers to shellSortWithIncrementArray and "Shell" refers to the Standard 
  shellsort, which is the method named shellSort() below. It shows they are evenly matched
  in terms of runtime for small N but starting at N=1048576 shellSortWithIncrementArray gains 
  the advantage and keeps it, becoming about 25% faster by N=2097152. This is interesting
  since by N=8192 shellSortWithIncrementArray does substantially fewer compares and exchanges 
  than shellSort and that should have showed up in greater decreases in runtime earlier.
  
  Doubling Test Results for Comparison of shellSort and and shellSortWithIncrementArray
  ======================================================================================
  N=2
  Sort Type      run time        exchanges              compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       0           0         1             0              
  Shell       0      0       0           0         1             0              
  =====================================================================
  N=4
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       3           0         6             0              
  Shell       0      0       3           0         6             0              
  =====================================================================
  N=8
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       4           0         16            0              
  Shell       0      0       6           0         16            0              
  =====================================================================
  N=16
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       25          0         57            0              
  Shell       0      0       31          0         57            0              
  =====================================================================
  N=32
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       81          0         160           0              
  Shell       0      1       65          0         130           0              
  =====================================================================
  N=64
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      1       225         0         414           0              
  Shell       0      0       251         0         422           0              
  =====================================================================
  N=128
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      0       534         121       1008          117            
  Shell       0      1       587         5         998           13             
  =====================================================================
  N=256
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    0      1       1452        13        2521          25             
  Shell       0      0       1444        77        2434          70             
  =====================================================================
  N=512
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    1      1       3534        23        5989          20             
  Shell       1      1       3708        25        6034          20             
  =====================================================================
  N=1024
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    1      1       8631        488       13995         499            
  Shell       1      1       8988        216       14139         223            
  =====================================================================
  N=2048
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    1      0       20232       239       32213         250            
  Shell       1      1       22513       1011      34252         1015           
  =====================================================================
  N=4096
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    3      1       49988       1525      75649         1521           
  Shell       3      2       52216       2798      78406         2792           
  =====================================================================
  N=8192
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    3      1       111313      3651      167536        3666           
  Shell       4      0       133861      10506     190890        10574          
  =====================================================================
  N=16384
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    6      1       258737      6958      377968        6984           
  Shell       5      0       302835      13658     428067        13670          
  =====================================================================
  N=32768
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    14     2       580076      4576      838119        4495           
  Shell       13     2       741686      51495     1011496       51497          
  =====================================================================
  N=65536
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    32     9       1287861     4172      1831033       4075           
  Shell       29     1       1790999     23087     2372240       23226          
  =====================================================================
  N=131072
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    78     3       2831961     51281     3996056       51399          
  Shell       80     3       4122881     357396    5375831       357622         
  =====================================================================
  N=262144
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    243    9       6260132     28603     8695589       28484          
  Shell       245    18      10792830    36249     13428361      36245          
  =====================================================================
  N=524288
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    626    53      13626523    46644     18808875      47039          
  Shell       610    47      24123046    1828414   29762062      1828595        
  =====================================================================
  N=1048576
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    1381   4       30055543    83643     40848835      83136          
  Shell       1523   99      57232845    1451422   69240304      1451416        
  =====================================================================
  N=2097152
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    3130   3       64289376    473988    87117802      474010         
  Shell       4033   61      146127530   3338435   171268516     3338003        
  =====================================================================
  N=4194304
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc    7385   108     140094015   1014453   187465226     1013987        
  Shell       10377  502     327199808   17564186  380399701     17564299       
 

 */

public class Ex2111ShellSortWithIncrementArray {
  
  public static double exchanges = 0;
  public static int compares = 0;

  public static  <T extends Comparable<? super T>> double[] shellSort(T[] a) { 
    // Sort a[] into increasing order.
    exchanges = 0;
    compares = 0;
    int N = a.length;
    int h = 1;
    while (h < N/3) h = 3*h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
    while (h >= 1) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h)
          exch(a, j, j-h);
      }
      h = h/3;
    }
    return new double[]{exchanges,compares};
  }
  
  public static int[] createIncrementArray() {
    // based on https://en.wikipedia.org/wiki/Shellsort#Gap_sequences this is O(N**(4/3))
    List<Integer> list = new ArrayList<>();
    int k = 0; long r = 0;
    list.add(1);
    
    while(true) {
      r = (long) (pow(4,k) + 3.*pow(2,k-1) + 1);
      if (r >= Integer.MAX_VALUE) break;
      if (r > 0) list.add((int)r);
      k++;
    }
    
    int[] z = (int[]) unbox(list.toArray(new Integer[0]));
    v.ArrayUtils.shellSort(z);
    return z;
  }

  public static  <T extends Comparable<? super T>> double[] shellSortWithIncrementArray(T[] a) { 
    // Sort a[] into increasing order.
    exchanges = 0;
    compares = 0;
    int N = a.length;
    int k = 0;
    int h = 1;
    int[] increments = {1, 3, 8, 23, 77, 281, 1073, 4193, 16577, 65921, 262913, 1050113, 
        4197377, 16783361, 67121153, 268460033, 1073790977};
    while (h < N/3) h = increments[++k];
    while (true) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h)
          exch(a, j, j-h);
      }
      if (k == 0) {
        break;
      } else h = increments[--k];
    }
    return new double[]{exchanges,compares};
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    compares++;
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
    exchanges++;
  }

  public static Integer[][] makeReverseIntegerArrays(int n, int length) {
    // return an array of n reverse Integer arrays of length length with 
    // values from length-1 to 0
    Integer[] ia = new Integer[length];
    for (int i = ia.length-1,j=0; i >= 0; i--,j++) ia[j] = i;
    Integer[][] x = new Integer[n][];
    x[0] = ia;
    for (int i = 1; i < n; i++) x[i] = Arrays.copyOf(ia, ia.length);
    return x;
  }
 
  public static double[][] testshellSortVsShellSortWithIncrementArray(int N, int trials) { 
    Random r = null;
    List<double[]> shellData = new ArrayList<>();
    List<double[]> shellIncData = new ArrayList<>();
    List<Double[]> randomData = new ArrayList<>();
    double[] time = new double[trials]; int c = 0;
    Double[] z = new Double[N];
    double data[];
    double meanTime; double stddevTime; double[] writes; double[] compares; 
    double avgWrites; double avgCompares; double stddevWrites; double stddevCompares; 
    Timer t = new Timer();

    // shellSortWithIncrementArray testing
    for (int i = 0; i < trials; i++) {
      r = new Random(System.currentTimeMillis());
      for (int j = 0; j < z.length; j++)  z[j] = r.nextDouble();
      randomData.add(Arrays.copyOf(z, z.length));
      t.reset();
      data = shellSortWithIncrementArray(z);
      time[c++] = t.num();
      shellIncData.add(data);
    }

    // shellSortWithIncrementArray results
    meanTime = round(mean(time));
    stddevTime = round(stddev(time));
    writes = new double[trials];
    compares = new double[trials];
    c = 0;
    for (double[] a : shellIncData) {
      writes[c] = a[0];  compares[c++] = a[1];
    }
    avgWrites = round(mean(writes));
    stddevWrites = round(stddev(writes));
    avgCompares = round(mean(compares));
    stddevCompares = round(stddev(compares));
    double[] d0 = new double[]
        {meanTime,stddevTime,avgWrites,stddevWrites,avgCompares,stddevCompares};

    // shellSort testing
    c = 0;
    Iterator<Double[]> it = randomData.iterator();
    for (int i = 0; i < trials; i++) {
      Double[] a = it.next();
      t.reset();
      data = shellSort(a);
      time[c++] = t.num();
      shellData.add(data);
    }

    // shellSort results
    meanTime = round(mean(time));
    stddevTime = round(stddev(time));
    writes = new double[trials];
    compares = new double[trials];
    c = 0;
    for (double[] a : shellData) {
      writes[c] = a[0];  compares[c++] = a[1];
    }
    avgWrites = round(mean(writes));
    stddevWrites = round(stddev(writes));
    avgCompares = round(mean(compares));
    stddevCompares = (long) round(stddev(compares));
    double[] d1 = new double[]
        {meanTime,stddevTime,avgWrites,stddevWrites,avgCompares,stddevCompares};
    double[][] d = new double[2][]; d[0] = d0; d[1] = d1;
    return d;
  }

  public static void doublingTestshellSortVsShellSortWithIncrementArray(int trials) {
    int N = 2;
    while (true) {
      double[][] q = testshellSortVsShellSortWithIncrementArray(N, trials);
      System.out.println("=====================================================================");
      System.out.println("N="+N);
      System.out.println("Sort Type      run time        exchanges               compares");
      System.out.println("---------   -------------  ------------------    --------------------");
      System.out.println("            mean   stddev  mean        stddev    mean          stddev");
      System.out.printf("ShellInc    %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n", 
          q[0][0],q[0][1],q[0][2],q[0][3],q[0][4],q[0][5]);
      System.out.printf("Shell       %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n",
          q[1][0],q[1][1],q[1][2],q[1][3],q[1][4],q[1][5]); 
      N *= 2;
    }
  }

  public static void main(String[] args) {
    
    doublingTestshellSortVsShellSortWithIncrementArray(3);
 
  }
}
