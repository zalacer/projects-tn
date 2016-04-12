package ch10.concurrency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// 19. Describe two different ways in which the Stack data 
// structure can fail to contain the correct elements.

// push contention:
// Suppose thread1 is in push and has just set n.value = newValue while 
// thread2 also enters push and sets n = new Node() before thread1 has 
// set top = n or even n.next = top, then thread1's newValue is lost. 
//  This implies a non-zero failure ratefor concurrent pushes and is 
// demonstrated below.

// pop contention: 
// Thread2 could enter pop and set n = top right after thread1 has set 
// top to n.next, then when thread1 returns n.value its really returning 
// n.next.value and n.next has been removed from the stack but never 
// returned. On the other hand, if thread2 sets n = top before thread1 has 
// set top to n.next they will both return the same n.value. Both conditions 
// imply a non-zero failure rate for concurrent pops, not that pop execution
//  would produce an error but that some nodes won't ever be returned from
// any pop and some will be returned more than once. This is demonstrated
// below.
//
// pop and push interference should be problematic without syncronization; 
// testing shows this is true even with synronized pops and pushes but that
// could be due to bias in concurrency management.

public class Ch1019StackImplementation {

  public static class Stack {
    class Node { Object value; Node next; };
    private Node top;
    public void push(Object newValue) {
      Node n = new Node();
      n.value = newValue;
      n.next = top;
      top = n;
    }
    public Object pop() {
      if (top == null) return null;
      Node n = top;
      top = n.next;
      return n.value;
    }

    public void clear() {
      top = null;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Stack[");
      Node current = top;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          builder.append(current.value);
          c++;
          break;
        } else {
          builder.append(current.value+",");
          c++;
        }
        current = current.next;
      }
      builder.append("]"+", size="+c);
      return "size="+c; //builder.toString();
    }

    public int size() {
      Node current = top;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          c++;
          break;
        } else {
          c++;
        }
        current = current.next;
      }

      return c;
    }

  }

  public static class ConcurrentStack {
    class Node { Object value; Node next; };
    private Node top;
    public synchronized void push(Object newValue) {
      synchronized (new ReentrantLock()) {
        Node n = new Node();
        n.value = newValue;
        n.next = top;
        top = n;
      }
    }
    public synchronized Object pop() {
      if (top == null) return null;
      Node n = top;
      top = n.next;
      return n.value;
    }

    public synchronized void clear() {
      top = null;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Stack[");
      Node current = top;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          builder.append(current.value);
          c++;
          break;
        } else {
          builder.append(current.value+",");
          c++;
        }
        current = current.next;
      }
      builder.append("]"+", size="+c);
      return builder.toString();
    }

    public int size() {
      Node current = top;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          c++;
          break;
        } else {
          c++;
        }
        current = current.next;
      }

      return c;
    }

  }

  public static void main(String[] args) throws InterruptedException {

    System.out.println("demo of basic stack behavior");
    Stack stack = new Stack();
    stack.push(1); stack.push(2); stack.push(3); 
    System.out.println(stack); // Stack[3,2,1], size=3
    System.out.println(stack.size()); // 3
    stack.pop();
    System.out.println(stack); // Stack[2,1], size=2
    System.out.println(stack.size()); // 2
    stack.clear();
    System.out.println(stack); // Stack[], size=0
    System.out.println(stack.size()); // 0

    System.out.println("\ndemo of push contention");
    // in this demo two threads will each push 500 ints to a stack 
    // and the size of stack will be save into an ArrayList. After
    // repeating this 1000 times the average, min and max of the 
    // stack's size will be determined and reportedthis will be done 
    // first for Stack and then for ConcurrentStack
    
    Stack stack1 = new Stack();
    Thread a,b;
    ArrayList<Integer> stack1sizes = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      stack1.clear();
      a = new Thread(()->IntStream.range(0,1000).filter(x->x%2==0).forEach(x -> stack1.push(x)));
      b = new Thread(()->IntStream.range(0,1000).filter(x->x%2==1).forEach(x -> stack1.push(x)));
      a.start(); b.start();
      a.join(); b.join();
      stack1sizes.add(stack1.size());
    }
    IntSummaryStatistics summary1 = stack1sizes.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary1.getAverage = "+summary1.getAverage()); // average size = 998.339
    System.out.println("summary1.getMin = "+summary1.getMin()); // min size = 745
    System.out.println("summary1.getMax = "+summary1.getMax()); // max size = 1000
    // this data above shows the non-zero failure rate for concurrent pushes, namely the average and 
    // min sizes of stack1sizes should be exactly 1000, but its less than that.

    System.out.println("\ndemo showing that push contention errors are eliminated with ConcurrentStack");        
    ConcurrentStack stack2 = new ConcurrentStack();
    Thread c,d;
    ArrayList<Integer> stack2sizes = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      stack2.clear();
      c = new Thread(()->IntStream.range(0,1000).filter(x->x%2==0).forEach(x -> stack2.push(x)));
      d = new Thread(()->IntStream.range(0,1000).filter(x->x%2==1).forEach(x -> stack2.push(x)));
      c.start(); d.start();
      c.join(); d.join();
      stack2sizes.add(stack2.size());
    }
    IntSummaryStatistics summary2 = stack2sizes.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary2.getAverage = "+summary2.getAverage()); // average size = 1000.0
    System.out.println("summary2.getMin = "+summary2.getMin()); // min size = 1000
    System.out.println("summary2.getMax = "+summary2.getMax()); // max size = 1000

    System.out.println("\ndemo of pop contention resulting in duplicated node retrievals and possibly lost nodes");
    // in this demo a Stack is filled with 1000 unique ints, then 4 
    // concurrent threads pop 250 ints and save them to a separate 
    // ArrayList, one for each thread. This is repeated 1000 times. 
    // For each pass, the 4 ArrayLists for the threads are merged, 
    // a set is constructed from it to determine the number of duplicate 
    // nodes by comparison, and the size of this set and the number of 
    // null elements in the merged ArrayList are saved in two additional
    // ArrayLists for subsequent analysis.

    Stack stack3 = new Stack();
    Thread e, f, g, h;
    ArrayList<Integer> totalPops = new ArrayList<>();
    ArrayList<Integer> combined = new ArrayList<>();
    ArrayList<Integer> combinedStats = new ArrayList<>();
    ArrayList<Integer> nullStats = new ArrayList<>();
    ArrayList<Integer> stackStats = new ArrayList<>();
    Set<Integer> combinedSet;
    int numNulls;
    for (int i = 0; i < 1000; i++) {
      stack3.clear();
      IntStream.range(0,1000).forEach(x -> stack3.push(x));
      assert stack3.size() == 1000;
      ArrayList<Integer> al1 = new ArrayList<>();
      ArrayList<Integer> al2 = new ArrayList<>();  
      ArrayList<Integer> al3 = new ArrayList<>();
      ArrayList<Integer> al4 = new ArrayList<>();       
      e = new Thread(()->IntStream.range(0,250).forEach(x -> al1.add((int) stack3.pop())));
      f = new Thread(()->IntStream.range(0,250).forEach(x -> al2.add((int) stack3.pop())));
      g = new Thread(()->IntStream.range(0,250).forEach(x -> al3.add((int) stack3.pop())));
      h = new Thread(()->IntStream.range(0,250).forEach(x -> al4.add((int) stack3.pop())));
      e.start(); f.start(); g.start(); h.start();
      e.join(); f.join(); g.join(); h.join();
      totalPops.add(al1.size() + al2.size() + al3.size() + al4.size());
      combined.addAll(al1);
      combined.addAll(al2);
      combined.addAll(al3);
      combined.addAll(al4);
      combinedSet = new HashSet<Integer>(combined);
      numNulls = 0;
      for (Integer k : combined) {
        if (k == null) numNulls++;
      }
      if (numNulls != 0) nullStats.add(numNulls);
      combinedStats.add(combinedSet.size() - numNulls);
      stackStats.add(stack3.size());
    }

    IntSummaryStatistics summary3 = totalPops.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary3.getAverage = "+summary3.getAverage()); // average size = 1000.0
    System.out.println("summary3.getMin = "+summary3.getMin()); // min size = 1000
    System.out.println("summary3.getMax = "+summary3.getMax()); // max size = 1000
    System.out.println();
    IntSummaryStatistics summary4 = combinedStats.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary4.getAverage = "+summary4.getAverage()); // average size = 999.87
    System.out.println("summary4.getMin = "+summary4.getMin()); // min size = 937
    System.out.println("summary4.getMin = "+summary4.getMin()); // max size = 1000
    System.out.println();
    System.out.println("nullStats.size = "+nullStats.size()); // nullStats.size = 0
    IntSummaryStatistics summary5 = stackStats.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary5.getAverage = "+summary5.getAverage()); // average size = 4.502
    System.out.println("summary5.getMin = "+summary5.getMin()); // min size = 0
    System.out.println("summary5.getMax = "+summary5.getMax()); // max size = 250

    // these results show pop failures, namely the combinedStats average 
    // and min should both be exactly 1000 for the 1000 pops of unique 
    // ints. Since this is true for totalPops and nullStats.size = 0, the 
    // deficiency in combinedStats must be due to pops that returned the
    // same int more than once which also implies that some ints were 
    // never returned as corroborated by the non-zero average and max stack 
    // size after all the pops. This does not show that data was or was not
    // lost, however the failure rate according to combinedStats is 0.00013
    // while its 0.004502 according to stackStats so it looks like most of 
    // the errors were probably due to lack of removal of nodes returned by 
    // pop instead of losing node data.

    System.out.println("\ndemo of resolution of pure pop contention issues with ConcurrentStack");
    ConcurrentStack stack4 = new ConcurrentStack();
    Thread r, s, t, u;
    ArrayList<Integer> totalPops2 = new ArrayList<>();
    ArrayList<Integer> combined2 = new ArrayList<>();
    ArrayList<Integer> combinedStats2 = new ArrayList<>();
    ArrayList<Integer> nullStats2 = new ArrayList<>();
    ArrayList<Integer> stackStats2 = new ArrayList<>();
    Set<Integer> combinedSet2;
    int numNulls2;
    for (int i = 0; i < 1000; i++) {
      stack4.clear();
      IntStream.range(0,1000).forEach(x -> stack4.push(x));
      assert stack4.size() == 1000;
      ArrayList<Integer> al1 = new ArrayList<>();
      ArrayList<Integer> al2 = new ArrayList<>();  
      ArrayList<Integer> al3 = new ArrayList<>();
      ArrayList<Integer> al4 = new ArrayList<>();       
      r = new Thread(()->IntStream.range(0,250).forEach(x -> al1.add((int) stack4.pop())));
      s = new Thread(()->IntStream.range(0,250).forEach(x -> al2.add((int) stack4.pop())));
      t = new Thread(()->IntStream.range(0,250).forEach(x -> al3.add((int) stack4.pop())));
      u = new Thread(()->IntStream.range(0,250).forEach(x -> al4.add((int) stack4.pop())));
      r.start(); s.start(); t.start(); u.start();
      r.join(); s.join(); t.join(); u.join();
      totalPops2.add(al1.size() + al2.size() + al3.size() + al4.size());
      combined2.addAll(al1);
      combined2.addAll(al2);
      combined2.addAll(al3);
      combined2.addAll(al4);
      combinedSet2 = new HashSet<Integer>(combined2);
      numNulls2 = 0;
      for (Integer k : combined2) {
        if (k == null) numNulls2++;
      }
      if (numNulls2 != 0) nullStats2.add(numNulls2);
      combinedStats2.add(combinedSet2.size() - numNulls2);
      stackStats2.add(stack4.size());
    }

    IntSummaryStatistics summary6 = totalPops2.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary6.getAverage = "+summary6.getAverage()); // average size = 1000.0
    System.out.println("summary6.getMin = "+summary6.getMin()); // min size = 1000
    System.out.println("summary6.getMax = "+summary6.getMax()); // max size = 1000
    System.out.println();
    IntSummaryStatistics summary7 = combinedStats2.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary7.getAverage = "+summary7.getAverage()); // average size = 1000.0
    System.out.println("summary7.getMin = "+summary7.getMin()); // min size = 1000
    System.out.println("summary7.getMax = "+summary7.getMax()); // max size = 1000
    System.out.println();
    System.out.println("nullStats2.size = "+nullStats2.size()); // nullStats2.size = 0
    IntSummaryStatistics summary8 = stackStats2.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary8.getAverage = "+summary8.getAverage()); // average size = 0.0
    System.out.println("summary8.getMin = "+summary8.getMin()); // min size = 0
    System.out.println("summary8.getMax = "+summary8.getMax()); // max size = 0

    System.out.println("\ndemo of concurrent push and pop with Stack");
    Stack stack5 = new Stack();
    Thread m, n, o, p;
    ArrayList<Integer> totalPops3 = new ArrayList<>();
    ArrayList<Integer> combined3 = new ArrayList<>();
    ArrayList<Integer> combinedStats3 = new ArrayList<>();
    ArrayList<Integer> nullStats3 = new ArrayList<>();
    ArrayList<Integer> stackStats3 = new ArrayList<>();
    Set<Integer> combinedSet3;
    int numNulls3;
    for (int i = 0; i < 1000; i++) {
      stack5.clear();
      IntStream.range(0,1000).forEach(x -> stack5.push(x));
      assert stack5.size() == 1000;
      ArrayList<Integer> al1 = new ArrayList<>();
      ArrayList<Integer> al2 = new ArrayList<>();      
      m = new Thread(()->IntStream.range(0,250).forEach(x -> al1.add((int) stack5.pop())));
      n = new Thread(()->IntStream.range(1000,1250).forEach(x -> stack5.push(x)));
      o = new Thread(()->IntStream.range(0,250).forEach(x -> al2.add((int) stack5.pop())));
      p = new Thread(()->IntStream.range(1250,1500).forEach(x -> stack5.push(x)));
      m.start(); n.start(); o.start(); p.start();
      m.join(); n.join(); o.join(); p.join();
      totalPops3.add(al1.size() + al2.size());
      combined3.addAll(al1);
      combined3.addAll(al2);
      combinedSet3 = new HashSet<Integer>(combined3);
      numNulls3 = 0;
      for (Integer k : combined3) {
        if (k == null) numNulls3++;
      }
      if (numNulls3 != 0) nullStats3.add(numNulls3);
      combinedStats3.add(combinedSet3.size() - numNulls3);
      stackStats3.add(stack5.size());
    }

    IntSummaryStatistics summary9 = totalPops3.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary9.getAverage = "+summary9.getAverage()); // average size = 500.0
    System.out.println("summary9.getMin = "+summary9.getMin()); // min size = 500
    System.out.println("summary9.getMax = "+summary9.getMax()); // max size = 500
    System.out.println();
    IntSummaryStatistics summary10 = combinedStats3.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary10.getAverage = "+summary10.getAverage()); // average size = 993.473
    System.out.println("summary10.getMin = "+summary10.getMin()); // min size = 491
    System.out.println("summary10.getMax = "+summary10.getMax()); // max size = 1000
    System.out.println();
    System.out.println("nullStats3.size = "+nullStats3.size()); // nullStats3.size = 0
    IntSummaryStatistics summary11 = stackStats3.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary11.getAverage = "+summary11.getAverage()); // average size = 1000.062
    System.out.println("summary11.getMin = "+summary11.getMin()); // min size = 920
    System.out.println("summary11.getMax = "+summary11.getMax()); // max size = 1151

    System.out.println("\ndemo of concurrent push and pop with ConcurrentStack");
    // seems to show even the ConcurrentStack has issues or else concurrency
    // management can favor pops over pushes, see summary13 results belolw.
    ConcurrentStack stack6 = new ConcurrentStack();
    Thread v, w, y, z;
    ArrayList<Integer> totalPops4 = new ArrayList<>();
    ArrayList<Integer> combined4 = new ArrayList<>();
    ArrayList<Integer> combinedStats4 = new ArrayList<>();
    ArrayList<Integer> nullStats4 = new ArrayList<>();
    ArrayList<Integer> stackStats4 = new ArrayList<>();
    Set<Integer> combinedSet4;
    int numNulls4;
    for (int i = 0; i < 1000; i++) {
      stack6.clear();
      IntStream.range(0,1000).forEach(x -> stack6.push(x));
      assert stack6.size() == 1000;
      ArrayList<Integer> al1 = new ArrayList<>();
      ArrayList<Integer> al2 = new ArrayList<>();      
      v = new Thread(()->IntStream.range(0,250).forEach(x -> al1.add((int) stack6.pop())));
      w = new Thread(()->IntStream.range(1000,1250).forEach(x -> stack6.push(x)));
      y = new Thread(()->IntStream.range(0,250).forEach(x -> al2.add((int) stack6.pop())));
      z = new Thread(()->IntStream.range(1250,1500).forEach(x -> stack6.push(x)));
      v.start(); w.start(); y.start(); z.start();
      v.join(); w.join(); y.join(); z.join();
      totalPops4.add(al1.size() + al2.size());
      combined4.addAll(al1);
      combined4.addAll(al2);
      combinedSet4 = new HashSet<Integer>(combined4);
      numNulls4 = 0;
      for (Integer k : combined4) {
        if (k == null) numNulls4++;
      }
      if (numNulls4 != 0) nullStats4.add(numNulls4);
      combinedStats4.add(combinedSet4.size() - numNulls4);
      stackStats4.add(stack6.size());
    }

    IntSummaryStatistics summary12 = totalPops4.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary12.getAverage = "+summary12.getAverage()); // average size = 500.0
    System.out.println("summary12.getMin = "+summary12.getMin()); // min size = 500
    System.out.println("summary12.getMax = "+summary12.getMax()); // max size = 500
    System.out.println();
    IntSummaryStatistics summary13 = combinedStats4.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary13.getAverage = "+summary13.getAverage()); // average size = 998.178
    System.out.println("summary13.getMin = "+summary13.getMin()); // min size = 500
    System.out.println("summary13.getMax = "+summary13.getMax()); // max size = 1000
    System.out.println();
    System.out.println("nullStats4.size = "+nullStats4.size()); // nullStats4.size = 0
    IntSummaryStatistics summary14 = stackStats4.stream().collect(Collectors.summarizingInt(x -> x));
    System.out.println("summary14.getAverage = "+summary14.getAverage()); // average size = 1000.00
    System.out.println("summary14.getMin = "+summary14.getMin()); // min size = 1000
    System.out.println("summary14.getMin = "+summary14.getMin()); // max size = 1000

  }

}
//    output from a complete run:
//    demo of basic stack behavior
//    size=3
//    3
//    size=2
//    2
//    size=0
//    0
//    
//    demo of push contention
//    summary1.getAverage = 997.109
//    summary1.getMin = 532
//    summary1.getMax = 1000
//    
//    demo showing that push contention errors are eliminated with ConcurrentStack
//    summary2.getAverage = 1000.0
//    summary2.getMin = 1000
//    summary2.getMax = 1000
//    
//    demo of pop contention resulting in duplicated node retrievals and possibly lost nodes
//    summary3.getAverage = 1000.0
//    summary3.getMin = 1000
//    summary3.getMax = 1000
//    
//    summary4.getAverage = 999.875
//    summary4.getMin = 884
//    summary4.getMin = 884
//    
//    nullStats.size = 0
//    summary5.getAverage = 7.344
//    summary5.getMin = 0
//    summary5.getMax = 250
//    
//    demo of resolution of pure pop contention issues with ConcurrentStack
//    summary6.getAverage = 1000.0
//    summary6.getMin = 1000
//    summary6.getMax = 1000
//    
//    summary7.getAverage = 1000.0
//    summary7.getMin = 1000
//    summary7.getMax = 1000
//    
//    nullStats2.size = 0
//    summary8.getAverage = 0.0
//    summary8.getMin = 0
//    summary8.getMax = 0
//    
//    demo of concurrent push and pop with Stack
//    summary9.getAverage = 500.0
//    summary9.getMin = 500
//    summary9.getMax = 500
//    
//    summary10.getAverage = 987.0
//    summary10.getMin = 500
//    summary10.getMax = 1000
//    
//    nullStats3.size = 0
//    summary11.getAverage = 1002.58
//    summary11.getMin = 750
//    summary11.getMax = 1250
//    
//    demo of concurrent push and pop with ConcurrentStack
//    summary12.getAverage = 500.0
//    summary12.getMin = 500
//    summary12.getMax = 500
//    
//    summary13.getAverage = 986.0
//    summary13.getMin = 500
//    summary13.getMax = 1000
//    
//    nullStats4.size = 0
//    summary14.getAverage = 1000.0
//    summary14.getMin = 1000
//    summary14.getMin = 1000
