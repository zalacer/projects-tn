package ex25;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;

import pq.MinPQ;
import v.Tuple2;

/* p355
  2.5.13 Load balancing. Write a program LPT.java that takes an integer M as 
  a commandline argument, reads job names and processing times from standard 
  input and prints a schedule assigning the jobs to M processors that approximately 
  minimizes the time when the last job completes using the longest processing time 
  first rule, as described on page 349.
 */

public class Ex2513LoadBalancingLongestProcessingTimeFirst {

  public static Comparator<Entry<String,Long>> entryComp = (e1, e2) -> {
    int c1 = e2.getValue().compareTo(e1.getValue());
    int c2 = e1.getKey().compareTo(e2.getKey());
    return c1 == 0 ? c2 : c1;
  };

  public static Comparator<Tuple2<Entry<String,Long>,Integer>> tcomp = (t1, t2) -> {
    int c1 = t1._1.getValue().compareTo(t2._1.getValue());
    int c2 = t2._1.getKey().compareTo(t1._1.getKey());
    return c1 == 0 ? c2 : c1;
  };

  public static void lpt(int M, boolean...all) {
    // M is the number of processors.
    // if all is null, empty or all[0] == false, prints only scheduled assignments;
    // else if all[0] == true, prints all processing events.
    
    if (M < 1) throw new IllegalArgumentException("no processers no jobs");
 
    // map the job names to their times and put in an array reverse sorted by times
    Map<String,Long> map = new HashMap<>();
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String[] data = sc.nextLine().split("\\s+");
      map.put(data[0], Long.parseLong(data[1]));
    }
    sc.close();
    if (map.size() == 0) { System.out.println("no jobs input"); return; }
    Stream <Entry<String,Long>> st = map.entrySet().stream();
    Object[] jobs = st.sorted(entryComp).toArray();
    int jlen = jobs.length;
    if (jlen == 0) { System.out.println("job map not empty but array is"); return; }

    // handle case of as many or more processors than jobs
    if (M >= jobs.length) {
      for (int i = 0; i < jobs.length; i++) {
        @SuppressWarnings("unchecked")
        Entry<String,Long> e = (Entry<String,Long>) jobs[i];
        System.out.println(e.getKey()+" runtime "+e.getValue()+" assigned to processor "
            +i+" at time 0"); 
      }
      // all jobs have been submitted; report their finish times.
      if (all != null && all.length > 0 && all[0] == true)
        for (int i = 0; i < jobs.length; i++) {
          @SuppressWarnings("unchecked")
          Entry<String,Long> e = (Entry<String,Long>) jobs[i];
          System.out.println(e.getKey()+" done at time "+e.getValue());
        }
      return;
    }

    long[] ptimes = new long[jobs.length]; // tracks cumulative time for jobs
 
    // handle case of only 1 processor
    if (M == 1) {
      for (int i = 0; i < jobs.length; i++) {
        @SuppressWarnings("unchecked")
        Entry<String, Long> e = (Entry<String,Long>) jobs[i];
        long t = i == 0 ? 0 : ptimes[i-1];
        System.out.println(e.getKey()+" runtime "+e.getValue()+" assigned to processor "
            +i+" at time "+t);
        if (i == 0)  ptimes[i] = e.getValue();
        else ptimes[i] = ptimes[i-1] + e.getValue();
      }
      // all jobs have been submitted; report their finish times
      if (all != null && all.length > 0 && all[0] == true)
        for (int i = 0; i < jobs.length; i++) {
          @SuppressWarnings("unchecked")
          Entry<String, Long> e = (Entry<String,Long>) jobs[i];
          System.out.println(e.getKey()+" done at time "+ptimes[i]);
        }
      return;
    }
    
    // handle all other cases, a PQ is useful for this

    MinPQ<Tuple2<Entry<String,Long>,Integer>> pq = new MinPQ<>(tcomp);
    int j = 0; // tracks the number of jobs submitted
    int p; // p is a processor int id
    ptimes = new long[M]; // tracks cumulative time by processor id

    // get the ball rolling by initially adding as many jobs as processors
    for (int i = 0; i < M; i++) {    
      @SuppressWarnings("unchecked")
      Entry<String,Long> e = (Entry<String,Long>) jobs[i];
      ptimes[i] = e.getValue();
      pq.insert(new Tuple2<Entry<String,Long>,Integer>(e,i)); j++;
      System.out.println(e.getKey()+" runtime "+e.getValue()+" assigned to processor "
          +(i)+" at time 0"); 
    }

    // continually remove the first job done and add the next one with the longest runtime
    while (j < jlen) {
      Tuple2<Entry<String,Long>,Integer> k = pq.delMin();
      p = k._2; // the id of the processor of the job just removed and to be recycled
      if (all != null && all.length > 0 && all[0] == true)
        System.out.println(k._1.getKey()+" done at time "+k._1.getValue());
      @SuppressWarnings("unchecked")
      Entry<String,Long> e = (Entry<String,Long>) jobs[j++];
      System.out.println(e.getKey()+" runtime "+e.getValue()+" assigned to processor "
          +(p)+" at time "+ptimes[p]);
      ptimes[p] = ptimes[p]+e.getValue();
      e.setValue(ptimes[p]); 
      pq.insert(new Tuple2<Entry<String,Long>,Integer>(e,p));
    }

    // all jobs have been submitted; remove them and report their finish times
    if (all != null && all.length > 0 && all[0] == true)
      while (!pq.isEmpty()) {
        Tuple2<Entry<String,Long>,Integer> k = pq.delMin();
        p = k._2; 
        System.out.println(k._1.getKey()+" done at time "+ptimes[p]);
      }
  }

  public static void main(String[] args) {

//    lpt(4); // print only job assignments
              // this must be run separately from lpt(4, true) below
    /*
    job9 runtime 9 assigned to processor 0 at time 0
    job8 runtime 8 assigned to processor 1 at time 0
    job7 runtime 7 assigned to processor 2 at time 0
    job6 runtime 6 assigned to processor 3 at time 0
    job5 runtime 5 assigned to processor 3 at time 6
    job4 runtime 4 assigned to processor 2 at time 7
    job3 runtime 3 assigned to processor 1 at time 8
    job2 runtime 2 assigned to processor 0 at time 9
    job1 runtime 1 assigned to processor 3 at time 11
     */
    
    lpt(4, true); // print all processing events
    /*
    job9 runtime 9 assigned to processor 0 at time 0
    job8 runtime 8 assigned to processor 1 at time 0
    job7 runtime 7 assigned to processor 2 at time 0
    job6 runtime 6 assigned to processor 3 at time 0
    job6 done at time 6
    job5 runtime 5 assigned to processor 3 at time 6
    job7 done at time 7
    job4 runtime 4 assigned to processor 2 at time 7
    job8 done at time 8
    job3 runtime 3 assigned to processor 1 at time 8
    job9 done at time 9
    job2 runtime 2 assigned to processor 0 at time 9
    job5 done at time 11
    job1 runtime 1 assigned to processor 3 at time 11
    job4 done at time 11
    job3 done at time 11
    job2 done at time 11
    job1 done at time 12
     */

  }

}
/* test jobs to be entered in System.in
job1 1
job2 2
job3 3
job4 4
job5 5
job6 6
job7 7
job8 8
job9 9

 */

