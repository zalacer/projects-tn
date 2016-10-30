package ex13;

import static v.ArrayUtils.pa;

import java.util.Iterator;

import ds.RandomBag;

//  p168
//  1.3.34  Random bag. A random bag stores a collection of items and supports the fol-
//  lowing API:
//  public class RandomBag<Item> implements Iterable<Item>
//  RandomBag()  create an empty random bag
//  boolean isEmpty()  is the bag empty?
//  int size()  number of items in the bag
//  void add(Item item)  add an item
//  API for a generic random bag
//  Write a class  RandomBag that implements this API. Note that this API is the same as for
//  Bag , except for the adjective random, which indicates that the iteration should provide
//  the items in random order (all N ! permutations equally likely, for each iterator). Hint :
//  Put the items in an array and randomize their order in the iterator’s constructor.

public class Ex1334RandomBag {

  public static void main(String[] args) {
    
    Integer[] ia = {1,2,3,4,5,6,7,8,9};
    RandomBag<Integer> rb = new RandomBag<Integer>(ia);
    System.out.println(rb); // RandomBag(7,6,8,3,1,9,2,4,5)    
    System.out.println(rb.toFifoString()); //RandomBag(9,8,7,6,5,4,3,2,1)
    pa(rb.toArray(1)); //Integer[3,6,2,5,8,4,1,9,7]
    pa(rb.toFifoArray(1)); //Integer[9,8,7,6,5,4,3,2,1]
    Iterator<Integer> randomIterator = rb.iterator(); 
    while (randomIterator.hasNext()) System.out.print(randomIterator.next()+" ");
    // 4 9 5 8 3 7 2 6 1 
       
  }

}
