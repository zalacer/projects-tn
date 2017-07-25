package ex42;

import analysis.ArithmeticExpressionEvaluation;
import analysis.ArithmeticExpressionEvaluation.Node;
import ds.Digraph;

/* p599
  4.2.29 Arithmetic expressions. Write a class that evaluates DAGs that represent 
  arithmetic expressions. Use a vertex-indexed array to hold values corresponding
  to each vertex. Assume that values corresponding to leaves have been established. 
  Describe a family of arithmetic expressions with the property that the size of the 
  expression tree is exponentially larger than the size of the corresponding DAG (so 
  the running time of your program for the DAG is proportional to the logarithm of 
  the running time for the tree).
  
  This is done in analysis.ArithmeticExpressionEvaluation and demonstrated below. 
  It compresses an expression tree to a DAG by identifying subtrees of depth 1 using
  hashcodes computed with Node.hashCode2() and applying that in constructDigraph().
  Larger subtrees could be deduplicated by comparing subtrees using their , but this isn't necessary for the simple expression tree that I used 
  for demonstration nor to demonstrate exponential performance improvement for some 
  classes of expressions. For example, for the following family of expressions the
  treeSize = (2^(dagSize-1)-1) and dagSize = ln(treeSize+1)+1 so treeSize ~ 2^dagSize
  and dagSize ~ ln(treeSize). Using postfix notation, the expression family is given by
  String init = "a b *", where a and b represent double variables, and 
  Function<String,String) generator = (exp) -> { return exp+" "+exp+" *"; };
  Alternatively a stream of this family's expressions can be generated with:
    Stream.iterate(init, s -> s+" "+s+" *").limit(x).forEach(System.out::println);
  For example for x = 4 this stream generates:
    a b *
    a b * a b * *
    a b * a b * * a b * a b * * *
    a b * a b * * a b * a b * * * a b * a b * * a b * a b * * * *
  Any arithmetic binary operator can be used similarly in place of "*".
 */  


public class Ex4229UseDAGs2EvaluateArithmeticExpressions {
  
  public static void main(String[] args) {
    
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
    System.out.println("evalTreeIterations = "+aee.evalTreeIterations());
    System.out.println("tree size = "+aee.size());
    System.out.println("tree:");
    aee.printTree();
    Digraph<Node> g = aee.digraph();
    System.out.println("Digraph: "+g);
    System.out.println("isDag = "+g.isDag()); // true
    System.out.println("expression digraph evaluates to: "+aee.evalDigraph());
    System.out.println("evalDigraphIterations = "+aee.evalDigraphIterations());
    
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
      Node(k=+,l.k=+,r.k=/) -> [Node(k=+,l.k=a,r.k=*), Node(k=/,l.k=*,r.k=d)]
      Node(k=b) -> []
      Node(k=a) -> []
      Node(k=d) -> []
      Node(k=c) -> []
      Node(k=*,l.k=b,r.k=c) -> [Node(k=b), Node(k=c)]
      Node(k=+,l.k=a,r.k=*) -> [Node(k=a), Node(k=*,l.k=b,r.k=c)]
      Node(k=/,l.k=*,r.k=d) -> [Node(k=*,l.k=b,r.k=c), Node(k=d)]
      V = 8
      E = 8
    isDag = true
    expression digraph evaluates to: 16.4
    evalDigraphIterations = 8
*/

}



