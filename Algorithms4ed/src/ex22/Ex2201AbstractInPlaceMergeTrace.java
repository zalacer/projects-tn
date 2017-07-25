package ex22;

public class Ex2201AbstractInPlaceMergeTrace {
  
/* p284
  2.2.1  Give a trace, in the style of the trace given at the beginning of this section, 
  showing how the keys  A E Q S U Y E I N O S T are merged with the abstract in-place
  merge() method.
  
                         a[]                                        aux[]   
      k  0  1  2  3  4  5   6  7  8  9 10 11   i  j   0  1  2  3  4  5   6  7  8  9 10 11
         -----------------------------------          -  -  -  -  -  -   -  -  -  -  -  -
  input  A  E  Q  S  U  Y | E  I  N  O  S  T          A  E  Q  S  U  Y | E  I  N  O  S  T
  copy   A  E  Q  S  U  Y | E  I  N  O  S  T
                                               0  6
      0  A                                     1  6  {A} E  Q  S  U  Y | E  I  N  O  S  T
      1  A  E                                  2  6     {E} Q  S  U  Y | E  I  N  O  S  T
      2  A  E  E                               2  7         Q  S  U  Y |{E} I  N  O  S  T
      3  A  E  E  I                            2  8         Q  S  U  Y |   {I} N  O  S  T
      4  A  E  E  I  N                         2  9         Q  S  U  Y |      {N} O  S  T
      5  A  E  E  I  N  O                      2 10         Q  S  U  Y |         {O} S  T
      6  A  E  E  I  N  O  Q                   3 10        {Q} S  U  Y |             S  T
      7  A  E  E  I  N  O  Q  S                4 10           {S} U  Y |             S  T
      8  A  E  E  I  N  O  Q  S  S             4 11               U  Y |            {S} T
      9  A  E  E  I  N  O  Q  S  S  T          4 12               U  Y |               {T}
     10  A  E  E  I  N  O  Q  S  S  T  U       5 12              {U} Y |               
     11  A  E  E  I  N  O  Q  S  S  T  U  Y    6 12                 {Y}|
         A  E  E  I  N  O  Q  S  S  T  U  Y
  
  
*/
  
  public static void main(String[] args) {

  }

}
