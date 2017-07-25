package ex33;

/* p449
  3.3.4  Prove that the height of a 2-3 tree with N keys is between 
  ~ floor(log₃N) .63 lg N (for a tree that is all 3-nodes) and 
  ~ floor(lg N) (for a tree that is all 2-nodes).
  
  A 2-3 tree of height h has at least 
    (1) 1 + 2 + ... + 2^h = (2^(h+1)-1)/(2-1) = 2^(h+1)-1 nodes
        N = 2^(h+1)-1 => h ~ lg(N)
  and at most
    (2) 1 + 3 + ... + 3^h = (3^(h+1)-1)/(3-1) = (3^(h+1)-1)/2 nodes.
        N = (3^(h+1)-1)/2 => h ~ log₃(N) = lg(N)/lg(3) ≅ 0.63*lg(N)
  
*/             

public class Ex3304HeightBoundsOfA23Tree {

  public static void main(String[] args) {
    
  }

}

