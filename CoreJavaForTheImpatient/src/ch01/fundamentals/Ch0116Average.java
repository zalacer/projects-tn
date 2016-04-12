package ch01.fundamentals;

// 16. Improve the average method so that it is called with at least one parameter.

public class Ch0116Average {

  public static double average(double d, double...values) {
    // this method requires at least one arg
    double sum = d;
    for (double v : values) sum += v;
    return sum / (values.length + 1);
  }

  public static void main(String[] args) {

    double d = average(1,9,2,8,3,7,4,6);
    System.out.println(d); // 5.0


  }

}
