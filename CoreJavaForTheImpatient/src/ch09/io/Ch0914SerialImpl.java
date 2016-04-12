package ch09.io;

import static utils.DateTimeUtils.now;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

// 14. Implement a serializable class Point with instance variables
// for x and y. Write a program that serializes an array of Point 
// objects to a file, and another that reads the file.

// This class requires Point V1 which is named Point in this package.

public class Ch0914SerialImpl {
  
  private static String outputDir = "ser";

  public static void checkOutputDir() {
    File outDir = new File(outputDir);
    if (outDir.exists()) {
      if (!outDir.isDirectory()) {
        System.out.println(""
            + outputDir + " exists but it's not a directory, please remove"
            + "\nor rename it and create a directory named " + outputDir);
      }
    } else {
      boolean successful = outDir.mkdir();
      if (!successful) {
        System.out.println(""
            + "a directory named " + outputDir + " does not exist and could not be"
            + "\ncreated with mkdir. try creating it interactively");
      }
    }
  }

  public static String serializeArrayOfPoints(Point...points) {
    // serialize an array of Points with default serialization
    long version = 1;
    String name = "ch09.io.PointArray";
    String opath = "ser\\" + name + "-V" + version + "-" +now() + ".ser";
    Path f = Paths.get(opath);
    OutputStream os = null;
    ObjectOutputStream oos = null;
    try {         
      os = Files.newOutputStream(f);
      oos = new ObjectOutputStream(os);
      oos.writeObject(points);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (oos != null)
          oos.close();
      } catch (IOException e) {}
      try {
        if (os != null)
          os.close();
      } catch (IOException e) {}
    }
    return f.toString();
  }

  public static String serializePoint(Point p) {
    long version = Point.getSerialversionuid();
    String name = Point.class.getName();
    String opath = "ser\\" + name + "-V" + version + "-" +now() + ".ser";
    Path f = Paths.get(opath);
    OutputStream os = null;
    ObjectOutputStream oos = null;
    try {         
      os = Files.newOutputStream(f);
      oos = new ObjectOutputStream(os);
      oos.writeObject(p);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (oos != null)
          oos.close();
      } catch (IOException e) {}
      try {
        if (os != null)
          os.close();
      } catch (IOException e) {}
    }
    return f.toString();
  }

  public static Object deSerialize(String f) {
    Object x = null;
    InputStream is = null;
    ObjectInputStream ois = null;
    try {         
      is = Files.newInputStream(Paths.get(f));
      ois = new ObjectInputStream(is);
      x = ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (ois != null)
          ois.close();
      } catch (IOException e) {}
      try {
        if (is != null)
          is.close();
      } catch (IOException e) {}
    }
    return x;
  }

  public static void main(String[] args) {

    // this program requires a directory for serialization output
    checkOutputDir();
    
    // serialize an array of Points
    Point[] pa = new Point[]{new Point(1,2),new Point(3,4),new Point(5,6)};
    String f = serializeArrayOfPoints(pa);
    Point[] paClone = (Point[]) deSerialize(f);
    System.out.println(Arrays.toString(paClone));
    // [Point[x=1.0, y=2.0], Point[x=3.0, y=4.0], Point[x=5.0, y=6.0]]
    System.out.println("f="+f);
    // f=ser\ch09.io.PointArray-V1-20160223210200406.ser
    
    // serialize a single Point (for testing in Ch0915SerialVersion)
    Point p = new Point(3,4);
    String f1 = serializePoint(p);
    System.out.println(f1);
    // f=ser\ch09.io.Point-V1-20160223225400068.ser

  }

}
