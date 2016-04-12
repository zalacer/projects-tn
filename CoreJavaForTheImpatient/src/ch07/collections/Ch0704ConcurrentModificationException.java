package ch07.collections;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//4. Produce a situation that yields a ConcurrentModificationException.
//What can you do to avoid it?

// Use synchronization as with Collections.synchronizedList or a concurrent 
// data structure but not its iterator if possible.

public class Ch0704ConcurrentModificationException {

  public static <T> void removeElements(List<T> a) {
    Iterator<T> it = a.iterator();
    while (it.hasNext()) {
      a.remove(it.next());
    }
  }

  public static <T> void concurrentRemoveElements(ConcurrentLinkedDeque<T> a) {
    while (true) {
      T e = a.poll();
      if (e == null) {
        break;
      } else System.out.println(Thread.currentThread().getName()+": "+e);
    }
  }

  public static void main(String[] args) {

    // A way to produce ConcurrentModificationException is to remove elements
    // concurrently from a non-thread-safe, non-synchronized data structure:
      List<Integer> a = IntStream.range(0,10000).boxed().collect(Collectors.toList());
      Runnable r1 = () -> removeElements(a);
      Runnable r2 = () -> removeElements(a);
      new Thread(r1).start();
      new Thread(r2).start();  
    //  Almost immediate response was:
    //  Exception in thread "Thread-0" java.util.ConcurrentModificationException
    //  ...
    //  Exception in thread "Thread-1" java.util.ConcurrentModificationException
    //  ...

    // To avoid ConcurrentModificationException use a synchronization locking 
    // mechanism or a concurrent data structure:
    ConcurrentLinkedDeque<Integer> cld = new ConcurrentLinkedDeque<>(
        IntStream.range(0,100).boxed().collect(Collectors.toList()));
    System.out.println("\ncld.size = "+cld.size()+"\n"); 

    Runnable r3 = () -> concurrentRemoveElements(cld);
    Thread t1 = new Thread(r3);
    Thread t2 = new Thread(r3);
    
    t1.start();
    t2.start();
    
    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    System.out.println("\ncld.size = "+cld.size()); 
    
    //  cld.size = 100
    //
    //  Thread-0: 0
    //  Thread-0: 1
    //  Thread-0: 3
    //  Thread-1: 2
    //  Thread-0: 4
    //  Thread-1: 5
    //  Thread-0: 6
    //  Thread-1: 7
    //  Thread-0: 8
    //  Thread-1: 9
    //  Thread-0: 10
    //  Thread-1: 11
    //  Thread-0: 12
    //  Thread-1: 13
    //  Thread-0: 14
    //  Thread-1: 15
    //  Thread-0: 16
    //  Thread-1: 17
    //  Thread-0: 18
    //  Thread-1: 19
    //  Thread-0: 20
    //  Thread-1: 21
    //  Thread-0: 22
    //  Thread-1: 23
    //  Thread-0: 24
    //  Thread-1: 25
    //  Thread-0: 26
    //  Thread-1: 27
    //  Thread-0: 28
    //  Thread-1: 29
    //  Thread-0: 30
    //  Thread-1: 31
    //  Thread-0: 32
    //  Thread-1: 33
    //  Thread-0: 34
    //  Thread-1: 35
    //  Thread-0: 36
    //  Thread-1: 37
    //  Thread-0: 38
    //  Thread-1: 39
    //  Thread-1: 41
    //  Thread-1: 42
    //  Thread-0: 40
    //  Thread-1: 43
    //  Thread-0: 44
    //  Thread-1: 45
    //  Thread-0: 46
    //  Thread-1: 47
    //  Thread-0: 48
    //  Thread-1: 49
    //  Thread-0: 50
    //  Thread-1: 51
    //  Thread-0: 52
    //  Thread-1: 53
    //  Thread-0: 54
    //  Thread-1: 55
    //  Thread-0: 56
    //  Thread-1: 57
    //  Thread-1: 59
    //  Thread-0: 58
    //  Thread-1: 60
    //  Thread-0: 61
    //  Thread-1: 62
    //  Thread-0: 63
    //  Thread-1: 64
    //  Thread-0: 65
    //  Thread-1: 66
    //  Thread-1: 68
    //  Thread-0: 67
    //  Thread-1: 69
    //  Thread-0: 70
    //  Thread-1: 71
    //  Thread-0: 72
    //  Thread-1: 73
    //  Thread-0: 74
    //  Thread-1: 75
    //  Thread-0: 76
    //  Thread-1: 77
    //  Thread-0: 78
    //  Thread-1: 79
    //  Thread-0: 80
    //  Thread-1: 81
    //  Thread-0: 82
    //  Thread-1: 83
    //  Thread-0: 84
    //  Thread-1: 85
    //  Thread-0: 86
    //  Thread-1: 87
    //  Thread-0: 88
    //  Thread-1: 89
    //  Thread-0: 90
    //  Thread-1: 91
    //  Thread-0: 92
    //  Thread-1: 93
    //  Thread-0: 94
    //  Thread-1: 95
    //  Thread-0: 96
    //  Thread-1: 97
    //  Thread-0: 98
    //  Thread-1: 99
    //
    //  cld.size = 0

  }

}
