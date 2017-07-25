package graph;

/******************************************************************************
 *  % java DepthFirstSearch tinyG.txt 0
 *  0 1 2 3 4 5 6 
 *  NOT connected
 *
 *  % java DepthFirstSearch tinyG.txt 9
 *  9 10 11 12 
 *  NOT connected
 *
 ******************************************************************************/

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DepthFirstSearch.java

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class DepthFirstSearchX {
    private boolean[] marked;    // marked[v] = is there an s-v path?
    private int count;           // number of vertices connected to s

    public DepthFirstSearchX(GraphX G, int s) {
        marked = new boolean[G.V()];
        validateVertex(s);
        dfs(G, s);
    }

    // depth first search from v
    private void dfs(GraphX G, int v) {
        count++;
        marked[v] = true;
        for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
    }

    public boolean marked(int v) { validateVertex(v); return marked[v]; }

    public int count() {
      // return number of vertices connected to the source vertex
        return count;
    }

    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
    }
    
    public boolean[] marked() { return marked; }

    public static void main(String[] args) {
        In in = new In(args[0]);
        GraphX G = new GraphX(in);
        System.out.println(G);
        int s = Integer.parseInt(args[1]);
        DepthFirstSearchX search = new DepthFirstSearchX(G, s);
        for (int v = 0; v < G.V(); v++) {
            if (search.marked(v))
                StdOut.print(v + " ");
        }

        StdOut.println();
        if (search.count() != G.V()) StdOut.println("NOT connected");
        else                         StdOut.println("connected");
    }

}
