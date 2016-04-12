package horstmann.ch09.sec05.Serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.geometry.Point2D;

public class LabeledPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    private String label;
    private transient Point2D point;
    
    public LabeledPoint(String label, Point2D point) {
        this.label = label;
        this.point = point;
    }
    
    public String getLabel() {
        return label;
    }



    public void setLabel(String label) {
        this.label = label;
    }



    public Point2D getPoint() {
        return point;
    }



    public void setPoint(Point2D point) {
        this.point = point;
    }



    public static long getSerialversionuid() {
        return serialVersionUID;
    }



    public String toString() {
        // TODO Auto-generated method stub
        return String.format("%s[label=%s,point=%s]", getClass().getSimpleName(), label, point);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(point.getX());
        out.writeDouble(point.getY());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        double x = in.readDouble();
        double y = in.readDouble();
        point = new Point2D(x, y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        LabeledPoint other = (LabeledPoint) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    } 
    
    
}
