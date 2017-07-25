package ex25;

/* p353
  2.5.3  Criticize the following implementation of a class intended to 
  represent account balances. Why is compareTo() a flawed implementation 
  of the Comparable interface?
  
  public class Balance implements Comparable<Balance>
  {
      ...
      private double amount;
      public int compareTo(Balance that) {
        if (this.amount < that.amount - 0.005) return -1;
        if (this.amount > that.amount + 0.005) return +1;
        return 0;
      }
      ...
  }
  Describe a way to fix this problem.
  
  It's a flawed implementation because it does not adhere to the contract of 
  Compare.compareTo which is that it "Returns a negative integer, zero, or a 
  positive integer as this object is less than, equal to, or greater than the 
  specified object." 
  (from https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html#compareTo-T-)
  Also it can let 0.005 slip in comparisons, which could be significant over 
  many transactions and it doesn't distinguish between values that are equal
  but have different implementations at the bit level such as -0.0 and 0.0. 
  A simplistic way to fix it is:
  
    public int compareTo(Balance that) {
      if (this.amount < that.amount) return -1;
      if (this.amount > that.amount) return 1;
      return 0;
    }
    
    However, java.lang.Double compares goes further to compare bits: 
    (from http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/lang/Double.java#Double.compare%28double%2Cdouble%29)
    
    public static int compare(double d1, double d2) {
        if (d1 < d2)
            return -1;           // Neither val is NaN, thisVal is smaller
        if (d1 > d2)
            return 1;            // Neither val is NaN, thisVal is larger

        long thisBits = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);

        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                          // (0.0, -0.0) or (NaN, !NaN)
    }
    
    So a correct way to fix it is to use Double.compareTo which autoboxes the
    doubles:
    
        public int compareTo(Balance that) {
          return Double.compare(this.amount, that.amount);
        }
  
    A demonstration of this version of Balance.compareTo() is given below.
  
 */

public class Ex2503FaultyCompare {

  public static class Balance implements Comparable<Balance> {
    private double amount;
    Balance(double x) { amount = x; }

    public int compareTo(Balance that) {
      return Double.compare(this.amount, that.amount);
    }
  }

  public static void main(String[] args) {

    Balance b1 = new Balance(10.);
    Balance b2 = new Balance(20.);
    Balance b3 = new Balance(30.);
    Balance b4 = new Balance(10.);
    Balance b5 = new Balance(-0.0);
    Balance b6 = new Balance(0.0);
    Balance b7 = new Balance(Double.NaN);
    Balance b8 = new Balance(-Double.NaN);
    Balance b9 = new Balance(0./0); // Double.NaN
    Balance b10 = new Balance(-0./0); // Double.NaN

    assert b1.compareTo(b2) < 0;
    assert b3.compareTo(b1) > 0;
    assert b4.compareTo(b1) == 0;
    assert b5.compareTo(b6) < 0;
    assert b6.compareTo(b5) > 0;
    assert b7.compareTo(b8) == 0;
    assert b8.compareTo(b9) == 0;
    assert b9.compareTo(b10) == 0;
    assert Double.isNaN(b9.amount) == true;
    assert Double.isNaN(b10.amount) == true;

  }

}
