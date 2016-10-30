package ex25;

import java.security.SecureRandom;
import java.util.Arrays;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

/* p356
  2.5.26 Simple polygon. Given N points in the plane, draw a simple polygon 
  with N points as vertices. Hint: Find the point p with the smallest y 
  coordinate, breaking ties with the smallest x coordinate. Connect the 
  points in increasing order of the polar angle they make with p.  
 */

public class Ex2526SimplePolygon {
  
  public static void drawPolygon(int n) {
    
    SecureRandom r = new SecureRandom();
 
    StdDraw.setCanvasSize(800, 800);
    StdDraw.setXscale(0, 100);
    StdDraw.setYscale(0, 100);
    StdDraw.setPenRadius(0.005);

    Point2D[] points = new Point2D[n];
    for (int i = 0; i < n; i++) {
      int x = r.nextInt(100)+1;
      int y = r.nextInt(100)+1;
      points[i] = new Point2D(x, y);
      points[i].draw();
    }
    
    double miny = Double.POSITIVE_INFINITY;
    int minidx = -1;
    for (int i = 0; i < n; i++) {
      if (points[i].y() < miny) { miny = points[i].y(); minidx = i; }
      else if (points[i].y() == miny && points[i].x() < points[minidx].x()) {
        miny = points[i].y(); minidx = i;
      }
    }
    
    if (minidx != 0) { // exchange points[minidx] with points[0]
      Point2D t = points[0]; points[0] = points[minidx]; points[minidx] = t;
    }
          
    Point2D p = points[0]; 
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(0.02);
    p.draw();
    
    // draw line segments from points[i] to points[i+1] in polar order by p 
    StdDraw.setPenRadius();
    StdDraw.setPenColor(StdDraw.BLUE);
    Arrays.sort(points, p.polarOrder());
    for (int i = 0; i < points.length-1; i++) {
      points[i].drawTo(points[i+1]);
    }
    p.drawTo(points[n-1]); // close the polygon
  }
  
  public static void main(String[] args) {
    
    drawPolygon(5);
    
  }

}


