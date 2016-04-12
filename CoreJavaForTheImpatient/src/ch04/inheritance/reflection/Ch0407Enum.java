package ch04.inheritance.reflection;

// 7. Define an enumeration type for the eight combinations of primary colors 
// BLACK, RED, BLUE, GREEN, CYAN, MAGENTA, YELLOW, WHITE with methods getRed, 
// getGreen, and getBlue.

public enum Ch0407Enum {

  BLACK,
  RED, 
  BLUE, 
  GREEN, 
  CYAN, 
  MAGENTA,
  YELLOW,
  WHITE;

  public static Ch0407Enum getRed() {
    return RED;
  }

  public static Ch0407Enum getGreen() {
    return GREEN;
  }

  public static Ch0407Enum getBlue() {
    return BLUE;
  }

  public static void main(String[] args) {

    System.out.println(WHITE.ordinal());WHITE.ordinal(); // 7
    System.out.println(Ch0407Enum.getRed()); // RED
    System.out.println(Ch0407Enum.getGreen()); // GREEN
    System.out.println(Ch0407Enum.getBlue()); // BLUE

  }

}
