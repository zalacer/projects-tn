package ch04.inheritance.reflection;

import utils.LabeledPoint;

// 3. Make the instance variables x and y of the Point class in Exercise 1
// protected. Show that the LabeledPoint class can access these variables only
// in LabeledPoint instances.

public class Ch0403Protected {

  public static void main(String[] args) {

    Point p1 = new Point(1,2);
    System.out.println(p1); // Point [x=1.0, y=2.0]

    LabeledPoint lp1 = new LabeledPoint("lp1", 3, 4);
    System.out.println(lp1); // LabeledPoint [Label=lp1]

    LabeledPoint lp2 = new LabeledPoint("lp2", 5, 6);

    System.out.println(lp1.getX()); // 3.0 
    // lp1.x visible from lp1 when private or protected in same or other package

    System.out.println(lp1.getOtherLabeledPointX(lp2)); // 5.0
    // lp2.x visible from lp1 when x is protected and Point and LabeledPoint in different pkgs
    
    // LabeledPoint cannot access x or y of a Point when they are protected and the two classes
    // are in different packages. To prove this uncomment the getOtherPointX() and getOtherPointY()
    // methods in LabeledPoint. When this is done in Eclipse an error occurs saying "the field 
    // Point.x (or Point.y) is not visible.




  }

}
