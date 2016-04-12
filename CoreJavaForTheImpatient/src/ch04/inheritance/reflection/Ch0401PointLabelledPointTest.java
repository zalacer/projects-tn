package ch04.inheritance.reflection;

import utils.LabeledPoint;

// 1. Define a class Point with a constructor public Point(double x,
// double y) and accessor methods getX, getY. Define a subclass
// LabeledPoint with a constructor public LabeledPoint(String
// label, double x, double y) and an accessor method getLabel.

// Point.java is in this package, LabeledPoint is in utils1

public class Ch0401PointLabelledPointTest {

  public static void main(String[] args) {

    Point p1 = new Point(1,2);
    System.out.println(p1); // Point [x=1.0, y=2.0]

    LabeledPoint lp1 = new LabeledPoint("lp1", 3, 4);
    // LabeledPoint.toString() resolves x and y even when they are private 
    // in Point but they are in different packages, since it accesses them
    // them with super which is Point
    System.out.println(lp1); 
    // LabeledPoint [Label=lp1, x=3.0, y=4.0]
    // Can directly access the value of lp1.x when LabeledPoint is in a 
    // different package than point only when x is public or protected 
    // in Point and not when it's private
    System.out.println(lp1.x); // 3.0
    // this works even when x is private in Point since Point.getX() is public
    System.out.println(lp1.getX());


  }

}
