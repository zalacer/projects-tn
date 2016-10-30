package ex25;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Stream;

/* p355
  2.5.12 Scheduling. Write a program SPT.java that reads job names and processing
  times from standard input and prints a schedule that minimizes average completion
  time using the shortest processing time first rule, as described on page 349.
 */

public class Ex2512SchedulingShortestProcessingTimeFirst {
  
  public static Comparator<Entry<String,Long>> entryComp = (e1, e2) -> {
    int c1 = e1.getValue().compareTo(e2.getValue());
    int c2 = e1.getKey().compareTo(e2.getKey());
    return c1 == 0 ? c2 : c1;
  };
   
  public static void spt() {
    Map<String,Long> map = new HashMap<>();
    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String[] data = sc.nextLine().split("\\s+");
       map.put(data[0], Long.parseLong(data[1]));
    }
    sc.close();
    Stream <Entry<String,Long>> st = map.entrySet().stream();
    st.sorted(entryComp).forEachOrdered(e -> {
      System.out.printf("%5s %-8d\n", e.getKey(), e.getValue());
    }); 
  }
 
  public static void main(String[] args) {

    spt();
    /*
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

  }

}
/* test jobs
job9 9
job8 8
job7 7
job6 6
job5 5
job4 4
job3 3
job2 2
job1 1

*/

