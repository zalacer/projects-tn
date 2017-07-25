package ex21;

import java.util.Arrays;
import java.util.Random;

import sort.Insertion;
import sort.Selection;
import sort.Shell;

/* p265
  2.1.17 Animation. Add code to  Insertion and  Selection to make them draw the
  array contents as vertical bars like the visual traces in this section, redrawing the bars
  after each pass, to produce an animated effect, ending in a “sorted” picture where the
  bars appear in order of their height. Hint: Use a client like the one in the text that gener-
  ates random Double values, insert calls to show() as appropriate in the sort code, and
  implement a show() method that clears the canvas and draws the bars.

  The way I implemented this, for example in sort.Insertion, was to:
  1. Make a copy of the original sort(T[]) method called visualSort(T[]) where
     <T extends Comparable<? super T>>.
  2. Create a new sort method numberSort(U[]) where U extends Number, which converts the input
     array to a double array using Number.doubleValue(), eliminates less by comparing doubles 
     directly with "<" and calls doubleExch(double[], int, int). This method works for any
     Number including Byte, Double, Float, Integer, Long, Short and more.
  3. Modify visualSort() to return numberSort((Number[]) a) if
     Number.class.isAssignableFrom(a.getClass().getComponentType()) where a is the name of the
     input array. It returns numberSort() because all the sort's were already modified to
     return the number of writes for exercise 2.1.15.
  4. Write a visual animation method visualShow(double[]) that displays the double array 
     created in numberSort() after every doubleExch() call.

  Strings are a different matter and it would be useful to have a method for converting an 
  arbitrary string to a positive or zero double such that for all strings their double values are 
  consistent with String.compareTo() and String.equals().
  
  I put shell sort in the mix. Oddly, selection sort is faster than insertion and shell 
  sorts visually, even with a bigger array and StdDraw.show(10) vs. StdDraw.show(1).

 */

public class Ex2117VisuallyAnimateInsertionAndSelection {

  public static void main(String[] args) {

    Random r = new Random();
    Double[] da = new Double[400];
    for (int i = 0; i < da.length; i++) da[i] = r.nextDouble();
    Selection.visualSort(Arrays.copyOf(da, 400));
    Insertion.visualSort(Arrays.copyOf(da, 100));
    Shell.visualSort(Arrays.copyOf(da, 200));

  }

}
