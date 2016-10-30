package ex15;

import ds.QuickFindUF;

/*
  p235
  1.5.1  Show the contents of the  id[] array and the number of times the ar-
  ray is accessed for each input pair when you use quick-find for the sequence
  9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2.
  
*/

public class Ex1501QuickFind {

  public static void main(String[] args) {
    
    QuickFindUF uf = new QuickFindUF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
    uf.printIdArrayAccesses(); // counting accesses after initialization
    uf.printIdArray();
    System.out.println();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("union("+p+", "+q+")");
      uf.printIdArrayAccesses();
      uf.printIdArray();
      System.out.println();
    }
/*
    0
    id[0,1,2,3,4,5,6,7,8,9]

    union(9, 0)
    24
    id[0,1,2,3,4,5,6,7,8,0]     0 1 2 3 4 5 6 7 8
                                |
                                9
    union(3, 4)
    48
    id[0,1,2,4,4,5,6,7,8,0]     0 1 2 4 5 6 7 8
                                |     |
                                9     3
    union(5, 8)
    72
    id[0,1,2,4,4,8,6,7,8,0]     0 1 2 4 6 7 8
                                |     |     |
                                9     3     5
    union(7, 2)
    96
    id[0,1,2,4,4,8,6,2,8,0]     0 1 2 4 6 8
                                |   | |   |
                                9   7 3   5
    union(2, 1)
    120
    id[0,1,1,4,4,8,6,1,8,0]     0   1   4 6 8
                                |  / \  |   |
                                9 2   7 3   5
    union(5, 7)
    144
    id[0,1,1,4,4,1,6,1,1,0]     0     1     4 6 
                                |    /|\    |  
                                9   / | \   3  
                                   / / \ \
                                  2 5   7 8
    union(0, 3)
    168
    id[4,1,1,4,4,1,6,1,1,4]     0      1       4   6
                                |     /|\     /|\  
                                9    / | \   0 3 9  
                                    / / \ \
                                   2 5   7 8
    union(4, 2)                 
    192                           
    id[1,1,1,1,1,1,6,1,1,1]            1         6
                                      /|\
                                     / | \ 
                                    / /|\ \
                                   / / | \ \ 
                                  / / /|\ \ \ 
                                 / / / | \ \ \ 
                                / / / / \ \ \ \
                               0 2 3 4   5 7 8 9

*/

  }

}
