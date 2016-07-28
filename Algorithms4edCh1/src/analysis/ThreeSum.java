package analysis;

import java.math.BigInteger;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from text p173
public class ThreeSum {

  public static int count(int[] a) { 
    // Count triples that sum to 0.
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)        //1
      for (int j = i+1; j < N; j++)    //~N
        for (int k = j+1; k < N; k++)  //~(N**2)/2 
          if (a[i] + a[j] + a[k] == 0) //~(N**3)/6
            cnt++;
    return cnt;
  }
  
  public static int countLong(int[] a) { 
    // Count triples that sum to 0.
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)
      for (int j = i+1; j < N; j++)
        for (int k = j+1; k < N; k++)
          if ((long)a[i] + (long)a[j] + (long)a[k] == 0)
            cnt++;
    return cnt;
  }
  
  public static int countBigInteger(int[] a) { 
    // Count triples that sum to 0.
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)
      for (int j = i+1; j < N; j++)
        for (int k = j+1; k < N; k++)
          if ((new BigInteger(""+a[i]).add(new BigInteger(""+a[j]))
              .add(new BigInteger(""+a[k]))) == BigInteger.ZERO)
            cnt++;
    return cnt;
  }

  public static void main(String[] args) {
    // default main contents, text p181
    // int[] a = In.readInts(args[0]);
    // StdOut.println(count(a));
    
    @SuppressWarnings("unused")
    String one = "1Kints.txt";
    String two = "2Kints.txt";
    @SuppressWarnings("unused")
    String four = "4Kints.txt";
    int[] a = new In(two).readAllInts();
    
    Timer t = new Timer();
    StdOut.println(count(a));
    t.stop();
    //one:75:156; two:528:1264; four:4039:9719
    //one:70:196; two:528:1333; using Watch
    //one:70:187; two:528:1346; using Watch
    //one:70:75;  two:528:1330;
    
  }

}
