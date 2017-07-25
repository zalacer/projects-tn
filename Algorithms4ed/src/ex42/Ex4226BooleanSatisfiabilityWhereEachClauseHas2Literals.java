package ex42;

import static v.ArrayUtils.take;
import static v.ArrayUtils.toArray;

import java.util.Arrays;
import java.util.Comparator;

import ds.BagX;
import ds.Seq;
import graph.DigraphX;
import graph.DirectedCycleX;
import graph.KosarajuSharirSCCX;
import graph.SymbolDigraphX;
import graph.TopologicalX;
import st.SeparateChainingHashSTX;

/* p599
  4.2.26 2-satisfiability. Given boolean formula in conjunctive normal form with M
  clauses and N literals such that each clause has exactly two literals, find a 
  satisfying assignment (if one exists). Hint: Form the implication digraph with 2N 
  vertices (one per literal and its negation). For each clause x + y, include edges 
  from y' to x and from x' to y. To satisfy the clause x + y, (i) if y is false, then 
  x is true and (ii) if x is false, then y is true. Claim: The formula is satisfiable 
  if and only if no variable x is in the same strong component as its negation x'. 
  Moreover, a topological sort of the kernel DAG (contract each strong component to a 
  single vertex) yields a satisfying assignment.
  
  This is done in main below with help from extensions to SymbolDigraphX including
  a constructor that accepts 2-CNF expression strings and components() that returns
  the strong components as a Seq<Seq<String>> and using the algorithm described at
  https://en.wikipedia.org/wiki/2-satisfiability#Strongly_connected_components.
  
 */                                                   

public class Ex4226BooleanSatisfiabilityWhereEachClauseHas2Literals {
  
  public static void main(String[] args) {
    
    boolean x0,x1,x2,x3,x4,x5,x6; x0=x1=x2=x3=x4=x5=x6=false;
    
    // this expression is from 
    //   https://en.wikipedia.org/wiki/2-satisfiability#Problem_representations
    // that shows a picture of its implication graph
    boolean expression = (x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4)
        && (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6);
    
    System.out.println("expression = "+expression);
    
    String formula = "(x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4) "
        + "&& (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6)";
    
    SymbolDigraphX sg = new SymbolDigraphX(formula);
    DigraphX g = sg.G(); BagX<Integer>[] adj = g.adj();
    Seq<Seq<String>> sgcomponents = sg.components();
    Seq<Integer> topOrder;
    // test if g is a DAG
    DirectedCycleX finder = new DirectedCycleX(g);
    if (finder.hasCycle()) {
      TopologicalX topological = new TopologicalX(g);
      topOrder = new Seq<>(toArray(topological.order().iterator()));
    }
    else {
      // test that no component and its negation are in the same strong component
      for (Seq<String> seq : sgcomponents) {
        if (seq.size() < 2) continue;
        for (String s : seq) {
          if (s.startsWith("!")) {
            String s2 = s.substring(1,s.length());
            if (seq.contains(s2)) {
              System.out.println("the formula isn't satisfieable");
              return;
            } 
          } else {
            String s2 = "!"+s;
            if (seq.contains(s2)) {
              System.out.println("the formula isn't satisfieable");
              return;
            } 
          }
        }
      }
      // construct condensation (AKA kernel) DAG of components in Digraph g
      // https://en.wikipedia.org/wiki/2-satisfiability#Problem_representations    
      Seq<Seq<Integer>> components = (new KosarajuSharirSCCX(g)).components();
      DigraphX condensation = new DigraphX(components.size());
      for (int i = 0; i < components.size()-1; i++) {
        Seq<Integer> c = components.get(i);
        for (int j : c) {
          for (int k = i+1; k < components.size(); k++) {
            Seq<Integer> c2 = components.get(k);
            for (int j2 : c2) {
              if (adj[j].contains(j2)) condensation.addEdge(i,k);
              if (adj[j2].contains(j)) condensation.addEdge(k,i);
            }
          }
        }  
      }
      TopologicalX topological = new TopologicalX(condensation);
      topOrder = new Seq<>(toArray(topological.order().iterator()));
    }
    Seq<Integer> reverseOrder = topOrder.reverse();
    String[] keys = new String[sgcomponents.size()];
    for (int i = 0; i < keys.length; i++) keys[i] = sgcomponents.get(i).get(0);
    Seq<String> reverseTop = new Seq<>();
    for (int i : reverseOrder) reverseTop.add(keys[i]);
    SeparateChainingHashSTX<String,Boolean> map = new SeparateChainingHashSTX<>();
    for (String s : reverseTop) {
      String r = s.startsWith("!") ? s.substring(1,s.length()) : "!"+s; 
      if (!(map.contains(s) || map.contains(r))) {
        map.put(s,true);
        if (s.startsWith("!")) map.put(s.substring(1,s.length()),false);
        else map.put("!"+s,false);
      }
    }
    Comparator<String> cmptr = (s1,s2) -> {
      if (s1.startsWith("!") && !s2.startsWith("!")) return 1;
      if (!s1.startsWith("!") && s2.startsWith("!")) return -1;
      return s1.compareTo(s2);
    };
    String[] mkeys = toArray(map.keys().iterator());
    Arrays.sort(mkeys,cmptr);
    mkeys = take(mkeys,mkeys.length/2);
    for (String k : mkeys) {
      boolean b = map.get(k);
      switch(k) {
        case "x0":  x0 = b;  break;
        case "x1":  x1 = b;  break;
        case "x2":  x2 = b;  break;
        case "x3":  x3 = b;  break;
        case "x4":  x4 = b;  break;
        case "x5":  x5 = b;  break;
        case "x6":  x6 = b;  break;
        case "!x0": x0 = !b; break;
        case "!x1": x1 = !b; break;
        case "!x2": x2 = !b; break;
        case "!x3": x3 = !b; break;
        case "!x4": x4 = !b; break;
        case "!x5": x5 = !b; break;
        case "!x6": x6 = !b; break;
      }
    }
    
    System.out.println("\na solution:");
    System.out.println("x0 = "+x0);
    System.out.println("x1 = "+x1);
    System.out.println("x2 = "+x2);
    System.out.println("x3 = "+x3);
    System.out.println("x4 = "+x4);
    System.out.println("x5 = "+x5);
    System.out.println("x6 = "+x6);

    expression = (x0 || x2) && (x0 ||!x3) && (x1 ||!x3) && (x1 ||!x4) && (x2 ||!x4)
        && (x0 ||!x5) && (x1 ||!x5) && (x2 ||!x5) && (x3 || x6) && (x4 || x6) && (x5 || x6);
    System.out.println("\nproof of the solution:");
    System.out.println("expression = "+expression);
/* 
    expression = false
    
    a solution:
    x0 = false
    x1 = false
    x2 = true
    x3 = false
    x4 = false
    x5 = false
    x6 = true
    
    proof of the solution:
    expression = true
*/
  }

}



