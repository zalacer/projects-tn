package ch04.inheritance.reflection;

// 4. Define an abstract class Shape with an instance variable of class Point, a
// constructor, a concrete method public void moveBy(double dx, double dy) that 
// moves the point by the given amount, and an abstract method public Point 
// getCenter(). Provide concrete subclasses Circle, Rectangle, Line with 
// constructors public Circle(Point center, double radius), public 
// Rectangle(Point topLeft, double width, double height) and 
// public Line(Point from, Point to).

// Shape, Circle, Rectangle and Line classes are each defined in a separate file
// in this package.

//5. Define clone methods for the classes of the preceding exercise.

public class Ch0404and05ShapesTests {

  public static void main(String[] args) {

    Circle circle = new Circle(new Point(3,3), 2);
    System.out.println(circle); 
    // Circle [radius=2.0, getCenter()=Point [x=3.0, y=3.0]]

    Rectangle rect = new Rectangle(new Point(9,9), 5, 7);
    System.out.println(rect); 
    // Rectangle [width=5.0, height=7.0, getCenter()=Point [x=11.5, y=5.5]]

    Line line = new Line(new Point(1,1), new Point(5,5));
    System.out.println(line);
    // Line [to=Point [x=5.0, y=5.0], getCenter()=Point [x=3.0, y=3.0]]

    try {
      Circle clonedCircle = circle.clone();
      System.out.println(clonedCircle);
      // Circle [radius=2.0, getCenter()=Point [x=3.0, y=3.0]]
      assert clonedCircle.equals(circle);

    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    try {
      Rectangle clonedRect = rect.clone();
      System.out.println(clonedRect);
      // Rectangle [width=5.0, height=7.0, getCenter()=Point [x=11.5, y=5.5]]
      assert clonedRect.equals(rect);
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    try {
      Line clonedLine = line.clone();
      System.out.println(clonedLine);
      // Line [to=Point [x=5.0, y=5.0], getCenter()=Point [x=3.0, y=3.0]]
      assert clonedLine.equals(line);
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }




  }

}
