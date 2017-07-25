package bst;

//http://grimore.org/java/binary_search_trees

class Node< GenericType extends Comparable<? super GenericType>> {
  public Node< GenericType> parent;
  public Node< GenericType> left;
  public Node< GenericType> right;
  public GenericType key;

  Node(GenericType key) {
      this.key = key;
  }

  Node(Node< GenericType> node) {
      parent = node.parent;
      left = node.left;
      right = node.right;
      key = node.key;
  }
}

