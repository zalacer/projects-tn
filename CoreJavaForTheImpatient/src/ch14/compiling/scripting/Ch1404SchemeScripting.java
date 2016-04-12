package ch14.compiling.scripting;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jscheme.JS;

//  4. Find a Scheme implementation that is compatible with the Java 
//  Scripting API. Write a factorial function in Scheme and call it from Java.

//  references for Java interoperable Scheme implementations: 
//  of the two, only Kawa is compatible with the Java Scripting API
//  http://www.gnu.org/software/kawa/index.html
//  http://jscheme.sourceforge.net/jscheme/main.html

public class Ch1404SchemeScripting {
  
  public static void main(String[] args) throws ScriptException, IOException {

//  using Kawa implementation of Scheme 
//  both these versions of factorial work with it:
//  String fact1 = "(define factorial1(lambda(n)(if (= n 0)1(* n (factorial1 (- n 1))))))(factorial1 9)";
    String fact2 = "(define (factorial2 n)(if (< n 1)1(* n (factorial2 (- n 1)))))(factorial2 9)";
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("scheme");
    Instant start = Instant.now();
    Object f = engine.eval(fact2);
    Instant stop = Instant.now();
    Duration timeElapsed = Duration.between(start, stop);
    long millis = timeElapsed.toMillis();
    System.out.println(f); //  362880
//  StackOverflowError for factorial 10,000
    System.out.println(millis+"\n");
//  178, 181, 173 for factorial 9 
//  185, 184, 184 for factorial 1000
//  this shows most time spent is initialization overhead
    
//  JScheme also works and is interoperable with Java although not supporting
//  the javax.script API and with limited arithmetic capabilities, but faster
//  than Kawa for factorials of small numbers
//  both these versions of factorial work with it:
//  String fac1 = "(define factorial(lambda(n)(if (= n 0)1(* n (factorial (- n 1))))))";
    String fac2 = "(define (factorial n)(if (< n 1)1(* n (factorial (- n 1)))))";
    JS.load(fac2);
    Instant start2 = Instant.now();
    Object r = JS.call("factorial", 9);
    Instant stop2 = Instant.now();
    Duration timeElapsed2 = Duration.between(start2, stop2);
    long millis2 = timeElapsed2.toMillis();
    System.out.println(r); //  362880, 
//  gives accurate result only up to factorial 31, then gives negative results
//  for factorial 32 and 33 and above that returns 0
    System.out.println(millis2);
//  3, 3, 3 for factorial 9 

  }

}
