package ex25;

/* p357
  2.5.30 Boerner’s theorem. True or false: If you sort each column of a matrix, 
  then sort each row, the columns are still sorted. Justify your answer.
  
  True.
  
  Proof: Column sort the matrix. Row sort it by permuting the columns until the
  first row is sorted, then permute the columns without the first row until the
  second row is sorted, and proceed likewise until all the rows are sorted. After 
  each step of sorting the rows the columns remain sorted. Therefore row sorting
  a matrix doesn't intefere with column sorting it and vice versa when applied
  to the transposed matrix.
                                    
  example:
  
       column        sort         sort         sort 
        sort         row 1        row 2        row 3
  f g h       c a b        a b c        a b c        a b c  
  e a i   ->  e d h   ->   d h e   ->   d e h   ->   d e h 
  c d b       f g i        g i f        g f i        f g i
  
  This works because sorting a given row is done by permuting the sorted 
  columns at and below its level which maintains order relationships downwards.
  
  From R. W. Cottle, J. L. Pietenpol, J. Luh, Row ordered and column ordered matrices,
  Amer. Math. Monthly 70 (1963) 212-213.
  In B. E. Tenner, A Non-Messing-up Phenomenon for Posets, arXiv:math/0404396v4 [math.CO] 
  (https://arxiv.org/abs/math/0404396v4) version v4 (17 Oct 2005) 1.
  
  Page 1 of the latter reference is in this project at
    Ex2530-Non-Messing-up-Phenomenon-for-Posets-Tenner-2005-page1.pdf.
  
 */

public class Ex2530BoernersTheorem { 

  public static void main(String[] args) {

  }

}


