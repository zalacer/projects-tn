package u;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.ByteArrayInputStream;
import io.ByteArrayOutputStream;

public class JavaPuzzlers {
 
  public static class Dog extends Exception {
    private static final long serialVersionUID = 1L;
    // Exception extends Serializable and and deserialization
    // constitutes a hidden constructor." 
    public static final Dog INSTANCE = new Dog();
    private Dog() { } // does this make Dog a singleton class? no

    public String toString() {
        return "Woof";
    }
  }

  public static Dog copyDog() {
    return (Dog) deepCopy(Dog.INSTANCE); 
  }
  
  public static class DogSingleton extends Exception {
    private static final long serialVersionUID = 1L;
    // Exception extends Serializable and and deserialization
    // constitutes a hidden constructor." 
    public static final DogSingleton INSTANCE = new DogSingleton();
    private DogSingleton() { } // does this make DogSingleton a singleton class? no
    
    private Object readResolve() { // this makes DogSingleton a singleton by forcing
      // Accept no substitutes!    // deserialization to return INSTANCE instead of 
      return INSTANCE;             // a new instance 
    }

    public String toString() {
        return "Woof";
    }
  }
  
  public static DogSingleton copyDogSingleton() {
    return (DogSingleton) deepCopy(DogSingleton.INSTANCE); 
  }
    
  static public Object deepCopy(Object obj) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bin);
      Object obj2 = ois.readObject();
      oos.close(); ois.close();
      return obj2;
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void main(String[] args) {

    Dog newDog = copyDog(); // You figure out what to put here

    // This line should print false
    System.out.println(newDog == Dog.INSTANCE); //false - Dog is not a singleton class

    // This line should print "Woof"
    System.out.println(newDog);
    
    DogSingleton newDogSingleton = copyDogSingleton();

    // This line should print true
    System.out.println(newDogSingleton == DogSingleton.INSTANCE); //true

    // This line should print "Woof"
    System.out.println(newDogSingleton);
    
    
  }

}
