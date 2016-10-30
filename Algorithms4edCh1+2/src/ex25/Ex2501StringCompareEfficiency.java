package ex25;

import static v.ArrayUtils.ofDim;

import analysis.Timer;
import pq.MaxPQ;

/* p353
  2.5.1  Consider the following implementation of the compareTo() method for  String .
  How does the third line help with efficiency?
  public int compareTo(String that) {
    if (this == that) return 0; // this line
    int n = Math.min(this.length(), that.length());
    for (int i = 0; i < n; i++) {
      if (this.charAt(i) < that.charAt(i)) return -1;
      else if (this.charAt(i) > that.charAt(i)) return +1;
    }
    return this.length() - that.length();
  }
  
  It can help by avoiding execution of lines 4-11 if the strings are identical (have
  the same identityHashcode). However, usually that isn't likely and may known to not 
  be possible, so most often line 3 would add extra overhead.
  
  For example, given a string "jello", if another "jello" is instantiated with new 
  the two aren't identical. Identity happens only when the other is instantiated
  with a string literal or set to the former with =. See demo below.
  
  It's best to avoid the slight extra overhead for the majority and let the rest test 
  for identity if they want and think of it. That's why isn't line 3 isn't in 
  java.lang.String.compare.
  
  Here's the code of java.lang.String.compare from openjdk/8u40-b25: 
  (from http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/lang/String.java#String.compareTo%28java.lang.String%29)  
  
  public int compareTo(String anotherString) {
        int len1 = value.length;
        int len2 = anotherString.value.length;
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;

        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }
  
 */

@SuppressWarnings("unused")
public class Ex2501StringCompareEfficiency {
 
    public static void main(String[] args) {

      String a = "jello" ;
      String b = a;
      String c = "jello";
      String d = new String("jello");
      char[] e = new char[5];
      e[0] = 'j'; e[1] = 'e'; e[2] = 'l'; e[3] = 'l'; e[4] = 'o';
      String f = new String(c);
      assert a == b;
      assert a.equals(b); 
      assert a == c;
      assert a.equals(c);
      assert a != d;
      assert a.equals(d);
      assert a != f;
      assert a.equals(f);

  }
  
}
