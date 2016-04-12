package utils;

//import ch04.inheritance.reflection.Point;

//  1. Define a class Point with a constructor public Point(double x,
//  double y) and accessor methods getX, getY. Define a subclass
//  LabeledPoint with a constructor public LabeledPoint(String
//  label, double x, double y) and an accessor method getLabel.

public class LabeledPoint extends ch04.inheritance.reflection.Point {

  private String Label;

  public LabeledPoint(String label, double x, double y) {
    super(x,y);
    this.Label = label;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return Label;
  }

  //  when LabeledPoint is in a different package than Point this works if
  //  x is public or protected but not when its private or has default access
  public double getX () {
    return x;
  }

  //  when LabeledPoint is in a different package than Point this works if
  //  y is public or protected but not when its private or has default access
  public double getY () {
    return y;
  }  

  //   this is for demo in ch04.inheritance.reflection.Ch0403Protected
  public double getOtherLabeledPointX (LabeledPoint p) {
    return p.x;
  }
  
//  public double getOtherPointX (Point p) {
//    return p.x;
//  }
//  
//  public double getOtherPointY (Point p) {
//    return p.y;
//  }

  /**
   * @param label the label to set
   */
  //    public void setLabel(String label) {
  //        Label = label;
  //    }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((Label == null) ? 0 : Label.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LabeledPoint other = (LabeledPoint) obj;
    if (Label == null) {
      if (other.Label != null)
        return false;
    } else if (!Label.equals(other.Label))
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LabeledPoint [Label=");
    builder.append(Label);
    builder.append(", x=");
    builder.append(super.getX());
    builder.append(", y=");
    builder.append(super.getY());
    builder.append("]");
    return builder.toString();
  }

}
