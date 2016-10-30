package ds;

import static java.lang.Math.*;
import static v.ArrayUtils.*;
import static ds.LinkedList.Node;
import static ds.LinkedList.nodeListToString;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import ds.LinkedList;
import analysis.Timer;

// based on last answer in
// https://stackoverflow.com/questions/15588033/how-do-i-shuffle-nodes-in-a-linked-list

@SuppressWarnings("unused")
public class LinkedListShuffle {
  
  //https://github.com/claudemartin/Recursive/blob/master/Recursive/src/ch/claude_martin/recursive/Recursive.java
  @FunctionalInterface
  public interface RecursiveBiFunction<T, U, R> {
    R apply(final T t, final U u, final BiFunction<T, U, R> self);
  }
  
  public static class Recursive<F> {
    private F f;
    public static <T, U, R> BiFunction<T, U, R> biFunction(RecursiveBiFunction<T, U, R> f) {
      final Recursive<BiFunction<T, U, R>> r = new Recursive<>();
      return r.f = (t, u) -> f.apply(t, u, r.f);
    }
  }
  
  @FunctionalInterface
  public interface Function4<A, B, C, D, E> {
    E apply(A a, B b, C c, D d);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void shuffle2(LinkedList<T> ll) {
    if (ll == null || ll.size() < 2) return;
//    System.out.println("ll.getFirst="+nodeListToString(ll.getFirst()));
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) {
      System.out.println("cannot get a SecureRandom strong instance, using pseudorandomization");
      r = new Random(System.currentTimeMillis());
    }
    Object[] o = shuffleMerge(ll.getFirst(), ll.size(), r);
//    System.out.println("r="+nodeListToString(r));
//  System.out.println("r="+nodeListToString(r));
    Node<T> last = (Node<T>)(o[1]);
    if (last.next != null) last = last.next;
//  System.out.println("originalLast="+ll.getLast());
    ll.setFirst((Node<T>)(o[0]));  ll.setLast(last);
//  System.out.println("first="+o[0]+" last="+o[1]);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void shuffle(LinkedList<T> ll) {
    if (ll == null || ll.size() < 2) return;
//    System.out.println("ll.getFirst="+nodeListToString(ll.getFirst()));
    Random q = null;
    try {
      q = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("cannot get a SecureRandom strong instance");
    }
    if (q == null) q = new Random(System.currentTimeMillis());
    final Random r = q;
    
    // merge replacement
    Function4<Object[],Object[],Integer,Integer,Object[]> mrg = (lobj, robj, ls, rs) -> {
      Node<T> left = (Node<T>)(lobj[0]); Node<T> right = (Node<T>)(robj[0]);
      Node<T> head = new Node<T>(), cur = head, last = null;
      int i = 0, j = 0, llen, rlen; double d;

      while (i < ls || j < rs) {
        llen = ls - i; rlen = rs - j;
        d = r.nextDouble();
        // use probabilities based on current ls and rs to select next node for uniformity
        if (d < 1.*llen/(llen+rlen)) { cur.next = left; left = left.next; i++; }
        else { cur.next = right; right = right.next; j++; }
        cur = cur.next;  last = cur;  
        //        System.out.println("cur="+nodeListToString(cur));
      }

      return new Object[]{head.next, last};
    };
      
    // shuffleMerge replacement
    BiFunction<Node<T>,Integer,Object[]> sm = Recursive.biFunction((node, size, self) -> {
      if (node == null) 
        throw new IllegalArgumentException("shuffleMerge: node can't be null");
      if (node.next == null) return new Object[]{node,node};

      Function<Node<T>,Object[]> getMiddle = (firstNode) -> {
        if (firstNode.next == null) return new Object[]{firstNode,1};
        Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
        while (fast.next != null && fast.next.next != null) {
          pslow = slow;
          slow = slow.next; c++;
          fast = fast.next.next;
        }
        return new Object[]{slow,c};
      };

      Node<T> left = node;    
      Object[] m = getMiddle.apply(left);
      Node<T> middle = (Node<T>)m[0];
      int ls = (int)m[1]; // left size
      int rs = size - ls; // right size
      Node<T> right = middle.next;
      //      Node<T> merged = null;
      middle.next = null;
      Object[] o = mrg.apply(self.apply(left,ls), self.apply(right,rs), ls, rs);
      //      System.out.println("merged="+nodeListToString(merged));
      return o;
    });
      
    Object[] o = sm.apply(ll.getFirst(), ll.size());
//    System.out.println("r="+nodeListToString(r));
    Node<T> last = (Node<T>)(o[1]);
    if (last.next != null) last = last.next;
//      while(last.next != null) last = last.next;
//    }
//    System.out.println("originalLast="+ll.getLast());
    ll.setFirst((Node<T>)(o[0]));  ll.setLast(last);
//    System.out.println("first="+o[0]+" last="+o[1]);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> Object[] shuffleMerge(Node<T> node, int size, Random r) {
    if (node == null) 
      throw new IllegalArgumentException("shuffleMerge: node can't be null");
    if (node.next == null) return new Object[]{node,node};
    Function<Node<T>,Object[]> getMiddle = (Node<T> firstNode) -> {
      if (firstNode.next == null) return new Object[]{firstNode,1};
      Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
      while (fast.next != null && fast.next.next != null) {
        pslow = slow;
        slow = slow.next; c++;
        fast = fast.next.next;
      }
      return new Object[]{slow,c};
    };
    Node<T> left = node;    
    Object[] m = getMiddle.apply(left);
    Node<T> middle = (Node<T>)m[0];
    int ls = (int)m[1]; // left size
    int rs = size - ls; // right size
    Node<T> right = middle.next;
//    Node<T> merged = null;
    middle.next = null;
    Object[] o = merge(shuffleMerge(left,ls,r), shuffleMerge(right,rs,r), ls, rs, r);
//    System.out.println("merged="+nodeListToString(merged));
    return o;
  }
   
  @SuppressWarnings("unchecked")
  private static <T> Object[] merge(Object[] lobj, Object[] robj, int ls, int rs, Random r) {
//  System.out.println("entered merge left="+left+" right="+right);
    Node<T> left = (Node<T>)(lobj[0]); Node<T> right = (Node<T>)(robj[0]);
    Node<T> head = new Node<T>(), cur = head, last = null;
    int i = 0, j = 0, llen, rlen; double d;

    while (i < ls || j < rs) {
      llen = ls - i; rlen = rs - j;
      d = r.nextDouble();
      // use probabilities based on current ls and rs to select next node for uniformity
      if (d < 1.*llen/(llen+rlen)) { cur.next = left; left = left.next; i++; }
      else { cur.next = right; right = right.next; j++; }
      cur = cur.next;  last = cur;  
//      System.out.println("cur="+nodeListToString(cur));
    }

    return new Object[]{head.next, last};
}
 
  public static <T> Object[] getMiddleForShuffleMerge(Node<T> firstNode){
    if (firstNode.next == null) return new Object[]{firstNode,1};
    Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
    while (fast.next != null && fast.next.next != null) {
      pslow = slow;
      slow = slow.next; c++;
      fast = fast.next.next;
    }
//    System.out.println("middle="+slow);
    return new Object[]{slow,c};
  }
  
  public static void testShuffle(int n, int trials) {
    // runs shuffle on a LinkedList with Integer node values from 0..n-1
    // trials times, collects bias data and determines elmentary statistical
    // measures of it.
    
    int[][] bias = new int[n][n];       // array of bias data
    Integer[] in = new Integer[n];      // per trial array of LinkedList node items
    int[] all = new int[n*n];   // all bias data for overall stats
    double[][] stat = new double[n][4]; // array of arrays of stat data for each position
    double[] allstat = new double[4];   // array of overall stats
    int c = 0; // counter for iterating from 0 to trials and writing data into all
    double nobias = 1.*trials/n; double maxbias;
    LinkedList<Integer> lla; // the test LinkedList
    
    Timer timer = new Timer();
    while (true) {
      c++;
      lla  = new LinkedList<Integer>(rangeInteger(0,n));
      shuffle2(lla);
//      try {
//        shuffle(lla);
//      } catch (NoSuchAlgorithmException e) {
//        e.printStackTrace();
//      }
      in = lla.toArray(1);
      assert lla.getLast().item == in[in.length-1];
      for (int i = 0; i < in.length; i++) bias[in[i]][i]++;
      if (c == trials) break;
    }
    System.out.println("elapsed time = "+timer.num()+" ms");   
    
    System.out.println("\nbias matrix for "+n+" element list and "+trials+" shuffles");
    for (int i = 0; i < bias.length; i++) pa(bias[i],-1);
    
    for (int i = 0; i < bias.length; i++){
      stat[i][0] = min(bias[i]); stat[i][1] = max(bias[i]); 
      stat[i][2] = mean(bias[i]); stat[i][3] = stddev(bias[i]); 
    }
    
    c = 0;
    for (int i = 0; i < bias.length; i++)
      for (int j = 0; j < bias[i].length; j++) 
        all[c++] = bias[i][j];
    
    allstat[0] = min(all); allstat[1] = max(all);
    allstat[2] = mean(all); allstat[3] = stddev(all);
    
    System.out.println("\nallstat");
    System.out.println("min max mean stddev");
    pa(allstat,-1);
    
    maxbias = abs(nobias-allstat[0]) > abs(nobias-allstat[1]) 
        ? 1.*allstat[0] - nobias : 1.*allstat[1] - nobias;
        
    System.out.println("\nmax bias = "+maxbias);
    System.out.println("max bias/trials % = "+maxbias*100/trials);
    
    System.out.println("\nstat");
    System.out.println("min max mean stddev");
    for (int i = 0; i < stat.length; i++) pa(stat[i],-1);
    System.out.println(); 
  }

  public static void main(String[] args) {
    
    testShuffle(8, 1000);

  }
  
}

//@SuppressWarnings("unchecked")
//private static <T> Object[] merge2(Object[] lobj, Object[] robj, int ls, int rs, Random r) {
////System.out.println("entered merge left="+left+" right="+right);
//  Node<T> left = (Node<T>)(lobj[0]); Node<T> right = (Node<T>)(robj[0]);
//  Node<T> head = new Node<T>(), cur = head, ex = null, last = null;
//  int c = 0, d, i, pos = 0;  boolean extra = false;
//
//  if (ls > 0 && rs > 0 && ls != rs) {
//    d = ls < rs ? rs - ls : ls - rs;
//    if (d != 1) System.out.println("merge2.d ="+d);
//    if (ls < rs) { ex = right; right = right.next; }
//    else         { ex = left;  left  = left.next; }
//    extra = true; pos = r.nextInt(ls+rs);
//    //    if (pos == ls+rs -1) System.out.println("extra at end");
//  }
//
//  while (left != null || right != null) {
//    if (extra && pos == c) { 
//      cur.next = ex; cur = cur.next; last = cur;
//      //      System.out.println("extra done");
//      extra = false; continue;
//    }
//    i = r.nextInt(2);
//    if (i == 0) {
//      if (left != null) { cur.next = left; left = left.next; } 
//      else { cur.next = right; right = right.next; }
//    }
//    else {
//      if (right != null) { cur.next = right; right = right.next; } 
//      else { cur.next = left; left = left.next; }
//    }
//    cur = cur.next;  last = cur; c++; 
////    System.out.println("cur="+nodeListToString(cur));
//  }
//  //  System.out.println("here1");
//  if (extra) { cur.next = ex; cur = cur.next; cur.next = null;}
//  //  System.out.println("here2");
//
//  //  System.out.println("head.next="+nodeListToString(head.next));
//
//  return new Object[]{head.next, last};
//}
//
//public static <T> Node<T> getMiddle(Node<T> firstNode){
//if (firstNode.next == null) return firstNode;
//
//Node<T> fast, slow, pslow;
//fast = slow = firstNode;
//while (fast.next != null && fast.next.next != null) {
//pslow = slow;
//slow = slow.next;
//fast = fast.next.next;
//}
////System.out.println("middle="+slow);
//return slow;
//}
//
//private static <T> Node<T> merge2(Node<T> left, Node<T> right, int ls, int rs) {
////System.out.println("entered merge left="+left+" right="+right);
//Node<T> tmp = new Node<T>();
//Node<T> cur = tmp; int i; double p, u = 1.*ls/(ls+rs); Random r = null; 
//
//try {
//  r = SecureRandom.getInstanceStrong();
//} catch (NoSuchAlgorithmException e) {}
//if (r == null) r = new Random(System.currentTimeMillis());
//while (left != null || right != null) {
//  p =  r.nextDouble(); 
//  i = r.nextInt(2);
//  //if (p < u) {
//  if (i == 0) {
//    if (left != null) { cur.next = left; left = left.next; } 
//    else { cur.next = right; right = right.next; }
//  }
//  else {
//    if (right != null) { cur.next = right; right = right.next; } 
//    else { cur.next = left; left = left.next; }
//  }
//  cur = cur.next;                     
//}
//return tmp.next;
//}
//
//private static <T> Node<T> merge(Node<T> left, Node<T> right) {
////System.out.println("entered merge left="+left+" right="+right);
//Node<T> dummy = new Node<T>();
//Node<T> cur = dummy; int i; Random r = null;
//
//try {
//  r = SecureRandom.getInstanceStrong();
//} catch (NoSuchAlgorithmException e) {}
//if (r == null) r = new Random(System.currentTimeMillis());
//while (left != null || right != null) {
//  i =  r.nextInt(2);
////  System.out.println("i="+i);
//  if (i == 0) {
//    if (left != null) { cur.next = left; left = left.next; } 
//    else { cur.next = right; right = right.next; }
//  }
//  else {
//    if (right != null) { cur.next = right; right = right.next; } 
//    else { cur.next = left; left = left.next; }
//  }
//  cur = cur.next;                     
//}
//return dummy.next;
//}
//
//public static <T> Node<T> Shuffle(Node<T> firstNode) {
//if (firstNode == null)
//throw new IllegalArgumentException();
//
//if (firstNode.next == null)
//return firstNode;
//
//Node<T> middle = GetMiddle(firstNode);
//Node<T> rightNode = middle.next;
//middle.next = null;
//
//Node<T> mergedResult = ShuffledMerge(Shuffle(firstNode), Shuffle(rightNode));
//return mergedResult;
//}
//
//
//private static <T> Node<T> merge2(Node<T> leftNode, Node<T> rightNode) {
//Node<T> dummyHead = new Node<T>();
//Node<T> curNode = dummyHead;
//
//Random r = null; 
//try {
//r = SecureRandom.getInstanceStrong();
//} catch (NoSuchAlgorithmException e) {}
//if (r == null) r = new Random(System.currentTimeMillis());
//while (leftNode != null || rightNode != null) {
//int rndRes =  r.nextInt(2);
//if (rndRes == 0) {
//  if (leftNode != null) {
//    curNode.next = leftNode;
//    leftNode = leftNode.next;
//  } else {
//    curNode.next = rightNode;
//    rightNode = rightNode.next;
//  }
//} else {
//  if (rightNode != null) {
//    curNode.next = rightNode;
//    rightNode = rightNode.next;
//  } else {
//    curNode.next = leftNode;
//    leftNode = leftNode.next;
//  }
//}
//curNode = curNode.next;                     
//}
//return dummyHead.next;
//}