package ex35;

import java.security.SecureRandom;

import st.HashSETdouble;
import st.HashSETint;

/* p507
  3.5.6  Develop classes HashSETint and HashSETdouble for maintaining 
  sets of keys of primitive int and double types, respectively. 
  (Eliminate code involving values in your solution to Exercise 3.5.4.)
  
  These are implemented as st.HashSETint and st.HashSETdouble. They are
  demonstrated below

*/

public class Ex3506DevelopHashSETintAndHashSETdoubleBasedOnHashSTintAndHashSTdouble {

  public static void main(String[] args) {
    
    SecureRandom r = new SecureRandom();
    r.setSeed(System.currentTimeMillis());
    
    System.out.println("HashSETint demo:");

    HashSETint hi = new HashSETint();
    
    for (int i = 0; i < 100000; i++) r.nextInt();
    
    int c = 0;
    while (c < 1000) {
      hi.add(r.nextInt(1000));
      c++; 
    }
    
    System.out.println("table size = "+hi.getM());
    System.out.println("number of keys = "+hi.size());
    System.out.println("alpha = "+hi.alpha());
    System.out.println("searchHitAvgCost = "+hi.searchHitAvgCost());
    System.out.println("searchMissAvgCost = "+hi.searchMissAvgCost()+"\n");
    
    System.out.println("HashSETdouble demo:");
    
    r.setSeed(System.currentTimeMillis());

    HashSETdouble hd = new HashSETdouble();
    
    for (int i = 0; i < 100000; i++) r.nextDouble();
    
    c = 0;
    while (c < 1000) {
      hd.add(r.nextDouble()*1000); 
      c++;
    }
    
    System.out.println("table size = "+hd.getM());
    System.out.println("number of keys = "+hd.size());
    System.out.println("alpha = "+hd.alpha());
    System.out.println("searchHitAvgCost = "+hd.searchHitAvgCost());
    System.out.println("searchMissAvgCost = "+hd.searchMissAvgCost());
    

/*
    HashSETint demo:
    table size = 2048
    number of keys = 614
    alpha = 0.2998
    searchHitAvgCost = 1.0
    searchMissAvgCost = 2.42626953125
    
    HashSETdouble demo:
    table size = 2048
    number of keys = 1000
    alpha = 0.4883
    searchHitAvgCost = 1.4691848906560636
    searchMissAvgCost = 2.81982421875

*/

  }

}

