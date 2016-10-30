package ex21;

import static v.ArrayUtils.*;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import sort.Selection;
import sort.Insertion;
import analysis.Timer;


/* p267
  2.1.28 Equal keys. Formulate and validate hypotheses about the running time of 
  insertion sort and selection sort for arrays that contain just two key values, 
  assuming that the values are equally likely to occur.
  
  My hypotheses is that for an array of length N substitution sort always does N
  writes (exchanges or insertions), but its insertions grow exponentially, while
  for insertion sort its writes and insertions stay about even and both grow
  exponentially. In order words, propositions A on p248 and B on p250 are correct 
  for the case of arrays containing just two key values that are equally likely to
  occur.
  
  To validate this I constructed a comparison doubling test and ran it for N = 2
  to 2**18.  The output is shown below. Some interesting correlations are that as
  N increases the mean compares (or writes) for selection at a given power of 2 
  becomes close to that of insertion at the next higher power of 2 and both 
  asymptotically quadruple with every increased power of two. Another way of saying
  that is for a fixed N even as low as 2**5 the sum of insertion's writes plus 
  compares (or 2 times its compares or writes) is about half of selection's compares, 
  since for selection writes adds relatively little overhead. That discrepancy shows
  insertion's relative efficiency compared to selection -- overall it's about 50% 
  more efficient. Starting at about 2**11 insertion's run time is 1/3 that of 
  substitution's and that ratio approximately holds through 2**18. Note that in this 
  test for each trial a new random generator with a new seed was instantiated to 
  create a new Double array for testing Insertion first, but a copy of each array was 
  saved to a list before sorting. Then the saved arrays were used for sorting by 
  Selection so that both used arrays with exactly the same data and in the same order 
  for sorting.
  
  Doubling Test Results for Comparison of Insertion Sort and Substitution Sort
  ============================================================================
  N=2
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   28     49      0           1         1             0              
  Selection   0      1       2           0         1             0              
  =====================================================================
  N=4
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      0       2           0         4             0              
  Selection   0      1       4           0         6             0              
  =====================================================================
  N=8
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      0       6           0         12            0              
  Selection   0      0       8           0         28            0              
  =====================================================================
  N=16
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      0       51          0         65            0              
  Selection   0      0       16          0         120           0              
  =====================================================================
  N=32
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      1       136         40        166           40             
  Selection   0      0       32          0         496           0              
  =====================================================================
  N=64
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      0       574         84        636           84             
  Selection   0      1       64          0         2016          0              
  =====================================================================
  N=128
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   0      1       2027        69        2153          69             
  Selection   1      1       128         0         8128          0              
  =====================================================================
  N=256
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   1      0       7794        156       8048          156            
  Selection   1      1       256         0         32640         0              
  =====================================================================
  N=512
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   2      1       32663       1587      33173         1587           
  Selection   5      1       512         0         130816        0              
  =====================================================================
  N=1024
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   2      2       129521      3605      130543        3605           
  Selection   11     1       1024        0         523776        0              
  =====================================================================
  N=2048
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   5      1       525901      8053      527947        8053           
  Selection   24     14      2048        0         2096128       0              
  =====================================================================
  N=4096
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   17     1       2092898     46693     2096992       46693          
  Selection   62     1       4096        0         8386560       0              
  =====================================================================
  N=8192
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   67     1       8375493     46459     8383684       46459          
  Selection   259    2       8192        0         33550336      0              
  =====================================================================
  N=16384
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   267    2       33272496    232289    33288879      232289         
  Selection   1097   41      16384       0         134209536     0              
  =====================================================================
  N=32768
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   1074   9       133911445   865058    133944212     865058         
  Selection   4245   12      32768       0         536854528     0              
  =====================================================================
  N=65536
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   4319   17      537005609   1915242   537071144     1915241        
  Selection   17167  31      65536       0         2147450880    0              
  =====================================================================
  N=131072
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   17873  67      2139927046  5445446   2140058117    5445446        
  Selection   72670  78      131072      0         -65536        0              
  =====================================================================
  N=262144
  Sort Type      run time          writes                compares
  ---------   -------------  ------------------    --------------------
              mean   stddev  mean        stddev    mean          stddev
  Insertion   77429  174     8608664222  30907416  8608926364    30907415       
  Selection   303142  1636    262144      0        -131072       0              

 PS: I can't explain negative means for Selection compares in the last two trials 
 (for N=131072 and N=262144) since I'm using doubles or Doubles all the way through.
 Oddly 65536 == 2**16 and 131072 is twice that. Here is the mean() method used:
 
   public static double mean(double[] a) {
    if (a == null)
      throw new IllegalArgumentException("mean: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("mean: array must have at least one element");
    return sum(a) * 1. / a.length;
   }
   
 And here is the sum() method it uses:
 
   public static double sum(double[] a) {
    // return the sum of all the elements of a
    if (a == null)
      throw new IllegalArgumentException("sum: array can't be null");
    if (a.length == 0)
      throw new IllegalArgumentException("sum: array must have at least one element");
    double sum = 0;
    for (int i = 0; i < a.length; i++)
      sum += a[i];
    return sum;
   }
 
 */

public class Ex2128SelectionAndInsertionWithTwoKeyValues {
  
  public static double[][] testSelectionVsInsertion(int N, int trials) { 
    Random r = null;
    List<double[]> selectionData = new ArrayList<>();
    List<double[]> insertionData = new ArrayList<>();
    List<Double[]> randomData = new ArrayList<>();
    double[] time = new double[trials]; int c = 0;
    Double[] z = new Double[N];
    double data[];
    double meanTime; double stddevTime; double[] writes; double[] compares; 
    double avgWrites; double avgCompares; double stddevWrites; double stddevCompares; 
    Timer t = new Timer();
    
    // Insertion testing
    for (int i = 0; i < trials; i++) {
      r = new Random(System.currentTimeMillis());
      for (int j = 0; j < z.length; j++)  z[j] = 1.*r.nextInt(2);
      randomData.add(Arrays.copyOf(z, z.length));
      t.reset();
      data = Insertion.insertionTestHypo2128(z);
      time[c++] = t.num();
      insertionData.add(data);
    }
    
    //Insertion results
    meanTime = round(mean(time));
    stddevTime = round(stddev(time));
    writes = new double[trials];
    compares = new double[trials];
    c = 0;
    for (double[] a : insertionData) {
      writes[c] = a[0];  compares[c++] = a[1];
    }
    avgWrites = round(mean(writes));
    stddevWrites = round(stddev(writes));
    avgCompares = round(mean(compares));
    stddevCompares = round(stddev(compares));
    double[] d0 = new double[]{meanTime,stddevTime,avgWrites,stddevWrites,avgCompares,stddevCompares};

    // Selection testing
    c = 0;
    Iterator<Double[]> it = randomData.iterator();
    for (int i = 0; i < trials; i++) {
        Double[] a = it.next();
        t.reset();
        data = Selection.selectionTestHypo2128(a);
        time[c++] = t.num();
        selectionData.add(data);
    }
  
    // Selection results
    meanTime = round(mean(time));
    stddevTime = round(stddev(time));
    writes = new double[trials];
    compares = new double[trials];
    c = 0;
    for (double[] a : selectionData) {
      writes[c] = a[0];  compares[c++] = a[1];
    }
    avgWrites = round(mean(writes));
    stddevWrites = round(stddev(writes));
    avgCompares = round(mean(compares));
    stddevCompares = (long) round(stddev(compares));
    double[] d1 = new double[]{meanTime,stddevTime,avgWrites,stddevWrites,avgCompares,stddevCompares};
    
    double[][] d = new double[2][]; d[0] = d0; d[1] = d1;
    return d;
  }
  
  public static void doublingTestSelectionVsInsertion(int trials) {
    int N = 2;
    while (true) {
      double[][] q = testSelectionVsInsertion(N, trials);
      System.out.println("=====================================================================");
      System.out.println("N="+N);
      System.out.println("Sort Type      run time          writes                compares");
      System.out.println("---------   -------------  ------------------    --------------------");
      System.out.println("            mean   stddev  mean        stddev    mean          stddev");
       System.out.printf("Insertion   %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n", 
           q[0][0],q[0][1],q[0][2],q[0][3],q[0][4],q[0][5]);
       System.out.printf("Selection   %-5.0f  %-5.0f   %-10.0f  %-7.0f   %-14.0f%-15.0f\n",
           q[1][0],q[1][1],q[1][2],q[1][3],q[1][4],q[1][5]); 
       N *= 2;
    }
  }

  public static void main(String[] args) {
    
    doublingTestSelectionVsInsertion(3);
    
    // below is a development version of testSelectionVsInsertion() as an inline client
      
//    Random r = null;
//    List<int[]> selectionData = new ArrayList<>();
//    List<int[]> insertionData = new ArrayList<>();
//    List<Integer[]> randomData = new ArrayList<>();
//    int trials = 2;
//    long[] time = new long[trials]; int c = 0;
//    int N = 10000;
//    Integer[] z = new Integer[N];
////    for (int i = 0; i < z.length; i++) z[i] = r.nextInt(2);
////    Integer[] y = Arrays.copyOf(z, z.length);
//    int data[];
//    double meanTime; double stddevTime; int[] writes; int[] compares; 
//    double avgWrites; double avgCompares; double stddevWrites; double stddevCompares; 
//    Timer t = new Timer();
//    
//    // Insertion testing
//    for (int i = 0; i < trials; i++) {
//      r = new Random(System.currentTimeMillis());
//      for (int j = 0; j < z.length; j++)  z[j] = r.nextInt(2);
//      randomData.add(Arrays.copyOf(z, z.length));
//      t.reset();
//      data = Insertion.insertionTestHypo2128(z);
//      time[c++] = t.num();
//      insertionData.add(data);
//    }
//    
//    System.out.println("Insertion results");
//    meanTime = mean(time);
//    stddevTime = stddev(time);
//    writes = new int[trials];
//    compares = new int[trials];
//    c = 0;
//    for (int[] a : insertionData) {
//      writes[c] = a[0];  compares[c++] = a[1];
//    }
//    avgWrites = mean(writes);
//    stddevWrites = stddev(writes);
//    avgCompares = mean(compares);
//    stddevCompares = stddev(compares);
//    System.out.printf("run time mean:   %5.3f\n", meanTime);
//    System.out.printf("run time stddev: %5.3f\n", stddevTime);
//    System.out.printf("writes mean:     %5.3f\n", avgWrites);
//    System.out.printf("writes stddev:   %5.3f\n", stddevWrites);
//    System.out.printf("compares mean:   %5.3f\n", avgCompares);
//    System.out.printf("compares stddev: %5.3f\n\n", stddevCompares);    
//    
//    // Selection testing
//    c = 0;
//    Iterator<Integer[]> it = randomData.iterator();
//    for (int i = 0; i < trials; i++) {
//        Integer[] a = it.next();
//        t.reset();
//        data = Selection.selectionTestHypo2128(a);
//        time[c++] = t.num();
//        selectionData.add(data);
//    }
//  
//    System.out.println("Selection results");
//    meanTime = mean(time);
//    stddevTime = stddev(time);
//    writes = new int[trials];
//    compares = new int[trials];
//    c = 0;
//    for (int[] a : selectionData) {
//      writes[c] = a[0];  compares[c++] = a[1];
//    }
//    avgWrites = mean(writes);
//    stddevWrites = stddev(writes);
//    avgCompares = mean(compares);
//    stddevCompares = stddev(compares);
//    System.out.printf("run time mean:   %5.3f\n", meanTime);
//    System.out.printf("run time stddev: %5.3f\n", stddevTime);
//    System.out.printf("writes mean:     %5.3f\n", avgWrites);
//    System.out.printf("writes stddev:   %5.3f\n", stddevWrites);
//    System.out.printf("compares mean:   %5.3f\n", avgCompares);
//    System.out.printf("compares stddev: %5.3f\n", stddevCompares);    
 
  }
}
