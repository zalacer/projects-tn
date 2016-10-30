package ex25;

import static v.ArrayUtils.ofDim;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import v.Tuple2;

/* p353
  2.5.9  Develop a data type that allows you to write a client 
  that can sort a file such as the one shown at below.
  
  input (DJI volumes for each day)
  1-Oct-28 3500000
  2-Oct-28 3850000
  3-Oct-28 4060000
  4-Oct-28 4330000
  5-Oct-28 4360000
  ...
  30-Dec-99 554680000
  31-Dec-99 374049984
  3-Jan-00 931800000
  4-Jan-00 1009000000
  5-Jan-00 1085500032
  ...
  output
  19-Aug-40 130000
  26-Aug-40 160000
  24-Jul-40 200000
  10-Aug-42 210000
  23-Jun-42 210000
  ...
  23-Jul-02 2441019904
  17-Jul-02 2566500096
  15-Jul-02 2574799872
  19-Jul-02 2654099968
  24-Jul-02 2775559936
  
  This data is a date-time mess because full years aren't given 
  and time zones are missing. Please provide better data or 
  clarification.
  
  As it is it could be read and sorted as Tuple2(LocalDate,Long). 
  Example below.
 
 */

public class Ex2509SortableDataType {
  
  public static Comparator<Tuple2<LocalDate,Long>> tcomp = (t1,t2) -> {
    int c2 = t1._1.compareTo(t2._1);
    int c1 = t2._2.compareTo(t2._2);
    return c2 == 0 ? c1 : c2;   
  };
  
  public static void sortData() {
    List<Tuple2<LocalDate,Long>> data = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      String[] sa = sc.nextLine().split("\\s+");
      String dstring = sa[0];
      LocalDate ld = LocalDate.parse(sa[0],DateTimeFormatter.ofPattern("d-MMM-yy"));
      if (!dstring.split("-")[2].startsWith("0")) ld = ld.minusYears(100);
      data.add(new Tuple2<LocalDate,Long>(ld, Long.parseLong(sa[1])));     
    }
    sc.close();
    Tuple2<LocalDate,Long>[] z = data.toArray(ofDim(Tuple2.class,0));
    Arrays.sort(z,tcomp);
    for (Tuple2<LocalDate,Long> t2 : z) System.out.println(t2);
  }
  
  public static void main(String[] args) {
    
    sortData();
    /*    
    (1928-10-01,3500000)
    (1928-10-02,3850000)
    (1928-10-03,4060000)
    (1928-10-04,4330000)
    (1928-10-05,4360000)
    (1999-12-30,554680000)
    (1999-12-31,374049984)
    (2000-01-03,931800000)
    (2000-01-04,1009000000)
    (2000-01-05,1085500032)    
    */    

  }

}

/* test data
5-Oct-28 4360000
4-Oct-28 4330000
3-Oct-28 4060000
2-Oct-28 3850000
1-Oct-28 3500000
30-Dec-99 554680000
31-Dec-99 374049984
3-Jan-00 931800000
4-Jan-00 1009000000
5-Jan-00 1085500032

*/

