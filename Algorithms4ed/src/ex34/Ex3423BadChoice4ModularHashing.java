package ex34;

import static utils.RandomUtils.randomString;

import java.security.SecureRandom;

/* p482
  3.4.23  Consider modular hashing for string keys with R = 256 and
  M = 255. Show that this is a bad choice because any permutation of 
  letters within a string hashes to the same value.
  
  This appears to reference the formula
    int hash = 0;
    for (int i = 0; i < s.length(); i++) 
      hash = (R * hash + s.charAt(i)) % M;
  for hashing a string key from page 460 of the text.
  
  Considering this algorithm, suppose R = M + 1 then it's equivilant to
    (s.charAt(0) + s.charAt(1) + ... + s.charAt(s.length()-1) % M
  and since addition is commutative, every permutation of
    [s.charAt(0), s.charAt(1), ..., s.charAt(s.length()-1)]
  gives the same result.
  
  Proof of equivilance:
  
  1. The fundamental basis of the proof is:
     1.1: a == x%n && b == y%n => (x+y)%n == (a+b)%n
     1.2:                      => (x*y)%n == (a*b)%n
     1.3:                      => (x+y)%n == (a+y)%n
     1.4:                      => (x+y)%n == (x+b)%n
     1.5:                      => (x*y)%n == (a*y)%n 
     1.6:                      => (x*y)%n == (x*b)%n
     Reference: Elements of Number Theory; Stillwell; Springer; 2003.

     These relations are testable using the method given below named
       modulusCongruenceRespectsAdditionAndMultiplication().
       
  2. ((n+1) * (x%n) + y) % n == ((n * x%n + x%n + y) % n by expansion of (n+1) * (x%n)
                             == (x%n + y) % n by 1.1 since (n * x%n) % n == 0
                             == (x + y) % n by 1.3
                             
  3. Additional terms (characters) are added by the same mechanism as in (2) with
     (x+y) substituted for x and z substituted for y, and so on, for example:
     ((n+1) * ((x+y)%n) + z) % n == ((x + y) + z) = (x + y + z) using arithmetic associativity
  
  The equivilance is testable using equivilanceOfHashMethods() given below.
    
*/

public class Ex3423BadChoice4ModularHashing {
  
  public static void modulusCongruenceRespectsAdditionAndMultiplication(int trials) {
    // demonstrate rules 1 and 2 and corollaries 1-4 using random ints.
    if (trials < 0) throw new IllegalArgumentException(
        "modulusCongruenceRespectsAdditionAndMultiplication: trials is < 0"); 
    SecureRandom  r = new SecureRandom(); int x, y, n, c = 0;
    while (c < trials) {
      x = r.nextInt(((int)Math.sqrt(Integer.MAX_VALUE)));
      y = r.nextInt(((int)Math.sqrt(Integer.MAX_VALUE)));
      n = r.nextInt(((int)Math.sqrt(Integer.MAX_VALUE)))+1;
      int a = x%n;
      int b = y%n;
      assert (x+y)%n == (a+b)%n;
      assert (x*y)%n == (a*b)%n;
      assert (x+y)%n == (a+y)%n;
      assert (x+y)%n == (x+b)%n;
      assert (x*y)%n == (a*y)%n;
      assert (x*y)%n == (x*b)%n;
      c++;
    }
  }
  
  public static void equivilanceOfHashMethods(int len, int trials) {
    if (trials < 0) throw new IllegalArgumentException(
        "equivilanceOfHashMethods: trials is < 0");
    // len is the exclusive upper bound on string length.
    SecureRandom  r = new SecureRandom(); int M, c = 0; String s;
    while (c < trials) {
      M = r.nextInt(((int)Math.sqrt(Integer.MAX_VALUE)))+1;
      s = randomString(r.nextInt(len));
      assert hash(s, M+1, M) == hash2(s, M);
      c++;
    }
  }
  
  public static int hash(String s, int R, int M) {
    // compute hashcode of s using the code given on p460 of the text.
    if (M <= 0) throw new IllegalArgumentException("hash: M is <=0");
    if (s == null) return 0;
    int hash = 0;
    for (int i = 0; i < s.length(); i++) {
      hash = (R * hash + s.charAt(i)) % M;
    }
    return hash;
  }
  
  public static int hash2(String s, int M) {
    // compute hashcode of s as the sum of its character values as ints mod M.
    if (M <= 0) throw new IllegalArgumentException("hash2: M is <=0");
    if (s == null) return 0;
    int sum = 0;
    for (int i = 0; i < s.length(); i++) sum += s.charAt(i);
    return sum % M;
  }
  
  public static void main(String[] args) {
    
      modulusCongruenceRespectsAdditionAndMultiplication(1000000);
      
      equivilanceOfHashMethods(1000,1000);
    
  }

}

