package graph;

import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.par;
import static v.ArrayUtils.range;

// from text p547

public class TwoColorTrace {
  private Boolean[] marked;
  private Boolean[] color;
  private boolean isTwoColorable = true;
  private final GraphX g;
  private final int V;
  private int dfsForLoopCount;

  public TwoColorTrace(GraphX G) {
    if (G == null) throw new IllegalArgumentException("TwoColorTrace: graph is null");
    g = G; V = g.V();
    System.out.print("graph.adj = "); par(g.adj());
    marked = new Boolean[G.V()];
    color = new Boolean[G.V()];
    String l = arrayToString(range(0,V),900,1,1);
    l = l.replaceAll("\\[|\\]","").replaceAll(",", " ");
    System.out.println("               marked[]                   color[]");
    System.out.println("               "+l+"  "+l);
    for (int j = 0; j < V; j++)
      if (!isTwoColorable) break;
      else if (marked[j] ==  null) dfs(j);
    System.out.println("isTwoColorable = "+isTwoColorable);
  }

  public TwoColorTrace(GraphX G, String quiet) {
    if (G == null) throw new IllegalArgumentException("TwoColorTrace: graph is null");
    g = G; V = g.V();
    marked = new Boolean[G.V()];
    color = new Boolean[G.V()];
    dfsForLoopCount = 0;
    for (int j = 0; j < V; j++)
      if (!isTwoColorable) break;
      else if (marked[j] ==  null) dfsQuiet(j);
  }
  
  private void dfs(int v, String...sa) {
    marked[v] = true;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true","T").replaceAll("false","F").replaceAll("\\[|\\]","")
        .replaceAll("null|,"," ");
    String c = arrayToString(color,900,1,1);
    c = c.replaceAll("true","T").replaceAll("false","F").replaceAll("\\[|\\]","")
        .replaceAll("null|,"," ");
    String s = sa != null && sa.length > 0 ? sa[0] : "";
    System.out.printf("%-12s %"+(V+V+1)+"s    %s\n", s+"dfs("+v+")", m, c);
    for (int w : g.adj(v)) 
      if (marked[w] == null) { 
        color[w] = color[v] == null || !color[v] ? true : false;
        dfs(w,s+" "); 
      }
      else if (color[w] == color[v]) { isTwoColorable = false; return; }
  }
  
  private void dfsQuiet(int v, String...sa) {
    marked[v] = true;
    for (int w : g.adj(v)) {
      dfsForLoopCount++;
      if (marked[w] == null) { 
        color[w] = color[v] == null || !color[v] ? true : false;
        dfsQuiet(w); 
      }
      else if (color[w] == color[v]) { isTwoColorable = false; return; }
    }
  }
  
  public int dfsForLoopCount() { return dfsForLoopCount; }
  
  public void zeroDfsForLoopCount() { dfsForLoopCount = 0; }
  
  public boolean isBipartite() { return isTwoColorable; }
  
  public boolean isTwoColorable() { return isTwoColorable; }

  public static void main(String[] args) {

    // edges are from tinyGex2.txt
//    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
//    GraphX G = new GraphX(12,edges);
//    new TwoColorTrace(G);
    
    int h = 4; TwoColorTrace trace = null;
    System.out.println("h        dfsForLoopCount   ratio");
    trace = new TwoColorTrace(GraphGeneratorX.eulerianCycle(h,h),"quiet");
    double previous = trace.dfsForLoopCount;
    while (h < 8193) {
      h = h*2;
      trace = new TwoColorTrace(GraphGeneratorX.eulerianCycle(h,h),"quiet");
      double x = trace.dfsForLoopCount;
      double r = x/previous;
      System.out.printf("%-7d  %-7d           %3.3f\n", h, trace.dfsForLoopCount, r); 
      previous = r;
      if (trace.isTwoColorable()) { System.out.println("twoColorability detected"); return; }
    }
    
    

  }

}
