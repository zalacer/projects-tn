package ch10.concurrency;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 24. Write a program that asks the user for a URL, reads the web page
// at that URL, and displays all the links. Use a CompletableFuture for 
// each stage. Don’t call get. To prevent your program from terminating 
// prematurely, call 
//   ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);

// the solution below is from the FutureDemo example provided in the sources 
// for "Java SE 8 for the Really Impatient" modified to sort the output

public class Ch1024CompletableFuture {

  public static Scanner in = new Scanner(System.in);

  public static String getInput(String prompt) {
    System.out.print(prompt + ": ");
    return in.nextLine();
  }

  public static <T> CompletableFuture<T> 
      repeat(Supplier<T> action, Predicate<T> until) {
    return CompletableFuture.supplyAsync(action).thenComposeAsync((T t) -> {
      return until.test(t) ? CompletableFuture.completedFuture(t) 
          : repeat(action, until);
    });
  }

  public static String getPage(String urlString) {
    try {
      Scanner in = new Scanner(new URL(urlString).openStream());
      StringBuilder builder = new StringBuilder();
      while (in.hasNextLine()) {
        builder.append(in.nextLine());
        builder.append("\n");
      }
      in.close();
      return builder.toString();
    } catch (IOException ex) {
      RuntimeException rex = new RuntimeException();
      rex.initCause(ex);
      throw rex;
    }
  }

  public static List<String> matches(String input, String patternString) {
    Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(input);
    List<String> result = new ArrayList<>();

    while (matcher.find()) 
      result.add(matcher.group(1));
    return result;
  }

  public static void main(String[] args) 
      throws ExecutionException, InterruptedException {
    String hrefPattern = "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";

    CompletableFuture<String> f = repeat(() -> 
    getInput("URL"), s -> s.startsWith("http://"))
        .thenApplyAsync((String url) -> getPage(url));

    CompletableFuture<List<String>> links = 
        f.thenApply(c -> matches(c, hrefPattern));

    links.thenAccept(a -> {
      TreeSet<String> t = new TreeSet<String>(a);
      for (String s: t) System.out.println(s);});

    ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS);

  }

}
// output for URL: http://www.horstmann.com/ on 201512171703
//    "http://code.google.com/p/twisty/"
//    "http://codecheck.it"
//    "http://corejsf.com/"
//    "http://frotz.sourceforge.net/"
//    "http://horstmann.com/PracticalOO.html"
//    "http://horstmann.com/applets/Intersection/Intersection.html"
//    "http://horstmann.com/applets/Retire/Retire.html"
//    "http://horstmann.com/applets/RoadApplet/RoadApplet.html"
//    "http://horstmann.com/applets/WeatherApplet/WeatherApplet.html"
//    "http://horstmann.com/bigcpp.html"
//    "http://horstmann.com/bigjava.html"
//    "http://horstmann.com/bjlo.html"
//    "http://horstmann.com/caypubkey.txt"
//    "http://horstmann.com/corejava.html"
//    "http://horstmann.com/cpp/iostreams.html"
//    "http://horstmann.com/cpp/pitfalls.html"
//    "http://horstmann.com/cpp4everyone/index.html"
//    "http://horstmann.com/design_and_patterns.html"
//    "http://horstmann.com/java8/index.html"
//    "http://horstmann.com/javaimpatient/index.html"
//    "http://horstmann.com/mcpp.html"
//    "http://horstmann.com/mood.html"
//    "http://horstmann.com/python4everyone.html"
//    "http://horstmann.com/quotes.html"
//    "http://horstmann.com/resume.html"
//    "http://horstmann.com/safestl.html"
//    "http://horstmann.com/scala/index.html"
//    "http://horstmann.com/violet/index.html"
//    "http://www.family-horstmann.net/"
//    "http://www.kiel.de/"
//    "http://www.mathcs.sjsu.edu/"
//    "http://www.sjsu.edu/people/cay.horstmann"
//    "http://www.stlport.org/"
//    "http://www.syr.edu/"
//    "http://www.umich.edu/"
//    "http://www.uni-kiel.de/"
//    "http://www.vaxdungeon.com/Infocom/"
//    "http://www.wiley.com/college/sc/horstmann/"
//    "https://plus.google.com/117406678785944293188/posts"
//    "mailto:cay@horstmann.com"
//    "unblog/index.html"
