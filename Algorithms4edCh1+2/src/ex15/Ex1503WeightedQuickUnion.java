package ex15;

import ds.WeightedQuickUnionUF;

/*
  p235
  1.5.3  Do Exercise 1.5.1, but use weighted quick-union (page 228).
*/

public class Ex1503WeightedQuickUnion {

  public static void main(String[] args) {
    
    WeightedQuickUnionUF uf = new WeightedQuickUnionUF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
    uf.printParentArrayAccesses(); // counting accesses after initialization
    uf.printSizeArrayAccesses();
    uf.printTotalArrayAccesses();
    uf.printArrays();;
    System.out.println();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("union("+p+", "+q+")");
      uf.printParentArrayAccesses();
      uf.printSizeArrayAccesses();
      uf.printTotalArrayAccesses();
      uf.printArrays();;
      System.out.println();
    }
/*
    parentArrayAccesses=0
    sizeArrayAccesses=0
    totalArrayAccesses=0
    size[1,1,1,1,1,1,1,1,1,1]
    parent[0,1,2,3,4,5,6,7,8,9]

    union(9, 0)
    parentArrayAccesses=9
    sizeArrayAccesses=4
    totalArrayAccesses=13
    size[1,1,1,1,1,1,1,1,1,2]
    parent[9,1,2,3,4,5,6,7,8,9]     1 2 3 4 5 6 7 8 9
                                                    |
                                                    0
    union(3, 4)
    parentArrayAccesses=18
    sizeArrayAccesses=8
    totalArrayAccesses=26
    size[1,1,1,2,1,1,1,1,1,2]
    parent[9,1,2,3,3,5,6,7,8,9]     1 2 3 5 6 7 8 9
                                        |         |
                                        4         0
    union(5, 8)
    parentArrayAccesses=27
    sizeArrayAccesses=12
    totalArrayAccesses=39
    size[1,1,1,2,1,2,1,1,1,2]
    parent[9,1,2,3,3,5,6,7,5,9]     1 2 3 5 6 7 9
                                        | |     |
                                        4 8     0
    union(7, 2)
    parentArrayAccesses=36
    sizeArrayAccesses=16
    totalArrayAccesses=52
    size[1,1,1,2,1,2,1,2,1,2]
    parent[9,1,7,3,3,5,6,7,5,9]     1 3 5 6 7 9
                                      | |   | |
                                      4 8   2 0
    union(2, 1)
    parentArrayAccesses=45
    sizeArrayAccesses=20
    totalArrayAccesses=65
    size[1,1,1,2,1,2,1,3,1,2]
    parent[9,7,7,3,3,5,6,7,5,9]     3 5 6   7   9
                                    | |    / \  |
                                    4 8   1   2 0
    union(5, 7)
    parentArrayAccesses=54
    sizeArrayAccesses=24
    totalArrayAccesses=78
    size[1,1,1,2,1,2,1,5,1,2]
    parent[9,7,7,3,3,7,6,7,5,9]     3 5 6   7   9
                                    | |    /|\  |
                                    4 8   1 2 5 0
                                              |
                                              8
    union(0, 3)
    parentArrayAccesses=63
    sizeArrayAccesses=28
    totalArrayAccesses=91
    size[1,1,1,2,1,2,1,5,1,4]
    parent[9,7,7,9,3,7,6,7,5,9]     5 6   7     9
                                    |    /|\   / \
                                    8   1 2 5 0   3
                                            |     |
                                            8     4
    union(4, 2)
    parentArrayAccesses=72
    sizeArrayAccesses=32
    totalArrayAccesses=104
    size[1,1,1,2,1,2,1,9,1,4]
    parent[9,7,7,9,3,7,6,7,5,7]     6      7
                                          /|\
                                         / | \
                                        / /|  \
                                       1 2 5   9
                                           |  / \
                                           8 0   3
                                                 |
                                                 4
   
*/

  }

}
