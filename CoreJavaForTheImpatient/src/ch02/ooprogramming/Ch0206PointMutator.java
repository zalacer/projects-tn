package ch02.ooprogramming;

// 6. Repeat the preceding exercise, but now make translate and scale into
// mutators.

/**
 * An example of a mutable class with field mutators.
 * @author X
 * @version 1.0
 */
public class Ch0206PointMutator {

  /**
   * static main entry point used for class testing
   * @param args is String[] for command line arguments
   */
  public static void main(String[] args) {

    Ch0206PointMutator p = new Ch0206PointMutator(3, 4);
    System.out.println(p);     // Ch0205Point(3.0,4.0)
    printIdentityHashCode(p);  // identityHashCode(Ch0205Point(3.0,4.0)) = 2a139a55
    Ch0206PointMutator p2 = p.translate(1, 3); 
    System.out.println(p2);    // Ch0205Point(4.0,7.0)
    printIdentityHashCode(p2); // identityHashCode(Ch0205Point(4.0,7.0)) = 2a139a55
    Ch0206PointMutator p3 = p2.scale(0.5);
    System.out.println(p3);    // Ch0205Point(2.0,3.5)
    printIdentityHashCode(p3); // identityHashCode(Ch0205Point(2.0,3.5)) = 2a139a55
    assert p == p2;
    assert p2 == p3;

  }

  private double x = 0;
  private double y = 0;

  Ch0206PointMutator() {
  };

  Ch0206PointMutator(double x, double y) {
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
   * setter for field x
   * @param x the x to set
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * getter for field y
   * @return the y field
   */
  public double getY() {
    return y;
  }

  /**
   * setter for field y
   * @param y the y to set
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * translates the position of a Point instance
   * @param xinc is the amount by which field x is updated by addition
   * @param yinc is the amount by which field y is updated by addition
   * @return this Point to enable method chaining
   */
  public Ch0206PointMutator translate(double xinc, double yinc) {
    setX(this.x+xinc);
    setY(this.y+yinc);
    return this;
  }

  /**
   * scales the position of a Point instance
   * @param z is the scale factor which is multiplicatively applied to x and y
   * @return this Point to enable method chaining
   */
  public Ch0206PointMutator scale(double z) {
    setX(this.x*z);
    setY(this.y*z);
    return this;
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
    Ch0206PointMutator other = (Ch0206PointMutator) obj;
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
