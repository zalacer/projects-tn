package st;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/33balanced/MemoryOfBSTs.java
 *  http://algs4.cs.princeton.edu/33balanced/MemoryOfBSTs.java.html
 *  Compilation:  javac -cp .:classmexer.jar MemoryOfBSTs.java
 *  Execution:    java  -cp .:classmexer.jar -XX:-UseCompressedOops -javaagent:classmexer.jar MemoryOfBSTs
 *  Dependencies: StdOut.java classmexer.jar
 *
 ******************************************************************************/

import com.javamex.classmexer.MemoryUtil;

import java.util.LinkedList;
import java.util.TreeMap;

import analysis.LinearRegression;


public class MemoryOfBSTs {

    public static void main(String[] args) {
        int[] sizes = { 8, 16, 32, 64, 128, 256, 512}; //, 1024, 2048, 4096, 8192 };
        int n = sizes.length;
        double[] x = new double[n];
        double[] bstMemory      = new double[n];
        double[] redBlackMemory = new double[n];
        double[] treeMapMemory  = new double[n];

        for (int i = 0; i < n; i++) {
            RedBlackBST<Integer, Integer> bst1 = new RedBlackBST<Integer, Integer>();
            BST<Integer, Integer> bst2 = new BST<Integer, Integer>();
            TreeMap<Integer, Integer> bst3 = new TreeMap<Integer, Integer>();
            LinkedList<Integer> list = new LinkedList<Integer>();
            for (int j = 0; j < sizes[i]; j++) {
                Integer z = new Integer(j);
                list.add(z);
                bst1.put(z, z);
                bst2.put(z, z);
                bst3.put(z, z);
            }
            x[i] = bst1.size();

            bstMemory[i]      = MemoryUtil.deepMemoryUsageOf(bst1) - MemoryUtil.deepMemoryUsageOfAll(list);
            redBlackMemory[i] = MemoryUtil.deepMemoryUsageOf(bst2) - MemoryUtil.deepMemoryUsageOfAll(list);
            treeMapMemory[i]  = MemoryUtil.deepMemoryUsageOf(bst3) - MemoryUtil.deepMemoryUsageOfAll(list);
        }


        LinearRegression regression1 = new LinearRegression(x, bstMemory);
        System.out.println("Memory of BST               with n key-value pairs = " + regression1);

        LinearRegression regression2 = new LinearRegression(x, redBlackMemory);
        System.out.println("Memory of RedBlackBST       with n key-value pairs = " + regression2);

        LinearRegression regression3 = new LinearRegression(x, treeMapMemory);
        System.out.println("Memory of java.util.TreeMap with n key-value pairs = " + regression3);
    }
                
}

