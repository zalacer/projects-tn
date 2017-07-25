package ex23;

import static sort.Quicks.quick3Way1stPartitionTrace;
import static sort.Quicks.quick3WayTrace;

public class Ex2312EntropyOptimal3WayPartitioningQuickSortTrace {

  /* p304  
  2.3.12  Show, in the style of the trace given with the code, how the entropy-optimal 
  sort first partitions the array  B A B A B A B A C A D A B R A 
  
  I guess this means like the trace on p299, but that's poorly faked because it shows
  i > gt on two lines and R and W at indices 7 and 8 on the next to last line aren't
  exchanged on the last line. It should have ended on third from last line. But why 
  not show the entire trace of the sort to see how it continues way after the array 
  is already sorted? Shown below is what I got for the first partitioning of 
  BABABABACADABRA as well as it full trace and that for R B W W R W B R R W B R using a 
  method that could as well been provided.
  */ 
  
  public static void main(String[] args) {
    
    String[] s = "BABABABACADABRA".split("");
    quick3Way1stPartitionTrace(s); 
/*
    lt   i  gt   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  
     0   0  14   B   A   B   A   B   A   B   A   C   A   D   A   B   R   A   
     0   1  14   B  [A]  B   A   B   A   B   A   C   A   D   A   B   R   A   
     1   2  14   A   B  [B]  A   B   A   B   A   C   A   D   A   B   R   A   
     1   3  14   A   B   B  [A]  B   A   B   A   C   A   D   A   B   R   A   
     2   4  14   A   A   B   B  [B]  A   B   A   C   A   D   A   B   R   A   
     2   5  14   A   A   B   B   B  [A]  B   A   C   A   D   A   B   R   A   
     3   6  14   A   A   A   B   B   B  [B]  A   C   A   D   A   B   R   A   
     3   7  14   A   A   A   B   B   B   B  [A]  C   A   D   A   B   R   A   
     4   8  14   A   A   A   A   B   B   B   B  [C]  A   D   A   B   R   A   
     4   8  13   A   A   A   A   B   B   B   B  [A]  A   D   A   B   R   C   
     5   9  13   A   A   A   A   A   B   B   B   B  [A]  D   A   B   R   C   
     6  10  13   A   A   A   A   A   A   B   B   B   B  [D]  A   B   R   C   
     6  10  12   A   A   A   A   A   A   B   B   B   B  [R]  A   B   D   C   
     6  10  11   A   A   A   A   A   A   B   B   B   B  [B]  A   R   D   C   
     6  11  11   A   A   A   A   A   A   B   B   B   B   B  [A]  R   D   C   
*/    
    
    // full trace of the sort
    s = "BABABABACADABRA".split("");
    quick3WayTrace(s);
/*
    lt   i  gt   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  
     0   0  14   B   A   B   A   B   A   B   A   C   A   D   A   B   R   A   
     0   1  14   B  [A]  B   A   B   A   B   A   C   A   D   A   B   R   A   
     1   2  14   A   B  [B]  A   B   A   B   A   C   A   D   A   B   R   A   
     1   3  14   A   B   B  [A]  B   A   B   A   C   A   D   A   B   R   A   
     2   4  14   A   A   B   B  [B]  A   B   A   C   A   D   A   B   R   A   
     2   5  14   A   A   B   B   B  [A]  B   A   C   A   D   A   B   R   A   
     3   6  14   A   A   A   B   B   B  [B]  A   C   A   D   A   B   R   A   
     3   7  14   A   A   A   B   B   B   B  [A]  C   A   D   A   B   R   A   
     4   8  14   A   A   A   A   B   B   B   B  [C]  A   D   A   B   R   A   
     4   8  13   A   A   A   A   B   B   B   B  [A]  A   D   A   B   R   C   
     5   9  13   A   A   A   A   A   B   B   B   B  [A]  D   A   B   R   C   
     6  10  13   A   A   A   A   A   A   B   B   B   B  [D]  A   B   R   C   
     6  10  12   A   A   A   A   A   A   B   B   B   B  [R]  A   B   D   C   
     6  10  11   A   A   A   A   A   A   B   B   B   B  [B]  A   R   D   C   
     6  11  11   A   A   A   A   A   A   B   B   B   B   B  [A]  R   D   C   
     0   0   6   A   A   A   A   A   A   A   B   B   B   B   B   R   D   C  1st subarray 
     0   1   6   A  [A]  A   A   A   A   A   B   B   B   B   B   R   D   C   
     0   2   6   A   A  [A]  A   A   A   A   B   B   B   B   B   R   D   C   
     0   3   6   A   A   A  [A]  A   A   A   B   B   B   B   B   R   D   C   
     0   4   6   A   A   A   A  [A]  A   A   B   B   B   B   B   R   D   C   
     0   5   6   A   A   A   A   A  [A]  A   B   B   B   B   B   R   D   C   
     0   6   6   A   A   A   A   A   A  [A]  B   B   B   B   B   R   D   C   
    12  12  14   A   A   A   A   A   A   A   B   B   B   B   B   R   D   C  2nd subarray
    12  13  14   A   A   A   A   A   A   A   B   B   B   B   B   R  [D]  C   
    13  14  14   A   A   A   A   A   A   A   B   B   B   B   B   D   R  [C]  
    12  12  13   A   A   A   A   A   A   A   B   B   B   B   B   D   C   R   
    12  13  13   A   A   A   A   A   A   A   B   B   B   B   B   D  [C]  R   
                 A   A   A   A   A   A   A   B   B   B   B   B   C   D   R  finally sorted
*/ 
    // full trace of the sort of "R B W W R W B R R W B R"
    s = "R B W W R W B R R W B R".split("\\s+");
    quick3WayTrace(s);   
/*                    
    lt   i  gt   0   1   2   3   4   5   6   7   8   9  10  11  
     0   0  11   R   B   W   W   R   W   B   R   R   W   B   R   
     0   1  11   R  [B]  W   W   R   W   B   R   R   W   B   R   
     1   2  11   B   R  [W]  W   R   W   B   R   R   W   B   R   
     1   2  10   B   R  [R]  W   R   W   B   R   R   W   B   W   
     1   3  10   B   R   R  [W]  R   W   B   R   R   W   B   W   
     1   3   9   B   R   R  [B]  R   W   B   R   R   W   W   W   
     2   4   9   B   B   R   R  [R]  W   B   R   R   W   W   W   
     2   5   9   B   B   R   R   R  [W]  B   R   R   W   W   W   
     2   5   8   B   B   R   R   R  [W]  B   R   R   W   W   W   
     2   5   7   B   B   R   R   R  [R]  B   R   W   W   W   W   
     2   6   7   B   B   R   R   R   R  [B]  R   W   W   W   W   
     3   7   7   B   B   B   R   R   R   R  [R]  W   W   W   W   already sorted
     0   0   2   B   B   B   R   R   R   R   R   W   W   W   W   
     0   1   2   B  [B]  B   R   R   R   R   R   W   W   W   W   
     0   2   2   B   B  [B]  R   R   R   R   R   W   W   W   W   
     8   8  11   B   B   B   R   R   R   R   R   W   W   W   W   
     8   9  11   B   B   B   R   R   R   R   R   W  [W]  W   W   
     8  10  11   B   B   B   R   R   R   R   R   W   W  [W]  W   
     8  11  11   B   B   B   R   R   R   R   R   W   W   W  [W]  
                 B   B   B   R   R   R   R   R   W   W   W   W   

*/
  }

}

