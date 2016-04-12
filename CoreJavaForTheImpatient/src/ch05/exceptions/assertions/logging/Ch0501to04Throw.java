package ch05.exceptions.assertions.logging;

import static utils.StackTraceUtils.shortenedStackTrace;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

// 1. Write a method public ArrayList<Double> readValues(String
// filename) throws… that reads a file containing floating-point numbers. Throw
// appropriate exceptions if the file could not be opened or if some of the inputs are not
// floating-point numbers.

// 2. Write a method public double sumOfValues(String filename)
// throws… that calls the preceding method and returns the sum of the values in the
// file. Propagate any exceptions to the caller.

// 3. Write a program that calls the preceding method and prints the result. Catch the
// exceptions and provide feedback to the user about any error conditions.

// 4. Repeat the preceding exercise, but don’t use exceptions. Instead, have
// readValues and sumOfValues return error codes of some kind.

public class Ch0501to04Throw {

  // throw exceptions
  public static ArrayList<Double> readValues(String filename) 
      throws IOException, NumberFormatException {

    ArrayList<Double> a = new ArrayList<Double>();

    BufferedReader br = Files.newBufferedReader(Paths.get(filename));
    br.lines().map(x -> Arrays.stream(x.split("\\s+")))
        .forEach(y -> y.forEach(z -> a.add(Double.parseDouble(z))));

    return a;
  }

  // propagate thrown exceptions
  public static double sumOfValues(String filename) 
      throws NumberFormatException, IOException {

    ArrayList<Double> a = readValues(filename);
    double r = a.stream().mapToDouble(Double::doubleValue).sum();
    return r;

  }

  // catch exceptions, one custom error message
  public static ArrayList<Double> readValues2(String filename) {

    ArrayList<Double> a = new ArrayList<Double>();

    BufferedReader br = null;
    try {
      br = Files.newBufferedReader(Paths.get(filename));
    } catch (IOException e) {
      e.getMessage();
    }
    br.lines().map(x -> Arrays.stream(x.split("\\s+")))
        .forEach(y -> y.forEach(z -> {
      try {
        a.add(Double.parseDouble(z));
      } catch (NumberFormatException e) {
        System.err.println("cannot convert " + z + " to double");
        // e.printStackTrace();
      }
    }));

    return a;

  }

  public static double sumOfValues2(String filename) {

    ArrayList<Double> a = readValues2(filename);
    double r = a.stream().mapToDouble(Double::doubleValue).sum();
    return r;

  }

  // catch exceptions, 1 line/error message + custom exit codes on errors
  public static ArrayList<Double> readValues3(String filename) {

    ArrayList<Double> a = new ArrayList<Double>();

    BufferedReader br = null;
    try {
      br = Files.newBufferedReader(Paths.get(filename));
    } catch (IOException e) {
      System.err.println(shortenedStackTrace(e, 1));
      System.exit(1);
    }

    try {
      br.lines().map(x -> Arrays.stream(x.split("\\s+")))
      .forEach(y -> y.forEach(z -> a.add(Double.parseDouble(z))));
    } catch (NumberFormatException e) {
      System.err.println(shortenedStackTrace(e, 1));
      System.exit(1);
    }

    return a;

  }

  // 
  public static double sumOfValues3(String filename) {

    ArrayList<Double> a = readValues3(filename);
    double r = a.stream().mapToDouble(Double::doubleValue).sum();
    return r;

  }

  // catch exceptions and shorten stack traces
  public static Object readValues4(String filename) {

    ArrayList<Double> a = new ArrayList<Double>();

    BufferedReader br = null;
    try {
      br = Files.newBufferedReader(Paths.get(filename));
    } catch (IOException e) {
      return shortenedStackTrace(e, 1);
    }

    try {
      br.lines().map(x -> Arrays.stream(x.split("\\s+")))
      .forEach(y -> y.forEach(z -> a.add(Double.parseDouble(z))));
    } catch (NumberFormatException e) {
      return shortenedStackTrace(e, 1);
    }

    return a;

  }

  // return custom warnings 
  public static Object sumOfValues4(String filename) {

    Object o = readValues4(filename);
    if (o instanceof ArrayList<?> ) {
      @SuppressWarnings("unchecked")
      ArrayList<Object> a = (ArrayList<Object>) o;
      if (a.size() == 0) return 0;
      Object e = a.get(0);
      String can = e.getClass().getCanonicalName();
      if (can.equals("java.lang.Double")) {
        @SuppressWarnings("unchecked")
        ArrayList<Double> b = (ArrayList<Double>) o;
        return b.stream().mapToDouble(Double::doubleValue).sum();
      } else {
        return "readValues4 returned unexpected ArrayList of "+can;
      }
    } else if (o instanceof String) {
      return o;
    } else {
      return "readValues4 returned unexpected Object of type "+ o.getClass().getCanonicalName();
    }

  }

  public static void main(String[] args) {

    String f = "Ch0501ThrowData"; // this file is in the project

    ArrayList<Double> a = null;
    try {
      a = readValues(f);
    } catch (NumberFormatException | IOException e1) {
      e1.printStackTrace();
    }
    if (a != null)
      System.out.println(a);
    // [1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]

    try {
      System.out.println(sumOfValues(f)); // 45.0
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }
    // 45, returns 0 if f is empty

    // demo of catching NumberFormatException
    String s = "hello";
    Double d = null;
    try {
      d = Double.parseDouble(s);
    } catch (NumberFormatException e) {
      System.out.println("caught NumberFormatException");
    } // caught NumberFormatException

    assert(!(d instanceof Double));

    System.out.println("\n" + sumOfValues2(f)); // 45.0

    System.out.println("\n" + sumOfValues3(f)); // 45.0

    Object o = sumOfValues4(f);
    double result;
    if (o instanceof Double) {
      result = (Double) o;
      System.out.println("result: "+result);
    } else {
      System.out.println("error: "+o);  
    }
    // result: 45.0

  }

}
