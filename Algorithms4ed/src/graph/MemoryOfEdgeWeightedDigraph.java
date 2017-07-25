package graph;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/44sp/MemoryOfEdgeWeightedDigraph.java
 *  http://algs4.cs.princeton.edu/44sp/MemoryOfEdgeWeightedDigraph.java.html
 *  Compilation:  javac -cp .:jama.jar:classmexer.jar MemoryOfEdgeWeightedDigraph.java
 *  Execution:    java  -cp .:jama.jar:classmexer.jar -XX:-UseCompressedOops -javaagent:classmexer.jar MemoryOfEdgeWeightedDigraph
 *  Dependencies: EdgeWeightedDigraph.java MultipleLinearRegression.java StdOut.java classmexer.jar jama.jar
 *
 *  % java -cp .:jama.jar:classmexer.jar -XX:-UseCompressedOops -javaagent:classmexer.jar MemoryOfEdgeWeightedDigraph
 *  memory of an EdgeWeightedDigraph with V vertices and E edges:
 *  56.00 + 40.00 V + 72.00 E bytes (R^2 = 1.000)
 *
 ******************************************************************************/

import com.javamex.classmexer.MemoryUtil;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import analysis.MultipleLinearRegression;

public class MemoryOfEdgeWeightedDigraph {

    public static void main(String[] args) {
        DirectedEdge e = new DirectedEdge(123456, 654321, 1.0);
        StdOut.println("size of DirectedEdge = " + MemoryUtil.memoryUsageOf(e) + " bytes");

        int n = 40;
        int[] V = new int[n];
        int[] E = new int[n];

        // build random graphs and compute memory usage
        long[] memory = new long[n];
        for (int i = 0; i < n; i++) {
            V[i] = 2*StdRandom.uniform(500);       // vertices
            E[i] = V[i] * StdRandom.uniform(10);   // edges
            EdgeWeightedDigraph G = new EdgeWeightedDigraph(V[i]);
            for (int j = 0; j < E[i]; j++) {
                int v = StdRandom.uniform(V[i]);
                int w = StdRandom.uniform(V[i]);
                double weight = StdRandom.uniform(0.0, 1.0);
                G.addEdge(new DirectedEdge(v, w, weight));
            }
            memory[i] = MemoryUtil.deepMemoryUsageOf(G);
        }

        // build multiple linear regression coefficients
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = memory[i];
        }

        double[][] x = new double[n][3];
        for (int i = 0; i < n; i++) {
            x[i][0] = 1.0;
            x[i][1] = V[i];
            x[i][2] = E[i];
        }


        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);
        StdOut.println("memory of an EdgeWeightedDigraph with V vertices and E edges:");
        StdOut.printf("%.2f + %.2f V + %.2f E bytes (R^2 = %.3f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
    }
                
}
