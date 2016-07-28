package ex15;

import ds.QuickUnionUF;

/*
  p235
  1.5.2  Do Exercise 1.5.1, but use quick-union (page 224). In addition, draw the forest of
  trees represented by the id[] array after each input pair is processed.
*/

public class Ex1502QuickUnion {

  public static void main(String[] args) {
    
    QuickUnionUF uf = new QuickUnionUF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
    uf.printParentArrayAccesses(); // counting accesses after initialization
    uf.printParentArray();
    System.out.println();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("union("+p+", "+q+")");
      uf.printParentArrayAccesses();
      uf.printParentArray();
      System.out.println();
    }
/*
    0
    parent[0,1,2,3,4,5,6,7,8,9]
    
    union(9, 0)
    5
    parent[0,1,2,3,4,5,6,7,8,0]     0 1 2 3 4 5 6 7 8
                                    |
                                    9
    union(3, 4)                     
    10
    parent[0,1,2,4,4,5,6,7,8,0]     0 1 2 4 5 6 7 8
                                    |     |  
                                    9     3    
    union(5, 8)
    15
    parent[0,1,2,4,4,8,6,7,8,0]     0 1 2 4 6 7 8
                                    |     |     |
                                    9     3     5                                    
    union(7, 2)
    20
    parent[0,1,2,4,4,8,6,2,8,0]     0 1 2 4 6 8
                                    |   | |   |
                                    9   7 3   5                                     
    union(2, 1)
    25
    parent[0,1,1,4,4,8,6,2,8,0]     0 1 4 6 8
                                    | | |   |
                                    9 2 3   5      
                                      |
                                      7
    union(5, 7)
    42
    parent[0,1,1,4,4,8,6,2,1,0]     0     1      4  6
                                    |    / \     |  
                                    9   2   8    3        
                                        |   |
                                        7   5                                       
    union(0, 3)
    51
    parent[4,1,1,4,4,8,6,2,1,0]     0     1       4    6
                                    |    / \     / \ 
                                    9   2   8   3   0     
                                        |   |       |
                                        7   5       9                               
    union(4, 2)
    60
    parent[4,1,1,4,1,8,6,2,1,0]         1      6
                                       /|\   
                                      2 4 8
                                     / / \ \   
                                    7 3   0 5                                
                                          |
                                          9
   

*/

  }

}
