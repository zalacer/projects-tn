package ex31;

import static v.ArrayUtils.*;
import st.SelfOrganizingUnorderedArraySTex3122;

/* p391
  3.1.22 Self-organizing search. A self-organizing search algorithm is
  one that rearranges items to make those that are accessed frequently 
  likely to be found early in the search. Modify your search implemen-
  tation for Exercise 3.1.2 to perform the following action on every 
  search hit: move the key-value pair found to the beginning of the list,
  moving all pairs between the beginning of the list and the vacated 
  position to the right one position. This procedure is called the move-
  to-front heuristic.
  
  This is implemented in st.SelfOrganizingUnorderedArraySTex3122 and 
  demonstrated below.
            
*/

public class Ex3122SelfOrganizingSearch {
  
  public static void main(String[] args) {
    
    String[] sa = "one two three four five".split("\\s+");
    Integer[] ia = rangeInteger(1,sa.length+1);
    
    SelfOrganizingUnorderedArraySTex3122<String,Integer> st = new SelfOrganizingUnorderedArraySTex3122<>();
    for (int i = 0; i < sa.length; i++) st.put(sa[i],ia[i]);
    System.out.println(st); //{one=1,two=2,three=3,four=4,five=5}
    System.out.println(st.get("three")); //3
    System.out.println(st); //{three=3,one=1,two=2,four=4,five=5}
  
  }
}
