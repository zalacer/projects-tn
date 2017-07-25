package ex25;

import puzzle8.Board;
import puzzle8.Solver;

/* p358
  2.5.32 8 puzzle. The 8 puzzle is a game popularized by S. Loyd in the 1870s. 
  It is played on a 3-by-3 grid with 8 tiles labeled 1 through 8 and a blank 
  square. Your goal is to rearrange the tiles so that they are in order. You 
  are permitted to slide one of the available tiles horizontally or vertically 
  (but not diagonally) into the blank square. Write a program that solves the 
  puzzle using the A* algorithm. Start by using as priority the sum of the 
  number of moves made to get to this board position plus the number of tiles
  in the wrong position. (Note that the number of moves you must make from a 
  given board position is at least as big as the number of tiles in the wrong 
  place.) Investigate substituting other functions for the number of tiles in 
  the wrong position, such as the sum of the Manhattan distance from each tile 
  to its correct position, or the sums of the squares of these distances.

  There is a solution to this at https://github.com/merwan/algs4/tree/master/8-puzzle/src.
  It's available in this project in the puzzle8 package. An example of using it
  is given below. In the interest of moving on to new material, I'm not going to 
  reinvent this wheel here. Thanks to Merouane Atig ("merwan") for a solution.
  
  Some information about puzzle 8 and hints for this exercise are at 
      https://www.cs.princeton.edu/courses/archive/fall12/cos226/assignments/8puzzle.html
  that's in this project at Ex2532-8-PuzzleProgrammingAssignment.txt.

 */
  
public class Ex2532Puzzle8 { 

  public static void main(String[] args) {
    
    int[][] blocks = new int[3][];
    blocks[0] = new int[]{1,2,3};
    blocks[1] = new int[]{6,5,4};
    blocks[2] = new int[]{8,7,0};
    
    Board initial = new Board(blocks);
    
    Solver solver = new Solver(initial);
    
    if (!solver.isSolvable())
      System.out.println("No solution possible");
    else {
      System.out.println("Minimum number of moves = " + solver.moves());
        for (Board board : solver.solution())
            System.out.println(board);
    }
/*    
    Minimum number of moves = 20
     1  2  3 
     6  5  4 
     8  7  0 
    
     1  2  3 
     6  5  0 
     8  7  4 
    
     1  2  3 
     6  0  5 
     8  7  4 
    
     1  2  3 
     6  7  5 
     8  0  4 
    
     1  2  3 
     6  7  5 
     0  8  4 
    
     1  2  3 
     0  7  5 
     6  8  4 
    
     1  2  3 
     7  0  5 
     6  8  4 
    
     1  2  3 
     7  8  5 
     6  0  4 
    
     1  2  3 
     7  8  5 
     6  4  0 
    
     1  2  3 
     7  8  0 
     6  4  5 
    
     1  2  3 
     7  0  8 
     6  4  5 
    
     1  2  3 
     7  4  8 
     6  0  5 
    
     1  2  3 
     7  4  8 
     0  6  5 
    
     1  2  3 
     0  4  8 
     7  6  5 
    
     1  2  3 
     4  0  8 
     7  6  5 
    
     1  2  3 
     4  8  0 
     7  6  5 
    
     1  2  3 
     4  8  5 
     7  6  0 
    
     1  2  3 
     4  8  5 
     7  0  6 
    
     1  2  3 
     4  0  5 
     7  8  6 
    
     1  2  3 
     4  5  0 
     7  8  6 
    
     1  2  3 
     4  5  6 
     7  8  0     
*/
  
  }

}


