package ch09.io;

import java.io.Serializable;

// This is Point V1 for Ch0914SerialImpl.
// For Ch0915SerialVersion relocate this class and PointV2 out of  
// ch09.io, then rename PointV2 to Point and move it back into ch09.io.

public class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    public Point(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Point(x=");
        builder.append(x);
        builder.append(", y=");
        builder.append(y);
        builder.append(")");
        return builder.toString();
    }
    

    

}
