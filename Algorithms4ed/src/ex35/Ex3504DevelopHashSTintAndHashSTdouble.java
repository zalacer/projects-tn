package ex35;

import java.security.SecureRandom;
import st.HashSTint;
import st.HashSTdouble;

/* p507
  3.5.4  Develop classes HashSTint and HashSTdouble for maintaining 
  sets of keys of primitive int and double types, respectively. 
  (Convert generics to primitives in LinearProbingHashST.)
 
  These are implemented as st.HashSTint and st.HashSTdouble. The main
  tricks used were to replace null with NULL=Integer.MIN_VALUE in HashSTint
  and NULL = Double.NEGATIVE_INFINITY in HashSTdouble and to create and
  populate new tables with NULLs using fillInt() and fillDouble() from
  v.ArrayUtils. If these NULL values are inconvenient they can be replaced 
  with setNULL(). Additionally HashSTdouble() was implemented by creating
  a Double and using its hashCode. HashSTint and HashSTdouble are 
  demonstrated below.  
 
*/

public class Ex3504DevelopHashSTintAndHashSTdouble {
  
  public static void main(String[] args) {
    
    SecureRandom r = new SecureRandom();
    r.setSeed(System.currentTimeMillis());
    
    System.out.println("HashSTint demo:");

    HashSTint hi = new HashSTint();
    
    for (int i = 0; i < 100000; i++) r.nextInt();
    
    int c = 0;
    while (c < 1000) {
      hi.put(r.nextInt(1000), r.nextInt(1000)); 
      c++; 
    }
    
    System.out.println("table size = "+hi.getM());
    System.out.println("number of keys = "+hi.size());
    System.out.println("alpha = "+hi.alpha());
    System.out.println("searchHitAvgCost = "+hi.searchHitAvgCost());
    System.out.println("searchMissAvgCost = "+hi.searchMissAvgCost()+"\n");
    
    System.out.println("HashSTdouble demo:");
    
    r.setSeed(System.currentTimeMillis());

    HashSTdouble hd = new HashSTdouble();
    
    for (int i = 0; i < 100000; i++) r.nextDouble();
    
    c = 0;
    while (c < 1000) {
      hd.put(r.nextDouble()*1000, r.nextDouble()*1000); 
      c++;
    }
    
    System.out.println("table size = "+hd.getM());
    System.out.println("number of keys = "+hd.size());
    System.out.println("alpha = "+hd.alpha());
    System.out.println("searchHitAvgCost = "+hd.searchHitAvgCost());
    System.out.println("searchMissAvgCost = "+hd.searchMissAvgCost());
    
/*
    HashSTint demo:
    table size = 2048
    number of keys = 630
    alpha = 0.3076
    searchHitAvgCost = 1.0
    searchMissAvgCost = 2.40869140625
    
    HashSTdouble demo:
    table size = 2048
    number of keys = 1000
    alpha = 0.4883
    searchHitAvgCost = 1.4353876739562623
    searchMissAvgCost = 2.84716796875

*/

  }

}

