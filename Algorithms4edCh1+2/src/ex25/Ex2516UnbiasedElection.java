package ex25;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/* p355
  2.5.16 Unbiased election. In order to thwart bias against candidates 
  whose names appear toward the end of the alphabet, California sorted 
  the candidates appearing on its 2003 gubernatorial ballot by using the 
  following order of characters:
      R W Q O J M V A H B S G Z X N T C I E K U P D Y F L
  Create a data type where this is the natural order and write a client  
  California with a single static method main() that sorts strings accord-
  ing to this ordering. Assume that each string is composed solely of 
  uppercase letters.

  This is done with the Strange class below that includes a sorting demo
  in main. Since there is no technical need to create another class for this
  demo and I want all of this exercise to be in the fewest number of files, 
  a separate class named California hasn't been implemented.

 */

public class Ex2516UnbiasedElection {

  //  Index values refer to {@code char} code units, so a supplementary
  //  character uses two positions in a {@code String}.

  public static class Strange implements Comparable<Strange>{
    // based on java.lang.String
    private final char value[];
    private Map<Character,Integer> map;

    public Strange() { this.value = new char[0];  map(); }

    public Strange(String s) {
      if (!s.matches("[a-zA-Z]+")) throw new IllegalArgumentException("Strange: string "
          + "argument must contain only alphabetic characters");
      value = s.toUpperCase().toCharArray(); map();
    }

    public Strange(char[] a) {
      String s = new String(a);
      if (!s.matches("\\p{Alpha}")) throw new IllegalArgumentException("Strange: char[a] "
          + "argument must contain only alphabetic characters");
      value = s.toUpperCase().toCharArray(); map();
    }

    public static void Sort(Strange[] z) { Arrays.sort(z); }

    private void map() {
      final String s = "R W Q O J M V A H B S G Z X N T C I E K U P D Y F L";
      map = new HashMap<>(); int c = 0;
      for (int i = 0; i < s.length(); i++) if (i%2==0) map.put(s.charAt(i), c++);
    }

    public int compareTo(Strange x) {
      int len1 = value.length;
      int len2 = x.value.length;
      int lim = Math.min(len1, len2);
      char v1[] = value;
      char v2[] = x.value;
      int i1, i2, k = 0;
      while (k < lim) {
        i1 = map.get(v1[k]);
        i2 = map.get(v2[k]);
        if (i1 != i2) return i1 - i2;
        k++;
      }
      return len1 - len2;
    }

    public Strange append(Strange that) {
      return new Strange(toString()+that.toString());   
    }

    public char[] getValue() { return value; }

    public Map<Character,Integer> getMap() { return map; }

    @Override
    public String toString() { return new String(value); }

    @Override
    public int hashCode() { return Objects.hash(value, map); }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Strange other = (Strange) obj;
      if (map == null) { if (other.map != null) return false; } 
      else if (!map.equals(other.map)) return false;
      if (!Arrays.equals(value, other.value)) return false;
      return true;
    }
  }

  public static void main(String[] args) {

    SecureRandom  r = new SecureRandom();
    String[] s = "ONE TWO THREE FOUR FIVE SIX SEVEN EIGHT NINE".split("\\s+");
    shuffle(s,r);
    String[] t = s.clone();
    Strange[] u = ofDim(Strange.class, t.length);
    for (int i = 0; i < t.length; i++) u[i] = new Strange(t[i]);
    Arrays.sort(s); show(s); // EIGHT FIVE FOUR NINE ONE SEVEN SIX THREE TWO 
    Arrays.sort(u); show(u); // ONE SIX SEVEN NINE TWO THREE EIGHT FOUR FIVE

  }

}


