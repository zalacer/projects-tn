package ex21;

/* p264
  2.1.4  Show, in the style of the example trace with Algorithm 2.2, how 
  insertion sort sorts the array  E A S Y Q U E S T I O N 
  
  This is what I got after munging the output from the code I wrote printed to 
  conform the style in the text, but without colorization or other enhancements, 
  
  (note in this "j" is  j-1 in the code)
  
  i   j    0  1  2  3  4  5  6  7  8  9  10 11
  --  ---  -- -- -- -- -- -- -- -- -- -- -- --
  initial  E  A  S  Y  Q  U  E  S  T  I  O  N
  1   0    A  E  S  Y  Q  U  E  S  T  I  O  N  // if j==0, then j-1==-1 ???
  2   2    A  E  S  Y  Q  U  E  S  T  I  O  N  
  3   3    A  E  S  Y  Q  U  E  S  T  I  O  N
  4   2    A  E  Q  S  Y  U  E  S  T  I  O  N 
  5   4    A  E  Q  S  U  Y  E  S  T  I  O  N 
  6   2    A  E  E  Q  S  U  Y  S  T  I  O  N 
  7   5    A  E  E  Q  S  S  U  Y  T  I  O  N 
  8   6    A  E  E  Q  S  S  T  U  Y  I  O  N 
  9   3    A  E  E  I  Q  S  S  T  U  Y  O  N 
  10  4    A  E  E  I  O  Q  S  S  T  U  Y  N 
  11  4    A  E  E  I  N  O  Q  S  S  T  U  Y 
           A  E  E  I  N  O  Q  S  S  T  U  Y

How did I get this?:

 I couldn't tell what was done to get the example trace on p251. Trying to reproduce it
 by coding printout of i, j and the array contents after every exch() execution, here's 
 what I got for S O R T E X A M P L E input:

  i   j    0  1  2  3  4  5  6  7  8  9  10
  --  --   -- -- -- -- -- -- -- -- -- -- --
  1   1    O  S  R  T  E  X  A  M  P  L  E 
  2   2    O  R  S  T  E  X  A  M  P  L  E
  3   3    O  R  S  T  E  X  A  M  P  L  E
  4   4    O  R  S  E  T  X  A  M  P  L  E 
  4   3    O  R  E  S  T  X  A  M  P  L  E 
  4   2    O  E  R  S  T  X  A  M  P  L  E 
  4   1    E  O  R  S  T  X  A  M  P  L  E
  5   5    E  O  R  S  T  X  A  M  P  L  E
  6   6    E  O  R  S  T  A  X  M  P  L  E 
  6   5    E  O  R  S  A  T  X  M  P  L  E 
  6   4    E  O  R  A  S  T  X  M  P  L  E 
  6   3    E  O  A  R  S  T  X  M  P  L  E 
  6   2    E  A  O  R  S  T  X  M  P  L  E 
  6   1    A  E  O  R  S  T  X  M  P  L  E 
  7   7    A  E  O  R  S  T  M  X  P  L  E 
  7   6    A  E  O  R  S  M  T  X  P  L  E 
  7   5    A  E  O  R  M  S  T  X  P  L  E 
  7   4    A  E  O  M  R  S  T  X  P  L  E 
  7   3    A  E  M  O  R  S  T  X  P  L  E 
  8   8    A  E  M  O  R  S  T  P  X  L  E 
  8   7    A  E  M  O  R  S  P  T  X  L  E 
  8   6    A  E  M  O  R  P  S  T  X  L  E 
  8   5    A  E  M  O  P  R  S  T  X  L  E 
  9   9    A  E  M  O  P  R  S  T  L  X  E 
  9   8    A  E  M  O  P  R  S  L  T  X  E 
  9   7    A  E  M  O  P  R  L  S  T  X  E 
  9   6    A  E  M  O  P  L  R  S  T  X  E 
  9   5    A  E  M  O  L  P  R  S  T  X  E 
  9   4    A  E  M  L  O  P  R  S  T  X  E 
  9   3    A  E  L  M  O  P  R  S  T  X  E 
  10  10   A  E  L  M  O  P  R  S  T  E  X 
  10  9    A  E  L  M  O  P  R  S  E  T  X 
  10  8    A  E  L  M  O  P  R  E  S  T  X 
  10  7    A  E  L  M  O  P  E  R  S  T  X 
  10  6    A  E  L  M  O  E  P  R  S  T  X 
  10  5    A  E  L  M  E  O  P  R  S  T  X 
  10  4    A  E  L  E  M  O  P  R  S  T  X 
  10  3    A  E  E  L  M  O  P  R  S  T  X 
           A  E  E  L  M  O  P  R  S  T  X 

  First notice that I got no output for i equals 3 and 5, ok so I faked data for those
  according to the style in the text. Then for i = 1 I got j = 1 and for i = 2 I got j = 2, 
  but the text has 0 and 1, so it's actually using j-1. But for i = 4, I got j = 4,3,2,1 and
  the text has 0.  So I'm supposed to take the lowest j for a given i and subtract one from 
  it and only make an entry for the lowest such j for every i that has multiple j's for which
  exch() was executed. If so, that would work for i = 6-10. Here's to what it boils down:
  
  i   j-1  0  1  2  3  4  5  6  7  8  9  10
  --  ---  -- -- -- -- -- -- -- -- -- -- --
  1   0    O  S  R  T  E  X  A  M  P  L  E 
  2   1    O  R  S  T  E  X  A  M  P  L  E
  3   3    O  R  S [T] E  X  A  M  P  L  E
  4   0    O  R  S  E  T  X  A  M  P  L  E 
  5   5    E  O  R  S  T [X] A  M  P  L  E
  6   O    A  E  O  R  S  T  X  M  P  L  E 
  7   2    A  E  M  O  R  S  T  X  P  L  E 
  8   4    A  E  M  O  P  R  S  T  X  L  E 
  9   3    A  E  L  M  O  P  R  S  T  X  E 
  10  2    A  E  E  L  M  O  P  R  S  T  X 
           A  E  E  L  M  O  P  R  S  T  X 
  
  After changing the code to print j-1 instead of j, for E A S Y Q U E S T I O N I got
  the following in which for each row a[j] has been swapped with a[j-1] from the 
  previous row:
  
  (The code to do this is in sort.InsertionWork where sortTestShowArray() was run in main.)
 
  i   j-1  0  1  2  3  4  5  6  7  8  9  10 11  swapped
  --  ---  -- -- -- -- -- -- -- -- -- -- -- --  -------
  initial  E  A  S  Y  Q  U  E  S  T  I  O  N   (NA)
  1   0    A  E  S  Y  Q  U  E  S  T  I  O  N   E, A
  4   3    A  E  S  Q  Y  U  E  S  T  I  O  N   Y, Q
  4   2    A  E  Q  S  Y  U  E  S  T  I  O  N   S, Q
  5   4    A  E  Q  S  U  Y  E  S  T  I  O  N   Y, U
  6   5    A  E  Q  S  U  E  Y  S  T  I  O  N   Y, E
  6   4    A  E  Q  S  E  U  Y  S  T  I  O  N   U, E
  6   3    A  E  Q  E  S  U  Y  S  T  I  O  N   S, E
  6   2    A  E  E  Q  S  U  Y  S  T  I  O  N   Q, E
  7   6    A  E  E  Q  S  U  S  Y  T  I  O  N   Y, S
  7   5    A  E  E  Q  S  S  U  Y  T  I  O  N   U, S
  8   7    A  E  E  Q  S  S  U  T  Y  I  O  N   Y, T
  8   6    A  E  E  Q  S  S  T  U  Y  I  O  N   U, T
  9   8    A  E  E  Q  S  S  T  U  I  Y  O  N   Y, I
  9   7    A  E  E  Q  S  S  T  I  U  Y  O  N   U, I
  9   6    A  E  E  Q  S  S  I  T  U  Y  O  N   T, I
  9   5    A  E  E  Q  S  I  S  T  U  Y  O  N   S, I
  9   4    A  E  E  Q  I  S  S  T  U  Y  O  N   S, I
  9   3    A  E  E  I  Q  S  S  T  U  Y  O  N   Q, I
  10  9    A  E  E  I  Q  S  S  T  U  O  Y  N   Y, O
  10  8    A  E  E  I  Q  S  S  T  O  U  Y  N   U, O
  10  7    A  E  E  I  Q  S  S  O  T  U  Y  N   T, O
  10  6    A  E  E  I  Q  S  O  S  T  U  Y  N   S, O
  10  5    A  E  E  I  Q  O  S  S  T  U  Y  N   S, O
  10  4    A  E  E  I  O  Q  S  S  T  U  Y  N   Q, O
  11  10   A  E  E  I  O  Q  S  S  T  U  N  Y   Y, N
  11  9    A  E  E  I  O  Q  S  S  T  N  U  Y   U, N
  11  8    A  E  E  I  O  Q  S  S  N  T  U  Y   T, N
  11  7    A  E  E  I  O  Q  S  N  S  T  U  Y   S, N 
  11  6    A  E  E  I  O  Q  N  S  S  T  U  Y   S, N
  11  5    A  E  E  I  O  N  Q  S  S  T  U  Y   Q, N
  11  4    A  E  E  I  N  O  Q  S  S  T  U  Y   O, N
           A  E  E  I  N  O  Q  S  S  T  U  Y 
           
  which boils down to:
  
  (j below is j-1 in the code)

  i   j    0  1  2  3  4  5  6  7  8  9  10 11
  --  ---  -- -- -- -- -- -- -- -- -- -- -- --
  1   0    A  E  S  Y  Q  U  E  S  T  I  O  N  
  2   2    A  E  S  Y  Q  U  E  S  T  I  O  N  
  3   3    A  E  S  Y  Q  U  E  S  T  I  O  N
  4   2    A  E  Q  S  Y  U  E  S  T  I  O  N 
  5   4    A  E  Q  S  U  Y  E  S  T  I  O  N 
  6   2    A  E  E  Q  S  U  Y  S  T  I  O  N 
  7   5    A  E  E  Q  S  S  U  Y  T  I  O  N 
  8   6    A  E  E  Q  S  S  T  U  Y  I  O  N 
  9   3    A  E  E  I  Q  S  S  T  U  Y  O  N 
  10  4    A  E  E  I  O  Q  S  S  T  U  Y  N 
  11  4    A  E  E  I  N  O  Q  S  S  T  U  Y 
           A  E  E  I  N  O  Q  S  S  T  U  Y

*/

public class Ex2104InsertionSortTrace {

}
