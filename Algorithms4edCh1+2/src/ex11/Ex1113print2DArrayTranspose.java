package ex11;

//  1.1.13  Write a code fragment to print the transposition (rows and columns changed)
//  of a two-dimensional array with M rows and N columns.

public class Ex1113print2DArrayTranspose {
  
  public static void printTransposition(int[][] m) {
    int n = m.length;
    int o = m[0].length;
    int max = 0;
    int len = 0;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
         len = (""+m[i][j]).length();
         if (len > max) max = len;    
      }
    }
    max = max+1;
    for (int j = 0; j < o; j++) { // col
      for (int i = 0; i < n; i++) { // row
         System.out.printf("%-"+max+"d", m[i][j]);       
      }
      System.out.println();
    }
  }
  
  public static void print2DArray(int[][] m) {
    int n = m.length;
    int o = m[0].length;
    int max = 0;
    int len = 0;
    for (int i = 0; i < n; i++) { // row
      for (int j = 0; j < o; j++) { // col
         len = (""+m[i][j]).length();
         if (len > max) max = len;    
      }
    }
    max = max+1;
    for (int i = 0; i < n; i++) { // row
      for (int j = 0; j < o; j++) { // col
         System.out.printf("%-"+max+"d", m[i][j]);       
      }
      System.out.println();
    }
  }

  public static void main(String[] args) {
    
    int[][] a = new int[5][3];
    a[0] = new int[]{1,2,3};
    a[1] = new int[]{4,5,6};
    a[2] = new int[]{7,8,9};
    a[3] = new int[]{10,11,12};
    a[4] = new int[]{13,14,15};
    
    print2DArray(a);
    //  1  2  3  
    //  4  5  6  
    //  7  8  9  
    //  10 11 12 
    //  13 14 15 
    
    System.out.println();
    printTransposition(a);
    //  1  4  7  10 13 
    //  2  5  8  11 14 
    //  3  6  9  12 15 
  

  }

 

}
