package st;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import v.Tuple2;

//modified from 
// https://github.com/bjorn9800991/2-3-tree/blob/master/lab4/src/main/java/se/kth/id1020/lab4/TwoThreeTree.java

@SuppressWarnings("unused")
public class TwoThreeTree <K extends Comparable<K>, V> {

  private Node root;
  private int nodeCount = 0; // We are only creating new nodes when splitting (with a 
                             // few exceptions). Just count the splits and we get the size.
  private Class<?> kclass = null;
  private Class<?> vclass = null;
  
  private class Node {
    protected KeyValuePair keyvalues1;
    protected KeyValuePair keyvalues2;
    protected KeyValuePair keyvalues3;
    protected Node parent, left, middle, middle2, right;

    public Node(KeyValuePair keyValues1) { this.keyvalues1 = keyValues1; }

    public Node(KeyValuePair keyValues1, KeyValuePair keyValues2) {
      this.keyvalues1 = keyValues1;
      this.keyvalues2 = keyValues2;
    }
    
    public Node(KeyValuePair kv1, KeyValuePair kv2, KeyValuePair kv3) {
      this.keyvalues1 = kv1;
      this.keyvalues2 = kv2;
      this.keyvalues3 = kv3;
    }
    
    @Override
    public String toString() {
      if (parent == null) {
        if (keyvalues2 != null)
          return "("+keyvalues1.key+","+keyvalues2.key+")";
        else return "("+keyvalues1.key+")";
      } else  {
        if (keyvalues2 != null)
          return "("+keyvalues1.key+","+keyvalues2.key+",["+parent.keyvalues1.key+"])";
        else return "("+keyvalues1.key+",["+parent.keyvalues1.key+"])";
      }
//      else if (parent.keyvalues2 != null)
//        return "("+keyvalues1+","+keyvalues2+",["+parent.keyvalues1+"])";
//      else return "("+keyvalues1+","+keyvalues2+")";
    }
    
    public String toShape() {
      // node shape is classified as 1; or 2;
      // for ex3305
      return keyvalues2 == null ? "1;" : "2;";
    }
  }
  
  private class FourNode extends Node {
    protected KeyValuePair keyvalues3;
    protected Node middle2;

    public FourNode (KeyValuePair kv1, KeyValuePair kv2, KeyValuePair kv3) {
      super(kv1, kv2);
      this.keyvalues3 = kv3;
    }
    
    @Override
    public String toString() {
      return super.toString().substring(0,super.toString().length()-1) 
          + "," + keyvalues3.key + ")";
    }
//      if (parent == null) {
//        if (keyvalues2 != null)
//          return "("+keyvalues1.key+","+keyvalues2.key+")";
//        else return "("+keyvalues1.key+")";
//      } else  {
//        if (keyvalues2 != null)
//          return "("+keyvalues1.key+","+keyvalues2.key+",["+parent.keyvalues1.key+"])";
//        else return "("+keyvalues1.key+",["+parent.keyvalues1.key+"])";
//      }
  }
  
  public Node getRoot() { return root; }

  /**
   * Retrieves the value of the key stored in the tree. If the key is not in 
   * the tree a null value is returned.
   * @param key The key to find in the tree.
   * @return The value that's associated with the specified key, returns null 
   * if the key is not found.
   */
  public V get(K key) {
    //Search for our key.
    Node searchResult = getNode(key, root, true);

    if (searchResult != null)  {
      //Since it can be either a two or a ThreeNode we need to return the correct value.
      if (searchResult.keyvalues1.key.equals(key))
        return searchResult.keyvalues1.value;
      else
        return searchResult.keyvalues2.value;
    }
    else return null;
  }

  /**
   * Inserts the key (with associated value) into the tree.
   * @param key The key to insert.
   * @param value The value associated with the key. Can not be null.
   */
  public void put(K key, V value) {
//    System.out.println("\nbegin put "+key+" "+value);
    //Null values are not supported, cause then get will not work properly.
    //Assume we store a null value and then get will return that null value. The calling
    //code will then not know if we have found the value (that is null) or if the search
    //was unsuccessful and that's why we are returning null. 
    if (key == null || value == null)
      throw new IllegalArgumentException("Null values not supported!");

    //If we don't have a root node we don't have a tree.
    //Create a TwoNode and store our key/value in it, done.
    if (root == null) {
      root = new Node(new KeyValuePair(key, value));
      nodeCount = 1; //We need to add 1 to our size variable, we have not done a split, but we have one node now.
//     System.out.println("created root node with key "+key+" value "+value);
//     System.out.println(this);
//     printLevels();
      return;
    }

    //Find the node we should add the key/value to.
    Node foundNode = getNode(key, root, false);
//    System.out.println("foundNode="+foundNode);

    //If the node is a TwoNode, then it's simple. Convert that TwoNode 
    // to a ThreeNode and add. Since we are always adding from the bottom 
    // we don't have any children to take into account.
    if (foundNode.keyvalues2 == null) {
      if (key.compareTo(foundNode.keyvalues1.key) < 0) { //Key is less than key in node.
        //We need to move stuff if we are inserting to the left.
        foundNode.keyvalues2 = foundNode.keyvalues1;
        foundNode.keyvalues1 = new KeyValuePair(key, value);
//        System.out.println("inserted key "+key+" value "+value+" in 2-foundNode "+foundNode);
//        System.out.println(this);
//        printLevels();
      }
      else if (key.equals(foundNode.keyvalues1.key)) {//Equal to key in node, replace value.
        foundNode.keyvalues1.value = value;
//        System.out.println("replaced value "+value+" for key "+key+" in 2-foundNode "+foundNode);
//        System.out.println(this);
//        printLevels();
      }
      else if (key.compareTo(foundNode.keyvalues1.key) > 0) { //Key is greater than key in node.
        foundNode.keyvalues2 = new KeyValuePair(key, value);
//        System.out.println("inserted key "+key+" for value "+value+" in 2-foundNode "+foundNode);
//        System.out.println(this);
//        printLevels();
      }
    }
    else {
      //If the node is a ThreeNode, then it's more complicated. Create a temporary 
      // FourNode, split it to a TwoNode and merge that back into the tree.       
      //If we have any key with the key we are trying to add, then just replace those values.  
      if (foundNode.keyvalues1.key.equals(key)) {
        foundNode.keyvalues1.value = value;
//        System.out.println("replaced value "+value+" for key "+key+" in 3-foundnode "+foundNode);
//        System.out.println(this);
//        printLevels();
        return;
      }
      if (foundNode.keyvalues2.key.equals(key)) {
        foundNode.keyvalues2.value = value;
//        System.out.println("replaced value "+value+" for key "+key+" in 3-foundnode "+foundNode);
//        System.out.println(this);
//        printLevels();
        return;
      }

      FourNode tempFourNode = null;

      //Key is less than left key, insert new value to the left. 
      if (key.compareTo(foundNode.keyvalues1.key) < 0) {
        tempFourNode = new FourNode(new KeyValuePair(key, value), 
            foundNode.keyvalues1, foundNode.keyvalues2);
//        System.out.println("inserted key "+key+" and value "+value
//            +" in new 4-foundNode.keyvalues1 "+foundNode);
//        System.out.println(this);
//        printLevels();
      }
      //Key is larger then the left key, and smaller then the right key, 
      //thus it should go in the middle.
      else if (key.compareTo(foundNode.keyvalues1.key) > 0 
          && key.compareTo(foundNode.keyvalues2.key) < 0) {
        tempFourNode = new FourNode(foundNode.keyvalues1, 
            new KeyValuePair(key, value), foundNode.keyvalues2);
//        System.out.println("inserted key "+key+" and value "+value
//            +" in new 4-foundNode.keyvalues2 "+foundNode);
//        System.out.println(this);
//        printLevels();
      }
      //Key is more then the right key, insert new value in the right.
      else if (key.compareTo(foundNode.keyvalues2.key) >= 1) {
        tempFourNode = new FourNode(foundNode.keyvalues1, 
            foundNode.keyvalues2, new KeyValuePair(key, value));
//        System.out.println("inserted key "+key+" and value "+value
//            +" in new 4-foundNode.keyvalues3 "+foundNode);
//        System.out.println(this);
//        printLevels();
//        foundNode.keyvalues1 = null; foundNode.keyvalues2 = null;
      }
      
//      if (foundNode.parent != null &&  foundNode.parent.left == foundNode) 
//        foundNode.parent.left = tempFourNode;
//      if (foundNode.parent != null && foundNode.parent.middle == foundNode) 
//        foundNode.parent.middle = tempFourNode;
//      if (foundNode.parent != null && foundNode.parent.right == foundNode) 
//        foundNode.parent.right = tempFourNode;
//      tempFourNode.parent = foundNode.parent;
//      foundNode.parent = null;
      
//      System.out.println("tempFourNode="+tempFourNode);
//      System.out.println("foundNode=");
      
//      tempFourNode.left = foundNode.left;
//      tempFourNode.middle = foundNode.middle;
//      tempFourNode.right = foundNode.right;
//      tempFourNode.parent = foundNode.parent;
      
      //Insert this FourNode into the tree.
      put4NodeInTree(foundNode, tempFourNode);
//      if (foundNode == root) {
//        put4NodeInTree(foundNode, tempFourNode);
//      } else {
//        put4NodeBelowRoot(foundNode, tempFourNode);   
//      }
      
//      System.out.println("end put "+key+" "+value);

    }
  }

  /**
   * Returns the number of nodes in our tree.
   * @return An integer of how many nodes the entire tree has.
   */
  public int size() { return nodeCount; }
  
  public boolean isEmpty() { return nodeCount == 0; }

  /**
   * Returns an iterable collection of all key/values in our tree.
   * @return A LinkedList with the elements sorted by key order.
   */
  public Iterable<KeyValuePair> keys() {
    //List to store the result in.
    LinkedList<KeyValuePair> results = new LinkedList<KeyValuePair>();
    //Traverse the tree in-order and add each keyValue to the LinkedList. 
    traverseTree(root, results);
    return results;
  }

  /**
   * Returns an iterable collection of all key/values in our tree.
   * @param lo The lowest key value that should occur in our collection.
   * @param hi The highest key value that should occur in our collection.
   * @return A LinkedList with the elements sorted by key order within the 
   * bounds of lo =< keys =< hi.
   */
  public Iterable<KeyValuePair> keys(K lo, K hi) {
    List<KeyValuePair> treeKeyValues = (LinkedList<KeyValuePair>)keys();
    List<KeyValuePair> result = new LinkedList<KeyValuePair>();

    int indexOfLow = indexOfKeyInList(lo, treeKeyValues);
    int indexOfHigh = indexOfKeyInList(hi, treeKeyValues)+1;

    if (indexOfLow == -1 || indexOfHigh == -1)
      throw new IllegalArgumentException(
          "Key lo and/or hi not found in the tree.");

    result.addAll(treeKeyValues.subList(indexOfLow, indexOfHigh));

    return result;
  }

  /**
   * Returns the depth of the entire tree, since it's a balanced tree  
   * the depth is the same in the entire tree.
   * @return The depth as an int. A tree with just one node (root) will 
   * have a depth of 0.
   */
  public int depth() {
    //The tree is balanced, so we have the same depth everywhere. 
    int depthCount = 0;
    Node curNode = root; 
    //Just go left until we have no children any more, count each "level".
    while (curNode != null) {
      curNode = curNode.left;
      depthCount++;
    }
    depthCount--; //We counted the "null-level" under the leaf, remove that.
    return depthCount;
  }

  /**
   * Returns the maximum number of elements we can add to the tree 
   * before our depth changes.
   * @return Returns the remaining elements we can add before our 
   * tree grows in depth.
   */
  public int howMuchMore()
  {
    // Get the number of different nodes for root. Number of TwoNodes 
    // is in index 0, ThreeNodes is index 1.
    int[] totalRes = countNodeTypes(root);

    // Get the depth once.
    int depth = depth();
    double numElementsInFullTree = 0;

    //This is how many elements a full tree of our depth have.
    //2 * 3^(depth) + 2 * 3^(depth -1) + 2 *3^(depth -2) + ...
    //Geometric sum.
    for (int n = depth; n >= 0; n--)
      numElementsInFullTree += 2 * Math.pow(3, (depth - n));

    //Take the full tree - how much we actually have, and the difference 
    //is how many we can add.
    Double result = numElementsInFullTree - (2 * totalRes[1] + totalRes[0]);
    return result.intValue();
  }

  /**
   * Returns the average value of two and threeNodes in our tree.
   * @return The value as a float.
   */
  public float density() {
    //Get the number of TwoNodes and ThreeeNodes in our tree. TwoNodes 
    //is stored at index 0, ThreeNodes at 1.
    int[] totalRes = countNodeTypes(root);
    //Average value of the number of TwoNodes and ThreeNodes. Need to 
    //multiply by 2 and 3 because that's their weight.
    return (float) (totalRes[0]*2 + totalRes[1]*3) / (totalRes[0] + totalRes[1]);
  }

  /**
   * Counts the number of TwoNodes and ThreeNodes in the specified node (included).
   * @param curNode The node that we wish to count.
   * @return A int[] with TwoNode count at index 0 and ThreeNode count at index 1.
   */
  private int[] countNodeTypes (Node curNode) {
    //totalRes: index 0 is TwoNodeCount, index 1 is ThreeNodeCount.
    int[] totalRes = new int[2];
    //Current node is a... TwoNode.
    if (curNode.keyvalues2 == null)
      totalRes[0]++;
    else //... ThreeNode.
      totalRes[1]++;
    //Count the left children if we have any.
    if (curNode.left != null) {
      int[] childRes = countNodeTypes(curNode.left);
      totalRes[0] += childRes[0];
      totalRes[1] += childRes[1];
    }
    //Count the middle children if we have any.
    if (curNode.middle != null) {
      int[] childRes = countNodeTypes(curNode.middle);
      totalRes[0] += childRes[0];
      totalRes[1] += childRes[1];
    }
    //Count the right children if we have any.
    if (curNode.right != null) {
      int[] childRes = countNodeTypes(curNode.right);
      totalRes[0] += childRes[0];
      totalRes[1] += childRes[1];
    }
    return totalRes;
  }

  /**
   * Gets the node with the specified key, or the node where the specified key should be.
   * @param key The key to search for.
   * @param startNode String node for our search.
   * @param returnNullOnMissing If the key is missing in the tree, should return null or the node the key should be stored in.
   * @return Returns a Node where the specified key is or should be.
   */
  private Node getNode(K key, Node startNode, boolean returnNullOnMissing) {
    //If we should return null on a missing key then the branch we've 
    //just gone into will be null if it is where the key should be.
    if (returnNullOnMissing) {
      if(startNode == null) return null;
    } else { 
      //If we should find the node the key should be in, we need to check 
      //for an empty branch before going down, so we can return before that happens. 
    
      //Doesn't matter what branch we check for nulls since we have a balanced 
      //tree, if any branch is null we have a leaf node.
      if (startNode.left == null) return startNode;
    }

    //If equal to our first value, this is the same for both TwoNodes and ThreeNodes.
    if (key.equals(startNode.keyvalues1.key)) return startNode;

    //If smaller than the first value search left, this is the same for 
    //both TwoNodes and ThreeNodes. 
    if (key.compareTo(startNode.keyvalues1.key) < 0)
      return getNode(key, startNode.left, returnNullOnMissing);

    //TwoNode.
    if (startNode.keyvalues2 == null) {
      //Must be greater than, search middle cause that's the right child in two nodes.
      //Middle is right child for TwoNodes.
      return getNode(key, startNode.middle, returnNullOnMissing); 
    }
    else {//ThreeNode
      //If equal to our second value.
      if (key.equals(startNode.keyvalues2.key))
        return startNode;

      //If in the middle, search middle child.
      if (key.compareTo(startNode.keyvalues1.key) > 0 
          && key.compareTo(startNode.keyvalues2.key) < 0)
        return getNode(key, startNode.middle, returnNullOnMissing);
      else //If greater than our second value, search right.
        return getNode(key, startNode.right, returnNullOnMissing);
    } 
  }

  /**
   * Inserts a FourNode into the tree. If we try to merge with a three node 
   * we recursively try to go up the three until we can insert. 
   * @param currentNode The node the value should be in.
   * @param tmpFourNode The FourNode we are trying to insert.
   */
  private void put4NodeInTree(Node currentNode, FourNode tmpFourNode) {
    //Split the FourNode into a TwoNode. 
//    System.out.println("begin put4NodeInTree:");
    Node splitResult = convert4to2(tmpFourNode);
    nodeCount++;
//    System.out.println("splitResult="+splitResult);
//    System.out.println(this);
//    printLevels();
    
    //If we've worked our way up to the root, then we don't need to merge, 
    //just set the root to the split result.
    if (currentNode == root) {
      root = splitResult;
      nodeCount++;
//      System.out.println("currentNode == root="+root);
//      System.out.println(this);
//      printLevels();
      //Normally splitting produces two new children, and then merging will reduce that by one.
      //Since we are not merging now we need to count the split above one more time.  
    }
    else {
      Node parent = currentNode.parent;
//      System.out.println("parent="+parent);
//      currentNode.left = currentNode.right = currentNode.middle = currentNode.parent = null;
//      currentNode = null;
//      if (currentNode.parent != null &&  currentNode.parent.left == currentNode) 
//        currentNode.parent.left = null;
//      if (currentNode.parent != null && currentNode.parent.middle == currentNode) 
//        currentNode.parent.middle = null;
//      if (currentNode.parent != null && currentNode.parent.right == currentNode) 
//        currentNode.parent.right = null;
      //Merge the splitResult with the parent.
//      System.out.println(this);
//      printLevels();
      FourNode mergeResult = mergeNodes (parent, splitResult);
//      System.out.println("mergeResult="+mergeResult);
//      System.out.println(this);
//      printLevels();
//      System.out.println("parent="+parent);
      //If the merge result is null the parent was a TwoNode, and we are done. It's inserted.
      //If not, we need to merge the new FourNode we have with the parent and repeat.
      if (mergeResult != null)
        put4NodeInTree(parent, mergeResult);
    }   
//    System.out.println("end put4NodeInTree:");
  }
  
  private void put4NodeBelowRoot(Node foundNode, FourNode tempFourNode) {
    
    
    //p428
  }

  /**
   * Splits a FourNode into a TwoNode.
   * @param inNode The FourNode to split.
   * @return Returns the root of the new TwoNode.
   */
  private Node convert4to2(FourNode inNode) {
//    System.out.println("begin convert4to2:");
//    System.out.println("inNode="+inNode);
    // Convert a FourNode that looks like this:
    //    
    //    (a, b, c)
    //    /  |  |  \
    //     /   |  |   \
    //    1    2  3    4
    //
    //       (b)
    //      /   \
    //       /     \
    //     (a)     (c)
    //    / \ /   \
    //     /   \ /     \
    //    1      2 3     4
    //
    //       (b)
    //      /   \
    //     /     \
    //   (a)     (c)
    //   / \      / \
    //  /   \    /   \
    // 1     2  3     4
    //Create a new local root node, b, from the middle keyValue. 
    Node newRoot = new Node (inNode.keyvalues2);
//    System.out.println("newRoot="+newRoot);

    //New left, a, is the left child. New right, c, is the right child.
    Node newLeft = new Node (inNode.keyvalues1);
    Node newRight = new Node (inNode.keyvalues3);
    
//    System.out.println("newLeft="+newLeft);
//    System.out.println("newRight="+newRight);

    //Set the new children to the NewRoot.
    newRoot.left = newLeft;
    newRoot.middle = newRight;

    //Link these to the root node.
    newLeft.parent = newRoot;
    newRight.parent = newRoot;

    //Move branch 1, and relink its parent if we have such a branch.
    newLeft.left = inNode.left;
//    System.out.println("newLeft.left="+newLeft.left);

    if (newLeft.left != null) {
      newLeft.left.parent = newLeft;
//      System.out.println("newLeft.left.parent="+newLeft.left.parent);
    }

    //Move branch 2, and relink its parent if we have such a branch.
    newLeft.middle = inNode.middle;
//    System.out.println("newLeft.middle="+newLeft.middle);
    
    if (newLeft.middle != null) {
      newLeft.middle.parent = newLeft;
//      System.out.println("newLeft.middle.parent="+newLeft.middle.parent);
    }
    //Move branch 3, and relink its parent if we have such a branch.
    newRight.left = inNode.middle2;
//    System.out.println("newRight.left="+newRight.left);

    if (newRight.left != null) {
      newRight.left.parent = newRight;
//      System.out.println("newRight.left.parent="+newRight.left.parent);
    }

    //Move branch 4, and relink its parent if we have such a branch.
    newRight.middle = inNode.right;
//    System.out.println("newRight.middle="+newRight.middle);

    if(newRight.middle != null) {
      newRight.middle.parent = newRight;
//      System.out.println("newRight.middle.parent="+newRight.middle.parent);
    }
    
//    inNode.left = inNode.right = inNode.middle = null;
//    inNode = null;
    
//    System.out.println("newLeft="+newLeft);
//    System.out.println("newRight="+newRight);
//    System.out.println(this);
//    printLevels();
 
//    System.out.println("end convert4to2 returning newRoot="+newRoot);
    return newRoot;
  }

  /**
   * Traverses the tree in-order and adds the KeyValues to a List.
   * @param curNode The current node we should traverse/add.
   * @param treeItems List to add KeyValues to.
   */
  private void traverseTree (Node curNode, List<KeyValuePair> treeItems) {
    //If leaf node.
    if (curNode.left == null) {
      //Add first value.
      treeItems.add(curNode.keyvalues1);

      //If leaf is ThreeNode, then add second value.
      if (curNode.keyvalues2 != null)
        treeItems.add(curNode.keyvalues2);
    }
    else if (curNode.keyvalues2 == null) { //If TwoNode.
      traverseTree(curNode.left, treeItems); //Add lesser values first.
      treeItems.add(curNode.keyvalues1); //Then this value.
      traverseTree(curNode.middle, treeItems); //And greater values.
    }
    else { //If ThreeNode.
      traverseTree(curNode.left, treeItems); //Lesser values.
      treeItems.add(curNode.keyvalues1); //Low value.
      traverseTree(curNode.middle, treeItems); //Middle values.
      treeItems.add(curNode.keyvalues2); //High value.
      traverseTree(curNode.right, treeItems); //Higher values.
    }
  }
  
  @Override public String toString() {
    // print keyvalues by keys ascending
    StringBuilder sb = new StringBuilder();
    buildStringInOrder(root, sb);
    return sb.toString();
  }
  
  public TwoThreeTree(){};
  
  public TwoThreeTree (K[] a, V[] b) {
    if (a == null || b == null) return;
    if (kclass == null) kclass = a.getClass().getComponentType();
    if (vclass == null) vclass = b.getClass().getComponentType();
    int n = Math.min(a.length, b.length);
    if (n == 0) return; int c = 0;
    Tuple2<K,V> ta[] = ofDim(Tuple2.class, n);
    for (int i = 0; i<n; i++)
      if (a[c] != null && b[c] != null)
        ta[c] = new Tuple2<K,V>(a[c], b[c++]);
    if (c == 0) return;
    ta = take(ta, c); n = ta.length;
    for (int i = 0; i<n; i++) { put(ta[i]._1, ta[i]._2); }  
  }

  
  private void buildStringInOrder(Node curNode, StringBuilder treeItems) {
    //If leaf node.
    if (curNode.left == null) {
      //Add first value.
      treeItems.append(curNode.keyvalues1);

      //If leaf is ThreeNode, then add second value.
      if (curNode.keyvalues2 != null)
        treeItems.append(curNode.keyvalues2);
    }
    else if (curNode.keyvalues2 == null) { //If TwoNode.
      buildStringInOrder(curNode.left, treeItems); //Add lesser values first.
      treeItems.append(curNode.keyvalues1); //Then this value.
      buildStringInOrder(curNode.middle, treeItems); //And greater values.
    }
    else { //If ThreeNode.
      buildStringInOrder(curNode.left, treeItems); //Lesser values.
      treeItems.append(curNode.keyvalues1); //Low value.
      buildStringInOrder(curNode.middle, treeItems); //Middle values.
      treeItems.append(curNode.keyvalues2); //High value.
      buildStringInOrder(curNode.right, treeItems); //Higher values.
    }
  }
  
  public void printLevels() {
    if (size() == 0) return;
    for (int i = 1; i < depth()+2; i++) {
      System.out.print("Level " + (i-1) + ": ");
      String levelNodes = printLevel(root, i);
      System.out.print(levelNodes + "\n");
    }
  }

  private String printLevel(Node node, int level) {
    if (node == null) return "";
    if (level == 1) {
      return node + " ";
    } else if (level > 1) {
      String leftStr = printLevel(node.left, level - 1);
      String midStr = printLevel(node.middle, level - 1);
      String rightStr = printLevel(node.right, level - 1);
      return leftStr + midStr + rightStr;
    }
    else  return "";
  }
  
  public String shape() {
    // return a string of level shapes classified as 1 or 2 in which levels
    // are separated with ":" and node shapes are separated with ";". 
    // on each level shapes are sorted to enable easy classification.
    // for example ":1;:1;2;:1;1;1;1;2;" means 1 2-node on level 0, one 
    // 2-node and 1 3-node on level 1 and 3 2-nodes and 1 3-node on level 2.
    // for ex3305 that states to ignore order of subtrees in each level.
    if (size() == 0) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < depth()+2; i++) {
//      System.out.print(":");
      String levelNodes = shape(root, i);
      String[] a = levelNodes.split(";");
      Arrays.sort(a);
      sb.append(":");
      for (int j = 0; j < a.length; j++) sb.append(a[j]+";");
      //System.out.print(sb.toString());
    }
    return sb.toString();
  }

  private String shape(Node node, int level) {
    if (node == null) return "";
    if (level == 1) {
      return node.toShape();
    } else if (level > 1) {
      String leftStr = shape(node.left, level - 1);
      String midStr = shape(node.middle, level - 1);
      String rightStr = shape(node.right, level - 1);
      return leftStr + midStr + rightStr;
    }
    else  return "";
  }

  /**
   * Merges the specified node with the specified tree node.
   * @param treeNode The node in the tree we are going to merge with.
   * @param separateNode TwoNode to merge into the tree.
   * @return If merged with a ThreeNode we get a resultant FourNode 
   * as a result that needs to be merged again.
   */
  private FourNode mergeNodes(Node treeNode, Node separateNode) {   
    //The separate node we are sending in is assumed to be a TwoNode.

    //Possible merge result.
    FourNode newFourNode = null;

    //If the node in the tree we are merging with is a TwoNode.
    if (treeNode.keyvalues2 == null) {
      //The thing we are merging is smaller than the treeNode's key.
      if(separateNode.keyvalues1.key.compareTo(treeNode.keyvalues1.key) < 0) {
        //Move the key/values to the right and insert.
        treeNode.keyvalues2 = treeNode.keyvalues1;
        treeNode.keyvalues1 = separateNode.keyvalues1;

        //Move the children to the right and insert the separateNode's 
        //children into the tree node.
        treeNode.right = treeNode.middle;
        treeNode.middle = separateNode.middle; //Right child in the separateNode.
        treeNode.left = separateNode.left;
      } else if (separateNode.keyvalues1.key.compareTo(treeNode.keyvalues1.key) > 0) {
        //Insert key/values.
        treeNode.keyvalues2 = separateNode.keyvalues1;

        //Insert the children.
        treeNode.right = separateNode.middle; //Right node in the separateNode.
        treeNode.middle = separateNode.left;
      }

      //Don't forget to relink the parent property after we've moved children around.
      separateNode.middle.parent = treeNode;
      separateNode.left.parent = treeNode;            
    } else { //If the node in the tree we are merging with is a ThreeNode.
//    {
      //If the key in the separate node is smaller than the three node's first key.
      if (separateNode.keyvalues1.key.compareTo(treeNode.keyvalues1.key) < 0) {
        newFourNode = new FourNode(separateNode.keyvalues1, treeNode.keyvalues1, 
            treeNode.keyvalues2);

        newFourNode.left = separateNode.left;
        newFourNode.middle = separateNode.middle;
        newFourNode.middle2 = treeNode.middle;
        newFourNode.right = treeNode.right;
      } else if (separateNode.keyvalues1.key.compareTo(treeNode.keyvalues1.key) > 0 
          && separateNode.keyvalues1.key.compareTo(treeNode.keyvalues2.key) < 0) {
        newFourNode = new FourNode(treeNode.keyvalues1, separateNode.keyvalues1, 
            treeNode.keyvalues2);

        newFourNode.left = treeNode.left;
        newFourNode.middle = separateNode.left;
        newFourNode.middle2 = separateNode.middle;
        newFourNode.right = treeNode.right;
      } else { //If not smaller or in the middle of our values it must be bigger.
        newFourNode = new FourNode(treeNode.keyvalues1, treeNode.keyvalues2, 
            separateNode.keyvalues1);

        newFourNode.left = treeNode.left;
        newFourNode.middle = treeNode.middle;
        newFourNode.middle2 = separateNode.left;
        newFourNode.right = separateNode.middle;
      }

      //Relink the children to our FourNode.
      newFourNode.left.parent = newFourNode;
      newFourNode.middle.parent = newFourNode;
      newFourNode.middle2.parent = newFourNode;
      newFourNode.right.parent = newFourNode;     
    }

    //If no new FourNode then this will be null. 
    return newFourNode;
  }

  /**
   * Searches a list for the specified key.
   * @param key The key to search for.
   * @param listToSearch The list of KeyValues to search for.
   * @return The index to the first (and only) occurrence of the key in the list.
   */
  private int indexOfKeyInList(K key, List<KeyValuePair> listToSearch) {
    for (int i = 0; i < listToSearch.size(); i++) {
      KeyValuePair element = listToSearch.get(i);

      if (element !=null && element.equals(new KeyValuePair (key, null)))
        return i;
    }
    return -1; //Not in the list.
  } 

  private class KeyValuePair {
    public K key;
    public V value;

    public KeyValuePair(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    @Override public String toString() {
      return "("+key+","+value+")";
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object o) {
      //Assume we are comparing to another KeyValuePair with the same K, V.
      if (o instanceof TwoThreeTree.KeyValuePair) {
        KeyValuePair obj = (KeyValuePair) o;
        //KeyValuePairs are equal if keys are equal.
        return this.key.equals(obj.key);
      } else return false;
    }
    
    @Override public int hashCode() {
      return Objects.hash(key);
    }
  }
  
  public static void main(String[] args) {
    
    Integer[] a = {0,1,2,5,6,3,4};
    Integer[] b = rangeInteger(0,a.length);
    TwoThreeTree<Integer,Integer> ttt = new TwoThreeTree<>(a,b); 
    // {0,1,2,5,6,3,4}
    System.out.println(ttt.shape()); //:1;:1;1;:1;1;1;2;
    System.out.println(ttt); //(0,0)(1,1)(2,2)(3,5)(3,5)(2,2)(5,3)(4,6)
    ttt.printLevels();
    System.out.println("\nputting 4,6");
    ttt.put(4,6);
    ttt.printLevels(); 
    System.out.println(ttt.shape());
    // new res
    // Level 0: (3) 
    // Level 1: (1,[3]) (5,[3]) 
    // Level 2: (0,[1]) (2,[1]) (4,[5]) (6,[5]) 
    // :1;:1;1;:1;1;1;1;
    
    // old res
    // Level 0: (3) 
    // Level 1: (1,[3]) (5,[3]) 
    // Level 2: (0,[1]) (2,3,[1]) (2,[5]) 4,[5])
    //
    // for {0,1,2,5,6,3} :
    // :2;:1;1;2;
    // (0,0)(1,1)(2,2)(3,5)(5,3)(6,4)
    // Level 0: (1,5) 
    // Level 1: (0,[1]) (2,3,[1]) (6,[1]) 
    // inserting 4 should put it in (2,3,4,[1]) that's split
    // into (2,[1]) and (4,[1]) on level 1 with 3 moved to root = (1,3,5)
    // then root is split resulting in
    //            3
    //           / \
    //         1    5
    //        0 2  4 6
    //
    // 
    
//    String[] sa = "EASYQUTION".split("");
//    Integer[] ia = rangeInteger(1,sa.length+1);    
//    TwoThreeTree<String,Integer> ttt = new TwoThreeTree<>(sa,ia);
////    for (int i = 0; i < sa.length; i++) ttt.put(sa[i],ia[i]);
////    list = ttt.traverseTree();
//    System.out.println(ttt); //(A,2)(E,7)(I,10)(N,12)(O,11)(Q,5)(S,8)(T,9)(U,6)(Y,4)
//    System.out.println(ttt.size()); //8
////    ttt.printLevels();
//    // Level 0: (S) 
//    // Level 1: (E,O,[S]) (U,[S]) 
//    // Level 2: (A,[E]) (I,N,[E]) (Q,[E]) (T,[U]) (Y,[U]) 
//    //ttt.printShapes(); // :1;:2;1;:1;2;1;1;1; :one;:two;one;:one;two;one;one;one;
//    System.out.println(ttt.shape());
    
    
  }

}