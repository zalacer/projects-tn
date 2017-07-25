package analysis;

public class GcdFinder {

  public static void gcd(int a, int b) {
    // from  http://www.oxfordmathcenter.com/drupal7/node/59
    //initialize variables in accordance with the algorithm
    int u1 = 1;
    int u2 = 0;
    int u3 = a;
    int v1 = 0;
    int v2 = 1;
    int v3 = b;
    int q = 0;

    //print the headers for the columns
    System.out.println();
    System.out.println("Calculations:");
    System.out.println();
    System.out.println("u1\tv1\tu2\tv2\tu3\tv3\tq");
    System.out.println("------------------------------------------------------------------");
    
    //while v3 is not zero, calculate and display each row...

    //print the first intermediate steps in the first row
    System.out.println(u1 + "\t" + v1 + "\t" + u2 + "\t" + v2 + "\t" + u3 + 
        "\t" + v3 + "\t" + q);

    //keep printing the intermediate steps in subsequent rows until v3 is zero
    while (v3 != 0) {
      //compute the greatest integer less than or equal to u3/v3
      //(note this is "int division")
      q = u3 / v3;

      int temp;

      //remember the old value of vi, and compute vi = ui - q * vi 
      //for i = 1 to 3 also make new ui equal to the old vi 
      //(note the need for a temp variable here)
      temp = v1;
      v1 = u1 - q * v1;
      u1 = temp;

      temp = v2;
      v2 = u2 - q * v2;
      u2 = temp;

      temp = v3;
      v3 = u3 - q * v3;
      u3 = temp;

      //print the intermediate steps
      System.out.println(u1 + "\t" + v1 + "\t" + u2 + "\t" + v2 + 
          "\t" + u3 + "\t" + v3 + "\t" + q);
    }

    //the gcd ends up being u3, and m is u1
    //n can be computed from gcd = m*a + n*b
    int gcd = u3;
    int m = u1;
    int n = (gcd - m*a) / b;

    //display the results
    System.out.println();
    System.out.println("The gcd of " + a + " and " + b + " is " + gcd + ".");
    System.out.println();
    System.out.println("One linear combination of " + a + 
        " and " + b + " that equals the gcd is given by:");
    System.out.println(gcd + " = " + m + "*" + a + " + " + n + "*" + b);
  }

  public static void main(String[] args) {

    gcd(1239,168);

  }

}
