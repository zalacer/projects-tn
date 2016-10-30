package ex15;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;
import ds.RandomBag;

/*
  p.237
  1.5.18 Random grid generator. Write a program RandomGrid that takes an int value
  N from the command line, generates all the connections in an N-by-N grid, puts 
  them in random order, randomly orients them (so that p q and  q p are equally likely 
  to occur), and prints the result to standard output. To randomly order the 
  connections, use a  RandomBag  (see Exercise 1.3.34 on page 167). To encapsulate p 
  and q in a single object, use the Connection nested class shown below. Package your
  program as two static methods: generate(), which takes  N as argument and returns 
  an array of connections, and  main(), which takes N from the command line, calls  
  generate() , and iterates through the returned array to print the connections.  
 */

public class Ex1518RandomGridGenerator {
  
  public static class Connection {
    int p;
    int q;
    public Connection(int p, int q) { 
      this.p = p; this.q = q; 
     }
    @Override
    public String toString() {
      return p+"-"+q;
    }
  }
  
  public static Connection[] generate(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("generate: N must be > 1");
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(10730059);
    
    //                                       adjacent    diagonal
    Connection[] c = ofDim(Connection.class, 2*n*(n-1) + 2*(n-1)*(n-1));
    
    int[][] z = new int[n][n];
    int offset = 0;
    for (int i = 0; i < n; i++) {
      z[i] = range(offset, offset+n);
      offset = n*(i+1);
    }
    
    int k = 0; // index for c
    
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n-1; j++)
        if (r.nextInt(2) == 0) { // adjacent sideways right or left
          c[k++] = new Connection(z[i][j], z[i][j+1]);
        } else {
          c[k++] = new Connection(z[i][j+1], z[i][j]);
        }
    
    for (int i = 0; i < n-1; i++) 
      for (int j = 0; j < n; j++) {
        if (r.nextInt(2) == 0) { // adjacent down or up
          c[k++] = new Connection(z[i][j], z[i+1][j]);
        } else {
          c[k++] = new Connection(z[i+1][j], z[i][j]);
        }
        
        if (j < n-1) { // right diagonal down or left diagonal up
          if (r.nextInt(2) == 0) {
            c[k++] = new Connection(z[i][j], z[i+1][j+1]);
          } else {
            c[k++] = new Connection(z[i+1][j+1], z[i][j]);
          }
        }
        
        if (j > 0) {// left diagonal down or right diagonal up
          if (r.nextInt(2) == 0) {
            c[k++] = new Connection(z[i][j], z[i+1][j-1]);
          } else {
            c[k++] = new Connection(z[i+1][j-1], z[i][j]);
          }
        }
      }

    RandomBag<Connection> bag = new  RandomBag<Connection>(c);
    return bag.toArray(c[0]);
  }
 
  public static void main(String[] args) {
    
    Scanner sc = new Scanner(System.in);
    if (sc.hasNextInt()) {
      int n = sc.nextInt();
      System.out.println("N="+n);
      Connection[] ca = generate(n);
      for (Connection c : ca) System.out.println(c);
    }
    sc.close();
    //  N=5
    //  2-8
    //  10-6
    //  24-18
    //  19-23
    //  14-13
    //  1-5
    //  2-6
    //  18-14
    //  13-18
    //  9-14
    //  16-22
    //  11-7
    //  17-12
    //  3-4
    //  24-19
    //  17-23
    //  9-3
    //  6-0
    //  11-15
    //  6-7
    //  5-11
    //  10-16
    //  19-14
    //  12-18
    //  21-22
    //  11-6
    //  10-11
    //  22-18
    //  2-3
    //  20-16
    //  9-4
    //  13-12
    //  22-23
    //  11-12
    //  7-3
    //  9-13
    //  5-0
    //  5-10
    //  21-16
    //  16-12
    //  12-7
    //  7-2
    //  6-1
    //  7-8
    //  24-23
    //  8-4
    //  13-17
    //  17-21
    //  18-23
    //  8-13
    //  1-0
    //  8-9
    //  18-19
    //  11-16
    //  6-12
    //  20-15
    //  16-17
    //  21-15
    //  17-18
    //  5-6
    //  11-17
    //  14-8
    //  16-15
    //  1-7
    //  21-20
    //  8-3
    //  12-8
    //  17-22
    //  13-19
    //  13-7
    //  10-15
    //  2-1
 
//  preliminary testing    
//    Connection[] c = generate(3);
//    pa(c,1000,1,1); 
    // straight
//  [(0,1),(1,2),(3,4),(4,5),(6,7),(7,8),(0,3),(0,4),(1,4),(1,5),(1,3),(2,5),(2,4),(3,6),(3,7),(4,7),(4,8),(4,6),(5,8),(5,7)]
    // with Random Bag
//  [(1,5),(3,4),(4,5),(4,8),(6,7),(0,1),(2,5),(5,7),(1,4),(0,4),(1,2),(5,8),(1,3),(2,4),(3,6),(7,8),(4,6),(0,3),(4,7),(3,7)]
    // with random connection orientation
//  [(5,7),(3,0),(5,8),(4,0),(7,4),(3,7),(4,6),(7,8),(2,4),(4,8),(3,4),(6,7),(2,5),(3,6),(2,1),(4,5),(1,5),(1,0),(1,4),(1,3)]
    // using SecureRandom for connection orientation - RandomBag already uses SecureRandom
//  [(4,1),(8,5),(1,2),(1,3),(7,4),(6,4),(5,4),(3,6),(1,0),(8,7),(4,2),(4,8),(1,5),(0,3),(5,7),(4,0),(5,2),(3,4),(6,7),(7,3)]

  }
}
