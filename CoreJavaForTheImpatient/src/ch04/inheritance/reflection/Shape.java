package ch04.inheritance.reflection;

//  4. Define an abstract class Shape with an instance variable of class Point, a
//  constructor, a concrete method public void moveBy(double dx,
//  double dy) that moves the point by the given amount, and an abstract method
//  public Point getCenter(). Provide concrete subclasses Circle,
//  Rectangle, Line with constructors public Circle(Point center,
//  double radius), public Rectangle(Point topLeft, double
//  width, double height) and public Line(Point from, Point to).

public abstract class Shape {

  private Point p;

  public Shape(Point p) {
    this.p = p;
  }

//  //  clone method for Shape not necessary to clone Circle, etc.
//  public Shape clone() throws CloneNotSupportedException {
//    return (Shape) super.clone();
//  }

  public void moveBy(double dx, double dy)  {
    p.setX(p.getX() + dx);
    p.setY(p.getY() + dy);
  }

  public abstract Point getCenter();

  public Point getP() {
    return p;
  }

  public void setP(Point p) {
    this.p = p;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((p == null) ? 0 : p.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Shape other = (Shape) obj;
    if (p == null) {
      if (other.p != null)
        return false;
    } else if (!p.equals(other.p))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Shape [p=");
    builder.append(p);
    builder.append("]");
    return builder.toString();
  }



}
