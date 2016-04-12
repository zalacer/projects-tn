package ch01.fundamentals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//  14. Write a program that reads a two-dimensional array of integers and determines
//  whether it is a magic square (that is, whether the sum of all rows, all columns, and
//  the diagonals is the same). Accept lines of input that you break up into individual
//  integers, and stop when the user enters a blank line. For example, with the input
//      16 3 2 13
//      3 10 11 8
//      9  6 7 12
//      4 15 14 1
//  your program should respond affirmatively.

// http://mathforum.org/alejandre/magic.square/adler/adler.whatsquare.html
// A magic square is an arrangement of the numbers from 1 to n^2 (n-squared) in an 
// nxn matrix, with each number occurring exactly once, and such that the sum of the 
// entries of any row, any column, or any main diagonal is the same. It is not hard 
// to show that this sum must be n(n^2+1)/2.

public class Ch0114MagicSquare {

  public static void isMagicSquare() {
    int cols = 0;
    int rows = 0;
    String[] x = null;
    boolean stop  = false;
    boolean anyInput = false;
    boolean someGoodInput = false;
    Scanner in = new Scanner(System.in);
    List<List<Integer>> a = new ArrayList<>();

    System.out.println(
            "Please enter lines of space-separated positive integers."
        + "\nEach line should have the same number of integers which"
        + "\nshould be the same as the number of lines with a minimum"
        + "\nof three for a magic square, but this program will let"
        + "\nyou try two. You will be prompted to enter each line.\n");
    TOP:
    while (true) {
      System.out.println("Enter at least 2 positive integers separated by spaces: ");
      String s = in.nextLine();
      if (s.equals("")) {
        System.out.println("closing input and doing magic square determination...\n");
        if (!anyInput) {
          System.out.println("can't get a magic square without any input");
          in.close();
          return;
        } else if (!someGoodInput){
          System.out.println("can't get a magic square without any good input");
          in.close();
          return;
        } else {
          in.close();
          stop = true;
        }
      }
      if (!stop) {
        x = s.trim().split("\\s+");
        anyInput = true;
        if (x.length == 1) {
          if (s.trim().matches("\\d+")) {
            System.out.println("this line contains only one number, but need at least two");
          } else {
            System.out.println("could not extract any numbers from this line");
          }
          System.out.println();
          continue;
        } else {
          for (int i = 0; i < x.length; i++) {
            if (!(x[i].matches("\\d+"))) {
              System.out.println("this line contains non-numeric data and cannot be used");
              continue TOP;
            }
          }
        }
      }
      if (stop && rows != cols) {
        System.out.println("it's not a magic square since it has different numbers of rows and columns");
        return;
      }
      if (cols == 0) {
        cols = x.length;
      } else {
        if (cols != x.length) {
          if (stop) {
            System.out.println("it's not a magic square since all rows don't have the same length");
            return;
          } else {
            System.out.println("the data in this line can't be accepted since it has a"
              +"\n  different number of integers than previous lines");
            continue;
          }
        }
      }
      someGoodInput = true;
      rows++;
      if (rows == cols) {
        System.out.println("you now have as many rows as columns so closing input\n");
        stop = true;
      }

      Integer[] t = new Integer[cols];
      for (int i = 0; i < cols; i++) {
        t[i] = new Integer(x[i]);
      }
      ArrayList<Integer> l = new ArrayList<Integer>(Arrays.asList(t));
      a.add(l);
      if (stop) {
        in.close();
        break;
      }
    } // end while

    
    if (a.size() == 0) {
      System.out.println("you entered no acceptable data and it's not a magic square");
    } else {
      int[][] ia = new int[a.size()][a.get(0).size()];
      for (int row = 0; row < a.size(); row++) {
        for (int col = 0; col < a.get(0).size(); col++) {              
          ia[row][col] =  a.get(row).get(col).intValue();
        }
      }
      System.out.println("here is the data you entered");
      for (int i = 0; i < ia.length; i++)
        System.out.println(Arrays.toString(ia[i]));
      System.out.println();
      isMagicSquare(ia);
    }
  }

  public static boolean isMagicSquare(int[][] ia) {
    int length = ia.length;
    int c, r, rowlength, rowsum, colsum, diagsum, antidiagsum;
    boolean always;

    if (length < 2) {
      System.out.println("it's not a magic square since it doesn't have at least 2 rows");
      // actually must have at least 3 rows
      return false;
    }

    // test uniqueness of elements
    c = 0;
    Set<Integer> iset = new HashSet<>();
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < ia[i].length; j++) {
        c++;
        iset.add(ia[i][j]);
      }
    }
    if (iset.size() < c) {
      System.out.println("it's not a magic square since all its elements aren't unique");
      return false;
    }

    // test lengths of rows vs columns
    rowlength = -1; always = true;
    for (int i = 0; i < length; i++) {
      if (rowlength == -1) {
        rowlength = ia[i].length;
      } else {
        if (ia[i].length != rowlength) 
          always = false;
      }
    }
    
    for (int i = 0; i < length; i++) {
      if (ia[i].length != length) {
        if (always) {
          System.out.println("it's not a magic square since its row length not the same as its column length");
        } else {
          System.out.println("it's not a magic square since its row length not always the same as its column length");
        }
        return false;
      }
    } 

    // test row sums
    rowsum = 0;
    for (int i = 0; i < length; i++) {
      r = 0;
      for (int j = 0; j < length; j++) {
        r += ia[i][j];
      }
      if (rowsum == 0) {
        rowsum = r;
      } else if (rowsum != r) {
        System.out.println("it's not a magic square since all its rows do not have the same sum");
        return false;
      }
    }

    // test col sums
    colsum = 0;
    for (int i = 0; i < length; i++) {
      c = 0;
      for (int j = 0; j < length; j++) {
        c += ia[j][i];
      }
      if (colsum == 0) {
        colsum = c;
      } else if (colsum != c) {
        System.out.println("it's not a magic square since all its columns do not have the same sum");
        return false;
      }
    }

    // test row sum vs col sum
    if (rowsum != colsum) {
      System.out.println("it's not a magic square since its row sum not the same as col sum");
      return false;
    }

    // test diagonal sum
    diagsum = 0;
    for (int i = 0; i < length; i++) {
      diagsum += ia[i][i];
    }
    if (diagsum != rowsum) {
      System.out.println("it's not a magic square since its diagonal sum not the same as rowsum");
      return false;
    }

    // test anti diagonal sum
    antidiagsum = 0;
    for (int i = 0; i < length; i++) {
      antidiagsum += ia[length-i-1][i];
    }
    if (antidiagsum != rowsum) {
      System.out.println("it's not a magic square since its antidiagonal sum not the same as rowsum");
      return false;
    }
    
    System.out.println("it's a magic square\n");
    return true;
  }

  public static void main(String[] args) {
    
    isMagicSquare();
 
  }

}

// here are two magic square examples from
//   http://mathforum.org/alejandre/magic.square/adler/adler.whatsquare.html

//8 1 6
//3 5 7
//4 9 2

//1  15 14 4
//12 6  7  9
//8  10 11 5
//13 3  2  16
