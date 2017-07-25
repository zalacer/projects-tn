package ex21;

/* p264
  2.1.1  Show, in the style of the example trace with Algorithm 2.1, how 
  selection sort sorts the array E A S Y Q U E S T I O N .
  
  in lieu of colorization:
  1. a[min] is in square braces
  2. entries in final position are in curly braces
  
  i  min  0  1  2  3  4  5  6  7  8  9  10 11  
          E  A  S  Y  Q  U  E  S  T  I  O  N
  0  1    E [A] S  Y  Q  U  E  S  T  I  O  N
  1  1   {A}[E] S  Y  Q  U  E  S  T  I  O  N  // A and 1st E exchanged
  2  6   {A  E} S  Y  Q  U [E] S  T  I  O  N  // 1st E exchanged with itself
  3  9   {A  E  E} Y  Q  U  S  S  T [I] O  N  // 2nd E and 1st S exchanged
  4  11  {A  E  E  I} Q  U  S  S  T  Y  O [N] // I and Y exchanged
  5  11  {A  E  E  I  N} U  S  S  T  Y [O] Q  // N and Q exchanged
  6  8   {A  E  E  I  N  O} S  S  T  Y  U [Q] // O and U exchanged 
  7  7   {A  E  E  I  N  O  Q}[S] T  Y  U  S  // Q and 1st S exchanged
  8  10  {A  E  E  I  N  O  Q  S} T  Y  U [S] // 2nd S exchanged with itself
  9  11  {A  E  E  I  N  O  Q  S  S} Y  U [T] // T and 1st S exchanged
  10 11  {A  E  E  I  N  O  Q  S  S  T}[U] Y  // Y and T exchanged
  11 11  {A  E  E  I  N  O  Q  S  S  T  U}[Y] // U exchanged with itself
         {A  E  E  I  N  O  Q  S  S  T  U  Y  //done
  
*/

public class Ex2101SelectionSortTrace {

}
