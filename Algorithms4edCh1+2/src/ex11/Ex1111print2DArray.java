package ex11;

import java.util.Arrays;

//  1.1.11  Write a code fragment that prints the contents of a two-dimensional boolean
//  array, using  * to represent  true and a space to represent  false . Include row and
// column numbers.

public class Ex1111print2DArray {
  
  public static void printBooleanArray(boolean[][] b) {
    int r = b.length;
    String rs = "" +r;
    int relen = rs.length(); // left indent width
    int c = 0;
    String cs = null;
    int celen = 0;
    if (r > 0) {
      c = b[0].length;
      cs = "" + c;
      celen = cs.length(); // col width
    }
    if (r == 0 && c == 0) {
      System.out.println("empty array");
      return;
    }
    System.out.printf(space(relen));
    for (int j = 0; j < c; j++) {
      System.out.printf(" %"+celen+"d", j);
    }
    System.out.println();
    for (int i = 0; i < r; i++) {
      System.out.printf("%"+relen+"d", i);
      for (int j = 0; j < c; j++) {
        if (b[i][j] == true) {
          System.out.printf(" %"+celen+"s", "*");
        } else {
          System.out.printf(" %"+celen+"s", " ");
        }
      }
      System.out.println();
    }
    System.out.println();
  }
  
  public static final String space(int length) {
    // create a new String consisting of a space repeated length times
    char[] data = new char[length];
    Arrays.fill(data, ' ');
    return new String(data);
  }

  public static void main(String[] args) {
    
    boolean[][] empty = new boolean[0][0];
    
    printBooleanArray(empty); 
    // empty array
    
    boolean[][] onerow = new boolean[1][0];
    onerow[0] = new boolean[]{true,false,true};
        
    System.out.println();
    printBooleanArray(onerow); 
    //    0 1 2
    //  0 *   *
    
    boolean[][] onecol = new boolean[3][1];
    onecol[0][0] = true;
    onecol[1][0] = false;
    onecol[2][0] = true;
    System.out.println();
    printBooleanArray(onecol);
    //    0
    //  0 *
    //  1  
    //  2 *
    
    boolean[][] b = new boolean[5][5];
    b[0] = new boolean[]{true,false,true,false,true};
    b[1] = new boolean[]{false,true,false,true,false};
    b[2] = new boolean[]{true,false,true,false,true};
    b[3] = new boolean[]{false,true,false,true,false};
    b[4] = new boolean[]{true,false,true,false,true};

    System.out.println();
    printBooleanArray(b);
    //    0 1 2 3 4
    //  0 *   *   *
    //  1   *   *  
    //  2 *   *   *
    //  3   *   *  
    //  4 *   *   *

    boolean[][] b2 = new boolean[15][15];
    b2[0] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[1] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[2] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[3] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[4] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[5] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[6] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[7] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[8] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[9] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[10] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[11] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[12] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
    b2[13] = new boolean[]{false,true,false,true,false,true,false,true,false,true,false,true,false,true,false};
    b2[14] = new boolean[]{true,false,true,false,true,false,true,false,true,false,true,false,true,false,true};
  
    System.out.println();
    printBooleanArray(b2);
    //      0  1  2  3  4  5  6  7  8  9 10 11 12 13 14
    //   0  *     *     *     *     *     *     *     *
    //   1     *     *     *     *     *     *     *   
    //   2  *     *     *     *     *     *     *     *
    //   3     *     *     *     *     *     *     *   
    //   4  *     *     *     *     *     *     *     *
    //   5     *     *     *     *     *     *     *   
    //   6  *     *     *     *     *     *     *     *
    //   7     *     *     *     *     *     *     *   
    //   8  *     *     *     *     *     *     *     *
    //   9     *     *     *     *     *     *     *   
    //  10  *     *     *     *     *     *     *     *
    //  11     *     *     *     *     *     *     *   
    //  12  *     *     *     *     *     *     *     *
    //  13     *     *     *     *     *     *     *   
    //  14  *     *     *     *     *     *     *     *

    
    
    
  }

 

}
