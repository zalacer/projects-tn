package st;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/33balanced/RandomizedBST.java
 *  http://algs4.cs.princeton.edu/33balanced/RandomizedBST.java.html
 *  Compilation:  javac RandomizedBST.java
 *  Execution:    java RandomizedBST
 *  Dependencies: StdRandom.java
 *  
 *  Symbol table (map) implemented with a randomized BST.
 *
 ******************************************************************************/


import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedBST<Key extends Comparable<Key>, Value> implements Iterable<Key> {

    private Node root;   // root of the BST

    // BST helper node data type
    private class Node {
        private Key key;            // key
        private Value val;          // associated data
        private Node left, right;   // left and right subtrees
        private int size;              // node count of descendents

        public Node(Key key, Value val) {
            this.key = key;
            this.val = val;
            this.size = 1;
        }
    }


   /***************************************************************************
    *  BST search.
    ***************************************************************************/

    public boolean contains(Key key) {
        return get(key) != null;
    }

    // return value associated with the given key
    // if no such value, return null
    // if multiple such values, return first one on path from root
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if      (cmp == 0) return x.val;
        else if (cmp  < 0) return get(x.left,  key);
        else               return get(x.right, key);
    }


   /***************************************************************************
    *  Randomized insertion.
    ***************************************************************************/
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    // make new node the root with uniform probability
    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val);
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            x.val = val;
            return x;
        }

        if (StdRandom.bernoulli(1.0 / (size(x) + 1.0))) return putRoot(x, key, val);
        if (cmp < 0) x.left  = put(x.left,  key, val); 
        else         x.right = put(x.right, key, val); 
        // (x.size)++;
        fix(x);
        return x;
    }


    private Node putRoot(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left  = putRoot(x.left,  key, val);
            x = rotR(x);
        }
        else if (cmp > 0) {
            x.right = putRoot(x.right, key, val);
            x = rotL(x);
        }
        else {
            x.val = val;
        }
        return x;
    }



   /***************************************************************************
    *  Randomized deletion.
    ***************************************************************************/
    private Node joinLR(Node a, Node b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (StdRandom.bernoulli((double) size(a) / (size(a) + size(b))))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    private Node remove(Node x, Key key) {
        if (x == null) return null; 
        int cmp = key.compareTo(x.key);
        if      (cmp == 0) x = joinLR(x.left, x.right);
        else if (cmp  < 0) x.left  = remove(x.left,  key);
        else               x.right = remove(x.right, key);
        fix(x);
        return x;
    }

    // remove and return value associated with given key; if no such key, return null
    public Value remove(Key key) {
        Value val = get(key);
        root = remove(root, key);
        return val;
    }

   /***************************************************************************
    *  Selection.
    ***************************************************************************/

    // return the kth largest key
    public Key select(int k) {
        Node x = select(root, k);
        return x.key;
    }
    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if      (t > k) return select(x.left,  k);
        else if (t < k) return select(x.right, k-t-1);
        else            return x;
    }


        
    // return the smallest key
    public Key min() {
        Key key = null;
        for (Node x = root; x != null; x = x.left)
            key = x.key;
        return key;
    }
    
    // return the largest key
    public Key max() {
        Key key = null;
        for (Node x = root; x != null; x = x.right)
            key = x.key;
        return key;
    }

    // return the smallest key >= query key; if no such key return null
    public Key ceiling(Key key) {
        Node best = ceiling(root, key, null);
        if (best == null) return null;
        return best.key;
    }
    private Node ceiling(Node x, Key key, Node best) {
        if      (x == null)        return best;
        else if (eq(key, x.key))   return x;
        else if (less(key, x.key)) return ceiling(x.left,  key, x);
        else                       return ceiling(x.right, key, best);
    }

    // return the smallest key >= query key; if no such key return null
    public Key ceiling2(Key key) {
        Node best = null;
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if  (cmp < 0) {
                best = x;
                x = x.left;
            }
            else if (cmp > 0) {
                x = x.right;
            }
            else {
                return x.key;
            }
        }
        if (best == null) return null;
        return best.key;
    }


   /***************************************************************************
    *  Iterate using inorder traversal using a stack.
    *  Iterating through N elements takes O(N) time.
    ***************************************************************************/
    public Iterator<Key> iterator() { return new BSTIterator(root); }

    // an iterator
    private class BSTIterator implements Iterator<Key> {
        private Stack<Node> stack = new Stack<Node>();

        public BSTIterator(Node x) {
            while (x != null) {
                stack.push(x);
                x = x.left;
            }
        }

        public boolean hasNext()  { return !stack.isEmpty();                    }

        // it's optional and we don't want to support it
        public void remove()      { throw new UnsupportedOperationException();  }

        public Key next() {
            if (!hasNext()) throw new NoSuchElementException();
            Node x = stack.pop();
            Key key = x.key;
            x = x.right;
            while (x != null) {
                stack.push(x);
                x = x.left;
            }
            return key;
        }
    }




   /***************************************************************************
    *  Utility functions.
    ***************************************************************************/

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node x) { 
        if (x == null) return 0;
        else           return x.size;
    }

    // height of tree (1-node tree has height 0)
    public int height() { return height(root); }
    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }


   /***************************************************************************
    *  Helper BST functions.
    ***************************************************************************/

    // fix subtree count field
    private void fix(Node x) {
        if (x == null) return;
        x.size = 1 + size(x.left) + size(x.right);
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }


   /***************************************************************************
    *  Debugging functions that test the integrity of the tree.
    ***************************************************************************/

    // check integrity of subtree count fields
    public boolean check() { return checkCount() && isBST(); }

    // check integrity of count fields
    private boolean checkCount() { return checkCount(root); }
    private boolean checkCount(Node x) {
        if (x == null) return true;
        return checkCount(x.left) && checkCount(x.right) && (x.size == 1 + size(x.left) + size(x.right));
    }


    // does this tree satisfy the BST property?
    private boolean isBST() { return isBST(root, min(), max()); }

    // are all the values in the BST rooted at x between min and max, and recursively?
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (less(x.key, min) || less(max, x.key)) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    } 



   /***************************************************************************
    *  Helper comparison functions.
    ***************************************************************************/

    private boolean less(Key k1, Key k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(Key k1, Key k2) {
        return k1.compareTo(k2) == 0;
    }



   /***************************************************************************
    *  Test client.
    ***************************************************************************/
    public static void main(String[] args) {
        RandomizedBST<String, String> st = new RandomizedBST<String, String>();

        // insert some key-value pairs
        st.put("www.cs.princeton.edu",   "128.112.136.11");
        st.put("www.cs.princeton.edu",   "128.112.136.35");    // overwrite old value
        st.put("www.princeton.edu",      "128.112.130.211");
        st.put("www.math.princeton.edu", "128.112.18.11");
        st.put("www.yale.edu",           "130.132.51.8");
        st.put("www.amazon.com",         "207.171.163.90");
        st.put("www.simpsons.com",       "209.123.16.34");
        st.put("www.stanford.edu",       "171.67.16.120");
        st.put("www.google.com",         "64.233.161.99");
        st.put("www.ibm.com",            "129.42.16.99");
        st.put("www.apple.com",          "17.254.0.91");
        st.put("www.slashdot.com",       "66.35.250.150");
        st.put("www.whitehouse.gov",     "204.153.49.136");
        st.put("www.espn.com",           "199.181.132.250");
        st.put("www.snopes.com",         "66.165.133.65");
        st.put("www.movies.com",         "199.181.132.250");
        st.put("www.cnn.com",            "64.236.16.20");
        st.put("www.iitb.ac.in",         "202.68.145.210");


        System.out.println(st.get("www.cs.princeton.edu"));
        System.out.println(st.get("www.harvardsucks.com"));
        System.out.println(st.get("www.simpsons.com"));
        System.out.println();

        System.out.println("integrity check: " + st.check());
        System.out.println();

        System.out.println("ceiling(www.simpsonr.com) = " + st.ceiling("www.simpsonr.com"));
        System.out.println("ceiling(www.simpsons.com) = " + st.ceiling("www.simpsons.com"));
        System.out.println("ceiling(www.simpsont.com) = " + st.ceiling("www.simpsont.com"));

        System.out.println("ceiling(www.simpsonr.com) = " + st.ceiling2("www.simpsonr.com"));
        System.out.println("ceiling(www.simpsons.com) = " + st.ceiling2("www.simpsons.com"));
        System.out.println("ceiling(www.simpsont.com) = " + st.ceiling2("www.simpsont.com"));
        System.out.println();

        for (int i = 0; i < st.size(); i++) {
            System.out.println(i + "th: key  " + st.select(i));
        }
        System.out.println();

        System.out.println("min key: " + st.min());
        System.out.println("max key: " + st.max());
        System.out.println("size:    " + st.size());
        System.out.println("height:  " + st.height());
        System.out.println();
    }

}
