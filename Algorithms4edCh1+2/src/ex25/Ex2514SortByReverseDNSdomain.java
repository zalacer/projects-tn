package ex25;

import static v.ArrayUtils.*;
import java.util.Arrays;
import ds.Domain;

/* p355
  2.5.14 Sort by reverse domain. Write a data type Domain that represents
  domain names,including an appropriate compareTo() method where the natural 
  order is in order of the reverse domain name. For example, the reverse 
  domain of cs.princeton.edu is edu.princeton.cs. This is useful for weblog 
  analysis. Hint: Use s.split("\\.") to split the string s into tokens, 
  delimited by dots. Write a client that reads domain names  from standard 
  input and prints the reverse domains in sorted order.
  
  A solution to this is at http://algs4.cs.princeton.edu/25applications/Domain.java
  and locally at ds.Domain. A demo of it is given below.
  
 */

public class Ex2514SortByReverseDNSdomain {

  public static void main(String[] args) {
    
    String[] s = "abc.edu pqr.org xyz.com".split("\\s+");
    Domain[] d = ofDim(Domain.class,s.length);
    for (int i = 0; i < s.length; i++) d[i] = new Domain(s[i]);
    Arrays.sort(d);
    show(d); // com.xyz edu.abc org.pqr 

  }

}


