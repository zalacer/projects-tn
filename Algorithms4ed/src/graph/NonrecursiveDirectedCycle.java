package graph;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/42digraph/NonrecursiveDirectedCycle.java
 *  http://algs4.cs.princeton.edu/42digraph/NonrecursiveDirectedCycle.java.html
 *  Compilation:  javac NonrecursiveDirectedCycle.java
 *  Execution:    java NonrecursiveDirectedCycle input.txt
 *  Dependencies: Digraph.java Stack.java StdOut.java In.java
 *  Data files:   http://algs4.cs.princeton.edu/42digraph/tinyDG.txt
 *                http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt
 *
 *  Finds a directed cycle in a digraph using nonrecursive DFS.
 *  Runs in O(E + V) time.
 *
 *  % java NonrecursiveDirectedCycle tinyDG.txt 
 *  Directed cycle: 3 5 4 3 
 *
 *  %  java NonrecursiveDirectedCycle tinyDAG.txt 
 *  No directed cycle
 *
 ******************************************************************************/
import static v.ArrayUtils.*;

import java.util.Iterator;

import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 *  The {@code NonrecursiveDirectedCycle} class represents a data type for 
 *  determining whether a digraph has a directed cycle.
 *  The <em>hasCycle</em> operation determines whether the digraph has
 *  a directed cycle and, and of so, the <em>cycle</em> operation
 *  returns one.
 *  <p>
 *  This implementation uses a nonrecursive depth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>
 *  (in the worst case),
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  Afterwards, the <em>hasCycle</em> operation takes constant time;
 *  the <em>cycle</em> operation takes time proportional
 *  to the length of the cycle.
 *  <p>
 *  See {@link NonrecursiveTopological} to compute a topological order if the
 *  digraph is acyclic.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class NonrecursiveDirectedCycle {
    private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)

    /**
     * Determines whether the digraph {@code G} has a directed cycle and, if so,
     * finds such a cycle.
     * @param G the digraph
     */
    public NonrecursiveDirectedCycle(Digraph G) {
        int[] edgeTo = new int[G.V()];            // edgeTo[v] = previous vertex on path to v
        boolean[] marked = new boolean[G.V()];    // marked[v] = has vertex v been marked?
        boolean[] onStack = new boolean[G.V()];   // onStack[v] = is vertex on the stack?
        Stack<Integer> stack = new Stack<Integer>();

        // to be able to iterate over each adjacency list, keeping track of which
        // vertex in each adjacency list needs to be explored next
        Iterator<Integer>[] adj = ofDim(Iterator.class, G.V());
        for (int v = 0; v < G.V(); v++)
            adj[v] = G.adj(v).iterator();

        for (int s = 0; s < G.V(); s++) {
            if (!marked[s]) {
                onStack[s] = true;
                marked[s] = true;
                stack.push(s);
                while (!stack.isEmpty()) {
                    int v = stack.peek();
                    if (adj[v].hasNext()) {
                        int w = adj[v].next();
                        if (!marked[w]) {
                            // discovered vertex w for the first time
                            marked[w] = true;
                            edgeTo[w] = v;
                            stack.push(w);
                            onStack[w] = true;
                        }
                        // trace back directed cycle
                        else if (onStack[w]) {
                            cycle = new Stack<Integer>();
                            for (int x = v; x != w; x = edgeTo[x]) {
                                cycle.push(x);
                            }
                            cycle.push(w);
                            cycle.push(v);
                            assert check();
                            return;
                        }
                    }
                    else {
                        // v's adjacency list is exhausted
                        int vCopy = stack.pop();
                        assert v == vCopy;
                        onStack[v] = false;
                    }
                }
            }
        }
    }



    /**
     * Does the digraph have a directed cycle?
     * @return {@code true} if the digraph has a directed cycle, {@code false} otherwise
     */
    public boolean hasCycle() {
        return cycle != null;
    }

    /**
     * Returns a directed cycle if the digraph has a directed cycle, and {@code null} otherwise.
     * @return a directed cycle (as an iterable) if the digraph has a directed cycle,
     *    and {@code null} otherwise
     */
    public Iterable<Integer> cycle() {
        return cycle;
    }


    // certify that digraph has a directed cycle if it reports one
    private boolean check() {

        if (hasCycle()) {
            // verify cycle
            int first = -1, last = -1;
            for (int v : cycle()) {
                if (first == -1) first = v;
                last = v;
            }
            if (first != last) {
                System.err.printf("cycle begins with %d and ends with %d\n", first, last);
                return false;
            }
        }


        return true;
    }

    /**
     * Unit tests the {@code NonrecursiveDirectedCycle} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);

        NonrecursiveDirectedCycle finder = new NonrecursiveDirectedCycle(G);
        if (finder.hasCycle()) {
            StdOut.print("Directed cycle: ");
            for (int v : finder.cycle()) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }

        else {
            StdOut.println("No directed cycle");
        }
    }
}

