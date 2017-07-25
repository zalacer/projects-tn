package graph;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/41graph/MemoryOfGraph.java
 *  http://algs4.cs.princeton.edu/41graph/MemoryOfGraph.java.html
 *  Compilation:  javac -cp .:jama.jar:classmexer.jar MemoryOfGraph.java
 *  Execution:    java  -cp .:jama.jar:classmexer.jar -XX:-UseCompressedOops -javaagent:classmexer.jar MemoryOfGraph
 *  Dependencies: Graph.java MultipleLinearRegression.java StdOut.java classmexer.jar jama.jar
 *
 *  % java -cp .:jama.jar:classmexer.jar -XX:-UseCompressedOops -javaagent:classmexer.jar MemoryOfGraph
 *  size of Integer = 24 bytes
 *  the memory of a Graph  with V vertices and E edges:  56.00 + 40.00 V + 128.00 E bytes (R^2 = 1.000)
 *  min memory of a GraphX with V vertices and E edges:  56.00 + 64.00 V + 8.00 E bytes (R^2 = 1.000)
 *  max memory of a GraphX with V vertices and E edges:  56.00 + 56.00 V + 16.00 E bytes (R^2 = 1.000)
 *
 *  % java -cp .:jama.jar:classmexer.jar -XX:+UseCompressedOops -javaagent:classmexer.jar MemoryOfGraph
 *  size of Integer = 16 bytes
 *  the memory of a Graph  with V vertices and E edges:  44.00 + 28.00 V + 80.00 E bytes (R^2 = 1.000)
 *  min memory of a GraphX with V vertices and E edges:  40.00 + 44.00 V + 8.00 E bytes (R^2 = 1.000)
 *  max memory of a GraphX with V vertices and E edges:  40.00 + 36.00 V + 16.00 E bytes (R^2 = 1.000)
 *
 *
 ******************************************************************************/

import com.javamex.classmexer.MemoryUtil;

import analysis.MultipleLinearRegression;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class MemoryOfGraph {

    // create a uniformly random k-regular graph on V vertices (not necessarily simple)
    public static GraphX regular(int V, int k) {
        if (V*k % 2 != 0) throw new IllegalArgumentException("Number of vertices * k must be even");
        GraphX G = new GraphX(V);

        // create k copies of each vertex
        int[] vertices = new int[V*k];
        for (int v = 0; v < V; v++) {
            for (int j = 0; j < k; j++) {
                vertices[v + V*j] = v;
            }
        }

        // pick a random perfect matching
        StdRandom.shuffle(vertices);
        for (int i = 0; i < V*k/2; i++) {
            G.addEdge(vertices[2*i], vertices[2*i + 1]);
        }
        return G;
    }


    // memory of Graph - assuming adjacency lists use linked-list representation
    public static void memoryOfGraph() {
        int n = 40;
        int[] V = new int[n];
        int[] E = new int[n];

        // build random graphs and compute memory usage
        long[] memory = new long[n];
        for (int i = 0; i < n; i++) {
            V[i] = 128 + 1 + 2 * StdRandom.uniform(500);  // number of vertices
            E[i] = V[i] * (2 + StdRandom.uniform(10));    // number of edges
            Graph G = new Graph(V[i]);
            for (int j = 0; j < E[i]; j++) {
                // first 128 Integer values are cached, so don't use these
                int v = 128 + StdRandom.uniform(V[i] - 128);
                int w = 128 + StdRandom.uniform(V[i] - 128);
                G.addEdge(v, w);
            }
            memory[i] = MemoryUtil.deepMemoryUsageOf(G);
        }

        double[][] x = new double[n][3];
        for (int i = 0; i < n; i++) {
            x[i][0] = 1.0;
            x[i][1] = V[i];
            x[i][2] = E[i];
        }


        // build multiple linear regression coefficients
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = memory[i];
        }

        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);
        StdOut.print("memory of a Graph  with V vertices and E edges:  ");
        StdOut.printf("%.2f + %.2f V + %.2f E bytes (R^2 = %.3f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
    }

    // min memory of GraphX - assuming adjacency lists use resizing array representation
    // and uses primitive type int (so don't need to worry about caching Integers)
    public static void minMemoryOfGraphX() {
        int n = 40;
        int[] V = new int[n];
        int[] E = new int[n];

        // build random k-regular graphs and compute memory usage
        long[] memory = new long[n];
        for (int i = 0; i < n; i++) {
            int even = 2+ 2*StdRandom.uniform(500);   // a multiple of 2
            int k = 1 << (1 + StdRandom.uniform(6));  // a power of 2 >= 2 since 2 is min size of resizing array
            GraphX G = regular(even, k);
            V[i] = G.V();  // number of vertices
            E[i] = G.E();  // number of edges
            memory[i] = MemoryUtil.deepMemoryUsageOf(G);
        }

        double[][] x = new double[n][3];
        for (int i = 0; i < n; i++) {
            x[i][0] = 1.0;
            x[i][1] = V[i];
            x[i][2] = E[i];
        }


        // build multiple linear regression coefficients
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = memory[i];
        }

        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);
        StdOut.print("min memory of a GraphX with V vertices and E edges:  ");
        StdOut.printf("%.2f + %.2f V + %.2f E bytes (R^2 = %.3f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
    }

    // min memory of GraphX - assuming adjacency lists use resizing array representation
    // and uses primitive type int (so don't need to worry about caching Integers)
    public static void maxMemoryOfGraphX() {
        int n = 40;
        int[] V = new int[n];
        int[] E = new int[n];

        // build random k-regular graphs and compute memory usage
        long[] memory = new long[n];
        for (int i = 0; i < n; i++) {
            int even = 2+ 2*StdRandom.uniform(500);         // a multiple of 2
            int k = 1 + (1 << (1 + StdRandom.uniform(6)));  // one more than a power of 2 >= 2 since 2 is min size of resizing array
            GraphX G = regular(even, k);
            V[i] = G.V();  // number of vertices
            E[i] = G.E();  // number of edges
            memory[i] = MemoryUtil.deepMemoryUsageOf(G);
        }

        double[][] x = new double[n][3];
        for (int i = 0; i < n; i++) {
            x[i][0] = 1.0;
            x[i][1] = V[i];
            x[i][2] = E[i];
        }


        // build multiple linear regression coefficients
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = memory[i];
        }

        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);
        StdOut.print("max memory of a GraphX with V vertices and E edges:  ");
        StdOut.printf("%.2f + %.2f V + %.2f E bytes (R^2 = %.3f)\n",
                      regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());
    }



    public static void main(String[] args) {

        Integer a = new Integer(123456);
        StdOut.println("size of Integer = " + MemoryUtil.memoryUsageOf(a) + " bytes");
        memoryOfGraph();
        minMemoryOfGraphX();
        maxMemoryOfGraphX();
    }
}
