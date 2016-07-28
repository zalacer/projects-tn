package ex12;


//  1.2.6  A string  s is a circular rotation of a string  t if it matches when the characters
//  are circularly shifted by any number of positions; e.g.,  ACTGACG is a circular shift of
//  TGACGAC , and vice versa. Detecting this condition is important in the study of genomic
//  sequences. Write a program that checks whether two given strings  s and  t are circular
//  shifts of one another. Hint : The solution is a one-liner with  indexOf() ,  length() , and
//  string concatenation.

public class Ex1206CircularStringRotation {
  
  public static boolean isCircularRotation(String a, String b) {
    // if a is a circular rotation of b return true else return false
    // this relation is symmetric between a and b
    String test = b+b;
    if (test.indexOf(a) == -1) return false;
    return true;
  }

  public static void main(String[] args) {
    String a = "happening"; String b = "inghappen";
    System.out.println(isCircularRotation(a,b)); //true
    System.out.println(isCircularRotation(b,a)); //true
    

  }

}
