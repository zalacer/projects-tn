package ex15;

import static java.lang.Math.ceil;
import static v.ArrayUtils.max;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.min;
import static v.ArrayUtils.unbox;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import edu.princeton.cs.algs4.StdDraw;


/*
  p240
   1.5.26 Amortized plot for Erdös-Renyi. Develop a client that takes an  int value N from
  the command line and does an amortized plot of the cost of all operations in the style
  of the plots in the text for the process of generating random pairs of integers between 0
  and N-1, calling  connected() to determine if they are connected and then  union() if
  not (as in our development client), looping until all sites are connected.

  This is basically exercise 1.5.16 using the method developed in exercise 1.5.17 to 
  generate pairs.

 */

@SuppressWarnings("unused")
public class Ex1526ErdösRenyiAmortizedPlot {
  
  public static void runErdösRenyiModel(int N, String u) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    Random r = new Random(10719157);
    UF uf = null;
    switch (u) {
    case "QuickFindUF": uf = new QuickFindUF(n); break;
    case "QuickUnionUF": uf = new QuickUnionUF(n); break;
    default: 
      throw new IllegalArgumentException("count: u unrecognized UF class name");
    }
    int p; int q; int con = 0; 
    while(true) {
      p = r.nextInt(n); q = r.nextInt(n);
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    uf.plot(); 
  }
  
  public static interface UF {
    // interface for UF (union-find) classes
    public void union(int p, int q);
    public int find(int p);
    public boolean connected(int p, int q);
    public int count();
    public void plot();
  }

  public static class QuickFindUF implements UF {
    private int[] id;  // the ith location in id is the ith component while it's value is a site
    // if id[i] = j then site j is in component i
    private int count; // number of components
    private int c;     // counter for the connections
    private int t;     // total number of array accesses
    private List<Integer> cost;  // records number of array accesses for ith connection
    private List<Integer> total; // records total number of array accesses
    private int unionCost; // fixed cost of union()
    private int tcost; // temporarily stores cost for connected execution


    public QuickFindUF(int N) {
      /* This class is modified to make amortized cost plots with the plot() method. In order
         to do this, cost data in terms of array accesses is gathered during a run. For each 
         input pair processed the number of array accesses required for that is added to the 
         cost list and the total number of array accesses up to the current point is added to
         the total list. connected() and union() are instrumented to increment the number of 
         array access they incur from find executions, which always two, and extra id array
         accesses for union() which is always 2*id.length. The constant number of array 
         accesses made directly by union is stored in the class field unionCost. When 
         connected() will return true it adds 2 to the the cost list and to t, which is a 
         class field that stores the total accumulated number of array accesses, t is added 
         to the total list and connected() returns true. When connected() will return false, 
         it sets the class field tcost to 2 and returns true. When union() runs it sums 
         unionCost plus tcost and adds the result to the cost list and to t which is added to 
         the total list. When all input pairs have been processed and plot() is executed it 
         converts the cost and total lists to arrays and processes and plots the data in them. 
         Both lists and the arrays produced from them contain the same number of elements, 
         which is exactly the number of pairs processed, so there's no need to track that 
         explicitly. plot() scales the plot it draws by the total number of connections in the 
         x axis and the max of the max cost per connection and the max amortized cost in the y 
         axis. Plot() plots text lables for the name of the class its runs as the header, the 
         max cost per connection, the max amortized cost, the total number of connections and 
         the total number of array references; and it prints this data to System.out along with
         the min and mean costs and amortized costs per connection.
       */
      if (N <= 0) throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
      // initialize component id array so that each site is in the component with the same number.
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
      c = 0; t = 0;
      cost = new ArrayList<Integer>();
      total = new ArrayList<Integer>();
      unionCost = 2 + 2*id.length;
      tcost = 0;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      int px = find(p);
      int qx = find(q);
      if (px == qx) {
        cost.add(2); t+=2; total.add(t);
        return true; 
      } else {
        tcost = 2;
        return false;
      }
    }

    public int find(int p) {
      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      return id[p]; 
    }

    public void union(int p, int q) {
      // put p and q into the same component.
      // after all input pairs have been processed, components are identified  
      // as those indices i in id such that id[i] == i.
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pID = find(p);
      int qID = find(q); 
      // nothing to do if p and q are already in the same component.
      if (pID == qID) return;
      // rename p’s component to q’s name.
      for (int i = 0; i < id.length; i++) if (id[i] == pID) id[i] = qID;
      tcost = unionCost + tcost;
      cost.add(tcost);
      t+=(tcost);
      total.add(t);
      tcost = 0;
      count--;
    }

    public void plot() {
      Integer[] x = new Integer[0];
      int[] costx = (int[]) unbox(cost.toArray(x));
      int[] totalx = (int[]) unbox(total.toArray(x));

      if (costx.length != totalx.length) {
        System.err.println(
            "Warning: costx.length=="+costx.length+" but totalx.length == "+totalx.length);
      }

      int totalLength = totalx.length; // used later for iteration
      int totCon = totalLength; // total number of connections     
      System.out.println("totCon="+totCon);
      int totRef = totalx[totalx.length-1]; // total # of array references
      System.out.println("totRef="+totRef);
      System.out.println("minCost="+min(costx));
      int maxCost = max(costx); // max cost for a connection
      System.out.println("maxCost="+maxCost);
      System.out.printf("meanCost=%5.3f\n", mean(costx));

      // calculating max amortized cost (maxAmortizedCost)
      double tmp = Double.MIN_VALUE; double t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t = (1.*totalx[i-1]/i);
        if (t > tmp) tmp = t;
      }
      int mac = (int) ceil(tmp);  // max amortized cost
      System.out.println("maxAmortizedCost="+mac);

      // calculating min amortized cost (minAmortizedCost)
      tmp = Double.MAX_VALUE; t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t = (1.*totalx[i-1]/i);
        if (t < tmp) tmp = t;
      }
      int mic = (int) ceil(tmp);  // min amortized cost
      System.out.println("minAmortizedCost="+mic);

      // calculating mean amortized cost for printout only
      t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t += (1.*totalx[i-1]/i);
      }
      System.out.printf("meanAmortizedCost=%5.3f\n", t/totalx.length);

      int maxY = maxCost > mac ? maxCost : mac;
      double maxYscaled = maxY+(30.*maxY/192);
      int maxX = totCon;
      double maxXscaled = 1.*maxX+(1.*maxX/1.5);
      StdDraw.setXscale(0, maxXscaled);
      StdDraw.setYscale(0, maxYscaled);
      StdDraw.setPenRadius(.01);
      StdDraw.setPenColor(StdDraw.BLACK);
      Font font = new Font("Arial", Font.BOLD, 20);
      StdDraw.setFont(font);
      StdDraw.textLeft(totCon/2-200.*totCon/1000, maxYscaled-45.*maxYscaled/1000, "QuickFindUF Erdös-Renyi");
      font = new Font("Arial", Font.BOLD, 16);
      StdDraw.setFont(font);
      StdDraw.textLeft(totCon, 3.*maxYscaled/192, ""+totCon+" connections");
      StdDraw.textLeft(totCon+totCon/100, maxY, ""+maxY);
      StdDraw.textLeft(totCon+totCon/100, mic, ""+mic+" min amortized cost");
      StdDraw.textLeft(totCon+totCon/100, maxCost, ""+maxCost+" max cost/connection");
      StdDraw.text(totCon+totCon/10, (mic+maxCost)/2, ""+totRef+" array references total");
      StdDraw.setPenColor(StdDraw.GRAY);
      StdDraw.setPenRadius(.01);
      // plot cost data in gray
      for (int i = 1; i < totalLength+1; i++) StdDraw.point(i, costx[i-1]); 
      StdDraw.setPenColor(StdDraw.RED);
      // plot amortized cost data in red
      for (int i = 1; i < totalLength+1; i++) StdDraw.point(i, totalx[i-1]/i);
    }
  }

  public static class QuickUnionUF implements UF {
    /* This class is modified to make amortized cost plots with the plot() method. In order
       to do this, cost data in terms of array accesses is gathered during a run. For each 
       input pair processed the number of array accesses required for that is added to the 
       cost list and the total number of array accesses up to the current point is added to 
       the total list. connected() and union() are instrumented to increment the number of 
       array access they incur from find executions, which also requires instrumenting find 
       to count the number of array accesses it makes. In order to accomplish that, before 
       executing find, connected() and union() set tcost to zero. Find runs and increments 
       tcost and then connected() and union() save the value of tcost to another variable 
       internal to them and reset tcost to zero. If connected() will return true, the 
       accumulated costs from two find executions are added to the cost list and to the 
       variable t that contains the total number of array accesses which is added to the total 
       list and it returns true. If connected() will return false, it saves the accumulated 
       costs from two find executions to tcost2, resets tcost to zero and returns false. When 
       union() runs it does two find executions and saves their costs in variables internal to 
       it, adds their sum to tcost2, adds the total to the cost list and to t which is then 
       added to the total list, resets tcost and tcost2 to zero and finishes its usual 
       function. When all input pairs have been processed and plot() is executed it converts 
       the cost and total lists to arrays and processes and plots the data in them. Both lists 
       and the arrays produced from them contain the same number of elements, which is exactly 
       the number of pairs processed, so there's no need to track that explicitly. plot() 
       scales the plot it draws by the total number of connections in the x axis the max of the 
       max cost per connection and the max amortized cost in the y axis. Plot() plots text 
       lables for the name of the class its runs as the header, the max cost per connection, 
       the max amortized cost, the total number of connections and the total number of array 
       references; and it prints this data to System.out along with the min and mean costs and 
       amortized costs per connection.
     */

    private int[] id; // the ith location in id is the ith site while it's value is a site
    // in the same component given by its root which is a site with a
    // value equal to its index in id as implemented in find, i.e.
    // if id[i] = i then i is a root == component
    private int count; // number of components
    private int t;     // total number of array accesses
    private List<Integer> cost;  // records number of array accesses for ith connection
    private List<Integer> total; // records total number of array accesses
    private int tcost; // temporarily stores cost for find execution
    private int tcost2; // temporarily stores cost for connected execution

    public QuickUnionUF(int N) {
      // initialize component id array so that each site is in the component with the same name,
      // i.e. is its own root
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
      for (int i = 0; i < N; i++) id[i] = i;
      t = 0;
      cost = new ArrayList<Integer>();
      total = new ArrayList<Integer>();
      tcost = 0;
      tcost2 = 0;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "connected: p and q must both be >= 0 and <= id.length");
      tcost = 0; tcost2 = 0;
      int px = find(p);
      int pfindcost = tcost;
      tcost = 0;
      int qx = find(q);
      int qfindcost = tcost;
      int tcost = pfindcost+qfindcost;
      if (px == qx) {
        cost.add(tcost); t+=tcost; total.add(t);
        tcost = 0;
        return true;
      } else {
        tcost2 = tcost;
        tcost = 0;
        return false;
      }
    }

    public int find(int p) {
      // Find the root of p, i.e. its component.
      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) {
        p = id[p];
        tcost+=2;
      }
      tcost++;
      return p;
    }

    public void union(int p, int q) {
      // Give p and q the same root, i.e. component
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      tcost = 0;
      int pRoot = find(p); 
      int pfindcost = tcost;
      tcost = 0; 
      int qRoot = find(q); 
      int qfindcost = tcost;
      int tcost = pfindcost+qfindcost+tcost2;
      cost.add(tcost); t+=tcost; total.add(t);
      tcost = 0; tcost2 = 0;
      if (pRoot == qRoot) return;
      id[pRoot] = qRoot;
      count--;
    }

    public void plot() {
      Integer[] x = new Integer[0];
      int[] costx = (int[]) unbox(cost.toArray(x));
      int[] totalx = (int[]) unbox(total.toArray(x));

      if (costx.length != totalx.length) {
        System.err.println(
            "Warning: costx.length=="+costx.length+" but totalx.length == "+totalx.length);
      }

      int totalLength = totalx.length; // used later for iteration
      int totCon = totalLength; // total number of connections     
      System.out.println("totCon="+totCon);
      int totRef = totalx[totalx.length-1]; // total # of array references
      System.out.println("totRef="+totRef);
      System.out.println("minCost="+min(costx));
      int maxCost = max(costx); // max cost for a connection
      System.out.println("maxCost="+maxCost);
      System.out.printf("meanCost=%5.3f\n", mean(costx));

      // calculating max amortized cost (maxAmortizedCost)
      double tmp = Double.MIN_VALUE; double t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t = (1.*totalx[i-1]/i);
        if (t > tmp) tmp = t;
      }
      int mac = (int) ceil(tmp); // max amortized cost
      System.out.println("maxAmortizedCost="+mac);

      // calculating min amortized cost (minAmortizedCost)
      tmp = Double.MAX_VALUE; t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t = (1.*totalx[i-1]/i);
        if (t < tmp) tmp = t;
      }
      int mic = (int) ceil(tmp); // min amortized cost    
      System.out.println("minAmortizedCost="+mic);

      // calculating mean amortized cost for printout only
      t = 0;
      for (int i = 1; i < totalx.length+1; i++) {
        t += (1.*totalx[i-1]/i);
      }
      System.out.printf("meanAmortizedCost=%5.3f\n", t/totalx.length);

      int maxY = maxCost > mac ? maxCost : mac;
      double maxYscaled = maxY+(20.*maxY/192);
      int maxX = totCon;
      double maxXscaled = 1.*maxX+(1.*maxX/1.5);
      StdDraw.setXscale(0, maxXscaled);
      StdDraw.setYscale(0, maxYscaled);
      StdDraw.setPenRadius(.01);
      StdDraw.setPenColor(StdDraw.BLACK);
      Font font = new Font("Arial", Font.BOLD, 20);
      StdDraw.setFont(font);
      StdDraw.textLeft(totCon/2-200.*totCon/1000, maxYscaled-25.*maxYscaled/1000, "QuickUnionUF Erdös-Renyi");
      font = new Font("Arial", Font.BOLD, 16);
      StdDraw.setFont(font);
      StdDraw.textLeft(totCon, 3.*maxYscaled/192, ""+totCon+" connections");
      StdDraw.textLeft(totCon+totCon/100, maxY, ""+maxY);
      StdDraw.textLeft(totCon+totCon/100, mac, ""+mac+" max amortized cost");
      StdDraw.textLeft(totCon+totCon/100, maxCost, ""+maxCost+" max cost/connection");
      StdDraw.textLeft(totCon/25, maxCost-50.*maxCost/1000, ""+totRef+" array");
      StdDraw.textLeft(totCon/25, maxCost-80.*maxCost/1000, "references total");
      StdDraw.setPenColor(StdDraw.GRAY);
      StdDraw.setPenRadius(.01);
      // plot cost data in gray
      for (int i = 1; i < totalLength+1; i++) StdDraw.point(i, costx[i-1]); 
      StdDraw.setPenColor(StdDraw.RED);
      // plot amortized cost data in red
      for (int i = 1; i < totalLength+1; i++) StdDraw.point(i, totalx[i-1]/i);
    }
  }

  public static void main(String[] args) {
    
    Scanner sc = new Scanner(System.in);
    if (sc.hasNextInt())
      runErdösRenyiModel(sc.nextInt(), "QuickUnionUF");
    sc.close();

//    runErdösRenyiModel(1000, "QuickFindUF"); // QuickFindUFErdösRenyiAmortizedCostPlot.jpg
    //  totCon=3426
    //  totRef=2006850
    //  minCost=2
    //  maxCost=2004
    //  meanCost=585.771
    //  maxAmortizedCost=2004
    //  minAmortizedCost=586
    //  meanAmortizedCost=1258.687
    
//    runErdösRenyiModel(1000, "QuickUnionUF"); // QuickUnionUFErdösRenyiAmortizedCostPlot.jpg
    //  totCon=3426
    //  totRef=1227340
    //  minCost=4
    //  maxCost=752
    //  meanCost=358.243
    //  maxAmortizedCost=359
    //  minAmortizedCost=4
    //  meanAmortizedCost=184.079
    

  }
}
