package ch09.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListSet;

import horstmann.ch09.sec05.Serialization.LabeledPoint;
import javafx.geometry.Point2D;

// 13. Implement a method that can produce a clone of any serializable
//  object byserializing it into a byte array and deserializing it.

public class Ch0913SerialClone {

  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T clone(T t) {
    // serialization to byte[]
    Object x = null;
    byte[] oBytes = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try {
      out = new ObjectOutputStream(bos); // implements ObjectOutput
      out.writeObject(t); 
      oBytes = bos.toByteArray(); // t now serialized to a byte array 
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ex) {}
      try {
          bos.close();
      } catch (IOException ex) {}
    }
    // deserialization from byte[]
    ByteArrayInputStream bis = new ByteArrayInputStream(oBytes);
    ObjectInput in = null;
    try {
      in = new ObjectInputStream(bis); // implements ObjectInput
      x = in.readObject(); // oBytes now deserialized to x
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {}
      try {
        bis.close();
      } catch (IOException ex) {}
    }
    // return cloned object
    return (T) x;
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {

    LabeledPoint lp1 = new LabeledPoint("red", new Point2D(1.0,3.0));
    LabeledPoint lp1clone = clone(lp1);

    assert lp1clone.equals(lp1);
    System.out.println(lp1.hashCode());                  // 112816
    System.out.println(lp1clone.hashCode());             // 112816
    System.out.println(lp1.getLabel());                  // red
    System.out.println(lp1clone.getLabel());             // red
    System.out.println(lp1.getPoint());                  // Point2D [x = 1.0, y = 3.0]
    System.out.println(lp1clone.getPoint());             // Point2D [x = 1.0, y = 3.0]
    
    // however
    assert lp1clone != lp1;
    System.out.println(System.identityHashCode(lp1));         // 589431969
    System.out.println(System.identityHashCode(lp1clone));    // 460141958
    
    // Even though it uses ObjectInputStream and ObjectOutputStream, my clone method
    // doesn't work for all objects of classes marked serializable  under all conditions. 
    // For example ConcurrentSkipListSet is marked serializable and  my clone methods 
    // works ok for it except when it's initialized with a comparator in which case it 
    // crashes on a java.io.NotSerializableException
    ConcurrentSkipListSet<String> css = new ConcurrentSkipListSet<>((x,y)-> y.compareTo(x));
    css.add("one"); css.add("two"); css.add("three");
    System.out.println("css="+css); // css=[two, three, one]
    @SuppressWarnings("unused")
    ConcurrentSkipListSet<String> cssClone = clone(css); 
    // produces as java.io.NotSerializableException

  }

}
