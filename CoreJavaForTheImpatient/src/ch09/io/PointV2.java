package ch09.io;

import static java.lang.Double.NEGATIVE_INFINITY;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Arrays;

// This is Point V2 for Ch0915SerialVersion.
// To use it relocate it and Point V1 (which is named Point) somewhere outside 
// of ch09.io. Then rename PointV2 to Point and move it back into ch09.io.

public class PointV2 implements Serializable {
    private static final long serialVersionUID = 1L;
    private double[] c = new double[2];
    
    public PointV2(double x, double y) {
        super();
        c[0] = x;
        c[1] = y;
    }
    
//    custom writeObject not needed
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
// 
//    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // in.defaultReadObject();
        ObjectInputStream.GetField gf = in.readFields();
        ObjectStreamClass osc = gf.getObjectStreamClass();
        ObjectStreamField[] fields = osc.getFields();
        boolean foundc = false;
        boolean foundx = false;
        boolean foundy = false;
        for (ObjectStreamField f : fields) {
            if (f.getName().equals("c")) foundc = true;
            if (f.getName().equals("x")) foundx = true;
            if (f.getName().equals("y")) foundy = true;
        }
        // The point of using Double.NEGATIVE_INFINITY in default values is to 
        // enable easy recognition that gf.get() was probably unable to get the 
        // value from the persistent field without throwing IOException and 
        // presuming Double.NEGATIVE_INFINITY is an unlikely value.
        if (foundc) {
            c = (double[]) gf.get("c",new double[]{NEGATIVE_INFINITY,NEGATIVE_INFINITY});
        } else if (foundx && foundy) {
            double x = gf.get("x", NEGATIVE_INFINITY);
            double y = gf.get("y", NEGATIVE_INFINITY);
            c = new double[]{x,y};
        } else {
            c = new double[]{NEGATIVE_INFINITY, NEGATIVE_INFINITY};
        }
    }

    public double[] getC() {
        return c;
    }
    
    public void setC(double x, double y) {
            c[0] = x;
            c[1] = y;
    }
        
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(c);
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
        PointV2 other = (PointV2) obj;
        if (!Arrays.equals(c, other.c))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Point(c=");
        builder.append(Arrays.toString(c));
        builder.append(")");
        return builder.toString();
    }
    
    


    

}
