package ch08.streams;

import java.util.Arrays;

// 1. Verify that asking for the first five long words does not call the filter
// method once the fifth long word has been found. Simply log each method call.

public class Ch0801Filter {

  public static void main(String[] args) {

    // define a long word as one with 12 or more characters
    // the words array contains 9 such long words followed by nine short words
    String[] words = new String[] {
        "acclimatizations",
        "accidentalness",
        "accommodativeness",
        "accomplishments",
        "accountabilities",
        "acculturationist",
        "accumulativeness",
        "accusative-dative",         
        "aceacenaphthene",
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",          
        "seven",
        "eight",
        "nine"
    };

    System.out.println(words.length);
    // 18

    int[] lengths = new int[words.length];
    for (int i = 0; i < words.length; i++)
      lengths[i] = words[i].length();

    System.out.println(Arrays.toString(lengths));
    // [16, 14, 17, 15, 16, 16, 16, 17, 15, 3, 3, 5, 4, 4, 3, 5, 5, 4]

    // if a reference is not changed it is effectively final even if the 
    // object referenced is changed, therefore external array elements
    // can be changed within a stream process without error

    int[] c = new int[]{0};
    String[] s = new String[1];

    Arrays.stream(words)
    .filter(x -> { c[0]++; s[0] = x; return x.length() > 12; })
    .limit(5).count();

    assert c[0] == 5;
    System.out.println(c[0]); 
    // 5

    assert s[0].equals(words[4]);
    System.out.println(s[0]);
    // accountabilities

    // this demonstrates that filtering stopped after the 5th long word
    // which is "accountabilities"

  }

}
