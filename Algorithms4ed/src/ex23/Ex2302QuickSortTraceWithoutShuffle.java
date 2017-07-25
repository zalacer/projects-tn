package ex23;

public class Ex2302QuickSortTraceWithoutShuffle {

  /* p303  
  2.3.2  Show, in the style of the quicksort trace given in this section, how 
  quicksort sorts the array  E A S Y Q U E S T I O N (for the purposes of this 
  exercise, ignore the initial shuffle).
  
                        without shuffle
                                                   [a]
                   lo   j  hi   0   1   2   3   4   5   6   7   8   9  10  11  
   initial values               E   A   S   Y   Q   U   E   S   T   I   O   N   
  
                    0   2  11   E   A  [E]  Y   Q   U   S   S   T   I   O   N   
                    0   1   1   A  [E]  E   Y   Q   U   S   S   T   I   O   N   
                    0       0  [A]  E   E   Y   Q   U   S   S   T   I   O   N   
                    3  11  11   A   E   E   N   Q   U   S   S   T   I   O  [Y]  
                    3   4  10   A   E   E   I  [N]  U   S   S   T   Q   O   Y   
                    3       3   A   E   E  [I]  N   U   S   S   T   Q   O   Y   
                    5  10  10   A   E   E   I   N   O   S   S   T   Q  [U]  Y   
                    5   5   9   A   E   E   I   N  [O]  S   S   T   Q   U   Y   
                    6   7   9   A   E   E   I   N   O   Q  [S]  T   S   U   Y   
                    6       6   A   E   E   I   N   O  [Q]  S   T   S   U   Y   
                    8   9   9   A   E   E   I   N   O   Q   S   S  [T]  U   Y   
                    8       8   A   E   E   I   N   O   Q   S  [S]  T   U   Y   
  
   result                       A   E   E   I   N   O   Q   S   S   T   U   Y  
   
 
                        with shuffle
                                                   [a]  
                   lo   j  hi   0   1   2   3   4   5   6   7   8   9  10  11
   initial values               E   A   S   Y   Q   U   E   S   T   I   O   N                  
   random shuffle               O   E   T   U   Q   I   Y   A   E   S   N   S   
                    0   5  11   I   E   N   E   A  [O]  Y   Q   U   S   T   S   
                    0   3   4   E   E   A  [I]  N   O   Y   Q   U   S   T   S   
                    0   1   2   A  [E]  E   I   N   O   Y   Q   U   S   T   S   
                    0       0  [A]  E   E   I   N   O   Y   Q   U   S   T   S   
                    2       2   A   E  [E]  I   N   O   Y   Q   U   S   T   S   
                    4       4   A   E   E   I  [N]  O   Y   Q   U   S   T   S   
                    6  11  11   A   E   E   I   N   O   S   Q   U   S   T  [Y]  
                    6   8  10   A   E   E   I   N   O   S   Q  [S]  U   T   Y   
                    6   7   7   A   E   E   I   N   O   Q  [S]  S   U   T   Y   
                    6       6   A   E   E   I   N   O  [Q]  S   S   U   T   Y   
                    9  10  10   A   E   E   I   N   O   Q   S   S   T  [U]  Y   
                    9       9   A   E   E   I   N   O   Q   S   S  [T]  U   Y   
  
   result                       A   E   E   I   N   O   Q   S   S   T   U   Y   


  */ 

  public static void main(String[] args) {   

  }
                      
}

