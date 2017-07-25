package st;

import static v.ArrayUtils.*;

// AlgorithmsInJava-3edPt5-GraphAlgorithms-Sedgewick-2003.pdf pp58-59 program 17.5

@SuppressWarnings("unused")
public class STidx {
  private final static int END = 0;
  private int N, val;
  private class Node { char c; int v; Node l, m, r; }
  private class Node2 { int l,r; }
  private Node head;
  private Node2[] adj;

  public STidx(int v) {
    head = null; N = 0; adj = fill(v,()->new Node2());
  }

  private Node indexR(Node h, char[] s, int i) { 
    char ch = (i < s.length) ? s[i] : END;
    if (h == null) { h = new Node(); h.c = ch; h.v = -1; }
    if (ch == END) {
      if (h.v == -1) h.v = N++;
      val = h.v;
      return h;
    }
    if (s[i] < h.c) h.l = indexR(h.l, s, i);
    if (s[i] == h.c) h.m = indexR(h.m, s, i+1);
    if (s[i] > h.c) h.r = indexR(h.r, s, i);
    return h;
  }

  public int index(String key) { 
    char[] s = key.toCharArray();
    head = indexR(head, s, 0); return val; 
  }
  
  public int compressR(Node h) {
    //AlgorithmsInJava-3edPt5-GraphAlgorithms-Sedgewick-2003.pdf p207 program 19.5
    if (h == null) return 0;
    int l = compressR(h.l);
    int r = compressR(h.r);
    int t = index(l + "" + r);
    adj[t].l = l; adj[t].r = r;
    return t;
  }
  /*
class ST
{
private final static int END = 0;
private int N, val;
private class Node
{ char c; int v; Node l, m, r; }
private Node head;
private Node indexR(Node h, char[] s, int i)
{ char ch = (i < s.length) ? s[i] : END;
if (h == null)
{ h = new Node(); h.c = ch; h.v = -1; }
if (ch == END)
{
if (h.v == -1) h.v = N++;
val = h.v;
return h;
}
if (s[i] < h.c) h.l = indexR(h.l, s, i);
if (s[i] == h.c) h.m = indexR(h.m, s, i+1);
if (s[i] > h.c) h.r = indexR(h.r, s, i);
return h;
}
ST()
{ head = null; N = 0; }
int index(String key)
{ char[] s = key.toCharArray();
head = indexR(head, s, 0); return val; }
}
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
