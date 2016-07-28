package ex12;

import java.util.Random;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import edu.princeton.cs.algs4.Interval1D;
import edu.princeton.cs.algs4.Interval2D;

//  1.2.3  Write an  Interval2D client that takes command-line arguments  N ,  min , and  max
//  and generates N random 2D intervals whose width and height are uniformly distributed
//  between  min and  max in the unit square. Draw them on  StdDraw and print the number
//  of pairs of intervals that intersect and the number of intervals that are contained in one
//  another.

public class Ex1203Interval2D {
  
  private static class Itvl2D extends Interval2D {
    //this class is defined to override Interval2D.toString()
    private final Interval1D x;
    private final Interval1D y;

    public Itvl2D(Interval1D x, Interval1D y) {
      super(x,y);
      this.x = x; this.y = y;
    }
    
    @Override
    public String toString() {
      return x + "," + y;
    }
  }
  
  public static boolean eitherContains(Itvl2D i1, Itvl2D i2) {
    // returns true if i1 contains i2 || i2 contains i1 else false
    double x1min = i1.x.min(); double x1max = i1.x.max();
    double y1min = i1.y.min(); double y1max = i1.y.max();
    double x2min = i2.x.min(); double x2max = i2.x.max();
    double y2min = i2.y.min(); double y2max = i2.y.max();
    if (x2min <= x1min && x2max >= x1max && y2min <= y1min && y2max >= y1max
        || x1min <= x2min && x1max >= x2max && y1min <= y2min && y1max >= y2max) {
      return true;
    } else return false;
  }
  
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]); // number of Interval2D to generate
    double min = Double.parseDouble(args[1]);
    double max = Double.parseDouble(args[2]);
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    if (r == null) r = new Random(778251903);
    Itvl2D[] a = new Itvl2D[n];
    double[] da = r.doubles(4*n,min,max).toArray();
    //pa(da,75,1,1); System.out.println();
    int c = 0;
    double[] t = null;
    Interval1D itvl1 = null;  Interval1D itvl2 = null; 
    for (int i = 0; i < 4*n; i+=4) {
      t = da[i]<=da[i+1] ? (new double[]{da[i],da[i+1]}) : (new double[]{da[i+1],da[i]});
      itvl1 = new Interval1D(t[0], t[1]);
      t = da[i+2]<=da[i+3] ? (new double[]{da[i+2],da[i+3]}) : (new double[]{da[i+3],da[i+2]});
      itvl2 = new Interval1D(t[0], t[1]);
      a[c++] = new Itvl2D(itvl1,itvl2);
    }
    int contain = 0;
    int intersect = 0;
    for (int j = 0; j < n; j++) {
      for (int k = j+1; k < n; k++) {
        if (eitherContains(a[j],a[k])) contain++;
        if (a[j].intersects(a[k])) intersect++;         
      }
    }
    pa(a,75,1,1);
    System.out.println("contain="+contain);
    System.out.println("intersect="+intersect);
    for (int i = 0; i < n; i++) a[i].draw();

    
  }

}
