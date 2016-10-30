package analysis;

import edu.princeton.cs.algs4.StdDraw;

public class StdDrawExample {

  // text p45
  public static void main(String[] args) {

    int N = 100;
    StdDraw.setXscale(0, N);
    StdDraw.setYscale(0, N*N);
    StdDraw.setPenRadius(.01);
    for (int i = 1; i <= N; i++) {
//      StdDraw.clear(StdDraw.WHITE);
//      StdDraw.setXscale(0, N);
//      StdDraw.setYscale(0, N*N);
//      StdDraw.setPenRadius(.01);
      StdDraw.point(i, i); // bottom
      StdDraw.point(i, i*Math.log(i)); //middle
      StdDraw.point(i, i*i); // top
    }
  }

}
