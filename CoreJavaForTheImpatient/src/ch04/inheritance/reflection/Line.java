package ch04.inheritance.reflection;

public class Line extends Shape implements Cloneable {

  private Point to;

  public Line(Point from, Point to) {
    super(from);
    this.to = to;
  }

  public Point getCenter() {
    return new Point((getP().getX() + to.getX())/2, (getP().getY() + to.getY())/2);
  }

  public Line clone() throws CloneNotSupportedException {
    return (Line) super.clone();
  }

  public Point getTo() {
    return to;
  }

  public void setTo(Point to) {
    this.to = to;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof Line))
      return false;
    Line other = (Line) obj;
    if (to == null) {
      if (other.to != null)
        return false;
    } else if (!to.equals(other.to))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Line [to=");
    builder.append(to);
    builder.append(", getCenter()=");
    builder.append(getCenter());
    builder.append("]");
    return builder.toString();
  }







}
