package ex35;

import static analysis.Log.lg;

import st.SETdouble;
import st.SETint;
import st.STint;

/* p507
  3.5.7 Develop classes SETint and SETdouble for maintaining ordered sets 
  of keys of primitive int and double types, respectively. 
  (Eliminate code involving values in your solution to Exercise 3.5.5.)
  
  These are implemented as st.SETint and st.SETdouble. They are demonstrated
  below.
  
*/

public class Ex3507DevelopSETintAndSETdoubleBasedOnSTintAndSTdouble {
  
  public static void testSETint() {
    System.out.println("testing SETint:");
    int[] u = new int[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    SETint set = new SETint(u);
    System.out.println("size = "+set.size());
    System.out.println("height = "+set.height());
    set.printTree();
    System.out.println("levels:");
    set.printLevels();
    System.out.println("paths:");
    set.printPaths();
    System.out.print("pathLengths map: ");
    STint map = set.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(set.size()));
    System.out.println();
  }
  
  public static void testSETdouble() {
    System.out.println("testing SETdouble:");
    double[] u = new double[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    SETdouble set = new SETdouble(u);
    System.out.println("size = "+set.size());
    System.out.println("height = "+set.height());
    set.printTree();
    System.out.println("levels:");
    set.printLevels();
    System.out.println("paths:");
    set.printPaths();
    System.out.print("pathLengths map: ");
    STint map = set.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(set.size()));
    System.out.println();
  }
  
  public static void main(String[] args) {
    
    testSETint();
    testSETdouble();
    
/*
    testing SETint:
    size = 16
    height = 4
    |                     /-----20
    |              /-----15
    |             |       \-----14
    |       /-----13
    |      |      |       /-----12
    |      |       \-----11
    |      |              \-----10
     \-----9
           |              /-----8
           |       /-----7
           |      |       \-----6
            \-----5
                  |       /-----4
                  |      |       \-----3(red)
                   \-----2
                          \-----1
    levels:
    Level 0: 9 
    Level 1: 5 13 
    Level 2: 2 7 11 15 
    Level 3: 1 4 6 8 10 12 14 20 
    Level 4: 3 
    paths:
    9 5 2 1 
    9 5 2 4 3 
    9 5 7 6 
    9 5 7 8 
    9 13 11 10 
    9 13 11 12 
    9 13 15 14 
    9 13 15 20 
    pathLengths map: {5:7,6:1}
    2lgN = 8.0
    
    testing SETdouble:
    size = 16
    height = 4
    |                     /-----20.0
    |              /-----15.0
    |             |       \-----14.0
    |       /-----13.0
    |      |      |       /-----12.0
    |      |       \-----11.0
    |      |              \-----10.0
     \-----9.0
           |              /-----8.0
           |       /-----7.0
           |      |       \-----6.0
            \-----5.0
                  |       /-----4.0
                  |      |       \-----3.0(red)
                   \-----2.0
                          \-----1.0
    levels:
    Level 0: 9.0 
    Level 1: 5.0 13.0 
    Level 2: 2.0 7.0 11.0 15.0 
    Level 3: 1.0 4.0 6.0 8.0 10.0 12.0 14.0 20.0 
    Level 4: 3.0 
    paths:
    9.0 5.0 2.0 1.0 
    9.0 5.0 2.0 4.0 3.0 
    9.0 5.0 7.0 6.0 
    9.0 5.0 7.0 8.0 
    9.0 13.0 11.0 10.0 
    9.0 13.0 11.0 12.0 
    9.0 13.0 15.0 14.0 
    9.0 13.0 15.0 20.0 
    pathLengths map: {5:7,6:1}
    2lgN = 8.0

*/

  }

}

