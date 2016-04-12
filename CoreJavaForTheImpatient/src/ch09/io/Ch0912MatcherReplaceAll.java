package ch09.io;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

// 12. Come up with a realistic use case for using group 
// references in Matcher.replaceAll and implement it.

public class Ch0912MatcherReplaceAll {

  public static void main(String[] args) {

    // Using group references in Matcher.replaceAll can be useful
    // for template like bulk replacement of text, for example:

    String text  =  "Sable Belle says this and Mable Belle says that.";                        

    String regex = "(([^ ]+) (Belle)) ";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);

    String replaceAll = matcher.replaceAll("Jiminy Cricket ");
    System.out.println(replaceAll);
    // Jiminy Cricket says this and Jiminy Cricket says that.

    replaceAll = matcher.replaceAll("Carlo Crocodile ");
    System.out.println(replaceAll);
    // Carlo Crocodile says this and Carlo Crocodile says that.

  }
}

