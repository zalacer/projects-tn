package ds;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/25applications/Domain.java
 *  Compilation:  javac Domain.java
 *  Execution:    java Domain < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  
 *  Sort by reverse domain name.
 * 
 *  % java Domain < domains.txt
 *  com.apple
 *  com.cnn
 *  com.google
 *  edu.princeton
 *  edu.princeton.cs
 *  edu.princeton.cs.bolle
 *  edu.princeton.cs.www
 *  edu.princeton.ee
 *
 ******************************************************************************/
//import static v.ArrayUtils.*;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdIn;

public class Domain implements Comparable<Domain> {
  private final String[] fields;
  private final int n;

  // store fields in reverse order
  public Domain(String name) {
    fields = name.split("\\.");
    n = fields.length;
  }

  // return string representation - fields, delimited by .
  @Override
  public String toString() {
    if (n == 0) return "";
    String s = fields[0];
    for (int i = 1; i < n; i++)
      s = fields[i] + "." + s;
    return s;
  }
  
  public String[] getFields() {
    return fields;
  }

  // compare by reverse domain name
  public int compareTo(Domain that) {
    for (int i = 0; i < Math.min(this.n, that.n); i++) {
      String s = this.fields[this.n - i - 1];
      String t = that.fields[that.n - i - 1];
      int c = s.compareTo(t);
      if      (c < 0) return -1;
      else if (c > 0) return +1;
    }
    return this.n - that.n;
  }
  
  // test client
  public static void main(String[] args) {

    // read in domain names
    String[] names = StdIn.readAllStrings();
    Domain[] domains = new Domain[names.length];
    for (int i = 0; i < domains.length; i++) {
      domains[i] = new Domain(names[i]);
    }

    // sort
    Arrays.sort(domains);

    // print results
    for (int i = 0; i < domains.length; i++) {
      System.out.println(domains[i]);
    }
  }

}

/*
apple.com
cnn.com
google.com
princeton.edu
cs.princeton.edu
bolle.cs.princeton.edu
www.cs.princeton.edu
ee.princeton.edu

*/
