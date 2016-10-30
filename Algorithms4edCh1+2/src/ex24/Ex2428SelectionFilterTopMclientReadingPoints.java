package ex24;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

import java.util.Comparator;
import java.util.Scanner;

import analysis.Timer;
import pq.MinPQ;
import v.Tuple3;

/* p331
  2.4.28 Selection filter. Write a TopM client that reads points (x, y, z) from 
  standard input, takes a value M from the command line, and prints the M points 
  that are closest to the origin in Euclidean distance. Estimate the running time 
  of your client for N = 10^8 and M = 10^4' 
  
*/

@SuppressWarnings("unused")
public class Ex2428SelectionFilterTopMclientReadingPoints {
  
  public static Comparator<Tuple3<Double,Double,Double>> tc = (a,b) -> {
    Double A = sqrt(a._1*a._1+a._2*a._2+a._3*a._3);
    Double B = sqrt(b._1*b._1+b._2*b._2+b._3*b._3);
    return A.compareTo(B); };
    
  public static double distance(Tuple3<Double,Double,Double> a) {
    return sqrt(a._1*a._1+a._2*a._2+a._3*a._3);
  }
  
  public static void estimate(int M) {
    // print estimate of the running time to process 10*8 points.
    MinPQ<Tuple3<Double,Double,Double>> pq = new MinPQ<>(tc);
    double[] a = new double[3]; int c = 0;
    Scanner sc = new Scanner("4Kints.txt");
    Timer t = new Timer();
    while (sc.hasNext() && c < 2970) { //990 points
      for (int i = 0; i < 3; i++) {
        if (sc.hasNextDouble()) a[i] = sc.nextDouble();  
      }
      pq.insert(new Tuple3<Double,Double,Double>(a[0],a[1],a[2]));
      c++;
    }
    sc.close();
    for (int i = 0; i < M; i++) pq.delMin();
    long time = t.num(); 
    double estimate = (1.*t.num()*100000000/990)/(1000*60);
    System.out.println("estimated time to process 10^8 points is "+
        (int)ceil(estimate)+" minutes."); 
    estimate = (1.*t.num()*10000/990);
    System.out.printf("estimated time to process 10^4 points is "+
        (int)ceil(estimate)+" milliseconds."); 
  }
 
  // this is the client
  public static void main(String[] args) {
    
    int M = Integer.parseInt(args[0]);
    if (M < 0) M = 0;
    System.out.println(M);
    
    estimate(M);
    
    MinPQ<Tuple3<Double,Double,Double>> pq = new MinPQ<>(tc);
    
    Scanner sc = new Scanner(System.in);
    double[] a = new double[3];
    while (sc.hasNext()) {
      for (int i = 0; i < 3; i++) {
        if (sc.hasNextDouble()) a[i] = sc.nextDouble();  
      }
      pq.insert(new Tuple3<Double,Double,Double>(a[0],a[1],a[2]));     
    }
    sc.close();
    int size = pq.size();
    int lim = size < M ? size : M;
    for (int i = 0; i < M; i++) {
      Tuple3<Double,Double,Double> x = pq.delMin(); 
      System.out.printf("%32s %8.0f\n", x, distance(x));     
    }
  
/*    
  transcript of example session using 1st 99 values from 4Kints.txt
  10
  estimated time to process 10^8 points is 425 minutes.
  estimated time to process 10^4 points is 2546 milliseconds.
  324110
  -442472
   626686
  -157678
   508681
   123414
   -77867
   155091
   129801
   287381
   604242
   686904
  -247109
    77867
   982455
  -210707
  -922943
  -738817
    85168
   855430
  -365580
  -174307
   -28560
   888769
  -887534
  -563503
   752524
   777031
   385644
  -768774
   211244
   792814
  -475905
   968127
  -504909
   570656
  -458444
  -957331
   259322
  -648617
   451074
   858015
   849248
  -361918
  -683640
  -449851
  -363749
  -425926
   418216
  -609284
   460009
   331758
  -600376
   325398
   279751
   592222
   309723
    52921
   600291
   799037
   836049
  -190454
    13962
  -714343
   696033
  -403836
  -712535
   604362
  -847079
   -30113
   823044
  -574291
   -72109
   719281
  -963727
  -769033
  -600693
  -247829
  -781838
  -313995
   911682
   347184
  -212857
    63665
   261049
  -445002
   486913
   372622
   980111
   982505
  -951004
   954267
   915640
  -547139
  -885732
  -774826
  -227720
  -893313
   795588
      (-77867.0,155091.0,129801.0)   216714
      (347184.0,-212857.0,63665.0)   412187
     (-157678.0,508681.0,123414.0)   546671
     (261049.0,-445002.0,486913.0)   709407
   (-449851.0,-363749.0,-425926.0)   718396
      (279751.0,592222.0,309723.0)   724511
     (331758.0,-600376.0,325398.0)   759209
     (324110.0,-442472.0,626686.0)   832805
      (836049.0,-190454.0,13962.0)   857581
     (418216.0,-609284.0,460009.0)   870483

*/  
  }

}
