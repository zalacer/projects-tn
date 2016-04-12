package ch01.fundamentals;

// 9. Section 1.5.3, “String Comparison,” on p. 21 has an example of two strings s and t
// so that s.equals(t) but s != t. Come up with a different example that doesn’t
// use substring).

public class Ch0109StringIdentity {

  public static void main(String[] args) {

    String s =  "jello";

    String k = new String("jello");
    assert k != s;
    assert k.equals(s);

    char[] c = new char[5];
    c[0] = 'j'; c[1] = 'e'; c[2] = 'l'; c[3] = 'l'; c[4] = 'o';
    String j = new String(c);
    assert j != s;
    assert j.equals(s);

  }

}
