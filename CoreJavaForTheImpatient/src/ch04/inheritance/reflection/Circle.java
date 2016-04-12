package ch04.inheritance.reflection;

public class Circle extends Shape implements Cloneable {

  private double radius;

  Circle(Point center, double radius) {
    super(center);
    this.radius = radius;
  }

  public Circle clone() throws CloneNotSupportedException {
    return (Circle) super.clone();
  }

  public Point getCenter() {
    return getP();
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(radius);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Circle other = (Circle) obj;
    if (Double.doubleToLongBits(radius) != Double.doubleToLongBits(other.radius))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Circle [radius=");
    builder.append(radius);
    builder.append(", getCenter()=");
    builder.append(getCenter());
    builder.append("]");
    return builder.toString();
  }











}
