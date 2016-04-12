package ch09.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 10. Using a regular expression, extract all decimal integers (including negative ones)
// from a string into an ArrayList<Integer> (a) using find, and (b) using
// split. Note that a + or - that is not followed by a digit is a delimiter.

// Assuming "not followed" means not immediately followed including whitespace.

public class Ch0910Regex {

  public static void main(String[] args) {

    String s = "1 and -2 and - 23 and + 16 and -15";
    String regex = "(((\\+|-)?[0-9]+))";
    List<Integer> ints = new ArrayList<>();

    // using find
    Matcher matcher = Pattern.compile(regex).matcher(s);
    while (matcher.find()) ints.add(new Integer(matcher.group()));       
    System.out.println(ints); // [1, -2, 23, 16, -15]

    // using split
    ints.clear();
    for(String e : s.split("\\s+")) 
      if (e.matches("(\\+|-)?[0-9]+")) 
        ints.add(new Integer(e)); 
    System.out.println(ints); // [1, -2, 23, 16, -15]

  }

}
