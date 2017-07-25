package ex42;

/* p597
  4.2.8  Draw all the nonisomorphic DAGs with two, three, four, and five 
  vertices (see Exercise 4.1.28).
  
  using lower case letters to represent vertices:
  
  0. (1 ordering)
  1. (1 ordering) a
  2. (3 orderings) a b, a>b, b<a
  3. (25 orderings)
     a b c
     a>b c  a<b c
     a b>c  a b<c
     a>c b  a<c b  
     a>b>c  a>c>b  b>a>c  b>c>a  c>a>b  c>b>a
     a>b<c  a>c<b  b>a<c  b>c<a  c>a<b  c>b<a
     a<b>c  a<c>b  b<a>c  b<c>a  c<a>b  c<b>a
  4. (543 orderings)...
  5. (29281 orderings)...
  
  
  There are some ways to approach this at 
  https://cs.stackexchange.com/questions/60460/generating-all-directed-acyclic-graphs-with-constraints?noredirect=1&lq=1
 
  According to 
    https://en.wikipedia.org/wiki/Directed_acyclic_graph#Combinatorial_enumeration
  there are 29281 DAGs with 5 vertices. After or while enumerating 
  them it would be necessary to filter out those that are isomorphic to others.
  
  A way to enumerate graphs interactively (slowly) is by using 
  http://treedecompositions.com/#/databasequery
   
 */                                                   

public class Ex4208NonIsomorphicDagsWith2To5Vertices {

  public static void main(String[] args) {
    
  }

}



