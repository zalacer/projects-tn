package ex23;

public class Ex2301QuickSortPartitionTrace {

  /* p303  
  2.3.1  Show, in the style of the trace given with partition(), how that method 
  partitions the array E A S Y Q U E S T I O N .
  
      /* partition traces
       
                        without shuffle
                                                [a]
                         i   j   0   1   2   3   4   5   6   7   8   9  10  11  
  
         initial values  0  11  [E]  A   S   Y   Q   U   E   S   T   I   O   N   
  
  scan left, scan right  2   6   E   A  [S]  Y   Q   U  [E]  S   T   I   O   N   
  
               exchange  2   6   E   A  [E]  Y   Q   U  [S]  S   T   I   O   N  
               
  scan left, scan right  3   2   E   A   S<->Y   Q   U   E   S   T   I   O   N                 
  
         final exchange  0   2  [E]  A  [E]  Y   Q   U   S   S   T   I   O   N   
  
                 result          E   A   E   Y   Q   U   S   S   T   I   O   N   
  
  
                        
                        with shuffle
                                                    [a] 
                         i   j   0   1   2   3   4   5   6   7   8   9  10  11  
  
         initial values  0  11   E   A   S   Y   Q   U   E   S   T   I   O   N   
  
          after shuffle  0  11   O   E   T   U   Q   I   Y   A   E   S   N   S   
  
  scan left, scan right  2  10   O   E  [T]  U   Q   I   Y   A   E   S  [N]  S   
  
               exchange  2  10   O   E  [N]  U   Q   I   Y   A   E   S  [T]  S   
  
  scan left, scan right  3   8   O   E   N  [U]  Q   I   Y   A  [E]  S   T   S   
  
               exchange  3   8   O   E   N  [E]  Q   I   Y   A  [U]  S   T   S   
  
  scan left, scan right  4   7   O   E   N   E  [Q]  I   Y  [A]  U   S   T   S   
  
               exchange  4   7   O   E   N   E  [A]  I   Y  [Q]  U   S   T   S   
                                         
  scan left, scan right  3   2   O   E   N<->E   A   I   Y   Q   U   S   T   S   
  
         final exchange  0   5  [I]  E   N   E   A  [O]  Y   Q   U   S   T   S   
  
                 result          I   E   N   E   A   O   Y   Q   U   S   T   S   


  */ 

  public static void main(String[] args) {   

  }
                      
}

