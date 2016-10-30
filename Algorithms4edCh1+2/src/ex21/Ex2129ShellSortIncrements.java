package ex21;

import static v.ArrayUtils.*;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import analysis.Timer;


/* p267
  2.1.29 Shellsort increments. Run experiments to compare the increment sequence in
  Algorithm 2.3 with the sequence 1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929,
  16001, 36289, 64769, 146305, 260609 (which is formed by merging together the se-
  quences 9*4**k - 9*2**k + 1 and 4**k - 3*2**k + 1). See Exercise 2.1.11.

  A version of shellsort using the given increment sequence expanded to 28 elements is 
  included below and is named shellSortWithIncrementArray2(). It starts with the first
  increment value > N/3. Although it could have started with the first increment smaller
  than N, it's not clear that it matters and if it did that could have been done in
  Algorithm 2.3, but it wasn't. The version of shellsort using the increment sequence in 
  Algorithm 2.3 is shellSort() and is also included below along with less() and exch() 
  which are used by both sorts are are instrumented to increment the int counters compares 
  and exchanges respectively. Both sort methods were instrumented to count compares and 
  exchanges during each execution and return an int[] array with the counts. 
  
  Running a doubling test with them from N=2 to N=8388608 shows they give the same runtime 
  performance up tp N=131072, however at N=256 shellSortWithIncrementArray2 made fewer 
  comparisons and exchanges than shellSort which became more pronounced with increasing 
  N from that point until by N=4194304 shellSortWithIncrementArray2's runtime was close to 
  38% smaller than shellSort's. However, it may have levelled of at that point, since at 
  N=2*4194304=8388608, shellSortWithIncrementArray2's runtime was under 37% of shellSort's. 
  The test was stopped at N=8388608 and all the results are given below. In those results 
  the Sort Type labeled "Shell" refers to shellSort and that labeled "ShellInc2" refers to 
  shellSortWithIncrementArray2 .

  Doubling Test Results for Comparison of shellSort and and shellSortWithIncrementArray2
  ======================================================================================
  N=2
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      1       1           1         1             0              
  Shell       0      0       1           1         1             0              
  =====================================================================
  N=4
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      0       0           1         3             1              
  Shell       0      0       0           1         3             1              
  =====================================================================
  N=8
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      0       5           0         14            0              
  Shell       0      0       5           0         15            0              
  =====================================================================
  N=16
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      0       31          0         51            0              
  Shell       0      0       27          0         52            0              
  =====================================================================
  N=32
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      0       72          0         133           0              
  Shell       0      0       86          0         150           0              
  =====================================================================
  N=64
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      1       262         8         425           10             
  Shell       0      0       231         7         404           9              
  =====================================================================
  N=128
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      0       742         0         1146          0              
  Shell       0      0       602         0         1005          0              
  =====================================================================
  N=256
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   0      1       1502        0         2489          0              
  Shell       0      0       1532        0         2510          0              
  =====================================================================
  N=512
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   1      0       3407        34        5846          29             
  Shell       0      1       3774        154       6101          151            
  =====================================================================
  N=1024
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   1      0       8156        190       13915         174            
  Shell       1      0       9180        187       14338         194            
  =====================================================================
  N=2048
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   1      1       18064       150       31586         173            
  Shell       1      1       21202       1545      32952         1538           
  =====================================================================
  N=4096
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   3      1       40154       279       70879         254            
  Shell       2      0       56056       3852      82257         3852           
  =====================================================================
  N=8192
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   3      1       87563       455       157224        513            
  Shell       3      1       128789      9489      185816        9452           
  =====================================================================
  N=16384
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   6      1       190074      1126      344729        1092           
  Shell       5      1       301582      16426     426783        16427          
  =====================================================================
  N=32768
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   16     3       412120      2453      754580        2448           
  Shell       13     2       722505      14853     992301        14745          
  =====================================================================
  N=65536
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   31     4       891444      4053      1639569       4041           
  Shell       31     1       1778463     86154     2359452       86068          
  =====================================================================
  N=131072
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   94     5       1911376     5367      3539843       5207           
  Shell       87     7       4134924     185093    5388110       184965         
  =====================================================================
  N=262144
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   314    65      4071614     10210     7585439       10225          
  Shell       325    33      9681110     460186    12316758      460215         
  =====================================================================
  N=524288
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   585    8       8647677     11072     16203340      11401          
  Shell       601    34      23788291    1273432   29427394      1273420        
  =====================================================================
  N=1048576
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   1345   8       18344086    9272      34488644      9311           
  Shell       1517   19      57539076    6056032   69546518      6055618        
  =====================================================================
  N=2097152
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   3084   40      38908629    78654     73313181      78673          
  Shell       3902   47      134035120   772034    159175809     771814         
  =====================================================================
  N=4194304
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   7167   62      82365476    147730    155316856     147282         
  Shell       11537  796     330342538   12909127  383541096     12909084       
  =====================================================================
  N=8388608
  Sort Type      run time        exchanges               compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  ShellInc2   16631  255     173841774   928974    328209075     928693         
  Shell       26350  616     766517981   14623811  878130928     14623124       
 */

public class Ex2129ShellSortIncrements {

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

  // excercise 2.1.11 p264
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

  public static int[] createIncrementArray2() {
    //merge sequences formed from 9*4**k - 9*2**k + 1 and 4**k - 3*2**k + 1
    List<Integer> list = new ArrayList<>();
    int k = 0; long r = 0;
    list.add(1);

    while(true) {
      r = (long) (9*pow(4,k) - 9*pow(2,k) + 1);
      if (r >= Integer.MAX_VALUE) break;
      if (r > 0) list.add((int)r);
      k++;
    }

    k = 0;
    while(true) {//4**k - 3*2**k + 1
      r = (long) (pow(4,k) - 3*pow(2,k) + 1);
      if (r >= Integer.MAX_VALUE) break;
      if (r > 0) list.add((int)r);
      k++;
    }

    int[] z = unique((int[]) unbox(list.toArray(new Integer[0])));
    v.ArrayUtils.shellSort(z);
    return z;
  }

  // excercise 2.1.11 p264 
  public static  <T extends Comparable<? super T>> double[] shellSortWithIncrementArray1(T[] a) { 
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

  public static  <T extends Comparable<? super T>> double[] shellSortWithIncrementArray2(T[] a) { 
    // Sort a[] into increasing order.
    exchanges = 0;
    compares = 0;
    int N = a.length;
    int k = 0;
    int h = 1;
    int[] increments = {1,5,19,41,109,209,505,929,2161,3905,8929,16001,36289,64769,146305,
        260609,587521,1045505,2354689,4188161,9427969,16764929,37730305,67084289,150958081,
        268386305,603906049,1073643521};
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

  public static double[][] testshellSortVsShellSortWithIncrementArray2(int N, int trials) { 
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

    // shellSortWithIncrementArray2 testing
    for (int i = 0; i < trials; i++) {
      r = new Random(System.currentTimeMillis());
      for (int j = 0; j < z.length; j++)  z[j] = r.nextDouble();
      randomData.add(Arrays.copyOf(z, z.length));
      t.reset();
      data = shellSortWithIncrementArray2(z);
      time[c++] = t.num();
      shellIncData.add(data);
    }

    // shellSortWithIncrementArray2 results
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

  public static void doublingTestshellSortVsShellSortWithIncrementArray2(int trials) {
    int N = 2;
    while (true) {
      double[][] q = testshellSortVsShellSortWithIncrementArray2(N, trials);
      System.out.println("=====================================================================");
      System.out.println("N="+N);
      System.out.println("Sort Type      run time        exchanges               compares");
      System.out.println("---------   -------------  ------------------    --------------------");
      System.out.println("            mean   stddev  mean        stddev    mean          stddev");
      System.out.printf("ShellInc2   %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n", 
          q[0][0],q[0][1],q[0][2],q[0][3],q[0][4],q[0][5]);
      System.out.printf("Shell       %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n",
          q[1][0],q[1][1],q[1][2],q[1][3],q[1][4],q[1][5]); 
      N *= 2;
    }
  }

  public static void main(String[] args) {

      doublingTestshellSortVsShellSortWithIncrementArray2(3);


//    int[] increments = createIncrementArray2();
//    System.out.println(increments.length);
//    pa(increments,1000,1,1);
//    [1,5,19,41,109,209,505,929,2161,3905,8929,16001,36289,64769,146305,260609,587521,1045505,2354689,4188161,9427969,16764929,37730305,67084289,150958081,268386305,603906049,1073643521]
//    baseline
//    {1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929, 16001, 36289, 64769, 146305, 260609};



  }
}
