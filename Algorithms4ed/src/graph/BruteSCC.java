package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/42digraph/BruteSCC.java
 *  http://algs4.cs.princeton.edu/42digraph/BruteSCC.java.html
 *  Compilation:  javac BruteSCC.java
 *  Execution:    java  BruteSCC filename.txt
 *  Dependencies: Digraph.java TransitiveClosure.java
 *  Data files:   http://algs4.cs.princeton.edu/42digraph/tinyDG.txt
 *                http://algs4.cs.princeton.edu/42digraph/mediumDG.txt
 *                http://algs4.cs.princeton.edu/42digraph/largeDG.txt
 *
 *  Compute the strongly-connected components of a digraph using 
 *  brute force.
 *
 *  Runs in O(EV) time.
 *
 *  % java BruteSCC tinyDG.txt
 *  5 components
 *  0 2 3 4 5 
 *  1 
 *  6 
 *  7 8 
 *  9 10 11 12 
 *
 ******************************************************************************/

public class BruteSCC {
    private int count;    // number of strongly connected components
    private int[] id;     // id[v] = id of strong component containing v

    public BruteSCC(Digraph G) {

        // initially each vertex is in its own component
        id = new int[G.V()];
        for (int v = 0; v < G.V(); v++)
            id[v] = v;

        // compute transitive closure
        TransitiveClosure tc = new TransitiveClosure(G);

        // if v and w are mutally reachable, assign v to w's component
        for (int v = 0; v < G.V(); v++)
            for (int w = 0; w < v; w++)
                if (tc.reachable(v, w) && tc.reachable(w, v)) 
                    id[v] = id[w];

        // compute number of strongly connected components
        for (int v = 0; v < G.V(); v++)
            if (id[v] == v)
                count++;
    }

    // return the number of strongly connected components
    public int count() { return count; }

    // are v and w strongly connected?
    public boolean stronglyConnected(int v, int w) {
        return id[v] == id[w];
    }

    // in which strongly connected component is vertex v?
    public int id(int v) { return id[v]; }


    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        BruteSCC scc = new BruteSCC(G);

        // number of connected components
        int m = scc.count();
        StdOut.println(m + " components");

        // compute list of vertices in each strong component
        Queue<Integer>[] components = ofDim(Queue.class, m);
        for (int i = 0; i < G.V(); i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < G.V(); v++) {
            components[scc.id(v)].enqueue(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : components[i]) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }

    }

}
