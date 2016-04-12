package ch02.ooprogramming;

// 12. Make a file HelloWorld.java that declares a class HelloWorld in a package
// ch01.sec01. Put it into some directory, but not in a ch01/sec01 subdirectory.
// From that directory, run javac HelloWorld.java. Do you get a class file?
// Where? Then run java HelloWorld. What happens? Why? (Hint: Run javap
// HelloWorld and study the warning message.) Finally, try javac -d .
// HelloWorld.java. Why is that better?

// Compiled HelloWorld.java as instructed resulting in HelloWorld.class, but trying
// to execute it with java gave "Error: Could not find or load main class HelloWorld".

// Running javap HelloWorld printed the following message:
// Warning: Binary file HelloWorld contains ch01.sec01.HelloWorld
// Compiled from "HelloWorld.java"
// public class ch01.sec01.HelloWorld {
//   public ch01.sec01.HelloWorld();
//   public static void main(java.lang.String[]);

// Compiling HelloWorld.java with javac -d . created ch01\sec01\HelloWorld.class 
// that could then be executed with java ch01.sec01.HelloWorld.  This is useful 
// because its works, instantiates the directory structure corresponding to the required
// class path and makes it clear what command line to use to run the program - with some
// help from information provided by javap.

public class Ch0212CompilationTest {

}
