package ch01.fundamentals;

// 5. What happens when you cast a double to an int that is larger than the largest
// possible int value? Try it out.

public class Ch0105DownCast {

  public static void main(String[] args) {

    double d = Integer.MAX_VALUE * 100. + 571.01;
    System.out.println("d = "+d); // 2.1474836527101E11
    int i = (int) d;
    System.out.println("i = "+i); // 2147483647
    assert i == Integer.MAX_VALUE;

  }

}
