package analysis;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/25applications/KendallTau.java
 *  http://algs4.cs.princeton.edu/25applications/KendallTau.java.html
 *  Compilation:  javac KendallTau.java
 *  Execution:    java KendallTau n
 *  Dependencies: StdOut.java Inversions.java
 *  
 *  Generate two random permutations of size N and compute their
 *  Kendall tau distance (number of inversions).
 *
 ******************************************************************************/

public class KendallTau {

    // return Kendall tau distance between two permutations
    public static long distance(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array dimensions disagree");
        }
        int n = a.length;

        int[] ainv = new int[n];
        for (int i = 0; i < n; i++)
            ainv[a[i]] = i;

        Integer[] bnew = new Integer[n];
        for (int i = 0; i < n; i++)
            bnew[i] = ainv[b[i]];

        return Inversions.count(bnew);
    }

    // return a random permutation of size n
    public static int[] permutation(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = i;
        StdRandom.shuffle(a);
        return a;
    }
    
    


    @SuppressWarnings("unused")
    public static void main(String[] args) {

        // two random permutation of size n
        int n = Integer.parseInt(args[0]);
        int[] a = KendallTau.permutation(n);
        int[] b = KendallTau.permutation(n);


        // print initial permutation
        for (int i = 0; i < n; i++)
            StdOut.println(a[i] + " " + b[i]);
        StdOut.println();

        StdOut.println("inversions = " + KendallTau.distance(a, b));
        
        // for arrays http://introcs.cs.princeton.edu/java/14array/KendallTau.java.html
        n = Integer.parseInt(args[0]);

        // first permutation
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            int r = (int) (Math.random() * (i+1));
            p[i] = p[r];
            p[r] = i;
        }

        // second permutation
        int[] q = new int[n];
        for (int i = 0; i < n; i++) {
            int r = (int) (Math.random() * (i+1));
            q[i] = q[r];
            q[r] = i;
        }

        // print permutations
        for (int i = 0; i < n; i++)
            System.out.println(p[i] + " " + q[i]);
        System.out.println();

        // inverse of 2nd permutation
        int[] inv = new int[n];
        for (int i = 0; i < n; i++)
            inv[q[i]] = i;


        // calculate Kendall tau distance
        int tau = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                // check if p[i] and p[j] are inverted
                if (inv[p[i]] > inv[p[j]]) tau++;
            }
        }
        
        
    }
}

