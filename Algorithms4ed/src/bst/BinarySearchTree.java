package bst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//http://grimore.org/java/binary_search_trees

class BinarySearchTree< GenericType extends Comparable<? super GenericType>> {
  
  public Node< GenericType> root;
  public int size = 0;

  /**
   * Binary Search Tree Constructor.
   */
  public BinarySearchTree() {
      // initially, the BST contains the null node.
      root = null;
  }

  /**
   * Insert an element into the tree.
   *
   * @param key the element to insert.
   */
  public void insert(GenericType key) {
      if (key == null) {
          return;
      }
      // if we do not have a root, then create one out of the node to be inserted.
      if (root == null) {
          root = new Node< >(key);
          ++size;
          return;
      }
      // replace the root by new root after the insertion.
      root = insertInPlace(root, key);
  }

  /**
   * Internal method to insert an element in-place.
   *
   * @param key the element to insert.
   * @param node the node to start inserting at.
   * @return the new root.
   */
  private Node< GenericType> insertInPlace(Node< GenericType> node, GenericType key) {
      // create a copy of the node starting from where to insert.
      Node< GenericType> currentNode = new Node< >(node);
      // ... and compare it to the key.
      int compare = currentNode.key.compareTo(key);
      // if greater...
      if (compare > 0) {
          // ... and we have a left node.
          if (currentNode.left != null) {
              // recurse in the left tree.;
              currentNode.left = insertInPlace(currentNode.left, key);
              return currentNode;
          }
          // if we do not have a left node, create a new node.
          currentNode.left = new Node< >(key);
          // set the parent of the created node.
          currentNode.left.parent = currentNode;
          // increase the size of the tree.
          ++size;
          // and return the new root.
          return currentNode;
      }
      // if smaller ...
      if (compare < 0) {
          // ... and we have a right node.
          if (currentNode.right != null) {
              // recurse in the right tree.
              currentNode.right = insertInPlace(currentNode.right, key);
              return currentNode;
          }
          // create it...
          currentNode.right = new Node< >(key);
          // ...set the parent...
          currentNode.right.parent = currentNode;
          // ...increase the size...
          ++size;
          // and return the new root.
          return currentNode;
      }
      // otherwise, just return the curent node as the root.
      return currentNode;
  }

  /**
   * Delete an element from the tree.
   *
   * @param key the element to delete.
   */
  public void delete(GenericType key) {
      // if either the key or the root are null then we have nothing to delete.
      if (key == null || root == null) {
          return;
      }
      root = deleteInPlace(root, key);
  }

  /**
   * Internal method to delete a node.
   *
   * @param key the element to delete.
   * @param node the node to start deleting at.
   * @return the new root.
   */
  private Node< GenericType> deleteInPlace(Node< GenericType> node, GenericType key) {
      // create a copy of the node from where to delete...
      Node< GenericType> currentNode = new Node< >(node);
      // ... and compare it to the key.
      int compare = currentNode.key.compareTo(key);
      // walk left or right depending on the comparison.
      if (compare > 0 && currentNode.left != null) {
          currentNode.left = deleteInPlace(currentNode.left, key);
      } else if (compare < 0 && currentNode.right != null) {
          currentNode.right = deleteInPlace(currentNode.right, key);
      } else if (currentNode.left != null && currentNode.right != null) {
          // the node has two children, so we set the minimum of the right node...
          currentNode.key = minimum(currentNode.right).key;
          // ..and delete.
          currentNode.right = deleteInPlace(currentNode.right, currentNode.key);
          // finally, decrease the size of the tree.
          --size;
      } else {
          // check whether the left node exists, if it does set it to the left node, otherwise the right.
          currentNode = currentNode.left != null ? currentNode.left : currentNode.right;
          // and decrease the size of the tree.
          --size;
      }
      // otherwise, return the new root as the current node.
      return currentNode;
  }

  /**
   * Finds the maximum element in the tree.
   *
   * @param node the node from where to start searching.
   * @return the maximum node in the tree from the starting node.
   */
  public Node< GenericType> maximum(Node< GenericType> node) {
      Node< GenericType> currentNode = node;
      if (currentNode == null) {
          return null;
      }
      // keep walking in the right direction to find the maximal node,
      // each time substituting the current node for the right node.
      while (currentNode.right != null) {
          if (currentNode.right == null) {
              return currentNode;
          }
          currentNode = currentNode.right;
      }
      return currentNode;
  }

  /**
   * Finds the minimum element in the tree.
   *
   * @param node the node from where to start searching.
   * @return the minimum node in the tree from the starting node.
   */
  public Node< GenericType> minimum(Node< GenericType> node) {
      Node< GenericType> currentNode = node;
      if (currentNode == null) {
          return null;
      }
      // conversely, keep walking in the left direction to find the minimum node,
      // each time substituting the current node for the right node.
      while (currentNode.left != null) {
          if (currentNode.left == null) {
              return currentNode;
          }
          currentNode = currentNode.left;
      }
      return currentNode;
  }

  /**
   * Finds the sucessor element in the tree.
   *
   * @param key the value of the node from where to start searching.
   * @return the successor element of the node corresponding to the specified
   * key.
   */
  public Node< GenericType> successor(GenericType key) {
      if (key == null) {
          return null;
      }
      Node< GenericType> currentNode = search(key);
      if (currentNode == null) {
          return null;
      }
      // search in the right part of the tree.
      if (currentNode.right != null) {
          return minimum(currentNode.right);
      }
      // if we do not have a right subtree, then walk up parents.
      Node< GenericType> parentNode = currentNode.parent;
      while (parentNode != null && currentNode == parentNode.right) {
          currentNode = parentNode;
          parentNode = parentNode.parent;
      }
      return parentNode;
  }

  /**
   * Finds the predecessor element in the tree.
   *
   * @param key the value of the node from where to start searching.
   * @return the predecessor element of the node corresponding to the
   * specified key.
   */
  public Node< GenericType> predecessor(GenericType key) {
      if (key == null) {
          return null;
      }
      Node< GenericType> currentNode = search(key);
      if (currentNode == null) {
          return null;
      }
      // search in the left part of the tree.
      if (currentNode.left != null) {
          return minimum(currentNode.left);
      }
      // if we do not have a left subtree, then walk up parents.
      Node< GenericType> parentNode = currentNode.parent;
      while (parentNode != null && currentNode == parentNode.left) {
          currentNode = parentNode;
          parentNode = parentNode.parent;
      }
      return parentNode;
  }

  /**
   * Finds a node in the tree.
   *
   * @param key the value to start searching for.
   * @return the node corresponding to the searched key.
   */
  public Node< GenericType> search(GenericType key) {
      if (root == null) {
          return null;
      }
      Node< GenericType> node = root;
      int compare;
      // search the tree by comparing keys.
      while ((compare = node.key.compareTo(key)) != 0) {
          if (compare > 0) {
              if (node.left == null) {
                  return null;
              }
              node = node.left;
              continue;
          }
          if (node.right == null) {
              return null;
          }
          node = node.right;
      }
      // return the node, not the key, this is done so the 
      // predecessor and successor functions can find the node.
      return node;
  }

  /**
   * Prints keys in-order, optionally using brackets to illustrate nesting.
   *
   * @param brackets boolean indicating whether brackets should be printed.
   */
  public void inOrderPrint(boolean brackets) {
      if (brackets) {
          System.out.print("{");
      }
      if (root == null) {
          if (brackets) {
              System.out.println("}");
          }
          return;
      }
      print(root, brackets);
      if (brackets) {
          System.out.print("}");
      }
      System.out.println("");
  }

  /**
   * Internal method to print nodes.
   *
   * @param brackets boolean value indicating whether brackets should be
   * printed.
   * @param node the node to start printing at.
   */
  private void print(Node< GenericType> node, boolean brackets) {
      if (node == null) {
          return;
      }
      // print left, middle and then right.
      if (node.left != null) {
          if (brackets) {
              System.out.print("{");
          }
          print(node.left, brackets);
          if (brackets) {
              System.out.print("}");
          }
      }
      System.out.print(" " + node.key + " ");
      if (node.right != null) {
          if (brackets) {
              System.out.print("{");
          }
          print(node.right, brackets);
          if (brackets) {
              System.out.print("}");
          }
      }
  }

  /**
   * Internal method to get the depth of the tree starting from a node.
   *
   * @param node the node to start counting the depth from.
   * @return the depth of the tree counting from the specified node.
   */
  public int getDepth(Node< GenericType> node) {
      if (node == null) {
          return 0;
      }
      // get the depth by recursively walking the left, respectively right
      // subtree and then compare the result at the end to return the depth.
      int left = getDepth(node.left);
      int right = getDepth(node.right);
      return (left > right) ? left + 1 : right + 1;
  }

  /**
   * Internal method to check whether a list of nodes contains null elements.
   *
   * @param nodes a list of nodes.
   * @return true if the list contains nulls, or false otherwise.
   */
  private boolean containsNull(List< Node< GenericType>> nodes) {
      for (Object object : nodes) {
          if (object != null) {
              return false;
          }
      }
      return true;
  }

  /**
   * Print-out nodes as an ASCII tree starting from the root node.
   *
   * @return nothing.
   */
  public void prettyPrint() {
      int depth = getDepth(root);
      // we get the depth and then get all the nodes as a list
      // we start from level 1, since the function starts from the root node.
      pretty(Collections.singletonList(root), 1, depth);
  }

  /**
   * Internal method to print nodes as ASCII trees.
   *
   * @param nodes a list of all nodes.
   * @param level the level from which printing should start.
   * @depth the depth of the tree.
   */
  private void pretty(List< Node< GenericType>> nodes, int level, int depth) {
      // if the list of nodes does not contain any nodes or if the nodes are null nodes
      // then just return since we are done - this is the exit point for the recursion.
      if (nodes.isEmpty() || containsNull(nodes)) {
          return;
      }
      // calculate the padding and spaces required to print the tree.
      int delta = depth - level;
      int lines = (int) Math.pow(2, (Math.max(delta - 1, 0)));
      int indent = (int) Math.pow(2, (delta)) - 1;
      int spacing = (int) Math.pow(2, (delta + 1)) - 1;
      // print out the indenting - we do this in-line, elegantly to not increase the 
      // requirements of using utility functions and importing new libraries.
      System.out.print(new String(new char[indent]).replace("\0", " "));
      // create a new list to hold the new nodes.
      List< Node< GenericType>> currentNodes = new ArrayList< >();
      // now for all nodes passed to the method...
      for (Node< GenericType> node : nodes) {
          // if the node exists...
          if (node != null) {
              // print out its value and...
              System.out.print(node.key);
              // add the left node to the new list...
              currentNodes.add(node.left);
              // add the right node to the new list...
              currentNodes.add(node.right);
              // finally print out the spacing and wend.
              System.out.print(new String(new char[spacing]).replace("\0", " "));
              continue;
          }
          // the node does not exist, so add nulls.
          currentNodes.add(null);
          currentNodes.add(null);
          // print a space and then print out the spacing
          System.out.print(new String(new char[spacing + 1]).replace("\0", " "));
      }
      // we are going down, so print a newline.
      System.out.println("");
      // now, for all lines and for all the nodes...
      for (int i = 1; i <= lines; ++i) {
          for (int j = 0; j < nodes.size(); ++j) {
              // print the indenting.
              System.out.print(new String(new char[Math.max(indent - i, 0)]).replace("\0", " "));
              if (nodes.get(j) == null) {
                  // if we do not have a node, just print out the spacing and continue.
                  System.out.print(new String(new char[Math.max(2 * lines + i + 1, 0)]).replace("\0", " "));
                  continue;
              }
              if (nodes.get(j).left != null) {
                  System.out.print("/");
              } else {
                  System.out.print(new String(new char[1]).replace("\0", " "));
              }
              System.out.print(new String(new char[Math.max(2 * i - 1, 0)]).replace("\0", " "));
              if (nodes.get(j).right != null) {
                  System.out.print("\\");
              } else {
                  System.out.print(new String(new char[1]).replace("\0", " "));
              }
              // finally, print out the spacing.
              System.out.print(new String(new char[Math.max(2 * lines - i, 0)]).replace("\0", " "));
          }
          System.out.println("");
      }
      // recurse for the next level on the list of nodes.
      pretty(currentNodes, level + 1, depth);
  }

  /**
   * Set a node in the tree.
   *
   * @param key the key to replace.
   */
  public void set(GenericType key, GenericType value) {
      Node< GenericType> currentNode = search(key);
      // if the current node exists, delete it.
      if (currentNode != null) {
          root = deleteInPlace(root, key);
      }
      // and then insert the new node.
      root = insertInPlace(root, value);
  }
  
  public static void main(String[] args) {
    
//    BinarySearchTree< String > bst = new BinarySearchTree< >(); // initializer
//    bst.insert("A"); // insert nodes
//    bst.insert("B");
//    bst.insert("C");
//    bst.insert("D");
//    bst.delete("B"); // delete nodes
//    bst.prettyPrint(); // prints the nodes using ASCII
//    bst.delete("A");
//    bst.prettyPrint();
    
    BinarySearchTree<Integer> st = new BinarySearchTree< >();
    Integer[] a = new Integer[]{8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 20, 15};
    for (Integer x : a) st.insert(x);
    st.prettyPrint();
    
  }
  
}

