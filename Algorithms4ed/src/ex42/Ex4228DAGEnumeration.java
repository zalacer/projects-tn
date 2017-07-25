package ex42;

import static java.math.BigInteger.*;
import static v.ArrayUtils.*;

import static java.lang.Math.*;

import java.math.BigInteger;
import java.util.function.Consumer;

/* p599
  4.2.28 DAG enumeration. Give a formula for the number of V-vertex DAGs with E
  edges.
  
  A method that calculates the number of V-vertex DAGs is given below. I believe 
  this is for DAGs in which any pair of vertices may have no edge, an edge in either 
  direction or two edges in opposite directions.

 */  

public class Ex4228DAGEnumeration {
  
  public static long numberOfDAGs(int V) {
    // recurrence formula based on http://mathworld.wolfram.com/AcyclicDigraph.html
    // and derived in Graphical Enumerations, Harary, New York: Academic Press, 1973
    // also see https://en.wikipedia.org/wiki/Directed_acyclic_graph#Combinatorial_enumeration
    if (V < 0) throw new IllegalArgumentException("numberOfDAGs: V is < 0");
    if (V > 10) throw new IllegalArgumentException(
        "numberOfDAGs: V is > 10 : use numberOfDAGsBigInt to avoid long overflow");
    if (V == 0) return 1;
    long[] a = new long[V+1];
    a[0] = 1;
    Consumer<Integer> r = n -> {
      for (int k = 1; k <= n; k++)
        a[n] += (long)(pow(-1,k-1)
            *(factorial(n)/(factorial(n-k)*factorial(k))
                *pow(2,k*(n-k))*a[n-k]));
    };
    for (int i = 1; i <= V; i++) r.accept(i);
    return a[V];
  }
  
  public static BigInteger numberOfDAGsBigInt(int V) {
    // recurrence formula based on http://mathworld.wolfram.com/AcyclicDigraph.html
    // and derived in Graphical Enumerations, Harary, New York: Academic Press, 1973
    // also see https://en.wikipedia.org/wiki/Directed_acyclic_graph#Combinatorial_enumeration
    if (V < 0) throw new IllegalArgumentException("numberOfDAGsBigInt: V is < 0");
    if (V == 0) return ONE;
    BigInteger[] a = fill(V+1,()->ZERO);
    a[0] = ONE;
    Consumer<Integer> r = n -> {
      BigInteger x,N,NK,K,y,z;
      for (int k = 1; k <= n; k++) {
        x = ONE.negate().pow(k-1);
        N = factorial(new BigInteger(""+n));
        NK = factorial(new BigInteger(""+(n-k)));
        K = fact(new BigInteger(""+k));
        y = N.divide(NK.multiply(K));
        z = new BigInteger(""+2).pow(k*(n-k));
        a[n] = a[n].add(x.multiply(y).multiply(z).multiply(a[n-k]));
      }
    };
    for (int i = 1; i <= V; i++) r.accept(i);
    return a[V];
  }
  
  public static void main(String[] args) {

    System.out.println(numberOfDAGs(10));
    //4175098976430598143 (= 0.45266513805880604*Long.MAX_VALUE)
    
    System.out.println(numberOfDAGsBigInt(10));
    //4175098976430598143 
    
    System.out.println(numberOfDAGsBigInt(20));
    //2344880451051088988152559855229099188899081192234291298795803236068491263 

  }
    

}



