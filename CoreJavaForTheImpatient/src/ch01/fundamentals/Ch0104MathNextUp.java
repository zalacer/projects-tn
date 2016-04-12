package ch01.fundamentals;

// 4. Write a program that prints the smallest and largest positive double value. Hint:
// Look up Math.nextUp in the Java API.

public class Ch0104MathNextUp {

  public static void main(String[] args) {
    
    double smallestPositiveDouble = Math.nextUp(0.0);
    System.out.println("smallestPositiveDouble= "+smallestPositiveDouble); // 4.9E-324

    double largestPositiveDouble = Double.MAX_VALUE;
    System.out.println("largestPositiveDouble = "+largestPositiveDouble); // 1.7976931348623157E308

  }

}
