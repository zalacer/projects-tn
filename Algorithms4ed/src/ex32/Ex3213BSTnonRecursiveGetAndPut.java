package ex32;

import st.BSTXI;

/* p418
  3.2.13 Give nonrecursive implementations of get() and put() for BST.
  
  This is done in st.BSTXI that has iterative get(), getNode(), put(),
  heightI() and sizeI(), while the recursive versions are named getR(), 
  getNodeR(), putR(), heightR() and sizeR() and the default versions of
  height() and size() are so named and rely on the Node height and size
  fields.
  
  Here is the iterative get():
  
    public Value get(Key key) {
      // iterative
      Node x = root;
      while (x != null) {
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x = x.left;
        else if (cmp > 0) x = x.right;
        else return x.val;
      }
      return null;
    }
   
  Here is the iterative put():
  
    public void put(Key key, Value val) {
      // iterative
      if (key == null) throw new IllegalArgumentException();
      if (val == null) { delete(key); return; }
      if (kclass == null) kclass = key.getClass();
      if (vclass == null) vclass = val.getClass();
      Node z = new Node(key, val, 1);
      if (root == null) { root = z;  pathCompares++; return; }
      Node parent = null, x = root;  int l = 0;
      Set<Node> set = new HashSet<>(); Iterator<Node> it;
      
      while (x != null) {
        parent = x; parent.height = 0;
        it = set.iterator();
        while (it.hasNext()) it.next().height++;
        int cmp = key.compareTo(x.key); compares++;
        if (cmp < 0) {
          x = x.left; l++;
          if (x != null) { parent.size++; set.add(parent); }
        } else if (cmp > 0) { 
          x = x.right; l++; 
          if (x != null) { parent.size++; set.add(parent); }
        } else {
          x.val = val; x.level = l; pathCompares+=(l+1);
          return; 
        }
      }
      
      it = set.iterator();
      while (it.hasNext()) it.next().height++;
      z.level = l; pathCompares+=(l+1); parent.size++; parent.height++;
  
      int cmp = key.compareTo(parent.key);
      if (cmp < 0) parent.left  = z;
      else         parent.right = z;
    }
    
    Below is a demo testing the iterative get() and put().
  
*/

public class Ex3213BSTnonRecursiveGetAndPut {
 
  public static void main(String[] args) {
    
    Integer[] u = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    Integer[] v = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    BSTXI<Integer, Integer> bst = new BSTXI<>(u,v);
    for (int i = 0; i < u.length; i++) {
      assert bst.get(u[i]) == v[i];
      bst.delete(u[i]);
      assert bst.avgCompares() == bst.avgComparesR();
    }
    assert bst.isEmpty() == true;

  }

}

