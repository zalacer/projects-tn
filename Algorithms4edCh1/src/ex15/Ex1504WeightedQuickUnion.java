package ex15;

import ds.WeightedQuickUnionUF;

/*
  p235
  1.5.4  Show the contents of the  sz[] and  id[] arrays and the number of array accesses
  for each input pair corresponding to the weighted quick-union examples in the text
  (both the reference input and the worst-case input).
  
  Since I'm using your WeightedQuickUnionUF class I'm showing the contents of the parent
  and size arrays.
  
*/

public class Ex1504WeightedQuickUnion {
  
  // ref from p229 and is same as data in tinyUF.txt
  public static String reference = "4-3 3-8 6-5 9-4 2-1 8-9 5-0 7-2 6-1 1-0 6-7";
  
  // worstcase is from p229
  public static String worstcase = "0-1 2-3 4-5 6-7 0-2 4-6 0-4";
  
  public static void testWeightedQuickUnionUF(String data) {
    WeightedQuickUnionUF uf = new WeightedQuickUnionUF(10);
    String[] sa = data.split("\\s+");
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
  }

  public static void main(String[] args) {
    
    testWeightedQuickUnionUF(reference);
/*
    parentArrayAccesses=0
    sizeArrayAccesses=0
    totalArrayAccesses=0
    size[1,1,1,1,1,1,1,1,1,1]
    parent[0,1,2,3,4,5,6,7,8,9]
    
    union(4, 3)
    parentArrayAccesses=9
    sizeArrayAccesses=4
    totalArrayAccesses=13
    size[1,1,1,1,2,1,1,1,1,1]
    parent[0,1,2,4,4,5,6,7,8,9]
    
    union(3, 8)
    parentArrayAccesses=18
    sizeArrayAccesses=8
    totalArrayAccesses=26
    size[1,1,1,1,3,1,1,1,1,1]
    parent[0,1,2,4,4,5,6,7,4,9]
    
    union(6, 5)
    parentArrayAccesses=27
    sizeArrayAccesses=12
    totalArrayAccesses=39
    size[1,1,1,1,3,1,2,1,1,1]
    parent[0,1,2,4,4,6,6,7,4,9]
    
    union(9, 4)
    parentArrayAccesses=36
    sizeArrayAccesses=16
    totalArrayAccesses=52
    size[1,1,1,1,4,1,2,1,1,1]
    parent[0,1,2,4,4,6,6,7,4,4]
    
    union(2, 1)
    parentArrayAccesses=45
    sizeArrayAccesses=20
    totalArrayAccesses=65
    size[1,1,2,1,4,1,2,1,1,1]
    parent[0,2,2,4,4,6,6,7,4,4]
    
    union(5, 0)
    parentArrayAccesses=58
    sizeArrayAccesses=24
    totalArrayAccesses=82
    size[1,1,2,1,4,1,3,1,1,1]
    parent[6,2,2,4,4,6,6,7,4,4]
    
    union(7, 2)
    parentArrayAccesses=67
    sizeArrayAccesses=28
    totalArrayAccesses=95
    size[1,1,3,1,4,1,3,1,1,1]
    parent[6,2,2,4,4,6,6,2,4,4]
    
    union(6, 1)
    parentArrayAccesses=76
    sizeArrayAccesses=32
    totalArrayAccesses=108
    size[1,1,3,1,4,1,6,1,1,1]
    parent[6,2,6,4,4,6,6,2,4,4]

*/
    
    testWeightedQuickUnionUF(worstcase);
    
/*
    parentArrayAccesses=0
    sizeArrayAccesses=0
    totalArrayAccesses=0
    size[1,1,1,1,1,1,1,1,1,1]
    parent[0,1,2,3,4,5,6,7,8,9]
    
    union(0, 1)
    parentArrayAccesses=9
    sizeArrayAccesses=4
    totalArrayAccesses=13
    size[2,1,1,1,1,1,1,1,1,1]
    parent[0,0,2,3,4,5,6,7,8,9]
    
    union(2, 3)
    parentArrayAccesses=18
    sizeArrayAccesses=8
    totalArrayAccesses=26
    size[2,1,2,1,1,1,1,1,1,1]
    parent[0,0,2,2,4,5,6,7,8,9]
    
    union(4, 5)
    parentArrayAccesses=27
    sizeArrayAccesses=12
    totalArrayAccesses=39
    size[2,1,2,1,2,1,1,1,1,1]
    parent[0,0,2,2,4,4,6,7,8,9]
    
    union(6, 7)
    parentArrayAccesses=36
    sizeArrayAccesses=16
    totalArrayAccesses=52
    size[2,1,2,1,2,1,2,1,1,1]
    parent[0,0,2,2,4,4,6,6,8,9]
    
    union(0, 2)
    parentArrayAccesses=45
    sizeArrayAccesses=20
    totalArrayAccesses=65
    size[4,1,2,1,2,1,2,1,1,1]
    parent[0,0,0,2,4,4,6,6,8,9]
    
    union(4, 6)
    parentArrayAccesses=54
    sizeArrayAccesses=24
    totalArrayAccesses=78
    size[4,1,2,1,4,1,2,1,1,1]
    parent[0,0,0,2,4,4,4,6,8,9]
    
    union(0, 4)
    parentArrayAccesses=63
    sizeArrayAccesses=28
    totalArrayAccesses=91
    size[8,1,2,1,4,1,2,1,1,1]
    parent[0,0,0,2,0,4,4,6,8,9]
    
*/
  }

}
