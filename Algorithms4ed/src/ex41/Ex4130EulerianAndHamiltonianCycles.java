package ex41;

import ds.Seq;
import graph.EulerianCycleX;
import graph.GraphX;
import graph.HamiltonianCycleX;
import v.Tuple3;

/* p562
  4.1.30 Eulerian and Hamiltonian cycles. Consider the graphs defined by the following
  four sets of edges:
    a : 0-1 0-2 0-3 1-3 1-4 2-5 2-9 3-6 4-7 4-8 5-8 5-9 6-7 6-9 7-8
    b : 0-1 0-2 0-3 1-3 0-3 2-5 5-6 3-6 4-7 4-8 5-8 5-9 6-7 6-9 8-8
    c : 0-1 1-2 1-3 0-3 0-4 2-5 2-9 3-6 4-7 4-8 5-8 5-9 6-7 6-9 7-8
    d : 4-1 7-9 6-2 7-3 5-0 0-2 0-8 1-6 3-9 6-3 2-8 1-5 9-8 4-5 4-7
  Which of these graphs have Euler cycles (cycles that visit each edge exactly once)?
  Which of them have Hamilton cycles (cycles that visit each vertex exactly once)?
  
  This is implemented using graph.EulerianCycleX and graph.HamiltonianCycleX
  that are used to test the sample graphs below.
  
 */                                                   

public class Ex4130EulerianAndHamiltonianCycles {
  
  public static final Seq<Tuple3<String,Integer,String>> s = new Seq<>(
      new Tuple3<String,Integer,String>("a",10,
          "0-1 0-2 0-3 1-3 1-4 2-5 2-9 3-6 4-7 4-8 5-8 5-9 6-7 6-9 7-8".replaceAll("-", " ")),
      new Tuple3<String,Integer,String>("b",10,
          "0-1 0-2 0-3 1-3 0-3 2-5 5-6 3-6 4-7 4-8 5-8 5-9 6-7 6-9 8-8".replaceAll("-", " ")),
      new Tuple3<String,Integer,String>("c",10,
          "0-1 1-2 1-3 0-3 0-4 2-5 2-9 3-6 4-7 4-8 5-8 5-9 6-7 6-9 7-8".replaceAll("-", " ")),
      new Tuple3<String,Integer,String>("d",10,
          "4-1 7-9 6-2 7-3 5-0 0-2 0-8 1-6 3-9 6-3 2-8 1-5 9-8 4-5 4-7".replaceAll("-", " ")));  

  public static final Tuple3<String,Integer,String>[] t = s.to();
  
  public static void testEulerian() {
    EulerianCycleX e; boolean b;
    for (Tuple3<String,Integer,String> x : t) {
      e = new EulerianCycleX(new GraphX(x._2,x._3));
      b = e.hasEulerianCycle();
      if (b) System.out.println(x._1+" has eulerian cycle: "+e.cycleToString());
      else {
        String r = ""+x._1+" doesn't have an eulerian cycle";
        if (e.hasCause()) System.out.println(r+" because "+e.cause());
        else System.out.println(r);
      }
    }
    System.out.println();
  }
  
  public static void testHamiltonian() {
    HamiltonianCycleX h = new HamiltonianCycleX(); boolean b;
    for (Tuple3<String,Integer,String> x : t) {
      h.findHamiltonianCycle(new GraphX(x._2,x._3).hArray());
      b = h.hasHamiltonianCycle();
      if (b) System.out.println(x._1+" has hamiltonian cycle: "+h.cycleToString());
      else System.out.println(x._1+" doesn't have a hamiltonian cycle");
    }
  }

  public static void main(String[] args) {
    
    testEulerian();    
    testHamiltonian();
/*    
    a doesn't have an eulerian cycle because a vertex has an odd degree
    b doesn't have an eulerian cycle because a vertex has an odd degree
    c doesn't have an eulerian cycle because a vertex has an odd degree
    d doesn't have an eulerian cycle because a vertex has an odd degree
    
    a has hamiltonian cycle: 0 1 3 6 7 4 8 5 9 2 0 
    b doesn't have a hamiltonian cycle
    c has hamiltonian cycle: 0 1 2 9 5 8 4 7 6 3 0 
    d has hamiltonian cycle: 0 2 6 1 5 4 7 3 9 8 0 

*/ 
    
  }

}



