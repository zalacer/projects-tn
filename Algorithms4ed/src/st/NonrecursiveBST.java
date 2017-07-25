package st;

import ds.Queue;
import ds.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/32bst/NonrecursiveBST.java
 *  http://algs4.cs.princeton.edu/32bst/NonrecursiveBST.java.html
 *  Compilation:  javac NonrecursiveBST.java
 *  Execution:    java  NonrecursiveBST < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *
 *  A symbol table implemented with a binary search tree using
 *  iteration instead of recursion for put(), get(), and keys().
 * 
 *  % more tinyST.txt
 *  S E A R C H E X A M P L E
 *  
 *  % java NonrecursiveBST < tinyST.txt
 *  A 8
 *  C 4
 *  E 12
 *  H 5
 *  L 11
 *  M 9
 *  P 10
 *  R 3
 *  S 0
 *  X 7
 *
 ******************************************************************************/

public class NonrecursiveBST<Key extends Comparable<Key>, Value> {

    // root of BST
    private Node root;  

    private class Node {
        private Key key;             // sorted by key
        private Value val;           // associated value
        private Node left, right;    // left and right subtrees

        public Node(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }


   /***************************************************************************
    *  Insert key-value pair into symbol table (nonrecursive version).
    ***************************************************************************/
    public void put(Key key, Value val) {
        Node z = new Node(key, val);
        if (root == null) {
            root = z;
            return;
        }

        Node parent = null, x = root;
        while (x != null) {
            parent = x;
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else {
                x.val = val;
                return; 
            }
        }
        int cmp = key.compareTo(parent.key);
        if (cmp < 0) parent.left  = z;
        else         parent.right = z;
    }
   

   /***************************************************************************
    *  Search BST for given key, nonrecursive version.
    ***************************************************************************/
    Value get(Key key) {
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else return x.val;
        }
        return null;
    }

   /***************************************************************************
    *  Inorder traversal.
    ***************************************************************************/
    public Iterable<Key> keys() {
        Stack<Node> stack = new Stack<Node>();
        Queue<Key> queue = new Queue<Key>();
        Node x = root;
        while (x != null || !stack.isEmpty()) {
            if (x != null) {
                stack.push(x);
                x = x.left;
            }
            else {
                x = stack.pop();
                queue.enqueue(x.key);
                x = x.right;
            }
        }
        return queue;
    }


   /***************************************************************************
    *  Test client.
    ***************************************************************************/
    public static void main(String[] args) { 
        String[] a = StdIn.readAllStrings();
        int n = a.length;
        NonrecursiveBST<String, Integer> st = new NonrecursiveBST<String, Integer>();
        for (int i = 0; i < n; i++)
            st.put(a[i], i);
        for (String s : st.keys())
            StdOut.println(s + " " + st.get(s));
    }

}

