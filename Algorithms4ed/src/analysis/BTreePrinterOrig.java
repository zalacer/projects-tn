package analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// from https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram

public class BTreePrinterOrig {
  
//  public static class Node<T extends Comparable<?>> {
//    Node<T> left, right;
//    T data;
//
//    public Node(T data) {
//        this.data = data;
//    }
//  }

  public static <T extends Comparable<?>> void printNode(Node<T> root) {
      int maxLevel = BTreePrinterOrig.maxLevel(root);

      printNodeInternal(Collections.singletonList(root), 1, maxLevel);
  }

  private static <T extends Comparable<?>> void printNodeInternal(List<Node<T>> nodes, int level, int maxLevel) {
      if (nodes.isEmpty() || BTreePrinterOrig.isAllElementsNull(nodes))
          return;

      int floor = maxLevel - level;
      int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
      int firstSpaces = (int) Math.pow(2, (floor)) - 1;
      int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

      BTreePrinterOrig.printWhitespaces(firstSpaces);

      List<Node<T>> newNodes = new ArrayList<Node<T>>();
      for (Node<T> node : nodes) {
          if (node != null) {
              System.out.print(node.data);
              newNodes.add(node.left);
              newNodes.add(node.right);
          } else {
              newNodes.add(null);
              newNodes.add(null);
              System.out.print(" ");
          }

          BTreePrinterOrig.printWhitespaces(betweenSpaces);
      }
      System.out.println("");

      for (int i = 1; i <= endgeLines; i++) {
          for (int j = 0; j < nodes.size(); j++) {
              BTreePrinterOrig.printWhitespaces(firstSpaces - i);
              if (nodes.get(j) == null) {
                  BTreePrinterOrig.printWhitespaces(endgeLines + endgeLines + i + 1);
                  continue;
              }

              if (nodes.get(j).left != null)
                  System.out.print("/");
              else
                  BTreePrinterOrig.printWhitespaces(1);

              BTreePrinterOrig.printWhitespaces(i + i - 1);

              if (nodes.get(j).right != null)
                  System.out.print("\\");
              else
                  BTreePrinterOrig.printWhitespaces(1);

              BTreePrinterOrig.printWhitespaces(endgeLines + endgeLines - i);
          }

          System.out.println("");
      }

      printNodeInternal(newNodes, level + 1, maxLevel);
  }

  private static void printWhitespaces(int count) {
      for (int i = 0; i < count; i++)
          System.out.print(" ");
  }

  private static <T extends Comparable<?>> int maxLevel(Node<T> node) {
      if (node == null)
          return 0;

      return Math.max(BTreePrinterOrig.maxLevel(node.left), BTreePrinterOrig.maxLevel(node.right)) + 1;
  }

  private static <T> boolean isAllElementsNull(List<T> list) {
      for (Object object : list) {
          if (object != null)
              return false;
      }

      return true;
  }

}