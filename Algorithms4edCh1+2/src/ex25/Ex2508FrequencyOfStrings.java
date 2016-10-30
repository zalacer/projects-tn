package ex25;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Stream;

/* p353
  2.5.8  Write a program  Frequency that reads strings from standard 
  input and prints the number of times each string occurs, in descending 
  order of frequency.

 */

public class Ex2508FrequencyOfStrings {
  
  public static Comparator<Entry<String,Long>> entryComp = (e1, e2) -> {
    int c2 = e2.getValue().compareTo(e1.getValue());
    int c1 = e1.getKey().compareTo(e2.getKey());
    return c2 == 0 ? c1 : c2;
  };
  
  public static void frequency() {
    Map<String,Long> map = new HashMap<>();
    Scanner sc = new Scanner(System.in);
    while(sc.hasNext()) map.merge(sc.next(), 1L, Long::sum);
    sc.close();
    Stream <Entry<String,Long>> st = map.entrySet().stream();
    st.sorted(entryComp).forEachOrdered(e -> {
      System.out.printf("%5d %-8s\n", e.getValue(), e.getKey());
    }); 
  }
  
  public static void main(String[] args) {
    
    frequency();
  /*
    9 nine    
    8 eight   
    7 seven   
    6 six     
    5 five    
    4 four    
    3 three   
    2 two     
    1 one       
 */
  }

}

/* test words
one
two
two
three
three
three
four
four
four
four
five
five
five
five
five
six
six
six
six
six
six
seven
seven
seven
seven
seven
seven
seven
eight
eight
eight
eight
eight
eight
eight
eight
nine
nine
nine
nine
nine
nine
nine
nine
nine

*/

