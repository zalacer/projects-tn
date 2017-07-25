package analysis;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ds.Digraph;
import ds.Queue;
import ds.Seq;
import ds.Stack;
import graph.EuclidianDigraph;
import graph.SymbolDigraphX;
import st.SeparateChainingHashSTX;

// modified from http://www.geeksforgeeks.org/expression-tree/
// and uses the generic Digraph class ds.Digraph from
// http://www.cs.cornell.edu/courses/cs2110/2011fa/Lectures/L20-Graphs/Digraph.java

//ex4229

public class ArithmeticExpressionEvaluation {
  private Node root;
  private int size;
  private char[] keys;
  private double[] vals;
  String infix;
  String postfix;
  Digraph<Node> g;
  private Map<Node,List<Node>> adj;
  private int evalTreeIterations;
  private int evalDigraphIterations;

  public class Node {
    char k; // the item that can be an operator (+,-,*,/,^) or literal a,b,c...
    Double v; // node value
    Node l, r; // left and right Nodes
    Node(char item) { k = item; l = r = null; }
    Node(char item, Node y, Node x) { k = item; r = y; l = x; }
    
    public void printNode(boolean isRight, String indent, int...dash) {
      if (r != null)  r.printNode(true, indent + (isRight ? "       " : "|      "));
      System.out.print(indent);
      if (isRight) System.out.print(" /");
      else System.out.print(" \\");
      String dashes = dash==null || dash.length==0 ? repeat('-',5) : repeat('-',dash[0]);
      System.out.print(dashes);
      printK();
      if (l != null) l.printNode(false, indent + (isRight ? "|      " : "       "),dash);     
    }
    
    private void printK() {
      if (k == '/') System.out.println("÷");
      else System.out.println(k);
    }
    
    @Override 
    public int hashCode() { return Objects.hash(k,l,r); }
    
    public int hashCode2() {
      // for identifying duplicate nodes 1 level deep
      if (l == null && r == null) return Objects.hash(k);
      if (l != null && r == null) return Objects.hash(k,l.k);
      if (l == null && r != null) return Objects.hash(k,r.k);
      if (l != null && r != null) { 
        // fix for commutativity of + and * by ordering l.k and r.k in the hash computation
        if (k == '+' || k == '*' && r.k < l.k) return Objects.hash(k,r.k,l.k);          
        else return Objects.hash(k,l.k,r.k);
      }
      return 0;
    }
    
    public String toShortString() { return ""+k; }
    
    @Override
    public String toString() { 
      if (l != null && r != null) return "("+k+","+l.k+","+r.k+")";
      if (l != null && r == null) return "("+k+","+l.k+")";
      if (l == null && r != null) return "("+k+","+l.r+")";
      return "("+k+")"; 
    }
  }
  
  public ArithmeticExpressionEvaluation(String postfix, char[] keys, double[] vals) {
    if (postfix == null) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: String postfix is null");
    if (keys == null) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: char[] keys is null");
    if (keys.length == 0) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: char[] keys has zero length");
    if (vals == null) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: double[] vals is null");
    if (vals.length == 0) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: double[] vals has zero length");
    if (keys.length != vals.length) throw new IllegalArgumentException(
        "ArithmeticExpressionEvaluation: keys.length != vals.length");
    this.postfix = postfix;
    char[] expression = postfix.replaceAll("\\s+","").toCharArray();
    root = constructTree(expression);
    infix = infixString();
    this.keys = keys;
    this.vals = vals;
    g = constructDigraph();
  }

  private Node constructTree(char postfix[]) {
    // return root of tree constructed from postfix
    Stack<Node> st = new Stack<>();
    for (int i = 0; i < postfix.length; i++)
      if (!isOp(postfix[i])) st.push(new Node(postfix[i]));
      else st.push(new Node(postfix[i], st.pop(), st.pop()));
    size = postfix.length;
    root = st.peek();
    return root;
  }
  
  private Digraph<Node> constructDigraph() {
    // build Digraph<Node> from tree elminating duplicate Nodes using Node.hashCode2()
    g = new Digraph<>();
    // h maps Node.hashCode2()s to nodes for deduplication
    SeparateChainingHashSTX<Integer,Node> h = new SeparateChainingHashSTX<>(size);
    constructDigraph(root,g,h);
    adj = g.neighbors();
    return g;
  }
  
  private void constructDigraph(Node t,Digraph<Node> g,SeparateChainingHashSTX<Integer,Node> h) {  
    if (t == null) return;
    if (t.l != null)
      if (h.contains(t.l.hashCode2())) g.add(t,h.get(t.l.hashCode2()));
      else { g.add(t,t.l); h.put(t.l.hashCode2(), t.l); constructDigraph(t.l,g,h); }
    if (t.r != null)
      if (h.contains(t.r.hashCode2())) g.add(t,h.get(t.r.hashCode2()));
      else { g.add(t,t.r); h.put(t.r.hashCode2(), t.r); constructDigraph(t.r,g,h); }    
  }
        
  private boolean isOp(char c) { return "+-*/^".indexOf(c) > -1; }
  
  public int size() { return size; }
  
  public Digraph<Node> digraph() { return g; }
  
  public Node root() { return root; }
  
  public int evalTreeIterations() { return evalTreeIterations; }
  
  public int evalDigraphIterations() { return evalDigraphIterations; }
  
  public String infix() { return infix; }
  
  public String postfix() { return postfix; }

  public String infixString() {
    StringBuilder sb = new StringBuilder(); 
    infixString(root,sb);  
    return sb.toString();
  }

  public void infixString(Node t,StringBuilder sb) {
    if (t != null) {
      sb.append("(");
      infixString(t.l,sb);
      sb.append(t.k);
      infixString(t.r,sb);
      sb.append(")");
    }
  }
  
  public double evalTree() { 
    evalTreeIterations = 0;
    double r = evalTree(root); 
    return r;
  }
  
  private double evalTree(Node t) {
    evalTreeIterations++;
    double lval,rval;
    if (t.l != null && t.r != null) {
      lval = evalTree(t.l);
      rval = evalTree(t.r);
      switch (t.k) {
        case '+': return lval + rval;
        case '-': return lval - rval;
        case '*': return lval * rval;
        case '/': return lval / rval;
        case '^': return pow(lval,rval);
      }
    }
    return vals[indexOf(keys,t.k)];
 }
 
 public double evalDigraph() { 
   evalDigraphIterations = 0;
   double r = evalDigraph(root); 
   return r;
 }
 
 public double evalDigraph(Node t) {
   if (t.v != null) return t.v;
   evalDigraphIterations++;
   double lval,rval; 
   List<Node> l = adj.get(t);
   if (!l.isEmpty()) {
     Node[] a = l.toArray(ofDim(Node.class,0));
     if (a.length > 1 && a[0] != null && a[1] != null) {
       lval = evalDigraph(a[0]);
       rval = evalDigraph(a[1]);
       switch (t.k) {
         case '+': t.v = lval + rval; return t.v;
         case '-': t.v = lval - rval; return t.v;
         case '*': t.v = lval * rval; return t.v;
         case '/': t.v = lval / rval; return t.v;
         case '^': t.v = pow(lval,rval); return t.v;
       }
     }
   }
   t.v = vals[indexOf(keys,t.k)];
   return t.v;
 }
  
  public Queue<Node> treeInorder() { 
    Queue<Node> q = new Queue<>();
    treeInorder(root,q);
    return q;
  }
  
  public void treeInorder(Node t, Queue<Node> q) {
    if (t != null) {
      treeInorder(t.l,q);
      q.enqueue(t);
      treeInorder(t.r,q);
    }
  }
    
  public Queue<Node> treePreorder() { 
    Queue<Node> q = new Queue<>();
    treePreorder(root,q);
    return q;
  }
  
  private void  treePreorder(Node t,Queue<Node> q) {
    if (t != null) {
      q.enqueue(t);
      treePreorder(t.l,q);
      treePreorder(t.r,q);
    }
  }
 
  @Override
  public int hashCode() {
    int h = 1;
    Iterator<Node> it = treePreorder().iterator();
    while (it.hasNext()) {
      Node e = it.next();
      h = 31*h + (e==null ? 0 : e.hashCode());
    }
    return h;
  }
  
  @Override 
  public String toString() {
    StringBuilder sb = new StringBuilder("(");
    if (root == null) return sb.append(")").toString();
    Iterator<Node> it = treeInorder().iterator();
    while (it.hasNext()) sb.append(it.next()+",");
    return sb.replace(sb.length()-1,sb.length(),")").toString();
  }

  public void printTree() { root.printNode(false, ""); }
  
  public void plotGraph() {
    if (adj.isEmpty()) { System.out.println("no data to plot"); return; }
    Map<String,double[]> coords = new HashMap<>(); // map of vertex strings -> coordinates
    Map<String,String> lbls = new HashMap<>(); // map of vertex strings -> labels
    Map<String,Seq<String>> smap = new HashMap<>(); // adj map for SymbolDigraphX constructor
    for (Node k : adj.keySet()) {
      Seq<String> seq = new Seq<>();
      for (Node n : adj.get(k)) seq.add(n.toString());
      String kstr = k.toString();
      switch (kstr) {
        case "(+,+,/)" : { 
          coords.put("(+,+,/)", new double[]{50,80}); 
          lbls.put("(+,+,/)", "+"); break;}
        case "(+,a,*)" : {
          coords.put("(+,a,*)", new double[]{30,60});
          lbls.put("(+,a,*)", "+"); break;}
        case "(/,*,d)" : {
          coords.put("(/,*,d)", new double[]{70,60});
          lbls.put("(/,*,d)", "/"); break;}
        case "(a)"     : {
          coords.put("(a)",     new double[]{10,40});
          lbls.put("(a)", "a"); break;}
        case "(*,b,c)" : {
          coords.put("(*,b,c)", new double[]{50,40});
          lbls.put("(*,b,c)", "*"); break;}
        case "(d)"     : {
          coords.put("(d)",     new double[]{90,40});
          lbls.put("(d)", "d"); break;}
        case "(b)"     : {
          coords.put("(b)",     new double[]{30,20});
          lbls.put("(b)", "b"); break;}
        case "(c)"     : {
          coords.put("(c)",     new double[]{70,20});
          lbls.put("(c)", "c"); break;}
      }
      smap.put(kstr, seq);      
    }
    SymbolDigraphX sd = new SymbolDigraphX(smap);
    EuclidianDigraph ed = new EuclidianDigraph(sd.digraph());
    String[] labels = new String[lbls.size()];
    for (String s : coords.keySet()) {
      int v = sd.index(s);
      double[] c = coords.get(s);
      ed.addCoords(v, c[0], c[1]);
      labels[v] = lbls.get(s);
    }
    ed.showLabelled(3.31,labels);
  }
  
  public static void main(String args[]) {    

    // define variables for evaluation
    char[] keys = {'a','b','c','d'}; // must be sorted 4 binarySearch lookup
    double[] vals = {2,3,4,5};       // must match keys
    // define postfix expression 
    String postfix = "a b c * + b c * d / +";
    // expression based on Algorithm Design Manual-2ed-Skiena-2008.pdf p186 (pdf p198)
    // corresponding infix expression is "a + b * c + (b*c)/d"
    postfix = "a b c * + b c * d / +";
    ArithmeticExpressionEvaluation aee = new ArithmeticExpressionEvaluation(postfix,keys,vals);
    System.out.println("postfix expression is: "+aee.postfix());
    System.out.println("infix expression is: "+aee.infix());
    System.out.println("expression tree evaluates to: "+aee.evalTree());
    System.out.println("evalTreeIterations = "+aee.evalTreeIterations);
    System.out.println("tree size = "+aee.size());
    System.out.println("tree:");
    aee.printTree();
    Digraph<Node> g = aee.digraph();
    System.out.println("Digraph:"+g);
    System.out.println("isDag = "+g.isDag()); // true
    System.out.println("expression digraph evaluates to: "+aee.evalDigraph());
    System.out.println("evalDigraphIterations = "+aee.evalDigraphIterations);
    aee.plotGraph();

  }
/* 
    postfix expression is: a b c * + b c * d / +
    infix expression is: (((a)+((b)*(c)))+(((b)*(c))/(d)))
    expression tree evaluates to: 16.4
    evalTreeIterations = 11
    tree size = 11
    tree:
    |              /-----d
    |       /-----÷
    |      |      |       /-----c
    |      |       \-----*
    |      |              \-----b
     \-----+
           |              /-----c
           |       /-----*
           |      |       \-----b
            \-----+
                   \-----a
    Digraph:
      (+,+,/) -> [(+,a,*), (/,*,d)]
      (b) -> []
      (a) -> []
      (d) -> []
      (c) -> []
      (*,b,c) -> [(b), (c)]
      (+,a,*) -> [(a), (*,b,c)]
      (/,*,d) -> [(*,b,c), (d)]
      V = 8
      E = 8
    isDag = true
    expression digraph evaluates to: 16.4
    evalDigraphIterations = 8
    
    < graph plotted in separate window >

*/
  
}

