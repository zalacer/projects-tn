package sort;

/******************************************************************************
 *  Compilation:  javac SortCompare.java
 *  Execution:    java SortCompare alg1 alg2 n trials
 *  Dependencies: StdOut.java Stopwatch.java
 *  
 *  Sort n random real numbers, trials times using the two
 *  algorithms specified on the command line.
 * 
 *  % java SortCompare Insertion Selection 1000 100 
 *  For 1000 random Doubles 
 *    Insertion is 1.7 times faster than Selection
 *
 *  Note: this program is designed to compare two sorting algorithms with
 *  roughly the same order of growth, e,g., insertion sort vs. selection
 *  sort or mergesort vs. quicksort. Otherwise, various system effects
 *  (such as just-in-time compiliation) may have a significant effect.
 *  One alternative is to execute with "java -Xint", which forces the JVM
 *  to use interpreted execution mode only.
 *
 ******************************************************************************/

import java.util.Arrays;

import edu.princeton.cs.algs4.BinaryInsertion;
import edu.princeton.cs.algs4.Heap;
import edu.princeton.cs.algs4.Insertion;
import edu.princeton.cs.algs4.InsertionX;
import edu.princeton.cs.algs4.Merge;
import edu.princeton.cs.algs4.MergeBU;
import edu.princeton.cs.algs4.MergeX;
import edu.princeton.cs.algs4.Quick;
import edu.princeton.cs.algs4.Quick3way;
import edu.princeton.cs.algs4.QuickX;
import edu.princeton.cs.algs4.Selection;
import edu.princeton.cs.algs4.Shell;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class SortCompareOrig { 

    public static double time(String alg, Double[] a) { 
        Stopwatch sw = new Stopwatch(); 
        if      (alg.equals("Insertion"))       Insertion.sort(a); 
        else if (alg.equals("InsertionX"))      InsertionX.sort(a); 
        else if (alg.equals("BinaryInsertion")) BinaryInsertion.sort(a); 
        else if (alg.equals("Selection"))       Selection.sort(a); 
        else if (alg.equals("Bubble"))          Bubble.sort(a); 
        else if (alg.equals("Shell"))           Shell.sort(a); 
        else if (alg.equals("Merge"))           Merge.sort(a); 
        else if (alg.equals("MergeX"))          MergeX.sort(a); 
        else if (alg.equals("MergeBU"))         MergeBU.sort(a); 
        else if (alg.equals("Quick"))           Quick.sort(a); 
        else if (alg.equals("Quick3way"))       Quick3way.sort(a); 
        else if (alg.equals("QuickX"))          QuickX.sort(a); 
        else if (alg.equals("Heap"))            Heap.sort(a); 
        else if (alg.equals("System"))          Arrays.sort(a); 
        else throw new IllegalArgumentException("Invalid algorithm: " + alg);
        return sw.elapsedTime(); 
    } 

    // Use alg to sort trials random arrays of length n.
    public static double timeRandomInput(String alg, int n, int trials)  {
        double total = 0.0;
        Double[] a = new Double[n];
        // Perform one experiment (generate and sort an array).
        for (int t = 0; t < trials; t++) {
            for (int i = 0; i < n; i++)
                a[i] = StdRandom.uniform(0.0, 1.0);
            total += time(alg, a);
        } 
        return total; 
    } 

    // Use alg to sort trials random arrays of length n. 
    public static double timeSortedInput(String alg, int n, int trials) {
        double total = 0.0;
        Double[] a = new Double[n];
        // Perform one experiment (generate and sort an array).
        for (int t = 0; t < trials; t++) {
            for (int i = 0; i < n; i++)
                a[i] = 1.0 * i;
            total += time(alg, a);
        } 
        return total; 
    } 

    public static void main(String[] args) { 
        String alg1 = args[0]; 
        String alg2 = args[1]; 
        int n = Integer.parseInt(args[2]);
        int trials = Integer.parseInt(args[3]);
        double time1, time2;
        if (args.length == 5 && args[4].equals("sorted")) {
            time1 = timeSortedInput(alg1, n, trials);   // Total for alg1. 
            time2 = timeSortedInput(alg2, n, trials);   // Total for alg2. 
        }
        else {
            time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
            time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
        }

        StdOut.printf("For %d random Doubles\n    %s is", n, alg1); 
        StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    } 
} 
