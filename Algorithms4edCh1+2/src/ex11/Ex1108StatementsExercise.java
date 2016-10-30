package ex11;

//  1.1.8  What do each of the following print?
//  a. System.out.println('b');
//  b. System.out.println('b' + 'c');
//  c. System.out.println((char) ('a' + 4));
//  Explain each outcome.

public class Ex1108StatementsExercise {
  
  public static void main(String[] args) {

  System.out.println('b'); // prints "b"
  // Using Eclipse to examine the Java "1.8.0_77" sources:
  // System.out is a java.io.PrintStream. In that class:
  //      public void println(char x) {
  //        synchronized (this) {
  //          print(x);
  //          newLine();
  //        }
  //
  //      public void print(char c) {
  //        write(String.valueOf(c));
  //      } 
  // Then in the java.lang.String sources:
  //      /**
  //       * Returns the string representation of the {@code char}
  //       * argument.
  //       *
  //       * @param   c   a {@code char}.
  //       * @return  a string of length {@code 1} containing
  //       *          as its single character the argument {@code c}.
  //       */
  //      public static String valueOf(char c) {
  //        char data[] = {c};
  //        return new String(data, true);
  //      }
  // Apparently this invokes the String constructor
  //      public String(char value[]) {
  //        this.value = Arrays.copyOf(value, value.length);
  //      }
  // since there is no String constructor that takes a boolean 2nd arg.
  // In any case, the point is that System.out.println('b') converts the char 'b'
  // to the String "b" and then prints it as a String.
 
  System.out.println('b' + 'c'); // prints "197" which is the sum of the code point 
    // values of 'b' and 'c'; e.g.: "bc".codePoints().toArray() == int[98, 99]
    // This since happens only the numeric representation of char has a "+" operator
    // and all println sees is that representation of the sum.
  
  System.out.println((char) ('a' + 4)); // prints "e" which has the code point value 
    // of 'a' (97) plus 4 cast to char which is then converted to a String and printed

  }

}

