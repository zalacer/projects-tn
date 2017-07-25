package ex35;

import static analysis.Log.lg;

import st.STint;
import st.STdouble;
import st.STintdouble;
import st.SparseVectorintdouble;

/* p507
  3.5.5  Develop classes STint and STdouble for maintaining ordered 
  symbol tables where keys are primitive  int and  double types, 
  respectively. (Convert generics  to primitive types in the code of  
  RedBlackBST.) Test your solution with a version of SparseVector as 
  a client.
  
  The following classes were implemented:
  st.STint                  : int keys and int values
  st.STdouble               : double keys and double values
  st.STintdouble            : int keys and double values
  st.SparseVectorintdouble  : internal vector type st.STintdouble
  
  These classes are tested below.
  
*/

public class Ex3505DevelopSTintAndSTdoubleBasedOnRedBlackBST {
  
  public static void testSTint() {
    System.out.println("testing STint:");
    int[] u = new int[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    int[] v = new int[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    STint st = new STint(u,v);
    System.out.println("size = "+st.size());
    System.out.println("height = "+st.height());
    st.printTree();
    System.out.println("levels:");
    st.printLevels();
    System.out.println("paths:");
    st.printPaths();
    System.out.print("pathLengths map: ");
    STint map = st.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(st.size()));
    System.out.println();
  }
  
  public static void testSTdouble() {
    System.out.println("testing STdouble:");
    double[] u = new double[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    double[] v = new double[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    STdouble st = new STdouble(u,v);
    System.out.println("size = "+st.size());
    System.out.println("height = "+st.height());
    st.printTree();
    System.out.println("levels:");
    st.printLevels();
    System.out.println("paths:");
    st.printPaths();
    System.out.print("pathLengths map: ");
    STint map = st.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(st.size()));
    System.out.println();
  }
  
  public static void testSTintdouble() {
    System.out.println("testing STintdouble:");
    int[] u = new int[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    double[] v = new double[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    STintdouble st = new STintdouble(u,v);
    System.out.println("size = "+st.size());
    System.out.println("height = "+st.height());
    st.printTree();
    System.out.println("levels:");
    st.printLevels();
    System.out.println("paths:");
    st.printPaths();
    System.out.print("pathLengths map: ");
    STint map = st.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(st.size()));
    System.out.println();
  }
  
  public static void testSparseVectorintdouble() {
    System.out.println("testing SparseVectorintdouble:");
    SparseVectorintdouble a = new SparseVectorintdouble(10);
    SparseVectorintdouble b = new SparseVectorintdouble(10);
    a.put(3, 0.50);
    a.put(9, 0.75);
    a.put(6, 0.11);
    a.put(6, 0.00);
    b.put(3, 0.60);
    b.put(4, 0.90);
    System.out.println("a = " + a);
    System.out.println("b = " + b);
    System.out.println("a dot b = " + a.dot(b));
    System.out.println("a + b   = " + a.plus(b));
    System.out.println();
  }
  
  public static void main(String[] args) {
    
    testSTint();
    testSTdouble();
    testSTintdouble();
    testSparseVectorintdouble();

/*
    testing STint:
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
    
    testing STdouble:
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
    
    testing STintdouble:
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
    
    testing SparseVectorintdouble:
    a = (3, 0.5) (9, 0.75) 
    b = (3, 0.6) (4, 0.9) 
    a dot b = 0.3
    a + b   = (3, 1.1) (4, 0.9) (9, 0.75) 
*/

  }

}

