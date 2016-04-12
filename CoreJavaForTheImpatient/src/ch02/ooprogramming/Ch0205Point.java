package ch02.ooprogramming;

// 5. Implement an immutable class Point that describes a point in the plane. Provide a
// constructor to set it to a specific point, a no-arg constructor to set it to the origin, and
// methods getX, getY, translate, and scale. The translate method moves
// the point by a given amount in x- and y-direction. The scale method scales both
// coordinates by a given factor. Implement these methods so that they return new
// points with the results. For example,
// Point p = new Point(3, 4).translate(1, 3).scale(0.5);
// should set p to a point with coordinates (2, 3.5).

/**
 * An example of an immutable class in which modification 
 * of field values always results in a new instance.
 * @author X
 * @version 1.0
 */
public class Ch0205Point {

  /**
   * static main entry point used for class testing
   * @param args is String[] for command line arguments
   */
  public static void main(String[] args) {

    Ch0205Point p = new Ch0205Point(3, 4).translate(1, 3).scale(0.5);
    System.out.println(p); // Ch0205Point(2.0,3.5)

    Ch0205Point p2 = new Ch0205Point(3, 4);
    System.out.println(p2); // Ch0205Point(3.0,4.0)
    printIdentityHashCode(p2); // identityHashCode(Ch0205Point(3.0,4.0)) = 2a139a55
    Ch0205Point p3 = p2.translate(1, 3).scale(0.5);
    System.out.println(p3); // Ch0205Point(2.0,3.5)
    printIdentityHashCode(p3); // identityHashCode(Ch0205Point(2.0,3.5)) = 15db9742
    assert p2 != p3;

  }

  private double x = 0;
  private double y = 0;

  Ch0205Point() {};

  Ch0205Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * getter for field x
   * @return the x field
   */
  public double getX() {
    return x;
  }

  /**
   * getter for field y
   * @return the y field
   */
  public double getY() {
    return y;
  }

  /**
   * translates the position of a Point instance
   * @param xinc is the amount by which field x is updated by addition
   * @param yinc is the amount by which field y is updated by addition
   * @return a new Point with the translated position
   */
  public Ch0205Point translate(double xinc, double yinc) {
    return new Ch0205Point(this.x+xinc, this.y+yinc);
  }

  /**
   * scales the position of a Point instance
   * @param z is the scale factor which is multiplicatively applied to x and y
   * @return a new Point with the scaled position
   */
  public Ch0205Point scale(double z) {
    return new Ch0205Point(this.x*z, this.y*z);
  }

  /**
   * computes custom hashcode which depends on the values of fields x and y
   * @return integer hashcode
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /**
   * custom equals method which depends on the values of fields x and y
   * @param obj is the object compared to this object for equality
   * @return boolean
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Ch0205Point other = (Ch0205Point) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    return true;
  }

  /**
   * custom toString method
   * @return a String representation of this Point
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Ch0205Point(" + x + "," + y + ")";
  }

  /**
   * static wrapper for printing Integer.toHexString(System.identityHashCode(Object obj)
   * @param obj is the object which is used as the argument of System.identityHashCode()
   * @see java.lang.System#identityHashCode(java.lang.Object)
   * @see java.lang.Integer#toHexString(int)
   */
  public static void printIdentityHashCode(Object obj) {
    System.out.println("identityHashCode("+obj+") = "
        +Integer.toHexString(System.identityHashCode(obj)));
  }

}
