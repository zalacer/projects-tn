package ex25;

import static v.ArrayUtils.ofDim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import v.Tuple2;

/* p356
  2.5.20 Idle time. Suppose that a parallel machine processes N jobs. 
  Write a program that, given the list of job start and finish times, 
  finds the largest interval where the machine is idle and the largest 
  interval where the machine is not idle.
    
 */

public class Ex2520ParallelProcessingLargestIdleAndBusyIntervals {
  
  public static Tuple2<Integer,Integer>[][] findMaxIntervals(Tuple2<Integer,Integer>[] z) {
    if (z == null || z.length == 0) return null;
    // define Comparator depending only on first component of tuples
    Comparator<Tuple2<Integer,Integer>> comp = (t1,t2) -> { return t1._1.compareTo(t2._1); };
    // fix data in z if necessary
    for (int i = 0; i < z.length; i++) {
      if (z[i] != null && z[i]._1 != null && z[i]._2 != null)
        if (z[i]._2 < z[i]._1) { int e = z[i]._1; z[i]._1 = z[i]._2; z[i]._2 = e; } 
    }
    Arrays.sort(z,comp); // sort by start times
    int start, end, pend = 0; boolean fdone = false;
    List<Tuple2<Integer,Integer>> idle = new ArrayList<>();
    List<Tuple2<Integer,Integer>> busy = new ArrayList<>();
    for (int i = 0; i < z.length; i++) {
      if (z[i] == null || z[i]._1 == null || z[i]._2 == null) continue;
      start = z[i]._1; end = z[i]._2;
      if (!fdone) {       
        busy.add(new Tuple2<Integer,Integer>(start, end));
        fdone = true; 
      } else {
        if (start > pend) { 
          // create new idle entry
          idle.add(new Tuple2<Integer,Integer>(pend, start));
          // create new busy entry
          busy.add(new Tuple2<Integer,Integer>(start, end));
        } else if (end > pend) {
          // extend previous busy entry
          busy.get(busy.size()-1)._2 = end;
        } else end = pend;
      }
      pend = end;
    }
    // redefine comp to depend on interval lengths
    comp = (t1,t2) -> { return (t1._2-t1._1) - (t2._2-t2._1); };
    // get max busy interval
    Tuple2<Integer,Integer>[] bsy = busy.toArray(ofDim(Tuple2.class,0));
    Arrays.sort(bsy,comp); Tuple2<Integer,Integer> maxbsy = bsy[bsy.length-1];
    // get max idle interval
    Tuple2<Integer,Integer>[] idl = idle.toArray(ofDim(Tuple2.class,0));
    Arrays.sort(idl,comp); Tuple2<Integer,Integer> maxidl = idl[idl.length-1]; 
    // gather output, print and return it
    Tuple2<Integer,Integer>[][] r = ofDim(Tuple2.class,2,1);
    r[0] = ofDim(Tuple2.class,1); r[0][0] = maxbsy;
    r[1] = ofDim(Tuple2.class,1); r[1][0] = maxidl;
    System.out.println("max busy = "+maxbsy+"  max idle = "+maxidl);
    return r;
  }
  
  public static void main(String[] args) {
    
    String[] data = "1 3; 4 7; 5 8; 6 7; 9 9; 11 15; 15 18".split(";\\s+");
    Tuple2<Integer,Integer>[] t = ofDim(Tuple2.class,data.length);
    for (int i = 0; i < data.length; i++) {
      String[] td = data[i].split("\\s+");
      t[i] = new Tuple2<Integer,Integer>(Integer.parseInt(td[0]),Integer.parseInt(td[1]));
    }
    findMaxIntervals(t); // max busy = (11,18)  max idle = (9,11)
   
  }

}


