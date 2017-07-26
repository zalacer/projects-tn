package ex21;


/* p264
  2.1.9  Show, in the style of the example trace with Algorithm 2.3, how shellsort 
  sorts the array  E A S Y S H E L L S O R T Q U E S T I O N .
  
  Here is it in the "array contents after each pass" style shown on p259.
  
      Shellsort trace (array contents after each pass)
    input   E  A  S  Y  S  H  E  L  L  S  O  R  T  Q  U  E  S  T  I  O  N 
  13-sort   E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N 
   4-sort   E  A  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y 
   1-sort   A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  S  T  T  U  Y
   
  This was generated by sort.ShellWork.printTraceAfterEachPass(T[]).
 
  A very detailed trace printed from sort.ShellWork.printVeryDetailedTrace(T[]) is 
  given below. It can be condensed to the style in the text on p260. The way to do
  this is:
  1. Note the rightmost column which contains values of a[j-h] and a[j] or just a[j].
  2. If it contains just a[j] color its value red in the array on the same line and 
     the remaining array elements are grey.
  3. If it contains a[j-h] and a[j] and the next line doesn't have a chained sequence, 
     color the first red and the second black and the remaining array elements are grey.
  4. If it contains a[j-h] and a[j] and the next line has a chained sequence, go to the
     last line of the chain and color it a[j-h] red, and all the other a[j]'s in all 
     lines of the chain black and delete all lines of the chain except its last. Chains
     can be recognized when the index j-h on one line is the same as j on the next. For
     example, below the first chain begins on the line annotated with "CHAIN" on the left
     and the right and it's two lines long. It's a[j-h] and a[j] values on the right line
     by line are "a[14]=I, a[18]=U" and "a[10]=I, a[14]=O" which means that "I" in a[18]
     was swapped with U in a[14] and then the "I" in a[14] was swapped with the "O" in
     a[10] with the result that "I" is in a[10], the "O" is in a[14] and the "U" is in
     a[18] on the last line of the chain. So on that line color a[10] red, color a[14]
     black, color a[18] black, color the rest of the elements in the array on that line
     grey and delete line above it which is the first line of the chain. There are several
     more chains to handle below this one.
     
     The final solution is shown on http://algs4.cs.princeton.edu/21elementary/.
 
    Very Detailed trace of shellsort (insertions)                           a[j-h], a[j]
    input   E  A  S  Y  S  H  E  L  L  S  O  R  T  Q  U  E  S  T  I  O  N   or just a[j]
            E  A  S  Y  S  H  E  L  L  S  O  R  T  Q  U  E  S  T  I  O  N   a[13]=Q
            E  A  S  Y  S  H  E  L  L  S  O  R  T  Q  U  E  S  T  I  O  N   a[14]=U
  13-sort   E  A  E  Y  S  H  E  L  L  S  O  R  T  Q  U  S  S  T  I  O  N   a[2]=E, a[15]=S
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[3]=S, a[16]=Y
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[17]=T
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[18]=I
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[19]=O
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[20]=N
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[4]=S
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[5]=H
            E  A  E  S  S  H  E  L  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[6]=E
   4-sort   E  A  E  L  S  H  E  S  L  S  O  R  T  Q  U  S  Y  T  I  O  N   a[3]=L, a[7]=S
            E  A  E  L  L  H  E  S  S  S  O  R  T  Q  U  S  Y  T  I  O  N   a[4]=L, a[8]=S
            E  A  E  L  L  H  E  S  S  S  O  R  T  Q  U  S  Y  T  I  O  N   a[4]=L
            E  A  E  L  L  H  E  S  S  S  O  R  T  Q  U  S  Y  T  I  O  N   a[9]=S
            E  A  E  L  L  H  E  S  S  S  O  R  T  Q  U  S  Y  T  I  O  N   a[10]=O
            E  A  E  L  L  H  E  R  S  S  O  S  T  Q  U  S  Y  T  I  O  N   a[7]=R, a[11]=S
            E  A  E  L  L  H  E  R  S  S  O  S  T  Q  U  S  Y  T  I  O  N   a[7]=R
            E  A  E  L  L  H  E  R  S  S  O  S  T  Q  U  S  Y  T  I  O  N   a[12]=T
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[9]=Q, a[13]=S
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[9]=Q
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[14]=U
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[15]=S
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[16]=Y
            E  A  E  L  L  H  E  R  S  Q  O  S  T  S  U  S  Y  T  I  O  N   a[17]=T
 CHAIN      E  A  E  L  L  H  E  R  S  Q  O  S  T  S  I  S  Y  T  U  O  N   a[14]=I, a[18]=U   CHAIN
            E  A  E  L  L  H  E  R  S  Q  I  S  T  S  O  S  Y  T  U  O  N   a[10]=I, a[14]=O
            E  A  E  L  L  H  E  R  S  Q  I  S  T  S  O  S  Y  T  U  O  N   a[10]=I
            E  A  E  L  L  H  E  R  S  Q  I  S  T  S  O  O  Y  T  U  S  N   a[15]=O, a[19]=S
            E  A  E  L  L  H  E  R  S  Q  I  O  T  S  O  S  Y  T  U  S  N   a[11]=O, a[15]=S
            E  A  E  L  L  H  E  O  S  Q  I  R  T  S  O  S  Y  T  U  S  N   a[7]=O, a[11]=R
            E  A  E  L  L  H  E  O  S  Q  I  R  T  S  O  S  Y  T  U  S  N   a[7]=O
            E  A  E  L  L  H  E  O  S  Q  I  R  T  S  O  S  N  T  U  S  Y   a[16]=N, a[20]=Y
            E  A  E  L  L  H  E  O  S  Q  I  R  N  S  O  S  T  T  U  S  Y   a[12]=N, a[16]=T
            E  A  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[8]=N, a[12]=S
            E  A  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[8]=N
   1-sort   A  E  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[0]=A, a[1]=E
            A  E  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[2]=E
            A  E  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[3]=L
            A  E  E  L  L  H  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[4]=L
            A  E  E  L  H  L  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[4]=H, a[5]=L
            A  E  E  H  L  L  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[3]=H, a[4]=L
            A  E  E  H  L  L  E  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[3]=H
            A  E  E  H  L  E  L  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[5]=E, a[6]=L
            A  E  E  H  E  L  L  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[4]=E, a[5]=L
            A  E  E  E  H  L  L  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[3]=E, a[4]=H
            A  E  E  E  H  L  L  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[3]=E
            A  E  E  E  H  L  L  O  N  Q  I  R  S  S  O  S  T  T  U  S  Y   a[7]=O
            A  E  E  E  H  L  L  N  O  Q  I  R  S  S  O  S  T  T  U  S  Y   a[7]=N, a[8]=O
            A  E  E  E  H  L  L  N  O  Q  I  R  S  S  O  S  T  T  U  S  Y   a[7]=N
            A  E  E  E  H  L  L  N  O  Q  I  R  S  S  O  S  T  T  U  S  Y   a[9]=Q
            A  E  E  E  H  L  L  N  O  I  Q  R  S  S  O  S  T  T  U  S  Y   a[9]=I, a[10]=Q
            A  E  E  E  H  L  L  N  I  O  Q  R  S  S  O  S  T  T  U  S  Y   a[8]=I, a[9]=O
            A  E  E  E  H  L  L  I  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[7]=I, a[8]=N
            A  E  E  E  H  L  I  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[6]=I, a[7]=L
            A  E  E  E  H  I  L  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[5]=I, a[6]=L
            A  E  E  E  H  I  L  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[5]=I
            A  E  E  E  H  I  L  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[11]=R
            A  E  E  E  H  I  L  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[12]=S
            A  E  E  E  H  I  L  L  N  O  Q  R  S  S  O  S  T  T  U  S  Y   a[13]=S
            A  E  E  E  H  I  L  L  N  O  Q  R  S  O  S  S  T  T  U  S  Y   a[13]=O, a[14]=S
            A  E  E  E  H  I  L  L  N  O  Q  R  O  S  S  S  T  T  U  S  Y   a[12]=O, a[13]=S
            A  E  E  E  H  I  L  L  N  O  Q  O  R  S  S  S  T  T  U  S  Y   a[11]=O, a[12]=R
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[10]=O, a[11]=Q
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[10]=O
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[15]=S
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[16]=T
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[17]=T
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  U  S  Y   a[18]=U
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  T  S  U  Y   a[18]=S, a[19]=U
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  T  S  T  U  Y   a[17]=S, a[18]=T
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  S  T  T  U  Y   a[16]=S, a[17]=T
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  S  T  T  U  Y   a[16]=S
            A  E  E  E  H  I  L  L  N  O  O  Q  R  S  S  S  S  T  T  U  Y   a[20]=Y
 
*/

public class Ex2109ShellSortTrace {

  public static void main(String[] args) {
    

  }
}