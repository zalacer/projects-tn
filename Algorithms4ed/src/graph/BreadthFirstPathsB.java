package graph;

import ds.Queue;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from text p540 Algorithm 4.2

public class BreadthFirstPathsB {
  private boolean[] marked; // Is a shortest path to this vertex known?
  private int[] edgeTo; // last vertex on known path to this vertex
  private final int s; // source

  public BreadthFirstPathsB(Graph G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    bfs(G, s);
  }

  private void bfs(Graph G, int s) {
    Queue<Integer> q = new Queue<>();
    marked[s] = true; // Mark the source
    q.enqueue(s); // and put it on the queue.
    while (!q.isEmpty())  {
      int v = q.dequeue(); // Remove next vertex from the queue.
      for (int w : G.adj(v))
        if (!marked[w]) { 
          // For every unmarked adjacent vertex,
          edgeTo[w] = v; // save last edge on a shortest path,
          marked[w] = true; // mark it because path is known,
          q.enqueue(w); // and add it to the queue.
        }
    }
  }

  public boolean hasPathTo(int v) { return marked[v]; }

  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != s; x = edgeTo[x]) path.push(x);
    path.push(s);
    return path;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);

    int s = Integer.parseInt(args[1]);
    BreadthFirstPathsB bfs = new BreadthFirstPathsB(G, s);

    for (int v = 0; v < G.V(); v++) {
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d:  ", s, v);
        for (int x : bfs.pathTo(v)) {
          if (x == s) StdOut.print(x);
          else        StdOut.print("-" + x);
        }
        StdOut.println();
      }

      else {
        StdOut.printf("%d to %d:  not connected\n", s, v);
      }
    }
  }
  
}



