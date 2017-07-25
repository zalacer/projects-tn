package ex24;

/* p331
  2.4.22 Array resizing. Add array resizing to MaxPQ, and prove bounds 
  like those of Proposition Q for array accesses, in an amortized sense.
  
  Array upsizing is already in http://algs4.cs.princeton.edu/24pq/MaxPQ.java.
  In pq.MaxPQ downsizing has been added.
  
  1. Each compare takes 2 array accesses and no resizing by code inspection.
  2. Each exchange takes 3 array accesses and no resizing by code inspection.
  3. Each insert takes at most amortized ~5lgN array accesses since it may incur 
     array resizing and calls swim which takes at most ~lgN compares and ~lgN 
     exchanges by code inspection.
  4. Each delMax takes at most amortized ~7lgN array accesses since it may incur
     array resizing and calls sink which takes at most ~2lgN compares and ~lgN
     exchanges by code inspection.
  5. Sink based heap construction takes at most amortized ~7lgN array accesses 
     since it may incur array resizing and has been shown to take at most ~2N 
     compares and ~N exchanges (p323).
  6. No comment on the performance of insert, change priority, and delete since 
     there are no reference implementations of them.
  
  Below is a list of all the propositions in section 2.4 of the text.
  
  p313
  Proposition O.  The largest key in a heap-ordered binary tree is found at the root.
  Proof:  By induction on the size of the tree.
  
  p314
  Proposition P. The height of a complete binary tree of size N is N floor(lgN).
  Proof:  The stated result is easy to prove by induction or by noting that the height
  increases by 1 when N is a power of 2.  
  
  p319
  Proposition Q. In an N-key priority queue, the heap 
  algorithms require no more than 1 + lg N compares for 
  insert and no more than 2 lg N compares for remove the
  maximum.
  Proof:  By Proposition P, both operations involve 
  moving along a path between the root and the bottom of 
  the heap whose number of links is no more than lg N. 
  The remove the maximum operation requires two compares
  for each node on the path (except at the bottom): one
  to find the child with the larger key, the other to 
  decide whether that child needs to be promoted.
  
  p321
  Proposition Q (continued). In an index priority queue of size N,
  the number of compares required is proportional to at most log N
  for insert, change priority, delete, and remove the minimum.
  Proof:  Immediate from inspection of the code and the fact that all
  paths in a heap are of length at most ~lg N.
  
  p323
  Proposition R. Sink-based heap construction uses fewer than 2N compares and
  fewer than N exchanges to construct a heap from N items.
  Proof:  This fact follows from the observation that most of the heaps processed are
  small. For example, to build a heap of 127 elements, we process 32 heaps of size 3,
  16 heaps of size 7, 8 heaps of size 15, 4 heaps of size 31, 2 heaps of size 63, and 1
  heap of size 127, so 32·1 + 16·2 + 8·3 + 4·4 + 2·5 + 1·6 = 120 exchanges (twice as
  many compares) are required (at worst). See Exercise 2.4.20 for a complete proof.
  
 */

public class Ex2422MaxPQWithArrayResizing {
  
  public static void main(String[] args) {
    
  }

}
