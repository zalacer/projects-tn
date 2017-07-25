package analysis;

import static java.lang.Math.max;
import static java.lang.Math.min;

import v.Tuple2;

public class LineIntersection {
  
  // static methods to tell if two line segments in the plane intersect
  // for building EuclidianEdgeWeightedDigraphs with no intersecting edges
  // from http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/

  private LineIntersection() {}
  
  public static boolean onSegment(Tuple2<Double,Double> p, Tuple2<Double,Double> q, 
      Tuple2<Double,Double> r) {
      if (q._1 <= max(p._1, r._1) && q._1 >= min(p._1, r._1) &&
          q._2 <= max(p._2, r._2) && q._2 >= min(p._2, r._2))
         return true;  
      return false;
  }
  
  public static int orientation(Tuple2<Double,Double> p, Tuple2<Double,Double> q, 
      Tuple2<Double,Double> r) {
      // See http://www.geeksforgeeks.org/orientation-3-ordered-points/
      // for details of below formula.
      double val = (q._2 - p._2) * (r._1 - q._1) -
                (q._1 - p._1) * (r._2 - q._2);  
      if (val == 0) return 0;  // colinear   
      return (val > 0)? 1: 2; // clock or counterclock wise
  }
  
  public static boolean intersect(Tuple2<Double,Double> p1, Tuple2<Double,Double> q1, 
      Tuple2<Double,Double> p2, Tuple2<Double,Double> q2) {
      // Find the four orientations needed for general and special cases
      double o1 = orientation(p1, q1, p2);
      double o2 = orientation(p1, q1, q2);
      double o3 = orientation(p2, q2, p1);
      double o4 = orientation(p2, q2, q1);
   
      // General case
      if (o1 != o2 && o3 != o4) return true;
   
      // Special Cases
      // p1, q1 and p2 are colinear and p2 lies on segment p1q1
      if (o1 == 0 && onSegment(p1, p2, q1)) return true;
   
      // p1, q1 and p2 are colinear and q2 lies on segment p1q1
      if (o2 == 0 && onSegment(p1, q2, q1)) return true;
   
      // p2, q2 and p1 are colinear and p1 lies on segment p2q2
      if (o3 == 0 && onSegment(p2, p1, q2)) return true;
   
       // p2, q2 and q1 are colinear and q1 lies on segment p2q2
      if (o4 == 0 && onSegment(p2, q1, q2)) return true;
   
      return false; // Doesn't fall in any of the above cases
  }
  
  

  public static void main(String[] args) {

  }

}
