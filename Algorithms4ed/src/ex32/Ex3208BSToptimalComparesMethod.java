package ex32;

/* p417
  3.2.8  Write a static method  optCompares() that takes an integer 
  argument N and computes the number of compares required by a random 
  search hit in an optimal (perfectly balanced) BST, where all the 
  null links are on the same level if the number of links is a power 
  of 2 or on one of two levels otherwise.
  
  This is already implemented in 
    https://github.com/nagajyothi/AlgorithmsByRobertSedgewick/blob/master/Searching/BinarySearchTrees/BST.java

  as
    public double optCompares(int N) { return log(N); }
    
  I can agree with this as giving the order of growth, however more precisely
  it should be lg(N) as mentioned at 
    https://www.cs.auckland.ac.nz/~jmor159/PLDS210/niemann/s_rbt.htm
  and also in Introduction to Algorithms, Cormen et al., 3ed, 2009, MIT, p311
  that proves it by comparison with a BST of height h, for which search
  runs in O(h) time, since a Red-Black (balanced BST) is a BST with N nodes
  and height lg(n).
 
*/

public class Ex3208BSToptimalComparesMethod {
  
  public static void main(String[] args) {

  }

}

