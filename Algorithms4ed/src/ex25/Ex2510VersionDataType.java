package ex25;

import static v.ArrayUtils.*;

import java.util.Arrays;

/* p353
  2.5.10  Create a data type  Version that represents a software version 
  number, such as 115.1.1,  115.10.1, 115.10.2. Implement the  Comparable 
  interface so that  115.1.1 is less than  115.10.1 , and so forth. 
 */

public class Ex2510VersionDataType {
  
  public static class Version implements Comparable<Version> {
    private int major;
    private int minor;
    private int rev;

    Version(String v) {
      String[] sa = v.split("\\.");
      if (sa.length != 3) throw new IllegalArgumentException(
          "string must have 3 components");
      major = Integer.parseInt(sa[0]);
      minor = Integer.parseInt(sa[1]);
      rev = Integer.parseInt(sa[2]);
    }
    
    @Override
    public int compareTo(Version that) {
      int c1 = this.major - that.major;
      int c2 = this.minor - that.minor;
      int c3 = this.rev - that.rev;
      if (c1 != 0) return c1;
      if (c2 != 0) return c2;
      return c3;
    } 
    
    @Override
    public String toString() {
      return major+"."+minor+"."+rev;
    }
  }
 
  public static void main(String[] args) {
    
    String[] d = "116.0.0 115.11.3 115.11.2 115.11.1 115.10.3 115.10.2 115.10.1".split("\\s+");
    Version[] v = ofDim(Version.class,d.length);
    for (int i = 0; i < d.length; i++) v[i] = new Version(d[i]);
    Arrays.sort(v);
    show(v); // 115.10.1 115.10.2 115.10.3 115.11.1 115.11.2 115.11.3 116.0.0  
    
  }

}

