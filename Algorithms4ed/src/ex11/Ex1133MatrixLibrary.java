package ex11;

import static v.ArrayUtils.*;

//  1.1.33 Matrix library. Write a library Matrix that implements the following API:
//  public class Matrix
//  static double dot(double[] x, double[] y)  vector dot product
//  static double[][] mult(double[][] a, double[][] b)  matrix-matrix product
//  static double[][] transpose(double[][] a)  transpose
//  static double[] mult(double[][] a, double[] x)  matrix-vector product
//  static double[] mult(double[] y, double[][] a)  vector-matrix product
//  Develop a test client that reads values from standard input and tests all the methods.

public class Ex1133MatrixLibrary {

  public static double dot(double[] x, double[] y) {
    // dot product
    assert x != null;  assert y!= null;
    int n = x.length;  assert n != 0;
    assert n == y.length;

    double sum = 0;
    for (int i = 0; i < x.length; i++) 
      sum+=x[i]*y[i];
    return sum;
  }

  public static double[][] mult(double[][] a, double[][] b) {
    // matrix-matrix product
    assert a != null; assert b!= null;
    int n = a.length;        assert n != 0;
    assert n == b.length;    assert n == a[0].length;
    assert b[0].length != 0; assert n == b[0].length;

    double[][] c = new double[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) { 
        for (int k = 0; k < n; k++)
          c[i][j] += a[i][k]*b[k][j];
      }
    return c;
  }

  public static double[][] transpose(double[][] a) {
    // interchange rows and columns or reflect over main diagonal
    assert a != null; int n = a.length;
    assert n != 0;    int o = a[0].length;

    double[][] t = new double[o][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++)
        t[j][i] = a[i][j];

    return t;
  }

  public static double[] mult(double[][] a, double[] x) {
    // matrix-vector product
    assert a != null; assert x != null;
    int n = a.length; assert n != 0;
    int o = x.length; assert o != 0;
    assert a[0].length == o;

    double[] b = new double[n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < o; j++) 
          b[i] += a[i][j]*x[j];
    return b;
  }
  
  public static double[] mult(double[] y, double[][] a) {
    //vector-matrix product
    assert a != null; assert y != null;
    int n = a.length; assert n != 0;
    int o = y.length; assert o != 0;
    assert n == o;    assert a[0].length != 0;
    
    double[] b = new double[a[0].length];
    for (int i = 0; i < a[0].length; i++)
      for (int j = 0; j < o; j++)
        b[i] +=  y[j]*a[j][i];
    return b; 
  }

  public static void main(String[] args) {
    // below is shown how I actually confirmed credibility of the methods
    // maybe Junit would be good, but I will fix the methods if and when they
    // appear to have an error in practice. they aren't methods I want to test 
    // or recommend testing with stdin.
    
    // dot product
    double[] z = {1,2,3}; double[] w = {4,5,6};
    double r1 = dot(z,w);
    System.out.println("r1="+r1); //32
    assert r1 == 1*4+2*5+3*6;
    
    double[][] a = new double[3][3];
    a[0] = new double[]{1,2,3};
    a[1] = new double[]{4,5,6};
    a[2] = new double[]{7,8,9};
    
    double[][] b = new double[3][3];
    b[0] = new double[]{9,8,7};
    b[1] = new double[]{6,5,4};
    b[2] = new double[]{3,2,1};
    double[][] c = mult(a,b);
    System.out.println("mult(a,b):");
    printArray(c);
    System.out.println();
    //      0                        1                        2                       
    //  0    30.00000000000000000000  24.00000000000000000000  18.00000000000000000000
    //  1    84.00000000000000000000  69.00000000000000000000  54.00000000000000000000
    //  2   138.00000000000000000000 114.00000000000000000000  90.00000000000000000000
    assert c[0][0] == 1.*9 + 2.*6 + 3.*3;
    assert c[1][2] == 4*7 + 5*4 + 6*1;
    
    a = new double[4][3];
    a[0] = new double[]{1,2,3};
    a[1] = new double[]{4,5,6};
    a[2] = new double[]{7,8,9};
    a[3] = new double[]{10,11,12};
    printArray(a);
    //      0                       1                       2                      
    //  0    1.00000000000000000000  2.00000000000000000000  3.00000000000000000000
    //  1    4.00000000000000000000  5.00000000000000000000  6.00000000000000000000
    //  2    7.00000000000000000000  8.00000000000000000000  9.00000000000000000000
    //  3   10.00000000000000000000 11.00000000000000000000 12.00000000000000000000
         
    printArray(transpose(a)); // looks perfect
    //      0                       1                       2                       3                      
    //  0     1.00000000000000000000  4.00000000000000000000  7.00000000000000000000 10.00000000000000000000
    //  1     2.00000000000000000000  5.00000000000000000000  8.00000000000000000000 11.00000000000000000000
    //  2     3.00000000000000000000  6.00000000000000000000  9.00000000000000000000 12.00000000000000000000

    double[] x = new double[]{1,3,5};
    double[] r5 = mult(a,x);
    printArray(r5);  //[22.0, 49.0, 76.0, 103.0] // checked by inspection ok
    
    System.out.println();
    
    b = new double[3][4];
    b[0] = new double[]{1,2,3,4};
    b[1] = new double[]{5,6,7,8};
    b[2] = new double[]{9,10,11,12};
    double[] d = mult(x,b);
    printArray(d); //[61.0, 70.0, 79.0, 88.0] // checked by inspection ok
  }

}
