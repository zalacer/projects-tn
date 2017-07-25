package ex11;

import static edu.princeton.cs.algs4.StdRandom.bernoulli;

import edu.princeton.cs.algs4.StdDraw;

//  1.1.31 Random connections. Write a program that takes as command-line arguments
//  an integer N and a double value p (between 0 and 1), plots N equally spaced
//  dots of size .05 on the circumference of a circle, and then, with probability p 
//  for each pair of points, draws a gray line connecting them.

public class Ex1131PlotCircleRandomConnections {
  
  public static void plotConnections(int N, double p) {
    // define a circle with radius 90 and center at 0.0 on canvas of
    // -100 to +100 in both x and y, but don't draw it
    int n = N;
    StdDraw.setXscale(-100, 100);
    StdDraw.setYscale(-100, 100);
    StdDraw.setPenRadius(.05);
    StdDraw.setPenColor(StdDraw.BLACK);
    double r = 90.0; // radius
    
    // draw n equally spaced points on circumference of circle
    double x = 0.; //  x coord for initial point on circumference
    double y = x + r; // y coord for initial point on circumference
    double t = 2*Math.PI/n; // angle between adjacent points
    double x2; double y2; // tmp vars
    double[][] a = new double[n][n]; // array of points
    for (int j = 0; j < n; j++) {
      x2 = x + r * Math.sin(t*j);
      y2 = y - r * (1 - Math.cos(t*j));
      a[j] = new double[]{x2,y2};
      StdDraw.point(x2, y2);
    }
    
    // with probability p draw a line in gray between each distinct pair 
    // of points that have already been drawn on the circumference
    StdDraw.setPenRadius(.01); // set smaller than for the points to not obscure them
    StdDraw.setPenColor(StdDraw.GRAY);
    
    for (int i = 0; i < a.length; i++) {
      for (int j = i+1; j < a.length; j++) {
        if (bernoulli(p)) StdDraw.line(a[i][0], a[i][1], a[j][0], a[j][1]);
      }
    }
    
  }

  public static void main(String[] args) {

    plotConnections(Integer.parseInt(args[0]), Double.parseDouble(args[1]));
    
//    StdDraw.save("Ex1131PlotCircleRandomConnections.p0.5.jpg");
    
  }
 

}
