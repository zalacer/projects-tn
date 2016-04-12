package ch04.inheritance.reflection;

public class Rectangle extends Shape implements Cloneable {

  private double width;
  private double height;

  public Rectangle(Point topLeft, double width, double height) {
    super(topLeft);
    this.width = width;
    this.height = height;        
  }

  public Rectangle clone() throws CloneNotSupportedException {
    return (Rectangle) super.clone();
  }

  public Point getCenter() {
    return new Point(getP().getX() + (width / 2), getP().getY() - (height / 2));
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(height);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(width);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof Rectangle))
      return false;
    Rectangle other = (Rectangle) obj;
    if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height))
      return false;
    if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Rectangle [width=");
    builder.append(width);
    builder.append(", height=");
    builder.append(height);
    builder.append(", getCenter()=");
    builder.append(getCenter());
    builder.append("]");
    return builder.toString();
  }





}
