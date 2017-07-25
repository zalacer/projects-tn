package ex42;

import static java.lang.Math.*;

/* p599
  4.2.27 Digraph enumeration. Show that the number of different V-vertex digraphs
  with no parallel edges is 2^(V^2). (How many digraphs are there that contain V 
  vertices and E edges?) Then compute an upper bound on the percentage of 20-vertex 
  digraphs that could ever be examined by any computer, under the assumptions that 
  every electron in the universe examines a digraph every nanosecond, that the uni-
  verse has fewer than 10^80 electrons, and that the age of the universe will be 
  less than 10^20 years.
  
  I don't believe 2^(V^2) for several reasons: (1) It doesn't account for possibly 
  practically uncountale numbers of edges to/from any vertex; (2) It's not complex 
  enough to discount isomorphic graphs; (3) Harary in Graphical Enumeration (1957) 
  relies on Polya's enumeration theorem to produce completely different formula on 
  pp 120-121 and provides no indication that it can generally be reduced.
  
  For a digraph of V vertices, suppose any pair of vertices may have no edge, an 
  edge in either direction or two edges in opposite directions, then an upper bound on 
  the the number of possible distinct graphs is 4^((V*V-1)/2) = 2^(V*V-1) ~ 2^(V^2). 

  percentage = 0.0122 using the 2^(V^2) formula as calculated below.
  
 */  


public class Ex4227DigraphEnumeration {
  
  public static void main(String[] args) {
    
    // from google; number of nanoseconds in a year = 3.154e+16
    double numberOfGraphsExamined = (pow(10,20)-1)*3.154e+16*(pow(10,80)-1);

    double numberOf20Vgraphs = pow(2,pow(20,2));
    
    double percentage = 100.*numberOfGraphsExamined/numberOf20Vgraphs;
    
    System.out.println("percentage = "+percentage);
    // percentage = 0.012214154899434752

  }
    

}



