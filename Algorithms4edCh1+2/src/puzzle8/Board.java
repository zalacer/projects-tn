package puzzle8;

// from https://github.com/merwan/algs4/blob/master/8-puzzle/src/Board.java

import ds.Stack;

public class Board {
  private final int N;
  private final int[][] tiles;

  /*
   * construct a board from an N-by-N array of blocks (where blocks[i][j] =
   * block in row i, column j)
   */
  public Board(int[][] blocks) {
    N = blocks.length;
    tiles = new int[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        tiles[i][j] = blocks[i][j];
      }
    }
  }

  private int[][] createGoalBoard() {
    int[][] array = new int[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        array[i][j] = goalValueAt(i, j);
      }
    }

    return array;
  }

  /*
   * board dimension N
   */
  public int dimension() {
    return N;
  }

  /*
   * number of blocks out of place
   */
  public int hamming() {
    int sum = 0;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (tiles[i][j] != goalValueAt(i, j) && !isEnd(i, j)) {
          sum++;
        }
      }
    }
    return sum;
  }

  private int goalValueAt(int i, int j) {
    if (isEnd(i, j)) {
      return 0;
    }
    return 1 + i * N + j;
  }

  private boolean isEnd(int i, int j) {
    return i == N - 1 && j == N - 1;
  }

  /*
   * sum of Manhattan distances between blocks and goal
   */
  public int manhattan() {
    int sum = 0;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        int value = tiles[i][j];
        if (value != 0 && value != goalValueAt(i, j)) {
          int initialX = (value - 1) / N;
          int initialY = value - 1 - initialX * N;
          int distance = Math.abs(i - initialX)
              + Math.abs(j - initialY);
          sum += distance;
        }
      }
    }
    return sum;
  }

  /*
   * is this board the goal board?
   */
  public boolean isGoal() {
    return tilesEquals(this.tiles, createGoalBoard());
  }

  private boolean tilesEquals(int[][] first, int[][] second) {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (first[i][j] != second[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

  /*
   * a board obtained by exchanging two adjacent blocks in the same row
   */
  public Board twin() {
    Board board = new Board(tiles);

    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N - 1; j++) {
        if (tiles[i][j] != 0 && tiles[i][j + 1] != 0) {
          board.swap(i, j, i, j + 1);
          return board;
        }
      }
    }

    return board;
  }

  private boolean swap(int i, int j, int it, int jt) {
    if (it < 0 || it >= N || jt < 0 || jt >= N) {
      return false;
    }
    int temp = tiles[i][j];
    tiles[i][j] = tiles[it][jt];
    tiles[it][jt] = temp;
    return true;
  }

  /*
   * does this board equal x? (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object x) {
    if (x == this)
      return true;
    if (x == null)
      return false;
    if (x.getClass() != this.getClass())
      return false;

    Board that = (Board) x;
    return this.N == that.N && tilesEquals(this.tiles, that.tiles);
  }

  /*
   * all neighboring boards
   */
  public Iterable<Board> neighbors() {
    int i0 = 0, j0 = 0;
    boolean found = false;
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        if (tiles[i][j] == 0) {
          i0 = i;
          j0 = j;
          found = true;
          break;
        }
      }
      if (found) {
        break;
      }
    }

    Stack<Board> boards = new Stack<Board>();
    Board board = new Board(tiles);
    boolean isNeighbor = board.swap(i0, j0, i0 - 1, j0);
    if (isNeighbor) {
      boards.push(board);
    }
    board = new Board(tiles);
    isNeighbor = board.swap(i0, j0, i0, j0 - 1);
    if (isNeighbor) {
      boards.push(board);
    }
    board = new Board(tiles);
    isNeighbor = board.swap(i0, j0, i0 + 1, j0);
    if (isNeighbor) {
      boards.push(board);
    }
    board = new Board(tiles);
    isNeighbor = board.swap(i0, j0, i0, j0 + 1);
    if (isNeighbor) {
      boards.push(board);
    }

    return boards;
  }

  /*
   * string representation of the board (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
//    s.append(N + "\n");
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        s.append(String.format("%2d ", tiles[i][j]));
      }
      s.append("\n");
    }
    return s.toString();
  }
}