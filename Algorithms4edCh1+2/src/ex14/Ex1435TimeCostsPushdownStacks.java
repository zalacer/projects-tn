package ex14;

/*
  p213
  1.4.35 Time costs for pushdown stacks. Justify the entries in the table below, which
  shows typical time costs for various pushdown stack implementations, using a cost
  model that counts both data references (references to data pushed onto the stack, either
  an array reference or a reference to an object’s instance variable) and objects created.
  
  Time costs for pushdown stacks (various implementations)
                                cost to push N int values
  data structure  item type  data references  objects created
  --------------  ---------  ---------------  ---------------
  linked list     int              2N               N
  linked list     Integer          3N               2N
  resizing array  int             ~5N               lgN
  resizing array  Integer         ~5N              ~N

   push from pushdown stack (linked-list implementation) on p149:
     public void push(Item item) {
       Node oldfirst = first;
       first = new Node();
       first.item = item;
       first.next = oldfirst;
       N++;
     }
     
   The pushdown stack (linked-list implementation) on p149 isn't suitable for int items
   since it's generic and so designed only for object items and would autobox ints.
   I wrote one that's purely int based, except for its iterator which is necessarily generic. 
   It's called IntStack and is at ds.IntStack.java.
   
   push from IntStack:
     public void push(int item) { 
       Node oldfirst = first;
       first = new Node(item, oldfirst); // Node here does not autobox item
       N++;
       opcount++;
  }
    
   1. Using the IntStack version of push for int items with a linked-list stack implementation
      item could be considered to be a data reference since its passed by reference (although
      for primitives that copies its value). first is also a reference to an object to a Node
      containing the item pushed so it can count as a data reference. For N items pushed there
      will be 2N data references and N Node objects created.
       
   2. Using push from pushdown stack (linked-list implementation) on p149 for Integer items
      the item passed to push is itself a reference and is passed by reference so it can count
      as two references and first.item is a third. For each item pushed a new Node is created.
      For N items pushed there will be 3N data references and N Node objects created.
   
   push() and resize() from pushdown (LIFO) stack (resizing array implementation) on p141
     public void push(Item item) {
       if (N == a.length) resize(2*a.length);
       a[N++] = item;
     }
     private void resize(int max) {
       Item[] temp = (Item[]) new Object[max];
       for (int i = 0; i < N; i++)
         temp[i] = a[i];
       a = temp;
     }
          
   The pushdown stack (resizing array implementation) on p141 isn't suitable for int 
   items, since it's generic and so designed only for object items and would autobox ints.
   I wrote one that's purely int based, except for its iterator which is necessarily generic. 
   It's called ResizingIntArrayStack and is at ds.ResizingIntArrayStack.java.
   
   push() and resize() from ResizingIntArrayStack:
     public void push(int item) { 
       if (N == a.length) resize(2*a.length);
       a[N++] = item;
     }
     private void resize(int max) { 
       int[] temp = new int[max];
       for (int i = 0; i < N; i++)
         temp[i] = a[i];
       a = temp;
    }
   
   3. Using the ResizingIntArrayStack version of push for int items with a resizing array
      stack implementation using the same criteria used in (1) and (2) above, item and
      a[N] can be considered as data references. Factoring in resize, for a stack that
      continually grows to size N, resize will be invoked ~lgN times creating ~lgN int
      array objects and overall using ~(2**lg(N-1) = N/2 ~ N data references to initialize
      array elements. Including the data references from each push that comes to ~3N data 
      references and ~lgN array objects.
    
   4. Using push from pushdown stack (resizing array implementation) on p141 for Integer
      items the item passed to push is itself a reference and is passed by reference so it
      can count as two references and a[N] is another. Somewhere along the line each Item 
      passed to push must be objectified for a total of N objects. Then, as in (3), for a 
      stack that continually doubles to size N, resize will use N data references to 
      initialize array elements. Including the data references from each push that comes
      to ~4N data references and ~N objects.
      
   The results in (3) and (4) could be beefed up by adding in an extra array reference from
   push when it assigns N to a.length, but that still doesn't give ~5N data references for
   ResizingIntArrayStack with int items. In conclusion it's not clear what is meant by data
   reference or exactly which stack implementations are to be used. For example, if the
   pushdown stack (resizing array implementation) on p141 is used, its push would autobox
   int arguments creating a worst case total of N of them over N pushes and contradicting
   the lgN cost given for that in the time costs table.

*/ 



  public class Ex1435TimeCostsPushdownStacks {
    
  public static void main(String[] args) {

  }

}
