package ex11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

//  1.1.21  Write a program that reads in lines from standard input with each line contain-
//  ing a name and two integers and then uses  printf() to print a table with a column of
//  the names, the integers, and the result of dividing the first by the second, accurate to
//  three decimal places. You could use a program like this to tabulate batting averages for
//  baseball players or grades for students.

public class Ex1121ParseStdIn {

  public static void parseStdIn() throws InterruptedException {
    // One pass method for handling lots of streaming input efficiently
    // Assuming reasonable formatting defaults of:
    // 1. 20 chars for max name length
    // 2. 15 chars for max int length
    Scanner sc = new Scanner(System.in);
    String line = "";
    String[] f = null;
    String name = "";
    int i1 = 0;
    int i2 = 0;
    int maxn = 15; // max name length
    int maxi = 7; // max int length as a string
    
    // print table header
    System.out.printf("%-"+maxn+"s %-"+maxi+"s %-"+maxi+"s %-"+maxi
        +"s\n","name","i1","i2","c");
    System.out.printf("%-"+maxn+"s %-"+maxi+"s %-"+maxi+"s %-"+maxi
        +"s\n",rep(maxn,'='),rep(maxi,'='),rep(maxi,'='),rep(maxi,'='));

    while (sc.hasNextLine()) {
        line = sc.nextLine();
        f = line.split("\\s+");
        
        // handle bad input
        if (f.length != 3) continue;
        if (!f[1].matches("[0-9]+")) continue;
        if (!f[2].matches("[0-9]+")) continue;
        
        // assign values to variables for output
        name = f[0];
        i1 = (new Integer(f[1])).intValue();
        i2 = (new Integer(f[2])).intValue();
        
        // print formatted output for current line
        System.out.printf("%-"+maxn+"s %"+maxi+"d %"+maxi+"d %"+maxi
            +".3f\n",name,i1,i2,(i1 + 0.)/i2);
//      }
    }
    sc.close();
  }

  public static String parseStdIn2() throws InterruptedException {
    // This is a longer method for small amounts of input data which determines
    // formatting parameters before printing the output.
    // Overall strategy: (1) get all the input; (2) process input to get formatting
    // parameters such as max widths of columns and skip lines that don't match the
    // the specifications; (3) format the input and return it as a String. I assume 
    // that in each line of input the name and the two ints are separated by whitespace
    // and there is nothing else in it.

    // (1) get the input
    List<String> list = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
    String line = "";
    while (sc.hasNextLine()) {
        line = sc.nextLine();
        list.add(line);
    }
    sc.close();

    // (2) get formatting parameters and skip bad lines
    String[] f = null;
    String name = "";
    int i1 = 0;
    int i2 = 0;
    int maxn = 0;
    int maxi = 0; 
    int maxc = 0;
    int tmp = 0;
    List<String> list2 = new ArrayList<>();
    for (String s : list)   {
      f = s.split("\\s+");
      if (f.length != 3) continue;
      if (!f[1].matches("[0-9]+")) continue;
      if (!f[2].matches("[0-9]+")) continue;
      name = f[0];
      if (name.length() > maxn) maxn = name.length();
      if (f[1].length() > maxi) maxi = f[1].length();
      if (f[2].length() > maxi) maxi = f[2].length();
      i1 = (new Integer(f[1])).intValue();
      i2 = (new Integer(f[2])).intValue();
      tmp = String.format("%.3f", (i1 + 0.)/i2).length();
      if (tmp > maxc) maxc = tmp;
      list2.add(name+","+f[1]+","+f[2]);
    }
    
    // (3) format the data and return it as a String
    StringBuilder sb = new StringBuilder();
    Formatter fmt = new Formatter(sb, Locale.US);
    // format the header line
    fmt.format("%-"+maxn+"s %-"+maxi+"s %-"+maxi+"s %-"+maxc+"s\n","name","i1","i2","c");
    fmt.format("%-"+maxn+"s %"+maxi+"s %"+maxi+"s %"+maxc+"s\n",
        rep(maxn,'='),rep(maxi,'='),rep(maxi,'='),rep(maxc,'='));
    for (String s : list2) {
      f = s.split(",");
      name = f[0];
      i1 = (new Integer(f[1])).intValue();
      i2 = (new Integer(f[2])).intValue();
      fmt.format("%-"+maxn+"s %"+maxi+"d %"+maxi+"d %"+maxc+".3f\n",
          name,i1,i2,(i1 + 0.)/i2);
    }
    fmt.close();
    return sb.toString();
  }
  
  public static final String rep(int length, char c) {
    // create a new String consisting of char c repeated length times
    char[] data = new char[length];
    Arrays.fill(data, c);
    return new String(data);
  }

  public static void main(String[] args) throws InterruptedException {
    // for both tests the input is as follows:
    //  joe 3 2
    //  mary 19 7
    //  pete 8 3
    //  beth 32 5
    //  steve 17 6
    //  ann 21 8

//    parseStdIn();
    //  name            i1      i2      c      
    //  =============== ======= ======= =======
    //  joe                   3       2   1.500
    //  mary                 19       7   2.714
    //  pete                  8       3   2.667
    //  beth                 32       5   6.400
    //  steve                17       6   2.833
    //  ann                  21       8   2.625


    System.out.println(parseStdIn2());
    //  name  i1 i2 c    
    //  ===== == == =====
    //  joe    3  2 1.500
    //  mary  19  7 2.714
    //  pete   8  3 2.667
    //  beth  32  5 6.400
    //  steve 17  6 2.833
    //  ann   21  8 2.625

  }

}
