package ch02.ooprogramming;

// 14. Compile the Network class. Note that the inner class file is named
// Network$Member.class. Use the javap program to spy on the generated
// code. The command javap -private Classname displays the methods and 
// instance variables. Where do you see the reference to the enclosing 
// class? (In Linux/Mac OS X, you need to put a \ before the $ symbol
// when running javap.)

// $ javap -private Network$Member
// Compiled from "Network.java"
// public class Network$Member {
//   private java.lang.String name;
//   private java.util.ArrayList<Network$Member> friends;
//   final Network this$0; // <== here is the reference to the enclosing class
//   public Network$Member(Network, java.lang.String);
//   public void leave();
//   public void addFriend(Network$Member);
//   public boolean belongsTo(Network);
//   public java.lang.String toString();

// $ javap -private Network
// Compiled from "Network.java"
// public class Network {
//   private java.util.ArrayList<Network$Member> members;
//   public Network();
//   public Network$Member enroll(java.lang.String);
//   public java.lang.String toString();
//   static java.util.ArrayList access$000(Network);
//}

public class Ch0214javap {

}
