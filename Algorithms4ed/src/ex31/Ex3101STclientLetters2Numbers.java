package ex31;

import java.util.Scanner;

import edu.princeton.cs.algs4.ST;

/* p389
  3.1.1  Write a client that creates a symbol table mapping letter grades
  to numerical scores, as in the table below, then reads from standard input
  a list of letter grades and computes and prints the GPA (the average of 
  the numbers corresponding to the grades).
    A+    A     A-    B+    B     B-    C+    C     C-    D     F
    4.33  4.00  3.67  3.33  3.00  2.67  2.33  2.00  1.67  1.00  0.00
    
  A solution to this is at http://algs4.cs.princeton.edu/31elementary/GPA.java.html
  (in this project at st.GPA) revealing it's ok to use edu.princeton.cs.algs4.ST 
  that "cheats" by using java.util.TreeMap, because "anyone" could do it with a TreeMap 
  or other Java Map and maps aren't introduced until section 3.4 in the text.
  
 */

public class Ex3101STclientLetters2Numbers {
  
  public static void STclient() {
    ST<String,Double> st = new ST<>();
    String[] lg = "A+ A A- B+ B B- C+ C C- D F".split("\\s+");
    String[] ng = "4.33 4.00 3.67 3.33 3.00 2.67 2.33 2.00 1.67 1.00 0.00".split("\\s+");
    assert lg.length == ng.length;
    for (int i = 0; i < lg.length; i++) st.put(lg[i], Double.parseDouble(ng[i]));
    Scanner sc = new Scanner(System.in); int c = 0; double sum = 0; String g;
    while (sc.hasNext()) {
      g = sc.next();
      if (st.contains(g)) { sum += st.get(g); c++; }      
    }
    sc.close();
    System.out.println("GPA = "+(sum/c));
  }
 
    public static void main(String[] args) {
      
      STclient();  // input: A+ A A- B+ B B- C+ C C- D F
                   // output: GPA = 2.5454545454545454
  }
  
}
