package ex21;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;

/* p268
  2.1.34 Corner cases. Write a client that runs sort() on difficult or pathological cases
  that might turn up in practical applications. Examples include arrays that are already
  in order, arrays in reverse order, arrays where all keys are the same, arrays consisting of
  only two distinct values, and arrays of size 0 or 1.

 */

public class Ex2134CornerCases {
  
  public static void sortCornerCase(String alg, String cornerCase, int N) {
    /* run the sort() specified by alg through the specified corner case.
       recognized corner cases are specified by the following strings:
       1. "sorted" for a sorted array of Doubles of length N with no null values.
       2. "reverse" for a reverse sorted array of Doubles of length N with no null values.
       3. "1value" for an array of length N with one distinct nonnull Double value.
       4. "2values" for an array of length N with two distinct nonnull Double values.
       5. "size0" for an array of Doubles of length zero.
       6. "size1" for an array of Doubles length 1 with one nonnull Double value.
       7. "nulls" for an unsorted array of Doubles length N with Double values and nulls.
       8. "infinity" for an unsorted array of Doubles with finite and infinite Double values.
    */
       
    if (alg == null || alg.length() == 0)  throw new IllegalArgumentException(
        "sortCornerCase: alg can't be null or empty");
    if (cornerCase == null || cornerCase.length() == 0)  throw new IllegalArgumentException(
        "sortCornerCase: cornerCase can't be null or empty");   
    
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }
    
    Double[] z = null; Random r = null;
    
    switch (cornerCase) {
    case "sorted":
      if (N < 0) throw new IllegalArgumentException("sortCornerCase: N must be >0");
      r = new Random(System.currentTimeMillis());
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = r.nextDouble();
      Arrays.sort(z);
      con.accept(z);
      break;
    case "reverse":
      if (N < 0) throw new IllegalArgumentException("sortCornerCase: N must be >0");
      r = new Random(System.currentTimeMillis());
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = r.nextDouble();
      Arrays.sort(z, Comparator.reverseOrder());
      con.accept(z);
      break;
    case "1value":
      r = new Random(System.currentTimeMillis());
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = 9.;
      con.accept(z);
      break;  
    case "2values":
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = i % 2 == 0 ? 5. : 8.;
      con.accept(z);
      break;
    case "size0" :
      z = new Double[0];
      con.accept(z);
      break;
    case "size1":
      z = new Double[1];
      z[0] = 3.;
      con.accept(z);
      break;
    case "nulls":
      if (N < 0) throw new IllegalArgumentException("sortCornerCase: N must be >0");
      r = new Random(System.currentTimeMillis());
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = r.nextDouble();
      z[0] = null; z[z.length/2] = null; z[z.length-1] = null;
      con.accept(z);
      break;
    case "infinity":
      if (N < 0) throw new IllegalArgumentException("sortCornerCase: N must be >0");
      r = new Random(System.currentTimeMillis());
      z = new Double[N];
      for (int i = 0; i < N; i++) z[i] = r.nextDouble();
      z[0] = Double.POSITIVE_INFINITY; z[z.length/2] = Double.NEGATIVE_INFINITY; 
      z[z.length-1] = Double.POSITIVE_INFINITY;
      con.accept(z);
      break;   
    case "default":
      throw new IllegalArgumentException("sort: cornerCase not recognized");
    }
  }

  public static void main(String[] args) {
    
    sortCornerCase("selection", "size0", -1);
    
    sortCornerCase("insertion", "infinity", 100);
    
    sortCornerCase("shell", "nulls", 10000);
    // java.lang.NullPointerException at sort.Shell.less(Shell.java:102)
    
  }
  
}
